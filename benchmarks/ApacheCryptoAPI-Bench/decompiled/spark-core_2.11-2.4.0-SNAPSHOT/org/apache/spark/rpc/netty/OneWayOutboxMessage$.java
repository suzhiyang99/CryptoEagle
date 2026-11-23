package org.apache.spark.rpc.netty;

import java.nio.ByteBuffer;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class OneWayOutboxMessage$ extends AbstractFunction1<ByteBuffer, OneWayOutboxMessage> implements Serializable {
   public static final OneWayOutboxMessage$ MODULE$;

   static {
      new OneWayOutboxMessage$();
   }

   public final String toString() {
      return "OneWayOutboxMessage";
   }

   public OneWayOutboxMessage apply(ByteBuffer content) {
      return new OneWayOutboxMessage(content);
   }

   public Option<ByteBuffer> unapply(OneWayOutboxMessage x$0) {
      return (Option<ByteBuffer>)(x$0 == null ? None$.MODULE$ : new Some(x$0.content()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OneWayOutboxMessage$() {
      MODULE$ = this;
   }
}
