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
   bytes = "\u0006\u0001\u0005Mc\u0001B\u0001\u0003\u0001.\u00111c\u00155vM\u001adW-\u00138eKb\u0014En\\2l\u0013\u0012T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\t\u0001a\u0001C\u0006\t\u0003\u001b9i\u0011AA\u0005\u0003\u001f\t\u0011qA\u00117pG.LE\r\u0005\u0002\u0012)5\t!CC\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)\"CA\u0004Qe>$Wo\u0019;\u0011\u0005E9\u0012B\u0001\r\u0013\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!Q\u0002A!f\u0001\n\u0003Y\u0012!C:ik\u001a4G.Z%e+\u0005a\u0002CA\t\u001e\u0013\tq\"CA\u0002J]RD\u0001\u0002\t\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\u000bg\",hM\u001a7f\u0013\u0012\u0004\u0003\u0002\u0003\u0012\u0001\u0005+\u0007I\u0011A\u000e\u0002\u000b5\f\u0007/\u00133\t\u0011\u0011\u0002!\u0011#Q\u0001\nq\ta!\\1q\u0013\u0012\u0004\u0003\u0002\u0003\u0014\u0001\u0005+\u0007I\u0011A\u000e\u0002\u0011I,G-^2f\u0013\u0012D\u0001\u0002\u000b\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\ne\u0016$WoY3JI\u0002BQA\u000b\u0001\u0005\u0002-\na\u0001P5oSRtD\u0003\u0002\u0017.]=\u0002\"!\u0004\u0001\t\u000biI\u0003\u0019\u0001\u000f\t\u000b\tJ\u0003\u0019\u0001\u000f\t\u000b\u0019J\u0003\u0019\u0001\u000f\t\u000bE\u0002A\u0011\t\u001a\u0002\t9\fW.Z\u000b\u0002gA\u0011Ag\u000e\b\u0003#UJ!A\u000e\n\u0002\rA\u0013X\rZ3g\u0013\tA\u0014H\u0001\u0004TiJLgn\u001a\u0006\u0003mIAqa\u000f\u0001\u0002\u0002\u0013\u0005A(\u0001\u0003d_BLH\u0003\u0002\u0017>}}BqA\u0007\u001e\u0011\u0002\u0003\u0007A\u0004C\u0004#uA\u0005\t\u0019\u0001\u000f\t\u000f\u0019R\u0004\u0013!a\u00019!9\u0011\tAI\u0001\n\u0003\u0011\u0015AD2paf$C-\u001a4bk2$H%M\u000b\u0002\u0007*\u0012A\u0004R\u0016\u0002\u000bB\u0011aiS\u0007\u0002\u000f*\u0011\u0001*S\u0001\nk:\u001c\u0007.Z2lK\u0012T!A\u0013\n\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002M\u000f\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f9\u0003\u0011\u0013!C\u0001\u0005\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012\u0004b\u0002)\u0001#\u0003%\tAQ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134\u0011\u001d\u0011\u0006!!A\u0005BM\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001+\u0011\u0005USV\"\u0001,\u000b\u0005]C\u0016\u0001\u00027b]\u001eT\u0011!W\u0001\u0005U\u00064\u0018-\u0003\u00029-\"9A\fAA\u0001\n\u0003Y\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bb\u00020\u0001\u0003\u0003%\taX\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\u00017\r\u0005\u0002\u0012C&\u0011!M\u0005\u0002\u0004\u0003:L\bb\u00023^\u0003\u0003\u0005\r\u0001H\u0001\u0004q\u0012\n\u0004b\u00024\u0001\u0003\u0003%\teZ\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0001\u000eE\u0002jY\u0002l\u0011A\u001b\u0006\u0003WJ\t!bY8mY\u0016\u001cG/[8o\u0013\ti'N\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dy\u0007!!A\u0005\u0002A\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003cR\u0004\"!\u0005:\n\u0005M\u0014\"a\u0002\"p_2,\u0017M\u001c\u0005\bI:\f\t\u00111\u0001a\u0011\u001d1\b!!A\u0005B]\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00029!9\u0011\u0010AA\u0001\n\u0003R\u0018AB3rk\u0006d7\u000f\u0006\u0002rw\"9A\r_A\u0001\u0002\u0004\u0001\u0007F\u0001\u0001~!\rq\u0018\u0011A\u0007\u0002\u007f*\u0011!\nB\u0005\u0004\u0003\u0007y(\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!CA\u0004\u0005\u0005\u0005\t\u0012AA\u0005\u0003M\u0019\u0006.\u001e4gY\u0016Le\u000eZ3y\u00052|7m[%e!\ri\u00111\u0002\u0004\t\u0003\t\t\t\u0011#\u0001\u0002\u000eM)\u00111BA\b-AA\u0011\u0011CA\f9qaB&\u0004\u0002\u0002\u0014)\u0019\u0011Q\u0003\n\u0002\u000fI,h\u000e^5nK&!\u0011\u0011DA\n\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gn\r\u0005\bU\u0005-A\u0011AA\u000f)\t\tI\u0001\u0003\u0006\u0002\"\u0005-\u0011\u0011!C#\u0003G\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002)\"Q\u0011qEA\u0006\u0003\u0003%\t)!\u000b\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000f1\nY#!\f\u00020!1!$!\nA\u0002qAaAIA\u0013\u0001\u0004a\u0002B\u0002\u0014\u0002&\u0001\u0007A\u0004\u0003\u0006\u00024\u0005-\u0011\u0011!CA\u0003k\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u00028\u0005\r\u0003#B\t\u0002:\u0005u\u0012bAA\u001e%\t1q\n\u001d;j_:\u0004b!EA 9qa\u0012bAA!%\t1A+\u001e9mKNB\u0011\"!\u0012\u00022\u0005\u0005\t\u0019\u0001\u0017\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002J\u0005-\u0011\u0011!C\u0005\u0003\u0017\n1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\n\t\u0004+\u0006=\u0013bAA)-\n1qJ\u00196fGR\u0004"
)
public class ShuffleIndexBlockId extends BlockId implements Product, Serializable {
   private final int shuffleId;
   private final int mapId;
   private final int reduceId;

   public static Option<Tuple3<Object, Object, Object>> unapply(ShuffleIndexBlockId var0) {
      return ShuffleIndexBlockId$.MODULE$.unapply(var0);
   }

   public static ShuffleIndexBlockId apply(int var0, int var1, int var2) {
      return ShuffleIndexBlockId$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, Object>, ShuffleIndexBlockId> tupled() {
      return ShuffleIndexBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Object, ShuffleIndexBlockId>>> curried() {
      return ShuffleIndexBlockId$.MODULE$.curried();
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
      return (new StringBuilder()).append((Object)"shuffle_").append((Object)BoxesRunTime.boxToInteger(this.shuffleId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.mapId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.reduceId())).append((Object)".index").toString();
   }

   public ShuffleIndexBlockId copy(int shuffleId, int mapId, int reduceId) {
      return new ShuffleIndexBlockId(shuffleId, mapId, reduceId);
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
      return "ShuffleIndexBlockId";
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
      return x$1 instanceof ShuffleIndexBlockId;
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
            if (x$1 instanceof ShuffleIndexBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               ShuffleIndexBlockId var4 = (ShuffleIndexBlockId)x$1;
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

   public ShuffleIndexBlockId(int shuffleId, int mapId, int reduceId) {
      this.shuffleId = shuffleId;
      this.mapId = mapId;
      this.reduceId = reduceId;
      Product$class.$init$(this);
   }
}
