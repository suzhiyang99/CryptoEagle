package org.apache.spark.rpc.netty;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcEndpointAddress;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcTimeout;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.concurrent.Future;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ed!B\u0001\u0003\u0001\ta!a\u0005(fiRL(\u000b]2F]\u0012\u0004x.\u001b8u%\u00164'BA\u0002\u0005\u0003\u0015qW\r\u001e;z\u0015\t)a!A\u0002sa\u000eT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\b\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\"A!\u0003\u0001BC\u0002\u0013%A#\u0001\u0003d_:47\u0001A\u000b\u0002+A\u0011acF\u0007\u0002\r%\u0011\u0001D\u0002\u0002\n'B\f'o[\"p]\u001aD\u0001B\u0007\u0001\u0003\u0002\u0003\u0006I!F\u0001\u0006G>tg\r\t\u0015\u00033q\u0001\"!\b\u0011\u000e\u0003yQ\u0011aH\u0001\u0006g\u000e\fG.Y\u0005\u0003Cy\u0011\u0011\u0002\u001e:b]NLWM\u001c;\t\u0011\r\u0002!Q1A\u0005\n\u0011\nq\"\u001a8ea>Lg\u000e^!eIJ,7o]\u000b\u0002KA\u0011aBJ\u0005\u0003O\u0011\u0011!C\u00159d\u000b:$\u0007o\\5oi\u0006#GM]3tg\"A\u0011\u0006\u0001B\u0001B\u0003%Q%\u0001\tf]\u0012\u0004x.\u001b8u\u0003\u0012$'/Z:tA!A1\u0006\u0001BA\u0002\u0013%A&\u0001\u0005oKR$\u00180\u00128w+\u0005i\u0003C\u0001\u00180\u001b\u0005\u0011\u0011B\u0001\u0019\u0003\u0005-qU\r\u001e;z%B\u001cWI\u001c<\t\u0011I\u0002!\u00111A\u0005\nM\nAB\\3uif,eN^0%KF$\"\u0001N\u001c\u0011\u0005u)\u0014B\u0001\u001c\u001f\u0005\u0011)f.\u001b;\t\u000fa\n\u0014\u0011!a\u0001[\u0005\u0019\u0001\u0010J\u0019\t\u0011i\u0002!\u0011!Q!\n5\n\u0011B\\3uif,eN\u001e\u0011)\u0005eb\u0004CA\u000f>\u0013\tqdD\u0001\u0005w_2\fG/\u001b7fQ\tID\u0004C\u0003B\u0001\u0011\u0005!)\u0001\u0004=S:LGO\u0010\u000b\u0005\u0007\u0012+e\t\u0005\u0002/\u0001!)!\u0003\u0011a\u0001+!)1\u0005\u0011a\u0001K!)1\u0006\u0011a\u0001[!I\u0001\n\u0001a\u0001\u0002\u0004%\t!S\u0001\u0007G2LWM\u001c;\u0016\u0003)\u0003\"aS(\u000e\u00031S!\u0001S'\u000b\u000593\u0011a\u00028fi^|'o[\u0005\u0003!2\u0013q\u0002\u0016:b]N\u0004xN\u001d;DY&,g\u000e\u001e\u0005\n%\u0002\u0001\r\u00111A\u0005\u0002M\u000b!b\u00197jK:$x\fJ3r)\t!D\u000bC\u00049#\u0006\u0005\t\u0019\u0001&\t\rY\u0003\u0001\u0015)\u0003K\u0003\u001d\u0019G.[3oi\u0002B#!\u0016\u001f)\u0005Uc\u0002\"\u0002.\u0001\t\u0003Z\u0016aB1eIJ,7o]\u000b\u00029B\u0011a\"X\u0005\u0003=\u0012\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011\u0015\u0001\u0007\u0001\"\u0003b\u0003)\u0011X-\u00193PE*,7\r\u001e\u000b\u0003i\tDQaY0A\u0002\u0011\f!!\u001b8\u0011\u0005\u0015TW\"\u00014\u000b\u0005\u001dD\u0017AA5p\u0015\u0005I\u0017\u0001\u00026bm\u0006L!a\u001b4\u0003#=\u0013'.Z2u\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u0003n\u0001\u0011%a.A\u0006xe&$Xm\u00142kK\u000e$HC\u0001\u001bp\u0011\u0015\u0001H\u000e1\u0001r\u0003\ryW\u000f\u001e\t\u0003KJL!a\u001d4\u0003%=\u0013'.Z2u\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006k\u0002!\tE^\u0001\u0005]\u0006lW-F\u0001x!\tA8P\u0004\u0002\u001es&\u0011!PH\u0001\u0007!J,G-\u001a4\n\u0005ql(AB*ue&twM\u0003\u0002{=!1q\u0010\u0001C!\u0003\u0003\t1!Y:l+\u0011\t\u0019!a\u0006\u0015\r\u0005\u0015\u0011\u0011HA\u001f)\u0011\t9!!\u000b\u0011\r\u0005%\u0011qBA\n\u001b\t\tYAC\u0002\u0002\u000ey\t!bY8oGV\u0014(/\u001a8u\u0013\u0011\t\t\"a\u0003\u0003\r\u0019+H/\u001e:f!\u0011\t)\"a\u0006\r\u0001\u00119\u0011\u0011\u0004@C\u0002\u0005m!!\u0001+\u0012\t\u0005u\u00111\u0005\t\u0004;\u0005}\u0011bAA\u0011=\t9aj\u001c;iS:<\u0007cA\u000f\u0002&%\u0019\u0011q\u0005\u0010\u0003\u0007\u0005s\u0017\u0010C\u0005\u0002,y\f\t\u0011q\u0001\u0002.\u0005QQM^5eK:\u001cW\rJ\u001a\u0011\r\u0005=\u0012QGA\n\u001b\t\t\tDC\u0002\u00024y\tqA]3gY\u0016\u001cG/\u0003\u0003\u00028\u0005E\"\u0001C\"mCN\u001cH+Y4\t\u000f\u0005mb\u00101\u0001\u0002$\u00059Q.Z:tC\u001e,\u0007bBA }\u0002\u0007\u0011\u0011I\u0001\bi&lWm\\;u!\rq\u00111I\u0005\u0004\u0003\u000b\"!A\u0003*qGRKW.Z8vi\"9\u0011\u0011\n\u0001\u0005B\u0005-\u0013\u0001B:f]\u0012$2\u0001NA'\u0011!\tY$a\u0012A\u0002\u0005\r\u0002bBA)\u0001\u0011\u0005\u00131K\u0001\ti>\u001cFO]5oOR\tq\u000fC\u0004\u0002X\u0001!)%!\u0017\u0002\r\u0015\fX/\u00197t)\u0011\tY&!\u0019\u0011\u0007u\ti&C\u0002\u0002`y\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005\u0002d\u0005U\u0003\u0019AA\u0012\u0003\u0011!\b.\u0019;\t\u000f\u0005\u001d\u0004\u0001\"\u0012\u0002j\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002lA\u0019Q$!\u001c\n\u0007\u0005=dDA\u0002J]R\u0004"
)
public class NettyRpcEndpointRef extends RpcEndpointRef {
   private final transient SparkConf conf;
   private final RpcEndpointAddress endpointAddress;
   private transient volatile NettyRpcEnv nettyEnv;
   private transient volatile TransportClient client;

