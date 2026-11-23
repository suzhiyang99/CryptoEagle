package org.spark_project.jetty.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpParser;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.io.ByteArrayEndPoint;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.ByteArrayOutputStream2;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.Scheduler;

public class LocalConnector extends AbstractConnector {
   private final BlockingQueue<LocalEndPoint> _connects;

   public LocalConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool pool, int acceptors, ConnectionFactory... factories) {
      super(server, executor, scheduler, pool, acceptors, factories);
      this._connects = new LinkedBlockingQueue();
      this.setIdleTimeout(30000L);
   }

   public LocalConnector(Server server) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, new HttpConnectionFactory());
   }

   public LocalConnector(Server server, SslContextFactory sslContextFactory) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public LocalConnector(Server server, ConnectionFactory connectionFactory) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, connectionFactory);
   }

   public LocalConnector(Server server, ConnectionFactory connectionFactory, SslContextFactory sslContextFactory) {
      this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, connectionFactory));
   }

   public Object getTransport() {
      return this;
   }

   public String getResponses(String requests) throws Exception {
      return this.getResponses(requests, 5L, TimeUnit.SECONDS);
   }

   public String getResponses(String requests, long idleFor, TimeUnit units) throws Exception {
      ByteBuffer result = this.getResponses(BufferUtil.toBuffer(requests, StandardCharsets.UTF_8), idleFor, units);
      return result == null ? null : BufferUtil.toString(result, StandardCharsets.UTF_8);
   }

   public ByteBuffer getResponses(ByteBuffer requestsBuffer) throws Exception {
      return this.getResponses(requestsBuffer, 5L, TimeUnit.SECONDS);
   }

   public ByteBuffer getResponses(ByteBuffer requestsBuffer, long idleFor, TimeUnit units) throws Exception {
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestsBuffer));
      }

      LocalEndPoint endp = this.executeRequest(requestsBuffer);
      endp.waitUntilClosedOrIdleFor(idleFor, units);
      ByteBuffer responses = endp.takeOutput();
      if (endp.isOutputShutdown()) {
         endp.close();
      }

      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("responses {}", BufferUtil.toUTF8String(responses));
      }

      return responses;
   }

   public LocalEndPoint executeRequest(String rawRequest) {
      return this.executeRequest(BufferUtil.toBuffer(rawRequest, StandardCharsets.UTF_8));
   }

   private LocalEndPoint executeRequest(ByteBuffer rawRequest) {
      if (!this.isStarted()) {
         throw new IllegalStateException("!STARTED");
      } else {
         LocalEndPoint endp = new LocalEndPoint();
         endp.addInput(rawRequest);
         this._connects.add(endp);
         return endp;
      }
   }

   public LocalEndPoint connect() {
      LocalEndPoint endp = new LocalEndPoint();
      this._connects.add(endp);
      return endp;
   }

   protected void accept(int acceptorID) throws IOException, InterruptedException {
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("accepting {}", (long)acceptorID);
      }

      LocalEndPoint endPoint = (LocalEndPoint)this._connects.take();
      endPoint.onOpen();
      this.onEndPointOpened(endPoint);
      Connection connection = this.getDefaultConnectionFactory().newConnection(this, endPoint);
      endPoint.setConnection(connection);
      connection.onOpen();
   }

   public ByteBuffer getResponse(ByteBuffer requestsBuffer) throws Exception {
      return this.getResponse(requestsBuffer, false, 10L, TimeUnit.SECONDS);
   }

   public ByteBuffer getResponse(ByteBuffer requestBuffer, long time, TimeUnit unit) throws Exception {
      boolean head = BufferUtil.toString(requestBuffer).toLowerCase().startsWith("head ");
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
      }

      LocalEndPoint endp = this.executeRequest(requestBuffer);
      return endp.waitForResponse(head, time, unit);
   }

   public ByteBuffer getResponse(ByteBuffer requestBuffer, boolean head, long time, TimeUnit unit) throws Exception {
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
      }

      LocalEndPoint endp = this.executeRequest(requestBuffer);
      return endp.waitForResponse(head, time, unit);
   }

   public String getResponse(String rawRequest) throws Exception {
      return this.getResponse(rawRequest, false, 30L, TimeUnit.SECONDS);
   }

   public String getResponse(String rawRequest, long time, TimeUnit unit) throws Exception {
      boolean head = rawRequest.toLowerCase().startsWith("head ");
      ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
      }

      LocalEndPoint endp = this.executeRequest(requestsBuffer);
      return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
   }

   public String getResponse(String rawRequest, boolean head, long time, TimeUnit unit) throws Exception {
      ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
      if (this.LOG.isDebugEnabled()) {
         this.LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
      }

      LocalEndPoint endp = this.executeRequest(requestsBuffer);
      return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
   }

   public class LocalEndPoint extends ByteArrayEndPoint {
      private final CountDownLatch _closed = new CountDownLatch(1);
      private ByteBuffer _responseData;

      public LocalEndPoint() {
         super(LocalConnector.this.getScheduler(), LocalConnector.this.getIdleTimeout());
         this.setGrowOutput(true);
      }

      protected void execute(Runnable task) {
         LocalConnector.this.getExecutor().execute(task);
      }

      public void close() {
         boolean wasOpen = this.isOpen();
         super.close();
         if (wasOpen) {
            this.getConnection().onClose();
            this.onClose();
         }

      }

      public void onClose() {
         LocalConnector.this.onEndPointClosed(this);
         super.onClose();
         this._closed.countDown();
      }

      public void shutdownOutput() {
         super.shutdownOutput();
         this.close();
      }

      public void waitUntilClosed() {
         while(true) {
            if (this.isOpen()) {
               try {
                  if (this._closed.await(10L, TimeUnit.SECONDS)) {
                     continue;
                  }
               } catch (Exception e) {
                  LocalConnector.this.LOG.warn(e);
                  continue;
               }
            }

            return;
         }
      }

      public void waitUntilClosedOrIdleFor(long idleFor, TimeUnit units) {
         Thread.yield();
         int size = this.getOutput().remaining();

         while(this.isOpen()) {
            try {
               if (!this._closed.await(idleFor, units)) {
                  if (size == this.getOutput().remaining()) {
                     if (LocalConnector.this.LOG.isDebugEnabled()) {
                        LocalConnector.this.LOG.debug("idle for {} {}", idleFor, units);
                     }

                     return;
                  }

                  size = this.getOutput().remaining();
               }
            } catch (Exception e) {
               LocalConnector.this.LOG.warn(e);
            }
         }

      }

      public String getResponse() throws Exception {
         return this.getResponse(false, 30L, TimeUnit.SECONDS);
      }

      public String getResponse(boolean head, long time, TimeUnit unit) throws Exception {
         ByteBuffer response = this.waitForResponse(head, time, unit);
         return response != null ? BufferUtil.toString(response) : null;
      }

      public ByteBuffer waitForResponse(boolean head, long time, TimeUnit unit) throws Exception {
         HttpParser.ResponseHandler handler = new HttpParser.ResponseHandler() {
            public void parsedHeader(HttpField field) {
            }

            public boolean contentComplete() {
               return false;
            }

            public boolean messageComplete() {
               return true;
            }

            public boolean headerComplete() {
               return false;
            }

            public int getHeaderCacheSize() {
               return 0;
            }

            public void earlyEOF() {
            }

            public boolean content(ByteBuffer item) {
               return false;
            }

            public void badMessage(int status, String reason) {
            }

            public boolean startResponse(HttpVersion version, int status, String reason) {
               return false;
            }
         };
         HttpParser parser = new HttpParser(handler);
         parser.setHeadResponse(head);
         ByteArrayOutputStream2 bout = new ByteArrayOutputStream2();
         Throwable var8 = null;

         try {
            label184:
            while(true) {
               ByteBuffer chunk;
               if (BufferUtil.hasContent(this._responseData)) {
                  chunk = this._responseData;
               } else {
                  chunk = this.waitForOutput(time, unit);
                  if (BufferUtil.isEmpty(chunk) && (!this.isOpen() || this.isOutputShutdown())) {
                     parser.atEOF();
                     parser.parseNext(BufferUtil.EMPTY_BUFFER);
                     break;
                  }
               }

               while(BufferUtil.hasContent(chunk)) {
                  int pos = chunk.position();
                  boolean complete = parser.parseNext(chunk);
                  if (chunk.position() == pos) {
                     if (BufferUtil.isEmpty(chunk)) {
                        break;
                     }

                     Object var12 = null;
                     return (ByteBuffer)var12;
                  }

                  bout.write(chunk.array(), chunk.arrayOffset() + pos, chunk.position() - pos);
                  if (complete) {
                     if (BufferUtil.hasContent(chunk)) {
                        this._responseData = chunk;
                     }
                     break label184;
                  }
               }
            }

            if (bout.getCount() == 0 && this.isOutputShutdown()) {
               Object var26 = null;
               return (ByteBuffer)var26;
            } else {
               ByteBuffer var25 = ByteBuffer.wrap(bout.getBuf(), 0, bout.getCount());
               return var25;
            }
         } catch (Throwable var23) {
            var8 = var23;
            throw var23;
         } finally {
            if (bout != null) {
               if (var8 != null) {
                  try {
                     bout.close();
                  } catch (Throwable var22) {
                     var8.addSuppressed(var22);
                  }
               } else {
                  bout.close();
               }
            }

         }
      }
   }
}
