package org.apache.spark.memory;

import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.memory.MemoryStore;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ma!B\u0001\u0003\u0001\tQ!!E*u_J\fw-Z'f[>\u0014\u0018\u0010U8pY*\u00111\u0001B\u0001\u0007[\u0016lwN]=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0010!\taQ\"D\u0001\u0003\u0013\tq!A\u0001\u0006NK6|'/\u001f)p_2\u0004\"\u0001E\n\u000e\u0003EQ!A\u0005\u0003\u0002\u0011%tG/\u001a:oC2L!\u0001F\t\u0003\u000f1{wmZ5oO\"Aa\u0003\u0001B\u0001B\u0003%\u0001$\u0001\u0003m_\u000e\\7\u0001\u0001\t\u00033yi\u0011A\u0007\u0006\u00037q\tA\u0001\\1oO*\tQ$\u0001\u0003kCZ\f\u0017BA\u0010\u001b\u0005\u0019y%M[3di\"A\u0011\u0005\u0001B\u0001B\u0003%!%\u0001\u0006nK6|'/_'pI\u0016\u0004\"\u0001D\u0012\n\u0005\u0011\u0012!AC'f[>\u0014\u00180T8eK\")a\u0005\u0001C\u0001O\u00051A(\u001b8jiz\"2\u0001K\u0015+!\ta\u0001\u0001C\u0003\u0017K\u0001\u0007\u0001\u0004C\u0003\"K\u0001\u0007!\u0005\u0003\u0004-\u0001\u0001\u0006I!L\u0001\ta>|GNT1nKB\u0011a\u0006\u000e\b\u0003_Ij\u0011\u0001\r\u0006\u0002c\u0005)1oY1mC&\u00111\u0007M\u0001\u0007!J,G-\u001a4\n\u0005U2$AB*ue&twM\u0003\u00024a!1\u0001\b\u0001Q!\ne\n1bX7f[>\u0014\u00180V:fIB\u0011qFO\u0005\u0003wA\u0012A\u0001T8oO\"\"q'P$I!\tqT)D\u0001@\u0015\t\u0001\u0015)\u0001\u0006d_:\u001cWO\u001d:f]RT!AQ\"\u0002\u0015\u0005tgn\u001c;bi&|gNC\u0001E\u0003\u0015Q\u0017M^1y\u0013\t1uHA\u0005Hk\u0006\u0014H-\u001a3Cs\u0006)a/\u00197vK\u0006\na\u0003C\u0003K\u0001\u0011\u00053*\u0001\u0006nK6|'/_+tK\u0012,\u0012!\u000f\u0005\n\u001b\u0002\u0001\r\u00111A\u0005\n9\u000bAbX7f[>\u0014\u0018p\u0015;pe\u0016,\u0012a\u0014\t\u0003!Rk\u0011!\u0015\u0006\u0003\u0007IS!a\u0015\u0003\u0002\u000fM$xN]1hK&\u0011Q+\u0015\u0002\f\u001b\u0016lwN]=Ti>\u0014X\rC\u0005X\u0001\u0001\u0007\t\u0019!C\u00051\u0006\u0001r,\\3n_JL8\u000b^8sK~#S-\u001d\u000b\u00033r\u0003\"a\f.\n\u0005m\u0003$\u0001B+oSRDq!\u0018,\u0002\u0002\u0003\u0007q*A\u0002yIEBaa\u0018\u0001!B\u0013y\u0015!D0nK6|'/_*u_J,\u0007\u0005C\u0003b\u0001\u0011\u0005a*A\u0006nK6|'/_*u_J,\u0007\"B2\u0001\t\u000b!\u0017AD:fi6+Wn\u001c:z'R|'/\u001a\u000b\u00033\u0016DQA\u001a2A\u0002=\u000bQa\u001d;pe\u0016DQ\u0001\u001b\u0001\u0005\u0002%\fQ\"Y2rk&\u0014X-T3n_JLHc\u00016ngB\u0011qf[\u0005\u0003YB\u0012qAQ8pY\u0016\fg\u000eC\u0003oO\u0002\u0007q.A\u0004cY>\u001c7.\u00133\u0011\u0005A\fX\"\u0001*\n\u0005I\u0014&a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\u0006i\u001e\u0004\r!O\u0001\t]Vl')\u001f;fg\")\u0001\u000e\u0001C\u0001mR!!n\u001e={\u0011\u0015qW\u000f1\u0001p\u0011\u0015IX\u000f1\u0001:\u0003EqW/\u001c\"zi\u0016\u001cHk\\!dcVL'/\u001a\u0005\u0006wV\u0004\r!O\u0001\u000f]Vl')\u001f;fgR{gI]3f\u0011\u0015i\b\u0001\"\u0001\u007f\u00035\u0011X\r\\3bg\u0016lU-\\8ssR\u0011\u0011l \u0005\u0007\u0003\u0003a\b\u0019A\u001d\u0002\tML'0\u001a\u0005\b\u0003\u000b\u0001A\u0011AA\u0004\u0003A\u0011X\r\\3bg\u0016\fE\u000e\\'f[>\u0014\u0018\u0010F\u0001Z\u0011\u001d\tY\u0001\u0001C\u0001\u0003\u001b\tQC\u001a:fKN\u0003\u0018mY3U_NC'/\u001b8l!>|G\u000eF\u0002:\u0003\u001fAq!!\u0005\u0002\n\u0001\u0007\u0011(A\u0006ta\u0006\u001cW\rV8Ge\u0016,\u0007"
)
public class StorageMemoryPool extends MemoryPool implements Logging {
   private final Object lock;
   private final MemoryMode memoryMode;
   private final String poolName;
   @GuardedBy("lock")
   public long org$apache$spark$memory$StorageMemoryPool$$_memoryUsed;
   private MemoryStore _memoryStore;
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

