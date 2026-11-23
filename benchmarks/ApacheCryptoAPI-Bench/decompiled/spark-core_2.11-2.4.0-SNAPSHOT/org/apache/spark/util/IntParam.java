package org.apache.spark.util;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001C%oiB\u000b'/Y7\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003\u0011%sG\u000fU1sC6\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000biaA\u0011A\u000e\u0002\u000fUt\u0017\r\u001d9msR\u0011AD\t\t\u0004!uy\u0012B\u0001\u0010\u0012\u0005\u0019y\u0005\u000f^5p]B\u0011\u0001\u0003I\u0005\u0003CE\u00111!\u00138u\u0011\u0015\u0019\u0013\u00041\u0001%\u0003\r\u0019HO\u001d\t\u0003K!r!\u0001\u0005\u0014\n\u0005\u001d\n\u0012A\u0002)sK\u0012,g-\u0003\u0002*U\t11\u000b\u001e:j]\u001eT!aJ\t"
)
public final class IntParam {
   public static Option<Object> unapply(String var0) {
      return IntParam$.MODULE$.unapply(var0);
   }
}
