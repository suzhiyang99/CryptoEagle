package org.apache.spark.deploy.master;

import scala.Enumeration;

public final class WorkerState$ extends Enumeration {
   public static final WorkerState$ MODULE$;
   private final Enumeration.Value ALIVE;
   private final Enumeration.Value DEAD;
   private final Enumeration.Value DECOMMISSIONED;
   private final Enumeration.Value UNKNOWN;

   static {
      new WorkerState$();
   }

   public Enumeration.Value ALIVE() {
      return this.ALIVE;
   }

   public Enumeration.Value DEAD() {
      return this.DEAD;
   }

   public Enumeration.Value DECOMMISSIONED() {
      return this.DECOMMISSIONED;
   }

   public Enumeration.Value UNKNOWN() {
      return this.UNKNOWN;
   }

   private WorkerState$() {
      MODULE$ = this;
      this.ALIVE = this.Value();
      this.DEAD = this.Value();
      this.DECOMMISSIONED = this.Value();
      this.UNKNOWN = this.Value();
   }
}
