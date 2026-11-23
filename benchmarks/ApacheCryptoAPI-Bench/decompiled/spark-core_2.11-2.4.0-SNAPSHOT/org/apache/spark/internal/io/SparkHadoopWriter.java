package org.apache.spark.internal.io;

import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import scala.Function0;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=rAB\u0001\u0003\u0011\u00031A\"A\tTa\u0006\u00148\u000eS1e_>\u0004xK]5uKJT!a\u0001\u0003\u0002\u0005%|'BA\u0003\u0007\u0003!Ig\u000e^3s]\u0006d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0011\u00055qQ\"\u0001\u0002\u0007\r=\u0011\u0001\u0012\u0001\u0004\u0011\u0005E\u0019\u0006/\u0019:l\u0011\u0006$wn\u001c9Xe&$XM]\n\u0004\u001dE9\u0002C\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"AB!osJ+g\r\u0005\u0002\u001935\tA!\u0003\u0002\u001b\t\t9Aj\\4hS:<\u0007\"\u0002\u000f\u000f\t\u0003q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u00031AQ\u0001\t\b\u0005\u0002\u0005\nQa\u001e:ji\u0016,2AI#2)\r\u0019#h\u0012\u000b\u0003I\u001d\u0002\"AE\u0013\n\u0005\u0019\u001a\"\u0001B+oSRDq\u0001K\u0010\u0002\u0002\u0003\u000f\u0011&\u0001\u0006fm&$WM\\2fIE\u00022AK\u00170\u001b\u0005Y#B\u0001\u0017\u0014\u0003\u001d\u0011XM\u001a7fGRL!AL\u0016\u0003\u0011\rc\u0017m]:UC\u001e\u0004\"\u0001M\u0019\r\u0001\u0011)!g\bb\u0001g\t\ta+\u0005\u00025oA\u0011!#N\u0005\u0003mM\u0011qAT8uQ&tw\r\u0005\u0002\u0013q%\u0011\u0011h\u0005\u0002\u0004\u0003:L\b\"B\u001e \u0001\u0004a\u0014a\u0001:eIB\u0019QhP!\u000e\u0003yR!a\u000f\u0004\n\u0005\u0001s$a\u0001*E\tB!!C\u0011#0\u0013\t\u00195C\u0001\u0004UkBdWM\r\t\u0003a\u0015#QAR\u0010C\u0002M\u0012\u0011a\u0013\u0005\u0006\u0011~\u0001\r!S\u0001\u0007G>tg-[4\u0011\t5QEiL\u0005\u0003\u0017\n\u0011Q\u0003S1e_>\u0004xK]5uK\u000e{gNZ5h+RLG\u000eC\u0003N\u001d\u0011%a*A\u0006fq\u0016\u001cW\u000f^3UCN\\WcA(tSRi\u0001K\u001b9u{\u0006\u0015\u0011\u0011BA\u0007\u0003/!\"!U3\u0011\u0005I\u0013gBA*a\u001d\t!vL\u0004\u0002V=:\u0011a+\u0018\b\u0003/rs!\u0001W.\u000e\u0003eS!AW\u000f\u0002\rq\u0012xn\u001c;?\u0013\u0005Y\u0011BA\u0005\u000b\u0013\t9\u0001\"\u0003\u0002\u0006\r%\u00111\u0001B\u0005\u0003C\n\t!CR5mK\u000e{W.\\5u!J|Go\\2pY&\u00111\r\u001a\u0002\u0012)\u0006\u001c8nQ8n[&$X*Z:tC\u001e,'BA1\u0003\u0011\u001d1G*!AA\u0004\u001d\f!\"\u001a<jI\u0016t7-\u001a\u00133!\rQS\u0006\u001b\t\u0003a%$QA\r'C\u0002MBQa\u001b'A\u00021\fqaY8oi\u0016DH\u000f\u0005\u0002n]6\ta!\u0003\u0002p\r\tYA+Y:l\u0007>tG/\u001a=u\u0011\u0015AE\n1\u0001r!\u0011i!J\u001d5\u0011\u0005A\u001aH!\u0002$M\u0005\u0004\u0019\u0004\"B;M\u0001\u00041\u0018\u0001\u00046pER\u0013\u0018mY6fe&#\u0007CA<{\u001d\t\u0011\u00020\u0003\u0002z'\u00051\u0001K]3eK\u001aL!a\u001f?\u0003\rM#(/\u001b8h\u0015\tI8\u0003C\u0003\u007f\u0019\u0002\u0007q0A\u0006d_6l\u0017\u000e\u001e&pE&#\u0007c\u0001\n\u0002\u0002%\u0019\u00111A\n\u0003\u0007%sG\u000f\u0003\u0004\u0002\b1\u0003\ra`\u0001\u0011gB\f'o\u001b)beRLG/[8o\u0013\u0012Da!a\u0003M\u0001\u0004y\u0018AE:qCJ\\\u0017\t\u001e;f[B$h*^7cKJDq!a\u0004M\u0001\u0004\t\t\"A\u0005d_6l\u0017\u000e\u001e;feB\u0019Q\"a\u0005\n\u0007\u0005U!A\u0001\nGS2,7i\\7nSR\u0004&o\u001c;pG>d\u0007bBA\r\u0019\u0002\u0007\u00111D\u0001\tSR,'/\u0019;peB1\u0011QDA\u0014\u0003[qA!a\b\u0002$9\u0019\u0001,!\t\n\u0003QI1!!\n\u0014\u0003\u001d\u0001\u0018mY6bO\u0016LA!!\u000b\u0002,\tA\u0011\n^3sCR|'OC\u0002\u0002&M\u0001BA\u0005\"sQ\u0002"
)
public final class SparkHadoopWriter {
   public static boolean initializeLogIfNecessary$default$2() {
      return SparkHadoopWriter$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SparkHadoopWriter$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SparkHadoopWriter$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SparkHadoopWriter$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SparkHadoopWriter$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SparkHadoopWriter$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SparkHadoopWriter$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SparkHadoopWriter$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SparkHadoopWriter$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SparkHadoopWriter$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SparkHadoopWriter$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SparkHadoopWriter$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SparkHadoopWriter$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SparkHadoopWriter$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SparkHadoopWriter$.MODULE$.log();
   }

   public static String logName() {
      return SparkHadoopWriter$.MODULE$.logName();
   }

   public static <K, V> void write(RDD<Tuple2<K, V>> var0, HadoopWriteConfigUtil<K, V> var1, ClassTag<V> var2) {
      SparkHadoopWriter$.MODULE$.write(var0, var1, var2);
   }
}
