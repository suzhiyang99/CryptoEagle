package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005-e\u0001B\u0001\u0003\u0001.\u0011\u0001f\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'O\u00117bG.d\u0017n\u001d;fI\u001a{'o\u0015;bO\u0016T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M)\u0001\u0001\u0004\n\u00173A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\u000b\u000e\u0003\tI!!\u0006\u0002\u0003%M\u0003\u0018M]6MSN$XM\\3s\u000bZ,g\u000e\u001e\t\u0003\u001b]I!\u0001\u0007\b\u0003\u000fA\u0013x\u000eZ;diB\u0011QBG\u0005\u000379\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\b\u0001\u0003\u0016\u0004%\tAH\u0001\u0005i&lW-F\u0001 !\ti\u0001%\u0003\u0002\"\u001d\t!Aj\u001c8h\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012!\u0002;j[\u0016\u0004\u0003\u0002C\u0013\u0001\u0005+\u0007I\u0011\u0001\u0014\u0002\u0015\u0015DXmY;u_JLE-F\u0001(!\tA3F\u0004\u0002\u000eS%\u0011!FD\u0001\u0007!J,G-\u001a4\n\u00051j#AB*ue&twM\u0003\u0002+\u001d!Aq\u0006\u0001B\tB\u0003%q%A\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\u0003\u0002C\u0019\u0001\u0005+\u0007I\u0011\u0001\u001a\u0002\u0019Q\f7o\u001b$bS2,(/Z:\u0016\u0003M\u0002\"!\u0004\u001b\n\u0005Ur!aA%oi\"Aq\u0007\u0001B\tB\u0003%1'A\u0007uCN\\g)Y5mkJ,7\u000f\t\u0005\ts\u0001\u0011)\u001a!C\u0001e\u000591\u000f^1hK&#\u0007\u0002C\u001e\u0001\u0005#\u0005\u000b\u0011B\u001a\u0002\u0011M$\u0018mZ3JI\u0002B\u0001\"\u0010\u0001\u0003\u0016\u0004%\tAM\u0001\u000fgR\fw-Z!ui\u0016l\u0007\u000f^%e\u0011!y\u0004A!E!\u0002\u0013\u0019\u0014aD:uC\u001e,\u0017\t\u001e;f[B$\u0018\n\u001a\u0011\t\u000b\u0005\u0003A\u0011\u0001\"\u0002\rqJg.\u001b;?)\u0019\u0019E)\u0012$H\u0011B\u00111\u0003\u0001\u0005\u0006;\u0001\u0003\ra\b\u0005\u0006K\u0001\u0003\ra\n\u0005\u0006c\u0001\u0003\ra\r\u0005\u0006s\u0001\u0003\ra\r\u0005\u0006{\u0001\u0003\ra\r\u0005\b\u0015\u0002\t\t\u0011\"\u0001L\u0003\u0011\u0019w\u000e]=\u0015\r\rcUJT(Q\u0011\u001di\u0012\n%AA\u0002}Aq!J%\u0011\u0002\u0003\u0007q\u0005C\u00042\u0013B\u0005\t\u0019A\u001a\t\u000feJ\u0005\u0013!a\u0001g!9Q(\u0013I\u0001\u0002\u0004\u0019\u0004b\u0002*\u0001#\u0003%\taU\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005!&FA\u0010VW\u00051\u0006CA,]\u001b\u0005A&BA-[\u0003%)hn\u00195fG.,GM\u0003\u0002\\\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005uC&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\fAI\u0001\n\u0003\u0001\u0017AD2paf$C-\u001a4bk2$HEM\u000b\u0002C*\u0012q%\u0016\u0005\bG\u0002\t\n\u0011\"\u0001e\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012!\u001a\u0016\u0003gUCqa\u001a\u0001\u0012\u0002\u0013\u0005A-\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\t\u000f%\u0004\u0011\u0013!C\u0001I\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012*\u0004bB6\u0001\u0003\u0003%\t\u0005\\\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u00035\u0004\"A\\:\u000e\u0003=T!\u0001]9\u0002\t1\fgn\u001a\u0006\u0002e\u0006!!.\u0019<b\u0013\tas\u000eC\u0004v\u0001\u0005\u0005I\u0011\u0001\u001a\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000f]\u0004\u0011\u0011!C\u0001q\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA=}!\ti!0\u0003\u0002|\u001d\t\u0019\u0011I\\=\t\u000fu4\u0018\u0011!a\u0001g\u0005\u0019\u0001\u0010J\u0019\t\u0011}\u0004\u0011\u0011!C!\u0003\u0003\tq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003\u0007\u0001R!!\u0002\u0002\fel!!a\u0002\u000b\u0007\u0005%a\"\u0001\u0006d_2dWm\u0019;j_:LA!!\u0004\u0002\b\tA\u0011\n^3sCR|'\u000fC\u0005\u0002\u0012\u0001\t\t\u0011\"\u0001\u0002\u0014\u0005A1-\u00198FcV\fG\u000e\u0006\u0003\u0002\u0016\u0005m\u0001cA\u0007\u0002\u0018%\u0019\u0011\u0011\u0004\b\u0003\u000f\t{w\u000e\\3b]\"AQ0a\u0004\u0002\u0002\u0003\u0007\u0011\u0010C\u0005\u0002 \u0001\t\t\u0011\"\u0011\u0002\"\u0005A\u0001.Y:i\u0007>$W\rF\u00014\u0011%\t)\u0003AA\u0001\n\u0003\n9#\u0001\u0005u_N#(/\u001b8h)\u0005i\u0007\"CA\u0016\u0001\u0005\u0005I\u0011IA\u0017\u0003\u0019)\u0017/^1mgR!\u0011QCA\u0018\u0011!i\u0018\u0011FA\u0001\u0002\u0004I\bf\u0001\u0001\u00024A!\u0011QGA\u001d\u001b\t\t9D\u0003\u0002\\\t%!\u00111HA\u001c\u00051!UM^3m_B,'/\u00119j\u000f%\tyDAA\u0001\u0012\u0003\t\t%\u0001\u0015Ta\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:CY\u0006\u001c7\u000e\\5ti\u0016$gi\u001c:Ti\u0006<W\rE\u0002\u0014\u0003\u00072\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011QI\n\u0006\u0003\u0007\n9%\u0007\t\u000b\u0003\u0013\nyeH\u00144gM\u001aUBAA&\u0015\r\tiED\u0001\beVtG/[7f\u0013\u0011\t\t&a\u0013\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tW\u0007C\u0004B\u0003\u0007\"\t!!\u0016\u0015\u0005\u0005\u0005\u0003BCA\u0013\u0003\u0007\n\t\u0011\"\u0012\u0002(!Q\u00111LA\"\u0003\u0003%\t)!\u0018\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0017\r\u000by&!\u0019\u0002d\u0005\u0015\u0014q\r\u0005\u0007;\u0005e\u0003\u0019A\u0010\t\r\u0015\nI\u00061\u0001(\u0011\u0019\t\u0014\u0011\fa\u0001g!1\u0011(!\u0017A\u0002MBa!PA-\u0001\u0004\u0019\u0004BCA6\u0003\u0007\n\t\u0011\"!\u0002n\u00059QO\\1qa2LH\u0003BA8\u0003w\u0002R!DA9\u0003kJ1!a\u001d\u000f\u0005\u0019y\u0005\u000f^5p]BAQ\"a\u001e OM\u001a4'C\u0002\u0002z9\u0011a\u0001V;qY\u0016,\u0004\"CA?\u0003S\n\t\u00111\u0001D\u0003\rAH\u0005\r\u0005\u000b\u0003\u0003\u000b\u0019%!A\u0005\n\u0005\r\u0015a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\"\u0011\u00079\f9)C\u0002\u0002\n>\u0014aa\u00142kK\u000e$\b"
)
public class SparkListenerExecutorBlacklistedForStage implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String executorId;
   private final int taskFailures;
   private final int stageId;
   private final int stageAttemptId;

   public static Option<Tuple5<Object, String, Object, Object, Object>> unapply(SparkListenerExecutorBlacklistedForStage var0) {
      return SparkListenerExecutorBlacklistedForStage$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorBlacklistedForStage apply(long var0, String var2, int var3, int var4, int var5) {
      return SparkListenerExecutorBlacklistedForStage$.MODULE$.apply(var0, var2, var3, var4, var5);
   }

   public static Function1<Tuple5<Object, String, Object, Object, Object>, SparkListenerExecutorBlacklistedForStage> tupled() {
      return SparkListenerExecutorBlacklistedForStage$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Object, Function1<Object, Function1<Object, SparkListenerExecutorBlacklistedForStage>>>>> curried() {
      return SparkListenerExecutorBlacklistedForStage$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public String executorId() {
      return this.executorId;
   }

   public int taskFailures() {
      return this.taskFailures;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public SparkListenerExecutorBlacklistedForStage copy(long time, String executorId, int taskFailures, int stageId, int stageAttemptId) {
      return new SparkListenerExecutorBlacklistedForStage(time, executorId, taskFailures, stageId, stageAttemptId);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.executorId();
   }

   public int copy$default$3() {
      return this.taskFailures();
   }

   public int copy$default$4() {
      return this.stageId();
   }

   public int copy$default$5() {
      return this.stageAttemptId();
   }

   public String productPrefix() {
      return "SparkListenerExecutorBlacklistedForStage";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 1:
            var10000 = this.executorId();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.taskFailures());
            break;
         case 3:
            var10000 = BoxesRunTime.boxToInteger(this.stageId());
            break;
         case 4:
            var10000 = BoxesRunTime.boxToInteger(this.stageAttemptId());
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerExecutorBlacklistedForStage;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      var1 = Statics.mix(var1, this.taskFailures());
      var1 = Statics.mix(var1, this.stageId());
      var1 = Statics.mix(var1, this.stageAttemptId());
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label60: {
            boolean var3;
            if (x$1 instanceof SparkListenerExecutorBlacklistedForStage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label43: {
                  SparkListenerExecutorBlacklistedForStage var4 = (SparkListenerExecutorBlacklistedForStage)x$1;
                  if (this.time() == var4.time()) {
                     label41: {
                        String var10000 = this.executorId();
                        String var5 = var4.executorId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label41;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label41;
                        }

                        if (this.taskFailures() == var4.taskFailures() && this.stageId() == var4.stageId() && this.stageAttemptId() == var4.stageAttemptId() && var4.canEqual(this)) {
                           var7 = true;
                           break label43;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label60;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerExecutorBlacklistedForStage(long time, String executorId, int taskFailures, int stageId, int stageAttemptId) {
      this.time = time;
      this.executorId = executorId;
      this.taskFailures = taskFailures;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
