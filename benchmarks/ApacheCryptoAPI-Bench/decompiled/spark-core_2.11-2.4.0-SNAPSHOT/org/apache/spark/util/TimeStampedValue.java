package org.apache.spark.util;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec!B\u0001\u0003\u0001\u0012Q!\u0001\u0005+j[\u0016\u001cF/Y7qK\u00124\u0016\r\\;f\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-q2\u0003\u0002\u0001\r%U\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\u0007\u0014\u0013\t!bBA\u0004Qe>$Wo\u0019;\u0011\u000551\u0012BA\f\u000f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0002A!f\u0001\n\u0003Y\u0012!\u0002<bYV,7\u0001A\u000b\u00029A\u0011QD\b\u0007\u0001\t\u0015y\u0002A1\u0001!\u0005\u00051\u0016CA\u0011%!\ti!%\u0003\u0002$\u001d\t9aj\u001c;iS:<\u0007CA\u0007&\u0013\t1cBA\u0002B]fD\u0001\u0002\u000b\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\u0007m\u0006dW/\u001a\u0011\t\u0011)\u0002!Q3A\u0005\u0002-\n\u0011\u0002^5nKN$\u0018-\u001c9\u0016\u00031\u0002\"!D\u0017\n\u00059r!\u0001\u0002'p]\u001eD\u0001\u0002\r\u0001\u0003\u0012\u0003\u0006I\u0001L\u0001\u000bi&lWm\u001d;b[B\u0004\u0003\"\u0002\u001a\u0001\t\u0003\u0019\u0014A\u0002\u001fj]&$h\bF\u00025m]\u00022!\u000e\u0001\u001d\u001b\u0005\u0011\u0001\"B\r2\u0001\u0004a\u0002\"\u0002\u00162\u0001\u0004a\u0003bB\u001d\u0001\u0003\u0003%\tAO\u0001\u0005G>\u0004\u00180\u0006\u0002<}Q\u0019Ah\u0010!\u0011\u0007U\u0002Q\b\u0005\u0002\u001e}\u0011)q\u0004\u000fb\u0001A!9\u0011\u0004\u000fI\u0001\u0002\u0004i\u0004b\u0002\u00169!\u0003\u0005\r\u0001\f\u0005\b\u0005\u0002\t\n\u0011\"\u0001D\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\"\u0001R(\u0016\u0003\u0015S#\u0001\b$,\u0003\u001d\u0003\"\u0001S'\u000e\u0003%S!AS&\u0002\u0013Ut7\r[3dW\u0016$'B\u0001'\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u001d&\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u0015y\u0012I1\u0001!\u0011\u001d\t\u0006!%A\u0005\u0002I\u000babY8qs\u0012\"WMZ1vYR$#'\u0006\u0002T+V\tAK\u000b\u0002-\r\u0012)q\u0004\u0015b\u0001A!9q\u000bAA\u0001\n\u0003B\u0016!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001Z!\tQv,D\u0001\\\u0015\taV,\u0001\u0003mC:<'\"\u00010\u0002\t)\fg/Y\u0005\u0003An\u0013aa\u0015;sS:<\u0007b\u00022\u0001\u0003\u0003%\taY\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002IB\u0011Q\"Z\u0005\u0003M:\u00111!\u00138u\u0011\u001dA\u0007!!A\u0005\u0002%\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002%U\"91nZA\u0001\u0002\u0004!\u0017a\u0001=%c!9Q\u000eAA\u0001\n\u0003r\u0017a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003=\u00042\u0001]:%\u001b\u0005\t(B\u0001:\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003iF\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\bm\u0002\t\t\u0011\"\u0001x\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001=|!\ti\u00110\u0003\u0002{\u001d\t9!i\\8mK\u0006t\u0007bB6v\u0003\u0003\u0005\r\u0001\n\u0005\b{\u0002\t\t\u0011\"\u0011\u007f\u0003!A\u0017m\u001d5D_\u0012,G#\u00013\t\u0013\u0005\u0005\u0001!!A\u0005B\u0005\r\u0011\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003eC\u0011\"a\u0002\u0001\u0003\u0003%\t%!\u0003\u0002\r\u0015\fX/\u00197t)\rA\u00181\u0002\u0005\tW\u0006\u0015\u0011\u0011!a\u0001I\u001dQ\u0011q\u0002\u0002\u0002\u0002#\u0005A!!\u0005\u0002!QKW.Z*uC6\u0004X\r\u001a,bYV,\u0007cA\u001b\u0002\u0014\u0019I\u0011AAA\u0001\u0012\u0003!\u0011QC\n\u0005\u0003'aQ\u0003C\u00043\u0003'!\t!!\u0007\u0015\u0005\u0005E\u0001BCA\u0001\u0003'\t\t\u0011\"\u0012\u0002\u0004!Q\u0011qDA\n\u0003\u0003%\t)!\t\u0002\u000b\u0005\u0004\b\u000f\\=\u0016\t\u0005\r\u0012\u0011\u0006\u000b\u0007\u0003K\tY#!\f\u0011\tU\u0002\u0011q\u0005\t\u0004;\u0005%BAB\u0010\u0002\u001e\t\u0007\u0001\u0005C\u0004\u001a\u0003;\u0001\r!a\n\t\r)\ni\u00021\u0001-\u0011)\t\t$a\u0005\u0002\u0002\u0013\u0005\u00151G\u0001\bk:\f\u0007\u000f\u001d7z+\u0011\t)$!\u0012\u0015\t\u0005]\u0012q\t\t\u0006\u001b\u0005e\u0012QH\u0005\u0004\u0003wq!AB(qi&|g\u000e\u0005\u0004\u000e\u0003\u007f\t\u0019\u0005L\u0005\u0004\u0003\u0003r!A\u0002+va2,'\u0007E\u0002\u001e\u0003\u000b\"aaHA\u0018\u0005\u0004\u0001\u0003BCA%\u0003_\t\t\u00111\u0001\u0002L\u0005\u0019\u0001\u0010\n\u0019\u0011\tU\u0002\u00111\t\u0005\u000b\u0003\u001f\n\u0019\"!A\u0005\n\u0005E\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0015\u0011\u0007i\u000b)&C\u0002\u0002Xm\u0013aa\u00142kK\u000e$\b"
)
public class TimeStampedValue<V> implements Product, Serializable {
   private final V value;
   private final long timestamp;

