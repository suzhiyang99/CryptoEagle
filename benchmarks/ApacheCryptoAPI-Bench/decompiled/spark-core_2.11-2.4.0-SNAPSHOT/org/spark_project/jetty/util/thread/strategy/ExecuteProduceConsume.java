package org.spark_project.jetty.util.thread.strategy;

import java.io.Closeable;
import java.util.concurrent.Executor;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Locker;
import org.spark_project.jetty.util.thread.ThreadPool;

public class ExecuteProduceConsume extends ExecutingExecutionStrategy implements ExecutionStrategy, Runnable {
   private static final Logger LOG = Log.getLogger(ExecuteProduceConsume.class);
   private final Locker _locker;
   private final Runnable _runExecute;
   private final ExecutionStrategy.Producer _producer;
   private final ThreadPool _threadPool;
   private boolean _idle;
   private boolean _execute;
   private boolean _producing;
   private boolean _pending;
   private boolean _lowThreads;

   public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
      super(executor);
      this._locker = new Locker();
      this._runExecute = new RunExecute();
      this._idle = true;
      this._producer = producer;
      this._threadPool = executor instanceof ThreadPool ? (ThreadPool)executor : null;
   }

   /** @deprecated */
   @Deprecated
   public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor, ExecutionStrategy lowResourceStrategy) {
      this(producer, executor);
   }

   public void execute() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} execute", this);
      }

      boolean produce = false;

      try (Locker.Lock locked = this._locker.lock()) {
         if (this._idle) {
            if (this._producing) {
               throw new IllegalStateException();
            }

            produce = this._producing = true;
            this._idle = false;
         } else {
            this._execute = true;
         }
      }

      if (produce) {
         this.produceConsume();
      }

   }

   public void dispatch() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} spawning", this);
      }

      boolean dispatch = false;

      try (Locker.Lock locked = this._locker.lock()) {
         if (this._idle) {
            dispatch = true;
         } else {
            this._execute = true;
         }
      }

      if (dispatch) {
         this.execute(this._runExecute);
      }

   }

   public void run() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} run", this);
      }

      boolean produce = false;

      try (Locker.Lock locked = this._locker.lock()) {
         this._pending = false;
         if (!this._idle && !this._producing) {
            produce = this._producing = true;
         }
      }

      if (produce) {
         this.produceConsume();
      }

   }

   private void produceConsume() {
      if (this._threadPool == null || !this._threadPool.isLowOnThreads() || this.produceExecuteConsume()) {
         this.executeProduceConsume();
      }
   }

   public boolean isLowOnThreads() {
      return this._lowThreads;
   }

   private boolean produceExecuteConsume() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} enter low threads mode", this);
      }

      this._lowThreads = true;

      boolean var25;
      try {
         boolean idle = false;

         while(this._threadPool.isLowOnThreads()) {
            Runnable task = this._producer.produce();
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} produced {}", this._producer, task);
            }

            if (task == null) {
               Locker.Lock locked = this._locker.lock();
               Throwable var4 = null;

               try {
                  if (!this._execute) {
                     this._producing = false;
                     idle = this._idle = true;
                     break;
                  }

                  this._execute = false;
                  this._producing = true;
                  this._idle = false;
               } catch (Throwable var22) {
                  var4 = var22;
                  throw var22;
               } finally {
                  if (locked != null) {
                     if (var4 != null) {
                        try {
                           locked.close();
                        } catch (Throwable var21) {
                           var4.addSuppressed(var21);
                        }
                     } else {
                        locked.close();
                     }
                  }

               }
            } else {
               this.executeProduct(task);
            }
         }

         var25 = !idle;
      } finally {
         this._lowThreads = false;
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} exit low threads mode", this);
         }

      }

      return var25;
   }

   protected void executeProduct(Runnable task) {
      if (task instanceof ExecutionStrategy.Rejectable) {
         try {
            ((ExecutionStrategy.Rejectable)task).reject();
            if (task instanceof Closeable) {
               ((Closeable)task).close();
            }
         } catch (Throwable x) {
            LOG.debug(x);
         }
      } else {
         this.execute(task);
      }

   }

   private void executeProduceConsume() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} produce enter", this);
      }

      while(true) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} producing", this);
         }

         Runnable task = this._producer.produce();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} produced {}", this, task);
         }

         boolean dispatch = false;

         try (Locker.Lock locked = this._locker.lock()) {
            this._producing = false;
            if (task == null) {
               if (this._execute) {
                  this._idle = false;
                  this._producing = true;
                  this._execute = false;
                  continue;
               }

               this._idle = true;
               break;
            }

            if (!this._pending) {
               dispatch = this._pending = true;
            }

            this._execute = false;
         }

         if (dispatch) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} dispatch", this);
            }

            if (!this.execute(this)) {
               task = null;
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} run {}", this, task);
         }

         if (task != null) {
            task.run();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} ran {}", this, task);
         }

         try (Locker.Lock locked = this._locker.lock()) {
            if (this._producing || this._idle) {
               break;
            }

            this._producing = true;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} produce exit", this);
      }

   }

   public Boolean isIdle() {
      Boolean var3;
      try (Locker.Lock locked = this._locker.lock()) {
         var3 = this._idle;
      }

      return var3;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("EPC ");

      try (Locker.Lock locked = this._locker.lock()) {
         builder.append(this._idle ? "Idle/" : "");
         builder.append(this._producing ? "Prod/" : "");
         builder.append(this._pending ? "Pend/" : "");
         builder.append(this._execute ? "Exec/" : "");
      }

      builder.append(this._producer);
      return builder.toString();
   }

   private class RunExecute implements Runnable {
      private RunExecute() {
      }

      public void run() {
         ExecuteProduceConsume.this.execute();
      }
   }

   public static class Factory implements ExecutionStrategy.Factory {
      public ExecutionStrategy newExecutionStrategy(ExecutionStrategy.Producer producer, Executor executor) {
         return new ExecuteProduceConsume(producer, executor);
      }
   }
}
