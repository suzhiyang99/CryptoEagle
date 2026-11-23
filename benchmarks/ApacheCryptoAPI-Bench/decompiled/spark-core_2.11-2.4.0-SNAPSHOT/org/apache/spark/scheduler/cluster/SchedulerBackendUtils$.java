package org.apache.spark.scheduler.cluster;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public final class SchedulerBackendUtils$ {
   public static final SchedulerBackendUtils$ MODULE$;
   private final int DEFAULT_NUMBER_EXECUTORS;

   static {
      new SchedulerBackendUtils$();
   }

   public int DEFAULT_NUMBER_EXECUTORS() {
      return this.DEFAULT_NUMBER_EXECUTORS;
   }

   public int getInitialTargetExecutorNumber(SparkConf conf, int numExecutors) {
      int var10000;
      if (Utils$.MODULE$.isDynamicAllocationEnabled(conf)) {
         int minNumExecutors = BoxesRunTime.unboxToInt(conf.get(package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS()));
         int initialNumExecutors = Utils$.MODULE$.getDynamicAllocationInitialExecutors(conf);
         int maxNumExecutors = BoxesRunTime.unboxToInt(conf.get(package$.MODULE$.DYN_ALLOCATION_MAX_EXECUTORS()));
         Predef$.MODULE$.require(initialNumExecutors >= minNumExecutors && initialNumExecutors <= maxNumExecutors, new Serializable(minNumExecutors, initialNumExecutors, maxNumExecutors) {
            public static final long serialVersionUID = 0L;
            private final int minNumExecutors$1;
            private final int initialNumExecutors$1;
            private final int maxNumExecutors$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"initial executor number ", " must between min executor number "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.initialNumExecutors$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " and max executor number ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.minNumExecutors$1), BoxesRunTime.boxToInteger(this.maxNumExecutors$1)}))).toString();
            }

            public {
               this.minNumExecutors$1 = minNumExecutors$1;
               this.initialNumExecutors$1 = initialNumExecutors$1;
               this.maxNumExecutors$1 = maxNumExecutors$1;
            }
         });
         var10000 = initialNumExecutors;
      } else {
         var10000 = BoxesRunTime.unboxToInt(((Option)conf.get((ConfigEntry)package$.MODULE$.EXECUTOR_INSTANCES())).getOrElse(new Serializable(numExecutors) {
            public static final long serialVersionUID = 0L;
            private final int numExecutors$1;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return this.numExecutors$1;
            }

            public {
               this.numExecutors$1 = numExecutors$1;
            }
         }));
      }

      return var10000;
   }

   public int getInitialTargetExecutorNumber$default$2() {
      return this.DEFAULT_NUMBER_EXECUTORS();
   }

   private SchedulerBackendUtils$() {
      MODULE$ = this;
      this.DEFAULT_NUMBER_EXECUTORS = 2;
   }
}
