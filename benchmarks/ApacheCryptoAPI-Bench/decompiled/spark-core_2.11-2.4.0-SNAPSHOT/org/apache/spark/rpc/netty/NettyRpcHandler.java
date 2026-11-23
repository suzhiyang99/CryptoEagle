package org.apache.spark.rpc.netty;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.network.server.RpcHandler;
import org.apache.spark.network.server.StreamManager;
import org.apache.spark.rpc.RpcAddress;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ea!B\u0001\u0003\u0001\ta!a\u0004(fiRL(\u000b]2IC:$G.\u001a:\u000b\u0005\r!\u0011!\u00028fiRL(BA\u0003\u0007\u0003\r\u0011\bo\u0019\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\u000b\u0011\u00059\u0019R\"A\b\u000b\u0005A\t\u0012AB:feZ,'O\u0003\u0002\u0013\r\u00059a.\u001a;x_J\\\u0017B\u0001\u000b\u0010\u0005)\u0011\u0006o\u0019%b]\u0012dWM\u001d\t\u0003-ei\u0011a\u0006\u0006\u00031\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\r\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u001f\u0003)!\u0017n\u001d9bi\u000eDWM]\u0002\u0001!\ty\u0002%D\u0001\u0003\u0013\t\t#A\u0001\u0006ESN\u0004\u0018\r^2iKJD\u0001b\t\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\t]\u0016$H/_#omB\u0011q$J\u0005\u0003M\t\u00111BT3uif\u0014\u0006oY#om\"A\u0001\u0006\u0001B\u0001B\u0003%\u0011&A\u0007tiJ,\u0017-\\'b]\u0006<WM\u001d\t\u0003\u001d)J!aK\b\u0003\u001bM#(/Z1n\u001b\u0006t\u0017mZ3s\u0011\u0015i\u0003\u0001\"\u0001/\u0003\u0019a\u0014N\\5u}Q!q\u0006M\u00193!\ty\u0002\u0001C\u0003\u001dY\u0001\u0007a\u0004C\u0003$Y\u0001\u0007A\u0005C\u0003)Y\u0001\u0007\u0011\u0006C\u00045\u0001\t\u0007I\u0011B\u001b\u0002\u001fI,Wn\u001c;f\u0003\u0012$'/Z:tKN,\u0012A\u000e\t\u0005oy\u0002\u0005)D\u00019\u0015\tI$(\u0001\u0006d_:\u001cWO\u001d:f]RT!a\u000f\u001f\u0002\tU$\u0018\u000e\u001c\u0006\u0002{\u0005!!.\u0019<b\u0013\ty\u0004HA\tD_:\u001cWO\u001d:f]RD\u0015m\u001d5NCB\u0004\"!\u0011\"\u000e\u0003\u0011I!a\u0011\u0003\u0003\u0015I\u00038-\u00113ee\u0016\u001c8\u000f\u0003\u0004F\u0001\u0001\u0006IAN\u0001\u0011e\u0016lw\u000e^3BI\u0012\u0014Xm]:fg\u0002BQa\u0012\u0001\u0005B!\u000bqA]3dK&4X\r\u0006\u0003J\u001fZs\u0006C\u0001&N\u001b\u0005Y%\"\u0001'\u0002\u000bM\u001c\u0017\r\\1\n\u00059[%\u0001B+oSRDQ\u0001\u0015$A\u0002E\u000baa\u00197jK:$\bC\u0001*U\u001b\u0005\u0019&B\u0001)\u0012\u0013\t)6KA\bUe\u0006t7\u000f]8si\u000ec\u0017.\u001a8u\u0011\u00159f\t1\u0001Y\u0003\u001diWm]:bO\u0016\u0004\"!\u0017/\u000e\u0003iS!a\u0017\u001f\u0002\u00079Lw.\u0003\u0002^5\nQ!)\u001f;f\u0005V4g-\u001a:\t\u000b}3\u0005\u0019\u00011\u0002\u0011\r\fG\u000e\u001c2bG.\u0004\"AU1\n\u0005\t\u001c&a\u0005*qGJ+7\u000f]8og\u0016\u001c\u0015\r\u001c7cC\u000e\\\u0007\"B$\u0001\t\u0003\"GcA%fM\")\u0001k\u0019a\u0001#\")qk\u0019a\u00011\")\u0001\u000e\u0001C\u0005S\u0006y\u0011N\u001c;fe:\fGNU3dK&4X\rF\u0002k[:\u0004\"aH6\n\u00051\u0014!A\u0004*fcV,7\u000f^'fgN\fw-\u001a\u0005\u0006!\u001e\u0004\r!\u0015\u0005\u0006/\u001e\u0004\r\u0001\u0017\u0005\u0006a\u0002!\t%]\u0001\u0011O\u0016$8\u000b\u001e:fC6l\u0015M\\1hKJ$\u0012!\u000b\u0005\u0006g\u0002!\t\u0005^\u0001\u0010Kb\u001cW\r\u001d;j_:\u001c\u0015-^4iiR!\u0011*^A\u0004\u0011\u00151(\u000f1\u0001x\u0003\u0015\u0019\u0017-^:f!\rA\u0018\u0011\u0001\b\u0003szt!A_?\u000e\u0003mT!\u0001`\u000f\u0002\rq\u0012xn\u001c;?\u0013\u0005a\u0015BA@L\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\u0001\u0002\u0006\tIA\u000b\u001b:po\u0006\u0014G.\u001a\u0006\u0003\u007f.CQ\u0001\u0015:A\u0002ECq!a\u0003\u0001\t\u0003\ni!A\u0007dQ\u0006tg.\u001a7BGRLg/\u001a\u000b\u0004\u0013\u0006=\u0001B\u0002)\u0002\n\u0001\u0007\u0011\u000bC\u0004\u0002\u0014\u0001!\t%!\u0006\u0002\u001f\rD\u0017M\u001c8fY&s\u0017m\u0019;jm\u0016$2!SA\f\u0011\u0019\u0001\u0016\u0011\u0003a\u0001#\u0002"
)
public class NettyRpcHandler extends RpcHandler implements Logging {
   private final Dispatcher dispatcher;
   private final NettyRpcEnv nettyEnv;
   private final StreamManager streamManager;
   private final ConcurrentHashMap<RpcAddress, RpcAddress> remoteAddresses;
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

   private ConcurrentHashMap<RpcAddress, RpcAddress> remoteAddresses() {
      return this.remoteAddresses;
   }

   public void receive(TransportClient client, ByteBuffer message, RpcResponseCallback callback) {
      RequestMessage messageToDispatch = this.internalReceive(client, message);
      this.dispatcher.postRemoteMessage(messageToDispatch, callback);
   }

   public void receive(TransportClient client, ByteBuffer message) {
      RequestMessage messageToDispatch = this.internalReceive(client, message);
      this.dispatcher.postOneWayMessage(messageToDispatch);
   }

   private RequestMessage internalReceive(TransportClient client, ByteBuffer message) {
      InetSocketAddress addr = (InetSocketAddress)client.getChannel().remoteAddress();
      Predef$.MODULE$.assert(addr != null);
      RpcAddress clientAddr = new RpcAddress(addr.getHostString(), addr.getPort());
      RequestMessage requestMessage = RequestMessage$.MODULE$.apply(this.nettyEnv, client, message);
      RequestMessage var10000;
      if (requestMessage.senderAddress() == null) {
         var10000 = new RequestMessage(clientAddr, requestMessage.receiver(), requestMessage.content());
      } else {
         RpcAddress remoteEnvAddress = requestMessage.senderAddress();
         if (this.remoteAddresses().putIfAbsent(clientAddr, remoteEnvAddress) == null) {
            this.dispatcher.postToAll(new RemoteProcessConnected(remoteEnvAddress));
         }

         var10000 = requestMessage;
      }

      return var10000;
   }

   public StreamManager getStreamManager() {
      return this.streamManager;
   }

   public void exceptionCaught(Throwable cause, TransportClient client) {
      InetSocketAddress addr = (InetSocketAddress)client.getChannel().remoteAddress();
      if (addr == null) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception before connecting to the client";
            }
         }, cause);
      } else {
         RpcAddress clientAddr = new RpcAddress(addr.getHostString(), addr.getPort());
         this.dispatcher.postToAll(new RemoteProcessConnectionError(cause, clientAddr));
         RpcAddress remoteEnvAddress = (RpcAddress)this.remoteAddresses().get(clientAddr);
         if (remoteEnvAddress != null) {
            this.dispatcher.postToAll(new RemoteProcessConnectionError(cause, remoteEnvAddress));
         }
      }

   }

   public void channelActive(TransportClient client) {
      InetSocketAddress addr = (InetSocketAddress)client.getChannel().remoteAddress();
      Predef$.MODULE$.assert(addr != null);
      RpcAddress clientAddr = new RpcAddress(addr.getHostString(), addr.getPort());
      this.dispatcher.postToAll(new RemoteProcessConnected(clientAddr));
   }

   public void channelInactive(TransportClient client) {
      InetSocketAddress addr = (InetSocketAddress)client.getChannel().remoteAddress();
      if (addr != null) {
         RpcAddress clientAddr = new RpcAddress(addr.getHostString(), addr.getPort());
         this.nettyEnv.removeOutbox(clientAddr);
         this.dispatcher.postToAll(new RemoteProcessDisconnected(clientAddr));
         RpcAddress remoteEnvAddress = (RpcAddress)this.remoteAddresses().remove(clientAddr);
         if (remoteEnvAddress != null) {
            this.dispatcher.postToAll(new RemoteProcessDisconnected(remoteEnvAddress));
         }
      }

   }

   public NettyRpcHandler(Dispatcher dispatcher, NettyRpcEnv nettyEnv, StreamManager streamManager) {
      this.dispatcher = dispatcher;
      this.nettyEnv = nettyEnv;
      this.streamManager = streamManager;
      Logging$class.$init$(this);
      this.remoteAddresses = new ConcurrentHashMap();
   }
}
