package org.apache.spark.network.netty;

import org.apache.spark.SparkConf;
import org.apache.spark.network.util.TransportConf;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u00019;Q!\u0001\u0002\t\u00025\t!c\u00159be.$&/\u00198ta>\u0014HoQ8oM*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\tqA\\3uo>\u00148N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u0001\"AD\b\u000e\u0003\t1Q\u0001\u0005\u0002\t\u0002E\u0011!c\u00159be.$&/\u00198ta>\u0014HoQ8oMN\u0011qB\u0005\t\u0003'Yi\u0011\u0001\u0006\u0006\u0002+\u0005)1oY1mC&\u0011q\u0003\u0006\u0002\u0007\u0003:L(+\u001a4\t\u000beyA\u0011\u0001\u000e\u0002\rqJg.\u001b;?)\u0005i\u0001b\u0002\u000f\u0010\u0005\u0004%I!H\u0001\u001a\u001b\u0006Cv\fR#G\u0003VcEk\u0018(F)RKv\f\u0016%S\u000b\u0006#5+F\u0001\u001f!\t\u0019r$\u0003\u0002!)\t\u0019\u0011J\u001c;\t\r\tz\u0001\u0015!\u0003\u001f\u0003ii\u0015\tW0E\u000b\u001a\u000bU\u000b\u0014+`\u001d\u0016#F+W0U\u0011J+\u0015\tR*!\u0011\u0015!s\u0002\"\u0001&\u000351'o\\7Ta\u0006\u00148nQ8oMR!a\u0005\f\u001a<!\t9#&D\u0001)\u0015\tIC!\u0001\u0003vi&d\u0017BA\u0016)\u00055!&/\u00198ta>\u0014HoQ8oM\")Qf\ta\u0001]\u0005)qlY8oMB\u0011q\u0006M\u0007\u0002\r%\u0011\u0011G\u0002\u0002\n'B\f'o[\"p]\u001aDQaM\u0012A\u0002Q\na!\\8ek2,\u0007CA\u001b9\u001d\t\u0019b'\u0003\u00028)\u00051\u0001K]3eK\u001aL!!\u000f\u001e\u0003\rM#(/\u001b8h\u0015\t9D\u0003C\u0004=GA\u0005\t\u0019\u0001\u0010\u0002\u001d9,X.V:bE2,7i\u001c:fg\")ah\u0004C\u0005\u007f\u0005\tB-\u001a4bk2$h*^7UQJ,\u0017\rZ:\u0015\u0005y\u0001\u0005\"\u0002\u001f>\u0001\u0004q\u0002b\u0002\"\u0010#\u0003%\taQ\u0001\u0018MJ|Wn\u00159be.\u001cuN\u001c4%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u0012\u0016\u0003=\u0015[\u0013A\u0012\t\u0003\u000f2k\u0011\u0001\u0013\u0006\u0003\u0013*\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005-#\u0012AC1o]>$\u0018\r^5p]&\u0011Q\n\u0013\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007"
)
public final class SparkTransportConf {
   public static int fromSparkConf$default$3() {
      return SparkTransportConf$.MODULE$.fromSparkConf$default$3();
   }

   public static TransportConf fromSparkConf(SparkConf var0, String var1, int var2) {
      return SparkTransportConf$.MODULE$.fromSparkConf(var0, var1, var2);
   }
}
