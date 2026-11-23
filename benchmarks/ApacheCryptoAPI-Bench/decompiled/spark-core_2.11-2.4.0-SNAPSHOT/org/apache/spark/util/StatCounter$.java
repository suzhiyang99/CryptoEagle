package org.apache.spark.util;

import scala.Serializable;
import scala.collection.Seq;
import scala.collection.TraversableOnce;

public final class StatCounter$ implements Serializable {
   public static final StatCounter$ MODULE$;

   static {
      new StatCounter$();
   }

   public StatCounter apply(TraversableOnce<Object> values) {
      return new StatCounter(values);
   }

   public StatCounter apply(Seq<Object> values) {
      return new StatCounter(values);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StatCounter$() {
      MODULE$ = this;
   }
}
