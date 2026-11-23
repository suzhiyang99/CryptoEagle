package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class AccumulatorMetadata$ extends AbstractFunction3<Object, Option<String>, Object, AccumulatorMetadata> implements Serializable {
   public static final AccumulatorMetadata$ MODULE$;

   static {
      new AccumulatorMetadata$();
   }

   public final String toString() {
      return "AccumulatorMetadata";
   }

   public AccumulatorMetadata apply(long id, Option<String> name, boolean countFailedValues) {
      return new AccumulatorMetadata(id, name, countFailedValues);
   }

   public Option<Tuple3<Object, Option<String>, Object>> unapply(AccumulatorMetadata x$0) {
      return (Option<Tuple3<Object, Option<String>, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.id()), x$0.name(), BoxesRunTime.boxToBoolean(x$0.countFailedValues()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AccumulatorMetadata$() {
      MODULE$ = this;
   }
}
