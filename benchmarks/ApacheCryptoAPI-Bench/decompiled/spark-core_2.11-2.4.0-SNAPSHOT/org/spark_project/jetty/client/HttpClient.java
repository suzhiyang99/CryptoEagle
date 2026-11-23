package org.spark_project.jetty.client;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.spark_project.jetty.client.api.AuthenticationStore;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.http.HttpClientTransportOverHTTP;
import org.spark_project.jetty.client.util.FormContentProvider;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.ClientConnectionFactory;
import org.spark_project.jetty.io.MappedByteBufferPool;
import org.spark_project.jetty.io.ssl.SslClientConnectionFactory;
import org.spark_project.jetty.util.Fields;
import org.spark_project.jetty.util.Jetty;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.SocketAddressResolver;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.QueuedThreadPool;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;

@ManagedObject("The HTTP client")
public class HttpClient extends ContainerLifeCycle {
   private static final Logger LOG = Log.getLogger(HttpClient.class);
   private final ConcurrentMap<Origin, HttpDestination> destinations;
   private final ProtocolHandlers handlers;
   private final List<Request.Listener> requestListeners;
   private final AuthenticationStore authenticationStore;
   private final Set<ContentDecoder.Factory> decoderFactories;
   private final ProxyConfiguration proxyConfig;
   private final HttpClientTransport transport;
   private final SslContextFactory sslContextFactory;
   private volatile CookieManager cookieManager;
   private volatile CookieStore cookieStore;
   private volatile Executor executor;
   private volatile ByteBufferPool byteBufferPool;
   private volatile Scheduler scheduler;
   private volatile SocketAddressResolver resolver;
   private volatile HttpField agentField;
   private volatile boolean followRedirects;
   private volatile int maxConnectionsPerDestination;
   private volatile int maxRequestsQueuedPerDestination;
   private volatile int requestBufferSize;
   private volatile int responseBufferSize;
   private volatile int maxRedirects;
   private volatile SocketAddress bindAddress;
   private volatile long connectTimeout;
   private volatile long addressResolutionTimeout;
   private volatile long idleTimeout;
   private volatile boolean tcpNoDelay;
   private volatile boolean strictEventOrdering;
   private volatile HttpField encodingField;
   private volatile boolean removeIdleDestinations;
   private volatile boolean connectBlocking;

   public HttpClient() {
      this((SslContextFactory)null);
   }

   public HttpClient(SslContextFactory sslContextFactory) {
      this(new HttpClientTransportOverHTTP(), sslContextFactory);
   }

   public HttpClient(HttpClientTransport transport, SslContextFactory sslContextFactory) {
      this.destinations = new ConcurrentHashMap();
      this.handlers = new ProtocolHandlers();
      this.requestListeners = new ArrayList();
      this.authenticationStore = new HttpAuthenticationStore();
      this.decoderFactories = new ContentDecoderFactorySet();
      this.proxyConfig = new ProxyConfiguration();
      this.agentField = new HttpField(HttpHeader.USER_AGENT, "Jetty/" + Jetty.VERSION);
      this.followRedirects = true;
      this.maxConnectionsPerDestination = 64;
      this.maxRequestsQueuedPerDestination = 1024;
      this.requestBufferSize = 4096;
      this.responseBufferSize = 16384;
      this.maxRedirects = 8;
      this.connectTimeout = 15000L;
      this.addressResolutionTimeout = 15000L;
      this.tcpNoDelay = true;
      this.strictEventOrdering = false;
      this.removeIdleDestinations = false;
      this.connectBlocking = false;
      this.transport = transport;
      this.sslContextFactory = sslContextFactory;
   }

   public HttpClientTransport getTransport() {
      return this.transport;
   }

   public SslContextFactory getSslContextFactory() {
      return this.sslContextFactory;
   }

