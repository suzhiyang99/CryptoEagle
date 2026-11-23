package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class HeartbeatResponse$ extends AbstractFunction1<Object, HeartbeatResponse> implements Serializable {
   public static final HeartbeatResponse$ MODULE$;

   static {
      new HeartbeatResponse$();
   }

   public final String toString() {
      return "HeartbeatResponse";
   }

   public HeartbeatResponse apply(boolean reregisterBlockManager) {
      return new HeartbeatResponse(reregisterBlockManager);
   }

   public Option<Object> unapply(HeartbeatResponse x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToBoolean(x$0.reregisterBlockManager())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HeartbeatResponse$() {
      MODULE$ = this;
   }
}
