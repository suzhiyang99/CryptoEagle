package org.apache.activemq.artemis.utils.actors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import org.jboss.logging.Logger;

public abstract class ProcessorBase<T> extends HandlerBase {
   private static final Logger logger = Logger.getLogger(ProcessorBase.class);
   public static final int STATE_NOT_RUNNING = 0;
   public static final int STATE_RUNNING = 1;
   public static final int STATE_FORCED_SHUTDOWN = 2;
   protected final Queue<T> tasks = new ConcurrentLinkedQueue();
   private final Executor delegate;
   private final Runnable task = this::executePendingTasks;
   private volatile int state = 0;
   private volatile boolean requestedForcedShutdown = false;
   private volatile boolean requestedShutdown = false;
   private static final AtomicIntegerFieldUpdater<ProcessorBase> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ProcessorBase.class, "state");

   private void executePendingTasks() {
      while(stateUpdater.compareAndSet(this, 0, 1)) {
         this.enter();

         T task;
         try {
            while(!this.requestedForcedShutdown && (task = (T)this.tasks.poll()) != null) {
               this.doTask(task);
            }
         } finally {
            this.leave();
            if (!stateUpdater.compareAndSet(this, 1, 0)) {
               return;
            }

         }

         if (this.tasks.isEmpty() || this.requestedShutdown) {
            return;
         }
      }

   }

   public void shutdown() {
      this.shutdown(30L, TimeUnit.SECONDS);
   }

   public void shutdown(long timeout, TimeUnit unit) {
      this.requestedShutdown = true;
      if (!this.inHandler()) {
         this.flush(timeout, unit);
      }

   }

   public int shutdownNow(Consumer<? super T> onPendingItem) {
      this.requestedForcedShutdown = true;
      this.requestedShutdown = true;
      if (this.inHandler()) {
         stateUpdater.set(this, 2);
      } else {
         do {
            int startState = stateUpdater.get(this);
            if (startState == 2) {
               break;
            }

            if (startState == 1) {
               LockSupport.parkNanos(100000000L);
            }
         } while(!stateUpdater.compareAndSet(this, 0, 2));
      }

      int pendingItems = 0;
      synchronized(this.tasks) {
         T item;
         while((item = (T)this.tasks.poll()) != null) {
            onPendingItem.accept(item);
            ++pendingItems;
         }

         return pendingItems;
      }
   }

   protected abstract void doTask(T var1);

   public ProcessorBase(Executor parent) {
      this.delegate = parent;
   }

   public final boolean isFlushed() {
      return this.state == 0;
   }

   public final boolean flush(long timeout, TimeUnit unit) {
      if (this.state == 0) {
         return true;
      } else {
         long timeLimit = System.currentTimeMillis() + unit.toMillis(timeout);

         try {
            while(this.state == 1 && timeLimit > System.currentTimeMillis()) {
               if (this.tasks.isEmpty()) {
                  return true;
               }

               Thread.sleep(10L);
            }
         } catch (InterruptedException var7) {
         }

         return this.state == 0;
      }
   }

   protected void task(T command) {
      if (this.requestedShutdown) {
         logAddOnShutdown();
      }

      this.tasks.add(command);
      int state = stateUpdater.get(this);
      if (state != 1) {
         this.onAddedTaskIfNotRunning(state);
      }

   }

   private void onAddedTaskIfNotRunning(int state) {
      if (state == 0) {
         this.delegate.execute(this.task);
      } else if (state == 2) {
         synchronized(this.tasks) {
            this.tasks.clear();
         }
      }

   }

   private static void logAddOnShutdown() {
      if (logger.isDebugEnabled()) {
         logger.debug("Ordered executor has been gently shutdown at", new Exception("debug"));
      }

   }

   public final int remaining() {
      return this.tasks.size();
   }

   public final int status() {
      return this.state;
   }
}
