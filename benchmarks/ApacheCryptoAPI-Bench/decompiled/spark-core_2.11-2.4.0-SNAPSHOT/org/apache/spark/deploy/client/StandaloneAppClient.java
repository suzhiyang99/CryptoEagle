package org.apache.spark.deploy.client;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.deploy.master.Master$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcAddress$;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\tme!B\u0001\u0003\u0001\u0019a!aE*uC:$\u0017\r\\8oK\u0006\u0003\bo\u00117jK:$(BA\u0002\u0005\u0003\u0019\u0019G.[3oi*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011AcF\u0007\u0002+)\u0011aCB\u0001\tS:$XM\u001d8bY&\u0011\u0001$\u0006\u0002\b\u0019><w-\u001b8h\u0011!Q\u0002A!A!\u0002\u0013a\u0012A\u0002:qG\u0016sgo\u0001\u0001\u0011\u0005u\u0001S\"\u0001\u0010\u000b\u0005}1\u0011a\u0001:qG&\u0011\u0011E\b\u0002\u0007%B\u001cWI\u001c<\t\u0011\r\u0002!\u0011!Q\u0001\n\u0011\n!\"\\1ti\u0016\u0014XK\u001d7t!\rqQeJ\u0005\u0003M=\u0011Q!\u0011:sCf\u0004\"\u0001K\u0016\u000f\u00059I\u0013B\u0001\u0016\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011A&\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005)z\u0001\u0002C\u0018\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u001d\u0005\u0004\b\u000fR3tGJL\u0007\u000f^5p]B\u0011\u0011GM\u0007\u0002\t%\u00111\u0007\u0002\u0002\u0017\u0003B\u0004H.[2bi&|g\u000eR3tGJL\u0007\u000f^5p]\"AQ\u0007\u0001B\u0001B\u0003%a'\u0001\u0005mSN$XM\\3s!\t9\u0004(D\u0001\u0003\u0013\tI$AA\u000eTi\u0006tG-\u00197p]\u0016\f\u0005\u000f]\"mS\u0016tG\u000fT5ti\u0016tWM\u001d\u0005\tw\u0001\u0011\t\u0011)A\u0005y\u0005!1m\u001c8g!\tid(D\u0001\u0007\u0013\tydAA\u0005Ta\u0006\u00148nQ8oM\")\u0011\t\u0001C\u0001\u0005\u00061A(\u001b8jiz\"ba\u0011#F\r\u001eC\u0005CA\u001c\u0001\u0011\u0015Q\u0002\t1\u0001\u001d\u0011\u0015\u0019\u0003\t1\u0001%\u0011\u0015y\u0003\t1\u00011\u0011\u0015)\u0004\t1\u00017\u0011\u0015Y\u0004\t1\u0001=\u0011\u001dQ\u0005A1A\u0005\n-\u000b!#\\1ti\u0016\u0014(\u000b]2BI\u0012\u0014Xm]:fgV\tA\nE\u0002\u000fK5\u0003\"!\b(\n\u0005=s\"A\u0003*qG\u0006#GM]3tg\"1\u0011\u000b\u0001Q\u0001\n1\u000b1#\\1ti\u0016\u0014(\u000b]2BI\u0012\u0014Xm]:fg\u0002Bqa\u0015\u0001C\u0002\u0013%A+\u0001\u000fS\u000b\u001eK5\u000b\u0016*B)&{ej\u0018+J\u001b\u0016{U\u000bV0T\u000b\u000e{e\nR*\u0016\u0003U\u0003\"A\u0004,\n\u0005]{!aA%oi\"1\u0011\f\u0001Q\u0001\nU\u000bQDU#H\u0013N#&+\u0011+J\u001f:{F+S'F\u001fV#vlU#D\u001f:#5\u000b\t\u0005\b7\u0002\u0011\r\u0011\"\u0003U\u0003Q\u0011ViR%T)J\u000bE+S(O?J+EKU%F'\"1Q\f\u0001Q\u0001\nU\u000bQCU#H\u0013N#&+\u0011+J\u001f:{&+\u0012+S\u0013\u0016\u001b\u0006\u0005C\u0004`\u0001\t\u0007I\u0011\u00021\u0002\u0011\u0015tG\r]8j]R,\u0012!\u0019\t\u0004E.lW\"A2\u000b\u0005\u0011,\u0017AB1u_6L7M\u0003\u0002gO\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005!L\u0017\u0001B;uS2T\u0011A[\u0001\u0005U\u00064\u0018-\u0003\u0002mG\ny\u0011\t^8nS\u000e\u0014VMZ3sK:\u001cW\r\u0005\u0002\u001e]&\u0011qN\b\u0002\u000f%B\u001cWI\u001c3q_&tGOU3g\u0011\u0019\t\b\u0001)A\u0005C\u0006IQM\u001c3q_&tG\u000f\t\u0005\bg\u0002\u0011\r\u0011\"\u0003u\u0003\u0015\t\u0007\u000f]%e+\u0005)\bc\u00012lO!1q\u000f\u0001Q\u0001\nU\fa!\u00199q\u0013\u0012\u0004\u0003bB=\u0001\u0005\u0004%IA_\u0001\u000be\u0016<\u0017n\u001d;fe\u0016$W#A>\u0011\u0005\td\u0018BA?d\u00055\tEo\\7jG\n{w\u000e\\3b]\"1q\u0010\u0001Q\u0001\nm\f1B]3hSN$XM]3eA\u00191\u00111\u0001\u0001\u0005\u0003\u000b\u0011ab\u00117jK:$XI\u001c3q_&tGo\u0005\u0004\u0002\u00025\t9a\u0005\t\u0004;\u0005%\u0011bAA\u0006=\t)B\u000b\u001b:fC\u0012\u001c\u0016MZ3Sa\u000e,e\u000e\u001a9pS:$\bB\u0003\u000e\u0002\u0002\t\u0015\r\u0011\"\u0011\u0002\u0010U\tA\u0004\u0003\u0006\u0002\u0014\u0005\u0005!\u0011!Q\u0001\nq\tqA\u001d9d\u000b:4\b\u0005C\u0004B\u0003\u0003!\t!a\u0006\u0015\t\u0005e\u0011Q\u0004\t\u0005\u00037\t\t!D\u0001\u0001\u0011\u0019Q\u0012Q\u0003a\u00019!Q\u0011\u0011EA\u0001\u0001\u0004%I!a\t\u0002\r5\f7\u000f^3s+\t\t)\u0003\u0005\u0003\u000f\u0003Oi\u0017bAA\u0015\u001f\t1q\n\u001d;j_:D!\"!\f\u0002\u0002\u0001\u0007I\u0011BA\u0018\u0003)i\u0017m\u001d;fe~#S-\u001d\u000b\u0005\u0003c\t9\u0004E\u0002\u000f\u0003gI1!!\u000e\u0010\u0005\u0011)f.\u001b;\t\u0015\u0005e\u00121FA\u0001\u0002\u0004\t)#A\u0002yIEB\u0011\"!\u0010\u0002\u0002\u0001\u0006K!!\n\u0002\u000f5\f7\u000f^3sA!Q\u0011\u0011IA\u0001\u0001\u0004%I!a\u0011\u0002'\u0005d'/Z1es\u0012K7oY8o]\u0016\u001cG/\u001a3\u0016\u0005\u0005\u0015\u0003c\u0001\b\u0002H%\u0019\u0011\u0011J\b\u0003\u000f\t{w\u000e\\3b]\"Q\u0011QJA\u0001\u0001\u0004%I!a\u0014\u0002/\u0005d'/Z1es\u0012K7oY8o]\u0016\u001cG/\u001a3`I\u0015\fH\u0003BA\u0019\u0003#B!\"!\u000f\u0002L\u0005\u0005\t\u0019AA#\u0011%\t)&!\u0001!B\u0013\t)%\u0001\u000bbYJ,\u0017\rZ=ESN\u001cwN\u001c8fGR,G\r\t\u0005\n\u00033\n\tA1A\u0005\ni\f1\"\u00197sK\u0006$\u0017\u0010R3bI\"A\u0011QLA\u0001A\u0003%10\u0001\u0007bYJ,\u0017\rZ=EK\u0006$\u0007\u0005\u0003\u0006\u0002b\u0005\u0005!\u0019!C\u0005\u0003G\nQC]3hSN$XM]'bgR,'OR;ukJ,7/\u0006\u0002\u0002fA!!m[A4!\u0011qQ%!\u001b1\t\u0005-\u0014q\u000f\t\u0007\u0003[\ny'a\u001d\u000e\u0003\u0015L1!!\u001df\u0005\u00191U\u000f^;sKB!\u0011QOA<\u0019\u0001!A\"!\u001f\u0002|\u0005\u0005\t\u0011!B\u0001\u0003\u007f\u00121a\u0018\u00132\u0011%\ti(!\u0001!\u0002\u0013\t)'\u0001\fsK\u001eL7\u000f^3s\u001b\u0006\u001cH/\u001a:GkR,(/Z:!#\u0011\t\t)a\"\u0011\u00079\t\u0019)C\u0002\u0002\u0006>\u0011qAT8uQ&tw\rE\u0002\u000f\u0003\u0013K1!a#\u0010\u0005\r\te.\u001f\u0005\u000b\u0003\u001f\u000b\tA1A\u0005\n\u0005E\u0015A\u0006:fO&\u001cHO]1uS>t'+\u001a;ssRKW.\u001a:\u0016\u0005\u0005M\u0005\u0003\u00022l\u0003+\u0003D!a&\u0002 B1\u0011QNAM\u0003;K1!a'f\u0005=\u00196\r[3ek2,GMR;ukJ,\u0007\u0003BA;\u0003?#A\"!)\u0002$\u0006\u0005\t\u0011!B\u0001\u0003\u007f\u00121a\u0018\u00133\u0011%\t)+!\u0001!\u0002\u0013\t\u0019*A\fsK\u001eL7\u000f\u001e:bi&|gNU3uef$\u0016.\\3sA!Q\u0011\u0011VA\u0001\u0005\u0004%I!a+\u00021I,w-[:uKJl\u0015m\u001d;feRC'/Z1e!>|G.\u0006\u0002\u0002.B!\u0011QNAX\u0013\r\t\t,\u001a\u0002\u0013)\"\u0014X-\u00193Q_>dW\t_3dkR|'\u000fC\u0005\u00026\u0006\u0005\u0001\u0015!\u0003\u0002.\u0006I\"/Z4jgR,'/T1ti\u0016\u0014H\u000b\u001b:fC\u0012\u0004vn\u001c7!\u0011)\tI,!\u0001C\u0002\u0013%\u00111X\u0001\u0018e\u0016<\u0017n\u001d;sCRLwN\u001c*fiJLH\u000b\u001b:fC\u0012,\"!!0\u0011\t\u00055\u0014qX\u0005\u0004\u0003\u0003,'\u0001G*dQ\u0016$W\u000f\\3e\u000bb,7-\u001e;peN+'O^5dK\"I\u0011QYA\u0001A\u0003%\u0011QX\u0001\u0019e\u0016<\u0017n\u001d;sCRLwN\u001c*fiJLH\u000b\u001b:fC\u0012\u0004\u0003\u0002CAe\u0003\u0003!\t%a3\u0002\u000f=t7\u000b^1siR\u0011\u0011\u0011\u0007\u0005\t\u0003\u001f\f\t\u0001\"\u0003\u0002R\u0006)BO]=SK\u001eL7\u000f^3s\u00032dW*Y:uKJ\u001cHCAAj!\u0011qQ%!61\t\u0005]\u00171\u001c\t\u0007\u0003[\ny'!7\u0011\t\u0005U\u00141\u001c\u0003\r\u0003;\fi-!A\u0001\u0002\u000b\u0005\u0011q\u0010\u0002\u0004?\u0012\u001a\u0004\u0002CAq\u0003\u0003!I!a9\u0002%I,w-[:uKJ<\u0016\u000e\u001e5NCN$XM\u001d\u000b\u0005\u0003c\t)\u000fC\u0004\u0002h\u0006}\u0007\u0019A+\u0002\u00119$\bNU3uefD\u0001\"a;\u0002\u0002\u0011%\u0011Q^\u0001\rg\u0016tG\rV8NCN$XM\u001d\u000b\u0005\u0003c\ty\u000f\u0003\u0005\u0002r\u0006%\b\u0019AAD\u0003\u001diWm]:bO\u0016D\u0001\"!>\u0002\u0002\u0011%\u0011q_\u0001\u0011SN\u0004vn]:jE2,W*Y:uKJ$B!!\u0012\u0002z\"9\u00111`Az\u0001\u0004i\u0015!\u0004:f[>$X-\u00113ee\u0016\u001c8\u000f\u0003\u0005\u0002\u0000\u0006\u0005A\u0011\tB\u0001\u0003\u001d\u0011XmY3jm\u0016,\"Aa\u0001\u0011\u000f9\u0011)!a\"\u00022%\u0019!qA\b\u0003\u001fA\u000b'\u000f^5bY\u001a+hn\u0019;j_:D\u0001Ba\u0003\u0002\u0002\u0011\u0005#QB\u0001\u0010e\u0016\u001cW-\u001b<f\u0003:$'+\u001a9msR!!1\u0001B\b\u0011!\u0011\tB!\u0003A\u0002\tM\u0011aB2p]R,\u0007\u0010\u001e\t\u0004;\tU\u0011b\u0001B\f=\tq!\u000b]2DC2d7i\u001c8uKb$\b\u0002\u0003B\u000e\u0003\u0003!IA!\b\u0002!\u0005\u001c8.\u00118e%\u0016\u0004H._!ts:\u001cW\u0003\u0002B\u0010\u0005[!\u0002\"!\r\u0003\"\t\u0015\"q\u0005\u0005\b\u0005G\u0011I\u00021\u0001n\u0003-)g\u000e\u001a9pS:$(+\u001a4\t\u0011\tE!\u0011\u0004a\u0001\u0005'A\u0001B!\u000b\u0003\u001a\u0001\u0007!1F\u0001\u0004[N<\u0007\u0003BA;\u0005[!\u0001Ba\f\u0003\u001a\t\u0007\u0011q\u0010\u0002\u0002)\"A!1GA\u0001\t\u0003\u0012)$\u0001\bp]\u0012K7oY8o]\u0016\u001cG/\u001a3\u0015\t\u0005E\"q\u0007\u0005\b\u0005s\u0011\t\u00041\u0001N\u0003\u001d\tG\r\u001a:fgND\u0001B!\u0010\u0002\u0002\u0011\u0005#qH\u0001\u000f_:tU\r^<pe.,%O]8s)\u0019\t\tD!\u0011\u0003^!A!1\tB\u001e\u0001\u0004\u0011)%A\u0003dCV\u001cX\r\u0005\u0003\u0003H\t]c\u0002\u0002B%\u0005'rAAa\u0013\u0003R5\u0011!Q\n\u0006\u0004\u0005\u001fZ\u0012A\u0002\u001fs_>$h(C\u0001\u0011\u0013\r\u0011)fD\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\u0011IFa\u0017\u0003\u0013QC'o\\<bE2,'b\u0001B+\u001f!9!\u0011\bB\u001e\u0001\u0004i\u0005\u0002\u0003B1\u0003\u0003!\t!a3\u0002!5\f'o\u001b#jg\u000e|gN\\3di\u0016$\u0007\u0002\u0003B3\u0003\u0003!\tAa\u001a\u0002\u00115\f'o\u001b#fC\u0012$B!!\r\u0003j!9!1\u000eB2\u0001\u00049\u0013A\u0002:fCN|g\u000e\u0003\u0005\u0003p\u0005\u0005A\u0011IAf\u0003\u0019ygn\u0015;pa\"9!1\u000f\u0001\u0005\u0002\u0005-\u0017!B:uCJ$\bb\u0002B<\u0001\u0011\u0005\u00111Z\u0001\u0005gR|\u0007\u000fC\u0004\u0003|\u0001!\tA! \u0002+I,\u0017/^3tiR{G/\u00197Fq\u0016\u001cW\u000f^8sgR!!q\u0010BD!\u0019\u0011\tI!\"\u0002F5\u0011!1\u0011\u0006\u0003M>IA!!\u001d\u0003\u0004\"9!\u0011\u0012B=\u0001\u0004)\u0016A\u0004:fcV,7\u000f^3e)>$\u0018\r\u001c\u0005\b\u0005\u001b\u0003A\u0011\u0001BH\u00035Y\u0017\u000e\u001c7Fq\u0016\u001cW\u000f^8sgR!!q\u0010BI\u0011!\u0011\u0019Ja#A\u0002\tU\u0015aC3yK\u000e,Ho\u001c:JIN\u0004RAa\u0012\u0003\u0018\u001eJAA!'\u0003\\\t\u00191+Z9"
)
public class StandaloneAppClient implements Logging {
   private final RpcEnv rpcEnv;
   public final ApplicationDescription org$apache$spark$deploy$client$StandaloneAppClient$$appDescription;
   public final StandaloneAppClientListener org$apache$spark$deploy$client$StandaloneAppClient$$listener;
   private final SparkConf conf;
   private final RpcAddress[] org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses;
   private final int org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_TIMEOUT_SECONDS;
   private final int org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_RETRIES;
   private final AtomicReference<RpcEndpointRef> endpoint;
   private final AtomicReference<String> org$apache$spark$deploy$client$StandaloneAppClient$$appId;
   private final AtomicBoolean org$apache$spark$deploy$client$StandaloneAppClient$$registered;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

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

