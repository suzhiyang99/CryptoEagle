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
   bytes = "\u0006\u0001\u0005-e\u0001B\u0001\u0003\u0001.\u0011Ae\u00159be.d\u0015n\u001d;f]\u0016\u0014hj\u001c3f\u00052\f7m\u001b7jgR,GMR8s'R\fw-\u001a\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0019I1\u0012\u0004\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'Qi\u0011AA\u0005\u0003+\t\u0011!c\u00159be.d\u0015n\u001d;f]\u0016\u0014XI^3oiB\u0011QbF\u0005\u000319\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e5%\u00111D\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t;\u0001\u0011)\u001a!C\u0001=\u0005!A/[7f+\u0005y\u0002CA\u0007!\u0013\t\tcB\u0001\u0003M_:<\u0007\u0002C\u0012\u0001\u0005#\u0005\u000b\u0011B\u0010\u0002\u000bQLW.\u001a\u0011\t\u0011\u0015\u0002!Q3A\u0005\u0002\u0019\na\u0001[8ti&#W#A\u0014\u0011\u0005!ZcBA\u0007*\u0013\tQc\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003Y5\u0012aa\u0015;sS:<'B\u0001\u0016\u000f\u0011!y\u0003A!E!\u0002\u00139\u0013a\u00025pgRLE\r\t\u0005\tc\u0001\u0011)\u001a!C\u0001e\u0005\u0001R\r_3dkR|'OR1jYV\u0014Xm]\u000b\u0002gA\u0011Q\u0002N\u0005\u0003k9\u00111!\u00138u\u0011!9\u0004A!E!\u0002\u0013\u0019\u0014!E3yK\u000e,Ho\u001c:GC&dWO]3tA!A\u0011\b\u0001BK\u0002\u0013\u0005!'A\u0004ti\u0006<W-\u00133\t\u0011m\u0002!\u0011#Q\u0001\nM\n\u0001b\u001d;bO\u0016LE\r\t\u0005\t{\u0001\u0011)\u001a!C\u0001e\u0005q1\u000f^1hK\u0006#H/Z7qi&#\u0007\u0002C \u0001\u0005#\u0005\u000b\u0011B\u001a\u0002\u001fM$\u0018mZ3BiR,W\u000e\u001d;JI\u0002BQ!\u0011\u0001\u0005\u0002\t\u000ba\u0001P5oSRtDCB\"E\u000b\u001a;\u0005\n\u0005\u0002\u0014\u0001!)Q\u0004\u0011a\u0001?!)Q\u0005\u0011a\u0001O!)\u0011\u0007\u0011a\u0001g!)\u0011\b\u0011a\u0001g!)Q\b\u0011a\u0001g!9!\nAA\u0001\n\u0003Y\u0015\u0001B2paf$ba\u0011'N\u001d>\u0003\u0006bB\u000fJ!\u0003\u0005\ra\b\u0005\bK%\u0003\n\u00111\u0001(\u0011\u001d\t\u0014\n%AA\u0002MBq!O%\u0011\u0002\u0003\u00071\u0007C\u0004>\u0013B\u0005\t\u0019A\u001a\t\u000fI\u0003\u0011\u0013!C\u0001'\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001++\u0005})6&\u0001,\u0011\u0005]cV\"\u0001-\u000b\u0005eS\u0016!C;oG\",7m[3e\u0015\tYf\"\u0001\u0006b]:|G/\u0019;j_:L!!\u0018-\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004`\u0001E\u0005I\u0011\u00011\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\t\u0011M\u000b\u0002(+\"91\rAI\u0001\n\u0003!\u0017AD2paf$C-\u001a4bk2$HeM\u000b\u0002K*\u00121'\u0016\u0005\bO\u0002\t\n\u0011\"\u0001e\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQBq!\u001b\u0001\u0012\u0002\u0013\u0005A-\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001b\t\u000f-\u0004\u0011\u0011!C!Y\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u001c\t\u0003]Nl\u0011a\u001c\u0006\u0003aF\fA\u0001\\1oO*\t!/\u0001\u0003kCZ\f\u0017B\u0001\u0017p\u0011\u001d)\b!!A\u0005\u0002I\nA\u0002\u001d:pIV\u001cG/\u0011:jifDqa\u001e\u0001\u0002\u0002\u0013\u0005\u00010\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005ed\bCA\u0007{\u0013\tYhBA\u0002B]fDq! <\u0002\u0002\u0003\u00071'A\u0002yIEB\u0001b \u0001\u0002\u0002\u0013\u0005\u0013\u0011A\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u00111\u0001\t\u0006\u0003\u000b\tY!_\u0007\u0003\u0003\u000fQ1!!\u0003\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u001b\t9A\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\t\t\u0002AA\u0001\n\u0003\t\u0019\"\u0001\u0005dC:,\u0015/^1m)\u0011\t)\"a\u0007\u0011\u00075\t9\"C\u0002\u0002\u001a9\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005~\u0003\u001f\t\t\u00111\u0001z\u0011%\ty\u0002AA\u0001\n\u0003\n\t#\u0001\u0005iCND7i\u001c3f)\u0005\u0019\u0004\"CA\u0013\u0001\u0005\u0005I\u0011IA\u0014\u0003!!xn\u0015;sS:<G#A7\t\u0013\u0005-\u0002!!A\u0005B\u00055\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002\u0016\u0005=\u0002\u0002C?\u0002*\u0005\u0005\t\u0019A=)\u0007\u0001\t\u0019\u0004\u0005\u0003\u00026\u0005eRBAA\u001c\u0015\tYF!\u0003\u0003\u0002<\u0005]\"\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!CA \u0005\u0005\u0005\t\u0012AA!\u0003\u0011\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe:{G-\u001a\"mC\u000e\\G.[:uK\u00124uN]*uC\u001e,\u0007cA\n\u0002D\u0019A\u0011AAA\u0001\u0012\u0003\t)eE\u0003\u0002D\u0005\u001d\u0013\u0004\u0005\u0006\u0002J\u0005=sdJ\u001a4g\rk!!a\u0013\u000b\u0007\u00055c\"A\u0004sk:$\u0018.\\3\n\t\u0005E\u00131\n\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:,\u0004bB!\u0002D\u0011\u0005\u0011Q\u000b\u000b\u0003\u0003\u0003B!\"!\n\u0002D\u0005\u0005IQIA\u0014\u0011)\tY&a\u0011\u0002\u0002\u0013\u0005\u0015QL\u0001\u0006CB\u0004H.\u001f\u000b\f\u0007\u0006}\u0013\u0011MA2\u0003K\n9\u0007\u0003\u0004\u001e\u00033\u0002\ra\b\u0005\u0007K\u0005e\u0003\u0019A\u0014\t\rE\nI\u00061\u00014\u0011\u0019I\u0014\u0011\fa\u0001g!1Q(!\u0017A\u0002MB!\"a\u001b\u0002D\u0005\u0005I\u0011QA7\u0003\u001d)h.\u00199qYf$B!a\u001c\u0002|A)Q\"!\u001d\u0002v%\u0019\u00111\u000f\b\u0003\r=\u0003H/[8o!!i\u0011qO\u0010(gM\u001a\u0014bAA=\u001d\t1A+\u001e9mKVB\u0011\"! \u0002j\u0005\u0005\t\u0019A\"\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002\u0002\u0006\r\u0013\u0011!C\u0005\u0003\u0007\u000b1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u0011\t\u0004]\u0006\u001d\u0015bAAE_\n1qJ\u00196fGR\u0004"
)
public class SparkListenerNodeBlacklistedForStage implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String hostId;
   private final int executorFailures;
   private final int stageId;
   private final int stageAttemptId;

   public static Option<Tuple5<Object, String, Object, Object, Object>> unapply(SparkListenerNodeBlacklistedForStage var0) {
      return SparkListenerNodeBlacklistedForStage$.MODULE$.unapply(var0);
   }

   public static SparkListenerNodeBlacklistedForStage apply(long var0, String var2, int var3, int var4, int var5) {
      return SparkListenerNodeBlacklistedForStage$.MODULE$.apply(var0, var2, var3, var4, var5);
   }

   public static Function1<Tuple5<Object, String, Object, Object, Object>, SparkListenerNodeBlacklistedForStage> tupled() {
      return SparkListenerNodeBlacklistedForStage$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Object, Function1<Object, Function1<Object, SparkListenerNodeBlacklistedForStage>>>>> curried() {
      return SparkListenerNodeBlacklistedForStage$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public String hostId() {
      return this.hostId;
   }

   public int executorFailures() {
      return this.executorFailures;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public SparkListenerNodeBlacklistedForStage copy(long time, String hostId, int executorFailures, int stageId, int stageAttemptId) {
      return new SparkListenerNodeBlacklistedForStage(time, hostId, executorFailures, stageId, stageAttemptId);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.hostId();
   }

   public int copy$default$3() {
      return this.executorFailures();
   }

   public int copy$default$4() {
      return this.stageId();
   }

   public int copy$default$5() {
      return this.stageAttemptId();
   }

   public String productPrefix() {
      return "SparkListenerNodeBlacklistedForStage";
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
            var10000 = this.hostId();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.executorFailures());
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
      return x$1 instanceof SparkListenerNodeBlacklistedForStage;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.hostId()));
      var1 = Statics.mix(var1, this.executorFailures());
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
            if (x$1 instanceof SparkListenerNodeBlacklistedForStage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label43: {
                  SparkListenerNodeBlacklistedForStage var4 = (SparkListenerNodeBlacklistedForStage)x$1;
                  if (this.time() == var4.time()) {
                     label41: {
                        String var10000 = this.hostId();
                        String var5 = var4.hostId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label41;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label41;
                        }

                        if (this.executorFailures() == var4.executorFailures() && this.stageId() == var4.stageId() && this.stageAttemptId() == var4.stageAttemptId() && var4.canEqual(this)) {
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

   public SparkListenerNodeBlacklistedForStage(long time, String hostId, int executorFailures, int stageId, int stageAttemptId) {
      this.time = time;
      this.hostId = hostId;
      this.executorFailures = executorFailures;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
