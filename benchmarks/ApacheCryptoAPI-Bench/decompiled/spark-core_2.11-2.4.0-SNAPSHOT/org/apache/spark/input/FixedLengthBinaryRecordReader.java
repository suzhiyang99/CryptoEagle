package org.apache.spark.input;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%b!B\u0001\u0003\u0001\u0011Q!!\b$jq\u0016$G*\u001a8hi\"\u0014\u0015N\\1ssJ+7m\u001c:e%\u0016\fG-\u001a:\u000b\u0005\r!\u0011!B5oaV$(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001\u0003\u0002\u0007\u0012'ei\u0011!\u0004\u0006\u0003\u001d=\t\u0011\"\\1qe\u0016$WoY3\u000b\u0005A1\u0011A\u00025bI>|\u0007/\u0003\u0002\u0013\u001b\ta!+Z2pe\u0012\u0014V-\u00193feB\u0011AcF\u0007\u0002+)\u0011acD\u0001\u0003S>L!\u0001G\u000b\u0003\u00191{gnZ,sSR\f'\r\\3\u0011\u0005QQ\u0012BA\u000e\u0016\u00055\u0011\u0015\u0010^3t/JLG/\u00192mK\")Q\u0004\u0001C\u0001?\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001!!\t\t\u0003!D\u0001\u0003\u0011\u001d\u0019\u0003\u00011A\u0005\n\u0011\n!b\u001d9mSR\u001cF/\u0019:u+\u0005)\u0003C\u0001\u0014*\u001b\u00059#\"\u0001\u0015\u0002\u000bM\u001c\u0017\r\\1\n\u0005):#\u0001\u0002'p]\u001eDq\u0001\f\u0001A\u0002\u0013%Q&\u0001\bta2LGo\u0015;beR|F%Z9\u0015\u00059\n\u0004C\u0001\u00140\u0013\t\u0001tE\u0001\u0003V]&$\bb\u0002\u001a,\u0003\u0003\u0005\r!J\u0001\u0004q\u0012\n\u0004B\u0002\u001b\u0001A\u0003&Q%A\u0006ta2LGo\u0015;beR\u0004\u0003b\u0002\u001c\u0001\u0001\u0004%I\u0001J\u0001\tgBd\u0017\u000e^#oI\"9\u0001\b\u0001a\u0001\n\u0013I\u0014\u0001D:qY&$XI\u001c3`I\u0015\fHC\u0001\u0018;\u0011\u001d\u0011t'!AA\u0002\u0015Ba\u0001\u0010\u0001!B\u0013)\u0013!C:qY&$XI\u001c3!\u0011\u001dq\u0004\u00011A\u0005\n\u0011\nqbY;se\u0016tG\u000fU8tSRLwN\u001c\u0005\b\u0001\u0002\u0001\r\u0011\"\u0003B\u0003M\u0019WO\u001d:f]R\u0004vn]5uS>tw\fJ3r)\tq#\tC\u00043\u007f\u0005\u0005\t\u0019A\u0013\t\r\u0011\u0003\u0001\u0015)\u0003&\u0003A\u0019WO\u001d:f]R\u0004vn]5uS>t\u0007\u0005C\u0004G\u0001\u0001\u0007I\u0011B$\u0002\u0019I,7m\u001c:e\u0019\u0016tw\r\u001e5\u0016\u0003!\u0003\"AJ%\n\u0005);#aA%oi\"9A\n\u0001a\u0001\n\u0013i\u0015\u0001\u0005:fG>\u0014H\rT3oORDw\fJ3r)\tqc\nC\u00043\u0017\u0006\u0005\t\u0019\u0001%\t\rA\u0003\u0001\u0015)\u0003I\u00035\u0011XmY8sI2+gn\u001a;iA!9!\u000b\u0001a\u0001\n\u0013\u0019\u0016a\u00044jY\u0016Le\u000e];u'R\u0014X-Y7\u0016\u0003Q\u0003\"!\u0016-\u000e\u0003YS!aV\b\u0002\u0005\u0019\u001c\u0018BA-W\u0005E15\u000bR1uC&s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\b7\u0002\u0001\r\u0011\"\u0003]\u0003M1\u0017\u000e\\3J]B,Ho\u0015;sK\u0006lw\fJ3r)\tqS\fC\u000435\u0006\u0005\t\u0019\u0001+\t\r}\u0003\u0001\u0015)\u0003U\u0003A1\u0017\u000e\\3J]B,Ho\u0015;sK\u0006l\u0007\u0005C\u0004b\u0001\u0001\u0007I\u0011\u00022\u0002\u0013I,7m\u001c:e\u0017\u0016LX#A\n\t\u000f\u0011\u0004\u0001\u0019!C\u0005K\u0006i!/Z2pe\u0012\\U-_0%KF$\"A\f4\t\u000fI\u001a\u0017\u0011!a\u0001'!1\u0001\u000e\u0001Q!\nM\t!B]3d_J$7*Z=!\u0011\u001dQ\u0007\u00011A\u0005\n-\f1B]3d_J$g+\u00197vKV\t\u0011\u0004C\u0004n\u0001\u0001\u0007I\u0011\u00028\u0002\u001fI,7m\u001c:e-\u0006dW/Z0%KF$\"AL8\t\u000fIb\u0017\u0011!a\u00013!1\u0011\u000f\u0001Q!\ne\tAB]3d_J$g+\u00197vK\u0002BQa\u001d\u0001\u0005BQ\fQa\u00197pg\u0016$\u0012A\f\u0005\u0006m\u0002!\te^\u0001\u000eO\u0016$8)\u001e:sK:$8*Z=\u0015\u0003MAQ!\u001f\u0001\u0005Bi\fqbZ3u\u0007V\u0014(/\u001a8u-\u0006dW/\u001a\u000b\u00023!)A\u0010\u0001C!{\u0006Yq-\u001a;Qe><'/Z:t)\u0005q\bC\u0001\u0014\u0000\u0013\r\t\ta\n\u0002\u0006\r2|\u0017\r\u001e\u0005\b\u0003\u000b\u0001A\u0011IA\u0004\u0003)Ig.\u001b;jC2L'0\u001a\u000b\u0006]\u0005%\u00111\u0003\u0005\t\u0003\u0017\t\u0019\u00011\u0001\u0002\u000e\u0005Q\u0011N\u001c9viN\u0003H.\u001b;\u0011\u00071\ty!C\u0002\u0002\u00125\u0011!\"\u00138qkR\u001c\u0006\u000f\\5u\u0011!\t)\"a\u0001A\u0002\u0005]\u0011aB2p]R,\u0007\u0010\u001e\t\u0004\u0019\u0005e\u0011bAA\u000e\u001b\t\u0011B+Y:l\u0003R$X-\u001c9u\u0007>tG/\u001a=u\u0011\u001d\ty\u0002\u0001C!\u0003C\tAB\\3yi.+\u0017PV1mk\u0016$\"!a\t\u0011\u0007\u0019\n)#C\u0002\u0002(\u001d\u0012qAQ8pY\u0016\fg\u000e"
)
public class FixedLengthBinaryRecordReader extends RecordReader<LongWritable, BytesWritable> {
   private long splitStart = 0L;
   private long splitEnd = 0L;
   private long currentPosition = 0L;
   private int recordLength = 0;
   private FSDataInputStream fileInputStream = null;
   private LongWritable recordKey = null;
   private BytesWritable recordValue = null;

