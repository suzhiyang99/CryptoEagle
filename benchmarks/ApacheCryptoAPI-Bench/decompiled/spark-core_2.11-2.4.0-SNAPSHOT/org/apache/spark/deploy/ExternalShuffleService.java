package org.apache.spark.deploy;

import java.io.File;
import java.util.List;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.metrics.MetricsSystem$;
import org.apache.spark.network.TransportContext;
import org.apache.spark.network.crypto.AuthServerBootstrap;
import org.apache.spark.network.netty.SparkTransportConf$;
import org.apache.spark.network.server.TransportServer;
import org.apache.spark.network.shuffle.ExternalShuffleBlockHandler;
import org.apache.spark.network.util.TransportConf;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=e!B\u0001\u0003\u0001\tQ!AF#yi\u0016\u0014h.\u00197TQV4g\r\\3TKJ4\u0018nY3\u000b\u0005\r!\u0011A\u00023fa2|\u0017P\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I)R\"A\n\u000b\u0005Q!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005Y\u0019\"a\u0002'pO\u001eLgn\u001a\u0005\t1\u0001\u0011\t\u0011)A\u00055\u0005I1\u000f]1sW\u000e{gNZ\u0002\u0001!\tYB$D\u0001\u0005\u0013\tiBAA\u0005Ta\u0006\u00148nQ8oM\"Aq\u0004\u0001B\u0001B\u0003%\u0001%A\btK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s!\tY\u0012%\u0003\u0002#\t\ty1+Z2ve&$\u00180T1oC\u001e,'\u000fC\u0003%\u0001\u0011\u0005Q%\u0001\u0004=S:LGO\u0010\u000b\u0004M!J\u0003CA\u0014\u0001\u001b\u0005\u0011\u0001\"\u0002\r$\u0001\u0004Q\u0002\"B\u0010$\u0001\u0004\u0001\u0003bB\u0016\u0001\u0005\u0004%\t\u0002L\u0001\u0014[\u0006\u001cH/\u001a:NKR\u0014\u0018nY:TsN$X-\\\u000b\u0002[A\u0011a&M\u0007\u0002_)\u0011\u0001\u0007B\u0001\b[\u0016$(/[2t\u0013\t\u0011tFA\u0007NKR\u0014\u0018nY:TsN$X-\u001c\u0005\u0007i\u0001\u0001\u000b\u0011B\u0017\u0002)5\f7\u000f^3s\u001b\u0016$(/[2t'f\u001cH/Z7!\u0011\u001d1\u0004A1A\u0005\n]\nq!\u001a8bE2,G-F\u00019!\ta\u0011(\u0003\u0002;\u001b\t9!i\\8mK\u0006t\u0007B\u0002\u001f\u0001A\u0003%\u0001(\u0001\u0005f]\u0006\u0014G.\u001a3!\u0011\u001dq\u0004A1A\u0005\n}\nA\u0001]8siV\t\u0001\t\u0005\u0002\r\u0003&\u0011!)\u0004\u0002\u0004\u0013:$\bB\u0002#\u0001A\u0003%\u0001)A\u0003q_J$\b\u0005C\u0004G\u0001\t\u0007I\u0011B$\u0002\u001bQ\u0014\u0018M\\:q_J$8i\u001c8g+\u0005A\u0005CA%O\u001b\u0005Q%BA&M\u0003\u0011)H/\u001b7\u000b\u00055#\u0011a\u00028fi^|'o[\u0005\u0003\u001f*\u0013Q\u0002\u0016:b]N\u0004xN\u001d;D_:4\u0007BB)\u0001A\u0003%\u0001*\u0001\bue\u0006t7\u000f]8si\u000e{gN\u001a\u0011\t\u000fM\u0003!\u0019!C\u0005)\u0006a!\r\\8dW\"\u000bg\u000e\u001a7feV\tQ\u000b\u0005\u0002W36\tqK\u0003\u0002Y\u0019\u000691\u000f[;gM2,\u0017B\u0001.X\u0005m)\u0005\u0010^3s]\u0006d7\u000b[;gM2,'\t\\8dW\"\u000bg\u000e\u001a7fe\"1A\f\u0001Q\u0001\nU\u000bQB\u00197pG.D\u0015M\u001c3mKJ\u0004\u0003b\u00020\u0001\u0005\u0004%IaX\u0001\u0011iJ\fgn\u001d9peR\u001cuN\u001c;fqR,\u0012\u0001\u0019\t\u0003C\nl\u0011\u0001T\u0005\u0003G2\u0013\u0001\u0003\u0016:b]N\u0004xN\u001d;D_:$X\r\u001f;\t\r\u0015\u0004\u0001\u0015!\u0003a\u0003E!(/\u00198ta>\u0014HoQ8oi\u0016DH\u000f\t\u0005\nO\u0002\u0001\r\u00111A\u0005\n!\faa]3sm\u0016\u0014X#A5\u0011\u0005)dW\"A6\u000b\u0005\u001dd\u0015BA7l\u0005=!&/\u00198ta>\u0014HoU3sm\u0016\u0014\b\"C8\u0001\u0001\u0004\u0005\r\u0011\"\u0003q\u0003)\u0019XM\u001d<fe~#S-\u001d\u000b\u0003cR\u0004\"\u0001\u0004:\n\u0005Ml!\u0001B+oSRDq!\u001e8\u0002\u0002\u0003\u0007\u0011.A\u0002yIEBaa\u001e\u0001!B\u0013I\u0017aB:feZ,'\u000f\t\u0005\bs\u0002\u0011\r\u0011\"\u0003{\u0003Q\u0019\b.\u001e4gY\u0016\u001cVM\u001d<jG\u0016\u001cv.\u001e:dKV\t1\u0010\u0005\u0002(y&\u0011QP\u0001\u0002\u001d\u000bb$XM\u001d8bYNCWO\u001a4mKN+'O^5dKN{WO]2f\u0011\u0019y\b\u0001)A\u0005w\u0006)2\u000f[;gM2,7+\u001a:wS\u000e,7k\\;sG\u0016\u0004\u0003bBA\u0002\u0001\u0011E\u0011QA\u0001\u0017]\u0016<8\u000b[;gM2,'\t\\8dW\"\u000bg\u000e\u001a7feR\u0019Q+a\u0002\t\u000f\u0005%\u0011\u0011\u0001a\u0001\u0011\u0006!1m\u001c8g\u0011\u001d\ti\u0001\u0001C\u0001\u0003\u001f\tab\u001d;beRLe-\u00128bE2,G\rF\u0001r\u0011\u001d\t\u0019\u0002\u0001C\u0001\u0003\u001f\tQa\u001d;beRDq!a\u0006\u0001\t\u0003\tI\"\u0001\nbaBd\u0017nY1uS>t'+Z7pm\u0016$GcA9\u0002\u001c!A\u0011QDA\u000b\u0001\u0004\ty\"A\u0003baBLE\r\u0005\u0003\u0002\"\u0005\u001dbb\u0001\u0007\u0002$%\u0019\u0011QE\u0007\u0002\rA\u0013X\rZ3g\u0013\u0011\tI#a\u000b\u0003\rM#(/\u001b8h\u0015\r\t)#\u0004\u0005\b\u0003_\u0001A\u0011AA\b\u0003\u0011\u0019Ho\u001c9\b\u000f\u0005M\"\u0001#\u0001\u00026\u00051R\t\u001f;fe:\fGn\u00155vM\u001adWmU3sm&\u001cW\rE\u0002(\u0003o1a!\u0001\u0002\t\u0002\u0005e2\u0003BA\u001c\u0017EAq\u0001JA\u001c\t\u0003\ti\u0004\u0006\u0002\u00026!Yq-a\u000eA\u0002\u0003\u0007I\u0011BA!+\u00051\u0003bC8\u00028\u0001\u0007\t\u0019!C\u0005\u0003\u000b\"2!]A$\u0011!)\u00181IA\u0001\u0002\u00041\u0003bB<\u00028\u0001\u0006KA\n\u0015\u0005\u0003\u0013\ni\u0005E\u0002\r\u0003\u001fJ1!!\u0015\u000e\u0005!1x\u000e\\1uS2,\u0007BCA+\u0003o\u0011\r\u0011\"\u0003\u0002X\u00059!-\u0019:sS\u0016\u0014XCAA-!\u0011\tY&a\u001a\u000e\u0005\u0005u#\u0002BA0\u0003C\n!bY8oGV\u0014(/\u001a8u\u0015\rY\u00151\r\u0006\u0003\u0003K\nAA[1wC&!\u0011\u0011NA/\u00059\u0019u.\u001e8u\t><h\u000eT1uG\"D\u0011\"!\u001c\u00028\u0001\u0006I!!\u0017\u0002\u0011\t\f'O]5fe\u0002B\u0001\"!\u001d\u00028\u0011\u0005\u00111O\u0001\u0005[\u0006Lg\u000eF\u0002r\u0003kB\u0001\"a\u001e\u0002p\u0001\u0007\u0011\u0011P\u0001\u0005CJ<7\u000fE\u0003\r\u0003w\ny\"C\u0002\u0002~5\u0011Q!\u0011:sCfD\u0011\"!\u001d\u00028\u0011\u0005A!!!\u0015\u000bE\f\u0019)!\"\t\u0011\u0005]\u0014q\u0010a\u0001\u0003sB\u0001\"a\"\u0002\u0000\u0001\u0007\u0011\u0011R\u0001\u0012]\u0016<8\u000b[;gM2,7+\u001a:wS\u000e,\u0007C\u0002\u0007\u0002\fj\u0001c%C\u0002\u0002\u000e6\u0011\u0011BR;oGRLwN\u001c\u001a"
)
public class ExternalShuffleService implements Logging {
   private final SecurityManager securityManager;
   private final MetricsSystem masterMetricsSystem;
   private final boolean enabled;
   private final int org$apache$spark$deploy$ExternalShuffleService$$port;
   private final TransportConf transportConf;
   private final ExternalShuffleBlockHandler blockHandler;
   private final TransportContext transportContext;
   private TransportServer server;
   private final ExternalShuffleServiceSource shuffleServiceSource;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static void main(String[] var0) {
      ExternalShuffleService$.MODULE$.main(var0);
   }

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

