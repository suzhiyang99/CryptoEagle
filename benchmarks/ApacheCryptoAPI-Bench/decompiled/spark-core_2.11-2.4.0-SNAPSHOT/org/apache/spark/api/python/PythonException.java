package org.apache.spark.api.python;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I2Q!\u0001\u0002\u0001\r1\u0011q\u0002U=uQ>tW\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001deq!a\u0004\f\u000f\u0005A!R\"A\t\u000b\u0005I\u0019\u0012A\u0002\u001fs_>$hh\u0001\u0001\n\u0003U\tQa]2bY\u0006L!a\u0006\r\u0002\u000fA\f7m[1hK*\tQ#\u0003\u0002\u001b7\t\u0001\"+\u001e8uS6,W\t_2faRLwN\u001c\u0006\u0003/aA\u0001\"\b\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0004[N<\u0007CA\u0010$\u001d\t\u0001\u0013%D\u0001\u0019\u0013\t\u0011\u0003$\u0001\u0004Qe\u0016$WMZ\u0005\u0003I\u0015\u0012aa\u0015;sS:<'B\u0001\u0012\u0019\u0011!9\u0003A!A!\u0002\u0013A\u0013!B2bkN,\u0007C\u0001\b*\u0013\tQ3DA\u0005Fq\u000e,\u0007\u000f^5p]\")A\u0006\u0001C\u0001[\u00051A(\u001b8jiz\"2A\f\u00192!\ty\u0003!D\u0001\u0003\u0011\u0015i2\u00061\u0001\u001f\u0011\u001593\u00061\u0001)\u0001"
)
public class PythonException extends RuntimeException {
   public PythonException(String msg, Exception cause) {
      super(msg, cause);
   }
}
