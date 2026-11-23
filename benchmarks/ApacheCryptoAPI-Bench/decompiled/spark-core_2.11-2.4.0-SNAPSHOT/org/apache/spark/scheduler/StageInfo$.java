package org.apache.spark.scheduler;

import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.storage.RDDInfo$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.List$;
import scala.runtime.BoxesRunTime;

public final class StageInfo$ {
   public static final StageInfo$ MODULE$;

   static {
      new StageInfo$();
   }

   public StageInfo fromStage(Stage stage, int attemptId, Option<Object> numTasks, TaskMetrics taskMetrics, Seq<Seq<TaskLocation>> taskLocalityPreferences) {
      Seq ancestorRddInfos = (Seq)stage.rdd().getNarrowAncestors().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final RDDInfo apply(RDD<?> rdd) {
            return RDDInfo$.MODULE$.fromRdd(rdd);
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq rddInfos = (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDDInfo[]{RDDInfo$.MODULE$.fromRdd(stage.rdd())}))).$plus$plus(ancestorRddInfos, Seq$.MODULE$.canBuildFrom());
      return new StageInfo(stage.id(), attemptId, stage.name(), BoxesRunTime.unboxToInt(numTasks.getOrElse(new Serializable(stage) {
         public static final long serialVersionUID = 0L;
         private final Stage stage$1;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.stage$1.numTasks();
         }

         public {
            this.stage$1 = stage$1;
         }
      })), rddInfos, (Seq)stage.parents().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(Stage x$1) {
            return x$1.id();
         }
      }, List$.MODULE$.canBuildFrom()), stage.details(), taskMetrics, taskLocalityPreferences);
   }

   public Option<Object> fromStage$default$3() {
      return None$.MODULE$;
   }

   public TaskMetrics fromStage$default$4() {
      return null;
   }

   public Seq<Seq<TaskLocation>> fromStage$default$5() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public TaskMetrics $lessinit$greater$default$8() {
      return null;
   }

   public Seq<Seq<TaskLocation>> $lessinit$greater$default$9() {
      return (Seq)Seq$.MODULE$.empty();
   }

   private StageInfo$() {
      MODULE$ = this;
   }
}