   protected void doStart() throws Exception {
      if (this.sslContextFactory != null) {
         this.addBean(this.sslContextFactory);
      }

      String name = HttpClient.class.getSimpleName() + "@" + this.hashCode();
      if (this.executor == null) {
         QueuedThreadPool threadPool = new QueuedThreadPool();
         threadPool.setName(name);
         this.executor = threadPool;
      }

      this.addBean(this.executor);
      if (this.byteBufferPool == null) {
         this.byteBufferPool = new MappedByteBufferPool();
      }

      this.addBean(this.byteBufferPool);
      if (this.scheduler == null) {
         this.scheduler = new ScheduledExecutorScheduler(name + "-scheduler", false);
      }

      this.addBean(this.scheduler);
      this.transport.setHttpClient(this);
      this.addBean(this.transport);
      if (this.resolver == null) {
         this.resolver = new SocketAddressResolver.Async(this.executor, this.scheduler, this.getAddressResolutionTimeout());
      }

      this.addBean(this.resolver);
      this.handlers.put(new ContinueProtocolHandler());
      this.handlers.put(new RedirectProtocolHandler(this));
      this.handlers.put(new WWWAuthenticationProtocolHandler(this));
      this.handlers.put(new ProxyAuthenticationProtocolHandler(this));
      this.decoderFactories.add(new GZIPContentDecoder.Factory());
      this.cookieManager = this.newCookieManager();
      this.cookieStore = this.cookieManager.getCookieStore();
      super.doStart();
   }

   private CookieManager newCookieManager() {
      return new CookieManager(this.getCookieStore(), CookiePolicy.ACCEPT_ALL);
   }

   protected void doStop() throws Exception {
      this.decoderFactories.clear();
      this.handlers.clear();

      for(HttpDestination destination : this.destinations.values()) {
         destination.close();
      }

      this.destinations.clear();
      this.requestListeners.clear();
      this.authenticationStore.clearAuthentications();
      this.authenticationStore.clearAuthenticationResults();
      super.doStop();
   }

   public List<Request.Listener> getRequestListeners() {
      return this.requestListeners;
   }

   public CookieStore getCookieStore() {
      return this.cookieStore;
   }

   public void setCookieStore(CookieStore cookieStore) {
      this.cookieStore = (CookieStore)Objects.requireNonNull(cookieStore);
      this.cookieManager = this.newCookieManager();
   }

   CookieManager getCookieManager() {
      return this.cookieManager;
   }

   public AuthenticationStore getAuthenticationStore() {
      return this.authenticationStore;
   }

   public Set<ContentDecoder.Factory> getContentDecoderFactories() {
      return this.decoderFactories;
   }

   public ContentResponse GET(String uri) throws InterruptedException, ExecutionException, TimeoutException {
      return this.GET(URI.create(uri));
   }

   public ContentResponse GET(URI uri) throws InterruptedException, ExecutionException, TimeoutException {
      return this.newRequest(uri).send();
   }

   public ContentResponse FORM(String uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
      return this.FORM(URI.create(uri), fields);
   }

   public ContentResponse FORM(URI uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
      return this.POST(uri).content(new FormContentProvider(fields)).send();
   }

   public Request POST(String uri) {
      return this.POST(URI.create(uri));
   }

   public Request POST(URI uri) {
      return this.newRequest(uri).method(HttpMethod.POST);
   }

   public Request newRequest(String host, int port) {
      return this.newRequest((new Origin("http", host, port)).asString());
   }

   public Request newRequest(String uri) {
      return this.newRequest(URI.create(uri));
   }

   public Request newRequest(URI uri) {
      return this.newHttpRequest(this.newConversation(), uri);
   }

