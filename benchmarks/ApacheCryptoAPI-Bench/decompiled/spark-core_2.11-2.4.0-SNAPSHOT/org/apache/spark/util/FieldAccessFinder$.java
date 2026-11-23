package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;

public final class FieldAccessFinder$ {
   public static final FieldAccessFinder$ MODULE$;

   static {
      new FieldAccessFinder$();
   }

   public Option<MethodIdentifier<?>> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   public Set<MethodIdentifier<?>> $lessinit$greater$default$4() {
      return Set$.MODULE$.empty();
   }

   private FieldAccessFinder$() {
      MODULE$ = this;
   }
}
