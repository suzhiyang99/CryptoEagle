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
   bytes = "\u0006\u0001\u0005Mc\u0001B\u0001\u0003\u0001.\u0011!c\u00155vM\u001adW\rR1uC\ncwnY6JI*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0005\u00011\u0001b\u0003\u0005\u0002\u000e\u001d5\t!!\u0003\u0002\u0010\u0005\t9!\t\\8dW&#\u0007CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"a\u0002)s_\u0012,8\r\u001e\t\u0003#]I!\u0001\u0007\n\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002m\t\u0011b\u001d5vM\u001adW-\u00133\u0016\u0003q\u0001\"!E\u000f\n\u0005y\u0011\"aA%oi\"A\u0001\u0005\u0001B\tB\u0003%A$\u0001\u0006tQV4g\r\\3JI\u0002B\u0001B\t\u0001\u0003\u0016\u0004%\taG\u0001\u0006[\u0006\u0004\u0018\n\u001a\u0005\tI\u0001\u0011\t\u0012)A\u00059\u00051Q.\u00199JI\u0002B\u0001B\n\u0001\u0003\u0016\u0004%\taG\u0001\te\u0016$WoY3JI\"A\u0001\u0006\u0001B\tB\u0003%A$A\u0005sK\u0012,8-Z%eA!)!\u0006\u0001C\u0001W\u00051A(\u001b8jiz\"B\u0001L\u0017/_A\u0011Q\u0002\u0001\u0005\u00065%\u0002\r\u0001\b\u0005\u0006E%\u0002\r\u0001\b\u0005\u0006M%\u0002\r\u0001\b\u0005\u0006c\u0001!\tEM\u0001\u0005]\u0006lW-F\u00014!\t!tG\u0004\u0002\u0012k%\u0011aGE\u0001\u0007!J,G-\u001a4\n\u0005aJ$AB*ue&twM\u0003\u00027%!91\bAA\u0001\n\u0003a\u0014\u0001B2paf$B\u0001L\u001f?\u007f!9!D\u000fI\u0001\u0002\u0004a\u0002b\u0002\u0012;!\u0003\u0005\r\u0001\b\u0005\bMi\u0002\n\u00111\u0001\u001d\u0011\u001d\t\u0005!%A\u0005\u0002\t\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001DU\taBiK\u0001F!\t15*D\u0001H\u0015\tA\u0015*A\u0005v]\u000eDWmY6fI*\u0011!JE\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001'H\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b\u001d\u0002\t\n\u0011\"\u0001C\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIIBq\u0001\u0015\u0001\u0012\u0002\u0013\u0005!)\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\t\u000fI\u0003\u0011\u0011!C!'\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012\u0001\u0016\t\u0003+jk\u0011A\u0016\u0006\u0003/b\u000bA\u0001\\1oO*\t\u0011,\u0001\u0003kCZ\f\u0017B\u0001\u001dW\u0011\u001da\u0006!!A\u0005\u0002m\tA\u0002\u001d:pIV\u001cG/\u0011:jifDqA\u0018\u0001\u0002\u0002\u0013\u0005q,\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005\u0001\u001c\u0007CA\tb\u0013\t\u0011'CA\u0002B]fDq\u0001Z/\u0002\u0002\u0003\u0007A$A\u0002yIEBqA\u001a\u0001\u0002\u0002\u0013\u0005s-A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005A\u0007cA5mA6\t!N\u0003\u0002l%\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u00055T'\u0001C%uKJ\fGo\u001c:\t\u000f=\u0004\u0011\u0011!C\u0001a\u0006A1-\u00198FcV\fG\u000e\u0006\u0002riB\u0011\u0011C]\u0005\u0003gJ\u0011qAQ8pY\u0016\fg\u000eC\u0004e]\u0006\u0005\t\u0019\u00011\t\u000fY\u0004\u0011\u0011!C!o\u0006A\u0001.Y:i\u0007>$W\rF\u0001\u001d\u0011\u001dI\b!!A\u0005Bi\fa!Z9vC2\u001cHCA9|\u0011\u001d!\u00070!AA\u0002\u0001D#\u0001A?\u0011\u0007y\f\t!D\u0001\u0000\u0015\tQE!C\u0002\u0002\u0004}\u0014A\u0002R3wK2|\u0007/\u001a:Ba&<\u0011\"a\u0002\u0003\u0003\u0003E\t!!\u0003\u0002%MCWO\u001a4mK\u0012\u000bG/\u0019\"m_\u000e\\\u0017\n\u001a\t\u0004\u001b\u0005-a\u0001C\u0001\u0003\u0003\u0003E\t!!\u0004\u0014\u000b\u0005-\u0011q\u0002\f\u0011\u0011\u0005E\u0011q\u0003\u000f\u001d91j!!a\u0005\u000b\u0007\u0005U!#A\u0004sk:$\u0018.\\3\n\t\u0005e\u00111\u0003\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u001c\u0004b\u0002\u0016\u0002\f\u0011\u0005\u0011Q\u0004\u000b\u0003\u0003\u0013A!\"!\t\u0002\f\u0005\u0005IQIA\u0012\u0003!!xn\u0015;sS:<G#\u0001+\t\u0015\u0005\u001d\u00121BA\u0001\n\u0003\u000bI#A\u0003baBd\u0017\u0010F\u0004-\u0003W\ti#a\f\t\ri\t)\u00031\u0001\u001d\u0011\u0019\u0011\u0013Q\u0005a\u00019!1a%!\nA\u0002qA!\"a\r\u0002\f\u0005\u0005I\u0011QA\u001b\u0003\u001d)h.\u00199qYf$B!a\u000e\u0002DA)\u0011#!\u000f\u0002>%\u0019\u00111\b\n\u0003\r=\u0003H/[8o!\u0019\t\u0012q\b\u000f\u001d9%\u0019\u0011\u0011\t\n\u0003\rQ+\b\u000f\\34\u0011%\t)%!\r\u0002\u0002\u0003\u0007A&A\u0002yIAB!\"!\u0013\u0002\f\u0005\u0005I\u0011BA&\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u00055\u0003cA+\u0002P%\u0019\u0011\u0011\u000b,\u0003\r=\u0013'.Z2u\u0001"
)
public class ShuffleDataBlockId extends BlockId implements Product, Serializable {
   private final int shuffleId;
   private final int mapId;
   private final int reduceId;

