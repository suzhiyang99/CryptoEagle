package org.apache.spark.metrics.source;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.annotation.Experimental;
import scala.reflect.ScalaSignature;

@Experimental
@ScalaSignature(
   bytes = "\u0006\u0001Q;Q!\u0001\u0002\t\u00025\tabQ8eK\u001e,g.T3ue&\u001c7O\u0003\u0002\u0004\t\u000511o\\;sG\u0016T!!\u0002\u0004\u0002\u000f5,GO]5dg*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0011\u00059yQ\"\u0001\u0002\u0007\u000bA\u0011\u0001\u0012A\t\u0003\u001d\r{G-Z4f]6+GO]5dgN\u0019qB\u0005\r\u0011\u0005M1R\"\u0001\u000b\u000b\u0003U\tQa]2bY\u0006L!a\u0006\u000b\u0003\r\u0005s\u0017PU3g!\tq\u0011$\u0003\u0002\u001b\u0005\t11k\\;sG\u0016DQ\u0001H\b\u0005\u0002u\ta\u0001P5oSRtD#A\u0007\t\u000f}y!\u0019!C!A\u0005Q1o\\;sG\u0016t\u0015-\\3\u0016\u0003\u0005\u0002\"AI\u0013\u000f\u0005M\u0019\u0013B\u0001\u0013\u0015\u0003\u0019\u0001&/\u001a3fM&\u0011ae\n\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0011\"\u0002BB\u0015\u0010A\u0003%\u0011%A\u0006t_V\u00148-\u001a(b[\u0016\u0004\u0003bB\u0016\u0010\u0005\u0004%\t\u0005L\u0001\u000f[\u0016$(/[2SK\u001eL7\u000f\u001e:z+\u0005i\u0003C\u0001\u00185\u001b\u0005y#BA\u00031\u0015\t\t$'\u0001\u0005d_\u0012\f\u0007.\u00197f\u0015\u0005\u0019\u0014aA2p[&\u0011Qg\f\u0002\u000f\u001b\u0016$(/[2SK\u001eL7\u000f\u001e:z\u0011\u00199t\u0002)A\u0005[\u0005yQ.\u001a;sS\u000e\u0014VmZ5tiJL\b\u0005C\u0004:\u001f\t\u0007I\u0011\u0001\u001e\u0002/5+EKU%D?N{UKU\"F?\u000e{E)R0T\u0013j+U#A\u001e\u0011\u00059b\u0014BA\u001f0\u0005%A\u0015n\u001d;pOJ\fW\u000e\u0003\u0004@\u001f\u0001\u0006IaO\u0001\u0019\u001b\u0016#&+S\"`'>+&kQ#`\u0007>#UiX*J5\u0016\u0003\u0003bB!\u0010\u0005\u0004%\tAO\u0001\u0018\u001b\u0016#&+S\"`\u0007>k\u0005+\u0013'B)&{ej\u0018+J\u001b\u0016CaaQ\b!\u0002\u0013Y\u0014\u0001G'F)JK5iX\"P\u001bBKE*\u0011+J\u001f:{F+S'FA!9Qi\u0004b\u0001\n\u0003Q\u0014\u0001J'F)JK5iX$F\u001d\u0016\u0013\u0016\tV#E?\u000ec\u0015iU*`\u0005f#ViQ(E\u000b~\u001b\u0016JW#\t\r\u001d{\u0001\u0015!\u0003<\u0003\u0015jU\t\u0016*J\u0007~;UIT#S\u0003R+EiX\"M\u0003N\u001bvLQ-U\u000b\u000e{E)R0T\u0013j+\u0005\u0005C\u0004J\u001f\t\u0007I\u0011\u0001\u001e\u0002K5+EKU%D?\u001e+e*\u0012*B)\u0016#u,T#U\u0011>#uLQ-U\u000b\u000e{E)R0T\u0013j+\u0005BB&\u0010A\u0003%1(\u0001\u0014N\u000bR\u0013\u0016jQ0H\u000b:+%+\u0011+F\t~kU\t\u0016%P\t~\u0013\u0015\fV#D\u001f\u0012+ulU%[\u000b\u0002B#aD'\u0011\u00059\u000bV\"A(\u000b\u0005A3\u0011AC1o]>$\u0018\r^5p]&\u0011!k\u0014\u0002\r\u000bb\u0004XM]5nK:$\u0018\r\u001c\u0015\u0003\u00015\u0003"
)
public final class CodegenMetrics {
   public static Histogram METRIC_GENERATED_METHOD_BYTECODE_SIZE() {
      return CodegenMetrics$.MODULE$.METRIC_GENERATED_METHOD_BYTECODE_SIZE();
   }

   public static Histogram METRIC_GENERATED_CLASS_BYTECODE_SIZE() {
      return CodegenMetrics$.MODULE$.METRIC_GENERATED_CLASS_BYTECODE_SIZE();
   }

   public static Histogram METRIC_COMPILATION_TIME() {
      return CodegenMetrics$.MODULE$.METRIC_COMPILATION_TIME();
   }

   public static Histogram METRIC_SOURCE_CODE_SIZE() {
      return CodegenMetrics$.MODULE$.METRIC_SOURCE_CODE_SIZE();
   }

   public static MetricRegistry metricRegistry() {
      return CodegenMetrics$.MODULE$.metricRegistry();
   }

   public static String sourceName() {
      return CodegenMetrics$.MODULE$.sourceName();
   }
}
