package org.apache.spark.api.r;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.ReadTimeoutException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

@Sharable
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005d!B\u0001\u0003\u0001\ta!a\u0004*CC\u000e\\WM\u001c3IC:$G.\u001a:\u000b\u0005\r!\u0011!\u0001:\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001b\u0001\u00022AD\u000b\u0018\u001b\u0005y!B\u0001\t\u0012\u0003\u001d\u0019\u0007.\u00198oK2T!AE\n\u0002\u000b9,G\u000f^=\u000b\u0003Q\t!![8\n\u0005Yy!aG*j[BdWm\u00115b]:,G.\u00138c_VtG\rS1oI2,'\u000fE\u0002\u00197ui\u0011!\u0007\u0006\u00025\u0005)1oY1mC&\u0011A$\u0007\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u00031yI!aH\r\u0003\t\tKH/\u001a\t\u0003C\u0011j\u0011A\t\u0006\u0003G\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003K\t\u0012q\u0001T8hO&tw\r\u0003\u0005(\u0001\t\u0005\t\u0015!\u0003*\u0003\u0019\u0019XM\u001d<fe\u000e\u0001\u0001C\u0001\u0016,\u001b\u0005\u0011\u0011B\u0001\u0017\u0003\u0005!\u0011&)Y2lK:$\u0007\"\u0002\u0018\u0001\t\u0003y\u0013A\u0002\u001fj]&$h\b\u0006\u00021cA\u0011!\u0006\u0001\u0005\u0006O5\u0002\r!\u000b\u0005\u0006g\u0001!\t\u0005N\u0001\rG\"\fgN\\3m%\u0016\fG\r\r\u000b\u0004kaj\u0004C\u0001\r7\u0013\t9\u0014D\u0001\u0003V]&$\b\"B\u001d3\u0001\u0004Q\u0014aA2uqB\u0011abO\u0005\u0003y=\u0011Qc\u00115b]:,G\u000eS1oI2,'oQ8oi\u0016DH\u000fC\u0003?e\u0001\u0007q#A\u0002ng\u001eDQ\u0001\u0011\u0001\u0005B\u0005\u000b1c\u00195b]:,GNU3bI\u000e{W\u000e\u001d7fi\u0016$\"!\u000e\"\t\u000bez\u0004\u0019\u0001\u001e\t\u000b\u0011\u0003A\u0011I#\u0002\u001f\u0015D8-\u001a9uS>t7)Y;hQR$2!\u000e$H\u0011\u0015I4\t1\u0001;\u0011\u0015A5\t1\u0001J\u0003\u0015\u0019\u0017-^:f!\tQ%K\u0004\u0002L!:\u0011AjT\u0007\u0002\u001b*\u0011a\nK\u0001\u0007yI|w\u000e\u001e \n\u0003iI!!U\r\u0002\u000fA\f7m[1hK&\u00111\u000b\u0016\u0002\n)\"\u0014xn^1cY\u0016T!!U\r\t\u000bY\u0003A\u0011A,\u0002!!\fg\u000e\u001a7f\u001b\u0016$\bn\u001c3DC2dGcB\u001bY;\u001aDWN\u001e\u0005\u00063V\u0003\rAW\u0001\tSN\u001cF/\u0019;jGB\u0011\u0001dW\u0005\u00039f\u0011qAQ8pY\u0016\fg\u000eC\u0003_+\u0002\u0007q,A\u0003pE*LE\r\u0005\u0002aG:\u0011\u0001$Y\u0005\u0003Ef\ta\u0001\u0015:fI\u00164\u0017B\u00013f\u0005\u0019\u0019FO]5oO*\u0011!-\u0007\u0005\u0006OV\u0003\raX\u0001\u000b[\u0016$\bn\u001c3OC6,\u0007\"B5V\u0001\u0004Q\u0017a\u00028v[\u0006\u0013xm\u001d\t\u00031-L!\u0001\\\r\u0003\u0007%sG\u000fC\u0003o+\u0002\u0007q.A\u0002eSN\u0004\"\u0001\u001d;\u000e\u0003ET!\u0001\u0006:\u000b\u0003M\fAA[1wC&\u0011Q/\u001d\u0002\u0010\t\u0006$\u0018-\u00138qkR\u001cFO]3b[\")q/\u0016a\u0001q\u0006\u0019Am\\:\u0011\u0005AL\u0018B\u0001>r\u0005A!\u0015\r^1PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0003}\u0001\u0011\u0005Q0\u0001\u0005sK\u0006$\u0017I]4t)\u0015q\u00181BA\u0007!\rA2d \t\u0005\u0003\u0003\t9!\u0004\u0002\u0002\u0004)\u0019\u0011Q\u0001:\u0002\t1\fgnZ\u0005\u0005\u0003\u0013\t\u0019A\u0001\u0004PE*,7\r\u001e\u0005\u0006Sn\u0004\rA\u001b\u0005\u0006]n\u0004\ra\u001c\u0005\b\u0003#\u0001A\u0011AA\n\u0003Q1\u0017N\u001c3NCR\u001c\u0007.\u001a3TS\u001et\u0017\r^;sKR1\u0011QCA\u000e\u0003\u0003\u0002B\u0001GA\fU&\u0019\u0011\u0011D\r\u0003\r=\u0003H/[8o\u0011!\ti\"a\u0004A\u0002\u0005}\u0011a\u00069be\u0006lW\r^3s)f\u0004Xm](g\u001b\u0016$\bn\u001c3t!\u0011A2$!\t\u0011\taY\u00121\u0005\u0019\u0005\u0003K\ty\u0003E\u0003a\u0003O\tY#C\u0002\u0002*\u0015\u0014Qa\u00117bgN\u0004B!!\f\u000201\u0001A\u0001DA\u0019\u00037\t\t\u0011!A\u0003\u0002\u0005M\"aA0%cE!\u0011QGA\u001e!\rA\u0012qG\u0005\u0004\u0003sI\"a\u0002(pi\"Lgn\u001a\t\u00041\u0005u\u0012bAA 3\t\u0019\u0011I\\=\t\u000f\u0005\r\u0013q\u0002a\u0001}\u0006!\u0011M]4tQ\r\u0001\u0011q\t\t\u0005\u0003\u0013\nYF\u0004\u0003\u0002L\u0005]c\u0002BA'\u0003+rA!a\u0014\u0002T9\u0019A*!\u0015\n\u0003QI!AE\n\n\u0005A\t\u0012bAA-\u001f\u0005q1\t[1o]\u0016d\u0007*\u00198eY\u0016\u0014\u0018\u0002BA/\u0003?\u0012\u0001b\u00155be\u0006\u0014G.\u001a\u0006\u0004\u00033z\u0001"
)
public class RBackendHandler extends SimpleChannelInboundHandler<byte[]> implements Logging {
   public final RBackend org$apache$spark$api$r$RBackendHandler$$server;
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

