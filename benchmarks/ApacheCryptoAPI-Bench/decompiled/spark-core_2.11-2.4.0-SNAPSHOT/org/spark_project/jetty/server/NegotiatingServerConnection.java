package org.spark_project.jetty.server;

import java.io.IOException;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class NegotiatingServerConnection extends AbstractConnection {
   private static final Logger LOG = Log.getLogger(NegotiatingServerConnection.class);
   private final Connector connector;
   private final SSLEngine engine;
   private final List<String> protocols;
   private final String defaultProtocol;
   private String protocol;

   protected NegotiatingServerConnection(Connector connector, EndPoint endPoint, SSLEngine engine, List<String> protocols, String defaultProtocol) {
      super(endPoint, connector.getExecutor());
      this.connector = connector;
      this.protocols = protocols;
      this.defaultProtocol = defaultProtocol;
      this.engine = engine;
   }

   protected List<String> getProtocols() {
      return this.protocols;
   }

   protected String getDefaultProtocol() {
      return this.defaultProtocol;
   }

   protected Connector getConnector() {
      return this.connector;
   }

   protected SSLEngine getSSLEngine() {
      return this.engine;
   }

   protected String getProtocol() {
      return this.protocol;
   }

   protected void setProtocol(String protocol) {
      this.protocol = protocol;
   }

   public void onOpen() {
      super.onOpen();
      this.fillInterested();
   }

   public void onFillable() {
      int filled = this.fill();
      if (filled == 0) {
         if (this.protocol == null) {
            if (this.engine.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("{} could not negotiate protocol, SSLEngine: {}", this, this.engine);
               }

               this.close();
            } else {
               this.fillInterested();
            }
         } else {
            ConnectionFactory connectionFactory = this.connector.getConnectionFactory(this.protocol);
            if (connectionFactory == null) {
               LOG.info("{} application selected protocol '{}', but no correspondent {} has been configured", this, this.protocol, ConnectionFactory.class.getName());
               this.close();
            } else {
               EndPoint endPoint = this.getEndPoint();
               Connection newConnection = connectionFactory.newConnection(this.connector, endPoint);
               endPoint.upgrade(newConnection);
            }
         }
      } else {
         if (filled >= 0) {
            throw new IllegalStateException();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} detected close on client side", this);
         }

         this.close();
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

   public void close() {
      this.getEndPoint().shutdownOutput();
      super.close();
   }

   public interface CipherDiscriminator {
      boolean isAcceptable(String var1, String var2, String var3);
   }
}
