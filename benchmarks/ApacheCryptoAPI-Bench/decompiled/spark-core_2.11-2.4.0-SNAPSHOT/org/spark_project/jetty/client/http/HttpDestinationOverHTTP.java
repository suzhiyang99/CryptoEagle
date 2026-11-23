package org.spark_project.jetty.client.http;

import org.spark_project.jetty.client.HttpClient;
import org.spark_project.jetty.client.HttpExchange;
import org.spark_project.jetty.client.Origin;
import org.spark_project.jetty.client.PoolingHttpDestination;
import org.spark_project.jetty.client.SendFailure;

public class HttpDestinationOverHTTP extends PoolingHttpDestination<HttpConnectionOverHTTP> {
   public HttpDestinationOverHTTP(HttpClient client, Origin origin) {
      super(client, origin);
   }

   protected SendFailure send(HttpConnectionOverHTTP connection, HttpExchange exchange) {
      return connection.send(exchange);
   }
}
