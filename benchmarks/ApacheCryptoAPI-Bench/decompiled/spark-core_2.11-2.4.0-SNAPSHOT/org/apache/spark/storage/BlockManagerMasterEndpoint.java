package org.apache.spark.storage;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.SparkListenerBlockManagerAdded;
import org.apache.spark.scheduler.SparkListenerBlockManagerRemoved;
import org.apache.spark.scheduler.SparkListenerBlockUpdated;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcJJ$sp;
import scala.collection.IndexedSeq;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.JavaConverters$;
import scala.collection.MapLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Set;
import scala.collection.SetLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;
import scala.util.Random;

@ScalaSignature(
   bytes = "\u0006\u0001\tUf!B\u0001\u0003\u0001\u0011Q!A\u0007\"m_\u000e\\W*\u00198bO\u0016\u0014X*Y:uKJ,e\u000e\u001a9pS:$(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0005\u0001-\tr\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Ui\u0011a\u0005\u0006\u0003)\u0011\t1A\u001d9d\u0013\t12CA\u000bUQJ,\u0017\rZ*bM\u0016\u0014\u0006oY#oIB|\u0017N\u001c;\u0011\u0005aYR\"A\r\u000b\u0005i!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005qI\"a\u0002'pO\u001eLgn\u001a\u0005\t=\u0001\u0011)\u0019!C!A\u00051!\u000f]2F]Z\u001c\u0001!F\u0001\"!\t\u0011\"%\u0003\u0002$'\t1!\u000b]2F]ZD\u0001\"\n\u0001\u0003\u0002\u0003\u0006I!I\u0001\beB\u001cWI\u001c<!\u0011!9\u0003A!b\u0001\n\u0003A\u0013aB5t\u0019>\u001c\u0017\r\\\u000b\u0002SA\u0011ABK\u0005\u0003W5\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003*\u0003!I7\u000fT8dC2\u0004\u0003\u0002C\u0018\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\t\r|gN\u001a\t\u0003cIj\u0011\u0001B\u0005\u0003g\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011U\u0002!\u0011!Q\u0001\nY\n1\u0002\\5ti\u0016tWM\u001d\"vgB\u0011qGO\u0007\u0002q)\u0011\u0011\bB\u0001\ng\u000eDW\rZ;mKJL!a\u000f\u001d\u0003\u001f1Kg/\u001a'jgR,g.\u001a:CkNDQ!\u0010\u0001\u0005\u0002y\na\u0001P5oSRtD#B B\u0005\u000e#\u0005C\u0001!\u0001\u001b\u0005\u0011\u0001\"\u0002\u0010=\u0001\u0004\t\u0003\"B\u0014=\u0001\u0004I\u0003\"B\u0018=\u0001\u0004\u0001\u0004\"B\u001b=\u0001\u00041\u0004b\u0002$\u0001\u0005\u0004%IaR\u0001\u0011E2|7m['b]\u0006<WM]%oM>,\u0012\u0001\u0013\t\u0005\u0013:\u00036+D\u0001K\u0015\tYE*A\u0004nkR\f'\r\\3\u000b\u00055k\u0011AC2pY2,7\r^5p]&\u0011qJ\u0013\u0002\b\u0011\u0006\u001c\b.T1q!\t\u0001\u0015+\u0003\u0002S\u0005\tq!\t\\8dW6\u000bg.Y4fe&#\u0007C\u0001!U\u0013\t)&A\u0001\tCY>\u001c7.T1oC\u001e,'/\u00138g_\"1q\u000b\u0001Q\u0001\n!\u000b\u0011C\u00197pG.l\u0015M\\1hKJLeNZ8!\u0011\u001dI\u0006A1A\u0005\ni\u000b\u0001D\u00197pG.l\u0015M\\1hKJLEMQ=Fq\u0016\u001cW\u000f^8s+\u0005Y\u0006\u0003B%O9B\u0003\"!\u00181\u000f\u00051q\u0016BA0\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011M\u0019\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005}k\u0001B\u00023\u0001A\u0003%1,A\rcY>\u001c7.T1oC\u001e,'/\u00133Cs\u0016CXmY;u_J\u0004\u0003b\u00024\u0001\u0005\u0004%IaZ\u0001\u000fE2|7m\u001b'pG\u0006$\u0018n\u001c8t+\u0005A\u0007\u0003B5o_Jl\u0011A\u001b\u0006\u0003W2\fA!\u001e;jY*\tQ.\u0001\u0003kCZ\f\u0017BA(k!\t\u0001\u0005/\u0003\u0002r\u0005\t9!\t\\8dW&#\u0007cA%t!&\u0011AO\u0013\u0002\b\u0011\u0006\u001c\bnU3u\u0011\u00191\b\u0001)A\u0005Q\u0006y!\r\\8dW2{7-\u0019;j_:\u001c\b\u0005C\u0004y\u0001\t\u0007I\u0011B=\u0002\u001b\u0005\u001c8\u000e\u00165sK\u0006$\u0007k\\8m+\u0005Q\bCA>\u007f\u001b\u0005a(BA?k\u0003)\u0019wN\\2veJ,g\u000e^\u0005\u0003\u007fr\u0014!\u0003\u00165sK\u0006$\u0007k\\8m\u000bb,7-\u001e;pe\"9\u00111\u0001\u0001!\u0002\u0013Q\u0018AD1tWRC'/Z1e!>|G\u000e\t\u0005\n\u0003\u000f\u0001!\u0019!C\u0006\u0003\u0013\t1#Y:l\u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\"!a\u0003\u0011\t\u00055\u0011\u0011C\u0007\u0003\u0003\u001fQ!!`\u0007\n\t\u0005M\u0011q\u0002\u0002 \u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\u00050Z2vi>\u00148+\u001a:wS\u000e,\u0007\u0002CA\f\u0001\u0001\u0006I!a\u0003\u0002)\u0005\u001c8.\u0012=fGV$\u0018n\u001c8D_:$X\r\u001f;!\u0011%\tY\u0002\u0001b\u0001\n\u0013\ti\"\u0001\bu_B|Gn\\4z\u001b\u0006\u0004\b/\u001a:\u0016\u0005\u0005}\u0001c\u0001!\u0002\"%\u0019\u00111\u0005\u0002\u0003\u001dQ{\u0007o\u001c7pOfl\u0015\r\u001d9fe\"A\u0011q\u0005\u0001!\u0002\u0013\ty\"A\bu_B|Gn\\4z\u001b\u0006\u0004\b/\u001a:!\u0011!\tY\u0003\u0001b\u0001\n\u0003A\u0013\u0001\u00069s_\u0006\u001cG/\u001b<fYf\u0014V\r\u001d7jG\u0006$X\rC\u0004\u00020\u0001\u0001\u000b\u0011B\u0015\u0002+A\u0014x.Y2uSZ,G.\u001f*fa2L7-\u0019;fA!9\u00111\u0007\u0001\u0005B\u0005U\u0012a\u0004:fG\u0016Lg/Z!oIJ+\u0007\u000f\\=\u0015\t\u0005]\u0012\u0011\n\t\b\u0019\u0005e\u0012QHA\"\u0013\r\tY$\u0004\u0002\u0010!\u0006\u0014H/[1m\rVt7\r^5p]B\u0019A\"a\u0010\n\u0007\u0005\u0005SBA\u0002B]f\u00042\u0001DA#\u0013\r\t9%\u0004\u0002\u0005+:LG\u000f\u0003\u0005\u0002L\u0005E\u0002\u0019AA'\u0003\u001d\u0019wN\u001c;fqR\u00042AEA(\u0013\r\t\tf\u0005\u0002\u000f%B\u001c7)\u00197m\u0007>tG/\u001a=u\u0011\u001d\t)\u0006\u0001C\u0005\u0003/\n\u0011B]3n_Z,'\u000b\u001a3\u0015\t\u0005e\u0013Q\u0010\t\u0007\u0003\u001b\tY&a\u0018\n\t\u0005u\u0013q\u0002\u0002\u0007\rV$XO]3\u0011\r\u0005\u0005\u0014\u0011OA<\u001d\u0011\t\u0019'!\u001c\u000f\t\u0005\u0015\u00141N\u0007\u0003\u0003OR1!!\u001b \u0003\u0019a$o\\8u}%\ta\"C\u0002\u0002p5\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002t\u0005U$aA*fc*\u0019\u0011qN\u0007\u0011\u00071\tI(C\u0002\u0002|5\u00111!\u00138u\u0011!\ty(a\u0015A\u0002\u0005]\u0014!\u0002:eI&#\u0007bBAB\u0001\u0011%\u0011QQ\u0001\u000ee\u0016lwN^3TQV4g\r\\3\u0015\t\u0005\u001d\u00151\u0012\t\u0007\u0003\u001b\tY&!#\u0011\u000b\u0005\u0005\u0014\u0011O\u0015\t\u0011\u00055\u0015\u0011\u0011a\u0001\u0003o\n\u0011b\u001d5vM\u001adW-\u00133\t\u000f\u0005E\u0005\u0001\"\u0003\u0002\u0014\u0006y!/Z7pm\u0016\u0014%o\\1eG\u0006\u001cH\u000f\u0006\u0004\u0002Z\u0005U\u0015q\u0014\u0005\t\u0003/\u000by\t1\u0001\u0002\u001a\u0006Y!M]8bI\u000e\f7\u000f^%e!\ra\u00111T\u0005\u0004\u0003;k!\u0001\u0002'p]\u001eDq!!)\u0002\u0010\u0002\u0007\u0011&\u0001\tsK6|g/\u001a$s_6$%/\u001b<fe\"9\u0011Q\u0015\u0001\u0005\n\u0005\u001d\u0016A\u0005:f[>4XM\u00117pG.l\u0015M\\1hKJ$B!a\u0011\u0002*\"9\u00111VAR\u0001\u0004\u0001\u0016A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\u0005\b\u0003_\u0003A\u0011BAY\u00039\u0011X-\\8wK\u0016CXmY;u_J$B!a\u0011\u00024\"9\u0011QWAW\u0001\u0004a\u0016AB3yK\u000eLE\rC\u0004\u0002:\u0002!I!a/\u0002#!,\u0017M\u001d;cK\u0006$(+Z2fSZ,G\rF\u0002*\u0003{Cq!a+\u00028\u0002\u0007\u0001\u000bC\u0004\u0002B\u0002!I!a1\u0002-I,Wn\u001c<f\u00052|7m\u001b$s_6<vN]6feN$B!a\u0011\u0002F\"9\u0011qYA`\u0001\u0004y\u0017a\u00022m_\u000e\\\u0017\n\u001a\u0005\b\u0003\u0017\u0004A\u0011BAg\u00031iW-\\8ssN#\u0018\r^;t+\t\ty\r\u0005\u0004^\u0003#\u0004\u0016Q[\u0005\u0004\u0003'\u0014'aA'baB9A\"a6\u0002\u001a\u0006e\u0015bAAm\u001b\t1A+\u001e9mKJBq!!8\u0001\t\u0013\ty.A\u0007ti>\u0014\u0018mZ3Ti\u0006$Xo]\u000b\u0003\u0003C\u0004R\u0001DAr\u0003OL1!!:\u000e\u0005\u0015\t%O]1z!\r\u0001\u0015\u0011^\u0005\u0004\u0003W\u0014!!D*u_J\fw-Z*uCR,8\u000fC\u0004\u0002p\u0002!I!!=\u0002\u0017\tdwnY6Ti\u0006$Xo\u001d\u000b\u0007\u0003g\u0014\u0019A!\u0002\u0011\ru\u000b\t\u000eUA{!\u0019\ti!a\u0017\u0002xB)A\"!?\u0002~&\u0019\u00111`\u0007\u0003\r=\u0003H/[8o!\r\u0001\u0015q`\u0005\u0004\u0005\u0003\u0011!a\u0003\"m_\u000e\\7\u000b^1ukNDq!a2\u0002n\u0002\u0007q\u000eC\u0004\u0003\b\u00055\b\u0019A\u0015\u0002\u0013\u0005\u001c8n\u00157bm\u0016\u001c\bb\u0002B\u0006\u0001\u0011%!QB\u0001\u0014O\u0016$X*\u0019;dQ&twM\u00117pG.LEm\u001d\u000b\u0007\u0005\u001f\u0011\u0019B!\b\u0011\r\u00055\u00111\fB\t!\u0015\t\t'!\u001dp\u0011!\u0011)B!\u0003A\u0002\t]\u0011A\u00024jYR,'\u000fE\u0003\r\u00053y\u0017&C\u0002\u0003\u001c5\u0011\u0011BR;oGRLwN\\\u0019\t\u000f\t\u001d!\u0011\u0002a\u0001S!9!\u0011\u0005\u0001\u0005\n\t\r\u0012\u0001\u0003:fO&\u001cH/\u001a:\u0015\u0013A\u0013)C!\u000b\u0003.\tE\u0002b\u0002B\u0014\u0005?\u0001\r\u0001U\u0001\u0016S\u0012<\u0016\u000e\u001e5pkR$v\u000e]8m_\u001eL\u0018J\u001c4p\u0011!\u0011YCa\bA\u0002\u0005e\u0015\u0001E7bq>s\u0007*Z1q\u001b\u0016l7+\u001b>f\u0011!\u0011yCa\bA\u0002\u0005e\u0015!E7bq>3g\rS3ba6+WnU5{K\"A!1\u0007B\u0010\u0001\u0004\u0011)$A\u0007tY\u00064X-\u00128ea>Lg\u000e\u001e\t\u0004%\t]\u0012b\u0001B\u001d'\tq!\u000b]2F]\u0012\u0004x.\u001b8u%\u00164\u0007b\u0002B\u001f\u0001\u0011%!qH\u0001\u0010kB$\u0017\r^3CY>\u001c7.\u00138g_RY\u0011F!\u0011\u0003D\t\u0015#q\nB*\u0011\u001d\tYKa\u000fA\u0002ACq!a2\u0003<\u0001\u0007q\u000e\u0003\u0005\u0003H\tm\u0002\u0019\u0001B%\u00031\u0019Ho\u001c:bO\u0016dUM^3m!\r\u0001%1J\u0005\u0004\u0005\u001b\u0012!\u0001D*u_J\fw-\u001a'fm\u0016d\u0007\u0002\u0003B)\u0005w\u0001\r!!'\u0002\u000f5,WnU5{K\"A!Q\u000bB\u001e\u0001\u0004\tI*\u0001\u0005eSN\\7+\u001b>f\u0011\u001d\u0011I\u0006\u0001C\u0005\u00057\nAbZ3u\u0019>\u001c\u0017\r^5p]N$BA!\u0018\u0003`A)\u0011\u0011MA9!\"9\u0011q\u0019B,\u0001\u0004y\u0007b\u0002B2\u0001\u0011%!QM\u0001\u0016O\u0016$Hj\\2bi&|gn]!oIN#\u0018\r^;t)\u0011\u00119Ga\"\u0011\u000b1\tIP!\u001b\u0011\t\t-$\u0011\u0011\b\u0005\u0005[\u0012iH\u0004\u0003\u0003p\tmd\u0002\u0002B9\u0005srAAa\u001d\u0003x9!\u0011Q\rB;\u0013\u0005I\u0011BA\u0004\t\u0013\t)a!\u0003\u0002\u0004\t%\u0019!q\u0010\u0002\u0002)\tcwnY6NC:\fw-\u001a:NKN\u001c\u0018mZ3t\u0013\u0011\u0011\u0019I!\"\u0003/\tcwnY6M_\u000e\fG/[8og\u0006sGm\u0015;biV\u001c(b\u0001B@\u0005!9\u0011q\u0019B1\u0001\u0004y\u0007b\u0002BF\u0001\u0011%!QR\u0001\u001dO\u0016$Hj\\2bi&|gn]'vYRL\u0007\u000f\\3CY>\u001c7.\u00133t)\u0011\u0011yI!&\u0011\r\u0005\u0005$\u0011\u0013B/\u0013\u0011\u0011\u0019*!\u001e\u0003\u0015%sG-\u001a=fIN+\u0017\u000f\u0003\u0005\u0003\u0018\n%\u0005\u0019\u0001BM\u0003!\u0011Gn\\2l\u0013\u0012\u001c\b\u0003\u0002\u0007\u0002d>DqA!(\u0001\t\u0013\u0011y*\u0001\u0005hKR\u0004V-\u001a:t)\u0011\u0011iF!)\t\u000f\u0005-&1\u0014a\u0001!\"9!Q\u0015\u0001\u0005\n\t\u001d\u0016AF4fi\u0016CXmY;u_J,e\u000e\u001a9pS:$(+\u001a4\u0015\t\t%&1\u0016\t\u0006\u0019\u0005e(Q\u0007\u0005\b\u0005[\u0013\u0019\u000b1\u0001]\u0003))\u00070Z2vi>\u0014\u0018\n\u001a\u0005\b\u0005c\u0003A\u0011\tBZ\u0003\u0019ygn\u0015;paR\u0011\u00111\t"
)
public class BlockManagerMasterEndpoint implements ThreadSafeRpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   private final boolean isLocal;
   public final LiveListenerBus org$apache$spark$storage$BlockManagerMasterEndpoint$$listenerBus;
   private final HashMap<BlockManagerId, BlockManagerInfo> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo;
   private final HashMap<String, BlockManagerId> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor;
   private final java.util.HashMap<BlockId, HashSet<BlockManagerId>> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations;
   private final ThreadPoolExecutor askThreadPool;
   private final ExecutionContextExecutorService org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext;
   private final TopologyMapper topologyMapper;
   private final boolean proactivelyReplicate;
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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return RpcEndpoint$class.receive(this);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onDisconnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onDisconnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
   }

   public void onStart() {
      RpcEndpoint$class.onStart(this);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public boolean isLocal() {
      return this.isLocal;
   }

   public HashMap<BlockManagerId, BlockManagerInfo> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo() {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo;
   }

   public HashMap<String, BlockManagerId> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor() {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor;
   }

   public java.util.HashMap<BlockId, HashSet<BlockManagerId>> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations() {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations;
   }

   private ThreadPoolExecutor askThreadPool() {
      return this.askThreadPool;
   }

   public ExecutionContextExecutorService org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext() {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext;
   }

   private TopologyMapper topologyMapper() {
      return this.topologyMapper;
   }

   public boolean proactivelyReplicate() {
      return this.proactivelyReplicate;
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var11;
            if (x1 instanceof BlockManagerMessages.RegisterBlockManager) {
               BlockManagerMessages.RegisterBlockManager var4 = (BlockManagerMessages.RegisterBlockManager)x1;
               BlockManagerId blockManagerId = var4.blockManagerId();
               long maxOnHeapMemSize = var4.maxOnHeapMemSize();
               long maxOffHeapMemSize = var4.maxOffHeapMemSize();
               RpcEndpointRef slaveEndpoint = var4.sender();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$register(blockManagerId, maxOnHeapMemSize, maxOffHeapMemSize, slaveEndpoint));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.UpdateBlockInfo) {
               BlockManagerMessages.UpdateBlockInfo var12 = (BlockManagerMessages.UpdateBlockInfo)x1;
               BlockManagerId blockManagerId = var12.blockManagerId();
               BlockId blockId = var12.blockId();
               StorageLevel storageLevel = var12.storageLevel();
               long deserializedSize = var12.memSize();
               long size = var12.diskSize();
               this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$updateBlockInfo(blockManagerId, blockId, storageLevel, deserializedSize, size)));
               this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$listenerBus.post(new SparkListenerBlockUpdated(BlockUpdatedInfo$.MODULE$.apply(var12)));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetLocations) {
               BlockManagerMessages.GetLocations var20 = (BlockManagerMessages.GetLocations)x1;
               BlockId blockId = var20.blockId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocations(blockId));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetLocationsAndStatus) {
               BlockManagerMessages.GetLocationsAndStatus var22 = (BlockManagerMessages.GetLocationsAndStatus)x1;
               BlockId blockId = var22.blockId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocationsAndStatus(blockId));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetLocationsMultipleBlockIds) {
               BlockManagerMessages.GetLocationsMultipleBlockIds var24 = (BlockManagerMessages.GetLocationsMultipleBlockIds)x1;
               BlockId[] blockIds = var24.blockIds();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocationsMultipleBlockIds(blockIds));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetPeers) {
               BlockManagerMessages.GetPeers var26 = (BlockManagerMessages.GetPeers)x1;
               BlockManagerId blockManagerId = var26.blockManagerId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getPeers(blockManagerId));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetExecutorEndpointRef) {
               BlockManagerMessages.GetExecutorEndpointRef var28 = (BlockManagerMessages.GetExecutorEndpointRef)x1;
               String executorId = var28.executorId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getExecutorEndpointRef(executorId));
               var11 = BoxedUnit.UNIT;
            } else if (BlockManagerMessages.GetMemoryStatus$.MODULE$.equals(x1)) {
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$memoryStatus());
               var11 = BoxedUnit.UNIT;
            } else if (BlockManagerMessages.GetStorageStatus$.MODULE$.equals(x1)) {
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$storageStatus());
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetBlockStatus) {
               BlockManagerMessages.GetBlockStatus var30 = (BlockManagerMessages.GetBlockStatus)x1;
               BlockId blockId = var30.blockId();
               boolean askSlaves = var30.askSlaves();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockStatus(blockId, askSlaves));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetMatchingBlockIds) {
               BlockManagerMessages.GetMatchingBlockIds var33 = (BlockManagerMessages.GetMatchingBlockIds)x1;
               Function1 filter = var33.filter();
               boolean askSlaves = var33.askSlaves();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getMatchingBlockIds(filter, askSlaves));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveRdd) {
               BlockManagerMessages.RemoveRdd var36 = (BlockManagerMessages.RemoveRdd)x1;
               int rddId = var36.rddId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeRdd(rddId));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveShuffle) {
               BlockManagerMessages.RemoveShuffle var38 = (BlockManagerMessages.RemoveShuffle)x1;
               int shuffleId = var38.shuffleId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeShuffle(shuffleId));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveBroadcast) {
               BlockManagerMessages.RemoveBroadcast var40 = (BlockManagerMessages.RemoveBroadcast)x1;
               long broadcastId = var40.broadcastId();
               boolean removeFromDriver = var40.removeFromDriver();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBroadcast(broadcastId, removeFromDriver));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveBlock) {
               BlockManagerMessages.RemoveBlock var44 = (BlockManagerMessages.RemoveBlock)x1;
               BlockId blockId = var44.blockId();
               this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBlockFromWorkers(blockId);
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveExecutor) {
               BlockManagerMessages.RemoveExecutor var46 = (BlockManagerMessages.RemoveExecutor)x1;
               String execId = var46.execId();
               this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeExecutor(execId);
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var11 = BoxedUnit.UNIT;
            } else if (BlockManagerMessages.StopBlockManagerMaster$.MODULE$.equals(x1)) {
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               this.$outer.stop();
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.BlockManagerHeartbeat) {
               BlockManagerMessages.BlockManagerHeartbeat var48 = (BlockManagerMessages.BlockManagerHeartbeat)x1;
               BlockManagerId blockManagerId = var48.blockManagerId();
               this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$heartbeatReceived(blockManagerId)));
               var11 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.HasCachedBlocks) {
               BlockManagerMessages.HasCachedBlocks var50 = (BlockManagerMessages.HasCachedBlocks)x1;
               String executorId = var50.executorId();
               Option var52 = this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().get(executorId);
               if (var52 instanceof Some) {
                  Some var53 = (Some)var52;
                  BlockManagerId bm = (BlockManagerId)var53.x();
                  if (this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().contains(bm)) {
                     BlockManagerInfo bmInfo = (BlockManagerInfo)this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(bm);
                     this.context$1.reply(BoxesRunTime.boxToBoolean(bmInfo.cachedBlocks().nonEmpty()));
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.context$1.reply(BoxesRunTime.boxToBoolean(false));
                     BoxedUnit var57 = BoxedUnit.UNIT;
                  }
               } else {
                  if (!None$.MODULE$.equals(var52)) {
                     throw new MatchError(var52);
                  }

                  this.context$1.reply(BoxesRunTime.boxToBoolean(false));
                  BoxedUnit var55 = BoxedUnit.UNIT;
               }

               var11 = BoxedUnit.UNIT;
            } else {
               var11 = var2.apply(x1);
            }

            return (B1)var11;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof BlockManagerMessages.RegisterBlockManager) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.UpdateBlockInfo) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetLocations) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetLocationsAndStatus) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetLocationsMultipleBlockIds) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetPeers) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetExecutorEndpointRef) {
               var3 = true;
            } else if (BlockManagerMessages.GetMemoryStatus$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (BlockManagerMessages.GetStorageStatus$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetBlockStatus) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetMatchingBlockIds) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveRdd) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveShuffle) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveBroadcast) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveBlock) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveExecutor) {
               var3 = true;
            } else if (BlockManagerMessages.StopBlockManagerMaster$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.BlockManagerHeartbeat) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.HasCachedBlocks) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      };
   }

   public Future<Seq<Object>> org$apache$spark$storage$BlockManagerMasterEndpoint$$removeRdd(int rddId) {
      Iterable blocks = (Iterable)((TraversableLike)((MapLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations()).asScala()).keys().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterable<RDDBlockId> apply(BlockId x$1) {
            return Option$.MODULE$.<RDDBlockId>option2Iterable(x$1.asRDDId());
         }
      }, Iterable$.MODULE$.canBuildFrom())).filter(new Serializable(this, rddId) {
         public static final long serialVersionUID = 0L;
         private final int rddId$1;

         public final boolean apply(RDDBlockId x$2) {
            return x$2.rddId() == this.rddId$1;
         }

         public {
            this.rddId$1 = rddId$1;
         }
      });
      blocks.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;

         public final HashSet<BlockManagerId> apply(RDDBlockId blockId) {
            HashSet bms = (HashSet)this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId);
            bms.foreach(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               public final RDDBlockId blockId$6;

               public final void apply(BlockManagerId bm) {
                  this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().get(bm).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply(BlockManagerInfo x$3) {
                        x$3.removeBlock(this.$outer.blockId$6);
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

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$6 = blockId$6;
                  }
               }
            });
            return (HashSet)this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().remove(blockId);
         }

         // $FF: synthetic method
         public BlockManagerMasterEndpoint org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      BlockManagerMessages.RemoveRdd removeMsg = new BlockManagerMessages.RemoveRdd(rddId);
      Seq futures = ((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().values().map(new Serializable(this, rddId, removeMsg) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;
         public final int rddId$1;
         private final BlockManagerMessages.RemoveRdd removeMsg$1;

         public final Future<Object> apply(BlockManagerInfo bm) {
            return bm.slaveEndpoint().<Object>ask(this.removeMsg$1, ClassTag$.MODULE$.Int()).<Object>recover(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final <A1 extends Throwable, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
                  Object var5;
                  if (x2 instanceof IOException) {
                     IOException var4 = (IOException)x2;
                     this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$$outer().logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error trying to remove RDD ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$anonfun$$$outer().rddId$1)}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }, var4);
                     var5 = BoxesRunTime.boxToInteger(0);
                  } else {
                     var5 = var2.apply(x2);
                  }

                  return (B1)var5;
               }

               public final boolean isDefinedAt(Throwable x2) {
                  boolean var3;
                  if (x2 instanceof IOException) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  return var3;
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
         }

         // $FF: synthetic method
         public BlockManagerMasterEndpoint org$apache$spark$storage$BlockManagerMasterEndpoint$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rddId$1 = rddId$1;
               this.removeMsg$1 = removeMsg$1;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
      return Future$.MODULE$.sequence(futures, Seq$.MODULE$.canBuildFrom(), this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
   }

   public Future<Seq<Object>> org$apache$spark$storage$BlockManagerMasterEndpoint$$removeShuffle(int shuffleId) {
      BlockManagerMessages.RemoveShuffle removeMsg = new BlockManagerMessages.RemoveShuffle(shuffleId);
      return Future$.MODULE$.sequence(((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().values().map(new Serializable(this, removeMsg) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerMessages.RemoveShuffle removeMsg$2;

         public final Future<Object> apply(BlockManagerInfo bm) {
            return bm.slaveEndpoint().<Object>ask(this.removeMsg$2, ClassTag$.MODULE$.Boolean());
         }

         public {
            this.removeMsg$2 = removeMsg$2;
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq(), Seq$.MODULE$.canBuildFrom(), this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
   }

   public Future<Seq<Object>> org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBroadcast(long broadcastId, boolean removeFromDriver) {
      BlockManagerMessages.RemoveBroadcast removeMsg = new BlockManagerMessages.RemoveBroadcast(broadcastId, removeFromDriver);
      Iterable requiredBlockManagers = (Iterable)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().values().filter(new Serializable(this, removeFromDriver) {
         public static final long serialVersionUID = 0L;
         private final boolean removeFromDriver$1;

         public final boolean apply(BlockManagerInfo info) {
            return this.removeFromDriver$1 || !info.blockManagerId().isDriver();
         }

         public {
            this.removeFromDriver$1 = removeFromDriver$1;
         }
      });
      return Future$.MODULE$.sequence(((TraversableOnce)requiredBlockManagers.map(new Serializable(this, removeMsg) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerMessages.RemoveBroadcast removeMsg$3;

         public final Future<Object> apply(BlockManagerInfo bm) {
            return bm.slaveEndpoint().<Object>ask(this.removeMsg$3, ClassTag$.MODULE$.Int());
         }

         public {
            this.removeMsg$3 = removeMsg$3;
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq(), Seq$.MODULE$.canBuildFrom(), this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
   }

   public void org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBlockManager(BlockManagerId blockManagerId) {
      BlockManagerInfo info = (BlockManagerInfo)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(blockManagerId);
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().$minus$eq(blockManagerId.executorId());
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().remove(blockManagerId);

      for(BlockId blockId : info.blocks().keySet()) {
         HashSet locations = (HashSet)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId);
         locations.$minus$eq(blockManagerId);
         if (locations.size() == 0) {
            this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().remove(blockId);
            this.logWarning(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$4;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No more replicas available for ", " !"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4}));
               }

               public {
                  this.blockId$4 = blockId$4;
               }
            });
         } else if (this.proactivelyReplicate() && (blockId.isRDD() || blockId instanceof TestBlockId)) {
            int maxReplicas = locations.size() + 1;
            int i = (new Random(blockId.hashCode())).nextInt(locations.size());
            Seq blockLocations = locations.toSeq();
            BlockManagerId candidateBMId = (BlockManagerId)blockLocations.apply(i);
            this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().get(candidateBMId).foreach(new Serializable(this, blockId, locations, maxReplicas, candidateBMId) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$4;
               private final HashSet locations$1;
               private final int maxReplicas$1;
               public final BlockManagerId candidateBMId$1;

               public final Future<Object> apply(BlockManagerInfo bm) {
                  Seq remainingLocations = (Seq)this.locations$1.toSeq().filter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final boolean apply(BlockManagerId bm) {
                        boolean var10000;
                        label23: {
                           BlockManagerId var2 = this.$outer.candidateBMId$1;
                           if (bm == null) {
                              if (var2 == null) {
                                 break label23;
                              }
                           } else if (bm.equals(var2)) {
                              break label23;
                           }

                           var10000 = true;
                           return var10000;
                        }

                        var10000 = false;
                        return var10000;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  BlockManagerMessages.ReplicateBlock replicateMsg = new BlockManagerMessages.ReplicateBlock(this.blockId$4, remainingLocations, this.maxReplicas$1);
                  return bm.slaveEndpoint().<Object>ask(replicateMsg, ClassTag$.MODULE$.Boolean());
               }

               public {
                  this.blockId$4 = blockId$4;
                  this.locations$1 = locations$1;
                  this.maxReplicas$1 = maxReplicas$1;
                  this.candidateBMId$1 = candidateBMId$1;
               }
            });
         }
      }

      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$listenerBus.post(new SparkListenerBlockManagerRemoved(System.currentTimeMillis(), blockManagerId));
      this.logInfo(new Serializable(this, blockManagerId) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerId blockManagerId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing block manager ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockManagerId$2}));
         }

         public {
            this.blockManagerId$2 = blockManagerId$2;
         }
      });
   }

   public void org$apache$spark$storage$BlockManagerMasterEndpoint$$removeExecutor(String execId) {
      this.logInfo(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Trying to remove executor ").append((Object)this.execId$1).append((Object)" from BlockManagerMaster.").toString();
         }

         public {
            this.execId$1 = execId$1;
         }
      });
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().get(execId).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;

         public final void apply(BlockManagerId blockManagerId) {
            this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBlockManager(blockManagerId);
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

   public boolean org$apache$spark$storage$BlockManagerMasterEndpoint$$heartbeatReceived(BlockManagerId blockManagerId) {
      boolean var10000;
      if (this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().contains(blockManagerId)) {
         ((BlockManagerInfo)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(blockManagerId)).updateLastSeenMs();
         var10000 = true;
      } else {
         var10000 = blockManagerId.isDriver() && !this.isLocal();
      }

      return var10000;
   }

   public void org$apache$spark$storage$BlockManagerMasterEndpoint$$removeBlockFromWorkers(BlockId blockId) {
      HashSet locations = (HashSet)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId);
      if (locations != null) {
         locations.foreach(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManagerMasterEndpoint $outer;
            private final BlockId blockId$3;

            public final Object apply(BlockManagerId blockManagerId) {
               Option blockManager = this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().get(blockManagerId);
               return blockManager.isDefined() ? ((BlockManagerInfo)blockManager.get()).slaveEndpoint().ask(new BlockManagerMessages.RemoveBlock(this.blockId$3), ClassTag$.MODULE$.Boolean()) : BoxedUnit.UNIT;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.blockId$3 = blockId$3;
               }
            }
         });
      }

   }

   public Map<BlockManagerId, Tuple2<Object, Object>> org$apache$spark$storage$BlockManagerMasterEndpoint$$memoryStatus() {
      return ((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<BlockManagerId, Tuple2<Object, Object>> apply(Tuple2<BlockManagerId, BlockManagerInfo> x0$1) {
            if (x0$1 != null) {
               BlockManagerId blockManagerId = (BlockManagerId)x0$1._1();
               BlockManagerInfo info = (BlockManagerInfo)x0$1._2();
               Tuple2 var5 = new Tuple2(blockManagerId, new Tuple2$mcJJ$sp(info.maxMem(), info.remainingMem()));
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, HashMap$.MODULE$.canBuildFrom())).<BlockManagerId, Tuple2<Object, Object>>toMap(Predef$.MODULE$.$conforms());
   }

   public StorageStatus[] org$apache$spark$storage$BlockManagerMasterEndpoint$$storageStatus() {
      return (StorageStatus[])((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final StorageStatus apply(Tuple2<BlockManagerId, BlockManagerInfo> x0$2) {
            if (x0$2 != null) {
               BlockManagerId blockManagerId = (BlockManagerId)x0$2._1();
               BlockManagerInfo info = (BlockManagerInfo)x0$2._2();
               StorageStatus var5 = new StorageStatus(blockManagerId, info.maxMem(), new Some(BoxesRunTime.boxToLong(info.maxOnHeapMem())), new Some(BoxesRunTime.boxToLong(info.maxOffHeapMem())), (scala.collection.Map)JavaConverters$.MODULE$.mapAsScalaMapConverter(info.blocks()).asScala());
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
         }
      }, scala.collection.mutable.Iterable$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(StorageStatus.class));
   }

   public Map<BlockManagerId, Future<Option<BlockStatus>>> org$apache$spark$storage$BlockManagerMasterEndpoint$$blockStatus(BlockId blockId, boolean askSlaves) {
      BlockManagerMessages.GetBlockStatus getBlockStatus = new BlockManagerMessages.GetBlockStatus(blockId, BlockManagerMessages.GetBlockStatus$.MODULE$.apply$default$2());
      return ((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().values().map(new Serializable(this, blockId, askSlaves, getBlockStatus) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;
         public final BlockId blockId$2;
         private final boolean askSlaves$1;
         private final BlockManagerMessages.GetBlockStatus getBlockStatus$1;

         public final Tuple2<BlockManagerId, Future<Option<BlockStatus>>> apply(BlockManagerInfo info) {
            Future blockStatusFuture = this.askSlaves$1 ? info.slaveEndpoint().ask(this.getBlockStatus$1, ClassTag$.MODULE$.apply(Option.class)) : Future$.MODULE$.apply(new Serializable(this, info) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final BlockManagerInfo info$1;

               public final Option<BlockStatus> apply() {
                  return this.info$1.getStatus(this.$outer.blockId$2);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.info$1 = info$1;
                  }
               }
            }, this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
            return new Tuple2<BlockManagerId, Future<Option<BlockStatus>>>(info.blockManagerId(), blockStatusFuture);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$2 = blockId$2;
               this.askSlaves$1 = askSlaves$1;
               this.getBlockStatus$1 = getBlockStatus$1;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom())).<BlockManagerId, Future<Option<BlockStatus>>>toMap(Predef$.MODULE$.$conforms());
   }

   public Future<Seq<BlockId>> org$apache$spark$storage$BlockManagerMasterEndpoint$$getMatchingBlockIds(Function1<BlockId, Object> filter, boolean askSlaves) {
      BlockManagerMessages.GetMatchingBlockIds getMatchingBlockIds = new BlockManagerMessages.GetMatchingBlockIds(filter, BlockManagerMessages.GetMatchingBlockIds$.MODULE$.apply$default$2());
      return Future$.MODULE$.sequence((TraversableOnce)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().values().map(new Serializable(this, filter, askSlaves, getMatchingBlockIds) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;
         public final Function1 filter$1;
         private final boolean askSlaves$2;
         private final BlockManagerMessages.GetMatchingBlockIds getMatchingBlockIds$1;

         public final Future<Seq<BlockId>> apply(BlockManagerInfo info) {
            Future future = this.askSlaves$2 ? info.slaveEndpoint().ask(this.getMatchingBlockIds$1, ClassTag$.MODULE$.apply(Seq.class)) : Future$.MODULE$.apply(new Serializable(this, info) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final BlockManagerInfo info$2;

               public final Seq<BlockId> apply() {
                  return ((TraversableOnce)((MapLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(this.info$2.blocks()).asScala()).keys().filter(this.$outer.filter$1)).toSeq();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.info$2 = info$2;
                  }
               }
            }, this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
            return future;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.filter$1 = filter$1;
               this.askSlaves$2 = askSlaves$2;
               this.getMatchingBlockIds$1 = getMatchingBlockIds$1;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom()), Iterable$.MODULE$.canBuildFrom(), this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext()).<Seq<BlockId>>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<BlockId> apply(Iterable<Seq<BlockId>> x$4) {
            return ((TraversableOnce)x$4.flatten(Predef$.MODULE$.$conforms())).toSeq();
         }
      }, this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext());
   }

   public BlockManagerId org$apache$spark$storage$BlockManagerMasterEndpoint$$register(BlockManagerId idWithoutTopologyInfo, long maxOnHeapMemSize, long maxOffHeapMemSize, RpcEndpointRef slaveEndpoint) {
      BlockManagerId id = BlockManagerId$.MODULE$.apply(idWithoutTopologyInfo.executorId(), idWithoutTopologyInfo.host(), idWithoutTopologyInfo.port(), this.topologyMapper().getTopologyForHost(idWithoutTopologyInfo.host()));
      long time = System.currentTimeMillis();
      if (!this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().contains(id)) {
         Option var10 = this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().get(id.executorId());
         if (var10 instanceof Some) {
            Some var11 = (Some)var10;
            BlockManagerId oldId = (BlockManagerId)var11.x();
            this.logError(new Serializable(this, id, oldId) {
               public static final long serialVersionUID = 0L;
               private final BlockManagerId id$1;
               private final BlockManagerId oldId$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Got two different block manager registrations on same executor - ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" will replace old one ", " with new one ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.oldId$1, this.id$1}))).toString();
               }

               public {
                  this.id$1 = id$1;
                  this.oldId$1 = oldId$1;
               }
            });
            this.org$apache$spark$storage$BlockManagerMasterEndpoint$$removeExecutor(id.executorId());
            BoxedUnit var13 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var10)) {
               throw new MatchError(var10);
            }

            BoxedUnit var14 = BoxedUnit.UNIT;
         }

         this.logInfo(new Serializable(this, maxOnHeapMemSize, maxOffHeapMemSize, id) {
            public static final long serialVersionUID = 0L;
            private final long maxOnHeapMemSize$1;
            private final long maxOffHeapMemSize$1;
            private final BlockManagerId id$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Registering block manager %s with %s RAM, %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1.hostPort(), Utils$.MODULE$.bytesToString(this.maxOnHeapMemSize$1 + this.maxOffHeapMemSize$1), this.id$1}));
            }

            public {
               this.maxOnHeapMemSize$1 = maxOnHeapMemSize$1;
               this.maxOffHeapMemSize$1 = maxOffHeapMemSize$1;
               this.id$1 = id$1;
            }
         });
         this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().update(id.executorId(), id);
         this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().update(id, new BlockManagerInfo(id, System.currentTimeMillis(), maxOnHeapMemSize, maxOffHeapMemSize, slaveEndpoint));
      }

      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$listenerBus.post(new SparkListenerBlockManagerAdded(time, id, maxOnHeapMemSize + maxOffHeapMemSize, new Some(BoxesRunTime.boxToLong(maxOnHeapMemSize)), new Some(BoxesRunTime.boxToLong(maxOffHeapMemSize))));
      return id;
   }

   public boolean org$apache$spark$storage$BlockManagerMasterEndpoint$$updateBlockInfo(BlockManagerId blockManagerId, BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      if (this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().contains(blockManagerId)) {
         if (blockId == null) {
            ((BlockManagerInfo)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(blockManagerId)).updateLastSeenMs();
            return true;
         } else {
            ((BlockManagerInfo)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(blockManagerId)).updateBlockInfo(blockId, storageLevel, memSize, diskSize);
            HashSet locations = null;
            if (this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().containsKey(blockId)) {
               locations = (HashSet)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               locations = new HashSet();
               this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().put(blockId, locations);
            }

            if (storageLevel.isValid()) {
               locations.add(blockManagerId);
            } else {
               locations.remove(blockManagerId);
            }

            if (locations.size() == 0) {
               this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().remove(blockId);
            } else {
               BoxedUnit var10 = BoxedUnit.UNIT;
            }

            return true;
         }
      } else {
         return blockManagerId.isDriver() && !this.isLocal();
      }
   }

   public Seq<BlockManagerId> org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocations(BlockId blockId) {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().containsKey(blockId) ? ((SetLike)this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId)).toSeq() : (Seq)Seq$.MODULE$.empty();
   }

   public Option<BlockManagerMessages.BlockLocationsAndStatus> org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocationsAndStatus(BlockId blockId) {
      Seq locations = (Seq)Option$.MODULE$.apply(this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations().get(blockId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<BlockManagerId> apply(HashSet<BlockManagerId> x$5) {
            return x$5.toSeq();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      Option status = locations.headOption().flatMap(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;
         private final BlockId blockId$1;

         public final Option<BlockStatus> apply(BlockManagerId bmId) {
            return ((BlockManagerInfo)this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().apply(bmId)).getStatus(this.blockId$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$1 = blockId$1;
            }
         }
      });
      return (Option<BlockManagerMessages.BlockLocationsAndStatus>)(locations.nonEmpty() && status.isDefined() ? new Some(new BlockManagerMessages.BlockLocationsAndStatus(locations, (BlockStatus)status.get())) : None$.MODULE$);
   }

   public IndexedSeq<Seq<BlockManagerId>> org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocationsMultipleBlockIds(BlockId[] blockIds) {
      return (IndexedSeq)Predef$.MODULE$.refArrayOps(blockIds).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;

         public final Seq<BlockManagerId> apply(BlockId blockId) {
            return this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$getLocations(blockId);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public Seq<BlockManagerId> org$apache$spark$storage$BlockManagerMasterEndpoint$$getPeers(BlockManagerId blockManagerId) {
      Set blockManagerIds = this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().keySet();
      return blockManagerIds.contains(blockManagerId) ? ((SetLike)((TraversableLike)blockManagerIds.filterNot(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(BlockManagerId x$6) {
            return x$6.isDriver();
         }
      })).filterNot(new Serializable(this, blockManagerId) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerId blockManagerId$1;

         public final boolean apply(BlockManagerId x$7) {
            boolean var10000;
            label23: {
               BlockManagerId var2 = this.blockManagerId$1;
               if (x$7 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$7.equals(var2)) {
                  break label23;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }

         public {
            this.blockManagerId$1 = blockManagerId$1;
         }
      })).toSeq() : (Seq)Seq$.MODULE$.empty();
   }

   public Option<RpcEndpointRef> org$apache$spark$storage$BlockManagerMasterEndpoint$$getExecutorEndpointRef(String executorId) {
      return this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor().get(executorId).<RpcEndpointRef>flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerMasterEndpoint $outer;

         public final Option<RpcEndpointRef> apply(BlockManagerId blockManagerId) {
            return this.$outer.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo().get(blockManagerId).<RpcEndpointRef>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final RpcEndpointRef apply(BlockManagerInfo info) {
                  return info.slaveEndpoint();
               }
            });
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

   public void onStop() {
      this.askThreadPool().shutdownNow();
   }

   public BlockManagerMasterEndpoint(RpcEnv rpcEnv, boolean isLocal, SparkConf conf, LiveListenerBus listenerBus) {
      this.rpcEnv = rpcEnv;
      this.isLocal = isLocal;
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$listenerBus = listenerBus;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerInfo = new HashMap<BlockManagerId, BlockManagerInfo>();
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockManagerIdByExecutor = new HashMap<String, BlockManagerId>();
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$blockLocations = new java.util.HashMap();
      this.askThreadPool = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("block-manager-ask-thread-pool");
      this.org$apache$spark$storage$BlockManagerMasterEndpoint$$askExecutionContext = ExecutionContext$.MODULE$.fromExecutorService(this.askThreadPool());
      String topologyMapperClassName = conf.get("spark.storage.replication.topologyMapper", DefaultTopologyMapper.class.getName());
      Class clazz = Utils$.MODULE$.classForName(topologyMapperClassName);
      TopologyMapper mapper = (TopologyMapper)clazz.getConstructor(SparkConf.class).newInstance(conf);
      this.logInfo(new Serializable(this, topologyMapperClassName) {
         public static final long serialVersionUID = 0L;
         private final String topologyMapperClassName$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using ", " for getting topology information"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.topologyMapperClassName$1}));
         }

         public {
            this.topologyMapperClassName$1 = topologyMapperClassName$1;
         }
      });
      this.topologyMapper = mapper;
      this.proactivelyReplicate = (new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.storage.replication.proactive", "false")))).toBoolean();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "BlockManagerMasterEndpoint up";
         }
      });
   }
}
