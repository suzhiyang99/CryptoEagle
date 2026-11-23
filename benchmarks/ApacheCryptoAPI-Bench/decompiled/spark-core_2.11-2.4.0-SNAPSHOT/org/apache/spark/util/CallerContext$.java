package org.apache.spark.util;

import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

public final class CallerContext$ implements Logging {
   public static final CallerContext$ MODULE$;
   private final boolean callerContextSupported;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new CallerContext$();
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

   public boolean callerContextSupported() {
      return this.callerContextSupported;
   }

   public Option<String> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$5() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$6() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$8() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$9() {
      return None$.MODULE$;
   }

   private final boolean liftedTree1$1() {
      boolean var10000;
      try {
         Utils$.MODULE$.classForName("org.apache.hadoop.ipc.CallerContext");
         Utils$.MODULE$.classForName("org.apache.hadoop.ipc.CallerContext$Builder");
         var10000 = true;
      } catch (Throwable var7) {
         boolean var3;
         if (var7 instanceof ClassNotFoundException) {
            var3 = false;
         } else {
            Option var4 = NonFatal$.MODULE$.unapply(var7);
            if (var4.isEmpty()) {
               throw var7;
            }

            Throwable e = (Throwable)var4.get();
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Fail to load the CallerContext class";
               }
            }, e);
            var3 = false;
         }

         var10000 = var3;
         return var10000;
      }

      return var10000;
   }

   private CallerContext$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.callerContextSupported = SparkHadoopUtil$.MODULE$.get().conf().getBoolean("hadoop.caller.context.enabled", false) && this.liftedTree1$1();
   }
}
