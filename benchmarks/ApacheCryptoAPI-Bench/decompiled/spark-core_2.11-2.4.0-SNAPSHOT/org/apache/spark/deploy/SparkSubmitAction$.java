package org.apache.spark.deploy;

import scala.Enumeration;

public final class SparkSubmitAction$ extends Enumeration {
   public static final SparkSubmitAction$ MODULE$;
   private final Enumeration.Value SUBMIT;
   private final Enumeration.Value KILL;
   private final Enumeration.Value REQUEST_STATUS;

   static {
      new SparkSubmitAction$();
   }

   public Enumeration.Value SUBMIT() {
      return this.SUBMIT;
   }

   public Enumeration.Value KILL() {
      return this.KILL;
   }

   public Enumeration.Value REQUEST_STATUS() {
      return this.REQUEST_STATUS;
   }

   private SparkSubmitAction$() {
      MODULE$ = this;
      this.SUBMIT = this.Value();
      this.KILL = this.Value();
      this.REQUEST_STATUS = this.Value();
   }
}
