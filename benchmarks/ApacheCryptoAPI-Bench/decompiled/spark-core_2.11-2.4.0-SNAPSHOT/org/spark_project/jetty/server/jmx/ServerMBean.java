package org.spark_project.jetty.server.jmx;

import org.spark_project.jetty.jmx.ObjectMBean;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;

@ManagedObject("MBean Wrapper for Server")
public class ServerMBean extends ObjectMBean {
   private final long startupTime = System.currentTimeMillis();
   private final Server server;

   public ServerMBean(Object managedObject) {
      super(managedObject);
      this.server = (Server)managedObject;
   }

   @ManagedAttribute("contexts on this server")
   public Handler[] getContexts() {
      return this.server.getChildHandlersByClass(ContextHandler.class);
   }

   @ManagedAttribute("the startup time since January 1st, 1970 (in ms)")
   public long getStartupTime() {
      return this.startupTime;
   }
}
