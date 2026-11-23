package org.spark_project.jetty.client.api;

import java.io.Closeable;

public interface Connection extends Closeable {
   void send(Request var1, Response.CompleteListener var2);

   void close();

   boolean isClosed();
}
