package org.apache.spark.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.base.Preconditions;
import org.spark_project.guava.base.Throwables;

public class ReadAheadInputStream extends InputStream {
   private static final Logger logger = LoggerFactory.getLogger(ReadAheadInputStream.class);
   private ReentrantLock stateChangeLock = new ReentrantLock();
   @GuardedBy("stateChangeLock")
   private ByteBuffer activeBuffer;
   @GuardedBy("stateChangeLock")
   private ByteBuffer readAheadBuffer;
   @GuardedBy("stateChangeLock")
   private boolean endOfStream;
   @GuardedBy("stateChangeLock")
   private boolean readInProgress;
   @GuardedBy("stateChangeLock")
   private boolean readAborted;
   @GuardedBy("stateChangeLock")
   private Throwable readException;
   @GuardedBy("stateChangeLock")
   private boolean isClosed;
   @GuardedBy("stateChangeLock")
   private boolean isUnderlyingInputStreamBeingClosed;
   @GuardedBy("stateChangeLock")
   private boolean isReading;
   private final int readAheadThresholdInBytes;
   private final InputStream underlyingInputStream;
   private final ExecutorService executorService = ThreadUtils.newDaemonSingleThreadExecutor("read-ahead");
   private final Condition asyncReadComplete;
   private static final ThreadLocal<byte[]> oneByte = ThreadLocal.withInitial(() -> new byte[1]);

   public ReadAheadInputStream(InputStream inputStream, int bufferSizeInBytes, int readAheadThresholdInBytes) {
      this.asyncReadComplete = this.stateChangeLock.newCondition();
      Preconditions.checkArgument(bufferSizeInBytes > 0, "bufferSizeInBytes should be greater than 0, but the value is " + bufferSizeInBytes);
      Preconditions.checkArgument(readAheadThresholdInBytes > 0 && readAheadThresholdInBytes < bufferSizeInBytes, "readAheadThresholdInBytes should be greater than 0 and less than bufferSizeInBytes, but the value is " + readAheadThresholdInBytes);
      this.activeBuffer = ByteBuffer.allocate(bufferSizeInBytes);
      this.readAheadBuffer = ByteBuffer.allocate(bufferSizeInBytes);
      this.readAheadThresholdInBytes = readAheadThresholdInBytes;
      this.underlyingInputStream = inputStream;
      this.activeBuffer.flip();
      this.readAheadBuffer.flip();
   }

   private boolean isEndOfStream() {
      return !this.activeBuffer.hasRemaining() && !this.readAheadBuffer.hasRemaining() && this.endOfStream;
   }

   private void checkReadException() throws IOException {
      if (this.readAborted) {
         Throwables.propagateIfPossible(this.readException, IOException.class);
         throw new IOException(this.readException);
      }
   }

   private void readAsync() throws IOException {
      this.stateChangeLock.lock();
      final byte[] arr = this.readAheadBuffer.array();

      label44: {
         try {
            if (!this.endOfStream && !this.readInProgress) {
               this.checkReadException();
               this.readAheadBuffer.position(0);
               this.readAheadBuffer.flip();
               this.readInProgress = true;
               break label44;
            }
         } finally {
            this.stateChangeLock.unlock();
         }

         return;
      }

      this.executorService.execute(new Runnable() {
         public void run() {
            ReadAheadInputStream.this.stateChangeLock.lock();

            try {
               if (ReadAheadInputStream.this.isClosed) {
                  ReadAheadInputStream.this.readInProgress = false;
                  return;
               }

               ReadAheadInputStream.this.isReading = true;
            } finally {
               ReadAheadInputStream.this.stateChangeLock.unlock();
            }

            int read = 0;
            Throwable exception = null;

            try {
               do {
                  read = ReadAheadInputStream.this.underlyingInputStream.read(arr);
               } while(0 == read);
            } catch (Throwable ex) {
               exception = ex;
               if (ex instanceof Error) {
                  throw (Error)ex;
               }
            } finally {
               ReadAheadInputStream.this.stateChangeLock.lock();
               if (read >= 0 && !(exception instanceof EOFException)) {
                  if (exception != null) {
                     ReadAheadInputStream.this.readAborted = true;
                     ReadAheadInputStream.this.readException = exception;
                  } else {
                     ReadAheadInputStream.this.readAheadBuffer.limit(read);
                  }
               } else {
                  ReadAheadInputStream.this.endOfStream = true;
               }

               ReadAheadInputStream.this.readInProgress = false;
               ReadAheadInputStream.this.signalAsyncReadComplete();
               ReadAheadInputStream.this.stateChangeLock.unlock();
               ReadAheadInputStream.this.closeUnderlyingInputStreamIfNecessary();
            }

         }
      });
   }

   private void closeUnderlyingInputStreamIfNecessary() {
      boolean needToCloseUnderlyingInputStream = false;
      this.stateChangeLock.lock();

      try {
         this.isReading = false;
         if (this.isClosed && !this.isUnderlyingInputStreamBeingClosed) {
            needToCloseUnderlyingInputStream = true;
         }
      } finally {
         this.stateChangeLock.unlock();
      }

      if (needToCloseUnderlyingInputStream) {
         try {
            this.underlyingInputStream.close();
         } catch (IOException e) {
            logger.warn(e.getMessage(), e);
         }
      }

   }

