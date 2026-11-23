package org.apache.spark.memory;

import org.apache.spark.SparkConf;
import org.apache.spark.storage.BlockId;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001M4Q!\u0001\u0002\u0001\t)\u00111c\u0015;bi&\u001cW*Z7pefl\u0015M\\1hKJT!a\u0001\u0003\u0002\r5,Wn\u001c:z\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taQ\"D\u0001\u0003\u0013\tq!AA\u0007NK6|'/_'b]\u0006<WM\u001d\u0005\t!\u0001\u0011\t\u0011)A\u0005%\u0005!1m\u001c8g\u0007\u0001\u0001\"a\u0005\u000b\u000e\u0003\u0011I!!\u0006\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007\u0002C\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u000215\f\u0007p\u00148IK\u0006\u0004X\t_3dkRLwN\\'f[>\u0014\u0018\u0010\u0005\u0002\u001a95\t!DC\u0001\u001c\u0003\u0015\u00198-\u00197b\u0013\ti\"D\u0001\u0003M_:<\u0007\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\t\u0011\u0002-5\f\u0007p\u00148IK\u0006\u00048\u000b^8sC\u001e,W*Z7pef,\u0012\u0001\u0007\u0005\tE\u0001\u0011\t\u0011)A\u00051\u00059R.\u0019=P]\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010\t\u0005\tI\u0001\u0011\t\u0011)A\u0005K\u0005Aa.^7D_J,7\u000f\u0005\u0002\u001aM%\u0011qE\u0007\u0002\u0004\u0013:$\b\"B\u0015\u0001\t\u0003Q\u0013A\u0002\u001fj]&$h\bF\u0003,Y5rs\u0006\u0005\u0002\r\u0001!)\u0001\u0003\u000ba\u0001%!)q\u0003\u000ba\u00011!)q\u0004\u000ba\u00011!)A\u0005\u000ba\u0001K!)\u0011\u0006\u0001C\u0001cQ\u00191FM\u001a\t\u000bA\u0001\u0004\u0019\u0001\n\t\u000b\u0011\u0002\u0004\u0019A\u0013\t\u000fU\u0002!\u0019!C\u0005A\u0005yQ.\u0019=V]J|G\u000e\\'f[>\u0014\u0018\u0010\u0003\u00048\u0001\u0001\u0006I\u0001G\u0001\u0011[\u0006DXK\u001c:pY2lU-\\8ss\u0002BQ!\u000f\u0001\u0005B\u0001\nq#\\1y\u001f\u001a4\u0007*Z1q'R|'/Y4f\u001b\u0016lwN]=\t\u000bm\u0002A\u0011\t\u001f\u0002)\u0005\u001c\u0017/^5sKN#xN]1hK6+Wn\u001c:z)\u0011i\u0004\t\u0013&\u0011\u0005eq\u0014BA \u001b\u0005\u001d\u0011un\u001c7fC:DQ!\u0011\u001eA\u0002\t\u000bqA\u00197pG.LE\r\u0005\u0002D\r6\tAI\u0003\u0002F\t\u000591\u000f^8sC\u001e,\u0017BA$E\u0005\u001d\u0011En\\2l\u0013\u0012DQ!\u0013\u001eA\u0002a\t\u0001B\\;n\u0005f$Xm\u001d\u0005\u0006\u0017j\u0002\r\u0001T\u0001\u000b[\u0016lwN]=N_\u0012,\u0007C\u0001\u0007N\u0013\tq%A\u0001\u0006NK6|'/_'pI\u0016DQ\u0001\u0015\u0001\u0005BE\u000b1#Y2rk&\u0014X-\u00168s_2dW*Z7pef$B!\u0010*T)\")\u0011i\u0014a\u0001\u0005\")\u0011j\u0014a\u00011!)1j\u0014a\u0001\u0019\"1a\u000b\u0001C!\u0005]\u000ba#Y2rk&\u0014X-\u0012=fGV$\u0018n\u001c8NK6|'/\u001f\u000b\u00051aK6\fC\u0003J+\u0002\u0007\u0001\u0004C\u0003[+\u0002\u0007\u0001$A\u0007uCN\\\u0017\t\u001e;f[B$\u0018\n\u001a\u0005\u0006\u0017V\u0003\r\u0001T\u0004\u0007;\nA\t\u0001\u00020\u0002'M#\u0018\r^5d\u001b\u0016lwN]=NC:\fw-\u001a:\u0011\u00051yfAB\u0001\u0003\u0011\u0003!\u0001m\u0005\u0002`CB\u0011\u0011DY\u0005\u0003Gj\u0011a!\u00118z%\u00164\u0007\"B\u0015`\t\u0003)G#\u00010\t\u000f\u001d|&\u0019!C\u0005Q\u0006\u0001R*\u0013(`\u001b\u0016kuJU-`\u0005f#ViU\u000b\u0002K!1!n\u0018Q\u0001\n\u0015\n\u0011#T%O?6+Uj\u0014*Z?\nKF+R*!\u0011\u0015aw\f\"\u0003n\u0003M9W\r^'bqN#xN]1hK6+Wn\u001c:z)\tAb\u000eC\u0003\u0011W\u0002\u0007!\u0003C\u0003q?\u0012%\u0011/A\u000bhKRl\u0015\r_#yK\u000e,H/[8o\u001b\u0016lwN]=\u0015\u0005a\u0011\b\"\u0002\tp\u0001\u0004\u0011\u0002"
)
public class StaticMemoryManager extends MemoryManager {
   private final long maxOnHeapStorageMemory;
   private final long maxUnrollMemory;

