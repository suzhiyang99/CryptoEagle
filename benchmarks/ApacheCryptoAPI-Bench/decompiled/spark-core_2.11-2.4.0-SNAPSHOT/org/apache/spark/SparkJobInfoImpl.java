package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M2A!\u0001\u0002\u0005\u0013\t\u00012\u000b]1sW*{'-\u00138g_&k\u0007\u000f\u001c\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sO\u000e\u00011c\u0001\u0001\u000b!A\u00111BD\u0007\u0002\u0019)\tQ\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0010\u0019\t1\u0011I\\=SK\u001a\u0004\"!\u0005\n\u000e\u0003\tI!a\u0005\u0002\u0003\u0019M\u0003\u0018M]6K_\nLeNZ8\t\u0011U\u0001!Q1A\u0005\u0002Y\tQA[8c\u0013\u0012,\u0012a\u0006\t\u0003\u0017aI!!\u0007\u0007\u0003\u0007%sG\u000f\u0003\u0005\u001c\u0001\t\u0005\t\u0015!\u0003\u0018\u0003\u0019QwNY%eA!AQ\u0004\u0001BC\u0002\u0013\u0005a$\u0001\u0005ti\u0006<W-\u00133t+\u0005y\u0002cA\u0006!/%\u0011\u0011\u0005\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\tG\u0001\u0011\t\u0011)A\u0005?\u0005I1\u000f^1hK&#7\u000f\t\u0005\tK\u0001\u0011)\u0019!C\u0001M\u000511\u000f^1ukN,\u0012a\n\t\u0003#!J!!\u000b\u0002\u0003%){'-\u0012=fGV$\u0018n\u001c8Ti\u0006$Xo\u001d\u0005\tW\u0001\u0011\t\u0011)A\u0005O\u000591\u000f^1ukN\u0004\u0003\"B\u0017\u0001\t\u0003q\u0013A\u0002\u001fj]&$h\b\u0006\u00030aE\u0012\u0004CA\t\u0001\u0011\u0015)B\u00061\u0001\u0018\u0011\u0015iB\u00061\u0001 \u0011\u0015)C\u00061\u0001(\u0001"
)
public class SparkJobInfoImpl implements SparkJobInfo {
   private final int jobId;
   private final int[] stageIds;
   private final JobExecutionStatus status;

   public int jobId() {
      return this.jobId;
   }

   public int[] stageIds() {
      return this.stageIds;
   }

   public JobExecutionStatus status() {
      return this.status;
   }

   public SparkJobInfoImpl(int jobId, int[] stageIds, JobExecutionStatus status) {
      this.jobId = jobId;
      this.stageIds = stageIds;
      this.status = status;
   }
}
