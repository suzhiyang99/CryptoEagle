package org.apache.spark.storage;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IndexedSeq;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.Iterable;
import scala.collection.immutable.Map;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\t5c!B\u0001\u0003\u0001\u0011Q!A\u0005\"m_\u000e\\W*\u00198bO\u0016\u0014X*Y:uKJT!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0002\u0004%\tAG\u0001\u000fIJLg/\u001a:F]\u0012\u0004x.\u001b8u\u0007\u0001)\u0012a\u0007\t\u00039}i\u0011!\b\u0006\u0003=\u0011\t1A\u001d9d\u0013\t\u0001SD\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0011\t\u0002!\u00111A\u0005\u0002\r\n!\u0003\u001a:jm\u0016\u0014XI\u001c3q_&tGo\u0018\u0013fcR\u0011Ae\n\t\u0003\u0019\u0015J!AJ\u0007\u0003\tUs\u0017\u000e\u001e\u0005\bQ\u0005\n\t\u00111\u0001\u001c\u0003\rAH%\r\u0005\tU\u0001\u0011\t\u0011)Q\u00057\u0005yAM]5wKJ,e\u000e\u001a9pS:$\b\u0005\u0003\u0005-\u0001\t\u0005\t\u0015!\u0003.\u0003\u0011\u0019wN\u001c4\u0011\u00059zS\"\u0001\u0003\n\u0005A\"!!C*qCJ\\7i\u001c8g\u0011!\u0011\u0004A!A!\u0002\u0013\u0019\u0014\u0001C5t\tJLg/\u001a:\u0011\u00051!\u0014BA\u001b\u000e\u0005\u001d\u0011un\u001c7fC:DQa\u000e\u0001\u0005\u0002a\na\u0001P5oSRtD\u0003B\u001d<yu\u0002\"A\u000f\u0001\u000e\u0003\tAQ\u0001\u0007\u001cA\u0002mAQ\u0001\f\u001cA\u00025BQA\r\u001cA\u0002MBqa\u0010\u0001C\u0002\u0013\u0005\u0001)A\u0004uS6,w.\u001e;\u0016\u0003\u0005\u0003\"\u0001\b\"\n\u0005\rk\"A\u0003*qGRKW.Z8vi\"1Q\t\u0001Q\u0001\n\u0005\u000b\u0001\u0002^5nK>,H\u000f\t\u0005\u0006\u000f\u0002!\t\u0001S\u0001\u000fe\u0016lwN^3Fq\u0016\u001cW\u000f^8s)\t!\u0013\nC\u0003K\r\u0002\u00071*\u0001\u0004fq\u0016\u001c\u0017\n\u001a\t\u0003\u0019>s!\u0001D'\n\u00059k\u0011A\u0002)sK\u0012,g-\u0003\u0002Q#\n11\u000b\u001e:j]\u001eT!AT\u0007\t\u000bM\u0003A\u0011\u0001+\u0002'I,Wn\u001c<f\u000bb,7-\u001e;pe\u0006\u001b\u0018P\\2\u0015\u0005\u0011*\u0006\"\u0002&S\u0001\u0004Y\u0005\"B,\u0001\t\u0003A\u0016\u0001\u0006:fO&\u001cH/\u001a:CY>\u001c7.T1oC\u001e,'\u000fF\u0003Z9z\u001bW\r\u0005\u0002;5&\u00111L\u0001\u0002\u000f\u00052|7m['b]\u0006<WM]%e\u0011\u0015if\u000b1\u0001Z\u00039\u0011Gn\\2l\u001b\u0006t\u0017mZ3s\u0013\u0012DQa\u0018,A\u0002\u0001\f\u0001#\\1y\u001f:DU-\u00199NK6\u001c\u0016N_3\u0011\u00051\t\u0017B\u00012\u000e\u0005\u0011auN\\4\t\u000b\u00114\u0006\u0019\u00011\u0002#5\f\u0007p\u00144g\u0011\u0016\f\u0007/T3n'&TX\rC\u0003g-\u0002\u00071$A\u0007tY\u00064X-\u00128ea>Lg\u000e\u001e\u0005\u0006Q\u0002!\t![\u0001\u0010kB$\u0017\r^3CY>\u001c7.\u00138g_R11G[6qk^DQ!X4A\u0002eCQ\u0001\\4A\u00025\fqA\u00197pG.LE\r\u0005\u0002;]&\u0011qN\u0001\u0002\b\u00052|7m[%e\u0011\u0015\tx\r1\u0001s\u00031\u0019Ho\u001c:bO\u0016dUM^3m!\tQ4/\u0003\u0002u\u0005\ta1\u000b^8sC\u001e,G*\u001a<fY\")ao\u001aa\u0001A\u00069Q.Z7TSj,\u0007\"\u0002=h\u0001\u0004\u0001\u0017\u0001\u00033jg.\u001c\u0016N_3\t\u000bi\u0004A\u0011A>\u0002\u0019\u001d,G\u000fT8dCRLwN\\:\u0015\u0007q\f\t\u0002\u0005\u0003~\u0003\u0017Ifb\u0001@\u0002\b9\u0019q0!\u0002\u000e\u0005\u0005\u0005!bAA\u00023\u00051AH]8pizJ\u0011AD\u0005\u0004\u0003\u0013i\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003\u001b\tyAA\u0002TKFT1!!\u0003\u000e\u0011\u0015a\u0017\u00101\u0001n\u0011\u001d\t)\u0002\u0001C\u0001\u0003/\tQcZ3u\u0019>\u001c\u0017\r^5p]N\fe\u000eZ*uCR,8\u000f\u0006\u0003\u0002\u001a\u0005u\u0002#\u0002\u0007\u0002\u001c\u0005}\u0011bAA\u000f\u001b\t1q\n\u001d;j_:\u0004B!!\t\u000289!\u00111EA\u001a\u001d\u0011\t)#!\r\u000f\t\u0005\u001d\u0012q\u0006\b\u0005\u0003S\tiCD\u0002\u0000\u0003WI\u0011!C\u0005\u0003\u000f!I!!\u0002\u0004\n\u0005\r!\u0011bAA\u001b\u0005\u0005!\"\t\\8dW6\u000bg.Y4fe6+7o]1hKNLA!!\u000f\u0002<\t9\"\t\\8dW2{7-\u0019;j_:\u001c\u0018I\u001c3Ti\u0006$Xo\u001d\u0006\u0004\u0003k\u0011\u0001B\u00027\u0002\u0014\u0001\u0007Q\u000e\u0003\u0004{\u0001\u0011\u0005\u0011\u0011\t\u000b\u0005\u0003\u0007\nI\u0005\u0005\u0003~\u0003\u000bb\u0018\u0002BA$\u0003\u001f\u0011!\"\u00138eKb,GmU3r\u0011!\tY%a\u0010A\u0002\u00055\u0013\u0001\u00032m_\u000e\\\u0017\nZ:\u0011\t1\ty%\\\u0005\u0004\u0003#j!!B!se\u0006L\bbBA+\u0001\u0011\u0005\u0011qK\u0001\tG>tG/Y5ogR\u00191'!\u0017\t\r1\f\u0019\u00061\u0001n\u0011\u001d\ti\u0006\u0001C\u0001\u0003?\n\u0001bZ3u!\u0016,'o\u001d\u000b\u0004y\u0006\u0005\u0004BB/\u0002\\\u0001\u0007\u0011\fC\u0004\u0002f\u0001!\t!a\u001a\u0002-\u001d,G/\u0012=fGV$xN]#oIB|\u0017N\u001c;SK\u001a$B!!\u001b\u0002lA!A\"a\u0007\u001c\u0011\u001d\ti'a\u0019A\u0002-\u000b!\"\u001a=fGV$xN]%e\u0011\u001d\t\t\b\u0001C\u0001\u0003g\n1B]3n_Z,'\t\\8dWR\u0019A%!\u001e\t\r1\fy\u00071\u0001n\u0011\u001d\tI\b\u0001C\u0001\u0003w\n\u0011B]3n_Z,'\u000b\u001a3\u0015\u000b\u0011\ni(a\"\t\u0011\u0005}\u0014q\u000fa\u0001\u0003\u0003\u000bQA\u001d3e\u0013\u0012\u00042\u0001DAB\u0013\r\t))\u0004\u0002\u0004\u0013:$\bbBAE\u0003o\u0002\raM\u0001\tE2|7m[5oO\"9\u0011Q\u0012\u0001\u0005\u0002\u0005=\u0015!\u0004:f[>4Xm\u00155vM\u001adW\rF\u0003%\u0003#\u000b)\n\u0003\u0005\u0002\u0014\u0006-\u0005\u0019AAA\u0003%\u0019\b.\u001e4gY\u0016LE\rC\u0004\u0002\n\u0006-\u0005\u0019A\u001a\t\u000f\u0005e\u0005\u0001\"\u0001\u0002\u001c\u0006y!/Z7pm\u0016\u0014%o\\1eG\u0006\u001cH\u000fF\u0004%\u0003;\u000b\t+!*\t\u000f\u0005}\u0015q\u0013a\u0001A\u0006Y!M]8bI\u000e\f7\u000f^%e\u0011\u001d\t\u0019+a&A\u0002M\n\u0001C]3n_Z,gI]8n\u001b\u0006\u001cH/\u001a:\t\u000f\u0005%\u0015q\u0013a\u0001g!9\u0011\u0011\u0016\u0001\u0005\u0002\u0005-\u0016aD4fi6+Wn\u001c:z'R\fG/^:\u0016\u0005\u00055\u0006C\u0002'\u00020f\u000b\u0019,C\u0002\u00022F\u00131!T1q!\u0015a\u0011Q\u00171a\u0013\r\t9,\u0004\u0002\u0007)V\u0004H.\u001a\u001a\t\u000f\u0005m\u0006\u0001\"\u0001\u0002>\u0006\u0001r-\u001a;Ti>\u0014\u0018mZ3Ti\u0006$Xo]\u000b\u0003\u0003\u007f\u0003R\u0001DA(\u0003\u0003\u00042AOAb\u0013\r\t)M\u0001\u0002\u000e'R|'/Y4f'R\fG/^:\t\u000f\u0005%\u0007\u0001\"\u0001\u0002L\u0006qq-\u001a;CY>\u001c7n\u0015;biV\u001cHCBAg\u0003+\f9\u000e\u0005\u0004M\u0003_K\u0016q\u001a\t\u0004u\u0005E\u0017bAAj\u0005\tY!\t\\8dWN#\u0018\r^;t\u0011\u0019a\u0017q\u0019a\u0001[\"I\u0011\u0011\\Ad!\u0003\u0005\raM\u0001\nCN\\7\u000b\\1wKNDq!!8\u0001\t\u0003\ty.A\nhKRl\u0015\r^2iS:<'\t\\8dW&#7\u000f\u0006\u0004\u0002b\u0006\r\u0018Q\u001e\t\u0005{\u0006-Q\u000e\u0003\u0005\u0002f\u0006m\u0007\u0019AAt\u0003\u00191\u0017\u000e\u001c;feB)A\"!;ng%\u0019\u00111^\u0007\u0003\u0013\u0019+hn\u0019;j_:\f\u0004bBAm\u00037\u0004\ra\r\u0005\b\u0003c\u0004A\u0011AAz\u0003=A\u0017m]\"bG\",GM\u00117pG.\u001cHcA\u001a\u0002v\"9\u0011QNAx\u0001\u0004Y\u0005bBA}\u0001\u0011\u0005\u00111`\u0001\u0005gR|\u0007\u000fF\u0001%\u0011\u001d\ty\u0010\u0001C\u0005\u0005\u0003\tA\u0001^3mYR\u0019AEa\u0001\t\u0011\t\u0015\u0011Q a\u0001\u0005\u000f\tq!\\3tg\u0006<W\rE\u0002\r\u0005\u0013I1Aa\u0003\u000e\u0005\r\te.\u001f\u0005\n\u0005\u001f\u0001\u0011\u0013!C\u0001\u0005#\t\u0001dZ3u\u00052|7m[*uCR,8\u000f\n3fM\u0006,H\u000e\u001e\u00133+\t\u0011\u0019BK\u00024\u0005+Y#Aa\u0006\u0011\t\te!1E\u0007\u0003\u00057QAA!\b\u0003 \u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0005Ci\u0011AC1o]>$\u0018\r^5p]&!!Q\u0005B\u000e\u0005E)hn\u00195fG.,GMV1sS\u0006t7-Z\u0004\t\u0005S\u0011\u0001\u0012\u0001\u0003\u0003,\u0005\u0011\"\t\\8dW6\u000bg.Y4fe6\u000b7\u000f^3s!\rQ$Q\u0006\u0004\b\u0003\tA\t\u0001\u0002B\u0018'\r\u0011ic\u0003\u0005\bo\t5B\u0011\u0001B\u001a)\t\u0011Y\u0003\u0003\u0006\u00038\t5\"\u0019!C\u0001\u0005s\tA\u0003\u0012*J-\u0016\u0013v,\u0012(E!>Ke\nV0O\u00036+UC\u0001B\u001e!\u0011\u0011iDa\u0012\u000e\u0005\t}\"\u0002\u0002B!\u0005\u0007\nA\u0001\\1oO*\u0011!QI\u0001\u0005U\u00064\u0018-C\u0002Q\u0005\u007fA\u0011Ba\u0013\u0003.\u0001\u0006IAa\u000f\u0002+\u0011\u0013\u0016JV#S?\u0016sE\tU(J\u001dR{f*Q'FA\u0001"
)
public class BlockManagerMaster implements Logging {
   private RpcEndpointRef driverEndpoint;
   private final boolean isDriver;
   private final RpcTimeout timeout;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String DRIVER_ENDPOINT_NAME() {
      return BlockManagerMaster$.MODULE$.DRIVER_ENDPOINT_NAME();
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

   public RpcEndpointRef driverEndpoint() {
      return this.driverEndpoint;
   }

   public void driverEndpoint_$eq(RpcEndpointRef x$1) {
      this.driverEndpoint = x$1;
   }

   public RpcTimeout timeout() {
      return this.timeout;
   }

   public void removeExecutor(String execId) {
      this.tell(new BlockManagerMessages.RemoveExecutor(execId));
      this.logInfo(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Removed ").append((Object)this.execId$1).append((Object)" successfully in removeExecutor").toString();
         }

         public {
            this.execId$1 = execId$1;
         }
      });
   }

   public void removeExecutorAsync(String execId) {
      this.driverEndpoint().ask(new BlockManagerMessages.RemoveExecutor(execId), ClassTag$.MODULE$.Boolean());
      this.logInfo(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Removal of executor ").append((Object)this.execId$2).append((Object)" requested").toString();
         }

         public {
            this.execId$2 = execId$2;
         }
      });
   }

   public BlockManagerId registerBlockManager(BlockManagerId blockManagerId, long maxOnHeapMemSize, long maxOffHeapMemSize, RpcEndpointRef slaveEndpoint) {
      this.logInfo(new Serializable(this, blockManagerId) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerId blockManagerId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Registering BlockManager ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockManagerId$1}));
         }

         public {
            this.blockManagerId$1 = blockManagerId$1;
         }
      });
      BlockManagerId updatedId = (BlockManagerId)this.driverEndpoint().askSync(new BlockManagerMessages.RegisterBlockManager(blockManagerId, maxOnHeapMemSize, maxOffHeapMemSize, slaveEndpoint), ClassTag$.MODULE$.apply(BlockManagerId.class));
      this.logInfo(new Serializable(this, updatedId) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerId updatedId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Registered BlockManager ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.updatedId$1}));
         }

         public {
            this.updatedId$1 = updatedId$1;
         }
      });
      return updatedId;
   }

   public boolean updateBlockInfo(BlockManagerId blockManagerId, BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      boolean res = BoxesRunTime.unboxToBoolean(this.driverEndpoint().askSync(new BlockManagerMessages.UpdateBlockInfo(blockManagerId, blockId, storageLevel, memSize, diskSize), ClassTag$.MODULE$.Boolean()));
      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Updated info of block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1}));
         }

         public {
            this.blockId$1 = blockId$1;
         }
      });
      return res;
   }

   public Seq<BlockManagerId> getLocations(BlockId blockId) {
      return (Seq)this.driverEndpoint().askSync(new BlockManagerMessages.GetLocations(blockId), ClassTag$.MODULE$.apply(Seq.class));
   }

   public Option<BlockManagerMessages.BlockLocationsAndStatus> getLocationsAndStatus(BlockId blockId) {
      return (Option)this.driverEndpoint().askSync(new BlockManagerMessages.GetLocationsAndStatus(blockId), ClassTag$.MODULE$.apply(Option.class));
   }

   public IndexedSeq<Seq<BlockManagerId>> getLocations(BlockId[] blockIds) {
      return (IndexedSeq)this.driverEndpoint().askSync(new BlockManagerMessages.GetLocationsMultipleBlockIds(blockIds), ClassTag$.MODULE$.apply(IndexedSeq.class));
   }

   public boolean contains(BlockId blockId) {
      return !this.getLocations(blockId).isEmpty();
   }

   public Seq<BlockManagerId> getPeers(BlockManagerId blockManagerId) {
      return (Seq)this.driverEndpoint().askSync(new BlockManagerMessages.GetPeers(blockManagerId), ClassTag$.MODULE$.apply(Seq.class));
   }

   public Option<RpcEndpointRef> getExecutorEndpointRef(String executorId) {
      return (Option)this.driverEndpoint().askSync(new BlockManagerMessages.GetExecutorEndpointRef(executorId), ClassTag$.MODULE$.apply(Option.class));
   }

   public void removeBlock(BlockId blockId) {
      this.driverEndpoint().askSync(new BlockManagerMessages.RemoveBlock(blockId), ClassTag$.MODULE$.Boolean());
   }

   public void removeRdd(int rddId, boolean blocking) {
      Future future = (Future)this.driverEndpoint().askSync(new BlockManagerMessages.RemoveRdd(rddId), ClassTag$.MODULE$.apply(Future.class));
      future.failed().foreach(new Serializable(this, rddId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMaster $outer;
         public final int rddId$1;

         public final void apply(Throwable e) {
            this.$outer.logWarning(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Throwable e$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to remove RDD ", " - ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.rddId$1), this.e$1.getMessage()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.e$1 = e$1;
                  }
               }
            }, e);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rddId$1 = rddId$1;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread());
      if (blocking) {
         this.timeout().awaitResult(future);
      }

   }

   public void removeShuffle(int shuffleId, boolean blocking) {
      Future future = (Future)this.driverEndpoint().askSync(new BlockManagerMessages.RemoveShuffle(shuffleId), ClassTag$.MODULE$.apply(Future.class));
      future.failed().foreach(new Serializable(this, shuffleId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMaster $outer;
         public final int shuffleId$1;

         public final void apply(Throwable e) {
            this.$outer.logWarning(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Throwable e$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to remove shuffle ", " - ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.shuffleId$1), this.e$2.getMessage()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.e$2 = e$2;
                  }
               }
            }, e);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.shuffleId$1 = shuffleId$1;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread());
      if (blocking) {
         this.timeout().awaitResult(future);
      }

   }

   public void removeBroadcast(long broadcastId, boolean removeFromMaster, boolean blocking) {
      Future future = (Future)this.driverEndpoint().askSync(new BlockManagerMessages.RemoveBroadcast(broadcastId, removeFromMaster), ClassTag$.MODULE$.apply(Future.class));
      future.failed().foreach(new Serializable(this, broadcastId, removeFromMaster) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMaster $outer;
         public final long broadcastId$1;
         public final boolean removeFromMaster$1;

         public final void apply(Throwable e) {
            this.$outer.logWarning(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Throwable e$3;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to remove broadcast ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.broadcastId$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" with removeFromMaster = ", " - ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(this.$outer.removeFromMaster$1), this.e$3.getMessage()}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.e$3 = e$3;
                  }
               }
            }, e);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.broadcastId$1 = broadcastId$1;
               this.removeFromMaster$1 = removeFromMaster$1;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread());
      if (blocking) {
         this.timeout().awaitResult(future);
      }

   }

   public Map<BlockManagerId, Tuple2<Object, Object>> getMemoryStatus() {
      return (Map)this.driverEndpoint().askSync(BlockManagerMessages.GetMemoryStatus$.MODULE$, ClassTag$.MODULE$.apply(Map.class));
   }

   public StorageStatus[] getStorageStatus() {
      return (StorageStatus[])this.driverEndpoint().askSync(BlockManagerMessages.GetStorageStatus$.MODULE$, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(StorageStatus.class)));
   }

   public Map<BlockManagerId, BlockStatus> getBlockStatus(BlockId blockId, boolean askSlaves) {
      BlockManagerMessages.GetBlockStatus msg = new BlockManagerMessages.GetBlockStatus(blockId, askSlaves);
      Map response = (Map)this.driverEndpoint().askSync(msg, ClassTag$.MODULE$.apply(Map.class));
      Tuple2 var6 = response.unzip(Predef$.MODULE$.$conforms());
      if (var6 != null) {
         Iterable blockManagerIds = (Iterable)var6._1();
         Iterable futures = (Iterable)var6._2();
         Tuple2 var9 = new Tuple2(blockManagerIds, futures);
         Iterable blockManagerIds = (Iterable)var9._1();
         Iterable futures = (Iterable)var9._2();
         ExecutionContextExecutor sameThread = ThreadUtils$.MODULE$.sameThread();
         CanBuildFrom cbf = (CanBuildFrom)Predef$.MODULE$.implicitly(Iterable$.MODULE$.canBuildFrom());
         scala.collection.Iterable blockStatus = (scala.collection.Iterable)this.timeout().awaitResult(Future$.MODULE$.sequence(futures, cbf, ThreadUtils$.MODULE$.sameThread()));
         if (blockStatus == null) {
            throw new SparkException((new StringBuilder()).append((Object)"BlockManager returned null for BlockStatus query: ").append((Object)blockId).toString());
         } else {
            return ((TraversableOnce)((TraversableLike)blockManagerIds.zip(blockStatus, scala.collection.immutable.Iterable$.MODULE$.canBuildFrom())).flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final scala.collection.Iterable<Tuple2<BlockManagerId, BlockStatus>> apply(Tuple2<BlockManagerId, Option<BlockStatus>> x0$1) {
                  if (x0$1 != null) {
                     BlockManagerId blockManagerId = (BlockManagerId)x0$1._1();
                     Option status = (Option)x0$1._2();
                     scala.collection.Iterable var5 = Option$.MODULE$.option2Iterable(status.map(new Serializable(this, blockManagerId) {
                        public static final long serialVersionUID = 0L;
                        private final BlockManagerId blockManagerId$2;

                        public final Tuple2<BlockManagerId, BlockStatus> apply(BlockStatus s) {
                           return new Tuple2<BlockManagerId, BlockStatus>(this.blockManagerId$2, s);
                        }

                        public {
                           this.blockManagerId$2 = blockManagerId$2;
                        }
                     }));
                     return var5;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }
            }, scala.collection.immutable.Iterable$.MODULE$.canBuildFrom())).<BlockManagerId, BlockStatus>toMap(Predef$.MODULE$.$conforms());
         }
      } else {
         throw new MatchError(var6);
      }
   }

   public boolean getBlockStatus$default$2() {
      return true;
   }

   public Seq<BlockId> getMatchingBlockIds(Function1<BlockId, Object> filter, boolean askSlaves) {
      BlockManagerMessages.GetMatchingBlockIds msg = new BlockManagerMessages.GetMatchingBlockIds(filter, askSlaves);
      Future future = (Future)this.driverEndpoint().askSync(msg, ClassTag$.MODULE$.apply(Future.class));
      return (Seq)this.timeout().awaitResult(future);
   }

   public boolean hasCachedBlocks(String executorId) {
      return BoxesRunTime.unboxToBoolean(this.driverEndpoint().askSync(new BlockManagerMessages.HasCachedBlocks(executorId), ClassTag$.MODULE$.Boolean()));
   }

   public void stop() {
      if (this.driverEndpoint() != null && this.isDriver) {
         this.tell(BlockManagerMessages.StopBlockManagerMaster$.MODULE$);
         this.driverEndpoint_$eq((RpcEndpointRef)null);
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "BlockManagerMaster stopped";
            }
         });
      }

   }

   private void tell(Object message) {
      if (!BoxesRunTime.unboxToBoolean(this.driverEndpoint().askSync(message, ClassTag$.MODULE$.Boolean()))) {
         throw new SparkException("BlockManagerMasterEndpoint returned false, expected true.");
      }
   }

   public BlockManagerMaster(RpcEndpointRef driverEndpoint, SparkConf conf, boolean isDriver) {
      this.driverEndpoint = driverEndpoint;
      this.isDriver = isDriver;
      super();
      Logging$class.$init$(this);
      this.timeout = RpcUtils$.MODULE$.askRpcTimeout(conf);
   }
}
