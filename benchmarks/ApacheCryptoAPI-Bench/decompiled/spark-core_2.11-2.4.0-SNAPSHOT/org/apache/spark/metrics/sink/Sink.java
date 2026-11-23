package org.apache.spark.metrics.sink;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y1\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u0005'&t7N\u0003\u0002\u0004\t\u0005!1/\u001b8l\u0015\t)a!A\u0004nKR\u0014\u0018nY:\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g\u0011\u0015!\u0002A\"\u0001\u0017\u0003\u0015\u0019H/\u0019:u\u0007\u0001!\u0012a\u0006\t\u0003\u001daI!!G\b\u0003\tUs\u0017\u000e\u001e\u0005\u00067\u00011\tAF\u0001\u0005gR|\u0007\u000fC\u0003\u001e\u0001\u0019\u0005a#\u0001\u0004sKB|'\u000f\u001e"
)
public interface Sink {
   void start();

   void stop();

   void report();
}
