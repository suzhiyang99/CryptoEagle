package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class HostTaskLocation$ extends AbstractFunction1<String, HostTaskLocation> implements Serializable {
   public static final HostTaskLocation$ MODULE$;

   static {
      new HostTaskLocation$();
   }

   public final String toString() {
      return "HostTaskLocation";
   }

   public HostTaskLocation apply(String host) {
      return new HostTaskLocation(host);
   }

   public Option<String> unapply(HostTaskLocation x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.host()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HostTaskLocation$() {
      MODULE$ = this;
   }
}
