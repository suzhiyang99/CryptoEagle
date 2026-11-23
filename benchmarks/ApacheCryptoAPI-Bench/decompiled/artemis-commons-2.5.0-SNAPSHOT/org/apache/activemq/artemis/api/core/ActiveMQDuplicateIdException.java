package org.apache.activemq.artemis.api.core;

public final class ActiveMQDuplicateIdException extends ActiveMQException {
   private static final long serialVersionUID = -4302979339865777119L;

   public ActiveMQDuplicateIdException() {
      super(ActiveMQExceptionType.DUPLICATE_ID_REJECTED);
   }

   public ActiveMQDuplicateIdException(String message) {
      super(ActiveMQExceptionType.DUPLICATE_ID_REJECTED, message);
   }
}
