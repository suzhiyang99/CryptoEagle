package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2Q!\u0001\u0002\u0001\r1\u0011\u0001d\u0011:fCR,7+\u001e2nSN\u001c\u0018n\u001c8SKN\u0004xN\\:f\u0015\t\u0019A!\u0001\u0003sKN$(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\u0005%\u0011\u0001C\u0001\u0002\u001b'V\u0014W.\u001b;SKN$\bK]8u_\u000e|GNU3ta>t7/\u001a\u0005\u0006%\u0001!\t\u0001F\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tQ\u0003\u0005\u0002\u000f\u0001!9q\u0003\u0001a\u0001\n\u0003A\u0012\u0001D:vE6L7o]5p]&#W#A\r\u0011\u0005i\u0001cBA\u000e\u001f\u001b\u0005a\"\"A\u000f\u0002\u000bM\u001c\u0017\r\\1\n\u0005}a\u0012A\u0002)sK\u0012,g-\u0003\u0002\"E\t11\u000b\u001e:j]\u001eT!a\b\u000f\t\u000f\u0011\u0002\u0001\u0019!C\u0001K\u0005\u00012/\u001e2nSN\u001c\u0018n\u001c8JI~#S-\u001d\u000b\u0003M%\u0002\"aG\u0014\n\u0005!b\"\u0001B+oSRDqAK\u0012\u0002\u0002\u0003\u0007\u0011$A\u0002yIEBa\u0001\f\u0001!B\u0013I\u0012!D:vE6L7o]5p]&#\u0007\u0005C\u0003/\u0001\u0011Es&\u0001\u0006e_Z\u000bG.\u001b3bi\u0016$\u0012A\n"
)
public class CreateSubmissionResponse extends SubmitRestProtocolResponse {
   private String submissionId = null;

   public String submissionId() {
      return this.submissionId;
   }

   public void submissionId_$eq(String x$1) {
      this.submissionId = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assertFieldIsSet(this.success(), "success");
   }
}
