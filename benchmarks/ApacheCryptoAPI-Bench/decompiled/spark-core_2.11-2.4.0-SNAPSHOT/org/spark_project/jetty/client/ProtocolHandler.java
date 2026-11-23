package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;

public interface ProtocolHandler {
   String getName();

   boolean accept(Request var1, Response var2);

   Response.Listener getResponseListener();
}
