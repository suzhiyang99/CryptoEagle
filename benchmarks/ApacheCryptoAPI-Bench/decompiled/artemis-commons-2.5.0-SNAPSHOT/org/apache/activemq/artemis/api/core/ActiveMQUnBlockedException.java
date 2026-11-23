package org.apache.activemq.artemis.api.core;

public final class ActiveMQUnBlockedException extends ActiveMQException {
   private static final long serialVersionUID = -4507889261891160608L;

   public ActiveMQUnBlockedException() {
      super(ActiveMQExceptionType.UNBLOCKED);
   }

   public ActiveMQUnBlockedException(String msg) {
      super(ActiveMQExceptionType.UNBLOCKED, msg);
   }
}
