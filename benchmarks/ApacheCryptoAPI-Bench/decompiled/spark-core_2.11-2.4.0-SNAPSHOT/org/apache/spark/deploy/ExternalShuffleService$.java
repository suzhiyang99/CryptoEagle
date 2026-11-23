package org.apache.spark.deploy;

import java.util.concurrent.CountDownLatch;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function2;
import scala.Serializable;

public final class ExternalShuffleService$ implements Logging {
   public static final ExternalShuffleService$ MODULE$;
   private volatile ExternalShuffleService org$apache$spark$deploy$ExternalShuffleService$$server;
   private final CountDownLatch org$apache$spark$deploy$ExternalShuffleService$$barrier;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new ExternalShuffleService$();
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

   public ExternalShuffleService org$apache$spark$deploy$ExternalShuffleService$$server() {
      return this.org$apache$spark$deploy$ExternalShuffleService$$server;
   }

   private void org$apache$spark$deploy$ExternalShuffleService$$server_$eq(ExternalShuffleService x$1) {
      this.org$apache$spark$deploy$ExternalShuffleService$$server = x$1;
   }

   public CountDownLatch org$apache$spark$deploy$ExternalShuffleService$$barrier() {
      return this.org$apache$spark$deploy$ExternalShuffleService$$barrier;
   }

   public void main(String[] args) {
      this.main(args, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ExternalShuffleService apply(SparkConf conf, SecurityManager sm) {
            return new ExternalShuffleService(conf, sm);
         }
      });
   }

   public void main(String[] args, Function2<SparkConf, SecurityManager, ExternalShuffleService> newShuffleService) {
      Utils$.MODULE$.initDaemon(this.log());
      SparkConf sparkConf = new SparkConf();
      Utils$.MODULE$.loadDefaultSparkProperties(sparkConf, Utils$.MODULE$.loadDefaultSparkProperties$default$2());
      SecurityManager securityManager = new SecurityManager(sparkConf, SecurityManager$.MODULE$.$lessinit$greater$default$2());
      sparkConf.set("spark.shuffle.service.enabled", "true");
      this.org$apache$spark$deploy$ExternalShuffleService$$server_$eq((ExternalShuffleService)newShuffleService.apply(sparkConf, securityManager));
      this.org$apache$spark$deploy$ExternalShuffleService$$server().start();
      this.logDebug(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Adding shutdown hook";
         }
      });
      ShutdownHookManager$.MODULE$.addShutdownHook(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            ExternalShuffleService$.MODULE$.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Shutting down shuffle service.";
               }
            });
            ExternalShuffleService$.MODULE$.org$apache$spark$deploy$ExternalShuffleService$$server().stop();
            ExternalShuffleService$.MODULE$.org$apache$spark$deploy$ExternalShuffleService$$barrier().countDown();
         }
      });
      this.org$apache$spark$deploy$ExternalShuffleService$$barrier().await();
   }

   private ExternalShuffleService$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$ExternalShuffleService$$barrier = new CountDownLatch(1);
   }
}
