package org.apache.spark.scheduler;

import scala.Enumeration;

public final class SchedulingMode$ extends Enumeration {
   public static final SchedulingMode$ MODULE$;
   private final Enumeration.Value FAIR;
   private final Enumeration.Value FIFO;
   private final Enumeration.Value NONE;

   static {
      new SchedulingMode$();
   }

   public Enumeration.Value FAIR() {
      return this.FAIR;
   }

   public Enumeration.Value FIFO() {
      return this.FIFO;
   }

   public Enumeration.Value NONE() {
      return this.NONE;
   }

   private SchedulingMode$() {
      MODULE$ = this;
      this.FAIR = this.Value();
      this.FIFO = this.Value();
      this.NONE = this.Value();
   }
}
