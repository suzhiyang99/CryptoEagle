package org.apache.spark.deploy.rest;

import scala.Serializable;

public final class SubmitRestProtocolException$ implements Serializable {
   public static final SubmitRestProtocolException$ MODULE$;

   static {
      new SubmitRestProtocolException$();
   }

   public Throwable $lessinit$greater$default$2() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SubmitRestProtocolException$() {
      MODULE$ = this;
   }
}
