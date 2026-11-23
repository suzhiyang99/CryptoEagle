package org.apache.spark.rpc.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpointAddress;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnvStoppedException;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.concurrent.Promise;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\t-b!B\u0001\u0003\u0001\ta!A\u0003#jgB\fGo\u00195fe*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\f\u000e\u0003UQ!A\u0006\u0004\u0002\u0011%tG/\u001a:oC2L!\u0001G\u000b\u0003\u000f1{wmZ5oO\"A!\u0004\u0001B\u0001B\u0003%A$\u0001\u0005oKR$\u00180\u00128w\u0007\u0001\u0001\"!\b\u0010\u000e\u0003\tI!a\b\u0002\u0003\u00179+G\u000f^=Sa\u000e,eN\u001e\u0005\tC\u0001\u0011\t\u0011)A\u0005E\u0005qa.^7Vg\u0006\u0014G.Z\"pe\u0016\u001c\bC\u0001\b$\u0013\t!sBA\u0002J]RDQA\n\u0001\u0005\u0002\u001d\na\u0001P5oSRtDc\u0001\u0015*UA\u0011Q\u0004\u0001\u0005\u00065\u0015\u0002\r\u0001\b\u0005\u0006C\u0015\u0002\rA\t\u0004\u0005Y\u0001!QF\u0001\u0007F]\u0012\u0004x.\u001b8u\t\u0006$\u0018m\u0005\u0002,\u001b!Aqf\u000bBC\u0002\u0013\u0005\u0001'\u0001\u0003oC6,W#A\u0019\u0011\u0005I*dB\u0001\b4\u0013\t!t\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003m]\u0012aa\u0015;sS:<'B\u0001\u001b\u0010\u0011!I4F!A!\u0002\u0013\t\u0014!\u00028b[\u0016\u0004\u0003\u0002C\u001e,\u0005\u000b\u0007I\u0011\u0001\u001f\u0002\u0011\u0015tG\r]8j]R,\u0012!\u0010\t\u0003}}j\u0011\u0001B\u0005\u0003\u0001\u0012\u00111B\u00159d\u000b:$\u0007o\\5oi\"A!i\u000bB\u0001B\u0003%Q(A\u0005f]\u0012\u0004x.\u001b8uA!AAi\u000bBC\u0002\u0013\u0005Q)A\u0002sK\u001a,\u0012A\u0012\t\u0003;\u001dK!\u0001\u0013\u0002\u0003'9+G\u000f^=Sa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0011)[#\u0011!Q\u0001\n\u0019\u000bAA]3gA!)ae\u000bC\u0001\u0019R!Qj\u0014)R!\tq5&D\u0001\u0001\u0011\u0015y3\n1\u00012\u0011\u0015Y4\n1\u0001>\u0011\u0015!5\n1\u0001G\u0011\u001d\u00196F1A\u0005\u0002Q\u000bQ!\u001b8c_b,\u0012!\u0016\t\u0003;YK!a\u0016\u0002\u0003\u000b%s'm\u001c=\t\re[\u0003\u0015!\u0003V\u0003\u0019IgNY8yA!91\f\u0001b\u0001\n\u0013a\u0016!C3oIB|\u0017N\u001c;t+\u0005i\u0006\u0003\u00020fc5k\u0011a\u0018\u0006\u0003A\u0006\f!bY8oGV\u0014(/\u001a8u\u0015\t\u00117-\u0001\u0003vi&d'\"\u00013\u0002\t)\fg/Y\u0005\u0003M~\u0013QbQ8oGV\u0014(/\u001a8u\u001b\u0006\u0004\bB\u00025\u0001A\u0003%Q,\u0001\u0006f]\u0012\u0004x.\u001b8ug\u0002BqA\u001b\u0001C\u0002\u0013%1.\u0001\u0007f]\u0012\u0004x.\u001b8u%\u001647/F\u0001m!\u0011qV-P7\u0011\u0005yr\u0017BA8\u0005\u00059\u0011\u0006oY#oIB|\u0017N\u001c;SK\u001aDa!\u001d\u0001!\u0002\u0013a\u0017!D3oIB|\u0017N\u001c;SK\u001a\u001c\b\u0005C\u0004t\u0001\t\u0007I\u0011\u0002;\u0002\u0013I,7-Z5wKJ\u001cX#A;\u0011\u0007y3X*\u0003\u0002x?\n\u0019B*\u001b8lK\u0012\u0014En\\2lS:<\u0017+^3vK\"1\u0011\u0010\u0001Q\u0001\nU\f!B]3dK&4XM]:!\u0011\u001dY\b\u00011A\u0005\nq\fqa\u001d;paB,G-F\u0001~!\tqa0\u0003\u0002\u0000\u001f\t9!i\\8mK\u0006t\u0007\"CA\u0002\u0001\u0001\u0007I\u0011BA\u0003\u0003-\u0019Ho\u001c9qK\u0012|F%Z9\u0015\t\u0005\u001d\u0011Q\u0002\t\u0004\u001d\u0005%\u0011bAA\u0006\u001f\t!QK\\5u\u0011%\ty!!\u0001\u0002\u0002\u0003\u0007Q0A\u0002yIEBq!a\u0005\u0001A\u0003&Q0\u0001\u0005ti>\u0004\b/\u001a3!Q!\t\t\"a\u0006\u0002*\u0005-\u0002\u0003BA\r\u0003Ki!!a\u0007\u000b\u0007\u0001\fiB\u0003\u0003\u0002 \u0005\u0005\u0012AC1o]>$\u0018\r^5p]*\u0011\u00111E\u0001\u0006U\u00064\u0018\r_\u0005\u0005\u0003O\tYBA\u0005Hk\u0006\u0014H-\u001a3Cs\u0006)a/\u00197vK\u0006\u0012\u0011QF\u0001\u0005i\"L7\u000fC\u0004\u00022\u0001!\t!a\r\u0002'I,w-[:uKJ\u0014\u0006oY#oIB|\u0017N\u001c;\u0015\u000b\u0019\u000b)$a\u000e\t\r=\ny\u00031\u00012\u0011\u0019Y\u0014q\u0006a\u0001{!9\u00111\b\u0001\u0005\u0002\u0005u\u0012!E4fiJ\u00038-\u00128ea>Lg\u000e\u001e*fMR\u0019Q.a\u0010\t\rm\nI\u00041\u0001>\u0011\u001d\t\u0019\u0005\u0001C\u0001\u0003\u000b\nAC]3n_Z,'\u000b]2F]\u0012\u0004x.\u001b8u%\u00164G\u0003BA\u0004\u0003\u000fBaaOA!\u0001\u0004i\u0004bBA&\u0001\u0011%\u0011QJ\u0001\u0016k:\u0014XmZ5ti\u0016\u0014(\u000b]2F]\u0012\u0004x.\u001b8u)\u0011\t9!a\u0014\t\r=\nI\u00051\u00012\u0011\u001d\t\u0019\u0006\u0001C\u0001\u0003+\nAa\u001d;paR!\u0011qAA,\u0011\u001d\tI&!\u0015A\u00025\faB\u001d9d\u000b:$\u0007o\\5oiJ+g\rC\u0004\u0002^\u0001!\t!a\u0018\u0002\u0013A|7\u000f\u001e+p\u00032dG\u0003BA\u0004\u0003CB\u0001\"a\u0019\u0002\\\u0001\u0007\u0011QM\u0001\b[\u0016\u001c8/Y4f!\ri\u0012qM\u0005\u0004\u0003S\u0012!\u0001D%oE>DX*Z:tC\u001e,\u0007bBA7\u0001\u0011\u0005\u0011qN\u0001\u0012a>\u001cHOU3n_R,W*Z:tC\u001e,GCBA\u0004\u0003c\nI\b\u0003\u0005\u0002d\u0005-\u0004\u0019AA:!\ri\u0012QO\u0005\u0004\u0003o\u0012!A\u0004*fcV,7\u000f^'fgN\fw-\u001a\u0005\t\u0003w\nY\u00071\u0001\u0002~\u0005A1-\u00197mE\u0006\u001c7\u000e\u0005\u0003\u0002\u0000\u0005%UBAAA\u0015\u0011\t\u0019)!\"\u0002\r\rd\u0017.\u001a8u\u0015\r\t9IB\u0001\b]\u0016$xo\u001c:l\u0013\u0011\tY)!!\u0003'I\u00038MU3ta>t7/Z\"bY2\u0014\u0017mY6\t\u000f\u0005=\u0005\u0001\"\u0001\u0002\u0012\u0006\u0001\u0002o\\:u\u0019>\u001c\u0017\r\\'fgN\fw-\u001a\u000b\u0007\u0003\u000f\t\u0019*!&\t\u0011\u0005\r\u0014Q\u0012a\u0001\u0003gB\u0001\"a&\u0002\u000e\u0002\u0007\u0011\u0011T\u0001\u0002aB1\u00111TAP\u0003Gk!!!(\u000b\u0005\u0001|\u0011\u0002BAQ\u0003;\u0013q\u0001\u0015:p[&\u001cX\rE\u0002\u000f\u0003KK1!a*\u0010\u0005\r\te.\u001f\u0005\b\u0003W\u0003A\u0011AAW\u0003E\u0001xn\u001d;P]\u0016<\u0016-_'fgN\fw-\u001a\u000b\u0005\u0003\u000f\ty\u000b\u0003\u0005\u0002d\u0005%\u0006\u0019AA:\u0011\u001d\t\u0019\f\u0001C\u0005\u0003k\u000b1\u0002]8ti6+7o]1hKRA\u0011qAA\\\u0003w\u000bi\fC\u0004\u0002:\u0006E\u0006\u0019A\u0019\u0002\u0019\u0015tG\r]8j]Rt\u0015-\\3\t\u0011\u0005\r\u0014\u0011\u0017a\u0001\u0003KB\u0001\"a0\u00022\u0002\u0007\u0011\u0011Y\u0001\u0012G\u0006dGNY1dW&37\u000b^8qa\u0016$\u0007c\u0002\b\u0002D\u0006\u001d\u0017qA\u0005\u0004\u0003\u000b|!!\u0003$v]\u000e$\u0018n\u001c82!\u0011\tI-!7\u000f\t\u0005-\u0017Q\u001b\b\u0005\u0003\u001b\f\u0019.\u0004\u0002\u0002P*\u0019\u0011\u0011[\u000e\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012bAAl\u001f\u00059\u0001/Y2lC\u001e,\u0017\u0002BAn\u0003;\u0014\u0011\"\u0012=dKB$\u0018n\u001c8\u000b\u0007\u0005]w\u0002C\u0004\u0002T\u0001!\t!!9\u0015\u0005\u0005\u001d\u0001bBAs\u0001\u0011\u0005\u0011\u0011]\u0001\u0011C^\f\u0017\u000e\u001e+fe6Lg.\u0019;j_:Dq!!;\u0001\t\u0003\tY/\u0001\u0004wKJLg-\u001f\u000b\u0004{\u00065\bBB\u0018\u0002h\u0002\u0007\u0011\u0007C\u0005\u0002r\u0002\u0011\r\u0011\"\u0003\u0002t\u0006QA\u000f\u001b:fC\u0012\u0004xn\u001c7\u0016\u0005\u0005U\bc\u00010\u0002x&\u0019\u0011\u0011`0\u0003%QC'/Z1e!>|G.\u0012=fGV$xN\u001d\u0005\t\u0003{\u0004\u0001\u0015!\u0003\u0002v\u0006YA\u000f\u001b:fC\u0012\u0004xn\u001c7!\r\u0019\u0011\t\u0001\u0001\u0003\u0003\u0004\tYQ*Z:tC\u001e,Gj\\8q'\u0019\tyP!\u0002\u0003\u0012A!!q\u0001B\u0007\u001b\t\u0011IAC\u0002\u0003\f\r\fA\u0001\\1oO&!!q\u0002B\u0005\u0005\u0019y%M[3diB!!q\u0001B\n\u0013\u0011\u0011)B!\u0003\u0003\u0011I+hN\\1cY\u0016DqAJA\u0000\t\u0003\u0011I\u0002\u0006\u0002\u0003\u001cA\u0019a*a@\t\u0011\t}\u0011q C!\u0003C\f1A];o\u0011%\u0011\u0019\u0003\u0001b\u0001\n\u0013\u0011)#\u0001\u0006Q_&\u001cxN\u001c)jY2,\u0012!\u0014\u0005\b\u0005S\u0001\u0001\u0015!\u0003N\u0003-\u0001v.[:p]BKG\u000e\u001c\u0011"
)
public class Dispatcher implements Logging {
   private final NettyRpcEnv nettyEnv;
   private final ConcurrentMap<String, EndpointData> endpoints;
   private final ConcurrentMap<RpcEndpoint, RpcEndpointRef> endpointRefs;
   private final LinkedBlockingQueue<EndpointData> org$apache$spark$rpc$netty$Dispatcher$$receivers;
   @GuardedBy("this")
   private boolean stopped;
   private final ThreadPoolExecutor threadpool;
   private final EndpointData org$apache$spark$rpc$netty$Dispatcher$$PoisonPill;
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

