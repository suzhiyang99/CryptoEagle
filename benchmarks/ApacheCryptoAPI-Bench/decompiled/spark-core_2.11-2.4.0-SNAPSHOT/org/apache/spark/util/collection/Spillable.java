package org.apache.spark.util.collection;

import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.memory.MemoryConsumer;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.memory.TaskMemoryManager;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001Y4a!\u0001\u0002\u0002\u0002\u0019a!!C*qS2d\u0017M\u00197f\u0015\t\u0019A!\u0001\u0006d_2dWm\u0019;j_:T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0011QbJ\n\u0004\u00019!\u0002CA\b\u0013\u001b\u0005\u0001\"BA\t\u0007\u0003\u0019iW-\\8ss&\u00111\u0003\u0005\u0002\u000f\u001b\u0016lwN]=D_:\u001cX/\\3s!\t)\u0002$D\u0001\u0017\u0015\t9b!\u0001\u0005j]R,'O\\1m\u0013\tIbCA\u0004M_\u001e<\u0017N\\4\t\u0011m\u0001!\u0011!Q\u0001\nu\t\u0011\u0003^1tW6+Wn\u001c:z\u001b\u0006t\u0017mZ3s\u0007\u0001\u0001\"a\u0004\u0010\n\u0005}\u0001\"!\u0005+bg.lU-\\8ss6\u000bg.Y4fe\")\u0011\u0005\u0001C\u0001E\u00051A(\u001b8jiz\"\"aI\u001a\u0011\u0007\u0011\u0002Q%D\u0001\u0003!\t1s\u0005\u0004\u0001\u0005\u000b!\u0002!\u0019A\u0015\u0003\u0003\r\u000b\"A\u000b\u0019\u0011\u0005-rS\"\u0001\u0017\u000b\u00035\nQa]2bY\u0006L!a\f\u0017\u0003\u000f9{G\u000f[5oOB\u00111&M\u0005\u0003e1\u00121!\u00118z\u0011\u0015Y\u0002\u00051\u0001\u001e\u0011\u0015)\u0004A\"\u00057\u0003\u0015\u0019\b/\u001b7m)\t9$\b\u0005\u0002,q%\u0011\u0011\b\f\u0002\u0005+:LG\u000fC\u0003\u0004i\u0001\u0007Q\u0005C\u0003=\u0001\u0019EQ(\u0001\u0006g_J\u001cWm\u00159jY2$\u0012A\u0010\t\u0003W}J!\u0001\u0011\u0017\u0003\u000f\t{w\u000e\\3b]\")!\t\u0001C\t\u0007\u0006aQ\r\\3nK:$8OU3bIV\tA\t\u0005\u0002,\u000b&\u0011a\t\f\u0002\u0005\u0019>tw\rC\u0003I\u0001\u0011E\u0011*A\bbI\u0012,E.Z7f]R\u001c(+Z1e)\u00059\u0004BB&\u0001A\u0003%A)\u0001\fj]&$\u0018.\u00197NK6|'/\u001f+ie\u0016\u001c\bn\u001c7e\u0011\u0019i\u0005\u0001)A\u0005\t\u0006qb.^7FY\u0016lWM\u001c;t\r>\u00148-Z*qS2dG\u000b\u001b:fg\"|G\u000e\u001a\u0005\u0007\u001f\u0002\u0001\u000b\u0015\u0002#\u0002#5LX*Z7pef$\u0006N]3tQ>dG\r\u000b\u0002O#B\u00111FU\u0005\u0003'2\u0012\u0001B^8mCRLG.\u001a\u0005\u0007+\u0002\u0001\u000b\u0015\u0002#\u0002\u001b}+G.Z7f]R\u001c(+Z1e\u0011\u00199\u0006\u0001)Q\u0005\t\u0006\u0019r,\\3n_JL()\u001f;fgN\u0003\u0018\u000e\u001c7fI\"\u0012a+\u0015\u0005\r5\u0002!\t\u0011!B\u0001\u0002\u0003\u0006KaW\u00018_J<G%\u00199bG\",Ge\u001d9be.$S\u000f^5mI\r|G\u000e\\3di&|g\u000eJ*qS2d\u0017M\u00197fI\u0011z6\u000f]5mY\u000e{WO\u001c;\u0011\u0005-b\u0016BA/-\u0005\rIe\u000e\u001e\u0005\u0006?\u0002!\t\u0002Y\u0001\u000b[\u0006L(-Z*qS2dGc\u0001 bE\")1A\u0018a\u0001K!)1M\u0018a\u0001\t\u0006i1-\u001e:sK:$X*Z7pefDQ!\u000e\u0001\u0005B\u0015$2\u0001\u00124i\u0011\u00159G\r1\u0001E\u0003\u0011\u0019\u0018N_3\t\u000b%$\u0007\u0019\u0001\b\u0002\u000fQ\u0014\u0018nZ4fe\")1\u000e\u0001C\u0001\u0007\u0006\u0011R.Z7pef\u0014\u0015\u0010^3t'BLG\u000e\\3e\u0011\u0015i\u0007\u0001\"\u0001J\u00035\u0011X\r\\3bg\u0016lU-\\8ss\")q\u000e\u0001C\u0005a\u0006YAn\\4Ta&dG.Y4f)\t9\u0014\u000fC\u0003h]\u0002\u0007A\t\u000b\u0002ogB\u00111\u0006^\u0005\u0003k2\u0012a!\u001b8mS:,\u0007"
)
public abstract class Spillable<C> extends MemoryConsumer implements Logging {
   private final TaskMemoryManager taskMemoryManager;
   private final long initialMemoryThreshold;
   private final long numElementsForceSpillThreshold;
   private volatile long myMemoryThreshold;
   private long _elementsRead;
   private volatile long _memoryBytesSpilled;
   public int org$apache$spark$util$collection$Spillable$$_spillCount;
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

