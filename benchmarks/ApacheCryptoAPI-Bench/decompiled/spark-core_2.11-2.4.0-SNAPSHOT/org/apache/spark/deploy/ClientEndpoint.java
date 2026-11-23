package org.apache.spark.deploy;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.master.DriverState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.util.SparkExitCode$;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple3;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.HashSet;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutor;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.Nothing$;
import scala.sys.package$;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%e\u0001B\u0001\u0003\t-\u0011ab\u00117jK:$XI\u001c3q_&tGO\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0011\u0001AB\u0005\r\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019b#D\u0001\u0015\u0015\t)B!A\u0002sa\u000eL!a\u0006\u000b\u0003+QC'/Z1e'\u00064WM\u00159d\u000b:$\u0007o\\5oiB\u0011\u0011\u0004H\u0007\u00025)\u00111\u0004B\u0001\tS:$XM\u001d8bY&\u0011QD\u0007\u0002\b\u0019><w-\u001b8h\u0011!y\u0002A!b\u0001\n\u0003\u0002\u0013A\u0002:qG\u0016sg/F\u0001\"!\t\u0019\"%\u0003\u0002$)\t1!\u000b]2F]ZD\u0001\"\n\u0001\u0003\u0002\u0003\u0006I!I\u0001\beB\u001cWI\u001c<!\u0011!9\u0003A!A!\u0002\u0013A\u0013A\u00033sSZ,'/\u0011:hgB\u0011\u0011FK\u0007\u0002\u0005%\u00111F\u0001\u0002\u0010\u00072LWM\u001c;Be\u001e,X.\u001a8ug\"AQ\u0006\u0001B\u0001B\u0003%a&A\bnCN$XM]#oIB|\u0017N\u001c;t!\rysG\u000f\b\u0003aUr!!\r\u001b\u000e\u0003IR!a\r\u0006\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0011B\u0001\u001c\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001O\u001d\u0003\u0007M+\u0017O\u0003\u00027\u001dA\u00111cO\u0005\u0003yQ\u0011aB\u00159d\u000b:$\u0007o\\5oiJ+g\r\u0003\u0005?\u0001\t\u0005\t\u0015!\u0003@\u0003\u0011\u0019wN\u001c4\u0011\u0005\u0001\u000bU\"\u0001\u0003\n\u0005\t#!!C*qCJ\\7i\u001c8g\u0011\u0015!\u0005\u0001\"\u0001F\u0003\u0019a\u0014N\\5u}Q)ai\u0012%J\u0015B\u0011\u0011\u0006\u0001\u0005\u0006?\r\u0003\r!\t\u0005\u0006O\r\u0003\r\u0001\u000b\u0005\u0006[\r\u0003\rA\f\u0005\u0006}\r\u0003\ra\u0010\u0005\b\u0019\u0002\u0011\r\u0011\"\u0003N\u0003Q1wN]<be\u0012lUm]:bO\u0016$\u0006N]3bIV\ta\n\u0005\u0002P-6\t\u0001K\u0003\u0002R%\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005M#\u0016\u0001B;uS2T\u0011!V\u0001\u0005U\u00064\u0018-\u0003\u0002X!\nA2k\u00195fIVdW\rZ#yK\u000e,Ho\u001c:TKJ4\u0018nY3\t\re\u0003\u0001\u0015!\u0003O\u0003U1wN]<be\u0012lUm]:bO\u0016$\u0006N]3bI\u0002Bqa\u0017\u0001C\u0002\u0013%A,\u0001\u0010g_J<\u0018M\u001d3NKN\u001c\u0018mZ3Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yiV\tQ\f\u0005\u0002_A6\tqL\u0003\u0002R\u001d%\u0011\u0011m\u0018\u0002\u0019\u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\u00050Z2vi>\u0014\bBB2\u0001A\u0003%Q,A\u0010g_J<\u0018M\u001d3NKN\u001c\u0018mZ3Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yi\u0002Bq!\u001a\u0001C\u0002\u0013%a-A\u0006m_N$X*Y:uKJ\u001cX#A4\u0011\u0007!lw.D\u0001j\u0015\tQ7.A\u0004nkR\f'\r\\3\u000b\u00051t\u0011AC2pY2,7\r^5p]&\u0011a.\u001b\u0002\b\u0011\u0006\u001c\bnU3u!\t\u0019\u0002/\u0003\u0002r)\tQ!\u000b]2BI\u0012\u0014Xm]:\t\rM\u0004\u0001\u0015!\u0003h\u00031awn\u001d;NCN$XM]:!\u0011\u001d)\b\u00011A\u0005\nY\fA#Y2uSZ,W*Y:uKJ,e\u000e\u001a9pS:$X#\u0001\u001e\t\u000fa\u0004\u0001\u0019!C\u0005s\u0006A\u0012m\u0019;jm\u0016l\u0015m\u001d;fe\u0016sG\r]8j]R|F%Z9\u0015\u0005il\bCA\u0007|\u0013\tahB\u0001\u0003V]&$\bb\u0002@x\u0003\u0003\u0005\rAO\u0001\u0004q\u0012\n\u0004bBA\u0001\u0001\u0001\u0006KAO\u0001\u0016C\u000e$\u0018N^3NCN$XM]#oIB|\u0017N\u001c;!\u0011\u001d\t)\u0001\u0001C!\u0003\u000f\tqa\u001c8Ti\u0006\u0014H\u000fF\u0001{\u0011\u001d\tY\u0001\u0001C\u0005\u0003\u001b\t\u0001%Y:z]\u000e\u001cVM\u001c3U_6\u000b7\u000f^3s\u0003:$gi\u001c:xCJ$'+\u001a9msV!\u0011qBA\u0014)\u0011\t\t\"!\u000f\u0015\u0007i\f\u0019\u0002\u0003\u0006\u0002\u0016\u0005%\u0011\u0011!a\u0002\u0003/\t!\"\u001a<jI\u0016t7-\u001a\u00132!\u0019\tI\"a\b\u0002$5\u0011\u00111\u0004\u0006\u0004\u0003;q\u0011a\u0002:fM2,7\r^\u0005\u0005\u0003C\tYB\u0001\u0005DY\u0006\u001c8\u000fV1h!\u0011\t)#a\n\r\u0001\u0011A\u0011\u0011FA\u0005\u0005\u0004\tYCA\u0001U#\u0011\ti#a\r\u0011\u00075\ty#C\u0002\u000229\u0011qAT8uQ&tw\rE\u0002\u000e\u0003kI1!a\u000e\u000f\u0005\r\te.\u001f\u0005\t\u0003w\tI\u00011\u0001\u00024\u00059Q.Z:tC\u001e,\u0007bBA \u0001\u0011\u0005\u0011\u0011I\u0001\u0014a>dG.\u00118e%\u0016\u0004xN\u001d;Ti\u0006$Xo\u001d\u000b\u0004u\u0006\r\u0003\u0002CA#\u0003{\u0001\r!a\u0012\u0002\u0011\u0011\u0014\u0018N^3s\u0013\u0012\u0004B!!\u0013\u0002P9\u0019Q\"a\u0013\n\u0007\u00055c\"\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003#\n\u0019F\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003\u001br\u0001bBA,\u0001\u0011\u0005\u0013\u0011L\u0001\be\u0016\u001cW-\u001b<f+\t\tY\u0006\u0005\u0004\u000e\u0003;\n\u0019D_\u0005\u0004\u0003?r!a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\t\u000f\u0005\r\u0004\u0001\"\u0011\u0002f\u0005qqN\u001c#jg\u000e|gN\\3di\u0016$Gc\u0001>\u0002h!9\u0011\u0011NA1\u0001\u0004y\u0017!\u0004:f[>$X-\u00113ee\u0016\u001c8\u000fC\u0004\u0002n\u0001!\t%a\u001c\u0002\u001d=tg*\u001a;x_J\\WI\u001d:peR)!0!\u001d\u0002|!A\u00111OA6\u0001\u0004\t)(A\u0003dCV\u001cX\rE\u00020\u0003oJ1!!\u001f:\u0005%!\u0006N]8xC\ndW\rC\u0004\u0002j\u0005-\u0004\u0019A8\t\u000f\u0005}\u0004\u0001\"\u0011\u0002\u0002\u00069qN\\#se>\u0014Hc\u0001>\u0002\u0004\"A\u00111OA?\u0001\u0004\t)\bC\u0004\u0002\b\u0002!\t%a\u0002\u0002\r=t7\u000b^8q\u0001"
)
public class ClientEndpoint implements ThreadSafeRpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   private final ClientArguments driverArgs;
   private final Seq<RpcEndpointRef> masterEndpoints;
   private final SparkConf conf;
   private final ScheduledExecutorService forwardMessageThread;
   private final ExecutionContextExecutor org$apache$spark$deploy$ClientEndpoint$$forwardMessageExecutionContext;
   private final HashSet<RpcAddress> lostMasters;
   private RpcEndpointRef org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint;
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

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return RpcEndpoint$class.receiveAndReply(this, context);
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

   private ScheduledExecutorService forwardMessageThread() {
      return this.forwardMessageThread;
   }

   public ExecutionContextExecutor org$apache$spark$deploy$ClientEndpoint$$forwardMessageExecutionContext() {
      return this.org$apache$spark$deploy$ClientEndpoint$$forwardMessageExecutionContext;
   }

   private HashSet<RpcAddress> lostMasters() {
      return this.lostMasters;
   }

   private RpcEndpointRef org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint() {
      return this.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint;
   }

   public void org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint_$eq(RpcEndpointRef x$1) {
      this.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint = x$1;
   }

   public void onStart() {
      String var1 = this.driverArgs.cmd();
      if ("launch".equals(var1)) {
         String mainClass = "org.apache.spark.deploy.worker.DriverWrapper";
         String classPathConf = "spark.driver.extraClassPath";
         Seq classPathEntries = (Seq)Option$.MODULE$.option2Iterable(package$.MODULE$.props().get(classPathConf)).toSeq().flatMap(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ArrayOps<String> apply(String cp) {
               return Predef$.MODULE$.<String>refArrayOps((Object[])cp.split(File.pathSeparator));
            }
         }, Seq$.MODULE$.canBuildFrom());
         String libraryPathConf = "spark.driver.extraLibraryPath";
         Seq libraryPathEntries = (Seq)Option$.MODULE$.option2Iterable(package$.MODULE$.props().get(libraryPathConf)).toSeq().flatMap(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ArrayOps<String> apply(String cp) {
               return Predef$.MODULE$.<String>refArrayOps((Object[])cp.split(File.pathSeparator));
            }
         }, Seq$.MODULE$.canBuildFrom());
         String extraJavaOptsConf = "spark.driver.extraJavaOptions";
         Seq extraJavaOpts = (Seq)package$.MODULE$.props().get(extraJavaOptsConf).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Seq<String> apply(String s) {
               return Utils$.MODULE$.splitCommandString(s);
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Seq<Nothing$> apply() {
               return (Seq)Seq$.MODULE$.empty();
            }
         });
         Seq sparkJavaOpts = Utils$.MODULE$.sparkJavaOpts(this.conf, Utils$.MODULE$.sparkJavaOpts$default$2());
         Seq javaOpts = (Seq)sparkJavaOpts.$plus$plus(extraJavaOpts, Seq$.MODULE$.canBuildFrom());
         Command command = new Command(mainClass, (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"{{WORKER_URL}}", "{{USER_JAR}}", this.driverArgs.mainClass()})))).$plus$plus(this.driverArgs.driverOptions(), Seq$.MODULE$.canBuildFrom()), package$.MODULE$.env(), classPathEntries, libraryPathEntries, javaOpts);
         DriverDescription driverDescription = new DriverDescription(this.driverArgs.jarUrl(), this.driverArgs.memory(), this.driverArgs.cores(), this.driverArgs.supervise(), command);
         this.asyncSendToMasterAndForwardReply(new DeployMessages.RequestSubmitDriver(driverDescription), ClassTag$.MODULE$.apply(DeployMessages.SubmitDriverResponse.class));
         BoxedUnit var2 = BoxedUnit.UNIT;
      } else {
         if (!"kill".equals(var1)) {
            throw new MatchError(var1);
         }

         String driverId = this.driverArgs.driverId();
         this.asyncSendToMasterAndForwardReply(new DeployMessages.RequestKillDriver(driverId), ClassTag$.MODULE$.apply(DeployMessages.KillDriverResponse.class));
         BoxedUnit var15 = BoxedUnit.UNIT;
      }

   }

   private <T> void asyncSendToMasterAndForwardReply(Object message, ClassTag<T> evidence$1) {
      this.masterEndpoints.foreach(new Serializable(this, message, evidence$1) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ClientEndpoint $outer;
         private final Object message$1;
         private final ClassTag evidence$1$1;

         public final void apply(RpcEndpointRef masterEndpoint) {
            masterEndpoint.ask(this.message$1, this.evidence$1$1).onComplete(new Serializable(this, masterEndpoint) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               public final RpcEndpointRef masterEndpoint$1;

               public final void apply(Try<T> x0$1) {
                  if (x0$1 instanceof Success) {
                     Success var3 = (Success)x0$1;
                     Object v = var3.value();
                     this.$outer.org$apache$spark$deploy$ClientEndpoint$$anonfun$$$outer().self().send(v);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     if (!(x0$1 instanceof Failure)) {
                        throw new MatchError(x0$1);
                     }

                     Failure var6 = (Failure)x0$1;
                     Throwable e = var6.exception();
                     this.$outer.org$apache$spark$deploy$ClientEndpoint$$anonfun$$$outer().logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error sending messages to master ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.masterEndpoint$1}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }, e);
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.masterEndpoint$1 = masterEndpoint$1;
                  }
               }
            }, this.$outer.org$apache$spark$deploy$ClientEndpoint$$forwardMessageExecutionContext());
         }

         // $FF: synthetic method
         public ClientEndpoint org$apache$spark$deploy$ClientEndpoint$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.message$1 = message$1;
               this.evidence$1$1 = evidence$1$1;
            }
         }
      });
   }

   public void pollAndReportStatus(String driverId) {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "... waiting before polling master for driver state";
         }
      });
      Thread.sleep(5000L);
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "... polling master for driver state";
         }
      });
      DeployMessages.DriverStatusResponse statusResponse = (DeployMessages.DriverStatusResponse)this.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint().askSync(new DeployMessages.RequestDriverStatus(driverId), ClassTag$.MODULE$.apply(DeployMessages.DriverStatusResponse.class));
      if (statusResponse.found()) {
         label31: {
            String id;
            String hostPort;
            label30: {
               this.logInfo(new Serializable(this, driverId, statusResponse) {
                  public static final long serialVersionUID = 0L;
                  private final String driverId$1;
                  private final DeployMessages.DriverStatusResponse statusResponse$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"State of ", " is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$1, this.statusResponse$1.state().get()}));
                  }

                  public {
                     this.driverId$1 = driverId$1;
                     this.statusResponse$1 = statusResponse$1;
                  }
               });
               Tuple3 var3 = new Tuple3(statusResponse.workerId(), statusResponse.workerHostPort(), statusResponse.state());
               if (var3 != null) {
                  Option var4 = (Option)var3._1();
                  Option var5 = (Option)var3._2();
                  Option var6 = (Option)var3._3();
                  if (var4 instanceof Some) {
                     Some var7 = (Some)var4;
                     id = (String)var7.x();
                     if (var5 instanceof Some) {
                        Some var9 = (Some)var5;
                        hostPort = (String)var9.x();
                        if (var6 instanceof Some) {
                           Some var11 = (Some)var6;
                           Enumeration.Value var12 = (Enumeration.Value)var11.x();
                           Enumeration.Value var10000 = DriverState$.MODULE$.RUNNING();
                           if (var10000 == null) {
                              if (var12 == null) {
                                 break label30;
                              }
                           } else if (var10000.equals(var12)) {
                              break label30;
                           }
                        }
                     }
                  }
               }

               BoxedUnit var14 = BoxedUnit.UNIT;
               break label31;
            }

            this.logInfo(new Serializable(this, id, hostPort) {
               public static final long serialVersionUID = 0L;
               private final String id$1;
               private final String hostPort$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver running on ", " (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hostPort$1, this.id$1}));
               }

               public {
                  this.id$1 = id$1;
                  this.hostPort$1 = hostPort$1;
               }
            });
            BoxedUnit var19 = BoxedUnit.UNIT;
         }

         Option var15 = statusResponse.exception();
         if (var15 instanceof Some) {
            Some var16 = (Some)var15;
            Exception e = (Exception)var16.x();
            this.logError(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               private final Exception e$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception from cluster was: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.e$1}));
               }

               public {
                  this.e$1 = e$1;
               }
            });
            e.printStackTrace();
            System.exit(-1);
            BoxedUnit var18 = BoxedUnit.UNIT;
         } else {
            System.exit(0);
            BoxedUnit var20 = BoxedUnit.UNIT;
         }
      } else {
         this.logError(new Serializable(this, driverId) {
            public static final long serialVersionUID = 0L;
            private final String driverId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ERROR: Cluster master did not recognize ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$1}));
            }

            public {
               this.driverId$1 = driverId$1;
            }
         });
         System.exit(-1);
      }

   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ClientEndpoint $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var9;
            if (x1 instanceof DeployMessages.SubmitDriverResponse) {
               DeployMessages.SubmitDriverResponse var4 = (DeployMessages.SubmitDriverResponse)x1;
               RpcEndpointRef master = var4.master();
               boolean success = var4.success();
               Option driverId = var4.driverId();
               String message = var4.message();
               this.$outer.logInfo(new Serializable(this, message) {
                  public static final long serialVersionUID = 0L;
                  private final String message$2;

                  public final String apply() {
                     return this.message$2;
                  }

                  public {
                     this.message$2 = message$2;
                  }
               });
               BoxedUnit var10000;
               if (success) {
                  this.$outer.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint_$eq(master);
                  this.$outer.pollAndReportStatus((String)driverId.get());
                  var10000 = BoxedUnit.UNIT;
               } else if (Utils$.MODULE$.responseFromBackup(message)) {
                  var10000 = BoxedUnit.UNIT;
               } else {
                  System.exit(-1);
                  var10000 = BoxedUnit.UNIT;
               }

               var9 = var10000;
            } else if (x1 instanceof DeployMessages.KillDriverResponse) {
               DeployMessages.KillDriverResponse var10 = (DeployMessages.KillDriverResponse)x1;
               RpcEndpointRef master = var10.master();
               String driverId = var10.driverId();
               boolean success = var10.success();
               String message = var10.message();
               this.$outer.logInfo(new Serializable(this, message) {
                  public static final long serialVersionUID = 0L;
                  private final String message$3;

                  public final String apply() {
                     return this.message$3;
                  }

                  public {
                     this.message$3 = message$3;
                  }
               });
               BoxedUnit var15;
               if (success) {
                  this.$outer.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint_$eq(master);
                  this.$outer.pollAndReportStatus(driverId);
                  var15 = BoxedUnit.UNIT;
               } else if (Utils$.MODULE$.responseFromBackup(message)) {
                  var15 = BoxedUnit.UNIT;
               } else {
                  System.exit(-1);
                  var15 = BoxedUnit.UNIT;
               }

               var9 = var15;
            } else {
               var9 = var2.apply(x1);
            }

            return (B1)var9;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof DeployMessages.SubmitDriverResponse) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.KillDriverResponse) {
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
            }
         }
      };
   }

   public void onDisconnected(RpcAddress remoteAddress) {
      if (!this.lostMasters().contains(remoteAddress)) {
         this.logError(new Serializable(this, remoteAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress remoteAddress$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error connecting to master ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$1}));
            }

            public {
               this.remoteAddress$1 = remoteAddress$1;
            }
         });
         this.lostMasters().$plus$eq(remoteAddress);
         if (this.lostMasters().size() >= this.masterEndpoints.size()) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "No master is available, exiting.";
               }
            });
            System.exit(-1);
         }
      }

   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      if (!this.lostMasters().contains(remoteAddress)) {
         this.logError(new Serializable(this, remoteAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress remoteAddress$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error connecting to master (", ")."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$2}));
            }

            public {
               this.remoteAddress$2 = remoteAddress$2;
            }
         });
         this.logError(new Serializable(this, cause) {
            public static final long serialVersionUID = 0L;
            private final Throwable cause$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cause was: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cause$1}));
            }

            public {
               this.cause$1 = cause$1;
            }
         });
         this.lostMasters().$plus$eq(remoteAddress);
         if (this.lostMasters().size() >= this.masterEndpoints.size()) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "No master is available, exiting.";
               }
            });
            System.exit(-1);
         }
      }

   }

   public void onError(Throwable cause) {
      this.logError(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error processing messages, exiting."})))).s(Nil$.MODULE$);
         }
      });
      cause.printStackTrace();
      System.exit(-1);
   }

   public void onStop() {
      this.forwardMessageThread().shutdownNow();
   }

   public ClientEndpoint(RpcEnv rpcEnv, ClientArguments driverArgs, Seq<RpcEndpointRef> masterEndpoints, SparkConf conf) {
      this.rpcEnv = rpcEnv;
      this.driverArgs = driverArgs;
      this.masterEndpoints = masterEndpoints;
      this.conf = conf;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.forwardMessageThread = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("client-forward-message");
      this.org$apache$spark$deploy$ClientEndpoint$$forwardMessageExecutionContext = ExecutionContext$.MODULE$.fromExecutor(this.forwardMessageThread(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ClientEndpoint $outer;

         public final void apply(Throwable t) {
            if (t instanceof InterruptedException) {
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               if (t == null) {
                  throw new MatchError(t);
               }

               this.$outer.logError(new Serializable(this, t) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable x3$1;

                  public final String apply() {
                     return this.x3$1.getMessage();
                  }

                  public {
                     this.x3$1 = x3$1;
                  }
               }, t);
               System.exit(SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION());
               BoxedUnit var5 = BoxedUnit.UNIT;
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.lostMasters = new HashSet<RpcAddress>();
      this.org$apache$spark$deploy$ClientEndpoint$$activeMasterEndpoint = null;
   }
}
