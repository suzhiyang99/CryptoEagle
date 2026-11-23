package org.apache.activemq.artemis.api.core;

public final class ActiveMQDuplicateMetaDataException extends ActiveMQException {
   private static final long serialVersionUID = 7877182872143004058L;

   public ActiveMQDuplicateMetaDataException() {
      super(ActiveMQExceptionType.DUPLICATE_METADATA);
   }

   public ActiveMQDuplicateMetaDataException(String msg) {
      super(ActiveMQExceptionType.DUPLICATE_METADATA, msg);
   }
}
