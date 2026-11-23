package org.apache.spark.api.python;

import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r<a!\u0001\u0002\t\u0002\ta\u0011\u0001\u0005)zi\"|g\u000eS1e_>\u0004X\u000b^5m\u0015\t\u0019A!\u0001\u0004qsRDwN\u001c\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<\u0007CA\u0007\u000f\u001b\u0005\u0011aAB\b\u0003\u0011\u0003\u0011\u0001C\u0001\tQsRDwN\u001c%bI>|\u0007/\u0016;jYN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000baqA\u0011\u0001\u000e\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\u0004\u0005\u000699!\t!H\u0001\n[\u0006\u0004Hk\\\"p]\u001a$\"A\b\u0014\u0011\u0005}!S\"\u0001\u0011\u000b\u0005\u0005\u0012\u0013\u0001B2p]\u001aT!a\t\u0005\u0002\r!\fGm\\8q\u0013\t)\u0003EA\u0007D_:4\u0017nZ;sCRLwN\u001c\u0005\u0006Om\u0001\r\u0001K\u0001\u0004[\u0006\u0004\b\u0003B\u0015/aAj\u0011A\u000b\u0006\u0003W1\nA!\u001e;jY*\tQ&\u0001\u0003kCZ\f\u0017BA\u0018+\u0005\ri\u0015\r\u001d\t\u0003cQr!A\u0005\u001a\n\u0005M\u001a\u0012A\u0002)sK\u0012,g-\u0003\u00026m\t11\u000b\u001e:j]\u001eT!aM\n\t\u000barA\u0011A\u001d\u0002\u00155,'oZ3D_:47\u000fF\u0002\u001fuqBQaO\u001cA\u0002y\tA\u0001\\3gi\")Qh\u000ea\u0001=\u0005)!/[4ii\")qH\u0004C\u0001\u0001\u0006Q1m\u001c8wKJ$(\u000b\u0012#\u0016\u0007\u0005\u001b&\f\u0006\u0003C\u001dr\u000b\u0007cA\"G\u00116\tAI\u0003\u0002F\r\u0005\u0019!\u000f\u001a3\n\u0005\u001d#%a\u0001*E\tB!!#S&L\u0013\tQ5C\u0001\u0004UkBdWM\r\t\u0003%1K!!T\n\u0003\u0007\u0005s\u0017\u0010C\u0003F}\u0001\u0007q\nE\u0002D\rB\u0003BAE%R3B\u0011!k\u0015\u0007\u0001\t\u0015!fH1\u0001V\u0005\u0005Y\u0015C\u0001,L!\t\u0011r+\u0003\u0002Y'\t9aj\u001c;iS:<\u0007C\u0001*[\t\u0015YfH1\u0001V\u0005\u00051\u0006\"B/?\u0001\u0004q\u0016\u0001D6fs\u000e{gN^3si\u0016\u0014\b\u0003B\u0007`\u0017.K!\u0001\u0019\u0002\u0003\u0013\r{gN^3si\u0016\u0014\b\"\u00022?\u0001\u0004q\u0016A\u0004<bYV,7i\u001c8wKJ$XM\u001d"
)
public final class PythonHadoopUtil {
   public static <K, V> RDD<Tuple2<Object, Object>> convertRDD(RDD<Tuple2<K, V>> var0, Converter<Object, Object> var1, Converter<Object, Object> var2) {
      return PythonHadoopUtil$.MODULE$.convertRDD(var0, var1, var2);
   }

   public static Configuration mergeConfs(Configuration var0, Configuration var1) {
      return PythonHadoopUtil$.MODULE$.mergeConfs(var0, var1);
   }

   public static Configuration mapToConf(Map<String, String> var0) {
      return PythonHadoopUtil$.MODULE$.mapToConf(var0);
   }
}