   public long maxOnHeapStorageMemory() {
      return this.maxOnHeapStorageMemory;
   }

   private long maxUnrollMemory() {
      return this.maxUnrollMemory;
   }

   public long maxOffHeapStorageMemory() {
      return 0L;
   }

   public synchronized boolean acquireStorageMemory(BlockId blockId, long numBytes, MemoryMode memoryMode) {
      Predef$ var10000;
      boolean var10001;
      label22: {
         label21: {
            var10000 = Predef$.MODULE$;
            MemoryMode var5 = MemoryMode.OFF_HEAP;
            if (memoryMode == null) {
               if (var5 == null) {
                  break label21;
               }
            } else if (memoryMode.equals(var5)) {
               break label21;
            }

            var10001 = true;
            break label22;
         }

         var10001 = false;
      }

      var10000.require(var10001, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "StaticMemoryManager does not support off-heap storage memory";
         }
      });
      boolean var6;
      if (numBytes > this.maxOnHeapStorageMemory()) {
         this.logInfo(new Serializable(this, blockId, numBytes) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StaticMemoryManager $outer;
            private final BlockId blockId$1;
            private final long numBytes$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Will not store ", " as the required space (", " bytes) exceeds our "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1, BoxesRunTime.boxToLong(this.numBytes$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"memory limit (", " bytes)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.maxOnHeapStorageMemory())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.blockId$1 = blockId$1;
                  this.numBytes$1 = numBytes$1;
               }
            }
         });
         var6 = false;
      } else {
         var6 = this.onHeapStorageMemoryPool().acquireMemory(blockId, numBytes);
      }

      return var6;
   }

   public synchronized boolean acquireUnrollMemory(BlockId blockId, long numBytes, MemoryMode memoryMode) {
      Predef$ var10000;
      boolean var10001;
      label17: {
         label16: {
            var10000 = Predef$.MODULE$;
            MemoryMode var5 = MemoryMode.OFF_HEAP;
            if (memoryMode == null) {
               if (var5 == null) {
                  break label16;
               }
            } else if (memoryMode.equals(var5)) {
               break label16;
            }

            var10001 = true;
            break label17;
         }

         var10001 = false;
      }

      var10000.require(var10001, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "StaticMemoryManager does not support off-heap unroll memory";
         }
      });
      long currentUnrollMemory = this.onHeapStorageMemoryPool().memoryStore().currentUnrollMemory();
      long freeMemory = this.onHeapStorageMemoryPool().memoryFree();
      long maxNumBytesToFree = scala.math.package$.MODULE$.max(0L, this.maxUnrollMemory() - currentUnrollMemory - freeMemory);
      long numBytesToFree = scala.math.package$.MODULE$.max(0L, scala.math.package$.MODULE$.min(maxNumBytesToFree, numBytes - freeMemory));
      return this.onHeapStorageMemoryPool().acquireMemory(blockId, numBytes, numBytesToFree);
   }

   public synchronized long acquireExecutionMemory(long numBytes, long taskAttemptId, MemoryMode memoryMode) {
      long var7;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var7 = this.onHeapExecutionMemoryPool().acquireMemory(numBytes, taskAttemptId, this.onHeapExecutionMemoryPool().acquireMemory$default$3(), this.onHeapExecutionMemoryPool().acquireMemory$default$4());
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var7 = this.offHeapExecutionMemoryPool().acquireMemory(numBytes, taskAttemptId, this.offHeapExecutionMemoryPool().acquireMemory$default$3(), this.offHeapExecutionMemoryPool().acquireMemory$default$4());
      }

      return var7;
   }

   public StaticMemoryManager(SparkConf conf, long maxOnHeapExecutionMemory, long maxOnHeapStorageMemory, int numCores) {
      super(conf, numCores, maxOnHeapStorageMemory, maxOnHeapExecutionMemory);
      this.maxOnHeapStorageMemory = maxOnHeapStorageMemory;
      this.offHeapExecutionMemoryPool().incrementPoolSize(this.offHeapStorageMemoryPool().poolSize());
      this.offHeapStorageMemoryPool().decrementPoolSize(this.offHeapStorageMemoryPool().poolSize());
      this.maxUnrollMemory = (long)((double)maxOnHeapStorageMemory * conf.getDouble("spark.storage.unrollFraction", 0.2));
   }

   public StaticMemoryManager(SparkConf conf, int numCores) {
      this(conf, StaticMemoryManager$.MODULE$.org$apache$spark$memory$StaticMemoryManager$$getMaxExecutionMemory(conf), StaticMemoryManager$.MODULE$.org$apache$spark$memory$StaticMemoryManager$$getMaxStorageMemory(conf), numCores);
   }
}
