package org.apache.spark.internal.io;

import java.util.Date;
import java.util.UUID;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.JobStatus.State;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.mapred.SparkHadoopMapRedUtil$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.generic.GenericTraversableTemplate;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005eg\u0001B\u0001\u0003\u00015\u0011Q\u0004S1e_>\u0004X*\u00199SK\u0012,8-Z\"p[6LG\u000f\u0015:pi>\u001cw\u000e\u001c\u0006\u0003\u0007\u0011\t!![8\u000b\u0005\u00151\u0011\u0001C5oi\u0016\u0014h.\u00197\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u001dIA\u0002CA\b\u0011\u001b\u0005\u0011\u0011BA\t\u0003\u0005I1\u0015\u000e\\3D_6l\u0017\u000e\u001e)s_R|7m\u001c7\u0011\u0005M1R\"\u0001\u000b\u000b\u0003U\tQa]2bY\u0006L!a\u0006\u000b\u0003\u0019M+'/[1mSj\f'\r\\3\u0011\u0005eQR\"\u0001\u0003\n\u0005m!!a\u0002'pO\u001eLgn\u001a\u0005\t;\u0001\u0011\t\u0011)A\u0005=\u0005)!n\u001c2JIB\u0011qD\t\b\u0003'\u0001J!!\t\u000b\u0002\rA\u0013X\rZ3g\u0013\t\u0019CE\u0001\u0004TiJLgn\u001a\u0006\u0003CQA\u0001B\n\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0005a\u0006$\b\u000e\u0003\u0005)\u0001\t\u0005\t\u0015!\u0003*\u0003e!\u0017P\\1nS\u000e\u0004\u0016M\u001d;ji&|gn\u0014<fe^\u0014\u0018\u000e^3\u0011\u0005MQ\u0013BA\u0016\u0015\u0005\u001d\u0011un\u001c7fC:DQ!\f\u0001\u0005\u00029\na\u0001P5oSRtD\u0003B\u00181cI\u0002\"a\u0004\u0001\t\u000bua\u0003\u0019\u0001\u0010\t\u000b\u0019b\u0003\u0019\u0001\u0010\t\u000f!b\u0003\u0013!a\u0001S!IA\u0007\u0001a\u0001\u0002\u0004%I!N\u0001\nG>lW.\u001b;uKJ,\u0012A\u000e\t\u0003oqj\u0011\u0001\u000f\u0006\u0003si\n\u0011\"\\1qe\u0016$WoY3\u000b\u0005mB\u0011A\u00025bI>|\u0007/\u0003\u0002>q\tyq*\u001e;qkR\u001cu.\\7jiR,'\u000fC\u0005@\u0001\u0001\u0007\t\u0019!C\u0005\u0001\u0006i1m\\7nSR$XM]0%KF$\"!\u0011#\u0011\u0005M\u0011\u0015BA\"\u0015\u0005\u0011)f.\u001b;\t\u000f\u0015s\u0014\u0011!a\u0001m\u0005\u0019\u0001\u0010J\u0019\t\r\u001d\u0003\u0001\u0015)\u00037\u0003)\u0019w.\\7jiR,'\u000f\t\u0015\u0003\r&\u0003\"a\u0005&\n\u0005-#\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011\u001di\u0005A1A\u0005\n9\u000bA\u0002[1t-\u0006d\u0017\u000e\u001a)bi\",\u0012!\u000b\u0005\u0007!\u0002\u0001\u000b\u0011B\u0015\u0002\u001b!\f7OV1mS\u0012\u0004\u0016\r\u001e5!\u0011\u001d\u0011\u0006\u00011A\u0005\nM\u000b\u0011#\u00193eK\u0012\f%m\u001d)bi\"4\u0015\u000e\\3t+\u0005!\u0006\u0003B+[=yi\u0011A\u0016\u0006\u0003/b\u000bq!\\;uC\ndWM\u0003\u0002Z)\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005m3&aA'ba\"9Q\f\u0001a\u0001\n\u0013q\u0016!F1eI\u0016$\u0017IY:QCRDg)\u001b7fg~#S-\u001d\u000b\u0003\u0003~Cq!\u0012/\u0002\u0002\u0003\u0007A\u000b\u0003\u0004b\u0001\u0001\u0006K\u0001V\u0001\u0013C\u0012$W\rZ!cgB\u000bG\u000f\u001b$jY\u0016\u001c\b\u0005\u000b\u0002a\u0013\"9A\r\u0001a\u0001\n\u0013)\u0017A\u00049beRLG/[8o!\u0006$\bn]\u000b\u0002MB\u0019Qk\u001a\u0010\n\u0005!4&aA*fi\"9!\u000e\u0001a\u0001\n\u0013Y\u0017A\u00059beRLG/[8o!\u0006$\bn]0%KF$\"!\u00117\t\u000f\u0015K\u0017\u0011!a\u0001M\"1a\u000e\u0001Q!\n\u0019\fq\u0002]1si&$\u0018n\u001c8QCRD7\u000f\t\u0015\u0003[&CQ!\u001d\u0001\u0005\nI\f!b\u001d;bO&tw\rR5s+\u0005\u0019\bC\u0001;x\u001b\u0005)(B\u0001<;\u0003\t17/\u0003\u0002yk\n!\u0001+\u0019;i\u0011\u0015Q\b\u0001\"\u0005|\u00039\u0019X\r^;q\u0007>lW.\u001b;uKJ$\"A\u000e?\t\u000buL\b\u0019\u0001@\u0002\u000f\r|g\u000e^3yiB\u0011qg`\u0005\u0004\u0003\u0003A$A\u0005+bg.\fE\u000f^3naR\u001cuN\u001c;fqRDq!!\u0002\u0001\t\u0003\n9!A\boK^$\u0016m]6UK6\u0004h)\u001b7f)\u001dq\u0012\u0011BA\u0007\u0003/Aq!a\u0003\u0002\u0004\u0001\u0007a0A\u0006uCN\\7i\u001c8uKb$\b\u0002CA\b\u0003\u0007\u0001\r!!\u0005\u0002\u0007\u0011L'\u000f\u0005\u0003\u0014\u0003'q\u0012bAA\u000b)\t1q\n\u001d;j_:Dq!!\u0007\u0002\u0004\u0001\u0007a$A\u0002fqRDq!!\b\u0001\t\u0003\ny\"\u0001\foK^$\u0016m]6UK6\u0004h)\u001b7f\u0003\n\u001c\b+\u0019;i)\u001dq\u0012\u0011EA\u0012\u0003OAq!a\u0003\u0002\u001c\u0001\u0007a\u0010C\u0004\u0002&\u0005m\u0001\u0019\u0001\u0010\u0002\u0017\u0005\u00147o\u001c7vi\u0016$\u0015N\u001d\u0005\b\u00033\tY\u00021\u0001\u001f\u0011\u001d\tY\u0003\u0001C\u0005\u0003[\t1bZ3u\r&dWM\\1nKR)a$a\f\u00022!9\u00111BA\u0015\u0001\u0004q\bbBA\r\u0003S\u0001\rA\b\u0005\b\u0003k\u0001A\u0011IA\u001c\u0003!\u0019X\r^;q\u0015>\u0014GcA!\u0002:!A\u00111HA\u001a\u0001\u0004\ti$\u0001\u0006k_\n\u001cuN\u001c;fqR\u00042aNA \u0013\r\t\t\u0005\u000f\u0002\u000b\u0015>\u00147i\u001c8uKb$\bbBA#\u0001\u0011\u0005\u0013qI\u0001\nG>lW.\u001b;K_\n$R!QA%\u0003\u0017B\u0001\"a\u000f\u0002D\u0001\u0007\u0011Q\b\u0005\t\u0003\u001b\n\u0019\u00051\u0001\u0002P\u0005YA/Y:l\u0007>lW.\u001b;t!\u0019\t\t&!\u0019\u0002h9!\u00111KA/\u001d\u0011\t)&a\u0017\u000e\u0005\u0005]#bAA-\u0019\u00051AH]8pizJ\u0011!F\u0005\u0004\u0003?\"\u0012a\u00029bG.\fw-Z\u0005\u0005\u0003G\n)GA\u0002TKFT1!a\u0018\u0015!\u0011\tI'a\u001c\u000f\u0007=\tY'C\u0002\u0002n\t\t!CR5mK\u000e{W.\\5u!J|Go\\2pY&!\u0011\u0011OA:\u0005E!\u0016m]6D_6l\u0017\u000e^'fgN\fw-\u001a\u0006\u0004\u0003[\u0012\u0001bBA<\u0001\u0011\u0005\u0013\u0011P\u0001\tC\n|'\u000f\u001e&pER\u0019\u0011)a\u001f\t\u0011\u0005m\u0012Q\u000fa\u0001\u0003{Aq!a \u0001\t\u0003\n\t)A\u0005tKR,\b\u000fV1tWR\u0019\u0011)a!\t\u000f\u0005-\u0011Q\u0010a\u0001}\"9\u0011q\u0011\u0001\u0005B\u0005%\u0015AC2p[6LG\u000fV1tWR!\u0011qMAF\u0011\u001d\tY!!\"A\u0002yDq!a$\u0001\t\u0003\n\t*A\u0005bE>\u0014H\u000fV1tWR\u0019\u0011)a%\t\u000f\u0005-\u0011Q\u0012a\u0001}\u001eI\u0011q\u0013\u0002\u0002\u0002#\u0005\u0011\u0011T\u0001\u001e\u0011\u0006$wn\u001c9NCB\u0014V\rZ;dK\u000e{W.\\5u!J|Go\\2pYB\u0019q\"a'\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\u0003;\u001bR!a'\u0002 J\u00012aEAQ\u0013\r\t\u0019\u000b\u0006\u0002\u0007\u0003:L(+\u001a4\t\u000f5\nY\n\"\u0001\u0002(R\u0011\u0011\u0011\u0014\u0005\u000b\u0003W\u000bY*%A\u0005\u0002\u00055\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$3'\u0006\u0002\u00020*\u001a\u0011&!-,\u0005\u0005M\u0006\u0003BA[\u0003\u007fk!!a.\u000b\t\u0005e\u00161X\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!0\u0015\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003\u0003\f9LA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D!\"!2\u0002\u001c\u0006\u0005I\u0011BAd\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005%\u0007\u0003BAf\u0003+l!!!4\u000b\t\u0005=\u0017\u0011[\u0001\u0005Y\u0006twM\u0003\u0002\u0002T\u0006!!.\u0019<b\u0013\u0011\t9.!4\u0003\r=\u0013'.Z2u\u0001"
)
public class HadoopMapReduceCommitProtocol extends FileCommitProtocol implements Serializable, Logging {
   private final String jobId;
   public final String org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path;
   private final boolean dynamicPartitionOverwrite;
   private transient OutputCommitter committer;
   private final boolean hasValidPath;
   private transient Map<String, String> addedAbsPathFiles;
   private transient Set<String> partitionPaths;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean $lessinit$greater$default$3() {
      return HadoopMapReduceCommitProtocol$.MODULE$.$lessinit$greater$default$3();
   }

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

