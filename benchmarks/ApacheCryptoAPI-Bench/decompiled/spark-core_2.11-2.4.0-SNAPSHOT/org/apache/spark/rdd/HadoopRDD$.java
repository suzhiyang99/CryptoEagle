package org.apache.spark.rdd;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.apache.hadoop.mapred.TaskAttemptID;
import org.apache.hadoop.mapred.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.HDFSCacheTaskLocation;
import org.apache.spark.scheduler.HostTaskLocation;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.runtime.TraitSetter;

public final class HadoopRDD$ implements Logging, Serializable {
   public static final HadoopRDD$ MODULE$;
   private final Object CONFIGURATION_INSTANTIATION_LOCK;
   private final int RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new HadoopRDD$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public Object CONFIGURATION_INSTANTIATION_LOCK() {
      return this.CONFIGURATION_INSTANTIATION_LOCK;
   }

   public int RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES() {
      return this.RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES;
   }

   public Object getCachedMetadata(String key) {
      return SparkEnv$.MODULE$.get().hadoopJobMetadata().get(key);
   }

   public void org$apache$spark$rdd$HadoopRDD$$putCachedMetadata(String key, Object value) {
      SparkEnv$.MODULE$.get().hadoopJobMetadata().put(key, value);
   }

   public void addLocalConfiguration(String jobTrackerId, int jobId, int splitId, int attemptId, JobConf conf) {
      JobID jobID = new JobID(jobTrackerId, jobId);
      TaskAttemptID taId = new TaskAttemptID(new TaskID(jobID, TaskType.MAP, splitId), attemptId);
      conf.set("mapreduce.task.id", taId.getTaskID().toString());
      conf.set("mapreduce.task.attempt.id", taId.toString());
      conf.setBoolean("mapreduce.task.ismap", true);
      conf.setInt("mapreduce.task.partition", splitId);
      conf.set("mapreduce.job.id", jobID.toString());
   }

   public Option<Seq<String>> convertSplitLocationInfo(SplitLocationInfo[] infos) {
      return Option$.MODULE$.<Seq<String>>apply(infos).<Seq<String>>map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<String> apply(SplitLocationInfo[] x$3) {
            return (Seq)Predef$.MODULE$.refArrayOps((Object[])x$3).flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterable<String> apply(SplitLocationInfo loc) {
                  Iterable var10000;
                  label26: {
                     String locationStr = loc.getLocation();
                     String var3 = "localhost";
                     if (locationStr == null) {
                        if (var3 == null) {
                           break label26;
                        }
                     } else if (locationStr.equals(var3)) {
                        break label26;
                     }

                     if (loc.isInMemory()) {
                        HadoopRDD$.MODULE$.logDebug(new Serializable(this, locationStr) {
                           public static final long serialVersionUID = 0L;
                           private final String locationStr$1;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Partition ", " is cached by Hadoop."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.locationStr$1}));
                           }

                           public {
                              this.locationStr$1 = locationStr$1;
                           }
                        });
                        var10000 = Option$.MODULE$.option2Iterable(new Some((new HDFSCacheTaskLocation(locationStr)).toString()));
                     } else {
                        var10000 = Option$.MODULE$.option2Iterable(new Some((new HostTaskLocation(locationStr)).toString()));
                     }

                     return var10000;
                  }

                  var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
                  return var10000;
               }
            }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
         }
      });
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HadoopRDD$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.CONFIGURATION_INSTANTIATION_LOCK = new Object();
      this.RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES = 256;
   }
}
