package org.apache.activemq.artemis.api.core;

public final class ActiveMQInvalidQueueConfiguration extends ActiveMQException {
   public ActiveMQInvalidQueueConfiguration() {
      super(ActiveMQExceptionType.INVALID_QUEUE_CONFIGURATION);
   }

   public ActiveMQInvalidQueueConfiguration(String msg) {
      super(ActiveMQExceptionType.INVALID_QUEUE_CONFIGURATION, msg);
   }
}
