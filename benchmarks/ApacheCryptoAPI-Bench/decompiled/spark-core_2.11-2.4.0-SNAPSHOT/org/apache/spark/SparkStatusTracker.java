package org.apache.spark;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.status.api.v1.MemoryMetrics;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001Y3A!\u0001\u0002\u0001\u0013\t\u00112\u000b]1sWN#\u0018\r^;t)J\f7m[3s\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\n\u0003\u0001)\u0001\"a\u0003\b\u000e\u00031Q\u0011!D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001f1\u0011a!\u00118z%\u00164\u0007\u0002C\t\u0001\u0005\u0003\u0005\u000b\u0011\u0002\n\u0002\u0005M\u001c\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u00051\u0019\u0006/\u0019:l\u0007>tG/\u001a=u\u0011!9\u0002A!A!\u0002\u0013A\u0012!B:u_J,\u0007CA\r\u001d\u001b\u0005Q\"BA\u000e\u0003\u0003\u0019\u0019H/\u0019;vg&\u0011QD\u0007\u0002\u000f\u0003B\u00048\u000b^1ukN\u001cFo\u001c:f\u0011\u0019y\u0002\u0001\"\u0001\u0003A\u00051A(\u001b8jiz\"2!\t\u0012$!\t\u0019\u0002\u0001C\u0003\u0012=\u0001\u0007!\u0003C\u0003\u0018=\u0001\u0007\u0001\u0004C\u0003&\u0001\u0011\u0005a%A\thKRTuNY%eg\u001a{'o\u0012:pkB$\"aJ\u0017\u0011\u0007-A#&\u0003\u0002*\u0019\t)\u0011I\u001d:bsB\u00111bK\u0005\u0003Y1\u00111!\u00138u\u0011\u0015qC\u00051\u00010\u0003!QwNY$s_V\u0004\bC\u0001\u00194\u001d\tY\u0011'\u0003\u00023\u0019\u00051\u0001K]3eK\u001aL!\u0001N\u001b\u0003\rM#(/\u001b8h\u0015\t\u0011D\u0002C\u00038\u0001\u0011\u0005\u0001(A\thKR\f5\r^5wKN#\u0018mZ3JIN$\u0012a\n\u0005\u0006u\u0001!\t\u0001O\u0001\u0010O\u0016$\u0018i\u0019;jm\u0016TuNY%eg\")A\b\u0001C\u0001{\u0005Qq-\u001a;K_\nLeNZ8\u0015\u0005y\"\u0005cA\u0006@\u0003&\u0011\u0001\t\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005M\u0011\u0015BA\"\u0003\u00051\u0019\u0006/\u0019:l\u0015>\u0014\u0017J\u001c4p\u0011\u0015)5\b1\u0001+\u0003\u0015QwNY%e\u0011\u00159\u0005\u0001\"\u0001I\u000319W\r^*uC\u001e,\u0017J\u001c4p)\tIU\nE\u0002\f\u007f)\u0003\"aE&\n\u00051\u0013!AD*qCJ\\7\u000b^1hK&sgm\u001c\u0005\u0006\u001d\u001a\u0003\rAK\u0001\bgR\fw-Z%e\u0011\u0015\u0001\u0006\u0001\"\u0001R\u0003A9W\r^#yK\u000e,Ho\u001c:J]\u001a|7/F\u0001S!\rY\u0001f\u0015\t\u0003'QK!!\u0016\u0002\u0003#M\u0003\u0018M]6Fq\u0016\u001cW\u000f^8s\u0013:4w\u000e"
)
public class SparkStatusTracker {
   public final AppStatusStore org$apache$spark$SparkStatusTracker$$store;

