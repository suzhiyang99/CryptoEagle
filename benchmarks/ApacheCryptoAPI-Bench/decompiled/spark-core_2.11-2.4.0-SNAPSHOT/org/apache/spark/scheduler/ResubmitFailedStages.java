package org.apache.spark.scheduler;

import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001];a!\u0001\u0002\t\u0002\nQ\u0011\u0001\u0006*fgV\u0014W.\u001b;GC&dW\rZ*uC\u001e,7O\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOB\u00111\u0002D\u0007\u0002\u0005\u00191QB\u0001EA\u00059\u0011ACU3tk\nl\u0017\u000e\u001e$bS2,Gm\u0015;bO\u0016\u001c8#\u0002\u0007\u0010+aY\u0002C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\r\u0005\u0002\f-%\u0011qC\u0001\u0002\u0012\t\u0006;5k\u00195fIVdWM]#wK:$\bC\u0001\t\u001a\u0013\tQ\u0012CA\u0004Qe>$Wo\u0019;\u0011\u0005Aa\u0012BA\u000f\u0012\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011\u0015yB\u0002\"\u0001\"\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000f\rb\u0011\u0011!C!I\u0005i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\n\t\u0003M-j\u0011a\n\u0006\u0003Q%\nA\u0001\\1oO*\t!&\u0001\u0003kCZ\f\u0017B\u0001\u0017(\u0005\u0019\u0019FO]5oO\"9a\u0006DA\u0001\n\u0003y\u0013\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001\u0019\u0011\u0005A\t\u0014B\u0001\u001a\u0012\u0005\rIe\u000e\u001e\u0005\bi1\t\t\u0011\"\u00016\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"AN\u001d\u0011\u0005A9\u0014B\u0001\u001d\u0012\u0005\r\te.\u001f\u0005\buM\n\t\u00111\u00011\u0003\rAH%\r\u0005\by1\t\t\u0011\"\u0011>\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u0001 \u0011\u0007}\u0012e'D\u0001A\u0015\t\t\u0015#\u0001\u0006d_2dWm\u0019;j_:L!a\u0011!\u0003\u0011%#XM]1u_JDq!\u0012\u0007\u0002\u0002\u0013\u0005a)\u0001\u0005dC:,\u0015/^1m)\t9%\n\u0005\u0002\u0011\u0011&\u0011\u0011*\u0005\u0002\b\u0005>|G.Z1o\u0011\u001dQD)!AA\u0002YBq\u0001\u0014\u0007\u0002\u0002\u0013\u0005S*\u0001\u0005iCND7i\u001c3f)\u0005\u0001\u0004bB(\r\u0003\u0003%\t\u0005U\u0001\ti>\u001cFO]5oOR\tQ\u0005C\u0004S\u0019\u0005\u0005I\u0011B*\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0002)B\u0011a%V\u0005\u0003-\u001e\u0012aa\u00142kK\u000e$\b"
)
public final class ResubmitFailedStages {
   public static String toString() {
      return ResubmitFailedStages$.MODULE$.toString();
   }

   public static int hashCode() {
      return ResubmitFailedStages$.MODULE$.hashCode();
   }

   public static boolean canEqual(Object var0) {
      return ResubmitFailedStages$.MODULE$.canEqual(var0);
   }

   public static Iterator<Object> productIterator() {
      return ResubmitFailedStages$.MODULE$.productIterator();
   }

   public static Object productElement(int var0) {
      return ResubmitFailedStages$.MODULE$.productElement(var0);
   }

   public static int productArity() {
      return ResubmitFailedStages$.MODULE$.productArity();
   }

   public static String productPrefix() {
      return ResubmitFailedStages$.MODULE$.productPrefix();
   }
}
