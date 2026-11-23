package org.spark_project.jetty.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class InputStreamResponseListener extends Response.Listener.Adapter {
   private static final Logger LOG = Log.getLogger(InputStreamResponseListener.class);
   private static final DeferredContentProvider.Chunk EOF;
   private final Object lock = this;
   private final CountDownLatch responseLatch = new CountDownLatch(1);
   private final CountDownLatch resultLatch = new CountDownLatch(1);
   private final AtomicReference<InputStream> stream = new AtomicReference();
   private final Queue<DeferredContentProvider.Chunk> chunks = new ArrayDeque();
   private Response response;
   private Result result;
   private Throwable failure;
   private boolean closed;

   public InputStreamResponseListener() {
   }

   /** @deprecated */
   @Deprecated
   public InputStreamResponseListener(long maxBufferSize) {
   }

   public void onHeaders(Response response) {
      synchronized(this.lock) {
         this.response = response;
         this.responseLatch.countDown();
      }
   }

   public void onContent(Response response, ByteBuffer content, Callback callback) {
      if (content.remaining() == 0) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Skipped empty content {}", content);
         }

         callback.succeeded();
      } else {
         boolean closed;
         synchronized(this.lock) {
            closed = this.closed;
            if (!closed) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Queueing content {}", content);
               }

               this.chunks.add(new DeferredContentProvider.Chunk(content, callback));
               this.lock.notifyAll();
            }
         }

         if (closed) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("InputStream closed, ignored content {}", content);
            }

            callback.failed(new AsynchronousCloseException());
         }

      }
   }

   public void onSuccess(Response response) {
      synchronized(this.lock) {
         if (!this.closed) {
            this.chunks.add(EOF);
         }

         this.lock.notifyAll();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("End of content");
      }

   }

   public void onFailure(Response response, Throwable failure) {
      List<Callback> callbacks;
      synchronized(this.lock) {
         if (this.failure != null) {
            return;
         }

         this.failure = failure;
         callbacks = this.drain();
         this.lock.notifyAll();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Content failure", failure);
      }

      callbacks.forEach((callback) -> callback.failed(failure));
   }

   public void onComplete(Result result) {
      Throwable failure = result.getFailure();
      List<Callback> callbacks = Collections.emptyList();
      synchronized(this.lock) {
         this.result = result;
         if (result.isFailed() && this.failure == null) {
            this.failure = failure;
            callbacks = this.drain();
         }

         this.responseLatch.countDown();
         this.resultLatch.countDown();
         this.lock.notifyAll();
      }

      if (LOG.isDebugEnabled()) {
         if (failure == null) {
            LOG.debug("Result success");
         } else {
            LOG.debug("Result failure", failure);
         }
      }

      callbacks.forEach((callback) -> callback.failed(failure));
   }

   public Response get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
      boolean expired = !this.responseLatch.await(timeout, unit);
      if (expired) {
         throw new TimeoutException();
      } else {
         synchronized(this.lock) {
            if (this.response == null) {
               throw new ExecutionException(this.failure);
            } else {
               return this.response;
            }
         }
      }
   }

   public Result await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
      boolean expired = !this.resultLatch.await(timeout, unit);
      if (expired) {
         throw new TimeoutException();
      } else {
         synchronized(this.lock) {
            return this.result;
         }
      }
   }

   public InputStream getInputStream() {
      InputStream result = new Input();
      return this.stream.compareAndSet((Object)null, result) ? result : IO.getClosedStream();
   }

   private List<Callback> drain() {
      List<Callback> callbacks = new ArrayList();
      synchronized(this.lock) {
         while(true) {
            DeferredContentProvider.Chunk chunk = (DeferredContentProvider.Chunk)this.chunks.peek();
            if (chunk == null || chunk == EOF) {
               return callbacks;
            }

            callbacks.add(chunk.callback);
            this.chunks.poll();
         }
      }
   }

   static {
      EOF = new DeferredContentProvider.Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
   }

   private class Input extends InputStream {
      private Input() {
      }

      public int read() throws IOException {
         byte[] tmp = new byte[1];
         int read = this.read(tmp);
         return read < 0 ? read : tmp[0] & 255;
      }

      public int read(byte[] b, int offset, int length) throws IOException {
         try {
            Callback callback = null;
            int result;
            synchronized(InputStreamResponseListener.this.lock) {
               while(true) {
                  DeferredContentProvider.Chunk chunk = (DeferredContentProvider.Chunk)InputStreamResponseListener.this.chunks.peek();
                  if (chunk == InputStreamResponseListener.EOF) {
                     return -1;
                  }

                  if (chunk != null) {
                     ByteBuffer buffer = chunk.buffer;
                     result = Math.min(buffer.remaining(), length);
                     buffer.get(b, offset, result);
                     if (!buffer.hasRemaining()) {
                        callback = chunk.callback;
                        InputStreamResponseListener.this.chunks.poll();
                     }
                     break;
                  }

                  if (InputStreamResponseListener.this.failure != null) {
                     throw this.toIOException(InputStreamResponseListener.this.failure);
                  }

                  if (InputStreamResponseListener.this.closed) {
                     throw new AsynchronousCloseException();
                  }

                  InputStreamResponseListener.this.lock.wait();
               }
            }

            if (callback != null) {
               callback.succeeded();
            }

            return result;
         } catch (InterruptedException var11) {
            throw new InterruptedIOException();
         }
      }

      private IOException toIOException(Throwable failure) {
         return failure instanceof IOException ? (IOException)failure : new IOException(failure);
      }

      public void close() throws IOException {
         List<Callback> callbacks;
         synchronized(InputStreamResponseListener.this.lock) {
            if (InputStreamResponseListener.this.closed) {
               return;
            }

            InputStreamResponseListener.this.closed = true;
            callbacks = InputStreamResponseListener.this.drain();
            InputStreamResponseListener.this.lock.notifyAll();
         }

         if (InputStreamResponseListener.LOG.isDebugEnabled()) {
            InputStreamResponseListener.LOG.debug("InputStream close");
         }

         Throwable failure = new AsynchronousCloseException();
         callbacks.forEach((callback) -> callback.failed(failure));
         super.close();
      }
   }
}
