package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2Q!\u0001\u0002\u0001\r1\u0011acS5mYN+(-\\5tg&|gNU3ta>t7/\u001a\u0006\u0003\u0007\u0011\tAA]3ti*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059yQ\"\u0001\u0002\n\u0005A\u0011!AG*vE6LGOU3tiB\u0013x\u000e^8d_2\u0014Vm\u001d9p]N,\u0007\"\u0002\n\u0001\t\u0003!\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003U\u0001\"A\u0004\u0001\t\u000f]\u0001\u0001\u0019!C\u00011\u0005a1/\u001e2nSN\u001c\u0018n\u001c8JIV\t\u0011\u0004\u0005\u0002\u001bA9\u00111DH\u0007\u00029)\tQ$A\u0003tG\u0006d\u0017-\u0003\u0002 9\u00051\u0001K]3eK\u001aL!!\t\u0012\u0003\rM#(/\u001b8h\u0015\tyB\u0004C\u0004%\u0001\u0001\u0007I\u0011A\u0013\u0002!M,(-\\5tg&|g.\u00133`I\u0015\fHC\u0001\u0014*!\tYr%\u0003\u0002)9\t!QK\\5u\u0011\u001dQ3%!AA\u0002e\t1\u0001\u001f\u00132\u0011\u0019a\u0003\u0001)Q\u00053\u0005i1/\u001e2nSN\u001c\u0018n\u001c8JI\u0002BQA\f\u0001\u0005R=\n!\u0002Z8WC2LG-\u0019;f)\u00051\u0003"
)
public class KillSubmissionResponse extends SubmitRestProtocolResponse {
   private String submissionId = null;

   public String submissionId() {
      return this.submissionId;
   }

   public void submissionId_$eq(String x$1) {
      this.submissionId = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assertFieldIsSet(this.submissionId(), "submissionId");
      this.assertFieldIsSet(this.success(), "success");
   }
}
