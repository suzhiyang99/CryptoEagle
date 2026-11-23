package org.spark_project.jetty.client;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.io.ClientConnectionFactory;
import org.spark_project.jetty.util.BlockingArrayQueue;
import org.spark_project.jetty.util.HostPort;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject
public abstract class HttpDestination extends ContainerLifeCycle implements Destination, Closeable, Dumpable {
   protected static final Logger LOG = Log.getLogger(HttpDestination.class);
   private final HttpClient client;
   private final Origin origin;
   private final Queue<HttpExchange> exchanges;
   private final RequestNotifier requestNotifier;
   private final ResponseNotifier responseNotifier;
   private final ProxyConfiguration.Proxy proxy;
   private final ClientConnectionFactory connectionFactory;
   private final HttpField hostField;

   public HttpDestination(HttpClient client, Origin origin) {
      this.client = client;
      this.origin = origin;
      this.exchanges = this.newExchangeQueue(client);
      this.requestNotifier = new RequestNotifier(client);
      this.responseNotifier = new ResponseNotifier();
      ProxyConfiguration proxyConfig = client.getProxyConfiguration();
      this.proxy = proxyConfig.match(origin);
      ClientConnectionFactory connectionFactory = client.getTransport();
      if (this.proxy != null) {
         connectionFactory = this.proxy.newClientConnectionFactory(connectionFactory);
         if (this.proxy.isSecure()) {
            connectionFactory = this.newSslClientConnectionFactory(connectionFactory);
         }
      } else if (HttpScheme.HTTPS.is(this.getScheme())) {
         connectionFactory = this.newSslClientConnectionFactory(connectionFactory);
      }

      this.connectionFactory = connectionFactory;
      String host = HostPort.normalizeHost(this.getHost());
      if (!client.isDefaultPort(this.getScheme(), this.getPort())) {
         host = host + ":" + this.getPort();
      }

      this.hostField = new HttpField(HttpHeader.HOST, host);
   }

   protected Queue<HttpExchange> newExchangeQueue(HttpClient client) {
      return new BlockingArrayQueue<HttpExchange>(client.getMaxRequestsQueuedPerDestination());
   }

   protected ClientConnectionFactory newSslClientConnectionFactory(ClientConnectionFactory connectionFactory) {
      return this.client.newSslClientConnectionFactory(connectionFactory);
   }

   public HttpClient getHttpClient() {
      return this.client;
   }

   public Origin getOrigin() {
      return this.origin;
   }

   public Queue<HttpExchange> getHttpExchanges() {
      return this.exchanges;
   }

   public RequestNotifier getRequestNotifier() {
      return this.requestNotifier;
   }

   public ResponseNotifier getResponseNotifier() {
      return this.responseNotifier;
   }

   public ProxyConfiguration.Proxy getProxy() {
      return this.proxy;
   }

   public ClientConnectionFactory getClientConnectionFactory() {
      return this.connectionFactory;
   }

   @ManagedAttribute(
      value = "The destination scheme",
      readonly = true
   )
   public String getScheme() {
      return this.origin.getScheme();
   }

   @ManagedAttribute(
      value = "The destination host",
      readonly = true
   )
   public String getHost() {
      return this.origin.getAddress().getHost();
   }

   @ManagedAttribute(
      value = "The destination port",
      readonly = true
   )
   public int getPort() {
      return this.origin.getAddress().getPort();
   }

   @ManagedAttribute(
      value = "The number of queued requests",
      readonly = true
   )
   public int getQueuedRequestCount() {
      return this.exchanges.size();
   }

   public Origin.Address getConnectAddress() {
      return this.proxy == null ? this.origin.getAddress() : this.proxy.getAddress();
   }

   public HttpField getHostField() {
      return this.hostField;
   }

   protected void send(HttpRequest request, List<Response.ResponseListener> listeners) {
      if (!this.getScheme().equalsIgnoreCase(request.getScheme())) {
         throw new IllegalArgumentException("Invalid request scheme " + request.getScheme() + " for destination " + this);
      } else if (!this.getHost().equalsIgnoreCase(request.getHost())) {
         throw new IllegalArgumentException("Invalid request host " + request.getHost() + " for destination " + this);
      } else {
         int port = request.getPort();
         if (port >= 0 && this.getPort() != port) {
            throw new IllegalArgumentException("Invalid request port " + port + " for destination " + this);
         } else {
            HttpExchange exchange = new HttpExchange(this, request, listeners);
            if (this.client.isRunning()) {
               if (this.enqueue(this.exchanges, exchange)) {
                  if (!this.client.isRunning() && this.exchanges.remove(exchange)) {
                     request.abort(new RejectedExecutionException(this.client + " is stopping"));
                  } else {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Queued {} for {}", request, this);
                     }

                     this.requestNotifier.notifyQueued(request);
                     this.send();
                  }
               } else {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Max queue size {} exceeded by {} for {}", this.client.getMaxRequestsQueuedPerDestination(), request, this);
                  }

                  request.abort(new RejectedExecutionException("Max requests per destination " + this.client.getMaxRequestsQueuedPerDestination() + " exceeded for " + this));
               }
            } else {
               request.abort(new RejectedExecutionException(this.client + " is stopped"));
            }

         }
      }
   }

   protected boolean enqueue(Queue<HttpExchange> queue, HttpExchange exchange) {
      return queue.offer(exchange);
   }

   public abstract void send();

   public void newConnection(Promise<Connection> promise) {
      this.createConnection(promise);
   }

   protected void createConnection(Promise<Connection> promise) {
      this.client.newConnection(this, promise);
   }

   public boolean remove(HttpExchange exchange) {
      return this.exchanges.remove(exchange);
   }

   public void close() {
      this.abort(new AsynchronousCloseException());
      if (LOG.isDebugEnabled()) {
         LOG.debug("Closed {}", this);
      }

   }

   public void release(Connection connection) {
   }

   public void close(Connection connection) {
   }

   public void abort(Throwable cause) {
      for(HttpExchange exchange : new ArrayList(this.exchanges)) {
         exchange.getRequest().abort(cause);
      }

   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      ContainerLifeCycle.dumpObject(out, this.toString());
   }

   public String asString() {
      return this.origin.asString();
   }

   public String toString() {
      return String.format("%s[%s]@%x%s,queue=%d", HttpDestination.class.getSimpleName(), this.asString(), this.hashCode(), this.proxy == null ? "" : "(via " + this.proxy + ")", this.exchanges.size());
   }
}
