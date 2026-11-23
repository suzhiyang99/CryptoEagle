package org.spark_project.jetty.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.util.DeferredContentProvider;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;

public class AsyncProxyServlet extends ProxyServlet {
   private static final String WRITE_LISTENER_ATTRIBUTE = AsyncProxyServlet.class.getName() + ".writeListener";

   protected ContentProvider proxyRequestContent(HttpServletRequest request, HttpServletResponse response, Request proxyRequest) throws IOException {
      ServletInputStream input = request.getInputStream();
      DeferredContentProvider provider = new DeferredContentProvider(new ByteBuffer[0]);
      input.setReadListener(this.newReadListener(request, response, proxyRequest, provider));
      return provider;
   }

   protected ReadListener newReadListener(HttpServletRequest request, HttpServletResponse response, Request proxyRequest, DeferredContentProvider provider) {
      return new StreamReader(request, response, proxyRequest, provider);
   }

   protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length, Callback callback) {
      try {
         if (this._log.isDebugEnabled()) {
            this._log.debug("{} proxying content to downstream: {} bytes", this.getRequestId(request), length);
         }

         StreamWriter writeListener = (StreamWriter)request.getAttribute(WRITE_LISTENER_ATTRIBUTE);
         if (writeListener == null) {
            writeListener = this.newWriteListener(request, proxyResponse);
            request.setAttribute(WRITE_LISTENER_ATTRIBUTE, writeListener);
            writeListener.data(buffer, offset, length, callback);
            response.getOutputStream().setWriteListener(writeListener);
         } else {
            writeListener.data(buffer, offset, length, callback);
            writeListener.onWritePossible();
         }
      } catch (Throwable x) {
         callback.failed(x);
         proxyResponse.abort(x);
      }

   }

   protected StreamWriter newWriteListener(HttpServletRequest request, Response proxyResponse) {
      return new StreamWriter(request, proxyResponse);
   }

   public static class Transparent extends AsyncProxyServlet {
      private final AbstractProxyServlet.TransparentDelegate delegate = new AbstractProxyServlet.TransparentDelegate(this);

      public void init(ServletConfig config) throws ServletException {
         super.init(config);
         this.delegate.init(config);
      }

      protected String rewriteTarget(HttpServletRequest clientRequest) {
         return this.delegate.rewriteTarget(clientRequest);
      }
   }

   protected class StreamReader extends IteratingCallback implements ReadListener {
      private final byte[] buffer = new byte[AsyncProxyServlet.this.getHttpClient().getRequestBufferSize()];
      private final HttpServletRequest request;
      private final HttpServletResponse response;
      private final Request proxyRequest;
      private final DeferredContentProvider provider;

      protected StreamReader(HttpServletRequest request, HttpServletResponse response, Request proxyRequest, DeferredContentProvider provider) {
         this.request = request;
         this.response = response;
         this.proxyRequest = proxyRequest;
         this.provider = provider;
      }

      public void onDataAvailable() throws IOException {
         this.iterate();
      }

      public void onAllDataRead() throws IOException {
         if (AsyncProxyServlet.this._log.isDebugEnabled()) {
            AsyncProxyServlet.this._log.debug("{} proxying content to upstream completed", (long)AsyncProxyServlet.this.getRequestId(this.request));
         }

         this.provider.close();
      }

      public void onError(Throwable t) {
         AsyncProxyServlet.this.onClientRequestFailure(this.request, this.proxyRequest, this.response, t);
      }

      protected IteratingCallback.Action process() throws Exception {
         int requestId = AsyncProxyServlet.this._log.isDebugEnabled() ? AsyncProxyServlet.this.getRequestId(this.request) : 0;
         ServletInputStream input = this.request.getInputStream();

         while(input.isReady() && !input.isFinished()) {
            int read = input.read(this.buffer);
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous read {} bytes on {}", requestId, read, input);
            }

            if (read > 0) {
               if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                  AsyncProxyServlet.this._log.debug("{} proxying content to upstream: {} bytes", requestId, read);
               }

               this.onRequestContent(this.request, this.proxyRequest, this.provider, this.buffer, 0, read, this);
               return IteratingCallback.Action.SCHEDULED;
            }
         }

         if (input.isFinished()) {
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous read complete on {}", requestId, input);
            }

            return IteratingCallback.Action.SUCCEEDED;
         } else {
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous read pending on {}", requestId, input);
            }

            return IteratingCallback.Action.IDLE;
         }
      }

      protected void onRequestContent(HttpServletRequest request, Request proxyRequest, DeferredContentProvider provider, byte[] buffer, int offset, int length, Callback callback) {
         provider.offer(ByteBuffer.wrap(buffer, offset, length), callback);
      }

      public void failed(Throwable x) {
         super.failed(x);
         this.onError(x);
      }
   }

   protected class StreamWriter implements WriteListener {
      private final HttpServletRequest request;
      private final Response proxyResponse;
      private WriteState state;
      private byte[] buffer;
      private int offset;
      private int length;
      private Callback callback;

      protected StreamWriter(HttpServletRequest request, Response proxyResponse) {
         this.request = request;
         this.proxyResponse = proxyResponse;
         this.state = AsyncProxyServlet.WriteState.IDLE;
      }

      protected void data(byte[] bytes, int offset, int length, Callback callback) {
         if (this.state != AsyncProxyServlet.WriteState.IDLE) {
            throw new WritePendingException();
         } else {
            this.state = AsyncProxyServlet.WriteState.READY;
            this.buffer = bytes;
            this.offset = offset;
            this.length = length;
            this.callback = callback;
         }
      }

      public void onWritePossible() throws IOException {
         int requestId = AsyncProxyServlet.this.getRequestId(this.request);
         ServletOutputStream output = this.request.getAsyncContext().getResponse().getOutputStream();
         if (this.state == AsyncProxyServlet.WriteState.READY) {
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous write start of {} bytes on {}", requestId, this.length, output);
            }

            output.write(this.buffer, this.offset, this.length);
            this.state = AsyncProxyServlet.WriteState.PENDING;
            if (output.isReady()) {
               if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                  AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes completed on {}", requestId, this.length, output);
               }

               this.complete();
            } else if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes pending on {}", requestId, this.length, output);
            }
         } else {
            if (this.state != AsyncProxyServlet.WriteState.PENDING) {
               throw new IllegalStateException();
            }

            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
               AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes completing on {}", requestId, this.length, output);
            }

            this.complete();
         }

      }

      protected void complete() {
         this.buffer = null;
         this.offset = 0;
         this.length = 0;
         Callback c = this.callback;
         this.callback = null;
         this.state = AsyncProxyServlet.WriteState.IDLE;
         c.succeeded();
      }

      public void onError(Throwable failure) {
         this.proxyResponse.abort(failure);
      }
   }

   private static enum WriteState {
      READY,
      PENDING,
      IDLE;
   }
}