   private ConcurrentMap<String, EndpointData> endpoints() {
      return this.endpoints;
   }

   private ConcurrentMap<RpcEndpoint, RpcEndpointRef> endpointRefs() {
      return this.endpointRefs;
   }

   public LinkedBlockingQueue<EndpointData> org$apache$spark$rpc$netty$Dispatcher$$receivers() {
      return this.org$apache$spark$rpc$netty$Dispatcher$$receivers;
   }

   private boolean stopped() {
      return this.stopped;
   }

   private void stopped_$eq(boolean x$1) {
      this.stopped = x$1;
   }

   public NettyRpcEndpointRef registerRpcEndpoint(String name, RpcEndpoint endpoint) {
      RpcEndpointAddress addr = new RpcEndpointAddress(this.nettyEnv.address(), name);
      NettyRpcEndpointRef endpointRef = new NettyRpcEndpointRef(this.nettyEnv.conf(), addr, this.nettyEnv);
      synchronized(this) {
         if (this.stopped()) {
            throw new IllegalStateException("RpcEnv has been stopped");
         } else if (this.endpoints().putIfAbsent(name, new EndpointData(name, endpoint, endpointRef)) == null) {
            EndpointData data = (EndpointData)this.endpoints().get(name);
            this.endpointRefs().put(data.endpoint(), data.ref());
            BoxesRunTime.boxToBoolean(this.org$apache$spark$rpc$netty$Dispatcher$$receivers().offer(data));
            return endpointRef;
         } else {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"There is already an RpcEndpoint called ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})));
         }
      }
   }

