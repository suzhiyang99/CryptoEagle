package org.apache.activemq.artemis.api.core;

public final class ActiveMQUnsupportedPacketException extends ActiveMQException {
   private static final long serialVersionUID = -7074681529482463675L;

   public ActiveMQUnsupportedPacketException() {
      super(ActiveMQExceptionType.UNSUPPORTED_PACKET);
   }

   public ActiveMQUnsupportedPacketException(String msg) {
      super(ActiveMQExceptionType.UNSUPPORTED_PACKET, msg);
   }
}
