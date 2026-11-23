package org.spark_project.jetty.io;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class AbstractConnection implements Connection {
   private static final Logger LOG = Log.getLogger(AbstractConnection.class);
   private final List<Connection.Listener> listeners = new CopyOnWriteArrayList();
   private final long _created = System.currentTimeMillis();
   private final EndPoint _endPoint;
   private final Executor _executor;
   private final Callback _readCallback;
   private int _inputBufferSize = 2048;

   protected AbstractConnection(EndPoint endp, Executor executor) {
      if (executor == null) {
         throw new IllegalArgumentException("Executor must not be null!");
      } else {
         this._endPoint = endp;
         this._executor = executor;
         this._readCallback = new ReadCallback();
      }
   }

   public void addListener(Connection.Listener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(Connection.Listener listener) {
      this.listeners.remove(listener);
   }

   public int getInputBufferSize() {
      return this._inputBufferSize;
   }

   public void setInputBufferSize(int inputBufferSize) {
      this._inputBufferSize = inputBufferSize;
   }

   protected Executor getExecutor() {
      return this._executor;
   }

   /** @deprecated */
   @Deprecated
   public boolean isDispatchIO() {
      return false;
   }

   protected void failedCallback(final Callback callback, final Throwable x) {
      if (callback.isNonBlocking()) {
         try {
            callback.failed(x);
         } catch (Exception e) {
            LOG.warn(e);
         }
      } else {
         try {
            this.getExecutor().execute(new Runnable() {
               public void run() {
                  try {
                     callback.failed(x);
                  } catch (Exception e) {
                     AbstractConnection.LOG.warn(e);
                  }

               }
            });
         } catch (RejectedExecutionException e) {
            LOG.debug(e);
            callback.failed(x);
         }
      }

   }

   public void fillInterested() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fillInterested {}", this);
      }

      this.getEndPoint().fillInterested(this._readCallback);
   }

   public void tryFillInterested() {
      this.tryFillInterested(this._readCallback);
   }

   public void tryFillInterested(Callback callback) {
      this.getEndPoint().tryFillInterested(callback);
   }

   public boolean isFillInterested() {
      return this.getEndPoint().isFillInterested();
   }

   public abstract void onFillable();

   protected void onFillInterestedFailed(Throwable cause) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} onFillInterestedFailed {}", this, cause);
      }

      if (this._endPoint.isOpen()) {
         boolean close = true;
         if (cause instanceof TimeoutException) {
            close = this.onReadTimeout();
         }

         if (close) {
            if (this._endPoint.isOutputShutdown()) {
               this._endPoint.close();
            } else {
               this._endPoint.shutdownOutput();
               this.fillInterested();
            }
         }
      }

   }

   protected boolean onReadTimeout() {
      return true;
   }

   public void onOpen() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen {}", this);
      }

      for(Connection.Listener listener : this.listeners) {
         listener.onOpened(this);
      }

   }

   public void onClose() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onClose {}", this);
      }

      for(Connection.Listener listener : this.listeners) {
         listener.onClosed(this);
      }

   }

   public EndPoint getEndPoint() {
      return this._endPoint;
   }

   public void close() {
      this.getEndPoint().close();
   }

   public boolean onIdleExpired() {
      return true;
   }

   public int getMessagesIn() {
      return -1;
   }

   public int getMessagesOut() {
      return -1;
   }

   public long getBytesIn() {
      return -1L;
   }

   public long getBytesOut() {
      return -1L;
   }

   public long getCreatedTimeStamp() {
      return this._created;
   }

   public String toString() {
      return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this._endPoint);
   }

   private class ReadCallback implements Callback {
      private ReadCallback() {
      }

      public void succeeded() {
         AbstractConnection.this.onFillable();
      }

      public void failed(Throwable x) {
         AbstractConnection.this.onFillInterestedFailed(x);
      }

      public String toString() {
         return String.format("AC.ReadCB@%x{%s}", AbstractConnection.this.hashCode(), AbstractConnection.this);
      }
   }
}
