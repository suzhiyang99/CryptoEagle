package org.apache.spark.deploy;

import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.sys.process.ProcessBuilder;

@ScalaSignature(
   bytes = "\u0006\u0001Q;Q!\u0001\u0002\t\n-\ta\u0001R8dW\u0016\u0014(BA\u0002\u0005\u0003\u0019!W\r\u001d7ps*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0011\u00051iQ\"\u0001\u0002\u0007\u000b9\u0011\u0001\u0012B\b\u0003\r\u0011{7m[3s'\ri\u0001C\u0006\t\u0003#Qi\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0005]QR\"\u0001\r\u000b\u0005e!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005mA\"a\u0002'pO\u001eLgn\u001a\u0005\u0006;5!\tAH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003-AQ\u0001I\u0007\u0005\u0002\u0005\n!\"\\1lKJ+hnQ7e)\u0011\u0011#fM\u001b\u0011\u0005\rBS\"\u0001\u0013\u000b\u0005\u00152\u0013a\u00029s_\u000e,7o\u001d\u0006\u0003OI\t1a]=t\u0013\tICE\u0001\bQe>\u001cWm]:Ck&dG-\u001a:\t\u000b-z\u0002\u0019\u0001\u0017\u0002\u0011%l\u0017mZ3UC\u001e\u0004\"!\f\u0019\u000f\u0005Eq\u0013BA\u0018\u0013\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011G\r\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005=\u0012\u0002b\u0002\u001b !\u0003\u0005\r\u0001L\u0001\u0005CJ<7\u000fC\u00047?A\u0005\t\u0019\u0001\u0017\u0002\u00115|WO\u001c;ESJDQ\u0001O\u0007\u0005\u0002e\nAa[5mYR\u0011!(\u0010\t\u0003#mJ!\u0001\u0010\n\u0003\tUs\u0017\u000e\u001e\u0005\u0006}]\u0002\raP\u0001\tI>\u001c7.\u001a:JIB\u0011A\u0002Q\u0005\u0003\u0003\n\u0011\u0001\u0002R8dW\u0016\u0014\u0018\n\u001a\u0005\u0006\u00076!\t\u0001R\u0001\u0011O\u0016$H*Y:u!J|7-Z:t\u0013\u0012,\u0012a\u0010\u0005\b\r6\t\n\u0011\"\u0001H\u0003Qi\u0017m[3Sk:\u001cU\u000e\u001a\u0013eK\u001a\fW\u000f\u001c;%eU\t\u0001J\u000b\u0002-\u0013.\n!\n\u0005\u0002L!6\tAJ\u0003\u0002N\u001d\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u001fJ\t!\"\u00198o_R\fG/[8o\u0013\t\tFJA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016DqaU\u0007\u0012\u0002\u0013\u0005q)\u0001\u000bnC.,'+\u001e8D[\u0012$C-\u001a4bk2$He\r"
)
public final class Docker {
   public static boolean initializeLogIfNecessary$default$2() {
      return Docker$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return Docker$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      Docker$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return Docker$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      Docker$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      Docker$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      Docker$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      Docker$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      Docker$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      Docker$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      Docker$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      Docker$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      Docker$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      Docker$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return Docker$.MODULE$.log();
   }

   public static String logName() {
      return Docker$.MODULE$.logName();
   }

   public static String makeRunCmd$default$3() {
      return Docker$.MODULE$.makeRunCmd$default$3();
   }

   public static String makeRunCmd$default$2() {
      return Docker$.MODULE$.makeRunCmd$default$2();
   }

   public static DockerId getLastProcessId() {
      return Docker$.MODULE$.getLastProcessId();
   }

   public static void kill(DockerId var0) {
      Docker$.MODULE$.kill(var0);
   }

   public static ProcessBuilder makeRunCmd(String var0, String var1, String var2) {
      return Docker$.MODULE$.makeRunCmd(var0, var1, var2);
   }
}
