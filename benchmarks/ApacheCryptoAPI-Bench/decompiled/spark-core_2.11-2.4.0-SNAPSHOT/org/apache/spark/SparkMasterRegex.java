package org.apache.spark;

import scala.reflect.ScalaSignature;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001A:Q!\u0001\u0002\t\n%\t\u0001c\u00159be.l\u0015m\u001d;feJ+w-\u001a=\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u000b\u00175\t!AB\u0003\r\u0005!%QB\u0001\tTa\u0006\u00148.T1ti\u0016\u0014(+Z4fqN\u00111B\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000bUYA\u0011\u0001\f\u0002\rqJg.\u001b;?)\u0005I\u0001b\u0002\r\f\u0005\u0004%\t!G\u0001\u000e\u0019>\u001b\u0015\tT0O?J+u)\u0012-\u0016\u0003i\u0001\"a\u0007\u0011\u000e\u0003qQ!!\b\u0010\u0002\u00115\fGo\u00195j]\u001eT!a\b\t\u0002\tU$\u0018\u000e\\\u0005\u0003Cq\u0011QAU3hKbDaaI\u0006!\u0002\u0013Q\u0012A\u0004'P\u0007\u0006cuLT0S\u000b\u001e+\u0005\f\t\u0005\bK-\u0011\r\u0011\"\u0001\u001a\u0003YaujQ!M?:{f)Q%M+J+5k\u0018*F\u000f\u0016C\u0006BB\u0014\fA\u0003%!$A\fM\u001f\u000e\u000bEj\u0018(`\r\u0006KE*\u0016*F'~\u0013ViR#YA!9\u0011f\u0003b\u0001\n\u0003I\u0012a\u0005'P\u0007\u0006cul\u0011'V'R+%k\u0018*F\u000f\u0016C\u0006BB\u0016\fA\u0003%!$\u0001\u000bM\u001f\u000e\u000bEjX\"M+N#VIU0S\u000b\u001e+\u0005\f\t\u0005\b[-\u0011\r\u0011\"\u0001\u001a\u0003-\u0019\u0006+\u0011*L?J+u)\u0012-\t\r=Z\u0001\u0015!\u0003\u001b\u00031\u0019\u0006+\u0011*L?J+u)\u0012-!\u0001"
)
public final class SparkMasterRegex {
   public static Regex SPARK_REGEX() {
      return SparkMasterRegex$.MODULE$.SPARK_REGEX();
   }

   public static Regex LOCAL_CLUSTER_REGEX() {
      return SparkMasterRegex$.MODULE$.LOCAL_CLUSTER_REGEX();
   }

   public static Regex LOCAL_N_FAILURES_REGEX() {
      return SparkMasterRegex$.MODULE$.LOCAL_N_FAILURES_REGEX();
   }

   public static Regex LOCAL_N_REGEX() {
      return SparkMasterRegex$.MODULE$.LOCAL_N_REGEX();
   }
}
