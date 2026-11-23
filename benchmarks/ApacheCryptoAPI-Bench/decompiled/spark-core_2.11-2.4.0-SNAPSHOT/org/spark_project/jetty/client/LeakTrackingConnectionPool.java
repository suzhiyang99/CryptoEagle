package org.spark_project.jetty.client;

import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.Destination;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.LeakDetector;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class LeakTrackingConnectionPool extends ConnectionPool {
   private static final Logger LOG = Log.getLogger(LeakTrackingConnectionPool.class);
   private final LeakDetector<Connection> leakDetector = new LeakDetector<Connection>() {
      protected void leaked(LeakDetector<Connection>.LeakInfo leakInfo) {
         LeakTrackingConnectionPool.this.leaked(leakInfo);
      }
   };

   public LeakTrackingConnectionPool(Destination destination, int maxConnections, Callback requester) {
      super(destination, maxConnections, requester);
      this.start();
   }

   private void start() {
      try {
         this.leakDetector.start();
      } catch (Exception x) {
         throw new RuntimeException(x);
      }
   }

   public void close() {
      this.stop();
      super.close();
   }

   private void stop() {
      try {
         this.leakDetector.stop();
      } catch (Exception x) {
         throw new RuntimeException(x);
      }
   }

   protected void acquired(Connection connection) {
      if (!this.leakDetector.acquired(connection)) {
         LOG.info("Connection {}@{} not tracked", connection, this.leakDetector.id(connection));
      }

   }

   protected void released(Connection connection) {
      if (!this.leakDetector.released(connection)) {
         LOG.info("Connection {}@{} released but not acquired", connection, this.leakDetector.id(connection));
      }

   }

   protected void leaked(LeakDetector.LeakInfo leakInfo) {
      LOG.info("Connection " + leakInfo.getResourceDescription() + " leaked at:", leakInfo.getStackFrames());
   }
}
