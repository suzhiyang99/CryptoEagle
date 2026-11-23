package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000192A!\u0001\u0002\u0001\u001f\t\u00192\u000b[;gM2,wK]5uK6+GO]5dg*\u00111\u0001B\u0001\u0003mFR!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u000511\u000f^1ukNT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\u0002\u0001'\t\u0001\u0001\u0003\u0005\u0002\u0012)5\t!CC\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)\"C\u0001\u0004B]f\u0014VM\u001a\u0005\t/\u0001\u0011)\u0019!C\u00011\u0005a!-\u001f;fg^\u0013\u0018\u000e\u001e;f]V\t\u0011\u0004\u0005\u0002\u00125%\u00111D\u0005\u0002\u0005\u0019>tw\r\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u001a\u00035\u0011\u0017\u0010^3t/JLG\u000f^3oA!Aq\u0004\u0001BC\u0002\u0013\u0005\u0001$A\u0005xe&$X\rV5nK\"A\u0011\u0005\u0001B\u0001B\u0003%\u0011$\u0001\u0006xe&$X\rV5nK\u0002B\u0001b\t\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u000fe\u0016\u001cwN\u001d3t/JLG\u000f^3o\u0011!)\u0003A!A!\u0002\u0013I\u0012a\u0004:fG>\u0014Hm],sSR$XM\u001c\u0011\t\r\u001d\u0002A\u0011\u0001\u0005)\u0003\u0019a\u0014N\\5u}Q!\u0011f\u000b\u0017.!\tQ\u0003!D\u0001\u0003\u0011\u00159b\u00051\u0001\u001a\u0011\u0015yb\u00051\u0001\u001a\u0011\u0015\u0019c\u00051\u0001\u001a\u0001"
)
public class ShuffleWriteMetrics {
   private final long bytesWritten;
   private final long writeTime;
   private final long recordsWritten;

   public long bytesWritten() {
      return this.bytesWritten;
   }

   public long writeTime() {
      return this.writeTime;
   }

   public long recordsWritten() {
      return this.recordsWritten;
   }

   public ShuffleWriteMetrics(long bytesWritten, long writeTime, long recordsWritten) {
      this.bytesWritten = bytesWritten;
      this.writeTime = writeTime;
      this.recordsWritten = recordsWritten;
   }
}
