package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0019<Q!\u0001\u0002\t\u0002&\tQ\"\u00168l]><hNU3bg>t'BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001A\u0011!bC\u0007\u0002\u0005\u0019)AB\u0001EA\u001b\tiQK\\6o_^t'+Z1t_:\u001cRa\u0003\b\u0015/i\u0001\"a\u0004\n\u000e\u0003AQ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'A\u0011a!\u00118z%\u00164\u0007C\u0001\u0006\u0016\u0013\t1\"A\u0001\tUCN\\g)Y5mK\u0012\u0014V-Y:p]B\u0011q\u0002G\u0005\u00033A\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u00107%\u0011A\u0004\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\u0006=-!\taH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003%AQ!I\u0006\u0005B\t\nQ\u0002^8FeJ|'o\u0015;sS:<W#A\u0012\u0011\u0005\u0011:cBA\b&\u0013\t1\u0003#\u0001\u0004Qe\u0016$WMZ\u0005\u0003Q%\u0012aa\u0015;sS:<'B\u0001\u0014\u0011\u0011\u001dY3\"!A\u0005B1\nQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A\u0017\u0011\u00059\u001aT\"A\u0018\u000b\u0005A\n\u0014\u0001\u00027b]\u001eT\u0011AM\u0001\u0005U\u00064\u0018-\u0003\u0002)_!9QgCA\u0001\n\u00031\u0014\u0001\u00049s_\u0012,8\r^!sSRLX#A\u001c\u0011\u0005=A\u0014BA\u001d\u0011\u0005\rIe\u000e\u001e\u0005\bw-\t\t\u0011\"\u0001=\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"!\u0010!\u0011\u0005=q\u0014BA \u0011\u0005\r\te.\u001f\u0005\b\u0003j\n\t\u00111\u00018\u0003\rAH%\r\u0005\b\u0007.\t\t\u0011\"\u0011E\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A#\u0011\u0007\u0019KU(D\u0001H\u0015\tA\u0005#\u0001\u0006d_2dWm\u0019;j_:L!AS$\u0003\u0011%#XM]1u_JDq\u0001T\u0006\u0002\u0002\u0013\u0005Q*\u0001\u0005dC:,\u0015/^1m)\tq\u0015\u000b\u0005\u0002\u0010\u001f&\u0011\u0001\u000b\u0005\u0002\b\u0005>|G.Z1o\u0011\u001d\t5*!AA\u0002uBqaU\u0006\u0002\u0002\u0013\u0005C+\u0001\u0005iCND7i\u001c3f)\u00059\u0004b\u0002,\f\u0003\u0003%\teV\u0001\ti>\u001cFO]5oOR\tQ\u0006C\u0004Z\u0017\u0005\u0005I\u0011\u0002.\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u00027B\u0011a\u0006X\u0005\u0003;>\u0012aa\u00142kK\u000e$\bFA\u0006`!\t\u00017-D\u0001b\u0015\t\u0011'!\u0001\u0006b]:|G/\u0019;j_:L!\u0001Z1\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5)\u0005\u0001y\u0006"
)
public final class UnknownReason {
   public static boolean countTowardsTaskFailures() {
      return UnknownReason$.MODULE$.countTowardsTaskFailures();
   }

   public static String toString() {
      return UnknownReason$.MODULE$.toString();
   }

   public static int hashCode() {
      return UnknownReason$.MODULE$.hashCode();
   }

   public static boolean canEqual(Object var0) {
      return UnknownReason$.MODULE$.canEqual(var0);
   }

   public static Iterator<Object> productIterator() {
      return UnknownReason$.MODULE$.productIterator();
   }

   public static Object productElement(int var0) {
      return UnknownReason$.MODULE$.productElement(var0);
   }

   public static int productArity() {
      return UnknownReason$.MODULE$.productArity();
   }

   public static String productPrefix() {
      return UnknownReason$.MODULE$.productPrefix();
   }

   public static String toErrorString() {
      return UnknownReason$.MODULE$.toErrorString();
   }
}
