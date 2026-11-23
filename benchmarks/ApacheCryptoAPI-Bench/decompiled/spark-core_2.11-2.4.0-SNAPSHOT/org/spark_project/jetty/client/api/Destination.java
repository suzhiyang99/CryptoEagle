package org.spark_project.jetty.client.api;

import org.spark_project.jetty.util.Promise;

public interface Destination {
   String getScheme();

   String getHost();

   int getPort();

   void newConnection(Promise<Connection> var1);
}
