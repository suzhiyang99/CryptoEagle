package org.apache.spark.storage;

import java.util.HashMap;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Set;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005d!B\u0001\u0003\u0001\u0011Q!\u0001\u0005\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018J\u001c4p\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!#F\u0007\u0002')\u0011A\u0003B\u0001\tS:$XM\u001d8bY&\u0011ac\u0005\u0002\b\u0019><w-\u001b8h\u0011!A\u0002A!b\u0001\n\u0003Q\u0012A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\nZ\u0002\u0001+\u0005Y\u0002C\u0001\u000f\u001e\u001b\u0005\u0011\u0011B\u0001\u0010\u0003\u00059\u0011En\\2l\u001b\u0006t\u0017mZ3s\u0013\u0012D\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006IaG\u0001\u0010E2|7m['b]\u0006<WM]%eA!A!\u0005\u0001B\u0001B\u0003%1%\u0001\u0004uS6,Wj\u001d\t\u0003\u0019\u0011J!!J\u0007\u0003\t1{gn\u001a\u0005\tO\u0001\u0011)\u0019!C\u0001Q\u0005aQ.\u0019=P]\"+\u0017\r]'f[V\t1\u0005\u0003\u0005+\u0001\t\u0005\t\u0015!\u0003$\u00035i\u0017\r_(o\u0011\u0016\f\u0007/T3nA!AA\u0006\u0001BC\u0002\u0013\u0005\u0001&A\u0007nCb|eM\u001a%fCBlU-\u001c\u0005\t]\u0001\u0011\t\u0011)A\u0005G\u0005qQ.\u0019=PM\u001aDU-\u00199NK6\u0004\u0003\u0002\u0003\u0019\u0001\u0005\u000b\u0007I\u0011A\u0019\u0002\u001bMd\u0017M^3F]\u0012\u0004x.\u001b8u+\u0005\u0011\u0004CA\u001a7\u001b\u0005!$BA\u001b\u0005\u0003\r\u0011\boY\u0005\u0003oQ\u0012aB\u00159d\u000b:$\u0007o\\5oiJ+g\r\u0003\u0005:\u0001\t\u0005\t\u0015!\u00033\u00039\u0019H.\u0019<f\u000b:$\u0007o\\5oi\u0002BQa\u000f\u0001\u0005\u0002q\na\u0001P5oSRtDCB\u001f?\u007f\u0001\u000b%\t\u0005\u0002\u001d\u0001!)\u0001D\u000fa\u00017!)!E\u000fa\u0001G!)qE\u000fa\u0001G!)AF\u000fa\u0001G!)\u0001G\u000fa\u0001e!9A\t\u0001b\u0001\n\u0003A\u0013AB7bq6+W\u000e\u0003\u0004G\u0001\u0001\u0006IaI\u0001\b[\u0006DX*Z7!\u0011\u001dA\u0005\u00011A\u0005\n!\n1b\u00187bgR\u001cV-\u001a8Ng\"9!\n\u0001a\u0001\n\u0013Y\u0015aD0mCN$8+Z3o\u001bN|F%Z9\u0015\u00051{\u0005C\u0001\u0007N\u0013\tqUB\u0001\u0003V]&$\bb\u0002)J\u0003\u0003\u0005\raI\u0001\u0004q\u0012\n\u0004B\u0002*\u0001A\u0003&1%\u0001\u0007`Y\u0006\u001cHoU3f]6\u001b\b\u0005C\u0004U\u0001\u0001\u0007I\u0011\u0002\u0015\u0002\u001b}\u0013X-\\1j]&tw-T3n\u0011\u001d1\u0006\u00011A\u0005\n]\u000b\u0011c\u0018:f[\u0006Lg.\u001b8h\u001b\u0016lw\fJ3r)\ta\u0005\fC\u0004Q+\u0006\u0005\t\u0019A\u0012\t\ri\u0003\u0001\u0015)\u0003$\u00039y&/Z7bS:LgnZ'f[\u0002Bq\u0001\u0018\u0001C\u0002\u0013%Q,A\u0004`E2|7m[:\u0016\u0003y\u0003Ba\u00183gS6\t\u0001M\u0003\u0002bE\u0006!Q\u000f^5m\u0015\u0005\u0019\u0017\u0001\u00026bm\u0006L!!\u001a1\u0003\u000f!\u000b7\u000f['baB\u0011AdZ\u0005\u0003Q\n\u0011qA\u00117pG.LE\r\u0005\u0002\u001dU&\u00111N\u0001\u0002\f\u00052|7m[*uCR,8\u000f\u0003\u0004n\u0001\u0001\u0006IAX\u0001\t?\ndwnY6tA!9q\u000e\u0001b\u0001\n\u0013\u0001\u0018!D0dC\u000eDW\r\u001a\"m_\u000e\\7/F\u0001r!\r\u0011xOZ\u0007\u0002g*\u0011A/^\u0001\b[V$\u0018M\u00197f\u0015\t1X\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001_:\u0003\u000f!\u000b7\u000f[*fi\"1!\u0010\u0001Q\u0001\nE\fabX2bG\",GM\u00117pG.\u001c\b\u0005C\u0003}\u0001\u0011\u0005Q0A\u0005hKR\u001cF/\u0019;vgR\u0019a0a\u0001\u0011\u00071y\u0018.C\u0002\u0002\u00025\u0011aa\u00149uS>t\u0007BBA\u0003w\u0002\u0007a-A\u0004cY>\u001c7.\u00133\t\u000f\u0005%\u0001\u0001\"\u0001\u0002\f\u0005\u0001R\u000f\u001d3bi\u0016d\u0015m\u001d;TK\u0016tWj\u001d\u000b\u0002\u0019\"9\u0011q\u0002\u0001\u0005\u0002\u0005E\u0011aD;qI\u0006$XM\u00117pG.LeNZ8\u0015\u00131\u000b\u0019\"!\u0006\u0002 \u0005\r\u0002bBA\u0003\u0003\u001b\u0001\rA\u001a\u0005\t\u0003/\ti\u00011\u0001\u0002\u001a\u0005a1\u000f^8sC\u001e,G*\u001a<fYB\u0019A$a\u0007\n\u0007\u0005u!A\u0001\u0007Ti>\u0014\u0018mZ3MKZ,G\u000eC\u0004\u0002\"\u00055\u0001\u0019A\u0012\u0002\u000f5,WnU5{K\"9\u0011QEA\u0007\u0001\u0004\u0019\u0013\u0001\u00033jg.\u001c\u0016N_3\t\u000f\u0005%\u0002\u0001\"\u0001\u0002,\u0005Y!/Z7pm\u0016\u0014En\\2l)\ra\u0015Q\u0006\u0005\b\u0003\u000b\t9\u00031\u0001g\u0011\u0019\t\t\u0004\u0001C\u0001Q\u0005a!/Z7bS:LgnZ'f[\"1\u0011Q\u0007\u0001\u0005\u0002!\n!\u0002\\1tiN+WM\\'t\u0011\u0019\tI\u0004\u0001C\u0001;\u00061!\r\\8dWNDq!!\u0010\u0001\t\u0003\ty$\u0001\u0007dC\u000eDW\r\u001a\"m_\u000e\\7/\u0006\u0002\u0002BA)\u00111IA#M6\tQ/C\u0002\u0002HU\u00141aU3u\u0011\u001d\tY\u0005\u0001C!\u0003\u001b\n\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003\u001f\u0002B!!\u0015\u0002X9\u0019A\"a\u0015\n\u0007\u0005US\"\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u00033\nYF\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003+j\u0001bBA0\u0001\u0011\u0005\u00111B\u0001\u0006G2,\u0017M\u001d"
)
public class BlockManagerInfo implements Logging {
   private final BlockManagerId blockManagerId;
   private final long timeMs;
   private final long maxOnHeapMem;
   private final long maxOffHeapMem;
   private final RpcEndpointRef slaveEndpoint;
   private final long maxMem;
   private long _lastSeenMs;
   private long org$apache$spark$storage$BlockManagerInfo$$_remainingMem;
   private final HashMap<BlockId, BlockStatus> _blocks;
   private final HashSet<BlockId> _cachedBlocks;
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

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public long maxOnHeapMem() {
      return this.maxOnHeapMem;
   }

