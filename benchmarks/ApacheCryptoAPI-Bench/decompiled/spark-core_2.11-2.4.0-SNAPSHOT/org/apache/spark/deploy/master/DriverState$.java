package org.apache.spark.deploy.master;

import scala.Enumeration;

public final class DriverState$ extends Enumeration {
   public static final DriverState$ MODULE$;
   private final Enumeration.Value SUBMITTED;
   private final Enumeration.Value RUNNING;
   private final Enumeration.Value FINISHED;
   private final Enumeration.Value RELAUNCHING;
   private final Enumeration.Value UNKNOWN;
   private final Enumeration.Value KILLED;
   private final Enumeration.Value FAILED;
   private final Enumeration.Value ERROR;

   static {
      new DriverState$();
   }

   public Enumeration.Value SUBMITTED() {
      return this.SUBMITTED;
   }

   public Enumeration.Value RUNNING() {
      return this.RUNNING;
   }

   public Enumeration.Value FINISHED() {
      return this.FINISHED;
   }

   public Enumeration.Value RELAUNCHING() {
      return this.RELAUNCHING;
   }

   public Enumeration.Value UNKNOWN() {
      return this.UNKNOWN;
   }

   public Enumeration.Value KILLED() {
      return this.KILLED;
   }

   public Enumeration.Value FAILED() {
      return this.FAILED;
   }

   public Enumeration.Value ERROR() {
      return this.ERROR;
   }

   private DriverState$() {
      MODULE$ = this;
      this.SUBMITTED = this.Value();
      this.RUNNING = this.Value();
      this.FINISHED = this.Value();
      this.RELAUNCHING = this.Value();
      this.UNKNOWN = this.Value();
      this.KILLED = this.Value();
      this.FAILED = this.Value();
      this.ERROR = this.Value();
   }
}
