package org.apache.spark.rpc.netty;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkException;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.rpc.RpcAddress;
import scala.Option;
import scala.Predef$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005c!B\u0001\u0003\u0001\ta!AB(vi\n|\u0007P\u0003\u0002\u0004\t\u0005)a.\u001a;us*\u0011QAB\u0001\u0004eB\u001c'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0003\u0005\u0015\u0001\t\u0005\t\u0015!\u0003\u0017\u0003!qW\r\u001e;z\u000b:48\u0001\u0001\t\u0003/ai\u0011AA\u0005\u00033\t\u00111BT3uif\u0014\u0006oY#om\"A1\u0004\u0001BC\u0002\u0013\u0005A$A\u0004bI\u0012\u0014Xm]:\u0016\u0003u\u0001\"AH\u0010\u000e\u0003\u0011I!\u0001\t\u0003\u0003\u0015I\u00038-\u00113ee\u0016\u001c8\u000f\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003\u001e\u0003!\tG\r\u001a:fgN\u0004\u0003\"\u0002\u0013\u0001\t\u0003)\u0013A\u0002\u001fj]&$h\bF\u0002'O!\u0002\"a\u0006\u0001\t\u000bQ\u0019\u0003\u0019\u0001\f\t\u000bm\u0019\u0003\u0019A\u000f\t\u000f)\u0002!\u0019!C\u0005W\u0005AQ.Z:tC\u001e,7/F\u0001-!\ri#\u0007N\u0007\u0002])\u0011q\u0006M\u0001\u0005kRLGNC\u00012\u0003\u0011Q\u0017M^1\n\u0005Mr#A\u0003'j].,G\rT5tiB\u0011q#N\u0005\u0003m\t\u0011QbT;uE>DX*Z:tC\u001e,\u0007B\u0002\u001d\u0001A\u0003%A&A\u0005nKN\u001c\u0018mZ3tA!\"qG\u000f#F!\tY$)D\u0001=\u0015\tid(\u0001\u0006d_:\u001cWO\u001d:f]RT!a\u0010!\u0002\u0015\u0005tgn\u001c;bi&|gNC\u0001B\u0003\u0015Q\u0017M^1y\u0013\t\u0019EHA\u0005Hk\u0006\u0014H-\u001a3Cs\u0006)a/\u00197vK\u0006\na)\u0001\u0003uQ&\u001c\bb\u0002%\u0001\u0001\u0004%I!S\u0001\u0007G2LWM\u001c;\u0016\u0003)\u0003\"aS(\u000e\u00031S!\u0001S'\u000b\u000593\u0011a\u00028fi^|'o[\u0005\u0003!2\u0013q\u0002\u0016:b]N\u0004xN\u001d;DY&,g\u000e\u001e\u0005\b%\u0002\u0001\r\u0011\"\u0003T\u0003)\u0019G.[3oi~#S-\u001d\u000b\u0003)^\u0003\"AD+\n\u0005Y{!\u0001B+oSRDq\u0001W)\u0002\u0002\u0003\u0007!*A\u0002yIEBaA\u0017\u0001!B\u0013Q\u0015aB2mS\u0016tG\u000f\t\u0015\u00053j\"U\tC\u0004^\u0001\u0001\u0007I\u0011\u00020\u0002\u001b\r|gN\\3di\u001a+H/\u001e:f+\u0005y\u0006c\u00011c)6\t\u0011M\u0003\u0002>]%\u00111-\u0019\u0002\u0007\rV$XO]3\t\u000f\u0015\u0004\u0001\u0019!C\u0005M\u0006\t2m\u001c8oK\u000e$h)\u001e;ve\u0016|F%Z9\u0015\u0005Q;\u0007b\u0002-e\u0003\u0003\u0005\ra\u0018\u0005\u0007S\u0002\u0001\u000b\u0015B0\u0002\u001d\r|gN\\3di\u001a+H/\u001e:fA!\"\u0001N\u000f#F\u0011\u001da\u0007\u00011A\u0005\n5\fqa\u001d;paB,G-F\u0001o!\tqq.\u0003\u0002q\u001f\t9!i\\8mK\u0006t\u0007b\u0002:\u0001\u0001\u0004%Ia]\u0001\fgR|\u0007\u000f]3e?\u0012*\u0017\u000f\u0006\u0002Ui\"9\u0001,]A\u0001\u0002\u0004q\u0007B\u0002<\u0001A\u0003&a.\u0001\u0005ti>\u0004\b/\u001a3!Q\u0011)(\bR#\t\u000fe\u0004\u0001\u0019!C\u0005[\u0006AAM]1j]&tw\rC\u0004|\u0001\u0001\u0007I\u0011\u0002?\u0002\u0019\u0011\u0014\u0018-\u001b8j]\u001e|F%Z9\u0015\u0005Qk\bb\u0002-{\u0003\u0003\u0005\rA\u001c\u0005\u0007\u007f\u0002\u0001\u000b\u0015\u00028\u0002\u0013\u0011\u0014\u0018-\u001b8j]\u001e\u0004\u0003\u0006\u0002@;\t\u0016Cq!!\u0002\u0001\t\u0003\t9!\u0001\u0003tK:$Gc\u0001+\u0002\n!9\u00111BA\u0002\u0001\u0004!\u0014aB7fgN\fw-\u001a\u0005\b\u0003\u001f\u0001A\u0011BA\t\u0003-!'/Y5o\u001fV$(m\u001c=\u0015\u0003QCq!!\u0006\u0001\t\u0013\t\t\"A\tmCVt7\r[\"p]:,7\r\u001e+bg.Dq!!\u0007\u0001\t\u0013\tY\"\u0001\u000biC:$G.\u001a(fi^|'o\u001b$bS2,(/\u001a\u000b\u0004)\u0006u\u0001\u0002CA\u0010\u0003/\u0001\r!!\t\u0002\u0003\u0015\u0004B!a\t\u000249!\u0011QEA\u0018\u001d\u0011\t9#!\f\u000e\u0005\u0005%\"bAA\u0016+\u00051AH]8pizJ\u0011\u0001E\u0005\u0004\u0003cy\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003k\t9DA\u0005UQJ|w/\u00192mK*\u0019\u0011\u0011G\b\t\u000f\u0005m\u0002\u0001\"\u0003\u0002\u0012\u0005Y1\r\\8tK\u000ec\u0017.\u001a8u\u0011\u001d\ty\u0004\u0001C\u0001\u0003#\tAa\u001d;pa\u0002"
)
public class Outbox {
   public final NettyRpcEnv org$apache$spark$rpc$netty$Outbox$$nettyEnv;
   private final RpcAddress address;
   @GuardedBy("this")
   private final LinkedList<OutboxMessage> messages;
   @GuardedBy("this")
   private TransportClient org$apache$spark$rpc$netty$Outbox$$client;
   @GuardedBy("this")
   private Future<BoxedUnit> org$apache$spark$rpc$netty$Outbox$$connectFuture;
   @GuardedBy("this")
   private boolean org$apache$spark$rpc$netty$Outbox$$stopped;
   @GuardedBy("this")
   private boolean draining;

