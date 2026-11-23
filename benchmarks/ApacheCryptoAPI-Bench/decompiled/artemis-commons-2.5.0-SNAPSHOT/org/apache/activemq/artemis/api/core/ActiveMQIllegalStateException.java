package org.apache.activemq.artemis.api.core;

public final class ActiveMQIllegalStateException extends ActiveMQException {
   private static final long serialVersionUID = -4480125401057788511L;

   public ActiveMQIllegalStateException() {
      super(ActiveMQExceptionType.ILLEGAL_STATE);
   }

   public ActiveMQIllegalStateException(String message) {
      super(ActiveMQExceptionType.ILLEGAL_STATE, message);
   }
}
