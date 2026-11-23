package org.apache.spark.internal.io;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.SerializableConfiguration;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015e!B\u0001\u0003\u0001\u0019a!A\b%bI>|\u0007/T1q%\u0016$WoY3Xe&$XmQ8oM&<W\u000b^5m\u0015\t\u0019A!\u0001\u0002j_*\u0011QAB\u0001\tS:$XM\u001d8bY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-F\u0002\u000e)\t\u001a2\u0001\u0001\b%!\u0011y\u0001CE\u0011\u000e\u0003\tI!!\u0005\u0002\u0003+!\u000bGm\\8q/JLG/Z\"p]\u001aLw-\u0016;jYB\u00111\u0003\u0006\u0007\u0001\t\u0015)\u0002A1\u0001\u0018\u0005\u0005Y5\u0001A\t\u00031y\u0001\"!\u0007\u000f\u000e\u0003iQ\u0011aG\u0001\u0006g\u000e\fG.Y\u0005\u0003;i\u0011qAT8uQ&tw\r\u0005\u0002\u001a?%\u0011\u0001E\u0007\u0002\u0004\u0003:L\bCA\n#\t\u0015\u0019\u0003A1\u0001\u0018\u0005\u00051\u0006CA\u0013'\u001b\u0005!\u0011BA\u0014\u0005\u0005\u001daunZ4j]\u001eD\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006IAK\u0001\u0005G>tg\r\u0005\u0002,]5\tAF\u0003\u0002.\r\u0005!Q\u000f^5m\u0013\tyCFA\rTKJL\u0017\r\\5{C\ndWmQ8oM&<WO]1uS>t\u0007\u0002C\u0019\u0001\u0005\u0007\u0005\u000b1\u0002\u001a\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$C\u0007E\u00024m\u0005j\u0011\u0001\u000e\u0006\u0003ki\tqA]3gY\u0016\u001cG/\u0003\u00028i\tA1\t\\1tgR\u000bw\rC\u0003:\u0001\u0011\u0005!(\u0001\u0004=S:LGO\u0010\u000b\u0003wy\"\"\u0001P\u001f\u0011\t=\u0001!#\t\u0005\u0006ca\u0002\u001dA\r\u0005\u0006Sa\u0002\rA\u000b\u0005\b\u0001\u0002\u0001\r\u0011\"\u0003B\u00031yW\u000f\u001e9vi\u001a{'/\\1u+\u0005\u0011\u0005GA\"L!\r!uI\u0013\b\u00033\u0015K!A\u0012\u000e\u0002\rA\u0013X\rZ3g\u0013\tA\u0015JA\u0003DY\u0006\u001c8O\u0003\u0002G5A\u00111c\u0013\u0003\n\u00196\u000b\t\u0011!A\u0003\u0002M\u00131a\u0018\u00134\u0011\u0019q\u0005\u0001)Q\u0005\u001f\u0006iq.\u001e;qkR4uN]7bi\u0002\u0002$\u0001\u0015*\u0011\u0007\u0011;\u0015\u000b\u0005\u0002\u0014%\u0012IA*TA\u0001\u0002\u0003\u0015\taU\t\u00031Q\u0003B!\u0016.\u0013C5\taK\u0003\u0002X1\u0006IQ.\u00199sK\u0012,8-\u001a\u0006\u00033\"\ta\u0001[1e_>\u0004\u0018BA.W\u00051yU\u000f\u001e9vi\u001a{'/\\1u\u0011\u001di\u0006\u00011A\u0005\ny\u000b\u0001c\\;uaV$hi\u001c:nCR|F%Z9\u0015\u0005}\u0013\u0007CA\ra\u0013\t\t'D\u0001\u0003V]&$\bbB2]\u0003\u0003\u0005\r\u0001Z\u0001\u0004q\u0012\n\u0004GA3h!\r!uI\u001a\t\u0003'\u001d$\u0011\u0002T'\u0002\u0002\u0003\u0005)\u0011A*\t\u000f%\u0004\u0001\u0019!C\u0005U\u00061qO]5uKJ,\u0012a\u001b\t\u0005+2\u0014\u0012%\u0003\u0002n-\na!+Z2pe\u0012<&/\u001b;fe\"9q\u000e\u0001a\u0001\n\u0013\u0001\u0018AC<sSR,'o\u0018\u0013fcR\u0011q,\u001d\u0005\bG:\f\t\u00111\u0001l\u0011\u0019\u0019\b\u0001)Q\u0005W\u00069qO]5uKJ\u0004\u0003\"B;\u0001\t\u00131\u0018aB4fi\u000e{gNZ\u000b\u0002oB\u0011\u0001P_\u0007\u0002s*\u0011\u0011\u0006W\u0005\u0003wf\u0014QbQ8oM&<WO]1uS>t\u0007\"B?\u0001\t\u0003r\u0018\u0001E2sK\u0006$XMS8c\u0007>tG/\u001a=u)\u0015y\u0018QAA\b!\r)\u0016\u0011A\u0005\u0004\u0003\u00071&A\u0003&pE\u000e{g\u000e^3yi\"9\u0011q\u0001?A\u0002\u0005%\u0011\u0001\u00046pER\u0013\u0018mY6fe&#\u0007c\u0001#\u0002\f%\u0019\u0011QB%\u0003\rM#(/\u001b8h\u0011\u001d\t\t\u0002 a\u0001\u0003'\tQA[8c\u0013\u0012\u00042!GA\u000b\u0013\r\t9B\u0007\u0002\u0004\u0013:$\bbBA\u000e\u0001\u0011\u0005\u0013QD\u0001\u0019GJ,\u0017\r^3UCN\\\u0017\t\u001e;f[B$8i\u001c8uKb$HCCA\u0010\u0003K\t9#!\u000b\u0002.A\u0019Q+!\t\n\u0007\u0005\rbK\u0001\nUCN\\\u0017\t\u001e;f[B$8i\u001c8uKb$\b\u0002CA\u0004\u00033\u0001\r!!\u0003\t\u0011\u0005E\u0011\u0011\u0004a\u0001\u0003'A\u0001\"a\u000b\u0002\u001a\u0001\u0007\u00111C\u0001\bgBd\u0017\u000e^%e\u0011!\ty#!\u0007A\u0002\u0005M\u0011!\u0004;bg.\fE\u000f^3naRLE\rC\u0004\u00024\u0001!\t%!\u000e\u0002\u001f\r\u0014X-\u0019;f\u0007>lW.\u001b;uKJ$B!a\u000e\u0002>A\u0019q\"!\u000f\n\u0007\u0005m\"AA\u000fIC\u0012|w\u000e]'baJ+G-^2f\u0007>lW.\u001b;Qe>$xnY8m\u0011!\t\t\"!\rA\u0002\u0005M\u0001bBA!\u0001\u0011\u0005\u00131I\u0001\u000bS:LGo\u0016:ji\u0016\u0014H#B0\u0002F\u0005%\u0003\u0002CA$\u0003\u007f\u0001\r!a\b\u0002\u0017Q\f7o[\"p]R,\u0007\u0010\u001e\u0005\t\u0003W\ty\u00041\u0001\u0002\u0014!9\u0011Q\n\u0001\u0005B\u0005=\u0013!B<sSR,GcA0\u0002R!A\u00111KA&\u0001\u0004\t)&\u0001\u0003qC&\u0014\b#B\r\u0002XI\t\u0013bAA-5\t1A+\u001e9mKJBq!!\u0018\u0001\t\u0003\ny&A\u0006dY>\u001cXm\u0016:ji\u0016\u0014HcA0\u0002b!A\u0011qIA.\u0001\u0004\ty\u0002C\u0004\u0002f\u0001!\t%a\u001a\u0002!%t\u0017\u000e^(viB,HOR8s[\u0006$HcA0\u0002j!9\u00111NA2\u0001\u0004y\u0018A\u00036pE\u000e{g\u000e^3yi\"9\u0011q\u000e\u0001\u0005\n\u0005E\u0014aD4fi>+H\u000f];u\r>\u0014X.\u0019;\u0015\u0003QCq!!\u001e\u0001\t\u0003\n9(\u0001\u0006bgN,'\u000f^\"p]\u001a$RaXA=\u0003wBq!a\u001b\u0002t\u0001\u0007q\u0010C\u0004*\u0003g\u0002\r!! \u0011\t\u0005}\u0014\u0011Q\u0007\u0002\r%\u0019\u00111\u0011\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007"
)
public class HadoopMapReduceWriteConfigUtil<K, V> extends HadoopWriteConfigUtil<K, V> implements Logging {
   private final SerializableConfiguration conf;
   private Class<? extends OutputFormat<K, V>> outputFormat;
   private RecordWriter<K, V> writer;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   private Class<? extends OutputFormat<K, V>> outputFormat() {
      return this.outputFormat;
   }

