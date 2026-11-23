package org.apache.activemq.artemis.api.core;

public final class ActiveMQInterruptedException extends RuntimeException {
   private static final long serialVersionUID = -5744690023549671221L;

   public ActiveMQInterruptedException(Throwable cause) {
      super(cause);
   }

   public ActiveMQInterruptedException(String message) {
      super(message);
   }
}
