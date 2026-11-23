package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mc\u0001B\u0001\u0003\u0001.\u0011ab\u00155vM\u001adWM\u00117pG.LEM\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\t\u0017!\tia\"D\u0001\u0003\u0013\ty!AA\u0004CY>\u001c7.\u00133\u0011\u0005E!R\"\u0001\n\u000b\u0003M\tQa]2bY\u0006L!!\u0006\n\u0003\u000fA\u0013x\u000eZ;diB\u0011\u0011cF\u0005\u00031I\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u0007\u0001\u0003\u0016\u0004%\taG\u0001\ng\",hM\u001a7f\u0013\u0012,\u0012\u0001\b\t\u0003#uI!A\b\n\u0003\u0007%sG\u000f\u0003\u0005!\u0001\tE\t\u0015!\u0003\u001d\u0003)\u0019\b.\u001e4gY\u0016LE\r\t\u0005\tE\u0001\u0011)\u001a!C\u00017\u0005)Q.\u00199JI\"AA\u0005\u0001B\tB\u0003%A$\u0001\u0004nCBLE\r\t\u0005\tM\u0001\u0011)\u001a!C\u00017\u0005A!/\u001a3vG\u0016LE\r\u0003\u0005)\u0001\tE\t\u0015!\u0003\u001d\u0003%\u0011X\rZ;dK&#\u0007\u0005C\u0003+\u0001\u0011\u00051&\u0001\u0004=S:LGO\u0010\u000b\u0005Y5rs\u0006\u0005\u0002\u000e\u0001!)!$\u000ba\u00019!)!%\u000ba\u00019!)a%\u000ba\u00019!)\u0011\u0007\u0001C!e\u0005!a.Y7f+\u0005\u0019\u0004C\u0001\u001b8\u001d\t\tR'\u0003\u00027%\u00051\u0001K]3eK\u001aL!\u0001O\u001d\u0003\rM#(/\u001b8h\u0015\t1$\u0003C\u0004<\u0001\u0005\u0005I\u0011\u0001\u001f\u0002\t\r|\u0007/\u001f\u000b\u0005Yurt\bC\u0004\u001buA\u0005\t\u0019\u0001\u000f\t\u000f\tR\u0004\u0013!a\u00019!9aE\u000fI\u0001\u0002\u0004a\u0002bB!\u0001#\u0003%\tAQ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0019%F\u0001\u000fEW\u0005)\u0005C\u0001$L\u001b\u00059%B\u0001%J\u0003%)hn\u00195fG.,GM\u0003\u0002K%\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00051;%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9a\nAI\u0001\n\u0003\u0011\u0015AD2paf$C-\u001a4bk2$HE\r\u0005\b!\u0002\t\n\u0011\"\u0001C\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBqA\u0015\u0001\u0002\u0002\u0013\u00053+A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002)B\u0011QKW\u0007\u0002-*\u0011q\u000bW\u0001\u0005Y\u0006twMC\u0001Z\u0003\u0011Q\u0017M^1\n\u0005a2\u0006b\u0002/\u0001\u0003\u0003%\taG\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\b=\u0002\t\t\u0011\"\u0001`\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001Y2\u0011\u0005E\t\u0017B\u00012\u0013\u0005\r\te.\u001f\u0005\bIv\u000b\t\u00111\u0001\u001d\u0003\rAH%\r\u0005\bM\u0002\t\t\u0011\"\u0011h\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00015\u0011\u0007%d\u0007-D\u0001k\u0015\tY'#\u0001\u0006d_2dWm\u0019;j_:L!!\u001c6\u0003\u0011%#XM]1u_JDqa\u001c\u0001\u0002\u0002\u0013\u0005\u0001/\u0001\u0005dC:,\u0015/^1m)\t\tH\u000f\u0005\u0002\u0012e&\u00111O\u0005\u0002\b\u0005>|G.Z1o\u0011\u001d!g.!AA\u0002\u0001DqA\u001e\u0001\u0002\u0002\u0013\u0005s/\u0001\u0005iCND7i\u001c3f)\u0005a\u0002bB=\u0001\u0003\u0003%\tE_\u0001\u0007KF,\u0018\r\\:\u0015\u0005E\\\bb\u00023y\u0003\u0003\u0005\r\u0001\u0019\u0015\u0003\u0001u\u00042A`A\u0001\u001b\u0005y(B\u0001&\u0005\u0013\r\t\u0019a \u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\n\u0003\u000f\u0011\u0011\u0011!E\u0001\u0003\u0013\tab\u00155vM\u001adWM\u00117pG.LE\rE\u0002\u000e\u0003\u00171\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011QB\n\u0006\u0003\u0017\tyA\u0006\t\t\u0003#\t9\u0002\b\u000f\u001dY5\u0011\u00111\u0003\u0006\u0004\u0003+\u0011\u0012a\u0002:v]RLW.Z\u0005\u0005\u00033\t\u0019BA\tBEN$(/Y2u\rVt7\r^5p]NBqAKA\u0006\t\u0003\ti\u0002\u0006\u0002\u0002\n!Q\u0011\u0011EA\u0006\u0003\u0003%)%a\t\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u0016\u0005\u000b\u0003O\tY!!A\u0005\u0002\u0006%\u0012!B1qa2LHc\u0002\u0017\u0002,\u00055\u0012q\u0006\u0005\u00075\u0005\u0015\u0002\u0019\u0001\u000f\t\r\t\n)\u00031\u0001\u001d\u0011\u00191\u0013Q\u0005a\u00019!Q\u00111GA\u0006\u0003\u0003%\t)!\u000e\u0002\u000fUt\u0017\r\u001d9msR!\u0011qGA\"!\u0015\t\u0012\u0011HA\u001f\u0013\r\tYD\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\rE\ty\u0004\b\u000f\u001d\u0013\r\t\tE\u0005\u0002\u0007)V\u0004H.Z\u001a\t\u0013\u0005\u0015\u0013\u0011GA\u0001\u0002\u0004a\u0013a\u0001=%a!Q\u0011\u0011JA\u0006\u0003\u0003%I!a\u0013\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u001b\u00022!VA(\u0013\r\t\tF\u0016\u0002\u0007\u001f\nTWm\u0019;"
)
public class ShuffleBlockId extends BlockId implements Product, Serializable {
   private final int shuffleId;
   private final int mapId;
   private final int reduceId;