   public long maxOffHeapMem() {
      return this.maxOffHeapMem;
   }

   public RpcEndpointRef slaveEndpoint() {
      return this.slaveEndpoint;
   }

   public long maxMem() {
      return this.maxMem;
   }

   private long _lastSeenMs() {
      return this._lastSeenMs;
   }

   private void _lastSeenMs_$eq(long x$1) {
      this._lastSeenMs = x$1;
   }

   public long org$apache$spark$storage$BlockManagerInfo$$_remainingMem() {
      return this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem;
   }

   private void org$apache$spark$storage$BlockManagerInfo$$_remainingMem_$eq(long x$1) {
      this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem = x$1;
   }

   private HashMap<BlockId, BlockStatus> _blocks() {
      return this._blocks;
   }

   private HashSet<BlockId> _cachedBlocks() {
      return this._cachedBlocks;
   }

   public Option<BlockStatus> getStatus(BlockId blockId) {
      return Option$.MODULE$.<BlockStatus>apply(this._blocks().get(blockId));
   }

   public void updateLastSeenMs() {
      this._lastSeenMs_$eq(System.currentTimeMillis());
   }

   public void updateBlockInfo(BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      this.updateLastSeenMs();
      boolean blockExists = this._blocks().containsKey(blockId);
      LongRef originalMemSize = LongRef.create(0L);
      LongRef originalDiskSize = LongRef.create(0L);
      StorageLevel originalLevel = StorageLevel$.MODULE$.NONE();
      if (blockExists) {
         BlockStatus blockStatus = (BlockStatus)this._blocks().get(blockId);
         originalLevel = blockStatus.storageLevel();
         originalMemSize.elem = blockStatus.memSize();
         originalDiskSize.elem = blockStatus.diskSize();
         if (originalLevel.useMemory()) {
            this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem_$eq(this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem() + originalMemSize.elem);
         }
      }

      if (storageLevel.isValid()) {
         BlockStatus blockStatus = null;
         if (storageLevel.useMemory()) {
            blockStatus = new BlockStatus(storageLevel, memSize, 0L);
            this._blocks().put(blockId, blockStatus);
            this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem_$eq(this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem() - memSize);
            if (blockExists) {
               this.logInfo(new Serializable(this, blockId, memSize, originalMemSize) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final BlockManagerInfo $outer;
                  private final BlockId blockId$5;
                  private final long memSize$1;
                  private final LongRef originalMemSize$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Updated ", " in memory on ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (current size: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.memSize$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" original size: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.originalMemSize$1.elem)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" free: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$BlockManagerInfo$$_remainingMem())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.blockId$5 = blockId$5;
                        this.memSize$1 = memSize$1;
                        this.originalMemSize$1 = originalMemSize$1;
                     }
                  }
               });
            } else {
               this.logInfo(new Serializable(this, blockId, memSize) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final BlockManagerInfo $outer;
                  private final BlockId blockId$5;
                  private final long memSize$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Added ", " in memory on ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (size: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.memSize$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" free: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$BlockManagerInfo$$_remainingMem())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.blockId$5 = blockId$5;
                        this.memSize$1 = memSize$1;
                     }
                  }
               });
            }
         }

         if (storageLevel.useDisk()) {
            blockStatus = new BlockStatus(storageLevel, 0L, diskSize);
            this._blocks().put(blockId, blockStatus);
            if (blockExists) {
               this.logInfo(new Serializable(this, blockId, diskSize, originalDiskSize) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final BlockManagerInfo $outer;
                  private final BlockId blockId$5;
                  private final long diskSize$1;
                  private final LongRef originalDiskSize$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Updated ", " on disk on ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (current size: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.diskSize$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" original size: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.originalDiskSize$1.elem)}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.blockId$5 = blockId$5;
                        this.diskSize$1 = diskSize$1;
                        this.originalDiskSize$1 = originalDiskSize$1;
                     }
                  }
               });
            } else {
               this.logInfo(new Serializable(this, blockId, diskSize) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final BlockManagerInfo $outer;
                  private final BlockId blockId$5;
                  private final long diskSize$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Added ", " on disk on ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (size: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.diskSize$1)}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.blockId$5 = blockId$5;
                        this.diskSize$1 = diskSize$1;
                     }
                  }
               });
            }
         }

         if (!blockId.isBroadcast() && blockStatus.isCached()) {
            this._cachedBlocks().$plus$eq(blockId);
         }
      } else if (blockExists) {
         this._blocks().remove(blockId);
         this._cachedBlocks().$minus$eq(blockId);
         if (originalLevel.useMemory()) {
            this.logInfo(new Serializable(this, blockId, originalMemSize) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManagerInfo $outer;
               private final BlockId blockId$5;
               private final LongRef originalMemSize$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removed ", " on ", " in memory"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (size: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.originalMemSize$1.elem)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" free: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$BlockManagerInfo$$_remainingMem())}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$5 = blockId$5;
                     this.originalMemSize$1 = originalMemSize$1;
                  }
               }
            });
         }

         if (originalLevel.useDisk()) {
            this.logInfo(new Serializable(this, blockId, originalDiskSize) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManagerInfo $outer;
               private final BlockId blockId$5;
               private final LongRef originalDiskSize$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removed ", " on ", " on disk"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5, this.$outer.blockManagerId().hostPort()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (size: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.originalDiskSize$1.elem)}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$5 = blockId$5;
                     this.originalDiskSize$1 = originalDiskSize$1;
                  }
               }
            });
         }
      }

   }

   public void removeBlock(BlockId blockId) {
      if (this._blocks().containsKey(blockId)) {
         this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem_$eq(this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem() + ((BlockStatus)this._blocks().get(blockId)).memSize());
         this._blocks().remove(blockId);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this._cachedBlocks().$minus$eq(blockId);
   }

   public long remainingMem() {
      return this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem();
   }

   public long lastSeenMs() {
      return this._lastSeenMs();
   }

   public HashMap<BlockId, BlockStatus> blocks() {
      return this._blocks();
   }

   public Set<BlockId> cachedBlocks() {
      return this._cachedBlocks();
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"BlockManagerInfo ").append((Object)BoxesRunTime.boxToLong(this.timeMs)).append((Object)" ").append((Object)BoxesRunTime.boxToLong(this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem())).toString();
   }

   public void clear() {
      this._blocks().clear();
   }

   public BlockManagerInfo(BlockManagerId blockManagerId, long timeMs, long maxOnHeapMem, long maxOffHeapMem, RpcEndpointRef slaveEndpoint) {
      this.blockManagerId = blockManagerId;
      this.timeMs = timeMs;
      this.maxOnHeapMem = maxOnHeapMem;
      this.maxOffHeapMem = maxOffHeapMem;
      this.slaveEndpoint = slaveEndpoint;
      Logging$class.$init$(this);
      this.maxMem = maxOnHeapMem + maxOffHeapMem;
      this._lastSeenMs = timeMs;
      this.org$apache$spark$storage$BlockManagerInfo$$_remainingMem = this.maxMem();
      this._blocks = new HashMap();
      this._cachedBlocks = new HashSet<BlockId>();
   }
}
