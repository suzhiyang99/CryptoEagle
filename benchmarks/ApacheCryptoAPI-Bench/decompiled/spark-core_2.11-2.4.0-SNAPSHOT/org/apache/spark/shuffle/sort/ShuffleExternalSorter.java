package org.apache.spark.shuffle.sort;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.annotation.Nullable;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.internal.config.package$;
import org.apache.spark.memory.MemoryConsumer;
import org.apache.spark.memory.SparkOutOfMemoryError;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.memory.TooLargePageException;
import org.apache.spark.serializer.DummySerializerInstance;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.DiskBlockObjectWriter;
import org.apache.spark.storage.FileSegment;
import org.apache.spark.storage.TempShuffleBlockId;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.unsafe.array.LongArray;
import org.apache.spark.unsafe.memory.MemoryBlock;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.annotations.VisibleForTesting;
import scala.Tuple2;

final class ShuffleExternalSorter extends MemoryConsumer {
   private static final Logger logger = LoggerFactory.getLogger(ShuffleExternalSorter.class);
   @VisibleForTesting
   static final int DISK_WRITE_BUFFER_SIZE = 1048576;
   private final int numPartitions;
   private final TaskMemoryManager taskMemoryManager;
   private final BlockManager blockManager;
   private final TaskContext taskContext;
   private final ShuffleWriteMetrics writeMetrics;
   private final int numElementsForSpillThreshold;
   private final int fileBufferSizeBytes;
   private final int diskWriteBufferSize;
   private final LinkedList<MemoryBlock> allocatedPages = new LinkedList();
   private final LinkedList<SpillInfo> spills = new LinkedList();
   private long peakMemoryUsedBytes;
   @Nullable
   private ShuffleInMemorySorter inMemSorter;
   @Nullable
   private MemoryBlock currentPage = null;
   private long pageCursor = -1L;

   ShuffleExternalSorter(TaskMemoryManager memoryManager, BlockManager blockManager, TaskContext taskContext, int initialSize, int numPartitions, SparkConf conf, ShuffleWriteMetrics writeMetrics) {
      super(memoryManager, (long)((int)Math.min(134217728L, memoryManager.pageSizeBytes())), memoryManager.getTungstenMemoryMode());
      this.taskMemoryManager = memoryManager;
      this.blockManager = blockManager;
      this.taskContext = taskContext;
      this.numPartitions = numPartitions;
      this.fileBufferSizeBytes = (int)(Long)conf.get(package$.MODULE$.SHUFFLE_FILE_BUFFER_SIZE()) * 1024;
      this.numElementsForSpillThreshold = (Integer)conf.get(package$.MODULE$.SHUFFLE_SPILL_NUM_ELEMENTS_FORCE_SPILL_THRESHOLD());
      this.writeMetrics = writeMetrics;
      this.inMemSorter = new ShuffleInMemorySorter(this, initialSize, conf.getBoolean("spark.shuffle.sort.useRadixSort", true));
      this.peakMemoryUsedBytes = this.getMemoryUsage();
      this.diskWriteBufferSize = (int)(Long)conf.get(package$.MODULE$.SHUFFLE_DISK_WRITE_BUFFER_SIZE());
   }

   private void writeSortedFile(boolean isLastFile) {
      ShuffleWriteMetrics writeMetricsToUse;
      if (isLastFile) {
         writeMetricsToUse = this.writeMetrics;
      } else {
         writeMetricsToUse = new ShuffleWriteMetrics();
      }

      ShuffleInMemorySorter.ShuffleSorterIterator sortedRecords = this.inMemSorter.getSortedIterator();
      byte[] writeBuffer = new byte[this.diskWriteBufferSize];
      Tuple2<TempShuffleBlockId, File> spilledFileInfo = this.blockManager.diskBlockManager().createTempShuffleBlock();
      File file = spilledFileInfo._2();
      TempShuffleBlockId blockId = spilledFileInfo._1();
      SpillInfo spillInfo = new SpillInfo(this.numPartitions, file, blockId);
      SerializerInstance ser = DummySerializerInstance.INSTANCE;
      DiskBlockObjectWriter writer = this.blockManager.getDiskWriter(blockId, file, ser, this.fileBufferSizeBytes, writeMetricsToUse);
      int currentPartition = -1;

      while(sortedRecords.hasNext()) {
         sortedRecords.loadNext();
         int partition = sortedRecords.packedRecordPointer.getPartitionId();

         assert partition >= currentPartition;

         if (partition != currentPartition) {
            if (currentPartition != -1) {
               FileSegment fileSegment = writer.commitAndGet();
               spillInfo.partitionLengths[currentPartition] = fileSegment.length();
            }

            currentPartition = partition;
         }

         long recordPointer = sortedRecords.packedRecordPointer.getRecordPointer();
         Object recordPage = this.taskMemoryManager.getPage(recordPointer);
         long recordOffsetInPage = this.taskMemoryManager.getOffsetInPage(recordPointer);
         int dataRemaining = Platform.getInt(recordPage, recordOffsetInPage);

         int toTransfer;
         for(long recordReadPosition = recordOffsetInPage + 4L; dataRemaining > 0; dataRemaining -= toTransfer) {
            toTransfer = Math.min(this.diskWriteBufferSize, dataRemaining);
            Platform.copyMemory(recordPage, recordReadPosition, writeBuffer, (long)Platform.BYTE_ARRAY_OFFSET, (long)toTransfer);
            writer.write(writeBuffer, 0, toTransfer);
            recordReadPosition += (long)toTransfer;
         }

         writer.recordWritten();
      }

      FileSegment committedSegment = writer.commitAndGet();
      writer.close();
      if (currentPartition != -1) {
         spillInfo.partitionLengths[currentPartition] = committedSegment.length();
         this.spills.add(spillInfo);
      }

      if (!isLastFile) {
         this.writeMetrics.incRecordsWritten(writeMetricsToUse.recordsWritten());
         this.taskContext.taskMetrics().incDiskBytesSpilled(writeMetricsToUse.bytesWritten());
      }

   }

