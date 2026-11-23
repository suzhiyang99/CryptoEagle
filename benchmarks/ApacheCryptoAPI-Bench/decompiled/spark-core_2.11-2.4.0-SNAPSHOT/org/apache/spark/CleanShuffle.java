package org.apache.spark;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a\u0001B\u0001\u0003\t&\u0011Ab\u00117fC:\u001c\u0006.\u001e4gY\u0016T!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001'\u0015\u0001!\u0002\u0005\u000b\u0018!\tYa\"D\u0001\r\u0015\u0005i\u0011!B:dC2\f\u0017BA\b\r\u0005\u0019\te.\u001f*fMB\u0011\u0011CE\u0007\u0002\u0005%\u00111C\u0001\u0002\f\u00072,\u0017M\\;q)\u0006\u001c8\u000e\u0005\u0002\f+%\u0011a\u0003\u0004\u0002\b!J|G-^2u!\tY\u0001$\u0003\u0002\u001a\u0019\ta1+\u001a:jC2L'0\u00192mK\"A1\u0004\u0001BK\u0002\u0013\u0005A$A\u0005tQV4g\r\\3JIV\tQ\u0004\u0005\u0002\f=%\u0011q\u0004\u0004\u0002\u0004\u0013:$\b\u0002C\u0011\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0015MDWO\u001a4mK&#\u0007\u0005C\u0003$\u0001\u0011\u0005A%\u0001\u0004=S:LGO\u0010\u000b\u0003K\u0019\u0002\"!\u0005\u0001\t\u000bm\u0011\u0003\u0019A\u000f\t\u000f!\u0002\u0011\u0011!C\u0001S\u0005!1m\u001c9z)\t)#\u0006C\u0004\u001cOA\u0005\t\u0019A\u000f\t\u000f1\u0002\u0011\u0013!C\u0001[\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001\u0018+\u0005uy3&\u0001\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014!C;oG\",7m[3e\u0015\t)D\"\u0001\u0006b]:|G/\u0019;j_:L!a\u000e\u001a\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004:\u0001\u0005\u0005I\u0011\t\u001e\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005Y\u0004C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003\u0011a\u0017M\\4\u000b\u0003\u0001\u000bAA[1wC&\u0011!)\u0010\u0002\u0007'R\u0014\u0018N\\4\t\u000f\u0011\u0003\u0011\u0011!C\u00019\u0005a\u0001O]8ek\u000e$\u0018I]5us\"9a\tAA\u0001\n\u00039\u0015A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003\u0011.\u0003\"aC%\n\u0005)c!aA!os\"9A*RA\u0001\u0002\u0004i\u0012a\u0001=%c!9a\nAA\u0001\n\u0003z\u0015a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003A\u00032!\u0015+I\u001b\u0005\u0011&BA*\r\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003+J\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b/\u0002\t\t\u0011\"\u0001Y\u0003!\u0019\u0017M\\#rk\u0006dGCA-]!\tY!,\u0003\u0002\\\u0019\t9!i\\8mK\u0006t\u0007b\u0002'W\u0003\u0003\u0005\r\u0001\u0013\u0005\b=\u0002\t\t\u0011\"\u0011`\u0003!A\u0017m\u001d5D_\u0012,G#A\u000f\t\u000f\u0005\u0004\u0011\u0011!C!E\u0006AAo\\*ue&tw\rF\u0001<\u0011\u001d!\u0007!!A\u0005B\u0015\fa!Z9vC2\u001cHCA-g\u0011\u001da5-!AA\u0002!;q\u0001\u001b\u0002\u0002\u0002#%\u0011.\u0001\u0007DY\u0016\fgn\u00155vM\u001adW\r\u0005\u0002\u0012U\u001a9\u0011AAA\u0001\u0012\u0013Y7c\u00016m/A!Q\u000e]\u000f&\u001b\u0005q'BA8\r\u0003\u001d\u0011XO\u001c;j[\u0016L!!\u001d8\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007C\u0003$U\u0012\u00051\u000fF\u0001j\u0011\u001d\t'.!A\u0005F\tDqA\u001e6\u0002\u0002\u0013\u0005u/A\u0003baBd\u0017\u0010\u0006\u0002&q\")1$\u001ea\u0001;!9!P[A\u0001\n\u0003[\u0018aB;oCB\u0004H.\u001f\u000b\u0003y~\u00042aC?\u001e\u0013\tqHB\u0001\u0004PaRLwN\u001c\u0005\t\u0003\u0003I\u0018\u0011!a\u0001K\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005\u0015!.!A\u0005\n\u0005\u001d\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0003\u0011\u0007q\nY!C\u0002\u0002\u000eu\u0012aa\u00142kK\u000e$\b"
)
public class CleanShuffle implements CleanupTask, Product, Serializable {
   private final int shuffleId;

   public static Option<Object> unapply(CleanShuffle var0) {
      return CleanShuffle$.MODULE$.unapply(var0);
   }

   public static CleanShuffle apply(int var0) {
      return CleanShuffle$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<CleanShuffle, A> var0) {
      return CleanShuffle$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, CleanShuffle> compose(Function1<A, Object> var0) {
      return CleanShuffle$.MODULE$.compose(var0);
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public CleanShuffle copy(int shuffleId) {
      return new CleanShuffle(shuffleId);
   }

   public int copy$default$1() {
      return this.shuffleId();
   }

   public String productPrefix() {
      return "CleanShuffle";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToInteger(this.shuffleId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof CleanShuffle;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.shuffleId());
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
            if (x$1 instanceof CleanShuffle) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               CleanShuffle var4 = (CleanShuffle)x$1;
               if (this.shuffleId() == var4.shuffleId() && var4.canEqual(this)) {
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

   public CleanShuffle(int shuffleId) {
      this.shuffleId = shuffleId;
      Product$class.$init$(this);
   }
}
