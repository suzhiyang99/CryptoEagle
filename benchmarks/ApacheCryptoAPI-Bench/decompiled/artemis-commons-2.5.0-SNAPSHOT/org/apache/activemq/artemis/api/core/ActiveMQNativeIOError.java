package org.apache.activemq.artemis.api.core;

public final class ActiveMQNativeIOError extends ActiveMQException {
   private static final long serialVersionUID = 2355120980683293085L;

   public ActiveMQNativeIOError() {
      super(ActiveMQExceptionType.NATIVE_ERROR_CANT_INITIALIZE_AIO);
   }

   public ActiveMQNativeIOError(String msg) {
      super(ActiveMQExceptionType.NATIVE_ERROR_CANT_INITIALIZE_AIO, msg);
   }

   public ActiveMQNativeIOError(String msg, Throwable e) {
      super(ActiveMQExceptionType.NATIVE_ERROR_CANT_INITIALIZE_AIO, msg, e);
   }
}