   public RpcAddress[] org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses() {
      return this.org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses;
   }

   public int org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_TIMEOUT_SECONDS() {
      return this.org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_TIMEOUT_SECONDS;
   }

   public int org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_RETRIES() {
      return this.org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_RETRIES;
   }

   private AtomicReference<RpcEndpointRef> endpoint() {
      return this.endpoint;
   }

   public AtomicReference<String> org$apache$spark$deploy$client$StandaloneAppClient$$appId() {
      return this.org$apache$spark$deploy$client$StandaloneAppClient$$appId;
   }

   public AtomicBoolean org$apache$spark$deploy$client$StandaloneAppClient$$registered() {
      return this.org$apache$spark$deploy$client$StandaloneAppClient$$registered;
   }

   public void start() {
      this.endpoint().set(this.rpcEnv.setupEndpoint("AppClient", new ClientEndpoint(this.rpcEnv)));
   }

   public void stop() {
      if (this.endpoint().get() != null) {
         try {
            RpcTimeout timeout = RpcUtils$.MODULE$.askRpcTimeout(this.conf);
            timeout.awaitResult(((RpcEndpointRef)this.endpoint().get()).ask(DeployMessages.StopAppClient$.MODULE$, ClassTag$.MODULE$.Boolean()));
         } catch (TimeoutException var3) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Stop request to Master timed out; it may already be shut down.";
               }
            });
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         this.endpoint().set((Object)null);
      }

   }

   public Future<Object> requestTotalExecutors(int requestedTotal) {
      Future var10000;
      if (this.endpoint().get() != null && this.org$apache$spark$deploy$client$StandaloneAppClient$$appId().get() != null) {
         var10000 = ((RpcEndpointRef)this.endpoint().get()).ask(new DeployMessages.RequestExecutors((String)this.org$apache$spark$deploy$client$StandaloneAppClient$$appId().get(), requestedTotal), ClassTag$.MODULE$.Boolean());
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempted to request executors before driver fully initialized.";
            }
         });
         var10000 = Future$.MODULE$.successful(BoxesRunTime.boxToBoolean(false));
      }

      return var10000;
   }

   public Future<Object> killExecutors(Seq<String> executorIds) {
      Future var10000;
      if (this.endpoint().get() != null && this.org$apache$spark$deploy$client$StandaloneAppClient$$appId().get() != null) {
         var10000 = ((RpcEndpointRef)this.endpoint().get()).ask(new DeployMessages.KillExecutors((String)this.org$apache$spark$deploy$client$StandaloneAppClient$$appId().get(), executorIds), ClassTag$.MODULE$.Boolean());
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempted to kill executors before driver fully initialized.";
            }
         });
         var10000 = Future$.MODULE$.successful(BoxesRunTime.boxToBoolean(false));
      }

      return var10000;
   }

   public StandaloneAppClient(RpcEnv rpcEnv, String[] masterUrls, ApplicationDescription appDescription, StandaloneAppClientListener listener, SparkConf conf) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$deploy$client$StandaloneAppClient$$appDescription = appDescription;
      this.org$apache$spark$deploy$client$StandaloneAppClient$$listener = listener;
      this.conf = conf;
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses = (RpcAddress[])Predef$.MODULE$.refArrayOps((Object[])masterUrls).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RpcAddress apply(String x$1) {
            return RpcAddress$.MODULE$.fromSparkURL(x$1);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(RpcAddress.class)));
      this.org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_TIMEOUT_SECONDS = 20;
      this.org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_RETRIES = 3;
      this.endpoint = new AtomicReference();
      this.org$apache$spark$deploy$client$StandaloneAppClient$$appId = new AtomicReference();
      this.org$apache$spark$deploy$client$StandaloneAppClient$$registered = new AtomicBoolean(false);
   }

   public class ClientEndpoint implements ThreadSafeRpcEndpoint, Logging {
      private final RpcEnv rpcEnv;
      private Option<RpcEndpointRef> org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master;
      private boolean org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected;
      private final AtomicBoolean alreadyDead;
      private final AtomicReference<java.util.concurrent.Future<?>[]> org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures;
      private final AtomicReference<ScheduledFuture<?>> registrationRetryTimer;
      private final ThreadPoolExecutor org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool;
      private final ScheduledExecutorService registrationRetryThread;
      // $FF: synthetic field
      public final StandaloneAppClient $outer;
      private transient Logger org$apache$spark$internal$Logging$$log_;

      public Logger org$apache$spark$internal$Logging$$log_() {
         return this.org$apache$spark$internal$Logging$$log_;
      }

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

      public void onError(Throwable cause) {
         RpcEndpoint$class.onError(this, cause);
      }

      public void onConnected(RpcAddress remoteAddress) {
         RpcEndpoint$class.onConnected(this, remoteAddress);
      }

      public final void stop() {
         RpcEndpoint$class.stop(this);
      }

      public RpcEnv rpcEnv() {
         return this.rpcEnv;
      }

      public Option<RpcEndpointRef> org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master() {
         return this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master;
      }

      public void org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master_$eq(Option<RpcEndpointRef> x$1) {
         this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master = x$1;
      }

      private boolean org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected() {
         return this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected;
      }

      public void org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected_$eq(boolean x$1) {
         this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected = x$1;
      }

      private AtomicBoolean alreadyDead() {
         return this.alreadyDead;
      }

      public AtomicReference<java.util.concurrent.Future<?>[]> org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures() {
         return this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures;
      }

      private AtomicReference<ScheduledFuture<?>> registrationRetryTimer() {
         return this.registrationRetryTimer;
      }

      public ThreadPoolExecutor org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool() {
         return this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool;
      }

      private ScheduledExecutorService registrationRetryThread() {
         return this.registrationRetryThread;
      }

      public void onStart() {
         try {
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerWithMaster(1);
         } catch (Exception var2) {
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Failed to connect to master";
               }
            }, var2);
            this.markDisconnected();
            this.stop();
         }

      }

      private java.util.concurrent.Future<?>[] tryRegisterAllMasters() {
         return (java.util.concurrent.Future[])Predef$.MODULE$.refArrayOps(this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ClientEndpoint $outer;

            public final java.util.concurrent.Future<?> apply(RpcAddress masterAddress) {
               return this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool().submit(new Runnable(this, masterAddress) {
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final RpcAddress masterAddress$1;

                  public void run() {
                     label46:
                     try {
                        if (this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$registered().get()) {
                           return;
                        }

                        this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().logInfo(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Connecting to master ").append((Object)this.$outer.masterAddress$1.toSparkURL()).append((Object)"...").toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        RpcEndpointRef masterRef = this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().rpcEnv().setupEndpointRef(this.masterAddress$1, Master$.MODULE$.ENDPOINT_NAME());
                        masterRef.send(new DeployMessages.RegisterApplication(this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appDescription, this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().self()));
                     } catch (Throwable var8) {
                        if (var8 instanceof InterruptedException) {
                           BoxedUnit var9 = BoxedUnit.UNIT;
                        } else {
                           Option var4 = NonFatal$.MODULE$.unapply(var8);
                           if (var4.isEmpty()) {
                              throw var8;
                           }

                           Throwable e = (Throwable)var4.get();
                           this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer().logWarning(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to connect to master ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.masterAddress$1}));
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           }, e);
                           BoxedUnit var3 = BoxedUnit.UNIT;
                        }
                        break label46;
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.masterAddress$1 = masterAddress$1;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public ClientEndpoint org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(java.util.concurrent.Future.class)));
      }

      public void org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerWithMaster(int nthRetry) {
         this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures().set(this.tryRegisterAllMasters());
         this.registrationRetryTimer().set(this.registrationRetryThread().schedule(new Runnable(this, nthRetry) {
            // $FF: synthetic field
            private final ClientEndpoint $outer;
            private final int nthRetry$1;

            public void run() {
               if (this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$registered().get()) {
                  Predef$.MODULE$.refArrayOps(this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures().get()).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(java.util.concurrent.Future<?> x$2) {
                        return x$2.cancel(true);
                     }
                  });
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool().shutdownNow();
               } else if (this.nthRetry$1 >= this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_RETRIES()) {
                  this.$outer.markDead("All masters are unresponsive! Giving up.");
               } else {
                  Predef$.MODULE$.refArrayOps(this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures().get()).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(java.util.concurrent.Future<?> x$3) {
                        return x$3.cancel(true);
                     }
                  });
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerWithMaster(this.nthRetry$1 + 1);
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.nthRetry$1 = nthRetry$1;
               }
            }
         }, (long)this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$REGISTRATION_TIMEOUT_SECONDS(), TimeUnit.SECONDS));
      }

      public void org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$sendToMaster(Object message) {
         Option var2 = this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master();
         if (var2 instanceof Some) {
            Some var3 = (Some)var2;
            RpcEndpointRef masterRef = (RpcEndpointRef)var3.x();
            masterRef.send(message);
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var2)) {
               throw new MatchError(var2);
            }

            this.logWarning(new Serializable(this, message) {
               public static final long serialVersionUID = 0L;
               private final Object message$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop ", " because has not yet connected to master"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.message$1}));
               }

               public {
                  this.message$1 = message$1;
               }
            });
            BoxedUnit var6 = BoxedUnit.UNIT;
         }

      }

      private boolean isPossibleMaster(RpcAddress remoteAddress) {
         return Predef$.MODULE$.refArrayOps(this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses()).contains(remoteAddress);
      }

      public PartialFunction<Object, BoxedUnit> receive() {
         return new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ClientEndpoint $outer;

            public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
               Object var7;
               if (x1 instanceof DeployMessages.RegisteredApplication) {
                  DeployMessages.RegisteredApplication var4 = (DeployMessages.RegisteredApplication)x1;
                  String appId_ = var4.appId();
                  RpcEndpointRef masterRef = var4.master();
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId().set(appId_);
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$registered().set(true);
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master_$eq(new Some(masterRef));
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.connected((String)this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId().get());
                  var7 = BoxedUnit.UNIT;
               } else if (x1 instanceof DeployMessages.ApplicationRemoved) {
                  DeployMessages.ApplicationRemoved var8 = (DeployMessages.ApplicationRemoved)x1;
                  String message = var8.message();
                  this.$outer.markDead((new StringOps(Predef$.MODULE$.augmentString("Master removed our application: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{message})));
                  this.$outer.stop();
                  var7 = BoxedUnit.UNIT;
               } else {
                  if (x1 instanceof DeployMessages.ExecutorAdded) {
                     DeployMessages.ExecutorAdded var10 = (DeployMessages.ExecutorAdded)x1;
                     int id = var10.id();
                     String workerId = var10.workerId();
                     String hostPort = var10.hostPort();
                     int cores = var10.cores();
                     int memory = var10.memory();
                     if (workerId != null && hostPort != null) {
                        String fullId = (new StringBuilder()).append((Object)Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId()), "/")).append((Object)BoxesRunTime.boxToInteger(id)).toString();
                        this.$outer.logInfo(new Serializable(this, fullId, workerId, hostPort, cores) {
                           public static final long serialVersionUID = 0L;
                           private final String fullId$1;
                           private final String x9$1;
                           private final String x10$1;
                           private final int x11$1;

                           public final String apply() {
                              return (new StringOps(Predef$.MODULE$.augmentString("Executor added: %s on %s (%s) with %d core(s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.fullId$1, this.x9$1, this.x10$1, BoxesRunTime.boxToInteger(this.x11$1)}));
                           }

                           public {
                              this.fullId$1 = fullId$1;
                              this.x9$1 = x9$1;
                              this.x10$1 = x10$1;
                              this.x11$1 = x11$1;
                           }
                        });
                        this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.executorAdded(fullId, workerId, hostPort, cores, memory);
                        var7 = BoxedUnit.UNIT;
                        return (B1)var7;
                     }
                  }

                  if (x1 instanceof DeployMessages.ExecutorUpdated) {
                     DeployMessages.ExecutorUpdated var22 = (DeployMessages.ExecutorUpdated)x1;
                     int id = var22.id();
                     Enumeration.Value state = var22.state();
                     Option message = var22.message();
                     Option exitStatus = var22.exitStatus();
                     boolean workerLost = var22.workerLost();
                     String fullId = (new StringBuilder()).append((Object)Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId()), "/")).append((Object)BoxesRunTime.boxToInteger(id)).toString();
                     String messageText = (String)message.map(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply(String s) {
                           return (new StringBuilder()).append((Object)" (").append((Object)s).append((Object)")").toString();
                        }
                     }).getOrElse(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "";
                        }
                     });
                     this.$outer.logInfo(new Serializable(this, state, fullId, messageText) {
                        public static final long serialVersionUID = 0L;
                        private final Enumeration.Value state$1;
                        private final String fullId$2;
                        private final String messageText$1;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Executor updated: %s is now %s%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.fullId$2, this.state$1, this.messageText$1}));
                        }

                        public {
                           this.state$1 = state$1;
                           this.fullId$2 = fullId$2;
                           this.messageText$1 = messageText$1;
                        }
                     });
                     BoxedUnit var10000;
                     if (ExecutorState$.MODULE$.isFinished(state)) {
                        this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.executorRemoved(fullId, (String)message.getOrElse(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "";
                           }
                        }), exitStatus, workerLost);
                        var10000 = BoxedUnit.UNIT;
                     } else {
                        var10000 = BoxedUnit.UNIT;
                     }

                     var7 = var10000;
                  } else if (x1 instanceof DeployMessages.WorkerRemoved) {
                     DeployMessages.WorkerRemoved var30 = (DeployMessages.WorkerRemoved)x1;
                     String id = var30.id();
                     String host = var30.host();
                     String message = var30.message();
                     this.$outer.logInfo(new Serializable(this, id, message) {
                        public static final long serialVersionUID = 0L;
                        private final String id$1;
                        private final String message$2;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Master removed worker %s: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1, this.message$2}));
                        }

                        public {
                           this.id$1 = id$1;
                           this.message$2 = message$2;
                        }
                     });
                     this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.workerRemoved(id, host, message);
                     var7 = BoxedUnit.UNIT;
                  } else if (x1 instanceof DeployMessages.MasterChanged) {
                     DeployMessages.MasterChanged var34 = (DeployMessages.MasterChanged)x1;
                     RpcEndpointRef masterRef = var34.master();
                     this.$outer.logInfo(new Serializable(this, masterRef) {
                        public static final long serialVersionUID = 0L;
                        private final RpcEndpointRef masterRef$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Master has changed, new master is at ").append((Object)this.masterRef$1.address().toSparkURL()).toString();
                        }

                        public {
                           this.masterRef$1 = masterRef$1;
                        }
                     });
                     this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master_$eq(new Some(masterRef));
                     this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected_$eq(false);
                     masterRef.send(new DeployMessages.MasterChangeAcknowledged((String)this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId().get()));
                     var7 = BoxedUnit.UNIT;
                  } else {
                     var7 = var2.apply(x1);
                  }
               }

               return (B1)var7;
            }

            public final boolean isDefinedAt(Object x1) {
               boolean var3;
               if (x1 instanceof DeployMessages.RegisteredApplication) {
                  var3 = true;
               } else if (x1 instanceof DeployMessages.ApplicationRemoved) {
                  var3 = true;
               } else {
                  if (x1 instanceof DeployMessages.ExecutorAdded) {
                     DeployMessages.ExecutorAdded var4 = (DeployMessages.ExecutorAdded)x1;
                     String workerId = var4.workerId();
                     String hostPort = var4.hostPort();
                     if (workerId != null && hostPort != null && true) {
                        var3 = true;
                        return var3;
                     }
                  }

                  if (x1 instanceof DeployMessages.ExecutorUpdated) {
                     var3 = true;
                  } else if (x1 instanceof DeployMessages.WorkerRemoved) {
                     var3 = true;
                  } else if (x1 instanceof DeployMessages.MasterChanged) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }
               }

               return var3;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
      }

      public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
         return new Serializable(this, context) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ClientEndpoint $outer;
            private final RpcCallContext context$1;

            public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
               Object var4;
               if (DeployMessages.StopAppClient$.MODULE$.equals(x2)) {
                  this.$outer.markDead("Application has been stopped.");
                  this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$sendToMaster(new DeployMessages.UnregisterApplication((String)this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$appId().get()));
                  this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                  this.$outer.stop();
                  var4 = BoxedUnit.UNIT;
               } else if (x2 instanceof DeployMessages.RequestExecutors) {
                  DeployMessages.RequestExecutors var5 = (DeployMessages.RequestExecutors)x2;
                  Option var6 = this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master();
                  if (var6 instanceof Some) {
                     Some var7 = (Some)var6;
                     RpcEndpointRef m = (RpcEndpointRef)var7.x();
                     this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$askAndReplyAsync(m, this.context$1, var5);
                     BoxedUnit var9 = BoxedUnit.UNIT;
                  } else {
                     if (!None$.MODULE$.equals(var6)) {
                        throw new MatchError(var6);
                     }

                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Attempted to request executors before registering with Master.";
                        }
                     });
                     this.context$1.reply(BoxesRunTime.boxToBoolean(false));
                     BoxedUnit var15 = BoxedUnit.UNIT;
                  }

                  var4 = BoxedUnit.UNIT;
               } else if (x2 instanceof DeployMessages.KillExecutors) {
                  DeployMessages.KillExecutors var10 = (DeployMessages.KillExecutors)x2;
                  Option var11 = this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master();
                  if (var11 instanceof Some) {
                     Some var12 = (Some)var11;
                     RpcEndpointRef m = (RpcEndpointRef)var12.x();
                     this.$outer.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$askAndReplyAsync(m, this.context$1, var10);
                     BoxedUnit var14 = BoxedUnit.UNIT;
                  } else {
                     if (!None$.MODULE$.equals(var11)) {
                        throw new MatchError(var11);
                     }

                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Attempted to kill executors before registering with Master.";
                        }
                     });
                     this.context$1.reply(BoxesRunTime.boxToBoolean(false));
                     BoxedUnit var16 = BoxedUnit.UNIT;
                  }

                  var4 = BoxedUnit.UNIT;
               } else {
                  var4 = var2.apply(x2);
               }

               return (B1)var4;
            }

            public final boolean isDefinedAt(Object x2) {
               boolean var3;
               if (DeployMessages.StopAppClient$.MODULE$.equals(x2)) {
                  var3 = true;
               } else if (x2 instanceof DeployMessages.RequestExecutors) {
                  var3 = true;
               } else if (x2 instanceof DeployMessages.KillExecutors) {
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

      public <T> void org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$askAndReplyAsync(RpcEndpointRef endpointRef, RpcCallContext context, T msg) {
         endpointRef.ask(msg, ClassTag$.MODULE$.Boolean()).andThen(new Serializable(this, context) {
            public static final long serialVersionUID = 0L;
            private final RpcCallContext context$2;

            public final <A1 extends Try<Object>, B1> B1 applyOrElse(A1 x3, Function1<A1, B1> var2) {
               boolean var3 = false;
               Failure var4 = null;
               Object var8;
               if (x3 instanceof Success) {
                  Success var6 = (Success)x3;
                  boolean b = BoxesRunTime.unboxToBoolean(var6.value());
                  this.context$2.reply(BoxesRunTime.boxToBoolean(b));
                  var8 = BoxedUnit.UNIT;
               } else {
                  if (x3 instanceof Failure) {
                     var3 = true;
                     var4 = (Failure)x3;
                     Throwable ie = var4.exception();
                     if (ie instanceof InterruptedException) {
                        var8 = BoxedUnit.UNIT;
                        return (B1)var8;
                     }
                  }

                  if (var3) {
                     Throwable var10 = var4.exception();
                     Option var11 = NonFatal$.MODULE$.unapply(var10);
                     if (!var11.isEmpty()) {
                        Throwable t = (Throwable)var11.get();
                        this.context$2.sendFailure(t);
                        var8 = BoxedUnit.UNIT;
                        return (B1)var8;
                     }
                  }

                  var8 = var2.apply(x3);
               }

               return (B1)var8;
            }

            public final boolean isDefinedAt(Try<Object> x3) {
               boolean var2 = false;
               Failure var3 = null;
               boolean var5;
               if (x3 instanceof Success) {
                  var5 = true;
               } else {
                  if (x3 instanceof Failure) {
                     var2 = true;
                     var3 = (Failure)x3;
                     Throwable ie = var3.exception();
                     if (ie instanceof InterruptedException) {
                        var5 = true;
                        return var5;
                     }
                  }

                  if (var2) {
                     Throwable var7 = var3.exception();
                     Option var8 = NonFatal$.MODULE$.unapply(var7);
                     if (!var8.isEmpty()) {
                        var5 = true;
                        return var5;
                     }
                  }

                  var5 = false;
               }

               return var5;
            }

            public {
               this.context$2 = context$2;
            }
         }, ThreadUtils$.MODULE$.sameThread());
      }

      public void onDisconnected(RpcAddress address) {
         if (this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master().exists(new Serializable(this, address) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress address$1;

            public final boolean apply(RpcEndpointRef x$4) {
               boolean var3;
               label23: {
                  RpcAddress var10000 = x$4.address();
                  RpcAddress var2 = this.address$1;
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label23;
                     }
                  } else if (var10000.equals(var2)) {
                     break label23;
                  }

                  var3 = false;
                  return var3;
               }

               var3 = true;
               return var3;
            }

            public {
               this.address$1 = address$1;
            }
         })) {
            this.logWarning(new Serializable(this, address) {
               public static final long serialVersionUID = 0L;
               private final RpcAddress address$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Connection to ", " failed; waiting for master to reconnect..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.address$1}));
               }

               public {
                  this.address$1 = address$1;
               }
            });
            this.markDisconnected();
         }

      }

      public void onNetworkError(Throwable cause, RpcAddress address) {
         if (this.isPossibleMaster(address)) {
            this.logWarning(new Serializable(this, cause, address) {
               public static final long serialVersionUID = 0L;
               private final Throwable cause$1;
               private final RpcAddress address$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not connect to ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.address$2, this.cause$1}));
               }

               public {
                  this.cause$1 = cause$1;
                  this.address$2 = address$2;
               }
            });
         }

      }

      public void markDisconnected() {
         if (!this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected()) {
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.disconnected();
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected_$eq(true);
         }

      }

      public void markDead(String reason) {
         if (!this.alreadyDead().get()) {
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer().org$apache$spark$deploy$client$StandaloneAppClient$$listener.dead(reason);
            this.alreadyDead().set(true);
         }

      }

      public void onStop() {
         if (this.registrationRetryTimer().get() == null) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            BoxesRunTime.boxToBoolean(((java.util.concurrent.Future)this.registrationRetryTimer().get()).cancel(true));
         }

         this.registrationRetryThread().shutdownNow();
         Predef$.MODULE$.refArrayOps(this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures().get()).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(java.util.concurrent.Future<?> x$5) {
               return x$5.cancel(true);
            }
         });
         this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool().shutdownNow();
      }

      // $FF: synthetic method
      public StandaloneAppClient org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$$outer() {
         return this.$outer;
      }

      public ClientEndpoint(RpcEnv rpcEnv) {
         this.rpcEnv = rpcEnv;
         if (StandaloneAppClient.this == null) {
            throw null;
         } else {
            this.$outer = StandaloneAppClient.this;
            super();
            RpcEndpoint$class.$init$(this);
            Logging$class.$init$(this);
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$master = None$.MODULE$;
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$alreadyDisconnected = false;
            this.alreadyDead = new AtomicBoolean(false);
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterFutures = new AtomicReference();
            this.registrationRetryTimer = new AtomicReference();
            this.org$apache$spark$deploy$client$StandaloneAppClient$ClientEndpoint$$registerMasterThreadPool = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("appclient-register-master-threadpool", StandaloneAppClient.this.org$apache$spark$deploy$client$StandaloneAppClient$$masterRpcAddresses().length, ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3());
            this.registrationRetryThread = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("appclient-registration-retry-thread");
         }
      }
   }
}
