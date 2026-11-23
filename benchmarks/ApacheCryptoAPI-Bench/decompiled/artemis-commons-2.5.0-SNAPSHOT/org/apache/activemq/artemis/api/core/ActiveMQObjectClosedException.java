package org.apache.activemq.artemis.api.core;

public final class ActiveMQObjectClosedException extends ActiveMQException {
   private static final long serialVersionUID = 809024052184914812L;

   public ActiveMQObjectClosedException() {
      super(ActiveMQExceptionType.OBJECT_CLOSED);
   }

   public ActiveMQObjectClosedException(String msg) {
      super(ActiveMQExceptionType.OBJECT_CLOSED, msg);
   }
}
