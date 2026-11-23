package org.apache.activemq.artemis.api.core;

public final class ActiveMQDeleteAddressException extends ActiveMQException {
   public ActiveMQDeleteAddressException() {
      super(ActiveMQExceptionType.DELETE_ADDRESS_ERROR);
   }

   public ActiveMQDeleteAddressException(String msg) {
      super(ActiveMQExceptionType.DELETE_ADDRESS_ERROR, msg);
   }
}
