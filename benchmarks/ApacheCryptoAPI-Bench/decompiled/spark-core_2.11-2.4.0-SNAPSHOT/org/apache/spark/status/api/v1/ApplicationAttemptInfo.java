package org.apache.spark.status.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple8;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@JsonIgnoreProperties(
   value = {"startTimeEpoch", "endTimeEpoch", "lastUpdatedEpoch"},
   allowGetters = true
)
@ScalaSignature(
   bytes = "\u0006\u0001\tMa\u0001B\u0001\u0003\u0001>\u0011a#\u00119qY&\u001c\u0017\r^5p]\u0006#H/Z7qi&sgm\u001c\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001!YI\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0005\u0002\u0012/%\u0011\u0001D\u0005\u0002\b!J|G-^2u!\t\t\"$\u0003\u0002\u001c%\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$A\u0005biR,W\u000e\u001d;JIV\tq\u0004E\u0002\u0012A\tJ!!\t\n\u0003\r=\u0003H/[8o!\t\u0019cE\u0004\u0002\u0012I%\u0011QEE\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#AB*ue&twM\u0003\u0002&%!A!\u0006\u0001B\tB\u0003%q$\u0001\u0006biR,W\u000e\u001d;JI\u0002B\u0001\u0002\f\u0001\u0003\u0016\u0004%\t!L\u0001\ngR\f'\u000f\u001e+j[\u0016,\u0012A\f\t\u0003_Qj\u0011\u0001\r\u0006\u0003cI\nA!\u001e;jY*\t1'\u0001\u0003kCZ\f\u0017BA\u001b1\u0005\u0011!\u0015\r^3\t\u0011]\u0002!\u0011#Q\u0001\n9\n!b\u001d;beR$\u0016.\\3!\u0011!I\u0004A!f\u0001\n\u0003i\u0013aB3oIRKW.\u001a\u0005\tw\u0001\u0011\t\u0012)A\u0005]\u0005AQM\u001c3US6,\u0007\u0005\u0003\u0005>\u0001\tU\r\u0011\"\u0001.\u0003-a\u0017m\u001d;Va\u0012\fG/\u001a3\t\u0011}\u0002!\u0011#Q\u0001\n9\nA\u0002\\1tiV\u0003H-\u0019;fI\u0002B\u0001\"\u0011\u0001\u0003\u0016\u0004%\tAQ\u0001\tIV\u0014\u0018\r^5p]V\t1\t\u0005\u0002\u0012\t&\u0011QI\u0005\u0002\u0005\u0019>tw\r\u0003\u0005H\u0001\tE\t\u0015!\u0003D\u0003%!WO]1uS>t\u0007\u0005\u0003\u0005J\u0001\tU\r\u0011\"\u0001K\u0003%\u0019\b/\u0019:l+N,'/F\u0001#\u0011!a\u0005A!E!\u0002\u0013\u0011\u0013AC:qCJ\\Wk]3sA!Aa\n\u0001BK\u0002\u0013\u0005q*A\u0005d_6\u0004H.\u001a;fIV\t\u0001\u000b\u0005\u0002\u0012#&\u0011!K\u0005\u0002\b\u0005>|G.Z1o\u0011!!\u0006A!E!\u0002\u0013\u0001\u0016AC2p[BdW\r^3eA!Aa\u000b\u0001BK\u0002\u0013\u0005!*A\bbaB\u001c\u0006/\u0019:l-\u0016\u00148/[8o\u0011!A\u0006A!E!\u0002\u0013\u0011\u0013\u0001E1qaN\u0003\u0018M]6WKJ\u001c\u0018n\u001c8!\u0011\u0019Q\u0006\u0001\"\u0001\t7\u00061A(\u001b8jiz\"\u0012\u0002\u00180`A\u0006\u00147\rZ3\u0011\u0005u\u0003Q\"\u0001\u0002\t\u000buI\u0006\u0019A\u0010\t\u000b1J\u0006\u0019\u0001\u0018\t\u000beJ\u0006\u0019\u0001\u0018\t\u000buJ\u0006\u0019\u0001\u0018\t\u000b\u0005K\u0006\u0019A\"\t\u000b%K\u0006\u0019\u0001\u0012\t\u000f9K\u0006\u0013!a\u0001!\")a+\u0017a\u0001E!)q\r\u0001C\u0001\u0005\u0006\tr-\u001a;Ti\u0006\u0014H\u000fV5nK\u0016\u0003xn\u00195\t\u000b%\u0004A\u0011\u0001\"\u0002\u001f\u001d,G/\u00128e)&lW-\u00129pG\"DQa\u001b\u0001\u0005\u0002\t\u000b1cZ3u\u0019\u0006\u001cH/\u00169eCR,G-\u00129pG\"Dq!\u001c\u0001\u0002\u0002\u0013\u0005a.\u0001\u0003d_BLH#\u0003/paF\u00148\u000f^;w\u0011\u001diB\u000e%AA\u0002}Aq\u0001\f7\u0011\u0002\u0003\u0007a\u0006C\u0004:YB\u0005\t\u0019\u0001\u0018\t\u000fub\u0007\u0013!a\u0001]!9\u0011\t\u001cI\u0001\u0002\u0004\u0019\u0005bB%m!\u0003\u0005\rA\t\u0005\b\u001d2\u0004\n\u00111\u0001Q\u0011\u001d1F\u000e%AA\u0002\tBq\u0001\u001f\u0001\u0012\u0002\u0013\u0005\u00110\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003iT#aH>,\u0003q\u00042!`A\u0003\u001b\u0005q(bA@\u0002\u0002\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003\u0007\u0011\u0012AC1o]>$\u0018\r^5p]&\u0019\u0011q\u0001@\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002\f\u0001\t\n\u0011\"\u0001\u0002\u000e\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012TCAA\bU\tq3\u0010C\u0005\u0002\u0014\u0001\t\n\u0011\"\u0001\u0002\u000e\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u001a\u0004\"CA\f\u0001E\u0005I\u0011AA\u0007\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQB\u0011\"a\u0007\u0001#\u0003%\t!!\b\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%kU\u0011\u0011q\u0004\u0016\u0003\u0007nD\u0011\"a\t\u0001#\u0003%\t!!\n\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%mU\u0011\u0011q\u0005\u0016\u0003EmD\u0011\"a\u000b\u0001#\u0003%\t!!\f\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%oU\u0011\u0011q\u0006\u0016\u0003!nD\u0011\"a\r\u0001#\u0003%\t!!\n\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%q!I\u0011q\u0007\u0001\u0002\u0002\u0013\u0005\u0013\u0011H\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005m\u0002\u0003BA\u001f\u0003\u0007j!!a\u0010\u000b\u0007\u0005\u0005#'\u0001\u0003mC:<\u0017bA\u0014\u0002@!I\u0011q\t\u0001\u0002\u0002\u0013\u0005\u0011\u0011J\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0003\u0003\u0017\u00022!EA'\u0013\r\tyE\u0005\u0002\u0004\u0013:$\b\"CA*\u0001\u0005\u0005I\u0011AA+\u00039\u0001(o\u001c3vGR,E.Z7f]R$B!a\u0016\u0002^A\u0019\u0011#!\u0017\n\u0007\u0005m#CA\u0002B]fD!\"a\u0018\u0002R\u0005\u0005\t\u0019AA&\u0003\rAH%\r\u0005\n\u0003G\u0002\u0011\u0011!C!\u0003K\nq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003O\u0002b!!\u001b\u0002p\u0005]SBAA6\u0015\r\tiGE\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA9\u0003W\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003k\u0002\u0011\u0011!C\u0001\u0003o\n\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0004!\u0006e\u0004BCA0\u0003g\n\t\u00111\u0001\u0002X!I\u0011Q\u0010\u0001\u0002\u0002\u0013\u0005\u0013qP\u0001\tQ\u0006\u001c\bnQ8eKR\u0011\u00111\n\u0005\n\u0003\u0007\u0003\u0011\u0011!C!\u0003\u000b\u000b\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003wA\u0011\"!#\u0001\u0003\u0003%\t%a#\u0002\r\u0015\fX/\u00197t)\r\u0001\u0016Q\u0012\u0005\u000b\u0003?\n9)!AA\u0002\u0005]\u0003f\u0003\u0001\u0002\u0012\u0006\u001d\u0016\u0011VA\\\u0003s\u0003B!a%\u0002$6\u0011\u0011Q\u0013\u0006\u0005\u0003\u0007\t9J\u0003\u0003\u0002\u001a\u0006m\u0015a\u00026bG.\u001cxN\u001c\u0006\u0005\u0003;\u000by*A\u0005gCN$XM\u001d=nY*\u0011\u0011\u0011U\u0001\u0004G>l\u0017\u0002BAS\u0003+\u0013ACS:p]&;gn\u001c:f!J|\u0007/\u001a:uS\u0016\u001c\u0018!\u0002<bYV,GFBAV\u0003_\u000b\u0019,\t\u0002\u0002.\u0006q1\u000f^1siRKW.Z#q_\u000eD\u0017EAAY\u00031)g\u000e\u001a+j[\u0016,\u0005o\\2iC\t\t),\u0001\tmCN$X\u000b\u001d3bi\u0016$W\t]8dQ\u0006a\u0011\r\u001c7po\u001e+G\u000f^3sgf\t\u0011aB\u0005\u0002>\n\t\t\u0011#\u0001\u0002@\u00061\u0012\t\u001d9mS\u000e\fG/[8o\u0003R$X-\u001c9u\u0013:4w\u000eE\u0002^\u0003\u00034\u0001\"\u0001\u0002\u0002\u0002#\u0005\u00111Y\n\u0006\u0003\u0003\f)-\u0007\t\u000e\u0003\u000f\fim\b\u0018/]\r\u0013\u0003K\t/\u000e\u0005\u0005%'bAAf%\u00059!/\u001e8uS6,\u0017\u0002BAh\u0003\u0013\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c89\u0011\u001dQ\u0016\u0011\u0019C\u0001\u0003'$\"!a0\t\u0015\u0005\r\u0015\u0011YA\u0001\n\u000b\n)\t\u0003\u0006\u0002Z\u0006\u0005\u0017\u0011!CA\u00037\fQ!\u00199qYf$\u0012\u0003XAo\u0003?\f\t/a9\u0002f\u0006\u001d\u0018\u0011^Av\u0011\u0019i\u0012q\u001ba\u0001?!1A&a6A\u00029Ba!OAl\u0001\u0004q\u0003BB\u001f\u0002X\u0002\u0007a\u0006\u0003\u0004B\u0003/\u0004\ra\u0011\u0005\u0007\u0013\u0006]\u0007\u0019\u0001\u0012\t\u00119\u000b9\u000e%AA\u0002ACaAVAl\u0001\u0004\u0011\u0003BCAx\u0003\u0003\f\t\u0011\"!\u0002r\u00069QO\\1qa2LH\u0003BAz\u0003w\u0004B!\u0005\u0011\u0002vBY\u0011#a> ]9r3I\t)#\u0013\r\tIP\u0005\u0002\u0007)V\u0004H.\u001a\u001d\t\u0013\u0005u\u0018Q^A\u0001\u0002\u0004a\u0016a\u0001=%a!Q!\u0011AAa#\u0003%\t!!\f\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uI]B!B!\u0002\u0002BF\u0005I\u0011AA\u0017\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%o!Q!\u0011BAa\u0003\u0003%IAa\u0003\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005\u001b\u0001B!!\u0010\u0003\u0010%!!\u0011CA \u0005\u0019y%M[3di\u0002"
)
public class ApplicationAttemptInfo implements Product, Serializable {
   private final Option<String> attemptId;
   private final Date startTime;
   private final Date endTime;
   private final Date lastUpdated;
   private final long duration;
   private final String sparkUser;
   private final boolean completed;
   private final String appSparkVersion;

