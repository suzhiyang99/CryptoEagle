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
   bytes = "\u0006\u0001\u0005\u001dd\u0001B\u0001\u0003\u0001.\u0011Ad\u00159be.d\u0015n\u001d;f]\u0016\u0014hj\u001c3f\u00052\f7m\u001b7jgR,GM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011#\u0002\u0001\r%YI\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u0014)5\t!!\u0003\u0002\u0016\u0005\t\u00112\u000b]1sW2K7\u000f^3oKJ,e/\u001a8u!\tiq#\u0003\u0002\u0019\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u001b\u0013\tYbB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001e\u0001\tU\r\u0011\"\u0001\u001f\u0003\u0011!\u0018.\\3\u0016\u0003}\u0001\"!\u0004\u0011\n\u0005\u0005r!\u0001\u0002'p]\u001eD\u0001b\t\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0006i&lW\r\t\u0005\tK\u0001\u0011)\u001a!C\u0001M\u00051\u0001n\\:u\u0013\u0012,\u0012a\n\t\u0003Q-r!!D\u0015\n\u0005)r\u0011A\u0002)sK\u0012,g-\u0003\u0002-[\t11\u000b\u001e:j]\u001eT!A\u000b\b\t\u0011=\u0002!\u0011#Q\u0001\n\u001d\nq\u0001[8ti&#\u0007\u0005\u0003\u00052\u0001\tU\r\u0011\"\u00013\u0003A)\u00070Z2vi>\u0014h)Y5mkJ,7/F\u00014!\tiA'\u0003\u00026\u001d\t\u0019\u0011J\u001c;\t\u0011]\u0002!\u0011#Q\u0001\nM\n\u0011#\u001a=fGV$xN\u001d$bS2,(/Z:!\u0011\u0015I\u0004\u0001\"\u0001;\u0003\u0019a\u0014N\\5u}Q!1\bP\u001f?!\t\u0019\u0002\u0001C\u0003\u001eq\u0001\u0007q\u0004C\u0003&q\u0001\u0007q\u0005C\u00032q\u0001\u00071\u0007C\u0004A\u0001\u0005\u0005I\u0011A!\u0002\t\r|\u0007/\u001f\u000b\u0005w\t\u001bE\tC\u0004\u001e\u007fA\u0005\t\u0019A\u0010\t\u000f\u0015z\u0004\u0013!a\u0001O!9\u0011g\u0010I\u0001\u0002\u0004\u0019\u0004b\u0002$\u0001#\u0003%\taR\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005A%FA\u0010JW\u0005Q\u0005CA&Q\u001b\u0005a%BA'O\u0003%)hn\u00195fG.,GM\u0003\u0002P\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005Ec%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"91\u000bAI\u0001\n\u0003!\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u0002+*\u0012q%\u0013\u0005\b/\u0002\t\n\u0011\"\u0001Y\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012!\u0017\u0016\u0003g%Cqa\u0017\u0001\u0002\u0002\u0013\u0005C,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002;B\u0011alY\u0007\u0002?*\u0011\u0001-Y\u0001\u0005Y\u0006twMC\u0001c\u0003\u0011Q\u0017M^1\n\u00051z\u0006bB3\u0001\u0003\u0003%\tAM\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\bO\u0002\t\t\u0011\"\u0001i\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"!\u001b7\u0011\u00055Q\u0017BA6\u000f\u0005\r\te.\u001f\u0005\b[\u001a\f\t\u00111\u00014\u0003\rAH%\r\u0005\b_\u0002\t\t\u0011\"\u0011q\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A9\u0011\u0007I,\u0018.D\u0001t\u0015\t!h\"\u0001\u0006d_2dWm\u0019;j_:L!A^:\u0003\u0011%#XM]1u_JDq\u0001\u001f\u0001\u0002\u0002\u0013\u0005\u00110\u0001\u0005dC:,\u0015/^1m)\tQX\u0010\u0005\u0002\u000ew&\u0011AP\u0004\u0002\b\u0005>|G.Z1o\u0011\u001diw/!AA\u0002%D\u0001b \u0001\u0002\u0002\u0013\u0005\u0013\u0011A\u0001\tQ\u0006\u001c\bnQ8eKR\t1\u0007C\u0005\u0002\u0006\u0001\t\t\u0011\"\u0011\u0002\b\u0005AAo\\*ue&tw\rF\u0001^\u0011%\tY\u0001AA\u0001\n\u0003\ni!\u0001\u0004fcV\fGn\u001d\u000b\u0004u\u0006=\u0001\u0002C7\u0002\n\u0005\u0005\t\u0019A5)\u0007\u0001\t\u0019\u0002\u0005\u0003\u0002\u0016\u0005eQBAA\f\u0015\tyE!\u0003\u0003\u0002\u001c\u0005]!\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!CA\u0010\u0005\u0005\u0005\t\u0012AA\u0011\u0003q\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe:{G-\u001a\"mC\u000e\\G.[:uK\u0012\u00042aEA\u0012\r!\t!!!A\t\u0002\u0005\u00152#BA\u0012\u0003OI\u0002\u0003CA\u0015\u0003_yreM\u001e\u000e\u0005\u0005-\"bAA\u0017\u001d\u00059!/\u001e8uS6,\u0017\u0002BA\u0019\u0003W\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c84\u0011\u001dI\u00141\u0005C\u0001\u0003k!\"!!\t\t\u0015\u0005\u0015\u00111EA\u0001\n\u000b\n9\u0001\u0003\u0006\u0002<\u0005\r\u0012\u0011!CA\u0003{\tQ!\u00199qYf$raOA \u0003\u0003\n\u0019\u0005\u0003\u0004\u001e\u0003s\u0001\ra\b\u0005\u0007K\u0005e\u0002\u0019A\u0014\t\rE\nI\u00041\u00014\u0011)\t9%a\t\u0002\u0002\u0013\u0005\u0015\u0011J\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tY%a\u0016\u0011\u000b5\ti%!\u0015\n\u0007\u0005=cB\u0001\u0004PaRLwN\u001c\t\u0007\u001b\u0005MsdJ\u001a\n\u0007\u0005UcB\u0001\u0004UkBdWm\r\u0005\n\u00033\n)%!AA\u0002m\n1\u0001\u001f\u00131\u0011)\ti&a\t\u0002\u0002\u0013%\u0011qL\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002bA\u0019a,a\u0019\n\u0007\u0005\u0015tL\u0001\u0004PE*,7\r\u001e"
)
public class SparkListenerNodeBlacklisted implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String hostId;
   private final int executorFailures;

   public static Option<Tuple3<Object, String, Object>> unapply(SparkListenerNodeBlacklisted var0) {
      return SparkListenerNodeBlacklisted$.MODULE$.unapply(var0);
   }

   public static SparkListenerNodeBlacklisted apply(long var0, String var2, int var3) {
      return SparkListenerNodeBlacklisted$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, String, Object>, SparkListenerNodeBlacklisted> tupled() {
      return SparkListenerNodeBlacklisted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Object, SparkListenerNodeBlacklisted>>> curried() {
      return SparkListenerNodeBlacklisted$.MODULE$.curried();
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

   public SparkListenerNodeBlacklisted copy(long time, String hostId, int executorFailures) {
      return new SparkListenerNodeBlacklisted(time, hostId, executorFailures);
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

   public String productPrefix() {
      return "SparkListenerNodeBlacklisted";
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
            var10000 = this.hostId();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.executorFailures());
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
      return x$1 instanceof SparkListenerNodeBlacklisted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.hostId()));
      var1 = Statics.mix(var1, this.executorFailures());
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
            if (x$1 instanceof SparkListenerNodeBlacklisted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  SparkListenerNodeBlacklisted var4 = (SparkListenerNodeBlacklisted)x$1;
                  if (this.time() == var4.time()) {
                     label37: {
                        String var10000 = this.hostId();
                        String var5 = var4.hostId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label37;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label37;
                        }

                        if (this.executorFailures() == var4.executorFailures() && var4.canEqual(this)) {
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

   public SparkListenerNodeBlacklisted(long time, String hostId, int executorFailures) {
      this.time = time;
      this.hostId = hostId;
      this.executorFailures = executorFailures;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
