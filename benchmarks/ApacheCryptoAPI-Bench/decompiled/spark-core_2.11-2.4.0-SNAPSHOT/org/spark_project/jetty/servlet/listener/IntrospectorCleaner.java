package org.spark_project.jetty.servlet.listener;

import java.beans.Introspector;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IntrospectorCleaner implements ServletContextListener {
   public void contextInitialized(ServletContextEvent sce) {
   }

   public void contextDestroyed(ServletContextEvent sce) {
      Introspector.flushCaches();
   }
}
