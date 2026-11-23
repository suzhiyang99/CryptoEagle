package org.apache.spark.ui.jobs;

import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.AccumulableInfo;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.status.api.v1.ShuffleReadMetrics;
import org.apache.spark.status.api.v1.StageData;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.math.Ordering;
import scala.runtime.BoxesRunTime;

public final class ApiHelper$ {
   public static final ApiHelper$ MODULE$;
   private final Map<String, String> COLUMN_TO_INDEX;

   static {
      new ApiHelper$();
   }

   private Map<String, String> COLUMN_TO_INDEX() {
      return this.COLUMN_TO_INDEX;
   }

   public boolean hasAccumulators(StageData stageData) {
      return stageData.accumulatorUpdates().exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(AccumulableInfo acc) {
            return acc.name() != null && acc.value() != null;
         }
      });
   }

   public boolean hasInput(StageData stageData) {
      return stageData.inputBytes() > 0L;
   }

   public boolean hasOutput(StageData stageData) {
      return stageData.outputBytes() > 0L;
   }

   public boolean hasShuffleRead(StageData stageData) {
      return stageData.shuffleReadBytes() > 0L;
   }

   public boolean hasShuffleWrite(StageData stageData) {
      return stageData.shuffleWriteBytes() > 0L;
   }

   public boolean hasBytesSpilled(StageData stageData) {
      return stageData.diskBytesSpilled() > 0L || stageData.memoryBytesSpilled() > 0L;
   }

   public long totalBytesRead(ShuffleReadMetrics metrics) {
      return metrics.localBytesRead() + metrics.remoteBytesRead();
   }

   public Option<String> indexName(String sortColumn) {
      Option var2 = this.COLUMN_TO_INDEX().get(sortColumn);
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         String v = (String)var3.x();
         Option var5 = Option$.MODULE$.apply(v);
         return var5;
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid sort column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
      }
   }

   public Tuple2<String, String> lastStageNameAndDescription(AppStatusStore store, JobData job) {
      Option stage = store.asOption(new Serializable(store, job) {
         public static final long serialVersionUID = 0L;
         private final AppStatusStore store$1;
         private final JobData job$1;

         public final StageData apply() {
            return this.store$1.lastStageAttempt(BoxesRunTime.unboxToInt(this.job$1.stageIds().max(Ordering.Int$.MODULE$)));
         }

         public {
            this.store$1 = store$1;
            this.job$1 = job$1;
         }
      });
      return new Tuple2<String, String>(stage.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(StageData x$24) {
            return x$24.name();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }), stage.flatMap(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply(StageData x$25) {
            return x$25.description();
         }
      }).getOrElse(new Serializable(job) {
         public static final long serialVersionUID = 0L;
         private final JobData job$1;

         public final String apply() {
            return this.job$1.name();
         }

         public {
            this.job$1 = job$1;
         }
      }));
   }

   private ApiHelper$() {
      MODULE$ = this;
      this.COLUMN_TO_INDEX = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("ID"), (Object)null), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Index"), "idx"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Attempt"), "att"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Status"), "sta"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Locality Level"), "loc"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID / Host"), "exe"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Launch Time"), "lt"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Duration"), "dur"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Scheduler Delay"), "dly"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Deserialization Time"), "des"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("GC Time"), "gc"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Result Serialization Time"), "rst"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Getting Result Time"), "grt"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Peak Execution Memory"), "pem"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Accumulators"), "acc"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Input Size / Records"), "is"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Output Size / Records"), "os"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Read Blocked Time"), "srt"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Read Size / Records"), "stby"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Remote Reads"), "srby"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Write Time"), "swt"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Write Size / Records"), "sws"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Spill (Memory)"), "mbs"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Spill (Disk)"), "dbs"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Errors"), "err")}));
   }
}
