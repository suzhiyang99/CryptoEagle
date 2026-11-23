package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%:a!\u0001\u0002\t\u0002\u0011Q\u0011!D*qCJ\\W\t_5u\u0007>$WM\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<\u0007CA\u0006\r\u001b\u0005\u0011aAB\u0007\u0003\u0011\u0003!aBA\u0007Ta\u0006\u00148.\u0012=ji\u000e{G-Z\n\u0003\u0019=\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007\"\u0002\f\r\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003)AqA\u0007\u0007C\u0002\u0013\u00051$\u0001\nV\u001d\u000e\u000bUk\u0012%U?\u0016C6)\u0012)U\u0013>sU#\u0001\u000f\u0011\u0005Ai\u0012B\u0001\u0010\u0012\u0005\rIe\u000e\u001e\u0005\u0007A1\u0001\u000b\u0011\u0002\u000f\u0002'Us5)Q+H\u0011R{V\tW\"F!RKuJ\u0014\u0011\t\u000f\tb!\u0019!C\u00017\u0005ARKT\"B+\u001eCEkX#Y\u0007\u0016\u0003F+S(O?R;\u0016jQ#\t\r\u0011b\u0001\u0015!\u0003\u001d\u0003e)fjQ!V\u000f\"#v,\u0012-D\u000bB#\u0016j\u0014(`)^K5)\u0012\u0011\t\u000f\u0019b!\u0019!C\u00017\u0005\u0019qjT'\t\r!b\u0001\u0015!\u0003\u001d\u0003\u0011yu*\u0014\u0011"
)
public final class SparkExitCode {
   public static int OOM() {
      return SparkExitCode$.MODULE$.OOM();
   }

   public static int UNCAUGHT_EXCEPTION_TWICE() {
      return SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION_TWICE();
   }

   public static int UNCAUGHT_EXCEPTION() {
      return SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION();
   }
}
