package org.spark_project.jetty.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public class ValidatingConnectionPool extends ConnectionPool {
   private static final Logger LOG = Log.getLogger(ValidatingConnectionPool.class);
   private final Scheduler scheduler;
   private final long timeout;
   private final Map<Connection, Holder> quarantine;

   public ValidatingConnectionPool(Destination destination, int maxConnections, Callback requester, Scheduler scheduler, long timeout) {
      super(destination, maxConnections, requester);
      this.scheduler = scheduler;
      this.timeout = timeout;
      this.quarantine = new HashMap(maxConnections);
   }

   @ManagedAttribute(
      value = "The number of validating connections",
      readonly = true
   )
   public int getValidatingConnectionCount() {
      return this.quarantine.size();
   }

   public boolean release(Connection connection) {
      this.lock();

      label49: {
         boolean var2;
         try {
            if (this.getActiveConnections().remove(connection)) {
               Holder holder = new Holder(connection);
               holder.task = this.scheduler.schedule(holder, this.timeout, TimeUnit.MILLISECONDS);
               this.quarantine.put(connection, holder);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Validating for {}ms {}", this.timeout, connection);
               }
               break label49;
            }

            var2 = false;
         } finally {
            this.unlock();
         }

         return var2;
      }

      this.released(connection);
      return true;
   }

   public boolean remove(Connection connection) {
      this.lock();

      Holder holder;
      try {
         holder = (Holder)this.quarantine.remove(connection);
      } finally {
         this.unlock();
      }

      if (holder == null) {
         return super.remove(connection);
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Removed while validating {}", connection);
         }

         boolean cancelled = holder.cancel();
         return cancelled ? this.remove(connection, true) : super.remove(connection);
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      super.dump(out, indent);
      ContainerLifeCycle.dump(out, indent, this.quarantine.values());
   }

   public String toString() {
      this.lock();

      int size;
      try {
         size = this.quarantine.size();
      } finally {
         this.unlock();
      }

      return String.format("%s[v=%d]", super.toString(), size);
   }

   private class Holder implements Runnable {
      private final long timestamp = System.nanoTime();
      private final AtomicBoolean latch = new AtomicBoolean();
      private final Connection connection;
      public Scheduler.Task task;

      public Holder(Connection connection) {
         this.connection = connection;
      }

      public void run() {
         if (this.latch.compareAndSet(false, true)) {
            ValidatingConnectionPool.this.lock();

            boolean idle;
            try {
               ValidatingConnectionPool.this.quarantine.remove(this.connection);
               idle = ValidatingConnectionPool.this.offerIdle(this.connection);
               if (ValidatingConnectionPool.LOG.isDebugEnabled()) {
                  ValidatingConnectionPool.LOG.debug("Validated {}", this.connection);
               }
            } finally {
               ValidatingConnectionPool.this.unlock();
            }

            if (ValidatingConnectionPool.this.idle(this.connection, idle)) {
               ValidatingConnectionPool.this.proceed();
            }
         }

      }

      public boolean cancel() {
         if (this.latch.compareAndSet(false, true)) {
            this.task.cancel();
            return true;
         } else {
            return false;
         }
      }

      public String toString() {
         return String.format("%s[validationLeft=%dms]", this.connection, ValidatingConnectionPool.this.timeout - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.timestamp));
      }
   }
}