   public static boolean $lessinit$greater$default$7() {
      return ApplicationAttemptInfo$.MODULE$.$lessinit$greater$default$7();
   }

   public static boolean apply$default$7() {
      return ApplicationAttemptInfo$.MODULE$.apply$default$7();
   }

   public static Option<Tuple8<Option<String>, Date, Date, Date, Object, String, Object, String>> unapply(ApplicationAttemptInfo var0) {
      return ApplicationAttemptInfo$.MODULE$.unapply(var0);
   }

   public static ApplicationAttemptInfo apply(Option<String> var0, Date var1, Date var2, Date var3, long var4, String var6, boolean var7, String var8) {
      return ApplicationAttemptInfo$.MODULE$.apply(var0, var1, var2, var3, var4, var6, var7, var8);
   }

   public static Function1<Tuple8<Option<String>, Date, Date, Date, Object, String, Object, String>, ApplicationAttemptInfo> tupled() {
      return ApplicationAttemptInfo$.MODULE$.tupled();
   }

   public static Function1<Option<String>, Function1<Date, Function1<Date, Function1<Date, Function1<Object, Function1<String, Function1<Object, Function1<String, ApplicationAttemptInfo>>>>>>>> curried() {
      return ApplicationAttemptInfo$.MODULE$.curried();
   }

