package org.spark_project.jetty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.ManagedSelector;
import org.spark_project.jetty.io.SelectChannelEndPoint;
import org.spark_project.jetty.io.SelectorManager;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject
public abstract class AbstractHttpClientTransport extends ContainerLifeCycle implements HttpClientTransport {
   protected static final Logger LOG = Log.getLogger(HttpClientTransport.class);
   private final int selectors;
   private volatile HttpClient client;
   private volatile SelectorManager selectorManager;

   protected AbstractHttpClientTransport(int selectors) {
      this.selectors = selectors;
   }

   protected HttpClient getHttpClient() {
      return this.client;
   }

   public void setHttpClient(HttpClient client) {
      this.client = client;
   }

   @ManagedAttribute(
      value = "The number of selectors",
      readonly = true
   )
   public int getSelectors() {
      return this.selectors;
   }

   protected void doStart() throws Exception {
      this.selectorManager = this.newSelectorManager(this.client);
      this.selectorManager.setConnectTimeout(this.client.getConnectTimeout());
      this.addBean(this.selectorManager);
      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this.selectorManager);
   }

   public void connect(InetSocketAddress address, Map<String, Object> context) {
      SocketChannel channel = null;

      try {
         channel = SocketChannel.open();
         HttpDestination destination = (HttpDestination)context.get("http.destination");
         HttpClient client = destination.getHttpClient();
         SocketAddress bindAddress = client.getBindAddress();
         if (bindAddress != null) {
            channel.bind(bindAddress);
         }

         this.configure(client, channel);
         context.put("ssl.peer.host", destination.getHost());
         context.put("ssl.peer.port", destination.getPort());
         if (client.isConnectBlocking()) {
            channel.socket().connect(address, (int)client.getConnectTimeout());
            channel.configureBlocking(false);
            this.selectorManager.accept(channel, context);
         } else {
            channel.configureBlocking(false);
            if (channel.connect(address)) {
               this.selectorManager.accept(channel, context);
            } else {
               this.selectorManager.connect(channel, context);
            }
         }
      } catch (Throwable var13) {
         Throwable x = var13;
         if (var13.getClass() == SocketException.class) {
            x = (new SocketException("Could not connect to " + address)).initCause(var13);
         }

         try {
            if (channel != null) {
               channel.close();
            }
         } catch (IOException xx) {
            LOG.ignore(xx);
         } finally {
            this.connectFailed(context, x);
         }
      }

   }

   protected void connectFailed(Map<String, Object> context, Throwable x) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Could not connect to {}", context.get("http.destination"));
      }

      Promise<Connection> promise = (Promise)context.get("http.connection.promise");
      promise.failed(x);
   }

   protected void configure(HttpClient client, SocketChannel channel) throws IOException {
      channel.socket().setTcpNoDelay(client.isTCPNoDelay());
   }

   protected SelectorManager newSelectorManager(HttpClient client) {
      return new ClientSelectorManager(client, this.selectors);
   }

   protected class ClientSelectorManager extends SelectorManager {
      private final HttpClient client;

      protected ClientSelectorManager(HttpClient client, int selectors) {
         super(client.getExecutor(), client.getScheduler(), selectors);
         this.client = client;
      }

      protected EndPoint newEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key) {
         return new SelectChannelEndPoint(channel, selector, key, this.getScheduler(), this.client.getIdleTimeout());
      }

      public org.spark_project.jetty.io.Connection newConnection(SocketChannel channel, EndPoint endPoint, Object attachment) throws IOException {
         Map<String, Object> context = (Map)attachment;
         HttpDestination destination = (HttpDestination)context.get("http.destination");
         return destination.getClientConnectionFactory().newConnection(endPoint, context);
      }

      protected void connectionFailed(SocketChannel channel, Throwable x, Object attachment) {
         Map<String, Object> context = (Map)attachment;
         AbstractHttpClientTransport.this.connectFailed(context, x);
      }
   }
}
