package org.apache.spark.scheduler;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@JsonTypeInfo(
   use = Id.CLASS,
   include = As.PROPERTY,
   property = "Event"
)
@ScalaSignature(
   bytes = "\u0006\u0001\t3q!\u0001\u0002\u0011\u0002\u0007\u00051B\u0001\nTa\u0006\u00148\u000eT5ti\u0016tWM]#wK:$(BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0019\u0002\u0001\"\u0001\u0015\u0003\u0019!\u0013N\\5uIQ\tQ\u0003\u0005\u0002\u000e-%\u0011qC\u0004\u0002\u0005+:LG\u000f\u0003\u0004\u001a\u0001\u0011EAAG\u0001\tY><WI^3oiV\t1\u0004\u0005\u0002\u000e9%\u0011QD\u0004\u0002\b\u0005>|G.Z1oQ!\u0001qd\u000b\u00174ieR\u0004C\u0001\u0011*\u001b\u0005\t#B\u0001\u0012$\u0003)\tgN\\8uCRLwN\u001c\u0006\u0003I\u0015\nqA[1dWN|gN\u0003\u0002'O\u0005Ia-Y:uKJDX\u000e\u001c\u0006\u0002Q\u0005\u00191m\\7\n\u0005)\n#\u0001\u0004&t_:$\u0016\u0010]3J]\u001a|\u0017aA;tK\u0012\nQ&\u0003\u0002/_\u0005)1\tT!T'*\u0011\u0001'M\u0001\u0003\u0013\u0012T!AM\u0011\u0002\u0019)\u001bxN\u001c+za\u0016LeNZ8\u0002\u000f%t7\r\\;eK\u0012\nQ'\u0003\u00027o\u0005A\u0001KU(Q\u000bJ#\u0016L\u0003\u00029c\u0005\u0011\u0011i]\u0001\taJ|\u0007/\u001a:us\u0006\n1(A\u0003Fm\u0016tG\u000f\u000b\u0002\u0001{A\u0011a\bQ\u0007\u0002\u007f)\u0011!\u0005B\u0005\u0003\u0003~\u0012A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public interface SparkListenerEvent {
   boolean logEvent();
}
