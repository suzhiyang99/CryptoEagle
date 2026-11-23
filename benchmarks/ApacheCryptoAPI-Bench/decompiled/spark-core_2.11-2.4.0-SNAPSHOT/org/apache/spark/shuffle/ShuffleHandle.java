package org.apache.spark.shuffle;

import org.apache.spark.annotation.DeveloperApi;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0002\u0002-\u0011Qb\u00155vM\u001adW\rS1oI2,'BA\u0002\u0005\u0003\u001d\u0019\b.\u001e4gY\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00055\u0019\u0012B\u0001\u000b\u000f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!1\u0002A!b\u0001\n\u00039\u0012!C:ik\u001a4G.Z%e+\u0005A\u0002CA\u0007\u001a\u0013\tQbBA\u0002J]RD\u0001\u0002\b\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u000bg\",hM\u001a7f\u0013\u0012\u0004\u0003\"\u0002\u0010\u0001\t\u0003y\u0012A\u0002\u001fj]&$h\b\u0006\u0002!EA\u0011\u0011\u0005A\u0007\u0002\u0005!)a#\ba\u00011!\u0012\u0001\u0001\n\t\u0003K!j\u0011A\n\u0006\u0003O\u0011\t!\"\u00198o_R\fG/[8o\u0013\tIcE\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public abstract class ShuffleHandle implements Serializable {
   private final int shuffleId;

   public int shuffleId() {
      return this.shuffleId;
   }

   public ShuffleHandle(int shuffleId) {
      this.shuffleId = shuffleId;
   }
}
