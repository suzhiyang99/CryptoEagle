package org.spark_project.jetty.client.http;

import java.io.IOException;
import java.util.Map;
import org.spark_project.jetty.client.AbstractHttpClientTransport;
import org.spark_project.jetty.client.HttpDestination;
import org.spark_project.jetty.client.Origin;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.Promise;
import org.spark_project.jetty.util.annotation.ManagedObject;

@ManagedObject("The HTTP/1.1 client transport")
public class HttpClientTransportOverHTTP extends AbstractHttpClientTransport {
   public HttpClientTransportOverHTTP() {
      this(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
   }

   public HttpClientTransportOverHTTP(int selectors) {
      super(selectors);
   }

   public HttpDestination newHttpDestination(Origin origin) {
      return new HttpDestinationOverHTTP(this.getHttpClient(), origin);
   }

   public org.spark_project.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      HttpDestination destination = (HttpDestination)context.get("http.destination");
      Promise<Connection> promise = (Promise)context.get("http.connection.promise");
      HttpConnectionOverHTTP connection = this.newHttpConnection(endPoint, destination, promise);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Created {}", connection);
      }

      return this.customize(connection, context);
   }

   protected HttpConnectionOverHTTP newHttpConnection(EndPoint endPoint, HttpDestination destination, Promise<Connection> promise) {
      return new HttpConnectionOverHTTP(endPoint, destination, promise);
   }
}
