package org.spark_project.jetty.proxy;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.ManagedSelector;
import org.spark_project.jetty.io.MappedByteBufferPool;
import org.spark_project.jetty.io.SelectChannelEndPoint;
import org.spark_project.jetty.io.SelectorManager;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.HttpConnection;
import org.spark_project.jetty.server.HttpTransport;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.handler.HandlerWrapper;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.HostPort;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.TypeUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;

public class ConnectHandler extends HandlerWrapper {
   protected static final Logger LOG = Log.getLogger(ConnectHandler.class);
   private final Set<String> whiteList;
   private final Set<String> blackList;
   private Executor executor;
   private Scheduler scheduler;
   private ByteBufferPool bufferPool;
   private SelectorManager selector;
   private long connectTimeout;
   private long idleTimeout;
   private int bufferSize;

   public ConnectHandler() {
      this((Handler)null);
   }

   public ConnectHandler(Handler handler) {
      this.whiteList = new HashSet();
      this.blackList = new HashSet();
      this.connectTimeout = 15000L;
      this.idleTimeout = 30000L;
      this.bufferSize = 4096;
      this.setHandler(handler);
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setExecutor(Executor executor) {
      this.executor = executor;
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public void setScheduler(Scheduler scheduler) {
      this.scheduler = scheduler;
   }

   public ByteBufferPool getByteBufferPool() {
      return this.bufferPool;
   }

   public void setByteBufferPool(ByteBufferPool bufferPool) {
      this.bufferPool = bufferPool;
   }

   public long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   protected void doStart() throws Exception {
      if (this.executor == null) {
         this.executor = this.getServer().getThreadPool();
      }

      if (this.scheduler == null) {
         this.addBean(this.scheduler = new ScheduledExecutorScheduler());
      }

      if (this.bufferPool == null) {
         this.addBean(this.bufferPool = new MappedByteBufferPool());
      }

      this.addBean(this.selector = this.newSelectorManager());
      this.selector.setConnectTimeout(this.getConnectTimeout());
      super.doStart();
   }

   protected SelectorManager newSelectorManager() {
      return new ConnectManager(this.getExecutor(), this.getScheduler(), 1);
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (HttpMethod.CONNECT.is(request.getMethod())) {
         String serverAddress = request.getRequestURI();
         if (LOG.isDebugEnabled()) {
            LOG.debug("CONNECT request for {}", serverAddress);
         }

         this.handleConnect(baseRequest, request, response, serverAddress);
      } else {
         super.handle(target, baseRequest, request, response);
      }

   }

   protected void handleConnect(Request baseRequest, final HttpServletRequest request, final HttpServletResponse response, String serverAddress) {
      baseRequest.setHandled(true);

      try {
         boolean proceed = this.handleAuthentication(request, response, serverAddress);
         if (!proceed) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Missing proxy authentication");
            }

            this.sendConnectResponse(request, response, 407);
            return;
         }

         HostPort hostPort = new HostPort(serverAddress);
         String host = hostPort.getHost();
         int port = hostPort.getPort(80);
         if (!this.validateDestination(host, port)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Destination {}:{} forbidden", host, port);
            }

            this.sendConnectResponse(request, response, 403);
            return;
         }

         final HttpTransport transport = baseRequest.getHttpChannel().getHttpTransport();
         if (!(transport instanceof HttpConnection)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("CONNECT not supported for {}", transport);
            }

            this.sendConnectResponse(request, response, 403);
            return;
         }

