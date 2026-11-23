package org.apache.spark.mapred;

import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m:Q!\u0001\u0002\t\u0002-\tQc\u00159be.D\u0015\rZ8pa6\u000b\u0007OU3e+RLGN\u0003\u0002\u0004\t\u00051Q.\u00199sK\u0012T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001!\taQ\"D\u0001\u0003\r\u0015q!\u0001#\u0001\u0010\u0005U\u0019\u0006/\u0019:l\u0011\u0006$wn\u001c9NCB\u0014V\rZ+uS2\u001c2!\u0004\t\u0017!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fMB\u0011qCG\u0007\u00021)\u0011\u0011\u0004B\u0001\tS:$XM\u001d8bY&\u00111\u0004\u0007\u0002\b\u0019><w-\u001b8h\u0011\u0015iR\u0002\"\u0001\u001f\u0003\u0019a\u0014N\\5u}Q\t1\u0002C\u0003!\u001b\u0011\u0005\u0011%\u0001\u0006d_6l\u0017\u000e\u001e+bg.$RAI\u00130ie\u0002\"!E\u0012\n\u0005\u0011\u0012\"\u0001B+oSRDQAJ\u0010A\u0002\u001d\n\u0011bY8n[&$H/\u001a:\u0011\u0005!jS\"A\u0015\u000b\u0005)Z\u0013!C7baJ,G-^2f\u0015\tac!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003]%\u0012qbT;uaV$8i\\7nSR$XM\u001d\u0005\u0006a}\u0001\r!M\u0001\u000e[J$\u0016m]6D_:$X\r\u001f;\u0011\u0005!\u0012\u0014BA\u001a*\u0005I!\u0016m]6BiR,W\u000e\u001d;D_:$X\r\u001f;\t\u000bUz\u0002\u0019\u0001\u001c\u0002\u000b)|'-\u00133\u0011\u0005E9\u0014B\u0001\u001d\u0013\u0005\rIe\u000e\u001e\u0005\u0006u}\u0001\rAN\u0001\bgBd\u0017\u000e^%e\u0001"
)
public final class SparkHadoopMapRedUtil {
   public static boolean initializeLogIfNecessary$default$2() {
      return SparkHadoopMapRedUtil$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SparkHadoopMapRedUtil$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SparkHadoopMapRedUtil$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SparkHadoopMapRedUtil$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SparkHadoopMapRedUtil$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SparkHadoopMapRedUtil$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SparkHadoopMapRedUtil$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SparkHadoopMapRedUtil$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SparkHadoopMapRedUtil$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SparkHadoopMapRedUtil$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SparkHadoopMapRedUtil$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SparkHadoopMapRedUtil$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SparkHadoopMapRedUtil$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SparkHadoopMapRedUtil$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SparkHadoopMapRedUtil$.MODULE$.log();
   }

   public static String logName() {
      return SparkHadoopMapRedUtil$.MODULE$.logName();
   }

   public static void commitTask(OutputCommitter var0, TaskAttemptContext var1, int var2, int var3) {
      SparkHadoopMapRedUtil$.MODULE$.commitTask(var0, var1, var2, var3);
   }
}
