package org.apache.activemq.artemis.api.core;

public final class ActiveMQAddressExistsException extends ActiveMQException {
   private static final long serialVersionUID = 3032730450033992367L;

   public ActiveMQAddressExistsException() {
      super(ActiveMQExceptionType.ADDRESS_EXISTS);
   }

   public ActiveMQAddressExistsException(String msg) {
      super(ActiveMQExceptionType.ADDRESS_EXISTS, msg);
   }
}
