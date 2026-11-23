package org.spark_project.jetty.io;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Locker;
import org.spark_project.jetty.util.thread.Scheduler;

public class ByteArrayEndPoint extends AbstractEndPoint {
   static final Logger LOG = Log.getLogger(ByteArrayEndPoint.class);
   public static final InetSocketAddress NOIP = new InetSocketAddress(0);
   private static final ByteBuffer EOF = BufferUtil.allocate(0);
   private final Runnable _runFillable;
   private final Locker _locker;
   private final Condition _hasOutput;
   private final Queue<ByteBuffer> _inQ;
   private ByteBuffer _out;
   private boolean _ishut;
   private boolean _oshut;
   private boolean _closed;
   private boolean _growOutput;

   public ByteArrayEndPoint() {
      this((Scheduler)null, 0L, (ByteBuffer)null, (ByteBuffer)null);
   }

   public ByteArrayEndPoint(byte[] input, int outputSize) {
      this((Scheduler)null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(String input, int outputSize) {
      this((Scheduler)null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler scheduler, long idleTimeoutMs) {
      this(scheduler, idleTimeoutMs, (ByteBuffer)null, (ByteBuffer)null);
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, byte[] input, int outputSize) {
      this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, String input, int outputSize) {
      this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
   }

   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, ByteBuffer input, ByteBuffer output) {
      super(timer, NOIP, NOIP);
      this._runFillable = new Runnable() {
         public void run() {
            ByteArrayEndPoint.this.getFillInterest().fillable();
         }
      };
      this._locker = new Locker();
      this._hasOutput = this._locker.newCondition();
      this._inQ = new ArrayDeque();
      if (BufferUtil.hasContent(input)) {
         this.addInput(input);
      }

      this._out = output == null ? BufferUtil.allocate(1024) : output;
      this.setIdleTimeout(idleTimeoutMs);
   }

   protected void onIncompleteFlush() {
   }

   protected void execute(Runnable task) {
      (new Thread(task, "BAEPoint-" + Integer.toHexString(this.hashCode()))).start();
   }

   protected void needsFillInterest() throws IOException {
      try (Locker.Lock lock = this._locker.lock()) {
         if (this._closed) {
            throw new ClosedChannelException();
         }

         ByteBuffer in = (ByteBuffer)this._inQ.peek();
         if (BufferUtil.hasContent(in) || in == EOF) {
            this.execute(this._runFillable);
         }
      }

   }

   public void addInputEOF() {
      this.addInput((ByteBuffer)null);
   }

   public void addInput(ByteBuffer in) {
      boolean fillable = false;

      try (Locker.Lock lock = this._locker.lock()) {
         if (this._inQ.peek() == EOF) {
            throw new RuntimeIOException(new EOFException());
         }

         boolean was_empty = this._inQ.isEmpty();
         if (in == null) {
            this._inQ.add(EOF);
            fillable = true;
         }

         if (BufferUtil.hasContent(in)) {
            this._inQ.add(in);
            fillable = was_empty;
         }
      }

      if (fillable) {
         this._runFillable.run();
      }

   }

   public void addInputAndExecute(ByteBuffer in) {
      boolean fillable = false;

      try (Locker.Lock lock = this._locker.lock()) {
         if (this._inQ.peek() == EOF) {
            throw new RuntimeIOException(new EOFException());
         }

         boolean was_empty = this._inQ.isEmpty();
         if (in == null) {
            this._inQ.add(EOF);
            fillable = true;
         }

         if (BufferUtil.hasContent(in)) {
            this._inQ.add(in);
            fillable = was_empty;
         }
      }

      if (fillable) {
         this.execute(this._runFillable);
      }

   }

   public void addInput(String s) {
      this.addInput(BufferUtil.toBuffer(s, StandardCharsets.UTF_8));
   }

   public void addInput(String s, Charset charset) {
      this.addInput(BufferUtil.toBuffer(s, charset));
   }

   public ByteBuffer getOutput() {
      ByteBuffer var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._out;
      }

      return var3;
   }

   public String getOutputString() {
      return this.getOutputString(StandardCharsets.UTF_8);
   }

   public String getOutputString(Charset charset) {
      return BufferUtil.toString(this._out, charset);
   }

   public ByteBuffer takeOutput() {
      ByteBuffer b;
      try (Locker.Lock lock = this._locker.lock()) {
         b = this._out;
         this._out = BufferUtil.allocate(b.capacity());
      }

      this.getWriteFlusher().completeWrite();
      return b;
   }

   public ByteBuffer waitForOutput(long time, TimeUnit unit) throws InterruptedException {
      ByteBuffer b;
      try (Locker.Lock lock = this._locker.lock()) {
         while(BufferUtil.isEmpty(this._out) && !this._closed && !this._oshut) {
            this._hasOutput.await(time, unit);
         }

         b = this._out;
         this._out = BufferUtil.allocate(b.capacity());
      }

      this.getWriteFlusher().completeWrite();
      return b;
   }

   public String takeOutputString() {
      return this.takeOutputString(StandardCharsets.UTF_8);
   }

   public String takeOutputString(Charset charset) {
      ByteBuffer buffer = this.takeOutput();
      return BufferUtil.toString(buffer, charset);
   }

   public void setOutput(ByteBuffer out) {
      try (Locker.Lock lock = this._locker.lock()) {
         this._out = out;
      }

      this.getWriteFlusher().completeWrite();
   }

   public boolean isOpen() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = !this._closed;
      }

