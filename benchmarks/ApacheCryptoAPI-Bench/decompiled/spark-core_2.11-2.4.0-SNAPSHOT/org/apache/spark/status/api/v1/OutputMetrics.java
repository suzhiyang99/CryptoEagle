package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%2A!\u0001\u0002\u0001\u001f\tiq*\u001e;qkRlU\r\u001e:jGNT!a\u0001\u0003\u0002\u0005Y\f$BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\taa\u001d;biV\u001c(BA\u0005\u000b\u0003\u0015\u0019\b/\u0019:l\u0015\tYA\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u001b\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0005\t\u0003#Qi\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\u0007\u0003:L(+\u001a4\t\u0011]\u0001!Q1A\u0005\u0002a\tABY=uKN<&/\u001b;uK:,\u0012!\u0007\t\u0003#iI!a\u0007\n\u0003\t1{gn\u001a\u0005\t;\u0001\u0011\t\u0011)A\u00053\u0005i!-\u001f;fg^\u0013\u0018\u000e\u001e;f]\u0002B\u0001b\b\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u000fe\u0016\u001cwN\u001d3t/JLG\u000f^3o\u0011!\t\u0003A!A!\u0002\u0013I\u0012a\u0004:fG>\u0014Hm],sSR$XM\u001c\u0011\t\r\r\u0002A\u0011\u0001\u0005%\u0003\u0019a\u0014N\\5u}Q\u0019Qe\n\u0015\u0011\u0005\u0019\u0002Q\"\u0001\u0002\t\u000b]\u0011\u0003\u0019A\r\t\u000b}\u0011\u0003\u0019A\r"
)
public class OutputMetrics {
   private final long bytesWritten;
   private final long recordsWritten;

   public long bytesWritten() {
      return this.bytesWritten;
   }

   public long recordsWritten() {
      return this.recordsWritten;
   }

   public OutputMetrics(long bytesWritten, long recordsWritten) {
      this.bytesWritten = bytesWritten;
      this.recordsWritten = recordsWritten;
   }
}
