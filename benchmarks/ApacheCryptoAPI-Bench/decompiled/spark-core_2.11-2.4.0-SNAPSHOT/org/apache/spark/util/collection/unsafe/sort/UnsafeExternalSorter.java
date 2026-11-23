package org.apache.spark.util.collection.unsafe.sort;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.memory.MemoryConsumer;
import org.apache.spark.memory.SparkOutOfMemoryError;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.memory.TooLargePageException;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.unsafe.UnsafeAlignedOffset;
import org.apache.spark.unsafe.array.LongArray;
import org.apache.spark.unsafe.memory.MemoryBlock;
import org.apache.spark.util.TaskCompletionListener;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.annotations.VisibleForTesting;

public final class UnsafeExternalSorter extends MemoryConsumer {
   private static final Logger logger = LoggerFactory.getLogger(UnsafeExternalSorter.class);
   @Nullable
   private final PrefixComparator prefixComparator;
   @Nullable
   private final Supplier<RecordComparator> recordComparatorSupplier;
   private final TaskMemoryManager taskMemoryManager;
   private final BlockManager blockManager;
   private final SerializerManager serializerManager;
   private final TaskContext taskContext;
   private final int fileBufferSizeBytes;
   private final int numElementsForSpillThreshold;
   private final LinkedList<MemoryBlock> allocatedPages = new LinkedList();
   private final LinkedList<UnsafeSorterSpillWriter> spillWriters = new LinkedList();
   @Nullable
   private volatile UnsafeInMemorySorter inMemSorter;
   private MemoryBlock currentPage = null;
   private long pageCursor = -1L;
   private long peakMemoryUsedBytes = 0L;
   private long totalSpillBytes = 0L;
   private long totalSortTimeNanos = 0L;
   private volatile SpillableIterator readingIterator = null;

   public static UnsafeExternalSorter createWithExistingInMemorySorter(TaskMemoryManager taskMemoryManager, BlockManager blockManager, SerializerManager serializerManager, TaskContext taskContext, Supplier<RecordComparator> recordComparatorSupplier, PrefixComparator prefixComparator, int initialSize, long pageSizeBytes, int numElementsForSpillThreshold, UnsafeInMemorySorter inMemorySorter) throws IOException {
      UnsafeExternalSorter sorter = new UnsafeExternalSorter(taskMemoryManager, blockManager, serializerManager, taskContext, recordComparatorSupplier, prefixComparator, initialSize, pageSizeBytes, numElementsForSpillThreshold, inMemorySorter, false);
      sorter.spill(Long.MAX_VALUE, sorter);
      sorter.inMemSorter = null;
      return sorter;
   }

   public static UnsafeExternalSorter create(TaskMemoryManager taskMemoryManager, BlockManager blockManager, SerializerManager serializerManager, TaskContext taskContext, Supplier<RecordComparator> recordComparatorSupplier, PrefixComparator prefixComparator, int initialSize, long pageSizeBytes, int numElementsForSpillThreshold, boolean canUseRadixSort) {
      return new UnsafeExternalSorter(taskMemoryManager, blockManager, serializerManager, taskContext, recordComparatorSupplier, prefixComparator, initialSize, pageSizeBytes, numElementsForSpillThreshold, (UnsafeInMemorySorter)null, canUseRadixSort);
   }

   private UnsafeExternalSorter(TaskMemoryManager taskMemoryManager, BlockManager blockManager, SerializerManager serializerManager, TaskContext taskContext, Supplier<RecordComparator> recordComparatorSupplier, PrefixComparator prefixComparator, int initialSize, long pageSizeBytes, int numElementsForSpillThreshold, @Nullable UnsafeInMemorySorter existingInMemorySorter, boolean canUseRadixSort) {
      super(taskMemoryManager, pageSizeBytes, taskMemoryManager.getTungstenMemoryMode());
      this.taskMemoryManager = taskMemoryManager;
      this.blockManager = blockManager;
      this.serializerManager = serializerManager;
      this.taskContext = taskContext;
      this.recordComparatorSupplier = recordComparatorSupplier;
      this.prefixComparator = prefixComparator;
      this.fileBufferSizeBytes = 32768;
      if (existingInMemorySorter == null) {
         RecordComparator comparator = null;
         if (recordComparatorSupplier != null) {
            comparator = (RecordComparator)recordComparatorSupplier.get();
         }

         this.inMemSorter = new UnsafeInMemorySorter(this, taskMemoryManager, comparator, prefixComparator, initialSize, canUseRadixSort);
      } else {
         this.inMemSorter = existingInMemorySorter;
      }

      this.peakMemoryUsedBytes = this.getMemoryUsage();
      this.numElementsForSpillThreshold = numElementsForSpillThreshold;
      taskContext.addTaskCompletionListener((TaskCompletionListener)((context) -> this.cleanupResources()));
   }

