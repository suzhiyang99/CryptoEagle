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
   bytes = "\u0006\u0001\u00055c\u0001B\u0001\u0003\u0001.\u0011Qb\u0015;sK\u0006l'\t\\8dW&#'BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0011\u0001A\u0002\u0005\f\u0011\u00055qQ\"\u0001\u0002\n\u0005=\u0011!a\u0002\"m_\u000e\\\u0017\n\u001a\t\u0003#Qi\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\b!J|G-^2u!\t\tr#\u0003\u0002\u0019%\ta1+\u001a:jC2L'0\u00192mK\"A!\u0004\u0001BK\u0002\u0013\u00051$\u0001\u0005tiJ,\u0017-\\%e+\u0005a\u0002CA\t\u001e\u0013\tq\"CA\u0002J]RD\u0001\u0002\t\u0001\u0003\u0012\u0003\u0006I\u0001H\u0001\ngR\u0014X-Y7JI\u0002B\u0001B\t\u0001\u0003\u0016\u0004%\taI\u0001\tk:L\u0017/^3JIV\tA\u0005\u0005\u0002\u0012K%\u0011aE\u0005\u0002\u0005\u0019>tw\r\u0003\u0005)\u0001\tE\t\u0015!\u0003%\u0003%)h.[9vK&#\u0007\u0005C\u0003+\u0001\u0011\u00051&\u0001\u0004=S:LGO\u0010\u000b\u0004Y5r\u0003CA\u0007\u0001\u0011\u0015Q\u0012\u00061\u0001\u001d\u0011\u0015\u0011\u0013\u00061\u0001%\u0011\u0015\u0001\u0004\u0001\"\u00112\u0003\u0011q\u0017-\\3\u0016\u0003I\u0002\"a\r\u001c\u000f\u0005E!\u0014BA\u001b\u0013\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0007\u000f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005U\u0012\u0002b\u0002\u001e\u0001\u0003\u0003%\taO\u0001\u0005G>\u0004\u0018\u0010F\u0002-yuBqAG\u001d\u0011\u0002\u0003\u0007A\u0004C\u0004#sA\u0005\t\u0019\u0001\u0013\t\u000f}\u0002\u0011\u0013!C\u0001\u0001\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A!+\u0005q\u00115&A\"\u0011\u0005\u0011KU\"A#\u000b\u0005\u0019;\u0015!C;oG\",7m[3e\u0015\tA%#\u0001\u0006b]:|G/\u0019;j_:L!AS#\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004M\u0001E\u0005I\u0011A'\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\taJ\u000b\u0002%\u0005\"9\u0001\u000bAA\u0001\n\u0003\n\u0016!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001S!\t\u0019\u0006,D\u0001U\u0015\t)f+\u0001\u0003mC:<'\"A,\u0002\t)\fg/Y\u0005\u0003oQCqA\u0017\u0001\u0002\u0002\u0013\u00051$\u0001\u0007qe>$Wo\u0019;Be&$\u0018\u0010C\u0004]\u0001\u0005\u0005I\u0011A/\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011a,\u0019\t\u0003#}K!\u0001\u0019\n\u0003\u0007\u0005s\u0017\u0010C\u0004c7\u0006\u0005\t\u0019\u0001\u000f\u0002\u0007a$\u0013\u0007C\u0004e\u0001\u0005\u0005I\u0011I3\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012A\u001a\t\u0004O*tV\"\u00015\u000b\u0005%\u0014\u0012AC2pY2,7\r^5p]&\u00111\u000e\u001b\u0002\t\u0013R,'/\u0019;pe\"9Q\u000eAA\u0001\n\u0003q\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005=\u0014\bCA\tq\u0013\t\t(CA\u0004C_>dW-\u00198\t\u000f\td\u0017\u0011!a\u0001=\"9A\u000fAA\u0001\n\u0003*\u0018\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003qAqa\u001e\u0001\u0002\u0002\u0013\u0005\u00030\u0001\u0004fcV\fGn\u001d\u000b\u0003_fDqA\u0019<\u0002\u0002\u0003\u0007a\f\u000b\u0002\u0001wB\u0011AP`\u0007\u0002{*\u0011\u0001\nB\u0005\u0003\u007fv\u0014A\u0002R3wK2|\u0007/\u001a:Ba&<\u0011\"a\u0001\u0003\u0003\u0003E\t!!\u0002\u0002\u001bM#(/Z1n\u00052|7m[%e!\ri\u0011q\u0001\u0004\t\u0003\t\t\t\u0011#\u0001\u0002\nM)\u0011qAA\u0006-A9\u0011QBA\n9\u0011bSBAA\b\u0015\r\t\tBE\u0001\beVtG/[7f\u0013\u0011\t)\"a\u0004\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t'\u0007C\u0004+\u0003\u000f!\t!!\u0007\u0015\u0005\u0005\u0015\u0001BCA\u000f\u0003\u000f\t\t\u0011\"\u0012\u0002 \u0005AAo\\*ue&tw\rF\u0001S\u0011)\t\u0019#a\u0002\u0002\u0002\u0013\u0005\u0015QE\u0001\u0006CB\u0004H.\u001f\u000b\u0006Y\u0005\u001d\u0012\u0011\u0006\u0005\u00075\u0005\u0005\u0002\u0019\u0001\u000f\t\r\t\n\t\u00031\u0001%\u0011)\ti#a\u0002\u0002\u0002\u0013\u0005\u0015qF\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t\t$!\u0010\u0011\u000bE\t\u0019$a\u000e\n\u0007\u0005U\"C\u0001\u0004PaRLwN\u001c\t\u0006#\u0005eB\u0004J\u0005\u0004\u0003w\u0011\"A\u0002+va2,'\u0007C\u0005\u0002@\u0005-\u0012\u0011!a\u0001Y\u0005\u0019\u0001\u0010\n\u0019\t\u0015\u0005\r\u0013qAA\u0001\n\u0013\t)%A\u0006sK\u0006$'+Z:pYZ,GCAA$!\r\u0019\u0016\u0011J\u0005\u0004\u0003\u0017\"&AB(cU\u0016\u001cG\u000f"
)
public class StreamBlockId extends BlockId implements Product, Serializable {
   private final int streamId;
   private final long uniqueId;

