package org.apache.spark.deploy;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.runtime.ObjectRef;
import scala.sys.process.ProcessBuilder;
import scala.sys.process.ProcessLogger$;
import scala.sys.process.package$;

public final class Docker$ implements Logging {
   public static final Docker$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new Docker$();
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

   public ProcessBuilder makeRunCmd(String imageTag, String args, String mountDir) {
      String var10000;
      label17: {
         label16: {
            String var5 = "";
            if (mountDir == null) {
               if (var5 == null) {
                  break label16;
               }
            } else if (mountDir.equals(var5)) {
               break label16;
            }

            var10000 = (new StringBuilder()).append((Object)" -v ").append((Object)mountDir).toString();
            break label17;
         }

         var10000 = "";
      }

      String mountCmd = var10000;
      String cmd = (new StringOps(Predef$.MODULE$.augmentString("docker run -privileged %s %s %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{mountCmd, imageTag, args}));
      this.logDebug(new Serializable(cmd) {
         public static final long serialVersionUID = 0L;
         private final String cmd$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Run command: ").append((Object)this.cmd$1).toString();
         }

         public {
            this.cmd$1 = cmd$1;
         }
      });
      return package$.MODULE$.stringToProcess(cmd);
   }

   public String makeRunCmd$default$2() {
      return "";
   }

   public String makeRunCmd$default$3() {
      return "";
   }

   public void kill(DockerId dockerId) {
      package$.MODULE$.stringToProcess((new StringOps(Predef$.MODULE$.augmentString("docker kill %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{dockerId.id()}))).$bang();
   }

   public DockerId getLastProcessId() {
      ObjectRef id = ObjectRef.create((Object)null);
      package$.MODULE$.stringToProcess("docker ps -l -q").$bang(ProcessLogger$.MODULE$.apply((Function1)(new Serializable(id) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef id$1;

         public final void apply(String line) {
            this.id$1.elem = (T)line;
         }

         public {
            this.id$1 = id$1;
         }
      })));
      return new DockerId((String)id.elem);
   }

   private Docker$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
