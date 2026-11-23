package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.concurrent.Promise;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2Q!\u0001\u0002\u0001\u00051\u0011\u0001\u0004T8dC2tU\r\u001e;z%B\u001c7)\u00197m\u0007>tG/\u001a=u\u0015\t\u0019A!A\u0003oKR$\u0018P\u0003\u0002\u0006\r\u0005\u0019!\u000f]2\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059yQ\"\u0001\u0002\n\u0005A\u0011!a\u0005(fiRL(\u000b]2DC2d7i\u001c8uKb$\b\"\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0019\u00035\u0019XM\u001c3fe\u0006#GM]3tg\u000e\u0001\u0001CA\u000b\u0017\u001b\u0005!\u0011BA\f\u0005\u0005)\u0011\u0006oY!eIJ,7o]\u0005\u0003%=A\u0001B\u0007\u0001\u0003\u0002\u0003\u0006IaG\u0001\u0002aB\u0019A$I\u0012\u000e\u0003uQ!AH\u0010\u0002\u0015\r|gnY;se\u0016tGOC\u0001!\u0003\u0015\u00198-\u00197b\u0013\t\u0011SDA\u0004Qe>l\u0017n]3\u0011\u0005\u0011*S\"A\u0010\n\u0005\u0019z\"aA!os\")\u0001\u0006\u0001C\u0001S\u00051A(\u001b8jiz\"2AK\u0016-!\tq\u0001\u0001C\u0003\u0013O\u0001\u0007A\u0003C\u0003\u001bO\u0001\u00071\u0004C\u0003/\u0001\u0011Es&\u0001\u0003tK:$GC\u0001\u00194!\t!\u0013'\u0003\u00023?\t!QK\\5u\u0011\u0015!T\u00061\u0001$\u0003\u001diWm]:bO\u0016\u0004"
)
public class LocalNettyRpcCallContext extends NettyRpcCallContext {
   private final Promise<Object> p;

   public void send(Object message) {
      this.p.success(message);
   }

   public LocalNettyRpcCallContext(RpcAddress senderAddress, Promise<Object> p) {
      super(senderAddress);
      this.p = p;
   }
}
