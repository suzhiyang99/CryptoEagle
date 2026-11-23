package org.spark_project.jetty.client;

import java.util.LinkedHashMap;
import java.util.Map;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;

public class ProtocolHandlers {
   private final Map<String, ProtocolHandler> handlers = new LinkedHashMap();

   protected ProtocolHandlers() {
   }

   public ProtocolHandler put(ProtocolHandler protocolHandler) {
      return (ProtocolHandler)this.handlers.put(protocolHandler.getName(), protocolHandler);
   }

   public ProtocolHandler remove(String name) {
      return (ProtocolHandler)this.handlers.remove(name);
   }

   public void clear() {
      this.handlers.clear();
   }

   public ProtocolHandler find(Request request, Response response) {
      for(ProtocolHandler handler : this.handlers.values()) {
         if (handler.accept(request, response)) {
            return handler;
         }
      }

      return null;
   }
}
