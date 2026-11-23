package org.apache.spark.status.api.v1;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Q2Q!\u0001\u0002\u0001\u00059\u0011QCQ1e!\u0006\u0014\u0018-\\3uKJ,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0011\u0001a\u0004\t\u0003!]i\u0011!\u0005\u0006\u0003%M\t!A]:\u000b\u0005Q)\u0012AA<t\u0015\u00051\u0012!\u00026bm\u0006D\u0018B\u0001\r\u0012\u0005]9VMY!qa2L7-\u0019;j_:,\u0005pY3qi&|g\u000e\u0003\u0005\u001b\u0001\t\u0005\t\u0015!\u0003\u001d\u0003\ri7oZ\u0002\u0001!\ti2E\u0004\u0002\u001fC5\tqDC\u0001!\u0003\u0015\u00198-\u00197b\u0013\t\u0011s$\u0001\u0004Qe\u0016$WMZ\u0005\u0003I\u0015\u0012aa\u0015;sS:<'B\u0001\u0012 \u0011\u00159\u0003\u0001\"\u0001)\u0003\u0019a\u0014N\\5u}Q\u0011\u0011f\u000b\t\u0003U\u0001i\u0011A\u0001\u0005\u00065\u0019\u0002\r\u0001\b\u0005\u0006O\u0001!\t!\f\u000b\u0005S9\u0002$\u0007C\u00030Y\u0001\u0007A$A\u0003qCJ\fW\u000eC\u00032Y\u0001\u0007A$A\u0002fqBDQa\r\u0017A\u0002q\ta!Y2uk\u0006d\u0007"
)
public class BadParameterException extends WebApplicationException {
   public BadParameterException(String msg) {
      super(new IllegalArgumentException(msg), Response.status(Status.BAD_REQUEST).entity(new ErrorWrapper(msg)).build());
   }

   public BadParameterException(String param, String exp, String actual) {
      this((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Bad value for parameter \"", "\".  Expected a ", ", got \"", "\""})))).raw(Predef$.MODULE$.genericWrapArray(new Object[]{param, exp, actual})));
   }
}
