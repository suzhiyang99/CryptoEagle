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
   bytes = "\u0006\u0001\u0005]a\u0001B\u0001\u0003\t&\u0011ab\u00117fC:\u0014%o\\1eG\u0006\u001cHO\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0006\u0011)]\u0001\"a\u0003\b\u000e\u00031Q\u0011!D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001f1\u0011a!\u00118z%\u00164\u0007CA\t\u0013\u001b\u0005\u0011\u0011BA\n\u0003\u0005-\u0019E.Z1okB$\u0016m]6\u0011\u0005-)\u0012B\u0001\f\r\u0005\u001d\u0001&o\u001c3vGR\u0004\"a\u0003\r\n\u0005ea!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000e\u0001\u0005+\u0007I\u0011\u0001\u000f\u0002\u0017\t\u0014x.\u00193dCN$\u0018\nZ\u000b\u0002;A\u00111BH\u0005\u0003?1\u0011A\u0001T8oO\"A\u0011\u0005\u0001B\tB\u0003%Q$\u0001\u0007ce>\fGmY1ti&#\u0007\u0005C\u0003$\u0001\u0011\u0005A%\u0001\u0004=S:LGO\u0010\u000b\u0003K\u0019\u0002\"!\u0005\u0001\t\u000bm\u0011\u0003\u0019A\u000f\t\u000f!\u0002\u0011\u0011!C\u0001S\u0005!1m\u001c9z)\t)#\u0006C\u0004\u001cOA\u0005\t\u0019A\u000f\t\u000f1\u0002\u0011\u0013!C\u0001[\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001\u0018+\u0005uy3&\u0001\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014!C;oG\",7m[3e\u0015\t)D\"\u0001\u0006b]:|G/\u0019;j_:L!a\u000e\u001a\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004:\u0001\u0005\u0005I\u0011\t\u001e\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005Y\u0004C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003\u0011a\u0017M\\4\u000b\u0003\u0001\u000bAA[1wC&\u0011!)\u0010\u0002\u0007'R\u0014\u0018N\\4\t\u000f\u0011\u0003\u0011\u0011!C\u0001\u000b\u0006a\u0001O]8ek\u000e$\u0018I]5usV\ta\t\u0005\u0002\f\u000f&\u0011\u0001\n\u0004\u0002\u0004\u0013:$\bb\u0002&\u0001\u0003\u0003%\taS\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tau\n\u0005\u0002\f\u001b&\u0011a\n\u0004\u0002\u0004\u0003:L\bb\u0002)J\u0003\u0003\u0005\rAR\u0001\u0004q\u0012\n\u0004b\u0002*\u0001\u0003\u0003%\teU\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\tA\u000bE\u0002V12k\u0011A\u0016\u0006\u0003/2\t!bY8mY\u0016\u001cG/[8o\u0013\tIfK\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dY\u0006!!A\u0005\u0002q\u000b\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003;\u0002\u0004\"a\u00030\n\u0005}c!a\u0002\"p_2,\u0017M\u001c\u0005\b!j\u000b\t\u00111\u0001M\u0011\u001d\u0011\u0007!!A\u0005B\r\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002\r\"9Q\rAA\u0001\n\u00032\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003mBq\u0001\u001b\u0001\u0002\u0002\u0013\u0005\u0013.\u0001\u0004fcV\fGn\u001d\u000b\u0003;*Dq\u0001U4\u0002\u0002\u0003\u0007AjB\u0004m\u0005\u0005\u0005\t\u0012B7\u0002\u001d\rcW-\u00198Ce>\fGmY1tiB\u0011\u0011C\u001c\u0004\b\u0003\t\t\t\u0011#\u0003p'\rq\u0007o\u0006\t\u0005cRlR%D\u0001s\u0015\t\u0019H\"A\u0004sk:$\u0018.\\3\n\u0005U\u0014(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!)1E\u001cC\u0001oR\tQ\u000eC\u0004f]\u0006\u0005IQ\t4\t\u000fit\u0017\u0011!CAw\u0006)\u0011\r\u001d9msR\u0011Q\u0005 \u0005\u00067e\u0004\r!\b\u0005\b}:\f\t\u0011\"!\u0000\u0003\u001d)h.\u00199qYf$B!!\u0001\u0002\bA!1\"a\u0001\u001e\u0013\r\t)\u0001\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0011\u0005%Q0!AA\u0002\u0015\n1\u0001\u001f\u00131\u0011%\tiA\\A\u0001\n\u0013\ty!A\u0006sK\u0006$'+Z:pYZ,GCAA\t!\ra\u00141C\u0005\u0004\u0003+i$AB(cU\u0016\u001cG\u000f"
)
public class CleanBroadcast implements CleanupTask, Product, Serializable {
   private final long broadcastId;

   public static Option<Object> unapply(CleanBroadcast var0) {
      return CleanBroadcast$.MODULE$.unapply(var0);
   }

   public static CleanBroadcast apply(long var0) {
      return CleanBroadcast$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<CleanBroadcast, A> var0) {
      return CleanBroadcast$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, CleanBroadcast> compose(Function1<A, Object> var0) {
      return CleanBroadcast$.MODULE$.compose(var0);
   }

   public long broadcastId() {
      return this.broadcastId;
   }

   public CleanBroadcast copy(long broadcastId) {
      return new CleanBroadcast(broadcastId);
   }

   public long copy$default$1() {
      return this.broadcastId();
   }

   public String productPrefix() {
      return "CleanBroadcast";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToLong(this.broadcastId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof CleanBroadcast;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.broadcastId()));
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
            if (x$1 instanceof CleanBroadcast) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               CleanBroadcast var4 = (CleanBroadcast)x$1;
               if (this.broadcastId() == var4.broadcastId() && var4.canEqual(this)) {
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

   public CleanBroadcast(long broadcastId) {
      this.broadcastId = broadcastId;
      Product$class.$init$(this);
   }
}
