package org.spark_project.jetty.client.http;

import java.nio.ByteBuffer;
import org.spark_project.jetty.client.HttpClient;
import org.spark_project.jetty.client.HttpContent;
import org.spark_project.jetty.client.HttpExchange;
import org.spark_project.jetty.client.HttpRequestException;
import org.spark_project.jetty.client.HttpSender;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.http.HttpGenerator;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IteratingCallback;

public class HttpSenderOverHTTP extends HttpSender {
   private final HttpGenerator generator = new HttpGenerator();
   private boolean shutdown;

   public HttpSenderOverHTTP(HttpChannelOverHTTP channel) {
      super(channel);
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return (HttpChannelOverHTTP)super.getHttpChannel();
   }

   protected void sendHeaders(HttpExchange exchange, HttpContent content, Callback callback) {
      try {
         (new HeadersCallback(exchange, content, callback)).iterate();
      } catch (Throwable var5) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var5);
         }

         callback.failed(var5);
      }

   }

   protected void sendContent(HttpExchange exchange, HttpContent content, Callback callback) {
      try {
         HttpClient client = this.getHttpChannel().getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = client.getByteBufferPool();
         ByteBuffer chunk = null;

         while(true) {
            ByteBuffer contentBuffer = content.getByteBuffer();
            boolean lastContent = content.isLast();
            HttpGenerator.Result result = this.generator.generateRequest((MetaData.Request)null, (ByteBuffer)null, chunk, contentBuffer, lastContent);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Generated content ({} bytes) - {}/{}", contentBuffer == null ? -1 : contentBuffer.remaining(), result, this.generator);
            }

            switch (result) {
               case NEED_CHUNK:
                  chunk = bufferPool.acquire(12, false);
                  break;
               case FLUSH:
                  EndPoint endPoint = this.getHttpChannel().getHttpConnection().getEndPoint();
                  if (chunk != null) {
                     endPoint.write(new ByteBufferRecyclerCallback(callback, bufferPool, new ByteBuffer[]{chunk}), chunk, contentBuffer);
                  } else {
                     endPoint.write(callback, contentBuffer);
                  }

                  return;
               case SHUTDOWN_OUT:
                  this.shutdownOutput();
                  break;
               case CONTINUE:
                  if (!lastContent) {
                     callback.succeeded();
                     return;
                  }
                  break;
               case DONE:
                  callback.succeeded();
                  return;
               default:
                  throw new IllegalStateException(result.toString());
            }
         }
      } catch (Throwable var11) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var11);
         }

         callback.failed(var11);
      }
   }

   protected void reset() {
      this.generator.reset();
      super.reset();
   }

   protected void dispose() {
      this.generator.abort();
      super.dispose();
      this.shutdownOutput();
   }

   private void shutdownOutput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Request shutdown output {}", this.getHttpExchange().getRequest());
      }

      this.shutdown = true;
   }

   protected boolean isShutdown() {
      return this.shutdown;
   }

   public String toString() {
      return String.format("%s[%s]", super.toString(), this.generator);
   }

   private class HeadersCallback extends IteratingCallback {
      private final HttpExchange exchange;
      private final Callback callback;
      private final MetaData.Request metaData;
      private ByteBuffer headerBuffer;
      private ByteBuffer chunkBuffer;
      private ByteBuffer contentBuffer;
      private boolean lastContent;
      private boolean generated;

      public HeadersCallback(HttpExchange exchange, HttpContent content, Callback callback) {
         super(false);
         this.exchange = exchange;
         this.callback = callback;
         Request request = exchange.getRequest();
         ContentProvider requestContent = request.getContent();
         long contentLength = requestContent == null ? -1L : requestContent.getLength();
         String path = request.getPath();
         String query = request.getQuery();
         if (query != null) {
            path = path + "?" + query;
         }

         this.metaData = new MetaData.Request(request.getMethod(), new HttpURI(path), request.getVersion(), request.getHeaders(), contentLength);
         if (!HttpSenderOverHTTP.this.expects100Continue(request)) {
            content.advance();
            this.contentBuffer = content.getByteBuffer();
            this.lastContent = content.isLast();
         }

      }

      protected IteratingCallback.Action process() throws Exception {
         HttpClient client = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = client.getByteBufferPool();

         while(true) {
            HttpGenerator.Result result = HttpSenderOverHTTP.this.generator.generateRequest(this.metaData, this.headerBuffer, this.chunkBuffer, this.contentBuffer, this.lastContent);
            if (HttpSenderOverHTTP.LOG.isDebugEnabled()) {
               HttpSenderOverHTTP.LOG.debug("Generated headers ({} bytes), chunk ({} bytes), content ({} bytes) - {}/{}", this.headerBuffer == null ? -1 : this.headerBuffer.remaining(), this.chunkBuffer == null ? -1 : this.chunkBuffer.remaining(), this.contentBuffer == null ? -1 : this.contentBuffer.remaining(), result, HttpSenderOverHTTP.this.generator);
            }

            switch (result) {
               case NEED_CHUNK:
                  this.chunkBuffer = bufferPool.acquire(12, false);
                  break;
               case FLUSH:
                  EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
                  if (this.chunkBuffer == null) {
                     if (this.contentBuffer == null) {
                        endPoint.write(this, this.headerBuffer);
                     } else {
                        endPoint.write(this, this.headerBuffer, this.contentBuffer);
                     }
                  } else if (this.contentBuffer == null) {
                     endPoint.write(this, this.headerBuffer, this.chunkBuffer);
                  } else {
                     endPoint.write(this, this.headerBuffer, this.chunkBuffer, this.contentBuffer);
                  }

                  this.generated = true;
                  return IteratingCallback.Action.SCHEDULED;
               case SHUTDOWN_OUT:
                  HttpSenderOverHTTP.this.shutdownOutput();
                  return IteratingCallback.Action.SUCCEEDED;
               case CONTINUE:
                  if (this.generated) {
                     return IteratingCallback.Action.SUCCEEDED;
                  }
                  break;
               case DONE:
                  if (this.generated) {
                     return IteratingCallback.Action.SUCCEEDED;
                  }

                  throw new HttpRequestException("Could not generate headers", this.exchange.getRequest());
               case NEED_HEADER:
                  this.headerBuffer = bufferPool.acquire(client.getRequestBufferSize(), false);
                  break;
               default:
                  throw new IllegalStateException(result.toString());
            }
         }
      }

      public void succeeded() {
         this.release();
         super.succeeded();
      }

      public void failed(Throwable x) {
         this.release();
         this.callback.failed(x);
         super.failed(x);
      }

      protected void onCompleteSuccess() {
         super.onCompleteSuccess();
         this.callback.succeeded();
      }

      private void release() {
         HttpClient client = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = client.getByteBufferPool();
         bufferPool.release(this.headerBuffer);
         this.headerBuffer = null;
         if (this.chunkBuffer != null) {
            bufferPool.release(this.chunkBuffer);
         }

         this.chunkBuffer = null;
      }
   }

   private class ByteBufferRecyclerCallback extends Callback.Nested {
      private final ByteBufferPool pool;
      private final ByteBuffer[] buffers;

      private ByteBufferRecyclerCallback(Callback callback, ByteBufferPool pool, ByteBuffer... buffers) {
         super(callback);
         this.pool = pool;
         this.buffers = buffers;
      }

      public void succeeded() {
         for(ByteBuffer buffer : this.buffers) {
            assert !buffer.hasRemaining();

            this.pool.release(buffer);
         }

         super.succeeded();
      }

      public void failed(Throwable x) {
         for(ByteBuffer buffer : this.buffers) {
            this.pool.release(buffer);
         }

         super.failed(x);
      }
   }
}
