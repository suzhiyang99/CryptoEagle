package org.spark_project.jetty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.ManagedSelector;
import org.spark_project.jetty.io.SelectChannelEndPoint;
import org.spark_project.jetty.io.SelectorManager;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.Name;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Scheduler;

@ManagedObject("HTTP connector using NIO ByteChannels and Selectors")
public class ServerConnector extends AbstractNetworkConnector {
   private final SelectorManager _manager;
   private volatile ServerSocketChannel _acceptChannel;
   private volatile boolean _inheritChannel;
   private volatile int _localPort;
   private volatile int _acceptQueueSize;
   private volatile boolean _reuseAddress;
   private volatile int _lingerTime;

   public ServerConnector(@Name("server") Server server) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, new HttpConnectionFactory());
   }

   public ServerConnector(@Name("server") Server server, @Name("acceptors") int acceptors, @Name("selectors") int selectors) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, acceptors, selectors, new HttpConnectionFactory());
   }

   public ServerConnector(@Name("server") Server server, @Name("acceptors") int acceptors, @Name("selectors") int selectors, @Name("factories") ConnectionFactory... factories) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, acceptors, selectors, factories);
   }

   public ServerConnector(@Name("server") Server server, @Name("factories") ConnectionFactory... factories) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, factories);
   }

   public ServerConnector(@Name("server") Server server, @Name("sslContextFactory") SslContextFactory sslContextFactory) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public ServerConnector(@Name("server") Server server, @Name("acceptors") int acceptors, @Name("selectors") int selectors, @Name("sslContextFactory") SslContextFactory sslContextFactory) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, acceptors, selectors, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public ServerConnector(@Name("server") Server server, @Name("sslContextFactory") SslContextFactory sslContextFactory, @Name("factories") ConnectionFactory... factories) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, factories));
   }

   public ServerConnector(@Name("server") Server server, @Name("executor") Executor executor, @Name("scheduler") Scheduler scheduler, @Name("bufferPool") ByteBufferPool bufferPool, @Name("acceptors") int acceptors, @Name("selectors") int selectors, @Name("factories") ConnectionFactory... factories) {
      super(server, executor, scheduler, bufferPool, acceptors, factories);
      this._inheritChannel = false;
      this._localPort = -1;
      this._acceptQueueSize = 0;
      this._reuseAddress = true;
      this._lingerTime = -1;
      this._manager = this.newSelectorManager(this.getExecutor(), this.getScheduler(), selectors > 0 ? selectors : Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2)));
      this.addBean(this._manager, true);
      this.setAcceptorPriorityDelta(-2);
   }

   protected SelectorManager newSelectorManager(Executor executor, Scheduler scheduler, int selectors) {
      return new ServerConnectorManager(executor, scheduler, selectors);
   }

   protected void doStart() throws Exception {
      super.doStart();
      if (this.getAcceptors() == 0) {
         this._acceptChannel.configureBlocking(false);
         this._manager.acceptor(this._acceptChannel);
      }

   }

   public boolean isOpen() {
      ServerSocketChannel channel = this._acceptChannel;
      return channel != null && channel.isOpen();
   }

   /** @deprecated */
   @Deprecated
   public int getSelectorPriorityDelta() {
      return this._manager.getSelectorPriorityDelta();
   }

   /** @deprecated */
   @Deprecated
   public void setSelectorPriorityDelta(int selectorPriorityDelta) {
      this._manager.setSelectorPriorityDelta(selectorPriorityDelta);
   }

   public boolean isInheritChannel() {
      return this._inheritChannel;
   }

   public void setInheritChannel(boolean inheritChannel) {
      this._inheritChannel = inheritChannel;
   }

   public void open() throws IOException {
      if (this._acceptChannel == null) {
         ServerSocketChannel serverChannel = null;
         if (this.isInheritChannel()) {
            Channel channel = System.inheritedChannel();
            if (channel instanceof ServerSocketChannel) {
               serverChannel = (ServerSocketChannel)channel;
            } else {
               this.LOG.warn("Unable to use System.inheritedChannel() [{}]. Trying a new ServerSocketChannel at {}:{}", channel, this.getHost(), this.getPort());
            }
         }

         if (serverChannel == null) {
            serverChannel = ServerSocketChannel.open();
            InetSocketAddress bindAddress = this.getHost() == null ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
            serverChannel.socket().setReuseAddress(this.getReuseAddress());
            serverChannel.socket().bind(bindAddress, this.getAcceptQueueSize());
            this._localPort = serverChannel.socket().getLocalPort();
            if (this._localPort <= 0) {
               throw new IOException("Server channel not bound");
            }
         }

         serverChannel.configureBlocking(true);
         this.addBean(serverChannel);
         this._acceptChannel = serverChannel;
      }

   }

   public Future<Void> shutdown() {
      return super.shutdown();
   }

   public void close() {
      ServerSocketChannel serverChannel = this._acceptChannel;
      this._acceptChannel = null;
      if (serverChannel != null) {
         this.removeBean(serverChannel);
         if (serverChannel.isOpen()) {
            try {
               serverChannel.close();
            } catch (IOException e) {
               this.LOG.warn(e);
            }
         }
      }

      this._localPort = -2;
   }

   public void accept(int acceptorID) throws IOException {
      ServerSocketChannel serverChannel = this._acceptChannel;
      if (serverChannel != null && serverChannel.isOpen()) {
         SocketChannel channel = serverChannel.accept();
         this.accepted(channel);
      }

   }

   private void accepted(SocketChannel channel) throws IOException {
      channel.configureBlocking(false);
      Socket socket = channel.socket();
      this.configure(socket);
      this._manager.accept(channel);
   }

   protected void configure(Socket socket) {
      try {
         socket.setTcpNoDelay(true);
         if (this._lingerTime >= 0) {
            socket.setSoLinger(true, this._lingerTime / 1000);
         } else {
            socket.setSoLinger(false, 0);
         }
      } catch (SocketException e) {
         this.LOG.ignore(e);
      }

   }

   public SelectorManager getSelectorManager() {
      return this._manager;
   }

   public Object getTransport() {
      return this._acceptChannel;
   }

   @ManagedAttribute("local port")
   public int getLocalPort() {
      return this._localPort;
   }

   protected SelectChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) throws IOException {
      return new SelectChannelEndPoint(channel, selectSet, key, this.getScheduler(), this.getIdleTimeout());
   }

   @ManagedAttribute("TCP/IP solinger time or -1 to disable")
   public int getSoLingerTime() {
      return this._lingerTime;
   }

   public void setSoLingerTime(int lingerTime) {
      this._lingerTime = lingerTime;
   }

   @ManagedAttribute("Accept Queue size")
   public int getAcceptQueueSize() {
      return this._acceptQueueSize;
   }

   public void setAcceptQueueSize(int acceptQueueSize) {
      this._acceptQueueSize = acceptQueueSize;
   }

   public boolean getReuseAddress() {
      return this._reuseAddress;
   }

   public void setReuseAddress(boolean reuseAddress) {
      this._reuseAddress = reuseAddress;
   }

   public ExecutionStrategy.Factory getExecutionStrategyFactory() {
      return this._manager.getExecutionStrategyFactory();
   }

   public void setExecutionStrategyFactory(ExecutionStrategy.Factory executionFactory) {
      this._manager.setExecutionStrategyFactory(executionFactory);
   }

   protected class ServerConnectorManager extends SelectorManager {
      public ServerConnectorManager(Executor executor, Scheduler scheduler, int selectors) {
         super(executor, scheduler, selectors);
      }

      protected void accepted(SocketChannel channel) throws IOException {
         ServerConnector.this.accepted(channel);
      }

      protected SelectChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey selectionKey) throws IOException {
         return ServerConnector.this.newEndPoint(channel, selectSet, selectionKey);
      }

      public Connection newConnection(SocketChannel channel, EndPoint endpoint, Object attachment) throws IOException {
         return ServerConnector.this.getDefaultConnectionFactory().newConnection(ServerConnector.this, endpoint);
      }

      protected void endPointOpened(EndPoint endpoint) {
         super.endPointOpened(endpoint);
         ServerConnector.this.onEndPointOpened(endpoint);
      }

      protected void endPointClosed(EndPoint endpoint) {
         ServerConnector.this.onEndPointClosed(endpoint);
         super.endPointClosed(endpoint);
      }
   }
}
