package org.apache.spark.deploy.master;

import scala.Enumeration;

public final class ApplicationState$ extends Enumeration {
   public static final ApplicationState$ MODULE$;
   private final Enumeration.Value WAITING;
   private final Enumeration.Value RUNNING;
   private final Enumeration.Value FINISHED;
   private final Enumeration.Value FAILED;
   private final Enumeration.Value KILLED;
   private final Enumeration.Value UNKNOWN;

   static {
      new ApplicationState$();
   }

   public Enumeration.Value WAITING() {
      return this.WAITING;
   }

   public Enumeration.Value RUNNING() {
      return this.RUNNING;
   }

   public Enumeration.Value FINISHED() {
      return this.FINISHED;
   }

   public Enumeration.Value FAILED() {
      return this.FAILED;
   }

   public Enumeration.Value KILLED() {
      return this.KILLED;
   }

   public Enumeration.Value UNKNOWN() {
      return this.UNKNOWN;
   }

   private ApplicationState$() {
      MODULE$ = this;
      this.WAITING = this.Value();
      this.RUNNING = this.Value();
      this.FINISHED = this.Value();
      this.FAILED = this.Value();
      this.KILLED = this.Value();
      this.UNKNOWN = this.Value();
   }
}
