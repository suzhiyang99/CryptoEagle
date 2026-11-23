package org.apache.spark.shuffle.sort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import org.apache.spark.Partitioner;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.scheduler.MapStatus$;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.shuffle.IndexShuffleBlockResolver;
import org.apache.spark.shuffle.ShuffleWriter;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.DiskBlockObjectWriter;
import org.apache.spark.storage.FileSegment;
import org.apache.spark.storage.TempShuffleBlockId;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.annotations.VisibleForTesting;
import org.spark_project.guava.io.Closeables;
import scala.None$;
import scala.Option;
import scala.Product2;
import scala.Tuple2;
import scala.collection.Iterator;

final class BypassMergeSortShuffleWriter<K, V> extends ShuffleWriter<K, V> {
   private static final Logger logger = LoggerFactory.getLogger(BypassMergeSortShuffleWriter.class);
   private final int fileBufferSize;
   private final boolean transferToEnabled;
   private final int numPartitions;
   private final BlockManager blockManager;
   private final Partitioner partitioner;
   private final ShuffleWriteMetrics writeMetrics;
   private final int shuffleId;
   private final int mapId;
   private final Serializer serializer;
   private final IndexShuffleBlockResolver shuffleBlockResolver;
   private DiskBlockObjectWriter[] partitionWriters;
   private FileSegment[] partitionWriterSegments;
   @Nullable
   private MapStatus mapStatus;
   private long[] partitionLengths;
   private boolean stopping = false;

   BypassMergeSortShuffleWriter(BlockManager blockManager, IndexShuffleBlockResolver shuffleBlockResolver, BypassMergeSortShuffleHandle<K, V> handle, int mapId, TaskContext taskContext, SparkConf conf) {
      this.fileBufferSize = (int)conf.getSizeAsKb("spark.shuffle.file.buffer", "32k") * 1024;
      this.transferToEnabled = conf.getBoolean("spark.file.transferTo", true);
      this.blockManager = blockManager;
      ShuffleDependency<K, V, V> dep = handle.dependency();
      this.mapId = mapId;
      this.shuffleId = dep.shuffleId();
      this.partitioner = dep.partitioner();
      this.numPartitions = this.partitioner.numPartitions();
      this.writeMetrics = taskContext.taskMetrics().shuffleWriteMetrics();
      this.serializer = dep.serializer();
      this.shuffleBlockResolver = shuffleBlockResolver;
   }

   public void write(Iterator<Product2<K, V>> records) throws IOException {
      assert this.partitionWriters == null;

      if (!records.hasNext()) {
         this.partitionLengths = new long[this.numPartitions];
         this.shuffleBlockResolver.writeIndexFileAndCommit(this.shuffleId, this.mapId, this.partitionLengths, (File)null);
         this.mapStatus = MapStatus$.MODULE$.apply(this.blockManager.shuffleServerId(), this.partitionLengths);
      } else {
         SerializerInstance serInstance = this.serializer.newInstance();
         long openStartTime = System.nanoTime();
         this.partitionWriters = new DiskBlockObjectWriter[this.numPartitions];
         this.partitionWriterSegments = new FileSegment[this.numPartitions];

         for(int i = 0; i < this.numPartitions; ++i) {
            Tuple2<TempShuffleBlockId, File> tempShuffleBlockIdPlusFile = this.blockManager.diskBlockManager().createTempShuffleBlock();
            File file = tempShuffleBlockIdPlusFile._2();
            BlockId blockId = tempShuffleBlockIdPlusFile._1();
            this.partitionWriters[i] = this.blockManager.getDiskWriter(blockId, file, serInstance, this.fileBufferSize, this.writeMetrics);
         }

         this.writeMetrics.incWriteTime(System.nanoTime() - openStartTime);

         while(records.hasNext()) {
            Product2<K, V> record = records.next();
            K key = record._1();
            this.partitionWriters[this.partitioner.getPartition(key)].write(key, record._2());
         }

         for(int i = 0; i < this.numPartitions; ++i) {
            DiskBlockObjectWriter writer = this.partitionWriters[i];
            this.partitionWriterSegments[i] = writer.commitAndGet();
            writer.close();
         }

         File output = this.shuffleBlockResolver.getDataFile(this.shuffleId, this.mapId);
         File tmp = Utils.tempFileWith(output);

         try {
            this.partitionLengths = this.writePartitionedFile(tmp);
            this.shuffleBlockResolver.writeIndexFileAndCommit(this.shuffleId, this.mapId, this.partitionLengths, tmp);
         } finally {
            if (tmp.exists() && !tmp.delete()) {
               logger.error("Error while deleting temp file {}", tmp.getAbsolutePath());
            }

         }

         this.mapStatus = MapStatus$.MODULE$.apply(this.blockManager.shuffleServerId(), this.partitionLengths);
      }
   }

   @VisibleForTesting
   long[] getPartitionLengths() {
      return this.partitionLengths;
   }

   private long[] writePartitionedFile(File outputFile) throws IOException {
      long[] lengths = new long[this.numPartitions];
      if (this.partitionWriters == null) {
         return lengths;
      } else {
         FileOutputStream out = new FileOutputStream(outputFile, true);
         long writeStartTime = System.nanoTime();
         boolean threwException = true;

         try {
            for(int i = 0; i < this.numPartitions; ++i) {
               File file = this.partitionWriterSegments[i].file();
               if (file.exists()) {
                  FileInputStream in = new FileInputStream(file);
                  boolean copyThrewException = true;

                  try {
                     lengths[i] = Utils.copyStream(in, out, false, this.transferToEnabled);
                     copyThrewException = false;
                  } finally {
                     Closeables.close(in, copyThrewException);
                  }

                  if (!file.delete()) {
                     logger.error("Unable to delete file for partition {}", i);
                  }
               }
            }

            threwException = false;
         } finally {
            Closeables.close(out, threwException);
            this.writeMetrics.incWriteTime(System.nanoTime() - writeStartTime);
         }

         this.partitionWriters = null;
         return lengths;
      }
   }

   public Option<MapStatus> stop(boolean success) {
      if (this.stopping) {
         return None$.empty();
      } else {
         this.stopping = true;
         if (success) {
            if (this.mapStatus == null) {
               throw new IllegalStateException("Cannot call stop(true) without having called write()");
            } else {
               return Option.<MapStatus>apply(this.mapStatus);
            }
         } else {
            if (this.partitionWriters != null) {
               try {
                  for(DiskBlockObjectWriter writer : this.partitionWriters) {
                     File file = writer.revertPartialWritesAndClose();
                     if (!file.delete()) {
                        logger.error("Error while deleting file {}", file.getAbsolutePath());
                     }
                  }
               } finally {
                  this.partitionWriters = null;
               }
            }

            return None$.empty();
         }
      }
   }
}
