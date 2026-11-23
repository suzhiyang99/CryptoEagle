package org.apache.spark.shuffle.sort;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.spark.Partitioner;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.Private;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.internal.config.package$;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.io.NioBufferedFileInputStream;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.network.util.LimitedInputStream;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.scheduler.MapStatus$;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.shuffle.IndexShuffleBlockResolver;
import org.apache.spark.shuffle.ShuffleWriter;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.TimeTrackingOutputStream;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.annotations.VisibleForTesting;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Closeables;
import org.spark_project.guava.io.Files;
import scala.Option;
import scala.Product2;
import scala.collection.JavaConverters;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

@Private
public class UnsafeShuffleWriter<K, V> extends ShuffleWriter<K, V> {
   private static final Logger logger = LoggerFactory.getLogger(UnsafeShuffleWriter.class);
   private static final ClassTag<Object> OBJECT_CLASS_TAG;
   @VisibleForTesting
   static final int DEFAULT_INITIAL_SORT_BUFFER_SIZE = 4096;
   static final int DEFAULT_INITIAL_SER_BUFFER_SIZE = 1048576;
   private final BlockManager blockManager;
   private final IndexShuffleBlockResolver shuffleBlockResolver;
   private final TaskMemoryManager memoryManager;
   private final SerializerInstance serializer;
   private final Partitioner partitioner;
   private final ShuffleWriteMetrics writeMetrics;
   private final int shuffleId;
   private final int mapId;
   private final TaskContext taskContext;
   private final SparkConf sparkConf;
   private final boolean transferToEnabled;
   private final int initialSortBufferSize;
   private final int inputBufferSizeInBytes;
   private final int outputBufferSizeInBytes;
   @Nullable
   private MapStatus mapStatus;
   @Nullable
   private ShuffleExternalSorter sorter;
   private long peakMemoryUsedBytes = 0L;
   private MyByteArrayOutputStream serBuffer;
   private SerializationStream serOutputStream;
   private boolean stopping = false;

   public UnsafeShuffleWriter(BlockManager blockManager, IndexShuffleBlockResolver shuffleBlockResolver, TaskMemoryManager memoryManager, SerializedShuffleHandle<K, V> handle, int mapId, TaskContext taskContext, SparkConf sparkConf) throws IOException {
      int numPartitions = handle.dependency().partitioner().numPartitions();
      if (numPartitions > SortShuffleManager.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE()) {
         throw new IllegalArgumentException("UnsafeShuffleWriter can only be used for shuffles with at most " + SortShuffleManager.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE() + " reduce partitions");
      } else {
         this.blockManager = blockManager;
         this.shuffleBlockResolver = shuffleBlockResolver;
         this.memoryManager = memoryManager;
         this.mapId = mapId;
         ShuffleDependency<K, V, V> dep = handle.dependency();
         this.shuffleId = dep.shuffleId();
         this.serializer = dep.serializer().newInstance();
         this.partitioner = dep.partitioner();
         this.writeMetrics = taskContext.taskMetrics().shuffleWriteMetrics();
         this.taskContext = taskContext;
         this.sparkConf = sparkConf;
         this.transferToEnabled = sparkConf.getBoolean("spark.file.transferTo", true);
         this.initialSortBufferSize = sparkConf.getInt("spark.shuffle.sort.initialBufferSize", 4096);
         this.inputBufferSizeInBytes = (int)(Long)sparkConf.get(package$.MODULE$.SHUFFLE_FILE_BUFFER_SIZE()) * 1024;
         this.outputBufferSizeInBytes = (int)(Long)sparkConf.get(package$.MODULE$.SHUFFLE_UNSAFE_FILE_OUTPUT_BUFFER_SIZE()) * 1024;
         this.open();
      }
   }

   private void updatePeakMemoryUsed() {
      if (this.sorter != null) {
         long mem = this.sorter.getPeakMemoryUsedBytes();
         if (mem > this.peakMemoryUsedBytes) {
            this.peakMemoryUsedBytes = mem;
         }
      }

   }

   public long getPeakMemoryUsedBytes() {
      this.updatePeakMemoryUsed();
      return this.peakMemoryUsedBytes;
   }

