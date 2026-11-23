package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.http.pathmap.PathSpecSet;
import org.spark_project.jetty.server.HttpChannel;
import org.spark_project.jetty.server.HttpOutput;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Response;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IncludeExclude;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class BufferedResponseHandler extends HandlerWrapper {
   static final Logger LOG = Log.getLogger(BufferedResponseHandler.class);
   private final IncludeExclude<String> _methods = new IncludeExclude<String>();
   private final IncludeExclude<String> _paths = new IncludeExclude<String>(PathSpecSet.class);
   private final IncludeExclude<String> _mimeTypes = new IncludeExclude<String>();

   public BufferedResponseHandler() {
      this._methods.include(HttpMethod.GET.asString());

      for(String type : MimeTypes.getKnownMimeTypes()) {
         if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
            this._mimeTypes.exclude(type);
         }
      }

      LOG.debug("{} mime types {}", this, this._mimeTypes);
   }

   public IncludeExclude<String> getMethodIncludeExclude() {
      return this._methods;
   }

   public IncludeExclude<String> getPathIncludeExclude() {
      return this._paths;
   }

   public IncludeExclude<String> getMimeIncludeExclude() {
      return this._mimeTypes;
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      ServletContext context = baseRequest.getServletContext();
      String path = context == null ? baseRequest.getRequestURI() : URIUtil.addPaths(baseRequest.getServletPath(), baseRequest.getPathInfo());
      LOG.debug("{} handle {} in {}", this, baseRequest, context);
      HttpOutput out = baseRequest.getResponse().getHttpOutput();

      for(HttpOutput.Interceptor interceptor = out.getInterceptor(); interceptor != null; interceptor = interceptor.getNextInterceptor()) {
         if (interceptor instanceof BufferedInterceptor) {
            LOG.debug("{} already intercepting {}", this, request);
            this._handler.handle(target, baseRequest, request, response);
            return;
         }
      }

      if (!this._methods.matches(baseRequest.getMethod())) {
         LOG.debug("{} excluded by method {}", this, request);
         this._handler.handle(target, baseRequest, request, response);
      } else if (!this.isPathBufferable(path)) {
         LOG.debug("{} excluded by path {}", this, request);
         this._handler.handle(target, baseRequest, request, response);
      } else {
         String mimeType = context == null ? MimeTypes.getDefaultMimeByExtension(path) : context.getMimeType(path);
         if (mimeType != null) {
            mimeType = MimeTypes.getContentTypeWithoutCharset(mimeType);
            if (!this.isMimeTypeBufferable(mimeType)) {
               LOG.debug("{} excluded by path suffix mime type {}", this, request);
               this._handler.handle(target, baseRequest, request, response);
               return;
            }
         }

         out.setInterceptor(new BufferedInterceptor(baseRequest.getHttpChannel(), out.getInterceptor()));
         if (this._handler != null) {
            this._handler.handle(target, baseRequest, request, response);
         }

      }
   }

   protected boolean isMimeTypeBufferable(String mimetype) {
      return this._mimeTypes.matches(mimetype);
   }

   protected boolean isPathBufferable(String requestURI) {
      return requestURI == null ? true : this._paths.matches(requestURI);
   }

   private class BufferedInterceptor implements HttpOutput.Interceptor {
      final HttpOutput.Interceptor _next;
      final HttpChannel _channel;
      final Queue<ByteBuffer> _buffers = new ConcurrentLinkedQueue();
      Boolean _aggregating;
      ByteBuffer _aggregate;

      public BufferedInterceptor(HttpChannel httpChannel, HttpOutput.Interceptor interceptor) {
         this._next = interceptor;
         this._channel = httpChannel;
      }

      public void resetBuffer() {
         this._buffers.clear();
         this._aggregating = null;
         this._aggregate = null;
      }

      public void write(ByteBuffer content, boolean last, Callback callback) {
         if (BufferedResponseHandler.LOG.isDebugEnabled()) {
            BufferedResponseHandler.LOG.debug("{} write last={} {}", this, last, BufferUtil.toDetailString(content));
         }

         if (this._aggregating == null) {
            Response response = this._channel.getResponse();
            int sc = response.getStatus();
            if (sc <= 0 || sc >= 200 && sc != 204 && sc != 205 && sc < 300) {
               String ct = response.getContentType();
               if (ct == null) {
                  this._aggregating = Boolean.TRUE;
               } else {
                  ct = MimeTypes.getContentTypeWithoutCharset(ct);
                  this._aggregating = BufferedResponseHandler.this.isMimeTypeBufferable(StringUtil.asciiToLowerCase(ct));
               }
            } else {
               this._aggregating = Boolean.FALSE;
            }
         }

         if (!this._aggregating) {
            this.getNextInterceptor().write(content, last, callback);
         } else {
            if (last) {
               if (BufferUtil.length(content) > 0) {
                  this._buffers.add(content);
               }

               if (BufferedResponseHandler.LOG.isDebugEnabled()) {
                  BufferedResponseHandler.LOG.debug("{} committing {}", this, this._buffers.size());
               }

               this.commit(this._buffers, callback);
            } else {
               if (BufferedResponseHandler.LOG.isDebugEnabled()) {
                  BufferedResponseHandler.LOG.debug("{} aggregating", this);
               }

               for(; BufferUtil.hasContent(content); BufferUtil.append(this._aggregate, content)) {
                  if (BufferUtil.space(this._aggregate) == 0) {
                     int size = Math.max(this._channel.getHttpConfiguration().getOutputBufferSize(), BufferUtil.length(content));
                     this._aggregate = BufferUtil.allocate(size);
                     this._buffers.add(this._aggregate);
                  }
               }

               callback.succeeded();
            }

         }
      }

      public HttpOutput.Interceptor getNextInterceptor() {
         return this._next;
      }

      public boolean isOptimizedForDirectBuffers() {
         return false;
      }

      protected void commit(Queue<ByteBuffer> buffers, final Callback callback) {
         if (this._buffers.size() == 0) {
            this.getNextInterceptor().write(BufferUtil.EMPTY_BUFFER, true, callback);
         } else if (this._buffers.size() == 1) {
            this.getNextInterceptor().write((ByteBuffer)this._buffers.remove(), true, callback);
         } else {
            IteratingCallback icb = new IteratingCallback() {
               protected IteratingCallback.Action process() throws Exception {
                  ByteBuffer buffer = (ByteBuffer)BufferedInterceptor.this._buffers.poll();
                  if (buffer == null) {
                     return IteratingCallback.Action.SUCCEEDED;
                  } else {
                     BufferedInterceptor.this.getNextInterceptor().write(buffer, BufferedInterceptor.this._buffers.isEmpty(), this);
                     return IteratingCallback.Action.SCHEDULED;
                  }
               }

               protected void onCompleteSuccess() {
                  callback.succeeded();
               }

               protected void onCompleteFailure(Throwable cause) {
                  callback.failed(cause);
               }
            };
            icb.iterate();
         }

      }
   }
}
