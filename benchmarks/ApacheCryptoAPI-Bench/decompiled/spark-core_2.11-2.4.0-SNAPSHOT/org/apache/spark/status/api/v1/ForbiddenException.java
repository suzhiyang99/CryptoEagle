package org.apache.spark.status.api.v1;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000112Q!\u0001\u0002\u0001\u00059\u0011!CR8sE&$G-\u001a8Fq\u000e,\u0007\u000f^5p]*\u00111\u0001B\u0001\u0003mFR!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u000511\u000f^1ukNT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\n\u0003\u0001=\u0001\"\u0001E\f\u000e\u0003EQ!AE\n\u0002\u0005I\u001c(B\u0001\u000b\u0016\u0003\t98OC\u0001\u0017\u0003\u0015Q\u0017M^1y\u0013\tA\u0012CA\fXK\n\f\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8Fq\u000e,\u0007\u000f^5p]\"A!\u0004\u0001B\u0001B\u0003%A$A\u0002ng\u001e\u001c\u0001\u0001\u0005\u0002\u001eG9\u0011a$I\u0007\u0002?)\t\u0001%A\u0003tG\u0006d\u0017-\u0003\u0002#?\u00051\u0001K]3eK\u001aL!\u0001J\u0013\u0003\rM#(/\u001b8h\u0015\t\u0011s\u0004C\u0003(\u0001\u0011\u0005\u0001&\u0001\u0004=S:LGO\u0010\u000b\u0003S-\u0002\"A\u000b\u0001\u000e\u0003\tAQA\u0007\u0014A\u0002q\u0001"
)
public class ForbiddenException extends WebApplicationException {
   public ForbiddenException(String msg) {
      super(Response.status(Status.FORBIDDEN).entity(msg).build());
   }
}
