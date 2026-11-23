package org.apache.spark.util.random;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\u0019a\u0011!\u0004)pSN\u001cxN\u001c\"pk:$7O\u0003\u0002\u0004\t\u00051!/\u00198e_6T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011Q\u0002U8jgN|gNQ8v]\u0012\u001c8C\u0001\b\u0012!\t\u0011R#D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019\te.\u001f*fM\")\u0001D\u0004C\u00015\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u0011\u0015ab\u0002\"\u0001\u001e\u000359W\r\u001e'po\u0016\u0014(i\\;oIR\u0011a$\t\t\u0003%}I!\u0001I\n\u0003\r\u0011{WO\u00197f\u0011\u0015\u00113\u00041\u0001\u001f\u0003\u0005\u0019\b\"\u0002\u0013\u000f\t\u0003)\u0013!D4fiV\u0003\b/\u001a:C_VtG\r\u0006\u0002\u001fM!)!e\ta\u0001=!)\u0001F\u0004C\u0005S\u00051a.^7Ti\u0012$\"A\b\u0016\t\u000b\t:\u0003\u0019\u0001\u0010"
)
public final class PoissonBounds {
   public static double getUpperBound(double var0) {
      return PoissonBounds$.MODULE$.getUpperBound(var0);
   }

   public static double getLowerBound(double var0) {
      return PoissonBounds$.MODULE$.getLowerBound(var0);
   }
}
