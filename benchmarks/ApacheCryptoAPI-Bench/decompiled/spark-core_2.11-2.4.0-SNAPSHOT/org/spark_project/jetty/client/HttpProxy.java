package org.spark_project.jetty.client;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.http.HttpConnectionOverHTTP;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.io.ClientConnectionFactory;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.ssl.SslContextFactory;

public class HttpProxy extends ProxyConfiguration.Proxy {
   private static final Logger LOG = Log.getLogger(HttpProxy.class);

   public HttpProxy(String host, int port) {
      this(new Origin.Address(host, port), false);
   }

   public HttpProxy(Origin.Address address, boolean secure) {
      super(address, secure);
   }

   public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory) {
      return new HttpProxyClientConnectionFactory(connectionFactory);
   }

   public URI getURI() {
      String scheme = this.isSecure() ? HttpScheme.HTTPS.asString() : HttpScheme.HTTP.asString();
      return URI.create((new Origin(scheme, this.getAddress())).asString());
   }

   private class HttpProxyClientConnectionFactory implements ClientConnectionFactory {
      private final ClientConnectionFactory connectionFactory;

      private HttpProxyClientConnectionFactory(ClientConnectionFactory connectionFactory) {
         this.connectionFactory = connectionFactory;
      }

      public org.spark_project.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
         HttpDestination destination = (HttpDestination)context.get("http.destination");
         boolean secure = HttpScheme.HTTPS.is(destination.getScheme());
         SslContextFactory sslContextFactory = destination.getHttpClient().getSslContextFactory();
         if (secure) {
            if (sslContextFactory != null) {
               Promise<Connection> promise = (Promise)context.get("http.connection.promise");
               Promise<Connection> wrapped = promise;
               if (promise instanceof Promise.Wrapper) {
                  wrapped = (promise).unwrap();
               }

               if (wrapped instanceof TunnelPromise) {
                  ((TunnelPromise)wrapped).setEndPoint(endPoint);
                  return this.connectionFactory.newConnection(endPoint, context);
               } else {
                  CreateTunnelPromise tunnelPromise = HttpProxy.this.new CreateTunnelPromise(this.connectionFactory, endPoint, promise, context);
                  context.put("http.connection.promise", tunnelPromise);
                  return this.connectionFactory.newConnection(endPoint, context);
               }
            } else {
               throw new IOException("Cannot tunnel request, missing " + SslContextFactory.class.getName() + " in " + HttpClient.class.getName());
            }
         } else {
            return this.connectionFactory.newConnection(endPoint, context);
         }
      }
   }

   private class CreateTunnelPromise implements Promise<Connection> {
      private final ClientConnectionFactory connectionFactory;
      private final EndPoint endPoint;
      private final Promise<Connection> promise;
      private final Map<String, Object> context;

      private CreateTunnelPromise(ClientConnectionFactory connectionFactory, EndPoint endPoint, Promise<Connection> promise, Map<String, Object> context) {
         this.connectionFactory = connectionFactory;
         this.endPoint = endPoint;
         this.promise = promise;
         this.context = context;
      }

      public void succeeded(Connection connection) {
         HttpDestination destination = (HttpDestination)this.context.get("http.destination");
         this.tunnel(destination, connection);
      }

      public void failed(Throwable x) {
         this.tunnelFailed(this.endPoint, x);
      }

      private void tunnel(HttpDestination destination, Connection connection) {
         String target = destination.getOrigin().getAddress().asString();
         Origin.Address proxyAddress = destination.getConnectAddress();
         HttpClient httpClient = destination.getHttpClient();
         long connectTimeout = httpClient.getConnectTimeout();
         Request connect = httpClient.newRequest(proxyAddress.getHost(), proxyAddress.getPort()).method(HttpMethod.CONNECT).path(target).header(HttpHeader.HOST, target).idleTimeout(2L * connectTimeout, TimeUnit.MILLISECONDS).timeout(connectTimeout, TimeUnit.MILLISECONDS);
         ProxyConfiguration.Proxy proxy = destination.getProxy();
         if (proxy != null && proxy.isSecure()) {
            connect.scheme(HttpScheme.HTTPS.asString());
         }

         HttpConversation conversation = ((HttpRequest)connect).getConversation();
         conversation.setAttribute(EndPoint.class.getName(), this.endPoint);
         connect.attribute(Connection.class.getName(), HttpProxy.this.new ProxyConnection(destination, connection, this.promise));
         connection.send(connect, (result) -> {
            EndPoint endPoint = (EndPoint)conversation.getAttribute(EndPoint.class.getName());
            if (result.isSucceeded()) {
               Response response = result.getResponse();
               if (response.getStatus() == 200) {
                  this.tunnelSucceeded(endPoint);
               } else {
                  HttpResponseException failure = new HttpResponseException("Unexpected " + response + " for " + result.getRequest(), response);
                  this.tunnelFailed(endPoint, failure);
               }
            } else {
               this.tunnelFailed(endPoint, result.getFailure());
            }

         });
      }

      private void tunnelSucceeded(EndPoint endPoint) {
         try {
            this.context.put("http.connection.promise", this.promise);
            HttpDestination destination = (HttpDestination)this.context.get("http.destination");
            HttpClient client = destination.getHttpClient();
            ClientConnectionFactory sslConnectionFactory = client.newSslClientConnectionFactory(this.connectionFactory);
            HttpConnectionOverHTTP oldConnection = (HttpConnectionOverHTTP)endPoint.getConnection();
            org.spark_project.jetty.io.Connection newConnection = sslConnectionFactory.newConnection(endPoint, this.context);
            endPoint.setConnection(oldConnection);
            endPoint.upgrade(newConnection);
            if (HttpProxy.LOG.isDebugEnabled()) {
               HttpProxy.LOG.debug("HTTP tunnel established: {} over {}", oldConnection, newConnection);
            }
         } catch (Throwable x) {
            this.tunnelFailed(endPoint, x);
         }

      }

      private void tunnelFailed(EndPoint endPoint, Throwable failure) {
         endPoint.close();
         this.promise.failed(failure);
      }
   }

   private class ProxyConnection implements Connection {
      private final Destination destination;
      private final Connection connection;
      private final Promise<Connection> promise;

      private ProxyConnection(Destination destination, Connection connection, Promise<Connection> promise) {
         this.destination = destination;
         this.connection = connection;
         this.promise = promise;
      }

      public void send(Request request, Response.CompleteListener listener) {
         if (this.connection.isClosed()) {
            this.destination.newConnection(HttpProxy.this.new TunnelPromise(request, listener, this.promise));
         } else {
            this.connection.send(request, listener);
         }

      }

      public void close() {
         this.connection.close();
      }

      public boolean isClosed() {
         return this.connection.isClosed();
      }
   }

   private class TunnelPromise implements Promise<Connection> {
      private final Request request;
      private final Response.CompleteListener listener;
      private final Promise<Connection> promise;

      private TunnelPromise(Request request, Response.CompleteListener listener, Promise<Connection> promise) {
         this.request = request;
         this.listener = listener;
         this.promise = promise;
      }

      public void succeeded(Connection connection) {
         connection.send(this.request, this.listener);
      }

      public void failed(Throwable x) {
         this.promise.failed(x);
      }

      private void setEndPoint(EndPoint endPoint) {
         HttpConversation conversation = ((HttpRequest)this.request).getConversation();
         conversation.setAttribute(EndPoint.class.getName(), endPoint);
      }
   }
}
