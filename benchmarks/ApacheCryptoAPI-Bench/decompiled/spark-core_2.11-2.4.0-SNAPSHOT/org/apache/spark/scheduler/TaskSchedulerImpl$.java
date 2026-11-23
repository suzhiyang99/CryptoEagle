package org.apache.spark.scheduler;

import org.apache.spark.ExecutorAllocationClient;
import org.apache.spark.SparkContext;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.SeqLike;
import scala.collection.immutable.List;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.runtime.BooleanRef;
import scala.runtime.IntRef;
import scala.runtime.Null$;

public final class TaskSchedulerImpl$ {
   public static final TaskSchedulerImpl$ MODULE$;
   private final String SCHEDULER_MODE_PROPERTY;

   static {
      new TaskSchedulerImpl$();
   }

   public String SCHEDULER_MODE_PROPERTY() {
      return this.SCHEDULER_MODE_PROPERTY;
   }

   public <K, T> List<T> prioritizeContainers(HashMap<K, ArrayBuffer<T>> map) {
      ArrayBuffer _keyList = new ArrayBuffer(map.size());
      _keyList.$plus$plus$eq(map.keys());
      ArrayBuffer keyList = (ArrayBuffer)_keyList.sortWith(new Serializable(map) {
         public static final long serialVersionUID = 0L;
         private final HashMap map$1;

         public final boolean apply(K left, K right) {
            return ((SeqLike)this.map$1.apply(left)).size() > ((SeqLike)this.map$1.apply(right)).size();
         }

         public {
            this.map$1 = map$1;
         }
      });
      ArrayBuffer retval = new ArrayBuffer(keyList.size() * 2);
      IntRef index = IntRef.create(0);

      for(BooleanRef found = BooleanRef.create(true); found.elem; ++index.elem) {
         found.elem = false;
         keyList.foreach(new Serializable(map, retval, index, found) {
            public static final long serialVersionUID = 0L;
            private final HashMap map$1;
            private final ArrayBuffer retval$1;
            private final IntRef index$1;
            private final BooleanRef found$1;

            public final void apply(K key) {
               ArrayBuffer containerList = (ArrayBuffer)this.map$1.getOrElse(key, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Null$ apply() {
                     return null;
                  }
               });
               Predef$.MODULE$.assert(containerList != null);
               if (this.index$1.elem < containerList.size()) {
                  this.retval$1.$plus$eq(containerList.apply(this.index$1.elem));
                  this.found$1.elem = true;
               }

            }

            public {
               this.map$1 = map$1;
               this.retval$1 = retval$1;
               this.index$1 = index$1;
               this.found$1 = found$1;
            }
         });
      }

      return retval.toList();
   }

   public Option<BlacklistTracker> org$apache$spark$scheduler$TaskSchedulerImpl$$maybeCreateBlacklistTracker(SparkContext sc) {
      Object var10000;
      if (BlacklistTracker$.MODULE$.isBlacklistEnabled(sc.conf())) {
         SchedulerBackend var3 = sc.schedulerBackend();
         Object executorAllocClient;
         if (var3 instanceof ExecutorAllocationClient) {
            executorAllocClient = new Some(var3);
         } else {
            executorAllocClient = None$.MODULE$;
         }

         var10000 = new Some(new BlacklistTracker(sc, (Option)executorAllocClient));
      } else {
         var10000 = None$.MODULE$;
      }

      return (Option<BlacklistTracker>)var10000;
   }

   public boolean $lessinit$greater$default$3() {
      return false;
   }

   private TaskSchedulerImpl$() {
      MODULE$ = this;
      this.SCHEDULER_MODE_PROPERTY = "spark.scheduler.mode";
   }
}
