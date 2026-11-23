package org.apache.spark.api.java;

import org.apache.spark.SparkContext;
import org.apache.spark.SparkJobInfo;
import org.apache.spark.SparkStageInfo;
import scala.Predef$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d3A!\u0001\u0002\u0001\u001b\t1\"*\u0019<b'B\f'o[*uCR,8\u000f\u0016:bG.,'O\u0003\u0002\u0004\t\u0005!!.\u0019<b\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\t\u0001a\u0002\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\u0005\t+\u0001\u0011\t\u0011)A\u0005-\u0005\u00111o\u0019\t\u0003/ai\u0011AB\u0005\u00033\u0019\u0011Ab\u00159be.\u001cuN\u001c;fqRDaa\u0007\u0001\u0005\u0002\u0019a\u0012A\u0002\u001fj]&$h\b\u0006\u0002\u001e?A\u0011a\u0004A\u0007\u0002\u0005!)QC\u0007a\u0001-!)\u0011\u0005\u0001C\u0001E\u0005\tr-\u001a;K_\nLEm\u001d$pe\u001e\u0013x.\u001e9\u0015\u0005\rJ\u0003cA\b%M%\u0011Q\u0005\u0005\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003\u001f\u001dJ!\u0001\u000b\t\u0003\u0007%sG\u000fC\u0003+A\u0001\u00071&\u0001\u0005k_\n<%o\\;q!\tasF\u0004\u0002\u0010[%\u0011a\u0006E\u0001\u0007!J,G-\u001a4\n\u0005A\n$AB*ue&twM\u0003\u0002/!!)1\u0007\u0001C\u0001i\u0005\tr-\u001a;BGRLg/Z*uC\u001e,\u0017\nZ:\u0015\u0003\rBQA\u000e\u0001\u0005\u0002Q\nqbZ3u\u0003\u000e$\u0018N^3K_\nLEm\u001d\u0005\u0006q\u0001!\t!O\u0001\u000bO\u0016$(j\u001c2J]\u001a|GC\u0001\u001e>!\t92(\u0003\u0002=\r\ta1\u000b]1sW*{'-\u00138g_\")ah\u000ea\u0001M\u0005)!n\u001c2JI\")\u0001\t\u0001C\u0001\u0003\u0006aq-\u001a;Ti\u0006<W-\u00138g_R\u0011!)\u0012\t\u0003/\rK!\u0001\u0012\u0004\u0003\u001dM\u0003\u0018M]6Ti\u0006<W-\u00138g_\")ai\u0010a\u0001M\u000591\u000f^1hK&#\u0007"
)
public class JavaSparkStatusTracker {
   private final SparkContext sc;

   public int[] getJobIdsForGroup(String jobGroup) {
      return this.sc.statusTracker().getJobIdsForGroup(jobGroup);
   }

   public int[] getActiveStageIds() {
      return this.sc.statusTracker().getActiveStageIds();
   }

   public int[] getActiveJobIds() {
      return this.sc.statusTracker().getActiveJobIds();
   }

   public SparkJobInfo getJobInfo(int jobId) {
      return (SparkJobInfo)this.sc.statusTracker().getJobInfo(jobId).orNull(Predef$.MODULE$.$conforms());
   }

   public SparkStageInfo getStageInfo(int stageId) {
      return (SparkStageInfo)this.sc.statusTracker().getStageInfo(stageId).orNull(Predef$.MODULE$.$conforms());
   }

   public JavaSparkStatusTracker(SparkContext sc) {
      this.sc = sc;
   }
}
