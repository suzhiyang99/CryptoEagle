package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rb!B\u0001\u0003\u0001\u0012Q!!\u0006%E\rN\u001b\u0015m\u00195f)\u0006\u001c8\u000eT8dCRLwN\u001c\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cR\u0001A\u0006\u0012+a\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0014\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u00051!\u0016m]6M_\u000e\fG/[8o!\taa#\u0003\u0002\u0018\u001b\t9\u0001K]8ek\u000e$\bC\u0001\u0007\u001a\u0013\tQRB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001d\u0001\tU\r\u0011\"\u0011\u001f\u0003\u0011Awn\u001d;\u0004\u0001U\tq\u0004\u0005\u0002!G9\u0011A\"I\u0005\u0003E5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0013&\u0005\u0019\u0019FO]5oO*\u0011!%\u0004\u0005\tO\u0001\u0011\t\u0012)A\u0005?\u0005)\u0001n\\:uA!)\u0011\u0006\u0001C\u0001U\u00051A(\u001b8jiz\"\"a\u000b\u0017\u0011\u0005I\u0001\u0001\"\u0002\u000f)\u0001\u0004y\u0002\"\u0002\u0018\u0001\t\u0003z\u0013\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003}Aq!\r\u0001\u0002\u0002\u0013\u0005!'\u0001\u0003d_BLHCA\u00164\u0011\u001da\u0002\u0007%AA\u0002}Aq!\u000e\u0001\u0012\u0002\u0013\u0005a'\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003]R#a\b\u001d,\u0003e\u0002\"AO \u000e\u0003mR!\u0001P\u001f\u0002\u0013Ut7\r[3dW\u0016$'B\u0001 \u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u0001n\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001d\u0011\u0005!!A\u0005B\r\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001#\u0011\u0005\u0015SU\"\u0001$\u000b\u0005\u001dC\u0015\u0001\u00027b]\u001eT\u0011!S\u0001\u0005U\u00064\u0018-\u0003\u0002%\r\"9A\nAA\u0001\n\u0003i\u0015\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001(\u0011\u00051y\u0015B\u0001)\u000e\u0005\rIe\u000e\u001e\u0005\b%\u0002\t\t\u0011\"\u0001T\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001V,\u0011\u00051)\u0016B\u0001,\u000e\u0005\r\te.\u001f\u0005\b1F\u000b\t\u00111\u0001O\u0003\rAH%\r\u0005\b5\u0002\t\t\u0011\"\u0011\\\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u0001/\u0011\u0007u\u0003G+D\u0001_\u0015\tyV\"\u0001\u0006d_2dWm\u0019;j_:L!!\u00190\u0003\u0011%#XM]1u_JDqa\u0019\u0001\u0002\u0002\u0013\u0005A-\u0001\u0005dC:,\u0015/^1m)\t)\u0007\u000e\u0005\u0002\rM&\u0011q-\u0004\u0002\b\u0005>|G.Z1o\u0011\u001dA&-!AA\u0002QCqA\u001b\u0001\u0002\u0002\u0013\u00053.\u0001\u0005iCND7i\u001c3f)\u0005q\u0005bB7\u0001\u0003\u0003%\tE\\\u0001\u0007KF,\u0018\r\\:\u0015\u0005\u0015|\u0007b\u0002-m\u0003\u0003\u0005\r\u0001V\u0004\tc\n\t\t\u0011#\u0001\u0005e\u0006)\u0002\n\u0012$T\u0007\u0006\u001c\u0007.\u001a+bg.dunY1uS>t\u0007C\u0001\nt\r!\t!!!A\t\u0002\u0011!8cA:v1A!a/_\u0010,\u001b\u00059(B\u0001=\u000e\u0003\u001d\u0011XO\u001c;j[\u0016L!A_<\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007C\u0003*g\u0012\u0005A\u0010F\u0001s\u0011\u001dq3/!A\u0005Fy$\u0012\u0001\u0012\u0005\n\u0003\u0003\u0019\u0018\u0011!CA\u0003\u0007\tQ!\u00199qYf$2aKA\u0003\u0011\u0015ar\u00101\u0001 \u0011%\tIa]A\u0001\n\u0003\u000bY!A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u00055\u00111\u0003\t\u0005\u0019\u0005=q$C\u0002\u0002\u00125\u0011aa\u00149uS>t\u0007\"CA\u000b\u0003\u000f\t\t\u00111\u0001,\u0003\rAH\u0005\r\u0005\n\u00033\u0019\u0018\u0011!C\u0005\u00037\t1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u0004\t\u0004\u000b\u0006}\u0011bAA\u0011\r\n1qJ\u00196fGR\u0004"
)
public class HDFSCacheTaskLocation implements TaskLocation, Product, Serializable {
   private final String host;

   public static Option<String> unapply(HDFSCacheTaskLocation var0) {
      return HDFSCacheTaskLocation$.MODULE$.unapply(var0);
   }

   public static HDFSCacheTaskLocation apply(String var0) {
      return HDFSCacheTaskLocation$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<HDFSCacheTaskLocation, A> var0) {
      return HDFSCacheTaskLocation$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, HDFSCacheTaskLocation> compose(Function1<A, String> var0) {
      return HDFSCacheTaskLocation$.MODULE$.compose(var0);
   }

   public String host() {
      return this.host;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)TaskLocation$.MODULE$.inMemoryLocationTag()).append((Object)this.host()).toString();
   }

   public HDFSCacheTaskLocation copy(String host) {
      return new HDFSCacheTaskLocation(host);
   }

   public String copy$default$1() {
      return this.host();
   }

   public String productPrefix() {
      return "HDFSCacheTaskLocation";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.host();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof HDFSCacheTaskLocation;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof HDFSCacheTaskLocation) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     HDFSCacheTaskLocation var4 = (HDFSCacheTaskLocation)x$1;
                     String var10000 = this.host();
                     String var5 = var4.host();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public HDFSCacheTaskLocation(String host) {
      this.host = host;
      Product$class.$init$(this);
   }
}