   public long spill(long size, MemoryConsumer trigger) throws IOException {
      if (trigger == this && this.inMemSorter != null && this.inMemSorter.numRecords() != 0) {
         logger.info("Thread {} spilling sort data of {} to disk ({} {} so far)", new Object[]{Thread.currentThread().getId(), Utils.bytesToString(this.getMemoryUsage()), this.spills.size(), this.spills.size() > 1 ? " times" : " time"});
         this.writeSortedFile(false);
         long spillSize = this.freeMemory();
         this.inMemSorter.reset();
         this.taskContext.taskMetrics().incMemoryBytesSpilled(spillSize);
         return spillSize;
      } else {
         return 0L;
      }
   }

   private long getMemoryUsage() {
      long totalPageSize = 0L;

      for(MemoryBlock page : this.allocatedPages) {
         totalPageSize += page.size();
      }

      return (this.inMemSorter == null ? 0L : this.inMemSorter.getMemoryUsage()) + totalPageSize;
   }

   private void updatePeakMemoryUsed() {
      long mem = this.getMemoryUsage();
      if (mem > this.peakMemoryUsedBytes) {
         this.peakMemoryUsedBytes = mem;
      }

   }

   long getPeakMemoryUsedBytes() {
      this.updatePeakMemoryUsed();
      return this.peakMemoryUsedBytes;
   }

   private long freeMemory() {
      this.updatePeakMemoryUsed();
      long memoryFreed = 0L;

      for(MemoryBlock block : this.allocatedPages) {
         memoryFreed += block.size();
         this.freePage(block);
      }

      this.allocatedPages.clear();
      this.currentPage = null;
      this.pageCursor = 0L;
      return memoryFreed;
   }

   public void cleanupResources() {
      this.freeMemory();
      if (this.inMemSorter != null) {
         this.inMemSorter.free();
         this.inMemSorter = null;
      }

      for(SpillInfo spill : this.spills) {
         if (spill.file.exists() && !spill.file.delete()) {
            logger.error("Unable to delete spill file {}", spill.file.getPath());
         }
      }

   }

   private void growPointerArrayIfNecessary() throws IOException {
      assert this.inMemSorter != null;

      if (!this.inMemSorter.hasSpaceForAnotherRecord()) {
         long used = this.inMemSorter.getMemoryUsage();

         LongArray array;
         try {
            array = this.allocateArray(used / 8L * 2L);
         } catch (TooLargePageException var5) {
            this.spill();
            return;
         } catch (SparkOutOfMemoryError e) {
            if (!this.inMemSorter.hasSpaceForAnotherRecord()) {
               logger.error("Unable to grow the pointer array");
               throw e;
            }

            return;
         }

         if (this.inMemSorter.hasSpaceForAnotherRecord()) {
            this.freeArray(array);
         } else {
            this.inMemSorter.expandPointerArray(array);
         }
      }

   }

   private void acquireNewPageIfNecessary(int required) {
      if (this.currentPage == null || this.pageCursor + (long)required > this.currentPage.getBaseOffset() + this.currentPage.size()) {
         this.currentPage = this.allocatePage((long)required);
         this.pageCursor = this.currentPage.getBaseOffset();
         this.allocatedPages.add(this.currentPage);
      }

   }

   public void insertRecord(Object recordBase, long recordOffset, int length, int partitionId) throws IOException {
      assert this.inMemSorter != null;

      if (this.inMemSorter.numRecords() >= this.numElementsForSpillThreshold) {
         logger.info("Spilling data because number of spilledRecords crossed the threshold " + this.numElementsForSpillThreshold);
         this.spill();
      }

      this.growPointerArrayIfNecessary();
      int required = length + 4;
      this.acquireNewPageIfNecessary(required);

      assert this.currentPage != null;

      Object base = this.currentPage.getBaseObject();
      long recordAddress = this.taskMemoryManager.encodePageNumberAndOffset(this.currentPage, this.pageCursor);
      Platform.putInt(base, this.pageCursor, length);
      this.pageCursor += 4L;
      Platform.copyMemory(recordBase, recordOffset, base, this.pageCursor, (long)length);
      this.pageCursor += (long)length;
      this.inMemSorter.insertRecord(recordAddress, partitionId);
   }

   public SpillInfo[] closeAndGetSpills() throws IOException {
      if (this.inMemSorter != null) {
         this.writeSortedFile(true);
         this.freeMemory();
         this.inMemSorter.free();
         this.inMemSorter = null;
      }

      return (SpillInfo[])this.spills.toArray(new SpillInfo[this.spills.size()]);
   }
}
