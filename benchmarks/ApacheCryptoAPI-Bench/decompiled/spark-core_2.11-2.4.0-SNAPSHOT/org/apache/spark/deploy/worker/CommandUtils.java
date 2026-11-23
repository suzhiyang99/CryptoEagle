package org.apache.spark.deploy.worker;

import java.io.File;
import java.io.InputStream;
import org.apache.spark.SecurityManager;
import org.apache.spark.deploy.Command;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.collection.Map;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055rAB\u0001\u0003\u0011\u0003!A\"\u0001\u0007D_6l\u0017M\u001c3Vi&d7O\u0003\u0002\u0004\t\u00051qo\u001c:lKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<\u0007CA\u0007\u000f\u001b\u0005\u0011aAB\b\u0003\u0011\u0003!\u0001C\u0001\u0007D_6l\u0017M\u001c3Vi&d7oE\u0002\u000f#]\u0001\"AE\u000b\u000e\u0003MQ\u0011\u0001F\u0001\u0006g\u000e\fG.Y\u0005\u0003-M\u0011a!\u00118z%\u00164\u0007C\u0001\r\u001c\u001b\u0005I\"B\u0001\u000e\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000f\u001a\u0005\u001daunZ4j]\u001eDQA\b\b\u0005\u0002\u0001\na\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!)!E\u0004C\u0001G\u0005\u0019\"-^5mIB\u0013xnY3tg\n+\u0018\u000e\u001c3feRAA\u0005\f\u001a9{\u0019[\u0015\f\u0005\u0002&U5\taE\u0003\u0002(Q\u0005!A.\u00198h\u0015\u0005I\u0013\u0001\u00026bm\u0006L!a\u000b\u0014\u0003\u001dA\u0013xnY3tg\n+\u0018\u000e\u001c3fe\")Q&\ta\u0001]\u000591m\\7nC:$\u0007CA\u00181\u001b\u0005!\u0011BA\u0019\u0005\u0005\u001d\u0019u.\\7b]\u0012DQaM\u0011A\u0002Q\n1b]3dkJLG/_'heB\u0011QGN\u0007\u0002\r%\u0011qG\u0002\u0002\u0010'\u0016\u001cWO]5us6\u000bg.Y4fe\")\u0011(\ta\u0001u\u00051Q.Z7pef\u0004\"AE\u001e\n\u0005q\u001a\"aA%oi\")a(\ta\u0001\u007f\u0005I1\u000f]1sW\"{W.\u001a\t\u0003\u0001\u000es!AE!\n\u0005\t\u001b\u0012A\u0002)sK\u0012,g-\u0003\u0002E\u000b\n11\u000b\u001e:j]\u001eT!AQ\n\t\u000b\u001d\u000b\u0003\u0019\u0001%\u0002'M,(m\u001d;jiV$X-\u0011:hk6,g\u000e^:\u0011\tIIuhP\u0005\u0003\u0015N\u0011\u0011BR;oGRLwN\\\u0019\t\u000f1\u000b\u0003\u0013!a\u0001\u001b\u0006Q1\r\\1tgB\u000bG\u000f[:\u0011\u000793vH\u0004\u0002P):\u0011\u0001kU\u0007\u0002#*\u0011!kH\u0001\u0007yI|w\u000e\u001e \n\u0003QI!!V\n\u0002\u000fA\f7m[1hK&\u0011q\u000b\u0017\u0002\u0004'\u0016\f(BA+\u0014\u0011\u001dQ\u0016\u0005%AA\u0002m\u000b1!\u001a8w!\u0011avlP \u000e\u0003uS!AX\n\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002a;\n\u0019Q*\u00199\t\u000b\ttA\u0011B2\u0002\u001f\t,\u0018\u000e\u001c3D_6l\u0017M\u001c3TKF$B!\u00143fM\")Q&\u0019a\u0001]!)\u0011(\u0019a\u0001u!)a(\u0019a\u0001\u007f!)\u0001N\u0004C\u0005S\u0006\t\"-^5mI2{7-\u00197D_6l\u0017M\u001c3\u0015\r9R7\u000e\\7p\u0011\u0015is\r1\u0001/\u0011\u0015\u0019t\r1\u00015\u0011\u00159u\r1\u0001I\u0011\u001dqw\r%AA\u00025\u000b\u0011b\u00197bgN\u0004\u0016\r\u001e5\t\u000bi;\u0007\u0019A.\t\u000bEtA\u0011\u0001:\u0002\u001dI,G-\u001b:fGR\u001cFO]3b[R\u00191O\u001e@\u0011\u0005I!\u0018BA;\u0014\u0005\u0011)f.\u001b;\t\u000b]\u0004\b\u0019\u0001=\u0002\u0005%t\u0007CA=}\u001b\u0005Q(BA>)\u0003\tIw.\u0003\u0002~u\nY\u0011J\u001c9viN#(/Z1n\u0011\u0019y\b\u000f1\u0001\u0002\u0002\u0005!a-\u001b7f!\rI\u00181A\u0005\u0004\u0003\u000bQ(\u0001\u0002$jY\u0016D\u0011\"!\u0003\u000f#\u0003%\t!a\u0003\u0002;\t,\u0018\u000e\u001c3Qe>\u001cWm]:Ck&dG-\u001a:%I\u00164\u0017-\u001e7uIY*\"!!\u0004+\u00075\u000bya\u000b\u0002\u0002\u0012A!\u00111CA\u000f\u001b\t\t)B\u0003\u0003\u0002\u0018\u0005e\u0011!C;oG\",7m[3e\u0015\r\tYbE\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BA\u0010\u0003+\u0011\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011%\t\u0019CDI\u0001\n\u0003\t)#A\u000fck&dG\r\u0015:pG\u0016\u001c8OQ;jY\u0012,'\u000f\n3fM\u0006,H\u000e\u001e\u00138+\t\t9CK\u0002\\\u0003\u001fA\u0011\"a\u000b\u000f#\u0003%I!a\u0003\u00027\t,\u0018\u000e\u001c3M_\u000e\fGnQ8n[\u0006tG\r\n3fM\u0006,H\u000e\u001e\u00135\u0001"
)
public final class CommandUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return CommandUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return CommandUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      CommandUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return CommandUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      CommandUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      CommandUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      CommandUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      CommandUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      CommandUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      CommandUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      CommandUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      CommandUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      CommandUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      CommandUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return CommandUtils$.MODULE$.log();
   }

   public static String logName() {
      return CommandUtils$.MODULE$.logName();
   }

   public static Map<String, String> buildProcessBuilder$default$7() {
      return CommandUtils$.MODULE$.buildProcessBuilder$default$7();
   }

   public static Seq<String> buildProcessBuilder$default$6() {
      return CommandUtils$.MODULE$.buildProcessBuilder$default$6();
   }

   public static void redirectStream(InputStream var0, File var1) {
      CommandUtils$.MODULE$.redirectStream(var0, var1);
   }

   public static ProcessBuilder buildProcessBuilder(Command var0, SecurityManager var1, int var2, String var3, Function1<String, String> var4, Seq<String> var5, Map<String, String> var6) {
      return CommandUtils$.MODULE$.buildProcessBuilder(var0, var1, var2, var3, var4, var5, var6);
   }
}
