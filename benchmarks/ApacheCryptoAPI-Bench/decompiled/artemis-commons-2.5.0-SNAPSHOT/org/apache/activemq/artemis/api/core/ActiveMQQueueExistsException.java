package org.apache.activemq.artemis.api.core;

public final class ActiveMQQueueExistsException extends ActiveMQException {
   private static final long serialVersionUID = -405552292451883063L;

   public ActiveMQQueueExistsException() {
      super(ActiveMQExceptionType.QUEUE_EXISTS);
   }

   public ActiveMQQueueExistsException(String msg) {
      super(ActiveMQExceptionType.QUEUE_EXISTS, msg);
   }
}
