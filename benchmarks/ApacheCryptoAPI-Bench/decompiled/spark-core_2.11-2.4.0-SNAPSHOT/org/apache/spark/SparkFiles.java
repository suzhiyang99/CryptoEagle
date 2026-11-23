package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0019:Q!\u0001\u0002\t\u0002%\t!b\u00159be.4\u0015\u000e\\3t\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001\u0001\t\u0003\u0015-i\u0011A\u0001\u0004\u0006\u0019\tA\t!\u0004\u0002\u000b'B\f'o\u001b$jY\u0016\u001c8CA\u0006\u000f!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fM\")Qc\u0003C\u0001-\u00051A(\u001b8jiz\"\u0012!\u0003\u0005\u00061-!\t!G\u0001\u0004O\u0016$HC\u0001\u000e\"!\tYbD\u0004\u0002\u00109%\u0011Q\u0004E\u0001\u0007!J,G-\u001a4\n\u0005}\u0001#AB*ue&twM\u0003\u0002\u001e!!)!e\u0006a\u00015\u0005Aa-\u001b7f]\u0006lW\rC\u0003%\u0017\u0011\u0005Q%\u0001\thKR\u0014vn\u001c;ESJ,7\r^8ssR\t!\u0004"
)
public final class SparkFiles {
   public static String getRootDirectory() {
      return SparkFiles$.MODULE$.getRootDirectory();
   }

   public static String get(String var0) {
      return SparkFiles$.MODULE$.get(var0);
   }
}
