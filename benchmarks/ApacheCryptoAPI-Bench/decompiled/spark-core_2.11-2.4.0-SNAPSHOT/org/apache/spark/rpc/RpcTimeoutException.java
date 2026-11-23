package org.apache.spark.rpc;

import java.util.concurrent.TimeoutException;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-2Q!\u0001\u0002\u0001\u0005)\u00111C\u00159d)&lWm\\;u\u000bb\u001cW\r\u001d;j_:T!a\u0001\u0003\u0002\u0007I\u00048M\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r'5\tQB\u0003\u0002\u000f\u001f\u0005Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005A\t\u0012\u0001B;uS2T\u0011AE\u0001\u0005U\u00064\u0018-\u0003\u0002\u0015\u001b\t\u0001B+[7f_V$X\t_2faRLwN\u001c\u0005\t-\u0001\u0011\t\u0011)A\u00051\u00059Q.Z:tC\u001e,7\u0001\u0001\t\u00033}q!AG\u000f\u000e\u0003mQ\u0011\u0001H\u0001\u0006g\u000e\fG.Y\u0005\u0003=m\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0011\"\u0005\u0019\u0019FO]5oO*\u0011ad\u0007\u0005\tG\u0001\u0011\t\u0011)A\u0005\u0017\u0005)1-Y;tK\")Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"2aJ\u0015+!\tA\u0003!D\u0001\u0003\u0011\u00151B\u00051\u0001\u0019\u0011\u0015\u0019C\u00051\u0001\f\u0001"
)
public class RpcTimeoutException extends TimeoutException {
   public RpcTimeoutException(String message, TimeoutException cause) {
      super(message);
      this.initCause(cause);
   }
}
