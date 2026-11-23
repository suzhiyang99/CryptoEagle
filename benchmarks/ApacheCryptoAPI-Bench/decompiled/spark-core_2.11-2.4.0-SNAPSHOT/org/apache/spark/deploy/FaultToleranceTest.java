package org.apache.spark.deploy;

import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rx!B\u0001\u0003\u0011\u0013Y\u0011A\u0005$bk2$Hk\u001c7fe\u0006t7-\u001a+fgRT!a\u0001\u0003\u0002\r\u0011,\u0007\u000f\\8z\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001\u0001\t\u0003\u00195i\u0011A\u0001\u0004\u0006\u001d\tAIa\u0004\u0002\u0013\r\u0006,H\u000e\u001e+pY\u0016\u0014\u0018M\\2f)\u0016\u001cHo\u0005\u0003\u000e!YI\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0005\u0002\u0012/%\u0011\u0001D\u0005\u0002\u0004\u0003B\u0004\bC\u0001\u000e\u001e\u001b\u0005Y\"B\u0001\u000f\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0010\u001c\u0005\u001daunZ4j]\u001eDQ\u0001I\u0007\u0005\u0002\u0005\na\u0001P5oSRtD#A\u0006\t\u000f\rj!\u0019!C\u0005I\u0005!1m\u001c8g+\u0005)\u0003C\u0001\u0014(\u001b\u0005!\u0011B\u0001\u0015\u0005\u0005%\u0019\u0006/\u0019:l\u0007>tg\r\u0003\u0004+\u001b\u0001\u0006I!J\u0001\u0006G>tg\r\t\u0005\bY5\u0011\r\u0011\"\u0003.\u0003\u0019Q6j\u0018#J%V\ta\u0006\u0005\u00020e9\u0011\u0011\u0003M\u0005\u0003cI\ta\u0001\u0015:fI\u00164\u0017BA\u001a5\u0005\u0019\u0019FO]5oO*\u0011\u0011G\u0005\u0005\u0007m5\u0001\u000b\u0011\u0002\u0018\u0002\u000fi[u\fR%SA!9\u0001(\u0004b\u0001\n\u0013I\u0014aB7bgR,'o]\u000b\u0002uA\u00191\b\u0011\"\u000e\u0003qR!!\u0010 \u0002\u000f5,H/\u00192mK*\u0011qHE\u0001\u000bG>dG.Z2uS>t\u0017BA!=\u0005)a\u0015n\u001d;Ck\u001a4WM\u001d\t\u0003\u0019\rK!\u0001\u0012\u0002\u0003\u001dQ+7\u000f^'bgR,'/\u00138g_\"1a)\u0004Q\u0001\ni\n\u0001\"\\1ti\u0016\u00148\u000f\t\u0005\b\u00116\u0011\r\u0011\"\u0003J\u0003\u001d9xN]6feN,\u0012A\u0013\t\u0004w\u0001[\u0005C\u0001\u0007M\u0013\ti%A\u0001\bUKN$xk\u001c:lKJLeNZ8\t\r=k\u0001\u0015!\u0003K\u0003!9xN]6feN\u0004\u0003\"C)\u000e\u0001\u0004\u0005\r\u0011\"\u0003S\u0003\t\u00198-F\u0001T!\t1C+\u0003\u0002V\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"Iq+\u0004a\u0001\u0002\u0004%I\u0001W\u0001\u0007g\u000e|F%Z9\u0015\u0005ec\u0006CA\t[\u0013\tY&C\u0001\u0003V]&$\bbB/W\u0003\u0003\u0005\raU\u0001\u0004q\u0012\n\u0004BB0\u000eA\u0003&1+A\u0002tG\u0002Bq!Y\u0007C\u0002\u0013%!-\u0001\u0002{WV\t1\r\u0005\u0002eS6\tQM\u0003\u0002gO\u0006IaM]1nK^|'o\u001b\u0006\u0003Q\u001a\tqaY;sCR|'/\u0003\u0002kK\n\u00012)\u001e:bi>\u0014hI]1nK^|'o\u001b\u0005\u0007Y6\u0001\u000b\u0011B2\u0002\u0007i\\\u0007\u0005C\u0004o\u001b\u0001\u0007I\u0011B8\u0002\u00139,X\u000eU1tg\u0016$W#\u00019\u0011\u0005E\t\u0018B\u0001:\u0013\u0005\rIe\u000e\u001e\u0005\bi6\u0001\r\u0011\"\u0003v\u00035qW/\u001c)bgN,Gm\u0018\u0013fcR\u0011\u0011L\u001e\u0005\b;N\f\t\u00111\u0001q\u0011\u0019AX\u0002)Q\u0005a\u0006Qa.^7QCN\u001cX\r\u001a\u0011\t\u000fil\u0001\u0019!C\u0005_\u0006Ia.^7GC&dW\r\u001a\u0005\by6\u0001\r\u0011\"\u0003~\u00035qW/\u001c$bS2,Gm\u0018\u0013fcR\u0011\u0011L \u0005\b;n\f\t\u00111\u0001q\u0011\u001d\t\t!\u0004Q!\nA\f!B\\;n\r\u0006LG.\u001a3!\u0011%\t)!\u0004b\u0001\n\u0013\t9!A\u0005ta\u0006\u00148\u000eS8nKV\u0011\u0011\u0011\u0002\t\u0005\u0003\u0017\t)\"\u0004\u0002\u0002\u000e)!\u0011qBA\t\u0003\u0011a\u0017M\\4\u000b\u0005\u0005M\u0011\u0001\u00026bm\u0006L1aMA\u0007\u0011!\tI\"\u0004Q\u0001\n\u0005%\u0011AC:qCJ\\\u0007j\\7fA!I\u0011QD\u0007C\u0002\u0013%\u0011qA\u0001\u0013G>tG/Y5oKJ\u001c\u0006/\u0019:l\u0011>lW\r\u0003\u0005\u0002\"5\u0001\u000b\u0011BA\u0005\u0003M\u0019wN\u001c;bS:,'o\u00159be.Du.\\3!\u0011!\t)#\u0004b\u0001\n\u0013i\u0013A\u00043pG.,'/T8v]R$\u0015N\u001d\u0005\b\u0003Si\u0001\u0015!\u0003/\u0003=!wnY6fe6{WO\u001c;ESJ\u0004\u0003bBA\u0017\u001b\u0011%\u0011qF\u0001\nC\u001a$XM]#bG\"$\u0012!\u0017\u0005\b\u0003giA\u0011BA\u001b\u0003\u0011!Xm\u001d;\u0015\t\u0005]\u00121\t\u000b\u00043\u0006e\u0002\"CA\u001e\u0003c!\t\u0019AA\u001f\u0003\t1g\u000e\u0005\u0003\u0012\u0003\u007fI\u0016bAA!%\tAAHY=oC6,g\bC\u0004\u0002F\u0005E\u0002\u0019\u0001\u0018\u0002\t9\fW.\u001a\u0005\b\u0003\u0013jA\u0011BA&\u0003)\tG\rZ'bgR,'o\u001d\u000b\u00043\u00065\u0003bBA(\u0003\u000f\u0002\r\u0001]\u0001\u0004]Vl\u0007bBA*\u001b\u0011%\u0011QK\u0001\u000bC\u0012$wk\u001c:lKJ\u001cHcA-\u0002X!9\u0011qJA)\u0001\u0004\u0001\bbBA.\u001b\u0011%\u0011qF\u0001\rGJ,\u0017\r^3DY&,g\u000e\u001e\u0005\b\u0003?jA\u0011BA1\u000359W\r^'bgR,'/\u0016:mgR\u0019a&a\u0019\t\u000fa\ni\u00061\u0001\u0002fA)\u0011qMA<\u0005:!\u0011\u0011NA:\u001d\u0011\tY'!\u001d\u000e\u0005\u00055$bAA8\u0015\u00051AH]8pizJ\u0011aE\u0005\u0004\u0003k\u0012\u0012a\u00029bG.\fw-Z\u0005\u0005\u0003s\nYHA\u0002TKFT1!!\u001e\u0013\u0011\u001d\ty(\u0004C\u0005\u0003\u0003\u000b\u0011bZ3u\u0019\u0016\fG-\u001a:\u0016\u0003\tCq!!\"\u000e\t\u0013\ty#\u0001\u0006lS2dG*Z1eKJDq!!#\u000e\t\u0013\tY)A\u0003eK2\f\u0017\u0010F\u0002Z\u0003\u001bC!\"a$\u0002\bB\u0005\t\u0019AAI\u0003\u0011\u0019XmY:\u0011\t\u0005M\u0015QT\u0007\u0003\u0003+SA!a&\u0002\u001a\u0006AA-\u001e:bi&|gNC\u0002\u0002\u001cJ\t!bY8oGV\u0014(/\u001a8u\u0013\u0011\ty*!&\u0003\u0011\u0011+(/\u0019;j_:Dq!a)\u000e\t\u0013\ty#\u0001\tuKJl\u0017N\\1uK\u000ecWo\u001d;fe\"9\u0011qU\u0007\u0005\n\u0005=\u0012\u0001D1tg\u0016\u0014H/V:bE2,\u0007bBAV\u001b\u0011%\u0011qF\u0001\u0018CN\u001cXM\u001d;WC2LGm\u00117vgR,'o\u0015;bi\u0016Dq!a,\u000e\t\u0013\t\t,\u0001\u0006bgN,'\u000f\u001e+sk\u0016$R!WAZ\u0003{C\u0001\"!.\u0002.\u0002\u0007\u0011qW\u0001\u0005E>|G\u000eE\u0002\u0012\u0003sK1!a/\u0013\u0005\u001d\u0011un\u001c7fC:D\u0011\"a0\u0002.B\u0005\t\u0019\u0001\u0018\u0002\u000f5,7o]1hK\"I\u00111Y\u0007\u0012\u0002\u0013%\u0011QY\u0001\u0015CN\u001cXM\u001d;UeV,G\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005\u001d'f\u0001\u0018\u0002J.\u0012\u00111\u001a\t\u0005\u0003\u001b\f9.\u0004\u0002\u0002P*!\u0011\u0011[Aj\u0003%)hn\u00195fG.,GMC\u0002\u0002VJ\t!\"\u00198o_R\fG/[8o\u0013\u0011\tI.a4\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002^6\t\n\u0011\"\u0003\u0002`\u0006yA-\u001a7bs\u0012\"WMZ1vYR$\u0013'\u0006\u0002\u0002b*\"\u0011\u0011SAe\u0001"
)
public final class FaultToleranceTest {
   public static void main(String[] var0) {
      FaultToleranceTest$.MODULE$.main(var0);
   }

