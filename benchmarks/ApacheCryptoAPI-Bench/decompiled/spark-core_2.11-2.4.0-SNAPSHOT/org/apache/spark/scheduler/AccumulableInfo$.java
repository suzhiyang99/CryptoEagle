package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.runtime.BoxesRunTime;

public final class AccumulableInfo$ implements Serializable {
   public static final AccumulableInfo$ MODULE$;

   static {
      new AccumulableInfo$();
   }

   /** @deprecated */
   public AccumulableInfo apply(long id, String name, Option<String> update, String value, boolean internal) {
      return new AccumulableInfo(id, Option$.MODULE$.apply(name), update, Option$.MODULE$.apply(value), internal, false, this.$lessinit$greater$default$7());
   }

   /** @deprecated */
   public AccumulableInfo apply(long id, String name, Option<String> update, String value) {
      return new AccumulableInfo(id, Option$.MODULE$.apply(name), update, Option$.MODULE$.apply(value), false, false, this.$lessinit$greater$default$7());
   }

   /** @deprecated */
   public AccumulableInfo apply(long id, String name, String value) {
      return new AccumulableInfo(id, Option$.MODULE$.apply(name), None$.MODULE$, Option$.MODULE$.apply(value), false, false, this.$lessinit$greater$default$7());
   }

   public Option<String> apply$default$7() {
      return None$.MODULE$;
   }

   public AccumulableInfo apply(long id, Option<String> name, Option<Object> update, Option<Object> value, boolean internal, boolean countFailedValues, Option<String> metadata) {
      return new AccumulableInfo(id, name, update, value, internal, countFailedValues, metadata);
   }

   public Option<Tuple7<Object, Option<String>, Option<Object>, Option<Object>, Object, Object, Option<String>>> unapply(AccumulableInfo x$0) {
      return (Option<Tuple7<Object, Option<String>, Option<Object>, Option<Object>, Object, Object, Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(BoxesRunTime.boxToLong(x$0.id()), x$0.name(), x$0.update(), x$0.value(), BoxesRunTime.boxToBoolean(x$0.internal()), BoxesRunTime.boxToBoolean(x$0.countFailedValues()), x$0.metadata())));
   }

   public Option<String> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AccumulableInfo$() {
      MODULE$ = this;
   }
}
