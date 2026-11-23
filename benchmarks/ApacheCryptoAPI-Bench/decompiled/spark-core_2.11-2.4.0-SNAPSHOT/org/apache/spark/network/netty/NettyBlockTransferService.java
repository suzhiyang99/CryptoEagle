package org.apache.spark.network.netty;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.network.BlockDataManager;
import org.apache.spark.network.BlockTransferService;
import org.apache.spark.network.TransportContext;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.network.client.TransportClientFactory;
import org.apache.spark.network.crypto.AuthClientBootstrap;
import org.apache.spark.network.crypto.AuthServerBootstrap;
import org.apache.spark.network.server.TransportServer;
import org.apache.spark.network.server.TransportServerBootstrap;
import org.apache.spark.network.shuffle.BlockFetchingListener;
import org.apache.spark.network.shuffle.OneForOneBlockFetcher;
import org.apache.spark.network.shuffle.RetryingBlockFetcher;
import org.apache.spark.network.shuffle.TempFileManager;
import org.apache.spark.network.shuffle.protocol.UploadBlock;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.network.util.TransportConf;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-h!B\u0001\u0003\u0001\u0019a!!\u0007(fiRL(\t\\8dWR\u0013\u0018M\\:gKJ\u001cVM\u001d<jG\u0016T!a\u0001\u0003\u0002\u000b9,G\u000f^=\u000b\u0005\u00151\u0011a\u00028fi^|'o\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011\u0001B\u0005\u0003!\u0011\u0011AC\u00117pG.$&/\u00198tM\u0016\u00148+\u001a:wS\u000e,\u0007\u0002\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0002\t\r|gNZ\u0002\u0001!\t)b#D\u0001\u0007\u0013\t9bAA\u0005Ta\u0006\u00148nQ8oM\"A\u0011\u0004\u0001B\u0001B\u0003%!$A\btK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s!\t)2$\u0003\u0002\u001d\r\ty1+Z2ve&$\u00180T1oC\u001e,'\u000f\u0003\u0005\u001f\u0001\t\u0005\t\u0015!\u0003 \u0003-\u0011\u0017N\u001c3BI\u0012\u0014Xm]:\u0011\u0005\u00012cBA\u0011%\u001b\u0005\u0011#\"A\u0012\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u0015\u0012\u0013A\u0002)sK\u0012,g-\u0003\u0002(Q\t11\u000b\u001e:j]\u001eT!!\n\u0012\t\u0011)\u0002!Q1A\u0005B-\n\u0001\u0002[8ti:\u000bW.Z\u000b\u0002?!AQ\u0006\u0001B\u0001B\u0003%q$A\u0005i_N$h*Y7fA!Aq\u0006\u0001B\u0001B\u0003%\u0001'A\u0003`a>\u0014H\u000f\u0005\u0002\"c%\u0011!G\t\u0002\u0004\u0013:$\b\u0002\u0003\u001b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u00119,XnQ8sKNDQA\u000e\u0001\u0005\u0002]\na\u0001P5oSRtDc\u0002\u001d;wqjdh\u0010\t\u0003s\u0001i\u0011A\u0001\u0005\u0006%U\u0002\r\u0001\u0006\u0005\u00063U\u0002\rA\u0007\u0005\u0006=U\u0002\ra\b\u0005\u0006UU\u0002\ra\b\u0005\u0006_U\u0002\r\u0001\r\u0005\u0006iU\u0002\r\u0001\r\u0005\b\u0003\u0002\u0011\r\u0011\"\u0003C\u0003)\u0019XM]5bY&TXM]\u000b\u0002\u0007B\u0011AIR\u0007\u0002\u000b*\u0011\u0011IB\u0005\u0003\u000f\u0016\u0013aBS1wCN+'/[1mSj,'\u000f\u0003\u0004J\u0001\u0001\u0006IaQ\u0001\fg\u0016\u0014\u0018.\u00197ju\u0016\u0014\b\u0005C\u0004L\u0001\t\u0007I\u0011\u0002'\u0002\u0017\u0005,H\u000f[#oC\ndW\rZ\u000b\u0002\u001bB\u0011\u0011ET\u0005\u0003\u001f\n\u0012qAQ8pY\u0016\fg\u000e\u0003\u0004R\u0001\u0001\u0006I!T\u0001\rCV$\b.\u00128bE2,G\r\t\u0005\b'\u0002\u0011\r\u0011\"\u0003U\u00035!(/\u00198ta>\u0014HoQ8oMV\tQ\u000b\u0005\u0002W36\tqK\u0003\u0002Y\t\u0005!Q\u000f^5m\u0013\tQvKA\u0007Ue\u0006t7\u000f]8si\u000e{gN\u001a\u0005\u00079\u0002\u0001\u000b\u0011B+\u0002\u001dQ\u0014\u0018M\\:q_J$8i\u001c8gA!Ia\f\u0001a\u0001\u0002\u0003\u0006KaX\u0001\u0011iJ\fgn\u001d9peR\u001cuN\u001c;fqR\u0004\"A\u00041\n\u0005\u0005$!\u0001\u0005+sC:\u001c\bo\u001c:u\u0007>tG/\u001a=u\u0011%\u0019\u0007\u00011A\u0001B\u0003&A-\u0001\u0004tKJ4XM\u001d\t\u0003K\u001el\u0011A\u001a\u0006\u0003G\u0012I!\u0001\u001b4\u0003\u001fQ\u0013\u0018M\\:q_J$8+\u001a:wKJD\u0011B\u001b\u0001A\u0002\u0003\u0005\u000b\u0015B6\u0002\u001b\rd\u0017.\u001a8u\r\u0006\u001cGo\u001c:z!\taw.D\u0001n\u0015\tqG!\u0001\u0004dY&,g\u000e^\u0005\u0003a6\u0014a\u0003\u0016:b]N\u0004xN\u001d;DY&,g\u000e\u001e$bGR|'/\u001f\u0005\ne\u0002\u0001\r\u0011!Q!\n}\tQ!\u00199q\u0013\u0012DQ\u0001\u001e\u0001\u0005BU\fA!\u001b8jiR\u0011a/\u001f\t\u0003C]L!\u0001\u001f\u0012\u0003\tUs\u0017\u000e\u001e\u0005\u0006uN\u0004\ra_\u0001\u0011E2|7m\u001b#bi\u0006l\u0015M\\1hKJ\u0004\"A\u0004?\n\u0005u$!\u0001\u0005\"m_\u000e\\G)\u0019;b\u001b\u0006t\u0017mZ3s\u0011\u0019y\b\u0001\"\u0003\u0002\u0002\u0005a1M]3bi\u0016\u001cVM\u001d<feR\u0019A-a\u0001\t\u000f\u0005\u0015a\u00101\u0001\u0002\b\u0005Q!m\\8ugR\u0014\u0018\r]:\u0011\r\u0005%\u0011\u0011DA\u0010\u001d\u0011\tY!!\u0006\u000f\t\u00055\u00111C\u0007\u0003\u0003\u001fQ1!!\u0005\u0014\u0003\u0019a$o\\8u}%\t1%C\u0002\u0002\u0018\t\nq\u0001]1dW\u0006<W-\u0003\u0003\u0002\u001c\u0005u!\u0001\u0002'jgRT1!a\u0006#!\r)\u0017\u0011E\u0005\u0004\u0003G1'\u0001\u0007+sC:\u001c\bo\u001c:u'\u0016\u0014h/\u001a:C_>$8\u000f\u001e:ba\"9\u0011q\u0005\u0001\u0005B\u0005%\u0012AD:ik\u001a4G.Z'fiJL7m\u001d\u000b\u0003\u0003W\u0001B!!\f\u0002<5\u0011\u0011q\u0006\u0006\u0005\u0003c\t\u0019$A\u0004nKR\u0014\u0018nY:\u000b\t\u0005U\u0012qG\u0001\tG>$\u0017\r[1mK*\u0011\u0011\u0011H\u0001\u0004G>l\u0017\u0002BA\u001f\u0003_\u0011\u0011\"T3ue&\u001c7+\u001a;\t\u000f\u0005\u0005\u0003\u0001\"\u0011\u0002D\u0005Ya-\u001a;dQ\ncwnY6t)51\u0018QIA%\u0003\u001b\n\t&a\u0017\u0002l!9\u0011qIA \u0001\u0004y\u0012\u0001\u00025pgRDq!a\u0013\u0002@\u0001\u0007\u0001'\u0001\u0003q_J$\bbBA(\u0003\u007f\u0001\raH\u0001\u0007Kb,7-\u00133\t\u0011\u0005M\u0013q\ba\u0001\u0003+\n\u0001B\u00197pG.LEm\u001d\t\u0005C\u0005]s$C\u0002\u0002Z\t\u0012Q!\u0011:sCfD\u0001\"!\u0018\u0002@\u0001\u0007\u0011qL\u0001\tY&\u001cH/\u001a8feB!\u0011\u0011MA4\u001b\t\t\u0019GC\u0002\u0002f\u0011\tqa\u001d5vM\u001adW-\u0003\u0003\u0002j\u0005\r$!\u0006\"m_\u000e\\g)\u001a;dQ&tw\rT5ti\u0016tWM\u001d\u0005\t\u0003[\ny\u00041\u0001\u0002p\u0005yA/Z7q\r&dW-T1oC\u001e,'\u000f\u0005\u0003\u0002b\u0005E\u0014\u0002BA:\u0003G\u0012q\u0002V3na\u001aKG.Z'b]\u0006<WM\u001d\u0005\b\u0003\u0017\u0002A\u0011IA<+\u0005\u0001\u0004bBA>\u0001\u0011\u0005\u0013QP\u0001\fkBdw.\u00193CY>\u001c7\u000e\u0006\t\u0002\u0000\u0005-\u0015qRAI\u0003'\u000b\u0019+a-\u0002>B)\u0011\u0011QADm6\u0011\u00111\u0011\u0006\u0004\u0003\u000b\u0013\u0013AC2p]\u000e,(O]3oi&!\u0011\u0011RAB\u0005\u00191U\u000f^;sK\"9\u0011QRA=\u0001\u0004y\u0012\u0001\u00035pgRt\u0017-\\3\t\u000f\u0005-\u0013\u0011\u0010a\u0001a!9\u0011qJA=\u0001\u0004y\u0002\u0002CAK\u0003s\u0002\r!a&\u0002\u000f\tdwnY6JIB!\u0011\u0011TAP\u001b\t\tYJC\u0002\u0002\u001e\u001a\tqa\u001d;pe\u0006<W-\u0003\u0003\u0002\"\u0006m%a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\t\u0003K\u000bI\b1\u0001\u0002(\u0006I!\r\\8dW\u0012\u000bG/\u0019\t\u0005\u0003S\u000by+\u0004\u0002\u0002,*\u0019\u0011Q\u0016\u0003\u0002\r\t,hMZ3s\u0013\u0011\t\t,a+\u0003\u001b5\u000bg.Y4fI\n+hMZ3s\u0011!\t),!\u001fA\u0002\u0005]\u0016!\u00027fm\u0016d\u0007\u0003BAM\u0003sKA!a/\u0002\u001c\na1\u000b^8sC\u001e,G*\u001a<fY\"A\u0011qXA=\u0001\u0004\t\t-\u0001\u0005dY\u0006\u001c8\u000fV1ha\u0011\t\u0019-a5\u0011\r\u0005\u0015\u00171ZAh\u001b\t\t9MC\u0002\u0002J\n\nqA]3gY\u0016\u001cG/\u0003\u0003\u0002N\u0006\u001d'\u0001C\"mCN\u001cH+Y4\u0011\t\u0005E\u00171\u001b\u0007\u0001\t1\t).!0\u0002\u0002\u0003\u0005)\u0011AAl\u0005\ryF%M\t\u0005\u00033\fy\u000eE\u0002\"\u00037L1!!8#\u0005\u001dqu\u000e\u001e5j]\u001e\u00042!IAq\u0013\r\t\u0019O\t\u0002\u0004\u0003:L\bbBAt\u0001\u0011\u0005\u0013\u0011^\u0001\u0006G2|7/\u001a\u000b\u0002m\u0002"
)
public class NettyBlockTransferService extends BlockTransferService {
   private final SparkConf conf;
   private final SecurityManager securityManager;
   private final String bindAddress;
   private final String hostName;
   private final int _port;
   private final JavaSerializer serializer;
   private final boolean authEnabled;
   private final TransportConf org$apache$spark$network$netty$NettyBlockTransferService$$transportConf;
   private TransportContext transportContext;
   public TransportServer org$apache$spark$network$netty$NettyBlockTransferService$$server;
   public TransportClientFactory org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory;
   public String org$apache$spark$network$netty$NettyBlockTransferService$$appId;

