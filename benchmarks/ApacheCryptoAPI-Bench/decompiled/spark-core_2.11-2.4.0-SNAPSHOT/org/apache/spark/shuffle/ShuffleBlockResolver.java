package org.apache.spark.shuffle;

import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.storage.ShuffleBlockId;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0015'\",hM\u001a7f\u00052|7m\u001b*fg>dg/\u001a:\u000b\u0005\r!\u0011aB:ik\u001a4G.\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0006\tI\u0001\u0001\u0001\u0006\u0002\n'\",hM\u001a7f\u0013\u0012\u001c\u0001\u0001\u0005\u0002\r+%\u0011a#\u0004\u0002\u0004\u0013:$\b\"\u0002\r\u0001\r\u0003I\u0012\u0001D4fi\ncwnY6ECR\fGC\u0001\u000e#!\tY\u0002%D\u0001\u001d\u0015\tib$\u0001\u0004ck\u001a4WM\u001d\u0006\u0003?\u0011\tqA\\3uo>\u00148.\u0003\u0002\"9\tiQ*\u00198bO\u0016$')\u001e4gKJDQaI\fA\u0002\u0011\nqA\u00197pG.LE\r\u0005\u0002&Q5\taE\u0003\u0002(\t\u000591\u000f^8sC\u001e,\u0017BA\u0015'\u00059\u0019\u0006.\u001e4gY\u0016\u0014En\\2l\u0013\u0012DQa\u000b\u0001\u0007\u00021\nAa\u001d;paR\tQ\u0006\u0005\u0002\r]%\u0011q&\u0004\u0002\u0005+:LG\u000f"
)
public interface ShuffleBlockResolver {
   ManagedBuffer getBlockData(ShuffleBlockId var1);

   void stop();
}
