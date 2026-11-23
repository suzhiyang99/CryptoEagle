package org.apache.spark.scheduler.local;

import scala.Serializable;
import scala.runtime.AbstractFunction0;

public final class StopExecutor$ extends AbstractFunction0<StopExecutor> implements Serializable {
   public static final StopExecutor$ MODULE$;

   static {
      new StopExecutor$();
   }

   public final String toString() {
      return "StopExecutor";
   }

   public StopExecutor apply() {
      return new StopExecutor();
   }

   public boolean unapply(StopExecutor x$0) {
      return x$0 != null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StopExecutor$() {
      MODULE$ = this;
   }
}
