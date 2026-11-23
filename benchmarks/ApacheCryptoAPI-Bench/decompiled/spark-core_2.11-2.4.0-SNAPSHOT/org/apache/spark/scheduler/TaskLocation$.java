package org.apache.spark.scheduler;

import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.SeqLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;

public final class TaskLocation$ {
   public static final TaskLocation$ MODULE$;
   private final String inMemoryLocationTag;
   private final String executorLocationTag;

   static {
      new TaskLocation$();
   }

   public String inMemoryLocationTag() {
      return this.inMemoryLocationTag;
   }

   public String executorLocationTag() {
      return this.executorLocationTag;
   }

   public TaskLocation apply(String host, String executorId) {
      return new ExecutorCacheTaskLocation(host, executorId);
   }

   public TaskLocation apply(String str) {
      String hstr = (new StringOps(Predef$.MODULE$.augmentString(str))).stripPrefix(this.inMemoryLocationTag());
      Object var10000;
      if (hstr.equals(str)) {
         if (str.startsWith(this.executorLocationTag())) {
            String hostAndExecutorId = (new StringOps(Predef$.MODULE$.augmentString(str))).stripPrefix(this.executorLocationTag());
            String[] splits = hostAndExecutorId.split("_", 2);
            Predef$.MODULE$.require(splits.length == 2, new Serializable(str) {
               public static final long serialVersionUID = 0L;
               private final String str$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Illegal executor location format: ").append((Object)this.str$1).toString();
               }

               public {
                  this.str$1 = str$1;
               }
            });
            Option var7 = Array$.MODULE$.unapplySeq(splits);
            if (var7.isEmpty() || var7.get() == null || ((SeqLike)var7.get()).lengthCompare(2) != 0) {
               throw new MatchError(splits);
            }

            String host = (String)((SeqLike)var7.get()).apply(0);
            String executorId = (String)((SeqLike)var7.get()).apply(1);
            Tuple2 var10 = new Tuple2(host, executorId);
            String host = (String)var10._1();
            String executorId = (String)var10._2();
            var10000 = new ExecutorCacheTaskLocation(host, executorId);
         } else {
            var10000 = new HostTaskLocation(str);
         }
      } else {
         var10000 = new HDFSCacheTaskLocation(hstr);
      }

      return (TaskLocation)var10000;
   }

   private TaskLocation$() {
      MODULE$ = this;
      this.inMemoryLocationTag = "hdfs_cache_";
      this.executorLocationTag = "executor_";
   }
}
