package org.spark_project.jetty.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

/** @deprecated */
@Deprecated
public class BlockingCallback implements Callback.NonBlocking {
   private static final Logger LOG = Log.getLogger(BlockingCallback.class);
   private static Throwable SUCCEEDED = new ConstantThrowable("SUCCEEDED");
   private final CountDownLatch _latch = new CountDownLatch(1);
   private final AtomicReference<Throwable> _state = new AtomicReference();

   public void succeeded() {
      if (this._state.compareAndSet((Object)null, SUCCEEDED)) {
         this._latch.countDown();
      }

   }

   public void failed(Throwable cause) {
      if (this._state.compareAndSet((Object)null, cause)) {
         this._latch.countDown();
      }

   }

   public void block() throws IOException {
      try {
         this._latch.await();
         Throwable state = (Throwable)this._state.get();
         if (state != SUCCEEDED) {
            if (state instanceof IOException) {
               throw (IOException)state;
            }

            if (state instanceof CancellationException) {
               throw (CancellationException)state;
            }

            throw new IOException(state);
         }
      } catch (final InterruptedException e) {
         throw new InterruptedIOException() {
            {
               this.initCause(e);
            }
         };
      } finally {
         this._state.set((Object)null);
      }

   }

   public String toString() {
      return String.format("%s@%x{%s}", BlockingCallback.class.getSimpleName(), this.hashCode(), this._state.get());
   }
}
