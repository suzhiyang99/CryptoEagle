package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;

public final class ResultTask$ implements Serializable {
   public static final ResultTask$ MODULE$;

   static {
      new ResultTask$();
   }

   public <T, U> Option<Object> $lessinit$greater$default$9() {
      return None$.MODULE$;
   }

   public <T, U> Option<String> $lessinit$greater$default$10() {
      return None$.MODULE$;
   }

   public <T, U> Option<String> $lessinit$greater$default$11() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ResultTask$() {
      MODULE$ = this;
   }
}
