package org.apache.spark.scheduler.local;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001=4A!\u0001\u0002E\u001b\ta1\u000b^8q\u000bb,7-\u001e;pe*\u00111\u0001B\u0001\u0006Y>\u001c\u0017\r\u001c\u0006\u0003\u000b\u0019\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u001dQ9\u0002CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\r\u0005\u0002\u0010+%\u0011a\u0003\u0005\u0002\b!J|G-^2u!\ty\u0001$\u0003\u0002\u001a!\ta1+\u001a:jC2L'0\u00192mK\")1\u0004\u0001C\u00019\u00051A(\u001b8jiz\"\u0012!\b\t\u0003=\u0001i\u0011A\u0001\u0005\bA\u0001\t\t\u0011\"\u0001\u001d\u0003\u0011\u0019w\u000e]=\t\u000f\t\u0002\u0011\u0011!C!G\u0005i\u0001O]8ek\u000e$\bK]3gSb,\u0012\u0001\n\t\u0003K)j\u0011A\n\u0006\u0003O!\nA\u0001\\1oO*\t\u0011&\u0001\u0003kCZ\f\u0017BA\u0016'\u0005\u0019\u0019FO]5oO\"9Q\u0006AA\u0001\n\u0003q\u0013\u0001\u00049s_\u0012,8\r^!sSRLX#A\u0018\u0011\u0005=\u0001\u0014BA\u0019\u0011\u0005\rIe\u000e\u001e\u0005\bg\u0001\t\t\u0011\"\u00015\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"!\u000e\u001d\u0011\u0005=1\u0014BA\u001c\u0011\u0005\r\te.\u001f\u0005\bsI\n\t\u00111\u00010\u0003\rAH%\r\u0005\bw\u0001\t\t\u0011\"\u0011=\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A\u001f\u0011\u0007y\nU'D\u0001@\u0015\t\u0001\u0005#\u0001\u0006d_2dWm\u0019;j_:L!AQ \u0003\u0011%#XM]1u_JDq\u0001\u0012\u0001\u0002\u0002\u0013\u0005Q)\u0001\u0005dC:,\u0015/^1m)\t1\u0015\n\u0005\u0002\u0010\u000f&\u0011\u0001\n\u0005\u0002\b\u0005>|G.Z1o\u0011\u001dI4)!AA\u0002UBqa\u0013\u0001\u0002\u0002\u0013\u0005C*\u0001\u0005iCND7i\u001c3f)\u0005y\u0003b\u0002(\u0001\u0003\u0003%\teT\u0001\ti>\u001cFO]5oOR\tA\u0005C\u0004R\u0001\u0005\u0005I\u0011\t*\u0002\r\u0015\fX/\u00197t)\t15\u000bC\u0004:!\u0006\u0005\t\u0019A\u001b\b\u000fU\u0013\u0011\u0011!E\u0005-\u0006a1\u000b^8q\u000bb,7-\u001e;peB\u0011ad\u0016\u0004\b\u0003\t\t\t\u0011#\u0003Y'\r9\u0016l\u0006\t\u00045vkR\"A.\u000b\u0005q\u0003\u0012a\u0002:v]RLW.Z\u0005\u0003=n\u0013\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c81\u0011\u0015Yr\u000b\"\u0001a)\u00051\u0006b\u0002(X\u0003\u0003%)e\u0014\u0005\bG^\u000b\t\u0011\"!\u001d\u0003\u0015\t\u0007\u000f\u001d7z\u0011\u001d)w+!A\u0005\u0002\u001a\fq!\u001e8baBd\u0017\u0010\u0006\u0002GO\"9\u0001\u000eZA\u0001\u0002\u0004i\u0012a\u0001=%a!9!nVA\u0001\n\u0013Y\u0017a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012\u0001\u001c\t\u0003K5L!A\u001c\u0014\u0003\r=\u0013'.Z2u\u0001"
)
public class StopExecutor implements Product, Serializable {
   public static boolean unapply(StopExecutor var0) {
      return StopExecutor$.MODULE$.unapply(var0);
   }

   public static StopExecutor apply() {
      return StopExecutor$.MODULE$.apply();
   }

   public StopExecutor copy() {
      return new StopExecutor();
   }

   public String productPrefix() {
      return "StopExecutor";
   }

   public int productArity() {
      return 0;
   }

   public Object productElement(int x$1) {
      throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof StopExecutor;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var3;
      if (x$1 instanceof StopExecutor) {
         var3 = true;
      } else {
         var3 = false;
      }

      return var3 && ((StopExecutor)x$1).canEqual(this);
   }

   public StopExecutor() {
      Product$class.$init$(this);
   }
}
