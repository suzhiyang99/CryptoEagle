package org.apache.spark.deploy.master;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.Serializer;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)3Q!\u0001\u0002\u0001\u00051\u0011QDR5mKNK8\u000f^3n%\u0016\u001cwN^3ss6{G-\u001a$bGR|'/\u001f\u0006\u0003\u0007\u0011\ta!\\1ti\u0016\u0014(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bE\u0001\"AD\b\u000e\u0003\tI!\u0001\u0005\u0002\u0003;M#\u0018M\u001c3bY>tWMU3d_Z,'/_'pI\u00164\u0015m\u0019;pef\u0004\"AE\u000b\u000e\u0003MQ!\u0001\u0006\u0004\u0002\u0011%tG/\u001a:oC2L!AF\n\u0003\u000f1{wmZ5oO\"A\u0001\u0004\u0001B\u0001B\u0003%!$\u0001\u0003d_:47\u0001\u0001\t\u00037qi\u0011AB\u0005\u0003;\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011}\u0001!\u0011!Q\u0001\n\u0001\n!b]3sS\u0006d\u0017N_3s!\t\t3%D\u0001#\u0015\tyb!\u0003\u0002%E\tQ1+\u001a:jC2L'0\u001a:\t\u000b\u0019\u0002A\u0011A\u0014\u0002\rqJg.\u001b;?)\rA\u0013F\u000b\t\u0003\u001d\u0001AQ\u0001G\u0013A\u0002iAQaH\u0013A\u0002\u0001Bq\u0001\f\u0001C\u0002\u0013\u0005Q&\u0001\u0007S\u000b\u000e{e+\u0012*Z?\u0012K%+F\u0001/!\tySG\u0004\u00021g5\t\u0011GC\u00013\u0003\u0015\u00198-\u00197b\u0013\t!\u0014'\u0001\u0004Qe\u0016$WMZ\u0005\u0003m]\u0012aa\u0015;sS:<'B\u0001\u001b2\u0011\u0019I\u0004\u0001)A\u0005]\u0005i!+R\"P-\u0016\u0013\u0016l\u0018#J%\u0002BQa\u000f\u0001\u0005\u0002q\nqc\u0019:fCR,\u0007+\u001a:tSN$XM\\2f\u000b:<\u0017N\\3\u0015\u0003u\u0002\"A\u0004 \n\u0005}\u0012!!\u0005)feNL7\u000f^3oG\u0016,enZ5oK\")\u0011\t\u0001C\u0001\u0005\u0006I2M]3bi\u0016dU-\u00193fe\u0016cWm\u0019;j_:\fu-\u001a8u)\t\u0019e\t\u0005\u0002\u000f\t&\u0011QI\u0001\u0002\u0014\u0019\u0016\fG-\u001a:FY\u0016\u001cG/[8o\u0003\u001e,g\u000e\u001e\u0005\u0006\u0007\u0001\u0003\ra\u0012\t\u0003\u001d!K!!\u0013\u0002\u0003\u001f1+\u0017\rZ3s\u000b2,7\r^1cY\u0016\u0004"
)
public class FileSystemRecoveryModeFactory extends StandaloneRecoveryModeFactory implements Logging {
   private final Serializer serializer;
   private final String RECOVERY_DIR;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public String RECOVERY_DIR() {
      return this.RECOVERY_DIR;
   }

   public PersistenceEngine createPersistenceEngine() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileSystemRecoveryModeFactory $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Persisting recovery state to directory: ").append((Object)this.$outer.RECOVERY_DIR()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return new FileSystemPersistenceEngine(this.RECOVERY_DIR(), this.serializer);
   }

   public LeaderElectionAgent createLeaderElectionAgent(LeaderElectable master) {
      return new MonarchyLeaderAgent(master);
   }

   public FileSystemRecoveryModeFactory(SparkConf conf, Serializer serializer) {
      super(conf, serializer);
      this.serializer = serializer;
      Logging$class.$init$(this);
      this.RECOVERY_DIR = conf.get("spark.deploy.recoveryDirectory", "");
   }
}
