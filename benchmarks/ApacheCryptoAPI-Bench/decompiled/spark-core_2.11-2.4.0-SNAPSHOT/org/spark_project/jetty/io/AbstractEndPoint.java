package org.spark_project.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public abstract class AbstractEndPoint extends IdleTimeout implements EndPoint {
   private static final Logger LOG = Log.getLogger(AbstractEndPoint.class);
   private final long _created = System.currentTimeMillis();
   private final InetSocketAddress _local;
   private final InetSocketAddress _remote;
   private volatile Connection _connection;
   private final FillInterest _fillInterest = new FillInterest() {
      protected void needsFillInterest() throws IOException {
         AbstractEndPoint.this.needsFillInterest();
      }
   };
   private final WriteFlusher _writeFlusher = new WriteFlusher(this) {
      protected void onIncompleteFlush() {
         AbstractEndPoint.this.onIncompleteFlush();
      }
   };

   protected AbstractEndPoint(Scheduler scheduler, InetSocketAddress local, InetSocketAddress remote) {
      super(scheduler);
      this._local = local;
      this._remote = remote;
   }

   public long getCreatedTimeStamp() {
      return this._created;
   }

   public InetSocketAddress getLocalAddress() {
      return this._local;
   }

   public InetSocketAddress getRemoteAddress() {
      return this._remote;
   }

   public Connection getConnection() {
      return this._connection;
   }

   public void setConnection(Connection connection) {
      this._connection = connection;
   }

   public boolean isOptimizedForDirectBuffers() {
      return false;
   }

   public void onOpen() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen {}", this);
      }

      super.onOpen();
   }

   public void close() {
      this.onClose();
      this._writeFlusher.onClose();
      this._fillInterest.onClose();
   }

   protected void close(Throwable failure) {
      this.onClose();
      this._writeFlusher.onFail(failure);
      this._fillInterest.onFail(failure);
   }

   public void fillInterested(Callback callback) {
      this.notIdle();
      this._fillInterest.register(callback);
   }

   public boolean tryFillInterested(Callback callback) {
      this.notIdle();
      return this._fillInterest.tryRegister(callback);
   }

   public boolean isFillInterested() {
      return this._fillInterest.isInterested();
   }

   public void write(Callback callback, ByteBuffer... buffers) throws IllegalStateException {
      this._writeFlusher.write(callback, buffers);
   }

   protected abstract void onIncompleteFlush();

   protected abstract void needsFillInterest() throws IOException;

   public FillInterest getFillInterest() {
      return this._fillInterest;
   }

   protected WriteFlusher getWriteFlusher() {
      return this._writeFlusher;
   }

   protected void onIdleExpired(TimeoutException timeout) {
      Connection connection = this._connection;
      if (connection == null || connection.onIdleExpired()) {
         boolean output_shutdown = this.isOutputShutdown();
         boolean input_shutdown = this.isInputShutdown();
         boolean fillFailed = this._fillInterest.onFail(timeout);
         boolean writeFailed = this._writeFlusher.onFail(timeout);
         if (this.isOpen() && (output_shutdown || input_shutdown) && !fillFailed && !writeFailed) {
            this.close();
         } else {
            LOG.debug("Ignored idle endpoint {}", this);
         }

      }
   }

   public void upgrade(Connection newConnection) {
      Connection old_connection = this.getConnection();
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} upgrading from {} to {}", this, old_connection, newConnection);
      }

      ByteBuffer prefilled = old_connection instanceof Connection.UpgradeFrom ? ((Connection.UpgradeFrom)old_connection).onUpgradeFrom() : null;
      old_connection.onClose();
      old_connection.getEndPoint().setConnection(newConnection);
      if (newConnection instanceof Connection.UpgradeTo) {
         ((Connection.UpgradeTo)newConnection).onUpgradeTo(prefilled);
      } else if (BufferUtil.hasContent(prefilled)) {
         throw new IllegalStateException();
      }

      newConnection.onOpen();
   }

   public String toString() {
      Class<?> c = this.getClass();

      String name;
      for(name = c.getSimpleName(); name.length() == 0 && c.getSuperclass() != null; name = c.getSimpleName()) {
         c = c.getSuperclass();
      }

      Connection connection = this.getConnection();
      return String.format("%s@%x{%s<->%d,%s,%s,%s,%s,%s,%d/%d,%s@%x}", name, this.hashCode(), this.getRemoteAddress(), this.getLocalAddress().getPort(), this.isOpen() ? "Open" : "CLOSED", this.isInputShutdown() ? "ISHUT" : "in", this.isOutputShutdown() ? "OSHUT" : "out", this._fillInterest.toStateString(), this._writeFlusher.toStateString(), this.getIdleFor(), this.getIdleTimeout(), connection == null ? null : connection.getClass().getSimpleName(), connection == null ? 0 : connection.hashCode());
   }
}
