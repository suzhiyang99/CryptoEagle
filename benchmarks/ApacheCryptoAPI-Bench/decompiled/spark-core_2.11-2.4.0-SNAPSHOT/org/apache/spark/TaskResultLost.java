package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0019<Q!\u0001\u0002\t\u0002&\ta\u0002V1tWJ+7/\u001e7u\u0019>\u001cHO\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0001\"AC\u0006\u000e\u0003\t1Q\u0001\u0004\u0002\t\u00026\u0011a\u0002V1tWJ+7/\u001e7u\u0019>\u001cHoE\u0003\f\u001dQ9\"\u0004\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\t\u0003\u0015UI!A\u0006\u0002\u0003!Q\u000b7o\u001b$bS2,GMU3bg>t\u0007CA\b\u0019\u0013\tI\u0002CA\u0004Qe>$Wo\u0019;\u0011\u0005=Y\u0012B\u0001\u000f\u0011\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011\u0015q2\u0002\"\u0001 \u0003\u0019a\u0014N\\5u}Q\t\u0011\u0002C\u0003\"\u0017\u0011\u0005#%A\u0007u_\u0016\u0013(o\u001c:TiJLgnZ\u000b\u0002GA\u0011Ae\n\b\u0003\u001f\u0015J!A\n\t\u0002\rA\u0013X\rZ3g\u0013\tA\u0013F\u0001\u0004TiJLgn\u001a\u0006\u0003MAAqaK\u0006\u0002\u0002\u0013\u0005C&A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002[A\u0011afM\u0007\u0002_)\u0011\u0001'M\u0001\u0005Y\u0006twMC\u00013\u0003\u0011Q\u0017M^1\n\u0005!z\u0003bB\u001b\f\u0003\u0003%\tAN\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002oA\u0011q\u0002O\u0005\u0003sA\u00111!\u00138u\u0011\u001dY4\"!A\u0005\u0002q\na\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002>\u0001B\u0011qBP\u0005\u0003\u007fA\u00111!\u00118z\u0011\u001d\t%(!AA\u0002]\n1\u0001\u001f\u00132\u0011\u001d\u00195\"!A\u0005B\u0011\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002\u000bB\u0019a)S\u001f\u000e\u0003\u001dS!\u0001\u0013\t\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002K\u000f\nA\u0011\n^3sCR|'\u000fC\u0004M\u0017\u0005\u0005I\u0011A'\u0002\u0011\r\fg.R9vC2$\"AT)\u0011\u0005=y\u0015B\u0001)\u0011\u0005\u001d\u0011un\u001c7fC:Dq!Q&\u0002\u0002\u0003\u0007Q\bC\u0004T\u0017\u0005\u0005I\u0011\t+\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\u000e\u0005\b-.\t\t\u0011\"\u0011X\u0003!!xn\u0015;sS:<G#A\u0017\t\u000fe[\u0011\u0011!C\u00055\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\u0005Y\u0006C\u0001\u0018]\u0013\tivF\u0001\u0004PE*,7\r\u001e\u0015\u0003\u0017}\u0003\"\u0001Y2\u000e\u0003\u0005T!A\u0019\u0002\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002eC\naA)\u001a<fY>\u0004XM]!qS\"\u0012\u0001a\u0018"
)
public final class TaskResultLost {
   public static boolean countTowardsTaskFailures() {
      return TaskResultLost$.MODULE$.countTowardsTaskFailures();
   }

   public static String toString() {
      return TaskResultLost$.MODULE$.toString();
   }

   public static int hashCode() {
      return TaskResultLost$.MODULE$.hashCode();
   }

   public static boolean canEqual(Object var0) {
      return TaskResultLost$.MODULE$.canEqual(var0);
   }

   public static Iterator<Object> productIterator() {
      return TaskResultLost$.MODULE$.productIterator();
   }

   public static Object productElement(int var0) {
      return TaskResultLost$.MODULE$.productElement(var0);
   }

   public static int productArity() {
      return TaskResultLost$.MODULE$.productArity();
   }

   public static String productPrefix() {
      return TaskResultLost$.MODULE$.productPrefix();
   }

   public static String toErrorString() {
      return TaskResultLost$.MODULE$.toErrorString();
   }
}
