package org.apache.spark.rpc.netty;

import java.util.LinkedList;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=b!B\u0001\u0003\u0001\ta!!B%oE>D(BA\u0002\u0005\u0003\u0015qW\r\u001e;z\u0015\t)a!A\u0002sa\u000eT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0019+\t9Aj\\4hS:<\u0007\u0002\u0003\u000e\u0001\u0005\u000b\u0007I\u0011\u0001\u000f\u0002\u0017\u0015tG\r]8j]R\u0014VMZ\u0002\u0001+\u0005i\u0002C\u0001\u0010 \u001b\u0005\u0011\u0011B\u0001\u0011\u0003\u0005MqU\r\u001e;z%B\u001cWI\u001c3q_&tGOU3g\u0011!\u0011\u0003A!A!\u0002\u0013i\u0012\u0001D3oIB|\u0017N\u001c;SK\u001a\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002\u0011\u0015tG\r]8j]R,\u0012A\n\t\u0003O!j\u0011\u0001B\u0005\u0003S\u0011\u00111B\u00159d\u000b:$\u0007o\\5oi\"A1\u0006\u0001B\u0001B\u0003%a%A\u0005f]\u0012\u0004x.\u001b8uA!)Q\u0006\u0001C\u0001]\u00051A(\u001b8jiz\"2a\f\u00192!\tq\u0002\u0001C\u0003\u001bY\u0001\u0007Q\u0004C\u0003%Y\u0001\u0007a\u0005C\u00044\u0001\t\u0007I\u0011\u0003\u001b\u0002\u00115,7o]1hKN,\u0012!\u000e\t\u0004mmjT\"A\u001c\u000b\u0005aJ\u0014\u0001B;uS2T\u0011AO\u0001\u0005U\u00064\u0018-\u0003\u0002=o\tQA*\u001b8lK\u0012d\u0015n\u001d;\u0011\u0005yq\u0014BA \u0003\u00051IeNY8y\u001b\u0016\u001c8/Y4f\u0011\u0019\t\u0005\u0001)A\u0005k\u0005IQ.Z:tC\u001e,7\u000f\t\u0015\u0005\u0001\u000eke\n\u0005\u0002E\u00176\tQI\u0003\u0002G\u000f\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005!K\u0015AC1o]>$\u0018\r^5p]*\t!*A\u0003kCZ\f\u00070\u0003\u0002M\u000b\nIq)^1sI\u0016$')_\u0001\u0006m\u0006dW/Z\u0011\u0002\u001f\u0006!A\u000f[5t\u0011\u001d\t\u0006\u00011A\u0005\nI\u000bqa\u001d;paB,G-F\u0001T!\tqA+\u0003\u0002V\u001f\t9!i\\8mK\u0006t\u0007bB,\u0001\u0001\u0004%I\u0001W\u0001\fgR|\u0007\u000f]3e?\u0012*\u0017\u000f\u0006\u0002Z9B\u0011aBW\u0005\u00037>\u0011A!\u00168ji\"9QLVA\u0001\u0002\u0004\u0019\u0016a\u0001=%c!1q\f\u0001Q!\nM\u000b\u0001b\u001d;paB,G\r\t\u0015\u0005=\u000eke\nC\u0004c\u0001\u0001\u0007I\u0011\u0002*\u0002!\u0015t\u0017M\u00197f\u0007>t7-\u001e:sK:$\bb\u00023\u0001\u0001\u0004%I!Z\u0001\u0015K:\f'\r\\3D_:\u001cWO\u001d:f]R|F%Z9\u0015\u0005e3\u0007bB/d\u0003\u0003\u0005\ra\u0015\u0005\u0007Q\u0002\u0001\u000b\u0015B*\u0002#\u0015t\u0017M\u00197f\u0007>t7-\u001e:sK:$\b\u0005\u000b\u0003h\u00076s\u0005bB6\u0001\u0001\u0004%I\u0001\\\u0001\u0011]Vl\u0017i\u0019;jm\u0016$\u0006N]3bIN,\u0012!\u001c\t\u0003\u001d9L!a\\\b\u0003\u0007%sG\u000fC\u0004r\u0001\u0001\u0007I\u0011\u0002:\u0002)9,X.Q2uSZ,G\u000b\u001b:fC\u0012\u001cx\fJ3r)\tI6\u000fC\u0004^a\u0006\u0005\t\u0019A7\t\rU\u0004\u0001\u0015)\u0003n\u0003EqW/\\!di&4X\r\u00165sK\u0006$7\u000f\t\u0015\u0005i\u000eke\nC\u0003y\u0001\u0011\u0005\u00110A\u0004qe>\u001cWm]:\u0015\u0005eS\b\"B>x\u0001\u0004a\u0018A\u00033jgB\fGo\u00195feB\u0011a$`\u0005\u0003}\n\u0011!\u0002R5ta\u0006$8\r[3s\u0011\u001d\t\t\u0001\u0001C\u0001\u0003\u0007\tA\u0001]8tiR\u0019\u0011,!\u0002\t\r\u0005\u001dq\u00101\u0001>\u0003\u001diWm]:bO\u0016Dq!a\u0003\u0001\t\u0003\ti!\u0001\u0003ti>\u0004H#A-\t\r\u0005E\u0001\u0001\"\u0001S\u0003\u001dI7/R7qifDq!!\u0006\u0001\t#\t9\"\u0001\u0004p]\u0012\u0013x\u000e\u001d\u000b\u00043\u0006e\u0001bBA\u0004\u0003'\u0001\r!\u0010\u0005\b\u0003;\u0001A\u0011BA\u0010\u0003)\u0019\u0018MZ3ms\u000e\u000bG\u000e\u001c\u000b\u0005\u0003C\ti\u0003F\u0002Z\u0003GA\u0011\"!\n\u0002\u001c\u0011\u0005\r!a\n\u0002\r\u0005\u001cG/[8o!\u0011q\u0011\u0011F-\n\u0007\u0005-rB\u0001\u0005=Eft\u0017-\\3?\u0011\u0019!\u00131\u0004a\u0001M\u0001"
)
public class Inbox implements Logging {
   private final NettyRpcEndpointRef endpointRef;
   private final RpcEndpoint endpoint;
   @GuardedBy("this")
   private final LinkedList<InboxMessage> messages;
   @GuardedBy("this")
   private boolean org$apache$spark$rpc$netty$Inbox$$stopped;
   @GuardedBy("this")
   private boolean org$apache$spark$rpc$netty$Inbox$$enableConcurrent;
   @GuardedBy("this")
   private int org$apache$spark$rpc$netty$Inbox$$numActiveThreads;
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

