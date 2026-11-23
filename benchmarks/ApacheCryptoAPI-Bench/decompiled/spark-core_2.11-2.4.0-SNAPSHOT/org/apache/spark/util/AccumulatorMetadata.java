package org.apache.spark.util;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec!B\u0001\u0003\u0001\u0012Q!aE!dGVlW\u000f\\1u_JlU\r^1eCR\f'BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0012)A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"\u0001\u0004\n\n\u0005Mi!\u0001D*fe&\fG.\u001b>bE2,\u0007C\u0001\u0007\u0016\u0013\t1RBA\u0004Qe>$Wo\u0019;\t\u0011a\u0001!Q3A\u0005\u0002i\t!!\u001b3\u0004\u0001U\t1\u0004\u0005\u0002\r9%\u0011Q$\u0004\u0002\u0005\u0019>tw\r\u0003\u0005 \u0001\tE\t\u0015!\u0003\u001c\u0003\rIG\r\t\u0005\tC\u0001\u0011)\u001a!C\u0001E\u0005!a.Y7f+\u0005\u0019\u0003c\u0001\u0007%M%\u0011Q%\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\u001dRcB\u0001\u0007)\u0013\tIS\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003W1\u0012aa\u0015;sS:<'BA\u0015\u000e\u0011!q\u0003A!E!\u0002\u0013\u0019\u0013!\u00028b[\u0016\u0004\u0003\u0002\u0003\u0019\u0001\u0005+\u0007I\u0011A\u0019\u0002#\r|WO\u001c;GC&dW\r\u001a,bYV,7/F\u00013!\ta1'\u0003\u00025\u001b\t9!i\\8mK\u0006t\u0007\u0002\u0003\u001c\u0001\u0005#\u0005\u000b\u0011\u0002\u001a\u0002%\r|WO\u001c;GC&dW\r\u001a,bYV,7\u000f\t\u0005\u0006q\u0001!\t!O\u0001\u0007y%t\u0017\u000e\u001e \u0015\tibTH\u0010\t\u0003w\u0001i\u0011A\u0001\u0005\u00061]\u0002\ra\u0007\u0005\u0006C]\u0002\ra\t\u0005\u0006a]\u0002\rA\r\u0005\b\u0001\u0002\t\t\u0011\"\u0001B\u0003\u0011\u0019w\u000e]=\u0015\ti\u00125\t\u0012\u0005\b1}\u0002\n\u00111\u0001\u001c\u0011\u001d\ts\b%AA\u0002\rBq\u0001M \u0011\u0002\u0003\u0007!\u0007C\u0004G\u0001E\u0005I\u0011A$\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0001J\u000b\u0002\u001c\u0013.\n!\n\u0005\u0002L!6\tAJ\u0003\u0002N\u001d\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u001f6\t!\"\u00198o_R\fG/[8o\u0013\t\tFJA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dqa\u0015\u0001\u0012\u0002\u0013\u0005A+\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003US#aI%\t\u000f]\u0003\u0011\u0013!C\u00011\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aT#A-+\u0005IJ\u0005bB.\u0001\u0003\u0003%\t\u0005X\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003u\u0003\"AX2\u000e\u0003}S!\u0001Y1\u0002\t1\fgn\u001a\u0006\u0002E\u0006!!.\u0019<b\u0013\tYs\fC\u0004f\u0001\u0005\u0005I\u0011\u00014\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u001d\u0004\"\u0001\u00045\n\u0005%l!aA%oi\"91\u000eAA\u0001\n\u0003a\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003[B\u0004\"\u0001\u00048\n\u0005=l!aA!os\"9\u0011O[A\u0001\u0002\u00049\u0017a\u0001=%c!91\u000fAA\u0001\n\u0003\"\u0018a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003U\u00042A^=n\u001b\u00059(B\u0001=\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003u^\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\by\u0002\t\t\u0011\"\u0001~\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001\u001a\u007f\u0011\u001d\t80!AA\u00025D\u0011\"!\u0001\u0001\u0003\u0003%\t%a\u0001\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\u001a\u0005\n\u0003\u000f\u0001\u0011\u0011!C!\u0003\u0013\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002;\"I\u0011Q\u0002\u0001\u0002\u0002\u0013\u0005\u0013qB\u0001\u0007KF,\u0018\r\\:\u0015\u0007I\n\t\u0002\u0003\u0005r\u0003\u0017\t\t\u00111\u0001n\u000f)\t)BAA\u0001\u0012\u0003!\u0011qC\u0001\u0014\u0003\u000e\u001cW/\\;mCR|'/T3uC\u0012\fG/\u0019\t\u0004w\u0005ea!C\u0001\u0003\u0003\u0003E\t\u0001BA\u000e'\u0015\tI\"!\b\u0012!!\ty\"!\n\u001cGIRTBAA\u0011\u0015\r\t\u0019#D\u0001\beVtG/[7f\u0013\u0011\t9#!\t\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t7\u0007C\u00049\u00033!\t!a\u000b\u0015\u0005\u0005]\u0001BCA\u0004\u00033\t\t\u0011\"\u0012\u0002\n!Q\u0011\u0011GA\r\u0003\u0003%\t)a\r\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000fi\n)$a\u000e\u0002:!1\u0001$a\fA\u0002mAa!IA\u0018\u0001\u0004\u0019\u0003B\u0002\u0019\u00020\u0001\u0007!\u0007\u0003\u0006\u0002>\u0005e\u0011\u0011!CA\u0003\u007f\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002B\u0005%\u0003\u0003\u0002\u0007%\u0003\u0007\u0002b\u0001DA#7\r\u0012\u0014bAA$\u001b\t1A+\u001e9mKNB\u0011\"a\u0013\u0002<\u0005\u0005\t\u0019\u0001\u001e\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002P\u0005e\u0011\u0011!C\u0005\u0003#\n1B]3bIJ+7o\u001c7wKR\u0011\u00111\u000b\t\u0004=\u0006U\u0013bAA,?\n1qJ\u00196fGR\u0004"
)
public class AccumulatorMetadata implements Serializable, Product {
   private final long id;
   private final Option<String> name;
   private final boolean countFailedValues;