   public int[] getJobIdsForGroup(String jobGroup) {
      Option expected = Option$.MODULE$.apply(jobGroup);
      return (int[])((TraversableOnce)((TraversableLike)this.org$apache$spark$SparkStatusTracker$$store.jobsList((List)null).filter(new Serializable(this, expected) {
         public static final long serialVersionUID = 0L;
         private final Option expected$1;

         public final boolean apply(JobData x$1) {
            boolean var3;
            label23: {
               Option var10000 = x$1.jobGroup();
               Option var2 = this.expected$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.expected$1 = expected$1;
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(JobData x$2) {
            return x$2.jobId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
   }

   public int[] getActiveStageIds() {
      return (int[])((TraversableOnce)this.org$apache$spark$SparkStatusTracker$$store.stageList(Arrays.asList((Object[])(new StageStatus[]{StageStatus.ACTIVE}))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageData x$3) {
            return x$3.stageId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
   }

   public int[] getActiveJobIds() {
      return (int[])((TraversableOnce)this.org$apache$spark$SparkStatusTracker$$store.jobsList(Arrays.asList((Object[])(new JobExecutionStatus[]{JobExecutionStatus.RUNNING}))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(JobData x$4) {
            return x$4.jobId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
   }

   public Option<SparkJobInfo> getJobInfo(int jobId) {
      return this.org$apache$spark$SparkStatusTracker$$store.<SparkJobInfo>asOption(new Serializable(this, jobId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkStatusTracker $outer;
         private final int jobId$1;

         public final JobData apply() {
            return this.$outer.org$apache$spark$SparkStatusTracker$$store.job(this.jobId$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.jobId$1 = jobId$1;
            }
         }
      }).<SparkJobInfo>map(new Serializable(this, jobId) {
         public static final long serialVersionUID = 0L;
         private final int jobId$1;

         public final SparkJobInfoImpl apply(JobData job) {
            return new SparkJobInfoImpl(this.jobId$1, (int[])job.stageIds().toArray(ClassTag$.MODULE$.Int()), job.status());
         }

         public {
            this.jobId$1 = jobId$1;
         }
      });
   }

   public Option<SparkStageInfo> getStageInfo(int stageId) {
      return this.org$apache$spark$SparkStatusTracker$$store.<SparkStageInfo>asOption(new Serializable(this, stageId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkStatusTracker $outer;
         private final int stageId$1;

         public final StageData apply() {
            return this.$outer.org$apache$spark$SparkStatusTracker$$store.lastStageAttempt(this.stageId$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageId$1 = stageId$1;
            }
         }
      }).<SparkStageInfo>map(new Serializable(this, stageId) {
         public static final long serialVersionUID = 0L;
         private final int stageId$1;

         public final SparkStageInfoImpl apply(StageData stage) {
            return new SparkStageInfoImpl(this.stageId$1, stage.attemptId(), BoxesRunTime.unboxToLong(stage.submissionTime().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(Date x$5) {
                  return x$5.getTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })), stage.name(), stage.numTasks(), stage.numActiveTasks(), stage.numCompleteTasks(), stage.numFailedTasks());
         }

         public {
            this.stageId$1 = stageId$1;
         }
      });
   }

   public SparkExecutorInfo[] getExecutorInfos() {
      return (SparkExecutorInfo[])((TraversableOnce)this.org$apache$spark$SparkStatusTracker$$store.executorList(true).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final SparkExecutorInfoImpl apply(ExecutorSummary exec) {
            String[] var4 = exec.hostPort().split(":", 2);
            Option var5 = Array$.MODULE$.unapplySeq(var4);
            Tuple2 var8;
            if (!var5.isEmpty() && var5.get() != null && ((SeqLike)var5.get()).lengthCompare(2) == 0) {
               String h = (String)((SeqLike)var5.get()).apply(0);
               String p = (String)((SeqLike)var5.get()).apply(1);
               var8 = new Tuple2(h, BoxesRunTime.boxToInteger((new StringOps(Predef$.MODULE$.augmentString(p))).toInt()));
            } else {
               Option var9 = Array$.MODULE$.unapplySeq(var4);
               if (var9.isEmpty() || var9.get() == null || ((SeqLike)var9.get()).lengthCompare(1) != 0) {
                  throw new MatchError(var4);
               }

               String h = (String)((SeqLike)var9.get()).apply(0);
               var8 = new Tuple2(h, BoxesRunTime.boxToInteger(-1));
            }

            if (var8 != null) {
               String host = (String)var8._1();
               int port = var8._2$mcI$sp();
               Tuple2 var13 = new Tuple2(host, BoxesRunTime.boxToInteger(port));
               String host = (String)var13._1();
               int port = var13._2$mcI$sp();
               long cachedMem = BoxesRunTime.unboxToLong(exec.memoryMetrics().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final long apply(MemoryMetrics mem) {
                     return mem.usedOnHeapStorageMemory() + mem.usedOffHeapStorageMemory();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final long apply() {
                     return this.apply$mcJ$sp();
                  }

                  public long apply$mcJ$sp() {
                     return 0L;
                  }
               }));
               return new SparkExecutorInfoImpl(host, port, cachedMem, exec.activeTasks());
            } else {
               throw new MatchError(var8);
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(SparkExecutorInfo.class));
   }

   public SparkStatusTracker(SparkContext sc, AppStatusStore store) {
      this.org$apache$spark$SparkStatusTracker$$store = store;
   }
}