   protected Request copyRequest(HttpRequest oldRequest, URI newURI) {
      Request newRequest = this.newHttpRequest(oldRequest.getConversation(), newURI);
      newRequest.method(oldRequest.getMethod()).version(oldRequest.getVersion()).content(oldRequest.getContent()).idleTimeout(oldRequest.getIdleTimeout(), TimeUnit.MILLISECONDS).timeout(oldRequest.getTimeout(), TimeUnit.MILLISECONDS).followRedirects(oldRequest.isFollowRedirects());

      for(HttpField field : oldRequest.getHeaders()) {
         HttpHeader header = field.getHeader();
         if (HttpHeader.HOST != header && HttpHeader.EXPECT != header && HttpHeader.COOKIE != header && HttpHeader.AUTHORIZATION != header && HttpHeader.PROXY_AUTHORIZATION != header) {
            String name = field.getName();
            String value = field.getValue();
            if (!newRequest.getHeaders().contains(name, value)) {
               newRequest.header(name, value);
            }
         }
      }

      return newRequest;
   }

   protected HttpRequest newHttpRequest(HttpConversation conversation, URI uri) {
      return new HttpRequest(this, conversation, this.checkHost(uri));
   }

   private URI checkHost(URI uri) {
      if (uri.getHost() == null) {
         throw new IllegalArgumentException(String.format("Invalid URI host: null (authority: %s)", uri.getRawAuthority()));
      } else {
         return uri;
      }
   }

   public Destination getDestination(String scheme, String host, int port) {
      return this.destinationFor(scheme, host, port);
   }

   protected HttpDestination destinationFor(String scheme, String host, int port) {
      if (!HttpScheme.HTTP.is(scheme) && !HttpScheme.HTTPS.is(scheme)) {
         throw new IllegalArgumentException("Invalid protocol " + scheme);
      } else {
         scheme = scheme.toLowerCase(Locale.ENGLISH);
         host = host.toLowerCase(Locale.ENGLISH);
         port = normalizePort(scheme, port);
         Origin origin = new Origin(scheme, host, port);
         HttpDestination destination = (HttpDestination)this.destinations.get(origin);
         if (destination == null) {
            destination = this.transport.newHttpDestination(origin);
            this.addManaged(destination);
            HttpDestination existing = (HttpDestination)this.destinations.putIfAbsent(origin, destination);
            if (existing != null) {
               this.removeBean(destination);
               destination = existing;
            } else if (LOG.isDebugEnabled()) {
               LOG.debug("Created {}", destination);
            }
         }

         return destination;
      }
   }

   protected boolean removeDestination(HttpDestination destination) {
      this.removeBean(destination);
      return this.destinations.remove(destination.getOrigin()) != null;
   }

   public List<Destination> getDestinations() {
      return new ArrayList(this.destinations.values());
   }

   protected void send(HttpRequest request, List<Response.ResponseListener> listeners) {
      HttpDestination destination = this.destinationFor(request.getScheme(), request.getHost(), request.getPort());
      destination.send(request, listeners);
   }

   protected void newConnection(final HttpDestination destination, final Promise<Connection> promise) {
      Origin.Address address = destination.getConnectAddress();
      this.resolver.resolve(address.getHost(), address.getPort(), new Promise<List<InetSocketAddress>>() {
         public void succeeded(List<InetSocketAddress> socketAddresses) {
            Map<String, Object> context = new HashMap();
            context.put("client.connector", HttpClient.this);
            context.put("http.destination", destination);
            this.connect(socketAddresses, 0, context);
         }

         public void failed(Throwable x) {
            promise.failed(x);
         }

         private void connect(final List<InetSocketAddress> socketAddresses, final int index, final Map<String, Object> context) {
            context.put("http.connection.promise", new Promise.Wrapper<Connection>(promise) {
               public void failed(Throwable x) {
                  int nextIndex = index + 1;
                  if (nextIndex == socketAddresses.size()) {
                     super.failed(x);
                  } else {
                     connect(socketAddresses, nextIndex, context);
                  }

               }
            });
            HttpClient.this.transport.connect((InetSocketAddress)socketAddresses.get(index), context);
         }
      });
   }

   private HttpConversation newConversation() {
      return new HttpConversation();
   }