   public String hostName() {
      return this.hostName;
   }

   private JavaSerializer serializer() {
      return this.serializer;
   }

   private boolean authEnabled() {
      return this.authEnabled;
   }

   public TransportConf org$apache$spark$network$netty$NettyBlockTransferService$$transportConf() {
      return this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf;
   }

   public void init(BlockDataManager blockDataManager) {
      NettyBlockRpcServer rpcHandler = new NettyBlockRpcServer(this.conf.getAppId(), this.serializer(), blockDataManager);
      Option serverBootstrap = None$.MODULE$;
      Option clientBootstrap = None$.MODULE$;
      if (this.authEnabled()) {
         serverBootstrap = new Some(new AuthServerBootstrap(this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf(), this.securityManager));
         clientBootstrap = new Some(new AuthClientBootstrap(this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf(), this.conf.getAppId(), this.securityManager));
      }

      this.transportContext = new TransportContext(this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf(), rpcHandler);
      this.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory = this.transportContext.createClientFactory((List)JavaConverters$.MODULE$.seqAsJavaListConverter(Option$.MODULE$.option2Iterable(clientBootstrap).toSeq()).asJava());
      this.org$apache$spark$network$netty$NettyBlockTransferService$$server = this.createServer(serverBootstrap.toList());
      this.org$apache$spark$network$netty$NettyBlockTransferService$$appId = this.conf.getAppId();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NettyBlockTransferService $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Server created on ", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.hostName(), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$server.getPort())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   private TransportServer createServer(scala.collection.immutable.List<TransportServerBootstrap> bootstraps) {
      return (TransportServer)Utils$.MODULE$.startServiceOnPort(this._port, new Serializable(this, bootstraps) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NettyBlockTransferService $outer;
         private final scala.collection.immutable.List bootstraps$1;

         public final Tuple2<TransportServer, Object> apply(int port) {
            return this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$startService$1(port, this.bootstraps$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.bootstraps$1 = bootstraps$1;
            }
         }
      }, this.conf, this.getClass().getName())._1();
   }

   public MetricSet shuffleMetrics() {
      Predef$.MODULE$.require(this.org$apache$spark$network$netty$NettyBlockTransferService$$server != null && this.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "NettyBlockTransferServer is not initialized";
         }
      });
      return new MetricSet(this) {
         private final HashMap<String, Metric> allMetrics;
         // $FF: synthetic field
         private final NettyBlockTransferService $outer;

         private HashMap<String, Metric> allMetrics() {
            return this.allMetrics;
         }

         public Map<String, Metric> getMetrics() {
            this.allMetrics().putAll(this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory.getAllMetrics().getMetrics());
            this.allMetrics().putAll(this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$server.getAllMetrics().getMetrics());
            return this.allMetrics();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.allMetrics = new HashMap();
            }
         }
      };
   }

   public void fetchBlocks(String host, int port, String execId, String[] blockIds, BlockFetchingListener listener, TempFileManager tempFileManager) {
      this.logTrace(new Serializable(this, host, port, execId) {
         public static final long serialVersionUID = 0L;
         private final String host$1;
         private final int port$1;
         private final String execId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fetch blocks from ", ":", " (executor id ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1, BoxesRunTime.boxToInteger(this.port$1), this.execId$1}));
         }

         public {
            this.host$1 = host$1;
            this.port$1 = port$1;
            this.execId$1 = execId$1;
         }
      });

      try {
         RetryingBlockFetcher.BlockFetchStarter blockFetchStarter = new RetryingBlockFetcher.BlockFetchStarter(this, host, port, execId, tempFileManager) {
            // $FF: synthetic field
            private final NettyBlockTransferService $outer;
            private final String host$1;
            private final int port$1;
            private final String execId$1;
            private final TempFileManager tempFileManager$1;

            public void createAndStart(String[] blockIds, BlockFetchingListener listener) {
               TransportClient client = this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory.createClient(this.host$1, this.port$1);
               (new OneForOneBlockFetcher(client, this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$appId, this.execId$1, blockIds, listener, this.$outer.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf(), this.tempFileManager$1)).start();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.host$1 = host$1;
                  this.port$1 = port$1;
                  this.execId$1 = execId$1;
                  this.tempFileManager$1 = tempFileManager$1;
               }
            }
         };
         int maxRetries = this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf().maxIORetries();
         if (maxRetries > 0) {
            (new RetryingBlockFetcher(this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf(), blockFetchStarter, blockIds, listener)).start();
         } else {
            blockFetchStarter.createAndStart(blockIds, listener);
         }
      } catch (Exception var10) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception while beginning fetchBlocks";
            }
         }, var10);
         Predef$.MODULE$.refArrayOps((Object[])blockIds).foreach(new Serializable(this, listener, var10) {
            public static final long serialVersionUID = 0L;
            private final BlockFetchingListener listener$1;
            private final Exception e$1;

            public final void apply(String x$1) {
               this.listener$1.onBlockFetchFailure(x$1, this.e$1);
            }

            public {
               this.listener$1 = listener$1;
               this.e$1 = e$1;
            }
         });
      }

   }

   public int port() {
      return this.org$apache$spark$network$netty$NettyBlockTransferService$$server.getPort();
   }

   public Future<BoxedUnit> uploadBlock(String hostname, int port, String execId, BlockId blockId, ManagedBuffer blockData, StorageLevel level, ClassTag<?> classTag) {
      Promise result = Promise$.MODULE$.apply();
      TransportClient client = this.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory.createClient(hostname, port);
      byte[] metadata = JavaUtils.bufferToArray(this.serializer().newInstance().serialize(new Tuple2(level, classTag), ClassTag$.MODULE$.apply(Tuple2.class)));
      byte[] array = JavaUtils.bufferToArray(blockData.nioByteBuffer());
      client.sendRpc((new UploadBlock(this.org$apache$spark$network$netty$NettyBlockTransferService$$appId, execId, blockId.name(), metadata, array)).toByteBuffer(), new RpcResponseCallback(this, blockId, result) {
         // $FF: synthetic field
         private final NettyBlockTransferService $outer;
         public final BlockId blockId$1;
         private final Promise result$1;

         public void onSuccess(ByteBuffer response) {
            this.$outer.logTrace(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Successfully uploaded block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.result$1.success(BoxedUnit.UNIT);
         }

         public void onFailure(Throwable e) {
            this.$outer.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error while uploading block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, e);
            this.result$1.failure(e);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$1 = blockId$1;
               this.result$1 = result$1;
            }
         }
      });
      return result.future();
   }

   public void close() {
      if (this.org$apache$spark$network$netty$NettyBlockTransferService$$server != null) {
         this.org$apache$spark$network$netty$NettyBlockTransferService$$server.close();
      }

      if (this.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory != null) {
         this.org$apache$spark$network$netty$NettyBlockTransferService$$clientFactory.close();
      }

   }

   public final Tuple2 org$apache$spark$network$netty$NettyBlockTransferService$$startService$1(int port, scala.collection.immutable.List bootstraps$1) {
      TransportServer server = this.transportContext.createServer(this.bindAddress, port, (List)JavaConverters$.MODULE$.seqAsJavaListConverter(bootstraps$1).asJava());
      return new Tuple2(server, BoxesRunTime.boxToInteger(server.getPort()));
   }

   public NettyBlockTransferService(SparkConf conf, SecurityManager securityManager, String bindAddress, String hostName, int _port, int numCores) {
      this.conf = conf;
      this.securityManager = securityManager;
      this.bindAddress = bindAddress;
      this.hostName = hostName;
      this._port = _port;
      this.serializer = new JavaSerializer(conf);
      this.authEnabled = securityManager.isAuthenticationEnabled();
      this.org$apache$spark$network$netty$NettyBlockTransferService$$transportConf = SparkTransportConf$.MODULE$.fromSparkConf(conf, "shuffle", numCores);
   }
}
