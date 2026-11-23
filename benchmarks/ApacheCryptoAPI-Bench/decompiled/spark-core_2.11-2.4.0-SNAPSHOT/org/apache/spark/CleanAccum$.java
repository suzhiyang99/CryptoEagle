package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class CleanAccum$ extends AbstractFunction1<Object, CleanAccum> implements Serializable {
   public static final CleanAccum$ MODULE$;

   static {
      new CleanAccum$();
   }

   public final String toString() {
      return "CleanAccum";
   }

   public CleanAccum apply(long accId) {
      return new CleanAccum(accId);
   }

   public Option<Object> unapply(CleanAccum x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(x$0.accId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CleanAccum$() {
      MODULE$ = this;
   }
}
