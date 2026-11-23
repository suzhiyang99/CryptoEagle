package org.apache.activemq.artemis.api.core;

public final class ActiveMQTransactionOutcomeUnknownException extends ActiveMQException {
   private static final long serialVersionUID = 7940794286427650558L;

   public ActiveMQTransactionOutcomeUnknownException() {
      super(ActiveMQExceptionType.TRANSACTION_OUTCOME_UNKNOWN);
   }

   public ActiveMQTransactionOutcomeUnknownException(String msg) {
      super(ActiveMQExceptionType.TRANSACTION_OUTCOME_UNKNOWN, msg);
   }
}
