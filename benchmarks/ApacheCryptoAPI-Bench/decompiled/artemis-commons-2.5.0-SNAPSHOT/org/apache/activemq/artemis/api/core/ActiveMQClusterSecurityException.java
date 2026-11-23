package org.apache.activemq.artemis.api.core;

public final class ActiveMQClusterSecurityException extends ActiveMQException {
   private static final long serialVersionUID = -5890578849781297933L;

   public ActiveMQClusterSecurityException() {
      super(ActiveMQExceptionType.CLUSTER_SECURITY_EXCEPTION);
   }

   public ActiveMQClusterSecurityException(String msg) {
      super(ActiveMQExceptionType.CLUSTER_SECURITY_EXCEPTION, msg);
   }
}
