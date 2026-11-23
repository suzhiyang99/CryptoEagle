package org.apache.openejb.log;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.loader.SystemInstance;

public class LoggerCreator implements Callable<Logger> {
   private final String name;
   private Logger logger;
   private volatile boolean init;

   public LoggerCreator(String channel) {
      this.name = channel;
      Properties p = SystemInstance.get().getProperties();
      String levelName = p.getProperty("logging.level." + channel);
      if (levelName != null) {
         try {
            this.call();
         } catch (Exception var5) {
         }
      }

   }

   public Logger call() throws Exception {
      if (this.logger == null) {
         synchronized(this) {
            if (this.logger == null) {
               Thread thread = Thread.currentThread();
               ClassLoader originalLoader = thread.getContextClassLoader();
               thread.setContextClassLoader(ParentClassLoaderFinder.Helper.get());

               try {
                  try {
                     this.logger = Logger.getLogger(this.name);
                  } catch (Exception var16) {
                     this.logger = Logger.getLogger(this.name);
                  }

                  Properties p = SystemInstance.get().getProperties();
                  String levelName = p.getProperty("logging.level." + this.logger.getName());
                  if (levelName != null) {
                     Level level = Level.parse(levelName);

                     for(Handler handler : this.logger.getHandlers()) {
                        handler.setLevel(level);
                     }
                  }
               } finally {
                  thread.setContextClassLoader(originalLoader);
               }
            }
         }
      }

      return this.logger;
   }

   public boolean isInit() {
      return this.init;
   }

   public static final class Get {
      private Get() {
      }

      private static Logger exec(LoggerCreator creator) {
         ClassLoader old = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(ParentClassLoaderFinder.Helper.get());

         Logger var3;
         try {
            Logger var2 = creator.call();
            return var2;
         } catch (Exception var7) {
            var3 = Logger.getLogger("default");
         } finally {
            Thread.currentThread().setContextClassLoader(old);
         }

         return var3;
      }

      public static Logger exec(LoggerCreator logger, AtomicBoolean debug, AtomicBoolean info) {
         Logger l = exec(logger);
         if (!logger.init) {
            levels(logger, debug, info);
         }

         return l;
      }

      public static void levels(LoggerCreator lc, AtomicBoolean debug, AtomicBoolean info) {
         if (!lc.init) {
            Logger l;
            try {
               l = lc.call();
            } catch (Exception var5) {
               return;
            }

            debug.set(l.isLoggable(Level.FINE));
            info.set(l.isLoggable(Level.INFO));
            lc.init = true;
         }
      }
   }
}
