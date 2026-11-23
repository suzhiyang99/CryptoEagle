package org.apache.activemq.artemis.api.core;

public final class ActiveMQIOErrorException extends ActiveMQException {
   private static final long serialVersionUID = 797277117077787396L;

   public ActiveMQIOErrorException() {
      super(ActiveMQExceptionType.IO_ERROR);
   }

   public ActiveMQIOErrorException(String msg) {
      super(ActiveMQExceptionType.IO_ERROR, msg);
   }

   public ActiveMQIOErrorException(String msg, Throwable cause) {
      super(ActiveMQExceptionType.IO_ERROR, msg, cause);
   }
}
