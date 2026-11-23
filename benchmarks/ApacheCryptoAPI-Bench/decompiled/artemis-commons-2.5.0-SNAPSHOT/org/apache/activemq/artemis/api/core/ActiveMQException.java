package org.apache.activemq.artemis.api.core;

public class ActiveMQException extends Exception {
   private static final long serialVersionUID = -4802014152804997417L;
   private final ActiveMQExceptionType type;

   public ActiveMQException() {
      this.type = ActiveMQExceptionType.GENERIC_EXCEPTION;
   }

   public ActiveMQException(String msg) {
      super(msg);
      this.type = ActiveMQExceptionType.GENERIC_EXCEPTION;
   }

   public ActiveMQException(String msg, ActiveMQExceptionType t) {
      super(msg);
      this.type = t;
   }

   public ActiveMQException(String message, Throwable t, ActiveMQExceptionType type) {
      super(message, t);
      this.type = type;
   }

   public ActiveMQException(int code, String msg) {
      super(msg);
      this.type = ActiveMQExceptionType.getType(code);
   }

   public ActiveMQException(ActiveMQExceptionType type, String msg) {
      super(msg);
      this.type = type;
   }

   public ActiveMQException(ActiveMQExceptionType type) {
      this.type = type;
   }

   public ActiveMQException(ActiveMQExceptionType type, String message, Throwable t) {
      super(message, t);
      this.type = type;
   }

   public ActiveMQExceptionType getType() {
      return this.type;
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[errorType=" + this.type + " message=" + this.getMessage() + "]";
   }
}
