package org.apache.spark.scheduler;

import scala.Function1;
import scala.Serializable;

public final class ReplayListenerBus$ {
   public static final ReplayListenerBus$ MODULE$;
   private final Function1<String, Object> SELECT_ALL_FILTER;

   static {
      new ReplayListenerBus$();
   }

   public Function1<String, Object> SELECT_ALL_FILTER() {
      return this.SELECT_ALL_FILTER;
   }

   private ReplayListenerBus$() {
      MODULE$ = this;
      this.SELECT_ALL_FILTER = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String eventString) {
            return true;
         }
      };
   }
}