   public RpcEndpointRef getRpcEndpointRef(RpcEndpoint endpoint) {
      return (RpcEndpointRef)this.endpointRefs().get(endpoint);
   }

   public void removeRpcEndpointRef(RpcEndpoint endpoint) {
      this.endpointRefs().remove(endpoint);
   }

   public void org$apache$spark$rpc$netty$Dispatcher$$unregisterRpcEndpoint(String name) {
      EndpointData data = (EndpointData)this.endpoints().remove(name);
      if (data != null) {
         data.inbox().stop();
         this.org$apache$spark$rpc$netty$Dispatcher$$receivers().offer(data);
      }

   }

   public synchronized void stop(RpcEndpointRef rpcEndpointRef) {
      if (!this.stopped()) {
         this.org$apache$spark$rpc$netty$Dispatcher$$unregisterRpcEndpoint(rpcEndpointRef.name());
      }
   }

   public void postToAll(InboxMessage message) {
      for(String name : this.endpoints().keySet()) {
         this.postMessage(name, message, new Serializable(this, message) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Dispatcher $outer;
            public final InboxMessage message$1;

            public final void apply(Exception e) {
               if (e instanceof RpcEnvStoppedException) {
                  RpcEnvStoppedException var3 = (RpcEnvStoppedException)e;
                  this.$outer.logDebug(new Serializable(this, var3) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final RpcEnvStoppedException x2$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Message ", " dropped. ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.message$1, this.x2$1.getMessage()}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.x2$1 = x2$1;
                        }
                     }
                  });
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  if (e == null) {
                     throw new MatchError(e);
                  }

                  this.$outer.logWarning(new Serializable(this, e) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final Exception x3$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Message ", " dropped. ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.message$1, this.x3$1.getMessage()}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.x3$1 = x3$1;
                        }
                     }
                  });
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.message$1 = message$1;
               }
            }
         });
      }

   }

   public void postRemoteMessage(RequestMessage message, RpcResponseCallback callback) {
      RemoteNettyRpcCallContext rpcCallContext = new RemoteNettyRpcCallContext(this.nettyEnv, callback, message.senderAddress());
      RpcMessage rpcMessage = new RpcMessage(message.senderAddress(), message.content(), rpcCallContext);
      this.postMessage(message.receiver().name(), rpcMessage, new Serializable(this, callback) {
         public static final long serialVersionUID = 0L;
         private final RpcResponseCallback callback$1;

         public final void apply(Exception e) {
            this.callback$1.onFailure(e);
         }

         public {
            this.callback$1 = callback$1;
         }
      });
   }

   public void postLocalMessage(RequestMessage message, Promise<Object> p) {
      LocalNettyRpcCallContext rpcCallContext = new LocalNettyRpcCallContext(message.senderAddress(), p);
      RpcMessage rpcMessage = new RpcMessage(message.senderAddress(), message.content(), rpcCallContext);
      this.postMessage(message.receiver().name(), rpcMessage, new Serializable(this, p) {
         public static final long serialVersionUID = 0L;
         private final Promise p$1;

         public final void apply(Exception e) {
            this.p$1.tryFailure(e);
         }

         public {
            this.p$1 = p$1;
         }
      });
   }

   public void postOneWayMessage(RequestMessage message) {
      this.postMessage(message.receiver().name(), new OneWayMessage(message.senderAddress(), message.content()), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply(Exception e) {
            throw e;
         }
      });
   }

   private void postMessage(String endpointName, InboxMessage message, Function1<Exception, BoxedUnit> callbackIfStopped) {
      Object var6;
      synchronized(this) {
         EndpointData data = (EndpointData)this.endpoints().get(endpointName);
         Object var10000;
         if (this.stopped()) {
            var10000 = new Some(new RpcEnvStoppedException());
         } else if (data == null) {
            var10000 = new Some(new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not find ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{endpointName}))));
         } else {
            data.inbox().post(message);
            this.org$apache$spark$rpc$netty$Dispatcher$$receivers().offer(data);
            var10000 = None$.MODULE$;
         }

         var6 = var10000;
      }

      Option error = (Option)var6;
      error.foreach(callbackIfStopped);
   }

   public void stop() {
      synchronized(this) {
         if (this.stopped()) {
            return;
         }

         this.stopped_$eq(true);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.endpoints().keySet()).asScala()).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Dispatcher $outer;

         public final void apply(String name) {
            this.$outer.org$apache$spark$rpc$netty$Dispatcher$$unregisterRpcEndpoint(name);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$rpc$netty$Dispatcher$$receivers().offer(this.org$apache$spark$rpc$netty$Dispatcher$$PoisonPill());
      this.threadpool().shutdown();
   }

   public void awaitTermination() {
      this.threadpool().awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
   }

   public boolean verify(String name) {
      return this.endpoints().containsKey(name);
   }

   private ThreadPoolExecutor threadpool() {
      return this.threadpool;
   }

   public EndpointData org$apache$spark$rpc$netty$Dispatcher$$PoisonPill() {
      return this.org$apache$spark$rpc$netty$Dispatcher$$PoisonPill;
   }

   public Dispatcher(NettyRpcEnv nettyEnv, int numUsableCores) {
      this.nettyEnv = nettyEnv;
      Logging$class.$init$(this);
      this.endpoints = new ConcurrentHashMap();
      this.endpointRefs = new ConcurrentHashMap();
      this.org$apache$spark$rpc$netty$Dispatcher$$receivers = new LinkedBlockingQueue();
      this.stopped = false;
      int availableCores = numUsableCores > 0 ? numUsableCores : Runtime.getRuntime().availableProcessors();
      int numThreads = nettyEnv.conf().getInt("spark.rpc.netty.dispatcher.numThreads", package$.MODULE$.max(2, availableCores));
      ThreadPoolExecutor pool = ThreadUtils$.MODULE$.newDaemonFixedThreadPool(numThreads, "dispatcher-event-loop");
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numThreads).foreach$mVc$sp(new Serializable(this, pool) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Dispatcher $outer;
         private final ThreadPoolExecutor pool$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.pool$1.execute(this.$outer.new MessageLoop());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.pool$1 = pool$1;
            }
         }
      });
      this.threadpool = pool;
      this.org$apache$spark$rpc$netty$Dispatcher$$PoisonPill = new EndpointData((String)null, (RpcEndpoint)null, (NettyRpcEndpointRef)null);
   }

   public class EndpointData {
      private final String name;
      private final RpcEndpoint endpoint;
      private final NettyRpcEndpointRef ref;
      private final Inbox inbox;
      // $FF: synthetic field
      public final Dispatcher $outer;

      public String name() {
         return this.name;
      }

      public RpcEndpoint endpoint() {
         return this.endpoint;
      }

      public NettyRpcEndpointRef ref() {
         return this.ref;
      }

      public Inbox inbox() {
         return this.inbox;
      }

      // $FF: synthetic method
      public Dispatcher org$apache$spark$rpc$netty$Dispatcher$EndpointData$$$outer() {
         return this.$outer;
      }

      public EndpointData(String name, RpcEndpoint endpoint, NettyRpcEndpointRef ref) {
         this.name = name;
         this.endpoint = endpoint;
         this.ref = ref;
         if (Dispatcher.this == null) {
            throw null;
         } else {
            this.$outer = Dispatcher.this;
            super();
            this.inbox = new Inbox(ref, endpoint);
         }
      }
   }

   public class MessageLoop implements Runnable {
      // $FF: synthetic field
      public final Dispatcher $outer;

      public void run() {
         try {
            while(true) {
               try {
                  while(true) {
                     EndpointData data = (EndpointData)this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer().org$apache$spark$rpc$netty$Dispatcher$$receivers().take();
                     EndpointData var8 = this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer().org$apache$spark$rpc$netty$Dispatcher$$PoisonPill();
                     if (data != null) {
                        if (data.equals(var8)) {
                           break;
                        }
                     } else if (var8 == null) {
                        break;
                     }

                     data.inbox().process(this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer());
                  }

                  this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer().org$apache$spark$rpc$netty$Dispatcher$$receivers().offer(this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer().org$apache$spark$rpc$netty$Dispatcher$$PoisonPill());
                  return;
               } catch (Throwable var11) {
                  Option var4 = NonFatal$.MODULE$.unapply(var11);
                  if (var4.isEmpty()) {
                     throw var11;
                  }

                  Throwable e = (Throwable)var4.get();
                  this.org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer().logError(new Serializable(this, e) {
                     public static final long serialVersionUID = 0L;
                     private final Throwable e$1;

                     public final String apply() {
                        return this.e$1.getMessage();
                     }

                     public {
                        this.e$1 = e$1;
                     }
                  }, e);
                  BoxedUnit var6 = BoxedUnit.UNIT;
                  continue;
               }
            }
         } catch (InterruptedException var12) {
         }
      }

      // $FF: synthetic method
      public Dispatcher org$apache$spark$rpc$netty$Dispatcher$MessageLoop$$$outer() {
         return this.$outer;
      }

      public MessageLoop() {
         if (Dispatcher.this == null) {
            throw null;
         } else {
            this.$outer = Dispatcher.this;
            super();
         }
      }
   }
}
