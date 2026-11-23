package org.apache.spark.input;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005bAB\u0001\u0003\u0003\u0003!!BA\fTiJ,\u0017-\u001c\"bg\u0016$'+Z2pe\u0012\u0014V-\u00193fe*\u00111\u0001B\u0001\u0006S:\u0004X\u000f\u001e\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00111\u0002I\n\u0003\u00011\u0001B!\u0004\n\u0015=5\taB\u0003\u0002\u0010!\u0005IQ.\u00199sK\u0012,8-\u001a\u0006\u0003#\u0019\ta\u0001[1e_>\u0004\u0018BA\n\u000f\u00051\u0011VmY8sIJ+\u0017\rZ3s!\t)2D\u0004\u0002\u001735\tqCC\u0001\u0019\u0003\u0015\u00198-\u00197b\u0013\tQr#\u0001\u0004Qe\u0016$WMZ\u0005\u00039u\u0011aa\u0015;sS:<'B\u0001\u000e\u0018!\ty\u0002\u0005\u0004\u0001\u0005\u000b\u0005\u0002!\u0019A\u0012\u0003\u0003Q\u001b\u0001!\u0005\u0002%OA\u0011a#J\u0005\u0003M]\u0011qAT8uQ&tw\r\u0005\u0002\u0017Q%\u0011\u0011f\u0006\u0002\u0004\u0003:L\b\u0002C\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0017\u0002\u000bM\u0004H.\u001b;\u0011\u00055\nT\"\u0001\u0018\u000b\u0005\ry#B\u0001\u0019\u000f\u0003\ra\u0017NY\u0005\u0003e9\u0012\u0001cQ8nE&tWMR5mKN\u0003H.\u001b;\t\u0011Q\u0002!\u0011!Q\u0001\nU\nqaY8oi\u0016DH\u000f\u0005\u0002\u000em%\u0011qG\u0004\u0002\u0013)\u0006\u001c8.\u0011;uK6\u0004HoQ8oi\u0016DH\u000f\u0003\u0005:\u0001\t\u0005\t\u0015!\u0003;\u0003\u0015Ig\u000eZ3y!\tY\u0004)D\u0001=\u0015\tid(\u0001\u0003mC:<'\"A \u0002\t)\fg/Y\u0005\u0003\u0003r\u0012q!\u00138uK\u001e,'\u000fC\u0003D\u0001\u0011\u0005A)\u0001\u0004=S:LGO\u0010\u000b\u0005\u000b\u001eC\u0015\nE\u0002G\u0001yi\u0011A\u0001\u0005\u0006W\t\u0003\r\u0001\f\u0005\u0006i\t\u0003\r!\u000e\u0005\u0006s\t\u0003\rA\u000f\u0005\b\u0017\u0002\u0001\r\u0011\"\u0003M\u0003%\u0001(o\\2fgN,G-F\u0001N!\t1b*\u0003\u0002P/\t9!i\\8mK\u0006t\u0007bB)\u0001\u0001\u0004%IAU\u0001\u000eaJ|7-Z:tK\u0012|F%Z9\u0015\u0005M3\u0006C\u0001\fU\u0013\t)vC\u0001\u0003V]&$\bbB,Q\u0003\u0003\u0005\r!T\u0001\u0004q\u0012\n\u0004BB-\u0001A\u0003&Q*\u0001\u0006qe>\u001cWm]:fI\u0002Bqa\u0017\u0001A\u0002\u0013%A,A\u0002lKf,\u0012!\u0018\t\u0003wyK!\u0001\b\u001f\t\u000f\u0001\u0004\u0001\u0019!C\u0005C\u000691.Z=`I\u0015\fHCA*c\u0011\u001d9v,!AA\u0002uCa\u0001\u001a\u0001!B\u0013i\u0016\u0001B6fs\u0002BqA\u001a\u0001A\u0002\u0013%q-A\u0003wC2,X-F\u0001\u001f\u0011\u001dI\u0007\u00011A\u0005\n)\f\u0011B^1mk\u0016|F%Z9\u0015\u0005M[\u0007bB,i\u0003\u0003\u0005\rA\b\u0005\u0007[\u0002\u0001\u000b\u0015\u0002\u0010\u0002\rY\fG.^3!\u0011\u0015y\u0007\u0001\"\u0011q\u0003)Ig.\u001b;jC2L'0\u001a\u000b\u0004'F,\b\"B\u0016o\u0001\u0004\u0011\bCA\u0007t\u0013\t!hB\u0001\u0006J]B,Ho\u00159mSRDQ\u0001\u000e8A\u0002UBQa\u001e\u0001\u0005Ba\fQa\u00197pg\u0016$\u0012a\u0015\u0005\u0006u\u0002!\te_\u0001\fO\u0016$\bK]8he\u0016\u001c8\u000fF\u0001}!\t1R0\u0003\u0002\u007f/\t)a\t\\8bi\"9\u0011\u0011\u0001\u0001\u0005B\u0005\r\u0011!D4fi\u000e+(O]3oi.+\u0017\u0010F\u0001\u0015\u0011\u001d\t9\u0001\u0001C!\u0003\u0013\tqbZ3u\u0007V\u0014(/\u001a8u-\u0006dW/\u001a\u000b\u0002=!9\u0011Q\u0002\u0001\u0005B\u0005=\u0011\u0001\u00048fqR\\U-\u001f,bYV,G#A'\t\u000f\u0005M\u0001A\"\u0001\u0002\u0016\u0005Y\u0001/\u0019:tKN#(/Z1n)\rq\u0012q\u0003\u0005\t\u00033\t\t\u00021\u0001\u0002\u001c\u0005A\u0011N\\*ue\u0016\fW\u000eE\u0002G\u0003;I1!a\b\u0003\u0005I\u0001vN\u001d;bE2,G)\u0019;b'R\u0014X-Y7"
)
public abstract class StreamBasedRecordReader<T> extends RecordReader<String, T> {
   private final CombineFileSplit split;
   private final TaskAttemptContext context;
   private final Integer index;
   private boolean processed;
   private String key;
   private T value;

   private boolean processed() {
      return this.processed;
   }

   private void processed_$eq(boolean x$1) {
      this.processed = x$1;
   }

   private String key() {
      return this.key;
   }

   private void key_$eq(String x$1) {
      this.key = x$1;
   }

   private T value() {
      return this.value;
   }

   private void value_$eq(T x$1) {
      this.value = (T)x$1;
   }

   public void initialize(InputSplit split, TaskAttemptContext context) {
   }

   public void close() {
   }

   public float getProgress() {
      return this.processed() ? 1.0F : 0.0F;
   }

   public String getCurrentKey() {
      return this.key();
   }

   public T getCurrentValue() {
      return (T)this.value();
   }

   public boolean nextKeyValue() {
      boolean var10000;
      if (this.processed()) {
         var10000 = false;
      } else {
         PortableDataStream fileIn = new PortableDataStream(this.split, this.context, this.index);
         this.value_$eq(this.parseStream(fileIn));
         this.key_$eq(fileIn.getPath());
         this.processed_$eq(true);
         var10000 = true;
      }

      return var10000;
   }

   public abstract T parseStream(PortableDataStream var1);

   public StreamBasedRecordReader(CombineFileSplit split, TaskAttemptContext context, Integer index) {
      this.split = split;
      this.context = context;
      this.index = index;
      this.processed = false;
      this.key = "";
      this.value = null;
   }
}