   public static void delayedInit(Function0<BoxedUnit> var0) {
      FaultToleranceTest$.MODULE$.delayedInit(var0);
   }

   public static String[] args() {
      return FaultToleranceTest$.MODULE$.args();
   }

   public static void scala$App$_setter_$executionStart_$eq(long var0) {
      FaultToleranceTest$.MODULE$.scala$App$_setter_$executionStart_$eq(var0);
   }

   public static long executionStart() {
      return FaultToleranceTest$.MODULE$.executionStart();
   }

   public static boolean initializeLogIfNecessary$default$2() {
      return FaultToleranceTest$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return FaultToleranceTest$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      FaultToleranceTest$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return FaultToleranceTest$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      FaultToleranceTest$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      FaultToleranceTest$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      FaultToleranceTest$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      FaultToleranceTest$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      FaultToleranceTest$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      FaultToleranceTest$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      FaultToleranceTest$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      FaultToleranceTest$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      FaultToleranceTest$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      FaultToleranceTest$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return FaultToleranceTest$.MODULE$.log();
   }

   public static String logName() {
      return FaultToleranceTest$.MODULE$.logName();
   }

   public static void delayedEndpoint$org$apache$spark$deploy$FaultToleranceTest$1() {
      FaultToleranceTest$.MODULE$.delayedEndpoint$org$apache$spark$deploy$FaultToleranceTest$1();
   }
}
