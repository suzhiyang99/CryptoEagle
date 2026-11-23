package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Some;
import scala.runtime.BoxesRunTime;

public final class MemoryParam$ {
   public static final MemoryParam$ MODULE$;

   static {
      new MemoryParam$();
   }

   public Option<Object> unapply(String str) {
      Object var10000;
      try {
         var10000 = new Some(BoxesRunTime.boxToInteger(Utils$.MODULE$.memoryStringToMb(str)));
      } catch (NumberFormatException var3) {
         var10000 = None$.MODULE$;
      }

      return (Option<Object>)var10000;
   }

   private MemoryParam$() {
      MODULE$ = this;
   }
}
