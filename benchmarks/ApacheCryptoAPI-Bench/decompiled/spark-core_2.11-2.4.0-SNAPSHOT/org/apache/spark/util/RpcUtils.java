package org.apache.spark.util;

import org.apache.spark.SparkConf;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcTimeout;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a;a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001\u0003*qGV#\u0018\u000e\\:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003\u0011I\u00038-\u0016;jYN\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000biaA\u0011A\u000e\u0002\u001b5\f7.\u001a#sSZ,'OU3g)\u0011a\"eK\u0019\u0011\u0005u\u0001S\"\u0001\u0010\u000b\u0005}!\u0011a\u0001:qG&\u0011\u0011E\b\u0002\u000f%B\u001cWI\u001c3q_&tGOU3g\u0011\u0015\u0019\u0013\u00041\u0001%\u0003\u0011q\u0017-\\3\u0011\u0005\u0015BcB\u0001\t'\u0013\t9\u0013#\u0001\u0004Qe\u0016$WMZ\u0005\u0003S)\u0012aa\u0015;sS:<'BA\u0014\u0012\u0011\u0015a\u0013\u00041\u0001.\u0003\u0011\u0019wN\u001c4\u0011\u00059zS\"\u0001\u0003\n\u0005A\"!!C*qCJ\\7i\u001c8g\u0011\u0015\u0011\u0014\u00041\u00014\u0003\u0019\u0011\boY#omB\u0011Q\u0004N\u0005\u0003ky\u0011aA\u00159d\u000b:4\b\"B\u001c\r\t\u0003A\u0014A\u00038v[J+GO]5fgR\u0011\u0011\b\u0010\t\u0003!iJ!aO\t\u0003\u0007%sG\u000fC\u0003-m\u0001\u0007Q\u0006C\u0003?\u0019\u0011\u0005q(A\u0006sKR\u0014\u0018pV1ji6\u001bHC\u0001!D!\t\u0001\u0012)\u0003\u0002C#\t!Aj\u001c8h\u0011\u0015aS\b1\u0001.\u0011\u0015)E\u0002\"\u0001G\u00035\t7o\u001b*qGRKW.Z8viR\u0011qI\u0013\t\u0003;!K!!\u0013\u0010\u0003\u0015I\u00038\rV5nK>,H\u000fC\u0003-\t\u0002\u0007Q\u0006C\u0003M\u0019\u0011\u0005Q*\u0001\tm_>\\W\u000f\u001d*qGRKW.Z8viR\u0011qI\u0014\u0005\u0006Y-\u0003\r!\f\u0005\b!2\u0011\r\u0011\"\u0003R\u0003Yi\u0015\tW0N\u000bN\u001b\u0016iR#`'&SViX%O?6\u0013U#A\u001d\t\rMc\u0001\u0015!\u0003:\u0003]i\u0015\tW0N\u000bN\u001b\u0016iR#`'&SViX%O?6\u0013\u0005\u0005C\u0003V\u0019\u0011\u0005a+A\nnCblUm]:bO\u0016\u001c\u0016N_3CsR,7\u000f\u0006\u0002:/\")A\u0006\u0016a\u0001[\u0001"
)
public final class RpcUtils {
   public static int maxMessageSizeBytes(SparkConf var0) {
      return RpcUtils$.MODULE$.maxMessageSizeBytes(var0);
   }

   public static RpcTimeout lookupRpcTimeout(SparkConf var0) {
      return RpcUtils$.MODULE$.lookupRpcTimeout(var0);
   }

   public static RpcTimeout askRpcTimeout(SparkConf var0) {
      return RpcUtils$.MODULE$.askRpcTimeout(var0);
   }

   public static long retryWaitMs(SparkConf var0) {
      return RpcUtils$.MODULE$.retryWaitMs(var0);
   }

   public static int numRetries(SparkConf var0) {
      return RpcUtils$.MODULE$.numRetries(var0);
   }

   public static RpcEndpointRef makeDriverRef(String var0, SparkConf var1, RpcEnv var2) {
      return RpcUtils$.MODULE$.makeDriverRef(var0, var1, var2);
   }
}
