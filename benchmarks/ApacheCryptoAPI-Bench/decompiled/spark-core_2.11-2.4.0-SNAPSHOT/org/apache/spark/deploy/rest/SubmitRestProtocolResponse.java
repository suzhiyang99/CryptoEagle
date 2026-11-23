package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000153a!\u0001\u0002\u0002\u0002\ta!AG*vE6LGOU3tiB\u0013x\u000e^8d_2\u0014Vm\u001d9p]N,'BA\u0002\u0005\u0003\u0011\u0011Xm\u001d;\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f\u001f5\t!!\u0003\u0002\u0011\u0005\tI2+\u001e2nSR\u0014Vm\u001d;Qe>$xnY8m\u001b\u0016\u001c8/Y4f\u0011\u0015\u0011\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000b\u0011\u00059\u0001\u0001bB\f\u0001\u0001\u0004%\t\u0001G\u0001\u0013g\u0016\u0014h/\u001a:Ta\u0006\u00148NV3sg&|g.F\u0001\u001a!\tQ\u0002E\u0004\u0002\u001c=5\tADC\u0001\u001e\u0003\u0015\u00198-\u00197b\u0013\tyB$\u0001\u0004Qe\u0016$WMZ\u0005\u0003C\t\u0012aa\u0015;sS:<'BA\u0010\u001d\u0011\u001d!\u0003\u00011A\u0005\u0002\u0015\nac]3sm\u0016\u00148\u000b]1sWZ+'o]5p]~#S-\u001d\u000b\u0003M%\u0002\"aG\u0014\n\u0005!b\"\u0001B+oSRDqAK\u0012\u0002\u0002\u0003\u0007\u0011$A\u0002yIEBa\u0001\f\u0001!B\u0013I\u0012aE:feZ,'o\u00159be.4VM]:j_:\u0004\u0003b\u0002\u0018\u0001\u0001\u0004%\taL\u0001\bgV\u001c7-Z:t+\u0005\u0001\u0004CA\u00197\u001b\u0005\u0011$BA\u001a5\u0003\u0011a\u0017M\\4\u000b\u0003U\nAA[1wC&\u0011qG\r\u0002\b\u0005>|G.Z1o\u0011\u001dI\u0004\u00011A\u0005\u0002i\n1b];dG\u0016\u001c8o\u0018\u0013fcR\u0011ae\u000f\u0005\bUa\n\t\u00111\u00011\u0011\u0019i\u0004\u0001)Q\u0005a\u0005A1/^2dKN\u001c\b\u0005C\u0004@\u0001\u0001\u0007I\u0011\u0001!\u0002\u001bUt7N\\8x]\u001aKW\r\u001c3t+\u0005\t\u0005cA\u000eC3%\u00111\t\b\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\b\u000b\u0002\u0001\r\u0011\"\u0001G\u0003E)hn\u001b8po:4\u0015.\u001a7eg~#S-\u001d\u000b\u0003M\u001dCqA\u000b#\u0002\u0002\u0003\u0007\u0011\t\u0003\u0004J\u0001\u0001\u0006K!Q\u0001\u000fk:\\gn\\<o\r&,G\u000eZ:!\u0011\u0015Y\u0005\u0001\"\u0015M\u0003)!wNV1mS\u0012\fG/\u001a\u000b\u0002M\u0001"
)
public abstract class SubmitRestProtocolResponse extends SubmitRestProtocolMessage {
   private String serverSparkVersion = null;
   private Boolean success = null;
   private String[] unknownFields = null;

   public String serverSparkVersion() {
      return this.serverSparkVersion;
   }

   public void serverSparkVersion_$eq(String x$1) {
      this.serverSparkVersion = x$1;
   }

   public Boolean success() {
      return this.success;
   }

   public void success_$eq(Boolean x$1) {
      this.success = x$1;
   }

   public String[] unknownFields() {
      return this.unknownFields;
   }

   public void unknownFields_$eq(String[] x$1) {
      this.unknownFields = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assertFieldIsSet(this.serverSparkVersion(), "serverSparkVersion");
   }
}
