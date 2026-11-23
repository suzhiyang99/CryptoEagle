package org.apache.activemq.artemis.api.core;

public final class ActiveMQQueueMaxConsumerLimitReached extends ActiveMQException {
   public ActiveMQQueueMaxConsumerLimitReached() {
      super(ActiveMQExceptionType.MAX_CONSUMER_LIMIT_EXCEEDED);
   }

   public ActiveMQQueueMaxConsumerLimitReached(String msg) {
      super(ActiveMQExceptionType.MAX_CONSUMER_LIMIT_EXCEEDED, msg);
   }
}
