package org.apache.spark.memory;

import org.apache.spark.SparkConf;
import org.apache.spark.storage.BlockId;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001Q4Q!\u0001\u0002\u0001\t)\u0011A#\u00168jM&,G-T3n_JLX*\u00198bO\u0016\u0014(BA\u0002\u0005\u0003\u0019iW-\\8ss*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u000e\u001b\u0016lwN]=NC:\fw-\u001a:\t\u0011A\u0001!\u0011!Q\u0001\nI\tAaY8oM\u000e\u0001\u0001CA\n\u0015\u001b\u0005!\u0011BA\u000b\u0005\u0005%\u0019\u0006/\u0019:l\u0007>tg\r\u0003\u0005\u0018\u0001\t\u0015\r\u0011\"\u0001\u0019\u00035i\u0017\r\u001f%fCBlU-\\8ssV\t\u0011\u0004\u0005\u0002\u001b;5\t1DC\u0001\u001d\u0003\u0015\u00198-\u00197b\u0013\tq2D\u0001\u0003M_:<\u0007\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001d5\f\u0007\u0010S3ba6+Wn\u001c:zA!A!\u0005\u0001B\u0001B\u0003%\u0011$A\fp]\"+\u0017\r]*u_J\fw-\u001a*fO&|gnU5{K\"AA\u0005\u0001B\u0001B\u0003%Q%\u0001\u0005ok6\u001cuN]3t!\tQb%\u0003\u0002(7\t\u0019\u0011J\u001c;\t\r%\u0002A\u0011\u0001\u0002+\u0003\u0019a\u0014N\\5u}Q)1\u0006L\u0017/_A\u0011A\u0002\u0001\u0005\u0006!!\u0002\rA\u0005\u0005\u0006/!\u0002\r!\u0007\u0005\u0006E!\u0002\r!\u0007\u0005\u0006I!\u0002\r!\n\u0005\u0006c\u0001!IAM\u0001\u0011CN\u001cXM\u001d;J]Z\f'/[1oiN$\u0012a\r\t\u00035QJ!!N\u000e\u0003\tUs\u0017\u000e\u001e\u0005\u0006o\u0001!\t\u0005G\u0001\u0017[\u0006DxJ\u001c%fCB\u001cFo\u001c:bO\u0016lU-\\8ss\")\u0011\b\u0001C!1\u00059R.\u0019=PM\u001aDU-\u00199Ti>\u0014\u0018mZ3NK6|'/\u001f\u0005\u0007w\u0001!\tE\u0001\u001f\u0002-\u0005\u001c\u0017/^5sK\u0016CXmY;uS>tW*Z7pef$B!G\u001f@\u0003\")aH\u000fa\u00013\u0005Aa.^7CsR,7\u000fC\u0003Au\u0001\u0007\u0011$A\u0007uCN\\\u0017\t\u001e;f[B$\u0018\n\u001a\u0005\u0006\u0005j\u0002\raQ\u0001\u000b[\u0016lwN]=N_\u0012,\u0007C\u0001\u0007E\u0013\t)%A\u0001\u0006NK6|'/_'pI\u0016DQa\u0012\u0001\u0005B!\u000bA#Y2rk&\u0014Xm\u0015;pe\u0006<W-T3n_JLH\u0003B%M)V\u0003\"A\u0007&\n\u0005-[\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006\u001b\u001a\u0003\rAT\u0001\bE2|7m[%e!\ty%+D\u0001Q\u0015\t\tF!A\u0004ti>\u0014\u0018mZ3\n\u0005M\u0003&a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\u0006}\u0019\u0003\r!\u0007\u0005\u0006\u0005\u001a\u0003\ra\u0011\u0005\u0006/\u0002!\t\u0005W\u0001\u0014C\u000e\fX/\u001b:f+:\u0014x\u000e\u001c7NK6|'/\u001f\u000b\u0005\u0013fS6\fC\u0003N-\u0002\u0007a\nC\u0003?-\u0002\u0007\u0011\u0004C\u0003C-\u0002\u00071iB\u0003^\u0005!\u0005a,\u0001\u000bV]&4\u0017.\u001a3NK6|'/_'b]\u0006<WM\u001d\t\u0003\u0019}3Q!\u0001\u0002\t\u0002\u0001\u001c\"aX1\u0011\u0005i\u0011\u0017BA2\u001c\u0005\u0019\te.\u001f*fM\")\u0011f\u0018C\u0001KR\ta\fC\u0004h?\n\u0007I\u0011\u00025\u00029I+5+\u0012*W\u000b\u0012{6+W*U\u000b6{V*R'P%f{&)\u0017+F'V\tQ\u0005\u0003\u0004k?\u0002\u0006I!J\u0001\u001e%\u0016\u001bVI\u0015,F\t~\u001b\u0016l\u0015+F\u001b~kU)T(S3~\u0013\u0015\fV#TA!)An\u0018C\u0001[\u0006)\u0011\r\u001d9msR\u00191F\\8\t\u000bAY\u0007\u0019\u0001\n\t\u000b\u0011Z\u0007\u0019A\u0013\t\u000bE|F\u0011\u0002:\u0002\u0019\u001d,G/T1y\u001b\u0016lwN]=\u0015\u0005e\u0019\b\"\u0002\tq\u0001\u0004\u0011\u0002"
)
public class UnifiedMemoryManager extends MemoryManager {
   private final long maxHeapMemory;
   private final long onHeapStorageRegionSize;

