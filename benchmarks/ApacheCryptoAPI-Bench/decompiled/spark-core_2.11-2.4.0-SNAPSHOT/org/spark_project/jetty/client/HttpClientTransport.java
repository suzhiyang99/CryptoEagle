package org.spark_project.jetty.client;

import java.net.InetSocketAddress;
import java.util.Map;
import org.spark_project.jetty.io.ClientConnectionFactory;

public interface HttpClientTransport extends ClientConnectionFactory {
   String HTTP_DESTINATION_CONTEXT_KEY = "http.destination";
   String HTTP_CONNECTION_PROMISE_CONTEXT_KEY = "http.connection.promise";

   void setHttpClient(HttpClient var1);

   HttpDestination newHttpDestination(Origin var1);

   void connect(InetSocketAddress var1, Map<String, Object> var2);
}
