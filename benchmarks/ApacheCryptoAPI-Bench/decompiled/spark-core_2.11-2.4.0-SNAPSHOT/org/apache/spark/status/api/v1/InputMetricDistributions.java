package org.apache.spark.status.api.v1;

import scala.collection.IndexedSeq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2A!\u0001\u0002\u0001\u001f\tA\u0012J\u001c9vi6+GO]5d\t&\u001cHO]5ckRLwN\\:\u000b\u0005\r!\u0011A\u0001<2\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\rM$\u0018\r^;t\u0015\tI!\"A\u0003ta\u0006\u00148N\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7\u0001A\n\u0003\u0001A\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011a!\u00118z%\u00164\u0007\u0002C\f\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u0013\tLH/Z:SK\u0006$W#A\r\u0011\u0007i\u0011SE\u0004\u0002\u001cA9\u0011AdH\u0007\u0002;)\u0011aDD\u0001\u0007yI|w\u000e\u001e \n\u0003MI!!\t\n\u0002\u000fA\f7m[1hK&\u00111\u0005\n\u0002\u000b\u0013:$W\r_3e'\u0016\f(BA\u0011\u0013!\t\tb%\u0003\u0002(%\t1Ai\\;cY\u0016D\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006I!G\u0001\u000bEf$Xm\u001d*fC\u0012\u0004\u0003\u0002C\u0016\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u0017I,7m\u001c:egJ+\u0017\r\u001a\u0005\t[\u0001\u0011\t\u0011)A\u00053\u0005a!/Z2pe\u0012\u001c(+Z1eA!1q\u0006\u0001C\u0001\u0011A\na\u0001P5oSRtDcA\u00194iA\u0011!\u0007A\u0007\u0002\u0005!)qC\fa\u00013!)1F\fa\u00013\u0001"
)
public class InputMetricDistributions {
   private final IndexedSeq<Object> bytesRead;
   private final IndexedSeq<Object> recordsRead;

   public IndexedSeq<Object> bytesRead() {
      return this.bytesRead;
   }

   public IndexedSeq<Object> recordsRead() {
      return this.recordsRead;
   }

   public InputMetricDistributions(IndexedSeq<Object> bytesRead, IndexedSeq<Object> recordsRead) {
      this.bytesRead = bytesRead;
      this.recordsRead = recordsRead;
   }
}
