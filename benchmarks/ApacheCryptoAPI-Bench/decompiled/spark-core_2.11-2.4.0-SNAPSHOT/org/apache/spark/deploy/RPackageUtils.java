package org.apache.spark.deploy;

import java.io.PrintStream;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055uAB\u0001\u0003\u0011\u0003\u0011!\"A\u0007S!\u0006\u001c7.Y4f+RLGn\u001d\u0006\u0003\u0007\u0011\ta\u0001Z3qY>L(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0011\u0005-aQ\"\u0001\u0002\u0007\r5\u0011\u0001\u0012\u0001\u0002\u000f\u00055\u0011\u0006+Y2lC\u001e,W\u000b^5mgN\u0019AbD\u000b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\t1\u0012$D\u0001\u0018\u0015\tAB!\u0001\u0005j]R,'O\\1m\u0013\tQrCA\u0004M_\u001e<\u0017N\\4\t\u000bqaA\u0011\u0001\u0010\u0002\rqJg.\u001b;?\u0007\u0001!\u0012A\u0003\u0005\bA1\u0011\r\u0011\"\u0004\"\u0003-A\u0017m\u001d*QC\u000e\\\u0017mZ3\u0016\u0003\tz\u0011aI\u0011\u0002I\u0005\t2\u000b]1sW6B\u0015m\u001d*QC\u000e\\\u0017mZ3\t\r\u0019b\u0001\u0015!\u0004#\u00031A\u0017m\u001d*QC\u000e\\\u0017mZ3!\u0011\u001dACB1A\u0005\u000e%\naBY1tK&s7\u000f^1mY\u000ekG-F\u0001+!\rYc\u0006M\u0007\u0002Y)\u0011Q&E\u0001\u000bG>dG.Z2uS>t\u0017BA\u0018-\u0005\r\u0019V-\u001d\t\u0003cYj\u0011A\r\u0006\u0003gQ\nA\u0001\\1oO*\tQ'\u0001\u0003kCZ\f\u0017BA\u001c3\u0005\u0019\u0019FO]5oO\"1\u0011\b\u0004Q\u0001\u000e)\nqBY1tK&s7\u000f^1mY\u000ekG\r\t\u0005\bw1\u0011\r\u0011\"\u0004=\u0003-\u0011&*\u0019:F]R\u0014\u0018.Z:\u0016\u0003uz\u0011AP\u0011\u0002\u007f\u0005)!k\f9lO\"1\u0011\t\u0004Q\u0001\u000eu\nAB\u0015&be\u0016sGO]5fg\u0002B\u0001b\u0011\u0007C\u0002\u0013\u0015!\u0001R\u0001\b%*\u000b'\u000fR8d+\u0005\u0001\u0004B\u0002$\rA\u00035\u0001'\u0001\u0005S\u0015\u0006\u0014Hi\\2!\u0011\u0015AE\u0002\"\u0003J\u0003\u0015\u0001(/\u001b8u)\u0015QU*V/h!\t\u00012*\u0003\u0002M#\t!QK\\5u\u0011\u0015qu\t1\u0001P\u0003\ri7o\u001a\t\u0003!Ns!\u0001E)\n\u0005I\u000b\u0012A\u0002)sK\u0012,g-\u0003\u00028)*\u0011!+\u0005\u0005\u0006-\u001e\u0003\raV\u0001\faJLg\u000e^*ue\u0016\fW\u000e\u0005\u0002Y76\t\u0011L\u0003\u0002[i\u0005\u0011\u0011n\\\u0005\u00039f\u00131\u0002\u0015:j]R\u001cFO]3b[\"9al\u0012I\u0001\u0002\u0004y\u0016!\u00027fm\u0016d\u0007C\u00011f\u001b\u0005\t'B\u00012d\u0003\u001dawnZ4j]\u001eT!\u0001\u001a\u001b\u0002\tU$\u0018\u000e\\\u0005\u0003M\u0006\u0014Q\u0001T3wK2Dq\u0001[$\u0011\u0002\u0003\u0007\u0011.A\u0001f!\tQ'O\u0004\u0002la:\u0011An\\\u0007\u0002[*\u0011a.H\u0001\u0007yI|w\u000e\u001e \n\u0003II!!]\t\u0002\u000fA\f7m[1hK&\u00111\u000f\u001e\u0002\n)\"\u0014xn^1cY\u0016T!!]\t\t\rYdA\u0011\u0001\u0002x\u0003E\u0019\u0007.Z2l\u001b\u0006t\u0017NZ3ti\u001a{'O\u0015\u000b\u0003qn\u0004\"\u0001E=\n\u0005i\f\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006yV\u0004\r!`\u0001\u0004U\u0006\u0014\bc\u0001@\u0002\u00025\tqP\u0003\u0002}G&\u0019\u00111A@\u0003\u000f)\u000b'OR5mK\"9\u0011q\u0001\u0007\u0005\n\u0005%\u0011a\u0004:QC\u000e\\\u0017mZ3Ck&dG-\u001a:\u0015\u0013a\fY!!\u0006\u0002\u0018\u0005m\u0001\u0002CA\u0007\u0003\u000b\u0001\r!a\u0004\u0002\u0007\u0011L'\u000fE\u0002Y\u0003#I1!a\u0005Z\u0005\u00111\u0015\u000e\\3\t\rY\u000b)\u00011\u0001X\u0011\u001d\tI\"!\u0002A\u0002a\fqA^3sE>\u001cX\rC\u0004\u0002\u001e\u0005\u0015\u0001\u0019A(\u0002\r1L'\rR5s\u0011\u001d\t\t\u0003\u0004C\u0005\u0003G\ta\"\u001a=ue\u0006\u001cGO\u0015$pY\u0012,'\u000f\u0006\u0005\u0002\u0010\u0005\u0015\u0012qEA\u0015\u0011\u0019a\u0018q\u0004a\u0001{\"1a+a\bA\u0002]Cq!!\u0007\u0002 \u0001\u0007\u0001\u0010\u0003\u0005\u0002.1!\tAAA\u0018\u0003U\u0019\u0007.Z2l\u0003:$')^5mIJ\u0003\u0016mY6bO\u0016$rASA\u0019\u0003k\t9\u0004C\u0004\u00024\u0005-\u0002\u0019A(\u0002\t)\f'o\u001d\u0005\t-\u0006-\u0002\u0013!a\u0001/\"I\u0011\u0011DA\u0016!\u0003\u0005\r\u0001\u001f\u0005\b\u0003waA\u0011BA\u001f\u0003Qa\u0017n\u001d;GS2,7OU3dkJ\u001c\u0018N^3msR1\u0011qHA#\u0003\u000f\u0002R\u0001UA!\u0003\u001fI1!a\u0011U\u0005\r\u0019V\r\u001e\u0005\t\u0003\u001b\tI\u00041\u0001\u0002\u0010!A\u0011\u0011JA\u001d\u0001\u0004\tY%A\bfq\u000edW\u000fZ3QCR$XM\u001d8t!\u0011Q\u0017QJ(\n\u0005=\"\b\u0002CA)\u0019\u0011\u0005!!a\u0015\u0002\u001biL\u0007O\u0015'jEJ\f'/[3t)\u0019\ty!!\u0016\u0002X!A\u0011QBA(\u0001\u0004\ty\u0001C\u0004\u0002Z\u0005=\u0003\u0019A(\u0002\t9\fW.\u001a\u0005\n\u0003;b\u0011\u0013!C\u0005\u0003?\nq\u0002\u001d:j]R$C-\u001a4bk2$HeM\u000b\u0003\u0003CR3aXA2W\t\t)\u0007\u0005\u0003\u0002h\u0005ETBAA5\u0015\u0011\tY'!\u001c\u0002\u0013Ut7\r[3dW\u0016$'bAA8#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005M\u0014\u0011\u000e\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007\"CA<\u0019E\u0005I\u0011BA=\u0003=\u0001(/\u001b8uI\u0011,g-Y;mi\u0012\"TCAA>U\rI\u00171\r\u0005\n\u0003\u007fb\u0011\u0013!C\u0001\u0003\u0003\u000bqd\u00195fG.\fe\u000e\u001a\"vS2$'\u000bU1dW\u0006<W\r\n3fM\u0006,H\u000e\u001e\u00133+\t\t\u0019IK\u0002X\u0003GB\u0011\"a\"\r#\u0003%\t!!#\u0002?\rDWmY6B]\u0012\u0014U/\u001b7e%B\u000b7m[1hK\u0012\"WMZ1vYR$3'\u0006\u0002\u0002\f*\u001a\u00010a\u0019"
)
public final class RPackageUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return RPackageUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return RPackageUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      RPackageUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return RPackageUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      RPackageUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      RPackageUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      RPackageUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      RPackageUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      RPackageUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      RPackageUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      RPackageUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      RPackageUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      RPackageUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      RPackageUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return RPackageUtils$.MODULE$.log();
   }

   public static String logName() {
      return RPackageUtils$.MODULE$.logName();
   }

   public static boolean checkAndBuildRPackage$default$3() {
      return RPackageUtils$.MODULE$.checkAndBuildRPackage$default$3();
   }

   public static PrintStream checkAndBuildRPackage$default$2() {
      return RPackageUtils$.MODULE$.checkAndBuildRPackage$default$2();
   }
}
