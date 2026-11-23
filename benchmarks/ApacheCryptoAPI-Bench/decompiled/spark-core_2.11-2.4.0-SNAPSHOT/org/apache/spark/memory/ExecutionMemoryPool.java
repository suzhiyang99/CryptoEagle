package org.apache.spark.memory;

import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Unit$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ea!B\u0001\u0003\u0001\tQ!aE#yK\u000e,H/[8o\u001b\u0016lwN]=Q_>d'BA\u0002\u0005\u0003\u0019iW-\\8ss*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017=\u0001\"\u0001D\u0007\u000e\u0003\tI!A\u0004\u0002\u0003\u00155+Wn\u001c:z!>|G\u000e\u0005\u0002\u0011'5\t\u0011C\u0003\u0002\u0013\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0015#\t9Aj\\4hS:<\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\t1|7m[\u0002\u0001!\tIb$D\u0001\u001b\u0015\tYB$\u0001\u0003mC:<'\"A\u000f\u0002\t)\fg/Y\u0005\u0003?i\u0011aa\u00142kK\u000e$\b\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u00155,Wn\u001c:z\u001b>$W\r\u0005\u0002\rG%\u0011AE\u0001\u0002\u000b\u001b\u0016lwN]=N_\u0012,\u0007\"\u0002\u0014\u0001\t\u00039\u0013A\u0002\u001fj]&$h\bF\u0002)S)\u0002\"\u0001\u0004\u0001\t\u000bY)\u0003\u0019\u0001\r\t\u000b\u0005*\u0003\u0019\u0001\u0012\t\r1\u0002\u0001\u0015!\u0003.\u0003!\u0001xn\u001c7OC6,\u0007C\u0001\u00185\u001d\ty#'D\u00011\u0015\u0005\t\u0014!B:dC2\f\u0017BA\u001a1\u0003\u0019\u0001&/\u001a3fM&\u0011QG\u000e\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005M\u0002\u0004b\u0002\u001d\u0001\u0005\u0004%I!O\u0001\u000e[\u0016lwN]=G_J$\u0016m]6\u0016\u0003i\u0002Ba\u000f!C\u00056\tAH\u0003\u0002>}\u00059Q.\u001e;bE2,'BA 1\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003\u0003r\u0012q\u0001S1tQ6\u000b\u0007\u000f\u0005\u00020\u0007&\u0011A\t\r\u0002\u0005\u0019>tw\r\u0003\u0004G\u0001\u0001\u0006IAO\u0001\u000f[\u0016lwN]=G_J$\u0016m]6!Q\u0011)\u0005JU*\u0011\u0005%\u0003V\"\u0001&\u000b\u0005-c\u0015AC2p]\u000e,(O]3oi*\u0011QJT\u0001\u000bC:tw\u000e^1uS>t'\"A(\u0002\u000b)\fg/\u0019=\n\u0005ES%!C$vCJ$W\r\u001a\"z\u0003\u00151\u0018\r\\;fC\u00051\u0002\"B+\u0001\t\u00032\u0016AC7f[>\u0014\u00180V:fIV\t!\tC\u0003Y\u0001\u0011\u0005\u0011,A\u000bhKRlU-\\8ssV\u001b\u0018mZ3G_J$\u0016m]6\u0015\u0005\tS\u0006\"B.X\u0001\u0004\u0011\u0015!\u0004;bg.\fE\u000f^3naRLE\r\u0003\u0004^\u0001\u0011\u0005!AX\u0001\u000eC\u000e\fX/\u001b:f\u001b\u0016lwN]=\u0015\u000b\t{\u0016M\u00196\t\u000b\u0001d\u0006\u0019\u0001\"\u0002\u00119,XNQ=uKNDQa\u0017/A\u0002\tCqa\u0019/\u0011\u0002\u0003\u0007A-A\u0007nCf\u0014Wm\u0012:poB{w\u000e\u001c\t\u0005_\u0015\u0014u-\u0003\u0002ga\tIa)\u001e8di&|g.\r\t\u0003_!L!!\u001b\u0019\u0003\tUs\u0017\u000e\u001e\u0005\bWr\u0003\n\u00111\u0001m\u0003I\u0019w.\u001c9vi\u0016l\u0015\r\u001f)p_2\u001c\u0016N_3\u0011\u0007=j')\u0003\u0002oa\tIa)\u001e8di&|g\u000e\r\u0005\u0006a\u0002!\t!]\u0001\u000ee\u0016dW-Y:f\u001b\u0016lwN]=\u0015\u0007\u001d\u00148\u000fC\u0003a_\u0002\u0007!\tC\u0003\\_\u0002\u0007!\tC\u0003v\u0001\u0011\u0005a/A\fsK2,\u0017m]3BY2lU-\\8ss\u001a{'\u000fV1tWR\u0011!i\u001e\u0005\u00067R\u0004\rA\u0011\u0005\bs\u0002\t\n\u0011\"\u0001{\u0003]\t7-];je\u0016lU-\\8ss\u0012\"WMZ1vYR$3'F\u0001|U\t!GpK\u0001~!\rq\u0018QA\u0007\u0002\u007f*!\u0011\u0011AA\u0002\u0003%)hn\u00195fG.,GM\u0003\u0002Na%\u0019\u0011qA@\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002\f\u0001\t\n\u0011\"\u0001\u0002\u000e\u00059\u0012mY9vSJ,W*Z7pef$C-\u001a4bk2$H\u0005N\u000b\u0003\u0003\u001fQ#\u0001\u001c?"
)
public class ExecutionMemoryPool extends MemoryPool implements Logging {
   private final Object lock;
   public final String org$apache$spark$memory$ExecutionMemoryPool$$poolName;
   @GuardedBy("lock")
   private final HashMap<Object, Object> memoryForTask;
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

