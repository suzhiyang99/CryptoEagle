package org.apache.activemq.artemis.api.core;

public final class ActiveMQConnectionTimedOutException extends ActiveMQException {
   private static final long serialVersionUID = 3244233758084830372L;

   public ActiveMQConnectionTimedOutException() {
      super(ActiveMQExceptionType.CONNECTION_TIMEDOUT);
   }

   public ActiveMQConnectionTimedOutException(String msg) {
      super(ActiveMQExceptionType.CONNECTION_TIMEDOUT, msg);
   }
}
