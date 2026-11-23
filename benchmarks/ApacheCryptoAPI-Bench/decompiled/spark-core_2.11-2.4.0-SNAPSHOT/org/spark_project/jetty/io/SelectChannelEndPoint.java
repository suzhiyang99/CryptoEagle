package org.spark_project.jetty.io;

import java.io.Closeable;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Locker;
import org.spark_project.jetty.util.thread.Scheduler;

public class SelectChannelEndPoint extends ChannelEndPoint implements ManagedSelector.SelectableEndPoint {
   public static final Logger LOG = Log.getLogger(SelectChannelEndPoint.class);
   private final Locker _locker = new Locker();
   private boolean _updatePending;
   private final AtomicBoolean _open = new AtomicBoolean();
   private final ManagedSelector _selector;
   private final SelectionKey _key;
   private int _currentInterestOps;
   private int _desiredInterestOps;
   private final Runnable _runUpdateKey = new Runnable() {
      public void run() {
         SelectChannelEndPoint.this.updateKey();
      }

      public String toString() {
         return SelectChannelEndPoint.this.toString() + ":runUpdateKey";
      }
   };
   private final Runnable _runFillable = new RunnableCloseable() {
      public void run() {
         SelectChannelEndPoint.this.getFillInterest().fillable();
      }

      public String toString() {
         return SelectChannelEndPoint.this.toString() + ":runFillable";
      }
   };
   private final Runnable _runCompleteWrite = new RunnableCloseable() {
      public void run() {
         SelectChannelEndPoint.this.getWriteFlusher().completeWrite();
      }

      public String toString() {
         return SelectChannelEndPoint.this.toString() + ":runCompleteWrite";
      }
   };
   private final Runnable _runCompleteWriteFillable = new RunnableCloseable() {
      public void run() {
         SelectChannelEndPoint.this.getWriteFlusher().completeWrite();
         SelectChannelEndPoint.this.getFillInterest().fillable();
      }

      public String toString() {
         return SelectChannelEndPoint.this.toString() + ":runFillableCompleteWrite";
      }
   };

   public SelectChannelEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler, long idleTimeout) {
      super(scheduler, channel);
      this._selector = selector;
      this._key = key;
      this.setIdleTimeout(idleTimeout);
   }

   protected void needsFillInterest() {
      this.changeInterests(1);
   }

   protected void onIncompleteFlush() {
      this.changeInterests(4);
   }

   public Runnable onSelected() {
      int readyOps = this._key.readyOps();

      int oldInterestOps;
      int newInterestOps;
      try (Locker.Lock lock = this._locker.lock()) {
         this._updatePending = true;
         oldInterestOps = this._desiredInterestOps;
         newInterestOps = oldInterestOps & ~readyOps;
         this._desiredInterestOps = newInterestOps;
      }

      boolean readable = (readyOps & 1) != 0;
      boolean writable = (readyOps & 4) != 0;
      if (LOG.isDebugEnabled()) {
         LOG.debug("onSelected {}->{} r={} w={} for {}", oldInterestOps, newInterestOps, readable, writable, this);
      }

      if (readable && this.getFillInterest().isCallbackNonBlocking()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Direct readable run {}", this);
         }

         this._runFillable.run();
         readable = false;
      }

      if (writable && this.getWriteFlusher().isCallbackNonBlocking()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Direct writable run {}", this);
         }

         this._runCompleteWrite.run();
         writable = false;
      }

      Runnable task = readable ? (writable ? this._runCompleteWriteFillable : this._runFillable) : (writable ? this._runCompleteWrite : null);
      if (LOG.isDebugEnabled()) {
         LOG.debug("task {}", task);
      }

      return task;
   }

   public void updateKey() {
      try (Locker.Lock lock = this._locker.lock()) {
         this._updatePending = false;
         int oldInterestOps = this._currentInterestOps;
         int newInterestOps = this._desiredInterestOps;
         if (oldInterestOps != newInterestOps) {
            this._currentInterestOps = newInterestOps;
            this._key.interestOps(newInterestOps);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Key interests updated {} -> {} on {}", oldInterestOps, newInterestOps, this);
         }
      } catch (CancelledKeyException var17) {
         LOG.debug("Ignoring key update for concurrently closed channel {}", this);
         this.close();
      } catch (Throwable x) {
         LOG.warn("Ignoring key update for " + this, x);
         this.close();
      }

   }

   private void changeInterests(int operation) {
      int oldInterestOps;
      int newInterestOps;
      boolean pending;
      try (Locker.Lock lock = this._locker.lock()) {
         pending = this._updatePending;
         oldInterestOps = this._desiredInterestOps;
         newInterestOps = oldInterestOps | operation;
         if (newInterestOps != oldInterestOps) {
            this._desiredInterestOps = newInterestOps;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("changeInterests p={} {}->{} for {}", pending, oldInterestOps, newInterestOps, this);
      }

      if (!pending) {
         this._selector.submit(this._runUpdateKey);
      }

   }

   public void close() {
      if (this._open.compareAndSet(true, false)) {
         super.close();
         this._selector.destroyEndPoint(this);
      }

   }

   public boolean isOpen() {
      return this._open.get();
   }

   public void onOpen() {
      if (this._open.compareAndSet(false, true)) {
         super.onOpen();
      }

   }

   public String toString() {
      try {
         boolean valid = this._key != null && this._key.isValid();
         int keyInterests = valid ? this._key.interestOps() : -1;
         int keyReadiness = valid ? this._key.readyOps() : -1;
         return String.format("%s{io=%d/%d,kio=%d,kro=%d}", super.toString(), this._currentInterestOps, this._desiredInterestOps, keyInterests, keyReadiness);
      } catch (Throwable var4) {
         return String.format("%s{io=%s,kio=-2,kro=-2}", super.toString(), this._desiredInterestOps);
      }
   }

   private abstract class RunnableCloseable implements Runnable, Closeable {
      private RunnableCloseable() {
      }

      public void close() {
         try {
            SelectChannelEndPoint.this.close();
         } catch (Throwable x) {
            SelectChannelEndPoint.LOG.warn(x);
         }

      }
   }
}
