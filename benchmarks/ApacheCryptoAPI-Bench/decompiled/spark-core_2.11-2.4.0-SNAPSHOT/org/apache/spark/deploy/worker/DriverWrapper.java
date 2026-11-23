package org.apache.spark.deploy.worker;

import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0001;Q!\u0001\u0002\t\u00025\tQ\u0002\u0012:jm\u0016\u0014xK]1qa\u0016\u0014(BA\u0002\u0005\u0003\u00199xN]6fe*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u000f\u001f5\t!AB\u0003\u0011\u0005!\u0005\u0011CA\u0007Ee&4XM],sCB\u0004XM]\n\u0004\u001fIA\u0002CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"AB!osJ+g\r\u0005\u0002\u001a95\t!D\u0003\u0002\u001c\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001e5\t9Aj\\4hS:<\u0007\"B\u0010\u0010\t\u0003\u0001\u0013A\u0002\u001fj]&$h\bF\u0001\u000e\u0011\u0015\u0011s\u0002\"\u0001$\u0003\u0011i\u0017-\u001b8\u0015\u0005\u0011:\u0003CA\n&\u0013\t1CC\u0001\u0003V]&$\b\"\u0002\u0015\"\u0001\u0004I\u0013\u0001B1sON\u00042a\u0005\u0016-\u0013\tYCCA\u0003BeJ\f\u0017\u0010\u0005\u0002.a9\u00111CL\u0005\u0003_Q\ta\u0001\u0015:fI\u00164\u0017BA\u00193\u0005\u0019\u0019FO]5oO*\u0011q\u0006\u0006\u0005\u0006i=!I!N\u0001\u0012g\u0016$X\u000f\u001d#fa\u0016tG-\u001a8dS\u0016\u001cHc\u0001\u00137}!)qg\ra\u0001q\u00051An\\1eKJ\u0004\"!\u000f\u001f\u000e\u0003iR!a\u000f\u0004\u0002\tU$\u0018\u000e\\\u0005\u0003{i\u0012Q#T;uC\ndW-\u0016*M\u00072\f7o\u001d'pC\u0012,'\u000fC\u0003@g\u0001\u0007A&A\u0004vg\u0016\u0014(*\u0019:"
)
public final class DriverWrapper {
   public static boolean initializeLogIfNecessary$default$2() {
      return DriverWrapper$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return DriverWrapper$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      DriverWrapper$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return DriverWrapper$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      DriverWrapper$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      DriverWrapper$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      DriverWrapper$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      DriverWrapper$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      DriverWrapper$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      DriverWrapper$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      DriverWrapper$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      DriverWrapper$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      DriverWrapper$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      DriverWrapper$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return DriverWrapper$.MODULE$.log();
   }

   public static String logName() {
      return DriverWrapper$.MODULE$.logName();
   }

   public static void main(String[] var0) {
      DriverWrapper$.MODULE$.main(var0);
   }
}
