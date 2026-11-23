package org.spark_project.jetty.io;

import java.io.IOException;
import java.util.Map;
import org.spark_project.jetty.util.component.ContainerLifeCycle;

public interface ClientConnectionFactory {
   String CONNECTOR_CONTEXT_KEY = "client.connector";

   Connection newConnection(EndPoint var1, Map<String, Object> var2) throws IOException;

   default Connection customize(Connection connection, Map<String, Object> context) {
      ContainerLifeCycle connector = (ContainerLifeCycle)context.get("client.connector");
      connector.getBeans(Connection.Listener.class).forEach(connection::addListener);
      return connection;
   }
}
