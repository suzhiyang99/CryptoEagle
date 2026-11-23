package org.apache.spark.scheduler.cluster;

import org.apache.spark.SparkConf;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m:a!\u0001\u0002\t\u0002\u0019a\u0011!F*dQ\u0016$W\u000f\\3s\u0005\u0006\u001c7.\u001a8e+RLGn\u001d\u0006\u0003\u0007\u0011\tqa\u00197vgR,'O\u0003\u0002\u0006\r\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011QcU2iK\u0012,H.\u001a:CC\u000e\\WM\u001c3Vi&d7o\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001aDQ\u0001\u0007\b\u0005\u0002i\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!9AD\u0004b\u0001\n\u0003i\u0012\u0001\u0007#F\r\u0006+F\nV0O+6\u0013UIU0F1\u0016\u001bU\u000bV(S'V\ta\u0004\u0005\u0002\u0013?%\u0011\u0001e\u0005\u0002\u0004\u0013:$\bB\u0002\u0012\u000fA\u0003%a$A\rE\u000b\u001a\u000bU\u000b\u0014+`\u001dVk%)\u0012*`\u000bb+5)\u0016+P%N\u0003\u0003\"\u0002\u0013\u000f\t\u0003)\u0013AH4fi&s\u0017\u000e^5bYR\u000b'oZ3u\u000bb,7-\u001e;pe:+XNY3s)\rqb\u0005\f\u0005\u0006O\r\u0002\r\u0001K\u0001\u0005G>tg\r\u0005\u0002*U5\ta!\u0003\u0002,\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\b[\r\u0002\n\u00111\u0001\u001f\u00031qW/\\#yK\u000e,Ho\u001c:t\u0011\u001dyc\"%A\u0005\u0002A\n\u0001fZ3u\u0013:LG/[1m)\u0006\u0014x-\u001a;Fq\u0016\u001cW\u000f^8s\u001dVl'-\u001a:%I\u00164\u0017-\u001e7uII*\u0012!\r\u0016\u0003=IZ\u0013a\r\t\u0003iej\u0011!\u000e\u0006\u0003m]\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005a\u001a\u0012AC1o]>$\u0018\r^5p]&\u0011!(\u000e\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007"
)
public final class SchedulerBackendUtils {
   public static int getInitialTargetExecutorNumber$default$2() {
      return SchedulerBackendUtils$.MODULE$.getInitialTargetExecutorNumber$default$2();
   }

   public static int getInitialTargetExecutorNumber(SparkConf var0, int var1) {
      return SchedulerBackendUtils$.MODULE$.getInitialTargetExecutorNumber(var0, var1);
   }

   public static int DEFAULT_NUMBER_EXECUTORS() {
      return SchedulerBackendUtils$.MODULE$.DEFAULT_NUMBER_EXECUTORS();
   }
}
