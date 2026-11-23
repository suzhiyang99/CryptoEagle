package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class CleanCheckpoint$ extends AbstractFunction1<Object, CleanCheckpoint> implements Serializable {
   public static final CleanCheckpoint$ MODULE$;

   static {
      new CleanCheckpoint$();
   }

   public final String toString() {
      return "CleanCheckpoint";
   }

   public CleanCheckpoint apply(int rddId) {
      return new CleanCheckpoint(rddId);
   }

   public Option<Object> unapply(CleanCheckpoint x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.rddId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CleanCheckpoint$() {
      MODULE$ = this;
   }
}