   public static Option<Tuple3<Object, Object, Object>> unapply(ShuffleDataBlockId var0) {
      return ShuffleDataBlockId$.MODULE$.unapply(var0);
   }

   public static ShuffleDataBlockId apply(int var0, int var1, int var2) {
      return ShuffleDataBlockId$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, Object>, ShuffleDataBlockId> tupled() {
      return ShuffleDataBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Object, ShuffleDataBlockId>>> curried() {
      return ShuffleDataBlockId$.MODULE$.curried();
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
      return (new StringBuilder()).append((Object)"shuffle_").append((Object)BoxesRunTime.boxToInteger(this.shuffleId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.mapId())).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(this.reduceId())).append((Object)".data").toString();
   }

   public ShuffleDataBlockId copy(int shuffleId, int mapId, int reduceId) {
      return new ShuffleDataBlockId(shuffleId, mapId, reduceId);
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
      return "ShuffleDataBlockId";
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
      return x$1 instanceof ShuffleDataBlockId;
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
            if (x$1 instanceof ShuffleDataBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               ShuffleDataBlockId var4 = (ShuffleDataBlockId)x$1;
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

   public ShuffleDataBlockId(int shuffleId, int mapId, int reduceId) {
      this.shuffleId = shuffleId;
      this.mapId = mapId;
      this.reduceId = reduceId;
      Product$class.$init$(this);
   }
}
