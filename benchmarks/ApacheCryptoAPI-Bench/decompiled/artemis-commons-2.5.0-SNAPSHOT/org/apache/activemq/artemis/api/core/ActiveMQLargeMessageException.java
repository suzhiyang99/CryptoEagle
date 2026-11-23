package org.apache.activemq.artemis.api.core;

public final class ActiveMQLargeMessageException extends ActiveMQException {
   private static final long serialVersionUID = 1087867463974768491L;

   public ActiveMQLargeMessageException() {
      super(ActiveMQExceptionType.LARGE_MESSAGE_ERROR_BODY);
   }

   public ActiveMQLargeMessageException(String msg) {
      super(ActiveMQExceptionType.LARGE_MESSAGE_ERROR_BODY, msg);
   }
}
