package org.apache.activemq.artemis.api.core;

public final class ActiveMQInterceptorRejectedPacketException extends ActiveMQException {
   private static final long serialVersionUID = -5798841227645281815L;

   public ActiveMQInterceptorRejectedPacketException() {
      super(ActiveMQExceptionType.INTERCEPTOR_REJECTED_PACKET);
   }

   public ActiveMQInterceptorRejectedPacketException(String msg) {
      super(ActiveMQExceptionType.INTERCEPTOR_REJECTED_PACKET, msg);
   }
}
