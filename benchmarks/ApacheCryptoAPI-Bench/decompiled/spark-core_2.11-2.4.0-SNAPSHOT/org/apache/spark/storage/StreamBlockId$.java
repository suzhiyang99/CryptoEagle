package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcIJ$sp;
import scala.runtime.AbstractFunction2;

public final class StreamBlockId$ extends AbstractFunction2<Object, Object, StreamBlockId> implements Serializable {
   public static final StreamBlockId$ MODULE$;

   static {
      new StreamBlockId$();
   }

   public final String toString() {
      return "StreamBlockId";
   }

   public StreamBlockId apply(int streamId, long uniqueId) {
      return new StreamBlockId(streamId, uniqueId);
   }

   public Option<Tuple2<Object, Object>> unapply(StreamBlockId x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcIJ$sp(x$0.streamId(), x$0.uniqueId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StreamBlockId$() {
      MODULE$ = this;
   }
}
