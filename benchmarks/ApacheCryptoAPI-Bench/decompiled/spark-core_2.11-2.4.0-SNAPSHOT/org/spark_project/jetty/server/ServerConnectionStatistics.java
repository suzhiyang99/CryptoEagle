package org.spark_project.jetty.server;

import org.spark_project.jetty.io.ConnectionStatistics;
import org.spark_project.jetty.util.component.Container;

public class ServerConnectionStatistics extends ConnectionStatistics {
   public static void addToAllConnectors(Server server) {
      for(Connector connector : server.getConnectors()) {
         if (connector instanceof Container) {
            ((Container)connector).addBean(new ConnectionStatistics());
         }
      }

   }
}