   public RpcAddress address() {
      return this.address;
   }

   private LinkedList<OutboxMessage> messages() {
      return this.messages;
   }

   private TransportClient org$apache$spark$rpc$netty$Outbox$$client() {
      return this.org$apache$spark$rpc$netty$Outbox$$client;
   }

   public void org$apache$spark$rpc$netty$Outbox$$client_$eq(TransportClient x$1) {
      this.org$apache$spark$rpc$netty$Outbox$$client = x$1;
   }

   private Future<BoxedUnit> org$apache$spark$rpc$netty$Outbox$$connectFuture() {
      return this.org$apache$spark$rpc$netty$Outbox$$connectFuture;
   }

   public void org$apache$spark$rpc$netty$Outbox$$connectFuture_$eq(Future<BoxedUnit> x$1) {
      this.org$apache$spark$rpc$netty$Outbox$$connectFuture = x$1;
   }

   public boolean org$apache$spark$rpc$netty$Outbox$$stopped() {
      return this.org$apache$spark$rpc$netty$Outbox$$stopped;
   }

   private void org$apache$spark$rpc$netty$Outbox$$stopped_$eq(boolean x$1) {
      this.org$apache$spark$rpc$netty$Outbox$$stopped = x$1;
   }

   private boolean draining() {
      return this.draining;
   }

   private void draining_$eq(boolean x$1) {
      this.draining = x$1;
   }

   public void send(OutboxMessage message) {
      Boolean var4;
      synchronized(this) {
         Boolean var10000;
         if (this.org$apache$spark$rpc$netty$Outbox$$stopped()) {
            var10000 = BoxesRunTime.boxToBoolean(true);
         } else {
            this.messages().add(message);
            var10000 = BoxesRunTime.boxToBoolean(false);
         }

         var4 = var10000;
      }

      boolean dropped = BoxesRunTime.unboxToBoolean(var4);
      if (dropped) {
         message.onFailure(new SparkException("Message is dropped because Outbox is stopped"));
      } else {
         this.org$apache$spark$rpc$netty$Outbox$$drainOutbox();
      }

   }

