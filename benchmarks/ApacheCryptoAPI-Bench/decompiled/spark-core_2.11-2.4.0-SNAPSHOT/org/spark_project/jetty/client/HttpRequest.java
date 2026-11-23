package org.spark_project.jetty.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.client.util.FutureResponseListener;
import org.spark_project.jetty.client.util.PathContentProvider;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.Fields;

public class HttpRequest implements Request {
   private static final URI NULL_URI = URI.create("null:0");
   private final HttpFields headers = new HttpFields();
   private final Fields params = new Fields(true);
   private final List<Response.ResponseListener> responseListeners = new ArrayList();
   private final AtomicReference<Throwable> aborted = new AtomicReference();
   private final HttpClient client;
   private final HttpConversation conversation;
   private final String host;
   private final int port;
   private URI uri;
   private String scheme;
   private String path;
   private String query;
   private String method;
   private HttpVersion version;
   private long idleTimeout;
   private long timeout;
   private ContentProvider content;
   private boolean followRedirects;
   private List<HttpCookie> cookies;
   private Map<String, Object> attributes;
   private List<Request.RequestListener> requestListeners;
   private BiFunction<Request, Request, Response.CompleteListener> pushListener;

   protected HttpRequest(HttpClient client, HttpConversation conversation, URI uri) {
      this.method = HttpMethod.GET.asString();
      this.version = HttpVersion.HTTP_1_1;
      this.client = client;
      this.conversation = conversation;
      this.scheme = uri.getScheme();
      this.host = client.normalizeHost(uri.getHost());
      this.port = HttpClient.normalizePort(this.scheme, uri.getPort());
      this.path = uri.getRawPath();
      this.query = uri.getRawQuery();
      this.extractParams(this.query);
      this.followRedirects(client.isFollowRedirects());
      this.idleTimeout = client.getIdleTimeout();
      HttpField acceptEncodingField = client.getAcceptEncodingField();
      if (acceptEncodingField != null) {
         this.headers.put(acceptEncodingField);
      }

      HttpField userAgentField = client.getUserAgentField();
      if (userAgentField != null) {
         this.headers.put(userAgentField);
      }

   }

   protected HttpConversation getConversation() {
      return this.conversation;
   }

   public String getScheme() {
      return this.scheme;
   }