   public ProtocolHandlers getProtocolHandlers() {
      return this.handlers;
   }

   protected ProtocolHandler findProtocolHandler(Request request, Response response) {
      return this.handlers.find(request, response);
   }

   public ByteBufferPool getByteBufferPool() {
      return this.byteBufferPool;
   }

   public void setByteBufferPool(ByteBufferPool byteBufferPool) {
      this.byteBufferPool = byteBufferPool;
   }

   @ManagedAttribute("The timeout, in milliseconds, for connect() operations")
   public long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public long getAddressResolutionTimeout() {
      return this.addressResolutionTimeout;
   }

   public void setAddressResolutionTimeout(long addressResolutionTimeout) {
      this.addressResolutionTimeout = addressResolutionTimeout;
   }

   @ManagedAttribute("The timeout, in milliseconds, to close idle connections")
   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   public SocketAddress getBindAddress() {
      return this.bindAddress;
   }

   public void setBindAddress(SocketAddress bindAddress) {
      this.bindAddress = bindAddress;
   }

   public HttpField getUserAgentField() {
      return this.agentField;
   }

   public void setUserAgentField(HttpField agent) {
      if (agent.getHeader() != HttpHeader.USER_AGENT) {
         throw new IllegalArgumentException();
      } else {
         this.agentField = agent;
      }
   }

   @ManagedAttribute("Whether HTTP redirects are followed")
   public boolean isFollowRedirects() {
      return this.followRedirects;
   }

   public void setFollowRedirects(boolean follow) {
      this.followRedirects = follow;
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

   public SocketAddressResolver getSocketAddressResolver() {
      return this.resolver;
   }

   public void setSocketAddressResolver(SocketAddressResolver resolver) {
      this.resolver = resolver;
   }

   @ManagedAttribute("The max number of connections per each destination")
   public int getMaxConnectionsPerDestination() {
      return this.maxConnectionsPerDestination;
   }

   public void setMaxConnectionsPerDestination(int maxConnectionsPerDestination) {
      this.maxConnectionsPerDestination = maxConnectionsPerDestination;
   }

   @ManagedAttribute("The max number of requests queued per each destination")
   public int getMaxRequestsQueuedPerDestination() {
      return this.maxRequestsQueuedPerDestination;
   }

   public void setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination) {
      this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
   }

   @ManagedAttribute("The request buffer size")
   public int getRequestBufferSize() {
      return this.requestBufferSize;
   }

   public void setRequestBufferSize(int requestBufferSize) {
      this.requestBufferSize = requestBufferSize;
   }

   @ManagedAttribute("The response buffer size")
   public int getResponseBufferSize() {
      return this.responseBufferSize;
   }

   public void setResponseBufferSize(int responseBufferSize) {
      this.responseBufferSize = responseBufferSize;
   }

   public int getMaxRedirects() {
      return this.maxRedirects;
   }

   public void setMaxRedirects(int maxRedirects) {
      this.maxRedirects = maxRedirects;
   }

   @ManagedAttribute(
      value = "Whether the TCP_NODELAY option is enabled",
      name = "tcpNoDelay"
   )
   public boolean isTCPNoDelay() {
      return this.tcpNoDelay;
   }

   public void setTCPNoDelay(boolean tcpNoDelay) {
      this.tcpNoDelay = tcpNoDelay;
   }

   /** @deprecated */
   @Deprecated
   public boolean isDispatchIO() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public void setDispatchIO(boolean dispatchIO) {
   }

   @ManagedAttribute("Whether request/response events must be strictly ordered")
   public boolean isStrictEventOrdering() {
      return this.strictEventOrdering;
   }

   public void setStrictEventOrdering(boolean strictEventOrdering) {
      this.strictEventOrdering = strictEventOrdering;
   }

   @ManagedAttribute("Whether idle destinations are removed")
   public boolean isRemoveIdleDestinations() {
      return this.removeIdleDestinations;
   }