   private long splitStart() {
      return this.splitStart;
   }

   private void splitStart_$eq(long x$1) {
      this.splitStart = x$1;
   }

   private long splitEnd() {
      return this.splitEnd;
   }

   private void splitEnd_$eq(long x$1) {
      this.splitEnd = x$1;
   }

   private long currentPosition() {
      return this.currentPosition;
   }

   private void currentPosition_$eq(long x$1) {
      this.currentPosition = x$1;
   }

   private int recordLength() {
      return this.recordLength;
   }

   private void recordLength_$eq(int x$1) {
      this.recordLength = x$1;
   }

   private FSDataInputStream fileInputStream() {
      return this.fileInputStream;
   }

   private void fileInputStream_$eq(FSDataInputStream x$1) {
      this.fileInputStream = x$1;
   }

   private LongWritable recordKey() {
      return this.recordKey;
   }

   private void recordKey_$eq(LongWritable x$1) {
      this.recordKey = x$1;
   }

   private BytesWritable recordValue() {
      return this.recordValue;
   }

   private void recordValue_$eq(BytesWritable x$1) {
      this.recordValue = x$1;
   }

   public void close() {
      if (this.fileInputStream() != null) {
         this.fileInputStream().close();
      }

   }

   public LongWritable getCurrentKey() {
      return this.recordKey();
   }

   public BytesWritable getCurrentValue() {
      return this.recordValue();
   }

   public float getProgress() {
      long var1 = this.splitStart();
      float var3;
      if (var1 == this.splitEnd()) {
         var3 = (float)(double)0.0F;
      } else {
         var3 = (float)Math.min((double)((float)((this.currentPosition() - this.splitStart()) / (this.splitEnd() - this.splitStart()))), (double)1.0F);
      }

      return var3;
   }

   public void initialize(InputSplit inputSplit, TaskAttemptContext context) {
      FileSplit fileSplit = (FileSplit)inputSplit;
      this.splitStart_$eq(fileSplit.getStart());
      this.splitEnd_$eq(this.splitStart() + fileSplit.getLength());
      Path file = fileSplit.getPath();
      Configuration conf = context.getConfiguration();
      CompressionCodec codec = (new CompressionCodecFactory(conf)).getCodec(file);
      if (codec == null) {
         this.recordLength_$eq(FixedLengthBinaryInputFormat$.MODULE$.getRecordLength(context));
         FileSystem fs = file.getFileSystem(conf);
         this.fileInputStream_$eq(fs.open(file));
         this.fileInputStream().seek(this.splitStart());
         this.currentPosition_$eq(this.splitStart());
      } else {
         throw new IOException("FixedLengthRecordReader does not support reading compressed files");
      }
   }

   public boolean nextKeyValue() {
      if (this.recordKey() == null) {
         this.recordKey_$eq(new LongWritable());
      }

      this.recordKey().set(this.currentPosition() / (long)this.recordLength());
      if (this.recordValue() == null) {
         this.recordValue_$eq(new BytesWritable(new byte[this.recordLength()]));
      }

      if (this.currentPosition() < this.splitEnd()) {
         byte[] buffer = this.recordValue().getBytes();
         this.fileInputStream().readFully(buffer);
         this.currentPosition_$eq(this.currentPosition() + (long)this.recordLength());
         return true;
      } else {
         return false;
      }
   }
}
