package org.apache.spark.util;

import java.io.File;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005qAB\u0001\u0003\u0011\u0003!!\"A\nTQV$Hm\\<o\u0011>|7.T1oC\u001e,'O\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<\u0007CA\u0006\r\u001b\u0005\u0011aAB\u0007\u0003\u0011\u0003!aBA\nTQV$Hm\\<o\u0011>|7.T1oC\u001e,'oE\u0002\r\u001fU\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007C\u0001\f\u001a\u001b\u00059\"B\u0001\r\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000e\u0018\u0005\u001daunZ4j]\u001eDQ\u0001\b\u0007\u0005\u0002y\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0015!9\u0001\u0005\u0004b\u0001\n\u0003\t\u0013!\u0007#F\r\u0006+F\nV0T\u0011V#FiT,O?B\u0013\u0016j\u0014*J)f+\u0012A\t\t\u0003!\rJ!\u0001J\t\u0003\u0007%sG\u000f\u0003\u0004'\u0019\u0001\u0006IAI\u0001\u001b\t\u00163\u0015)\u0016'U?NCU\u000b\u0016#P/:{\u0006KU%P%&#\u0016\f\t\u0005\bQ1\u0011\r\u0011\"\u0001\"\u0003}\u0019\u0006+\u0011*L?\u000e{e\nV#Y)~\u001b\u0006*\u0016+E\u001f^su\f\u0015*J\u001fJKE+\u0017\u0005\u0007U1\u0001\u000b\u0011\u0002\u0012\u0002AM\u0003\u0016IU&`\u0007>sE+\u0012-U?NCU\u000b\u0016#P/:{\u0006KU%P%&#\u0016\f\t\u0005\bY1\u0011\r\u0011\"\u0001\"\u0003i!V)\u0014)`\t&\u0013vl\u0015%V)\u0012{uKT0Q%&{%+\u0013+Z\u0011\u0019qC\u0002)A\u0005E\u0005YB+R'Q?\u0012K%kX*I+R#uj\u0016(`!JKuJU%U3\u0002B\u0001\u0002\r\u0007\t\u0006\u0004%I!M\u0001\u000eg\",H\u000fZ8x]\"{wn[:\u0016\u0003I\u0002\"aC\u001a\n\u0005Q\u0012!\u0001G*qCJ\\7\u000b[;uI><h\u000eS8pW6\u000bg.Y4fe\"Aa\u0007\u0004E\u0001B\u0003&!'\u0001\btQV$Hm\\<o\u0011>|7n\u001d\u0011\t\u000fab!\u0019!C\u0005s\u0005\u00192\u000f[;uI><h\u000eR3mKR,\u0007+\u0019;igV\t!\bE\u0002<\u0001\nk\u0011\u0001\u0010\u0006\u0003{y\nq!\\;uC\ndWM\u0003\u0002@#\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0005c$a\u0002%bg\"\u001cV\r\u001e\t\u0003\u0007\u001as!\u0001\u0005#\n\u0005\u0015\u000b\u0012A\u0002)sK\u0012,g-\u0003\u0002H\u0011\n11\u000b\u001e:j]\u001eT!!R\t\t\r)c\u0001\u0015!\u0003;\u0003Q\u0019\b.\u001e;e_^tG)\u001a7fi\u0016\u0004\u0016\r\u001e5tA!)A\n\u0004C\u0001\u001b\u0006I\"/Z4jgR,'o\u00155vi\u0012|wO\u001c#fY\u0016$X\rR5s)\tq\u0015\u000b\u0005\u0002\u0011\u001f&\u0011\u0001+\u0005\u0002\u0005+:LG\u000fC\u0003S\u0017\u0002\u00071+\u0001\u0003gS2,\u0007C\u0001+Z\u001b\u0005)&B\u0001,X\u0003\tIwNC\u0001Y\u0003\u0011Q\u0017M^1\n\u0005i+&\u0001\u0002$jY\u0016DQ\u0001\u0018\u0007\u0005\u0002u\u000bqC]3n_Z,7\u000b[;uI><h\u000eR3mKR,G)\u001b:\u0015\u00059s\u0006\"\u0002*\\\u0001\u0004\u0019\u0006\"\u00021\r\t\u0003\t\u0017\u0001\u00065bgNCW\u000f\u001e3po:$U\r\\3uK\u0012K'\u000f\u0006\u0002cKB\u0011\u0001cY\u0005\u0003IF\u0011qAQ8pY\u0016\fg\u000eC\u0003S?\u0002\u00071\u000bC\u0003h\u0019\u0011\u0005\u0001.\u0001\u000eiCN\u0014vn\u001c;BgNCW\u000f\u001e3po:$U\r\\3uK\u0012K'\u000f\u0006\u0002cS\")!K\u001aa\u0001'\")1\u000e\u0004C\u0001Y\u0006Q\u0011N\\*ikR$wn\u001e8\u0015\u0003\tDQA\u001c\u0007\u0005\u0002=\fq\"\u00193e'\",H\u000fZ8x]\"{wn\u001b\u000b\u0003\u001fADQ!]7A\u0002I\fA\u0001[8pWB\u0019\u0001c\u001d(\n\u0005Q\f\"!\u0003$v]\u000e$\u0018n\u001c81\u0011\u0015qG\u0002\"\u0001w)\t9\u0018\u0010\u0006\u0002\u0010q\")\u0011/\u001ea\u0001e\")!0\u001ea\u0001E\u0005A\u0001O]5pe&$\u0018\u0010C\u0003}\u0019\u0011\u0005Q0\u0001\nsK6|g/Z*ikR$wn\u001e8I_>\\GC\u00012\u007f\u0011\u0015y8\u00101\u0001\u0010\u0003\r\u0011XM\u001a"
)
public final class ShutdownHookManager {
   public static boolean initializeLogIfNecessary$default$2() {
      return ShutdownHookManager$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return ShutdownHookManager$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      ShutdownHookManager$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return ShutdownHookManager$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      ShutdownHookManager$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      ShutdownHookManager$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      ShutdownHookManager$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      ShutdownHookManager$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      ShutdownHookManager$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      ShutdownHookManager$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      ShutdownHookManager$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      ShutdownHookManager$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      ShutdownHookManager$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      ShutdownHookManager$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return ShutdownHookManager$.MODULE$.log();
   }

