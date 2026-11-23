package org.spark_project.jetty.server.session.jmx;

import org.spark_project.jetty.server.handler.AbstractHandlerContainer;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.jmx.AbstractHandlerMBean;
import org.spark_project.jetty.server.session.AbstractSessionManager;
import org.spark_project.jetty.server.session.SessionHandler;

public class AbstractSessionManagerMBean extends AbstractHandlerMBean {
   public AbstractSessionManagerMBean(Object managedObject) {
      super(managedObject);
   }

   public String getObjectContextBasis() {
      if (this._managed != null && this._managed instanceof AbstractSessionManager) {
         AbstractSessionManager manager = (AbstractSessionManager)this._managed;
         String basis = null;
         SessionHandler handler = manager.getSessionHandler();
         if (handler != null) {
            ContextHandler context = (ContextHandler)AbstractHandlerContainer.findContainerOf(handler.getServer(), ContextHandler.class, handler);
            if (context != null) {
               basis = this.getContextName(context);
            }
         }

         if (basis != null) {
            return basis;
         }
      }

      return super.getObjectContextBasis();
   }
}
