package org.apache.spark.status.api.v1;

import scala.collection.IndexedSeq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U2A!\u0001\u0002\u0001\u001f\tIr*\u001e;qkRlU\r\u001e:jG\u0012K7\u000f\u001e:jEV$\u0018n\u001c8t\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0003\u0005\u0018\u0001\t\u0015\r\u0011\"\u0001\u0019\u00031\u0011\u0017\u0010^3t/JLG\u000f^3o+\u0005I\u0002c\u0001\u000e#K9\u00111\u0004\t\b\u00039}i\u0011!\b\u0006\u0003=9\ta\u0001\u0010:p_Rt\u0014\"A\n\n\u0005\u0005\u0012\u0012a\u00029bG.\fw-Z\u0005\u0003G\u0011\u0012!\"\u00138eKb,GmU3r\u0015\t\t#\u0003\u0005\u0002\u0012M%\u0011qE\u0005\u0002\u0007\t>,(\r\\3\t\u0011%\u0002!\u0011!Q\u0001\ne\tQBY=uKN<&/\u001b;uK:\u0004\u0003\u0002C\u0016\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u001dI,7m\u001c:eg^\u0013\u0018\u000e\u001e;f]\"AQ\u0006\u0001B\u0001B\u0003%\u0011$A\bsK\u000e|'\u000fZ:Xe&$H/\u001a8!\u0011\u0019y\u0003\u0001\"\u0001\ta\u00051A(\u001b8jiz\"2!M\u001a5!\t\u0011\u0004!D\u0001\u0003\u0011\u00159b\u00061\u0001\u001a\u0011\u0015Yc\u00061\u0001\u001a\u0001"
)
public class OutputMetricDistributions {
   private final IndexedSeq<Object> bytesWritten;
   private final IndexedSeq<Object> recordsWritten;

   public IndexedSeq<Object> bytesWritten() {
      return this.bytesWritten;
   }

   public IndexedSeq<Object> recordsWritten() {
      return this.recordsWritten;
   }

   public OutputMetricDistributions(IndexedSeq<Object> bytesWritten, IndexedSeq<Object> recordsWritten) {
      this.bytesWritten = bytesWritten;
      this.recordsWritten = recordsWritten;
   }
}