   public static String logName() {
      return ShutdownHookManager$.MODULE$.logName();
   }

   public static boolean removeShutdownHook(Object var0) {
      return ShutdownHookManager$.MODULE$.removeShutdownHook(var0);
   }

   public static Object addShutdownHook(int var0, Function0<BoxedUnit> var1) {
      return ShutdownHookManager$.MODULE$.addShutdownHook(var0, var1);
   }

   public static Object addShutdownHook(Function0<BoxedUnit> var0) {
      return ShutdownHookManager$.MODULE$.addShutdownHook(var0);
   }

   public static boolean inShutdown() {
      return ShutdownHookManager$.MODULE$.inShutdown();
   }

   public static boolean hasRootAsShutdownDeleteDir(File var0) {
      return ShutdownHookManager$.MODULE$.hasRootAsShutdownDeleteDir(var0);
   }

   public static boolean hasShutdownDeleteDir(File var0) {
      return ShutdownHookManager$.MODULE$.hasShutdownDeleteDir(var0);
   }

   public static void removeShutdownDeleteDir(File var0) {
      ShutdownHookManager$.MODULE$.removeShutdownDeleteDir(var0);
   }

   public static void registerShutdownDeleteDir(File var0) {
      ShutdownHookManager$.MODULE$.registerShutdownDeleteDir(var0);
   }

   public static int TEMP_DIR_SHUTDOWN_PRIORITY() {
      return ShutdownHookManager$.MODULE$.TEMP_DIR_SHUTDOWN_PRIORITY();
   }

   public static int SPARK_CONTEXT_SHUTDOWN_PRIORITY() {
      return ShutdownHookManager$.MODULE$.SPARK_CONTEXT_SHUTDOWN_PRIORITY();
   }

   public static int DEFAULT_SHUTDOWN_PRIORITY() {
      return ShutdownHookManager$.MODULE$.DEFAULT_SHUTDOWN_PRIORITY();
   }
}