   public abstract void spill(C var1);

   public abstract boolean forceSpill();

   public long elementsRead() {
      return this._elementsRead;
   }

   public void addElementsRead() {
      ++this._elementsRead;
   }

   public boolean maybeSpill(C collection, long currentMemory) {
      boolean shouldSpill = false;
      if (this.elementsRead() % 32L == 0L && currentMemory >= this.myMemoryThreshold) {
         long amountToRequest = 2L * currentMemory - this.myMemoryThreshold;
         long granted = this.acquireMemory(amountToRequest);
         this.myMemoryThreshold += granted;
         shouldSpill = currentMemory >= this.myMemoryThreshold;
      }

      shouldSpill = shouldSpill || this._elementsRead > this.numElementsForceSpillThreshold;
      if (shouldSpill) {
         ++this.org$apache$spark$util$collection$Spillable$$_spillCount;
         this.logSpillage(currentMemory);
         this.spill(collection);
         this._elementsRead = 0L;
         this._memoryBytesSpilled += currentMemory;
         this.releaseMemory();
      }

      return shouldSpill;
   }

   public long spill(long size, MemoryConsumer trigger) {
      long var9;
      label35: {
         if (trigger == null) {
            if (this == null) {
               break label35;
            }
         } else if (trigger.equals(this)) {
            break label35;
         }

         MemoryMode var10000 = this.taskMemoryManager.getTungstenMemoryMode();
         MemoryMode var5 = MemoryMode.ON_HEAP;
         if (var10000 == null) {
            if (var5 != null) {
               break label35;
            }
         } else if (!var10000.equals(var5)) {
            break label35;
         }

         boolean isSpilled = this.forceSpill();
         if (isSpilled) {
            long freeMemory = this.myMemoryThreshold - this.initialMemoryThreshold;
            this._memoryBytesSpilled += freeMemory;
            this.releaseMemory();
            var9 = freeMemory;
         } else {
            var9 = 0L;
         }

         return var9;
      }

      var9 = 0L;
      return var9;
   }

   public long memoryBytesSpilled() {
      return this._memoryBytesSpilled;
   }

   public void releaseMemory() {
      this.freeMemory(this.myMemoryThreshold - this.initialMemoryThreshold);
      this.myMemoryThreshold = this.initialMemoryThreshold;
   }

   private void logSpillage(long size) {
      long threadId = Thread.currentThread().getId();
      this.logInfo(new Serializable(this, size, threadId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Spillable $outer;
         private final long size$1;
         private final long threadId$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Thread %d spilling in-memory map of %s to disk (%d time%s so far)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.threadId$1), org.apache.spark.util.Utils$.MODULE$.bytesToString(this.size$1), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$util$collection$Spillable$$_spillCount), this.$outer.org$apache$spark$util$collection$Spillable$$_spillCount > 1 ? "s" : ""}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.size$1 = size$1;
               this.threadId$1 = threadId$1;
            }
         }
      });
   }

   public Spillable(TaskMemoryManager taskMemoryManager) {
      super(taskMemoryManager);
      this.taskMemoryManager = taskMemoryManager;
      Logging$class.$init$(this);
      this.initialMemoryThreshold = SparkEnv$.MODULE$.get().conf().getLong("spark.shuffle.spill.initialMemoryThreshold", 5242880L);
      this.numElementsForceSpillThreshold = SparkEnv$.MODULE$.get().conf().getLong("spark.shuffle.spill.numElementsForceSpillThreshold", Long.MAX_VALUE);
      this.myMemoryThreshold = this.initialMemoryThreshold;
      this._elementsRead = 0L;
      this._memoryBytesSpilled = 0L;
      this.org$apache$spark$util$collection$Spillable$$_spillCount = 0;
   }
}
