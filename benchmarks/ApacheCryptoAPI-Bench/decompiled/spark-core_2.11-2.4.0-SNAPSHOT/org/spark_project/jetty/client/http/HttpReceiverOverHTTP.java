package org.spark_project.jetty.client.http;

import java.io.EOFException;
import java.nio.ByteBuffer;
import org.spark_project.jetty.client.HttpClient;
import org.spark_project.jetty.client.HttpExchange;
import org.spark_project.jetty.client.HttpReceiver;
import org.spark_project.jetty.client.HttpResponse;
import org.spark_project.jetty.client.HttpResponseException;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpParser;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.CompletableCallback;

public class HttpReceiverOverHTTP extends HttpReceiver implements HttpParser.ResponseHandler {
   private final HttpParser parser = new HttpParser(this);
   private ByteBuffer buffer;
   private boolean shutdown;

   public HttpReceiverOverHTTP(HttpChannelOverHTTP channel) {
      super(channel);
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return (HttpChannelOverHTTP)super.getHttpChannel();
   }

   private HttpConnectionOverHTTP getHttpConnection() {
      return this.getHttpChannel().getHttpConnection();
   }

   protected ByteBuffer getResponseBuffer() {
      return this.buffer;
   }

   public void receive() {
      if (this.buffer == null) {
         this.acquireBuffer();
      }

      this.process();
   }

   private void acquireBuffer() {
      HttpClient client = this.getHttpDestination().getHttpClient();
      ByteBufferPool bufferPool = client.getByteBufferPool();
      this.buffer = bufferPool.acquire(client.getResponseBufferSize(), true);
   }

   private void releaseBuffer() {
      if (this.buffer == null) {
         throw new IllegalStateException();
      } else if (BufferUtil.hasContent(this.buffer)) {
         throw new IllegalStateException();
      } else {
         HttpClient client = this.getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = client.getByteBufferPool();
         bufferPool.release(this.buffer);
         this.buffer = null;
      }
   }

   private void process() {
      try {
         HttpConnectionOverHTTP connection = this.getHttpConnection();
         EndPoint endPoint = connection.getEndPoint();

         do {
            boolean upgraded = connection != endPoint.getConnection();
            if (connection.isClosed() || upgraded) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("{} {}", connection, upgraded ? "upgraded" : "closed");
               }

               this.releaseBuffer();
               return;
            }

            if (this.parse()) {
               return;
            }

            int read = endPoint.fill(this.buffer);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Read {} bytes {} from {}", read, BufferUtil.toDetailString(this.buffer), endPoint);
            }

            if (read <= 0) {
               if (read == 0) {
                  this.releaseBuffer();
                  this.fillInterested();
                  return;
               }

               this.releaseBuffer();
               this.shutdown();
               return;
            }
         } while(!this.parse());

      } catch (Throwable var5) {
         if (LOG.isDebugEnabled()) {
            LOG.debug(var5);
         }

         BufferUtil.clear(this.buffer);
         if (this.buffer != null) {
            this.releaseBuffer();
         }

         this.failAndClose(var5);
      }
   }

   private boolean parse() {
      boolean handle;
      do {
         handle = this.parser.parseNext(this.buffer);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Parsed {}, remaining {} {}", handle, this.buffer.remaining(), this.parser);
         }
      } while(!handle && this.buffer.hasRemaining());

      return handle;
   }

   protected void fillInterested() {
      this.getHttpConnection().fillInterested();
   }

   private void shutdown() {
      this.shutdown = true;
      this.parser.atEOF();
      this.parser.parseNext(BufferUtil.EMPTY_BUFFER);
   }

   protected boolean isShutdown() {
      return this.shutdown;
   }

   public int getHeaderCacheSize() {
      return 256;
   }

   public boolean startResponse(HttpVersion version, int status, String reason) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         String method = exchange.getRequest().getMethod();
         this.parser.setHeadResponse(HttpMethod.HEAD.is(method) || HttpMethod.CONNECT.is(method) && status == 200);
         exchange.getResponse().version(version).status(status).reason(reason);
         return !this.responseBegin(exchange);
      }
   }

   public void parsedHeader(HttpField field) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         this.responseHeader(exchange, field);
      }
   }

   public boolean headerComplete() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         return !this.responseHeaders(exchange);
      }
   }

   public boolean content(ByteBuffer buffer) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         CompletableCallback callback = new CompletableCallback() {
            public void resume() {
               if (HttpReceiverOverHTTP.LOG.isDebugEnabled()) {
                  HttpReceiverOverHTTP.LOG.debug("Content consumed asynchronously, resuming processing");
               }

               HttpReceiverOverHTTP.this.process();
            }

            public void abort(Throwable x) {
               HttpReceiverOverHTTP.this.failAndClose(x);
            }
         };
         boolean proceed = this.responseContent(exchange, buffer, callback);
         boolean async = callback.tryComplete();
         return !proceed || async;
      }
   }

   public boolean contentComplete() {
      return false;
   }

   public boolean messageComplete() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange == null) {
         return false;
      } else {
         boolean proceed = this.responseSuccess(exchange);
         if (!proceed) {
            return true;
         } else {
            int status = exchange.getResponse().getStatus();
            if (status == 101) {
               return true;
            } else {
               return HttpMethod.CONNECT.is(exchange.getRequest().getMethod()) && status == 200;
            }
         }
      }
   }

   public void earlyEOF() {
      HttpExchange exchange = this.getHttpExchange();
      HttpConnectionOverHTTP connection = this.getHttpConnection();
      if (exchange == null) {
         connection.close();
      } else {
         this.failAndClose(new EOFException(String.valueOf(connection)));
      }

   }

   public void badMessage(int status, String reason) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         HttpResponse response = exchange.getResponse();
         response.status(status).reason(reason);
         this.failAndClose(new HttpResponseException("HTTP protocol violation: bad response on " + this.getHttpConnection(), response));
      }

   }

   protected void reset() {
      super.reset();
      this.parser.reset();
   }

   protected void dispose() {
      super.dispose();
      this.parser.close();
   }

   private void failAndClose(Throwable failure) {
      if (this.responseFailure(failure)) {
         this.getHttpConnection().close(failure);
      }

   }

   public String toString() {
      return String.format("%s[%s]", super.toString(), this.parser);
   }
}
