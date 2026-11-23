package org.apache.activemq.artemis.api.core;

public final class ActiveMQIncompatibleClientServerException extends ActiveMQException {
   private static final long serialVersionUID = -1662999230291452298L;

   public ActiveMQIncompatibleClientServerException() {
      super(ActiveMQExceptionType.INCOMPATIBLE_CLIENT_SERVER_VERSIONS);
   }

   public ActiveMQIncompatibleClientServerException(String msg) {
      super(ActiveMQExceptionType.INCOMPATIBLE_CLIENT_SERVER_VERSIONS, msg);
   }
}