   private HashMap<Object, Object> memoryForTask() {
      return this.memoryForTask;
   }

   public long memoryUsed() {
      Object var2;
      synchronized(this.lock) {
         var2 = this.memoryForTask().values().sum(Numeric.LongIsIntegral$.MODULE$);
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public long getMemoryUsageForTask(long taskAttemptId) {
      Object var4;
      synchronized(this.lock) {
         var4 = this.memoryForTask().getOrElse(BoxesRunTime.boxToLong(taskAttemptId), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return 0L;
            }
         });
      }

      return BoxesRunTime.unboxToLong(var4);
   }

   public long acquireMemory(long numBytes, long taskAttemptId, Function1<Object, BoxedUnit> maybeGrowPool, Function0<Object> computeMaxPoolSize) {
      synchronized(this.lock) {
         Predef$.MODULE$.assert(numBytes > 0L, new Serializable(this, numBytes) {
            public static final long serialVersionUID = 0L;
            private final long numBytes$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"invalid number of bytes requested: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.numBytes$1)}));
            }

            public {
               this.numBytes$1 = numBytes$1;
            }
         });
         if (!this.memoryForTask().contains(BoxesRunTime.boxToLong(taskAttemptId))) {
            this.memoryForTask().update(BoxesRunTime.boxToLong(taskAttemptId), BoxesRunTime.boxToLong(0L));
            this.lock.notifyAll();
         }

         while(true) {
            int numActiveTasks = this.memoryForTask().keys().size();
            long curMem = BoxesRunTime.unboxToLong(this.memoryForTask().apply(BoxesRunTime.boxToLong(taskAttemptId)));
            maybeGrowPool.apply$mcVJ$sp(numBytes - this.memoryFree());
            long maxPoolSize = computeMaxPoolSize.apply$mcJ$sp();
            long maxMemoryPerTask = maxPoolSize / (long)numActiveTasks;
            long minMemoryPerTask = this.poolSize() / (long)(2 * numActiveTasks);
            long maxToGrant = scala.math.package$.MODULE$.min(numBytes, scala.math.package$.MODULE$.max(0L, maxMemoryPerTask - curMem));
            long toGrant = scala.math.package$.MODULE$.min(maxToGrant, this.memoryFree());
            if (toGrant >= numBytes || curMem + toGrant >= minMemoryPerTask) {
               this.memoryForTask().update(BoxesRunTime.boxToLong(taskAttemptId), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(this.memoryForTask().apply(BoxesRunTime.boxToLong(taskAttemptId))) + toGrant));
               return toGrant;
            }

            this.logInfo(new Serializable(this, taskAttemptId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutionMemoryPool $outer;
               private final long taskAttemptId$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TID ", " waiting for at least 1/2N of ", " pool to be free"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskAttemptId$1), this.$outer.org$apache$spark$memory$ExecutionMemoryPool$$poolName}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.taskAttemptId$1 = taskAttemptId$1;
                  }
               }
            });
            this.lock.wait();
         }
      }
   }

   public Function1<Object, BoxedUnit> acquireMemory$default$3() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(long additionalSpaceNeeded) {
            this.apply$mcVJ$sp(additionalSpaceNeeded);
         }

         public void apply$mcVJ$sp(long additionalSpaceNeeded) {
            Unit$ var10000 = Unit$.MODULE$;
         }
      };
   }

   public Function0<Object> acquireMemory$default$4() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExecutionMemoryPool $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.$outer.poolSize();
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

   public void releaseMemory(long numBytes, long taskAttemptId) {
      synchronized(this.lock) {
         long curMem = BoxesRunTime.unboxToLong(this.memoryForTask().getOrElse(BoxesRunTime.boxToLong(taskAttemptId), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return 0L;
            }
         }));
         long var10000;
         if (curMem < numBytes) {
            this.logWarning(new Serializable(this, numBytes, curMem) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutionMemoryPool $outer;
               private final long numBytes$2;
               private final long curMem$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Internal error: release called on ", " bytes but task only has ", " bytes "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.numBytes$2), BoxesRunTime.boxToLong(this.curMem$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"of memory from the ", " pool"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$memory$ExecutionMemoryPool$$poolName}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.numBytes$2 = numBytes$2;
                     this.curMem$1 = curMem$1;
                  }
               }
            });
            var10000 = curMem;
         } else {
            var10000 = numBytes;
         }

         long memoryToFree = var10000;
         if (this.memoryForTask().contains(BoxesRunTime.boxToLong(taskAttemptId))) {
            this.memoryForTask().update(BoxesRunTime.boxToLong(taskAttemptId), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(this.memoryForTask().apply(BoxesRunTime.boxToLong(taskAttemptId))) - memoryToFree));
            if (BoxesRunTime.unboxToLong(this.memoryForTask().apply(BoxesRunTime.boxToLong(taskAttemptId))) <= 0L) {
               this.memoryForTask().remove(BoxesRunTime.boxToLong(taskAttemptId));
            } else {
               BoxedUnit var11 = BoxedUnit.UNIT;
            }
         } else {
            BoxedUnit var12 = BoxedUnit.UNIT;
         }

         this.lock.notifyAll();
         BoxedUnit var13 = BoxedUnit.UNIT;
      }
   }

   public long releaseAllMemoryForTask(long taskAttemptId) {
      Long var4;
      synchronized(this.lock) {
         long numBytesToFree = this.getMemoryUsageForTask(taskAttemptId);
         this.releaseMemory(numBytesToFree, taskAttemptId);
         var4 = BoxesRunTime.boxToLong(numBytesToFree);
      }

      return BoxesRunTime.unboxToLong(var4);
   }

   public ExecutionMemoryPool(Object lock, MemoryMode memoryMode) {
      super(lock);
      this.lock = lock;
      Logging$class.$init$(this);
      String var4;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var4 = "on-heap execution";
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var4 = "off-heap execution";
      }

      this.org$apache$spark$memory$ExecutionMemoryPool$$poolName = var4;
      this.memoryForTask = new HashMap<Object, Object>();
   }
}
