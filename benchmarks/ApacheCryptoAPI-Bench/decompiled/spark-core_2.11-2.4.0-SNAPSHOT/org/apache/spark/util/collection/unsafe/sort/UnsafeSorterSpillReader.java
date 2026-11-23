package org.apache.spark.util.collection.unsafe.sort;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.spark.SparkEnv;
import org.apache.spark.TaskContext;
import org.apache.spark.io.NioBufferedFileInputStream;
import org.apache.spark.io.ReadAheadInputStream;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.unsafe.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Closeables;

public final class UnsafeSorterSpillReader extends UnsafeSorterIterator implements Closeable {
   private static final Logger logger = LoggerFactory.getLogger(UnsafeSorterSpillReader.class);
   private static final int DEFAULT_BUFFER_SIZE_BYTES = 1048576;
   private static final int MAX_BUFFER_SIZE_BYTES = 16777216;
   private InputStream in;
   private DataInputStream din;
   private int recordLength;
   private long keyPrefix;
   private int numRecords;
   private int numRecordsRemaining;
   private byte[] arr = new byte[1048576];
   private Object baseObject;
   private final long baseOffset;
   private final TaskContext taskContext;

   public UnsafeSorterSpillReader(SerializerManager serializerManager, File file, BlockId blockId) throws IOException {
      this.baseObject = this.arr;
      this.baseOffset = (long)Platform.BYTE_ARRAY_OFFSET;
      this.taskContext = TaskContext.get();

      assert file.length() > 0L;

      long bufferSizeBytes = SparkEnv.get() == null ? 1048576L : SparkEnv.get().conf().getSizeAsBytes("spark.unsafe.sorter.spill.reader.buffer.size", 1048576L);
      if (bufferSizeBytes > 16777216L || bufferSizeBytes < 1048576L) {
         logger.warn("Value of config \"spark.unsafe.sorter.spill.reader.buffer.size\" = {} not in allowed range [{}, {}). Falling back to default value : {} bytes", new Object[]{bufferSizeBytes, 1048576, 16777216, 1048576});
         bufferSizeBytes = 1048576L;
      }

      double readAheadFraction = SparkEnv.get() == null ? (double)0.5F : SparkEnv.get().conf().getDouble("spark.unsafe.sorter.spill.read.ahead.fraction", (double)0.5F);
      boolean readAheadEnabled = SparkEnv.get() != null && SparkEnv.get().conf().getBoolean("spark.unsafe.sorter.spill.read.ahead.enabled", true);
      InputStream bs = new NioBufferedFileInputStream(file, (int)bufferSizeBytes);

      try {
         if (readAheadEnabled) {
            this.in = new ReadAheadInputStream(serializerManager.wrapStream(blockId, bs), (int)bufferSizeBytes, (int)((double)bufferSizeBytes * readAheadFraction));
         } else {
            this.in = serializerManager.wrapStream(blockId, bs);
         }

         this.din = new DataInputStream(this.in);
         this.numRecords = this.numRecordsRemaining = this.din.readInt();
      } catch (IOException e) {
         Closeables.close(bs, true);
         throw e;
      }
   }

   public int getNumRecords() {
      return this.numRecords;
   }

   public boolean hasNext() {
      return this.numRecordsRemaining > 0;
   }

   public void loadNext() throws IOException {
      if (this.taskContext != null) {
         this.taskContext.killTaskIfInterrupted();
      }

      this.recordLength = this.din.readInt();
      this.keyPrefix = this.din.readLong();
      if (this.recordLength > this.arr.length) {
         this.arr = new byte[this.recordLength];
         this.baseObject = this.arr;
      }

      ByteStreams.readFully(this.in, this.arr, 0, this.recordLength);
      --this.numRecordsRemaining;
      if (this.numRecordsRemaining == 0) {
         this.close();
      }

   }

   public Object getBaseObject() {
      return this.baseObject;
   }

   public long getBaseOffset() {
      return this.baseOffset;
   }

   public int getRecordLength() {
      return this.recordLength;
   }

   public long getKeyPrefix() {
      return this.keyPrefix;
   }

   public void close() throws IOException {
      if (this.in != null) {
         try {
            this.in.close();
         } finally {
            this.in = null;
            this.din = null;
         }
      }

   }
}
