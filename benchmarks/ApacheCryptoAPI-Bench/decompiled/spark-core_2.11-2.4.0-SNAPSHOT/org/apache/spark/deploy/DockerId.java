package org.apache.spark.deploy;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2A!\u0001\u0002\u0005\u0017\tAAi\\2lKJLEM\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\u0005\t'\u0001\u0011)\u0019!C\u0001)\u0005\u0011\u0011\u000eZ\u000b\u0002+A\u0011a#\u0007\b\u0003\u001b]I!\u0001\u0007\b\u0002\rA\u0013X\rZ3g\u0013\tQ2D\u0001\u0004TiJLgn\u001a\u0006\u000319A\u0001\"\b\u0001\u0003\u0002\u0003\u0006I!F\u0001\u0004S\u0012\u0004\u0003\"B\u0010\u0001\t\u0003\u0001\u0013A\u0002\u001fj]&$h\b\u0006\u0002\"GA\u0011!\u0005A\u0007\u0002\u0005!)1C\ba\u0001+!)Q\u0005\u0001C!M\u0005AAo\\*ue&tw\rF\u0001\u0016\u0001"
)
public class DockerId {
   private final String id;

   public String id() {
      return this.id;
   }

   public String toString() {
      return this.id();
   }

   public DockerId(String id) {
      this.id = id;
   }
}
