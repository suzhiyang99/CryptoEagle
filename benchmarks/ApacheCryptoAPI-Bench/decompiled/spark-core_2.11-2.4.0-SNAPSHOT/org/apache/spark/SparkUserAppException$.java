package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class SparkUserAppException$ extends AbstractFunction1<Object, SparkUserAppException> implements Serializable {
   public static final SparkUserAppException$ MODULE$;

   static {
      new SparkUserAppException$();
   }

   public final String toString() {
      return "SparkUserAppException";
   }

   public SparkUserAppException apply(int exitCode) {
      return new SparkUserAppException(exitCode);
   }

   public Option<Object> unapply(SparkUserAppException x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.exitCode())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkUserAppException$() {
      MODULE$ = this;
   }
}
