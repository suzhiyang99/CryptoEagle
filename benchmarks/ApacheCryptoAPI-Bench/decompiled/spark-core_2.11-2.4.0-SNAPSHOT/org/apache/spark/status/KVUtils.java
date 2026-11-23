package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.module.scala.DefaultScalaModule.;
import java.io.File;
import org.apache.spark.util.kvstore.KVStoreSerializer;
import org.apache.spark.util.kvstore.KVStoreView;
import org.apache.spark.util.kvstore.LevelDB;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001drAB\u0001\u0003\u0011\u0003!!\"A\u0004L-V#\u0018\u000e\\:\u000b\u0005\r!\u0011AB:uCR,8O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h!\tYA\"D\u0001\u0003\r\u0019i!\u0001#\u0001\u0005\u001d\t91JV+uS2\u001c8c\u0001\u0007\u0010+A\u0011\u0001cE\u0007\u0002#)\t!#A\u0003tG\u0006d\u0017-\u0003\u0002\u0015#\t1\u0011I\\=SK\u001a\u0004\"AF\r\u000e\u0003]Q!\u0001\u0007\u0003\u0002\u0011%tG/\u001a:oC2L!AG\f\u0003\u000f1{wmZ5oO\")A\u0004\u0004C\u0001=\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u000b\u000b\u0011\u0001C\u0002A\u0011\u0003\u0019-3\u0016J\u001c3fqB\u000b'/Y7+\u0005\tR\u0003CA\u0012)\u001b\u0005!#BA\u0013'\u0003\u001dYgo\u001d;pe\u0016T!a\n\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003S\u0011\u0012qa\u0013,J]\u0012,\u0007pK\u0001,!\ta\u0013'D\u0001.\u0015\tqs&\u0001\u0003nKR\f'B\u0001\u0019\u0012\u0003)\tgN\\8uCRLwN\\\u0005\u0003e5\u0012aaZ3ui\u0016\u0014h!\u0002\u001b\r\u0001\u0011)$AF&W'R|'/Z*dC2\f7+\u001a:jC2L'0\u001a:\u0014\u0005M2\u0004CA\u00128\u0013\tADEA\tL-N#xN]3TKJL\u0017\r\\5{KJDQ\u0001H\u001a\u0005\u0002i\"\u0012a\u000f\t\u0003yMj\u0011\u0001\u0004\u0005\u0006}1!\taP\u0001\u0005_B,g.\u0006\u0002A\u001fR\u0019\u0011\t\u00172\u0015\u0005\t+\u0005CA\u0012D\u0013\t!EEA\u0004MKZ,G\u000e\u0012\"\t\u000f\u0019k\u0014\u0011!a\u0002\u000f\u0006QQM^5eK:\u001cW\rJ\u0019\u0011\u0007![U*D\u0001J\u0015\tQ\u0015#A\u0004sK\u001adWm\u0019;\n\u00051K%\u0001C\"mCN\u001cH+Y4\u0011\u00059{E\u0002\u0001\u0003\u0006!v\u0012\r!\u0015\u0002\u0002\u001bF\u0011!+\u0016\t\u0003!MK!\u0001V\t\u0003\u000f9{G\u000f[5oOB\u0011\u0001CV\u0005\u0003/F\u00111!\u00118z\u0011\u0015IV\b1\u0001[\u0003\u0011\u0001\u0018\r\u001e5\u0011\u0005m\u0003W\"\u0001/\u000b\u0005us\u0016AA5p\u0015\u0005y\u0016\u0001\u00026bm\u0006L!!\u0019/\u0003\t\u0019KG.\u001a\u0005\u0006Gv\u0002\r!T\u0001\t[\u0016$\u0018\rZ1uC\")Q\r\u0004C\u0001M\u0006Ia/[3x)>\u001cV-]\u000b\u0003OZ$R\u0001[A\u0001\u0003\u0017!\"!\u001b=\u0011\u0007)\u0014XO\u0004\u0002la:\u0011An\\\u0007\u0002[*\u0011a.H\u0001\u0007yI|w\u000e\u001e \n\u0003II!!]\t\u0002\u000fA\f7m[1hK&\u00111\u000f\u001e\u0002\u0004'\u0016\f(BA9\u0012!\tqe\u000fB\u0003xI\n\u0007\u0011KA\u0001U\u0011\u0015IH\r1\u0001{\u0003\u00191\u0017\u000e\u001c;feB!\u0001c_;~\u0013\ta\u0018CA\u0005Gk:\u001cG/[8ocA\u0011\u0001C`\u0005\u0003\u007fF\u0011qAQ8pY\u0016\fg\u000eC\u0004\u0002\u0004\u0011\u0004\r!!\u0002\u0002\tYLWm\u001e\t\u0005G\u0005\u001dQ/C\u0002\u0002\n\u0011\u00121b\u0013,Ti>\u0014XMV5fo\"9\u0011Q\u00023A\u0002\u0005=\u0011aA7bqB\u0019\u0001#!\u0005\n\u0007\u0005M\u0011CA\u0002J]R4q!a\u0006\r\u0001\u0011\tIBA\rNKR\fG-\u0019;b\u001b&\u001cX.\u0019;dQ\u0016C8-\u001a9uS>t7\u0003BA\u000b\u00037\u00012A[A\u000f\u0013\r\ty\u0002\u001e\u0002\n\u000bb\u001cW\r\u001d;j_:Dq\u0001HA\u000b\t\u0003\t\u0019\u0003\u0006\u0002\u0002&A\u0019A(!\u0006"
)
public final class KVUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return KVUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return KVUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      KVUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return KVUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      KVUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      KVUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      KVUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      KVUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      KVUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      KVUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      KVUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      KVUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      KVUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      KVUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return KVUtils$.MODULE$.log();
   }

   public static String logName() {
      return KVUtils$.MODULE$.logName();
   }

   public static <T> Seq<T> viewToSeq(KVStoreView<T> var0, int var1, Function1<T, Object> var2) {
      return KVUtils$.MODULE$.<T>viewToSeq(var0, var1, var2);
   }

   public static <M> LevelDB open(File var0, M var1, ClassTag<M> var2) {
      return KVUtils$.MODULE$.open(var0, var1, var2);
   }

   public static class KVStoreScalaSerializer extends KVStoreSerializer {
      public KVStoreScalaSerializer() {
         this.mapper.registerModule(.MODULE$);
         this.mapper.setSerializationInclusion(Include.NON_NULL);
      }
   }

   public static class MetadataMismatchException extends Exception {
   }
}
