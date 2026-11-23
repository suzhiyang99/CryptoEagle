package org.spark_project.jetty.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.client.ContentDecoder;
import org.spark_project.jetty.client.GZIPContentDecoder;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.client.util.DeferredContentProvider;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.io.RuntimeIOException;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.CountingCallback;
import org.spark_project.jetty.util.IteratingCallback;
import org.spark_project.jetty.util.component.Destroyable;

public class AsyncMiddleManServlet extends AbstractProxyServlet {
   private static final String PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".proxyRequestContentCommitted";
   private static final String CLIENT_TRANSFORMER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".clientTransformer";
   private static final String SERVER_TRANSFORMER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".serverTransformer";
   private static final String CONTINUE_ACTION_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".continueAction";

   protected void service(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) throws ServletException, IOException {
      String rewrittenTarget = this.rewriteTarget(clientRequest);
      if (this._log.isDebugEnabled()) {
         StringBuffer target = clientRequest.getRequestURL();
         if (clientRequest.getQueryString() != null) {
            target.append("?").append(clientRequest.getQueryString());
         }

         this._log.debug("{} rewriting: {} -> {}", this.getRequestId(clientRequest), target, rewrittenTarget);
      }

      if (rewrittenTarget == null) {
         this.onProxyRewriteFailed(clientRequest, proxyResponse);
      } else {
         Request proxyRequest = this.getHttpClient().newRequest(rewrittenTarget).method(clientRequest.getMethod()).version(HttpVersion.fromString(clientRequest.getProtocol()));
         this.copyRequestHeaders(clientRequest, proxyRequest);
         this.addProxyHeaders(clientRequest, proxyRequest);
         AsyncContext asyncContext = clientRequest.startAsync();
         asyncContext.setTimeout(0L);
         proxyRequest.timeout(this.getTimeout(), TimeUnit.MILLISECONDS);
         if (this.hasContent(clientRequest)) {
            DeferredContentProvider provider = this.newProxyContentProvider(clientRequest, proxyResponse, proxyRequest);
            proxyRequest.content(provider);
            if (this.expects100Continue(clientRequest)) {
               proxyRequest.attribute("org.spark_project.jetty.proxy.clientRequest", clientRequest);
               proxyRequest.attribute(CONTINUE_ACTION_ATTRIBUTE, (Runnable)() -> {
                  try {
                     ServletInputStream input = clientRequest.getInputStream();
                     input.setReadListener(this.newProxyReadListener(clientRequest, proxyResponse, proxyRequest, provider));
                  } catch (Throwable failure) {
                     this.onClientRequestFailure(clientRequest, proxyRequest, proxyResponse, failure);
                  }

               });
               this.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            } else {
               ServletInputStream input = clientRequest.getInputStream();
               input.setReadListener(this.newProxyReadListener(clientRequest, proxyResponse, proxyRequest, provider));
            }
         } else {
            this.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
         }

      }
   }

