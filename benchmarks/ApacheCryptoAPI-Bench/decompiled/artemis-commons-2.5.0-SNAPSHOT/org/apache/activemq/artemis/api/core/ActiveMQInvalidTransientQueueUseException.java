package org.apache.activemq.artemis.api.core;

public final class ActiveMQInvalidTransientQueueUseException extends ActiveMQException {
   private static final long serialVersionUID = -405552292451883063L;

   public ActiveMQInvalidTransientQueueUseException() {
      super(ActiveMQExceptionType.INVALID_TRANSIENT_QUEUE_USE);
   }

   public ActiveMQInvalidTransientQueueUseException(String msg) {
      super(ActiveMQExceptionType.INVALID_TRANSIENT_QUEUE_USE, msg);
   }
}
