package org.apache.spark.deploy.worker;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcAddress$;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001db!B\u0001\u0003\u0001\u0019a!!D,pe.,'oV1uG\",'O\u0003\u0002\u0004\t\u00051qo\u001c:lKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0003\u0002\u0001\u000e'e\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0018\u001b\u0005)\"B\u0001\f\u0007\u0003\r\u0011\boY\u0005\u00031U\u00111B\u00159d\u000b:$\u0007o\\5oiB\u0011!$H\u0007\u00027)\u0011ADB\u0001\tS:$XM\u001d8bY&\u0011ad\u0007\u0002\b\u0019><w-\u001b8h\u0011!\u0001\u0003A!b\u0001\n\u0003\u0012\u0013A\u0002:qG\u0016sgo\u0001\u0001\u0016\u0003\r\u0002\"\u0001\u0006\u0013\n\u0005\u0015*\"A\u0002*qG\u0016sg\u000f\u0003\u0005(\u0001\t\u0005\t\u0015!\u0003$\u0003\u001d\u0011\boY#om\u0002B\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006IAK\u0001\no>\u00148.\u001a:Ve2\u0004\"a\u000b\u0018\u000f\u00059a\u0013BA\u0017\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0006\r\u0002\u0007'R\u0014\u0018N\\4\u000b\u00055z\u0001\u0002\u0003\u001a\u0001\u0005\u0003\u0005\u000b\u0011B\u001a\u0002\u0013%\u001cH+Z:uS:<\u0007C\u0001\b5\u0013\t)tBA\u0004C_>dW-\u00198\t\u000b]\u0002A\u0011\u0001\u001d\u0002\rqJg.\u001b;?)\u0011I4\bP\u001f\u0011\u0005i\u0002Q\"\u0001\u0002\t\u000b\u00012\u0004\u0019A\u0012\t\u000b%2\u0004\u0019\u0001\u0016\t\u000fI2\u0004\u0013!a\u0001g!Aq\b\u0001a\u0001\n\u0003!\u0001)\u0001\u0006jgNCW\u000f\u001e#po:,\u0012a\r\u0005\t\u0005\u0002\u0001\r\u0011\"\u0001\u0005\u0007\u0006q\u0011n]*ikR$un\u001e8`I\u0015\fHC\u0001#H!\tqQ)\u0003\u0002G\u001f\t!QK\\5u\u0011\u001dA\u0015)!AA\u0002M\n1\u0001\u001f\u00132\u0011\u0019Q\u0005\u0001)Q\u0005g\u0005Y\u0011n]*ikR$un\u001e8!\u0011\u001da\u0005A1A\u0005\n5\u000bq\"\u001a=qK\u000e$X\rZ!eIJ,7o]\u000b\u0002\u001dB\u0011AcT\u0005\u0003!V\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011\u0019\u0011\u0006\u0001)A\u0005\u001d\u0006\u0001R\r\u001f9fGR,G-\u00113ee\u0016\u001c8\u000f\t\u0005\u0006)\u0002!I!V\u0001\tSN<vN]6feR\u00111G\u0016\u0005\u0006/N\u0003\rAT\u0001\bC\u0012$'/Z:t\u0011\u0015I\u0006\u0001\"\u0003[\u0003-)\u00070\u001b;O_:TVM]8\u0015\u0003\u0011CQ\u0001\u0018\u0001\u0005Bu\u000bqA]3dK&4X-F\u0001_!\u0011qq,\u0019#\n\u0005\u0001|!a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\u0011\u00059\u0011\u0017BA2\u0010\u0005\r\te.\u001f\u0005\u0006K\u0002!\tEZ\u0001\f_:\u001cuN\u001c8fGR,G\r\u0006\u0002EO\")\u0001\u000e\u001aa\u0001\u001d\u0006i!/Z7pi\u0016\fE\r\u001a:fgNDQA\u001b\u0001\u0005B-\fab\u001c8ESN\u001cwN\u001c8fGR,G\r\u0006\u0002EY\")\u0001.\u001ba\u0001\u001d\")a\u000e\u0001C!_\u0006qqN\u001c(fi^|'o[#se>\u0014Hc\u0001#q}\")\u0011/\u001ca\u0001e\u0006)1-Y;tKB\u00111o\u001f\b\u0003ift!!\u001e=\u000e\u0003YT!a^\u0011\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012B\u0001>\u0010\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001`?\u0003\u0013QC'o\\<bE2,'B\u0001>\u0010\u0011\u0015AW\u000e1\u0001O\u000f)\t\tAAA\u0001\u0012\u00031\u00111A\u0001\u000e/>\u00148.\u001a:XCR\u001c\u0007.\u001a:\u0011\u0007i\n)AB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0004\u0002\bM\u0019\u0011QA\u0007\t\u000f]\n)\u0001\"\u0001\u0002\fQ\u0011\u00111\u0001\u0005\u000b\u0003\u001f\t)!%A\u0005\u0002\u0005E\u0011a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$3'\u0006\u0002\u0002\u0014)\u001a1'!\u0006,\u0005\u0005]\u0001\u0003BA\r\u0003Gi!!a\u0007\u000b\t\u0005u\u0011qD\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\t\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003K\tYBA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class WorkerWatcher implements RpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   public final String org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl;
   private final boolean isTesting;
   private boolean isShutDown;
   private final RpcAddress expectedAddress;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean $lessinit$greater$default$3() {
      return WorkerWatcher$.MODULE$.$lessinit$greater$default$3();
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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return RpcEndpoint$class.receiveAndReply(this, context);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onStart() {
      RpcEndpoint$class.onStart(this);
   }

   public void onStop() {
      RpcEndpoint$class.onStop(this);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public boolean isShutDown() {
      return this.isShutDown;
   }

   public void isShutDown_$eq(boolean x$1) {
      this.isShutDown = x$1;
   }

   private RpcAddress expectedAddress() {
      return this.expectedAddress;
   }

   private boolean isWorker(RpcAddress address) {
      boolean var3;
      label23: {
         RpcAddress var10000 = this.expectedAddress();
         if (var10000 == null) {
            if (address == null) {
               break label23;
            }
         } else if (var10000.equals(address)) {
            break label23;
         }

         var3 = false;
         return var3;
      }

      var3 = true;
      return var3;
   }

   private void exitNonZero() {
      if (this.isTesting) {
         this.isShutDown_$eq(true);
      } else {
         System.exit(-1);
      }

   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerWatcher $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            this.$outer.logWarning(new Serializable(this, x1) {
               public static final long serialVersionUID = 0L;
               private final Object x1$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received unexpected message: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x1$1}));
               }

               public {
                  this.x1$1 = x1$1;
               }
            });
            BoxedUnit var4 = BoxedUnit.UNIT;
            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3 = true;
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

   public void onConnected(RpcAddress remoteAddress) {
      if (this.isWorker(remoteAddress)) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WorkerWatcher $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Successfully connected to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl}));
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

   }

   public void onDisconnected(RpcAddress remoteAddress) {
      if (this.isWorker(remoteAddress)) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WorkerWatcher $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lost connection to worker rpc endpoint ", ". Exiting."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.exitNonZero();
      }

   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      if (this.isWorker(remoteAddress)) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WorkerWatcher $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not initialize connection to worker ", ". Exiting."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.logError(new Serializable(this, cause) {
            public static final long serialVersionUID = 0L;
            private final Throwable cause$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error was: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cause$1}));
            }

            public {
               this.cause$1 = cause$1;
            }
         });
         this.exitNonZero();
      }

   }

   public WorkerWatcher(RpcEnv rpcEnv, String workerUrl, boolean isTesting) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl = workerUrl;
      this.isTesting = isTesting;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerWatcher $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Connecting to worker ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$WorkerWatcher$$workerUrl}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      if (isTesting) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         rpcEnv.asyncSetupEndpointRefByURI(workerUrl);
      }

      this.isShutDown = false;
      this.expectedAddress = RpcAddress$.MODULE$.fromURIString(workerUrl);
   }
}
