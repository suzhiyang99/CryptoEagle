package org.apache.spark.internal.io;

import java.util.Date;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.OutputMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.LongRef;
import scala.runtime.TraitSetter;

public final class SparkHadoopWriter$ implements Logging {
   public static final SparkHadoopWriter$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkHadoopWriter$();
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

   public <K, V> void write(RDD<Tuple2<K, V>> rdd, HadoopWriteConfigUtil<K, V> config, ClassTag<V> evidence$1) {
      SparkContext sparkContext = rdd.context();
      int commitJobId = rdd.id();
      String jobTrackerId = SparkHadoopWriterUtils$.MODULE$.createJobTrackerID(new Date());
      JobContext jobContext = config.createJobContext(jobTrackerId, commitJobId);
      config.initOutputFormat(jobContext);
      config.assertConf(jobContext, rdd.conf());
      HadoopMapReduceCommitProtocol committer = config.createCommitter(commitJobId);
      committer.setupJob(jobContext);

      try {
         FileCommitProtocol.TaskCommitMessage[] ret = (FileCommitProtocol.TaskCommitMessage[])sparkContext.runJob(rdd, new Serializable(config, evidence$1, commitJobId, jobTrackerId, committer) {
            public static final long serialVersionUID = 0L;
            private final HadoopWriteConfigUtil config$1;
            private final ClassTag evidence$1$1;
            private final int commitJobId$1;
            private final String jobTrackerId$1;
            private final HadoopMapReduceCommitProtocol committer$1;

            public final FileCommitProtocol.TaskCommitMessage apply(TaskContext context, Iterator<Tuple2<K, V>> iter) {
               return SparkHadoopWriter$.MODULE$.org$apache$spark$internal$io$SparkHadoopWriter$$executeTask(context, this.config$1, this.jobTrackerId$1, this.commitJobId$1, context.partitionId(), context.attemptNumber(), this.committer$1, iter, this.evidence$1$1);
            }

            public {
               this.config$1 = config$1;
               this.evidence$1$1 = evidence$1$1;
               this.commitJobId$1 = commitJobId$1;
               this.jobTrackerId$1 = jobTrackerId$1;
               this.committer$1 = committer$1;
            }
         }, ClassTag$.MODULE$.apply(FileCommitProtocol.TaskCommitMessage.class));
         committer.commitJob(jobContext, Predef$.MODULE$.wrapRefArray(ret));
         this.logInfo(new Serializable(jobContext) {
            public static final long serialVersionUID = 0L;
            private final JobContext jobContext$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Job ", " committed."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.jobContext$1.getJobID()}));
            }

            public {
               this.jobContext$1 = jobContext$1;
            }
         });
      } catch (Throwable var12) {
         this.logError(new Serializable(jobContext) {
            public static final long serialVersionUID = 0L;
            private final JobContext jobContext$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Aborting job ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.jobContext$1.getJobID()}));
            }

            public {
               this.jobContext$1 = jobContext$1;
            }
         }, var12);
         committer.abortJob(jobContext);
         throw new SparkException("Job aborted.", var12);
      }
   }

   public <K, V> FileCommitProtocol.TaskCommitMessage org$apache$spark$internal$io$SparkHadoopWriter$$executeTask(TaskContext context, HadoopWriteConfigUtil<K, V> config, String jobTrackerId, int commitJobId, int sparkPartitionId, int sparkAttemptNumber, FileCommitProtocol committer, Iterator<Tuple2<K, V>> iterator, ClassTag<V> evidence$2) {
      TaskAttemptContext taskContext = config.createTaskAttemptContext(jobTrackerId, commitJobId, sparkPartitionId, sparkAttemptNumber);
      committer.setupTask(taskContext);
      Tuple2 var12 = SparkHadoopWriterUtils$.MODULE$.initHadoopOutputMetrics(context);
      if (var12 != null) {
         OutputMetrics outputMetrics = (OutputMetrics)var12._1();
         Function0 callback = (Function0)var12._2();
         Tuple2 var15 = new Tuple2(outputMetrics, callback);
         OutputMetrics outputMetrics = (OutputMetrics)var15._1();
         Function0 callback = (Function0)var15._2();
         config.initWriter(taskContext, sparkPartitionId);
         LongRef recordsWritten = LongRef.create(0L);

         try {
            Function0 x$2 = new Serializable(config, committer, iterator, taskContext, outputMetrics, callback, recordsWritten) {
               public static final long serialVersionUID = 0L;
               private final HadoopWriteConfigUtil config$2;
               private final FileCommitProtocol committer$2;
               private final Iterator iterator$1;
               private final TaskAttemptContext taskContext$1;
               private final OutputMetrics outputMetrics$1;
               private final Function0 callback$1;
               private final LongRef recordsWritten$1;

               public final FileCommitProtocol.TaskCommitMessage apply() {
                  while(this.iterator$1.hasNext()) {
                     Tuple2 pair = (Tuple2)this.iterator$1.next();
                     this.config$2.write(pair);
                     SparkHadoopWriterUtils$.MODULE$.maybeUpdateOutputMetrics(this.outputMetrics$1, this.callback$1, this.recordsWritten$1.elem);
                     ++this.recordsWritten$1.elem;
                  }

                  this.config$2.closeWriter(this.taskContext$1);
                  return this.committer$2.commitTask(this.taskContext$1);
               }

               public {
                  this.config$2 = config$2;
                  this.committer$2 = committer$2;
                  this.iterator$1 = iterator$1;
                  this.taskContext$1 = taskContext$1;
                  this.outputMetrics$1 = outputMetrics$1;
                  this.callback$1 = callback$1;
                  this.recordsWritten$1 = recordsWritten$1;
               }
            };
            Function0 x$3 = new Serializable(config, committer, taskContext) {
               public static final long serialVersionUID = 0L;
               private final HadoopWriteConfigUtil config$2;
               private final FileCommitProtocol committer$2;
               public final TaskAttemptContext taskContext$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  try {
                     this.config$2.closeWriter(this.taskContext$1);
                  } finally {
                     this.committer$2.abortTask(this.taskContext$1);
                     SparkHadoopWriter$.MODULE$.logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " aborted."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.taskContext$1.getTaskAttemptID()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                  }

               }

               public {
                  this.config$2 = config$2;
                  this.committer$2 = committer$2;
                  this.taskContext$1 = taskContext$1;
               }
            };
            Function0 x$4 = new Serializable(x$2) {
               public static final long serialVersionUID = 0L;
               private final Function0 x$2$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  Utils$.MODULE$.tryWithSafeFinallyAndFailureCallbacks$default$3(this.x$2$1);
               }

               public {
                  this.x$2$1 = x$2$1;
               }
            };
            FileCommitProtocol.TaskCommitMessage ret = (FileCommitProtocol.TaskCommitMessage)Utils$.MODULE$.tryWithSafeFinallyAndFailureCallbacks(x$2, x$3, x$4);
            outputMetrics.setBytesWritten(callback.apply$mcJ$sp());
            outputMetrics.setRecordsWritten(recordsWritten.elem);
            return ret;
         } catch (Throwable var25) {
            throw new SparkException("Task failed while writing rows", var25);
         }
      } else {
         throw new MatchError(var12);
      }
   }

   private SparkHadoopWriter$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
