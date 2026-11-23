package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001u;Q!\u0001\u0002\t\u0002&\tqaU;dG\u0016\u001c8O\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0001\"AC\u0006\u000e\u0003\t1Q\u0001\u0004\u0002\t\u00026\u0011qaU;dG\u0016\u001c8oE\u0003\f\u001dQ9\"\u0004\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\t\u0003\u0015UI!A\u0006\u0002\u0003\u001bQ\u000b7o[#oIJ+\u0017m]8o!\ty\u0001$\u0003\u0002\u001a!\t9\u0001K]8ek\u000e$\bCA\b\u001c\u0013\ta\u0002C\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0003\u001f\u0017\u0011\u0005q$\u0001\u0004=S:LGO\u0010\u000b\u0002\u0013!9\u0011eCA\u0001\n\u0003\u0012\u0013!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001$!\t!\u0013&D\u0001&\u0015\t1s%\u0001\u0003mC:<'\"\u0001\u0015\u0002\t)\fg/Y\u0005\u0003U\u0015\u0012aa\u0015;sS:<\u0007b\u0002\u0017\f\u0003\u0003%\t!L\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002]A\u0011qbL\u0005\u0003aA\u00111!\u00138u\u0011\u001d\u00114\"!A\u0005\u0002M\na\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u00025oA\u0011q\"N\u0005\u0003mA\u00111!\u00118z\u0011\u001dA\u0014'!AA\u00029\n1\u0001\u001f\u00132\u0011\u001dQ4\"!A\u0005Bm\nq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002yA\u0019Q\b\u0011\u001b\u000e\u0003yR!a\u0010\t\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002B}\tA\u0011\n^3sCR|'\u000fC\u0004D\u0017\u0005\u0005I\u0011\u0001#\u0002\u0011\r\fg.R9vC2$\"!\u0012%\u0011\u0005=1\u0015BA$\u0011\u0005\u001d\u0011un\u001c7fC:Dq\u0001\u000f\"\u0002\u0002\u0003\u0007A\u0007C\u0004K\u0017\u0005\u0005I\u0011I&\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012A\f\u0005\b\u001b.\t\t\u0011\"\u0011O\u0003!!xn\u0015;sS:<G#A\u0012\t\u000fA[\u0011\u0011!C\u0005#\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\u0005\u0011\u0006C\u0001\u0013T\u0013\t!VE\u0001\u0004PE*,7\r\u001e\u0015\u0003\u0017Y\u0003\"a\u0016.\u000e\u0003aS!!\u0017\u0002\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002\\1\naA)\u001a<fY>\u0004XM]!qS\"\u0012\u0001A\u0016"
)
public final class Success {
   public static String toString() {
      return Success$.MODULE$.toString();
   }

   public static int hashCode() {
      return Success$.MODULE$.hashCode();
   }

   public static boolean canEqual(Object var0) {
      return Success$.MODULE$.canEqual(var0);
   }

   public static Iterator<Object> productIterator() {
      return Success$.MODULE$.productIterator();
   }

   public static Object productElement(int var0) {
      return Success$.MODULE$.productElement(var0);
   }

   public static int productArity() {
      return Success$.MODULE$.productArity();
   }

   public static String productPrefix() {
      return Success$.MODULE$.productPrefix();
   }
}