   private void outputFormat_$eq(Class<? extends OutputFormat<K, V>> x$1) {
      this.outputFormat = x$1;
   }

   private RecordWriter<K, V> writer() {
      return this.writer;
   }

   private void writer_$eq(RecordWriter<K, V> x$1) {
      this.writer = x$1;
   }

   private Configuration getConf() {
      return this.conf.value();
   }

   public JobContext createJobContext(String jobTrackerId, int jobId) {
      TaskAttemptID jobAttemptId = new TaskAttemptID(jobTrackerId, jobId, TaskType.MAP, 0, 0);
      return new TaskAttemptContextImpl(this.getConf(), jobAttemptId);
   }

   public TaskAttemptContext createTaskAttemptContext(String jobTrackerId, int jobId, int splitId, int taskAttemptId) {
      TaskAttemptID attemptId = new TaskAttemptID(jobTrackerId, jobId, TaskType.REDUCE, splitId, taskAttemptId);
      return new TaskAttemptContextImpl(this.getConf(), attemptId);
   }

   public HadoopMapReduceCommitProtocol createCommitter(int jobId) {
      return (HadoopMapReduceCommitProtocol)FileCommitProtocol$.MODULE$.instantiate(HadoopMapReduceCommitProtocol.class.getName(), BoxesRunTime.boxToInteger(jobId).toString(), this.getConf().get("mapreduce.output.fileoutputformat.outputdir"), FileCommitProtocol$.MODULE$.instantiate$default$4());
   }

