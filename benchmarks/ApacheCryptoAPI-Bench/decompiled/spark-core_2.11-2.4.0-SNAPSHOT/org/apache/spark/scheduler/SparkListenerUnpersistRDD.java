package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a\u0001B\u0001\u0003\u0001.\u0011\u0011d\u00159be.d\u0015n\u001d;f]\u0016\u0014XK\u001c9feNL7\u000f\u001e*E\t*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0015\u0001AB\u0005\f\u001a!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111\u0003F\u0007\u0002\u0005%\u0011QC\u0001\u0002\u0013'B\f'o\u001b'jgR,g.\u001a:Fm\u0016tG\u000f\u0005\u0002\u000e/%\u0011\u0001D\u0004\u0002\b!J|G-^2u!\ti!$\u0003\u0002\u001c\u001d\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$A\u0003sI\u0012LE-F\u0001 !\ti\u0001%\u0003\u0002\"\u001d\t\u0019\u0011J\u001c;\t\u0011\r\u0002!\u0011#Q\u0001\n}\taA\u001d3e\u0013\u0012\u0004\u0003\"B\u0013\u0001\t\u00031\u0013A\u0002\u001fj]&$h\b\u0006\u0002(QA\u00111\u0003\u0001\u0005\u0006;\u0011\u0002\ra\b\u0005\bU\u0001\t\t\u0011\"\u0001,\u0003\u0011\u0019w\u000e]=\u0015\u0005\u001db\u0003bB\u000f*!\u0003\u0005\ra\b\u0005\b]\u0001\t\n\u0011\"\u00010\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012\u0001\r\u0016\u0003?EZ\u0013A\r\t\u0003gaj\u0011\u0001\u000e\u0006\u0003kY\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005]r\u0011AC1o]>$\u0018\r^5p]&\u0011\u0011\b\u000e\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB\u001e\u0001\u0003\u0003%\t\u0005P\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003u\u0002\"AP\"\u000e\u0003}R!\u0001Q!\u0002\t1\fgn\u001a\u0006\u0002\u0005\u0006!!.\u0019<b\u0013\t!uH\u0001\u0004TiJLgn\u001a\u0005\b\r\u0002\t\t\u0011\"\u0001\u001f\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011\u001dA\u0005!!A\u0005\u0002%\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002K\u001bB\u0011QbS\u0005\u0003\u0019:\u00111!\u00118z\u0011\u001dqu)!AA\u0002}\t1\u0001\u001f\u00132\u0011\u001d\u0001\u0006!!A\u0005BE\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002%B\u00191K\u0016&\u000e\u0003QS!!\u0016\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002X)\nA\u0011\n^3sCR|'\u000fC\u0004Z\u0001\u0005\u0005I\u0011\u0001.\u0002\u0011\r\fg.R9vC2$\"a\u00170\u0011\u00055a\u0016BA/\u000f\u0005\u001d\u0011un\u001c7fC:DqA\u0014-\u0002\u0002\u0003\u0007!\nC\u0004a\u0001\u0005\u0005I\u0011I1\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\b\u0005\bG\u0002\t\t\u0011\"\u0011e\u0003!!xn\u0015;sS:<G#A\u001f\t\u000f\u0019\u0004\u0011\u0011!C!O\u00061Q-];bYN$\"a\u00175\t\u000f9+\u0017\u0011!a\u0001\u0015\"\u0012\u0001A\u001b\t\u0003W6l\u0011\u0001\u001c\u0006\u0003o\u0011I!A\u001c7\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\b\u000fA\u0014\u0011\u0011!E\u0001c\u0006I2\u000b]1sW2K7\u000f^3oKJ,f\u000e]3sg&\u001cHO\u0015#E!\t\u0019\"OB\u0004\u0002\u0005\u0005\u0005\t\u0012A:\u0014\u0007I$\u0018\u0004\u0005\u0003vq~9S\"\u0001<\u000b\u0005]t\u0011a\u0002:v]RLW.Z\u0005\u0003sZ\u0014\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u0015)#\u000f\"\u0001|)\u0005\t\bbB2s\u0003\u0003%)\u0005\u001a\u0005\b}J\f\t\u0011\"!\u0000\u0003\u0015\t\u0007\u000f\u001d7z)\r9\u0013\u0011\u0001\u0005\u0006;u\u0004\ra\b\u0005\n\u0003\u000b\u0011\u0018\u0011!CA\u0003\u000f\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\n\u0005=\u0001\u0003B\u0007\u0002\f}I1!!\u0004\u000f\u0005\u0019y\u0005\u000f^5p]\"I\u0011\u0011CA\u0002\u0003\u0003\u0005\raJ\u0001\u0004q\u0012\u0002\u0004\"CA\u000be\u0006\u0005I\u0011BA\f\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005e\u0001c\u0001 \u0002\u001c%\u0019\u0011QD \u0003\r=\u0013'.Z2u\u0001"
)
public class SparkListenerUnpersistRDD implements SparkListenerEvent, Product, Serializable {
   private final int rddId;

   public static Option<Object> unapply(SparkListenerUnpersistRDD var0) {
      return SparkListenerUnpersistRDD$.MODULE$.unapply(var0);
   }

   public static SparkListenerUnpersistRDD apply(int var0) {
      return SparkListenerUnpersistRDD$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<SparkListenerUnpersistRDD, A> var0) {
      return SparkListenerUnpersistRDD$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerUnpersistRDD> compose(Function1<A, Object> var0) {
      return SparkListenerUnpersistRDD$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public int rddId() {
      return this.rddId;
   }

   public SparkListenerUnpersistRDD copy(int rddId) {
      return new SparkListenerUnpersistRDD(rddId);
   }

   public int copy$default$1() {
      return this.rddId();
   }

   public String productPrefix() {
      return "SparkListenerUnpersistRDD";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.rddId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerUnpersistRDD;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.rddId());
      return Statics.finalizeHash(var1, 1);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof SparkListenerUnpersistRDD) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               SparkListenerUnpersistRDD var4 = (SparkListenerUnpersistRDD)x$1;
               if (this.rddId() == var4.rddId() && var4.canEqual(this)) {
                  break label48;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public SparkListenerUnpersistRDD(int rddId) {
      this.rddId = rddId;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
