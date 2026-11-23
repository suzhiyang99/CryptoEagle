package org.apache.spark.util;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\u0011Q\u0011aC'f[>\u0014\u0018\u0010U1sC6T!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOB\u00111\u0002D\u0007\u0002\u0005\u00191QB\u0001E\u0001\t9\u00111\"T3n_JL\b+\u0019:b[N\u0011Ab\u0004\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000bYaA\u0011\u0001\r\u0002\rqJg.\u001b;?\u0007\u0001!\u0012A\u0003\u0005\u000651!\taG\u0001\bk:\f\u0007\u000f\u001d7z)\ta\"\u0005E\u0002\u0011;}I!AH\t\u0003\r=\u0003H/[8o!\t\u0001\u0002%\u0003\u0002\"#\t\u0019\u0011J\u001c;\t\u000b\rJ\u0002\u0019\u0001\u0013\u0002\u0007M$(\u000f\u0005\u0002&Q9\u0011\u0001CJ\u0005\u0003OE\ta\u0001\u0015:fI\u00164\u0017BA\u0015+\u0005\u0019\u0019FO]5oO*\u0011q%\u0005"
)
public final class MemoryParam {
   public static Option<Object> unapply(String var0) {
      return MemoryParam$.MODULE$.unapply(var0);
   }
}
