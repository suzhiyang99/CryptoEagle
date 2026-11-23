package org.apache.spark.status;

import org.apache.spark.status.api.v1.AccumulableInfo;
import org.apache.spark.status.api.v1.InputMetrics;
import org.apache.spark.status.api.v1.OutputMetrics;
import org.apache.spark.status.api.v1.ShuffleReadMetrics;
import org.apache.spark.status.api.v1.ShuffleWriteMetrics;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.util.AccumulatorContext$;
import org.spark_project.guava.collect.Interner;
import org.spark_project.guava.collect.Interners;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;

public final class LiveEntityHelpers$ {
   public static final LiveEntityHelpers$ MODULE$;
   private final Interner<String> stringInterner;

   static {
      new LiveEntityHelpers$();
   }

   private Interner<String> stringInterner() {
      return this.stringInterner;
   }

   public Seq<AccumulableInfo> newAccumulatorInfos(Iterable<org.apache.spark.scheduler.AccumulableInfo> accums) {
      return ((TraversableOnce)((TraversableLike)accums.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(org.apache.spark.scheduler.AccumulableInfo acc) {
            boolean var3;
            if (!acc.internal()) {
               label31: {
                  if (acc.metadata().isDefined()) {
                     Object var10000 = acc.metadata().get();
                     Some var2 = new Some(AccumulatorContext$.MODULE$.SQL_ACCUM_IDENTIFIER());
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label31;
                        }
                     } else if (var10000.equals(var2)) {
                        break label31;
                     }
                  }

                  var3 = true;
                  return var3;
               }
            }

            var3 = false;
            return var3;
         }
      })).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final AccumulableInfo apply(org.apache.spark.scheduler.AccumulableInfo acc) {
            return new AccumulableInfo(acc.id(), (String)acc.name().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String s) {
                  return LiveEntityHelpers$.MODULE$.weakIntern(s);
               }
            }).orNull(Predef$.MODULE$.$conforms()), acc.update().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Object x$5) {
                  return x$5.toString();
               }
            }), (String)acc.value().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Object x$6) {
                  return x$6.toString();
               }
            }).orNull(Predef$.MODULE$.$conforms()));
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
   }

   public String weakIntern(String s) {
      return (String)this.stringInterner().intern(s);
   }

   public TaskMetrics createMetrics(long executorDeserializeTime, long executorDeserializeCpuTime, long executorRunTime, long executorCpuTime, long resultSize, long jvmGcTime, long resultSerializationTime, long memoryBytesSpilled, long diskBytesSpilled, long peakExecutionMemory, long inputBytesRead, long inputRecordsRead, long outputBytesWritten, long outputRecordsWritten, long shuffleRemoteBlocksFetched, long shuffleLocalBlocksFetched, long shuffleFetchWaitTime, long shuffleRemoteBytesRead, long shuffleRemoteBytesReadToDisk, long shuffleLocalBytesRead, long shuffleRecordsRead, long shuffleBytesWritten, long shuffleWriteTime, long shuffleRecordsWritten) {
      return new TaskMetrics(executorDeserializeTime, executorDeserializeCpuTime, executorRunTime, executorCpuTime, resultSize, jvmGcTime, resultSerializationTime, memoryBytesSpilled, diskBytesSpilled, peakExecutionMemory, new InputMetrics(inputBytesRead, inputRecordsRead), new OutputMetrics(outputBytesWritten, outputRecordsWritten), new ShuffleReadMetrics(shuffleRemoteBlocksFetched, shuffleLocalBlocksFetched, shuffleFetchWaitTime, shuffleRemoteBytesRead, shuffleRemoteBytesReadToDisk, shuffleLocalBytesRead, shuffleRecordsRead), new ShuffleWriteMetrics(shuffleBytesWritten, shuffleWriteTime, shuffleRecordsWritten));
   }

   public TaskMetrics createMetrics(long var1) {
      return this.createMetrics(var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1, var1);
   }

   public TaskMetrics addMetrics(TaskMetrics m1, TaskMetrics m2) {
      return this.addMetrics(m1, m2, 1);
   }

   public TaskMetrics subtractMetrics(TaskMetrics m1, TaskMetrics m2) {
      return this.addMetrics(m1, m2, -1);
   }

   private TaskMetrics addMetrics(TaskMetrics m1, TaskMetrics m2, int mult) {
      return this.createMetrics(m1.executorDeserializeTime() + m2.executorDeserializeTime() * (long)mult, m1.executorDeserializeCpuTime() + m2.executorDeserializeCpuTime() * (long)mult, m1.executorRunTime() + m2.executorRunTime() * (long)mult, m1.executorCpuTime() + m2.executorCpuTime() * (long)mult, m1.resultSize() + m2.resultSize() * (long)mult, m1.jvmGcTime() + m2.jvmGcTime() * (long)mult, m1.resultSerializationTime() + m2.resultSerializationTime() * (long)mult, m1.memoryBytesSpilled() + m2.memoryBytesSpilled() * (long)mult, m1.diskBytesSpilled() + m2.diskBytesSpilled() * (long)mult, m1.peakExecutionMemory() + m2.peakExecutionMemory() * (long)mult, m1.inputMetrics().bytesRead() + m2.inputMetrics().bytesRead() * (long)mult, m1.inputMetrics().recordsRead() + m2.inputMetrics().recordsRead() * (long)mult, m1.outputMetrics().bytesWritten() + m2.outputMetrics().bytesWritten() * (long)mult, m1.outputMetrics().recordsWritten() + m2.outputMetrics().recordsWritten() * (long)mult, m1.shuffleReadMetrics().remoteBlocksFetched() + m2.shuffleReadMetrics().remoteBlocksFetched() * (long)mult, m1.shuffleReadMetrics().localBlocksFetched() + m2.shuffleReadMetrics().localBlocksFetched() * (long)mult, m1.shuffleReadMetrics().fetchWaitTime() + m2.shuffleReadMetrics().fetchWaitTime() * (long)mult, m1.shuffleReadMetrics().remoteBytesRead() + m2.shuffleReadMetrics().remoteBytesRead() * (long)mult, m1.shuffleReadMetrics().remoteBytesReadToDisk() + m2.shuffleReadMetrics().remoteBytesReadToDisk() * (long)mult, m1.shuffleReadMetrics().localBytesRead() + m2.shuffleReadMetrics().localBytesRead() * (long)mult, m1.shuffleReadMetrics().recordsRead() + m2.shuffleReadMetrics().recordsRead() * (long)mult, m1.shuffleWriteMetrics().bytesWritten() + m2.shuffleWriteMetrics().bytesWritten() * (long)mult, m1.shuffleWriteMetrics().writeTime() + m2.shuffleWriteMetrics().writeTime() * (long)mult, m1.shuffleWriteMetrics().recordsWritten() + m2.shuffleWriteMetrics().recordsWritten() * (long)mult);
   }

   private LiveEntityHelpers$() {
      MODULE$ = this;
      this.stringInterner = Interners.newWeakInterner();
   }
}
