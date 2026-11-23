package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
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

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001db\u0001B\u0001\u0003\u0001.\u00111d\u00159be.d\u0015n\u001d;f]\u0016\u0014\u0018\t\u001d9mS\u000e\fG/[8o\u000b:$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\tQLW.Z\u000b\u0002?A\u0011Q\u0002I\u0005\u0003C9\u0011A\u0001T8oO\"A1\u0005\u0001B\tB\u0003%q$A\u0003uS6,\u0007\u0005C\u0003&\u0001\u0011\u0005a%\u0001\u0004=S:LGO\u0010\u000b\u0003O!\u0002\"a\u0005\u0001\t\u000bu!\u0003\u0019A\u0010\t\u000f)\u0002\u0011\u0011!C\u0001W\u0005!1m\u001c9z)\t9C\u0006C\u0004\u001eSA\u0005\t\u0019A\u0010\t\u000f9\u0002\u0011\u0013!C\u0001_\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001\u0019+\u0005}\t4&\u0001\u001a\u0011\u0005MBT\"\u0001\u001b\u000b\u0005U2\u0014!C;oG\",7m[3e\u0015\t9d\"\u0001\u0006b]:|G/\u0019;j_:L!!\u000f\u001b\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004<\u0001\u0005\u0005I\u0011\t\u001f\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005i\u0004C\u0001 D\u001b\u0005y$B\u0001!B\u0003\u0011a\u0017M\\4\u000b\u0003\t\u000bAA[1wC&\u0011Ai\u0010\u0002\u0007'R\u0014\u0018N\\4\t\u000f\u0019\u0003\u0011\u0011!C\u0001\u000f\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t\u0001\n\u0005\u0002\u000e\u0013&\u0011!J\u0004\u0002\u0004\u0013:$\bb\u0002'\u0001\u0003\u0003%\t!T\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tq\u0015\u000b\u0005\u0002\u000e\u001f&\u0011\u0001K\u0004\u0002\u0004\u0003:L\bb\u0002*L\u0003\u0003\u0005\r\u0001S\u0001\u0004q\u0012\n\u0004b\u0002+\u0001\u0003\u0003%\t%V\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\ta\u000bE\u0002X5:k\u0011\u0001\u0017\u0006\u00033:\t!bY8mY\u0016\u001cG/[8o\u0013\tY\u0006L\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001di\u0006!!A\u0005\u0002y\u000b\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003?\n\u0004\"!\u00041\n\u0005\u0005t!a\u0002\"p_2,\u0017M\u001c\u0005\b%r\u000b\t\u00111\u0001O\u0011\u001d!\u0007!!A\u0005B\u0015\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002\u0011\"9q\rAA\u0001\n\u0003B\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003uBqA\u001b\u0001\u0002\u0002\u0013\u00053.\u0001\u0004fcV\fGn\u001d\u000b\u0003?2DqAU5\u0002\u0002\u0003\u0007a\n\u000b\u0002\u0001]B\u0011q.]\u0007\u0002a*\u0011q\u0007B\u0005\u0003eB\u0014A\u0002R3wK2|\u0007/\u001a:Ba&<q\u0001\u001e\u0002\u0002\u0002#\u0005Q/A\u000eTa\u0006\u00148\u000eT5ti\u0016tWM]!qa2L7-\u0019;j_:,e\u000e\u001a\t\u0003'Y4q!\u0001\u0002\u0002\u0002#\u0005qoE\u0002wqf\u0001B!\u001f? O5\t!P\u0003\u0002|\u001d\u00059!/\u001e8uS6,\u0017BA?{\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\u0006KY$\ta \u000b\u0002k\"9qM^A\u0001\n\u000bB\u0007\"CA\u0003m\u0006\u0005I\u0011QA\u0004\u0003\u0015\t\u0007\u000f\u001d7z)\r9\u0013\u0011\u0002\u0005\u0007;\u0005\r\u0001\u0019A\u0010\t\u0013\u00055a/!A\u0005\u0002\u0006=\u0011aB;oCB\u0004H.\u001f\u000b\u0005\u0003#\t9\u0002\u0005\u0003\u000e\u0003'y\u0012bAA\u000b\u001d\t1q\n\u001d;j_:D\u0011\"!\u0007\u0002\f\u0005\u0005\t\u0019A\u0014\u0002\u0007a$\u0003\u0007C\u0005\u0002\u001eY\f\t\u0011\"\u0003\u0002 \u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t\t\u0003E\u0002?\u0003GI1!!\n@\u0005\u0019y%M[3di\u0002"
)
public class SparkListenerApplicationEnd implements SparkListenerEvent, Product, Serializable {
   private final long time;

   public static Option<Object> unapply(SparkListenerApplicationEnd var0) {
      return SparkListenerApplicationEnd$.MODULE$.unapply(var0);
   }

   public static SparkListenerApplicationEnd apply(long var0) {
      return SparkListenerApplicationEnd$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<SparkListenerApplicationEnd, A> var0) {
      return SparkListenerApplicationEnd$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerApplicationEnd> compose(Function1<A, Object> var0) {
      return SparkListenerApplicationEnd$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public SparkListenerApplicationEnd copy(long time) {
      return new SparkListenerApplicationEnd(time);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String productPrefix() {
      return "SparkListenerApplicationEnd";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToLong(this.time());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerApplicationEnd;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
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
            if (x$1 instanceof SparkListenerApplicationEnd) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               SparkListenerApplicationEnd var4 = (SparkListenerApplicationEnd)x$1;
               if (this.time() == var4.time() && var4.canEqual(this)) {
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

   public SparkListenerApplicationEnd(long time) {
      this.time = time;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