   protected DeferredContentProvider newProxyContentProvider(final HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) throws IOException {
      return new DeferredContentProvider(new ByteBuffer[0]) {
         public boolean offer(ByteBuffer buffer, Callback callback) {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} proxying content to upstream: {} bytes", AsyncMiddleManServlet.this.getRequestId(clientRequest), buffer.remaining());
            }

            return super.offer(buffer, callback);
         }
      };
   }

   protected ReadListener newProxyReadListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest, DeferredContentProvider provider) {
      return new ProxyReader(clientRequest, proxyResponse, proxyRequest, provider);
   }

   protected ProxyWriter newProxyWriteListener(HttpServletRequest clientRequest, Response proxyResponse) {
      return new ProxyWriter(clientRequest, proxyResponse);
   }

   protected Response.CompleteListener newProxyResponseListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) {
      return new ProxyResponseListener(clientRequest, proxyResponse);
   }

   protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
      return AsyncMiddleManServlet.ContentTransformer.IDENTITY;
   }

   protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
      return AsyncMiddleManServlet.ContentTransformer.IDENTITY;
   }

   protected void onContinue(HttpServletRequest clientRequest, Request proxyRequest) {
      super.onContinue(clientRequest, proxyRequest);
      Runnable action = (Runnable)proxyRequest.getAttributes().get(CONTINUE_ACTION_ATTRIBUTE);
      action.run();
   }

   private void transform(ContentTransformer transformer, ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
      try {
         transformer.transform(input, finished, output);
      } catch (Throwable x) {
         this._log.info("Exception while transforming " + transformer, x);
         throw x;
      }
   }

   int readClientRequestContent(ServletInputStream input, byte[] buffer) throws IOException {
      return input.read(buffer);
   }

   void writeProxyResponseContent(ServletOutputStream output, ByteBuffer content) throws IOException {
      write(output, content);
   }

   private static void write(OutputStream output, ByteBuffer content) throws IOException {
      int length = content.remaining();
      int offset = 0;
      byte[] buffer;
      if (content.hasArray()) {
         offset = content.arrayOffset();
         buffer = content.array();
      } else {
         buffer = new byte[length];
         content.get(buffer);
      }

      output.write(buffer, offset, length);
   }

   private void cleanup(HttpServletRequest clientRequest) {
      ContentTransformer clientTransformer = (ContentTransformer)clientRequest.getAttribute(CLIENT_TRANSFORMER_ATTRIBUTE);
      if (clientTransformer instanceof Destroyable) {
         ((Destroyable)clientTransformer).destroy();
      }

      ContentTransformer serverTransformer = (ContentTransformer)clientRequest.getAttribute(SERVER_TRANSFORMER_ATTRIBUTE);
      if (serverTransformer instanceof Destroyable) {
         ((Destroyable)serverTransformer).destroy();
      }

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

   protected class ProxyReader extends IteratingCallback implements ReadListener {
      private final byte[] buffer = new byte[AsyncMiddleManServlet.this.getHttpClient().getRequestBufferSize()];
      private final List<ByteBuffer> buffers = new ArrayList();
      private final HttpServletRequest clientRequest;
      private final HttpServletResponse proxyResponse;
      private final Request proxyRequest;
      private final DeferredContentProvider provider;
      private final int contentLength;
      private final boolean expects100Continue;
      private int length;

      protected ProxyReader(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest, DeferredContentProvider provider) {
         this.clientRequest = clientRequest;
         this.proxyResponse = proxyResponse;
         this.proxyRequest = proxyRequest;
         this.provider = provider;
         this.contentLength = clientRequest.getContentLength();
         this.expects100Continue = AsyncMiddleManServlet.this.expects100Continue(clientRequest);
      }

      public void onDataAvailable() throws IOException {
         this.iterate();
      }

      public void onAllDataRead() throws IOException {
         if (!this.provider.isClosed()) {
            this.process(BufferUtil.EMPTY_BUFFER, new Callback() {
               public void failed(Throwable x) {
                  ProxyReader.this.onError(x);
               }
            }, true);
         }

         if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
            AsyncMiddleManServlet.this._log.debug("{} proxying content to upstream completed", (long)AsyncMiddleManServlet.this.getRequestId(this.clientRequest));
         }

      }

      public void onError(Throwable t) {
         AsyncMiddleManServlet.this.cleanup(this.clientRequest);
         AsyncMiddleManServlet.this.onClientRequestFailure(this.clientRequest, this.proxyRequest, this.proxyResponse, t);
      }

      protected IteratingCallback.Action process() throws Exception {
         ServletInputStream input = this.clientRequest.getInputStream();

         while(input.isReady() && !input.isFinished()) {
            int read = AsyncMiddleManServlet.this.readClientRequestContent(input, this.buffer);
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} asynchronous read {} bytes on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), read, input);
            }

            if (read < 0) {
               return IteratingCallback.Action.SUCCEEDED;
            }

            if (this.contentLength > 0 && read > 0) {
               this.length += read;
            }

            ByteBuffer content = read > 0 ? ByteBuffer.wrap(this.buffer, 0, read) : BufferUtil.EMPTY_BUFFER;
            boolean finished = this.length == this.contentLength;
            this.process(content, this, finished);
            if (read > 0) {
               return IteratingCallback.Action.SCHEDULED;
            }
         }

         if (input.isFinished()) {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} asynchronous read complete on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), input);
            }

            return IteratingCallback.Action.SUCCEEDED;
         } else {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} asynchronous read pending on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), input);
            }

            return IteratingCallback.Action.IDLE;
         }
      }

      private void process(ByteBuffer content, Callback callback, boolean finished) throws IOException {
         ContentTransformer transformer = (ContentTransformer)this.clientRequest.getAttribute(AsyncMiddleManServlet.CLIENT_TRANSFORMER_ATTRIBUTE);
         if (transformer == null) {
            transformer = AsyncMiddleManServlet.this.newClientRequestContentTransformer(this.clientRequest, this.proxyRequest);
            this.clientRequest.setAttribute(AsyncMiddleManServlet.CLIENT_TRANSFORMER_ATTRIBUTE, transformer);
         }

         int contentBytes = content.remaining();
         if (contentBytes == 0 && !finished) {
            callback.succeeded();
         } else {
            AsyncMiddleManServlet.this.transform(transformer, content, finished, this.buffers);
            int newContentBytes = 0;
            int size = this.buffers.size();
            if (size > 0) {
               CountingCallback counter = new CountingCallback(callback, size);

               for(int i = 0; i < size; ++i) {
                  ByteBuffer buffer = (ByteBuffer)this.buffers.get(i);
                  newContentBytes += buffer.remaining();
                  this.provider.offer(buffer, counter);
               }

               this.buffers.clear();
            }

            if (finished) {
               this.provider.close();
            }

            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} upstream content transformation {} -> {} bytes", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), contentBytes, newContentBytes);
            }

            boolean contentCommitted = this.clientRequest.getAttribute(AsyncMiddleManServlet.PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE) != null;
            if (!contentCommitted && (size > 0 || finished)) {
               this.clientRequest.setAttribute(AsyncMiddleManServlet.PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE, true);
               if (!this.expects100Continue) {
                  this.proxyRequest.header((HttpHeader)HttpHeader.CONTENT_LENGTH, (String)null);
                  AsyncMiddleManServlet.this.sendProxyRequest(this.clientRequest, this.proxyResponse, this.proxyRequest);
               }
            }

            if (size == 0) {
               callback.succeeded();
            }

         }
      }

      protected void onCompleteFailure(Throwable x) {
         this.onError(x);
      }
   }

   protected class ProxyResponseListener extends Response.Listener.Adapter implements Callback {
      private final String WRITE_LISTENER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".writeListener";
      private final Callback complete = new CountingCallback(this, 2);
      private final List<ByteBuffer> buffers = new ArrayList();
      private final HttpServletRequest clientRequest;
      private final HttpServletResponse proxyResponse;
      private boolean hasContent;
      private long contentLength;
      private long length;
      private Response response;

      protected ProxyResponseListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) {
         this.clientRequest = clientRequest;
         this.proxyResponse = proxyResponse;
      }

      public void onBegin(Response serverResponse) {
         this.response = serverResponse;
         this.proxyResponse.setStatus(serverResponse.getStatus());
      }

      public void onHeaders(Response serverResponse) {
         this.contentLength = serverResponse.getHeaders().getLongField(HttpHeader.CONTENT_LENGTH.asString());
         AsyncMiddleManServlet.this.onServerResponseHeaders(this.clientRequest, this.proxyResponse, serverResponse);
      }

      public void onContent(Response serverResponse, ByteBuffer content, Callback callback) {
         try {
            int contentBytes = content.remaining();
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} received server content: {} bytes", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), contentBytes);
            }

            this.hasContent = true;
            ProxyWriter proxyWriter = (ProxyWriter)this.clientRequest.getAttribute(this.WRITE_LISTENER_ATTRIBUTE);
            boolean committed = proxyWriter != null;
            if (proxyWriter == null) {
               proxyWriter = AsyncMiddleManServlet.this.newProxyWriteListener(this.clientRequest, serverResponse);
               this.clientRequest.setAttribute(this.WRITE_LISTENER_ATTRIBUTE, proxyWriter);
            }

            ContentTransformer transformer = (ContentTransformer)this.clientRequest.getAttribute(AsyncMiddleManServlet.SERVER_TRANSFORMER_ATTRIBUTE);
            if (transformer == null) {
               transformer = AsyncMiddleManServlet.this.newServerResponseContentTransformer(this.clientRequest, this.proxyResponse, serverResponse);
               this.clientRequest.setAttribute(AsyncMiddleManServlet.SERVER_TRANSFORMER_ATTRIBUTE, transformer);
            }

            this.length += (long)contentBytes;
            boolean finished = this.contentLength >= 0L && this.length == this.contentLength;
            AsyncMiddleManServlet.this.transform(transformer, content, finished, this.buffers);
            int newContentBytes = 0;
            int size = this.buffers.size();
            if (size <= 0) {
               proxyWriter.offer(BufferUtil.EMPTY_BUFFER, callback);
            } else {
               Callback counter = (Callback)(size == 1 ? callback : new CountingCallback(callback, size));

               for(int i = 0; i < size; ++i) {
                  ByteBuffer buffer = (ByteBuffer)this.buffers.get(i);
                  newContentBytes += buffer.remaining();
                  proxyWriter.offer(buffer, counter);
               }

               this.buffers.clear();
            }

            if (finished) {
               proxyWriter.offer(BufferUtil.EMPTY_BUFFER, this.complete);
            }

            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} downstream content transformation {} -> {} bytes", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), contentBytes, newContentBytes);
            }

            if (committed) {
               proxyWriter.onWritePossible();
            } else {
               if (this.contentLength >= 0L) {
                  this.proxyResponse.setContentLength(-1);
               }

               this.proxyResponse.getOutputStream().setWriteListener(proxyWriter);
            }
         } catch (Throwable x) {
            callback.failed(x);
         }

      }

      public void onSuccess(Response serverResponse) {
         try {
            if (this.hasContent) {
               if (this.contentLength < 0L) {
                  ProxyWriter proxyWriter = (ProxyWriter)this.clientRequest.getAttribute(this.WRITE_LISTENER_ATTRIBUTE);
                  ContentTransformer transformer = (ContentTransformer)this.clientRequest.getAttribute(AsyncMiddleManServlet.SERVER_TRANSFORMER_ATTRIBUTE);
                  AsyncMiddleManServlet.this.transform(transformer, BufferUtil.EMPTY_BUFFER, true, this.buffers);
                  long newContentBytes = 0L;
                  int size = this.buffers.size();
                  if (size <= 0) {
                     proxyWriter.offer(BufferUtil.EMPTY_BUFFER, this.complete);
                  } else {
                     Callback callback = (Callback)(size == 1 ? this.complete : new CountingCallback(this.complete, size));

                     for(int i = 0; i < size; ++i) {
                        ByteBuffer buffer = (ByteBuffer)this.buffers.get(i);
                        newContentBytes += (long)buffer.remaining();
                        proxyWriter.offer(buffer, callback);
                     }

                     this.buffers.clear();
                  }

                  if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                     AsyncMiddleManServlet.this._log.debug("{} downstream content transformation to {} bytes", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), newContentBytes);
                  }

                  proxyWriter.onWritePossible();
               }
            } else {
               this.complete.succeeded();
            }
         } catch (Throwable x) {
            this.complete.failed(x);
         }

      }

      public void onComplete(Result result) {
         if (result.isSucceeded()) {
            this.complete.succeeded();
         } else {
            this.complete.failed(result.getFailure());
         }

      }

      public void succeeded() {
         AsyncMiddleManServlet.this.cleanup(this.clientRequest);
         AsyncMiddleManServlet.this.onProxyResponseSuccess(this.clientRequest, this.proxyResponse, this.response);
      }

      public void failed(Throwable failure) {
         AsyncMiddleManServlet.this.cleanup(this.clientRequest);
         AsyncMiddleManServlet.this.onProxyResponseFailure(this.clientRequest, this.proxyResponse, this.response, failure);
      }
   }

   protected class ProxyWriter implements WriteListener {
      private final Queue<DeferredContentProvider.Chunk> chunks = new ArrayDeque();
      private final HttpServletRequest clientRequest;
      private final Response serverResponse;
      private DeferredContentProvider.Chunk chunk;
      private boolean writePending;

      protected ProxyWriter(HttpServletRequest clientRequest, Response serverResponse) {
         this.clientRequest = clientRequest;
         this.serverResponse = serverResponse;
      }

      public boolean offer(ByteBuffer content, Callback callback) {
         if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
            AsyncMiddleManServlet.this._log.debug("{} proxying content to downstream: {} bytes {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), content.remaining(), callback);
         }

         return this.chunks.offer(new DeferredContentProvider.Chunk(content, callback));
      }

      public void onWritePossible() throws IOException {
         ServletOutputStream output = this.clientRequest.getAsyncContext().getResponse().getOutputStream();
         if (this.writePending) {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
               AsyncMiddleManServlet.this._log.debug("{} pending async write complete of {} on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), this.chunk, output);
            }

            this.writePending = false;
            if (this.succeed(this.chunk.callback)) {
               return;
            }
         }

         int length = 0;
         DeferredContentProvider.Chunk chunk = null;

         while(output.isReady()) {
            if (chunk != null) {
               if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                  AsyncMiddleManServlet.this._log.debug("{} async write complete of {} ({} bytes) on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), chunk, length, output);
               }

               if (this.succeed(chunk.callback)) {
                  return;
               }
            }

            this.chunk = chunk = (DeferredContentProvider.Chunk)this.chunks.poll();
            if (chunk == null) {
               return;
            }

            length = chunk.buffer.remaining();
            if (length > 0) {
               AsyncMiddleManServlet.this.writeProxyResponseContent(output, chunk.buffer);
            }
         }

         if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
            AsyncMiddleManServlet.this._log.debug("{} async write pending of {} ({} bytes) on {}", AsyncMiddleManServlet.this.getRequestId(this.clientRequest), chunk, length, output);
         }

         this.writePending = true;
      }

      private boolean succeed(Callback callback) {
         callback.succeeded();
         return this.writePending;
      }

      public void onError(Throwable failure) {
         DeferredContentProvider.Chunk chunk = this.chunk;
         if (chunk != null) {
            chunk.callback.failed(failure);
         } else {
            this.serverResponse.abort(failure);
         }

      }
   }

   public interface ContentTransformer {
      ContentTransformer IDENTITY = new IdentityContentTransformer();

      void transform(ByteBuffer var1, boolean var2, List<ByteBuffer> var3) throws IOException;
   }

   private static class IdentityContentTransformer implements ContentTransformer {
      private IdentityContentTransformer() {
      }

      public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
         output.add(input);
      }
   }

   public static class GZIPContentTransformer implements ContentTransformer {
      private final List<ByteBuffer> buffers = new ArrayList(2);
      private final ContentDecoder decoder = new GZIPContentDecoder();
      private final ContentTransformer transformer;
      private final ByteArrayOutputStream out;
      private final GZIPOutputStream gzipOut;

      public GZIPContentTransformer(ContentTransformer transformer) {
         try {
            this.transformer = transformer;
            this.out = new ByteArrayOutputStream();
            this.gzipOut = new GZIPOutputStream(this.out);
         } catch (IOException x) {
            throw new RuntimeIOException(x);
         }
      }

      public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
         if (!input.hasRemaining()) {
            if (finished) {
               this.transformer.transform(input, true, this.buffers);
            }
         } else {
            while(input.hasRemaining()) {
               ByteBuffer decoded = this.decoder.decode(input);
               if (decoded.hasRemaining()) {
                  this.transformer.transform(decoded, finished && !input.hasRemaining(), this.buffers);
               }
            }
         }

         if (!this.buffers.isEmpty() || finished) {
            ByteBuffer result = this.gzip(this.buffers, finished);
            this.buffers.clear();
            output.add(result);
         }

      }

      private ByteBuffer gzip(List<ByteBuffer> buffers, boolean finished) throws IOException {
         for(ByteBuffer buffer : buffers) {
            AsyncMiddleManServlet.write(this.gzipOut, buffer);
         }

         if (finished) {
            this.gzipOut.close();
         }

         byte[] gzipBytes = this.out.toByteArray();
         this.out.reset();
         return ByteBuffer.wrap(gzipBytes);
      }
   }
}
