package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.LongAccumulator;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001)4A!\u0001\u0002\u0001\u0017\t\u00192\u000b[;gM2,wK]5uK6+GO]5dg*\u00111\u0001B\u0001\tKb,7-\u001e;pe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001bMI!\u0001\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\rY\u0001A\u0011\u0001\u0003\u0018\u0003\u0019a\u0014N\\5u}Q\t\u0001\u0004\u0005\u0002\u001a\u00015\t!\u0001\u0003\u0005\u001c\u0001\t\u0007I\u0011\u0001\u0002\u001d\u00035y&-\u001f;fg^\u0013\u0018\u000e\u001e;f]V\tQ\u0004\u0005\u0002\u001fC5\tqD\u0003\u0002!\t\u0005!Q\u000f^5m\u0013\t\u0011sDA\bM_:<\u0017iY2v[Vd\u0017\r^8s\u0011\u0019!\u0003\u0001)A\u0005;\u0005qqLY=uKN<&/\u001b;uK:\u0004\u0003\u0002\u0003\u0014\u0001\u0005\u0004%\tA\u0001\u000f\u0002\u001f}\u0013XmY8sIN<&/\u001b;uK:Da\u0001\u000b\u0001!\u0002\u0013i\u0012\u0001E0sK\u000e|'\u000fZ:Xe&$H/\u001a8!\u0011!Q\u0003A1A\u0005\u0002\ta\u0012AC0xe&$X\rV5nK\"1A\u0006\u0001Q\u0001\nu\t1bX<sSR,G+[7fA!)a\u0006\u0001C\u0001_\u0005a!-\u001f;fg^\u0013\u0018\u000e\u001e;f]V\t\u0001\u0007\u0005\u0002\u000ec%\u0011!G\u0004\u0002\u0005\u0019>tw\rC\u00035\u0001\u0011\u0005q&\u0001\bsK\u000e|'\u000fZ:Xe&$H/\u001a8\t\u000bY\u0002A\u0011A\u0018\u0002\u0013]\u0014\u0018\u000e^3US6,\u0007B\u0002\u001d\u0001\t\u0003!\u0011(A\bj]\u000e\u0014\u0015\u0010^3t/JLG\u000f^3o)\tQT\b\u0005\u0002\u000ew%\u0011AH\u0004\u0002\u0005+:LG\u000fC\u0003?o\u0001\u0007\u0001'A\u0001w\u0011\u0019\u0001\u0005\u0001\"\u0001\u0005\u0003\u0006\t\u0012N\\2SK\u000e|'\u000fZ:Xe&$H/\u001a8\u0015\u0005i\u0012\u0005\"\u0002 @\u0001\u0004\u0001\u0004B\u0002#\u0001\t\u0003!Q)\u0001\u0007j]\u000e<&/\u001b;f)&lW\r\u0006\u0002;\r\")ah\u0011a\u0001a!1\u0001\n\u0001C\u0001\t%\u000bq\u0002Z3d\u0005f$Xm],sSR$XM\u001c\u000b\u0003u)CQAP$A\u0002ABa\u0001\u0014\u0001\u0005\u0002\u0011i\u0015!\u00053fGJ+7m\u001c:eg^\u0013\u0018\u000e\u001e;f]R\u0011!H\u0014\u0005\u0006}-\u0003\r\u0001\r\u0005\u0006!\u0002!\taL\u0001\u0014g\",hM\u001a7f\u0005f$Xm],sSR$XM\u001c\u0015\u0005\u001fJ+v\u000b\u0005\u0002\u000e'&\u0011AK\u0004\u0002\u000bI\u0016\u0004(/Z2bi\u0016$\u0017%\u0001,\u00021U\u001cX\r\t2zi\u0016\u001cxK]5ui\u0016t\u0007%\u001b8ti\u0016\fG-I\u0001Y\u0003\u0015\u0011d\u0006\r\u00181\u0011\u0015Q\u0006\u0001\"\u00010\u0003A\u0019\b.\u001e4gY\u0016<&/\u001b;f)&lW\r\u000b\u0003Z%r;\u0016%A/\u0002+U\u001cX\rI<sSR,G+[7fA%t7\u000f^3bI\")q\f\u0001C\u0001_\u0005)2\u000f[;gM2,'+Z2pe\u0012\u001cxK]5ui\u0016t\u0007\u0006\u00020SC^\u000b\u0013AY\u0001\u001bkN,\u0007E]3d_J$7o\u0016:jiR,g\u000eI5ogR,\u0017\r\u001a\u0015\u0003\u0001\u0011\u0004\"!\u001a5\u000e\u0003\u0019T!a\u001a\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002jM\naA)\u001a<fY>\u0004XM]!qS\u0002"
)
public class ShuffleWriteMetrics implements Serializable {
   private final LongAccumulator _bytesWritten = new LongAccumulator();
   private final LongAccumulator _recordsWritten = new LongAccumulator();
   private final LongAccumulator _writeTime = new LongAccumulator();

   public LongAccumulator _bytesWritten() {
      return this._bytesWritten;
   }

   public LongAccumulator _recordsWritten() {
      return this._recordsWritten;
   }

   public LongAccumulator _writeTime() {
      return this._writeTime;
   }

   public long bytesWritten() {
      return this._bytesWritten().sum();
   }

   public long recordsWritten() {
      return this._recordsWritten().sum();
   }

   public long writeTime() {
      return this._writeTime().sum();
   }

   public void incBytesWritten(long v) {
      this._bytesWritten().add(v);
   }

   public void incRecordsWritten(long v) {
      this._recordsWritten().add(v);
   }

   public void incWriteTime(long v) {
      this._writeTime().add(v);
   }

   public void decBytesWritten(long v) {
      this._bytesWritten().setValue(this.bytesWritten() - v);
   }

   public void decRecordsWritten(long v) {
      this._recordsWritten().setValue(this.recordsWritten() - v);
   }

   /** @deprecated */
   public long shuffleBytesWritten() {
      return this.bytesWritten();
   }

   /** @deprecated */
   public long shuffleWriteTime() {
      return this.writeTime();
   }

   /** @deprecated */
   public long shuffleRecordsWritten() {
      return this.recordsWritten();
   }
}