      return var3;
   }

   public boolean isInputShutdown() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._ishut || this._closed;
      }

      return var3;
   }

   public boolean isOutputShutdown() {
      boolean var3;
      try (Locker.Lock lock = this._locker.lock()) {
         var3 = this._oshut || this._closed;
      }

      return var3;
   }

   public void shutdownInput() {
      boolean close = false;

      try (Locker.Lock lock = this._locker.lock()) {
         this._ishut = true;
         if (this._oshut && !this._closed) {
            close = this._closed = true;
         }
      }

      if (close) {
         super.close();
      }

   }

   public void shutdownOutput() {
      boolean close = false;

      try (Locker.Lock lock = this._locker.lock()) {
         this._oshut = true;
         this._hasOutput.signalAll();
         if (this._ishut && !this._closed) {
            close = this._closed = true;
         }
      }

      if (close) {
         super.close();
      }

   }

   public void close() {
      boolean close = false;

      try (Locker.Lock lock = this._locker.lock()) {
         if (!this._closed) {
            close = this._closed = this._ishut = this._oshut = true;
         }

         this._hasOutput.signalAll();
      }

      if (close) {
         super.close();
      }

   }

   public boolean hasMore() {
      return this.getOutput().position() > 0;
   }

   public int fill(ByteBuffer buffer) throws IOException {
      int filled = 0;
      boolean close = false;

      try (Locker.Lock lock = this._locker.lock()) {
         while(true) {
            if (this._closed) {
               throw new EofException("CLOSED");
            }

            if (this._ishut) {
               byte var18 = -1;
               return var18;
            }

            if (this._inQ.isEmpty()) {
               break;
            }

            ByteBuffer in = (ByteBuffer)this._inQ.peek();
            if (in == EOF) {
               this._ishut = true;
               if (this._oshut) {
                  close = this._closed = true;
               }

               filled = -1;
               break;
            } else if (!BufferUtil.hasContent(in)) {
               this._inQ.poll();
            } else {
               filled = BufferUtil.append(buffer, in);
               if (BufferUtil.isEmpty(in)) {
                  this._inQ.poll();
               }
               break;
            }
         }
      }

      if (close) {
         super.close();
      }

      if (filled > 0) {
         this.notIdle();
      }

      return filled;
   }

   public boolean flush(ByteBuffer... buffers) throws IOException {
      boolean flushed = true;

      try (Locker.Lock lock = this._locker.lock()) {
         if (this._closed) {
            throw new IOException("CLOSED");
         }

         if (this._oshut) {
            throw new IOException("OSHUT");
         }

         boolean idle = true;

         for(ByteBuffer b : buffers) {
            if (BufferUtil.hasContent(b)) {
               if (this._growOutput && b.remaining() > BufferUtil.space(this._out)) {
                  BufferUtil.compact(this._out);
                  if (b.remaining() > BufferUtil.space(this._out)) {
                     ByteBuffer n = BufferUtil.allocate(this._out.capacity() + b.remaining() * 2);
                     BufferUtil.append(n, this._out);
                     this._out = n;
                  }
               }

               if (BufferUtil.append(this._out, b) > 0) {
                  idle = false;
               }

               if (BufferUtil.hasContent(b)) {
                  flushed = false;
                  break;
               }
            }
         }

         if (!idle) {
            this.notIdle();
            this._hasOutput.signalAll();
         }
      }

      return flushed;
   }

   public void reset() {
      try (Locker.Lock lock = this._locker.lock()) {
         this.getFillInterest().onClose();
         this.getWriteFlusher().onClose();
         this._ishut = false;
         this._oshut = false;
         this._closed = false;
         this._inQ.clear();
         BufferUtil.clear(this._out);
      }

   }

   public Object getTransport() {
      return null;
   }

   public boolean isGrowOutput() {
      return this._growOutput;
   }

   public void setGrowOutput(boolean growOutput) {
      this._growOutput = growOutput;
   }

   public String toString() {
      int q;
      ByteBuffer b;
      String o;
      try (Locker.Lock lock = this._locker.lock()) {
         q = this._inQ.size();
         b = (ByteBuffer)this._inQ.peek();
         o = BufferUtil.toDetailString(this._out);
      }

      return String.format("%s[q=%d,q[0]=%s,o=%s]", super.toString(), q, b, o);
   }
}