   @VisibleForTesting
   public void write(Iterator<Product2<K, V>> records) throws IOException {
      this.write((scala.collection.Iterator)JavaConverters.asScalaIteratorConverter(records).asScala());
   }

   public void write(scala.collection.Iterator<Product2<K, V>> records) throws IOException {
      boolean success = false;

      try {
         while(records.hasNext()) {
            this.insertRecordIntoSorter(records.next());
         }

         this.closeAndWriteOutput();
         success = true;
      } finally {
         if (this.sorter != null) {
            try {
               this.sorter.cleanupResources();
            } catch (Exception e) {
               if (success) {
                  throw e;
               }

               logger.error("In addition to a failure during writing, we failed during cleanup.", e);
            }
         }

      }

   }

   private void open() {
      assert this.sorter == null;

      this.sorter = new ShuffleExternalSorter(this.memoryManager, this.blockManager, this.taskContext, this.initialSortBufferSize, this.partitioner.numPartitions(), this.sparkConf, this.writeMetrics);
      this.serBuffer = new MyByteArrayOutputStream(1048576);
      this.serOutputStream = this.serializer.serializeStream(this.serBuffer);
   }

   @VisibleForTesting
   void closeAndWriteOutput() throws IOException {
      assert this.sorter != null;

      this.updatePeakMemoryUsed();
      this.serBuffer = null;
      this.serOutputStream = null;
      SpillInfo[] spills = this.sorter.closeAndGetSpills();
      this.sorter = null;
      File output = this.shuffleBlockResolver.getDataFile(this.shuffleId, this.mapId);
      File tmp = Utils.tempFileWith(output);

      long[] partitionLengths;
      try {
         boolean var19 = false;

         try {
            var19 = true;
            partitionLengths = this.mergeSpills(spills, tmp);
            var19 = false;
         } finally {
            if (var19) {
               SpillInfo[] var10 = spills;
               int var11 = spills.length;
               int var12 = 0;

               while(true) {
                  if (var12 >= var11) {
                     ;
                  } else {
                     SpillInfo spill = var10[var12];
                     if (spill.file.exists() && !spill.file.delete()) {
                        logger.error("Error while deleting spill file {}", spill.file.getPath());
                     }

                     ++var12;
                  }
               }
            }
         }

         for(SpillInfo spill : spills) {
            if (spill.file.exists() && !spill.file.delete()) {
               logger.error("Error while deleting spill file {}", spill.file.getPath());
            }
         }

         this.shuffleBlockResolver.writeIndexFileAndCommit(this.shuffleId, this.mapId, partitionLengths, tmp);
      } finally {
         if (tmp.exists() && !tmp.delete()) {
            logger.error("Error while deleting temp file {}", tmp.getAbsolutePath());
         }

      }

      this.mapStatus = MapStatus$.MODULE$.apply(this.blockManager.shuffleServerId(), partitionLengths);
   }

   @VisibleForTesting
   void insertRecordIntoSorter(Product2<K, V> record) throws IOException {
      assert this.sorter != null;

      K key = record._1();
      int partitionId = this.partitioner.getPartition(key);
      this.serBuffer.reset();
      this.serOutputStream.writeKey(key, OBJECT_CLASS_TAG);
      this.serOutputStream.writeValue(record._2(), OBJECT_CLASS_TAG);
      this.serOutputStream.flush();
      int serializedRecordSize = this.serBuffer.size();

      assert serializedRecordSize > 0;

      this.sorter.insertRecord(this.serBuffer.getBuf(), (long)Platform.BYTE_ARRAY_OFFSET, serializedRecordSize, partitionId);
   }

   @VisibleForTesting
   void forceSorterToSpill() throws IOException {
      assert this.sorter != null;

      this.sorter.spill();
   }