   public static UnifiedMemoryManager apply(SparkConf var0, int var1) {
      return UnifiedMemoryManager$.MODULE$.apply(var0, var1);
   }

   public long maxHeapMemory() {
      return this.maxHeapMemory;
   }

   private void assertInvariants() {
      Predef$.MODULE$.assert(this.onHeapExecutionMemoryPool().poolSize() + this.onHeapStorageMemoryPool().poolSize() == this.maxHeapMemory());
      Predef$.MODULE$.assert(this.offHeapExecutionMemoryPool().poolSize() + this.offHeapStorageMemoryPool().poolSize() == this.maxOffHeapMemory());
   }

   public synchronized long maxOnHeapStorageMemory() {
      return this.maxHeapMemory() - this.onHeapExecutionMemoryPool().memoryUsed();
   }

   public synchronized long maxOffHeapStorageMemory() {
      return this.maxOffHeapMemory() - this.offHeapExecutionMemoryPool().memoryUsed();
   }

   public synchronized long acquireExecutionMemory(long numBytes, long taskAttemptId, MemoryMode memoryMode) {
      this.assertInvariants();
      Predef$.MODULE$.assert(numBytes >= 0L);
      Tuple4 var9;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var9 = new Tuple4(this.onHeapExecutionMemoryPool(), this.onHeapStorageMemoryPool(), BoxesRunTime.boxToLong(this.onHeapStorageRegionSize), BoxesRunTime.boxToLong(this.maxHeapMemory()));
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var9 = new Tuple4(this.offHeapExecutionMemoryPool(), this.offHeapStorageMemoryPool(), BoxesRunTime.boxToLong(this.offHeapStorageMemory()), BoxesRunTime.boxToLong(this.maxOffHeapMemory()));
      }