   public NettyRpcEndpointRef endpointRef() {
      return this.endpointRef;
   }

   public RpcEndpoint endpoint() {
      return this.endpoint;
   }

   public LinkedList<InboxMessage> messages() {
      return this.messages;
   }

   public boolean org$apache$spark$rpc$netty$Inbox$$stopped() {
      return this.org$apache$spark$rpc$netty$Inbox$$stopped;
   }

   private void org$apache$spark$rpc$netty$Inbox$$stopped_$eq(boolean x$1) {
      this.org$apache$spark$rpc$netty$Inbox$$stopped = x$1;
   }

   private boolean org$apache$spark$rpc$netty$Inbox$$enableConcurrent() {
      return this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent;
   }

   public void org$apache$spark$rpc$netty$Inbox$$enableConcurrent_$eq(boolean x$1) {
      this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent = x$1;
   }

   public int org$apache$spark$rpc$netty$Inbox$$numActiveThreads() {
      return this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads;
   }

   private void org$apache$spark$rpc$netty$Inbox$$numActiveThreads_$eq(int x$1) {
      this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads = x$1;
   }

   public void process(Dispatcher dispatcher) {
      ObjectRef message = ObjectRef.create((Object)null);
      synchronized(this) {
         if (!this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent() && this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads() != 0) {
            return;
         }

         message.elem = (T)((InboxMessage)this.messages().poll());
         if ((InboxMessage)message.elem == null) {
            return;
         }

         this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads_$eq(this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads() + 1);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      while(true) {
         this.safelyCall(this.endpoint(), new Serializable(this, dispatcher, message) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Inbox $outer;
            private final Dispatcher dispatcher$1;
            public final ObjectRef message$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               InboxMessage var1 = (InboxMessage)this.message$1.elem;
               if (var1 instanceof RpcMessage) {
                  RpcMessage var2 = (RpcMessage)var1;
                  RpcAddress _sender = var2.senderAddress();
                  Object content = var2.content();
                  NettyRpcCallContext context = var2.context();

                  try {
                     BoxedUnit var10000 = (BoxedUnit)this.$outer.endpoint().receiveAndReply(context).applyOrElse(content, new Serializable(this, _sender) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final RpcAddress _sender$1;

                        public final Nothing$ apply(Object msg) {
                           throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsupported message ", " from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(InboxMessage)this.$outer.message$1.elem, this._sender$1})));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this._sender$1 = _sender$1;
                           }
                        }
                     });
                  } catch (Throwable var32) {
                     Option var9 = NonFatal$.MODULE$.unapply(var32);
                     if (var9.isEmpty()) {
                        throw var32;
                     }

                     Throwable e = (Throwable)var9.get();
                     context.sendFailure(e);
                     throw e;
                  }
               } else if (var1 instanceof OneWayMessage) {
                  OneWayMessage var12 = (OneWayMessage)var1;
                  RpcAddress _sender = var12.senderAddress();
                  Object content = var12.content();
                  BoxedUnit var6 = (BoxedUnit)this.$outer.endpoint().receive().applyOrElse(content, new Serializable(this, _sender) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final RpcAddress _sender$2;

                     public final Nothing$ apply(Object msg) {
                        throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsupported message ", " from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(InboxMessage)this.$outer.message$1.elem, this._sender$2})));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this._sender$2 = _sender$2;
                        }
                     }
                  });
               } else if (OnStart$.MODULE$.equals(var1)) {
                  this.$outer.endpoint().onStart();
                  if (this.$outer.endpoint() instanceof ThreadSafeRpcEndpoint) {
                     BoxedUnit var37 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var16;
                     synchronized(this.$outer) {
                        BoxedUnit var38;
                        if (this.$outer.org$apache$spark$rpc$netty$Inbox$$stopped()) {
                           var38 = BoxedUnit.UNIT;
                        } else {
                           this.$outer.org$apache$spark$rpc$netty$Inbox$$enableConcurrent_$eq(true);
                           var38 = BoxedUnit.UNIT;
                        }

                        var16 = var38;
                     }

                     BoxedUnit var39 = var16;
                  }
               } else if (OnStop$.MODULE$.equals(var1)) {
                  Integer var19;
                  synchronized(this.$outer) {
                     var19 = BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$rpc$netty$Inbox$$numActiveThreads());
                  }

                  int activeThreads = BoxesRunTime.unboxToInt(var19);
                  Predef$.MODULE$.assert(activeThreads == 1, new Serializable(this, activeThreads) {
                     public static final long serialVersionUID = 0L;
                     private final int activeThreads$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"There should be only a single active thread but found ", " threads."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.activeThreads$1)}));
                     }

                     public {
                        this.activeThreads$1 = activeThreads$1;
                     }
                  });
                  this.dispatcher$1.removeRpcEndpointRef(this.$outer.endpoint());
                  this.$outer.endpoint().onStop();
                  Predef$.MODULE$.assert(this.$outer.isEmpty(), new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "OnStop should be the last message";
                     }
                  });
                  BoxedUnit var33 = BoxedUnit.UNIT;
               } else if (var1 instanceof RemoteProcessConnected) {
                  RemoteProcessConnected var20 = (RemoteProcessConnected)var1;
                  RpcAddress remoteAddress = var20.remoteAddress();
                  this.$outer.endpoint().onConnected(remoteAddress);
                  BoxedUnit var34 = BoxedUnit.UNIT;
               } else if (var1 instanceof RemoteProcessDisconnected) {
                  RemoteProcessDisconnected var22 = (RemoteProcessDisconnected)var1;
                  RpcAddress remoteAddress = var22.remoteAddress();
                  this.$outer.endpoint().onDisconnected(remoteAddress);
                  BoxedUnit var35 = BoxedUnit.UNIT;
               } else {
                  if (!(var1 instanceof RemoteProcessConnectionError)) {
                     throw new MatchError(var1);
                  }

                  RemoteProcessConnectionError var24 = (RemoteProcessConnectionError)var1;
                  Throwable cause = var24.cause();
                  RpcAddress remoteAddress = var24.remoteAddress();
                  this.$outer.endpoint().onNetworkError(cause, remoteAddress);
                  BoxedUnit var36 = BoxedUnit.UNIT;
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.dispatcher$1 = dispatcher$1;
                  this.message$1 = message$1;
               }
            }
         });
         synchronized(this) {
            if (!this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent() && this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads() != 1) {
               this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads_$eq(this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads() - 1);
               return;
            }

            message.elem = (T)((InboxMessage)this.messages().poll());
            if ((InboxMessage)message.elem == null) {
               this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads_$eq(this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads() - 1);
               return;
            }

            BoxedUnit var7 = BoxedUnit.UNIT;
         }
      }
   }

   public void post(InboxMessage message) {
      synchronized(this) {
         if (this.org$apache$spark$rpc$netty$Inbox$$stopped()) {
            this.onDrop(message);
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.messages().add(message);
            BoxesRunTime.boxToBoolean(false);
         }

      }
   }

   public void stop() {
      synchronized(this) {
         if (this.org$apache$spark$rpc$netty$Inbox$$stopped()) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent_$eq(false);
            this.org$apache$spark$rpc$netty$Inbox$$stopped_$eq(true);
            BoxesRunTime.boxToBoolean(this.messages().add(OnStop$.MODULE$));
         }

      }
   }

   public synchronized boolean isEmpty() {
      return this.messages().isEmpty();
   }

   public void onDrop(InboxMessage message) {
      this.logWarning(new Serializable(this, message) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Inbox $outer;
         private final InboxMessage message$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop ", " because ", " is stopped"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.message$2, this.$outer.endpointRef()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.message$2 = message$2;
            }
         }
      });
   }

   private void safelyCall(RpcEndpoint endpoint, Function0<BoxedUnit> action) {
      try {
         action.apply$mcV$sp();
      } catch (Throwable var18) {
         Option var5 = NonFatal$.MODULE$.unapply(var18);
         if (var5.isEmpty()) {
            throw var18;
         }

         Throwable e = (Throwable)var5.get();

         try {
            endpoint.onError(e);
            BoxedUnit var21 = BoxedUnit.UNIT;
            return;
         } catch (Throwable var17) {
            Option var10 = NonFatal$.MODULE$.unapply(var17);
            if (var10.isEmpty()) {
               throw var17;
            }

            Throwable ee = (Throwable)var10.get();
            if (this.org$apache$spark$rpc$netty$Inbox$$stopped()) {
               this.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Ignoring error";
                  }
               }, ee);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               this.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Ignoring error";
                  }
               }, ee);
               BoxedUnit var19 = BoxedUnit.UNIT;
            }

            BoxedUnit var20 = BoxedUnit.UNIT;
            return;
         }
      }

   }

   public Inbox(NettyRpcEndpointRef endpointRef, RpcEndpoint endpoint) {
      this.endpointRef = endpointRef;
      this.endpoint = endpoint;
      Logging$class.$init$(this);
      this.messages = new LinkedList();
      this.org$apache$spark$rpc$netty$Inbox$$stopped = false;
      this.org$apache$spark$rpc$netty$Inbox$$enableConcurrent = false;
      this.org$apache$spark$rpc$netty$Inbox$$numActiveThreads = 0;
      synchronized(this) {
         BoxesRunTime.boxToBoolean(this.messages().add(OnStart$.MODULE$));
      }
   }
}