   public Option<String> attemptId() {
      return this.attemptId;
   }

   public Date startTime() {
      return this.startTime;
   }

   public Date endTime() {
      return this.endTime;
   }

   public Date lastUpdated() {
      return this.lastUpdated;
   }

   public long duration() {
      return this.duration;
   }

   public String sparkUser() {
      return this.sparkUser;
   }

   public boolean completed() {
      return this.completed;
   }

   public String appSparkVersion() {
      return this.appSparkVersion;
   }

   public long getStartTimeEpoch() {
      return this.startTime().getTime();
   }

   public long getEndTimeEpoch() {
      return this.endTime().getTime();
   }

   public long getLastUpdatedEpoch() {
      return this.lastUpdated().getTime();
   }

   public ApplicationAttemptInfo copy(Option<String> attemptId, Date startTime, Date endTime, Date lastUpdated, long duration, String sparkUser, boolean completed, String appSparkVersion) {
      return new ApplicationAttemptInfo(attemptId, startTime, endTime, lastUpdated, duration, sparkUser, completed, appSparkVersion);
   }

   public Option<String> copy$default$1() {
      return this.attemptId();
   }

   public Date copy$default$2() {
      return this.startTime();
   }

   public Date copy$default$3() {
      return this.endTime();
   }

   public Date copy$default$4() {
      return this.lastUpdated();
   }