      if (var9 != null) {
         ExecutionMemoryPool executionPool = (ExecutionMemoryPool)var9._1();
         StorageMemoryPool storagePool = (StorageMemoryPool)var9._2();
         long storageRegionSize = BoxesRunTime.unboxToLong(var9._3());
         long maxMemory = BoxesRunTime.unboxToLong(var9._4());
         Tuple4 var16 = new Tuple4(executionPool, storagePool, BoxesRunTime.boxToLong(storageRegionSize), BoxesRunTime.boxToLong(maxMemory));
         ExecutionMemoryPool executionPool = (ExecutionMemoryPool)var16._1();
         StorageMemoryPool storagePool = (StorageMemoryPool)var16._2();
         long storageRegionSize = BoxesRunTime.unboxToLong(var16._3());
         long maxMemory = BoxesRunTime.unboxToLong(var16._4());
         return executionPool.acquireMemory(numBytes, taskAttemptId, new Serializable(this, executionPool, storagePool, storageRegionSize) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final UnifiedMemoryManager $outer;
            private final ExecutionMemoryPool executionPool$1;
            private final StorageMemoryPool storagePool$1;
            private final long storageRegionSize$1;

            public final void apply(long extraMemoryNeeded) {
               this.apply$mcVJ$sp(extraMemoryNeeded);
            }

            public void apply$mcVJ$sp(long extraMemoryNeeded) {
               this.$outer.org$apache$spark$memory$UnifiedMemoryManager$$maybeGrowExecutionPool$1(extraMemoryNeeded, this.executionPool$1, this.storagePool$1, this.storageRegionSize$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.executionPool$1 = executionPool$1;
                  this.storagePool$1 = storagePool$1;
                  this.storageRegionSize$1 = storageRegionSize$1;
               }
            }
         }, new Serializable(this, storagePool, storageRegionSize, maxMemory) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final UnifiedMemoryManager $outer;
            private final StorageMemoryPool storagePool$1;
            private final long storageRegionSize$1;
            private final long maxMemory$1;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return this.$outer.org$apache$spark$memory$UnifiedMemoryManager$$computeMaxExecutionPoolSize$1(this.storagePool$1, this.storageRegionSize$1, this.maxMemory$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.storagePool$1 = storagePool$1;
                  this.storageRegionSize$1 = storageRegionSize$1;
                  this.maxMemory$1 = maxMemory$1;
               }
            }
         });
      } else {
         throw new MatchError(var9);
      }
   }

   public synchronized boolean acquireStorageMemory(BlockId blockId, long numBytes, MemoryMode memoryMode) {
      this.assertInvariants();
      Predef$.MODULE$.assert(numBytes >= 0L);
      Tuple3 var8;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var8 = new Tuple3(this.onHeapExecutionMemoryPool(), this.onHeapStorageMemoryPool(), BoxesRunTime.boxToLong(this.maxOnHeapStorageMemory()));
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var8 = new Tuple3(this.offHeapExecutionMemoryPool(), this.offHeapStorageMemoryPool(), BoxesRunTime.boxToLong(this.maxOffHeapStorageMemory()));
      }

      if (var8 != null) {
         ExecutionMemoryPool executionPool = (ExecutionMemoryPool)var8._1();
         StorageMemoryPool storagePool = (StorageMemoryPool)var8._2();
         long maxMemory = BoxesRunTime.unboxToLong(var8._3());
         Tuple3 var13 = new Tuple3(executionPool, storagePool, BoxesRunTime.boxToLong(maxMemory));
         ExecutionMemoryPool executionPool = (ExecutionMemoryPool)var13._1();
         StorageMemoryPool storagePool = (StorageMemoryPool)var13._2();
         long maxMemory = BoxesRunTime.unboxToLong(var13._3());
         if (numBytes > maxMemory) {
            this.logInfo(new Serializable(this, blockId, numBytes, maxMemory) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$1;
               private final long numBytes$1;
               private final long maxMemory$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Will not store ", " as the required space (", " bytes) exceeds our "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1, BoxesRunTime.boxToLong(this.numBytes$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"memory limit (", " bytes)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.maxMemory$2)}))).toString();
               }

               public {
                  this.blockId$1 = blockId$1;
                  this.numBytes$1 = numBytes$1;
                  this.maxMemory$2 = maxMemory$2;
               }
            });
            return false;
         } else {
            if (numBytes > storagePool.memoryFree()) {
               long memoryBorrowedFromExecution = Math.min(executionPool.memoryFree(), numBytes - storagePool.memoryFree());
               executionPool.decrementPoolSize(memoryBorrowedFromExecution);
               storagePool.incrementPoolSize(memoryBorrowedFromExecution);
            }

            return storagePool.acquireMemory(blockId, numBytes);
         }
      } else {
         throw new MatchError(var8);
      }
   }

   public synchronized boolean acquireUnrollMemory(BlockId blockId, long numBytes, MemoryMode memoryMode) {
      return this.acquireStorageMemory(blockId, numBytes, memoryMode);
   }

   public final void org$apache$spark$memory$UnifiedMemoryManager$$maybeGrowExecutionPool$1(long extraMemoryNeeded, ExecutionMemoryPool executionPool$1, StorageMemoryPool storagePool$1, long storageRegionSize$1) {
      if (extraMemoryNeeded > 0L) {
         long memoryReclaimableFromStorage = scala.math.package$.MODULE$.max(storagePool$1.memoryFree(), storagePool$1.poolSize() - storageRegionSize$1);
         if (memoryReclaimableFromStorage > 0L) {
            long spaceToReclaim = storagePool$1.freeSpaceToShrinkPool(scala.math.package$.MODULE$.min(extraMemoryNeeded, memoryReclaimableFromStorage));
            storagePool$1.decrementPoolSize(spaceToReclaim);
            executionPool$1.incrementPoolSize(spaceToReclaim);
         }
      }

   }

   public final long org$apache$spark$memory$UnifiedMemoryManager$$computeMaxExecutionPoolSize$1(StorageMemoryPool storagePool$1, long storageRegionSize$1, long maxMemory$1) {
      return maxMemory$1 - scala.math.package$.MODULE$.min(storagePool$1.memoryUsed(), storageRegionSize$1);
   }

   public UnifiedMemoryManager(SparkConf conf, long maxHeapMemory, long onHeapStorageRegionSize, int numCores) {
      super(conf, numCores, onHeapStorageRegionSize, maxHeapMemory - onHeapStorageRegionSize);
      this.maxHeapMemory = maxHeapMemory;
      this.onHeapStorageRegionSize = onHeapStorageRegionSize;
      this.assertInvariants();
   }
}
