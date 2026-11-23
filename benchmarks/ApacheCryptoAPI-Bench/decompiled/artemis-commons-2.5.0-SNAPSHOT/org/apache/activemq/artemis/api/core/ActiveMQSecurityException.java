package org.apache.activemq.artemis.api.core;

public final class ActiveMQSecurityException extends ActiveMQException {
   private static final long serialVersionUID = 3291210307590756881L;

   public ActiveMQSecurityException() {
      super(ActiveMQExceptionType.SECURITY_EXCEPTION);
   }

   public ActiveMQSecurityException(String msg) {
      super(ActiveMQExceptionType.SECURITY_EXCEPTION, msg);
   }
}
