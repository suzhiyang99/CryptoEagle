package org.apache.spark.network.netty;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.BlockDataManager;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.buffer.NioManagedBuffer;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.network.server.OneForOneStreamManager;
import org.apache.spark.network.server.RpcHandler;
import org.apache.spark.network.server.StreamManager;
import org.apache.spark.network.shuffle.protocol.BlockTransferMessage;
import org.apache.spark.network.shuffle.protocol.OpenBlocks;
import org.apache.spark.network.shuffle.protocol.StreamHandle;
import org.apache.spark.network.shuffle.protocol.UploadBlock;
import org.apache.spark.network.shuffle.protocol.BlockTransferMessage.Decoder;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockId$;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.SeqView;
import scala.collection.SeqView$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154A!\u0001\u0002\u0001\u001b\t\u0019b*\u001a;us\ncwnY6Sa\u000e\u001cVM\u001d<fe*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\tqA\\3uo>\u00148N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\b\u0015!\ty!#D\u0001\u0011\u0015\t\tB!\u0001\u0004tKJ4XM]\u0005\u0003'A\u0011!B\u00159d\u0011\u0006tG\r\\3s!\t)\u0002$D\u0001\u0017\u0015\t9b!\u0001\u0005j]R,'O\\1m\u0013\tIbCA\u0004M_\u001e<\u0017N\\4\t\u0011m\u0001!\u0011!Q\u0001\nq\tQ!\u00199q\u0013\u0012\u0004\"!H\u0012\u000f\u0005y\tS\"A\u0010\u000b\u0003\u0001\nQa]2bY\u0006L!AI\u0010\u0002\rA\u0013X\rZ3g\u0013\t!SE\u0001\u0004TiJLgn\u001a\u0006\u0003E}A\u0001b\n\u0001\u0003\u0002\u0003\u0006I\u0001K\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014\bCA\u0015,\u001b\u0005Q#BA\u0014\u0007\u0013\ta#F\u0001\u0006TKJL\u0017\r\\5{KJD\u0001B\f\u0001\u0003\u0002\u0003\u0006IaL\u0001\rE2|7m['b]\u0006<WM\u001d\t\u0003aEj\u0011\u0001B\u0005\u0003e\u0011\u0011\u0001C\u00117pG.$\u0015\r^1NC:\fw-\u001a:\t\u000bQ\u0002A\u0011A\u001b\u0002\rqJg.\u001b;?)\u00111\u0004(\u000f\u001e\u0011\u0005]\u0002Q\"\u0001\u0002\t\u000bm\u0019\u0004\u0019\u0001\u000f\t\u000b\u001d\u001a\u0004\u0019\u0001\u0015\t\u000b9\u001a\u0004\u0019A\u0018\t\u000fq\u0002!\u0019!C\u0005{\u0005i1\u000f\u001e:fC6l\u0015M\\1hKJ,\u0012A\u0010\t\u0003\u001f}J!\u0001\u0011\t\u0003-=sWMR8s\u001f:,7\u000b\u001e:fC6l\u0015M\\1hKJDaA\u0011\u0001!\u0002\u0013q\u0014AD:ue\u0016\fW.T1oC\u001e,'\u000f\t\u0005\u0006\t\u0002!\t%R\u0001\be\u0016\u001cW-\u001b<f)\u00111\u0015\n\u0015.\u0011\u0005y9\u0015B\u0001% \u0005\u0011)f.\u001b;\t\u000b)\u001b\u0005\u0019A&\u0002\r\rd\u0017.\u001a8u!\tae*D\u0001N\u0015\tQE!\u0003\u0002P\u001b\nyAK]1ogB|'\u000f^\"mS\u0016tG\u000fC\u0003R\u0007\u0002\u0007!+\u0001\u0006sa\u000elUm]:bO\u0016\u0004\"a\u0015-\u000e\u0003QS!!\u0016,\u0002\u00079LwNC\u0001X\u0003\u0011Q\u0017M^1\n\u0005e#&A\u0003\"zi\u0016\u0014UO\u001a4fe\")1l\u0011a\u00019\u0006y!/Z:q_:\u001cXmQ8oi\u0016DH\u000f\u0005\u0002M;&\u0011a,\u0014\u0002\u0014%B\u001c'+Z:q_:\u001cXmQ1mY\n\f7m\u001b\u0005\u0006A\u0002!\t%Y\u0001\u0011O\u0016$8\u000b\u001e:fC6l\u0015M\\1hKJ$\u0012A\u0019\t\u0003\u001f\rL!\u0001\u001a\t\u0003\u001bM#(/Z1n\u001b\u0006t\u0017mZ3s\u0001"
)
public class NettyBlockRpcServer extends RpcHandler implements Logging {
   private final String appId;
   private final Serializer serializer;
   public final BlockDataManager org$apache$spark$network$netty$NettyBlockRpcServer$$blockManager;
   private final OneForOneStreamManager streamManager;
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

