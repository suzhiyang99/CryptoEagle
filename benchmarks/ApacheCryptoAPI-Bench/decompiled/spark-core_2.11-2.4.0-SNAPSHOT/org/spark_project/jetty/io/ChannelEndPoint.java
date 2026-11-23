package org.spark_project.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public class ChannelEndPoint extends AbstractEndPoint {
   private static final Logger LOG = Log.getLogger(ChannelEndPoint.class);
   private final SocketChannel _channel;
   private final Socket _socket;
   private volatile boolean _ishut;
   private volatile boolean _oshut;

   public ChannelEndPoint(Scheduler scheduler, SocketChannel channel) {
      super(scheduler, (InetSocketAddress)channel.socket().getLocalSocketAddress(), (InetSocketAddress)channel.socket().getRemoteSocketAddress());
      this._channel = channel;
      this._socket = channel.socket();
   }

   public boolean isOptimizedForDirectBuffers() {
      return true;
   }

   public boolean isOpen() {
      return this._channel.isOpen();
   }

   protected void shutdownInput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("ishut {}", this);
      }

      this._ishut = true;
      if (this._oshut) {
         this.close();
      }

   }

   public void shutdownOutput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("oshut {}", this);
      }

      this._oshut = true;
      if (this._channel.isOpen()) {
         try {
            if (!this._socket.isOutputShutdown()) {
               this._socket.shutdownOutput();
            }
         } catch (IOException e) {
            LOG.debug(e);
         } finally {
            if (this._ishut) {
               this.close();
            }

         }
      }

   }

   public boolean isOutputShutdown() {
      return this._oshut || !this._channel.isOpen() || this._socket.isOutputShutdown();
   }

   public boolean isInputShutdown() {
      return this._ishut || !this._channel.isOpen() || this._socket.isInputShutdown();
   }

   public void close() {
      super.close();
      if (LOG.isDebugEnabled()) {
         LOG.debug("close {}", this);
      }

      try {
         this._channel.close();
      } catch (IOException e) {
         LOG.debug(e);
      } finally {
         this._ishut = true;
         this._oshut = true;
      }

   }

   public int fill(ByteBuffer buffer) throws IOException {
      if (this._ishut) {
         return -1;
      } else {
         int pos = BufferUtil.flipToFill(buffer);

         int var4;
         try {
            int filled = this._channel.read(buffer);
            if (LOG.isDebugEnabled()) {
               LOG.debug("filled {} {}", filled, this);
            }

            if (filled > 0) {
               this.notIdle();
            } else if (filled == -1) {
               this.shutdownInput();
            }

            var4 = filled;
            return var4;
         } catch (IOException e) {
            LOG.debug(e);
            this.shutdownInput();
            var4 = -1;
         } finally {
            BufferUtil.flipToFlush(buffer, pos);
         }

         return var4;
      }
   }

   public boolean flush(ByteBuffer... buffers) throws IOException {
      long flushed = 0L;

      try {
         if (buffers.length == 1) {
            flushed = (long)this._channel.write(buffers[0]);
         } else if (buffers.length > 1) {
            flushed = this._channel.write(buffers, 0, buffers.length);
         } else {
            for(ByteBuffer b : buffers) {
               if (b.hasRemaining()) {
                  int l = this._channel.write(b);
                  if (l > 0) {
                     flushed += (long)l;
                  }

                  if (b.hasRemaining()) {
                     break;
                  }
               }
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("flushed {} {}", flushed, this);
         }
      } catch (IOException e) {
         throw new EofException(e);
      }

      if (flushed > 0L) {
         this.notIdle();
      }

      for(ByteBuffer b : buffers) {
         if (!BufferUtil.isEmpty(b)) {
            return false;
         }
      }

      return true;
   }

   public ByteChannel getChannel() {
      return this._channel;
   }

   public Object getTransport() {
      return this._channel;
   }

   public Socket getSocket() {
      return this._socket;
   }

   protected void onIncompleteFlush() {
      throw new UnsupportedOperationException();
   }

   protected void needsFillInterest() throws IOException {
      throw new UnsupportedOperationException();
   }
}
