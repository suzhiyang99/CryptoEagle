package org.apache.spark.status;

import org.apache.spark.util.kvstore.KVIndex;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a2Q!\u0001\u0002\u0001\t)\u0011!\"\u00119q'VlW.\u0019:z\u0015\t\u0019A!\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011I\u0001!Q1A\u0005\u0002Q\t\u0001C\\;n\u0007>l\u0007\u000f\\3uK\u0012TuNY:\u0004\u0001U\tQ\u0003\u0005\u0002\r-%\u0011q#\u0004\u0002\u0004\u0013:$\b\u0002C\r\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002#9,XnQ8na2,G/\u001a3K_\n\u001c\b\u0005\u0003\u0005\u001c\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003IqW/\\\"p[BdW\r^3e'R\fw-Z:\t\u0011u\u0001!\u0011!Q\u0001\nU\t1C\\;n\u0007>l\u0007\u000f\\3uK\u0012\u001cF/Y4fg\u0002BQa\b\u0001\u0005\u0002\u0001\na\u0001P5oSRtDcA\u0011$IA\u0011!\u0005A\u0007\u0002\u0005!)!C\ba\u0001+!)1D\ba\u0001+!)a\u0005\u0001C\u0001O\u0005\u0011\u0011\u000eZ\u000b\u0002QA\u0011\u0011\u0006\f\b\u0003\u0019)J!aK\u0007\u0002\rA\u0013X\rZ3g\u0013\ticF\u0001\u0004TiJLgn\u001a\u0006\u0003W5A#!\n\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014aB6wgR|'/\u001a\u0006\u0003k\u0011\tA!\u001e;jY&\u0011qG\r\u0002\b\u0017ZKe\u000eZ3y\u0001"
)
public class AppSummary {
   private final int numCompletedJobs;
   private final int numCompletedStages;

   public int numCompletedJobs() {
      return this.numCompletedJobs;
   }

   public int numCompletedStages() {
      return this.numCompletedStages;
   }

   @KVIndex
   public String id() {
      return AppSummary.class.getName();
   }

   public AppSummary(int numCompletedJobs, int numCompletedStages) {
      this.numCompletedJobs = numCompletedJobs;
      this.numCompletedStages = numCompletedStages;
   }
}
