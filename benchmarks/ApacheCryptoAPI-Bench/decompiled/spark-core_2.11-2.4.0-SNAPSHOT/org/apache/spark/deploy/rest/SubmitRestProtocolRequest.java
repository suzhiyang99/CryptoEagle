package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2a!\u0001\u0002\u0002\u0002\ta!!G*vE6LGOU3tiB\u0013x\u000e^8d_2\u0014V-];fgRT!a\u0001\u0003\u0002\tI,7\u000f\u001e\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0010\u001b\u0005\u0011\u0011B\u0001\t\u0003\u0005e\u0019VOY7jiJ+7\u000f\u001e)s_R|7m\u001c7NKN\u001c\u0018mZ3\t\u000bI\u0001A\u0011\u0001\u000b\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0006\t\u0003\u001d\u0001Aqa\u0006\u0001A\u0002\u0013\u0005\u0001$\u0001\ndY&,g\u000e^*qCJ\\g+\u001a:tS>tW#A\r\u0011\u0005i\u0001cBA\u000e\u001f\u001b\u0005a\"\"A\u000f\u0002\u000bM\u001c\u0017\r\\1\n\u0005}a\u0012A\u0002)sK\u0012,g-\u0003\u0002\"E\t11\u000b\u001e:j]\u001eT!a\b\u000f\t\u000f\u0011\u0002\u0001\u0019!C\u0001K\u000512\r\\5f]R\u001c\u0006/\u0019:l-\u0016\u00148/[8o?\u0012*\u0017\u000f\u0006\u0002'SA\u00111dJ\u0005\u0003Qq\u0011A!\u00168ji\"9!fIA\u0001\u0002\u0004I\u0012a\u0001=%c!1A\u0006\u0001Q!\ne\t1c\u00197jK:$8\u000b]1sWZ+'o]5p]\u0002BQA\f\u0001\u0005R=\n!\u0002Z8WC2LG-\u0019;f)\u00051\u0003"
)
public abstract class SubmitRestProtocolRequest extends SubmitRestProtocolMessage {
   private String clientSparkVersion = null;

   public String clientSparkVersion() {
      return this.clientSparkVersion;
   }

   public void clientSparkVersion_$eq(String x$1) {
      this.clientSparkVersion = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assertFieldIsSet(this.clientSparkVersion(), "clientSparkVersion");
   }
}
