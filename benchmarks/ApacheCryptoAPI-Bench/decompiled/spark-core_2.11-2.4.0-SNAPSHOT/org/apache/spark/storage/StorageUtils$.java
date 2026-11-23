package org.apache.spark.storage;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.math.Numeric;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

public final class StorageUtils$ implements Logging {
   public static final StorageUtils$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new StorageUtils$();
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

   public void dispose(ByteBuffer buffer) {
      if (buffer != null && buffer instanceof MappedByteBuffer) {
         this.logTrace(new Serializable(buffer) {
            public static final long serialVersionUID = 0L;
            private final ByteBuffer buffer$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Disposing of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.buffer$1}));
            }

            public {
               this.buffer$1 = buffer$1;
            }
         });
         this.cleanDirectBuffer((DirectBuffer)buffer);
      }

   }

   private void cleanDirectBuffer(DirectBuffer buffer) {
      Cleaner cleaner = buffer.cleaner();
      if (cleaner != null) {
         cleaner.clean();
      }

   }

   public void updateRddInfo(Seq<RDDInfo> rddInfos, Seq<StorageStatus> statuses) {
      rddInfos.foreach(new Serializable(statuses) {
         public static final long serialVersionUID = 0L;
         private final Seq statuses$1;

         public final void apply(RDDInfo rddInfo) {
            int rddId = rddInfo.id();
            StorageLevel storageLevel = (StorageLevel)((TraversableLike)this.statuses$1.flatMap(new Serializable(this, rddId) {
               public static final long serialVersionUID = 0L;
               private final int rddId$1;

               public final Iterable<StorageLevel> apply(StorageStatus x$13) {
                  return Option$.MODULE$.<StorageLevel>option2Iterable(x$13.rddStorageLevel(this.rddId$1));
               }

               public {
                  this.rddId$1 = rddId$1;
               }
            }, Seq$.MODULE$.canBuildFrom())).headOption().getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StorageLevel apply() {
                  return StorageLevel$.MODULE$.NONE();
               }
            });
            int numCachedPartitions = BoxesRunTime.unboxToInt(((TraversableOnce)this.statuses$1.map(new Serializable(this, rddId) {
               public static final long serialVersionUID = 0L;
               private final int rddId$1;

               public final int apply(StorageStatus x$14) {
                  return x$14.numRddBlocksById(this.rddId$1);
               }

               public {
                  this.rddId$1 = rddId$1;
               }
            }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$));
            long memSize = BoxesRunTime.unboxToLong(((TraversableOnce)this.statuses$1.map(new Serializable(this, rddId) {
               public static final long serialVersionUID = 0L;
               private final int rddId$1;

               public final long apply(StorageStatus x$15) {
                  return x$15.memUsedByRdd(this.rddId$1);
               }

               public {
                  this.rddId$1 = rddId$1;
               }
            }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
            long diskSize = BoxesRunTime.unboxToLong(((TraversableOnce)this.statuses$1.map(new Serializable(this, rddId) {
               public static final long serialVersionUID = 0L;
               private final int rddId$1;

               public final long apply(StorageStatus x$16) {
                  return x$16.diskUsedByRdd(this.rddId$1);
               }

               public {
                  this.rddId$1 = rddId$1;
               }
            }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
            rddInfo.storageLevel_$eq(storageLevel);
            rddInfo.numCachedPartitions_$eq(numCachedPartitions);
            rddInfo.memSize_$eq(memSize);
            rddInfo.diskSize_$eq(diskSize);
         }

         public {
            this.statuses$1 = statuses$1;
         }
      });
   }

   public Map<BlockId, Seq<String>> getRddBlockLocations(int rddId, Seq<StorageStatus> statuses) {
      HashMap blockLocations = new HashMap();
      statuses.foreach(new Serializable(rddId, blockLocations) {
         public static final long serialVersionUID = 0L;
         private final int rddId$2;
         public final HashMap blockLocations$1;

         public final void apply(StorageStatus status) {
            status.rddBlocksById(this.rddId$2).foreach(new Serializable(this, status) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final StorageStatus status$1;

               public final ListBuffer<String> apply(Tuple2<BlockId, BlockStatus> x0$4) {
                  if (x0$4 != null) {
                     BlockId bid = (BlockId)x0$4._1();
                     String location = this.status$1.blockManagerId().hostPort();
                     ListBuffer var4 = ((ListBuffer)this.$outer.blockLocations$1.getOrElseUpdate(bid, new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final ListBuffer<String> apply() {
                           return (ListBuffer)ListBuffer$.MODULE$.empty();
                        }
                     })).$plus$eq(location);
                     return var4;
                  } else {
                     throw new MatchError(x0$4);
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.status$1 = status$1;
                  }
               }
            });
         }

         public {
            this.rddId$2 = rddId$2;
            this.blockLocations$1 = blockLocations$1;
         }
      });
      return blockLocations;
   }

   private StorageUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