   private SparkConf conf() {
      return this.conf;
   }

   private RpcEndpointAddress endpointAddress() {
      return this.endpointAddress;
   }

   private NettyRpcEnv nettyEnv() {
      return this.nettyEnv;
   }

   private void nettyEnv_$eq(NettyRpcEnv x$1) {
      this.nettyEnv = x$1;
   }

   public TransportClient client() {
      return this.client;
   }

   public void client_$eq(TransportClient x$1) {
      this.client = x$1;
   }

   public RpcAddress address() {
      return this.endpointAddress().rpcAddress() == null ? null : this.endpointAddress().rpcAddress();
   }

   private void readObject(ObjectInputStream in) {
      in.defaultReadObject();
      this.nettyEnv_$eq((NettyRpcEnv)NettyRpcEnv$.MODULE$.currentEnv().value());
      this.client_$eq((TransportClient)NettyRpcEnv$.MODULE$.currentClient().value());
   }

   private void writeObject(ObjectOutputStream out) {
      out.defaultWriteObject();
   }

   public String name() {
      return this.endpointAddress().name();
   }

   public <T> Future<T> ask(Object message, RpcTimeout timeout, ClassTag<T> evidence$3) {
      return this.nettyEnv().<T>ask(new RequestMessage(this.nettyEnv().address(), this, message), timeout, evidence$3);
   }

   public void send(Object message) {
      Predef$.MODULE$.require(message != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Message is null";
         }
      });
      this.nettyEnv().send(new RequestMessage(this.nettyEnv().address(), this, message));
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"NettyRpcEndpointRef(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.endpointAddress()}));
   }

   public final boolean equals(Object that) {
      boolean var4;
      if (that instanceof NettyRpcEndpointRef) {
         boolean var6;
         label20: {
            label19: {
               NettyRpcEndpointRef var3 = (NettyRpcEndpointRef)that;
               RpcEndpointAddress var10000 = this.endpointAddress();
               RpcEndpointAddress var5 = var3.endpointAddress();
               if (var10000 == null) {
                  if (var5 == null) {
                     break label19;
                  }
               } else if (var10000.equals(var5)) {
                  break label19;
               }

               var6 = false;
               break label20;
            }

            var6 = true;
         }

         var4 = var6;
      } else {
         var4 = false;
      }

      return var4;
   }

   public final int hashCode() {
      return this.endpointAddress() == null ? 0 : this.endpointAddress().hashCode();
   }

   public NettyRpcEndpointRef(SparkConf conf, RpcEndpointAddress endpointAddress, NettyRpcEnv nettyEnv) {
      this.conf = conf;
      this.endpointAddress = endpointAddress;
      this.nettyEnv = nettyEnv;
      super(conf);
   }
}
