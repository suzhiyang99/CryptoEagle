package org.spark_project.jetty.util.thread;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.strategy.ExecuteProduceConsume;

public interface ExecutionStrategy {
   void dispatch();

   void execute();

   public interface Factory {
      ExecutionStrategy newExecutionStrategy(Producer var1, Executor var2);

      static Factory getDefault() {
         return ExecutionStrategy.DefaultExecutionStrategyFactory.INSTANCE;
      }

      /** @deprecated */
      @Deprecated
      static ExecutionStrategy instanceFor(Producer producer, Executor executor) {
         return getDefault().newExecutionStrategy(producer, executor);
      }
   }

   public static class DefaultExecutionStrategyFactory implements Factory {
      private static final Logger LOG = Log.getLogger(Factory.class);
      private static final Factory INSTANCE = new DefaultExecutionStrategyFactory();

      public ExecutionStrategy newExecutionStrategy(Producer producer, Executor executor) {
         String strategy = System.getProperty(producer.getClass().getName() + ".ExecutionStrategy");
         if (strategy != null) {
            try {
               Class<? extends ExecutionStrategy> c = Loader.loadClass(producer.getClass(), strategy);
               Constructor<? extends ExecutionStrategy> m = c.getConstructor(Producer.class, Executor.class);
               LOG.info("Use {} for {}", c.getSimpleName(), producer.getClass().getName());
               return (ExecutionStrategy)m.newInstance(producer, executor);
            } catch (Exception e) {
               LOG.warn(e);
            }
         }

         return new ExecuteProduceConsume(producer, executor);
      }
   }

   public interface Producer {
      Runnable produce();
   }

   public interface Rejectable {
      void reject();
   }
}
