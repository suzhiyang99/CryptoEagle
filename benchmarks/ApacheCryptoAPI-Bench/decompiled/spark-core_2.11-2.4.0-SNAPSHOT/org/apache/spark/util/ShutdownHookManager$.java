package org.apache.spark.util;

import java.io.File;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class ShutdownHookManager$ implements Logging {
   public static final ShutdownHookManager$ MODULE$;
   private final int DEFAULT_SHUTDOWN_PRIORITY;
   private final int SPARK_CONTEXT_SHUTDOWN_PRIORITY;
   private final int TEMP_DIR_SHUTDOWN_PRIORITY;
   private SparkShutdownHookManager shutdownHooks;
   private final HashSet<String> org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   static {
      new ShutdownHookManager$();
   }

   private SparkShutdownHookManager shutdownHooks$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            SparkShutdownHookManager manager = new SparkShutdownHookManager();
            manager.install();
            this.shutdownHooks = manager;
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.shutdownHooks;
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

   public int DEFAULT_SHUTDOWN_PRIORITY() {
      return this.DEFAULT_SHUTDOWN_PRIORITY;
   }

   public int SPARK_CONTEXT_SHUTDOWN_PRIORITY() {
      return this.SPARK_CONTEXT_SHUTDOWN_PRIORITY;
   }

   public int TEMP_DIR_SHUTDOWN_PRIORITY() {
      return this.TEMP_DIR_SHUTDOWN_PRIORITY;
   }

   private SparkShutdownHookManager shutdownHooks() {
      return this.bitmap$0 ? this.shutdownHooks : this.shutdownHooks$lzycompute();
   }

   public HashSet<String> org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths() {
      return this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths;
   }

   public void registerShutdownDeleteDir(File file) {
      String absolutePath = file.getAbsolutePath();
      synchronized(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths()) {
         this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths().$plus$eq(absolutePath);
      }
   }

   public void removeShutdownDeleteDir(File file) {
      String absolutePath = file.getAbsolutePath();
      synchronized(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths()) {
         BoxesRunTime.boxToBoolean(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths().remove(absolutePath));
      }
   }

   public boolean hasShutdownDeleteDir(File file) {
      String absolutePath = file.getAbsolutePath();
      Boolean var4;
      synchronized(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths()) {
         var4 = BoxesRunTime.boxToBoolean(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths().contains(absolutePath));
      }

      return BoxesRunTime.unboxToBoolean(var4);
   }

   public boolean hasRootAsShutdownDeleteDir(File file) {
      String absolutePath = file.getAbsolutePath();
      Boolean var5;
      synchronized(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths()) {
         var5 = BoxesRunTime.boxToBoolean(this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths().exists(new Serializable(absolutePath) {
            public static final long serialVersionUID = 0L;
            private final String absolutePath$1;

            public final boolean apply(String path) {
               return !this.absolutePath$1.equals(path) && this.absolutePath$1.startsWith(path);
            }

            public {
               this.absolutePath$1 = absolutePath$1;
            }
         }));
      }

      boolean retval = BoxesRunTime.unboxToBoolean(var5);
      if (retval) {
         this.logInfo(new Serializable(file) {
            public static final long serialVersionUID = 0L;
            private final File file$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"path = ").append((Object)this.file$1).append((Object)", already present as root for deletion.").toString();
            }

            public {
               this.file$1 = file$1;
            }
         });
      }

      return retval;
   }

   public boolean inShutdown() {
      try {
         Thread hook = new Thread() {
            public void run() {
            }
         };
         Runtime.getRuntime().addShutdownHook(hook);
         Runtime.getRuntime().removeShutdownHook(hook);
      } catch (IllegalStateException var3) {
         return true;
      }

      return false;
   }

   public Object addShutdownHook(Function0<BoxedUnit> hook) {
      return this.addShutdownHook(this.DEFAULT_SHUTDOWN_PRIORITY(), hook);
   }

   public Object addShutdownHook(int priority, Function0<BoxedUnit> hook) {
      return this.shutdownHooks().add(priority, hook);
   }

   public boolean removeShutdownHook(Object ref) {
      return this.shutdownHooks().remove(ref);
   }

   private ShutdownHookManager$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.DEFAULT_SHUTDOWN_PRIORITY = 100;
      this.SPARK_CONTEXT_SHUTDOWN_PRIORITY = 50;
      this.TEMP_DIR_SHUTDOWN_PRIORITY = 25;
      this.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths = new HashSet<String>();
      this.logDebug(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Adding shutdown hook";
         }
      });
      this.addShutdownHook(this.TEMP_DIR_SHUTDOWN_PRIORITY(), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            ShutdownHookManager$.MODULE$.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Shutdown hook called";
               }
            });
            Predef$.MODULE$.refArrayOps(ShutdownHookManager$.MODULE$.org$apache$spark$util$ShutdownHookManager$$shutdownDeletePaths().toArray(ClassTag$.MODULE$.apply(String.class))).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(String dirPath) {
                  try {
                     ShutdownHookManager$.MODULE$.logInfo(new Serializable(this, dirPath) {
                        public static final long serialVersionUID = 0L;
                        private final String dirPath$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Deleting directory ").append((Object)this.dirPath$1).toString();
                        }

                        public {
                           this.dirPath$1 = dirPath$1;
                        }
                     });
                     Utils$.MODULE$.deleteRecursively(new File(dirPath));
                  } catch (Exception var3) {
                     ShutdownHookManager$.MODULE$.logError(new Serializable(this, dirPath) {
                        public static final long serialVersionUID = 0L;
                        private final String dirPath$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception while deleting Spark temp dir: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.dirPath$1}));
                        }

                        public {
                           this.dirPath$1 = dirPath$1;
                        }
                     }, var3);
                  }

               }
            });
         }
      });
   }
}
