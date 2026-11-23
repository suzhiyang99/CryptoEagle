package org.apache.spark.rdd;

import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class ReliableRDDCheckpointData$ implements Logging, Serializable {
   public static final ReliableRDDCheckpointData$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new ReliableRDDCheckpointData$();
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

   public Option<Path> checkpointPath(SparkContext sc, int rddId) {
      return sc.checkpointDir().<Path>map(new Serializable(rddId) {
         public static final long serialVersionUID = 0L;
         private final int rddId$1;

         public final Path apply(String dir) {
            return new Path(dir, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"rdd-", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.rddId$1)})));
         }

         public {
            this.rddId$1 = rddId$1;
         }
      });
   }

   public void cleanCheckpoint(SparkContext sc, int rddId) {
      this.checkpointPath(sc, rddId).foreach(new Serializable(sc) {
         public static final long serialVersionUID = 0L;
         private final SparkContext sc$1;

         public final boolean apply(Path path) {
            return path.getFileSystem(this.sc$1.hadoopConfiguration()).delete(path, true);
         }

         public {
            this.sc$1 = sc$1;
         }
      });
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ReliableRDDCheckpointData$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
