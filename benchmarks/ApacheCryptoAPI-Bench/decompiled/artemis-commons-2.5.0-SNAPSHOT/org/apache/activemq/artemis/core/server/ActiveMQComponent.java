package org.apache.activemq.artemis.core.server;

public interface ActiveMQComponent {
   void start() throws Exception;

   void stop() throws Exception;

   boolean isStarted();
}
