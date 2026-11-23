package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Connector;
import org.spark_project.jetty.server.NetworkConnector;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ShutdownHandler extends HandlerWrapper {
   private static final Logger LOG = Log.getLogger(ShutdownHandler.class);
   private final String _shutdownToken;
   private boolean _sendShutdownAtStart;
   private boolean _exitJvm;

   /** @deprecated */
   @Deprecated
   public ShutdownHandler(Server server, String shutdownToken) {
      this(shutdownToken);
   }

   public ShutdownHandler(String shutdownToken) {
      this(shutdownToken, false, false);
   }

   public ShutdownHandler(String shutdownToken, boolean exitJVM, boolean sendShutdownAtStart) {
      this._exitJvm = false;
      this._shutdownToken = shutdownToken;
      this.setExitJvm(exitJVM);
      this.setSendShutdownAtStart(sendShutdownAtStart);
   }

   public void sendShutdown() throws IOException {
      URL url = new URL(this.getServerUrl() + "/shutdown?token=" + this._shutdownToken);

      try {
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("POST");
         connection.getResponseCode();
         LOG.info("Shutting down " + url + ": " + connection.getResponseCode() + " " + connection.getResponseMessage());
      } catch (SocketException var3) {
         LOG.debug("Not running");
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   private String getServerUrl() {
      NetworkConnector connector = null;

      for(Connector c : this.getServer().getConnectors()) {
         if (c instanceof NetworkConnector) {
            connector = (NetworkConnector)c;
            break;
         }
      }

      return connector == null ? "http://localhost" : "http://localhost:" + connector.getPort();
   }

   protected void doStart() throws Exception {
      super.doStart();
      if (this._sendShutdownAtStart) {
         this.sendShutdown();
      }

   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (!target.equals("/shutdown")) {
         super.handle(target, baseRequest, request, response);
      } else if (!request.getMethod().equals("POST")) {
         response.sendError(400);
      } else if (!this.hasCorrectSecurityToken(request)) {
         LOG.warn("Unauthorized tokenless shutdown attempt from " + request.getRemoteAddr());
         response.sendError(401);
      } else if (!this.requestFromLocalhost(baseRequest)) {
         LOG.warn("Unauthorized non-loopback shutdown attempt from " + request.getRemoteAddr());
         response.sendError(401);
      } else {
         LOG.info("Shutting down by request from " + request.getRemoteAddr());
         this.doShutdown(baseRequest, response);
      }
   }

   protected void doShutdown(Request baseRequest, HttpServletResponse response) throws IOException {
      for(Connector connector : this.getServer().getConnectors()) {
         connector.shutdown();
      }

      response.sendError(200, "Connectors closed, commencing full shutdown");
      baseRequest.setHandled(true);
      final Server server = this.getServer();
      (new Thread() {
         public void run() {
            try {
               ShutdownHandler.this.shutdownServer(server);
            } catch (InterruptedException e) {
               ShutdownHandler.LOG.ignore(e);
            } catch (Exception e) {
               throw new RuntimeException("Shutting down server", e);
            }

         }
      }).start();
   }

   private boolean requestFromLocalhost(Request request) {
      InetSocketAddress addr = request.getRemoteInetSocketAddress();
      return addr == null ? false : addr.getAddress().isLoopbackAddress();
   }

   private boolean hasCorrectSecurityToken(HttpServletRequest request) {
      String tok = request.getParameter("token");
      if (LOG.isDebugEnabled()) {
         LOG.debug("Token: {}", tok);
      }

      return this._shutdownToken.equals(tok);
   }

   private void shutdownServer(Server server) throws Exception {
      server.stop();
      if (this._exitJvm) {
         System.exit(0);
      }

   }

   public void setExitJvm(boolean exitJvm) {
      this._exitJvm = exitJvm;
   }

   public boolean isSendShutdownAtStart() {
      return this._sendShutdownAtStart;
   }

   public void setSendShutdownAtStart(boolean sendShutdownAtStart) {
      this._sendShutdownAtStart = sendShutdownAtStart;
   }

   public String getShutdownToken() {
      return this._shutdownToken;
   }

   public boolean isExitJvm() {
      return this._exitJvm;
   }
}
