package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;

/** @deprecated */
public final class Accumulator$ implements Serializable {
   public static final Accumulator$ MODULE$;

   static {
      new Accumulator$();
   }

   public <T> Option<String> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   public <T> boolean $lessinit$greater$default$4() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Accumulator$() {
      MODULE$ = this;
   }
}