   public void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
      ByteArrayOutputStream bos;
      label44: {
         DataInputStream dis;
         DataOutputStream dos;
         boolean isStatic;
         String objId;
         String methodName;
         int numArgs;
         label46: {
            ByteArrayInputStream bis = new ByteArrayInputStream(msg);
            dis = new DataInputStream(bis);
            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);
            isStatic = SerDe$.MODULE$.readBoolean(dis);
            objId = SerDe$.MODULE$.readString(dis);
            methodName = SerDe$.MODULE$.readString(dis);
            numArgs = SerDe$.MODULE$.readInt(dis);
            String var11 = "SparkRHandler";
            if (objId == null) {
               if (var11 != null) {
                  break label46;
               }
            } else if (!objId.equals(var11)) {
               break label46;
            }

            if ("echo".equals(methodName)) {
               Object[] args = this.readArgs(numArgs, dis);
               Predef$.MODULE$.assert(numArgs == 1);
               SerDe$.MODULE$.writeInt(dos, 0);
               SerDe$.MODULE$.writeObject(dos, args[0], this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker());
               BoxedUnit var13 = BoxedUnit.UNIT;
            } else if ("stopBackend".equals(methodName)) {
               SerDe$.MODULE$.writeInt(dos, 0);
               SerDe$.MODULE$.writeType(dos, "void");
               this.org$apache$spark$api$r$RBackendHandler$$server.close();
               BoxedUnit var26 = BoxedUnit.UNIT;
            } else if ("rm".equals(methodName)) {
               try {
                  char t = SerDe$.MODULE$.readObjectType(dis);
                  Predef$.MODULE$.assert(t == 'c');
                  String objToRemove = SerDe$.MODULE$.readString(dis);
                  this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker().remove(new JVMObjectId(objToRemove));
                  SerDe$.MODULE$.writeInt(dos, 0);
                  SerDe$.MODULE$.writeObject(dos, (Object)null, this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker());
                  BoxedUnit var28 = BoxedUnit.UNIT;
               } catch (Exception var25) {
                  this.logError(new Serializable(this, objId) {
                     public static final long serialVersionUID = 0L;
                     private final String objId$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing ", " failed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.objId$1}));
                     }

                     public {
                        this.objId$1 = objId$1;
                     }
                  }, var25);
                  SerDe$.MODULE$.writeInt(dos, -1);
                  SerDe$.MODULE$.writeString(dos, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing ", " failed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{objId, var25.getMessage()})));
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               }
            } else {
               dos.writeInt(-1);
               SerDe$.MODULE$.writeString(dos, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error: unknown method ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{methodName})));
               BoxedUnit var27 = BoxedUnit.UNIT;
            }

            BoxedUnit var29 = BoxedUnit.UNIT;
            break label44;
         }

         ScheduledExecutorService execService = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("SparkRKeepAliveThread");
         Runnable pingRunner = new Runnable(this, ctx) {
            private final ChannelHandlerContext ctx$1;

            public void run() {
               ByteArrayOutputStream pingBaos = new ByteArrayOutputStream();
               DataOutputStream pingDaos = new DataOutputStream(pingBaos);
               SerDe$.MODULE$.writeInt(pingDaos, 1);
               this.ctx$1.write(pingBaos.toByteArray());
            }

            public {
               this.ctx$1 = ctx$1;
            }
         };
         SparkConf conf = new SparkConf();
         int heartBeatInterval = conf.getInt("spark.r.heartBeatInterval", SparkRDefaults$.MODULE$.DEFAULT_HEARTBEAT_INTERVAL());
         int backendConnectionTimeout = conf.getInt("spark.r.backendConnectionTimeout", SparkRDefaults$.MODULE$.DEFAULT_CONNECTION_TIMEOUT());
         int interval = Math.min(heartBeatInterval, backendConnectionTimeout - 1);
         execService.scheduleAtFixedRate(pingRunner, (long)interval, (long)interval, TimeUnit.SECONDS);
         this.handleMethodCall(isStatic, objId, methodName, numArgs, dis, dos);
         execService.shutdown();
         BoxesRunTime.boxToBoolean(execService.awaitTermination(1L, TimeUnit.SECONDS));
      }

      byte[] reply = bos.toByteArray();
      ctx.write(reply);
   }

   public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (cause instanceof ReadTimeoutException) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Ignoring read timeout in RBackendHandler";
            }
         });
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         cause.printStackTrace();
         ctx.close();
         BoxedUnit var5 = BoxedUnit.UNIT;
      }

   }

   public void handleMethodCall(boolean isStatic, String objId, String methodName, int numArgs, DataInputStream dis, DataOutputStream dos) {
      Object obj = null;

      try {
         Class var10000;
         if (isStatic) {
            var10000 = Utils$.MODULE$.classForName(objId);
         } else {
            obj = this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker().apply(new JVMObjectId(objId));
            var10000 = obj.getClass();
         }

         Class cls = var10000;
         Object[] args = this.readArgs(numArgs, dis);
         Method[] methods = cls.getMethods();
         Method[] selectedMethods = (Method[])Predef$.MODULE$.refArrayOps((Object[])methods).filter(new Serializable(this, methodName) {
            public static final long serialVersionUID = 0L;
            private final String methodName$1;

            public final boolean apply(Method m) {
               boolean var3;
               label23: {
                  String var10000 = m.getName();
                  String var2 = this.methodName$1;
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
               this.methodName$1 = methodName$1;
            }
         });
         if (selectedMethods.length > 0) {
            Option index = this.findMatchedSignature((Class[][])Predef$.MODULE$.refArrayOps((Object[])selectedMethods).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Class<?>[] apply(Method x$1) {
                  return x$1.getParameterTypes();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Class.class)))), args);
            if (index.isEmpty()) {
               this.logWarning(new Serializable(this, methodName, cls) {
                  public static final long serialVersionUID = 0L;
                  private final String methodName$1;
                  private final Class cls$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"cannot find matching method ", ".", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cls$1, this.methodName$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Candidates are:"})))).s(Nil$.MODULE$)).toString();
                  }

                  public {
                     this.methodName$1 = methodName$1;
                     this.cls$1 = cls$1;
                  }
               });
               Predef$.MODULE$.refArrayOps((Object[])selectedMethods).foreach(new Serializable(this, methodName) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final RBackendHandler $outer;
                  public final String methodName$1;

                  public final void apply(Method method) {
                     this.$outer.logWarning(new Serializable(this, method) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final Method method$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.methodName$1, Predef$.MODULE$.refArrayOps((Object[])this.method$1.getParameterTypes()).mkString(",")}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.method$1 = method$1;
                           }
                        }
                     });
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.methodName$1 = methodName$1;
                     }
                  }
               });
               throw new Exception((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No matched method found for ", ".", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{cls, methodName})));
            }

            Object ret = selectedMethods[BoxesRunTime.unboxToInt(index.get())].invoke(obj, args);
            SerDe$.MODULE$.writeInt(dos, 0);
            SerDe$.MODULE$.writeObject(dos, ret, this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker());
         } else {
            String var15 = "<init>";
            if (methodName == null) {
               if (var15 != null) {
                  throw new IllegalArgumentException((new StringBuilder()).append((Object)"invalid method ").append((Object)methodName).append((Object)" for object ").append((Object)objId).toString());
               }
            } else if (!methodName.equals(var15)) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)"invalid method ").append((Object)methodName).append((Object)" for object ").append((Object)objId).toString());
            }

            Constructor[] ctors = cls.getConstructors();
            Option index = this.findMatchedSignature((Class[][])Predef$.MODULE$.refArrayOps((Object[])ctors).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Class<?>[] apply(Constructor<?> x$2) {
                  return x$2.getParameterTypes();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Class.class)))), args);
            if (index.isEmpty()) {
               this.logWarning(new Serializable(this, cls) {
                  public static final long serialVersionUID = 0L;
                  private final Class cls$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"cannot find matching constructor for ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cls$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Candidates are:"})))).s(Nil$.MODULE$)).toString();
                  }

                  public {
                     this.cls$1 = cls$1;
                  }
               });
               Predef$.MODULE$.refArrayOps((Object[])ctors).foreach(new Serializable(this, cls) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final RBackendHandler $outer;
                  public final Class cls$1;

                  public final void apply(Constructor<?> ctor) {
                     this.$outer.logWarning(new Serializable(this, ctor) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final Constructor ctor$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.cls$1, Predef$.MODULE$.refArrayOps((Object[])this.ctor$1.getParameterTypes()).mkString(",")}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.ctor$1 = ctor$1;
                           }
                        }
                     });
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.cls$1 = cls$1;
                     }
                  }
               });
               throw new Exception((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No matched constructor found for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{cls})));
            }

            Object obj = ctors[BoxesRunTime.unboxToInt(index.get())].newInstance(args);
            SerDe$.MODULE$.writeInt(dos, 0);
            SerDe$.MODULE$.writeObject(dos, obj, this.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker());
            return;
         }
      } catch (Exception var19) {
         this.logError(new Serializable(this, objId, methodName) {
            public static final long serialVersionUID = 0L;
            private final String objId$2;
            private final String methodName$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " on ", " failed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.methodName$1, this.objId$2}));
            }

            public {
               this.objId$2 = objId$2;
               this.methodName$1 = methodName$1;
            }
         }, var19);
         SerDe$.MODULE$.writeInt(dos, -1);
         SerDe$.MODULE$.writeString(dos, Utils$.MODULE$.exceptionString(var19.getCause()));
      }

   }

   public Object[] readArgs(int numArgs, DataInputStream dis) {
      return ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numArgs).map(new Serializable(this, dis) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RBackendHandler $outer;
         private final DataInputStream dis$1;

         public final Object apply(int x$3) {
            return SerDe$.MODULE$.readObject(this.dis$1, this.$outer.org$apache$spark$api$r$RBackendHandler$$server.jvmObjectTracker());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.dis$1 = dis$1;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Object());
   }

   public Option<Object> findMatchedSignature(Class<?>[][] parameterTypesOfMethods, Object[] args) {
      Object var3 = new Object();

      Object var10000;
      try {
         int numArgs = args.length;
         Predef$.MODULE$.refArrayOps((Object[])parameterTypesOfMethods).indices().foreach$mVc$sp(new Serializable(this, parameterTypesOfMethods, args, numArgs, var3) {
            public static final long serialVersionUID = 0L;
            private final Class[][] parameterTypesOfMethods$1;
            public final Object[] args$1;
            private final int numArgs$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(int index) {
               this.apply$mcVI$sp(index);
            }

            public void apply$mcVI$sp(int index) {
               Class[] parameterTypes = this.parameterTypesOfMethods$1[index];
               if (parameterTypes.length == this.numArgs$1) {
                  boolean argMatched = true;

                  for(int i = 0; i < this.numArgs$1 && argMatched; ++i) {
                     Class parameterType;
                     label94: {
                        parameterType = parameterTypes[i];
                        Class var6 = Seq.class;
                        if (parameterType == null) {
                           if (var6 != null) {
                              break label94;
                           }
                        } else if (!parameterType.equals(var6)) {
                           break label94;
                        }

                        if (this.args$1[i].getClass().isArray()) {
                           continue;
                        }
                     }

                     Class parameterWrapperType = parameterType;
                     if (parameterType.isPrimitive()) {
                        Class var10;
                        label84: {
                           label107: {
                              Class var10000 = Integer.TYPE;
                              if (var10000 == null) {
                                 if (parameterType == null) {
                                    break label107;
                                 }
                              } else if (var10000.equals(parameterType)) {
                                 break label107;
                              }

                              label108: {
                                 var10000 = Long.TYPE;
                                 if (var10000 == null) {
                                    if (parameterType == null) {
                                       break label108;
                                    }
                                 } else if (var10000.equals(parameterType)) {
                                    break label108;
                                 }

                                 label109: {
                                    var10000 = Double.TYPE;
                                    if (var10000 == null) {
                                       if (parameterType == null) {
                                          break label109;
                                       }
                                    } else if (var10000.equals(parameterType)) {
                                       break label109;
                                    }

                                    label62: {
                                       var10000 = Boolean.TYPE;
                                       if (var10000 == null) {
                                          if (parameterType == null) {
                                             break label62;
                                          }
                                       } else if (var10000.equals(parameterType)) {
                                          break label62;
                                       }

                                       var10 = parameterType;
                                       break label84;
                                    }

                                    var10 = Boolean.class;
                                    break label84;
                                 }

                                 var10 = Double.class;
                                 break label84;
                              }

                              var10 = Integer.class;
                              break label84;
                           }

                           var10 = Integer.class;
                        }

                        parameterWrapperType = var10;
                     }

                     if ((parameterType.isPrimitive() || this.args$1[i] != null) && !parameterWrapperType.isInstance(this.args$1[i])) {
                        argMatched = false;
                     }
                  }

                  if (argMatched) {
                     Class[] parameterTypes = this.parameterTypesOfMethods$1[index];
                     RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.numArgs$1).foreach$mVc$sp(new Serializable(this, parameterTypes) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final Class[] parameterTypes$1;

                        public final void apply(int i) {
                           this.apply$mcVI$sp(i);
                        }

                        public void apply$mcVI$sp(int i) {
                           Class var10000 = this.parameterTypes$1[i];
                           Class var2 = Seq.class;
                           if (var10000 == null) {
                              if (var2 != null) {
                                 return;
                              }
                           } else if (!var10000.equals(var2)) {
                              return;
                           }

                           if (this.$outer.args$1[i].getClass().isArray()) {
                              this.$outer.args$1[i] = Predef$.MODULE$.genericArrayOps(this.$outer.args$1[i]).toSeq();
                           }

                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.parameterTypes$1 = parameterTypes$1;
                           }
                        }
                     });
                     throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(BoxesRunTime.boxToInteger(index)));
                  }
               }

            }

            public {
               this.parameterTypesOfMethods$1 = parameterTypesOfMethods$1;
               this.args$1 = args$1;
               this.numArgs$1 = numArgs$1;
               this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
            }
         });
         var10000 = None$.MODULE$;
      } catch (NonLocalReturnControl var6) {
         if (var6.key() != var3) {
            throw var6;
         }

         var10000 = (Option)var6.value();
      }

      return (Option<Object>)var10000;
   }

   public RBackendHandler(RBackend server) {
      this.org$apache$spark$api$r$RBackendHandler$$server = server;
      Logging$class.$init$(this);
   }
}
