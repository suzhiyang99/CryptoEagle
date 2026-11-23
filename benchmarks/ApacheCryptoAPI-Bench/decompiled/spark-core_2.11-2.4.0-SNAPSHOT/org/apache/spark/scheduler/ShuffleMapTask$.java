package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;

public final class ShuffleMapTask$ implements Serializable {
   public static final ShuffleMapTask$ MODULE$;

   static {
      new ShuffleMapTask$();
   }

   public Option<Object> $lessinit$greater$default$8() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$9() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$10() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ShuffleMapTask$() {
      MODULE$ = this;
   }
}
