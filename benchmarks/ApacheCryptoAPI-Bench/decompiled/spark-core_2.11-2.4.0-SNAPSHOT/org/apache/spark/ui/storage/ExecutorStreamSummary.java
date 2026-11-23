package org.apache.spark.ui.storage;

import org.apache.spark.status.StreamBlockData;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001!3A!\u0001\u0002\u0005\u001b\t)R\t_3dkR|'o\u0015;sK\u0006l7+^7nCJL(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u0011U\u0001!\u0011!Q\u0001\nY\taA\u00197pG.\u001c\bcA\f E9\u0011\u0001$\b\b\u00033qi\u0011A\u0007\u0006\u000371\ta\u0001\u0010:p_Rt\u0014\"A\t\n\u0005y\u0001\u0012a\u00029bG.\fw-Z\u0005\u0003A\u0005\u00121aU3r\u0015\tq\u0002\u0003\u0005\u0002$M5\tAE\u0003\u0002&\r\u000511\u000f^1ukNL!a\n\u0013\u0003\u001fM#(/Z1n\u00052|7m\u001b#bi\u0006DQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtDCA\u0016.!\ta\u0003!D\u0001\u0003\u0011\u0015)\u0002\u00061\u0001\u0017\u0011\u0015y\u0003\u0001\"\u00011\u0003))\u00070Z2vi>\u0014\u0018\nZ\u000b\u0002cA\u0011!'\u000e\b\u0003\u001fMJ!\u0001\u000e\t\u0002\rA\u0013X\rZ3g\u0013\t1tG\u0001\u0004TiJLgn\u001a\u0006\u0003iAAQ!\u000f\u0001\u0005\u0002A\n\u0001\u0002\\8dCRLwN\u001c\u0005\u0006w\u0001!\t\u0001P\u0001\ri>$\u0018\r\\'f[NK'0Z\u000b\u0002{A\u0011qBP\u0005\u0003\u007fA\u0011A\u0001T8oO\")\u0011\t\u0001C\u0001y\u0005iAo\u001c;bY\u0012K7o[*ju\u0016DQa\u0011\u0001\u0005\u0002\u0011\u000bqB\\;n'R\u0014X-Y7CY>\u001c7n]\u000b\u0002\u000bB\u0011qBR\u0005\u0003\u000fB\u00111!\u00138u\u0001"
)
public class ExecutorStreamSummary {
   private final Seq<StreamBlockData> blocks;

   public String executorId() {
      return ((StreamBlockData)this.blocks.head()).executorId();
   }

   public String location() {
      return ((StreamBlockData)this.blocks.head()).hostPort();
   }

   public long totalMemSize() {
      return BoxesRunTime.unboxToLong(((TraversableOnce)this.blocks.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(StreamBlockData x$6) {
            return x$6.memSize();
         }
      }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
   }

   public long totalDiskSize() {
      return BoxesRunTime.unboxToLong(((TraversableOnce)this.blocks.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(StreamBlockData x$7) {
            return x$7.diskSize();
         }
      }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
   }

   public int numStreamBlocks() {
      return this.blocks.size();
   }

   public ExecutorStreamSummary(Seq<StreamBlockData> blocks) {
      this.blocks = blocks;
   }
}
