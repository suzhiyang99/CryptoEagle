package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I2A!\u0001\u0002\u0001\u001f\tY!+\u001e8uS6,\u0017J\u001c4p\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0003\u0005\u0018\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003-Q\u0017M^1WKJ\u001c\u0018n\u001c8\u0016\u0003e\u0001\"AG\u000f\u000f\u0005EY\u0012B\u0001\u000f\u0013\u0003\u0019\u0001&/\u001a3fM&\u0011ad\b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005q\u0011\u0002\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u0019)\fg/\u0019,feNLwN\u001c\u0011\t\u0011\r\u0002!Q1A\u0005\u0002a\t\u0001B[1wC\"{W.\u001a\u0005\tK\u0001\u0011\t\u0011)A\u00053\u0005I!.\u0019<b\u0011>lW\r\t\u0005\tO\u0001\u0011)\u0019!C\u00011\u0005a1oY1mCZ+'o]5p]\"A\u0011\u0006\u0001B\u0001B\u0003%\u0011$A\u0007tG\u0006d\u0017MV3sg&|g\u000e\t\u0005\u0007W\u0001!\t\u0001\u0003\u0017\u0002\rqJg.\u001b;?)\u0011is\u0006M\u0019\u0011\u00059\u0002Q\"\u0001\u0002\t\u000b]Q\u0003\u0019A\r\t\u000b\rR\u0003\u0019A\r\t\u000b\u001dR\u0003\u0019A\r"
)
public class RuntimeInfo {
   private final String javaVersion;
   private final String javaHome;
   private final String scalaVersion;

   public String javaVersion() {
      return this.javaVersion;
   }

   public String javaHome() {
      return this.javaHome;
   }

   public String scalaVersion() {
      return this.scalaVersion;
   }

   public RuntimeInfo(String javaVersion, String javaHome, String scalaVersion) {
      this.javaVersion = javaVersion;
      this.javaHome = javaHome;
      this.scalaVersion = scalaVersion;
   }
}
