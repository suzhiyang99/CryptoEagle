package org.apache.spark.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import java.util.UUID;
import org.apache.spark.ExceptionFailure;
import org.apache.spark.ExceptionFailure$;
import org.apache.spark.ExecutorLostFailure;
import org.apache.spark.FetchFailed;
import org.apache.spark.InternalAccumulator$;
import org.apache.spark.Resubmitted$;
import org.apache.spark.Success$;
import org.apache.spark.TaskCommitDenied;
import org.apache.spark.TaskEndReason;
import org.apache.spark.TaskKilled;
import org.apache.spark.TaskResultLost$;
import org.apache.spark.UnknownReason$;
import org.apache.spark.executor.InputMetrics;
import org.apache.spark.executor.OutputMetrics;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.executor.TaskMetrics$;
import org.apache.spark.executor.TempShuffleReadMetrics;
import org.apache.spark.rdd.RDDOperationScope;
import org.apache.spark.rdd.RDDOperationScope$;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.scheduler.JobFailed;
import org.apache.spark.scheduler.JobResult;
import org.apache.spark.scheduler.JobSucceeded$;
import org.apache.spark.scheduler.SparkListenerApplicationEnd;
import org.apache.spark.scheduler.SparkListenerApplicationStart;
import org.apache.spark.scheduler.SparkListenerBlockManagerAdded;
import org.apache.spark.scheduler.SparkListenerBlockManagerRemoved;
import org.apache.spark.scheduler.SparkListenerBlockUpdated;
import org.apache.spark.scheduler.SparkListenerEnvironmentUpdate;
import org.apache.spark.scheduler.SparkListenerEvent;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorMetricsUpdate;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.scheduler.SparkListenerJobStart;
import org.apache.spark.scheduler.SparkListenerLogStart;
import org.apache.spark.scheduler.SparkListenerStageCompleted;
import org.apache.spark.scheduler.SparkListenerStageSubmitted;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import org.apache.spark.scheduler.SparkListenerTaskGettingResult;
import org.apache.spark.scheduler.SparkListenerTaskStart;
import org.apache.spark.scheduler.SparkListenerUnpersistRDD;
import org.apache.spark.scheduler.StageInfo;
import org.apache.spark.scheduler.StageInfo$;
import org.apache.spark.scheduler.TaskInfo;
import org.apache.spark.scheduler.TaskLocality$;
import org.apache.spark.scheduler.cluster.ExecutorInfo;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockId$;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.BlockManagerId$;
import org.apache.spark.storage.BlockStatus;
import org.apache.spark.storage.BlockUpdatedInfo;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import org.json4s.DefaultFormats;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods.;
import scala.Array$;
import scala.Enumeration;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Map$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.math.BigInt;
import scala.math.BigInt$;
import scala.reflect.ClassTag$;
import scala.reflect.Manifest;
import scala.reflect.ManifestFactory$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.Null$;

public final class JsonProtocol$ {
   public static final JsonProtocol$ MODULE$;
   private final DefaultFormats org$apache$spark$util$JsonProtocol$$format;
   private final ObjectMapper mapper;
   private Set<String> org$apache$spark$util$JsonProtocol$$accumulableBlacklist;
   private volatile boolean bitmap$0;

   static {
      new JsonProtocol$();
   }

