package org.apache.spark.internal.io;

import java.text.NumberFormat;
import java.util.Locale;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobContextImpl;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TaskAttemptContextImpl;
import org.apache.hadoop.mapred.TaskAttemptID;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.spark.SerializableWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.HadoopRDD$;
import org.apache.spark.util.SerializableJobConf;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001de!B\u0001\u0003\u0001\u0019a!a\u0007%bI>|\u0007/T1q%\u0016$wK]5uK\u000e{gNZ5h+RLGN\u0003\u0002\u0004\t\u0005\u0011\u0011n\u001c\u0006\u0003\u000b\u0019\t\u0001\"\u001b8uKJt\u0017\r\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0019Q\u0002\u0006\u0012\u0014\u0007\u0001qA\u0005\u0005\u0003\u0010!I\tS\"\u0001\u0002\n\u0005E\u0011!!\u0006%bI>|\u0007o\u0016:ji\u0016\u001cuN\u001c4jOV#\u0018\u000e\u001c\t\u0003'Qa\u0001\u0001B\u0003\u0016\u0001\t\u0007qCA\u0001L\u0007\u0001\t\"\u0001\u0007\u0010\u0011\u0005eaR\"\u0001\u000e\u000b\u0003m\tQa]2bY\u0006L!!\b\u000e\u0003\u000f9{G\u000f[5oOB\u0011\u0011dH\u0005\u0003Ai\u00111!\u00118z!\t\u0019\"\u0005B\u0003$\u0001\t\u0007qCA\u0001W!\t)c%D\u0001\u0005\u0013\t9CAA\u0004M_\u001e<\u0017N\\4\t\u0011%\u0002!\u0011!Q\u0001\n)\nAaY8oMB\u00111FL\u0007\u0002Y)\u0011QFB\u0001\u0005kRLG.\u0003\u00020Y\t\u00192+\u001a:jC2L'0\u00192mK*{'mQ8oM\"A\u0011\u0007\u0001B\u0002B\u0003-!'\u0001\u0006fm&$WM\\2fIM\u00022a\r\u001c\"\u001b\u0005!$BA\u001b\u001b\u0003\u001d\u0011XM\u001a7fGRL!a\u000e\u001b\u0003\u0011\rc\u0017m]:UC\u001eDQ!\u000f\u0001\u0005\u0002i\na\u0001P5oSRtDCA\u001e?)\taT\b\u0005\u0003\u0010\u0001I\t\u0003\"B\u00199\u0001\b\u0011\u0004\"B\u00159\u0001\u0004Q\u0003b\u0002!\u0001\u0001\u0004%I!Q\u0001\r_V$\b/\u001e;G_Jl\u0017\r^\u000b\u0002\u0005B\u00121i\u0013\t\u0004\t\u001eSeBA\rF\u0013\t1%$\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0011&\u0013Qa\u00117bgNT!A\u0012\u000e\u0011\u0005MYE!\u0003'N\u0003\u0003\u0005\tQ!\u0001T\u0005\ryF%\r\u0005\u0007\u001d\u0002\u0001\u000b\u0015B(\u0002\u001b=,H\u000f];u\r>\u0014X.\u0019;!a\t\u0001&\u000bE\u0002E\u000fF\u0003\"a\u0005*\u0005\u00131k\u0015\u0011!A\u0001\u0006\u0003\u0019\u0016C\u0001\rU!\u0011)&LE\u0011\u000e\u0003YS!a\u0016-\u0002\r5\f\u0007O]3e\u0015\tI\u0006\"\u0001\u0004iC\u0012|w\u000e]\u0005\u00037Z\u0013AbT;uaV$hi\u001c:nCRDq!\u0018\u0001A\u0002\u0013%a,\u0001\tpkR\u0004X\u000f\u001e$pe6\fGo\u0018\u0013fcR\u0011qL\u0019\t\u00033\u0001L!!\u0019\u000e\u0003\tUs\u0017\u000e\u001e\u0005\bGr\u000b\t\u00111\u0001e\u0003\rAH%\r\u0019\u0003K\u001e\u00042\u0001R$g!\t\u0019r\rB\u0005M\u001b\u0006\u0005\t\u0011!B\u0001'\"9\u0011\u000e\u0001a\u0001\n\u0013Q\u0017AB<sSR,'/F\u0001l!\u0011)FNE\u0011\n\u000554&\u0001\u0004*fG>\u0014Hm\u0016:ji\u0016\u0014\bbB8\u0001\u0001\u0004%I\u0001]\u0001\u000boJLG/\u001a:`I\u0015\fHCA0r\u0011\u001d\u0019g.!AA\u0002-Daa\u001d\u0001!B\u0013Y\u0017aB<sSR,'\u000f\t\u0005\u0006k\u0002!IA^\u0001\bO\u0016$8i\u001c8g+\u00059\bCA+y\u0013\tIhKA\u0004K_\n\u001cuN\u001c4\t\u000bm\u0004A\u0011\t?\u0002!\r\u0014X-\u0019;f\u0015>\u00147i\u001c8uKb$H#B?\u0002\b\u0005E\u0001c\u0001@\u0002\u00045\tqPC\u0002\u0002\u0002a\u000b\u0011\"\\1qe\u0016$WoY3\n\u0007\u0005\u0015qP\u0001\u0006K_\n\u001cuN\u001c;fqRDq!!\u0003{\u0001\u0004\tY!\u0001\u0007k_\n$&/Y2lKJLE\rE\u0002E\u0003\u001bI1!a\u0004J\u0005\u0019\u0019FO]5oO\"9\u00111\u0003>A\u0002\u0005U\u0011!\u00026pE&#\u0007cA\r\u0002\u0018%\u0019\u0011\u0011\u0004\u000e\u0003\u0007%sG\u000fC\u0004\u0002\u001e\u0001!\t%a\b\u00021\r\u0014X-\u0019;f)\u0006\u001c8.\u0011;uK6\u0004HoQ8oi\u0016DH\u000f\u0006\u0006\u0002\"\u0005\u001d\u0012\u0011FA\u0016\u0003_\u00012A`A\u0012\u0013\r\t)c \u0002\u0013)\u0006\u001c8.\u0011;uK6\u0004HoQ8oi\u0016DH\u000f\u0003\u0005\u0002\n\u0005m\u0001\u0019AA\u0006\u0011!\t\u0019\"a\u0007A\u0002\u0005U\u0001\u0002CA\u0017\u00037\u0001\r!!\u0006\u0002\u000fM\u0004H.\u001b;JI\"A\u0011\u0011GA\u000e\u0001\u0004\t)\"A\u0007uCN\\\u0017\t\u001e;f[B$\u0018\n\u001a\u0005\b\u0003k\u0001A\u0011IA\u001c\u0003=\u0019'/Z1uK\u000e{W.\\5ui\u0016\u0014H\u0003BA\u001d\u0003\u007f\u00012aDA\u001e\u0013\r\tiD\u0001\u0002\u001e\u0011\u0006$wn\u001c9NCB\u0014V\rZ;dK\u000e{W.\\5u!J|Go\\2pY\"A\u00111CA\u001a\u0001\u0004\t)\u0002C\u0004\u0002D\u0001!\t%!\u0012\u0002\u0015%t\u0017\u000e^,sSR,'\u000fF\u0003`\u0003\u000f\nY\u0005\u0003\u0005\u0002J\u0005\u0005\u0003\u0019AA\u0011\u0003-!\u0018m]6D_:$X\r\u001f;\t\u0011\u00055\u0012\u0011\ta\u0001\u0003+Aq!a\u0014\u0001\t\u0003\n\t&A\u0003xe&$X\rF\u0002`\u0003'B\u0001\"!\u0016\u0002N\u0001\u0007\u0011qK\u0001\u0005a\u0006L'\u000fE\u0003\u001a\u00033\u0012\u0012%C\u0002\u0002\\i\u0011a\u0001V;qY\u0016\u0014\u0004bBA0\u0001\u0011\u0005\u0013\u0011M\u0001\fG2|7/Z,sSR,'\u000fF\u0002`\u0003GB\u0001\"!\u0013\u0002^\u0001\u0007\u0011\u0011\u0005\u0005\b\u0003O\u0002A\u0011IA5\u0003AIg.\u001b;PkR\u0004X\u000f\u001e$pe6\fG\u000fF\u0002`\u0003WBq!!\u001c\u0002f\u0001\u0007Q0\u0001\u0006k_\n\u001cuN\u001c;fqRDq!!\u001d\u0001\t\u0013\t\u0019(A\bhKR|U\u000f\u001e9vi\u001a{'/\\1u)\u0005!\u0006bBA<\u0001\u0011\u0005\u0013\u0011P\u0001\u000bCN\u001cXM\u001d;D_:4G#B0\u0002|\u0005u\u0004bBA7\u0003k\u0002\r! \u0005\bS\u0005U\u0004\u0019AA@!\u0011\t\t)a!\u000e\u0003\u0019I1!!\"\u0007\u0005%\u0019\u0006/\u0019:l\u0007>tg\r"
)
public class HadoopMapRedWriteConfigUtil<K, V> extends HadoopWriteConfigUtil<K, V> implements Logging {
   private final SerializableJobConf conf;
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

