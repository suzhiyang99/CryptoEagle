package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Some;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxesRunTime;

public final class IntParam$ {
   public static final IntParam$ MODULE$;

   static {
      new IntParam$();
   }

   public Option<Object> unapply(String str) {
      Object var10000;
      try {
         var10000 = new Some(BoxesRunTime.boxToInteger((new StringOps(Predef$.MODULE$.augmentString(str))).toInt()));
      } catch (NumberFormatException var3) {
         var10000 = None$.MODULE$;
      }

      return (Option<Object>)var10000;
   }

   private IntParam$() {
      MODULE$ = this;
   }
}