   private OneForOneStreamManager streamManager() {
      return this.streamManager;
   }

   public void receive(TransportClient client, ByteBuffer rpcMessage, RpcResponseCallback responseContext) {
      BlockTransferMessage message = Decoder.fromByteBuffer(rpcMessage);
      this.logTrace(new Serializable(this, message) {
         public static final long serialVersionUID = 0L;
         private final BlockTransferMessage message$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received request: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.message$1}));
         }

         public {
            this.message$1 = message$1;
         }
      });
      if (message instanceof OpenBlocks) {
         OpenBlocks var6 = (OpenBlocks)message;
         int blocksNum = var6.blockIds.length;
         SeqView blocks = (SeqView)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), blocksNum).view().map(new Serializable(this, var6) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final NettyBlockRpcServer $outer;
            private final OpenBlocks x2$1;

            public final ManagedBuffer apply(int i) {
               return this.$outer.org$apache$spark$network$netty$NettyBlockRpcServer$$blockManager.getBlockData(BlockId$.MODULE$.apply(this.x2$1.blockIds[i]));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.x2$1 = x2$1;
               }
            }
         }, SeqView$.MODULE$.canBuildFrom());
         long streamId = this.streamManager().registerStream(this.appId, (Iterator)JavaConverters$.MODULE$.asJavaIteratorConverter(blocks.iterator()).asJava());
         this.logTrace(new Serializable(this, blocksNum, streamId) {
            public static final long serialVersionUID = 0L;
            private final int blocksNum$1;
            private final long streamId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Registered streamId ", " with ", " buffers"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.streamId$1), BoxesRunTime.boxToInteger(this.blocksNum$1)}));
            }

            public {
               this.blocksNum$1 = blocksNum$1;
               this.streamId$1 = streamId$1;
            }
         });
         responseContext.onSuccess((new StreamHandle(streamId, blocksNum)).toByteBuffer());
         BoxedUnit var24 = BoxedUnit.UNIT;
      } else if (!(message instanceof UploadBlock)) {
         throw new MatchError(message);
      } else {
         UploadBlock var12 = (UploadBlock)message;
         Tuple2 var14 = (Tuple2)this.serializer.newInstance().deserialize(ByteBuffer.wrap(var12.metadata), ClassTag$.MODULE$.Nothing());
         if (var14 != null) {
            StorageLevel level = (StorageLevel)var14._1();
            ClassTag classTag = (ClassTag)var14._2();
            if (level != null && classTag instanceof ClassTag) {
               Tuple2 var19 = new Tuple2(level, classTag);
               StorageLevel level = (StorageLevel)var19._1();
               ClassTag classTag = (ClassTag)var19._2();
               NioManagedBuffer data = new NioManagedBuffer(ByteBuffer.wrap(var12.blockData));
               BlockId blockId = BlockId$.MODULE$.apply(var12.blockId);
               this.org$apache$spark$network$netty$NettyBlockRpcServer$$blockManager.putBlockData(blockId, data, level, classTag);
               responseContext.onSuccess(ByteBuffer.allocate(0));
               BoxedUnit var7 = BoxedUnit.UNIT;
               return;
            }
         }

         throw new MatchError(var14);
      }
   }

   public StreamManager getStreamManager() {
      return this.streamManager();
   }

   public NettyBlockRpcServer(String appId, Serializer serializer, BlockDataManager blockManager) {
      this.appId = appId;
      this.serializer = serializer;
      this.org$apache$spark$network$netty$NettyBlockRpcServer$$blockManager = blockManager;
      Logging$class.$init$(this);
      this.streamManager = new OneForOneStreamManager();
   }
}
