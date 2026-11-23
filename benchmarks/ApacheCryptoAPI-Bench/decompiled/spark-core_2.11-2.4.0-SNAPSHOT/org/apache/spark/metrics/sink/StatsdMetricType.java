package org.apache.spark.metrics.sink;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Q:a!\u0001\u0002\t\u0002\u0019a\u0011\u0001E*uCR\u001cH-T3ue&\u001cG+\u001f9f\u0015\t\u0019A!\u0001\u0003tS:\\'BA\u0003\u0007\u0003\u001diW\r\u001e:jGNT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'o\u001a\t\u0003\u001b9i\u0011A\u0001\u0004\u0007\u001f\tA\tA\u0002\t\u0003!M#\u0018\r^:e\u001b\u0016$(/[2UsB,7C\u0001\b\u0012!\t\u0011R#D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019\te.\u001f*fM\")\u0001D\u0004C\u00015\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u0011\u001dabB1A\u0005\u0002u\tqaQ(V\u001dR+%+F\u0001\u001f!\tyB%D\u0001!\u0015\t\t#%\u0001\u0003mC:<'\"A\u0012\u0002\t)\fg/Y\u0005\u0003K\u0001\u0012aa\u0015;sS:<\u0007BB\u0014\u000fA\u0003%a$\u0001\u0005D\u001fVsE+\u0012*!\u0011\u001dIcB1A\u0005\u0002u\tQaR!V\u000f\u0016Caa\u000b\b!\u0002\u0013q\u0012AB$B+\u001e+\u0005\u0005C\u0004.\u001d\t\u0007I\u0011A\u000f\u0002\u000bQKU*\u0012*\t\r=r\u0001\u0015!\u0003\u001f\u0003\u0019!\u0016*T#SA!9\u0011G\u0004b\u0001\n\u0003i\u0012aA*fi\"11G\u0004Q\u0001\ny\tAaU3uA\u0001"
)
public final class StatsdMetricType {
   public static String Set() {
      return StatsdMetricType$.MODULE$.Set();
   }

   public static String TIMER() {
      return StatsdMetricType$.MODULE$.TIMER();
   }

   public static String GAUGE() {
      return StatsdMetricType$.MODULE$.GAUGE();
   }

   public static String COUNTER() {
      return StatsdMetricType$.MODULE$.COUNTER();
   }
}