   @VisibleForTesting
   public void closeCurrentPage() {
      if (this.currentPage != null) {
         this.pageCursor = this.currentPage.getBaseOffset() + this.currentPage.size();
      }

   }

   public long spill(long size, MemoryConsumer trigger) throws IOException {
      if (trigger != this) {
         return this.readingIterator != null ? this.readingIterator.spill() : 0L;
      } else if (this.inMemSorter != null && this.inMemSorter.numRecords() > 0) {
         logger.info("Thread {} spilling sort data of {} to disk ({} {} so far)", new Object[]{Thread.currentThread().getId(), Utils.bytesToString(this.getMemoryUsage()), this.spillWriters.size(), this.spillWriters.size() > 1 ? " times" : " time"});
         ShuffleWriteMetrics writeMetrics = new ShuffleWriteMetrics();
         if (this.inMemSorter.numRecords() > 0) {
            UnsafeSorterSpillWriter spillWriter = new UnsafeSorterSpillWriter(this.blockManager, this.fileBufferSizeBytes, writeMetrics, this.inMemSorter.numRecords());
            this.spillWriters.add(spillWriter);
            spillIterator(this.inMemSorter.getSortedIterator(), spillWriter);
         }

         long spillSize = this.freeMemory();
         this.inMemSorter.reset();
         this.taskContext.taskMetrics().incMemoryBytesSpilled(spillSize);
         this.taskContext.taskMetrics().incDiskBytesSpilled(writeMetrics.bytesWritten());
         this.totalSpillBytes += spillSize;
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

   public long getPeakMemoryUsedBytes() {
      this.updatePeakMemoryUsed();
      return this.peakMemoryUsedBytes;
   }

   public long getSortTimeNanos() {
      UnsafeInMemorySorter sorter = this.inMemSorter;
      return sorter != null ? sorter.getSortTimeNanos() : this.totalSortTimeNanos;
   }

   public long getSpillSize() {
      return this.totalSpillBytes;
   }

   @VisibleForTesting
   public int getNumberOfAllocatedPages() {
      return this.allocatedPages.size();
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

   private void deleteSpillFiles() {
      for(UnsafeSorterSpillWriter spill : this.spillWriters) {
         File file = spill.getFile();
         if (file != null && file.exists() && !file.delete()) {
            logger.error("Was unable to delete spill file {}", file.getAbsolutePath());
         }
      }

   }

   public void cleanupResources() {
      synchronized(this) {
         this.deleteSpillFiles();
         this.freeMemory();
         if (this.inMemSorter != null) {
            this.inMemSorter.free();
            this.inMemSorter = null;
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

   public void insertRecord(Object recordBase, long recordOffset, int length, long prefix, boolean prefixIsNull) throws IOException {
      assert this.inMemSorter != null;

      if (this.inMemSorter.numRecords() >= this.numElementsForSpillThreshold) {
         logger.info("Spilling data because number of spilledRecords crossed the threshold " + this.numElementsForSpillThreshold);
         this.spill();
      }

      this.growPointerArrayIfNecessary();
      int uaoSize = UnsafeAlignedOffset.getUaoSize();
      int required = length + uaoSize;
      this.acquireNewPageIfNecessary(required);
      Object base = this.currentPage.getBaseObject();
      long recordAddress = this.taskMemoryManager.encodePageNumberAndOffset(this.currentPage, this.pageCursor);
      UnsafeAlignedOffset.putSize(base, this.pageCursor, length);
      this.pageCursor += (long)uaoSize;
      Platform.copyMemory(recordBase, recordOffset, base, this.pageCursor, (long)length);
      this.pageCursor += (long)length;
      this.inMemSorter.insertRecord(recordAddress, prefix, prefixIsNull);
   }

   public void insertKVRecord(Object keyBase, long keyOffset, int keyLen, Object valueBase, long valueOffset, int valueLen, long prefix, boolean prefixIsNull) throws IOException {
      this.growPointerArrayIfNecessary();
      int uaoSize = UnsafeAlignedOffset.getUaoSize();
      int required = keyLen + valueLen + 2 * uaoSize;
      this.acquireNewPageIfNecessary(required);
      Object base = this.currentPage.getBaseObject();
      long recordAddress = this.taskMemoryManager.encodePageNumberAndOffset(this.currentPage, this.pageCursor);
      UnsafeAlignedOffset.putSize(base, this.pageCursor, keyLen + valueLen + uaoSize);
      this.pageCursor += (long)uaoSize;
      UnsafeAlignedOffset.putSize(base, this.pageCursor, keyLen);
      this.pageCursor += (long)uaoSize;
      Platform.copyMemory(keyBase, keyOffset, base, this.pageCursor, (long)keyLen);
      this.pageCursor += (long)keyLen;
      Platform.copyMemory(valueBase, valueOffset, base, this.pageCursor, (long)valueLen);
      this.pageCursor += (long)valueLen;

      assert this.inMemSorter != null;

      this.inMemSorter.insertRecord(recordAddress, prefix, prefixIsNull);
   }

   public void merge(UnsafeExternalSorter other) throws IOException {
      other.spill();
      this.spillWriters.addAll(other.spillWriters);
      other.spillWriters.clear();
      other.cleanupResources();
   }

   public UnsafeSorterIterator getSortedIterator() throws IOException {
      assert this.recordComparatorSupplier != null;

      if (this.spillWriters.isEmpty()) {
         assert this.inMemSorter != null;

         this.readingIterator = new SpillableIterator(this.inMemSorter.getSortedIterator());
         return this.readingIterator;
      } else {
         UnsafeSorterSpillMerger spillMerger = new UnsafeSorterSpillMerger((RecordComparator)this.recordComparatorSupplier.get(), this.prefixComparator, this.spillWriters.size());

         for(UnsafeSorterSpillWriter spillWriter : this.spillWriters) {
            spillMerger.addSpillIfNotEmpty(spillWriter.getReader(this.serializerManager));
         }

         if (this.inMemSorter != null) {
            this.readingIterator = new SpillableIterator(this.inMemSorter.getSortedIterator());
            spillMerger.addSpillIfNotEmpty(this.readingIterator);
         }

         return spillMerger.getSortedIterator();
      }
   }

   @VisibleForTesting
   boolean hasSpaceForAnotherRecord() {
      return this.inMemSorter.hasSpaceForAnotherRecord();
   }

   private static void spillIterator(UnsafeSorterIterator inMemIterator, UnsafeSorterSpillWriter spillWriter) throws IOException {
      while(inMemIterator.hasNext()) {
         inMemIterator.loadNext();
         Object baseObject = inMemIterator.getBaseObject();
         long baseOffset = inMemIterator.getBaseOffset();
         int recordLength = inMemIterator.getRecordLength();
         spillWriter.write(baseObject, baseOffset, recordLength, inMemIterator.getKeyPrefix());
      }

      spillWriter.close();
   }

   public UnsafeSorterIterator getIterator(int startIndex) throws IOException {
      if (this.spillWriters.isEmpty()) {
         assert this.inMemSorter != null;

         UnsafeSorterIterator iter = this.inMemSorter.getSortedIterator();
         this.moveOver(iter, startIndex);
         return iter;
      } else {
         LinkedList<UnsafeSorterIterator> queue = new LinkedList();
         int i = 0;

         for(UnsafeSorterSpillWriter spillWriter : this.spillWriters) {
            if (i + spillWriter.recordsSpilled() > startIndex) {
               UnsafeSorterIterator iter = spillWriter.getReader(this.serializerManager);
               this.moveOver(iter, startIndex - i);
               queue.add(iter);
            }

            i += spillWriter.recordsSpilled();
         }

         if (this.inMemSorter != null) {
            UnsafeSorterIterator iter = this.inMemSorter.getSortedIterator();
            this.moveOver(iter, startIndex - i);
            queue.add(iter);
         }

         return new ChainedIterator(queue);
      }
   }

   private void moveOver(UnsafeSorterIterator iter, int steps) throws IOException {
      if (steps > 0) {
         for(int i = 0; i < steps; ++i) {
            if (!iter.hasNext()) {
               throw new ArrayIndexOutOfBoundsException("Failed to move the iterator " + steps + " steps forward");
            }

            iter.loadNext();
         }
      }

   }

   class SpillableIterator extends UnsafeSorterIterator {
      private UnsafeSorterIterator upstream;
      private UnsafeSorterIterator nextUpstream = null;
      private MemoryBlock lastPage = null;
      private boolean loaded = false;
      private int numRecords = 0;

      SpillableIterator(UnsafeSorterIterator inMemIterator) {
         this.upstream = inMemIterator;
         this.numRecords = inMemIterator.getNumRecords();
      }

      public int getNumRecords() {
         return this.numRecords;
      }

      public long spill() throws IOException {
         synchronized(this) {
            if (this.upstream instanceof UnsafeInMemorySorter.SortedIterator && this.nextUpstream == null && this.numRecords > 0) {
               UnsafeInMemorySorter.SortedIterator inMemIterator = ((UnsafeInMemorySorter.SortedIterator)this.upstream).clone();
               ShuffleWriteMetrics writeMetrics = new ShuffleWriteMetrics();
               UnsafeSorterSpillWriter spillWriter = new UnsafeSorterSpillWriter(UnsafeExternalSorter.this.blockManager, UnsafeExternalSorter.this.fileBufferSizeBytes, writeMetrics, this.numRecords);
               UnsafeExternalSorter.spillIterator(inMemIterator, spillWriter);
               UnsafeExternalSorter.this.spillWriters.add(spillWriter);
               this.nextUpstream = spillWriter.getReader(UnsafeExternalSorter.this.serializerManager);
               long released = 0L;
               synchronized(UnsafeExternalSorter.this) {
                  for(MemoryBlock page : UnsafeExternalSorter.this.allocatedPages) {
                     if (this.loaded && (long)page.pageNumber == ((UnsafeInMemorySorter.SortedIterator)this.upstream).getCurrentPageNumber()) {
                        this.lastPage = page;
                     } else {
                        released += page.size();
                        UnsafeExternalSorter.this.freePage(page);
                     }
                  }

                  UnsafeExternalSorter.this.allocatedPages.clear();
               }

               assert UnsafeExternalSorter.this.inMemSorter != null;

               released += UnsafeExternalSorter.this.inMemSorter.getMemoryUsage();
               UnsafeExternalSorter.this.totalSortTimeNanos = UnsafeExternalSorter.this.totalSortTimeNanos + UnsafeExternalSorter.this.inMemSorter.getSortTimeNanos();
               UnsafeExternalSorter.this.inMemSorter.free();
               UnsafeExternalSorter.this.inMemSorter = null;
               UnsafeExternalSorter.this.taskContext.taskMetrics().incMemoryBytesSpilled(released);
               UnsafeExternalSorter.this.taskContext.taskMetrics().incDiskBytesSpilled(writeMetrics.bytesWritten());
               UnsafeExternalSorter.this.totalSpillBytes = UnsafeExternalSorter.this.totalSpillBytes + released;
               return released;
            } else {
               return 0L;
            }
         }
      }

      public boolean hasNext() {
         return this.numRecords > 0;
      }

      public void loadNext() throws IOException {
         synchronized(this) {
            this.loaded = true;
            if (this.nextUpstream != null) {
               if (this.lastPage != null) {
                  UnsafeExternalSorter.this.freePage(this.lastPage);
                  this.lastPage = null;
               }

               this.upstream = this.nextUpstream;
               this.nextUpstream = null;
            }

            --this.numRecords;
            this.upstream.loadNext();
         }
      }

      public Object getBaseObject() {
         return this.upstream.getBaseObject();
      }

      public long getBaseOffset() {
         return this.upstream.getBaseOffset();
      }

      public int getRecordLength() {
         return this.upstream.getRecordLength();
      }

      public long getKeyPrefix() {
         return this.upstream.getKeyPrefix();
      }
   }

   static class ChainedIterator extends UnsafeSorterIterator {
      private final Queue<UnsafeSorterIterator> iterators;
      private UnsafeSorterIterator current;
      private int numRecords;

      ChainedIterator(Queue<UnsafeSorterIterator> iterators) {
         assert iterators.size() > 0;

         this.numRecords = 0;

         for(UnsafeSorterIterator iter : iterators) {
            this.numRecords += iter.getNumRecords();
         }

         this.iterators = iterators;
         this.current = (UnsafeSorterIterator)iterators.remove();
      }

      public int getNumRecords() {
         return this.numRecords;
      }

      public boolean hasNext() {
         while(!this.current.hasNext() && !this.iterators.isEmpty()) {
            this.current = (UnsafeSorterIterator)this.iterators.remove();
         }

         return this.current.hasNext();
      }

      public void loadNext() throws IOException {
         while(!this.current.hasNext() && !this.iterators.isEmpty()) {
            this.current = (UnsafeSorterIterator)this.iterators.remove();
         }

         this.current.loadNext();
      }

      public Object getBaseObject() {
         return this.current.getBaseObject();
      }

      public long getBaseOffset() {
         return this.current.getBaseOffset();
      }

      public int getRecordLength() {
         return this.current.getRecordLength();
      }

      public long getKeyPrefix() {
         return this.current.getKeyPrefix();
      }
   }
}
