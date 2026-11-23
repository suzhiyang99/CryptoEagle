package org.apache.activemq.artemis.api.core;

public final class ActiveMQInternalErrorException extends ActiveMQException {
   private static final long serialVersionUID = -5987814047521530695L;

   public ActiveMQInternalErrorException() {
      super(ActiveMQExceptionType.INTERNAL_ERROR);
   }

   public ActiveMQInternalErrorException(String msg) {
      super(ActiveMQExceptionType.INTERNAL_ERROR, msg);
   }

   public ActiveMQInternalErrorException(String message, Exception e) {
      super((ActiveMQExceptionType)ActiveMQExceptionType.INTERNAL_ERROR, (String)message, (Throwable)e);
   }

   public ActiveMQInternalErrorException(String message, Throwable t) {
      super(ActiveMQExceptionType.INTERNAL_ERROR, message, t);
   }
}
