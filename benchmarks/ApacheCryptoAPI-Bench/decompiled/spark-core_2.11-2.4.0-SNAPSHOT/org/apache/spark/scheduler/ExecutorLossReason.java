package org.apache.spark.scheduler;

import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0001\t)\u0011!#\u0012=fGV$xN\u001d'pgN\u0014V-Y:p]*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\r%%\u00111#\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t+\u0001\u0011)\u0019!C\u0001/\u00059Q.Z:tC\u001e,7\u0001A\u000b\u00021A\u0011\u0011\u0004\b\b\u0003\u0019iI!aG\u0007\u0002\rA\u0013X\rZ3g\u0013\tibD\u0001\u0004TiJLgn\u001a\u0006\u000375A\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\t[\u0016\u001c8/Y4fA!)!\u0005\u0001C\u0001G\u00051A(\u001b8jiz\"\"\u0001\n\u0014\u0011\u0005\u0015\u0002Q\"\u0001\u0002\t\u000bU\t\u0003\u0019\u0001\r\t\u000b!\u0002A\u0011I\u0015\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u0007"
)
public class ExecutorLossReason implements Serializable {
   private final String message;

   public String message() {
      return this.message;
   }

   public String toString() {
      return this.message();
   }

   public ExecutorLossReason(String message) {
      this.message = message;
   }
}
