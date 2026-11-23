package org.apache.activemq.artemis.api.core;

public final class ActiveMQNotConnectedException extends ActiveMQException {
   private static final long serialVersionUID = -3489189971813613325L;

   public ActiveMQNotConnectedException(String message) {
      super(ActiveMQExceptionType.NOT_CONNECTED, message);
   }

   public ActiveMQNotConnectedException() {
      super(ActiveMQExceptionType.NOT_CONNECTED);
   }
}
