package org.apache.spark.util.collection;

import scala.Serializable;

public final class AppendOnlyMap$ implements Serializable {
   public static final AppendOnlyMap$ MODULE$;
   private final int MAXIMUM_CAPACITY;

   static {
      new AppendOnlyMap$();
   }

   public int MAXIMUM_CAPACITY() {
      return this.MAXIMUM_CAPACITY;
   }

   public <K, V> int $lessinit$greater$default$1() {
      return 64;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AppendOnlyMap$() {
      MODULE$ = this;
      this.MAXIMUM_CAPACITY = 536870912;
   }
}