   private void signalAsyncReadComplete() {
      this.stateChangeLock.lock();

      try {
         this.asyncReadComplete.signalAll();
      } finally {
         this.stateChangeLock.unlock();
      }

   }

   private void waitForAsyncReadComplete() throws IOException {
      this.stateChangeLock.lock();

      try {
         while(this.readInProgress) {
            this.asyncReadComplete.await();
         }
      } catch (InterruptedException e) {
         InterruptedIOException iio = new InterruptedIOException(e.getMessage());
         iio.initCause(e);
         throw iio;
      } finally {
         this.stateChangeLock.unlock();
      }

      this.checkReadException();
   }

   public int read() throws IOException {
      byte[] oneByteArray = (byte[])oneByte.get();
      return this.read(oneByteArray, 0, 1) == -1 ? -1 : oneByteArray[0] & 255;
   }

   public int read(byte[] b, int offset, int len) throws IOException {
      if (offset >= 0 && len >= 0 && len <= b.length - offset) {
         if (len == 0) {
            return 0;
         } else {
            this.stateChangeLock.lock();

            int var4;
            try {
               var4 = this.readInternal(b, offset, len);
            } finally {
               this.stateChangeLock.unlock();
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void swapBuffers() {
      ByteBuffer temp = this.activeBuffer;
      this.activeBuffer = this.readAheadBuffer;
      this.readAheadBuffer = temp;
   }

   private int readInternal(byte[] b, int offset, int len) throws IOException {
      assert this.stateChangeLock.isLocked();

      if (!this.activeBuffer.hasRemaining()) {
         this.waitForAsyncReadComplete();
         if (this.readAheadBuffer.hasRemaining()) {
            this.swapBuffers();
         } else {
            this.readAsync();
            this.waitForAsyncReadComplete();
            if (this.isEndOfStream()) {
               return -1;
            }

            this.swapBuffers();
         }
      } else {
         this.checkReadException();
      }

      len = Math.min(len, this.activeBuffer.remaining());
      this.activeBuffer.get(b, offset, len);
      if (this.activeBuffer.remaining() <= this.readAheadThresholdInBytes && !this.readAheadBuffer.hasRemaining()) {
         this.readAsync();
      }

      return len;
   }

   public int available() throws IOException {
      this.stateChangeLock.lock();

      int var1;
      try {
         var1 = (int)Math.min(2147483647L, (long)this.activeBuffer.remaining() + (long)this.readAheadBuffer.remaining());
      } finally {
         this.stateChangeLock.unlock();
      }

      return var1;
   }

   public long skip(long n) throws IOException {
      if (n <= 0L) {
         return 0L;
      } else {
         this.stateChangeLock.lock();

         long skipped;
         try {
            skipped = this.skipInternal(n);
         } finally {
            this.stateChangeLock.unlock();
         }

         return skipped;
      }
   }

   private long skipInternal(long n) throws IOException {
      assert this.stateChangeLock.isLocked();

      this.waitForAsyncReadComplete();
      if (this.isEndOfStream()) {
         return 0L;
      } else if ((long)this.available() >= n) {
         int toSkip = (int)n;
         if (toSkip <= this.activeBuffer.remaining()) {
            this.activeBuffer.position(toSkip + this.activeBuffer.position());
            if (this.activeBuffer.remaining() <= this.readAheadThresholdInBytes && !this.readAheadBuffer.hasRemaining()) {
               this.readAsync();
            }

            return n;
         } else {
            toSkip -= this.activeBuffer.remaining();
            this.activeBuffer.position(0);
            this.activeBuffer.flip();
            this.readAheadBuffer.position(toSkip + this.readAheadBuffer.position());
            this.swapBuffers();
            this.readAsync();
            return n;
         }
      } else {
         int skippedBytes = this.available();
         long toSkip = n - (long)skippedBytes;
         this.activeBuffer.position(0);
         this.activeBuffer.flip();
         this.readAheadBuffer.position(0);
         this.readAheadBuffer.flip();
         long skippedFromInputStream = this.underlyingInputStream.skip(toSkip);
         this.readAsync();
         return (long)skippedBytes + skippedFromInputStream;
      }
   }

   public void close() throws IOException {
      boolean isSafeToCloseUnderlyingInputStream = false;
      this.stateChangeLock.lock();

      try {
         if (this.isClosed) {
            return;
         }

         this.isClosed = true;
         if (!this.isReading) {
            isSafeToCloseUnderlyingInputStream = true;
            this.isUnderlyingInputStreamBeingClosed = true;
         }
      } finally {
         this.stateChangeLock.unlock();
      }

      try {
         this.executorService.shutdownNow();
         this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         InterruptedIOException iio = new InterruptedIOException(e.getMessage());
         iio.initCause(e);
         throw iio;
      } finally {
         if (isSafeToCloseUnderlyingInputStream) {
            this.underlyingInputStream.close();
         }

      }

   }
}
