package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r2Q!\u0001\u0002\u0001\u00051\u0011qdU;c[&$(+Z:u\u001b&\u001c8/\u001b8h\r&,G\u000eZ#yG\u0016\u0004H/[8o\u0015\t\u0019A!\u0001\u0003sKN$(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\u0005%\u0011\u0001C\u0001\u0002\u001c'V\u0014W.\u001b;SKN$\bK]8u_\u000e|G.\u0012=dKB$\u0018n\u001c8\t\u0011I\u0001!\u0011!Q\u0001\nQ\tq!\\3tg\u0006<Wm\u0001\u0001\u0011\u0005UYbB\u0001\f\u001a\u001b\u00059\"\"\u0001\r\u0002\u000bM\u001c\u0017\r\\1\n\u0005i9\u0012A\u0002)sK\u0012,g-\u0003\u0002\u001d;\t11\u000b\u001e:j]\u001eT!AG\f\t\u000b}\u0001A\u0011\u0001\u0011\u0002\rqJg.\u001b;?)\t\t#\u0005\u0005\u0002\u000f\u0001!)!C\ba\u0001)\u0001"
)
public class SubmitRestMissingFieldException extends SubmitRestProtocolException {
   public SubmitRestMissingFieldException(String message) {
      super(message, SubmitRestProtocolException$.MODULE$.$lessinit$greater$default$2());
   }
}
