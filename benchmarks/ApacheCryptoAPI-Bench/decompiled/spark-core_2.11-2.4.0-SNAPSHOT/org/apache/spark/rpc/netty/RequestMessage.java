package org.apache.spark.rpc.netty;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.util.ByteBufferOutputStream;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u4Q!\u0001\u0002\u0001\u00051\u0011aBU3rk\u0016\u001cH/T3tg\u0006<WM\u0003\u0002\u0004\t\u0005)a.\u001a;us*\u0011QAB\u0001\u0004eB\u001c'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0003\u0005\u0015\u0001\t\u0015\r\u0011\"\u0001\u0017\u00035\u0019XM\u001c3fe\u0006#GM]3tg\u000e\u0001Q#A\f\u0011\u0005aIR\"\u0001\u0003\n\u0005i!!A\u0003*qG\u0006#GM]3tg\"AA\u0004\u0001B\u0001B\u0003%q#\u0001\btK:$WM]!eIJ,7o\u001d\u0011\t\u0011y\u0001!Q1A\u0005\u0002}\t\u0001B]3dK&4XM]\u000b\u0002AA\u0011\u0011EI\u0007\u0002\u0005%\u00111E\u0001\u0002\u0014\u001d\u0016$H/\u001f*qG\u0016sG\r]8j]R\u0014VM\u001a\u0005\tK\u0001\u0011\t\u0011)A\u0005A\u0005I!/Z2fSZ,'\u000f\t\u0005\tO\u0001\u0011)\u0019!C\u0001Q\u000591m\u001c8uK:$X#A\u0015\u0011\u00059Q\u0013BA\u0016\u0010\u0005\r\te.\u001f\u0005\t[\u0001\u0011\t\u0011)A\u0005S\u0005A1m\u001c8uK:$\b\u0005C\u00030\u0001\u0011\u0005\u0001'\u0001\u0004=S:LGO\u0010\u000b\u0005cI\u001aD\u0007\u0005\u0002\"\u0001!)AC\fa\u0001/!)aD\fa\u0001A!)qE\fa\u0001S!)a\u0007\u0001C\u0001o\u0005I1/\u001a:jC2L'0\u001a\u000b\u0003q\u0001\u0003\"!\u000f \u000e\u0003iR!a\u000f\u001f\u0002\u00079LwNC\u0001>\u0003\u0011Q\u0017M^1\n\u0005}R$A\u0003\"zi\u0016\u0014UO\u001a4fe\")\u0011)\u000ea\u0001\u0005\u0006Aa.\u001a;us\u0016sg\u000f\u0005\u0002\"\u0007&\u0011AI\u0001\u0002\f\u001d\u0016$H/\u001f*qG\u0016sg\u000fC\u0003G\u0001\u0011%q)A\bxe&$XM\u00159d\u0003\u0012$'/Z:t)\rA5j\u0015\t\u0003\u001d%K!AS\b\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u0019\u0016\u0003\r!T\u0001\u0004_V$\bC\u0001(R\u001b\u0005y%B\u0001)=\u0003\tIw.\u0003\u0002S\u001f\n\u0001B)\u0019;b\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006)\u0016\u0003\raF\u0001\u000beB\u001c\u0017\t\u001a3sKN\u001c\b\"\u0002,\u0001\t\u0003:\u0016\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003a\u0003\"!\u0017/\u000f\u00059Q\u0016BA.\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011QL\u0018\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005m{qA\u00021\u0003\u0011\u0003\u0011\u0011-\u0001\bSKF,Xm\u001d;NKN\u001c\u0018mZ3\u0011\u0005\u0005\u0012gAB\u0001\u0003\u0011\u0003\u00111m\u0005\u0002c\u001b!)qF\u0019C\u0001KR\t\u0011\rC\u0003hE\u0012%\u0001.\u0001\bsK\u0006$'\u000b]2BI\u0012\u0014Xm]:\u0015\u0005]I\u0007\"\u00026g\u0001\u0004Y\u0017AA5o!\tqE.\u0003\u0002n\u001f\nyA)\u0019;b\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u0003pE\u0012\u0005\u0001/A\u0003baBd\u0017\u0010\u0006\u00032cJ\\\b\"B!o\u0001\u0004\u0011\u0005\"B:o\u0001\u0004!\u0018AB2mS\u0016tG\u000f\u0005\u0002vs6\taO\u0003\u0002to*\u0011\u0001PB\u0001\b]\u0016$xo\u001c:l\u0013\tQhOA\bUe\u0006t7\u000f]8si\u000ec\u0017.\u001a8u\u0011\u0015ah\u000e1\u00019\u0003\u0015\u0011\u0017\u0010^3t\u0001"
)
public class RequestMessage {
   private final RpcAddress senderAddress;
   private final NettyRpcEndpointRef receiver;
   private final Object content;

   public static RequestMessage apply(NettyRpcEnv var0, TransportClient var1, ByteBuffer var2) {
      return RequestMessage$.MODULE$.apply(var0, var1, var2);
   }

   public RpcAddress senderAddress() {
      return this.senderAddress;
   }

   public NettyRpcEndpointRef receiver() {
      return this.receiver;
   }

   public Object content() {
      return this.content;
   }

   public ByteBuffer serialize(NettyRpcEnv nettyEnv) {
      ByteBufferOutputStream bos = new ByteBufferOutputStream();
      DataOutputStream out = new DataOutputStream(bos);

      try {
         this.writeRpcAddress(out, this.senderAddress());
         this.writeRpcAddress(out, this.receiver().address());
         out.writeUTF(this.receiver().name());
         SerializationStream s = nettyEnv.serializeStream(out);

         try {
            s.writeObject(this.content(), ClassTag$.MODULE$.Any());
         } finally {
            s.close();
         }
      } finally {
         out.close();
      }

      return bos.toByteBuffer();
   }

   private void writeRpcAddress(DataOutputStream out, RpcAddress rpcAddress) {
      if (rpcAddress == null) {
         out.writeBoolean(false);
      } else {
         out.writeBoolean(true);
         out.writeUTF(rpcAddress.host());
         out.writeInt(rpcAddress.port());
      }

   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"RequestMessage(", ", ", ", ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.senderAddress(), this.receiver(), this.content()}));
   }

   public RequestMessage(RpcAddress senderAddress, NettyRpcEndpointRef receiver, Object content) {
      this.senderAddress = senderAddress;
      this.receiver = receiver;
      this.content = content;
   }
}
