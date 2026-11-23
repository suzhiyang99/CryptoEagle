package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.LongAccumulator;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001!3A!\u0001\u0002\u0001\u0017\ta\u0011J\u001c9vi6+GO]5dg*\u00111\u0001B\u0001\tKb,7-\u001e;pe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001bMI!\u0001\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\rY\u0001A\u0011\u0001\u0003\u0018\u0003\u0019a\u0014N\\5u}Q\t\u0001\u0004\u0005\u0002\u001a\u00015\t!\u0001\u0003\u0005\u001c\u0001\t\u0007I\u0011\u0001\u0002\u001d\u0003)y&-\u001f;fgJ+\u0017\rZ\u000b\u0002;A\u0011a$I\u0007\u0002?)\u0011\u0001\u0005B\u0001\u0005kRLG.\u0003\u0002#?\tyAj\u001c8h\u0003\u000e\u001cW/\\;mCR|'\u000f\u0003\u0004%\u0001\u0001\u0006I!H\u0001\f?\nLH/Z:SK\u0006$\u0007\u0005\u0003\u0005'\u0001\t\u0007I\u0011\u0001\u0002\u001d\u00031y&/Z2pe\u0012\u001c(+Z1e\u0011\u0019A\u0003\u0001)A\u0005;\u0005iqL]3d_J$7OU3bI\u0002BQA\u000b\u0001\u0005\u0002-\n\u0011BY=uKN\u0014V-\u00193\u0016\u00031\u0002\"!D\u0017\n\u00059r!\u0001\u0002'p]\u001eDQ\u0001\r\u0001\u0005\u0002-\n1B]3d_J$7OU3bI\"1!\u0007\u0001C\u0001\tM\nA\"\u001b8d\u0005f$Xm\u001d*fC\u0012$\"\u0001N\u001c\u0011\u00055)\u0014B\u0001\u001c\u000f\u0005\u0011)f.\u001b;\t\u000ba\n\u0004\u0019\u0001\u0017\u0002\u0003YDaA\u000f\u0001\u0005\u0002\u0011Y\u0014AD5oGJ+7m\u001c:egJ+\u0017\r\u001a\u000b\u0003iqBQ\u0001O\u001dA\u00021BaA\u0010\u0001\u0005\u0002\u0011y\u0014\u0001D:fi\nKH/Z:SK\u0006$GC\u0001\u001bA\u0011\u0015AT\b1\u0001-Q\t\u0001!\t\u0005\u0002D\r6\tAI\u0003\u0002F\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u001d#%\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class InputMetrics implements Serializable {
   private final LongAccumulator _bytesRead = new LongAccumulator();
   private final LongAccumulator _recordsRead = new LongAccumulator();

   public LongAccumulator _bytesRead() {
      return this._bytesRead;
   }

   public LongAccumulator _recordsRead() {
      return this._recordsRead;
   }

   public long bytesRead() {
      return this._bytesRead().sum();
   }

   public long recordsRead() {
      return this._recordsRead().sum();
   }

   public void incBytesRead(long v) {
      this._bytesRead().add(v);
   }

   public void incRecordsRead(long v) {
      this._recordsRead().add(v);
   }

   public void setBytesRead(long v) {
      this._bytesRead().setValue(v);
   }
}
