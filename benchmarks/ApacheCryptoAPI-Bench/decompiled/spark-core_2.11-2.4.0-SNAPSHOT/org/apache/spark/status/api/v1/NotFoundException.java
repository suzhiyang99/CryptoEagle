package org.apache.spark.status.api.v1;

import java.util.NoSuchElementException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000112Q!\u0001\u0002\u0001\u00059\u0011\u0011CT8u\r>,h\u000eZ#yG\u0016\u0004H/[8o\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001fA\u0011\u0001cF\u0007\u0002#)\u0011!cE\u0001\u0003eNT!\u0001F\u000b\u0002\u0005]\u001c(\"\u0001\f\u0002\u000b)\fg/\u0019=\n\u0005a\t\"aF,fE\u0006\u0003\b\u000f\\5dCRLwN\\#yG\u0016\u0004H/[8o\u0011!Q\u0002A!A!\u0002\u0013a\u0012aA7tO\u000e\u0001\u0001CA\u000f$\u001d\tq\u0012%D\u0001 \u0015\u0005\u0001\u0013!B:dC2\f\u0017B\u0001\u0012 \u0003\u0019\u0001&/\u001a3fM&\u0011A%\n\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tz\u0002\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\b\u0006\u0002*WA\u0011!\u0006A\u0007\u0002\u0005!)!D\na\u00019\u0001"
)
public class NotFoundException extends WebApplicationException {
   public NotFoundException(String msg) {
      super(new NoSuchElementException(msg), Response.status(Status.NOT_FOUND).entity(new ErrorWrapper(msg)).build());
   }
}
