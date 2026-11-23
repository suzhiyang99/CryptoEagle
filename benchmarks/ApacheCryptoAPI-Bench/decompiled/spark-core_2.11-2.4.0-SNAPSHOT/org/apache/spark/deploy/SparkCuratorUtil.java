package org.apache.spark.deploy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%<a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001E*qCJ\\7)\u001e:bi>\u0014X\u000b^5m\u0015\t\u0019A!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOB\u00111\u0002D\u0007\u0002\u0005\u00191QB\u0001E\u0001\t9\u0011\u0001c\u00159be.\u001cUO]1u_J,F/\u001b7\u0014\u00071yQ\u0003\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\rC\u0003\u001d\u0019\u0011\u0005a$\u0001\u0004=S:LGOP\u0002\u0001)\u0005Q\u0001b\u0002\u0011\r\u0005\u0004%I!I\u0001\u001d5.{6i\u0014(O\u000b\u000e#\u0016j\u0014(`)&kUiT+U?6KE\nT%T+\u0005\u0011\u0003C\u0001\t$\u0013\t!\u0013CA\u0002J]RDaA\n\u0007!\u0002\u0013\u0011\u0013!\b.L?\u000e{eJT#D)&{ej\u0018+J\u001b\u0016{U\u000bV0N\u00132c\u0015j\u0015\u0011\t\u000f!b!\u0019!C\u0005C\u0005I\"lS0T\u000bN\u001b\u0016j\u0014(`)&kUiT+U?6KE\nT%T\u0011\u0019QC\u0002)A\u0005E\u0005Q\"lS0T\u000bN\u001b\u0016j\u0014(`)&kUiT+U?6KE\nT%TA!9A\u0006\u0004b\u0001\n\u0013\t\u0013!\u0005*F)JKvlV!J)~k\u0015\n\u0014'J'\"1a\u0006\u0004Q\u0001\n\t\n!CU#U%f{v+Q%U?6KE\nT%TA!9\u0001\u0007\u0004b\u0001\n\u0013\t\u0013AF'B1~\u0013ViQ(O\u001d\u0016\u001bEkX!U)\u0016k\u0005\u000bV*\t\rIb\u0001\u0015!\u0003#\u0003]i\u0015\tW0S\u000b\u000e{eJT#D)~\u000bE\u000bV#N!R\u001b\u0006\u0005C\u00035\u0019\u0011\u0005Q'A\u0005oK^\u001cE.[3oiR\u0019aG\u0010#\u0011\u0005]bT\"\u0001\u001d\u000b\u0005eR\u0014!\u00034sC6,wo\u001c:l\u0015\tYd!A\u0004dkJ\fGo\u001c:\n\u0005uB$\u0001E\"ve\u0006$xN\u001d$sC6,wo\u001c:l\u0011\u0015y4\u00071\u0001A\u0003\u0011\u0019wN\u001c4\u0011\u0005\u0005\u0013U\"\u0001\u0003\n\u0005\r#!!C*qCJ\\7i\u001c8g\u0011\u001d)5\u0007%AA\u0002\u0019\u000b\u0011B_6Ve2\u001cuN\u001c4\u0011\u0005\u001dSeB\u0001\tI\u0013\tI\u0015#\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u00172\u0013aa\u0015;sS:<'BA%\u0012\u0011\u0015qE\u0002\"\u0001P\u0003\u0015i7\u000eZ5s)\r\u00016+\u0016\t\u0003!EK!AU\t\u0003\tUs\u0017\u000e\u001e\u0005\u0006)6\u0003\rAN\u0001\u0003u.DQAV'A\u0002\u0019\u000bA\u0001]1uQ\")\u0001\f\u0004C\u00013\u0006yA-\u001a7fi\u0016\u0014VmY;sg&4X\rF\u0002Q5nCQ\u0001V,A\u0002YBQAV,A\u0002\u0019Cq!\u0018\u0007\u0012\u0002\u0013\u0005a,A\noK^\u001cE.[3oi\u0012\"WMZ1vYR$#'F\u0001`U\t1\u0005mK\u0001b!\t\u0011w-D\u0001d\u0015\t!W-A\u0005v]\u000eDWmY6fI*\u0011a-E\u0001\u000bC:tw\u000e^1uS>t\u0017B\u00015d\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a"
)
public final class SparkCuratorUtil {
   public static boolean initializeLogIfNecessary$default$2() {
      return SparkCuratorUtil$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SparkCuratorUtil$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SparkCuratorUtil$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SparkCuratorUtil$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SparkCuratorUtil$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SparkCuratorUtil$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SparkCuratorUtil$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SparkCuratorUtil$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SparkCuratorUtil$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SparkCuratorUtil$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SparkCuratorUtil$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SparkCuratorUtil$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SparkCuratorUtil$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SparkCuratorUtil$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SparkCuratorUtil$.MODULE$.log();
   }

   public static String logName() {
      return SparkCuratorUtil$.MODULE$.logName();
   }

   public static String newClient$default$2() {
      return SparkCuratorUtil$.MODULE$.newClient$default$2();
   }

   public static void deleteRecursive(CuratorFramework var0, String var1) {
      SparkCuratorUtil$.MODULE$.deleteRecursive(var0, var1);
   }

   public static void mkdir(CuratorFramework var0, String var1) {
      SparkCuratorUtil$.MODULE$.mkdir(var0, var1);
   }

   public static CuratorFramework newClient(SparkConf var0, String var1) {
      return SparkCuratorUtil$.MODULE$.newClient(var0, var1);
   }
}
