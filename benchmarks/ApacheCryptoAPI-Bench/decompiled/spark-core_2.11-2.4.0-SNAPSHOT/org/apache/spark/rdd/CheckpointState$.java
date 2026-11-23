package org.apache.spark.rdd;

import scala.Enumeration;

public final class CheckpointState$ extends Enumeration {
   public static final CheckpointState$ MODULE$;
   private final Enumeration.Value Initialized;
   private final Enumeration.Value CheckpointingInProgress;
   private final Enumeration.Value Checkpointed;

   static {
      new CheckpointState$();
   }

   public Enumeration.Value Initialized() {
      return this.Initialized;
   }

   public Enumeration.Value CheckpointingInProgress() {
      return this.CheckpointingInProgress;
   }

   public Enumeration.Value Checkpointed() {
      return this.Checkpointed;
   }

   private CheckpointState$() {
      MODULE$ = this;
      this.Initialized = this.Value();
      this.CheckpointingInProgress = this.Value();
      this.Checkpointed = this.Value();
   }
}
