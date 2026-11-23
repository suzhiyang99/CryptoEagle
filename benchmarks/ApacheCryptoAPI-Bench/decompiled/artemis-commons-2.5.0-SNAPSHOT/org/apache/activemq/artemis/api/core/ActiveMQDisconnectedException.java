package org.apache.activemq.artemis.api.core;

public final class ActiveMQDisconnectedException extends ActiveMQException {
   private static final long serialVersionUID = 7414966383933311627L;

   public ActiveMQDisconnectedException() {
      super(ActiveMQExceptionType.DISCONNECTED);
   }

   public ActiveMQDisconnectedException(String message) {
      super(ActiveMQExceptionType.DISCONNECTED, message);
   }
}
