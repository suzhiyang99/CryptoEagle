package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class StageCancelled$ extends AbstractFunction2<Object, Option<String>, StageCancelled> implements Serializable {
   public static final StageCancelled$ MODULE$;

   static {
      new StageCancelled$();
   }

   public final String toString() {
      return "StageCancelled";
   }

   public StageCancelled apply(int stageId, Option<String> reason) {
      return new StageCancelled(stageId, reason);
   }

   public Option<Tuple2<Object, Option<String>>> unapply(StageCancelled x$0) {
      return (Option<Tuple2<Object, Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToInteger(x$0.stageId()), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StageCancelled$() {
      MODULE$ = this;
   }
}
