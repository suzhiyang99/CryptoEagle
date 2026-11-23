package org.apache.spark.storage;

import org.apache.spark.SparkException;
import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001%2A!\u0001\u0002\u0001\u0017\t\u0019RK\u001c:fG><g.\u001b>fI\ncwnY6JI*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\b\u000e\u0003\u0011I!a\u0004\u0003\u0003\u001dM\u0003\u0018M]6Fq\u000e,\u0007\u000f^5p]\"A\u0011\u0003\u0001B\u0001B\u0003%!#\u0001\u0003oC6,\u0007CA\n\u001a\u001d\t!r#D\u0001\u0016\u0015\u00051\u0012!B:dC2\f\u0017B\u0001\r\u0016\u0003\u0019\u0001&/\u001a3fM&\u0011!d\u0007\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005a)\u0002\"B\u000f\u0001\t\u0003q\u0012A\u0002\u001fj]&$h\b\u0006\u0002 CA\u0011\u0001\u0005A\u0007\u0002\u0005!)\u0011\u0003\ba\u0001%!\u0012\u0001a\t\t\u0003I\u001dj\u0011!\n\u0006\u0003M\u0011\t!\"\u00198o_R\fG/[8o\u0013\tASE\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class UnrecognizedBlockId extends SparkException {
   public UnrecognizedBlockId(String name) {
      super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to parse ", " into a block ID"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})));
   }
}
