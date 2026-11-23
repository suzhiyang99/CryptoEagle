package org.apache.spark.util;

import java.util.concurrent.atomic.AtomicInteger;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000152Q!\u0001\u0002\u0001\t)\u00111\"\u00133HK:,'/\u0019;pe*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\u0005\u0006%\u0001!\t\u0001F\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tQ\u0003\u0005\u0002\u0017\u00015\t!\u0001C\u0004\u0019\u0001\t\u0007I\u0011B\r\u0002\u0005%$W#\u0001\u000e\u0011\u0005m\u0019S\"\u0001\u000f\u000b\u0005uq\u0012AB1u_6L7M\u0003\u0002 A\u0005Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005\r\t#\"\u0001\u0012\u0002\t)\fg/Y\u0005\u0003Iq\u0011Q\"\u0011;p[&\u001c\u0017J\u001c;fO\u0016\u0014\bB\u0002\u0014\u0001A\u0003%!$A\u0002jI\u0002BQ\u0001\u000b\u0001\u0005\u0002%\nAA\\3yiV\t!\u0006\u0005\u0002\rW%\u0011A&\u0004\u0002\u0004\u0013:$\b"
)
public class IdGenerator {
   private final AtomicInteger id = new AtomicInteger();

   private AtomicInteger id() {
      return this.id;
   }

   public int next() {
      return this.id().incrementAndGet();
   }
}
