package org.apache.spark.util;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import org.apache.spark.annotation.DeveloperApi;
import org.slf4j.Logger;
import scala.Function0;
import scala.collection.immutable.List;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t-v!B\u0001\u0003\u0011\u0003Y\u0011!D*ju\u0016,5\u000f^5nCR|'O\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001\u0001\t\u0003\u00195i\u0011A\u0001\u0004\u0006\u001d\tA\ta\u0004\u0002\u000e'&TX-R:uS6\fGo\u001c:\u0014\u00075\u0001b\u0003\u0005\u0002\u0012)5\t!CC\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)\"C\u0001\u0004B]f\u0014VM\u001a\t\u0003/ii\u0011\u0001\u0007\u0006\u00033\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00037a\u0011q\u0001T8hO&tw\rC\u0003\u001e\u001b\u0011\u0005a$\u0001\u0004=S:LGO\u0010\u000b\u0002\u0017!)\u0001%\u0004C\u0001C\u0005AQm\u001d;j[\u0006$X\r\u0006\u0002#KA\u0011\u0011cI\u0005\u0003II\u0011A\u0001T8oO\")ae\ba\u0001!\u0005\u0019qN\u00196\t\u000f!j!\u0019!C\u0005S\u0005I!)\u0017+F?NK%,R\u000b\u0002UA\u0011\u0011cK\u0005\u0003YI\u00111!\u00138u\u0011\u0019qS\u0002)A\u0005U\u0005Q!)\u0017+F?NK%,\u0012\u0011\t\u000fAj!\u0019!C\u0005S\u0005a!iT(M\u000b\u0006sulU%[\u000b\"1!'\u0004Q\u0001\n)\nQBQ(P\u0019\u0016\u000bejX*J5\u0016\u0003\u0003b\u0002\u001b\u000e\u0005\u0004%I!K\u0001\n\u0007\"\u000b%kX*J5\u0016CaAN\u0007!\u0002\u0013Q\u0013AC\"I\u0003J{6+\u0013.FA!9\u0001(\u0004b\u0001\n\u0013I\u0013AC*I\u001fJ#vlU%[\u000b\"1!(\u0004Q\u0001\n)\n1b\u0015%P%R{6+\u0013.FA!9A(\u0004b\u0001\n\u0013I\u0013\u0001C%O)~\u001b\u0016JW#\t\ryj\u0001\u0015!\u0003+\u0003%Ie\nV0T\u0013j+\u0005\u0005C\u0004A\u001b\t\u0007I\u0011B\u0015\u0002\u00131{ejR0T\u0013j+\u0005B\u0002\"\u000eA\u0003%!&\u0001\u0006M\u001f:;ulU%[\u000b\u0002Bq\u0001R\u0007C\u0002\u0013%\u0011&\u0001\u0006G\u0019>\u000bEkX*J5\u0016CaAR\u0007!\u0002\u0013Q\u0013a\u0003$M\u001f\u0006#vlU%[\u000b\u0002Bq\u0001S\u0007C\u0002\u0013%\u0011&A\u0006E\u001fV\u0013E*R0T\u0013j+\u0005B\u0002&\u000eA\u0003%!&\u0001\u0007E\u001fV\u0013E*R0T\u0013j+\u0005\u0005C\u0004M\u001b\t\u0007I\u0011B'\u0002\u0015\u0019LW\r\u001c3TSj,7/F\u0001O!\ryEKK\u0007\u0002!*\u0011\u0011KU\u0001\nS6lW\u000f^1cY\u0016T!a\u0015\n\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002V!\n!A*[:u\u0011\u00199V\u0002)A\u0005\u001d\u0006Ya-[3mINK'0Z:!\u0011\u001dIVB1A\u0005\n%\n!\"\u0011'J\u000f:{6+\u0013.F\u0011\u0019YV\u0002)A\u0005U\u0005Y\u0011\tT%H\u001d~\u001b\u0016JW#!\u0011\u001diVB1A\u0005\ny\u000b!b\u00197bgNLeNZ8t+\u0005y\u0006\u0003\u00021gQrl\u0011!\u0019\u0006\u0003E\u000e\f!bY8oGV\u0014(/\u001a8u\u0015\t\u0019AMC\u0001f\u0003\u0011Q\u0017M^1\n\u0005\u001d\f'!D\"p]\u000e,(O]3oi6\u000b\u0007\u000f\r\u0002jcB\u0019!.\\8\u000e\u0003-T!\u0001\u001c3\u0002\t1\fgnZ\u0005\u0003].\u0014Qa\u00117bgN\u0004\"\u0001]9\r\u0001\u0011I!o]A\u0001\u0002\u0003\u0015\t!\u001e\u0002\u0004?\u0012\n\u0004B\u0002;\u000eA\u0003%q,A\u0006dY\u0006\u001c8/\u00138g_N\u0004\u0013C\u0001<z!\t\tr/\u0003\u0002y%\t9aj\u001c;iS:<\u0007CA\t{\u0013\tY(CA\u0002B]f\u0004\"! @\u000e\u000351Qa`\u0007\u0005\u0003\u0003\u0011\u0011b\u00117bgNLeNZ8\u0014\u0005y\u0004\u0002BCA\u0003}\n\u0015\r\u0011\"\u0001\u0002\b\u0005I1\u000f[3mYNK'0Z\u000b\u0002E!I\u00111\u0002@\u0003\u0002\u0003\u0006IAI\u0001\u000bg\",G\u000e\\*ju\u0016\u0004\u0003BCA\b}\n\u0015\r\u0011\"\u0001\u0002\u0012\u0005i\u0001o\\5oi\u0016\u0014h)[3mIN,\"!a\u0005\u0011\r\u0005U\u0011QEA\u0015\u001d\u0011\t9\"!\t\u000f\t\u0005e\u0011qD\u0007\u0003\u00037Q1!!\b\u000b\u0003\u0019a$o\\8u}%\t1#C\u0002\u0002$I\tq\u0001]1dW\u0006<W-C\u0002V\u0003OQ1!a\t\u0013!\u0011\tY#!\r\u000e\u0005\u00055\"bAA\u0018W\u00069!/\u001a4mK\u000e$\u0018\u0002BA\u001a\u0003[\u0011QAR5fY\u0012D!\"a\u000e\u007f\u0005\u0003\u0005\u000b\u0011BA\n\u00039\u0001x.\u001b8uKJ4\u0015.\u001a7eg\u0002Ba!\b@\u0005\u0002\u0005mB#\u0002?\u0002>\u0005}\u0002bBA\u0003\u0003s\u0001\rA\t\u0005\t\u0003\u001f\tI\u00041\u0001\u0002\u0014!I\u00111I\u0007A\u0002\u0013%\u0011QI\u0001\bSN4DGY5u+\t\t9\u0005E\u0002\u0012\u0003\u0013J1!a\u0013\u0013\u0005\u001d\u0011un\u001c7fC:D\u0011\"a\u0014\u000e\u0001\u0004%I!!\u0015\u0002\u0017%\u001ch\u0007\u000e2ji~#S-\u001d\u000b\u0005\u0003'\nI\u0006E\u0002\u0012\u0003+J1!a\u0016\u0013\u0005\u0011)f.\u001b;\t\u0015\u0005m\u0013QJA\u0001\u0002\u0004\t9%A\u0002yIEB\u0001\"a\u0018\u000eA\u0003&\u0011qI\u0001\tSN4DGY5uA!I\u00111M\u0007A\u0002\u0013%\u0011QI\u0001\u0011SN\u001cu.\u001c9sKN\u001cX\rZ(paND\u0011\"a\u001a\u000e\u0001\u0004%I!!\u001b\u0002)%\u001c8i\\7qe\u0016\u001c8/\u001a3P_B\u001cx\fJ3r)\u0011\t\u0019&a\u001b\t\u0015\u0005m\u0013QMA\u0001\u0002\u0004\t9\u0005\u0003\u0005\u0002p5\u0001\u000b\u0015BA$\u0003EI7oQ8naJ,7o]3e\u001f>\u00048\u000f\t\u0005\t\u0003gj\u0001\u0019!C\u0005S\u0005Y\u0001o\\5oi\u0016\u00148+\u001b>f\u0011%\t9(\u0004a\u0001\n\u0013\tI(A\bq_&tG/\u001a:TSj,w\fJ3r)\u0011\t\u0019&a\u001f\t\u0013\u0005m\u0013QOA\u0001\u0002\u0004Q\u0003bBA@\u001b\u0001\u0006KAK\u0001\ra>Lg\u000e^3s'&TX\r\t\u0005\t\u0003\u0007k\u0001\u0019!C\u0005S\u0005QqN\u00196fGR\u001c\u0016N_3\t\u0013\u0005\u001dU\u00021A\u0005\n\u0005%\u0015AD8cU\u0016\u001cGoU5{K~#S-\u001d\u000b\u0005\u0003'\nY\tC\u0005\u0002\\\u0005\u0015\u0015\u0011!a\u0001U!9\u0011qR\u0007!B\u0013Q\u0013aC8cU\u0016\u001cGoU5{K\u0002Bq!a%\u000e\t\u0013\t)*\u0001\u0006j]&$\u0018.\u00197ju\u0016$\"!a\u0015\t\u000f\u0005eU\u0002\"\u0003\u0002F\u0005\u0019r-\u001a;Jg\u000e{W\u000e\u001d:fgN,GmT8qg\u001a1\u0011QT\u0007\u0005\u0003?\u00131bU3be\u000eD7\u000b^1uKN\u0019\u00111\u0014\t\t\u0017\u0005\r\u00161\u0014BC\u0002\u0013\u0005\u0011QU\u0001\bm&\u001c\u0018\u000e^3e+\t\t9\u000b\u0005\u0004\u0002*\u0006-\u0006\u0003E\u0007\u0002G&\u0019\u0011QV2\u0003\u001f%#WM\u001c;jifD\u0015m\u001d5NCBD1\"!-\u0002\u001c\n\u0005\t\u0015!\u0003\u0002(\u0006Aa/[:ji\u0016$\u0007\u0005C\u0004\u001e\u00037#\t!!.\u0015\t\u0005]\u0016\u0011\u0018\t\u0004{\u0006m\u0005\u0002CAR\u0003g\u0003\r!a*\t\u0015\u0005u\u00161\u0014b\u0001\n\u0003\ty,A\u0003ti\u0006\u001c7.\u0006\u0002\u0002BB)\u00111YAe!5\u0011\u0011Q\u0019\u0006\u0004\u0003\u000f\u0014\u0016aB7vi\u0006\u0014G.Z\u0005\u0005\u0003\u0017\f)MA\u0006BeJ\f\u0017PQ;gM\u0016\u0014\b\"CAh\u00037\u0003\u000b\u0011BAa\u0003\u0019\u0019H/Y2lA!Q\u00111[AN\u0001\u0004%\t!a\u0002\u0002\tML'0\u001a\u0005\u000b\u0003/\fY\n1A\u0005\u0002\u0005e\u0017\u0001C:ju\u0016|F%Z9\u0015\t\u0005M\u00131\u001c\u0005\n\u00037\n).!AA\u0002\tB\u0001\"a8\u0002\u001c\u0002\u0006KAI\u0001\u0006g&TX\r\t\u0005\t\u0003G\fY\n\"\u0001\u0002f\u00069QM\\9vKV,G\u0003BA*\u0003ODaAJAq\u0001\u0004\u0001\u0002\u0002CAv\u00037#\t!!<\u0002\u0015%\u001ch)\u001b8jg\",G\r\u0006\u0002\u0002H!A\u0011\u0011_AN\t\u0003\t\u00190A\u0004eKF,X-^3\u0015\u0003AAa\u0001I\u0007\u0005\n\u0005]H#\u0002\u0012\u0002z\u0006m\bB\u0002\u0014\u0002v\u0002\u0007\u0001\u0003\u0003\u0005\u0002$\u0006U\b\u0019AAT\u0011\u001d\ty0\u0004C\u0005\u0005\u0003\t\u0011C^5tSR\u001c\u0016N\\4mK>\u0013'.Z2u)\u0019\t\u0019Fa\u0001\u0003\u0006!1a%!@A\u0002AA\u0001Ba\u0002\u0002~\u0002\u0007\u0011qW\u0001\u0006gR\fG/\u001a\u0005\t\u0005\u0017i!\u0019!C\u0005S\u00059\u0012I\u0015*B3~\u001b\u0016JW#`\r>\u0013vlU!N!2Kej\u0012\u0005\b\u0005\u001fi\u0001\u0015!\u0003+\u0003a\t%KU!Z?NK%,R0G\u001fJ{6+Q'Q\u0019&su\t\t\u0005\t\u0005'i!\u0019!C\u0005S\u0005\t\u0012I\u0015*B3~\u001b\u0016)\u0014)M\u000b~\u001b\u0016JW#\t\u000f\t]Q\u0002)A\u0005U\u0005\u0011\u0012I\u0015*B3~\u001b\u0016)\u0014)M\u000b~\u001b\u0016JW#!\u0011\u001d\u0011Y\"\u0004C\u0005\u0005;\t!B^5tSR\f%O]1z)!\t\u0019Fa\b\u0003$\tm\u0002b\u0002B\u0011\u00053\u0001\r\u0001E\u0001\u0006CJ\u0014\u0018-\u001f\u0005\t\u0005K\u0011I\u00021\u0001\u0003(\u0005Q\u0011M\u001d:bs\u000ec\u0017m]:1\t\t%\"q\u0007\t\u0007\u0005W\u0011\tD!\u000e\u000f\u0007E\u0011i#C\u0002\u00030I\ta\u0001\u0015:fI\u00164\u0017b\u00018\u00034)\u0019!q\u0006\n\u0011\u0007A\u00149\u0004B\u0006\u0003:\t\r\u0012\u0011!A\u0001\u0006\u0003)(aA0%g!A!q\u0001B\r\u0001\u0004\t9\fC\u0004\u0003@5!IA!\u0011\u0002\u0017M\fW\u000e\u001d7f\u0003J\u0014\u0018-\u001f\u000b\fE\t\r#Q\tB$\u0005#\u0012y\u0006C\u0004\u0003\"\tu\u0002\u0019\u0001\t\t\u0011\t\u001d!Q\ba\u0001\u0003oC\u0001B!\u0013\u0003>\u0001\u0007!1J\u0001\u0005e\u0006tG\r\u0005\u0003\u0002*\n5\u0013b\u0001B(G\n1!+\u00198e_6D\u0001Ba\u0015\u0003>\u0001\u0007!QK\u0001\u0006IJ\fwO\u001c\t\u0006\u0005/\u0012YFK\u0007\u0003\u00053R!a\u0015\u0002\n\t\tu#\u0011\f\u0002\f\u001fB,g\u000eS1tQN+G\u000fC\u0004\u0003b\tu\u0002\u0019\u0001\u0016\u0002\r1,gn\u001a;i\u0011\u001d\u0011)'\u0004C\u0005\u0005O\nQ\u0002\u001d:j[&$\u0018N^3TSj,Gc\u0001\u0016\u0003j!A!1\u000eB2\u0001\u0004\u0011i'A\u0002dYN\u0004DAa\u001c\u0003tA1!1\u0006B\u0019\u0005c\u00022\u0001\u001dB:\t-\u0011)H!\u001b\u0002\u0002\u0003\u0005)\u0011A;\u0003\u0007}#C\u0007C\u0004\u0003z5!IAa\u001f\u0002\u0019\u001d,Go\u00117bgNLeNZ8\u0015\u0007q\u0014i\b\u0003\u0005\u0003l\t]\u0004\u0019\u0001B@a\u0011\u0011\tI!\"\u0011\r\t-\"\u0011\u0007BB!\r\u0001(Q\u0011\u0003\f\u0005\u000f\u0013i(!A\u0001\u0002\u000b\u0005QOA\u0002`IUBqAa#\u000e\t\u0013\u0011i)A\u0005bY&<gnU5{KR\u0019!Ea$\t\u000f\u0005M'\u0011\u0012a\u0001E!9!1S\u0007\u0005\n\tU\u0015aC1mS\u001et7+\u001b>f+B$RA\tBL\u00053Cq!a5\u0003\u0012\u0002\u0007!\u0005C\u0004\u0003\f\nE\u0005\u0019\u0001\u0016)\u00075\u0011i\n\u0005\u0003\u0003 \n\u0015VB\u0001BQ\u0015\r\u0011\u0019\u000bB\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002BT\u0005C\u0013A\u0002R3wK2|\u0007/\u001a:Ba&D3\u0001\u0001BO\u0001"
)
public final class SizeEstimator {
   public static boolean initializeLogIfNecessary$default$2() {
      return SizeEstimator$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SizeEstimator$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SizeEstimator$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SizeEstimator$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SizeEstimator$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SizeEstimator$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SizeEstimator$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SizeEstimator$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SizeEstimator$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SizeEstimator$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SizeEstimator$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SizeEstimator$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SizeEstimator$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SizeEstimator$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SizeEstimator$.MODULE$.log();
   }