   private Set org$apache$spark$util$JsonProtocol$$accumulableBlacklist$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.org$apache$spark$util$JsonProtocol$$accumulableBlacklist = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"internal.metrics.updatedBlockStatuses"})));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$util$JsonProtocol$$accumulableBlacklist;
   }

   public DefaultFormats org$apache$spark$util$JsonProtocol$$format() {
      return this.org$apache$spark$util$JsonProtocol$$format;
   }

   private ObjectMapper mapper() {
      return this.mapper;
   }

   public JsonAST.JValue sparkEventToJson(SparkListenerEvent event) {
      JsonAST.JValue var4;
      if (event instanceof SparkListenerStageSubmitted) {
         SparkListenerStageSubmitted var3 = (SparkListenerStageSubmitted)event;
         var4 = this.stageSubmittedToJson(var3);
      } else if (event instanceof SparkListenerStageCompleted) {
         SparkListenerStageCompleted var5 = (SparkListenerStageCompleted)event;
         var4 = this.stageCompletedToJson(var5);
      } else if (event instanceof SparkListenerTaskStart) {
         SparkListenerTaskStart var6 = (SparkListenerTaskStart)event;
         var4 = this.taskStartToJson(var6);
      } else if (event instanceof SparkListenerTaskGettingResult) {
         SparkListenerTaskGettingResult var7 = (SparkListenerTaskGettingResult)event;
         var4 = this.taskGettingResultToJson(var7);
      } else if (event instanceof SparkListenerTaskEnd) {
         SparkListenerTaskEnd var8 = (SparkListenerTaskEnd)event;
         var4 = this.taskEndToJson(var8);
      } else if (event instanceof SparkListenerJobStart) {
         SparkListenerJobStart var9 = (SparkListenerJobStart)event;
         var4 = this.jobStartToJson(var9);
      } else if (event instanceof SparkListenerJobEnd) {
         SparkListenerJobEnd var10 = (SparkListenerJobEnd)event;
         var4 = this.jobEndToJson(var10);
      } else if (event instanceof SparkListenerEnvironmentUpdate) {
         SparkListenerEnvironmentUpdate var11 = (SparkListenerEnvironmentUpdate)event;
         var4 = this.environmentUpdateToJson(var11);
      } else if (event instanceof SparkListenerBlockManagerAdded) {
         SparkListenerBlockManagerAdded var12 = (SparkListenerBlockManagerAdded)event;
         var4 = this.blockManagerAddedToJson(var12);
      } else if (event instanceof SparkListenerBlockManagerRemoved) {
         SparkListenerBlockManagerRemoved var13 = (SparkListenerBlockManagerRemoved)event;
         var4 = this.blockManagerRemovedToJson(var13);
      } else if (event instanceof SparkListenerUnpersistRDD) {
         SparkListenerUnpersistRDD var14 = (SparkListenerUnpersistRDD)event;
         var4 = this.unpersistRDDToJson(var14);
      } else if (event instanceof SparkListenerApplicationStart) {
         SparkListenerApplicationStart var15 = (SparkListenerApplicationStart)event;
         var4 = this.applicationStartToJson(var15);
      } else if (event instanceof SparkListenerApplicationEnd) {
         SparkListenerApplicationEnd var16 = (SparkListenerApplicationEnd)event;
         var4 = this.applicationEndToJson(var16);
      } else if (event instanceof SparkListenerExecutorAdded) {
         SparkListenerExecutorAdded var17 = (SparkListenerExecutorAdded)event;
         var4 = this.executorAddedToJson(var17);
      } else if (event instanceof SparkListenerExecutorRemoved) {
         SparkListenerExecutorRemoved var18 = (SparkListenerExecutorRemoved)event;
         var4 = this.executorRemovedToJson(var18);
      } else if (event instanceof SparkListenerLogStart) {
         SparkListenerLogStart var19 = (SparkListenerLogStart)event;
         var4 = this.logStartToJson(var19);
      } else if (event instanceof SparkListenerExecutorMetricsUpdate) {
         SparkListenerExecutorMetricsUpdate var20 = (SparkListenerExecutorMetricsUpdate)event;
         var4 = this.executorMetricsUpdateToJson(var20);
      } else if (event instanceof SparkListenerBlockUpdated) {
         SparkListenerBlockUpdated var21 = (SparkListenerBlockUpdated)event;
         var4 = this.blockUpdateToJson(var21);
      } else {
         var4 = .MODULE$.parse(org.json4s.package..MODULE$.string2JsonInput(this.mapper().writeValueAsString(event)), .MODULE$.parse$default$2(), .MODULE$.parse$default$3());
      }

      return var4;
   }

   public JsonAST.JValue stageSubmittedToJson(SparkListenerStageSubmitted stageSubmitted) {
      JsonAST.JValue stageInfo = this.stageInfoToJson(stageSubmitted.stageInfo());
      JsonAST.JValue properties = this.propertiesToJson(stageSubmitted.properties());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.stageSubmitted()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Info"), stageInfo), Predef$.MODULE$.$conforms())).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Properties"), properties));
   }

   public JsonAST.JValue stageCompletedToJson(SparkListenerStageCompleted stageCompleted) {
      JsonAST.JValue stageInfo = this.stageInfoToJson(stageCompleted.stageInfo());
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.stageCompleted()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Info"), stageInfo), Predef$.MODULE$.$conforms());
   }

   public JsonAST.JValue taskStartToJson(SparkListenerTaskStart taskStart) {
      TaskInfo taskInfo = taskStart.taskInfo();
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskStart()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage ID"), BoxesRunTime.boxToInteger(taskStart.stageId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Attempt ID"), BoxesRunTime.boxToInteger(taskStart.stageAttemptId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Info"), this.taskInfoToJson(taskInfo)));
   }

   public JsonAST.JValue taskGettingResultToJson(SparkListenerTaskGettingResult taskGettingResult) {
      TaskInfo taskInfo = taskGettingResult.taskInfo();
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskGettingResult()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Info"), this.taskInfoToJson(taskInfo)), Predef$.MODULE$.$conforms());
   }

   public JsonAST.JValue taskEndToJson(SparkListenerTaskEnd taskEnd) {
      JsonAST.JValue taskEndReason = this.taskEndReasonToJson(taskEnd.reason());
      TaskInfo taskInfo = taskEnd.taskInfo();
      TaskMetrics taskMetrics = taskEnd.taskMetrics();
      JsonAST.JValue taskMetricsJson = (JsonAST.JValue)(taskMetrics == null ? org.json4s.JsonAST.JNothing..MODULE$ : this.taskMetricsToJson(taskMetrics));
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskEnd()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage ID"), BoxesRunTime.boxToInteger(taskEnd.stageId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Attempt ID"), BoxesRunTime.boxToInteger(taskEnd.stageAttemptId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Type"), taskEnd.taskType()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task End Reason"), taskEndReason))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Info"), this.taskInfoToJson(taskInfo)))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task Metrics"), taskMetricsJson));
   }

   public JsonAST.JValue jobStartToJson(SparkListenerJobStart jobStart) {
      JsonAST.JValue properties = this.propertiesToJson(jobStart.properties());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.jobStart()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Job ID"), BoxesRunTime.boxToInteger(jobStart.jobId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Submission Time"), BoxesRunTime.boxToLong(jobStart.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Infos"), jobStart.stageInfos().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(StageInfo stageInfo) {
            return JsonProtocol$.MODULE$.stageInfoToJson(stageInfo);
         }
      }, Seq$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JValue> s) {
            return org.json4s.JsonDSL..MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage IDs"), jobStart.stageIds()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<Object> s) {
            return org.json4s.JsonDSL..MODULE$.seq2jvalue(s, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(int x) {
                  return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
               }
            });
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Properties"), properties));
   }

   public JsonAST.JValue jobEndToJson(SparkListenerJobEnd jobEnd) {
      JsonAST.JValue jobResult = this.jobResultToJson(jobEnd.jobResult());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.jobEnd()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Job ID"), BoxesRunTime.boxToInteger(jobEnd.jobId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Completion Time"), BoxesRunTime.boxToLong(jobEnd.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Job Result"), jobResult));
   }

   public JsonAST.JValue environmentUpdateToJson(SparkListenerEnvironmentUpdate environmentUpdate) {
      Map environmentDetails = environmentUpdate.environmentDetails();
      JsonAST.JValue jvmInformation = this.mapToJson(((TraversableOnce)environmentDetails.apply("JVM Information")).toMap(Predef$.MODULE$.$conforms()));
      JsonAST.JValue sparkProperties = this.mapToJson(((TraversableOnce)environmentDetails.apply("Spark Properties")).toMap(Predef$.MODULE$.$conforms()));
      JsonAST.JValue systemProperties = this.mapToJson(((TraversableOnce)environmentDetails.apply("System Properties")).toMap(Predef$.MODULE$.$conforms()));
      JsonAST.JValue classpathEntries = this.mapToJson(((TraversableOnce)environmentDetails.apply("Classpath Entries")).toMap(Predef$.MODULE$.$conforms()));
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.environmentUpdate()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("JVM Information"), jvmInformation), Predef$.MODULE$.$conforms())).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Spark Properties"), sparkProperties))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("System Properties"), systemProperties))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Classpath Entries"), classpathEntries));
   }

   public JsonAST.JValue blockManagerAddedToJson(SparkListenerBlockManagerAdded blockManagerAdded) {
      JsonAST.JValue blockManagerId = this.blockManagerIdToJson(blockManagerAdded.blockManagerId());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockManagerAdded()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block Manager ID"), blockManagerId), Predef$.MODULE$.$conforms())).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Maximum Memory"), BoxesRunTime.boxToLong(blockManagerAdded.maxMem())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(blockManagerAdded.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Maximum Onheap Memory"), blockManagerAdded.maxOnHeapMem()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<Object> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(long x) {
                  return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
               }
            });
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Maximum Offheap Memory"), blockManagerAdded.maxOffHeapMem()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<Object> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(long x) {
                  return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
               }
            });
         }
      }));
   }

   public JsonAST.JValue blockManagerRemovedToJson(SparkListenerBlockManagerRemoved blockManagerRemoved) {
      JsonAST.JValue blockManagerId = this.blockManagerIdToJson(blockManagerRemoved.blockManagerId());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockManagerRemoved()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block Manager ID"), blockManagerId), Predef$.MODULE$.$conforms())).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(blockManagerRemoved.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue unpersistRDDToJson(SparkListenerUnpersistRDD unpersistRDD) {
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.unpersistRDD()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("RDD ID"), BoxesRunTime.boxToInteger(unpersistRDD.rddId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      });
   }

   public JsonAST.JValue applicationStartToJson(SparkListenerApplicationStart applicationStart) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.applicationStart()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("App Name"), applicationStart.appName()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      })).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("App ID"), applicationStart.appId().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JString apply(String x$1) {
            return new JsonAST.JString(x$1);
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      })))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(applicationStart.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("User"), applicationStart.sparkUser()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("App Attempt ID"), applicationStart.appAttemptId().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JString apply(String x$2) {
            return new JsonAST.JString(x$2);
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      })))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Driver Logs"), applicationStart.driverLogs().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Map<String, String> m) {
            return JsonProtocol$.MODULE$.mapToJson(m);
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      })));
   }

   public JsonAST.JValue applicationEndToJson(SparkListenerApplicationEnd applicationEnd) {
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.applicationEnd()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(applicationEnd.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      });
   }

   public JsonAST.JValue executorAddedToJson(SparkListenerExecutorAdded executorAdded) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.executorAdded()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(executorAdded.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), executorAdded.executorId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor Info"), this.executorInfoToJson(executorAdded.executorInfo())));
   }

   public JsonAST.JValue executorRemovedToJson(SparkListenerExecutorRemoved executorRemoved) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.executorRemoved()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Timestamp"), BoxesRunTime.boxToLong(executorRemoved.time())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), executorRemoved.executorId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Removed Reason"), executorRemoved.reason()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue logStartToJson(SparkListenerLogStart logStart) {
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.logStart()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Spark Version"), org.apache.spark.package$.MODULE$.SPARK_VERSION()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      });
   }

   public JsonAST.JValue executorMetricsUpdateToJson(SparkListenerExecutorMetricsUpdate metricsUpdate) {
      String execId = metricsUpdate.execId();
      Seq accumUpdates = metricsUpdate.accumUpdates();
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.metricsUpdate()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), execId), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Metrics Updated"), accumUpdates.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(Tuple4<Object, Object, Object, Seq<AccumulableInfo>> x0$1) {
            if (x0$1 != null) {
               long taskId = BoxesRunTime.unboxToLong(x0$1._1());
               int stageId = BoxesRunTime.unboxToInt(x0$1._2());
               int stageAttemptId = BoxesRunTime.unboxToInt(x0$1._3());
               Seq updates = (Seq)x0$1._4();
               JsonAST.JObject var8 = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task ID"), BoxesRunTime.boxToLong(taskId)), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(long x) {
                     return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
                  }
               }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage ID"), BoxesRunTime.boxToInteger(stageId)), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(int x) {
                     return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
                  }
               })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Attempt ID"), BoxesRunTime.boxToInteger(stageAttemptId)), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(int x) {
                     return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
                  }
               }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Accumulator Updates"), new JsonAST.JArray(((TraversableOnce)updates.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(AccumulableInfo accumulableInfo) {
                     return JsonProtocol$.MODULE$.accumulableInfoToJson(accumulableInfo);
                  }
               }, Seq$.MODULE$.canBuildFrom())).toList())));
               return var8;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Seq$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return org.json4s.JsonDSL..MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }));
   }

   public JsonAST.JValue blockUpdateToJson(SparkListenerBlockUpdated blockUpdate) {
      JsonAST.JValue blockUpdatedInfo = this.blockUpdatedInfoToJson(blockUpdate.blockUpdatedInfo());
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event"), JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockUpdate()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block Updated Info"), blockUpdatedInfo), Predef$.MODULE$.$conforms());
   }

   public JsonAST.JValue stageInfoToJson(StageInfo stageInfo) {
      JsonAST.JArray rddInfo = new JsonAST.JArray(((TraversableOnce)stageInfo.rddInfos().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(RDDInfo rddInfo) {
            return JsonProtocol$.MODULE$.rddInfoToJson(rddInfo);
         }
      }, Seq$.MODULE$.canBuildFrom())).toList());
      JsonAST.JArray parentIds = new JsonAST.JArray(((TraversableOnce)stageInfo.parentIds().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JInt apply(int x$3) {
            return new JsonAST.JInt(BigInt$.MODULE$.int2bigInt(x$3));
         }
      }, Seq$.MODULE$.canBuildFrom())).toList());
      JsonAST.JValue submissionTime = (JsonAST.JValue)stageInfo.submissionTime().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JInt apply(long x$4) {
            return new JsonAST.JInt(BigInt$.MODULE$.long2bigInt(x$4));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      });
      JsonAST.JValue completionTime = (JsonAST.JValue)stageInfo.completionTime().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JInt apply(long x$5) {
            return new JsonAST.JInt(BigInt$.MODULE$.long2bigInt(x$5));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      });
      JsonAST.JValue failureReason = (JsonAST.JValue)stageInfo.failureReason().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JString apply(String x$6) {
            return new JsonAST.JString(x$6);
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      });
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage ID"), BoxesRunTime.boxToInteger(stageInfo.stageId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Attempt ID"), BoxesRunTime.boxToInteger(stageInfo.attemptNumber())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stage Name"), stageInfo.name()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Number of Tasks"), BoxesRunTime.boxToInteger(stageInfo.numTasks())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("RDD Info"), rddInfo))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Parent IDs"), parentIds))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Details"), stageInfo.details()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Submission Time"), submissionTime))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Completion Time"), completionTime))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Failure Reason"), failureReason))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Accumulables"), this.accumulablesToJson(stageInfo.accumulables().values())));
   }

   public JsonAST.JValue taskInfoToJson(TaskInfo taskInfo) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Task ID"), BoxesRunTime.boxToLong(taskInfo.taskId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Index"), BoxesRunTime.boxToInteger(taskInfo.index())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Attempt"), BoxesRunTime.boxToInteger(taskInfo.attemptNumber())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Launch Time"), BoxesRunTime.boxToLong(taskInfo.launchTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), taskInfo.executorId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Host"), taskInfo.host()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Locality"), taskInfo.taskLocality().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Speculative"), BoxesRunTime.boxToBoolean(taskInfo.speculative())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Getting Result Time"), BoxesRunTime.boxToLong(taskInfo.gettingResultTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Finish Time"), BoxesRunTime.boxToLong(taskInfo.finishTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Failed"), BoxesRunTime.boxToBoolean(taskInfo.failed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Killed"), BoxesRunTime.boxToBoolean(taskInfo.killed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Accumulables"), this.accumulablesToJson(taskInfo.accumulables())));
   }

   public Set<String> org$apache$spark$util$JsonProtocol$$accumulableBlacklist() {
      return this.bitmap$0 ? this.org$apache$spark$util$JsonProtocol$$accumulableBlacklist : this.org$apache$spark$util$JsonProtocol$$accumulableBlacklist$lzycompute();
   }

   public JsonAST.JArray accumulablesToJson(Traversable<AccumulableInfo> accumulables) {
      return new JsonAST.JArray((List)((TraversableOnce)accumulables.filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(AccumulableInfo x$7) {
            return x$7.name().exists(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String elem) {
                  return JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$accumulableBlacklist().contains(elem);
               }
            });
         }
      })).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(AccumulableInfo accumulableInfo) {
            return JsonProtocol$.MODULE$.accumulableInfoToJson(accumulableInfo);
         }
      }, List$.MODULE$.canBuildFrom()));
   }

   public JsonAST.JValue accumulableInfoToJson(AccumulableInfo accumulableInfo) {
      Option name = accumulableInfo.name();
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("ID"), BoxesRunTime.boxToLong(accumulableInfo.id())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Name"), name), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<String> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            });
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Update"), accumulableInfo.update().map(new Serializable(name) {
         public static final long serialVersionUID = 0L;
         private final Option name$1;

         public final JsonAST.JValue apply(Object v) {
            return JsonProtocol$.MODULE$.accumValueToJson(this.name$1, v);
         }

         public {
            this.name$1 = name$1;
         }
      })), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<JsonAST.JValue> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Value"), accumulableInfo.value().map(new Serializable(name) {
         public static final long serialVersionUID = 0L;
         private final Option name$1;

         public final JsonAST.JValue apply(Object v) {
            return JsonProtocol$.MODULE$.accumValueToJson(this.name$1, v);
         }

         public {
            this.name$1 = name$1;
         }
      })), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<JsonAST.JValue> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Internal"), BoxesRunTime.boxToBoolean(accumulableInfo.internal())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Count Failed Values"), BoxesRunTime.boxToBoolean(accumulableInfo.countFailedValues())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Metadata"), accumulableInfo.metadata()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<String> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            });
         }
      }));
   }

   public JsonAST.JValue accumValueToJson(Option<String> name, Object value) {
      Object var10000;
      if (name.exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$8) {
            return x$8.startsWith(InternalAccumulator$.MODULE$.METRICS_PREFIX());
         }
      })) {
         Object var5;
         if (value instanceof Integer) {
            int var4 = BoxesRunTime.unboxToInt(value);
            var5 = new JsonAST.JInt(BigInt$.MODULE$.int2bigInt(var4));
         } else if (value instanceof Long) {
            long var6 = BoxesRunTime.unboxToLong(value);
            var5 = new JsonAST.JInt(BigInt$.MODULE$.long2bigInt(var6));
         } else {
            var5 = new JsonAST.JArray((List)((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter((java.util.List)value).asScala()).toList().map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JObject apply(Tuple2<BlockId, BlockStatus> x0$2) {
                  if (x0$2 != null) {
                     BlockId id = (BlockId)x0$2._1();
                     BlockStatus status = (BlockStatus)x0$2._2();
                     JsonAST.JObject var5 = org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block ID"), id.toString()), new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final JsonAST.JValue apply(String x) {
                           return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
                        }
                     }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Status"), JsonProtocol$.MODULE$.blockStatusToJson(status)), Predef$.MODULE$.$conforms());
                     return var5;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }
            }, List$.MODULE$.canBuildFrom()));
         }

         var10000 = var5;
      } else {
         var10000 = new JsonAST.JString(value.toString());
      }

      return (JsonAST.JValue)var10000;
   }

   public JsonAST.JValue taskMetricsToJson(TaskMetrics taskMetrics) {
      JsonAST.JValue shuffleReadMetrics = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Remote Blocks Fetched"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().remoteBlocksFetched())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Local Blocks Fetched"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().localBlocksFetched())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Fetch Wait Time"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().fetchWaitTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Remote Bytes Read"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().remoteBytesRead())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Remote Bytes Read To Disk"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().remoteBytesReadToDisk())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Local Bytes Read"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().localBytesRead())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Total Records Read"), BoxesRunTime.boxToLong(taskMetrics.shuffleReadMetrics().recordsRead())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
      JsonAST.JValue shuffleWriteMetrics = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Bytes Written"), BoxesRunTime.boxToLong(taskMetrics.shuffleWriteMetrics().bytesWritten())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Write Time"), BoxesRunTime.boxToLong(taskMetrics.shuffleWriteMetrics().writeTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Records Written"), BoxesRunTime.boxToLong(taskMetrics.shuffleWriteMetrics().recordsWritten())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
      JsonAST.JValue inputMetrics = org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Bytes Read"), BoxesRunTime.boxToLong(taskMetrics.inputMetrics().bytesRead())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Records Read"), BoxesRunTime.boxToLong(taskMetrics.inputMetrics().recordsRead())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      });
      JsonAST.JValue outputMetrics = org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Bytes Written"), BoxesRunTime.boxToLong(taskMetrics.outputMetrics().bytesWritten())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Records Written"), BoxesRunTime.boxToLong(taskMetrics.outputMetrics().recordsWritten())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      });
      JsonAST.JArray updatedBlocks = new JsonAST.JArray((List)taskMetrics.updatedBlockStatuses().toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(Tuple2<BlockId, BlockStatus> x0$3) {
            if (x0$3 != null) {
               BlockId id = (BlockId)x0$3._1();
               BlockStatus status = (BlockStatus)x0$3._2();
               JsonAST.JObject var5 = org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block ID"), id.toString()), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(String x) {
                     return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
                  }
               }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Status"), JsonProtocol$.MODULE$.blockStatusToJson(status)), Predef$.MODULE$.$conforms());
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }
      }, List$.MODULE$.canBuildFrom()));
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor Deserialize Time"), BoxesRunTime.boxToLong(taskMetrics.executorDeserializeTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor Deserialize CPU Time"), BoxesRunTime.boxToLong(taskMetrics.executorDeserializeCpuTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor Run Time"), BoxesRunTime.boxToLong(taskMetrics.executorRunTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor CPU Time"), BoxesRunTime.boxToLong(taskMetrics.executorCpuTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Result Size"), BoxesRunTime.boxToLong(taskMetrics.resultSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("JVM GC Time"), BoxesRunTime.boxToLong(taskMetrics.jvmGCTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Result Serialization Time"), BoxesRunTime.boxToLong(taskMetrics.resultSerializationTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Memory Bytes Spilled"), BoxesRunTime.boxToLong(taskMetrics.memoryBytesSpilled())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Disk Bytes Spilled"), BoxesRunTime.boxToLong(taskMetrics.diskBytesSpilled())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Read Metrics"), shuffleReadMetrics))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle Write Metrics"), shuffleWriteMetrics))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Input Metrics"), inputMetrics))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Output Metrics"), outputMetrics))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Updated Blocks"), updatedBlocks));
   }

   public JsonAST.JValue taskEndReasonToJson(TaskEndReason taskEndReason) {
      String reason = Utils$.MODULE$.getFormattedClassName(taskEndReason);
      JsonAST.JObject json;
      if (taskEndReason instanceof FetchFailed) {
         FetchFailed var5 = (FetchFailed)taskEndReason;
         JsonAST.JValue blockManagerAddress = (JsonAST.JValue)Option$.MODULE$.apply(var5.bmAddress()).map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(BlockManagerId blockManagerId) {
               return JsonProtocol$.MODULE$.blockManagerIdToJson(blockManagerId);
            }
         }).getOrElse(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JNothing apply() {
               return org.json4s.JsonAST.JNothing..MODULE$;
            }
         });
         json = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block Manager Address"), blockManagerAddress), Predef$.MODULE$.$conforms()).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Shuffle ID"), BoxesRunTime.boxToInteger(var5.shuffleId())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Map ID"), BoxesRunTime.boxToInteger(var5.mapId())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Reduce ID"), BoxesRunTime.boxToInteger(var5.reduceId())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Message"), var5.message()), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         }));
      } else if (taskEndReason instanceof ExceptionFailure) {
         ExceptionFailure var8 = (ExceptionFailure)taskEndReason;
         JsonAST.JValue stackTrace = this.stackTraceToJson(var8.stackTrace());
         JsonAST.JArray accumUpdates = this.accumulablesToJson(var8.accumUpdates());
         json = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Class Name"), var8.className()), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Description"), var8.description()), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         })).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stack Trace"), stackTrace))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Full Stack Trace"), var8.fullStackTrace()), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Accumulator Updates"), accumUpdates));
      } else if (taskEndReason instanceof TaskCommitDenied) {
         TaskCommitDenied var11 = (TaskCommitDenied)taskEndReason;
         json = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Job ID"), BoxesRunTime.boxToInteger(var11.jobID())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Partition ID"), BoxesRunTime.boxToInteger(var11.partitionID())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Attempt Number"), BoxesRunTime.boxToInteger(var11.attemptNumber())), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(int x) {
               return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
            }
         }));
      } else if (taskEndReason instanceof ExecutorLostFailure) {
         ExecutorLostFailure var12 = (ExecutorLostFailure)taskEndReason;
         String executorId = var12.execId();
         boolean exitCausedByApp = var12.exitCausedByApp();
         Option reason = var12.reason();
         json = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), executorId), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Exit Caused By App"), BoxesRunTime.boxToBoolean(exitCausedByApp)), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(boolean x) {
               return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
            }
         })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Loss Reason"), reason.map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply(String x$9) {
               return x$9.toString();
            }
         })), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(Option<String> opt) {
               return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final JsonAST.JValue apply(String x) {
                     return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
                  }
               });
            }
         }));
      } else if (taskEndReason instanceof TaskKilled) {
         TaskKilled var16 = (TaskKilled)taskEndReason;
         json = org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Kill Reason"), var16.reason()), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final JsonAST.JValue apply(String x) {
               return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
            }
         });
      } else {
         json = Utils$.MODULE$.emptyJson();
      }

      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Reason"), reason), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(json);
   }

   public JsonAST.JValue blockManagerIdToJson(BlockManagerId blockManagerId) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Executor ID"), blockManagerId.executorId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Host"), blockManagerId.host()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Port"), BoxesRunTime.boxToInteger(blockManagerId.port())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue jobResultToJson(JobResult jobResult) {
      String result = Utils$.MODULE$.getFormattedClassName(jobResult);
      JsonAST.JObject json;
      if (JobSucceeded$.MODULE$.equals(jobResult)) {
         json = Utils$.MODULE$.emptyJson();
      } else {
         if (!(jobResult instanceof JobFailed)) {
            throw new MatchError(jobResult);
         }

         JobFailed var6 = (JobFailed)jobResult;
         json = org.json4s.JsonAST.JObject..MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Exception"), this.exceptionToJson(var6.exception()))}));
      }

      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Result"), result), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(json);
   }

   public JsonAST.JValue rddInfoToJson(RDDInfo rddInfo) {
      JsonAST.JValue storageLevel = this.storageLevelToJson(rddInfo.storageLevel());
      JsonAST.JArray parentIds = new JsonAST.JArray(((TraversableOnce)rddInfo.parentIds().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JInt apply(int x$10) {
            return new JsonAST.JInt(BigInt$.MODULE$.int2bigInt(x$10));
         }
      }, Seq$.MODULE$.canBuildFrom())).toList());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("RDD ID"), BoxesRunTime.boxToInteger(rddInfo.id())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Name"), rddInfo.name()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Scope"), rddInfo.scope().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(RDDOperationScope x$11) {
            return x$11.toJson();
         }
      })), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Option<String> opt) {
            return org.json4s.JsonDSL..MODULE$.option2jvalue(opt, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            });
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Callsite"), rddInfo.callSite()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Parent IDs"), parentIds))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Storage Level"), storageLevel))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Number of Partitions"), BoxesRunTime.boxToInteger(rddInfo.numPartitions())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Number of Cached Partitions"), BoxesRunTime.boxToInteger(rddInfo.numCachedPartitions())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Memory Size"), BoxesRunTime.boxToLong(rddInfo.memSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Disk Size"), BoxesRunTime.boxToLong(rddInfo.diskSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue storageLevelToJson(StorageLevel storageLevel) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Use Disk"), BoxesRunTime.boxToBoolean(storageLevel.useDisk())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Use Memory"), BoxesRunTime.boxToBoolean(storageLevel.useMemory())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Deserialized"), BoxesRunTime.boxToBoolean(storageLevel.deserialized())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(boolean x) {
            return org.json4s.JsonDSL..MODULE$.boolean2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Replication"), BoxesRunTime.boxToInteger(storageLevel.replication())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue blockStatusToJson(BlockStatus blockStatus) {
      JsonAST.JValue storageLevel = this.storageLevelToJson(blockStatus.storageLevel());
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Storage Level"), storageLevel), Predef$.MODULE$.$conforms()).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Memory Size"), BoxesRunTime.boxToLong(blockStatus.memSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Disk Size"), BoxesRunTime.boxToLong(blockStatus.diskSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue executorInfoToJson(ExecutorInfo executorInfo) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Host"), executorInfo.executorHost()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Total Cores"), BoxesRunTime.boxToInteger(executorInfo.totalCores())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
         }
      })).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Log Urls"), this.mapToJson(executorInfo.logUrlMap())));
   }

   public JsonAST.JValue blockUpdatedInfoToJson(BlockUpdatedInfo blockUpdatedInfo) {
      return org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block Manager ID"), this.blockManagerIdToJson(blockUpdatedInfo.blockManagerId())), Predef$.MODULE$.$conforms()).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Block ID"), blockUpdatedInfo.blockId().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      })).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Storage Level"), this.storageLevelToJson(blockUpdatedInfo.storageLevel())))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Memory Size"), BoxesRunTime.boxToLong(blockUpdatedInfo.memSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Disk Size"), BoxesRunTime.boxToLong(blockUpdatedInfo.diskSize())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JValue mapToJson(Map<String, String> m) {
      Map jsonFields = (Map)m.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, JsonAST.JValue> apply(Tuple2<String, String> x0$4) {
            if (x0$4 != null) {
               String k = (String)x0$4._1();
               String v = (String)x0$4._2();
               Tuple2 var5 = org.json4s.JsonAST.JField..MODULE$.apply(k, new JsonAST.JString(v));
               return var5;
            } else {
               throw new MatchError(x0$4);
            }
         }
      }, Map$.MODULE$.canBuildFrom());
      return new JsonAST.JObject(jsonFields.toList());
   }

   public JsonAST.JValue propertiesToJson(Properties properties) {
      return (JsonAST.JValue)Option$.MODULE$.apply(properties).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(Properties p) {
            return JsonProtocol$.MODULE$.mapToJson((Map)JavaConverters$.MODULE$.propertiesAsScalaMapConverter(p).asScala());
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JNothing apply() {
            return org.json4s.JsonAST.JNothing..MODULE$;
         }
      });
   }

   public JsonAST.JValue UUIDToJson(UUID id) {
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Least Significant Bits"), BoxesRunTime.boxToLong(id.getLeastSignificantBits())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Most Significant Bits"), BoxesRunTime.boxToLong(id.getMostSignificantBits())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return org.json4s.JsonDSL..MODULE$.long2jvalue(x);
         }
      });
   }

   public JsonAST.JValue stackTraceToJson(StackTraceElement[] stackTrace) {
      return new JsonAST.JArray(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])stackTrace).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(StackTraceElement x0$5) {
            JsonAST.JObject var3 = org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.jobject2assoc(org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Declaring Class"), x0$5.getClassName()), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Method Name"), x0$5.getMethodName()), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            })).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("File Name"), x0$5.getFileName()), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(String x) {
                  return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
               }
            }))).$tilde(org.json4s.JsonDSL..MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Line Number"), BoxesRunTime.boxToInteger(x0$5.getLineNumber())), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final JsonAST.JValue apply(int x) {
                  return org.json4s.JsonDSL..MODULE$.int2jvalue(x);
               }
            }));
            return var3;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(JsonAST.JObject.class)))).toList());
   }

   public JsonAST.JValue exceptionToJson(Exception exception) {
      return org.json4s.JsonDSL..MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Message"), exception.getMessage()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return org.json4s.JsonDSL..MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Stack Trace"), this.stackTraceToJson(exception.getStackTrace())), Predef$.MODULE$.$conforms());
   }

   public SparkListenerEvent sparkEventFromJson(JsonAST.JValue json) {
      Object var4;
      label295: {
         String var2 = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Event")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         String var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.stageSubmitted();
         if (var10000 == null) {
            if (var2 == null) {
               break label295;
            }
         } else if (var10000.equals(var2)) {
            break label295;
         }

         label296: {
            var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.stageCompleted();
            if (var10000 == null) {
               if (var2 == null) {
                  break label296;
               }
            } else if (var10000.equals(var2)) {
               break label296;
            }

            label297: {
               var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskStart();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label297;
                  }
               } else if (var10000.equals(var2)) {
                  break label297;
               }

               label298: {
                  var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskGettingResult();
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label298;
                     }
                  } else if (var10000.equals(var2)) {
                     break label298;
                  }

                  label299: {
                     var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.taskEnd();
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label299;
                        }
                     } else if (var10000.equals(var2)) {
                        break label299;
                     }

                     label300: {
                        var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.jobStart();
                        if (var10000 == null) {
                           if (var2 == null) {
                              break label300;
                           }
                        } else if (var10000.equals(var2)) {
                           break label300;
                        }

                        label301: {
                           var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.jobEnd();
                           if (var10000 == null) {
                              if (var2 == null) {
                                 break label301;
                              }
                           } else if (var10000.equals(var2)) {
                              break label301;
                           }

                           label302: {
                              var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.environmentUpdate();
                              if (var10000 == null) {
                                 if (var2 == null) {
                                    break label302;
                                 }
                              } else if (var10000.equals(var2)) {
                                 break label302;
                              }

                              label303: {
                                 var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockManagerAdded();
                                 if (var10000 == null) {
                                    if (var2 == null) {
                                       break label303;
                                    }
                                 } else if (var10000.equals(var2)) {
                                    break label303;
                                 }

                                 label304: {
                                    var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockManagerRemoved();
                                    if (var10000 == null) {
                                       if (var2 == null) {
                                          break label304;
                                       }
                                    } else if (var10000.equals(var2)) {
                                       break label304;
                                    }

                                    label305: {
                                       var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.unpersistRDD();
                                       if (var10000 == null) {
                                          if (var2 == null) {
                                             break label305;
                                          }
                                       } else if (var10000.equals(var2)) {
                                          break label305;
                                       }

                                       label306: {
                                          var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.applicationStart();
                                          if (var10000 == null) {
                                             if (var2 == null) {
                                                break label306;
                                             }
                                          } else if (var10000.equals(var2)) {
                                             break label306;
                                          }

                                          label307: {
                                             var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.applicationEnd();
                                             if (var10000 == null) {
                                                if (var2 == null) {
                                                   break label307;
                                                }
                                             } else if (var10000.equals(var2)) {
                                                break label307;
                                             }

                                             label308: {
                                                var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.executorAdded();
                                                if (var10000 == null) {
                                                   if (var2 == null) {
                                                      break label308;
                                                   }
                                                } else if (var10000.equals(var2)) {
                                                   break label308;
                                                }

                                                label309: {
                                                   var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.executorRemoved();
                                                   if (var10000 == null) {
                                                      if (var2 == null) {
                                                         break label309;
                                                      }
                                                   } else if (var10000.equals(var2)) {
                                                      break label309;
                                                   }

                                                   label310: {
                                                      var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.logStart();
                                                      if (var10000 == null) {
                                                         if (var2 == null) {
                                                            break label310;
                                                         }
                                                      } else if (var10000.equals(var2)) {
                                                         break label310;
                                                      }

                                                      label311: {
                                                         var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.metricsUpdate();
                                                         if (var10000 == null) {
                                                            if (var2 == null) {
                                                               break label311;
                                                            }
                                                         } else if (var10000.equals(var2)) {
                                                            break label311;
                                                         }

                                                         label312: {
                                                            var10000 = JsonProtocol.SPARK_LISTENER_EVENT_FORMATTED_CLASS_NAMES$.MODULE$.blockUpdate();
                                                            if (var10000 == null) {
                                                               if (var2 == null) {
                                                                  break label312;
                                                               }
                                                            } else if (var10000.equals(var2)) {
                                                               break label312;
                                                            }

                                                            var4 = (SparkListenerEvent)this.mapper().readValue(.MODULE$.compact(.MODULE$.render(json, this.org$apache$spark$util$JsonProtocol$$format())), Utils$.MODULE$.classForName(var2));
                                                            return (SparkListenerEvent)var4;
                                                         }

                                                         var4 = this.blockUpdateFromJson(json);
                                                         return (SparkListenerEvent)var4;
                                                      }

                                                      var4 = this.executorMetricsUpdateFromJson(json);
                                                      return (SparkListenerEvent)var4;
                                                   }

                                                   var4 = this.logStartFromJson(json);
                                                   return (SparkListenerEvent)var4;
                                                }

                                                var4 = this.executorRemovedFromJson(json);
                                                return (SparkListenerEvent)var4;
                                             }

                                             var4 = this.executorAddedFromJson(json);
                                             return (SparkListenerEvent)var4;
                                          }

                                          var4 = this.applicationEndFromJson(json);
                                          return (SparkListenerEvent)var4;
                                       }

                                       var4 = this.applicationStartFromJson(json);
                                       return (SparkListenerEvent)var4;
                                    }

                                    var4 = this.unpersistRDDFromJson(json);
                                    return (SparkListenerEvent)var4;
                                 }

                                 var4 = this.blockManagerRemovedFromJson(json);
                                 return (SparkListenerEvent)var4;
                              }

                              var4 = this.blockManagerAddedFromJson(json);
                              return (SparkListenerEvent)var4;
                           }

                           var4 = this.environmentUpdateFromJson(json);
                           return (SparkListenerEvent)var4;
                        }

                        var4 = this.jobEndFromJson(json);
                        return (SparkListenerEvent)var4;
                     }

                     var4 = this.jobStartFromJson(json);
                     return (SparkListenerEvent)var4;
                  }

                  var4 = this.taskEndFromJson(json);
                  return (SparkListenerEvent)var4;
               }

               var4 = this.taskGettingResultFromJson(json);
               return (SparkListenerEvent)var4;
            }

            var4 = this.taskStartFromJson(json);
            return (SparkListenerEvent)var4;
         }

         var4 = this.stageCompletedFromJson(json);
         return (SparkListenerEvent)var4;
      }

      var4 = this.stageSubmittedFromJson(json);
      return (SparkListenerEvent)var4;
   }

   public SparkListenerStageSubmitted stageSubmittedFromJson(JsonAST.JValue json) {
      StageInfo stageInfo = this.stageInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Info"));
      Properties properties = this.propertiesFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Properties"));
      return new SparkListenerStageSubmitted(stageInfo, properties);
   }

   public SparkListenerStageCompleted stageCompletedFromJson(JsonAST.JValue json) {
      StageInfo stageInfo = this.stageInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Info"));
      return new SparkListenerStageCompleted(stageInfo);
   }

   public SparkListenerTaskStart taskStartFromJson(JsonAST.JValue json) {
      int stageId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      int stageAttemptId = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Attempt ID")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(JsonAST.JValue x$12) {
            return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$12).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
      TaskInfo taskInfo = this.taskInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task Info"));
      return new SparkListenerTaskStart(stageId, stageAttemptId, taskInfo);
   }

   public SparkListenerTaskGettingResult taskGettingResultFromJson(JsonAST.JValue json) {
      TaskInfo taskInfo = this.taskInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task Info"));
      return new SparkListenerTaskGettingResult(taskInfo);
   }

   public SparkListenerTaskEnd taskEndFromJson(JsonAST.JValue json) {
      int stageId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      int stageAttemptId = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Attempt ID")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(JsonAST.JValue x$13) {
            return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$13).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
      String taskType = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task Type")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      TaskEndReason taskEndReason = this.taskEndReasonFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task End Reason"));
      TaskInfo taskInfo = this.taskInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task Info"));
      TaskMetrics taskMetrics = this.taskMetricsFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task Metrics"));
      return new SparkListenerTaskEnd(stageId, stageAttemptId, taskType, taskEndReason, taskInfo, taskMetrics);
   }

   public SparkListenerJobStart jobStartFromJson(JsonAST.JValue json) {
      int jobId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Job ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      long submissionTime = BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Submission Time")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$14) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$14).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      }));
      List stageIds = (List)((List)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage IDs")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(JsonAST.JValue x$15) {
            return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$15).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         }
      }, List$.MODULE$.canBuildFrom());
      Properties properties = this.propertiesFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Properties"));
      Seq stageInfos = (Seq)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Infos")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<StageInfo> apply(JsonAST.JValue x$16) {
            return (Seq)((TraversableLike)org.json4s.package..MODULE$.jvalue2extractable(x$16).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(Seq.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StageInfo apply(JsonAST.JValue json) {
                  return JsonProtocol$.MODULE$.stageInfoFromJson(json);
               }
            }, Seq$.MODULE$.canBuildFrom());
         }
      }).getOrElse(new Serializable(stageIds) {
         public static final long serialVersionUID = 0L;
         private final List stageIds$1;

         public final List<StageInfo> apply() {
            return (List)this.stageIds$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StageInfo apply(int id) {
                  return new StageInfo(id, 0, "unknown", 0, (Seq)Seq$.MODULE$.empty(), (Seq)Seq$.MODULE$.empty(), "unknown", StageInfo$.MODULE$.$lessinit$greater$default$8(), StageInfo$.MODULE$.$lessinit$greater$default$9());
               }
            }, List$.MODULE$.canBuildFrom());
         }

         public {
            this.stageIds$1 = stageIds$1;
         }
      });
      return new SparkListenerJobStart(jobId, submissionTime, stageInfos, properties);
   }

   public SparkListenerJobEnd jobEndFromJson(JsonAST.JValue json) {
      int jobId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Job ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      long completionTime = BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Completion Time")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$17) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$17).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      }));
      JobResult jobResult = this.jobResultFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Job Result"));
      return new SparkListenerJobEnd(jobId, completionTime, jobResult);
   }

   public SparkListenerEnvironmentUpdate environmentUpdateFromJson(JsonAST.JValue json) {
      Map environmentDetails = (Map)Map$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("JVM Information"), this.mapFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("JVM Information")).toSeq()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Spark Properties"), this.mapFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Spark Properties")).toSeq()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("System Properties"), this.mapFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("System Properties")).toSeq()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Classpath Entries"), this.mapFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Classpath Entries")).toSeq())}));
      return new SparkListenerEnvironmentUpdate(environmentDetails);
   }

   public SparkListenerBlockManagerAdded blockManagerAddedFromJson(JsonAST.JValue json) {
      BlockManagerId blockManagerId = this.blockManagerIdFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block Manager ID"));
      long maxMem = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Maximum Memory")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long time = BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$18) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$18).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      }));
      Option maxOnHeapMem = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Maximum Onheap Memory")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$19) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$19).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      });
      Option maxOffHeapMem = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Maximum Offheap Memory")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$20) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$20).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      });
      return new SparkListenerBlockManagerAdded(time, blockManagerId, maxMem, maxOnHeapMem, maxOffHeapMem);
   }

   public SparkListenerBlockManagerRemoved blockManagerRemovedFromJson(JsonAST.JValue json) {
      BlockManagerId blockManagerId = this.blockManagerIdFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block Manager ID"));
      long time = BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$21) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$21).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      }));
      return new SparkListenerBlockManagerRemoved(time, blockManagerId);
   }

   public SparkListenerUnpersistRDD unpersistRDDFromJson(JsonAST.JValue json) {
      return new SparkListenerUnpersistRDD(BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("RDD ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int())));
   }

   public SparkListenerApplicationStart applicationStartFromJson(JsonAST.JValue json) {
      String appName = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("App Name")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      Option appId = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("App ID")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$22) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$22).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      });
      long time = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      String sparkUser = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("User")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      Option appAttemptId = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("App Attempt ID")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$23) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$23).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      });
      Option driverLogs = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Driver Logs")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Map<String, String> apply(JsonAST.JValue json) {
            return JsonProtocol$.MODULE$.mapFromJson(json);
         }
      });
      return new SparkListenerApplicationStart(appName, appId, time, sparkUser, appAttemptId, driverLogs);
   }

   public SparkListenerApplicationEnd applicationEndFromJson(JsonAST.JValue json) {
      return new SparkListenerApplicationEnd(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
   }

   public SparkListenerExecutorAdded executorAddedFromJson(JsonAST.JValue json) {
      long time = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      String executorId = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      ExecutorInfo executorInfo = this.executorInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor Info"));
      return new SparkListenerExecutorAdded(time, executorId, executorInfo);
   }

   public SparkListenerExecutorRemoved executorRemovedFromJson(JsonAST.JValue json) {
      long time = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Timestamp")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      String executorId = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      String reason = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Removed Reason")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      return new SparkListenerExecutorRemoved(time, executorId, reason);
   }

   public SparkListenerLogStart logStartFromJson(JsonAST.JValue json) {
      String sparkVersion = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Spark Version")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      return new SparkListenerLogStart(sparkVersion);
   }

   public SparkListenerExecutorMetricsUpdate executorMetricsUpdateFromJson(JsonAST.JValue json) {
      String execInfo = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      List accumUpdates = (List)((List)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Metrics Updated")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple4<Object, Object, Object, List<AccumulableInfo>> apply(JsonAST.JValue json) {
            long taskId = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task ID")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
            int stageId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage ID")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            int stageAttemptId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Attempt ID")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            List updates = (List)((List)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Accumulator Updates")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final AccumulableInfo apply(JsonAST.JValue json) {
                  return JsonProtocol$.MODULE$.accumulableInfoFromJson(json);
               }
            }, List$.MODULE$.canBuildFrom());
            return new Tuple4<Object, Object, Object, List<AccumulableInfo>>(BoxesRunTime.boxToLong(taskId), BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(stageAttemptId), updates);
         }
      }, List$.MODULE$.canBuildFrom());
      return new SparkListenerExecutorMetricsUpdate(execInfo, accumUpdates);
   }

   public SparkListenerBlockUpdated blockUpdateFromJson(JsonAST.JValue json) {
      BlockUpdatedInfo blockUpdatedInfo = this.blockUpdatedInfoFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block Updated Info"));
      return new SparkListenerBlockUpdated(blockUpdatedInfo);
   }

   public StageInfo stageInfoFromJson(JsonAST.JValue json) {
      int stageId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      int attemptId = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Attempt ID")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(JsonAST.JValue x$24) {
            return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$24).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
      String stageName = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stage Name")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      int numTasks = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Number of Tasks")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      List rddInfos = (List)((List)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("RDD Info")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final RDDInfo apply(JsonAST.JValue json) {
            return JsonProtocol$.MODULE$.rddInfoFromJson(json);
         }
      }, List$.MODULE$.canBuildFrom());
      Seq parentIds = (Seq)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Parent IDs")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final List<Object> apply(JsonAST.JValue l) {
            return (List)((List)org.json4s.package..MODULE$.jvalue2extractable(l).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(JsonAST.JValue x$25) {
                  return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$25).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
               }
            }, List$.MODULE$.canBuildFrom());
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      String details = (String)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Details")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$26) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$26).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      Option submissionTime = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Submission Time")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$27) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$27).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      });
      Option completionTime = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Completion Time")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(JsonAST.JValue x$28) {
            return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$28).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
         }
      });
      Option failureReason = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Failure Reason")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$29) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$29).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      });
      Option var13 = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Accumulables")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final List<JsonAST.JValue> apply(JsonAST.JValue x$30) {
            return (List)org.json4s.package..MODULE$.jvalue2extractable(x$30).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])));
         }
      });
      Seq accumulatedValues;
      if (var13 instanceof Some) {
         Some var14 = (Some)var13;
         List values = (List)var14.x();
         accumulatedValues = (Seq)values.map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final AccumulableInfo apply(JsonAST.JValue json) {
               return JsonProtocol$.MODULE$.accumulableInfoFromJson(json);
            }
         }, List$.MODULE$.canBuildFrom());
      } else {
         if (!None$.MODULE$.equals(var13)) {
            throw new MatchError(var13);
         }

         accumulatedValues = (Seq)Seq$.MODULE$.empty();
      }

      StageInfo stageInfo = new StageInfo(stageId, attemptId, stageName, numTasks, rddInfos, parentIds, details, StageInfo$.MODULE$.$lessinit$greater$default$8(), StageInfo$.MODULE$.$lessinit$greater$default$9());
      stageInfo.submissionTime_$eq(submissionTime);
      stageInfo.completionTime_$eq(completionTime);
      stageInfo.failureReason_$eq(failureReason);
      accumulatedValues.foreach(new Serializable(stageInfo) {
         public static final long serialVersionUID = 0L;
         private final StageInfo stageInfo$1;

         public final void apply(AccumulableInfo accInfo) {
            this.stageInfo$1.accumulables().update(BoxesRunTime.boxToLong(accInfo.id()), accInfo);
         }

         public {
            this.stageInfo$1 = stageInfo$1;
         }
      });
      return stageInfo;
   }

   public TaskInfo taskInfoFromJson(JsonAST.JValue json) {
      long taskId = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Task ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      int index = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Index")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      int attempt = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Attempt")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(JsonAST.JValue x$31) {
            return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$31).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 1;
         }
      }));
      long launchTime = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Launch Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      String executorId = ((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class))).intern();
      String host = ((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Host")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class))).intern();
      Enumeration.Value taskLocality = TaskLocality$.MODULE$.withName((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Locality")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class)));
      boolean speculative = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Speculative")).exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JsonAST.JValue x$32) {
            return BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(x$32).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
         }
      });
      long gettingResultTime = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Getting Result Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long finishTime = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Finish Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      boolean failed = BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Failed")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
      boolean killed = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Killed")).exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JsonAST.JValue x$33) {
            return BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(x$33).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
         }
      });
      Option var19 = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Accumulables")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<JsonAST.JValue> apply(JsonAST.JValue x$34) {
            return (Seq)org.json4s.package..MODULE$.jvalue2extractable(x$34).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(Seq.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])));
         }
      });
      Seq accumulables;
      if (var19 instanceof Some) {
         Some var20 = (Some)var19;
         Seq values = (Seq)var20.x();
         accumulables = (Seq)values.map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final AccumulableInfo apply(JsonAST.JValue json) {
               return JsonProtocol$.MODULE$.accumulableInfoFromJson(json);
            }
         }, Seq$.MODULE$.canBuildFrom());
      } else {
         if (!None$.MODULE$.equals(var19)) {
            throw new MatchError(var19);
         }

         accumulables = (Seq)Seq$.MODULE$.empty();
      }

      TaskInfo taskInfo = new TaskInfo(taskId, index, attempt, launchTime, executorId, host, taskLocality, speculative);
      taskInfo.gettingResultTime_$eq(gettingResultTime);
      taskInfo.finishTime_$eq(finishTime);
      taskInfo.failed_$eq(failed);
      taskInfo.killed_$eq(killed);
      taskInfo.setAccumulables(accumulables);
      return taskInfo;
   }

   public AccumulableInfo accumulableInfoFromJson(JsonAST.JValue json) {
      long id = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      Option name = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Name")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$35) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$35).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      });
      Option update = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Update")).map(new Serializable(name) {
         public static final long serialVersionUID = 0L;
         private final Option name$2;

         public final Object apply(JsonAST.JValue v) {
            return JsonProtocol$.MODULE$.accumValueFromJson(this.name$2, v);
         }

         public {
            this.name$2 = name$2;
         }
      });
      Option value = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Value")).map(new Serializable(name) {
         public static final long serialVersionUID = 0L;
         private final Option name$2;

         public final Object apply(JsonAST.JValue v) {
            return JsonProtocol$.MODULE$.accumValueFromJson(this.name$2, v);
         }

         public {
            this.name$2 = name$2;
         }
      });
      boolean internal = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Internal")).exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JsonAST.JValue x$36) {
            return BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(x$36).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
         }
      });
      boolean countFailedValues = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Count Failed Values")).exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JsonAST.JValue x$37) {
            return BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(x$37).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
         }
      });
      Option metadata = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Metadata")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$38) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$38).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      });
      return new AccumulableInfo(id, name, update, value, internal, countFailedValues, metadata);
   }

   public Object accumValueFromJson(Option<String> name, JsonAST.JValue value) {
      Object var10000;
      if (name.exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$39) {
            return x$39.startsWith(InternalAccumulator$.MODULE$.METRICS_PREFIX());
         }
      })) {
         Object var6;
         if (value instanceof JsonAST.JInt) {
            JsonAST.JInt var4 = (JsonAST.JInt)value;
            BigInt v = var4.num();
            var6 = BoxesRunTime.boxToLong(v.toLong());
         } else {
            if (!(value instanceof JsonAST.JArray)) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unexpected json value ", " for "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{value}))).append((Object)"accumulator ").append(name.get()).toString());
            }

            JsonAST.JArray var7 = (JsonAST.JArray)value;
            List v = var7.arr();
            var6 = JavaConverters$.MODULE$.seqAsJavaListConverter((Seq)v.map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final Tuple2<BlockId, BlockStatus> apply(JsonAST.JValue blockJson) {
                  BlockId id = BlockId$.MODULE$.apply((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(blockJson).$bslash("Block ID")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class)));
                  BlockStatus status = JsonProtocol$.MODULE$.blockStatusFromJson(org.json4s.package..MODULE$.jvalue2monadic(blockJson).$bslash("Status"));
                  return new Tuple2<BlockId, BlockStatus>(id, status);
               }
            }, List$.MODULE$.canBuildFrom())).asJava();
         }

         var10000 = var6;
      } else {
         var10000 = org.json4s.package..MODULE$.jvalue2extractable(value).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      }

      return var10000;
   }

   public TaskMetrics taskMetricsFromJson(JsonAST.JValue json) {
      TaskMetrics metrics = TaskMetrics$.MODULE$.empty();
      JsonAST.JNothing var3 = org.json4s.JsonAST.JNothing..MODULE$;
      if (json == null) {
         if (var3 == null) {
            return metrics;
         }
      } else if (json.equals(var3)) {
         return metrics;
      }

      metrics.setExecutorDeserializeTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor Deserialize Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      JsonAST.JValue var4 = org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor Deserialize CPU Time");
      long var5;
      if (org.json4s.JsonAST.JNothing..MODULE$.equals(var4)) {
         var5 = 0L;
      } else {
         var5 = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(var4).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      }

      metrics.setExecutorDeserializeCpuTime(var5);
      metrics.setExecutorRunTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor Run Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      JsonAST.JValue var7 = org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor CPU Time");
      long var8;
      if (org.json4s.JsonAST.JNothing..MODULE$.equals(var7)) {
         var8 = 0L;
      } else {
         var8 = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(var7).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      }

      metrics.setExecutorCpuTime(var8);
      metrics.setResultSize(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Result Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      metrics.setJvmGCTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("JVM GC Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      metrics.setResultSerializationTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Result Serialization Time")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      metrics.incMemoryBytesSpilled(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Memory Bytes Spilled")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      metrics.incDiskBytesSpilled(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Disk Bytes Spilled")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
      Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Shuffle Read Metrics")).foreach(new Serializable(metrics) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics metrics$1;

         public final void apply(JsonAST.JValue readJson) {
            TempShuffleReadMetrics readMetrics = this.metrics$1.createTempShuffleReadMetrics();
            readMetrics.incRemoteBlocksFetched((long)BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Remote Blocks Fetched")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int())));
            readMetrics.incLocalBlocksFetched((long)BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Local Blocks Fetched")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int())));
            readMetrics.incRemoteBytesRead(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Remote Bytes Read")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
            Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Remote Bytes Read To Disk")).foreach(new Serializable(this, readMetrics) {
               public static final long serialVersionUID = 0L;
               private final TempShuffleReadMetrics readMetrics$1;

               public final void apply(JsonAST.JValue v) {
                  this.readMetrics$1.incRemoteBytesReadToDisk(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(v).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
               }

               public {
                  this.readMetrics$1 = readMetrics$1;
               }
            });
            readMetrics.incLocalBytesRead(BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Local Bytes Read")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(JsonAST.JValue x$40) {
                  return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$40).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })));
            readMetrics.incFetchWaitTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Fetch Wait Time")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
            readMetrics.incRecordsRead(BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(readJson).$bslash("Total Records Read")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(JsonAST.JValue x$41) {
                  return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$41).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })));
            this.metrics$1.mergeShuffleReadMetrics();
         }

         public {
            this.metrics$1 = metrics$1;
         }
      });
      Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Shuffle Write Metrics")).foreach(new Serializable(metrics) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics metrics$1;

         public final void apply(JsonAST.JValue writeJson) {
            ShuffleWriteMetrics writeMetrics = this.metrics$1.shuffleWriteMetrics();
            writeMetrics.incBytesWritten(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(writeJson).$bslash("Shuffle Bytes Written")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
            writeMetrics.incRecordsWritten(BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(writeJson).$bslash("Shuffle Records Written")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(JsonAST.JValue x$42) {
                  return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$42).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })));
            writeMetrics.incWriteTime(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(writeJson).$bslash("Shuffle Write Time")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
         }

         public {
            this.metrics$1 = metrics$1;
         }
      });
      Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Output Metrics")).foreach(new Serializable(metrics) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics metrics$1;

         public final void apply(JsonAST.JValue outJson) {
            OutputMetrics outputMetrics = this.metrics$1.outputMetrics();
            outputMetrics.setBytesWritten(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(outJson).$bslash("Bytes Written")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
            outputMetrics.setRecordsWritten(BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(outJson).$bslash("Records Written")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(JsonAST.JValue x$43) {
                  return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$43).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })));
         }

         public {
            this.metrics$1 = metrics$1;
         }
      });
      Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Input Metrics")).foreach(new Serializable(metrics) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics metrics$1;

         public final void apply(JsonAST.JValue inJson) {
            InputMetrics inputMetrics = this.metrics$1.inputMetrics();
            inputMetrics.incBytesRead(BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(inJson).$bslash("Bytes Read")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long())));
            inputMetrics.incRecordsRead(BoxesRunTime.unboxToLong(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(inJson).$bslash("Records Read")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(JsonAST.JValue x$44) {
                  return BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(x$44).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })));
         }

         public {
            this.metrics$1 = metrics$1;
         }
      });
      Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Updated Blocks")).foreach(new Serializable(metrics) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics metrics$1;

         public final void apply(JsonAST.JValue blocksJson) {
            this.metrics$1.setUpdatedBlockStatuses((Seq)((List)org.json4s.package..MODULE$.jvalue2extractable(blocksJson).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<BlockId, BlockStatus> apply(JsonAST.JValue blockJson) {
                  BlockId id = BlockId$.MODULE$.apply((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(blockJson).$bslash("Block ID")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class)));
                  BlockStatus status = JsonProtocol$.MODULE$.blockStatusFromJson(org.json4s.package..MODULE$.jvalue2monadic(blockJson).$bslash("Status"));
                  return new Tuple2<BlockId, BlockStatus>(id, status);
               }
            }, List$.MODULE$.canBuildFrom()));
         }

         public {
            this.metrics$1 = metrics$1;
         }
      });
      return metrics;
   }

   public TaskEndReason taskEndReasonFromJson(JsonAST.JValue json) {
      Object var4;
      label148: {
         String var2 = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Reason")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         String var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.success();
         if (var10000 == null) {
            if (var2 == null) {
               break label148;
            }
         } else if (var10000.equals(var2)) {
            break label148;
         }

         label149: {
            var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.resubmitted();
            if (var10000 == null) {
               if (var2 == null) {
                  break label149;
               }
            } else if (var10000.equals(var2)) {
               break label149;
            }

            label150: {
               var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.fetchFailed();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label150;
                  }
               } else if (var10000.equals(var2)) {
                  break label150;
               }

               label151: {
                  var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.exceptionFailure();
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label151;
                     }
                  } else if (var10000.equals(var2)) {
                     break label151;
                  }

                  label152: {
                     var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.taskResultLost();
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label152;
                        }
                     } else if (var10000.equals(var2)) {
                        break label152;
                     }

                     label153: {
                        var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.taskKilled();
                        if (var10000 == null) {
                           if (var2 == null) {
                              break label153;
                           }
                        } else if (var10000.equals(var2)) {
                           break label153;
                        }

                        label154: {
                           var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.taskCommitDenied();
                           if (var10000 == null) {
                              if (var2 == null) {
                                 break label154;
                              }
                           } else if (var10000.equals(var2)) {
                              break label154;
                           }

                           label155: {
                              var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.executorLostFailure();
                              if (var10000 == null) {
                                 if (var2 == null) {
                                    break label155;
                                 }
                              } else if (var10000.equals(var2)) {
                                 break label155;
                              }

                              label57: {
                                 var10000 = JsonProtocol.TASK_END_REASON_FORMATTED_CLASS_NAMES$.MODULE$.unknownReason();
                                 if (var10000 == null) {
                                    if (var2 == null) {
                                       break label57;
                                    }
                                 } else if (var10000.equals(var2)) {
                                    break label57;
                                 }

                                 throw new MatchError(var2);
                              }

                              var4 = UnknownReason$.MODULE$;
                              return (TaskEndReason)var4;
                           }

                           Option exitCausedByApp = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Exit Caused By App")).map(new Serializable() {
                              public static final long serialVersionUID = 0L;

                              public final boolean apply(JsonAST.JValue x$52) {
                                 return BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(x$52).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
                              }
                           });
                           Option executorId = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).map(new Serializable() {
                              public static final long serialVersionUID = 0L;

                              public final String apply(JsonAST.JValue x$53) {
                                 return (String)org.json4s.package..MODULE$.jvalue2extractable(x$53).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
                              }
                           });
                           Option reason = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Loss Reason")).map(new Serializable() {
                              public static final long serialVersionUID = 0L;

                              public final String apply(JsonAST.JValue x$54) {
                                 return (String)org.json4s.package..MODULE$.jvalue2extractable(x$54).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
                              }
                           });
                           var4 = new ExecutorLostFailure((String)executorId.getOrElse(new Serializable() {
                              public static final long serialVersionUID = 0L;

                              public final String apply() {
                                 return "Unknown";
                              }
                           }), BoxesRunTime.unboxToBoolean(exitCausedByApp.getOrElse(new Serializable() {
                              public static final long serialVersionUID = 0L;

                              public final boolean apply() {
                                 return this.apply$mcZ$sp();
                              }

                              public boolean apply$mcZ$sp() {
                                 return true;
                              }
                           })), reason);
                           return (TaskEndReason)var4;
                        }

                        int jobId = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Job ID")).map(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply(JsonAST.JValue x$49) {
                              return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$49).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
                           }
                        }).getOrElse(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply() {
                              return this.apply$mcI$sp();
                           }

                           public int apply$mcI$sp() {
                              return -1;
                           }
                        }));
                        int partitionId = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Partition ID")).map(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply(JsonAST.JValue x$50) {
                              return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$50).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
                           }
                        }).getOrElse(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply() {
                              return this.apply$mcI$sp();
                           }

                           public int apply$mcI$sp() {
                              return -1;
                           }
                        }));
                        int attemptNo = BoxesRunTime.unboxToInt(Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Attempt Number")).map(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply(JsonAST.JValue x$51) {
                              return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$51).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
                           }
                        }).getOrElse(new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply() {
                              return this.apply$mcI$sp();
                           }

                           public int apply$mcI$sp() {
                              return -1;
                           }
                        }));
                        var4 = new TaskCommitDenied(jobId, partitionId, attemptNo);
                        return (TaskEndReason)var4;
                     }

                     String killReason = (String)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Kill Reason")).map(new Serializable() {
                        public static final long serialVersionUID = 0L;

                        public final String apply(JsonAST.JValue x$48) {
                           return (String)org.json4s.package..MODULE$.jvalue2extractable(x$48).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
                        }
                     }).getOrElse(new Serializable() {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "unknown reason";
                        }
                     });
                     var4 = new TaskKilled(killReason);
                     return (TaskEndReason)var4;
                  }

                  var4 = TaskResultLost$.MODULE$;
                  return (TaskEndReason)var4;
               }

               String className = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Class Name")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
               String description = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Description")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
               StackTraceElement[] stackTrace = this.stackTraceFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stack Trace"));
               String fullStackTrace = (String)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Full Stack Trace")).map(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply(JsonAST.JValue x$46) {
                     return (String)org.json4s.package..MODULE$.jvalue2extractable(x$46).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
                  }
               }).orNull(Predef$.MODULE$.$conforms());
               Seq accumUpdates = (Seq)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Accumulator Updates")).map(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final List<AccumulableInfo> apply(JsonAST.JValue x$47) {
                     return (List)((List)org.json4s.package..MODULE$.jvalue2extractable(x$47).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final AccumulableInfo apply(JsonAST.JValue json) {
                           return JsonProtocol$.MODULE$.accumulableInfoFromJson(json);
                        }
                     }, List$.MODULE$.canBuildFrom());
                  }
               }).getOrElse(new Serializable(json) {
                  public static final long serialVersionUID = 0L;
                  private final JsonAST.JValue json$2;

                  public final Seq<AccumulableInfo> apply() {
                     return (Seq)JsonProtocol$.MODULE$.taskMetricsFromJson(org.json4s.package..MODULE$.jvalue2monadic(this.json$2).$bslash("Metrics")).accumulators().map(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final AccumulableInfo apply(AccumulatorV2<?, ?> acc) {
                           return acc.toInfo(new Some(acc.value()), None$.MODULE$);
                        }
                     }, Seq$.MODULE$.canBuildFrom());
                  }

                  public {
                     this.json$2 = json$2;
                  }
               });
               var4 = new ExceptionFailure(className, description, stackTrace, fullStackTrace, None$.MODULE$, accumUpdates, ExceptionFailure$.MODULE$.apply$default$7());
               return (TaskEndReason)var4;
            }

            BlockManagerId blockManagerAddress = this.blockManagerIdFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block Manager Address"));
            int shuffleId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Shuffle ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            int mapId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Map ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            int reduceId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Reduce ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            Option message = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Message")).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply(JsonAST.JValue x$45) {
                  return (String)org.json4s.package..MODULE$.jvalue2extractable(x$45).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
               }
            });
            var4 = new FetchFailed(blockManagerAddress, shuffleId, mapId, reduceId, (String)message.getOrElse(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Unknown reason";
               }
            }));
            return (TaskEndReason)var4;
         }

         var4 = Resubmitted$.MODULE$;
         return (TaskEndReason)var4;
      }

      var4 = Success$.MODULE$;
      return (TaskEndReason)var4;
   }

   public BlockManagerId blockManagerIdFromJson(JsonAST.JValue json) {
      label14: {
         JsonAST.JNothing var2 = org.json4s.JsonAST.JNothing..MODULE$;
         if (json == null) {
            if (var2 == null) {
               break label14;
            }
         } else if (json.equals(var2)) {
            break label14;
         }

         String executorId = ((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Executor ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class))).intern();
         String host = ((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Host")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class))).intern();
         int port = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Port")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
         return BlockManagerId$.MODULE$.apply(executorId, host, port, BlockManagerId$.MODULE$.apply$default$4());
      }

      Object var10000 = null;
      return null;
   }

   public JobResult jobResultFromJson(JsonAST.JValue json) {
      Object var4;
      label36: {
         String var2 = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Result")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         String var10000 = JsonProtocol.JOB_RESULT_FORMATTED_CLASS_NAMES$.MODULE$.jobSucceeded();
         if (var10000 == null) {
            if (var2 == null) {
               break label36;
            }
         } else if (var10000.equals(var2)) {
            break label36;
         }

         label22: {
            var10000 = JsonProtocol.JOB_RESULT_FORMATTED_CLASS_NAMES$.MODULE$.jobFailed();
            if (var10000 == null) {
               if (var2 == null) {
                  break label22;
               }
            } else if (var10000.equals(var2)) {
               break label22;
            }

            throw new MatchError(var2);
         }

         Exception exception = this.exceptionFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Exception"));
         var4 = new JobFailed(exception);
         return (JobResult)var4;
      }

      var4 = JobSucceeded$.MODULE$;
      return (JobResult)var4;
   }

   public RDDInfo rddInfoFromJson(JsonAST.JValue json) {
      int rddId = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("RDD ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      String name = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Name")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      Option scope = Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Scope")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$55) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$55).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final RDDOperationScope apply(String s) {
            return RDDOperationScope$.MODULE$.fromJson(s);
         }
      });
      String callsite = (String)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Callsite")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue x$56) {
            return (String)org.json4s.package..MODULE$.jvalue2extractable(x$56).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      Seq parentIds = (Seq)Utils$.MODULE$.jsonOption(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Parent IDs")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final List<Object> apply(JsonAST.JValue l) {
            return (List)((List)org.json4s.package..MODULE$.jvalue2extractable(l).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(JsonAST.JValue x$57) {
                  return BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(x$57).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
               }
            }, List$.MODULE$.canBuildFrom());
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      StorageLevel storageLevel = this.storageLevelFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Storage Level"));
      int numPartitions = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Number of Partitions")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      int numCachedPartitions = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Number of Cached Partitions")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      long memSize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Memory Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long diskSize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Disk Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      RDDInfo rddInfo = new RDDInfo(rddId, name, numPartitions, storageLevel, parentIds, callsite, scope);
      rddInfo.numCachedPartitions_$eq(numCachedPartitions);
      rddInfo.memSize_$eq(memSize);
      rddInfo.diskSize_$eq(diskSize);
      return rddInfo;
   }

   public StorageLevel storageLevelFromJson(JsonAST.JValue json) {
      boolean useDisk = BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Use Disk")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
      boolean useMemory = BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Use Memory")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
      boolean deserialized = BoxesRunTime.unboxToBoolean(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Deserialized")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Boolean()));
      int replication = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Replication")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      return StorageLevel$.MODULE$.apply(useDisk, useMemory, deserialized, replication);
   }

   public BlockStatus blockStatusFromJson(JsonAST.JValue json) {
      StorageLevel storageLevel = this.storageLevelFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Storage Level"));
      long memorySize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Memory Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long diskSize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Disk Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      return new BlockStatus(storageLevel, memorySize, diskSize);
   }

   public ExecutorInfo executorInfoFromJson(JsonAST.JValue json) {
      String executorHost = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Host")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
      int totalCores = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Total Cores")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
      scala.collection.immutable.Map logUrls = this.mapFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Log Urls")).toMap(Predef$.MODULE$.$conforms());
      return new ExecutorInfo(executorHost, totalCores, logUrls);
   }

   public BlockUpdatedInfo blockUpdatedInfoFromJson(JsonAST.JValue json) {
      BlockManagerId blockManagerId = this.blockManagerIdFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block Manager ID"));
      BlockId blockId = BlockId$.MODULE$.apply((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Block ID")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class)));
      StorageLevel storageLevel = this.storageLevelFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Storage Level"));
      long memorySize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Memory Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long diskSize = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Disk Size")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      return new BlockUpdatedInfo(blockManagerId, blockId, storageLevel, memorySize, diskSize);
   }

   public Map<String, String> mapFromJson(JsonAST.JValue json) {
      List jsonFields = ((JsonAST.JObject)json).obj();
      return ((TraversableOnce)jsonFields.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(Tuple2<String, JsonAST.JValue> x0$6) {
            Option var3 = org.json4s.JsonAST.JField..MODULE$.unapply(x0$6);
            if (!var3.isEmpty()) {
               String k = (String)((Tuple2)var3.get())._1();
               JsonAST.JValue var5 = (JsonAST.JValue)((Tuple2)var3.get())._2();
               if (var5 instanceof JsonAST.JString) {
                  JsonAST.JString var6 = (JsonAST.JString)var5;
                  String v = var6.s();
                  Tuple2 var8 = new Tuple2(k, v);
                  return var8;
               }
            }

            throw new MatchError(x0$6);
         }
      }, List$.MODULE$.canBuildFrom())).<String, String>toMap(Predef$.MODULE$.$conforms());
   }

   public Properties propertiesFromJson(JsonAST.JValue json) {
      return (Properties)Utils$.MODULE$.jsonOption(json).map(new Serializable(json) {
         public static final long serialVersionUID = 0L;
         private final JsonAST.JValue json$1;

         public final Properties apply(JsonAST.JValue value) {
            Properties properties = new Properties();
            JsonProtocol$.MODULE$.mapFromJson(this.json$1).foreach(new Serializable(this, properties) {
               public static final long serialVersionUID = 0L;
               private final Properties properties$1;

               public final Object apply(Tuple2<String, String> x0$7) {
                  if (x0$7 != null) {
                     String k = (String)x0$7._1();
                     String v = (String)x0$7._2();
                     Object var5 = this.properties$1.setProperty(k, v);
                     return var5;
                  } else {
                     throw new MatchError(x0$7);
                  }
               }

               public {
                  this.properties$1 = properties$1;
               }
            });
            return properties;
         }

         public {
            this.json$1 = json$1;
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Null$ apply() {
            return null;
         }
      });
   }

   public UUID UUIDFromJson(JsonAST.JValue json) {
      long leastSignificantBits = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Least Significant Bits")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      long mostSignificantBits = BoxesRunTime.unboxToLong(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Most Significant Bits")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Long()));
      return new UUID(leastSignificantBits, mostSignificantBits);
   }

   public StackTraceElement[] stackTraceFromJson(JsonAST.JValue json) {
      return (StackTraceElement[])((TraversableOnce)((List)org.json4s.package..MODULE$.jvalue2extractable(json).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(List.class, ManifestFactory$.MODULE$.classType(JsonAST.JValue.class), Predef$.MODULE$.wrapRefArray(new Manifest[0])))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final StackTraceElement apply(JsonAST.JValue line) {
            String declaringClass = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(line).$bslash("Declaring Class")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
            String methodName = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(line).$bslash("Method Name")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
            String fileName = (String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(line).$bslash("File Name")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class));
            int lineNumber = BoxesRunTime.unboxToInt(org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(line).$bslash("Line Number")).extract(JsonProtocol$.MODULE$.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.Int()));
            return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
         }
      }, List$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(StackTraceElement.class));
   }

   public Exception exceptionFromJson(JsonAST.JValue json) {
      Exception e = new Exception((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Message")).extract(this.org$apache$spark$util$JsonProtocol$$format(), ManifestFactory$.MODULE$.classType(String.class)));
      e.setStackTrace(this.stackTraceFromJson(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("Stack Trace")));
      return e;
   }

   private JsonProtocol$() {
      MODULE$ = this;
      this.org$apache$spark$util$JsonProtocol$$format = org.json4s.DefaultFormats..MODULE$;
      this.mapper = (new ObjectMapper()).registerModule(com.fasterxml.jackson.module.scala.DefaultScalaModule..MODULE$);
   }
}
