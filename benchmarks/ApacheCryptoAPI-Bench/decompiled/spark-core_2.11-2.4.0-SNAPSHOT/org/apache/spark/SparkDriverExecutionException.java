package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r2Q!\u0001\u0002\u0001\u0005!\u0011Qd\u00159be.$%/\u001b<fe\u0016CXmY;uS>tW\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sON\u0011\u0001!\u0003\t\u0003\u0015-i\u0011AA\u0005\u0003\u0019\t\u0011ab\u00159be.,\u0005pY3qi&|g\u000e\u0003\u0005\u000f\u0001\t\u0005\t\u0015!\u0003\u0011\u0003\u0015\u0019\u0017-^:f\u0007\u0001\u0001\"!E\u000e\u000f\u0005IAbBA\n\u0017\u001b\u0005!\"BA\u000b\u0010\u0003\u0019a$o\\8u}%\tq#A\u0003tG\u0006d\u0017-\u0003\u0002\u001a5\u00059\u0001/Y2lC\u001e,'\"A\f\n\u0005qi\"!\u0003+ie><\u0018M\u00197f\u0015\tI\"\u0004C\u0003 \u0001\u0011\u0005\u0001%\u0001\u0004=S:LGO\u0010\u000b\u0003C\t\u0002\"A\u0003\u0001\t\u000b9q\u0002\u0019\u0001\t"
)
public class SparkDriverExecutionException extends SparkException {
   public SparkDriverExecutionException(Throwable cause) {
      super("Execution error", cause);
   }
}
