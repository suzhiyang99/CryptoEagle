package org.apache.activemq.artemis.api.core;

public final class ActiveMQSessionCreationException extends ActiveMQException {
   private static final long serialVersionUID = -4486139158452585895L;

   public ActiveMQSessionCreationException() {
      super(ActiveMQExceptionType.SESSION_CREATION_REJECTED);
   }

   public ActiveMQSessionCreationException(String msg) {
      super(ActiveMQExceptionType.SESSION_CREATION_REJECTED, msg);
   }
}
