package org.spark_project.jetty.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.log.Logger;

public abstract class ProxyConnection extends AbstractConnection {
   protected static final Logger LOG;
   private final IteratingCallback pipe = new ProxyIteratingCallback();
   private final ByteBufferPool bufferPool;
   private final ConcurrentMap<String, Object> context;
   private Connection connection;

   protected ProxyConnection(EndPoint endp, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context) {
      super(endp, executor);
      this.bufferPool = bufferPool;
      this.context = context;
   }

   public ByteBufferPool getByteBufferPool() {
      return this.bufferPool;
   }

   public ConcurrentMap<String, Object> getContext() {
      return this.context;
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void setConnection(Connection connection) {
      this.connection = connection;
   }

   public void onFillable() {
      this.pipe.iterate();
   }

   protected abstract int read(EndPoint var1, ByteBuffer var2) throws IOException;

   protected abstract void write(EndPoint var1, ByteBuffer var2, Callback var3);

   public String toString() {
      return String.format("%s[l:%d<=>r:%d]", super.toString(), this.getEndPoint().getLocalAddress().getPort(), this.getEndPoint().getRemoteAddress().getPort());
   }

   static {
      LOG = ConnectHandler.LOG;
   }

   private class ProxyIteratingCallback extends IteratingCallback {
      private ByteBuffer buffer;
      private int filled;

      private ProxyIteratingCallback() {
      }

      protected IteratingCallback.Action process() throws Exception {
         this.buffer = ProxyConnection.this.bufferPool.acquire(ProxyConnection.this.getInputBufferSize(), true);

         try {
            int filled = this.filled = ProxyConnection.this.read(ProxyConnection.this.getEndPoint(), this.buffer);
            if (ProxyConnection.LOG.isDebugEnabled()) {
               ProxyConnection.LOG.debug("{} filled {} bytes", ProxyConnection.this, filled);
            }

            if (filled > 0) {
               ProxyConnection.this.write(ProxyConnection.this.connection.getEndPoint(), this.buffer, this);
               return IteratingCallback.Action.SCHEDULED;
            } else if (filled == 0) {
               ProxyConnection.this.bufferPool.release(this.buffer);
               ProxyConnection.this.fillInterested();
               return IteratingCallback.Action.IDLE;
            } else {
               ProxyConnection.this.bufferPool.release(this.buffer);
               ProxyConnection.this.connection.getEndPoint().shutdownOutput();
               return IteratingCallback.Action.SUCCEEDED;
            }
         } catch (IOException x) {
            if (ProxyConnection.LOG.isDebugEnabled()) {
               ProxyConnection.LOG.debug(ProxyConnection.this + " could not fill", (Throwable)x);
            }

            ProxyConnection.this.bufferPool.release(this.buffer);
            this.disconnect();
            return IteratingCallback.Action.SUCCEEDED;
         }
      }

      public void succeeded() {
         if (ProxyConnection.LOG.isDebugEnabled()) {
            ProxyConnection.LOG.debug("{} wrote {} bytes", ProxyConnection.this, this.filled);
         }

         ProxyConnection.this.bufferPool.release(this.buffer);
         super.succeeded();
      }

      protected void onCompleteSuccess() {
      }

      protected void onCompleteFailure(Throwable x) {
         if (ProxyConnection.LOG.isDebugEnabled()) {
            ProxyConnection.LOG.debug(ProxyConnection.this + " failed to write " + this.filled + " bytes", x);
         }

         this.disconnect();
      }

      private void disconnect() {
         ProxyConnection.this.bufferPool.release(this.buffer);
         ProxyConnection.this.close();
         ProxyConnection.this.connection.close();
      }
   }
}
