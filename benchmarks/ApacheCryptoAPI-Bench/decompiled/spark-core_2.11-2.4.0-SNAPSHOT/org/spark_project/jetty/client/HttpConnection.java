package org.spark_project.jetty.client;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.spark_project.jetty.client.api.Authentication;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class HttpConnection implements Connection {
   private static final Logger LOG = Log.getLogger(HttpConnection.class);
   private static final HttpField CHUNKED_FIELD;
   private final HttpDestination destination;
   private int idleTimeoutGuard;
   private long idleTimeoutStamp;

   protected HttpConnection(HttpDestination destination) {
      this.destination = destination;
      this.idleTimeoutStamp = System.nanoTime();
   }

   public HttpClient getHttpClient() {
      return this.destination.getHttpClient();
   }

   public HttpDestination getHttpDestination() {
      return this.destination;
   }

   public void send(Request request, Response.CompleteListener listener) {
      HttpRequest httpRequest = (HttpRequest)request;
      ArrayList<Response.ResponseListener> listeners = new ArrayList(httpRequest.getResponseListeners());
      if (httpRequest.getTimeout() > 0L) {
         TimeoutCompleteListener timeoutListener = new TimeoutCompleteListener(httpRequest);
         timeoutListener.schedule(this.getHttpClient().getScheduler());
         listeners.add(timeoutListener);
      }

      if (listener != null) {
         listeners.add(listener);
      }

      HttpExchange exchange = new HttpExchange(this.getHttpDestination(), httpRequest, listeners);
      SendFailure result = this.send(exchange);
      if (result != null) {
         httpRequest.abort(result.failure);
      }

   }

   protected abstract SendFailure send(HttpExchange var1);

   protected void normalizeRequest(Request request) {
      HttpVersion version = request.getVersion();
      HttpFields headers = request.getHeaders();
      ContentProvider content = request.getContent();
      ProxyConfiguration.Proxy proxy = this.destination.getProxy();
      String path = request.getPath();
      if (path.trim().length() == 0) {
         path = "/";
         request.path(path);
      }

      URI uri = request.getURI();
      if (proxy instanceof HttpProxy && !HttpScheme.HTTPS.is(request.getScheme()) && uri != null) {
         path = uri.toString();
         request.path(path);
      }

      if (version.getVersion() <= 11 && !headers.containsKey(HttpHeader.HOST.asString())) {
         headers.put(this.getHttpDestination().getHostField());
      }

      if (content != null) {
         if (!headers.containsKey(HttpHeader.CONTENT_TYPE.asString())) {
            String contentType = null;
            if (content instanceof ContentProvider.Typed) {
               contentType = ((ContentProvider.Typed)content).getContentType();
            }

            if (contentType != null) {
               headers.put(HttpHeader.CONTENT_TYPE, contentType);
            } else {
               headers.put(HttpHeader.CONTENT_TYPE, "application/octet-stream");
            }
         }

         long contentLength = content.getLength();
         if (contentLength >= 0L && !headers.containsKey(HttpHeader.CONTENT_LENGTH.asString())) {
            headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(contentLength));
         }
      }

      CookieStore cookieStore = this.getHttpClient().getCookieStore();
      if (cookieStore != null) {
         StringBuilder cookies = null;
         if (uri != null) {
            cookies = this.convertCookies(cookieStore.get(uri), (StringBuilder)null);
         }

         cookies = this.convertCookies(request.getCookies(), cookies);
         if (cookies != null) {
            request.header(HttpHeader.COOKIE.asString(), cookies.toString());
         }
      }

      this.applyAuthentication(request, proxy != null ? proxy.getURI() : null);
      this.applyAuthentication(request, uri);
   }

   private StringBuilder convertCookies(List<HttpCookie> cookies, StringBuilder builder) {
      for(int i = 0; i < cookies.size(); ++i) {
         if (builder == null) {
            builder = new StringBuilder();
         }

         if (builder.length() > 0) {
            builder.append("; ");
         }

         HttpCookie cookie = (HttpCookie)cookies.get(i);
         builder.append(cookie.getName()).append("=").append(cookie.getValue());
      }

      return builder;
   }

   private void applyAuthentication(Request request, URI uri) {
      if (uri != null) {
         Authentication.Result result = this.getHttpClient().getAuthenticationStore().findAuthenticationResult(uri);
         if (result != null) {
            result.apply(request);
         }
      }

   }

   protected SendFailure send(HttpChannel channel, HttpExchange exchange) {
      boolean send;
      synchronized(this) {
         send = this.idleTimeoutGuard >= 0;
         if (send) {
            ++this.idleTimeoutGuard;
         }
      }

      if (send) {
         HttpRequest request = exchange.getRequest();
         SendFailure result;
         if (channel.associate(exchange)) {
            channel.send();
            result = null;
         } else {
            channel.release();
            result = new SendFailure(new HttpRequestException("Could not associate request to connection", request), false);
         }

         synchronized(this) {
            --this.idleTimeoutGuard;
            this.idleTimeoutStamp = System.nanoTime();
            return result;
         }
      } else {
         return new SendFailure(new TimeoutException(), true);
      }
   }

   public boolean onIdleTimeout(long idleTimeout) {
      synchronized(this) {
         if (this.idleTimeoutGuard == 0) {
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.idleTimeoutStamp);
            boolean idle = elapsed > idleTimeout / 2L;
            if (idle) {
               this.idleTimeoutGuard = -1;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Idle timeout {}/{}ms - {}", elapsed, idleTimeout, this);
            }

            return idle;
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Idle timeout skipped - {}", this);
            }

            return false;
         }
      }
   }

   public String toString() {
      return String.format("%s@%h", this.getClass().getSimpleName(), this);
   }

   static {
      CHUNKED_FIELD = new HttpField(HttpHeader.TRANSFER_ENCODING, HttpHeaderValue.CHUNKED);
   }
}