   private long[] mergeSpills(SpillInfo[] spills, File outputFile) throws IOException {
      boolean compressionEnabled = this.sparkConf.getBoolean("spark.shuffle.compress", true);
      CompressionCodec compressionCodec = CompressionCodec$.MODULE$.createCodec(this.sparkConf);
      boolean fastMergeEnabled = this.sparkConf.getBoolean("spark.shuffle.unsafe.fastMergeEnabled", true);
      boolean fastMergeIsSupported = !compressionEnabled || CompressionCodec$.MODULE$.supportsConcatenationOfSerializedStreams(compressionCodec);
      boolean encryptionEnabled = this.blockManager.serializerManager().encryptionEnabled();

      try {
         if (spills.length == 0) {
            (new FileOutputStream(outputFile)).close();
            return new long[this.partitioner.numPartitions()];
         } else if (spills.length == 1) {
            Files.move(spills[0].file, outputFile);
            return spills[0].partitionLengths;
         } else {
            long[] partitionLengths;
            if (fastMergeEnabled && fastMergeIsSupported) {
               if (this.transferToEnabled && !encryptionEnabled) {
                  logger.debug("Using transferTo-based fast merge");
                  partitionLengths = this.mergeSpillsWithTransferTo(spills, outputFile);
               } else {
                  logger.debug("Using fileStream-based fast merge");
                  partitionLengths = this.mergeSpillsWithFileStream(spills, outputFile, (CompressionCodec)null);
               }
            } else {
               logger.debug("Using slow merge");
               partitionLengths = this.mergeSpillsWithFileStream(spills, outputFile, compressionCodec);
            }

            this.writeMetrics.decBytesWritten(spills[spills.length - 1].file.length());
            this.writeMetrics.incBytesWritten(outputFile.length());
            return partitionLengths;
         }
      } catch (IOException e) {
         if (outputFile.exists() && !outputFile.delete()) {
            logger.error("Unable to delete output file {}", outputFile.getPath());
         }

         throw e;
      }
   }

