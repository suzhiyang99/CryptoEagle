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
   bytes = "\u0006\u0001\u0005]a\u0001B\u0001\u0003\t&\u0011!b\u00117fC:\f5mY;n\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\n\u0006\u0001)\u0001Bc\u0006\t\u0003\u00179i\u0011\u0001\u0004\u0006\u0002\u001b\u0005)1oY1mC&\u0011q\u0002\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005E\u0011R\"\u0001\u0002\n\u0005M\u0011!aC\"mK\u0006tW\u000f\u001d+bg.\u0004\"aC\u000b\n\u0005Ya!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0017aI!!\u0007\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011m\u0001!Q3A\u0005\u0002q\tQ!Y2d\u0013\u0012,\u0012!\b\t\u0003\u0017yI!a\b\u0007\u0003\t1{gn\u001a\u0005\tC\u0001\u0011\t\u0012)A\u0005;\u00051\u0011mY2JI\u0002BQa\t\u0001\u0005\u0002\u0011\na\u0001P5oSRtDCA\u0013'!\t\t\u0002\u0001C\u0003\u001cE\u0001\u0007Q\u0004C\u0004)\u0001\u0005\u0005I\u0011A\u0015\u0002\t\r|\u0007/\u001f\u000b\u0003K)BqaG\u0014\u0011\u0002\u0003\u0007Q\u0004C\u0004-\u0001E\u0005I\u0011A\u0017\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\taF\u000b\u0002\u001e_-\n\u0001\u0007\u0005\u00022m5\t!G\u0003\u00024i\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003k1\t!\"\u00198o_R\fG/[8o\u0013\t9$GA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq!\u000f\u0001\u0002\u0002\u0013\u0005#(A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002wA\u0011A(Q\u0007\u0002{)\u0011ahP\u0001\u0005Y\u0006twMC\u0001A\u0003\u0011Q\u0017M^1\n\u0005\tk$AB*ue&tw\rC\u0004E\u0001\u0005\u0005I\u0011A#\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u0019\u0003\"aC$\n\u0005!c!aA%oi\"9!\nAA\u0001\n\u0003Y\u0015A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003\u0019>\u0003\"aC'\n\u00059c!aA!os\"9\u0001+SA\u0001\u0002\u00041\u0015a\u0001=%c!9!\u000bAA\u0001\n\u0003\u001a\u0016a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003Q\u00032!\u0016-M\u001b\u00051&BA,\r\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u00033Z\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b7\u0002\t\t\u0011\"\u0001]\u0003!\u0019\u0017M\\#rk\u0006dGCA/a!\tYa,\u0003\u0002`\u0019\t9!i\\8mK\u0006t\u0007b\u0002)[\u0003\u0003\u0005\r\u0001\u0014\u0005\bE\u0002\t\t\u0011\"\u0011d\u0003!A\u0017m\u001d5D_\u0012,G#\u0001$\t\u000f\u0015\u0004\u0011\u0011!C!M\u0006AAo\\*ue&tw\rF\u0001<\u0011\u001dA\u0007!!A\u0005B%\fa!Z9vC2\u001cHCA/k\u0011\u001d\u0001v-!AA\u00021;q\u0001\u001c\u0002\u0002\u0002#%Q.\u0001\u0006DY\u0016\fg.Q2dk6\u0004\"!\u00058\u0007\u000f\u0005\u0011\u0011\u0011!E\u0005_N\u0019a\u000e]\f\u0011\tE$X$J\u0007\u0002e*\u00111\u000fD\u0001\beVtG/[7f\u0013\t)(OA\tBEN$(/Y2u\rVt7\r^5p]FBQa\t8\u0005\u0002]$\u0012!\u001c\u0005\bK:\f\t\u0011\"\u0012g\u0011\u001dQh.!A\u0005\u0002n\fQ!\u00199qYf$\"!\n?\t\u000bmI\b\u0019A\u000f\t\u000fyt\u0017\u0011!CA\u007f\u00069QO\\1qa2LH\u0003BA\u0001\u0003\u000f\u0001BaCA\u0002;%\u0019\u0011Q\u0001\u0007\u0003\r=\u0003H/[8o\u0011!\tI!`A\u0001\u0002\u0004)\u0013a\u0001=%a!I\u0011Q\u00028\u0002\u0002\u0013%\u0011qB\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002\u0012A\u0019A(a\u0005\n\u0007\u0005UQH\u0001\u0004PE*,7\r\u001e"
)
public class CleanAccum implements CleanupTask, Product, Serializable {
   private final long accId;

   public static Option<Object> unapply(CleanAccum var0) {
      return CleanAccum$.MODULE$.unapply(var0);
   }

   public static CleanAccum apply(long var0) {
      return CleanAccum$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<CleanAccum, A> var0) {
      return CleanAccum$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, CleanAccum> compose(Function1<A, Object> var0) {
      return CleanAccum$.MODULE$.compose(var0);
   }

   public long accId() {
      return this.accId;
   }

   public CleanAccum copy(long accId) {
      return new CleanAccum(accId);
   }

   public long copy$default$1() {
      return this.accId();
   }

   public String productPrefix() {
      return "CleanAccum";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToLong(this.accId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof CleanAccum;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.accId()));
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
            if (x$1 instanceof CleanAccum) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               CleanAccum var4 = (CleanAccum)x$1;
               if (this.accId() == var4.accId() && var4.canEqual(this)) {
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

   public CleanAccum(long accId) {
      this.accId = accId;
      Product$class.$init$(this);
   }
}
