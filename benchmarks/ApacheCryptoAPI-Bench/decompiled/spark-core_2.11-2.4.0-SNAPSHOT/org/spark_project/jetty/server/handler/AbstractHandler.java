package org.spark_project.jetty.server.handler;

import java.io.IOException;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Jetty Handler")
public abstract class AbstractHandler extends ContainerLifeCycle implements Handler {
   private static final Logger LOG = Log.getLogger(AbstractHandler.class);
   private Server _server;

   protected void doStart() throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("starting {}", this);
      }

      if (this._server == null) {
         LOG.warn("No Server set for {}", this);
      }

      super.doStart();
   }

   protected void doStop() throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("stopping {}", this);
      }

      super.doStop();
   }

   public void setServer(Server server) {
      if (this._server != server) {
         if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
         } else {
            this._server = server;
         }
      }
   }

   public Server getServer() {
      return this._server;
   }

   public void destroy() {
      if (!this.isStopped()) {
         throw new IllegalStateException("!STOPPED");
      } else {
         super.destroy();
      }
   }

   public void dumpThis(Appendable out) throws IOException {
      out.append(this.toString()).append(" - ").append(this.getState()).append('\n');
   }
}