   public void setRemoveIdleDestinations(boolean removeIdleDestinations) {
      this.removeIdleDestinations = removeIdleDestinations;
   }

   @ManagedAttribute("Whether the connect() operation is blocking")
   public boolean isConnectBlocking() {
      return this.connectBlocking;
   }

   public void setConnectBlocking(boolean connectBlocking) {
      this.connectBlocking = connectBlocking;
   }

   public ProxyConfiguration getProxyConfiguration() {
      return this.proxyConfig;
   }

   protected HttpField getAcceptEncodingField() {
      return this.encodingField;
   }

   protected String normalizeHost(String host) {
      return host != null && host.matches("\\[.*\\]") ? host.substring(1, host.length() - 1) : host;
   }

   public static int normalizePort(String scheme, int port) {
      return port > 0 ? port : (HttpScheme.HTTPS.is(scheme) ? 443 : 80);
   }

   public boolean isDefaultPort(String scheme, int port) {
      return HttpScheme.HTTPS.is(scheme) ? port == 443 : port == 80;
   }

   protected ClientConnectionFactory newSslClientConnectionFactory(ClientConnectionFactory connectionFactory) {
      return new SslClientConnectionFactory(this.getSslContextFactory(), this.getByteBufferPool(), this.getExecutor(), connectionFactory);
   }

   private class ContentDecoderFactorySet implements Set<ContentDecoder.Factory> {
      private final Set<ContentDecoder.Factory> set;

      private ContentDecoderFactorySet() {
         this.set = new HashSet();
      }

      public boolean add(ContentDecoder.Factory e) {
         boolean result = this.set.add(e);
         this.invalidate();
         return result;
      }

      public boolean addAll(Collection<? extends ContentDecoder.Factory> c) {
         boolean result = this.set.addAll(c);
         this.invalidate();
         return result;
      }

      public boolean remove(Object o) {
         boolean result = this.set.remove(o);
         this.invalidate();
         return result;
      }

      public boolean removeAll(Collection<?> c) {
         boolean result = this.set.removeAll(c);
         this.invalidate();
         return result;
      }

      public boolean retainAll(Collection<?> c) {
         boolean result = this.set.retainAll(c);
         this.invalidate();
         return result;
      }

      public void clear() {
         this.set.clear();
         this.invalidate();
      }

      public int size() {
         return this.set.size();
      }

      public boolean isEmpty() {
         return this.set.isEmpty();
      }

      public boolean contains(Object o) {
         return this.set.contains(o);
      }

      public boolean containsAll(Collection<?> c) {
         return this.set.containsAll(c);
      }

      public Iterator<ContentDecoder.Factory> iterator() {
         final Iterator<ContentDecoder.Factory> iterator = this.set.iterator();
         return new Iterator<ContentDecoder.Factory>() {
            public boolean hasNext() {
               return iterator.hasNext();
            }

            public ContentDecoder.Factory next() {
               return (ContentDecoder.Factory)iterator.next();
            }

            public void remove() {
               iterator.remove();
               ContentDecoderFactorySet.this.invalidate();
            }
         };
      }

      public Object[] toArray() {
         return this.set.toArray();
      }

      public <T> T[] toArray(T[] a) {
         return (T[])this.set.toArray(a);
      }

      private void invalidate() {
         if (this.set.isEmpty()) {
            HttpClient.this.encodingField = null;
         } else {
            StringBuilder value = new StringBuilder();
            Iterator<ContentDecoder.Factory> iterator = this.set.iterator();

            while(iterator.hasNext()) {
               ContentDecoder.Factory decoderFactory = (ContentDecoder.Factory)iterator.next();
               value.append(decoderFactory.getEncoding());
               if (iterator.hasNext()) {
                  value.append(",");
               }
            }

            HttpClient.this.encodingField = new HttpField(HttpHeader.ACCEPT_ENCODING, value.toString());
         }

      }
   }
}
