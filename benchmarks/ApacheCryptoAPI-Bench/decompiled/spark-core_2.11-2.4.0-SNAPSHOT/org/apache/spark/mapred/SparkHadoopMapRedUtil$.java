package org.apache.spark.mapred;

import java.io.IOException;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext$;
import org.apache.spark.executor.CommitDeniedException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.OutputCommitCoordinator;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.runtime.TraitSetter;

public final class SparkHadoopMapRedUtil$ implements Logging {
   public static final SparkHadoopMapRedUtil$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkHadoopMapRedUtil$();
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

   public void commitTask(OutputCommitter committer, TaskAttemptContext mrTaskContext, int jobId, int splitId) {
      TaskAttemptID mrTaskAttemptID = mrTaskContext.getTaskAttemptID();
      if (committer.needsTaskCommit(mrTaskContext)) {
         SparkConf sparkConf = SparkEnv$.MODULE$.get().conf();
         boolean shouldCoordinateWithDriver = sparkConf.getBoolean("spark.hadoop.outputCommitCoordination.enabled", true);
         if (shouldCoordinateWithDriver) {
            OutputCommitCoordinator outputCommitCoordinator = SparkEnv$.MODULE$.get().outputCommitCoordinator();
            int taskAttemptNumber = TaskContext$.MODULE$.get().attemptNumber();
            int stageId = TaskContext$.MODULE$.get().stageId();
            boolean canCommit = outputCommitCoordinator.canCommit(stageId, splitId, taskAttemptNumber);
            if (!canCommit) {
               String message = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": Not committed because the driver did not authorize commit"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{mrTaskAttemptID}));
               this.logInfo(new Serializable(message) {
                  public static final long serialVersionUID = 0L;
                  private final String message$1;

                  public final String apply() {
                     return this.message$1;
                  }

                  public {
                     this.message$1 = message$1;
                  }
               });
               committer.abortTask(mrTaskContext);
               throw new CommitDeniedException(message, stageId, splitId, taskAttemptNumber);
            }

            this.performCommit$1(committer, mrTaskContext, mrTaskAttemptID);
         } else {
            this.performCommit$1(committer, mrTaskContext, mrTaskAttemptID);
         }
      } else {
         this.logInfo(new Serializable(mrTaskAttemptID) {
            public static final long serialVersionUID = 0L;
            private final TaskAttemptID mrTaskAttemptID$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No need to commit output of task because needsTaskCommit=false: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.mrTaskAttemptID$1}));
            }

            public {
               this.mrTaskAttemptID$1 = mrTaskAttemptID$1;
            }
         });
      }

   }

   private final void performCommit$1(OutputCommitter committer$1, TaskAttemptContext mrTaskContext$1, TaskAttemptID mrTaskAttemptID$1) {
      try {
         committer$1.commitTask(mrTaskContext$1);
         this.logInfo(new Serializable(mrTaskAttemptID$1) {
            public static final long serialVersionUID = 0L;
            private final TaskAttemptID mrTaskAttemptID$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": Committed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.mrTaskAttemptID$1}));
            }

            public {
               this.mrTaskAttemptID$1 = mrTaskAttemptID$1;
            }
         });
      } catch (IOException var5) {
         this.logError(new Serializable(mrTaskAttemptID$1) {
            public static final long serialVersionUID = 0L;
            private final TaskAttemptID mrTaskAttemptID$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error committing the output of task: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.mrTaskAttemptID$1}));
            }

            public {
               this.mrTaskAttemptID$1 = mrTaskAttemptID$1;
            }
         }, var5);
         committer$1.abortTask(mrTaskContext$1);
         throw var5;
      }
   }

   private SparkHadoopMapRedUtil$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
