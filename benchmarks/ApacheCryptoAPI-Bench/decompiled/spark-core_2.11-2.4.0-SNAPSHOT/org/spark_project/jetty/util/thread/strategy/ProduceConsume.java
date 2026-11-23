package org.spark_project.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Locker;

public class ProduceConsume implements ExecutionStrategy, Runnable {
   private static final Logger LOG = Log.getLogger(ExecuteProduceConsume.class);
   private final Locker _locker = new Locker();
   private final ExecutionStrategy.Producer _producer;
   private final Executor _executor;
   private State _state;

   public ProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
      this._state = ProduceConsume.State.IDLE;
      this._producer = producer;
      this._executor = executor;
   }

   public void execute() {
      try (Locker.Lock lock = this._locker.lock()) {
         switch (this._state) {
            case IDLE:
               this._state = ProduceConsume.State.PRODUCE;
               break;
            case PRODUCE:
            case EXECUTE:
               this._state = ProduceConsume.State.EXECUTE;
               return;
         }
      }

      while(true) {
         Runnable task = this._producer.produce();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} produced {}", this._producer, task);
         }

         if (task == null) {
            try (Locker.Lock lock = this._locker.lock()) {
               switch (this._state) {
                  case IDLE:
                     throw new IllegalStateException();
                  case PRODUCE:
                     this._state = ProduceConsume.State.IDLE;
                     return;
                  case EXECUTE:
                     this._state = ProduceConsume.State.PRODUCE;
                     continue;
               }
            }
         }

         task.run();
      }
   }

   public void dispatch() {
      this._executor.execute(this);
   }

   public void run() {
      this.execute();
   }

   public static class Factory implements ExecutionStrategy.Factory {
      public ExecutionStrategy newExecutionStrategy(ExecutionStrategy.Producer producer, Executor executor) {
         return new ProduceConsume(producer, executor);
      }
   }

   private static enum State {
      IDLE,
      PRODUCE,
      EXECUTE;
   }
}
