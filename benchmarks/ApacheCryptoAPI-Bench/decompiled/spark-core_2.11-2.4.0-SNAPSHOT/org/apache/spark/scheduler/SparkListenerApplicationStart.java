package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple6;
import scala.collection.Iterator;
import scala.collection.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005]f\u0001B\u0001\u0003\u0001.\u0011Qd\u00159be.d\u0015n\u001d;f]\u0016\u0014\u0018\t\u001d9mS\u000e\fG/[8o'R\f'\u000f\u001e\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0019I1\u0012\u0004\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'Qi\u0011AA\u0005\u0003+\t\u0011!c\u00159be.d\u0015n\u001d;f]\u0016\u0014XI^3oiB\u0011QbF\u0005\u000319\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e5%\u00111D\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t;\u0001\u0011)\u001a!C\u0001=\u00059\u0011\r\u001d9OC6,W#A\u0010\u0011\u0005\u0001\u001acBA\u0007\"\u0013\t\u0011c\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003I\u0015\u0012aa\u0015;sS:<'B\u0001\u0012\u000f\u0011!9\u0003A!E!\u0002\u0013y\u0012\u0001C1qa:\u000bW.\u001a\u0011\t\u0011%\u0002!Q3A\u0005\u0002)\nQ!\u00199q\u0013\u0012,\u0012a\u000b\t\u0004\u001b1z\u0012BA\u0017\u000f\u0005\u0019y\u0005\u000f^5p]\"Aq\u0006\u0001B\tB\u0003%1&\u0001\u0004baBLE\r\t\u0005\tc\u0001\u0011)\u001a!C\u0001e\u0005!A/[7f+\u0005\u0019\u0004CA\u00075\u0013\t)dB\u0001\u0003M_:<\u0007\u0002C\u001c\u0001\u0005#\u0005\u000b\u0011B\u001a\u0002\u000bQLW.\u001a\u0011\t\u0011e\u0002!Q3A\u0005\u0002y\t\u0011b\u001d9be.,6/\u001a:\t\u0011m\u0002!\u0011#Q\u0001\n}\t!b\u001d9be.,6/\u001a:!\u0011!i\u0004A!f\u0001\n\u0003Q\u0013\u0001D1qa\u0006#H/Z7qi&#\u0007\u0002C \u0001\u0005#\u0005\u000b\u0011B\u0016\u0002\u001b\u0005\u0004\b/\u0011;uK6\u0004H/\u00133!\u0011!\t\u0005A!f\u0001\n\u0003\u0011\u0015A\u00033sSZ,'\u000fT8hgV\t1\tE\u0002\u000eY\u0011\u0003B!\u0012% ?5\taI\u0003\u0002H\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005%3%aA'ba\"A1\n\u0001B\tB\u0003%1)A\u0006ee&4XM\u001d'pON\u0004\u0003\"B'\u0001\t\u0003q\u0015A\u0002\u001fj]&$h\bF\u0004P!F\u00136\u000bV+\u0011\u0005M\u0001\u0001\"B\u000fM\u0001\u0004y\u0002\"B\u0015M\u0001\u0004Y\u0003\"B\u0019M\u0001\u0004\u0019\u0004\"B\u001dM\u0001\u0004y\u0002\"B\u001fM\u0001\u0004Y\u0003bB!M!\u0003\u0005\ra\u0011\u0005\b/\u0002\t\t\u0011\"\u0001Y\u0003\u0011\u0019w\u000e]=\u0015\u000f=K&l\u0017/^=\"9QD\u0016I\u0001\u0002\u0004y\u0002bB\u0015W!\u0003\u0005\ra\u000b\u0005\bcY\u0003\n\u00111\u00014\u0011\u001dId\u000b%AA\u0002}Aq!\u0010,\u0011\u0002\u0003\u00071\u0006C\u0004B-B\u0005\t\u0019A\"\t\u000f\u0001\u0004\u0011\u0013!C\u0001C\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u00012+\u0005}\u00197&\u00013\u0011\u0005\u0015TW\"\u00014\u000b\u0005\u001dD\u0017!C;oG\",7m[3e\u0015\tIg\"\u0001\u0006b]:|G/\u0019;j_:L!a\u001b4\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004n\u0001E\u0005I\u0011\u00018\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\tqN\u000b\u0002,G\"9\u0011\u000fAI\u0001\n\u0003\u0011\u0018AD2paf$C-\u001a4bk2$HeM\u000b\u0002g*\u00121g\u0019\u0005\bk\u0002\t\n\u0011\"\u0001b\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQBqa\u001e\u0001\u0012\u0002\u0013\u0005a.\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001b\t\u000fe\u0004\u0011\u0013!C\u0001u\u0006q1m\u001c9zI\u0011,g-Y;mi\u00122T#A>+\u0005\r\u001b\u0007bB?\u0001\u0003\u0003%\tE`\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003}\u0004B!!\u0001\u0002\f5\u0011\u00111\u0001\u0006\u0005\u0003\u000b\t9!\u0001\u0003mC:<'BAA\u0005\u0003\u0011Q\u0017M^1\n\u0007\u0011\n\u0019\u0001C\u0005\u0002\u0010\u0001\t\t\u0011\"\u0001\u0002\u0012\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011\u00111\u0003\t\u0004\u001b\u0005U\u0011bAA\f\u001d\t\u0019\u0011J\u001c;\t\u0013\u0005m\u0001!!A\u0005\u0002\u0005u\u0011A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0005\u0003?\t)\u0003E\u0002\u000e\u0003CI1!a\t\u000f\u0005\r\te.\u001f\u0005\u000b\u0003O\tI\"!AA\u0002\u0005M\u0011a\u0001=%c!I\u00111\u0006\u0001\u0002\u0002\u0013\u0005\u0013QF\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011q\u0006\t\u0006\u000b\u0006E\u0012qD\u0005\u0004\u0003g1%\u0001C%uKJ\fGo\u001c:\t\u0013\u0005]\u0002!!A\u0005\u0002\u0005e\u0012\u0001C2b]\u0016\u000bX/\u00197\u0015\t\u0005m\u0012\u0011\t\t\u0004\u001b\u0005u\u0012bAA \u001d\t9!i\\8mK\u0006t\u0007BCA\u0014\u0003k\t\t\u00111\u0001\u0002 !I\u0011Q\t\u0001\u0002\u0002\u0013\u0005\u0013qI\u0001\tQ\u0006\u001c\bnQ8eKR\u0011\u00111\u0003\u0005\n\u0003\u0017\u0002\u0011\u0011!C!\u0003\u001b\n\u0001\u0002^8TiJLgn\u001a\u000b\u0002\u007f\"I\u0011\u0011\u000b\u0001\u0002\u0002\u0013\u0005\u00131K\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005m\u0012Q\u000b\u0005\u000b\u0003O\ty%!AA\u0002\u0005}\u0001f\u0001\u0001\u0002ZA!\u00111LA0\u001b\t\tiF\u0003\u0002j\t%!\u0011\u0011MA/\u00051!UM^3m_B,'/\u00119j\u000f%\t)GAA\u0001\u0012\u0003\t9'A\u000fTa\u0006\u00148\u000eT5ti\u0016tWM]!qa2L7-\u0019;j_:\u001cF/\u0019:u!\r\u0019\u0012\u0011\u000e\u0004\t\u0003\t\t\t\u0011#\u0001\u0002lM)\u0011\u0011NA73AY\u0011qNA;?-\u001atdK\"P\u001b\t\t\tHC\u0002\u0002t9\tqA];oi&lW-\u0003\u0003\u0002x\u0005E$!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8om!9Q*!\u001b\u0005\u0002\u0005mDCAA4\u0011)\tY%!\u001b\u0002\u0002\u0013\u0015\u0013Q\n\u0005\u000b\u0003\u0003\u000bI'!A\u0005\u0002\u0006\r\u0015!B1qa2LH#D(\u0002\u0006\u0006\u001d\u0015\u0011RAF\u0003\u001b\u000by\t\u0003\u0004\u001e\u0003\u007f\u0002\ra\b\u0005\u0007S\u0005}\u0004\u0019A\u0016\t\rE\ny\b1\u00014\u0011\u0019I\u0014q\u0010a\u0001?!1Q(a A\u0002-B\u0001\"QA@!\u0003\u0005\ra\u0011\u0005\u000b\u0003'\u000bI'!A\u0005\u0002\u0006U\u0015aB;oCB\u0004H.\u001f\u000b\u0005\u0003/\u000by\n\u0005\u0003\u000eY\u0005e\u0005#C\u0007\u0002\u001c~Y3gH\u0016D\u0013\r\tiJ\u0004\u0002\u0007)V\u0004H.\u001a\u001c\t\u0013\u0005\u0005\u0016\u0011SA\u0001\u0002\u0004y\u0015a\u0001=%a!I\u0011QUA5#\u0003%\tA_\u0001\u0010CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%m!I\u0011\u0011VA5#\u0003%\tA_\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001c\t\u0015\u00055\u0016\u0011NA\u0001\n\u0013\ty+A\u0006sK\u0006$'+Z:pYZ,GCAAY!\u0011\t\t!a-\n\t\u0005U\u00161\u0001\u0002\u0007\u001f\nTWm\u0019;"
)
public class SparkListenerApplicationStart implements SparkListenerEvent, Product, Serializable {
   private final String appName;
   private final Option<String> appId;
   private final long time;
   private final String sparkUser;
   private final Option<String> appAttemptId;
   private final Option<Map<String, String>> driverLogs;