   private JobConf getConf() {
      return this.conf.value();
   }

   public JobContext createJobContext(String jobTrackerId, int jobId) {
      SerializableWritable jobAttemptId = new SerializableWritable(new JobID(jobTrackerId, jobId));
      return new JobContextImpl(this.getConf(), (org.apache.hadoop.mapreduce.JobID)jobAttemptId.value());
   }

   public TaskAttemptContext createTaskAttemptContext(String jobTrackerId, int jobId, int splitId, int taskAttemptId) {
      HadoopRDD$.MODULE$.addLocalConfiguration(jobTrackerId, jobId, splitId, taskAttemptId, this.conf.value());
      TaskAttemptID attemptId = new TaskAttemptID(jobTrackerId, jobId, TaskType.MAP, splitId, taskAttemptId);
      return new TaskAttemptContextImpl(this.getConf(), attemptId);
   }

   public HadoopMapReduceCommitProtocol createCommitter(int jobId) {
      HadoopRDD$.MODULE$.addLocalConfiguration("", 0, 0, 0, this.getConf());
      return (HadoopMapReduceCommitProtocol)FileCommitProtocol$.MODULE$.instantiate(HadoopMapRedCommitProtocol.class.getName(), BoxesRunTime.boxToInteger(jobId).toString(), this.getConf().get("mapred.output.dir"), FileCommitProtocol$.MODULE$.instantiate$default$4());
   }

