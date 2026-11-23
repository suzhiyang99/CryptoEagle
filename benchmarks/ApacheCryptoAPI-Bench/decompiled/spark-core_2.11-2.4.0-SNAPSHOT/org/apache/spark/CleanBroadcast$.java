package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class CleanBroadcast$ extends AbstractFunction1<Object, CleanBroadcast> implements Serializable {
   public static final CleanBroadcast$ MODULE$;

   static {
      new CleanBroadcast$();
   }

   public final String toString() {
      return "CleanBroadcast";
   }

   public CleanBroadcast apply(long broadcastId) {
      return new CleanBroadcast(broadcastId);
   }

   public Option<Object> unapply(CleanBroadcast x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(x$0.broadcastId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CleanBroadcast$() {
      MODULE$ = this;
   }
}
