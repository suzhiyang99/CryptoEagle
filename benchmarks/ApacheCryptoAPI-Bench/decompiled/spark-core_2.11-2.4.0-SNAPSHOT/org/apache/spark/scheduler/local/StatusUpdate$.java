package org.apache.spark.scheduler.local;

import java.nio.ByteBuffer;
import scala.Enumeration;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class StatusUpdate$ extends AbstractFunction3<Object, Enumeration.Value, ByteBuffer, StatusUpdate> implements Serializable {
   public static final StatusUpdate$ MODULE$;

   static {
      new StatusUpdate$();
   }

   public final String toString() {
      return "StatusUpdate";
   }

   public StatusUpdate apply(long taskId, Enumeration.Value state, ByteBuffer serializedData) {
      return new StatusUpdate(taskId, state, serializedData);
   }

   public Option<Tuple3<Object, Enumeration.Value, ByteBuffer>> unapply(StatusUpdate x$0) {
      return (Option<Tuple3<Object, Enumeration.Value, ByteBuffer>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.taskId()), x$0.state(), x$0.serializedData())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StatusUpdate$() {
      MODULE$ = this;
   }
}