   public static <V> Option<Tuple2<V, Object>> unapply(TimeStampedValue<V> var0) {
      return TimeStampedValue$.MODULE$.unapply(var0);
   }

   public static <V> TimeStampedValue<V> apply(V var0, long var1) {
      return TimeStampedValue$.MODULE$.<V>apply(var0, var1);
   }

   public V value() {
      return this.value;
   }

   public long timestamp() {
      return this.timestamp;
   }

   public <V> TimeStampedValue<V> copy(V value, long timestamp) {
      return new TimeStampedValue<V>(value, timestamp);
   }

   public <V> V copy$default$1() {
      return (V)this.value();
   }

   public <V> long copy$default$2() {
      return this.timestamp();
   }

   public String productPrefix() {
      return "TimeStampedValue";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.value();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.timestamp());
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
      return x$1 instanceof TimeStampedValue;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.value()));
      var1 = Statics.mix(var1, Statics.longHash(this.timestamp()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label50: {
            boolean var3;
            if (x$1 instanceof TimeStampedValue) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               TimeStampedValue var4 = (TimeStampedValue)x$1;
               if (BoxesRunTime.equals(this.value(), var4.value()) && this.timestamp() == var4.timestamp() && var4.canEqual(this)) {
                  break label50;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public TimeStampedValue(V value, long timestamp) {
      this.value = (V)value;
      this.timestamp = timestamp;
      Product$class.$init$(this);
   }
}
