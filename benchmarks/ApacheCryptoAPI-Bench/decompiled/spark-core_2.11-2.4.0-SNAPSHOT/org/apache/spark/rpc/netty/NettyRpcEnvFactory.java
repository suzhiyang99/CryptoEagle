package org.apache.spark.rpc.netty;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnvConfig;
import org.apache.spark.rpc.RpcEnvFactory;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.JavaSerializerInstance;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.Serializable;
import scala.Tuple2;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u000192Q!\u0001\u0002\u0001\t1\u0011!CT3uif\u0014\u0006oY#om\u001a\u000b7\r^8ss*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0003\u0002\u0001\u000e']\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0016\u001b\u0005!\u0011B\u0001\f\u0005\u00055\u0011\u0006oY#om\u001a\u000b7\r^8ssB\u0011\u0001dG\u0007\u00023)\u0011!DB\u0001\tS:$XM\u001d8bY&\u0011A$\u0007\u0002\b\u0019><w-\u001b8h\u0011\u0015q\u0002\u0001\"\u0001!\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u0011\u0011\u0005\t\u0002Q\"\u0001\u0002\t\u000b\u0011\u0002A\u0011A\u0013\u0002\r\r\u0014X-\u0019;f)\t1\u0013\u0006\u0005\u0002\u0015O%\u0011\u0001\u0006\u0002\u0002\u0007%B\u001cWI\u001c<\t\u000b)\u001a\u0003\u0019A\u0016\u0002\r\r|gNZ5h!\t!B&\u0003\u0002.\t\ta!\u000b]2F]Z\u001cuN\u001c4jO\u0002"
)
public class NettyRpcEnvFactory implements RpcEnvFactory, Logging {
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

   public RpcEnv create(RpcEnvConfig config) {
      SparkConf sparkConf = config.conf();
      JavaSerializerInstance javaSerializerInstance = (JavaSerializerInstance)(new JavaSerializer(sparkConf)).newInstance();
      NettyRpcEnv nettyEnv = new NettyRpcEnv(sparkConf, javaSerializerInstance, config.advertiseAddress(), config.securityManager(), config.numUsableCores());
      if (config.clientMode()) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         Function1 startNettyRpcEnv = new Serializable(this, config, nettyEnv) {
            public static final long serialVersionUID = 0L;
            private final RpcEnvConfig config$1;
            private final NettyRpcEnv nettyEnv$1;

            public final Tuple2<NettyRpcEnv, Object> apply(int actualPort) {
               this.nettyEnv$1.startServer(this.config$1.bindAddress(), actualPort);
               return new Tuple2<NettyRpcEnv, Object>(this.nettyEnv$1, BoxesRunTime.boxToInteger(this.nettyEnv$1.address().port()));
            }

            public {
               this.config$1 = config$1;
               this.nettyEnv$1 = nettyEnv$1;
            }
         };

         try {
            Utils$.MODULE$.startServiceOnPort(config.port(), startNettyRpcEnv, sparkConf, config.name())._1();
         } catch (Throwable var11) {
            Option var8 = NonFatal$.MODULE$.unapply(var11);
            if (var8.isEmpty()) {
               throw var11;
            }

            Throwable e = (Throwable)var8.get();
            nettyEnv.shutdown();
            throw e;
         }
      }

      return nettyEnv;
   }

   public NettyRpcEnvFactory() {
      Logging$class.$init$(this);
   }
}
