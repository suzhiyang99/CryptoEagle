package org.spark_project.jetty.client;

import java.io.IOException;
import java.util.Collections;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.thread.Sweeper;

@ManagedObject
public abstract class PoolingHttpDestination<C extends Connection> extends HttpDestination implements Callback {
   private DuplexConnectionPool connectionPool;

   public PoolingHttpDestination(HttpClient client, Origin origin) {
      super(client, origin);
      this.connectionPool = this.newConnectionPool(client);
      this.addBean(this.connectionPool);
      Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
      if (sweeper != null) {
         sweeper.offer(this.connectionPool);
      }

   }

   protected void doStart() throws Exception {
      HttpClient client = this.getHttpClient();
      this.connectionPool = this.newConnectionPool(client);
      this.addBean(this.connectionPool);
      super.doStart();
      Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
      if (sweeper != null) {
         sweeper.offer(this.connectionPool);
      }

   }

   protected void doStop() throws Exception {
      HttpClient client = this.getHttpClient();
      Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
      if (sweeper != null) {
         sweeper.remove(this.connectionPool);
      }

      super.doStop();
      this.removeBean(this.connectionPool);
   }

   protected DuplexConnectionPool newConnectionPool(HttpClient client) {
      return new DuplexConnectionPool(this, client.getMaxConnectionsPerDestination(), this);
   }

   @ManagedAttribute(
      value = "The connection pool",
      readonly = true
   )
   public DuplexConnectionPool getConnectionPool() {
      return this.connectionPool;
   }

   public void succeeded() {
      this.send();
   }

   public void failed(Throwable x) {
      this.abort(x);
   }

   public void send() {
      if (!this.getHttpExchanges().isEmpty()) {
         this.process();
      }
   }

   public C acquire() {
      return (C)this.connectionPool.acquire();
   }

   private void process() {
      while(true) {
         C connection = this.acquire();
         if (connection != null) {
            boolean proceed = this.process(connection);
            if (proceed) {
               continue;
            }
         }

         return;
      }
   }

   public boolean process(C connection) {
      HttpClient client = this.getHttpClient();
      HttpExchange exchange = (HttpExchange)this.getHttpExchanges().poll();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Processing exchange {} on {} of {}", exchange, connection, this);
      }

      if (exchange == null) {
         if (!this.connectionPool.release(connection)) {
            connection.close();
         }

         if (!client.isRunning()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} is stopping", client);
            }

            connection.close();
         }

         return false;
      } else {
         Request request = exchange.getRequest();
         Throwable cause = request.getAbortCause();
         if (cause != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborted before processing {}: {}", exchange, cause);
            }

            if (!this.connectionPool.release(connection)) {
               connection.close();
            }

            exchange.abort(cause);
         } else {
            SendFailure result = this.send(connection, exchange);
            if (result != null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Send failed {} for {}", result, exchange);
               }

               if (result.retry && this.enqueue(this.getHttpExchanges(), exchange)) {
                  return true;
               }

               request.abort(result.failure);
            }
         }

         return this.getHttpExchanges().peek() != null;
      }
   }

   protected abstract SendFailure send(C var1, HttpExchange var2);

   public void release(Connection c) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Released {}", c);
      }

      HttpClient client = this.getHttpClient();
      if (client.isRunning()) {
         if (this.connectionPool.isActive(c)) {
            if (this.connectionPool.release(c)) {
               this.send();
            } else {
               c.close();
            }
         } else if (LOG.isDebugEnabled()) {
            LOG.debug("Released explicit {}", c);
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} is stopped", client);
         }

         c.close();
      }

   }

   public void close(Connection connection) {
      super.close(connection);
      boolean removed = this.connectionPool.remove(connection);
      if (this.getHttpExchanges().isEmpty()) {
         if (this.getHttpClient().isRemoveIdleDestinations() && this.connectionPool.isEmpty()) {
            this.getHttpClient().removeDestination(this);
         }
      } else if (removed) {
         this.process();
      }

   }

   public void close() {
      super.close();
      this.connectionPool.close();
   }

   public void dump(Appendable out, String indent) throws IOException {
      super.dump(out, indent);
      ContainerLifeCycle.dump(out, indent, Collections.singletonList(this.connectionPool));
   }

   public String toString() {
      return String.format("%s,pool=%s", super.toString(), this.connectionPool);
   }
}
