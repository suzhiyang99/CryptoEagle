package org.apache.spark.scheduler;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import org.apache.spark.InternalAccumulator$;
import org.apache.spark.SparkEnv;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.TaskResultLost$;
import org.apache.spark.TaskState$;
import org.apache.spark.UnknownReason$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.storage.BlockId;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.LongAccumulator;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcV$sp;
import scala.runtime.ObjectRef;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a!B\u0001\u0003\u0001\u0011Q!\u0001\u0005+bg.\u0014Vm];mi\u001e+G\u000f^3s\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0002\u0003\u0006IAG\u0001\tgB\f'o[#om\u000e\u0001\u0001CA\u000e\u001d\u001b\u0005!\u0011BA\u000f\u0005\u0005!\u0019\u0006/\u0019:l\u000b:4\b\u0002C\u0002\u0001\u0005\u0003\u0005\u000b\u0011B\u0010\u0011\u0005\u0001\nS\"\u0001\u0002\n\u0005\t\u0012!!\u0005+bg.\u001c6\r[3ek2,'/S7qY\")A\u0005\u0001C\u0001K\u00051A(\u001b8jiz\"2AJ\u0014)!\t\u0001\u0003\u0001C\u0003\u0019G\u0001\u0007!\u0004C\u0003\u0004G\u0001\u0007q\u0004C\u0004+\u0001\t\u0007I\u0011B\u0016\u0002\u000fQC%+R!E'V\tA\u0006\u0005\u0002\r[%\u0011a&\u0004\u0002\u0004\u0013:$\bB\u0002\u0019\u0001A\u0003%A&\u0001\u0005U\u0011J+\u0015\tR*!\u0011\u001d\u0011\u0004A1A\u0005\u0012M\nQcZ3u)\u0006\u001c8NU3tk2$X\t_3dkR|'/F\u00015!\t)D(D\u00017\u0015\t9\u0004(\u0001\u0006d_:\u001cWO\u001d:f]RT!!\u000f\u001e\u0002\tU$\u0018\u000e\u001c\u0006\u0002w\u0005!!.\u0019<b\u0013\tidGA\bFq\u0016\u001cW\u000f^8s'\u0016\u0014h/[2f\u0011\u0019y\u0004\u0001)A\u0005i\u00051r-\u001a;UCN\\'+Z:vYR,\u00050Z2vi>\u0014\b\u0005C\u0004B\u0001\t\u0007I\u0011\u0003\"\u0002\u0015M,'/[1mSj,'/F\u0001D!\r!u)S\u0007\u0002\u000b*\u0011aIO\u0001\u0005Y\u0006tw-\u0003\u0002I\u000b\nYA\u000b\u001b:fC\u0012dunY1m!\tQE*D\u0001L\u0015\t\tE!\u0003\u0002N\u0017\n\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011\u0019y\u0005\u0001)A\u0005\u0007\u0006Y1/\u001a:jC2L'0\u001a:!\u0011\u001d\t\u0006A1A\u0005\u0012\t\u000bA\u0003^1tWJ+7/\u001e7u'\u0016\u0014\u0018.\u00197ju\u0016\u0014\bBB*\u0001A\u0003%1)A\u000buCN\\'+Z:vYR\u001cVM]5bY&TXM\u001d\u0011\t\u000bU\u0003A\u0011\u0001,\u0002+\u0015t\u0017/^3vKN+8mY3tg\u001a,H\u000eV1tWR!qKW0e!\ta\u0001,\u0003\u0002Z\u001b\t!QK\\5u\u0011\u0015YF\u000b1\u0001]\u00039!\u0018m]6TKRl\u0015M\\1hKJ\u0004\"\u0001I/\n\u0005y\u0013!A\u0004+bg.\u001cV\r^'b]\u0006<WM\u001d\u0005\u0006AR\u0003\r!Y\u0001\u0004i&$\u0007C\u0001\u0007c\u0013\t\u0019WB\u0001\u0003M_:<\u0007\"B3U\u0001\u00041\u0017AD:fe&\fG.\u001b>fI\u0012\u000bG/\u0019\t\u0003O*l\u0011\u0001\u001b\u0006\u0003Sj\n1A\\5p\u0013\tY\u0007N\u0001\u0006CsR,')\u001e4gKJDQ!\u001c\u0001\u0005\u00029\f\u0011#\u001a8rk\u0016,XMR1jY\u0016$G+Y:l)\u00199v\u000e]9\u0002\b!)1\f\u001ca\u00019\")\u0001\r\u001ca\u0001C\")!\u000f\u001ca\u0001g\u0006IA/Y:l'R\fG/\u001a\t\u0004i\u0006\u0005aBA;\u007f\u001d\t1XP\u0004\u0002xy:\u0011\u0001p_\u0007\u0002s*\u0011!0G\u0001\u0007yI|w\u000e\u001e \n\u0003%I!a\u0002\u0005\n\u0005\u00151\u0011BA@\u0005\u0003%!\u0016m]6Ti\u0006$X-\u0003\u0003\u0002\u0004\u0005\u0015!!\u0003+bg.\u001cF/\u0019;f\u0015\tyH\u0001C\u0003fY\u0002\u0007a\rC\u0004\u0002\f\u0001!\t!!\u0004\u0002\tM$x\u000e\u001d\u000b\u0002/\u0002"
)
public class TaskResultGetter implements Logging {
   public final SparkEnv org$apache$spark$scheduler$TaskResultGetter$$sparkEnv;
   public final TaskSchedulerImpl org$apache$spark$scheduler$TaskResultGetter$$scheduler;
   private final int THREADS;
   private final ExecutorService getTaskResultExecutor;
   private final ThreadLocal<SerializerInstance> serializer;
   private final ThreadLocal<SerializerInstance> taskResultSerializer;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   private int THREADS() {
      return this.THREADS;
   }

