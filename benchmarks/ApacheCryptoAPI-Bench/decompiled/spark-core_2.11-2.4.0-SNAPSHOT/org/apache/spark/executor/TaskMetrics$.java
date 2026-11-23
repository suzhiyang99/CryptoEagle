package org.apache.spark.executor;

import java.util.List;
import org.apache.spark.InternalAccumulator$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockStatus;
import org.apache.spark.util.AccumulatorContext$;
import org.apache.spark.util.AccumulatorMetadata;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class TaskMetrics$ implements Logging, Serializable {
   public static final TaskMetrics$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new TaskMetrics$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

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

   public TaskMetrics empty() {
      TaskMetrics tm = new TaskMetrics();
      tm.nameToAccums().foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(Tuple2<String, AccumulatorV2<? extends Object, ? super List<Tuple2<BlockId, BlockStatus>>>> x0$2) {
            if (x0$2 != null) {
               String name = (String)x0$2._1();
               AccumulatorV2 acc = (AccumulatorV2)x0$2._2();
               acc.metadata_$eq(new AccumulatorMetadata(AccumulatorContext$.MODULE$.newId(), new Some(name), true));
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$2);
            }
         }
      });
      return tm;
   }

   public TaskMetrics registered() {
      TaskMetrics tm = this.empty();
      tm.internalAccums().foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(AccumulatorV2<?, ?> a) {
            AccumulatorContext$.MODULE$.register(a);
         }
      });
      return tm;
   }

   public TaskMetrics fromAccumulatorInfos(Seq<AccumulableInfo> infos) {
      TaskMetrics tm = new TaskMetrics();
      ((IterableLike)infos.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(AccumulableInfo info) {
            return info.name().isDefined() && info.update().isDefined();
         }
      })).foreach(new Serializable(tm) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics tm$1;

         public final void apply(AccumulableInfo info) {
            Object value;
            label23: {
               String name = (String)info.name().get();
               value = info.update().get();
               String var4 = InternalAccumulator$.MODULE$.UPDATED_BLOCK_STATUSES();
               if (name == null) {
                  if (var4 == null) {
                     break label23;
                  }
               } else if (name.equals(var4)) {
                  break label23;
               }

               this.tm$1.nameToAccums().get(name).foreach(new Serializable(this, value) {
                  public static final long serialVersionUID = 0L;
                  private final Object value$1;

                  public final void apply(AccumulatorV2<? extends Object, ? super List<Tuple2<BlockId, BlockStatus>>> x$3) {
                     ((LongAccumulator)x$3).setValue(BoxesRunTime.unboxToLong(this.value$1));
                  }

                  public {
                     this.value$1 = value$1;
                  }
               });
               return;
            }

            this.tm$1.setUpdatedBlockStatuses((List)value);
         }

         public {
            this.tm$1 = tm$1;
         }
      });
      return tm;
   }

   public TaskMetrics fromAccumulators(Seq<AccumulatorV2<?, ?>> accums) {
      TaskMetrics tm = new TaskMetrics();
      accums.foreach(new Serializable(tm) {
         public static final long serialVersionUID = 0L;
         private final TaskMetrics tm$2;

         public final Object apply(AccumulatorV2<?, ?> acc) {
            Option name = acc.name();
            Object var10000;
            if (name.isDefined() && this.tm$2.nameToAccums().contains(name.get())) {
               AccumulatorV2 tmAcc = (AccumulatorV2)this.tm$2.nameToAccums().apply(name.get());
               tmAcc.metadata_$eq(acc.metadata());
               tmAcc.merge(acc);
               var10000 = BoxedUnit.UNIT;
            } else {
               var10000 = this.tm$2.externalAccums().$plus$eq(acc);
            }

            return var10000;
         }

         public {
            this.tm$2 = tm$2;
         }
      });
      return tm;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskMetrics$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
