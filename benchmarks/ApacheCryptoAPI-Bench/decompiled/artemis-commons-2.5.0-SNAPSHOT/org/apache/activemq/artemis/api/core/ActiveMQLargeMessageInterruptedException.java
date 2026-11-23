package org.apache.activemq.artemis.api.core;

public class ActiveMQLargeMessageInterruptedException extends ActiveMQException {
   private static final long serialVersionUID = 0L;

   public ActiveMQLargeMessageInterruptedException(String message) {
      super(ActiveMQExceptionType.LARGE_MESSAGE_INTERRUPTED, message);
   }

   public ActiveMQLargeMessageInterruptedException() {
      super(ActiveMQExceptionType.LARGE_MESSAGE_INTERRUPTED);
   }
}
