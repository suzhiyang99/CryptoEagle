package org.apache.spark.memory;

import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.memory.MemoryStore;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.unsafe.array.ByteArrayMethods;
import org.apache.spark.unsafe.memory.MemoryAllocator;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005eeAB\u0001\u0003\u0003\u0003!!BA\u0007NK6|'/_'b]\u0006<WM\u001d\u0006\u0003\u0007\u0011\ta!\\3n_JL(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Ui\u0011a\u0005\u0006\u0003)\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003-M\u0011q\u0001T8hO&tw\r\u0003\u0005\u0019\u0001\t\u0005\t\u0015!\u0003\u001b\u0003\u0011\u0019wN\u001c4\u0004\u0001A\u00111\u0004H\u0007\u0002\t%\u0011Q\u0004\u0002\u0002\n'B\f'o[\"p]\u001aD\u0001b\b\u0001\u0003\u0002\u0003\u0006I\u0001I\u0001\t]Vl7i\u001c:fgB\u0011A\"I\u0005\u0003E5\u00111!\u00138u\u0011!!\u0003A!A!\u0002\u0013)\u0013aE8o\u0011\u0016\f\u0007o\u0015;pe\u0006<W-T3n_JL\bC\u0001\u0007'\u0013\t9SB\u0001\u0003M_:<\u0007\u0002C\u0015\u0001\u0005\u0003\u0005\u000b\u0011B\u0013\u0002+=t\u0007*Z1q\u000bb,7-\u001e;j_:lU-\\8ss\")1\u0006\u0001C\u0001Y\u00051A(\u001b8jiz\"R!L\u00181cI\u0002\"A\f\u0001\u000e\u0003\tAQ\u0001\u0007\u0016A\u0002iAQa\b\u0016A\u0002\u0001BQ\u0001\n\u0016A\u0002\u0015BQ!\u000b\u0016A\u0002\u0015Bq\u0001\u000e\u0001C\u0002\u0013EQ'A\fp]\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010U8pYV\ta\u0007\u0005\u0002/o%\u0011\u0001H\u0001\u0002\u0012'R|'/Y4f\u001b\u0016lwN]=Q_>d\u0007B\u0002\u001e\u0001A\u0003%a'\u0001\rp]\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010U8pY\u0002BC!\u000f\u001fG\u000fB\u0011Q\bR\u0007\u0002})\u0011q\bQ\u0001\u000bG>t7-\u001e:sK:$(BA!C\u0003)\tgN\\8uCRLwN\u001c\u0006\u0002\u0007\u0006)!.\u0019<bq&\u0011QI\u0010\u0002\n\u000fV\f'\u000fZ3e\u0005f\fQA^1mk\u0016\f\u0013\u0001S\u0001\u0005i\"L7\u000fC\u0004K\u0001\t\u0007I\u0011C\u001b\u00021=4g\rS3baN#xN]1hK6+Wn\u001c:z!>|G\u000e\u0003\u0004M\u0001\u0001\u0006IAN\u0001\u001a_\u001a4\u0007*Z1q'R|'/Y4f\u001b\u0016lwN]=Q_>d\u0007\u0005\u000b\u0003Ly\u0019;\u0005bB(\u0001\u0005\u0004%\t\u0002U\u0001\u001a_:DU-\u00199Fq\u0016\u001cW\u000f^5p]6+Wn\u001c:z!>|G.F\u0001R!\tq#+\u0003\u0002T\u0005\t\u0019R\t_3dkRLwN\\'f[>\u0014\u0018\u0010U8pY\"1Q\u000b\u0001Q\u0001\nE\u000b!d\u001c8IK\u0006\u0004X\t_3dkRLwN\\'f[>\u0014\u0018\u0010U8pY\u0002BC\u0001\u0016\u001fG\u000f\"9\u0001\f\u0001b\u0001\n#\u0001\u0016AG8gM\"+\u0017\r]#yK\u000e,H/[8o\u001b\u0016lwN]=Q_>d\u0007B\u0002.\u0001A\u0003%\u0011+A\u000epM\u001aDU-\u00199Fq\u0016\u001cW\u000f^5p]6+Wn\u001c:z!>|G\u000e\t\u0015\u00053r2u\tC\u0004^\u0001\t\u0007K\u0011\u00030\u0002!5\f\u0007p\u00144g\u0011\u0016\f\u0007/T3n_JLX#A\u0013\t\r\u0001\u0004\u0001\u0015!\u0003&\u0003Ei\u0017\r_(gM\"+\u0017\r]'f[>\u0014\u0018\u0010\t\u0005\bE\u0002\u0011\r\u0015\"\u0005_\u0003QygM\u001a%fCB\u001cFo\u001c:bO\u0016lU-\\8ss\"1A\r\u0001Q\u0001\n\u0015\nQc\u001c4g\u0011\u0016\f\u0007o\u0015;pe\u0006<W-T3n_JL\b\u0005C\u0003g\u0001\u0019\u0005a,\u0001\fnCb|e\u000eS3baN#xN]1hK6+Wn\u001c:z\u0011\u0015A\u0007A\"\u0001_\u0003]i\u0017\r_(gM\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010C\u0003k\u0001\u0011\u00151.\u0001\btKRlU-\\8ssN#xN]3\u0015\u00051|\u0007C\u0001\u0007n\u0013\tqWB\u0001\u0003V]&$\b\"\u00029j\u0001\u0004\t\u0018!B:u_J,\u0007C\u0001:w\u001b\u0005\u0019(BA\u0002u\u0015\t)H!A\u0004ti>\u0014\u0018mZ3\n\u0005]\u001c(aC'f[>\u0014\u0018p\u0015;pe\u0016DQ!\u001f\u0001\u0007\u0002i\fA#Y2rk&\u0014Xm\u0015;pe\u0006<W-T3n_JLHCB>\u007f\u0003\u0013\ti\u0001\u0005\u0002\ry&\u0011Q0\u0004\u0002\b\u0005>|G.Z1o\u0011\u0019y\b\u00101\u0001\u0002\u0002\u00059!\r\\8dW&#\u0007\u0003BA\u0002\u0003\u000bi\u0011\u0001^\u0005\u0004\u0003\u000f!(a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\u0007\u0003\u0017A\b\u0019A\u0013\u0002\u00119,XNQ=uKNDq!a\u0004y\u0001\u0004\t\t\"\u0001\u0006nK6|'/_'pI\u0016\u00042ALA\n\u0013\r\t)B\u0001\u0002\u000b\u001b\u0016lwN]=N_\u0012,\u0007bBA\r\u0001\u0019\u0005\u00111D\u0001\u0014C\u000e\fX/\u001b:f+:\u0014x\u000e\u001c7NK6|'/\u001f\u000b\bw\u0006u\u0011qDA\u0011\u0011\u001dy\u0018q\u0003a\u0001\u0003\u0003Aq!a\u0003\u0002\u0018\u0001\u0007Q\u0005\u0003\u0005\u0002\u0010\u0005]\u0001\u0019AA\t\u0011!\t)\u0003\u0001D\u0001\u0005\u0005\u001d\u0012AF1dcVL'/Z#yK\u000e,H/[8o\u001b\u0016lwN]=\u0015\u000f\u0015\nI#a\u000b\u00020!9\u00111BA\u0012\u0001\u0004)\u0003bBA\u0017\u0003G\u0001\r!J\u0001\u000ei\u0006\u001c8.\u0011;uK6\u0004H/\u00133\t\u0011\u0005=\u00111\u0005a\u0001\u0003#A\u0001\"a\r\u0001\t\u0003\u0011\u0011QG\u0001\u0017e\u0016dW-Y:f\u000bb,7-\u001e;j_:lU-\\8ssR9A.a\u000e\u0002:\u0005m\u0002bBA\u0006\u0003c\u0001\r!\n\u0005\b\u0003[\t\t\u00041\u0001&\u0011!\ty!!\rA\u0002\u0005E\u0001\u0002CA \u0001\u0011\u0005!!!\u0011\u0002AI,G.Z1tK\u0006cG.\u0012=fGV$\u0018n\u001c8NK6|'/\u001f$peR\u000b7o\u001b\u000b\u0004K\u0005\r\u0003bBA\u0017\u0003{\u0001\r!\n\u0005\b\u0003\u000f\u0002A\u0011AA%\u0003Q\u0011X\r\\3bg\u0016\u001cFo\u001c:bO\u0016lU-\\8ssR)A.a\u0013\u0002N!9\u00111BA#\u0001\u0004)\u0003\u0002CA\b\u0003\u000b\u0002\r!!\u0005\t\u000f\u0005E\u0003\u0001\"\u0002\u0002T\u00059\"/\u001a7fCN,\u0017\t\u001c7Ti>\u0014\u0018mZ3NK6|'/\u001f\u000b\u0002Y\"9\u0011q\u000b\u0001\u0005\u0006\u0005e\u0013a\u0005:fY\u0016\f7/Z+oe>dG.T3n_JLH#\u00027\u0002\\\u0005u\u0003bBA\u0006\u0003+\u0002\r!\n\u0005\t\u0003\u001f\t)\u00061\u0001\u0002\u0012!1\u0011\u0011\r\u0001\u0005\u0006y\u000b1#\u001a=fGV$\u0018n\u001c8NK6|'/_+tK\u0012Da!!\u001a\u0001\t\u000bq\u0016!E:u_J\fw-Z'f[>\u0014\u00180V:fI\"A\u0011\u0011\u000e\u0001\u0005\u0002\t\tY'\u0001\u0010hKR,\u00050Z2vi&|g.T3n_JLXk]1hK\u001a{'\u000fV1tWR\u0019Q%!\u001c\t\u000f\u00055\u0012q\ra\u0001K!I\u0011\u0011\u000f\u0001C\u0002\u0013\u0015\u00111O\u0001\u0013iVtwm\u001d;f]6+Wn\u001c:z\u001b>$W-\u0006\u0002\u0002\u0012!A\u0011q\u000f\u0001!\u0002\u001b\t\t\"A\nuk:<7\u000f^3o\u001b\u0016lwN]=N_\u0012,\u0007\u0005\u0003\u0005\u0002|\u0001\u0011\r\u0011\"\u0001_\u00035\u0001\u0018mZ3TSj,')\u001f;fg\"9\u0011q\u0010\u0001!\u0002\u0013)\u0013A\u00049bO\u0016\u001c\u0016N_3CsR,7\u000f\t\u0005\u000b\u0003\u0007\u0003!\u0019!C\u0003\u0005\u0005\u0015\u0015a\u0006;v]\u001e\u001cH/\u001a8NK6|'/_!mY>\u001c\u0017\r^8s+\t\t9\t\u0005\u0003\u0002\n\u0006EUBAAF\u0015\r\u0019\u0011Q\u0012\u0006\u0004\u0003\u001f#\u0011AB;og\u00064W-\u0003\u0003\u0002\u0014\u0006-%aD'f[>\u0014\u00180\u00117m_\u000e\fGo\u001c:\t\u0011\u0005]\u0005\u0001)A\u0007\u0003\u000f\u000b\u0001\u0004^;oON$XM\\'f[>\u0014\u00180\u00117m_\u000e\fGo\u001c:!\u0001"
)
public abstract class MemoryManager implements Logging {
   @GuardedBy("this")
   private final StorageMemoryPool onHeapStorageMemoryPool;
   @GuardedBy("this")
   private final StorageMemoryPool offHeapStorageMemoryPool;
   @GuardedBy("this")
   private final ExecutionMemoryPool onHeapExecutionMemoryPool;
   @GuardedBy("this")
   private final ExecutionMemoryPool offHeapExecutionMemoryPool;
   private final long maxOffHeapMemory;
   private final long offHeapStorageMemory;
   private final MemoryMode tungstenMemoryMode;
   private final long pageSizeBytes;
   private final MemoryAllocator tungstenMemoryAllocator;
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

