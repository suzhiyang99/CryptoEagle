package org.apache.activemq.artemis.utils.actors;

import java.util.concurrent.Executor;
import org.apache.activemq.artemis.api.core.ActiveMQInterruptedException;
import org.jboss.logging.Logger;

public class OrderedExecutor extends ProcessorBase<Runnable> implements ArtemisExecutor {
   private static final Logger logger = Logger.getLogger(OrderedExecutor.class);

   public OrderedExecutor(Executor delegate) {
      super(delegate);
   }

   protected final void doTask(Runnable task) {
      try {
         task.run();
      } catch (ActiveMQInterruptedException e) {
         logger.debug("Interrupted Thread", e);
      } catch (Throwable t) {
         logger.warn(t.getMessage(), t);
      }

   }

   public final void execute(Runnable run) {
      this.task(run);
   }

   public String toString() {
      return "OrderedExecutor(tasks=" + this.tasks + ")";
   }
}
