package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005c\u0001B\u0001\u0003\u0001.\u0011!B\u0015#E\u00052|7m[%e\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019A1\u0002CA\u0007\u000f\u001b\u0005\u0011\u0011BA\b\u0003\u0005\u001d\u0011En\\2l\u0013\u0012\u0004\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0012/%\u0011\u0001D\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t5\u0001\u0011)\u001a!C\u00017\u0005)!\u000f\u001a3JIV\tA\u0004\u0005\u0002\u0012;%\u0011aD\u0005\u0002\u0004\u0013:$\b\u0002\u0003\u0011\u0001\u0005#\u0005\u000b\u0011\u0002\u000f\u0002\rI$G-\u00133!\u0011!\u0011\u0003A!f\u0001\n\u0003Y\u0012AC:qY&$\u0018J\u001c3fq\"AA\u0005\u0001B\tB\u0003%A$A\u0006ta2LG/\u00138eKb\u0004\u0003\"\u0002\u0014\u0001\t\u00039\u0013A\u0002\u001fj]&$h\bF\u0002)S)\u0002\"!\u0004\u0001\t\u000bi)\u0003\u0019\u0001\u000f\t\u000b\t*\u0003\u0019\u0001\u000f\t\u000b1\u0002A\u0011I\u0017\u0002\t9\fW.Z\u000b\u0002]A\u0011qF\r\b\u0003#AJ!!\r\n\u0002\rA\u0013X\rZ3g\u0013\t\u0019DG\u0001\u0004TiJLgn\u001a\u0006\u0003cIAqA\u000e\u0001\u0002\u0002\u0013\u0005q'\u0001\u0003d_BLHc\u0001\u00159s!9!$\u000eI\u0001\u0002\u0004a\u0002b\u0002\u00126!\u0003\u0005\r\u0001\b\u0005\bw\u0001\t\n\u0011\"\u0001=\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u0010\u0016\u00039yZ\u0013a\u0010\t\u0003\u0001\u0016k\u0011!\u0011\u0006\u0003\u0005\u000e\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0011\u0013\u0012AC1o]>$\u0018\r^5p]&\u0011a)\u0011\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002%\u0001#\u0003%\t\u0001P\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133\u0011\u001dQ\u0005!!A\u0005B-\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001'\u0011\u00055\u0013V\"\u0001(\u000b\u0005=\u0003\u0016\u0001\u00027b]\u001eT\u0011!U\u0001\u0005U\u00064\u0018-\u0003\u00024\u001d\"9A\u000bAA\u0001\n\u0003Y\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bb\u0002,\u0001\u0003\u0003%\taV\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tA6\f\u0005\u0002\u00123&\u0011!L\u0005\u0002\u0004\u0003:L\bb\u0002/V\u0003\u0003\u0005\r\u0001H\u0001\u0004q\u0012\n\u0004b\u00020\u0001\u0003\u0003%\teX\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0001\rE\u0002bIbk\u0011A\u0019\u0006\u0003GJ\t!bY8mY\u0016\u001cG/[8o\u0013\t)'M\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d9\u0007!!A\u0005\u0002!\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003S2\u0004\"!\u00056\n\u0005-\u0014\"a\u0002\"p_2,\u0017M\u001c\u0005\b9\u001a\f\t\u00111\u0001Y\u0011\u001dq\u0007!!A\u0005B=\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00029!9\u0011\u000fAA\u0001\n\u0003\u0012\u0018AB3rk\u0006d7\u000f\u0006\u0002jg\"9A\f]A\u0001\u0002\u0004A\u0006F\u0001\u0001v!\t1\b0D\u0001x\u0015\t!E!\u0003\u0002zo\naA)\u001a<fY>\u0004XM]!qS\u001e91PAA\u0001\u0012\u0003a\u0018A\u0003*E\t\ncwnY6JIB\u0011Q\" \u0004\b\u0003\t\t\t\u0011#\u0001\u007f'\rixP\u0006\t\b\u0003\u0003\t9\u0001\b\u000f)\u001b\t\t\u0019AC\u0002\u0002\u0006I\tqA];oi&lW-\u0003\u0003\u0002\n\u0005\r!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!1a% C\u0001\u0003\u001b!\u0012\u0001 \u0005\n\u0003#i\u0018\u0011!C#\u0003'\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002\u0019\"I\u0011qC?\u0002\u0002\u0013\u0005\u0015\u0011D\u0001\u0006CB\u0004H.\u001f\u000b\u0006Q\u0005m\u0011Q\u0004\u0005\u00075\u0005U\u0001\u0019\u0001\u000f\t\r\t\n)\u00021\u0001\u001d\u0011%\t\t#`A\u0001\n\u0003\u000b\u0019#A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005\u0015\u0012\u0011\u0007\t\u0006#\u0005\u001d\u00121F\u0005\u0004\u0003S\u0011\"AB(qi&|g\u000eE\u0003\u0012\u0003[aB$C\u0002\u00020I\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\u001a\u0003?\t\t\u00111\u0001)\u0003\rAH\u0005\r\u0005\n\u0003oi\u0018\u0011!C\u0005\u0003s\t1B]3bIJ+7o\u001c7wKR\u0011\u00111\b\t\u0004\u001b\u0006u\u0012bAA \u001d\n1qJ\u00196fGR\u0004"
)
public class RDDBlockId extends BlockId implements Product, Serializable {
   private final int rddId;
   private final int splitIndex;

   public static Option<Tuple2<Object, Object>> unapply(RDDBlockId var0) {
      return RDDBlockId$.MODULE$.unapply(var0);
   }

   public static RDDBlockId apply(int var0, int var1) {
      return RDDBlockId$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, Object>, RDDBlockId> tupled() {
      return RDDBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, RDDBlockId>> curried() {
      return RDDBlockId$.MODULE$.curried();
   }

   public int rddId() {
      return this.rddId;
   }

   public int splitIndex() {
      return this.splitIndex;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"rdd_").append((Object)BoxesRunTime.boxToInteger(this.rddId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.splitIndex())).toString();
   }

   public RDDBlockId copy(int rddId, int splitIndex) {
      return new RDDBlockId(rddId, splitIndex);
   }

   public int copy$default$1() {
      return this.rddId();
   }

   public int copy$default$2() {
      return this.splitIndex();
   }

   public String productPrefix() {
      return "RDDBlockId";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Integer var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.rddId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.splitIndex());
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
      return x$1 instanceof RDDBlockId;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.rddId());
      var1 = Statics.mix(var1, this.splitIndex());
      return Statics.finalizeHash(var1, 2);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label50: {
            boolean var3;
            if (x$1 instanceof RDDBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               RDDBlockId var4 = (RDDBlockId)x$1;
               if (this.rddId() == var4.rddId() && this.splitIndex() == var4.splitIndex() && var4.canEqual(this)) {
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

   public RDDBlockId(int rddId, int splitIndex) {
      this.rddId = rddId;
      this.splitIndex = splitIndex;
      Product$class.$init$(this);
   }
}
