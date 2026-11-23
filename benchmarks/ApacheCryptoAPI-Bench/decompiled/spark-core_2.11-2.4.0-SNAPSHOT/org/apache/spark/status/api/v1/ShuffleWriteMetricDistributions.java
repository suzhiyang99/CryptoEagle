package org.apache.spark.status.api.v1;

import scala.collection.IndexedSeq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001i2A!\u0001\u0002\u0001\u001f\ty2\u000b[;gM2,wK]5uK6+GO]5d\t&\u001cHO]5ckRLwN\\:\u000b\u0005\r!\u0011A\u0001<2\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\rM$\u0018\r^;t\u0015\tI!\"A\u0003ta\u0006\u00148N\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7\u0001A\n\u0003\u0001A\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011a!\u00118z%\u00164\u0007\u0002C\f\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u0015]\u0014\u0018\u000e^3CsR,7/F\u0001\u001a!\rQ\"%\n\b\u00037\u0001r!\u0001H\u0010\u000e\u0003uQ!A\b\b\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0019\u0012BA\u0011\u0013\u0003\u001d\u0001\u0018mY6bO\u0016L!a\t\u0013\u0003\u0015%sG-\u001a=fIN+\u0017O\u0003\u0002\"%A\u0011\u0011CJ\u0005\u0003OI\u0011a\u0001R8vE2,\u0007\u0002C\u0015\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u0017]\u0014\u0018\u000e^3CsR,7\u000f\t\u0005\tW\u0001\u0011)\u0019!C\u00011\u0005aqO]5uKJ+7m\u001c:eg\"AQ\u0006\u0001B\u0001B\u0003%\u0011$A\u0007xe&$XMU3d_J$7\u000f\t\u0005\t_\u0001\u0011)\u0019!C\u00011\u0005IqO]5uKRKW.\u001a\u0005\tc\u0001\u0011\t\u0011)A\u00053\u0005QqO]5uKRKW.\u001a\u0011\t\rM\u0002A\u0011\u0001\u00055\u0003\u0019a\u0014N\\5u}Q!Qg\u000e\u001d:!\t1\u0004!D\u0001\u0003\u0011\u00159\"\u00071\u0001\u001a\u0011\u0015Y#\u00071\u0001\u001a\u0011\u0015y#\u00071\u0001\u001a\u0001"
)
public class ShuffleWriteMetricDistributions {
   private final IndexedSeq<Object> writeBytes;
   private final IndexedSeq<Object> writeRecords;
   private final IndexedSeq<Object> writeTime;

   public IndexedSeq<Object> writeBytes() {
      return this.writeBytes;
   }

   public IndexedSeq<Object> writeRecords() {
      return this.writeRecords;
   }

   public IndexedSeq<Object> writeTime() {
      return this.writeTime;
   }

   public ShuffleWriteMetricDistributions(IndexedSeq<Object> writeBytes, IndexedSeq<Object> writeRecords, IndexedSeq<Object> writeTime) {
      this.writeBytes = writeBytes;
      this.writeRecords = writeRecords;
      this.writeTime = writeTime;
   }
}