   public static Option<Map<String, String>> $lessinit$greater$default$6() {
      return SparkListenerApplicationStart$.MODULE$.$lessinit$greater$default$6();
   }

   public static Option<Map<String, String>> apply$default$6() {
      return SparkListenerApplicationStart$.MODULE$.apply$default$6();
   }

   public static Option<Tuple6<String, Option<String>, Object, String, Option<String>, Option<Map<String, String>>>> unapply(SparkListenerApplicationStart var0) {
      return SparkListenerApplicationStart$.MODULE$.unapply(var0);
   }

   public static SparkListenerApplicationStart apply(String var0, Option<String> var1, long var2, String var4, Option<String> var5, Option<Map<String, String>> var6) {
      return SparkListenerApplicationStart$.MODULE$.apply(var0, var1, var2, var4, var5, var6);
   }

   public static Function1<Tuple6<String, Option<String>, Object, String, Option<String>, Option<Map<String, String>>>, SparkListenerApplicationStart> tupled() {
      return SparkListenerApplicationStart$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Option<String>, Function1<Object, Function1<String, Function1<Option<String>, Function1<Option<Map<String, String>>, SparkListenerApplicationStart>>>>>> curried() {
      return SparkListenerApplicationStart$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public String appName() {
      return this.appName;
   }

   public Option<String> appId() {
      return this.appId;
   }

   public long time() {
      return this.time;
   }

   public String sparkUser() {
      return this.sparkUser;
   }

   public Option<String> appAttemptId() {
      return this.appAttemptId;
   }

   public Option<Map<String, String>> driverLogs() {
      return this.driverLogs;
   }

   public SparkListenerApplicationStart copy(String appName, Option<String> appId, long time, String sparkUser, Option<String> appAttemptId, Option<Map<String, String>> driverLogs) {
      return new SparkListenerApplicationStart(appName, appId, time, sparkUser, appAttemptId, driverLogs);
   }

   public String copy$default$1() {
      return this.appName();
   }

   public Option<String> copy$default$2() {
      return this.appId();
   }

   public long copy$default$3() {
      return this.time();
   }

   public String copy$default$4() {
      return this.sparkUser();
   }

   public Option<String> copy$default$5() {
      return this.appAttemptId();
   }

   public Option<Map<String, String>> copy$default$6() {
      return this.driverLogs();
   }

   public String productPrefix() {
      return "SparkListenerApplicationStart";
   }

   public int productArity() {
      return 6;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.appName();
            break;
         case 1:
            var10000 = this.appId();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 3:
            var10000 = this.sparkUser();
            break;
         case 4:
            var10000 = this.appAttemptId();
            break;
         case 5:
            var10000 = this.driverLogs();
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
      return x$1 instanceof SparkListenerApplicationStart;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.appName()));
      var1 = Statics.mix(var1, Statics.anyHash(this.appId()));
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.sparkUser()));
      var1 = Statics.mix(var1, Statics.anyHash(this.appAttemptId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.driverLogs()));
      return Statics.finalizeHash(var1, 6);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var15;
      if (this != x$1) {
         label92: {
            boolean var3;
            if (x$1 instanceof SparkListenerApplicationStart) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label73: {
                  label82: {
                     SparkListenerApplicationStart var4 = (SparkListenerApplicationStart)x$1;
                     String var10000 = this.appName();
                     String var5 = var4.appName();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label82;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label82;
                     }

                     Option var10 = this.appId();
                     Option var6 = var4.appId();
                     if (var10 == null) {
                        if (var6 != null) {
                           break label82;
                        }
                     } else if (!var10.equals(var6)) {
                        break label82;
                     }

                     if (this.time() == var4.time()) {
                        label83: {
                           String var11 = this.sparkUser();
                           String var7 = var4.sparkUser();
                           if (var11 == null) {
                              if (var7 != null) {
                                 break label83;
                              }
                           } else if (!var11.equals(var7)) {
                              break label83;
                           }

                           Option var12 = this.appAttemptId();
                           Option var8 = var4.appAttemptId();
                           if (var12 == null) {
                              if (var8 != null) {
                                 break label83;
                              }
                           } else if (!var12.equals(var8)) {
                              break label83;
                           }

                           var12 = this.driverLogs();
                           Option var9 = var4.driverLogs();
                           if (var12 == null) {
                              if (var9 != null) {
                                 break label83;
                              }
                           } else if (!var12.equals(var9)) {
                              break label83;
                           }

                           if (var4.canEqual(this)) {
                              var15 = true;
                              break label73;
                           }
                        }
                     }
                  }

                  var15 = false;
               }

               if (var15) {
                  break label92;
               }
            }

            var15 = false;
            return var15;
         }
      }

      var15 = true;
      return var15;
   }

   public SparkListenerApplicationStart(String appName, Option<String> appId, long time, String sparkUser, Option<String> appAttemptId, Option<Map<String, String>> driverLogs) {
      this.appName = appName;
      this.appId = appId;
      this.time = time;
      this.sparkUser = sparkUser;
      this.appAttemptId = appAttemptId;
      this.driverLogs = driverLogs;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
