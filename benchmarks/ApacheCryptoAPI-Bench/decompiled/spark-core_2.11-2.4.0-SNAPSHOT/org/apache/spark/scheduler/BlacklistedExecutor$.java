package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class BlacklistedExecutor$ extends AbstractFunction2<String, Object, BlacklistedExecutor> implements Serializable {
   public static final BlacklistedExecutor$ MODULE$;

   static {
      new BlacklistedExecutor$();
   }

   public final String toString() {
      return "BlacklistedExecutor";
   }

   public BlacklistedExecutor apply(String node, long expiryTime) {
      return new BlacklistedExecutor(node, expiryTime);
   }

   public Option<Tuple2<String, Object>> unapply(BlacklistedExecutor x$0) {
      return (Option<Tuple2<String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.node(), BoxesRunTime.boxToLong(x$0.expiryTime()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlacklistedExecutor$() {
      MODULE$ = this;
   }
}
