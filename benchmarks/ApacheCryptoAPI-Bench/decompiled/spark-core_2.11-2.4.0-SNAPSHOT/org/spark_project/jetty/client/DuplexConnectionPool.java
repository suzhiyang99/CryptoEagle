package org.spark_project.jetty.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.util.BlockingArrayQueue;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Sweeper;

@ManagedObject("The connection pool")
public class DuplexConnectionPool implements Closeable, Dumpable, Sweeper.Sweepable {
   private static final Logger LOG = Log.getLogger(DuplexConnectionPool.class);
   private final AtomicInteger connectionCount = new AtomicInteger();
   private final ReentrantLock lock = new ReentrantLock();
   private final Destination destination;
   private final int maxConnections;
   private final Callback requester;
   private final Deque<Connection> idleConnections;
   private final Queue<Connection> activeConnections;

   public DuplexConnectionPool(Destination destination, int maxConnections, Callback requester) {
      this.destination = destination;
      this.maxConnections = maxConnections;
      this.requester = requester;
      this.idleConnections = new LinkedBlockingDeque(maxConnections);
      this.activeConnections = new BlockingArrayQueue<Connection>(maxConnections);
   }

   @ManagedAttribute(
      value = "The number of connections",
      readonly = true
   )
   public int getConnectionCount() {
      return this.connectionCount.get();
   }

   @ManagedAttribute(
      value = "The number of idle connections",
      readonly = true
   )
   public int getIdleConnectionCount() {
      this.lock();

      int var1;
      try {
         var1 = this.idleConnections.size();
      } finally {
         this.unlock();
      }

      return var1;
   }

   @ManagedAttribute(
      value = "The number of active connections",
      readonly = true
   )
   public int getActiveConnectionCount() {
      this.lock();

      int var1;
      try {
         var1 = this.activeConnections.size();
      } finally {
         this.unlock();
      }

      return var1;
   }

   public Queue<Connection> getIdleConnections() {
      return this.idleConnections;
   }

   public Queue<Connection> getActiveConnections() {
      return this.activeConnections;
   }

   public Connection acquire() {
      Connection connection = this.activateIdle();
      if (connection == null) {
         connection = this.tryCreate();
      }

      return connection;
   }

