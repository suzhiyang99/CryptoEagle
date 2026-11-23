package org.apache.spark.rpc.netty;

public final class RpcEndpointVerifier$ {
   public static final RpcEndpointVerifier$ MODULE$;
   private final String NAME;

   static {
      new RpcEndpointVerifier$();
   }

   public String NAME() {
      return this.NAME;
   }

   private RpcEndpointVerifier$() {
      MODULE$ = this;
      this.NAME = "endpoint-verifier";
   }
}