   public static Option<Tuple3<Object, Option<String>, Object>> unapply(AccumulatorMetadata var0) {
      return AccumulatorMetadata$.MODULE$.unapply(var0);
   }

   public static AccumulatorMetadata apply(long var0, Option<String> var2, boolean var3) {
      return AccumulatorMetadata$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, Option<String>, Object>, AccumulatorMetadata> tupled() {
      return AccumulatorMetadata$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Option<String>, Function1<Object, AccumulatorMetadata>>> curried() {
      return AccumulatorMetadata$.MODULE$.curried();
   }

   public long id() {
      return this.id;
   }

   public Option<String> name() {
      return this.name;
   }

   public boolean countFailedValues() {
      return this.countFailedValues;
   }

   public AccumulatorMetadata copy(long id, Option<String> name, boolean countFailedValues) {
      return new AccumulatorMetadata(id, name, countFailedValues);
   }

   public long copy$default$1() {
      return this.id();
   }

   public Option<String> copy$default$2() {
      return this.name();
   }

   public boolean copy$default$3() {
      return this.countFailedValues();
   }

   public String productPrefix() {
      return "AccumulatorMetadata";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.id());
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToBoolean(this.countFailedValues());
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
      return x$1 instanceof AccumulatorMetadata;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.id()));
      var1 = Statics.mix(var1, Statics.anyHash(this.name()));
      var1 = Statics.mix(var1, this.countFailedValues() ? 1231 : 1237);
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
            if (x$1 instanceof AccumulatorMetadata) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  AccumulatorMetadata var4 = (AccumulatorMetadata)x$1;
                  if (this.id() == var4.id()) {
                     label37: {
                        Option var10000 = this.name();
                        Option var5 = var4.name();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label37;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label37;
                        }

                        if (this.countFailedValues() == var4.countFailedValues() && var4.canEqual(this)) {
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

   public AccumulatorMetadata(long id, Option<String> name, boolean countFailedValues) {
      this.id = id;
      this.name = name;
      this.countFailedValues = countFailedValues;
      Product$class.$init$(this);
   }
}
