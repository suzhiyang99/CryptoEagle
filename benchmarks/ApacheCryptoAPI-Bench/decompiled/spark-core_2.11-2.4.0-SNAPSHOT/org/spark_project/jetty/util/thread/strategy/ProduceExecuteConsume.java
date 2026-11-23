package org.spark_project.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Locker;

public class ProduceExecuteConsume extends ExecutingExecutionStrategy implements ExecutionStrategy {
   private static final Logger LOG = Log.getLogger(ProduceExecuteConsume.class);
   private final Locker _locker = new Locker();
   private final ExecutionStrategy.Producer _producer;
   private State _state;

   public ProduceExecuteConsume(ExecutionStrategy.Producer producer, Executor executor) {
      super(executor);
      this._state = ProduceExecuteConsume.State.IDLE;
      this._producer = producer;
   }

   public void execute() {
      try (Locker.Lock locked = this._locker.lock()) {
         switch (this._state) {
            case IDLE:
               this._state = ProduceExecuteConsume.State.PRODUCE;
               break;
            case PRODUCE:
            case EXECUTE:
               this._state = ProduceExecuteConsume.State.EXECUTE;
               return;
         }
      }

      while(true) {
         Runnable task = this._producer.produce();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} produced {}", this._producer, task);
         }

         if (task == null) {
            try (Locker.Lock locked = this._locker.lock()) {
               switch (this._state) {
                  case IDLE:
                     throw new IllegalStateException();
                  case PRODUCE:
                     this._state = ProduceExecuteConsume.State.IDLE;
                     return;
                  case EXECUTE:
                     this._state = ProduceExecuteConsume.State.PRODUCE;
                     continue;
               }
            }
         }

         this.execute(task);
      }
   }

   public void dispatch() {
      this.execute();
   }

   public static class Factory implements ExecutionStrategy.Factory {
      public ExecutionStrategy newExecutionStrategy(ExecutionStrategy.Producer producer, Executor executor) {
         return new ProduceExecuteConsume(producer, executor);
      }
   }

   private static enum State {
      IDLE,
      PRODUCE,
      EXECUTE;
   }
}
