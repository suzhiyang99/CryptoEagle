package org.apache.spark.deploy;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\tQ\u0011!E*qCJ\\7+\u001e2nSR\f5\r^5p]*\u00111\u0001B\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\tq!!E*qCJ\\7+\u001e2nSR\f5\r^5p]N\u0011Ab\u0004\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0017\u0019\u0011\u0005\u0001$\u0001\u0004=S:LGOP\u0002\u0001)\u0005QQ\u0001B\u0007\r\u0001i\u0001\"a\u0007\u000f\u000e\u00031I!!H\n\u0003\u000bY\u000bG.^3\t\u000f}a!\u0019!C\u0001A\u000511+\u0016\"N\u0013R+\u0012A\u0007\u0005\u0007E1\u0001\u000b\u0011\u0002\u000e\u0002\u000fM+&)T%UA!9A\u0005\u0004b\u0001\n\u0003\u0001\u0013\u0001B&J\u00192CaA\n\u0007!\u0002\u0013Q\u0012!B&J\u00192\u0003\u0003b\u0002\u0015\r\u0005\u0004%\t\u0001I\u0001\u000f%\u0016\u000bV+R*U?N#\u0016\tV+T\u0011\u0019QC\u0002)A\u00055\u0005y!+R)V\u000bN#vl\u0015+B)V\u001b\u0006\u0005"
)
public final class SparkSubmitAction {
   public static Enumeration.Value REQUEST_STATUS() {
      return SparkSubmitAction$.MODULE$.REQUEST_STATUS();
   }

   public static Enumeration.Value KILL() {
      return SparkSubmitAction$.MODULE$.KILL();
   }

   public static Enumeration.Value SUBMIT() {
      return SparkSubmitAction$.MODULE$.SUBMIT();
   }

   public static Enumeration.Value withName(String var0) {
      return SparkSubmitAction$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return SparkSubmitAction$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return SparkSubmitAction$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return SparkSubmitAction$.MODULE$.values();
   }

   public static String toString() {
      return SparkSubmitAction$.MODULE$.toString();
   }
}
