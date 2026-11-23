package org.apache.spark.util.logging;

import java.io.File;
import java.io.InputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.IntParam$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class FileAppender$ implements Logging {
   public static final FileAppender$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new FileAppender$();
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

   public FileAppender apply(InputStream inputStream, File file, SparkConf conf) {
      String rollingStrategy = conf.get(RollingFileAppender$.MODULE$.STRATEGY_PROPERTY(), RollingFileAppender$.MODULE$.STRATEGY_DEFAULT());
      String rollingSizeBytes = conf.get(RollingFileAppender$.MODULE$.SIZE_PROPERTY(), RollingFileAppender$.MODULE$.STRATEGY_DEFAULT());
      String rollingInterval = conf.get(RollingFileAppender$.MODULE$.INTERVAL_PROPERTY(), RollingFileAppender$.MODULE$.INTERVAL_DEFAULT());
      FileAppender var8;
      if ("".equals(rollingStrategy)) {
         var8 = new FileAppender(inputStream, file, this.$lessinit$greater$default$3());
      } else if ("time".equals(rollingStrategy)) {
         var8 = this.createTimeBasedAppender$1(inputStream, file, conf, rollingInterval);
      } else if ("size".equals(rollingStrategy)) {
         var8 = this.createSizeBasedAppender$1(inputStream, file, conf, rollingSizeBytes);
      } else {
         this.logWarning(new Serializable(rollingStrategy) {
            public static final long serialVersionUID = 0L;
            private final String rollingStrategy$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Illegal strategy [", "] for rolling executor logs, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rollingStrategy$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"rolling logs not enabled"})))).s(Nil$.MODULE$)).toString();
            }

            public {
               this.rollingStrategy$1 = rollingStrategy$1;
            }
         });
         var8 = new FileAppender(inputStream, file, this.$lessinit$greater$default$3());
      }

      return var8;
   }

   public int $lessinit$greater$default$3() {
      return 8192;
   }

   private final FileAppender createTimeBasedAppender$1(InputStream inputStream$1, File file$1, SparkConf conf$1, String rollingInterval$1) {
      Object validatedParams;
      if ("daily".equals(rollingInterval$1)) {
         this.logInfo(new Serializable(file$1) {
            public static final long serialVersionUID = 0L;
            private final File file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling executor logs enabled for ", " with daily rolling"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
            }

            public {
               this.file$1 = file$1;
            }
         });
         validatedParams = new Some(new Tuple2(BoxesRunTime.boxToLong(86400000L), "--yyyy-MM-dd"));
      } else if ("hourly".equals(rollingInterval$1)) {
         this.logInfo(new Serializable(file$1) {
            public static final long serialVersionUID = 0L;
            private final File file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling executor logs enabled for ", " with hourly rolling"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
            }

            public {
               this.file$1 = file$1;
            }
         });
         validatedParams = new Some(new Tuple2(BoxesRunTime.boxToLong(3600000L), "--yyyy-MM-dd--HH"));
      } else if ("minutely".equals(rollingInterval$1)) {
         this.logInfo(new Serializable(file$1) {
            public static final long serialVersionUID = 0L;
            private final File file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling executor logs enabled for ", " with rolling every minute"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
            }

            public {
               this.file$1 = file$1;
            }
         });
         validatedParams = new Some(new Tuple2(BoxesRunTime.boxToLong(60000L), "--yyyy-MM-dd--HH-mm"));
      } else {
         Option var8 = IntParam$.MODULE$.unapply(rollingInterval$1);
         if (var8.isEmpty()) {
            this.logWarning(new Serializable(rollingInterval$1) {
               public static final long serialVersionUID = 0L;
               private final String rollingInterval$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Illegal interval for rolling executor logs [", "], "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rollingInterval$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"rolling logs not enabled"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.rollingInterval$1 = rollingInterval$1;
               }
            });
            validatedParams = None$.MODULE$;
         } else {
            int seconds = BoxesRunTime.unboxToInt(var8.get());
            this.logInfo(new Serializable(file$1, seconds) {
               public static final long serialVersionUID = 0L;
               private final File file$1;
               private final int seconds$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling executor logs enabled for ", " with rolling ", " seconds"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1, BoxesRunTime.boxToInteger(this.seconds$1)}));
               }

               public {
                  this.file$1 = file$1;
                  this.seconds$1 = seconds$1;
               }
            });
            validatedParams = new Some(new Tuple2(BoxesRunTime.boxToLong((long)seconds * 1000L), "--yyyy-MM-dd--HH-mm-ss"));
         }
      }

      return (FileAppender)((Option)validatedParams).map(new Serializable(inputStream$1, file$1, conf$1) {
         public static final long serialVersionUID = 0L;
         private final InputStream inputStream$1;
         private final File file$1;
         private final SparkConf conf$1;

         public final RollingFileAppender apply(Tuple2<Object, String> x0$1) {
            if (x0$1 != null) {
               long interval = x0$1._1$mcJ$sp();
               String pattern = (String)x0$1._2();
               RollingFileAppender var6 = new RollingFileAppender(this.inputStream$1, this.file$1, new TimeBasedRollingPolicy(interval, pattern, TimeBasedRollingPolicy$.MODULE$.$lessinit$greater$default$3()), this.conf$1, RollingFileAppender$.MODULE$.$lessinit$greater$default$5());
               return var6;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.inputStream$1 = inputStream$1;
            this.file$1 = file$1;
            this.conf$1 = conf$1;
         }
      }).getOrElse(new Serializable(inputStream$1, file$1) {
         public static final long serialVersionUID = 0L;
         private final InputStream inputStream$1;
         private final File file$1;

         public final FileAppender apply() {
            return new FileAppender(this.inputStream$1, this.file$1, FileAppender$.MODULE$.$lessinit$greater$default$3());
         }

         public {
            this.inputStream$1 = inputStream$1;
            this.file$1 = file$1;
         }
      });
   }

   private final FileAppender createSizeBasedAppender$1(InputStream inputStream$1, File file$1, SparkConf conf$1, String rollingSizeBytes$1) {
      Option var6 = IntParam$.MODULE$.unapply(rollingSizeBytes$1);
      Object var8;
      if (var6.isEmpty()) {
         this.logWarning(new Serializable(rollingSizeBytes$1) {
            public static final long serialVersionUID = 0L;
            private final String rollingSizeBytes$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Illegal size [", "] for rolling executor logs, rolling logs not enabled"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rollingSizeBytes$1}));
            }

            public {
               this.rollingSizeBytes$1 = rollingSizeBytes$1;
            }
         });
         var8 = new FileAppender(inputStream$1, file$1, this.$lessinit$greater$default$3());
      } else {
         int bytes = BoxesRunTime.unboxToInt(var6.get());
         this.logInfo(new Serializable(file$1, bytes) {
            public static final long serialVersionUID = 0L;
            private final File file$1;
            private final int bytes$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling executor logs enabled for ", " with rolling every ", " bytes"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1, BoxesRunTime.boxToInteger(this.bytes$1)}));
            }

            public {
               this.file$1 = file$1;
               this.bytes$1 = bytes$1;
            }
         });
         var8 = new RollingFileAppender(inputStream$1, file$1, new SizeBasedRollingPolicy((long)bytes, SizeBasedRollingPolicy$.MODULE$.$lessinit$greater$default$2()), conf$1, RollingFileAppender$.MODULE$.$lessinit$greater$default$5());
      }

      return (FileAppender)var8;
   }

   private FileAppender$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
