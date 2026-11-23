package org.apache.activemq.artemis.api.core;

public final class ActiveMQAddressFullException extends ActiveMQException {
   private static final long serialVersionUID = 0L;

   public ActiveMQAddressFullException(String message) {
      super(ActiveMQExceptionType.ADDRESS_FULL, message);
   }

   public ActiveMQAddressFullException() {
      super(ActiveMQExceptionType.ADDRESS_FULL);
   }
}