   public void initWriter(TaskAttemptContext taskContext, int splitId) {
      NumberFormat numfmt = NumberFormat.getInstance(Locale.US);
      numfmt.setMinimumIntegerDigits(5);
      numfmt.setGroupingUsed(false);
      String outputName = (new StringBuilder()).append((Object)"part-").append((Object)numfmt.format((long)splitId)).toString();
      Path path = FileOutputFormat.getOutputPath(this.getConf());
      FileSystem fs = path == null ? FileSystem.get(this.getConf()) : path.getFileSystem(this.getConf());
      this.writer_$eq(this.getConf().getOutputFormat().getRecordWriter(fs, this.getConf(), outputName, Reporter.NULL));
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
      if (this.writer() != null) {
         this.writer().close(Reporter.NULL);
      }

   }

   public void initOutputFormat(JobContext jobContext) {
      if (this.outputFormat() == null) {
         this.outputFormat_$eq(this.getConf().getOutputFormat().getClass());
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
      OutputFormat outputFormatInstance = this.getOutputFormat();
      Class keyClass = this.getConf().getOutputKeyClass();
      Class valueClass = this.getConf().getOutputValueClass();
      if (outputFormatInstance == null) {
         throw new SparkException("Output format class not set");
      } else if (keyClass == null) {
         throw new SparkException("Output key class not set");
      } else if (valueClass == null) {
         throw new SparkException("Output value class not set");
      } else {
         SparkHadoopUtil$.MODULE$.get().addCredentials(this.getConf());
         this.logDebug(new Serializable(this, keyClass, valueClass) {
            public static final long serialVersionUID = 0L;
            private final Class keyClass$1;
            private final Class valueClass$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Saving as hadoop file of type (").append((Object)this.keyClass$1.getSimpleName()).append((Object)", ").append((Object)this.valueClass$1.getSimpleName()).append((Object)")").toString();
            }

            public {
               this.keyClass$1 = keyClass$1;
               this.valueClass$1 = valueClass$1;
            }
         });
         if (SparkHadoopWriterUtils$.MODULE$.isOutputSpecValidationEnabled(conf)) {
            FileSystem ignoredFs = FileSystem.get(this.getConf());
            this.getOutputFormat().checkOutputSpecs(ignoredFs, this.getConf());
         }

      }
   }

   public HadoopMapRedWriteConfigUtil(SerializableJobConf conf, ClassTag<V> evidence$3) {
      super(evidence$3);
      this.conf = conf;
      Logging$class.$init$(this);
      this.outputFormat = null;
      this.writer = null;
   }
}