   public long memoryUsed() {
      Long var2;
      synchronized(this.lock) {
         var2 = BoxesRunTime.boxToLong(this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed);
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   private MemoryStore _memoryStore() {
      return this._memoryStore;
   }

   private void _memoryStore_$eq(MemoryStore x$1) {
      this._memoryStore = x$1;
   }

   public MemoryStore memoryStore() {
      if (this._memoryStore() == null) {
         throw new IllegalStateException("memory store not initialized yet");
      } else {
         return this._memoryStore();
      }
   }

   public final void setMemoryStore(MemoryStore store) {
      this._memoryStore_$eq(store);
   }

   public boolean acquireMemory(BlockId blockId, long numBytes) {
      Boolean var5;
      synchronized(this.lock) {
         long numBytesToFree = scala.math.package$.MODULE$.max(0L, numBytes - this.memoryFree());
         var5 = BoxesRunTime.boxToBoolean(this.acquireMemory(blockId, numBytes, numBytesToFree));
      }

      return BoxesRunTime.unboxToBoolean(var5);
   }

   public boolean acquireMemory(BlockId blockId, long numBytesToAcquire, long numBytesToFree) {
      Boolean var7;
      synchronized(this.lock) {
         Predef$.MODULE$.assert(numBytesToAcquire >= 0L);
         Predef$.MODULE$.assert(numBytesToFree >= 0L);
         Predef$.MODULE$.assert(this.memoryUsed() <= this.poolSize());
         if (numBytesToFree > 0L) {
            BoxesRunTime.boxToLong(this.memoryStore().evictBlocksToFreeSpace(new Some(blockId), numBytesToFree, this.memoryMode));
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         boolean enoughMemory = numBytesToAcquire <= this.memoryFree();
         if (enoughMemory) {
            this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed += numBytesToAcquire;
         }

         var7 = BoxesRunTime.boxToBoolean(enoughMemory);
      }

      return BoxesRunTime.unboxToBoolean(var7);
   }

   public void releaseMemory(long size) {
      synchronized(this.lock) {
         if (size > this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed) {
            this.logWarning(new Serializable(this, size) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final StorageMemoryPool $outer;
               private final long size$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to release ", " bytes of storage "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.size$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"memory when we only have ", " bytes"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed)}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.size$1 = size$1;
                  }
               }
            });
            this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed = 0L;
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed -= size;
            BoxedUnit var5 = BoxedUnit.UNIT;
         }

      }
   }

   public void releaseAllMemory() {
      synchronized(this.lock) {
         this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed = 0L;
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public long freeSpaceToShrinkPool(long spaceToFree) {
      Long var4;
      synchronized(this.lock) {
         long spaceFreedByReleasingUnusedMemory = scala.math.package$.MODULE$.min(spaceToFree, this.memoryFree());
         long remainingSpaceToFree = spaceToFree - spaceFreedByReleasingUnusedMemory;
         Long var10000;
         if (remainingSpaceToFree > 0L) {
            long spaceFreedByEviction = this.memoryStore().evictBlocksToFreeSpace(None$.MODULE$, remainingSpaceToFree, this.memoryMode);
            var10000 = BoxesRunTime.boxToLong(spaceFreedByReleasingUnusedMemory + spaceFreedByEviction);
         } else {
            var10000 = BoxesRunTime.boxToLong(spaceFreedByReleasingUnusedMemory);
         }

         var4 = var10000;
      }

      return BoxesRunTime.unboxToLong(var4);
   }

   public StorageMemoryPool(Object lock, MemoryMode memoryMode) {
      super(lock);
      this.lock = lock;
      this.memoryMode = memoryMode;
      Logging$class.$init$(this);
      String var4;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var4 = "on-heap storage";
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var4 = "off-heap storage";
      }

      this.poolName = var4;
      this.org$apache$spark$memory$StorageMemoryPool$$_memoryUsed = 0L;
   }
}
