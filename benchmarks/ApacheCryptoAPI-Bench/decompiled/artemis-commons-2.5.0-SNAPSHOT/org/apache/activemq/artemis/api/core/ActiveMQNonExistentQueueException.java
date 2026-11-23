package org.apache.activemq.artemis.api.core;

public final class ActiveMQNonExistentQueueException extends ActiveMQException {
   private static final long serialVersionUID = -8199298881947523607L;

   public ActiveMQNonExistentQueueException() {
      super(ActiveMQExceptionType.QUEUE_DOES_NOT_EXIST);
   }

   public ActiveMQNonExistentQueueException(String msg) {
      super(ActiveMQExceptionType.QUEUE_DOES_NOT_EXIST, msg);
   }
}
