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
   bytes = "\u0006\u0001\u0005-a!B\u0001\u0003\u0001\nA!!\u0005%fCJ$(-Z1u%\u0016\u001c\bo\u001c8tK*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0013=\u0011\u0002C\u0001\u0006\u000e\u001b\u0005Y!\"\u0001\u0007\u0002\u000bM\u001c\u0017\r\\1\n\u00059Y!AB!osJ+g\r\u0005\u0002\u000b!%\u0011\u0011c\u0003\u0002\b!J|G-^2u!\tQ1#\u0003\u0002\u0015\u0017\ta1+\u001a:jC2L'0\u00192mK\"Aa\u0003\u0001BK\u0002\u0013\u0005\u0001$\u0001\fsKJ,w-[:uKJ\u0014En\\2l\u001b\u0006t\u0017mZ3s\u0007\u0001)\u0012!\u0007\t\u0003\u0015iI!aG\u0006\u0003\u000f\t{w\u000e\\3b]\"AQ\u0004\u0001B\tB\u0003%\u0011$A\fsKJ,w-[:uKJ\u0014En\\2l\u001b\u0006t\u0017mZ3sA!)q\u0004\u0001C\u0001A\u00051A(\u001b8jiz\"\"!I\u0012\u0011\u0005\t\u0002Q\"\u0001\u0002\t\u000bYq\u0002\u0019A\r\t\u000f\u0015\u0002\u0011\u0011!C\u0001M\u0005!1m\u001c9z)\t\ts\u0005C\u0004\u0017IA\u0005\t\u0019A\r\t\u000f%\u0002\u0011\u0013!C\u0001U\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\u0016+\u0005ea3&A\u0017\u0011\u00059\u001aT\"A\u0018\u000b\u0005A\n\u0014!C;oG\",7m[3e\u0015\t\u00114\"\u0001\u0006b]:|G/\u0019;j_:L!\u0001N\u0018\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u00047\u0001\u0005\u0005I\u0011I\u001c\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005A\u0004CA\u001d?\u001b\u0005Q$BA\u001e=\u0003\u0011a\u0017M\\4\u000b\u0003u\nAA[1wC&\u0011qH\u000f\u0002\u0007'R\u0014\u0018N\\4\t\u000f\u0005\u0003\u0011\u0011!C\u0001\u0005\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t1\t\u0005\u0002\u000b\t&\u0011Qi\u0003\u0002\u0004\u0013:$\bbB$\u0001\u0003\u0003%\t\u0001S\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tIE\n\u0005\u0002\u000b\u0015&\u00111j\u0003\u0002\u0004\u0003:L\bbB'G\u0003\u0003\u0005\raQ\u0001\u0004q\u0012\n\u0004bB(\u0001\u0003\u0003%\t\u0005U\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0011\u000bE\u0002S+&k\u0011a\u0015\u0006\u0003).\t!bY8mY\u0016\u001cG/[8o\u0013\t16K\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dA\u0006!!A\u0005\u0002e\u000b\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u00033iCq!T,\u0002\u0002\u0003\u0007\u0011\nC\u0004]\u0001\u0005\u0005I\u0011I/\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\u0011\u0005\b?\u0002\t\t\u0011\"\u0011a\u0003!!xn\u0015;sS:<G#\u0001\u001d\t\u000f\t\u0004\u0011\u0011!C!G\u00061Q-];bYN$\"!\u00073\t\u000f5\u000b\u0017\u0011!a\u0001\u0013\u001eAaMAA\u0001\u0012\u0003\u0011q-A\tIK\u0006\u0014HOY3biJ+7\u000f]8og\u0016\u0004\"A\t5\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\u0005%\u001c2\u0001\u001b6\u0013!\u0011Yg.G\u0011\u000e\u00031T!!\\\u0006\u0002\u000fI,h\u000e^5nK&\u0011q\u000e\u001c\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004\"B\u0010i\t\u0003\tH#A4\t\u000f}C\u0017\u0011!C#A\"9A\u000f[A\u0001\n\u0003+\u0018!B1qa2LHCA\u0011w\u0011\u001512\u000f1\u0001\u001a\u0011\u001dA\b.!A\u0005\u0002f\fq!\u001e8baBd\u0017\u0010\u0006\u0002{{B\u0019!b_\r\n\u0005q\\!AB(qi&|g\u000eC\u0004\u007fo\u0006\u0005\t\u0019A\u0011\u0002\u0007a$\u0003\u0007C\u0005\u0002\u0002!\f\t\u0011\"\u0003\u0002\u0004\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t)\u0001E\u0002:\u0003\u000fI1!!\u0003;\u0005\u0019y%M[3di\u0002"
)
public class HeartbeatResponse implements Product, Serializable {
   private final boolean reregisterBlockManager;

   public static Option<Object> unapply(HeartbeatResponse var0) {
      return HeartbeatResponse$.MODULE$.unapply(var0);
   }

   public static HeartbeatResponse apply(boolean var0) {
      return HeartbeatResponse$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<HeartbeatResponse, A> var0) {
      return HeartbeatResponse$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, HeartbeatResponse> compose(Function1<A, Object> var0) {
      return HeartbeatResponse$.MODULE$.compose(var0);
   }

   public boolean reregisterBlockManager() {
      return this.reregisterBlockManager;
   }

   public HeartbeatResponse copy(boolean reregisterBlockManager) {
      return new HeartbeatResponse(reregisterBlockManager);
   }

   public boolean copy$default$1() {
      return this.reregisterBlockManager();
   }

   public String productPrefix() {
      return "HeartbeatResponse";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToBoolean(this.reregisterBlockManager());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof HeartbeatResponse;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.reregisterBlockManager() ? 1231 : 1237);
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
            if (x$1 instanceof HeartbeatResponse) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               HeartbeatResponse var4 = (HeartbeatResponse)x$1;
               if (this.reregisterBlockManager() == var4.reregisterBlockManager() && var4.canEqual(this)) {
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

   public HeartbeatResponse(boolean reregisterBlockManager) {
      this.reregisterBlockManager = reregisterBlockManager;
      Product$class.$init$(this);
   }
}
