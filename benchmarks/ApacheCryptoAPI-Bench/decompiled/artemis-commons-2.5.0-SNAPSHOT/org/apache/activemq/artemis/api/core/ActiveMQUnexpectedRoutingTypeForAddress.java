package org.apache.activemq.artemis.api.core;

public final class ActiveMQUnexpectedRoutingTypeForAddress extends ActiveMQException {
   public ActiveMQUnexpectedRoutingTypeForAddress() {
      super(ActiveMQExceptionType.MAX_CONSUMER_LIMIT_EXCEEDED);
   }

   public ActiveMQUnexpectedRoutingTypeForAddress(String msg) {
      super(ActiveMQExceptionType.MAX_CONSUMER_LIMIT_EXCEEDED, msg);
   }
}
