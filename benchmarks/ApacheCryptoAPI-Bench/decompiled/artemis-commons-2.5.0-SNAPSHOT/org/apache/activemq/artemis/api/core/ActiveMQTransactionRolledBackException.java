package org.apache.activemq.artemis.api.core;

public final class ActiveMQTransactionRolledBackException extends ActiveMQException {
   private static final long serialVersionUID = 5823412198677126300L;

   public ActiveMQTransactionRolledBackException() {
      super(ActiveMQExceptionType.TRANSACTION_ROLLED_BACK);
   }

   public ActiveMQTransactionRolledBackException(String msg) {
      super(ActiveMQExceptionType.TRANSACTION_ROLLED_BACK, msg);
   }
}
