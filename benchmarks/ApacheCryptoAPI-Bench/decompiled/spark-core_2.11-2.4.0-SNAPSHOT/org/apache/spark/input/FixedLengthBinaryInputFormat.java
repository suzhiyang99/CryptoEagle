package org.apache.spark.input;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005EqAB\u0001\u0003\u0011\u0003!!\"\u0001\u000fGSb,G\rT3oORD')\u001b8befLe\u000e];u\r>\u0014X.\u0019;\u000b\u0005\r!\u0011!B5oaV$(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0011\u0005-aQ\"\u0001\u0002\u0007\r5\u0011\u0001\u0012\u0001\u0003\u000f\u0005q1\u0015\u000e_3e\u0019\u0016tw\r\u001e5CS:\f'/_%oaV$hi\u001c:nCR\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000fia!\u0019!C\u00017\u00051\"+R\"P%\u0012{F*\u0012(H)\"{\u0006KU(Q\u000bJ#\u0016,F\u0001\u001d!\ti\"%D\u0001\u001f\u0015\ty\u0002%\u0001\u0003mC:<'\"A\u0011\u0002\t)\fg/Y\u0005\u0003Gy\u0011aa\u0015;sS:<\u0007BB\u0013\rA\u0003%A$A\fS\u000b\u000e{%\u000bR0M\u000b:;E\u000bS0Q%>\u0003VI\u0015+ZA!)q\u0005\u0004C\u0001Q\u0005yq-\u001a;SK\u000e|'\u000f\u001a'f]\u001e$\b\u000e\u0006\u0002*YA\u0011\u0001CK\u0005\u0003WE\u00111!\u00138u\u0011\u0015ic\u00051\u0001/\u0003\u001d\u0019wN\u001c;fqR\u0004\"a\f\u001b\u000e\u0003AR!!\r\u001a\u0002\u00135\f\u0007O]3ek\u000e,'BA\u001a\u0007\u0003\u0019A\u0017\rZ8pa&\u0011Q\u0007\r\u0002\u000b\u0015>\u00147i\u001c8uKb$h!B\u0007\u0003\u0001\u001194c\u0001\u001c9\u0011B!\u0011(P F\u001b\u0005Q$BA\u0002<\u0015\ta\u0004'A\u0002mS\nL!A\u0010\u001e\u0003\u001f\u0019KG.Z%oaV$hi\u001c:nCR\u0004\"\u0001Q\"\u000e\u0003\u0005S!A\u0011\u001a\u0002\u0005%|\u0017B\u0001#B\u00051auN\\4Xe&$\u0018M\u00197f!\t\u0001e)\u0003\u0002H\u0003\ni!)\u001f;fg^\u0013\u0018\u000e^1cY\u0016\u0004\"!\u0013'\u000e\u0003)S!a\u0013\u0003\u0002\u0011%tG/\u001a:oC2L!!\u0014&\u0003\u000f1{wmZ5oO\")aC\u000eC\u0001\u001fR\t\u0001\u000b\u0005\u0002\fm!9!K\u000ea\u0001\n\u0013\u0019\u0016\u0001\u0004:fG>\u0014H\rT3oORDW#A\u0015\t\u000fU3\u0004\u0019!C\u0005-\u0006\u0001\"/Z2pe\u0012dUM\\4uQ~#S-\u001d\u000b\u0003/j\u0003\"\u0001\u0005-\n\u0005e\u000b\"\u0001B+oSRDqa\u0017+\u0002\u0002\u0003\u0007\u0011&A\u0002yIEBa!\u0018\u001c!B\u0013I\u0013!\u0004:fG>\u0014H\rT3oORD\u0007\u0005C\u0003`m\u0011\u0005\u0003-A\u0006jgN\u0003H.\u001b;bE2,GcA1eKB\u0011\u0001CY\u0005\u0003GF\u0011qAQ8pY\u0016\fg\u000eC\u0003.=\u0002\u0007a\u0006C\u0003g=\u0002\u0007q-\u0001\u0005gS2,g.Y7f!\tA7.D\u0001j\u0015\tQ''\u0001\u0002gg&\u0011A.\u001b\u0002\u0005!\u0006$\b\u000eC\u0003om\u0011\u0005s.\u0001\td_6\u0004X\u000f^3Ta2LGoU5{KR!\u0001o];x!\t\u0001\u0012/\u0003\u0002s#\t!Aj\u001c8h\u0011\u0015!X\u000e1\u0001q\u0003%\u0011Gn\\2l'&TX\rC\u0003w[\u0002\u0007\u0001/A\u0004nS:\u001c\u0016N_3\t\u000bal\u0007\u0019\u00019\u0002\u000f5\f\u0007pU5{K\")!P\u000eC!w\u0006\u00112M]3bi\u0016\u0014VmY8sIJ+\u0017\rZ3s)\u0011ax0!\u0003\u0011\t=jx(R\u0005\u0003}B\u0012ABU3d_J$'+Z1eKJDq!!\u0001z\u0001\u0004\t\u0019!A\u0003ta2LG\u000fE\u00020\u0003\u000bI1!a\u00021\u0005)Ie\u000e];u'Bd\u0017\u000e\u001e\u0005\u0007[e\u0004\r!a\u0003\u0011\u0007=\ni!C\u0002\u0002\u0010A\u0012!\u0003V1tW\u0006#H/Z7qi\u000e{g\u000e^3yi\u0002"
)
public class FixedLengthBinaryInputFormat extends FileInputFormat<LongWritable, BytesWritable> implements Logging {
   private int recordLength;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static int getRecordLength(JobContext var0) {
      return FixedLengthBinaryInputFormat$.MODULE$.getRecordLength(var0);
   }

   public static String RECORD_LENGTH_PROPERTY() {
      return FixedLengthBinaryInputFormat$.MODULE$.RECORD_LENGTH_PROPERTY();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   private int recordLength() {
      return this.recordLength;
   }

   private void recordLength_$eq(int x$1) {
      this.recordLength = x$1;
   }

   public boolean isSplitable(JobContext context, Path filename) {
      if (this.recordLength() == -1) {
         this.recordLength_$eq(FixedLengthBinaryInputFormat$.MODULE$.getRecordLength(context));
      }

      boolean var10000;
      if (this.recordLength() <= 0) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "record length is less than 0, file cannot be split";
            }
         });
         var10000 = false;
      } else {
         var10000 = true;
      }

      return var10000;
   }

   public long computeSplitSize(long blockSize, long minSize, long maxSize) {
      long defaultSize = super.computeSplitSize(blockSize, minSize, maxSize);
      return defaultSize < (long)this.recordLength() ? (long)this.recordLength() : (long)(Math.floor((double)(defaultSize / (long)this.recordLength())) * (double)this.recordLength());
   }

   public RecordReader<LongWritable, BytesWritable> createRecordReader(InputSplit split, TaskAttemptContext context) {
      return new FixedLengthBinaryRecordReader();
   }

   public FixedLengthBinaryInputFormat() {
      Logging$class.$init$(this);
      this.recordLength = -1;
   }
}
