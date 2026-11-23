package org.spark_project.jetty.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.ManagedSelector;
import org.spark_project.jetty.io.NetworkTrafficListener;
import org.spark_project.jetty.io.NetworkTrafficSelectChannelEndPoint;
import org.spark_project.jetty.io.SelectChannelEndPoint;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.Scheduler;

public class NetworkTrafficServerConnector extends ServerConnector {
   private final List<NetworkTrafficListener> listeners;

   public NetworkTrafficServerConnector(Server server) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, 0, 0, new HttpConnectionFactory());
   }

   public NetworkTrafficServerConnector(Server server, ConnectionFactory connectionFactory, SslContextFactory sslContextFactory) {
      super(server, sslContextFactory, connectionFactory);
      this.listeners = new CopyOnWriteArrayList();
   }

   public NetworkTrafficServerConnector(Server server, ConnectionFactory connectionFactory) {
      super(server, connectionFactory);
      this.listeners = new CopyOnWriteArrayList();
   }

   public NetworkTrafficServerConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool pool, int acceptors, int selectors, ConnectionFactory... factories) {
      super(server, executor, scheduler, pool, acceptors, selectors, factories);
      this.listeners = new CopyOnWriteArrayList();
   }

   public NetworkTrafficServerConnector(Server server, SslContextFactory sslContextFactory) {
      super(server, sslContextFactory);
      this.listeners = new CopyOnWriteArrayList();
   }

   public void addNetworkTrafficListener(NetworkTrafficListener listener) {
      this.listeners.add(listener);
   }

   public void removeNetworkTrafficListener(NetworkTrafficListener listener) {
      this.listeners.remove(listener);
   }

   protected SelectChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) throws IOException {
      NetworkTrafficSelectChannelEndPoint endPoint = new NetworkTrafficSelectChannelEndPoint(channel, selectSet, key, this.getScheduler(), this.getIdleTimeout(), this.listeners);
      return endPoint;
   }
}
