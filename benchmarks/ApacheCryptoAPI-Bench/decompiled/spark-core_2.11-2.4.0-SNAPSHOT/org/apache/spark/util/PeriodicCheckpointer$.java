package org.apache.spark.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.runtime.TraitSetter;

public final class PeriodicCheckpointer$ implements Logging {
   public static final PeriodicCheckpointer$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new PeriodicCheckpointer$();
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

   public void removeCheckpointFile(String checkpointFile, Configuration conf) {
      try {
         Path path = new Path(checkpointFile);
         FileSystem fs = path.getFileSystem(conf);
         fs.delete(path, true);
      } catch (Exception var6) {
         this.logWarning(new Serializable(checkpointFile) {
            public static final long serialVersionUID = 0L;
            private final String checkpointFile$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"PeriodicCheckpointer could not remove old checkpoint file: ").append((Object)this.checkpointFile$1).toString();
            }

            public {
               this.checkpointFile$1 = checkpointFile$1;
            }
         });
      }

   }

   private PeriodicCheckpointer$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
