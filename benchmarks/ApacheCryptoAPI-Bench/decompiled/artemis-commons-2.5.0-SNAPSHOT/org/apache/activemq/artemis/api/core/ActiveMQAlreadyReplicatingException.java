package org.apache.activemq.artemis.api.core;

public final class ActiveMQAlreadyReplicatingException extends ActiveMQException {
   private static final long serialVersionUID = -7352538521961996152L;

   public ActiveMQAlreadyReplicatingException() {
      super(ActiveMQExceptionType.ALREADY_REPLICATING);
   }

   public ActiveMQAlreadyReplicatingException(String msg) {
      super(ActiveMQExceptionType.ALREADY_REPLICATING, msg);
   }
}
