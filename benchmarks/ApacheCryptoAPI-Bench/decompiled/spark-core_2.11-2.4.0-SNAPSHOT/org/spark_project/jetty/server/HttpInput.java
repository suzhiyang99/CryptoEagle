package org.spark_project.jetty.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.spark_project.jetty.http.BadMessageException;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.io.RuntimeIOException;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HttpInput extends ServletInputStream implements Runnable {
   private static final Logger LOG = Log.getLogger(HttpInput.class);
   private static final Content EOF_CONTENT = new EofContent("EOF");
   private static final Content EARLY_EOF_CONTENT = new EofContent("EARLY_EOF");
   private final byte[] _oneByteBuffer = new byte[1];
   private final Deque<Content> _inputQ = new ArrayDeque();
   private final HttpChannelState _channelState;
   private ReadListener _listener;
   private State _state;
   private long _firstByteTimeStamp;
   private long _contentArrived;
   private long _contentConsumed;
   private long _blockUntil;
   protected static final State STREAM = new State() {
      public boolean blockForContent(HttpInput input) throws IOException {
         input.blockForContent();
         return true;
      }

      public String toString() {
         return "STREAM";
      }
   };
   protected static final State ASYNC = new State() {
      public int noContent() throws IOException {
         return 0;
      }

      public String toString() {
         return "ASYNC";
      }
   };
   protected static final State EARLY_EOF = new EOFState() {
      public int noContent() throws IOException {
         throw new EofException("Early EOF");
      }

      public String toString() {
         return "EARLY_EOF";
      }
   };
   protected static final State EOF = new EOFState() {
      public String toString() {
         return "EOF";
      }
   };
   protected static final State AEOF = new EOFState() {
      public String toString() {
         return "AEOF";
      }
   };

   public HttpInput(HttpChannelState state) {
      this._state = STREAM;
      this._firstByteTimeStamp = -1L;
      this._channelState = state;
   }

   protected HttpChannelState getHttpChannelState() {
      return this._channelState;
   }

   public void recycle() {
      synchronized(this._inputQ) {
         for(Content item = (Content)this._inputQ.poll(); item != null; item = (Content)this._inputQ.poll()) {
            item.failed((Throwable)null);
         }

         this._listener = null;
         this._state = STREAM;
         this._contentArrived = 0L;
         this._contentConsumed = 0L;
         this._firstByteTimeStamp = -1L;
         this._blockUntil = 0L;
      }
   }

   public int available() {
      int available = 0;
      boolean woken = false;
      synchronized(this._inputQ) {
         Content content = (Content)this._inputQ.peek();
         if (content == null) {
            try {
               this.produceContent();
            } catch (IOException e) {
               woken = this.failed(e);
            }

            content = (Content)this._inputQ.peek();
         }

         if (content != null) {
            available = this.remaining(content);
         }
      }

      if (woken) {
         this.wake();
      }

      return available;
   }

   private void wake() {
      HttpChannel channel = this._channelState.getHttpChannel();
      Executor executor = channel.getConnector().getServer().getThreadPool();
      executor.execute(channel);
   }

   private long getBlockingTimeout() {
      return this.getHttpChannelState().getHttpChannel().getHttpConfiguration().getBlockingTimeout();
   }

   public int read() throws IOException {
      int read = this.read(this._oneByteBuffer, 0, 1);
      if (read == 0) {
         throw new IllegalStateException("unready read=0");
      } else {
         return read < 0 ? -1 : this._oneByteBuffer[0] & 255;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      synchronized(this._inputQ) {
         if (!this.isAsync() && this._blockUntil == 0L) {
            long blockingTimeout = this.getBlockingTimeout();
            if (blockingTimeout > 0L) {
               this._blockUntil = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(blockingTimeout);
            }
         }

         long minRequestDataRate = this._channelState.getHttpChannel().getHttpConfiguration().getMinRequestDataRate();
         if (minRequestDataRate > 0L && this._firstByteTimeStamp != -1L) {
            long period = System.nanoTime() - this._firstByteTimeStamp;
            if (period > 0L) {
               long minimum_data = minRequestDataRate * TimeUnit.NANOSECONDS.toMillis(period) / TimeUnit.SECONDS.toMillis(1L);
               if (this._contentArrived < minimum_data) {
                  throw new BadMessageException(408, String.format("Request data rate < %d B/s", minRequestDataRate));
               }
            }
         }

         do {
            Content item = this.nextContent();
            if (item != null) {
               int l = this.get(item, b, off, len);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("{} read {} from {}", this, l, item);
               }

               this.consumeNonContent();
               return l;
            }
         } while(this._state.blockForContent(this));

         return this._state.noContent();
      }
   }

   protected void produceContent() throws IOException {
   }

   protected Content nextContent() throws IOException {
      Content content = this.pollContent();
      if (content == null && !this.isFinished()) {
         this.produceContent();
         content = this.pollContent();
      }

      return content;
   }

   protected Content pollContent() {
      Content content;
      for(content = (Content)this._inputQ.peek(); content != null && this.remaining(content) == 0; content = (Content)this._inputQ.peek()) {
         this._inputQ.poll();
         content.succeeded();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} consumed {}", this, content);
         }

         if (content == EOF_CONTENT) {
            if (this._listener == null) {
               this._state = EOF;
            } else {
               this._state = AEOF;
               boolean woken = this._channelState.onReadReady();
               if (woken) {
                  this.wake();
               }
            }
         } else if (content == EARLY_EOF_CONTENT) {
            this._state = EARLY_EOF;
         }
      }

      return content;
   }

   protected void consumeNonContent() {
      for(Content content = (Content)this._inputQ.peek(); content != null && this.remaining(content) == 0 && !(content instanceof EofContent); content = (Content)this._inputQ.peek()) {
         this._inputQ.poll();
         content.succeeded();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} consumed {}", this, content);
         }
      }

   }

   protected Content nextReadable() throws IOException {
      Content content = this.pollReadable();
      if (content == null && !this.isFinished()) {
         this.produceContent();
         content = this.pollReadable();
      }

      return content;
   }

   protected Content pollReadable() {
      for(Content content = (Content)this._inputQ.peek(); content != null; content = (Content)this._inputQ.peek()) {
         if (content == EOF_CONTENT || content == EARLY_EOF_CONTENT || this.remaining(content) > 0) {
            return content;
         }

         this._inputQ.poll();
         content.succeeded();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} consumed {}", this, content);
         }
      }

      return null;
   }

   protected int remaining(Content item) {
      return item.remaining();
   }

   protected int get(Content content, byte[] buffer, int offset, int length) {
      int l = Math.min(content.remaining(), length);
      content.getContent().get(buffer, offset, l);
      this._contentConsumed += (long)l;
      return l;
   }

   protected void skip(Content content, int length) {
      int l = Math.min(content.remaining(), length);
      ByteBuffer buffer = content.getContent();
      buffer.position(buffer.position() + l);
      this._contentConsumed += (long)l;
      if (l > 0 && !content.hasContent()) {
         this.pollContent();
      }

   }

   protected void blockForContent() throws IOException {
      try {
         long timeout = 0L;
         if (this._blockUntil != 0L) {
            timeout = TimeUnit.NANOSECONDS.toMillis(this._blockUntil - System.nanoTime());
            if (timeout <= 0L) {
               throw new TimeoutException();
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} blocking for content timeout={}", this, timeout);
         }

         if (timeout > 0L) {
            this._inputQ.wait(timeout);
         } else {
            this._inputQ.wait();
         }

         if (this._blockUntil != 0L && TimeUnit.NANOSECONDS.toMillis(this._blockUntil - System.nanoTime()) <= 0L) {
            throw new TimeoutException(String.format("Blocking timeout %d ms", this.getBlockingTimeout()));
         }
      } catch (Throwable e) {
         throw (IOException)(new InterruptedIOException()).initCause(e);
      }
   }

   public boolean prependContent(Content item) {
      boolean woken = false;
      synchronized(this._inputQ) {
         this._inputQ.push(item);
         this._contentConsumed -= (long)item.remaining();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} prependContent {}", this, item);
         }

         if (this._listener == null) {
            this._inputQ.notify();
         } else {
            woken = this._channelState.onReadPossible();
         }

         return woken;
      }
   }

   public boolean addContent(Content item) {
      boolean woken = false;
      synchronized(this._inputQ) {
         if (this._firstByteTimeStamp == -1L) {
            this._firstByteTimeStamp = System.nanoTime();
         }

         this._contentArrived += (long)item.remaining();
         this._inputQ.offer(item);
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} addContent {}", this, item);
         }

         if (this._listener == null) {
            this._inputQ.notify();
         } else {
            woken = this._channelState.onReadPossible();
         }

         return woken;
      }
   }

   public boolean hasContent() {
      synchronized(this._inputQ) {
         return this._inputQ.size() > 0;
      }
   }

   public void unblock() {
      synchronized(this._inputQ) {
         this._inputQ.notify();
      }
   }

   public long getContentConsumed() {
      synchronized(this._inputQ) {
         return this._contentConsumed;
      }
   }

   public boolean earlyEOF() {
      return this.addContent(EARLY_EOF_CONTENT);
   }

   public boolean eof() {
      return this.addContent(EOF_CONTENT);
   }

   public boolean consumeAll() {
      synchronized(this._inputQ) {
         boolean var10000;
         try {
            while(!this.isFinished()) {
               Content item = this.nextContent();
               if (item == null) {
                  break;
               }

               this.skip(item, this.remaining(item));
            }

            var10000 = this.isFinished() && !this.isError();
         } catch (IOException e) {
            LOG.debug(e);
            return false;
         }

         return var10000;
      }
   }

   public boolean isError() {
      synchronized(this._inputQ) {
         return this._state instanceof ErrorState;
      }
   }

   public boolean isAsync() {
      synchronized(this._inputQ) {
         return this._state == ASYNC;
      }
   }

   public boolean isFinished() {
      synchronized(this._inputQ) {
         return this._state instanceof EOFState;
      }
   }

   public boolean isReady() {
      try {
         synchronized(this._inputQ) {
            if (this._listener == null) {
               return true;
            }

            if (this._state instanceof EOFState) {
               return true;
            }

            if (this.nextReadable() != null) {
               return true;
            }

            this._channelState.onReadUnready();
         }

         return false;
      } catch (IOException e) {
         LOG.ignore(e);
         return true;
      }
   }

   public void setReadListener(ReadListener readListener) {
      readListener = (ReadListener)Objects.requireNonNull(readListener);
      boolean woken = false;

      try {
         synchronized(this._inputQ) {
            if (this._listener != null) {
               throw new IllegalStateException("ReadListener already set");
            }

            if (this._state != STREAM) {
               throw new IllegalStateException("State " + STREAM + " != " + this._state);
            }

            this._state = ASYNC;
            this._listener = readListener;
            boolean content = this.nextContent() != null;
            if (content) {
               woken = this._channelState.onReadReady();
            } else {
               this._channelState.onReadUnready();
            }
         }
      } catch (IOException e) {
         throw new RuntimeIOException(e);
      }

      if (woken) {
         this.wake();
      }

   }

   public boolean failed(Throwable x) {
      boolean woken = false;
      synchronized(this._inputQ) {
         if (this._state instanceof ErrorState) {
            LOG.warn(x);
         } else {
            this._state = new ErrorState(x);
         }

         if (this._listener == null) {
            this._inputQ.notify();
         } else {
            woken = this._channelState.onReadPossible();
         }

         return woken;
      }
   }

   public void run() {
      boolean aeof = false;
      Throwable error;
      ReadListener listener;
      synchronized(this._inputQ) {
         if (this._state == EOF) {
            return;
         }

         if (this._state == AEOF) {
            this._state = EOF;
            aeof = true;
         }

         listener = this._listener;
         error = this._state instanceof ErrorState ? ((ErrorState)this._state).getError() : null;
      }

      try {
         if (error != null) {
            this._channelState.getHttpChannel().getResponse().getHttpFields().add(HttpConnection.CONNECTION_CLOSE);
            listener.onError(error);
         } else if (aeof) {
            listener.onAllDataRead();
         } else {
            listener.onDataAvailable();
         }
      } catch (Throwable var7) {
         Throwable e = var7;
         LOG.warn(var7.toString());
         LOG.debug(var7);

         try {
            if (aeof || error == null) {
               this._channelState.getHttpChannel().getResponse().getHttpFields().add(HttpConnection.CONNECTION_CLOSE);
               listener.onError(e);
            }
         } catch (Throwable e2) {
            LOG.warn(e2.toString());
            LOG.debug(e2);
            throw new RuntimeIOException(e2);
         }
      }

   }

   public String toString() {
      State state;
      long consumed;
      int q;
      Content content;
      synchronized(this._inputQ) {
         state = this._state;
         consumed = this._contentConsumed;
         q = this._inputQ.size();
         content = (Content)this._inputQ.peekFirst();
      }

      return String.format("%s@%x[c=%d,q=%d,[0]=%s,s=%s]", this.getClass().getSimpleName(), this.hashCode(), consumed, q, content, state);
   }

   public static class PoisonPillContent extends Content {
      private final String _name;

      public PoisonPillContent(String name) {
         super(BufferUtil.EMPTY_BUFFER);
         this._name = name;
      }

      public String toString() {
         return this._name;
      }
   }

   public static class EofContent extends PoisonPillContent {
      EofContent(String name) {
         super(name);
      }
   }

   public static class Content implements Callback {
      private final ByteBuffer _content;

      public Content(ByteBuffer content) {
         this._content = content;
      }

      public boolean isNonBlocking() {
         return true;
      }

      public ByteBuffer getContent() {
         return this._content;
      }

      public boolean hasContent() {
         return this._content.hasRemaining();
      }

      public int remaining() {
         return this._content.remaining();
      }

      public String toString() {
         return String.format("Content@%x{%s}", this.hashCode(), BufferUtil.toDetailString(this._content));
      }
   }

   protected abstract static class State {
      public boolean blockForContent(HttpInput in) throws IOException {
         return false;
      }

      public int noContent() throws IOException {
         return -1;
      }
   }

   protected static class EOFState extends State {
   }

   protected class ErrorState extends EOFState {
      final Throwable _error;

      ErrorState(Throwable error) {
         this._error = error;
      }

      public Throwable getError() {
         return this._error;
      }

      public int noContent() throws IOException {
         if (this._error instanceof IOException) {
            throw (IOException)this._error;
         } else {
            throw new IOException(this._error);
         }
      }

      public String toString() {
         return "ERROR:" + this._error;
      }
   }
}