   public void org$apache$spark$rpc$netty$Outbox$$drainOutbox() {
      OutboxMessage message = null;
      synchronized(this) {
         if (this.org$apache$spark$rpc$netty$Outbox$$stopped()) {
            return;
         }

         if (this.org$apache$spark$rpc$netty$Outbox$$connectFuture() != null) {
            return;
         }

         if (this.org$apache$spark$rpc$netty$Outbox$$client() == null) {
            this.launchConnectTask();
            return;
         }

         if (this.draining()) {
            return;
         }

         message = (OutboxMessage)this.messages().poll();
         if (message == null) {
            return;
         }

         this.draining_$eq(true);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      while(true) {
         try {
            TransportClient var10;
            synchronized(this) {
               var10 = this.org$apache$spark$rpc$netty$Outbox$$client();
            }

            TransportClient _client = var10;
            if (_client == null) {
               Predef$.MODULE$.assert(this.org$apache$spark$rpc$netty$Outbox$$stopped());
            } else {
               message.sendWith(_client);
            }
         } catch (Throwable var19) {
            Option var5 = NonFatal$.MODULE$.unapply(var19);
            if (var5.isEmpty()) {
               throw var19;
            }

            Throwable e = (Throwable)var5.get();
            this.org$apache$spark$rpc$netty$Outbox$$handleNetworkFailure(e);
            return;
         }

         synchronized(this) {
            if (this.org$apache$spark$rpc$netty$Outbox$$stopped()) {
               return;
            }

            message = (OutboxMessage)this.messages().poll();
            if (message == null) {
               this.draining_$eq(false);
               return;
            }

            BoxedUnit var21 = BoxedUnit.UNIT;
         }
      }
   }

   private void launchConnectTask() {
      this.org$apache$spark$rpc$netty$Outbox$$connectFuture_$eq(this.org$apache$spark$rpc$netty$Outbox$$nettyEnv.clientConnectionExecutor().submit(new Callable<BoxedUnit>(this) {
         // $FF: synthetic field
         private final Outbox $outer;

         public void call() {
            try {
               TransportClient _client = this.$outer.org$apache$spark$rpc$netty$Outbox$$nettyEnv.createClient(this.$outer.address());
               synchronized(this.$outer) {
                  this.$outer.org$apache$spark$rpc$netty$Outbox$$client_$eq(_client);
                  if (this.$outer.org$apache$spark$rpc$netty$Outbox$$stopped()) {
                     this.$outer.org$apache$spark$rpc$netty$Outbox$$closeClient();
                     BoxedUnit var19 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var20 = BoxedUnit.UNIT;
                  }
               }
            } catch (Throwable var18) {
               if (var18 instanceof InterruptedException) {
                  return;
               }

               Option var4 = NonFatal$.MODULE$.unapply(var18);
               if (var4.isEmpty()) {
                  throw var18;
               }

               Throwable e = (Throwable)var4.get();
               synchronized(this.$outer) {
                  this.$outer.org$apache$spark$rpc$netty$Outbox$$connectFuture_$eq((Future)null);
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               }

               this.$outer.org$apache$spark$rpc$netty$Outbox$$handleNetworkFailure(e);
               return;
            }

            synchronized(this.$outer) {
               this.$outer.org$apache$spark$rpc$netty$Outbox$$connectFuture_$eq((Future)null);
               BoxedUnit var21 = BoxedUnit.UNIT;
            }

            this.$outer.org$apache$spark$rpc$netty$Outbox$$drainOutbox();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
   }

   public void org$apache$spark$rpc$netty$Outbox$$handleNetworkFailure(Throwable e) {
      synchronized(this) {
         Predef$.MODULE$.assert(this.org$apache$spark$rpc$netty$Outbox$$connectFuture() == null);
         if (this.org$apache$spark$rpc$netty$Outbox$$stopped()) {
            return;
         }

         this.org$apache$spark$rpc$netty$Outbox$$stopped_$eq(true);
         this.org$apache$spark$rpc$netty$Outbox$$closeClient();
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.org$apache$spark$rpc$netty$Outbox$$nettyEnv.removeOutbox(this.address());

      for(OutboxMessage message = (OutboxMessage)this.messages().poll(); message != null; message = (OutboxMessage)this.messages().poll()) {
         message.onFailure(e);
      }

      Predef$.MODULE$.assert(this.messages().isEmpty());
   }

   public synchronized void org$apache$spark$rpc$netty$Outbox$$closeClient() {
      this.org$apache$spark$rpc$netty$Outbox$$client_$eq((TransportClient)null);
   }

   public void stop() {
      synchronized(this) {
         if (this.org$apache$spark$rpc$netty$Outbox$$stopped()) {
            return;
         }

         this.org$apache$spark$rpc$netty$Outbox$$stopped_$eq(true);
         if (this.org$apache$spark$rpc$netty$Outbox$$connectFuture() == null) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            BoxesRunTime.boxToBoolean(this.org$apache$spark$rpc$netty$Outbox$$connectFuture().cancel(true));
         }

         this.org$apache$spark$rpc$netty$Outbox$$closeClient();
         BoxedUnit var4 = BoxedUnit.UNIT;
      }

      for(OutboxMessage message = (OutboxMessage)this.messages().poll(); message != null; message = (OutboxMessage)this.messages().poll()) {
         message.onFailure(new SparkException("Message is dropped because Outbox is stopped"));
      }

   }

   public Outbox(NettyRpcEnv nettyEnv, RpcAddress address) {
      this.org$apache$spark$rpc$netty$Outbox$$nettyEnv = nettyEnv;
      this.address = address;
      this.messages = new LinkedList();
      this.org$apache$spark$rpc$netty$Outbox$$client = null;
      this.org$apache$spark$rpc$netty$Outbox$$connectFuture = null;
      this.org$apache$spark$rpc$netty$Outbox$$stopped = false;
      this.draining = false;
   }
}