   public Request scheme(String scheme) {
      this.scheme = scheme;
      this.uri = null;
      return this;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getMethod() {
      return this.method;
   }

   public Request method(HttpMethod method) {
      return this.method(method.asString());
   }

   public Request method(String method) {
      this.method = ((String)Objects.requireNonNull(method)).toUpperCase(Locale.ENGLISH);
      return this;
   }

   public String getPath() {
      return this.path;
   }

   public Request path(String path) {
      URI uri = this.newURI(path);
      if (uri == null) {
         this.path = path;
         this.query = null;
      } else {
         String rawPath = uri.getRawPath();
         if (rawPath == null) {
            rawPath = "";
         }

         this.path = rawPath;
         String query = uri.getRawQuery();
         if (query != null) {
            this.query = query;
            this.params.clear();
            this.extractParams(query);
         }

         if (uri.isAbsolute()) {
            this.path = this.buildURI(false).toString();
         }
      }

      this.uri = null;
      return this;
   }

   public String getQuery() {
      return this.query;
   }

   public URI getURI() {
      if (this.uri == null) {
         this.uri = this.buildURI(true);
      }

      return this.uri == NULL_URI ? null : this.uri;
   }

   public HttpVersion getVersion() {
      return this.version;
   }

   public Request version(HttpVersion version) {
      this.version = (HttpVersion)Objects.requireNonNull(version);
      return this;
   }

   public Request param(String name, String value) {
      return this.param(name, value, false);
   }

   private Request param(String name, String value, boolean fromQuery) {
      this.params.add(name, value);
      if (!fromQuery) {
         if (this.query != null) {
            this.query = this.query + "&" + this.urlEncode(name) + "=" + this.urlEncode(value);
         } else {
            this.query = this.buildQuery();
         }

         this.uri = null;
      }

      return this;
   }

   public Fields getParams() {
      return new Fields(this.params, true);
   }

   public String getAgent() {
      return this.headers.get(HttpHeader.USER_AGENT);
   }

   public Request agent(String agent) {
      this.headers.put(HttpHeader.USER_AGENT, agent);
      return this;
   }

   public Request accept(String... accepts) {
      StringBuilder result = new StringBuilder();

      for(String accept : accepts) {
         if (result.length() > 0) {
            result.append(", ");
         }

         result.append(accept);
      }

      if (result.length() > 0) {
         this.headers.put(HttpHeader.ACCEPT, result.toString());
      }

      return this;
   }

   public Request header(String name, String value) {
      if (value == null) {
         this.headers.remove(name);
      } else {
         this.headers.add(name, value);
      }

      return this;
   }

   public Request header(HttpHeader header, String value) {
      if (value == null) {
         this.headers.remove(header);
      } else {
         this.headers.add(header, value);
      }

      return this;
   }

   public List<HttpCookie> getCookies() {
      return this.cookies != null ? this.cookies : Collections.emptyList();
   }

   public Request cookie(HttpCookie cookie) {
      if (this.cookies == null) {
         this.cookies = new ArrayList();
      }

      this.cookies.add(cookie);
      return this;
   }

   public Request attribute(String name, Object value) {
      if (this.attributes == null) {
         this.attributes = new HashMap(4);
      }

      this.attributes.put(name, value);
      return this;
   }

   public Map<String, Object> getAttributes() {
      return this.attributes != null ? this.attributes : Collections.emptyMap();
   }

   public HttpFields getHeaders() {
      return this.headers;
   }

   public <T extends Request.RequestListener> List<T> getRequestListeners(Class<T> type) {
      if (type != null && this.requestListeners != null) {
         ArrayList<T> result = new ArrayList();

         for(Request.RequestListener listener : this.requestListeners) {
            if (type.isInstance(listener)) {
               result.add(listener);
            }
         }

         return result;
      } else {
         return this.requestListeners != null ? this.requestListeners : Collections.emptyList();
      }
   }

   public Request listener(Request.Listener listener) {
      return this.requestListener(listener);
   }

   public Request onRequestQueued(final Request.QueuedListener listener) {
      return this.requestListener(new Request.QueuedListener() {
         public void onQueued(Request request) {
            listener.onQueued(request);
         }
      });
   }

   public Request onRequestBegin(final Request.BeginListener listener) {
      return this.requestListener(new Request.BeginListener() {
         public void onBegin(Request request) {
            listener.onBegin(request);
         }
      });
   }

   public Request onRequestHeaders(final Request.HeadersListener listener) {
      return this.requestListener(new Request.HeadersListener() {
         public void onHeaders(Request request) {
            listener.onHeaders(request);
         }
      });
   }

   public Request onRequestCommit(final Request.CommitListener listener) {
      return this.requestListener(new Request.CommitListener() {
         public void onCommit(Request request) {
            listener.onCommit(request);
         }
      });
   }

   public Request onRequestContent(final Request.ContentListener listener) {
      return this.requestListener(new Request.ContentListener() {
         public void onContent(Request request, ByteBuffer content) {
            listener.onContent(request, content);
         }
      });
   }

   public Request onRequestSuccess(final Request.SuccessListener listener) {
      return this.requestListener(new Request.SuccessListener() {
         public void onSuccess(Request request) {
            listener.onSuccess(request);
         }
      });
   }

   public Request onRequestFailure(final Request.FailureListener listener) {
      return this.requestListener(new Request.FailureListener() {
         public void onFailure(Request request, Throwable failure) {
            listener.onFailure(request, failure);
         }
      });
   }

   private Request requestListener(Request.RequestListener listener) {
      if (this.requestListeners == null) {
         this.requestListeners = new ArrayList();
      }

      this.requestListeners.add(listener);
      return this;
   }

   public Request onResponseBegin(final Response.BeginListener listener) {
      this.responseListeners.add(new Response.BeginListener() {
         public void onBegin(Response response) {
            listener.onBegin(response);
         }
      });
      return this;
   }

   public Request onResponseHeader(final Response.HeaderListener listener) {
      this.responseListeners.add(new Response.HeaderListener() {
         public boolean onHeader(Response response, HttpField field) {
            return listener.onHeader(response, field);
         }
      });
      return this;
   }

   public Request onResponseHeaders(final Response.HeadersListener listener) {
      this.responseListeners.add(new Response.HeadersListener() {
         public void onHeaders(Response response) {
            listener.onHeaders(response);
         }
      });
      return this;
   }

   public Request onResponseContent(final Response.ContentListener listener) {
      this.responseListeners.add(new Response.AsyncContentListener() {
         public void onContent(Response response, ByteBuffer content, Callback callback) {
            try {
               listener.onContent(response, content);
               callback.succeeded();
            } catch (Throwable x) {
               callback.failed(x);
            }

         }
      });
      return this;
   }

   public Request onResponseContentAsync(final Response.AsyncContentListener listener) {
      this.responseListeners.add(new Response.AsyncContentListener() {
         public void onContent(Response response, ByteBuffer content, Callback callback) {
            listener.onContent(response, content, callback);
         }
      });
      return this;
   }

   public Request onResponseSuccess(final Response.SuccessListener listener) {
      this.responseListeners.add(new Response.SuccessListener() {
         public void onSuccess(Response response) {
            listener.onSuccess(response);
         }
      });
      return this;
   }

   public Request onResponseFailure(final Response.FailureListener listener) {
      this.responseListeners.add(new Response.FailureListener() {
         public void onFailure(Response response, Throwable failure) {
            listener.onFailure(response, failure);
         }
      });
      return this;
   }

   public Request onComplete(final Response.CompleteListener listener) {
      this.responseListeners.add(new Response.CompleteListener() {
         public void onComplete(Result result) {
            listener.onComplete(result);
         }
      });
      return this;
   }

   public Request pushListener(BiFunction<Request, Request, Response.CompleteListener> listener) {
      this.pushListener = listener;
      return this;
   }

   public ContentProvider getContent() {
      return this.content;
   }

   public Request content(ContentProvider content) {
      return this.content(content, (String)null);
   }

   public Request content(ContentProvider content, String contentType) {
      if (contentType != null) {
         this.header(HttpHeader.CONTENT_TYPE, contentType);
      }

      this.content = content;
      return this;
   }

   public Request file(Path file) throws IOException {
      return this.file(file, "application/octet-stream");
   }

   public Request file(Path file, String contentType) throws IOException {
      return this.content(new PathContentProvider(contentType, file));
   }

   public boolean isFollowRedirects() {
      return this.followRedirects;
   }

   public Request followRedirects(boolean follow) {
      this.followRedirects = follow;
      return this;
   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public Request idleTimeout(long timeout, TimeUnit unit) {
      this.idleTimeout = unit.toMillis(timeout);
      return this;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public Request timeout(long timeout, TimeUnit unit) {
      this.timeout = unit.toMillis(timeout);
      return this;
   }

   public ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException {
      FutureResponseListener listener = new FutureResponseListener(this);
      this.send(this, listener);

      try {
         long timeout = this.getTimeout();
         return timeout <= 0L ? listener.get() : listener.get(timeout, TimeUnit.MILLISECONDS);
      } catch (Throwable x) {
         this.abort(x);
         throw x;
      }
   }

   public void send(Response.CompleteListener listener) {
      TimeoutCompleteListener timeoutListener = null;

      try {
         if (this.getTimeout() > 0L) {
            timeoutListener = new TimeoutCompleteListener(this);
            timeoutListener.schedule(this.client.getScheduler());
            this.responseListeners.add(timeoutListener);
         }

         this.send(this, listener);
      } catch (Throwable x) {
         if (timeoutListener != null) {
            timeoutListener.cancel();
         }

         throw x;
      }
   }

   private void send(HttpRequest request, Response.CompleteListener listener) {
      if (listener != null) {
         this.responseListeners.add(listener);
      }

      this.client.send(request, this.responseListeners);
   }

   protected List<Response.ResponseListener> getResponseListeners() {
      return this.responseListeners;
   }

   public BiFunction<Request, Request, Response.CompleteListener> getPushListener() {
      return this.pushListener;
   }

   public boolean abort(Throwable cause) {
      if (this.aborted.compareAndSet((Object)null, Objects.requireNonNull(cause))) {
         if (this.content instanceof Callback) {
            ((Callback)this.content).failed(cause);
         }

         return this.conversation.abort(cause);
      } else {
         return false;
      }
   }

   public Throwable getAbortCause() {
      return (Throwable)this.aborted.get();
   }

   private String buildQuery() {
      StringBuilder result = new StringBuilder();
      Iterator<Fields.Field> iterator = this.params.iterator();

      while(iterator.hasNext()) {
         Fields.Field field = (Fields.Field)iterator.next();
         List<String> values = field.getValues();

         for(int i = 0; i < values.size(); ++i) {
            if (i > 0) {
               result.append("&");
            }

            result.append(field.getName()).append("=");
            result.append(this.urlEncode((String)values.get(i)));
         }

         if (iterator.hasNext()) {
            result.append("&");
         }
      }

      return result.toString();
   }

   private String urlEncode(String value) {
      if (value == null) {
         return "";
      } else {
         String encoding = "utf-8";

         try {
            return URLEncoder.encode(value, encoding);
         } catch (UnsupportedEncodingException var4) {
            throw new UnsupportedCharsetException(encoding);
         }
      }
   }

   private void extractParams(String query) {
      if (query != null) {
         for(String nameValue : query.split("&")) {
            String[] parts = nameValue.split("=");
            if (parts.length > 0) {
               String name = this.urlDecode(parts[0]);
               if (name.trim().length() != 0) {
                  this.param(name, parts.length < 2 ? "" : this.urlDecode(parts[1]), true);
               }
            }
         }
      }

   }

   private String urlDecode(String value) {
      String charset = "utf-8";

      try {
         return URLDecoder.decode(value, charset);
      } catch (UnsupportedEncodingException var4) {
         throw new UnsupportedCharsetException(charset);
      }
   }

   private URI buildURI(boolean withQuery) {
      String path = this.getPath();
      String query = this.getQuery();
      if (query != null && withQuery) {
         path = path + "?" + query;
      }

      URI result = this.newURI(path);
      if (result == null) {
         return NULL_URI;
      } else {
         if (!result.isAbsolute()) {
            result = URI.create((new Origin(this.getScheme(), this.getHost(), this.getPort())).asString() + path);
         }

         return result;
      }
   }

   private URI newURI(String uri) {
      try {
         if ("*".equals(uri)) {
            return null;
         } else {
            URI result = new URI(uri);
            return result.isOpaque() ? null : result;
         }
      } catch (URISyntaxException var3) {
         return null;
      }
   }

   public String toString() {
      return String.format("%s[%s %s %s]@%x", HttpRequest.class.getSimpleName(), this.getMethod(), this.getPath(), this.getVersion(), this.hashCode());
   }
}
