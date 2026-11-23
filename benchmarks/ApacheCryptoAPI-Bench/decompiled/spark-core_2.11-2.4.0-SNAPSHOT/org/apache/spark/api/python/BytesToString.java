package org.apache.spark.api.python;

import java.nio.charset.StandardCharsets;
import org.apache.spark.api.java.function.Function;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M2A!\u0001\u0002\u0005\u001b\ti!)\u001f;fgR{7\u000b\u001e:j]\u001eT!a\u0001\u0003\u0002\rALH\u000f[8o\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\r\u0001a\u0002\u0006\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\tUQBDI\u0007\u0002-)\u0011q\u0003G\u0001\tMVt7\r^5p]*\u0011\u0011\u0004B\u0001\u0005U\u00064\u0018-\u0003\u0002\u001c-\tAa)\u001e8di&|g\u000eE\u0002\u0010;}I!A\b\t\u0003\u000b\u0005\u0013(/Y=\u0011\u0005=\u0001\u0013BA\u0011\u0011\u0005\u0011\u0011\u0015\u0010^3\u0011\u0005\r2cBA\b%\u0013\t)\u0003#\u0001\u0004Qe\u0016$WMZ\u0005\u0003O!\u0012aa\u0015;sS:<'BA\u0013\u0011\u0011\u0015Q\u0003\u0001\"\u0001,\u0003\u0019a\u0014N\\5u}Q\tA\u0006\u0005\u0002.\u00015\t!\u0001C\u00030\u0001\u0011\u0005\u0003'\u0001\u0003dC2dGC\u0001\u00122\u0011\u0015\u0011d\u00061\u0001\u001d\u0003\r\t'O\u001d"
)
public class BytesToString implements Function<byte[], String> {
   public String call(byte[] arr) {
      return new String(arr, StandardCharsets.UTF_8);
   }
}
