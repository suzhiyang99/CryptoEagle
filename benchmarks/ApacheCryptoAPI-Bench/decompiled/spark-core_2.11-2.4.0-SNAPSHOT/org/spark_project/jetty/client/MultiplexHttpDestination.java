package org.spark_project.jetty.client;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.util.Promise;

public abstract class MultiplexHttpDestination<C extends Connection> extends HttpDestination implements Promise<Connection> {
   private final AtomicReference<ConnectState> connect;
   private final AtomicInteger requestsPerConnection;
   private int maxRequestsPerConnection;
   private C connection;

   protected MultiplexHttpDestination(HttpClient client, Origin origin) {
      super(client, origin);
      this.connect = new AtomicReference(MultiplexHttpDestination.ConnectState.DISCONNECTED);
      this.requestsPerConnection = new AtomicInteger();
      this.maxRequestsPerConnection = 1024;
   }

   public int getMaxRequestsPerConnection() {
      return this.maxRequestsPerConnection;
   }

   public void setMaxRequestsPerConnection(int maxRequestsPerConnection) {
      this.maxRequestsPerConnection = maxRequestsPerConnection;
   }

   public void send() {
      while(true) {
         ConnectState current = (ConnectState)this.connect.get();
         switch (current) {
            case DISCONNECTED:
               if (!this.connect.compareAndSet(current, MultiplexHttpDestination.ConnectState.CONNECTING)) {
                  break;
               }

               this.newConnection(this);
               return;
            case CONNECTING:
               return;
            case CONNECTED:
               if (this.process(this.connection)) {
                  break;
               }

               return;
            default:
               this.abort(new IllegalStateException("Invalid connection state " + current));
               return;
         }
      }
   }

   public void succeeded(Connection result) {
      C connection = this.connection = result;
      if (this.connect.compareAndSet(MultiplexHttpDestination.ConnectState.CONNECTING, MultiplexHttpDestination.ConnectState.CONNECTED)) {
         this.send();
      } else {
         connection.close();
         this.failed(new IllegalStateException("Invalid connection state " + this.connect));
      }

   }

   public void failed(Throwable x) {
      this.connect.set(MultiplexHttpDestination.ConnectState.DISCONNECTED);
      this.abort(x);
   }

   protected boolean process(C connection) {
      int max;
      int count;
      int next;
      do {
         max = this.getMaxRequestsPerConnection();
         count = this.requestsPerConnection.get();
         next = count + 1;
         if (next > max) {
            return false;
         }
      } while(!this.requestsPerConnection.compareAndSet(count, next));

      HttpExchange exchange = (HttpExchange)this.getHttpExchanges().poll();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Processing {}/{} {} on {}", next, max, exchange, connection);
      }

      if (exchange == null) {
         this.requestsPerConnection.decrementAndGet();
         return false;
      } else {
         Request request = exchange.getRequest();
         Throwable cause = request.getAbortCause();
         if (cause != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborted before processing {}: {}", exchange, cause);
            }

            this.requestsPerConnection.decrementAndGet();
            exchange.abort(cause);
         } else {
            SendFailure result = this.send(connection, exchange);
            if (result != null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Send failed {} for {}", result, exchange);
               }

               this.requestsPerConnection.decrementAndGet();
               if (result.retry && this.enqueue(this.getHttpExchanges(), exchange)) {
                  return true;
               }

               request.abort(result.failure);
            }
         }

         return this.getHttpExchanges().peek() != null;
      }
   }

   public void release(Connection connection) {
      this.requestsPerConnection.decrementAndGet();
      this.send();
   }

   public void close() {
      super.close();
      C connection = this.connection;
      if (connection != null) {
         connection.close();
      }

   }

   public void close(Connection connection) {
      super.close(connection);

      ConnectState current;
      do {
         current = (ConnectState)this.connect.get();
      } while(!this.connect.compareAndSet(current, MultiplexHttpDestination.ConnectState.DISCONNECTED));

      if (this.getHttpClient().isRemoveIdleDestinations()) {
         this.getHttpClient().removeDestination(this);
      }

   }

   protected abstract SendFailure send(C var1, HttpExchange var2);

   private static enum ConnectState {
      DISCONNECTED,
      CONNECTING,
      CONNECTED;
   }
}
