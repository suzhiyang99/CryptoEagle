package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.util.Callback;

/** @deprecated */
@Deprecated
public class ConnectionPool extends DuplexConnectionPool {
   public ConnectionPool(Destination destination, int maxConnections, Callback requester) {
      super(destination, maxConnections, requester);
   }
}
