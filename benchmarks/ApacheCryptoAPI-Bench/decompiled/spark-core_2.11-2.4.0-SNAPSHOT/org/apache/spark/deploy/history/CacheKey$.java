package org.apache.spark.deploy.history;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class CacheKey$ extends AbstractFunction2<String, Option<String>, CacheKey> implements Serializable {
   public static final CacheKey$ MODULE$;

   static {
      new CacheKey$();
   }

   public final String toString() {
      return "CacheKey";
   }

   public CacheKey apply(String appId, Option<String> attemptId) {
      return new CacheKey(appId, attemptId);
   }

   public Option<Tuple2<String, Option<String>>> unapply(CacheKey x$0) {
      return (Option<Tuple2<String, Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.appId(), x$0.attemptId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CacheKey$() {
      MODULE$ = this;
   }
}
