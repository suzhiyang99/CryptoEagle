package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001};Q!\u0001\u0002\t\u0002.\tABS8c'V\u001c7-Z3eK\u0012T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001A\u0011A\"D\u0007\u0002\u0005\u0019)aB\u0001EA\u001f\ta!j\u001c2Tk\u000e\u001cW-\u001a3fIN)Q\u0002\u0005\f\u001a9A\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t1\u0011I\\=SK\u001a\u0004\"\u0001D\f\n\u0005a\u0011!!\u0003&pEJ+7/\u001e7u!\t\t\"$\u0003\u0002\u001c%\t9\u0001K]8ek\u000e$\bCA\t\u001e\u0013\tq\"C\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0003!\u001b\u0011\u0005\u0011%\u0001\u0004=S:LGO\u0010\u000b\u0002\u0017!91%DA\u0001\n\u0003\"\u0013!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001&!\t13&D\u0001(\u0015\tA\u0013&\u0001\u0003mC:<'\"\u0001\u0016\u0002\t)\fg/Y\u0005\u0003Y\u001d\u0012aa\u0015;sS:<\u0007b\u0002\u0018\u000e\u0003\u0003%\taL\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002aA\u0011\u0011#M\u0005\u0003eI\u00111!\u00138u\u0011\u001d!T\"!A\u0005\u0002U\na\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u00027sA\u0011\u0011cN\u0005\u0003qI\u00111!\u00118z\u0011\u001dQ4'!AA\u0002A\n1\u0001\u001f\u00132\u0011\u001daT\"!A\u0005Bu\nq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002}A\u0019qH\u0011\u001c\u000e\u0003\u0001S!!\u0011\n\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002D\u0001\nA\u0011\n^3sCR|'\u000fC\u0004F\u001b\u0005\u0005I\u0011\u0001$\u0002\u0011\r\fg.R9vC2$\"a\u0012&\u0011\u0005EA\u0015BA%\u0013\u0005\u001d\u0011un\u001c7fC:DqA\u000f#\u0002\u0002\u0003\u0007a\u0007C\u0004M\u001b\u0005\u0005I\u0011I'\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012\u0001\r\u0005\b\u001f6\t\t\u0011\"\u0011Q\u0003!!xn\u0015;sS:<G#A\u0013\t\u000fIk\u0011\u0011!C\u0005'\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\u0005!\u0006C\u0001\u0014V\u0013\t1vE\u0001\u0004PE*,7\r\u001e\u0015\u0003\u001ba\u0003\"!\u0017/\u000e\u0003iS!a\u0017\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002^5\naA)\u001a<fY>\u0004XM]!qS\"\u0012\u0001\u0001\u0017"
)
public final class JobSucceeded {
   public static String toString() {
      return JobSucceeded$.MODULE$.toString();
   }

   public static int hashCode() {
      return JobSucceeded$.MODULE$.hashCode();
   }

   public static boolean canEqual(Object var0) {
      return JobSucceeded$.MODULE$.canEqual(var0);
   }

   public static Iterator<Object> productIterator() {
      return JobSucceeded$.MODULE$.productIterator();
   }

   public static Object productElement(int var0) {
      return JobSucceeded$.MODULE$.productElement(var0);
   }

   public static int productArity() {
      return JobSucceeded$.MODULE$.productArity();
   }

   public static String productPrefix() {
      return JobSucceeded$.MODULE$.productPrefix();
   }
}
