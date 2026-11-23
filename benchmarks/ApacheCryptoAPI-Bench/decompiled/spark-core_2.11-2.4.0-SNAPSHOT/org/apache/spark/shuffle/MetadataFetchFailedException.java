package org.apache.spark.shuffle;

import org.apache.spark.storage.BlockManagerId;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0001\t)\u0011A$T3uC\u0012\fG/\u0019$fi\u000eDg)Y5mK\u0012,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u000591\u000f[;gM2,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005Q1U\r^2i\r\u0006LG.\u001a3Fq\u000e,\u0007\u000f^5p]\"A\u0001\u0003\u0001B\u0001B\u0003%!#A\u0005tQV4g\r\\3JI\u000e\u0001\u0001CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"aA%oi\"A\u0011\u0004\u0001B\u0001B\u0003%!#\u0001\u0005sK\u0012,8-Z%e\u0011!Y\u0002A!A!\u0002\u0013a\u0012aB7fgN\fw-\u001a\t\u0003;\u0001r!a\u0005\u0010\n\u0005}!\u0012A\u0002)sK\u0012,g-\u0003\u0002\"E\t11\u000b\u001e:j]\u001eT!a\b\u000b\t\u000b\u0011\u0002A\u0011A\u0013\u0002\rqJg.\u001b;?)\u00111s\u0005K\u0015\u0011\u00051\u0001\u0001\"\u0002\t$\u0001\u0004\u0011\u0002\"B\r$\u0001\u0004\u0011\u0002\"B\u000e$\u0001\u0004a\u0002"
)
public class MetadataFetchFailedException extends FetchFailedException {
   public MetadataFetchFailedException(int shuffleId, int reduceId, String message) {
      super((BlockManagerId)null, shuffleId, -1, reduceId, message, FetchFailedException$.MODULE$.$lessinit$greater$default$6());
   }
}
