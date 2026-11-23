package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2Q!\u0001\u0002\u0001\u00051\u0011Q\"\u0012:s_J\u0014Vm\u001d9p]N,'BA\u0002\u0005\u0003\u0011\u0011Xm\u001d;\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f\u001f5\t!!\u0003\u0002\u0011\u0005\tQ2+\u001e2nSR\u0014Vm\u001d;Qe>$xnY8m%\u0016\u001c\bo\u001c8tK\")!\u0003\u0001C\u0001)\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u0016!\tq\u0001\u0001C\u0004\u0018\u0001\u0001\u0007I\u0011\u0001\r\u0002-!Lw\r[3tiB\u0013x\u000e^8d_24VM]:j_:,\u0012!\u0007\t\u00035\u0001r!a\u0007\u0010\u000e\u0003qQ\u0011!H\u0001\u0006g\u000e\fG.Y\u0005\u0003?q\ta\u0001\u0015:fI\u00164\u0017BA\u0011#\u0005\u0019\u0019FO]5oO*\u0011q\u0004\b\u0005\bI\u0001\u0001\r\u0011\"\u0001&\u0003iA\u0017n\u001a5fgR\u0004&o\u001c;pG>dg+\u001a:tS>tw\fJ3r)\t1\u0013\u0006\u0005\u0002\u001cO%\u0011\u0001\u0006\b\u0002\u0005+:LG\u000fC\u0004+G\u0005\u0005\t\u0019A\r\u0002\u0007a$\u0013\u0007\u0003\u0004-\u0001\u0001\u0006K!G\u0001\u0018Q&<\u0007.Z:u!J|Go\\2pYZ+'o]5p]\u0002BQA\f\u0001\u0005R=\n!\u0002Z8WC2LG-\u0019;f)\u00051\u0003"
)
public class ErrorResponse extends SubmitRestProtocolResponse {
   private String highestProtocolVersion = null;

   public String highestProtocolVersion() {
      return this.highestProtocolVersion;
   }

   public void highestProtocolVersion_$eq(String x$1) {
      this.highestProtocolVersion = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assertFieldIsSet(this.message(), "message");
   }
}
