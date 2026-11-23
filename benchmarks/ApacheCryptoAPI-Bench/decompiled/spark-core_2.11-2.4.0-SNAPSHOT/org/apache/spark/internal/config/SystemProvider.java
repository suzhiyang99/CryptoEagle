package org.apache.spark.internal.config;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-2Q!\u0001\u0002\u0001\r1\u0011abU=ti\u0016l\u0007K]8wS\u0012,'O\u0003\u0002\u0004\t\u000511m\u001c8gS\u001eT!!\u0002\u0004\u0002\u0011%tG/\u001a:oC2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\tq1i\u001c8gS\u001e\u0004&o\u001c<jI\u0016\u0014\b\"\u0002\r\u0001\t\u0003Q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003m\u0001\"\u0001\u0006\u0001\t\u000bu\u0001A\u0011\t\u0010\u0002\u0007\u001d,G\u000f\u0006\u0002 SA\u0019a\u0002\t\u0012\n\u0005\u0005z!AB(qi&|g\u000e\u0005\u0002$M9\u0011a\u0002J\u0005\u0003K=\ta\u0001\u0015:fI\u00164\u0017BA\u0014)\u0005\u0019\u0019FO]5oO*\u0011Qe\u0004\u0005\u0006Uq\u0001\rAI\u0001\u0004W\u0016L\b"
)
public class SystemProvider implements ConfigProvider {
   public Option<String> get(String key) {
      return scala.sys.package$.MODULE$.props().get(key);
   }
}
