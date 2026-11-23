package org.apache.spark.rpc.netty;

import java.nio.ByteBuffer;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.rpc.RpcAddress;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u2Q!\u0001\u0002\u0001\u00051\u0011\u0011DU3n_R,g*\u001a;usJ\u00038mQ1mY\u000e{g\u000e^3yi*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0003\u0013\t\u0001\"AA\nOKR$\u0018P\u00159d\u0007\u0006dGnQ8oi\u0016DH\u000f\u0003\u0005\u0013\u0001\t\u0005\t\u0015!\u0003\u0015\u0003!qW\r\u001e;z\u000b:48\u0001\u0001\t\u0003\u001dUI!A\u0006\u0002\u0003\u00179+G\u000f^=Sa\u000e,eN\u001e\u0005\t1\u0001\u0011\t\u0011)A\u00053\u0005A1-\u00197mE\u0006\u001c7\u000e\u0005\u0002\u001b?5\t1D\u0003\u0002\u001d;\u000511\r\\5f]RT!A\b\u0004\u0002\u000f9,Go^8sW&\u0011\u0001e\u0007\u0002\u0014%B\u001c'+Z:q_:\u001cXmQ1mY\n\f7m\u001b\u0005\nE\u0001\u0011\t\u0011)A\u0005G\u001d\nQb]3oI\u0016\u0014\u0018\t\u001a3sKN\u001c\bC\u0001\u0013&\u001b\u0005!\u0011B\u0001\u0014\u0005\u0005)\u0011\u0006oY!eIJ,7o]\u0005\u0003E=AQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtD\u0003B\u0016-[9\u0002\"A\u0004\u0001\t\u000bIA\u0003\u0019\u0001\u000b\t\u000baA\u0003\u0019A\r\t\u000b\tB\u0003\u0019A\u0012\t\u000bA\u0002A\u0011K\u0019\u0002\tM,g\u000e\u001a\u000b\u0003ea\u0002\"a\r\u001c\u000e\u0003QR\u0011!N\u0001\u0006g\u000e\fG.Y\u0005\u0003oQ\u0012A!\u00168ji\")\u0011h\fa\u0001u\u00059Q.Z:tC\u001e,\u0007CA\u001a<\u0013\taDGA\u0002B]f\u0004"
)
public class RemoteNettyRpcCallContext extends NettyRpcCallContext {
   private final NettyRpcEnv nettyEnv;
   private final RpcResponseCallback callback;

   public void send(Object message) {
      ByteBuffer reply = this.nettyEnv.serialize(message);
      this.callback.onSuccess(reply);
   }

   public RemoteNettyRpcCallContext(NettyRpcEnv nettyEnv, RpcResponseCallback callback, RpcAddress senderAddress) {
      super(senderAddress);
      this.nettyEnv = nettyEnv;
      this.callback = callback;
   }
}
