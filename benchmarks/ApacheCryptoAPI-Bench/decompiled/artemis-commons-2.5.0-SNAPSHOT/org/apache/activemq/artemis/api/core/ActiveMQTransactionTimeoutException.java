package org.apache.activemq.artemis.api.core;

public class ActiveMQTransactionTimeoutException extends ActiveMQException {
   public ActiveMQTransactionTimeoutException() {
      super(ActiveMQExceptionType.TRANSACTION_TIMEOUT);
   }

   public ActiveMQTransactionTimeoutException(String message) {
      super(ActiveMQExceptionType.TRANSACTION_TIMEOUT, message);
   }
}
