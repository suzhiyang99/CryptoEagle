package org.apache.activemq.artemis.api.core;

public final class ActiveMQRemoteDisconnectException extends ActiveMQException {
   public ActiveMQRemoteDisconnectException() {
      super(ActiveMQExceptionType.REMOTE_DISCONNECT);
   }

   public ActiveMQRemoteDisconnectException(String msg) {
      super(ActiveMQExceptionType.REMOTE_DISCONNECT, msg);
   }
}
