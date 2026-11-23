package org.apache.activemq.artemis.api.core;

public final class ActiveMQAddressDoesNotExistException extends ActiveMQException {
   public ActiveMQAddressDoesNotExistException() {
      super(ActiveMQExceptionType.ADDRESS_DOES_NOT_EXIST);
   }

   public ActiveMQAddressDoesNotExistException(String msg) {
      super(ActiveMQExceptionType.ADDRESS_DOES_NOT_EXIST, msg);
   }
}
