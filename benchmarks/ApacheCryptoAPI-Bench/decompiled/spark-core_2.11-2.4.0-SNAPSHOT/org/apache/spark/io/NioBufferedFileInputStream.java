package org.apache.spark.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import org.apache.spark.storage.StorageUtils;

public final class NioBufferedFileInputStream extends InputStream {
   private static final int DEFAULT_BUFFER_SIZE_BYTES = 8192;
   private final ByteBuffer byteBuffer;
   private final FileChannel fileChannel;

   public NioBufferedFileInputStream(File file, int bufferSizeInBytes) throws IOException {
      this.byteBuffer = ByteBuffer.allocateDirect(bufferSizeInBytes);
      this.fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
      this.byteBuffer.flip();
   }

   public NioBufferedFileInputStream(File file) throws IOException {
      this(file, 8192);
   }

   private boolean refill() throws IOException {
      if (!this.byteBuffer.hasRemaining()) {
         this.byteBuffer.clear();

         int nRead;
         for(nRead = 0; nRead == 0; nRead = this.fileChannel.read(this.byteBuffer)) {
         }

         if (nRead < 0) {
            return false;
         }

         this.byteBuffer.flip();
      }

      return true;
   }

   public synchronized int read() throws IOException {
      return !this.refill() ? -1 : this.byteBuffer.get() & 255;
   }

   public synchronized int read(byte[] b, int offset, int len) throws IOException {
      if (offset >= 0 && len >= 0 && offset + len >= 0 && offset + len <= b.length) {
         if (!this.refill()) {
            return -1;
         } else {
            len = Math.min(len, this.byteBuffer.remaining());
            this.byteBuffer.get(b, offset, len);
            return len;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized int available() throws IOException {
      return this.byteBuffer.remaining();
   }

   public synchronized long skip(long n) throws IOException {
      if (n <= 0L) {
         return 0L;
      } else if ((long)this.byteBuffer.remaining() >= n) {
         this.byteBuffer.position(this.byteBuffer.position() + (int)n);
         return n;
      } else {
         long skippedFromBuffer = (long)this.byteBuffer.remaining();
         long toSkipFromFileChannel = n - skippedFromBuffer;
         this.byteBuffer.position(0);
         this.byteBuffer.flip();
         return skippedFromBuffer + this.skipFromFileChannel(toSkipFromFileChannel);
      }
   }

   private long skipFromFileChannel(long n) throws IOException {
      long currentFilePosition = this.fileChannel.position();
      long size = this.fileChannel.size();
      if (n > size - currentFilePosition) {
         this.fileChannel.position(size);
         return size - currentFilePosition;
      } else {
         this.fileChannel.position(currentFilePosition + n);
         return n;
      }
   }

   public synchronized void close() throws IOException {
      this.fileChannel.close();
      StorageUtils.dispose(this.byteBuffer);
   }

   protected void finalize() throws IOException {
      this.close();
   }
}
