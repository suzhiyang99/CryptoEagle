package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2A!\u0001\u0002\u0001\u001f\tYa+\u001a:tS>t\u0017J\u001c4p\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0003\u0005\n\u0001\t\u0015\r\u0011\"\u0001\u0018+\u0005A\u0002CA\r\u001d\u001d\t\t\"$\u0003\u0002\u001c%\u00051\u0001K]3eK\u001aL!!\b\u0010\u0003\rM#(/\u001b8h\u0015\tY\"\u0003\u0003\u0005!\u0001\t\u0005\t\u0015!\u0003\u0019\u0003\u0019\u0019\b/\u0019:lA!1!\u0005\u0001C\u0001\u0011\r\na\u0001P5oSRtDC\u0001\u0013'!\t)\u0003!D\u0001\u0003\u0011\u0015I\u0011\u00051\u0001\u0019\u0001"
)
public class VersionInfo {
   private final String spark;

   public String spark() {
      return this.spark;
   }

   public VersionInfo(String spark) {
      this.spark = spark;
   }
}
