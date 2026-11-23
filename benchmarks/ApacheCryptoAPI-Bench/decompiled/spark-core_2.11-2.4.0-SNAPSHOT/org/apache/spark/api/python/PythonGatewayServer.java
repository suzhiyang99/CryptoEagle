package org.apache.spark.api.python;

import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M:a!\u0001\u0002\t\u0002\u0019a\u0011a\u0005)zi\"|gnR1uK^\f\u0017pU3sm\u0016\u0014(BA\u0002\u0005\u0003\u0019\u0001\u0018\u0010\u001e5p]*\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0011\u00055qQ\"\u0001\u0002\u0007\r=\u0011\u0001\u0012\u0001\u0004\u0011\u0005M\u0001\u0016\u0010\u001e5p]\u001e\u000bG/Z<bsN+'O^3s'\rq\u0011c\u0006\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0005aYR\"A\r\u000b\u0005i1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005qI\"a\u0002'pO\u001eLgn\u001a\u0005\u0006=9!\t\u0001I\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tA\u0002C\u0003#\u001d\u0011\u00051%\u0001\u0003nC&tGC\u0001\u0013(!\t\u0011R%\u0003\u0002''\t!QK\\5u\u0011\u0015A\u0013\u00051\u0001*\u0003\u0011\t'oZ:\u0011\u0007IQC&\u0003\u0002,'\t)\u0011I\u001d:bsB\u0011Q\u0006\r\b\u0003%9J!aL\n\u0002\rA\u0013X\rZ3g\u0013\t\t$G\u0001\u0004TiJLgn\u001a\u0006\u0003_M\u0001"
)
public final class PythonGatewayServer {
   public static boolean initializeLogIfNecessary$default$2() {
      return PythonGatewayServer$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return PythonGatewayServer$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      PythonGatewayServer$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return PythonGatewayServer$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      PythonGatewayServer$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      PythonGatewayServer$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      PythonGatewayServer$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      PythonGatewayServer$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      PythonGatewayServer$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      PythonGatewayServer$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      PythonGatewayServer$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      PythonGatewayServer$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      PythonGatewayServer$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      PythonGatewayServer$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return PythonGatewayServer$.MODULE$.log();
   }

   public static String logName() {
      return PythonGatewayServer$.MODULE$.logName();
   }

   public static void main(String[] var0) {
      PythonGatewayServer$.MODULE$.main(var0);
   }
}
