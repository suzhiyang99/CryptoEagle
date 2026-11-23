package org.apache.spark.deploy.rest;

public final class RestSubmissionServer$ {
   public static final RestSubmissionServer$ MODULE$;
   private final String PROTOCOL_VERSION;
   private final int SC_UNKNOWN_PROTOCOL_VERSION;

   static {
      new RestSubmissionServer$();
   }

   public String PROTOCOL_VERSION() {
      return this.PROTOCOL_VERSION;
   }

   public int SC_UNKNOWN_PROTOCOL_VERSION() {
      return this.SC_UNKNOWN_PROTOCOL_VERSION;
   }

   private RestSubmissionServer$() {
      MODULE$ = this;
      this.PROTOCOL_VERSION = RestSubmissionClient$.MODULE$.PROTOCOL_VERSION();
      this.SC_UNKNOWN_PROTOCOL_VERSION = 468;
   }
}
