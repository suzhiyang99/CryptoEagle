package org.apache.openejb.log;

import java.util.logging.Level;
import org.apache.openejb.loader.SystemInstance;

public final class JULUtil {
   public static final String DEFAULT_LOG_LEVEL = "INFO";
   public static final String OPENEJB_LOG_LEVEL = "openejb.log.level";

   private JULUtil() {
   }

   public static Level level() {
      String propLevel = SystemInstance.get().getProperty("openejb.log.level", "INFO").toUpperCase();

      try {
         return (Level)Level.class.getDeclaredField(propLevel).get((Object)null);
      } catch (IllegalAccessException var2) {
         return Level.INFO;
      } catch (NoSuchFieldException var3) {
         return Level.INFO;
      }
   }
}
