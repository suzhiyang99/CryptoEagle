package org.apache.spark.internal.io;

import java.util.Date;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.OutputMetrics;
import scala.Function0;
import scala.Tuple2;
import scala.reflect.ScalaSignature;
import scala.util.DynamicVariable;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mqAB\u0001\u0003\u0011\u00031A\"\u0001\fTa\u0006\u00148\u000eS1e_>\u0004xK]5uKJ,F/\u001b7t\u0015\t\u0019A!\u0001\u0002j_*\u0011QAB\u0001\tS:$XM\u001d8bY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x\r\u0005\u0002\u000e\u001d5\t!A\u0002\u0004\u0010\u0005!\u0005a\u0001\u0005\u0002\u0017'B\f'o\u001b%bI>|\u0007o\u0016:ji\u0016\u0014X\u000b^5mgN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000baqA\u0011\u0001\u000e\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\u0004\u0005\b99\u0011\r\u0011\"\u0003\u001e\u00031\u0012ViQ(S\tN{&)\u0012+X\u000b\u0016suLQ-U\u000bN{vKU%U)\u0016su,T#U%&\u001bu,\u0016)E\u0003R+5+F\u0001\u001f!\t\u0011r$\u0003\u0002!'\t\u0019\u0011J\u001c;\t\r\tr\u0001\u0015!\u0003\u001f\u00035\u0012ViQ(S\tN{&)\u0012+X\u000b\u0016suLQ-U\u000bN{vKU%U)\u0016su,T#U%&\u001bu,\u0016)E\u0003R+5\u000b\t\u0005\u0006I9!\t!J\u0001\fGJ,\u0017\r^3K_\nLE\tF\u0002']a\u0002\"a\n\u0017\u000e\u0003!R!!\u000b\u0016\u0002\r5\f\u0007O]3e\u0015\tY\u0003\"\u0001\u0004iC\u0012|w\u000e]\u0005\u0003[!\u0012QAS8c\u0013\u0012CQaL\u0012A\u0002A\nA\u0001^5nKB\u0011\u0011GN\u0007\u0002e)\u00111\u0007N\u0001\u0005kRLGNC\u00016\u0003\u0011Q\u0017M^1\n\u0005]\u0012$\u0001\u0002#bi\u0016DQ!O\u0012A\u0002y\t!!\u001b3\t\u000bmrA\u0011\u0001\u001f\u0002%\r\u0014X-\u0019;f\u0015>\u0014GK]1dW\u0016\u0014\u0018\n\u0012\u000b\u0003{\u0011\u0003\"AP!\u000f\u0005Iy\u0014B\u0001!\u0014\u0003\u0019\u0001&/\u001a3fM&\u0011!i\u0011\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0001\u001b\u0002\"B\u0018;\u0001\u0004\u0001\u0004\"\u0002$\u000f\t\u00039\u0015\u0001F2sK\u0006$X\rU1uQ\u001a\u0013x.\\*ue&tw\rF\u0002I\u001dB\u0003\"!\u0013'\u000e\u0003)S!a\u0013\u0016\u0002\u0005\u0019\u001c\u0018BA'K\u0005\u0011\u0001\u0016\r\u001e5\t\u000b=+\u0005\u0019A\u001f\u0002\tA\fG\u000f\u001b\u0005\u0006#\u0016\u0003\rAU\u0001\u0005G>tg\r\u0005\u0002('&\u0011A\u000b\u000b\u0002\b\u0015>\u00147i\u001c8g\u0011\u00151f\u0002\"\u0001X\u0003uI7oT;uaV$8\u000b]3d-\u0006d\u0017\u000eZ1uS>tWI\\1cY\u0016$GC\u0001-\\!\t\u0011\u0012,\u0003\u0002['\t9!i\\8mK\u0006t\u0007\"B)V\u0001\u0004a\u0006CA/_\u001b\u00051\u0011BA0\u0007\u0005%\u0019\u0006/\u0019:l\u0007>tg\rC\u0003b\u001d\u0011\u0005!-A\fj]&$\b*\u00193p_B|U\u000f\u001e9vi6+GO]5dgR\u00111M\u001d\t\u0005%\u00114G.\u0003\u0002f'\t1A+\u001e9mKJ\u0002\"a\u001a6\u000e\u0003!T!!\u001b\u0004\u0002\u0011\u0015DXmY;u_JL!a\u001b5\u0003\u001b=+H\u000f];u\u001b\u0016$(/[2t!\r\u0011Rn\\\u0005\u0003]N\u0011\u0011BR;oGRLwN\u001c\u0019\u0011\u0005I\u0001\u0018BA9\u0014\u0005\u0011auN\\4\t\u000bM\u0004\u0007\u0019\u0001;\u0002\u000f\r|g\u000e^3yiB\u0011Q,^\u0005\u0003m\u001a\u00111\u0002V1tW\u000e{g\u000e^3yi\")\u0001P\u0004C\u0001s\u0006AR.Y=cKV\u0003H-\u0019;f\u001fV$\b/\u001e;NKR\u0014\u0018nY:\u0015\u000bilx0a\u0001\u0011\u0005IY\u0018B\u0001?\u0014\u0005\u0011)f.\u001b;\t\u000by<\b\u0019\u00014\u0002\u001b=,H\u000f];u\u001b\u0016$(/[2t\u0011\u0019\t\ta\u001ea\u0001Y\u0006A1-\u00197mE\u0006\u001c7\u000e\u0003\u0004\u0002\u0006]\u0004\ra\\\u0001\u000fe\u0016\u001cwN\u001d3t/JLG\u000f^3o\u0011%\tIA\u0004b\u0001\n\u0003\tY!A\u000eeSN\f'\r\\3PkR\u0004X\u000f^*qK\u000e4\u0016\r\\5eCRLwN\\\u000b\u0003\u0003\u001b\u0001R!a\u0004\u0002\u0014ak!!!\u0005\u000b\u0005M\u001a\u0012\u0002BA\u000b\u0003#\u0011q\u0002R=oC6L7MV1sS\u0006\u0014G.\u001a\u0005\t\u00033q\u0001\u0015!\u0003\u0002\u000e\u0005aB-[:bE2,w*\u001e;qkR\u001c\u0006/Z2WC2LG-\u0019;j_:\u0004\u0003"
)
public final class SparkHadoopWriterUtils {
   public static DynamicVariable<Object> disableOutputSpecValidation() {
      return SparkHadoopWriterUtils$.MODULE$.disableOutputSpecValidation();
   }

   public static void maybeUpdateOutputMetrics(OutputMetrics var0, Function0<Object> var1, long var2) {
      SparkHadoopWriterUtils$.MODULE$.maybeUpdateOutputMetrics(var0, var1, var2);
   }

   public static Tuple2<OutputMetrics, Function0<Object>> initHadoopOutputMetrics(TaskContext var0) {
      return SparkHadoopWriterUtils$.MODULE$.initHadoopOutputMetrics(var0);
   }

   public static boolean isOutputSpecValidationEnabled(SparkConf var0) {
      return SparkHadoopWriterUtils$.MODULE$.isOutputSpecValidationEnabled(var0);
   }

   public static Path createPathFromString(String var0, JobConf var1) {
      return SparkHadoopWriterUtils$.MODULE$.createPathFromString(var0, var1);
   }

   public static String createJobTrackerID(Date var0) {
      return SparkHadoopWriterUtils$.MODULE$.createJobTrackerID(var0);
   }

   public static JobID createJobID(Date var0, int var1) {
      return SparkHadoopWriterUtils$.MODULE$.createJobID(var0, var1);
   }
}
