package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.LongAccumulator;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u00113A!\u0001\u0002\u0001\u0017\tiq*\u001e;qkRlU\r\u001e:jGNT!a\u0001\u0003\u0002\u0011\u0015DXmY;u_JT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00055\u0019\u0012B\u0001\u000b\u000f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011\u00191\u0002\u0001\"\u0001\u0005/\u00051A(\u001b8jiz\"\u0012\u0001\u0007\t\u00033\u0001i\u0011A\u0001\u0005\t7\u0001\u0011\r\u0011\"\u0001\u00039\u0005iqLY=uKN<&/\u001b;uK:,\u0012!\b\t\u0003=\u0005j\u0011a\b\u0006\u0003A\u0011\tA!\u001e;jY&\u0011!e\b\u0002\u0010\u0019>tw-Q2dk6,H.\u0019;pe\"1A\u0005\u0001Q\u0001\nu\tab\u00182zi\u0016\u001cxK]5ui\u0016t\u0007\u0005\u0003\u0005'\u0001\t\u0007I\u0011\u0001\u0002\u001d\u0003=y&/Z2pe\u0012\u001cxK]5ui\u0016t\u0007B\u0002\u0015\u0001A\u0003%Q$\u0001\t`e\u0016\u001cwN\u001d3t/JLG\u000f^3oA!)!\u0006\u0001C\u0001W\u0005a!-\u001f;fg^\u0013\u0018\u000e\u001e;f]V\tA\u0006\u0005\u0002\u000e[%\u0011aF\u0004\u0002\u0005\u0019>tw\rC\u00031\u0001\u0011\u00051&\u0001\bsK\u000e|'\u000fZ:Xe&$H/\u001a8\t\rI\u0002A\u0011\u0001\u00034\u0003=\u0019X\r\u001e\"zi\u0016\u001cxK]5ui\u0016tGC\u0001\u001b8!\tiQ'\u0003\u00027\u001d\t!QK\\5u\u0011\u0015A\u0014\u00071\u0001-\u0003\u00051\bB\u0002\u001e\u0001\t\u0003!1(A\ttKR\u0014VmY8sIN<&/\u001b;uK:$\"\u0001\u000e\u001f\t\u000baJ\u0004\u0019\u0001\u0017)\u0005\u0001q\u0004CA C\u001b\u0005\u0001%BA!\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u0007\u0002\u0013A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public class OutputMetrics implements Serializable {
   private final LongAccumulator _bytesWritten = new LongAccumulator();
   private final LongAccumulator _recordsWritten = new LongAccumulator();

   public LongAccumulator _bytesWritten() {
      return this._bytesWritten;
   }

   public LongAccumulator _recordsWritten() {
      return this._recordsWritten;
   }

   public long bytesWritten() {
      return this._bytesWritten().sum();
   }

   public long recordsWritten() {
      return this._recordsWritten().sum();
   }

   public void setBytesWritten(long v) {
      this._bytesWritten().setValue(v);
   }

   public void setRecordsWritten(long v) {
      this._recordsWritten().setValue(v);
   }
}
