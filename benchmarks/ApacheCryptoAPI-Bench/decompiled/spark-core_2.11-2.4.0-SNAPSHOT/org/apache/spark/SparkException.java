package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E2A!\u0001\u0002\u0001\u0013\tq1\u000b]1sW\u0016C8-\u001a9uS>t'BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0003\t\u0003\u0017Uq!\u0001\u0004\n\u000f\u00055\u0001R\"\u0001\b\u000b\u0005=A\u0011A\u0002\u001fs_>$h(C\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019B#A\u0004qC\u000e\\\u0017mZ3\u000b\u0003EI!AF\f\u0003\u0013\u0015C8-\u001a9uS>t'BA\n\u0015\u0011!I\u0002A!A!\u0002\u0013Q\u0012aB7fgN\fw-\u001a\t\u00037}q!\u0001H\u000f\u000e\u0003QI!A\b\u000b\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0013E\u0001\u0004TiJLgn\u001a\u0006\u0003=QA\u0001b\t\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\u0006G\u0006,8/\u001a\t\u0003\u0017\u0015J!AJ\f\u0003\u0013QC'o\\<bE2,\u0007\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\bF\u0002+Y5\u0002\"a\u000b\u0001\u000e\u0003\tAQ!G\u0014A\u0002iAQaI\u0014A\u0002\u0011BQ\u0001\u000b\u0001\u0005\u0002=\"\"A\u000b\u0019\t\u000beq\u0003\u0019\u0001\u000e"
)
public class SparkException extends Exception {
   public SparkException(String message, Throwable cause) {
      super(message, cause);
   }

   public SparkException(String message) {
      this(message, (Throwable)null);
   }
}
