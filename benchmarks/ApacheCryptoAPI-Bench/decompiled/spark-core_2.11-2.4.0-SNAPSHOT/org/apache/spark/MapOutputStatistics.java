package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0001\u0005!\u00111#T1q\u001fV$\b/\u001e;Ti\u0006$\u0018n\u001d;jGNT!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\n\u0003\u0001%\u0001\"AC\u0007\u000e\u0003-Q\u0011\u0001D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001d-\u0011a!\u00118z%\u00164\u0007\u0002\u0003\t\u0001\u0005\u000b\u0007I\u0011\u0001\n\u0002\u0013MDWO\u001a4mK&#7\u0001A\u000b\u0002'A\u0011!\u0002F\u0005\u0003+-\u00111!\u00138u\u0011!9\u0002A!A!\u0002\u0013\u0019\u0012AC:ik\u001a4G.Z%eA!A\u0011\u0004\u0001BC\u0002\u0013\u0005!$\u0001\ncsR,7OQ=QCJ$\u0018\u000e^5p]&#W#A\u000e\u0011\u0007)ab$\u0003\u0002\u001e\u0017\t)\u0011I\u001d:bsB\u0011!bH\u0005\u0003A-\u0011A\u0001T8oO\"A!\u0005\u0001B\u0001B\u0003%1$A\ncsR,7OQ=QCJ$\u0018\u000e^5p]&#\u0007\u0005C\u0003%\u0001\u0011\u0005Q%\u0001\u0004=S:LGO\u0010\u000b\u0004M!J\u0003CA\u0014\u0001\u001b\u0005\u0011\u0001\"\u0002\t$\u0001\u0004\u0019\u0002\"B\r$\u0001\u0004Y\u0002"
)
public class MapOutputStatistics {
   private final int shuffleId;
   private final long[] bytesByPartitionId;

   public int shuffleId() {
      return this.shuffleId;
   }

   public long[] bytesByPartitionId() {
      return this.bytesByPartitionId;
   }

   public MapOutputStatistics(int shuffleId, long[] bytesByPartitionId) {
      this.shuffleId = shuffleId;
      this.bytesByPartitionId = bytesByPartitionId;
   }
}