   private Connection tryCreate() {
      int current;
      final int next;
      do {
         current = this.getConnectionCount();
         next = current + 1;
         if (next > this.maxConnections) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Max connections {}/{} reached", current, this.maxConnections);
            }

            return this.activateIdle();
         }
      } while(!this.connectionCount.compareAndSet(current, next));

      if (LOG.isDebugEnabled()) {
         LOG.debug("Connection {}/{} creation", next, this.maxConnections);
      }

      this.destination.newConnection(new Promise<Connection>() {
         public void succeeded(Connection connection) {
            if (DuplexConnectionPool.LOG.isDebugEnabled()) {
               DuplexConnectionPool.LOG.debug("Connection {}/{} creation succeeded {}", next, DuplexConnectionPool.this.maxConnections, connection);
            }

            DuplexConnectionPool.this.idleCreated(connection);
            DuplexConnectionPool.this.proceed();
         }

         public void failed(Throwable x) {
            if (DuplexConnectionPool.LOG.isDebugEnabled()) {
               DuplexConnectionPool.LOG.debug("Connection " + next + "/" + DuplexConnectionPool.this.maxConnections + " creation failed", x);
            }

            DuplexConnectionPool.this.connectionCount.decrementAndGet();
            DuplexConnectionPool.this.requester.failed(x);
         }
      });
      return this.activateIdle();
   }

   protected void proceed() {
      this.requester.succeeded();
   }

   protected void idleCreated(Connection connection) {
      this.lock();

      boolean idle;
      try {
         idle = this.idleConnections.offerLast(connection);
      } finally {
         this.unlock();
      }

      this.idle(connection, idle);
   }

   private Connection activateIdle() {
      this.lock();

      boolean acquired;
      Connection connection;
      try {
         connection = (Connection)this.idleConnections.pollFirst();
         if (connection == null) {
            Object var3 = null;
            return (Connection)var3;
         }

         acquired = this.activeConnections.offer(connection);
      } finally {
         this.unlock();
      }

      if (acquired) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connection active {}", connection);
         }

         this.acquired(connection);
         return connection;
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connection active overflow {}", connection);
         }

         connection.close();
         return null;
      }
   }

   protected void acquired(Connection connection) {
   }

   public boolean release(Connection connection) {
      this.lock();

      boolean idle;
      label44: {
         boolean var3;
         try {
            if (this.activeConnections.remove(connection)) {
               idle = this.offerIdle(connection);
               break label44;
            }

            var3 = false;
         } finally {
            this.unlock();
         }

         return var3;
      }

      this.released(connection);
      return this.idle(connection, idle);
   }

   protected boolean offerIdle(Connection connection) {
      return this.idleConnections.offerFirst(connection);
   }

   protected boolean idle(Connection connection, boolean idle) {
      if (idle) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connection idle {}", connection);
         }

         return true;
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connection idle overflow {}", connection);
         }

         connection.close();
         return false;
      }
   }

   protected void released(Connection connection) {
   }

   public boolean remove(Connection connection) {
      return this.remove(connection, false);
   }

   protected boolean remove(Connection connection, boolean force) {
      this.lock();

      boolean activeRemoved;
      boolean idleRemoved;
      try {
         activeRemoved = this.activeConnections.remove(connection);
         idleRemoved = this.idleConnections.remove(connection);
      } finally {
         this.unlock();
      }

      if (activeRemoved || force) {
         this.released(connection);
      }

      boolean removed = activeRemoved || idleRemoved || force;
      if (removed) {
         int pooled = this.connectionCount.decrementAndGet();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connection removed {} - pooled: {}", connection, pooled);
         }
      }

      return removed;
   }

   public boolean isActive(Connection connection) {
      this.lock();

      boolean var2;
      try {
         var2 = this.activeConnections.contains(connection);
      } finally {
         this.unlock();
      }

      return var2;
   }

   public boolean isIdle(Connection connection) {
      this.lock();

      boolean var2;
      try {
         var2 = this.idleConnections.contains(connection);
      } finally {
         this.unlock();
      }

      return var2;
   }

   public boolean isEmpty() {
      return this.connectionCount.get() == 0;
   }

   public void close() {
      List<Connection> idles = new ArrayList();
      List<Connection> actives = new ArrayList();
      this.lock();

      try {
         idles.addAll(this.idleConnections);
         this.idleConnections.clear();
         actives.addAll(this.activeConnections);
         this.activeConnections.clear();
      } finally {
         this.unlock();
      }

      this.connectionCount.set(0);

      for(Connection connection : idles) {
         connection.close();
      }

      for(Connection connection : actives) {
         connection.close();
      }

   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      List<Connection> actives = new ArrayList();
      List<Connection> idles = new ArrayList();
      this.lock();

      try {
         actives.addAll(this.activeConnections);
         idles.addAll(this.idleConnections);
      } finally {
         this.unlock();
      }

      ContainerLifeCycle.dumpObject(out, this);
      ContainerLifeCycle.dump(out, indent, actives, idles);
   }

   public boolean sweep() {
      List<Connection> toSweep = new ArrayList();
      this.lock();

      try {
         for(Connection connection : this.activeConnections) {
            if (connection instanceof Sweeper.Sweepable) {
               toSweep.add(connection);
            }
         }
      } finally {
         this.unlock();
      }

      for(Connection connection : toSweep) {
         if (((Sweeper.Sweepable)connection).sweep()) {
            boolean removed = this.remove(connection, true);
            LOG.warn("Connection swept: {}{}{} from active connections{}{}", connection, System.lineSeparator(), removed ? "Removed" : "Not removed", System.lineSeparator(), this.dump());
         }
      }

      return false;
   }

   protected void lock() {
      this.lock.lock();
   }

   protected void unlock() {
      this.lock.unlock();
   }

   public String toString() {
      this.lock();

      int activeSize;
      int idleSize;
      try {
         activeSize = this.activeConnections.size();
         idleSize = this.idleConnections.size();
      } finally {
         this.unlock();
      }

      return String.format("%s[c=%d/%d,a=%d,i=%d]", this.getClass().getSimpleName(), this.connectionCount.get(), this.maxConnections, activeSize, idleSize);
   }
}