   public void initWriter(TaskAttemptContext taskContext, int splitId) {
      OutputFormat taskFormat = this.getOutputFormat();
      if (taskFormat instanceof Configurable) {
         ((Configurable)taskFormat).setConf(this.getConf());
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

      this.writer_$eq(taskFormat.getRecordWriter(taskContext));
      Predef$.MODULE$.require(this.writer() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unable to obtain RecordWriter";
         }
      });
   }

   public void write(Tuple2<K, V> pair) {
      Predef$.MODULE$.require(this.writer() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Must call createWriter before write.";
         }
      });
      this.writer().write(pair._1(), pair._2());
   }

   public void closeWriter(TaskAttemptContext taskContext) {
      if (this.writer() == null) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Writer has been closed.";
            }
         });
      } else {
         this.writer().close(taskContext);
         this.writer_$eq((RecordWriter)null);
      }

   }

   public void initOutputFormat(JobContext jobContext) {
      if (this.outputFormat() == null) {
         this.outputFormat_$eq(jobContext.getOutputFormatClass());
      }

   }

   private OutputFormat<K, V> getOutputFormat() {
      Predef$.MODULE$.require(this.outputFormat() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Must call initOutputFormat first.";
         }
      });
      return (OutputFormat)this.outputFormat().newInstance();
   }

   public void assertConf(JobContext jobContext, SparkConf conf) {
      if (SparkHadoopWriterUtils$.MODULE$.isOutputSpecValidationEnabled(conf)) {
         this.getOutputFormat().checkOutputSpecs(jobContext);
      }

   }

   public HadoopMapReduceWriteConfigUtil(SerializableConfiguration conf, ClassTag<V> evidence$4) {
      super(evidence$4);
      this.conf = conf;
      Logging$class.$init$(this);
      this.outputFormat = null;
      this.writer = null;
   }
}