         final AsyncContext asyncContext = request.startAsync();
         asyncContext.setTimeout(0L);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connecting to {}:{}", host, port);
         }

         this.connectToServer(request, host, port, new Promise<SocketChannel>() {
            public void succeeded(SocketChannel channel) {
               ConnectContext connectContext = new ConnectContext(request, response, asyncContext, (HttpConnection)transport);
               if (channel.isConnected()) {
                  ConnectHandler.this.selector.accept(channel, connectContext);
               } else {
                  ConnectHandler.this.selector.connect(channel, connectContext);
               }

            }

            public void failed(Throwable x) {
               ConnectHandler.this.onConnectFailure(request, response, asyncContext, x);
            }
         });
      } catch (Exception x) {
         this.onConnectFailure(request, response, (AsyncContext)null, x);
      }

   }

   protected void connectToServer(HttpServletRequest request, String host, int port, Promise<SocketChannel> promise) {
      SocketChannel channel = null;

      try {
         channel = SocketChannel.open();
         channel.socket().setTcpNoDelay(true);
         channel.configureBlocking(false);
         InetSocketAddress address = this.newConnectAddress(host, port);
         channel.connect(address);
         promise.succeeded(channel);
      } catch (Throwable x) {
         this.close(channel);
         promise.failed(x);
      }

   }

   private void close(Closeable closeable) {
      try {
         if (closeable != null) {
            closeable.close();
         }
      } catch (Throwable x) {
         LOG.ignore(x);
      }

   }

   protected InetSocketAddress newConnectAddress(String host, int port) {
      return new InetSocketAddress(host, port);
   }

   protected void onConnectSuccess(ConnectContext connectContext, UpstreamConnection upstreamConnection) {
      ConcurrentMap<String, Object> context = connectContext.getContext();
      HttpServletRequest request = connectContext.getRequest();
      this.prepareContext(request, context);
      HttpConnection httpConnection = connectContext.getHttpConnection();
      EndPoint downstreamEndPoint = httpConnection.getEndPoint();
      DownstreamConnection downstreamConnection = this.newDownstreamConnection(downstreamEndPoint, context, BufferUtil.EMPTY_BUFFER);
      downstreamConnection.setInputBufferSize(this.getBufferSize());
      upstreamConnection.setConnection(downstreamConnection);
      downstreamConnection.setConnection(upstreamConnection);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Connection setup completed: {}<->{}", downstreamConnection, upstreamConnection);
      }

      HttpServletResponse response = connectContext.getResponse();
      this.sendConnectResponse(request, response, 200);
      this.upgradeConnection(request, response, downstreamConnection);
      connectContext.getAsyncContext().complete();
   }

   protected void onConnectFailure(HttpServletRequest request, HttpServletResponse response, AsyncContext asyncContext, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("CONNECT failed", failure);
      }

      this.sendConnectResponse(request, response, 500);
      if (asyncContext != null) {
         asyncContext.complete();
      }

   }

   private void sendConnectResponse(HttpServletRequest request, HttpServletResponse response, int statusCode) {
      try {
         response.setStatus(statusCode);
         response.setContentLength(0);
         if (statusCode != 200) {
            response.setHeader(HttpHeader.CONNECTION.asString(), HttpHeaderValue.CLOSE.asString());
         }

         response.getOutputStream().close();
         if (LOG.isDebugEnabled()) {
            LOG.debug("CONNECT response sent {} {}", request.getProtocol(), response.getStatus());
         }
      } catch (IOException x) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not send CONNECT response", (Throwable)x);
         }
      }

   }

   protected boolean handleAuthentication(HttpServletRequest request, HttpServletResponse response, String address) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   protected DownstreamConnection newDownstreamConnection(EndPoint endPoint, ConcurrentMap<String, Object> context, ByteBuffer buffer) {
      return this.newDownstreamConnection(endPoint, context);
   }

   protected DownstreamConnection newDownstreamConnection(EndPoint endPoint, ConcurrentMap<String, Object> context) {
      return new DownstreamConnection(endPoint, this.getExecutor(), this.getByteBufferPool(), context);
   }

   protected UpstreamConnection newUpstreamConnection(EndPoint endPoint, ConnectContext connectContext) {
      return new UpstreamConnection(endPoint, this.getExecutor(), this.getByteBufferPool(), connectContext);
   }

   protected void prepareContext(HttpServletRequest request, ConcurrentMap<String, Object> context) {
   }

   private void upgradeConnection(HttpServletRequest request, HttpServletResponse response, Connection connection) {
      request.setAttribute("org.spark_project.jetty.server.HttpConnection.UPGRADE", connection);
      response.setStatus(101);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Upgraded connection to {}", connection);
      }

   }

   protected int read(EndPoint endPoint, ByteBuffer buffer, ConcurrentMap<String, Object> context) throws IOException {
      int read = this.read(endPoint, buffer);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} read {} bytes", this, read);
      }

      return read;
   }

   /** @deprecated */
   @Deprecated
   protected int read(EndPoint endPoint, ByteBuffer buffer) throws IOException {
      return endPoint.fill(buffer);
   }

   protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback, ConcurrentMap<String, Object> context) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} writing {} bytes", this, buffer.remaining());
      }

      this.write(endPoint, buffer, callback);
   }

   /** @deprecated */
   @Deprecated
   protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback) {
      endPoint.write(callback, buffer);
   }

   public Set<String> getWhiteListHosts() {
      return this.whiteList;
   }

   public Set<String> getBlackListHosts() {
      return this.blackList;
   }

   public boolean validateDestination(String host, int port) {
      String hostPort = host + ":" + port;
      if (!this.whiteList.isEmpty() && !this.whiteList.contains(hostPort)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Host {}:{} not whitelisted", host, port);
         }

         return false;
      } else if (!this.blackList.isEmpty() && this.blackList.contains(hostPort)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Host {}:{} blacklisted", host, port);
         }

         return false;
      } else {
         return true;
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpThis(out);
      dump(out, indent, new Collection[]{this.getBeans(), TypeUtil.asList(this.getHandlers())});
   }

   protected class ConnectManager extends SelectorManager {
      protected ConnectManager(Executor executor, Scheduler scheduler, int selectors) {
         super(executor, scheduler, selectors);
      }

      protected EndPoint newEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey selectionKey) throws IOException {
         return new SelectChannelEndPoint(channel, selector, selectionKey, this.getScheduler(), ConnectHandler.this.getIdleTimeout());
      }

      public Connection newConnection(SocketChannel channel, EndPoint endpoint, Object attachment) throws IOException {
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Connected to {}", channel.getRemoteAddress());
         }

         ConnectContext connectContext = (ConnectContext)attachment;
         UpstreamConnection connection = ConnectHandler.this.newUpstreamConnection(endpoint, connectContext);
         connection.setInputBufferSize(ConnectHandler.this.getBufferSize());
         return connection;
      }

      protected void connectionFailed(SocketChannel channel, Throwable ex, Object attachment) {
         ConnectHandler.this.close(channel);
         ConnectContext connectContext = (ConnectContext)attachment;
         ConnectHandler.this.onConnectFailure(connectContext.request, connectContext.response, connectContext.asyncContext, ex);
      }
   }

   protected static class ConnectContext {
      private final ConcurrentMap<String, Object> context = new ConcurrentHashMap();
      private final HttpServletRequest request;
      private final HttpServletResponse response;
      private final AsyncContext asyncContext;
      private final HttpConnection httpConnection;

      public ConnectContext(HttpServletRequest request, HttpServletResponse response, AsyncContext asyncContext, HttpConnection httpConnection) {
         this.request = request;
         this.response = response;
         this.asyncContext = asyncContext;
         this.httpConnection = httpConnection;
      }

      public ConcurrentMap<String, Object> getContext() {
         return this.context;
      }

      public HttpServletRequest getRequest() {
         return this.request;
      }

      public HttpServletResponse getResponse() {
         return this.response;
      }

      public AsyncContext getAsyncContext() {
         return this.asyncContext;
      }

      public HttpConnection getHttpConnection() {
         return this.httpConnection;
      }
   }

   public class UpstreamConnection extends ProxyConnection {
      private ConnectContext connectContext;

      public UpstreamConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConnectContext connectContext) {
         super(endPoint, executor, bufferPool, connectContext.getContext());
         this.connectContext = connectContext;
      }

      public void onOpen() {
         super.onOpen();
         ConnectHandler.this.onConnectSuccess(this.connectContext, this);
         this.fillInterested();
      }

      protected int read(EndPoint endPoint, ByteBuffer buffer) throws IOException {
         return ConnectHandler.this.read(endPoint, buffer, this.getContext());
      }

      protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback) {
         ConnectHandler.this.write(endPoint, buffer, callback, this.getContext());
      }
   }

   public class DownstreamConnection extends ProxyConnection implements Connection.UpgradeTo {
      private ByteBuffer buffer;

      public DownstreamConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context) {
         super(endPoint, executor, bufferPool, context);
      }

      /** @deprecated */
      @Deprecated
      public DownstreamConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context, ByteBuffer buffer) {
         this(endPoint, executor, bufferPool, context);
      }

      public void onUpgradeTo(ByteBuffer buffer) {
         this.buffer = buffer == null ? BufferUtil.EMPTY_BUFFER : buffer;
      }

      public void onOpen() {
         super.onOpen();
         final int remaining = this.buffer.remaining();
         this.write(this.getConnection().getEndPoint(), this.buffer, new Callback() {
            public void succeeded() {
               if (ProxyConnection.LOG.isDebugEnabled()) {
                  ProxyConnection.LOG.debug("{} wrote initial {} bytes to server", DownstreamConnection.this, remaining);
               }

               DownstreamConnection.this.fillInterested();
            }

            public void failed(Throwable x) {
               if (ProxyConnection.LOG.isDebugEnabled()) {
                  ProxyConnection.LOG.debug(this + " failed to write initial " + remaining + " bytes to server", x);
               }

               DownstreamConnection.this.close();
               DownstreamConnection.this.getConnection().close();
            }
         });
      }

      protected int read(EndPoint endPoint, ByteBuffer buffer) throws IOException {
         return ConnectHandler.this.read(endPoint, buffer, this.getContext());
      }

      protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback) {
         ConnectHandler.this.write(endPoint, buffer, callback, this.getContext());
      }
   }
}