   public ExecutorService getTaskResultExecutor() {
      return this.getTaskResultExecutor;
   }

   public ThreadLocal<SerializerInstance> serializer() {
      return this.serializer;
   }

   public ThreadLocal<SerializerInstance> taskResultSerializer() {
      return this.taskResultSerializer;
   }

   public void enqueueSuccessfulTask(TaskSetManager taskSetManager, long tid, ByteBuffer serializedData) {
      this.getTaskResultExecutor().execute(new Runnable(this, taskSetManager, tid, serializedData) {
         // $FF: synthetic field
         private final TaskResultGetter $outer;
         public final TaskSetManager taskSetManager$1;
         public final long tid$1;
         public final ByteBuffer serializedData$1;

         public void run() {
            Object var1 = new Object();

            try {
               Utils$.MODULE$.logUncaughtExceptions(new Serializable(this, var1) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final Object nonLocalReturnKey1$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     label90:
                     try {
                        TaskResult var9 = (TaskResult)((SerializerInstance)this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().serializer().get()).deserialize(this.$outer.serializedData$1, ClassTag$.MODULE$.apply(TaskResult.class));
                        Tuple2 var11;
                        if (var9 instanceof DirectTaskResult) {
                           DirectTaskResult var10 = (DirectTaskResult)var9;
                           if (!this.$outer.taskSetManager$1.canFetchMoreResults((long)this.$outer.serializedData$1.limit())) {
                              throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
                           }

                           var10.value((SerializerInstance)this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().taskResultSerializer().get());
                           var11 = new Tuple2(var10, BoxesRunTime.boxToInteger(this.$outer.serializedData$1.limit()));
                        } else {
                           if (!(var9 instanceof IndirectTaskResult)) {
                              throw new MatchError(var9);
                           }

                           IndirectTaskResult var12 = (IndirectTaskResult)var9;
                           BlockId blockId = var12.blockId();
                           int size = var12.size();
                           if (!this.$outer.taskSetManager$1.canFetchMoreResults((long)size)) {
                              this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.blockManager().master().removeBlock(blockId);
                              throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
                           }

                           this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().logDebug(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringOps(Predef$.MODULE$.augmentString("Fetching indirect task result for TID %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$anonfun$$$outer().tid$1)}));
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                           this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$scheduler.handleTaskGettingResult(this.$outer.taskSetManager$1, this.$outer.tid$1);
                           Option serializedTaskResult = this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.blockManager().getRemoteBytes(blockId);
                           if (!serializedTaskResult.isDefined()) {
                              this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$scheduler.handleFailedTask(this.$outer.taskSetManager$1, this.$outer.tid$1, TaskState$.MODULE$.FINISHED(), TaskResultLost$.MODULE$);
                              throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
                           }

                           DirectTaskResult deserializedResult = (DirectTaskResult)((SerializerInstance)this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().serializer().get()).deserialize(((ChunkedByteBuffer)serializedTaskResult.get()).toByteBuffer(), ClassTag$.MODULE$.apply(DirectTaskResult.class));
                           deserializedResult.value((SerializerInstance)this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().taskResultSerializer().get());
                           this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.blockManager().master().removeBlock(blockId);
                           var11 = new Tuple2(deserializedResult, BoxesRunTime.boxToInteger(size));
                        }

                        if (var11 == null) {
                           throw new MatchError(var11);
                        }

                        DirectTaskResult result = (DirectTaskResult)var11._1();
                        int size = var11._2$mcI$sp();
                        Tuple2 var19 = new Tuple2(result, BoxesRunTime.boxToInteger(size));
                        DirectTaskResult result = (DirectTaskResult)var19._1();
                        int size = var19._2$mcI$sp();
                        result.accumUpdates_$eq((Seq)result.accumUpdates().map(new Serializable(this, size) {
                           public static final long serialVersionUID = 0L;
                           private final int size$1;

                           public final AccumulatorV2<? super Long, ? super Long> apply(AccumulatorV2<?, ?> a) {
                              Object var4;
                              label31: {
                                 Option var10000 = a.name();
                                 Some var2 = new Some(InternalAccumulator$.MODULE$.RESULT_SIZE());
                                 if (var10000 == null) {
                                    if (var2 != null) {
                                       break label31;
                                    }
                                 } else if (!var10000.equals(var2)) {
                                    break label31;
                                 }

                                 LongAccumulator acc = (LongAccumulator)a;
                                 Predef$.MODULE$.assert(acc.sum() == 0L, new Serializable(this) {
                                    public static final long serialVersionUID = 0L;

                                    public final String apply() {
                                       return "task result size should not have been set on the executors";
                                    }
                                 });
                                 acc.setValue((long)this.size$1);
                                 var4 = acc;
                                 return (AccumulatorV2<? super Long, ? super Long>)var4;
                              }

                              var4 = a;
                              return (AccumulatorV2<? super Long, ? super Long>)var4;
                           }

                           public {
                              this.size$1 = size$1;
                           }
                        }, Seq$.MODULE$.canBuildFrom()));
                        this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$scheduler.handleSuccessfulTask(this.$outer.taskSetManager$1, this.$outer.tid$1, result);
                     } catch (Throwable var23) {
                        if (var23 instanceof ClassNotFoundException) {
                           ClassLoader loader = Thread.currentThread().getContextClassLoader();
                           this.$outer.taskSetManager$1.abort((new StringBuilder()).append((Object)"ClassNotFound with classloader: ").append((Object)loader).toString(), this.$outer.taskSetManager$1.abort$default$2());
                           BoxedUnit var24 = BoxedUnit.UNIT;
                        } else {
                           Option var5 = NonFatal$.MODULE$.unapply(var23);
                           if (var5.isEmpty()) {
                              throw var23;
                           }

                           Throwable ex = (Throwable)var5.get();
                           this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().logError(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final String apply() {
                                 return "Exception while getting task result";
                              }
                           }, ex);
                           this.$outer.taskSetManager$1.abort((new StringOps(Predef$.MODULE$.augmentString("Exception while getting task result: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{ex})), this.$outer.taskSetManager$1.abort$default$2());
                           BoxedUnit var3 = BoxedUnit.UNIT;
                        }
                        break label90;
                     }

                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$scheduler$TaskResultGetter$$anon$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
            } catch (NonLocalReturnControl var3) {
               if (var3.key() != var1) {
                  throw var3;
               }

               var3.value$mcV$sp();
            }

         }

         // $FF: synthetic method
         public TaskResultGetter org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.taskSetManager$1 = taskSetManager$1;
               this.tid$1 = tid$1;
               this.serializedData$1 = serializedData$1;
            }
         }
      });
   }

   public void enqueueFailedTask(TaskSetManager taskSetManager, long tid, Enumeration.Value taskState, ByteBuffer serializedData) {
      ObjectRef reason = ObjectRef.create(UnknownReason$.MODULE$);

      try {
         this.getTaskResultExecutor().execute(new Runnable(this, taskSetManager, tid, taskState, serializedData, reason) {
            // $FF: synthetic field
            private final TaskResultGetter $outer;
            public final TaskSetManager taskSetManager$2;
            public final long tid$2;
            public final Enumeration.Value taskState$1;
            public final ByteBuffer serializedData$2;
            public final ObjectRef reason$1;

            public void run() {
               Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     ClassLoader loader = Utils$.MODULE$.getContextOrSparkClassLoader();

                     try {
                        if (this.$outer.serializedData$2 != null && this.$outer.serializedData$2.limit() > 0) {
                           this.$outer.reason$1.elem = (T)((TaskFailedReason)((SerializerInstance)this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().serializer().get()).deserialize(this.$outer.serializedData$2, loader, ClassTag$.MODULE$.apply(TaskFailedReason.class)));
                        }
                     } catch (ClassNotFoundException var8) {
                        this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().logError(new Serializable(this, loader) {
                           public static final long serialVersionUID = 0L;
                           private final ClassLoader loader$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Could not deserialize TaskEndReason: ClassNotFound with classloader ").append((Object)this.loader$1).toString();
                           }

                           public {
                              this.loader$1 = loader$1;
                           }
                        });
                     } catch (Exception var9) {
                     } finally {
                        this.$outer.org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer().org$apache$spark$scheduler$TaskResultGetter$$scheduler.handleFailedTask(this.$outer.taskSetManager$2, this.$outer.tid$2, this.$outer.taskState$1, (TaskFailedReason)this.$outer.reason$1.elem);
                     }

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

            // $FF: synthetic method
            public TaskResultGetter org$apache$spark$scheduler$TaskResultGetter$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.taskSetManager$2 = taskSetManager$2;
                  this.tid$2 = tid$2;
                  this.taskState$1 = taskState$1;
                  this.serializedData$2 = serializedData$2;
                  this.reason$1 = reason$1;
               }
            }
         });
      } catch (Throwable var11) {
         if (!(var11 instanceof RejectedExecutionException) || !this.org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.isStopped()) {
            throw var11;
         }

         BoxedUnit var9 = BoxedUnit.UNIT;
         return;
      }

   }

   public void stop() {
      this.getTaskResultExecutor().shutdownNow();
   }

   public TaskResultGetter(SparkEnv sparkEnv, TaskSchedulerImpl scheduler) {
      this.org$apache$spark$scheduler$TaskResultGetter$$sparkEnv = sparkEnv;
      this.org$apache$spark$scheduler$TaskResultGetter$$scheduler = scheduler;
      Logging$class.$init$(this);
      this.THREADS = sparkEnv.conf().getInt("spark.resultGetter.threads", 4);
      this.getTaskResultExecutor = ThreadUtils$.MODULE$.newDaemonFixedThreadPool(this.THREADS(), "task-result-getter");
      this.serializer = new ThreadLocal<SerializerInstance>(this) {
         // $FF: synthetic field
         private final TaskResultGetter $outer;

         public SerializerInstance initialValue() {
            return this.$outer.org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.closureSerializer().newInstance();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.taskResultSerializer = new ThreadLocal<SerializerInstance>(this) {
         // $FF: synthetic field
         private final TaskResultGetter $outer;

         public SerializerInstance initialValue() {
            return this.$outer.org$apache$spark$scheduler$TaskResultGetter$$sparkEnv.serializer().newInstance();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }
}
