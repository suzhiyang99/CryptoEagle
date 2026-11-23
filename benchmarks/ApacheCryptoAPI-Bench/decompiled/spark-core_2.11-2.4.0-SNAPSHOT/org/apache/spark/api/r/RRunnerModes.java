package org.apache.spark.api.r;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\u0019a\u0011\u0001\u0004*Sk:tWM]'pI\u0016\u001c(BA\u0002\u0005\u0003\u0005\u0011(BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011AB\u0015*v]:,'/T8eKN\u001c\"AD\t\u0011\u0005I)R\"A\n\u000b\u0003Q\tQa]2bY\u0006L!AF\n\u0003\r\u0005s\u0017PU3g\u0011\u0015Ab\u0002\"\u0001\u001b\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0007\t\u000fqq!\u0019!C\u0001;\u0005\u0019!\u000b\u0012#\u0016\u0003y\u0001\"AE\u0010\n\u0005\u0001\u001a\"aA%oi\"1!E\u0004Q\u0001\ny\tAA\u0015#EA!9AE\u0004b\u0001\n\u0003i\u0012\u0001\u0005#B)\u00063%+Q'F?\u0012\u000b\u0005\u000b\u0015'Z\u0011\u00191c\u0002)A\u0005=\u0005\tB)\u0011+B\rJ\u000bU*R0E\u0003B\u0003F*\u0017\u0011\t\u000f!r!\u0019!C\u0001;\u0005\u0001B)\u0011+B\rJ\u000bU*R0H\u0003B\u0003F*\u0017\u0005\u0007U9\u0001\u000b\u0011\u0002\u0010\u0002#\u0011\u000bE+\u0011$S\u00036+ulR!Q!2K\u0006\u0005"
)
public final class RRunnerModes {
   public static int DATAFRAME_GAPPLY() {
      return RRunnerModes$.MODULE$.DATAFRAME_GAPPLY();
   }

   public static int DATAFRAME_DAPPLY() {
      return RRunnerModes$.MODULE$.DATAFRAME_DAPPLY();
   }

   public static int RDD() {
      return RRunnerModes$.MODULE$.RDD();
   }
}
