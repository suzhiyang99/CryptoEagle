package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class HDFSCacheTaskLocation$ extends AbstractFunction1<String, HDFSCacheTaskLocation> implements Serializable {
   public static final HDFSCacheTaskLocation$ MODULE$;

   static {
      new HDFSCacheTaskLocation$();
   }

   public final String toString() {
      return "HDFSCacheTaskLocation";
   }

   public HDFSCacheTaskLocation apply(String host) {
      return new HDFSCacheTaskLocation(host);
   }

   public Option<String> unapply(HDFSCacheTaskLocation x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.host()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HDFSCacheTaskLocation$() {
      MODULE$ = this;
   }
}
