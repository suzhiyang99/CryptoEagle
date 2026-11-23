package org.spark_project.jetty.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritePendingException;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.spark_project.jetty.http.HttpContent;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.IteratingNestedCallback;
import org.spark_project.jetty.util.SharedBlockingCallback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HttpOutput extends ServletOutputStream implements Runnable {
   private static Logger LOG = Log.getLogger(HttpOutput.class);
   private final HttpChannel _channel;
   private final SharedBlockingCallback _writeBlocker;
   private Interceptor _interceptor;
   private long _written;
   private ByteBuffer _aggregate;
   private int _bufferSize;
   private int _commitSize;
   private WriteListener _writeListener;
   private volatile Throwable _onError;
   private final AtomicReference<OutputState> _state;

   public HttpOutput(HttpChannel channel) {
      this._state = new AtomicReference(HttpOutput.OutputState.OPEN);
      this._channel = channel;
      this._interceptor = channel;
      this._writeBlocker = new WriteBlocker(channel);
      HttpConfiguration config = channel.getHttpConfiguration();
      this._bufferSize = config.getOutputBufferSize();
      this._commitSize = config.getOutputAggregationSize();
      if (this._commitSize > this._bufferSize) {
         LOG.warn("OutputAggregationSize {} exceeds bufferSize {}", this._commitSize, this._bufferSize);
         this._commitSize = this._bufferSize;
      }

   }

   public HttpChannel getHttpChannel() {
      return this._channel;
   }

   public Interceptor getInterceptor() {
      return this._interceptor;
   }

   public void setInterceptor(Interceptor filter) {
      this._interceptor = filter;
   }

   public boolean isWritten() {
      return this._written > 0L;
   }

   public long getWritten() {
      return this._written;
   }

   public void reopen() {
      this._state.set(HttpOutput.OutputState.OPEN);
   }

   private boolean isLastContentToWrite(int len) {
      this._written += (long)len;
      return this._channel.getResponse().isAllContentWritten(this._written);
   }

   public boolean isAllContentWritten() {
      return this._channel.getResponse().isAllContentWritten(this._written);
   }

   protected SharedBlockingCallback.Blocker acquireWriteBlockingCallback() throws IOException {
      return this._writeBlocker.acquire();
   }

   private void write(ByteBuffer content, boolean complete) throws IOException {
      try {
         SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
         Throwable var4 = null;

         try {
            this.write(content, complete, blocker);
            blocker.block();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (blocker != null) {
               if (var4 != null) {
                  try {
                     blocker.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  blocker.close();
               }
            }

         }

      } catch (Exception var16) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var16);
         }

         this.abort(var16);
         if (var16 instanceof IOException) {
            throw var16;
         } else {
            throw new IOException(var16);
         }
      }
   }

   protected void write(ByteBuffer content, boolean complete, Callback callback) {
      this._interceptor.write(content, complete, callback);
   }

   private void abort(Throwable failure) {
      this.closed();
      this._channel.abort(failure);
   }

   public void close() {
      while(true) {
         OutputState state = (OutputState)this._state.get();
         switch (state) {
            case CLOSED:
               return;
            case UNREADY:
               if (this._state.compareAndSet(state, HttpOutput.OutputState.ERROR)) {
                  this._writeListener.onError((Throwable)(this._onError == null ? new EofException("Async close") : this._onError));
               }
               break;
            default:
               if (this._state.compareAndSet(state, HttpOutput.OutputState.CLOSED)) {
                  try {
                     this.write(BufferUtil.hasContent(this._aggregate) ? this._aggregate : BufferUtil.EMPTY_BUFFER, !this._channel.getResponse().isIncluding());
                  } catch (IOException var6) {
                  } finally {
                     this.releaseBuffer();
                  }

                  return;
               }
         }
      }
   }

   void closed() {
      while(true) {
         OutputState state = (OutputState)this._state.get();
         switch (state) {
            case CLOSED:
               return;
            case UNREADY:
               if (this._state.compareAndSet(state, HttpOutput.OutputState.ERROR)) {
                  this._writeListener.onError((Throwable)(this._onError == null ? new EofException("Async closed") : this._onError));
               }
               break;
            default:
               if (this._state.compareAndSet(state, HttpOutput.OutputState.CLOSED)) {
                  try {
                     this._channel.getResponse().closeOutput();
                  } catch (Throwable var6) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug(var6);
                     }

                     this.abort(var6);
                  } finally {
                     this.releaseBuffer();
                  }

                  return;
               }
         }
      }
   }

   private void releaseBuffer() {
      if (this._aggregate != null) {
         this._channel.getConnector().getByteBufferPool().release(this._aggregate);
         this._aggregate = null;
      }

   }

   public boolean isClosed() {
      return this._state.get() == HttpOutput.OutputState.CLOSED;
   }

   public void flush() throws IOException {
      while(true) {
         switch ((OutputState)this._state.get()) {
            case CLOSED:
               return;
            case UNREADY:
            case PENDING:
               throw new WritePendingException();
            case OPEN:
               this.write(BufferUtil.hasContent(this._aggregate) ? this._aggregate : BufferUtil.EMPTY_BUFFER, false);
               return;
            case ASYNC:
               throw new IllegalStateException("isReady() not called");
            case READY:
               if (!this._state.compareAndSet(HttpOutput.OutputState.READY, HttpOutput.OutputState.PENDING)) {
                  break;
               }

               (new AsyncFlush()).iterate();
               return;
            case ERROR:
               throw new EofException(this._onError);
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      while(true) {
         switch ((OutputState)this._state.get()) {
            case CLOSED:
               throw new EofException("Closed");
            case UNREADY:
            case PENDING:
               throw new WritePendingException();
            case OPEN:
               int capacity = this.getBufferSize();
               boolean last = this.isLastContentToWrite(len);
               if (!last && len <= this._commitSize) {
                  if (this._aggregate == null) {
                     this._aggregate = this._channel.getByteBufferPool().acquire(capacity, this._interceptor.isOptimizedForDirectBuffers());
                  }

                  int filled = BufferUtil.fill(this._aggregate, b, off, len);
                  if (filled == len && !BufferUtil.isFull(this._aggregate)) {
                     return;
                  }

                  off += filled;
                  len -= filled;
               }

               if (BufferUtil.hasContent(this._aggregate)) {
                  this.write(this._aggregate, last && len == 0);
                  if (len > 0 && !last && len <= this._commitSize && len <= BufferUtil.space(this._aggregate)) {
                     BufferUtil.append(this._aggregate, b, off, len);
                     return;
                  }
               }

               if (len > 0) {
                  ByteBuffer view = ByteBuffer.wrap(b, off, len);

                  while(len > this.getBufferSize()) {
                     int p = view.position();
                     int l = p + this.getBufferSize();
                     view.limit(p + this.getBufferSize());
                     this.write(view, false);
                     len -= this.getBufferSize();
                     view.limit(l + Math.min(len, this.getBufferSize()));
                     view.position(l);
                  }

                  this.write(view, last);
               } else if (last) {
                  this.write(BufferUtil.EMPTY_BUFFER, true);
               }

               if (last) {
                  this.closed();
               }

               return;
            case ASYNC:
               throw new IllegalStateException("isReady() not called");
            case READY:
               if (!this._state.compareAndSet(HttpOutput.OutputState.READY, HttpOutput.OutputState.PENDING)) {
                  break;
               }

               boolean last = this.isLastContentToWrite(len);
               if (!last && len <= this._commitSize) {
                  if (this._aggregate == null) {
                     this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
                  }

                  int filled = BufferUtil.fill(this._aggregate, b, off, len);
                  if (filled == len && !BufferUtil.isFull(this._aggregate)) {
                     if (!this._state.compareAndSet(HttpOutput.OutputState.PENDING, HttpOutput.OutputState.ASYNC)) {
                        throw new IllegalStateException();
                     }

                     return;
                  }

                  off += filled;
                  len -= filled;
               }

               (new AsyncWrite(b, off, len, last)).iterate();
               return;
            case ERROR:
               throw new EofException(this._onError);
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void write(ByteBuffer buffer) throws IOException {
      while(true) {
         switch ((OutputState)this._state.get()) {
            case CLOSED:
               throw new EofException("Closed");
            case UNREADY:
            case PENDING:
               throw new WritePendingException();
            case OPEN:
               int len = BufferUtil.length(buffer);
               boolean last = this.isLastContentToWrite(len);
               if (BufferUtil.hasContent(this._aggregate)) {
                  this.write(this._aggregate, last && len == 0);
               }

               if (len > 0) {
                  this.write(buffer, last);
               } else if (last) {
                  this.write(BufferUtil.EMPTY_BUFFER, true);
               }

               if (last) {
                  this.closed();
               }

               return;
            case ASYNC:
               throw new IllegalStateException("isReady() not called");
            case READY:
               if (!this._state.compareAndSet(HttpOutput.OutputState.READY, HttpOutput.OutputState.PENDING)) {
                  break;
               }

               boolean last = this.isLastContentToWrite(buffer.remaining());
               (new AsyncWrite(buffer, last)).iterate();
               return;
            case ERROR:
               throw new EofException(this._onError);
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void write(int b) throws IOException {
      ++this._written;
      boolean complete = this._channel.getResponse().isAllContentWritten(this._written);

      while(true) {
         switch ((OutputState)this._state.get()) {
            case CLOSED:
               throw new EofException("Closed");
            case UNREADY:
            case PENDING:
               throw new WritePendingException();
            case OPEN:
               if (this._aggregate == null) {
                  this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
               }

               BufferUtil.append(this._aggregate, (byte)b);
               if (complete || BufferUtil.isFull(this._aggregate)) {
                  this.write(this._aggregate, complete);
                  if (complete) {
                     this.closed();
                  }
               }

               return;
            case ASYNC:
               throw new IllegalStateException("isReady() not called");
            case READY:
               if (this._state.compareAndSet(HttpOutput.OutputState.READY, HttpOutput.OutputState.PENDING)) {
                  if (this._aggregate == null) {
                     this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
                  }

                  BufferUtil.append(this._aggregate, (byte)b);
                  if (!complete && !BufferUtil.isFull(this._aggregate)) {
                     if (!this._state.compareAndSet(HttpOutput.OutputState.PENDING, HttpOutput.OutputState.ASYNC)) {
                        throw new IllegalStateException();
                     }

                     return;
                  }

                  (new AsyncFlush()).iterate();
                  return;
               }
               break;
            case ERROR:
               throw new EofException(this._onError);
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void print(String s) throws IOException {
      if (this.isClosed()) {
         throw new IOException("Closed");
      } else {
         this.write(s.getBytes(this._channel.getResponse().getCharacterEncoding()));
      }
   }

   public void sendContent(ByteBuffer content) throws IOException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendContent({})", BufferUtil.toDetailString(content));
      }

      this.write(content, true);
      this.closed();
   }

   public void sendContent(InputStream in) throws IOException {
      try {
         SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
         Throwable var3 = null;

         try {
            (new InputStreamWritingCB(in, blocker)).iterate();
            blocker.block();
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (blocker != null) {
               if (var3 != null) {
                  try {
                     blocker.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  blocker.close();
               }
            }

         }

      } catch (Throwable var15) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var15);
         }

         this.abort(var15);
         throw var15;
      }
   }

   public void sendContent(ReadableByteChannel in) throws IOException {
      try {
         SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
         Throwable var3 = null;

         try {
            (new ReadableByteChannelWritingCB(in, blocker)).iterate();
            blocker.block();
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (blocker != null) {
               if (var3 != null) {
                  try {
                     blocker.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  blocker.close();
               }
            }

         }

      } catch (Throwable var15) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var15);
         }

         this.abort(var15);
         throw var15;
      }
   }

   public void sendContent(HttpContent content) throws IOException {
      try {
         SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
         Throwable var3 = null;

         try {
            this.sendContent((HttpContent)content, blocker);
            blocker.block();
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (blocker != null) {
               if (var3 != null) {
                  try {
                     blocker.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  blocker.close();
               }
            }

         }

      } catch (Throwable var15) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var15);
         }

         this.abort(var15);
         throw var15;
      }
   }

   public void sendContent(ByteBuffer content, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendContent(buffer={},{})", BufferUtil.toDetailString(content), callback);
      }

      this.write(content, true, new Callback.Nested(callback) {
         public void succeeded() {
            HttpOutput.this.closed();
            super.succeeded();
         }

         public void failed(Throwable x) {
            HttpOutput.this.abort(x);
            super.failed(x);
         }
      });
   }

   public void sendContent(InputStream in, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendContent(stream={},{})", in, callback);
      }

      (new InputStreamWritingCB(in, callback)).iterate();
   }

   public void sendContent(ReadableByteChannel in, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendContent(channel={},{})", in, callback);
      }

      (new ReadableByteChannelWritingCB(in, callback)).iterate();
   }

   public void sendContent(HttpContent httpContent, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendContent(http={},{})", httpContent, callback);
      }

      if (BufferUtil.hasContent(this._aggregate)) {
         callback.failed(new IOException("cannot sendContent() after write()"));
      } else if (this._channel.isCommitted()) {
         callback.failed(new IOException("cannot sendContent(), output already committed"));
      } else {
         label51:
         while(true) {
            switch ((OutputState)this._state.get()) {
               case CLOSED:
                  callback.failed(new EofException("Closed"));
                  return;
               case OPEN:
                  if (this._state.compareAndSet(HttpOutput.OutputState.OPEN, HttpOutput.OutputState.PENDING)) {
                     break label51;
                  }
                  break;
               case ERROR:
                  callback.failed(new EofException(this._onError));
                  return;
               default:
                  throw new IllegalStateException();
            }
         }

         ByteBuffer buffer = this._channel.useDirectBuffers() ? httpContent.getDirectBuffer() : null;
         if (buffer == null) {
            buffer = httpContent.getIndirectBuffer();
         }

         if (buffer != null) {
            this.sendContent(buffer, callback);
         } else {
            try {
               ReadableByteChannel rbc = httpContent.getReadableByteChannel();
               if (rbc != null) {
                  this.sendContent(rbc, callback);
               } else {
                  InputStream in = httpContent.getInputStream();
                  if (in != null) {
                     this.sendContent(in, callback);
                  } else {
                     throw new IllegalArgumentException("unknown content for " + httpContent);
                  }
               }
            } catch (Throwable th) {
               this.abort(th);
               callback.failed(th);
            }
         }
      }
   }

   public int getBufferSize() {
      return this._bufferSize;
   }

   public void setBufferSize(int size) {
      this._bufferSize = size;
      this._commitSize = size;
   }

   public void recycle() {
      this._interceptor = this._channel;
      HttpConfiguration config = this._channel.getHttpConfiguration();
      this._bufferSize = config.getOutputBufferSize();
      this._commitSize = config.getOutputAggregationSize();
      if (this._commitSize > this._bufferSize) {
         this._commitSize = this._bufferSize;
      }

      this.releaseBuffer();
      this._written = 0L;
      this._writeListener = null;
      this._onError = null;
      this.reopen();
   }

   public void resetBuffer() {
      this._interceptor.resetBuffer();
      if (BufferUtil.hasContent(this._aggregate)) {
         BufferUtil.clear(this._aggregate);
      }

      this._written = 0L;
      this.reopen();
   }

   public void setWriteListener(WriteListener writeListener) {
      if (!this._channel.getState().isAsync()) {
         throw new IllegalStateException("!ASYNC");
      } else if (this._state.compareAndSet(HttpOutput.OutputState.OPEN, HttpOutput.OutputState.READY)) {
         this._writeListener = writeListener;
         if (this._channel.getState().onWritePossible()) {
            this._channel.execute(this._channel);
         }

      } else {
         throw new IllegalStateException();
      }
   }

   public boolean isReady() {
      while(true) {
         switch ((OutputState)this._state.get()) {
            case CLOSED:
               return true;
            case UNREADY:
               return false;
            case OPEN:
               return true;
            case ASYNC:
               if (!this._state.compareAndSet(HttpOutput.OutputState.ASYNC, HttpOutput.OutputState.READY)) {
                  break;
               }

               return true;
            case READY:
               return true;
            case PENDING:
               if (!this._state.compareAndSet(HttpOutput.OutputState.PENDING, HttpOutput.OutputState.UNREADY)) {
                  break;
               }

               return false;
            case ERROR:
               return true;
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void run() {
      while(true) {
         OutputState state = (OutputState)this._state.get();
         if (this._onError != null) {
            switch (state) {
               case CLOSED:
               case ERROR:
                  this._onError = null;
                  break;
               default:
                  if (!this._state.compareAndSet(state, HttpOutput.OutputState.ERROR)) {
                     continue;
                  }

                  Throwable th = this._onError;
                  this._onError = null;
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("onError", th);
                  }

                  this._writeListener.onError(th);
                  this.close();
            }
         } else {
            switch ((OutputState)this._state.get()) {
               case CLOSED:
               case UNREADY:
               case ASYNC:
               case READY:
               case PENDING:
                  try {
                     this._writeListener.onWritePossible();
                     break;
                  } catch (Throwable e) {
                     this._onError = e;
                     continue;
                  }
               case OPEN:
               default:
                  this._onError = new IllegalStateException("state=" + this._state.get());
                  continue;
            }
         }

         return;
      }
   }

   private void close(Closeable resource) {
      try {
         resource.close();
      } catch (Throwable x) {
         LOG.ignore(x);
      }

   }

   public String toString() {
      return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._state.get());
   }

   public interface Interceptor {
      void write(ByteBuffer var1, boolean var2, Callback var3);

      Interceptor getNextInterceptor();

      boolean isOptimizedForDirectBuffers();

      default void resetBuffer() throws IllegalStateException {
         Interceptor next = this.getNextInterceptor();
         if (next != null) {
            next.resetBuffer();
         }

      }
   }

   private static enum OutputState {
      OPEN,
      ASYNC,
      READY,
      PENDING,
      UNREADY,
      ERROR,
      CLOSED;
   }

   private abstract class AsyncICB extends IteratingCallback {
      final boolean _last;

      AsyncICB(boolean last) {
         this._last = last;
      }

      protected void onCompleteSuccess() {
         while(true) {
            OutputState last = (OutputState)HttpOutput.this._state.get();
            switch (last) {
               case CLOSED:
                  break;
               case UNREADY:
                  if (!HttpOutput.this._state.compareAndSet(HttpOutput.OutputState.UNREADY, HttpOutput.OutputState.READY)) {
                     continue;
                  }

                  if (this._last) {
                     HttpOutput.this.closed();
                  }

                  if (HttpOutput.this._channel.getState().onWritePossible()) {
                     HttpOutput.this._channel.execute(HttpOutput.this._channel);
                  }
                  break;
               case PENDING:
                  if (!HttpOutput.this._state.compareAndSet(HttpOutput.OutputState.PENDING, HttpOutput.OutputState.ASYNC)) {
                     continue;
                  }
                  break;
               default:
                  throw new IllegalStateException();
            }

            return;
         }
      }

      public void onCompleteFailure(Throwable e) {
         HttpOutput.this._onError = (Throwable)(e == null ? new IOException() : e);
         if (HttpOutput.this._channel.getState().onWritePossible()) {
            HttpOutput.this._channel.execute(HttpOutput.this._channel);
         }

      }
   }

   private class AsyncFlush extends AsyncICB {
      protected volatile boolean _flushed;

      public AsyncFlush() {
         super(false);
      }

      protected IteratingCallback.Action process() {
         if (BufferUtil.hasContent(HttpOutput.this._aggregate)) {
            this._flushed = true;
            HttpOutput.this.write(HttpOutput.this._aggregate, false, this);
            return IteratingCallback.Action.SCHEDULED;
         } else if (!this._flushed) {
            this._flushed = true;
            HttpOutput.this.write(BufferUtil.EMPTY_BUFFER, false, this);
            return IteratingCallback.Action.SCHEDULED;
         } else {
            return IteratingCallback.Action.SUCCEEDED;
         }
      }
   }

   private class AsyncWrite extends AsyncICB {
      private final ByteBuffer _buffer;
      private final ByteBuffer _slice;
      private final int _len;
      protected volatile boolean _completed;

      public AsyncWrite(byte[] b, int off, int len, boolean last) {
         super(last);
         this._buffer = ByteBuffer.wrap(b, off, len);
         this._len = len;
         this._slice = this._len < HttpOutput.this.getBufferSize() ? null : this._buffer.duplicate();
      }

      public AsyncWrite(ByteBuffer buffer, boolean last) {
         super(last);
         this._buffer = buffer;
         this._len = buffer.remaining();
         if (!this._buffer.isDirect() && this._len >= HttpOutput.this.getBufferSize()) {
            this._slice = this._buffer.duplicate();
         } else {
            this._slice = null;
         }

      }

      protected IteratingCallback.Action process() {
         if (BufferUtil.hasContent(HttpOutput.this._aggregate)) {
            this._completed = this._len == 0;
            HttpOutput.this.write(HttpOutput.this._aggregate, this._last && this._completed, this);
            return IteratingCallback.Action.SCHEDULED;
         } else if (!this._last && this._len < BufferUtil.space(HttpOutput.this._aggregate) && this._len < HttpOutput.this._commitSize) {
            int position = BufferUtil.flipToFill(HttpOutput.this._aggregate);
            BufferUtil.put(this._buffer, HttpOutput.this._aggregate);
            BufferUtil.flipToFlush(HttpOutput.this._aggregate, position);
            return IteratingCallback.Action.SUCCEEDED;
         } else if (!this._buffer.hasRemaining()) {
            if (this._last && !this._completed) {
               this._completed = true;
               HttpOutput.this.write(BufferUtil.EMPTY_BUFFER, true, this);
               return IteratingCallback.Action.SCHEDULED;
            } else {
               if (HttpOutput.LOG.isDebugEnabled() && this._completed) {
                  HttpOutput.LOG.debug("EOF of {}", this);
               }

               return IteratingCallback.Action.SUCCEEDED;
            }
         } else if (this._slice == null) {
            this._completed = true;
            HttpOutput.this.write(this._buffer, this._last, this);
            return IteratingCallback.Action.SCHEDULED;
         } else {
            int p = this._buffer.position();
            int l = Math.min(HttpOutput.this.getBufferSize(), this._buffer.remaining());
            int pl = p + l;
            this._slice.limit(pl);
            this._buffer.position(pl);
            this._slice.position(p);
            this._completed = !this._buffer.hasRemaining();
            HttpOutput.this.write(this._slice, this._last && this._completed, this);
            return IteratingCallback.Action.SCHEDULED;
         }
      }
   }

   private class InputStreamWritingCB extends IteratingNestedCallback {
      private final InputStream _in;
      private final ByteBuffer _buffer;
      private boolean _eof;

      public InputStreamWritingCB(InputStream in, Callback callback) {
         super(callback);
         this._in = in;
         this._buffer = HttpOutput.this._channel.getByteBufferPool().acquire(HttpOutput.this.getBufferSize(), false);
      }

      protected IteratingCallback.Action process() throws Exception {
         if (this._eof) {
            if (HttpOutput.LOG.isDebugEnabled()) {
               HttpOutput.LOG.debug("EOF of {}", this);
            }

            this._in.close();
            HttpOutput.this.closed();
            HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
            return IteratingCallback.Action.SUCCEEDED;
         } else {
            int len = 0;

            while(len < this._buffer.capacity() && !this._eof) {
               int r = this._in.read(this._buffer.array(), this._buffer.arrayOffset() + len, this._buffer.capacity() - len);
               if (r < 0) {
                  this._eof = true;
               } else {
                  len += r;
               }
            }

            this._buffer.position(0);
            this._buffer.limit(len);
            HttpOutput.this.write(this._buffer, this._eof, this);
            return IteratingCallback.Action.SCHEDULED;
         }
      }

      public void onCompleteFailure(Throwable x) {
         HttpOutput.this.abort(x);
         HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
         HttpOutput.this.close(this._in);
         super.onCompleteFailure(x);
      }
   }

   private class ReadableByteChannelWritingCB extends IteratingNestedCallback {
      private final ReadableByteChannel _in;
      private final ByteBuffer _buffer;
      private boolean _eof;

      public ReadableByteChannelWritingCB(ReadableByteChannel in, Callback callback) {
         super(callback);
         this._in = in;
         this._buffer = HttpOutput.this._channel.getByteBufferPool().acquire(HttpOutput.this.getBufferSize(), HttpOutput.this._channel.useDirectBuffers());
      }

      protected IteratingCallback.Action process() throws Exception {
         if (this._eof) {
            if (HttpOutput.LOG.isDebugEnabled()) {
               HttpOutput.LOG.debug("EOF of {}", this);
            }

            this._in.close();
            HttpOutput.this.closed();
            HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
            return IteratingCallback.Action.SUCCEEDED;
         } else {
            BufferUtil.clearToFill(this._buffer);

            while(this._buffer.hasRemaining() && !this._eof) {
               this._eof = this._in.read(this._buffer) < 0;
            }

            BufferUtil.flipToFlush(this._buffer, 0);
            HttpOutput.this.write(this._buffer, this._eof, this);
            return IteratingCallback.Action.SCHEDULED;
         }
      }

      public void onCompleteFailure(Throwable x) {
         HttpOutput.this.abort(x);
         HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
         HttpOutput.this.close(this._in);
         super.onCompleteFailure(x);
      }
   }

   private static class WriteBlocker extends SharedBlockingCallback {
      private final HttpChannel _channel;

      private WriteBlocker(HttpChannel channel) {
         this._channel = channel;
      }

      protected long getIdleTimeout() {
         long blockingTimeout = this._channel.getHttpConfiguration().getBlockingTimeout();
         return blockingTimeout == 0L ? this._channel.getIdleTimeout() : blockingTimeout;
      }
   }
}