   private OutputCommitter committer() {
      return this.committer;
   }

   private void committer_$eq(OutputCommitter x$1) {
      this.committer = x$1;
   }

   private boolean hasValidPath() {
      return this.hasValidPath;
   }

   private Map<String, String> addedAbsPathFiles() {
      return this.addedAbsPathFiles;
   }

   private void addedAbsPathFiles_$eq(Map<String, String> x$1) {
      this.addedAbsPathFiles = x$1;
   }

   private Set<String> partitionPaths() {
      return this.partitionPaths;
   }

   private void partitionPaths_$eq(Set<String> x$1) {
      this.partitionPaths = x$1;
   }

   public Path org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir() {
      return new Path(this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path, (new StringBuilder()).append((Object)".spark-staging-").append((Object)this.jobId).toString());
   }

   public OutputCommitter setupCommitter(TaskAttemptContext context) {
      OutputFormat format = (OutputFormat)context.getOutputFormatClass().newInstance();
      if (format instanceof Configurable) {
         ((Configurable)format).setConf(context.getConfiguration());
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

      return format.getOutputCommitter(context);
   }

   public String newTaskTempFile(TaskAttemptContext taskContext, Option<String> dir, String ext) {
      String filename = this.getFilename(taskContext, ext);
      OutputCommitter var6 = this.committer();
      Path stagingDir;
      if (this.dynamicPartitionOverwrite) {
         Predef$.MODULE$.assert(dir.isDefined(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "The dataset to be written must be partitioned when dynamicPartitionOverwrite is true.";
            }
         });
         this.partitionPaths().$plus$eq(dir.get());
         stagingDir = this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir();
      } else if (var6 instanceof FileOutputCommitter) {
         FileOutputCommitter var8 = (FileOutputCommitter)var6;
         stagingDir = new Path((String)Option$.MODULE$.apply(var8.getWorkPath()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(Path x$1) {
               return x$1.toString();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HadoopMapReduceCommitProtocol $outer;

            public final String apply() {
               return this.$outer.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }));
      } else {
         stagingDir = new Path(this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path);
      }

      return (String)dir.map(new Serializable(this, filename, stagingDir) {
         public static final long serialVersionUID = 0L;
         private final String filename$1;
         private final Path stagingDir$1;

         public final String apply(String d) {
            return (new Path(new Path(this.stagingDir$1, d), this.filename$1)).toString();
         }

         public {
            this.filename$1 = filename$1;
            this.stagingDir$1 = stagingDir$1;
         }
      }).getOrElse(new Serializable(this, filename, stagingDir) {
         public static final long serialVersionUID = 0L;
         private final String filename$1;
         private final Path stagingDir$1;

         public final String apply() {
            return (new Path(this.stagingDir$1, this.filename$1)).toString();
         }

         public {
            this.filename$1 = filename$1;
            this.stagingDir$1 = stagingDir$1;
         }
      });
   }

   public String newTaskTempFileAbsPath(TaskAttemptContext taskContext, String absoluteDir, String ext) {
      String filename = this.getFilename(taskContext, ext);
      String absOutputPath = (new Path(absoluteDir, filename)).toString();
      String tmpOutputPath = (new Path(this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir(), (new StringBuilder()).append((Object)UUID.randomUUID().toString()).append((Object)"-").append((Object)filename).toString())).toString();
      this.addedAbsPathFiles().update(tmpOutputPath, absOutputPath);
      return tmpOutputPath;
   }

   private String getFilename(TaskAttemptContext taskContext, String ext) {
      int split = taskContext.getTaskAttemptID().getTaskID().getId();
      Object arg$macro$2 = this.jobId;
      return (new StringOps("part-%05d-%s%s")).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(split), arg$macro$2, ext}));
   }

   public void setupJob(JobContext jobContext) {
      JobID jobId = SparkHadoopWriterUtils$.MODULE$.createJobID(new Date(), 0);
      TaskID taskId = new TaskID(jobId, TaskType.MAP, 0);
      TaskAttemptID taskAttemptId = new TaskAttemptID(taskId, 0);
      jobContext.getConfiguration().set("mapreduce.job.id", jobId.toString());
      jobContext.getConfiguration().set("mapreduce.task.id", taskAttemptId.getTaskID().toString());
      jobContext.getConfiguration().set("mapreduce.task.attempt.id", taskAttemptId.toString());
      jobContext.getConfiguration().setBoolean("mapreduce.task.ismap", true);
      jobContext.getConfiguration().setInt("mapreduce.task.partition", 0);
      TaskAttemptContextImpl taskAttemptContext = new TaskAttemptContextImpl(jobContext.getConfiguration(), taskAttemptId);
      this.committer_$eq(this.setupCommitter(taskAttemptContext));
      this.committer().setupJob(jobContext);
   }

   public void commitJob(JobContext jobContext, Seq<FileCommitProtocol.TaskCommitMessage> taskCommits) {
      this.committer().commitJob(jobContext);
      if (this.hasValidPath()) {
         Tuple2 var4 = ((GenericTraversableTemplate)taskCommits.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<scala.collection.immutable.Map<String, String>, scala.collection.immutable.Set<String>> apply(FileCommitProtocol.TaskCommitMessage x$2) {
               return (Tuple2)x$2.obj();
            }
         }, Seq$.MODULE$.canBuildFrom())).unzip(Predef$.MODULE$.$conforms());
         if (var4 == null) {
            throw new MatchError(var4);
         }

         Seq allAbsPathFiles = (Seq)var4._1();
         Seq allPartitionPaths = (Seq)var4._2();
         Tuple2 var7 = new Tuple2(allAbsPathFiles, allPartitionPaths);
         Seq allAbsPathFiles = (Seq)var7._1();
         Seq allPartitionPaths = (Seq)var7._2();
         FileSystem fs = this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir().getFileSystem(jobContext.getConfiguration());
         scala.collection.immutable.Map filesToMove = (scala.collection.immutable.Map)allAbsPathFiles.foldLeft(Predef$.MODULE$.Map().apply(Nil$.MODULE$), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final scala.collection.immutable.Map<String, String> apply(scala.collection.immutable.Map<String, String> x$4, scala.collection.immutable.Map<String, String> x$5) {
               return x$4.$plus$plus(x$5);
            }
         });
         this.logDebug(new Serializable(this, filesToMove) {
            public static final long serialVersionUID = 0L;
            private final scala.collection.immutable.Map filesToMove$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Committing files staged for absolute locations ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.filesToMove$1}));
            }

            public {
               this.filesToMove$1 = filesToMove$1;
            }
         });
         if (this.dynamicPartitionOverwrite) {
            scala.collection.immutable.Set absPartitionPaths = ((TraversableOnce)filesToMove.values().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Path apply(String x$6) {
                  return (new Path(x$6)).getParent();
               }
            }, Iterable$.MODULE$.canBuildFrom())).toSet();
            this.logDebug(new Serializable(this, absPartitionPaths) {
               public static final long serialVersionUID = 0L;
               private final scala.collection.immutable.Set absPartitionPaths$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clean up absolute partition directories for overwriting: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.absPartitionPaths$1}));
               }

               public {
                  this.absPartitionPaths$1 = absPartitionPaths$1;
               }
            });
            absPartitionPaths.foreach(new Serializable(this, fs) {
               public static final long serialVersionUID = 0L;
               private final FileSystem fs$1;

               public final boolean apply(Path x$7) {
                  return this.fs$1.delete(x$7, true);
               }

               public {
                  this.fs$1 = fs$1;
               }
            });
         }

         filesToMove.withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
               boolean var3;
               if (check$ifrefutable$1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).foreach(new Serializable(this, fs) {
            public static final long serialVersionUID = 0L;
            private final FileSystem fs$1;

            public final boolean apply(Tuple2<String, String> x$8) {
               if (x$8 != null) {
                  String src = (String)x$8._1();
                  String dst = (String)x$8._2();
                  boolean var5 = this.fs$1.rename(new Path(src), new Path(dst));
                  return var5;
               } else {
                  throw new MatchError(x$8);
               }
            }

            public {
               this.fs$1 = fs$1;
            }
         });
         if (this.dynamicPartitionOverwrite) {
            scala.collection.immutable.Set partitionPaths = (scala.collection.immutable.Set)allPartitionPaths.foldLeft(Predef$.MODULE$.Set().apply(Nil$.MODULE$), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final scala.collection.immutable.Set<String> apply(scala.collection.immutable.Set<String> x$9, scala.collection.immutable.Set<String> x$10) {
                  return (scala.collection.immutable.Set)x$9.$plus$plus(x$10);
               }
            });
            this.logDebug(new Serializable(this, partitionPaths) {
               public static final long serialVersionUID = 0L;
               private final scala.collection.immutable.Set partitionPaths$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clean up default partition directories for overwriting: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.partitionPaths$1}));
               }

               public {
                  this.partitionPaths$1 = partitionPaths$1;
               }
            });
            partitionPaths.foreach(new Serializable(this, fs) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final HadoopMapReduceCommitProtocol $outer;
               private final FileSystem fs$1;

               public final boolean apply(String part) {
                  Path finalPartPath = new Path(this.$outer.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path, part);
                  this.fs$1.delete(finalPartPath, true);
                  return this.fs$1.rename(new Path(this.$outer.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir(), part), finalPartPath);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.fs$1 = fs$1;
                  }
               }
            });
         }

         fs.delete(this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir(), true);
      }

   }

   public void abortJob(JobContext jobContext) {
      this.committer().abortJob(jobContext, State.FAILED);
      if (this.hasValidPath()) {
         FileSystem fs = this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir().getFileSystem(jobContext.getConfiguration());
         fs.delete(this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$stagingDir(), true);
      }

   }

   public void setupTask(TaskAttemptContext taskContext) {
      this.committer_$eq(this.setupCommitter(taskContext));
      this.committer().setupTask(taskContext);
      this.addedAbsPathFiles_$eq((Map)Map$.MODULE$.apply(Nil$.MODULE$));
      this.partitionPaths_$eq((Set)Set$.MODULE$.apply(Nil$.MODULE$));
   }

   public FileCommitProtocol.TaskCommitMessage commitTask(TaskAttemptContext taskContext) {
      TaskAttemptID attemptId = taskContext.getTaskAttemptID();
      SparkHadoopMapRedUtil$.MODULE$.commitTask(this.committer(), taskContext, attemptId.getJobID().getId(), attemptId.getTaskID().getId());
      return new FileCommitProtocol.TaskCommitMessage(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(this.addedAbsPathFiles().toMap(Predef$.MODULE$.$conforms())), this.partitionPaths().toSet()));
   }

   public void abortTask(TaskAttemptContext taskContext) {
      this.committer().abortTask(taskContext);
      this.addedAbsPathFiles().withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, taskContext) {
         public static final long serialVersionUID = 0L;
         private final TaskAttemptContext taskContext$1;

         public final boolean apply(Tuple2<String, String> x$11) {
            if (x$11 != null) {
               String src = (String)x$11._1();
               Path tmp = new Path(src);
               boolean var4 = tmp.getFileSystem(this.taskContext$1.getConfiguration()).delete(tmp, false);
               return var4;
            } else {
               throw new MatchError(x$11);
            }
         }

         public {
            this.taskContext$1 = taskContext$1;
         }
      });
   }

   public HadoopMapReduceCommitProtocol(String jobId, String path, boolean dynamicPartitionOverwrite) {
      this.jobId = jobId;
      this.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path = path;
      this.dynamicPartitionOverwrite = dynamicPartitionOverwrite;
      Logging$class.$init$(this);
      this.hasValidPath = Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopMapReduceCommitProtocol $outer;

         public final Path apply() {
            return new Path(this.$outer.org$apache$spark$internal$io$HadoopMapReduceCommitProtocol$$path);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).isSuccess();
      this.addedAbsPathFiles = null;
      this.partitionPaths = null;
   }
}