   public StorageMemoryPool onHeapStorageMemoryPool() {
      return this.onHeapStorageMemoryPool;
   }

   public StorageMemoryPool offHeapStorageMemoryPool() {
      return this.offHeapStorageMemoryPool;
   }

   public ExecutionMemoryPool onHeapExecutionMemoryPool() {
      return this.onHeapExecutionMemoryPool;
   }

   public ExecutionMemoryPool offHeapExecutionMemoryPool() {
      return this.offHeapExecutionMemoryPool;
   }

   public long maxOffHeapMemory() {
      return this.maxOffHeapMemory;
   }

   public long offHeapStorageMemory() {
      return this.offHeapStorageMemory;
   }

   public abstract long maxOnHeapStorageMemory();

   public abstract long maxOffHeapStorageMemory();

   public final synchronized void setMemoryStore(MemoryStore store) {
      this.onHeapStorageMemoryPool().setMemoryStore(store);
      this.offHeapStorageMemoryPool().setMemoryStore(store);
   }

   public abstract boolean acquireStorageMemory(BlockId var1, long var2, MemoryMode var4);

   public abstract boolean acquireUnrollMemory(BlockId var1, long var2, MemoryMode var4);

   public abstract long acquireExecutionMemory(long var1, long var3, MemoryMode var5);