   public MetricsSystem masterMetricsSystem() {
      return this.masterMetricsSystem;
   }

   private boolean enabled() {
      return this.enabled;
   }

   public int org$apache$spark$deploy$ExternalShuffleService$$port() {
      return this.org$apache$spark$deploy$ExternalShuffleService$$port;
   }

   private TransportConf transportConf() {
      return this.transportConf;
   }

   private ExternalShuffleBlockHandler blockHandler() {
      return this.blockHandler;
   }

   private TransportContext transportContext() {
      return this.transportContext;
   }

   private TransportServer server() {
      return this.server;
   }

   private void server_$eq(TransportServer x$1) {
      this.server = x$1;
   }

   private ExternalShuffleServiceSource shuffleServiceSource() {
      return this.shuffleServiceSource;
   }

   public ExternalShuffleBlockHandler newShuffleBlockHandler(TransportConf conf) {
      return new ExternalShuffleBlockHandler(conf, (File)null);
   }

   public void startIfEnabled() {
      if (this.enabled()) {
         this.start();
      }

   }

   public void start() {
      Predef$.MODULE$.require(this.server() == null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Shuffle server already started";
         }
      });
      boolean authEnabled = this.securityManager.isAuthenticationEnabled();
      this.logInfo(new Serializable(this, authEnabled) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExternalShuffleService $outer;
         private final boolean authEnabled$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting shuffle service on port ", " (auth enabled = ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$deploy$ExternalShuffleService$$port()), BoxesRunTime.boxToBoolean(this.authEnabled$1)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.authEnabled$1 = authEnabled$1;
            }
         }
      });
      Seq bootstraps = (Seq)(authEnabled ? (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new AuthServerBootstrap[]{new AuthServerBootstrap(this.transportConf(), this.securityManager)}))) : Nil$.MODULE$);
      this.server_$eq(this.transportContext().createServer(this.org$apache$spark$deploy$ExternalShuffleService$$port(), (List)JavaConverters$.MODULE$.seqAsJavaListConverter(bootstraps).asJava()));
      this.shuffleServiceSource().registerMetricSet(this.server().getAllMetrics());
      this.shuffleServiceSource().registerMetricSet(this.blockHandler().getAllMetrics());
      this.masterMetricsSystem().registerSource(this.shuffleServiceSource());
      this.masterMetricsSystem().start();
   }

   public void applicationRemoved(String appId) {
      this.blockHandler().applicationRemoved(appId, true);
   }

   public void stop() {
      if (this.server() != null) {
         this.server().close();
         this.server_$eq((TransportServer)null);
      }

   }

   public ExternalShuffleService(SparkConf sparkConf, SecurityManager securityManager) {
      this.securityManager = securityManager;
      Logging$class.$init$(this);
      this.masterMetricsSystem = MetricsSystem$.MODULE$.createMetricsSystem("shuffleService", sparkConf, securityManager);
      this.enabled = sparkConf.getBoolean("spark.shuffle.service.enabled", false);
      this.org$apache$spark$deploy$ExternalShuffleService$$port = sparkConf.getInt("spark.shuffle.service.port", 7337);
      this.transportConf = SparkTransportConf$.MODULE$.fromSparkConf(sparkConf, "shuffle", 0);
      this.blockHandler = this.newShuffleBlockHandler(this.transportConf());
      this.transportContext = new TransportContext(this.transportConf(), this.blockHandler(), true);
      this.shuffleServiceSource = new ExternalShuffleServiceSource();
   }
}
