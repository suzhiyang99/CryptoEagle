package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dd\u0001B\u0001\u0003\u0001.\u0011\u0001e\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'O\u00117bG.d\u0017n\u001d;fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0015\u0001AB\u0005\f\u001a!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111\u0003F\u0007\u0002\u0005%\u0011QC\u0001\u0002\u0013'B\f'o\u001b'jgR,g.\u001a:Fm\u0016tG\u000f\u0005\u0002\u000e/%\u0011\u0001D\u0004\u0002\b!J|G-^2u!\ti!$\u0003\u0002\u001c\u001d\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$\u0001\u0003uS6,W#A\u0010\u0011\u00055\u0001\u0013BA\u0011\u000f\u0005\u0011auN\\4\t\u0011\r\u0002!\u0011#Q\u0001\n}\tQ\u0001^5nK\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAJ\u0001\u000bKb,7-\u001e;pe&#W#A\u0014\u0011\u0005!ZcBA\u0007*\u0013\tQc\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003Y5\u0012aa\u0015;sS:<'B\u0001\u0016\u000f\u0011!y\u0003A!E!\u0002\u00139\u0013aC3yK\u000e,Ho\u001c:JI\u0002B\u0001\"\r\u0001\u0003\u0016\u0004%\tAM\u0001\ri\u0006\u001c8NR1jYV\u0014Xm]\u000b\u0002gA\u0011Q\u0002N\u0005\u0003k9\u00111!\u00138u\u0011!9\u0004A!E!\u0002\u0013\u0019\u0014!\u0004;bg.4\u0015-\u001b7ve\u0016\u001c\b\u0005C\u0003:\u0001\u0011\u0005!(\u0001\u0004=S:LGO\u0010\u000b\u0005wqjd\b\u0005\u0002\u0014\u0001!)Q\u0004\u000fa\u0001?!)Q\u0005\u000fa\u0001O!)\u0011\u0007\u000fa\u0001g!9\u0001\tAA\u0001\n\u0003\t\u0015\u0001B2paf$Ba\u000f\"D\t\"9Qd\u0010I\u0001\u0002\u0004y\u0002bB\u0013@!\u0003\u0005\ra\n\u0005\bc}\u0002\n\u00111\u00014\u0011\u001d1\u0005!%A\u0005\u0002\u001d\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001IU\ty\u0012jK\u0001K!\tY\u0005+D\u0001M\u0015\tie*A\u0005v]\u000eDWmY6fI*\u0011qJD\u0001\u000bC:tw\u000e^1uS>t\u0017BA)M\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b'\u0002\t\n\u0011\"\u0001U\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\u0012!\u0016\u0016\u0003O%Cqa\u0016\u0001\u0012\u0002\u0013\u0005\u0001,\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0003eS#aM%\t\u000fm\u0003\u0011\u0011!C!9\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u0018\t\u0003=\u000el\u0011a\u0018\u0006\u0003A\u0006\fA\u0001\\1oO*\t!-\u0001\u0003kCZ\f\u0017B\u0001\u0017`\u0011\u001d)\u0007!!A\u0005\u0002I\nA\u0002\u001d:pIV\u001cG/\u0011:jifDqa\u001a\u0001\u0002\u0002\u0013\u0005\u0001.\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005%d\u0007CA\u0007k\u0013\tYgBA\u0002B]fDq!\u001c4\u0002\u0002\u0003\u00071'A\u0002yIEBqa\u001c\u0001\u0002\u0002\u0013\u0005\u0003/A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005\t\bc\u0001:vS6\t1O\u0003\u0002u\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005Y\u001c(\u0001C%uKJ\fGo\u001c:\t\u000fa\u0004\u0011\u0011!C\u0001s\u0006A1-\u00198FcV\fG\u000e\u0006\u0002{{B\u0011Qb_\u0005\u0003y:\u0011qAQ8pY\u0016\fg\u000eC\u0004no\u0006\u0005\t\u0019A5\t\u0011}\u0004\u0011\u0011!C!\u0003\u0003\t\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002g!I\u0011Q\u0001\u0001\u0002\u0002\u0013\u0005\u0013qA\u0001\ti>\u001cFO]5oOR\tQ\fC\u0005\u0002\f\u0001\t\t\u0011\"\u0011\u0002\u000e\u00051Q-];bYN$2A_A\b\u0011!i\u0017\u0011BA\u0001\u0002\u0004I\u0007f\u0001\u0001\u0002\u0014A!\u0011QCA\r\u001b\t\t9B\u0003\u0002P\t%!\u00111DA\f\u00051!UM^3m_B,'/\u00119j\u000f%\tyBAA\u0001\u0012\u0003\t\t#\u0001\u0011Ta\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:CY\u0006\u001c7\u000e\\5ti\u0016$\u0007cA\n\u0002$\u0019A\u0011AAA\u0001\u0012\u0003\t)cE\u0003\u0002$\u0005\u001d\u0012\u0004\u0005\u0005\u0002*\u0005=rdJ\u001a<\u001b\t\tYCC\u0002\u0002.9\tqA];oi&lW-\u0003\u0003\u00022\u0005-\"!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8og!9\u0011(a\t\u0005\u0002\u0005UBCAA\u0011\u0011)\t)!a\t\u0002\u0002\u0013\u0015\u0013q\u0001\u0005\u000b\u0003w\t\u0019#!A\u0005\u0002\u0006u\u0012!B1qa2LHcB\u001e\u0002@\u0005\u0005\u00131\t\u0005\u0007;\u0005e\u0002\u0019A\u0010\t\r\u0015\nI\u00041\u0001(\u0011\u0019\t\u0014\u0011\ba\u0001g!Q\u0011qIA\u0012\u0003\u0003%\t)!\u0013\u0002\u000fUt\u0017\r\u001d9msR!\u00111JA,!\u0015i\u0011QJA)\u0013\r\tyE\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\r5\t\u0019fH\u00144\u0013\r\t)F\u0004\u0002\u0007)V\u0004H.Z\u001a\t\u0013\u0005e\u0013QIA\u0001\u0002\u0004Y\u0014a\u0001=%a!Q\u0011QLA\u0012\u0003\u0003%I!a\u0018\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003C\u00022AXA2\u0013\r\t)g\u0018\u0002\u0007\u001f\nTWm\u0019;"
)
public class SparkListenerExecutorBlacklisted implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String executorId;
   private final int taskFailures;

   public static Option<Tuple3<Object, String, Object>> unapply(SparkListenerExecutorBlacklisted var0) {
      return SparkListenerExecutorBlacklisted$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorBlacklisted apply(long var0, String var2, int var3) {
      return SparkListenerExecutorBlacklisted$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, String, Object>, SparkListenerExecutorBlacklisted> tupled() {
      return SparkListenerExecutorBlacklisted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Object, SparkListenerExecutorBlacklisted>>> curried() {
      return SparkListenerExecutorBlacklisted$.MODULE$.curried();
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

   public SparkListenerExecutorBlacklisted copy(long time, String executorId, int taskFailures) {
      return new SparkListenerExecutorBlacklisted(time, executorId, taskFailures);
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

   public String productPrefix() {
      return "SparkListenerExecutorBlacklisted";
   }

   public int productArity() {
      return 3;
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
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerExecutorBlacklisted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      var1 = Statics.mix(var1, this.taskFailures());
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof SparkListenerExecutorBlacklisted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  SparkListenerExecutorBlacklisted var4 = (SparkListenerExecutorBlacklisted)x$1;
                  if (this.time() == var4.time()) {
                     label37: {
                        String var10000 = this.executorId();
                        String var5 = var4.executorId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label37;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label37;
                        }

                        if (this.taskFailures() == var4.taskFailures() && var4.canEqual(this)) {
                           var7 = true;
                           break label39;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerExecutorBlacklisted(long time, String executorId, int taskFailures) {
      this.time = time;
      this.executorId = executorId;
      this.taskFailures = taskFailures;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
