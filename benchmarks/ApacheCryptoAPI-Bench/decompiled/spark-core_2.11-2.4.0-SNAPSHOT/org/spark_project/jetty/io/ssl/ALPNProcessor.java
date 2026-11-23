package org.spark_project.jetty.io.ssl;

import java.util.List;
import javax.net.ssl.SSLEngine;

public interface ALPNProcessor {
   public interface Server {
      Server NOOP = new Server() {
      };

      default void configure(SSLEngine sslEngine) {
      }
   }

   public interface Client {
      Client NOOP = new Client() {
      };

      default void configure(SSLEngine sslEngine, List<String> protocols) {
      }

      default void process(SSLEngine sslEngine) {
      }
   }
}
