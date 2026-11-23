package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%2A!\u0001\u0002\u0001\u001f\ta\u0011J\u001c9vi6+GO]5dg*\u00111\u0001B\u0001\u0003mFR!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u000511\u000f^1ukNT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\u0002\u0001'\t\u0001\u0001\u0003\u0005\u0002\u0012)5\t!CC\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)\"C\u0001\u0004B]f\u0014VM\u001a\u0005\t/\u0001\u0011)\u0019!C\u00011\u0005I!-\u001f;fgJ+\u0017\rZ\u000b\u00023A\u0011\u0011CG\u0005\u00037I\u0011A\u0001T8oO\"AQ\u0004\u0001B\u0001B\u0003%\u0011$\u0001\u0006csR,7OU3bI\u0002B\u0001b\b\u0001\u0003\u0006\u0004%\t\u0001G\u0001\fe\u0016\u001cwN\u001d3t%\u0016\fG\r\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003\u001a\u00031\u0011XmY8sIN\u0014V-\u00193!\u0011\u0019\u0019\u0003\u0001\"\u0001\tI\u00051A(\u001b8jiz\"2!J\u0014)!\t1\u0003!D\u0001\u0003\u0011\u00159\"\u00051\u0001\u001a\u0011\u0015y\"\u00051\u0001\u001a\u0001"
)
public class InputMetrics {
   private final long bytesRead;
   private final long recordsRead;

   public long bytesRead() {
      return this.bytesRead;
   }

   public long recordsRead() {
      return this.recordsRead;
   }

   public InputMetrics(long bytesRead, long recordsRead) {
      this.bytesRead = bytesRead;
      this.recordsRead = recordsRead;
   }
}
