package org.apache.spark.internal;

import java.net.URL;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxedUnit;

public abstract class Logging$class {
   public static String logName(Logging $this) {
      return (new StringOps(Predef$.MODULE$.augmentString($this.getClass().getName()))).stripSuffix("$");
   }

   public static Logger log(Logging $this) {
      if ($this.org$apache$spark$internal$Logging$$log_() == null) {
         $this.initializeLogIfNecessary(false);
         $this.org$apache$spark$internal$Logging$$log__$eq(LoggerFactory.getLogger($this.logName()));
      }

      return $this.org$apache$spark$internal$Logging$$log_();
   }

   public static void logInfo(Logging $this, Function0 msg) {
      if ($this.log().isInfoEnabled()) {
         $this.log().info((String)msg.apply());
      }

   }

   public static void logDebug(Logging $this, Function0 msg) {
      if ($this.log().isDebugEnabled()) {
         $this.log().debug((String)msg.apply());
      }

   }

   public static void logTrace(Logging $this, Function0 msg) {
      if ($this.log().isTraceEnabled()) {
         $this.log().trace((String)msg.apply());
      }

   }

   public static void logWarning(Logging $this, Function0 msg) {
      if ($this.log().isWarnEnabled()) {
         $this.log().warn((String)msg.apply());
      }

   }

   public static void logError(Logging $this, Function0 msg) {
      if ($this.log().isErrorEnabled()) {
         $this.log().error((String)msg.apply());
      }

   }

   public static void logInfo(Logging $this, Function0 msg, Throwable throwable) {
      if ($this.log().isInfoEnabled()) {
         $this.log().info((String)msg.apply(), throwable);
      }

   }

   public static void logDebug(Logging $this, Function0 msg, Throwable throwable) {
      if ($this.log().isDebugEnabled()) {
         $this.log().debug((String)msg.apply(), throwable);
      }

   }

   public static void logTrace(Logging $this, Function0 msg, Throwable throwable) {
      if ($this.log().isTraceEnabled()) {
         $this.log().trace((String)msg.apply(), throwable);
      }

   }

   public static void logWarning(Logging $this, Function0 msg, Throwable throwable) {
      if ($this.log().isWarnEnabled()) {
         $this.log().warn((String)msg.apply(), throwable);
      }

   }

   public static void logError(Logging $this, Function0 msg, Throwable throwable) {
      if ($this.log().isErrorEnabled()) {
         $this.log().error((String)msg.apply(), throwable);
      }

   }

   public static boolean isTraceEnabled(Logging $this) {
      return $this.log().isTraceEnabled();
   }

   public static void initializeLogIfNecessary(Logging $this, boolean isInterpreter) {
      $this.initializeLogIfNecessary(isInterpreter, false);
   }

   public static boolean initializeLogIfNecessary(Logging $this, boolean isInterpreter, boolean silent) {
      if (Logging$.MODULE$.org$apache$spark$internal$Logging$$initialized()) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         synchronized(Logging$.MODULE$.initLock()) {
            if (!Logging$.MODULE$.org$apache$spark$internal$Logging$$initialized()) {
               initializeLogging($this, isInterpreter, silent);
               return true;
            }

            BoxedUnit var4 = BoxedUnit.UNIT;
         }
      }

      return false;
   }

   public static boolean initializeLogIfNecessary$default$2(Logging $this) {
      return false;
   }

   private static void initializeLogging(Logging $this, boolean isInterpreter, boolean silent) {
      if (Logging$.MODULE$.org$apache$spark$internal$Logging$$isLog4j12()) {
         boolean log4j12Initialized = LogManager.getRootLogger().getAllAppenders().hasMoreElements();
         if (!log4j12Initialized) {
            Logging$.MODULE$.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig_$eq(true);
            String defaultLogProps = "org/apache/spark/log4j-defaults.properties";
            Option var5 = Option$.MODULE$.apply(Utils$.MODULE$.getSparkClassLoader().getResource(defaultLogProps));
            if (var5 instanceof Some) {
               Some var6 = (Some)var5;
               URL url = (URL)var6.x();
               PropertyConfigurator.configure(url);
               if (silent) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  System.err.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using Spark's default log4j profile: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{defaultLogProps})));
                  BoxedUnit var13 = BoxedUnit.UNIT;
               }
            } else {
               if (!None$.MODULE$.equals(var5)) {
                  throw new MatchError(var5);
               }

               System.err.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark was unable to load ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{defaultLogProps})));
               BoxedUnit var8 = BoxedUnit.UNIT;
            }
         }

         org.apache.log4j.Logger rootLogger = LogManager.getRootLogger();
         if (Logging$.MODULE$.org$apache$spark$internal$Logging$$defaultRootLevel() == null) {
            Logging$.MODULE$.org$apache$spark$internal$Logging$$defaultRootLevel_$eq(rootLogger.getLevel());
         }

         if (isInterpreter) {
            label45: {
               org.apache.log4j.Logger replLogger = LogManager.getLogger($this.logName());
               Level replLevel = (Level)Option$.MODULE$.apply(replLogger.getLevel()).getOrElse(new Serializable($this) {
                  public static final long serialVersionUID = 0L;

                  public final Level apply() {
                     return Level.WARN;
                  }
               });
               Level var12 = rootLogger.getEffectiveLevel();
               if (replLevel == null) {
                  if (var12 == null) {
                     break label45;
                  }
               } else if (replLevel.equals(var12)) {
                  break label45;
               }

               if (!silent) {
                  System.err.printf("Setting default log level to \"%s\".\n", replLevel);
                  System.err.println("To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).");
               }

               rootLogger.setLevel(replLevel);
            }
         }
      }

      Logging$.MODULE$.org$apache$spark$internal$Logging$$initialized_$eq(true);
      $this.log();
   }

   public static void $init$(Logging $this) {
      $this.org$apache$spark$internal$Logging$$log__$eq((Logger)null);
   }
}
