package org.spark_project.jetty.io;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class NegotiatingClientConnection extends AbstractConnection {
   private static final Logger LOG = Log.getLogger(NegotiatingClientConnection.class);
   private final SSLEngine engine;
   private final ClientConnectionFactory connectionFactory;
   private final Map<String, Object> context;
   private volatile boolean completed;

   protected NegotiatingClientConnection(EndPoint endp, Executor executor, SSLEngine sslEngine, ClientConnectionFactory connectionFactory, Map<String, Object> context) {
      super(endp, executor);
      this.engine = sslEngine;
      this.connectionFactory = connectionFactory;
      this.context = context;
   }

   protected SSLEngine getSSLEngine() {
      return this.engine;
   }

   protected void completed() {
      this.completed = true;
   }

   public void onOpen() {
      super.onOpen();

      try {
         this.getEndPoint().flush(BufferUtil.EMPTY_BUFFER);
         if (this.completed) {
            this.replaceConnection();
         } else {
            this.fillInterested();
         }

      } catch (IOException x) {
         this.close();
         throw new RuntimeIOException(x);
      }
   }

   public void onFillable() {
      int filled;
      do {
         filled = this.fill();
         if (filled == 0 && !this.completed) {
            this.fillInterested();
         }
      } while(filled > 0 && !this.completed);

      if (this.completed) {
         this.replaceConnection();
      }

   }

   private int fill() {
      try {
         return this.getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
      } catch (IOException x) {
         LOG.debug(x);
         this.close();
         return -1;
      }
   }

   private void replaceConnection() {
      EndPoint endPoint = this.getEndPoint();

      try {
         endPoint.upgrade(this.connectionFactory.newConnection(endPoint, this.context));
      } catch (Throwable x) {
         LOG.debug(x);
         this.close();
      }

   }

   public void close() {
      this.getEndPoint().shutdownOutput();
      super.close();
   }
}