   public synchronized void releaseExecutionMemory(long numBytes, long taskAttemptId, MemoryMode memoryMode) {
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         this.onHeapExecutionMemoryPool().releaseMemory(numBytes, taskAttemptId);
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         this.offHeapExecutionMemoryPool().releaseMemory(numBytes, taskAttemptId);
         BoxedUnit var8 = BoxedUnit.UNIT;
      }

   }

   public synchronized long releaseAllExecutionMemoryForTask(long taskAttemptId) {
      return this.onHeapExecutionMemoryPool().releaseAllMemoryForTask(taskAttemptId) + this.offHeapExecutionMemoryPool().releaseAllMemoryForTask(taskAttemptId);
   }

   public synchronized void releaseStorageMemory(long numBytes, MemoryMode memoryMode) {
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         this.onHeapStorageMemoryPool().releaseMemory(numBytes);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         this.offHeapStorageMemoryPool().releaseMemory(numBytes);
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   public final synchronized void releaseAllStorageMemory() {
      this.onHeapStorageMemoryPool().releaseAllMemory();
      this.offHeapStorageMemoryPool().releaseAllMemory();
   }

   public final synchronized void releaseUnrollMemory(long numBytes, MemoryMode memoryMode) {
      this.releaseStorageMemory(numBytes, memoryMode);
   }

   public final synchronized long executionMemoryUsed() {
      return this.onHeapExecutionMemoryPool().memoryUsed() + this.offHeapExecutionMemoryPool().memoryUsed();
   }

   public final synchronized long storageMemoryUsed() {
      return this.onHeapStorageMemoryPool().memoryUsed() + this.offHeapStorageMemoryPool().memoryUsed();
   }

   public synchronized long getExecutionMemoryUsageForTask(long taskAttemptId) {
      return this.onHeapExecutionMemoryPool().getMemoryUsageForTask(taskAttemptId) + this.offHeapExecutionMemoryPool().getMemoryUsageForTask(taskAttemptId);
   }

   public final MemoryMode tungstenMemoryMode() {
      return this.tungstenMemoryMode;
   }

   public long pageSizeBytes() {
      return this.pageSizeBytes;
   }

   public final MemoryAllocator tungstenMemoryAllocator() {
      return this.tungstenMemoryAllocator;
   }

   public MemoryManager(SparkConf conf, int numCores, long onHeapStorageMemory, long onHeapExecutionMemory) {
      Logging$class.$init$(this);
      this.onHeapStorageMemoryPool = new StorageMemoryPool(this, MemoryMode.ON_HEAP);
      this.offHeapStorageMemoryPool = new StorageMemoryPool(this, MemoryMode.OFF_HEAP);
      this.onHeapExecutionMemoryPool = new ExecutionMemoryPool(this, MemoryMode.ON_HEAP);
      this.offHeapExecutionMemoryPool = new ExecutionMemoryPool(this, MemoryMode.OFF_HEAP);
      this.onHeapStorageMemoryPool().incrementPoolSize(onHeapStorageMemory);
      this.onHeapExecutionMemoryPool().incrementPoolSize(onHeapExecutionMemory);
      this.maxOffHeapMemory = BoxesRunTime.unboxToLong(conf.get(org.apache.spark.internal.config.package$.MODULE$.MEMORY_OFFHEAP_SIZE()));
      this.offHeapStorageMemory = (long)((double)this.maxOffHeapMemory() * conf.getDouble("spark.memory.storageFraction", (double)0.5F));
      this.offHeapExecutionMemoryPool().incrementPoolSize(this.maxOffHeapMemory() - this.offHeapStorageMemory());
      this.offHeapStorageMemoryPool().incrementPoolSize(this.offHeapStorageMemory());
      MemoryMode var10001;
      if (BoxesRunTime.unboxToBoolean(conf.get(org.apache.spark.internal.config.package$.MODULE$.MEMORY_OFFHEAP_ENABLED()))) {
         Predef$.MODULE$.require(BoxesRunTime.unboxToLong(conf.get(org.apache.spark.internal.config.package$.MODULE$.MEMORY_OFFHEAP_SIZE())) > 0L, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "spark.memory.offHeap.size must be > 0 when spark.memory.offHeap.enabled == true";
            }
         });
         Predef$.MODULE$.require(Platform.unaligned(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "No support for unaligned Unsafe. Set spark.memory.offHeap.enabled to false.";
            }
         });
         var10001 = MemoryMode.OFF_HEAP;
      } else {
         var10001 = MemoryMode.ON_HEAP;
      }

      this.tungstenMemoryMode = var10001;
      long minPageSize = 1048576L;
      long maxPageSize = 64L * minPageSize;
      int cores = numCores > 0 ? numCores : Runtime.getRuntime().availableProcessors();
      int safetyFactor = 16;
      MemoryMode var15 = this.tungstenMemoryMode();
      long maxTungstenMemory;
      if (MemoryMode.ON_HEAP.equals(var15)) {
         maxTungstenMemory = this.onHeapExecutionMemoryPool().poolSize();
      } else {
         if (!MemoryMode.OFF_HEAP.equals(var15)) {
            throw new MatchError(var15);
         }

         maxTungstenMemory = this.offHeapExecutionMemoryPool().poolSize();
      }

      long size = ByteArrayMethods.nextPowerOf2(maxTungstenMemory / (long)cores / (long)safetyFactor);
      long var20 = scala.math.package$.MODULE$.min(maxPageSize, scala.math.package$.MODULE$.max(minPageSize, size));
      this.pageSizeBytes = conf.getSizeAsBytes("spark.buffer.pageSize", var20);
      MemoryMode var22 = this.tungstenMemoryMode();
      MemoryAllocator var23;
      if (MemoryMode.ON_HEAP.equals(var22)) {
         var23 = MemoryAllocator.HEAP;
      } else {
         if (!MemoryMode.OFF_HEAP.equals(var22)) {
            throw new MatchError(var22);
         }

         var23 = MemoryAllocator.UNSAFE;
      }

      this.tungstenMemoryAllocator = var23;
   }
}
