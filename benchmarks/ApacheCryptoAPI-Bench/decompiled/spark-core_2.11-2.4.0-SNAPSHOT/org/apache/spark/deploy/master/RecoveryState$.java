package org.apache.spark.deploy.master;

import scala.Enumeration;

public final class RecoveryState$ extends Enumeration {
   public static final RecoveryState$ MODULE$;
   private final Enumeration.Value STANDBY;
   private final Enumeration.Value ALIVE;
   private final Enumeration.Value RECOVERING;
   private final Enumeration.Value COMPLETING_RECOVERY;

   static {
      new RecoveryState$();
   }

   public Enumeration.Value STANDBY() {
      return this.STANDBY;
   }

   public Enumeration.Value ALIVE() {
      return this.ALIVE;
   }

   public Enumeration.Value RECOVERING() {
      return this.RECOVERING;
   }

   public Enumeration.Value COMPLETING_RECOVERY() {
      return this.COMPLETING_RECOVERY;
   }

   private RecoveryState$() {
      MODULE$ = this;
      this.STANDBY = this.Value();
      this.ALIVE = this.Value();
      this.RECOVERING = this.Value();
      this.COMPLETING_RECOVERY = this.Value();
   }
}