   public static Option<Tuple2<Object, Object>> unapply(StreamBlockId var0) {
      return StreamBlockId$.MODULE$.unapply(var0);
   }

   public static StreamBlockId apply(int var0, long var1) {
      return StreamBlockId$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, Object>, StreamBlockId> tupled() {
      return StreamBlockId$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, StreamBlockId>> curried() {
      return StreamBlockId$.MODULE$.curried();
   }

   public int streamId() {
      return this.streamId;
   }

   public long uniqueId() {
      return this.uniqueId;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"input-").append((Object)BoxesRunTime.boxToInteger(this.streamId())).append((Object)"-").append((Object)BoxesRunTime.boxToLong(this.uniqueId())).toString();
   }

   public StreamBlockId copy(int streamId, long uniqueId) {
      return new StreamBlockId(streamId, uniqueId);
   }

   public int copy$default$1() {
      return this.streamId();
   }

   public long copy$default$2() {
      return this.uniqueId();
   }

   public String productPrefix() {
      return "StreamBlockId";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.streamId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.uniqueId());
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
      return x$1 instanceof StreamBlockId;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.streamId());
      var1 = Statics.mix(var1, Statics.longHash(this.uniqueId()));
      return Statics.finalizeHash(var1, 2);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label50: {
            boolean var3;
            if (x$1 instanceof StreamBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               StreamBlockId var4 = (StreamBlockId)x$1;
               if (this.streamId() == var4.streamId() && this.uniqueId() == var4.uniqueId() && var4.canEqual(this)) {
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

   public StreamBlockId(int streamId, long uniqueId) {
      this.streamId = streamId;
      this.uniqueId = uniqueId;
      Product$class.$init$(this);
   }
}
