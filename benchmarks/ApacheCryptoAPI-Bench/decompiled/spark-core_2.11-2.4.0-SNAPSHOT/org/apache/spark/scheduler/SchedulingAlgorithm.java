package org.apache.spark.scheduler;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00012\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0014'\u000eDW\rZ;mS:<\u0017\t\\4pe&$\b.\u001c\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0011\u0002A\"\u0001\u0015\u0003)\u0019w.\u001c9be\u0006$xN]\u0002\u0001)\r)\u0002D\b\t\u0003\u0019YI!aF\u0007\u0003\u000f\t{w\u000e\\3b]\")\u0011$\u0005a\u00015\u0005\u00111/\r\t\u00037qi\u0011AA\u0005\u0003;\t\u00111bU2iK\u0012,H.\u00192mK\")q$\u0005a\u00015\u0005\u00111O\r"
)
public interface SchedulingAlgorithm {
   boolean comparator(Schedulable var1, Schedulable var2);
}
