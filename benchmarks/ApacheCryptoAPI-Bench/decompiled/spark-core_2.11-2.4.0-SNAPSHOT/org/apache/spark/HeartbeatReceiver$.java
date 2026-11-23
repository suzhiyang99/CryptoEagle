package org.apache.spark;

public final class HeartbeatReceiver$ {
   public static final HeartbeatReceiver$ MODULE$;
   private final String ENDPOINT_NAME;

   static {
      new HeartbeatReceiver$();
   }

   public String ENDPOINT_NAME() {
      return this.ENDPOINT_NAME;
   }

   private HeartbeatReceiver$() {
      MODULE$ = this;
      this.ENDPOINT_NAME = "HeartbeatReceiver";
   }
}