   public long copy$default$5() {
      return this.duration();
   }

   public String copy$default$6() {
      return this.sparkUser();
   }

   public boolean copy$default$7() {
      return this.completed();
   }

   public String copy$default$8() {
      return this.appSparkVersion();
   }

   public String productPrefix() {
      return "ApplicationAttemptInfo";
   }

   public int productArity() {
      return 8;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.attemptId();
            break;
         case 1:
            var10000 = this.startTime();
            break;
         case 2:
            var10000 = this.endTime();
            break;
         case 3:
            var10000 = this.lastUpdated();
            break;
         case 4:
            var10000 = BoxesRunTime.boxToLong(this.duration());
            break;
         case 5:
            var10000 = this.sparkUser();
            break;
         case 6:
            var10000 = BoxesRunTime.boxToBoolean(this.completed());
            break;
         case 7:
            var10000 = this.appSparkVersion();
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
      return x$1 instanceof ApplicationAttemptInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.attemptId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.startTime()));
      var1 = Statics.mix(var1, Statics.anyHash(this.endTime()));
      var1 = Statics.mix(var1, Statics.anyHash(this.lastUpdated()));
      var1 = Statics.mix(var1, Statics.longHash(this.duration()));
      var1 = Statics.mix(var1, Statics.anyHash(this.sparkUser()));
      var1 = Statics.mix(var1, this.completed() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.appSparkVersion()));
      return Statics.finalizeHash(var1, 8);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var17;
      if (this != x$1) {
         label102: {
            boolean var3;
            if (x$1 instanceof ApplicationAttemptInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label84: {
                  label93: {
                     ApplicationAttemptInfo var4 = (ApplicationAttemptInfo)x$1;
                     Option var10000 = this.attemptId();
                     Option var5 = var4.attemptId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label93;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label93;
                     }

                     Date var11 = this.startTime();
                     Date var6 = var4.startTime();
                     if (var11 == null) {
                        if (var6 != null) {
                           break label93;
                        }
                     } else if (!var11.equals(var6)) {
                        break label93;
                     }

                     var11 = this.endTime();
                     Date var7 = var4.endTime();
                     if (var11 == null) {
                        if (var7 != null) {
                           break label93;
                        }
                     } else if (!var11.equals(var7)) {
                        break label93;
                     }

                     var11 = this.lastUpdated();
                     Date var8 = var4.lastUpdated();
                     if (var11 == null) {
                        if (var8 != null) {
                           break label93;
                        }
                     } else if (!var11.equals(var8)) {
                        break label93;
                     }

                     if (this.duration() == var4.duration()) {
                        label62: {
                           String var14 = this.sparkUser();
                           String var9 = var4.sparkUser();
                           if (var14 == null) {
                              if (var9 != null) {
                                 break label62;
                              }
                           } else if (!var14.equals(var9)) {
                              break label62;
                           }

                           if (this.completed() == var4.completed()) {
                              label56: {
                                 var14 = this.appSparkVersion();
                                 String var10 = var4.appSparkVersion();
                                 if (var14 == null) {
                                    if (var10 != null) {
                                       break label56;
                                    }
                                 } else if (!var14.equals(var10)) {
                                    break label56;
                                 }

                                 if (var4.canEqual(this)) {
                                    var17 = true;
                                    break label84;
                                 }
                              }
                           }
                        }
                     }
                  }

                  var17 = false;
               }

               if (var17) {
                  break label102;
               }
            }

            var17 = false;
            return var17;
         }
      }

      var17 = true;
      return var17;
   }

   public ApplicationAttemptInfo(Option<String> attemptId, Date startTime, Date endTime, Date lastUpdated, long duration, String sparkUser, boolean completed, String appSparkVersion) {
      this.attemptId = attemptId;
      this.startTime = startTime;
      this.endTime = endTime;
      this.lastUpdated = lastUpdated;
      this.duration = duration;
      this.sparkUser = sparkUser;
      this.completed = completed;
      this.appSparkVersion = appSparkVersion;
      Product$class.$init$(this);
   }
}