   private long[] mergeSpillsWithFileStream(SpillInfo[] spills, File outputFile, @Nullable CompressionCodec compressionCodec) throws IOException {
      assert spills.length >= 2;

      int numPartitions = this.partitioner.numPartitions();
      long[] partitionLengths = new long[numPartitions];
      InputStream[] spillInputStreams = new InputStream[spills.length];
      OutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile), this.outputBufferSizeInBytes);
      CountingOutputStream mergedFileOutputStream = new CountingOutputStream(bos);
      boolean threwException = true;
      boolean var26 = false;

      try {
         var26 = true;

         for(int i = 0; i < spills.length; ++i) {
            spillInputStreams[i] = new NioBufferedFileInputStream(spills[i].file, this.inputBufferSizeInBytes);
         }

         for(int partition = 0; partition < numPartitions; ++partition) {
            long initialFileLength = mergedFileOutputStream.getByteCount();
            OutputStream partitionOutput = new CloseAndFlushShieldOutputStream(new TimeTrackingOutputStream(this.writeMetrics, mergedFileOutputStream));
            OutputStream stream = this.blockManager.serializerManager().wrapForEncryption(partitionOutput);
            if (compressionCodec != null) {
               stream = compressionCodec.compressedOutputStream(stream);
            }

            for(int i = 0; i < spills.length; ++i) {
               long partitionLengthInSpill = spills[i].partitionLengths[partition];
               if (partitionLengthInSpill > 0L) {
                  InputStream partitionInputStream = new LimitedInputStream(spillInputStreams[i], partitionLengthInSpill, false);

                  try {
                     partitionInputStream = this.blockManager.serializerManager().wrapForEncryption(partitionInputStream);
                     if (compressionCodec != null) {
                        partitionInputStream = compressionCodec.compressedInputStream(partitionInputStream);
                     }

                     ByteStreams.copy(partitionInputStream, stream);
                  } finally {
                     partitionInputStream.close();
                  }
               }
            }

            stream.flush();
            stream.close();
            partitionLengths[partition] = mergedFileOutputStream.getByteCount() - initialFileLength;
         }

         threwException = false;
         var26 = false;
      } finally {
         if (var26) {
            for(InputStream stream : spillInputStreams) {
               Closeables.close(stream, threwException);
            }

            Closeables.close(mergedFileOutputStream, threwException);
         }
      }

      for(InputStream stream : spillInputStreams) {
         Closeables.close(stream, threwException);
      }

      Closeables.close(mergedFileOutputStream, threwException);
      return partitionLengths;
   }

   private long[] mergeSpillsWithTransferTo(SpillInfo[] spills, File outputFile) throws IOException {
      assert spills.length >= 2;

      int numPartitions = this.partitioner.numPartitions();
      long[] partitionLengths = new long[numPartitions];
      FileChannel[] spillInputChannels = new FileChannel[spills.length];
      long[] spillInputChannelPositions = new long[spills.length];
      FileChannel mergedFileOutputChannel = null;
      boolean threwException = true;
      boolean var21 = false;

      try {
         var21 = true;

         for(int i = 0; i < spills.length; ++i) {
            spillInputChannels[i] = (new FileInputStream(spills[i].file)).getChannel();
         }

         mergedFileOutputChannel = (new FileOutputStream(outputFile, true)).getChannel();
         long bytesWrittenToMergedFile = 0L;

         for(int partition = 0; partition < numPartitions; ++partition) {
            for(int i = 0; i < spills.length; ++i) {
               long partitionLengthInSpill = spills[i].partitionLengths[partition];
               FileChannel spillInputChannel = spillInputChannels[i];
               long writeStartTime = System.nanoTime();
               Utils.copyFileStreamNIO(spillInputChannel, mergedFileOutputChannel, spillInputChannelPositions[i], partitionLengthInSpill);
               spillInputChannelPositions[i] += partitionLengthInSpill;
               this.writeMetrics.incWriteTime(System.nanoTime() - writeStartTime);
               bytesWrittenToMergedFile += partitionLengthInSpill;
               partitionLengths[partition] += partitionLengthInSpill;
            }
         }

         if (mergedFileOutputChannel.position() != bytesWrittenToMergedFile) {
            throw new IOException("Current position " + mergedFileOutputChannel.position() + " does not equal expected position " + bytesWrittenToMergedFile + " after transferTo. Please check your kernel version to see if it is 2.6.32, as there is a kernel bug which will lead to unexpected behavior when using transferTo. You can set spark.file.transferTo=false to disable this NIO feature.");
         }

         threwException = false;
         var21 = false;
      } finally {
         if (var21) {
            int i = 0;

            while(true) {
               if (i >= spills.length) {
                  Closeables.close(mergedFileOutputChannel, threwException);
               } else {
                  assert spillInputChannelPositions[i] == spills[i].file.length();

                  Closeables.close(spillInputChannels[i], threwException);
                  ++i;
               }
            }
         }
      }

      for(int i = 0; i < spills.length; ++i) {
         assert spillInputChannelPositions[i] == spills[i].file.length();

         Closeables.close(spillInputChannels[i], threwException);
      }

      Closeables.close(mergedFileOutputChannel, threwException);
      return partitionLengths;
   }

   public Option<MapStatus> stop(boolean success) {
      Option var2;
      try {
         this.taskContext.taskMetrics().incPeakExecutionMemory(this.getPeakMemoryUsedBytes());
         if (!this.stopping) {
            this.stopping = true;
            if (!success) {
               var2 = Option.apply((Object)null);
               return var2;
            }

            if (this.mapStatus == null) {
               throw new IllegalStateException("Cannot call stop(true) without having called write()");
            }

            var2 = Option.apply(this.mapStatus);
            return var2;
         }

         var2 = Option.apply((Object)null);
      } finally {
         if (this.sorter != null) {
            this.sorter.cleanupResources();
         }

      }

      return var2;
   }

   static {
      OBJECT_CLASS_TAG = ClassTag$.MODULE$.Object();
   }

   private static final class MyByteArrayOutputStream extends ByteArrayOutputStream {
      MyByteArrayOutputStream(int size) {
         super(size);
      }

      public byte[] getBuf() {
         return this.buf;
      }
   }

   private class CloseAndFlushShieldOutputStream extends CloseShieldOutputStream {
      CloseAndFlushShieldOutputStream(OutputStream outputStream) {
         super(outputStream);
      }

      public void flush() {
      }
   }
}