   public static String logName() {
      return SizeEstimator$.MODULE$.logName();
   }

   public static long estimate(Object var0) {
      return SizeEstimator$.MODULE$.estimate(var0);
   }

   public static class SearchState {
      private final IdentityHashMap<Object, Object> visited;
      private final ArrayBuffer<Object> stack;
      private long size;

      public IdentityHashMap<Object, Object> visited() {
         return this.visited;
      }

      public ArrayBuffer<Object> stack() {
         return this.stack;
      }

      public long size() {
         return this.size;
      }

      public void size_$eq(long x$1) {
         this.size = x$1;
      }

      public void enqueue(Object obj) {
         if (obj != null && !this.visited().containsKey(obj)) {
            this.visited().put(obj, (Object)null);
            this.stack().$plus$eq(obj);
         }

      }

      public boolean isFinished() {
         return this.stack().isEmpty();
      }

      public Object dequeue() {
         Object elem = this.stack().last();
         this.stack().trimEnd(1);
         return elem;
      }

      public SearchState(IdentityHashMap<Object, Object> visited) {
         this.visited = visited;
         this.stack = new ArrayBuffer<Object>();
         this.size = 0L;
      }
   }

   public static class ClassInfo {
      private final long shellSize;
      private final List<Field> pointerFields;

      public long shellSize() {
         return this.shellSize;
      }

      public List<Field> pointerFields() {
         return this.pointerFields;
      }

      public ClassInfo(long shellSize, List<Field> pointerFields) {
         this.shellSize = shellSize;
         this.pointerFields = pointerFields;
      }
   }
}
