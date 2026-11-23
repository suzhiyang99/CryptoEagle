package org.spark_project.jetty.proxy;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.client.AsyncContentProvider;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.client.util.DeferredContentProvider;
import org.spark_project.jetty.client.util.InputStreamContentProvider;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;

public class ProxyServlet extends AbstractProxyServlet {
   private static final String CONTINUE_ACTION_ATTRIBUTE = ProxyServlet.class.getName() + ".continueAction";

   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      int requestId = this.getRequestId(request);
      String rewrittenTarget = this.rewriteTarget(request);
      if (this._log.isDebugEnabled()) {
         StringBuffer uri = request.getRequestURL();
         if (request.getQueryString() != null) {
            uri.append("?").append(request.getQueryString());
         }

         if (this._log.isDebugEnabled()) {
            this._log.debug("{} rewriting: {} -> {}", requestId, uri, rewrittenTarget);
         }
      }

      if (rewrittenTarget == null) {
         this.onProxyRewriteFailed(request, response);
      } else {
         Request proxyRequest = this.getHttpClient().newRequest(rewrittenTarget).method(request.getMethod()).version(HttpVersion.fromString(request.getProtocol()));
         this.copyRequestHeaders(request, proxyRequest);
         this.addProxyHeaders(request, proxyRequest);
         AsyncContext asyncContext = request.startAsync();
         asyncContext.setTimeout(0L);
         proxyRequest.timeout(this.getTimeout(), TimeUnit.MILLISECONDS);
         if (this.hasContent(request)) {
            if (this.expects100Continue(request)) {
               DeferredContentProvider deferred = new DeferredContentProvider(new ByteBuffer[0]);
               proxyRequest.content(deferred);
               proxyRequest.attribute("org.spark_project.jetty.proxy.clientRequest", request);
               proxyRequest.attribute(CONTINUE_ACTION_ATTRIBUTE, (Runnable)() -> {
                  try {
                     ContentProvider provider = this.proxyRequestContent(request, response, proxyRequest);
                     (new DelegatingContentProvider(request, proxyRequest, response, provider, deferred)).iterate();
                  } catch (Throwable failure) {
                     this.onClientRequestFailure(request, proxyRequest, response, failure);
                  }

               });
            } else {
               proxyRequest.content(this.proxyRequestContent(request, response, proxyRequest));
            }
         }

         this.sendProxyRequest(request, response, proxyRequest);
      }
   }

   protected ContentProvider proxyRequestContent(HttpServletRequest request, HttpServletResponse response, Request proxyRequest) throws IOException {
      return new ProxyInputStreamContentProvider(request, response, proxyRequest, request.getInputStream());
   }

   protected Response.Listener newProxyResponseListener(HttpServletRequest request, HttpServletResponse response) {
      return new ProxyResponseListener(request, response);
   }

   protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length, Callback callback) {
      try {
         if (this._log.isDebugEnabled()) {
            this._log.debug("{} proxying content to downstream: {} bytes", this.getRequestId(request), length);
         }

         response.getOutputStream().write(buffer, offset, length);
         callback.succeeded();
      } catch (Throwable x) {
         callback.failed(x);
      }

   }

   protected void onContinue(HttpServletRequest clientRequest, Request proxyRequest) {
      super.onContinue(clientRequest, proxyRequest);
      Runnable action = (Runnable)proxyRequest.getAttributes().get(CONTINUE_ACTION_ATTRIBUTE);
      Executor executor = this.getHttpClient().getExecutor();
      executor.execute(action);
   }

   public static class Transparent extends ProxyServlet {
      private final AbstractProxyServlet.TransparentDelegate delegate = new AbstractProxyServlet.TransparentDelegate(this);

      public void init(ServletConfig config) throws ServletException {
         super.init(config);
         this.delegate.init(config);
      }

      protected String rewriteTarget(HttpServletRequest request) {
         return this.delegate.rewriteTarget(request);
      }
   }

   protected class ProxyResponseListener extends Response.Listener.Adapter {
      private final HttpServletRequest request;
      private final HttpServletResponse response;

      protected ProxyResponseListener(HttpServletRequest request, HttpServletResponse response) {
         this.request = request;
         this.response = response;
      }

      public void onBegin(Response proxyResponse) {
         this.response.setStatus(proxyResponse.getStatus());
      }

      public void onHeaders(Response proxyResponse) {
         ProxyServlet.this.onServerResponseHeaders(this.request, this.response, proxyResponse);
      }

      public void onContent(final Response proxyResponse, ByteBuffer content, Callback callback) {
         int length = content.remaining();
         byte[] buffer;
         int offset;
         if (content.hasArray()) {
            buffer = content.array();
            offset = content.arrayOffset();
         } else {
            buffer = new byte[length];
            content.get(buffer);
            offset = 0;
         }

         ProxyServlet.this.onResponseContent(this.request, this.response, proxyResponse, buffer, offset, length, new Callback.Nested(callback) {
            public void failed(Throwable x) {
               super.failed(x);
               proxyResponse.abort(x);
            }
         });
      }

      public void onComplete(Result result) {
         if (result.isSucceeded()) {
            ProxyServlet.this.onProxyResponseSuccess(this.request, this.response, result.getResponse());
         } else {
            ProxyServlet.this.onProxyResponseFailure(this.request, this.response, result.getResponse(), result.getFailure());
         }

         if (ProxyServlet.this._log.isDebugEnabled()) {
            ProxyServlet.this._log.debug("{} proxying complete", (long)ProxyServlet.this.getRequestId(this.request));
         }

      }
   }

   protected class ProxyInputStreamContentProvider extends InputStreamContentProvider {
      private final HttpServletResponse response;
      private final Request proxyRequest;
      private final HttpServletRequest request;

      protected ProxyInputStreamContentProvider(HttpServletRequest request, HttpServletResponse response, Request proxyRequest, InputStream input) {
         super(input);
         this.request = request;
         this.response = response;
         this.proxyRequest = proxyRequest;
      }

      public long getLength() {
         return (long)this.request.getContentLength();
      }

      protected ByteBuffer onRead(byte[] buffer, int offset, int length) {
         if (ProxyServlet.this._log.isDebugEnabled()) {
            ProxyServlet.this._log.debug("{} proxying content to upstream: {} bytes", ProxyServlet.this.getRequestId(this.request), length);
         }

         return this.onRequestContent(this.request, this.proxyRequest, buffer, offset, length);
      }

      protected ByteBuffer onRequestContent(HttpServletRequest request, Request proxyRequest, byte[] buffer, int offset, int length) {
         return super.onRead(buffer, offset, length);
      }

      protected void onReadFailure(Throwable failure) {
         ProxyServlet.this.onClientRequestFailure(this.request, this.proxyRequest, this.response, failure);
      }
   }

   private class DelegatingContentProvider extends IteratingCallback implements AsyncContentProvider.Listener {
      private final HttpServletRequest clientRequest;
      private final Request proxyRequest;
      private final HttpServletResponse proxyResponse;
      private final Iterator<ByteBuffer> iterator;
      private final DeferredContentProvider deferred;

      private DelegatingContentProvider(HttpServletRequest clientRequest, Request proxyRequest, HttpServletResponse proxyResponse, ContentProvider provider, DeferredContentProvider deferred) {
         this.clientRequest = clientRequest;
         this.proxyRequest = proxyRequest;
         this.proxyResponse = proxyResponse;
         this.iterator = provider.iterator();
         this.deferred = deferred;
         if (provider instanceof AsyncContentProvider) {
            ((AsyncContentProvider)provider).setListener(this);
         }

      }

      protected IteratingCallback.Action process() throws Exception {
         if (!this.iterator.hasNext()) {
            return IteratingCallback.Action.SUCCEEDED;
         } else {
            ByteBuffer buffer = (ByteBuffer)this.iterator.next();
            if (buffer == null) {
               return IteratingCallback.Action.IDLE;
            } else {
               this.deferred.offer(buffer, this);
               return IteratingCallback.Action.SCHEDULED;
            }
         }
      }

      public void succeeded() {
         if (this.iterator instanceof Callback) {
            ((Callback)this.iterator).succeeded();
         }

         super.succeeded();
      }

      protected void onCompleteSuccess() {
         try {
            if (this.iterator instanceof Closeable) {
               ((Closeable)this.iterator).close();
            }

            this.deferred.close();
         } catch (Throwable x) {
            ProxyServlet.this._log.ignore(x);
         }

      }

      protected void onCompleteFailure(Throwable failure) {
         if (this.iterator instanceof Callback) {
            ((Callback)this.iterator).failed(failure);
         }

         ProxyServlet.this.onClientRequestFailure(this.clientRequest, this.proxyRequest, this.proxyResponse, failure);
      }

      public boolean isNonBlocking() {
         return true;
      }

      public void onContent() {
         this.iterate();
      }
   }
}
