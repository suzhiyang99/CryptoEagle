package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class SlaveLost$ extends AbstractFunction2<String, Object, SlaveLost> implements Serializable {
   public static final SlaveLost$ MODULE$;

   static {
      new SlaveLost$();
   }

   public final String toString() {
      return "SlaveLost";
   }

   public SlaveLost apply(String _message, boolean workerLost) {
      return new SlaveLost(_message, workerLost);
   }

   public Option<Tuple2<String, Object>> unapply(SlaveLost x$0) {
      return (Option<Tuple2<String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0._message(), BoxesRunTime.boxToBoolean(x$0.workerLost()))));
   }

   public String apply$default$1() {
      return "Slave lost";
   }

   public boolean apply$default$2() {
      return false;
   }

   public String $lessinit$greater$default$1() {
      return "Slave lost";
   }

   public boolean $lessinit$greater$default$2() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SlaveLost$() {
      MODULE$ = this;
   }
}
