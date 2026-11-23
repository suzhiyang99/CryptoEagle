package org.apache.activemq.artemis.api.core;

public final class ActiveMQInvalidFilterExpressionException extends ActiveMQException {
   private static final long serialVersionUID = 7188625553939665128L;

   public ActiveMQInvalidFilterExpressionException() {
      super(ActiveMQExceptionType.INVALID_FILTER_EXPRESSION);
   }

   public ActiveMQInvalidFilterExpressionException(String msg) {
      super(ActiveMQExceptionType.INVALID_FILTER_EXPRESSION, msg);
   }
}