   public static Option<Tuple3<Object, Object, Object>> unapply(ShuffleBlockId var0) {
      return ShuffleBlockId$.MODULE$.unapply(var0);
   }

   public static ShuffleBlockId apply(int var0, int var1, int var2) {
      return ShuffleBlockId$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, Object>, ShuffleBlockId> tupled() {
      return ShuffleBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Object, ShuffleBlockId>>> curried() {
      return ShuffleBlockId$.MODULE$.curried();
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public int mapId() {
      return this.mapId;
   }

   public int reduceId() {
      return this.reduceId;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"shuffle_").append((Object)BoxesRunTime.boxToInteger(this.shuffleId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.mapId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.reduceId())).toString();
   }

   public ShuffleBlockId copy(int shuffleId, int mapId, int reduceId) {
      return new ShuffleBlockId(shuffleId, mapId, reduceId);
   }

   public int copy$default$1() {
      return this.shuffleId();
   }

   public int copy$default$2() {
      return this.mapId();
   }

   public int copy$default$3() {
      return this.reduceId();
   }

   public String productPrefix() {
      return "ShuffleBlockId";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Integer var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.shuffleId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.mapId());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.reduceId());
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
      return x$1 instanceof ShuffleBlockId;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.shuffleId());
      var1 = Statics.mix(var1, this.mapId());
      var1 = Statics.mix(var1, this.reduceId());
      return Statics.finalizeHash(var1, 3);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof ShuffleBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               ShuffleBlockId var4 = (ShuffleBlockId)x$1;
               if (this.shuffleId() == var4.shuffleId() && this.mapId() == var4.mapId() && this.reduceId() == var4.reduceId() && var4.canEqual(this)) {
                  break label52;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public ShuffleBlockId(int shuffleId, int mapId, int reduceId) {
      this.shuffleId = shuffleId;
      this.mapId = mapId;
      this.reduceId = reduceId;
      Product$class.$init$(this);
   }
}
