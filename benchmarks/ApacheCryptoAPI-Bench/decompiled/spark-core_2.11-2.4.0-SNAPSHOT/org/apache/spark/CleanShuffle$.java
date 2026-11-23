package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class CleanShuffle$ extends AbstractFunction1<Object, CleanShuffle> implements Serializable {
   public static final CleanShuffle$ MODULE$;

   static {
      new CleanShuffle$();
   }

   public final String toString() {
      return "CleanShuffle";
   }

   public CleanShuffle apply(int shuffleId) {
      return new CleanShuffle(shuffleId);
   }

   public Option<Object> unapply(CleanShuffle x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.shuffleId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CleanShuffle$() {
      MODULE$ = this;
   }
}
