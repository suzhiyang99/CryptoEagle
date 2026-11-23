package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class GetMapOutputStatuses$ extends AbstractFunction1<Object, GetMapOutputStatuses> implements Serializable {
   public static final GetMapOutputStatuses$ MODULE$;

   static {
      new GetMapOutputStatuses$();
   }

   public final String toString() {
      return "GetMapOutputStatuses";
   }

   public GetMapOutputStatuses apply(int shuffleId) {
      return new GetMapOutputStatuses(shuffleId);
   }

   public Option<Object> unapply(GetMapOutputStatuses x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.shuffleId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private GetMapOutputStatuses$() {
      MODULE$ = this;
   }
}
