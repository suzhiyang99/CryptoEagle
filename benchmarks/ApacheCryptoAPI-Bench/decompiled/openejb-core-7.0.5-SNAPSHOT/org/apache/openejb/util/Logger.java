package org.apache.openejb.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;

public class Logger {
   private static final String SUFFIX = ".Messages";
   private static final String OPENEJB = "org.apache.tomee";
   private static LogStreamFactory logStreamFactory;
   private static final Computable<String, String> heirarchyResolver = new Computable<String, String>() {
      public String compute(String key) throws InterruptedException {
         int index = key.lastIndexOf(".");
         String parent = key.substring(0, index);
         return parent.contains("org.apache.tomee") ? parent : null;
      }
   };
   private static final Computable<String, ResourceBundle> bundleResolver = new Computable<String, ResourceBundle>() {
      public ResourceBundle compute(String baseName) throws InterruptedException {
         try {
            return ResourceBundle.getBundle(baseName + ".Messages");
         } catch (MissingResourceException var3) {
            return null;
         }
      }
   };
   private static final Computable<LoggerKey, Logger> loggerResolver = new Computable<LoggerKey, Logger>() {
      public Logger compute(LoggerKey args) throws InterruptedException {
         return new Logger(args.category, Logger.logStreamFactory.createLogStream(args.category), args.baseName);
      }
   };
   private static final Computable<String, MessageFormat> messageFormatResolver = new Computable<String, MessageFormat>() {
      public MessageFormat compute(String message) throws InterruptedException {
         return new MessageFormat(message);
      }
   };
   private static final Computable<String, String> heirarchyCache;
   private static final Computable<String, ResourceBundle> bundleCache;
   private static final Computable<LoggerKey, Logger> loggerCache;
   private static final Computable<String, MessageFormat> messageFormatCache;
   private final LogCategory category;
   private final LogStream logStream;
   private final String baseName;
   private static Boolean isLog4j;

   public static String delegateClass() {
      if (logStreamFactory == null) {
         throw new IllegalStateException("Call this method after having configured the logger");
      } else {
         return logStreamFactory.getClass().getName();
      }
   }

   public static LogStreamFactory unsafeDelegateClass() {
      return logStreamFactory;
   }

   public static synchronized void configure() {
      configure(SystemInstance.isInitialized() ? SystemInstance.get().getProperties() : JavaSecurityManagers.getSystemProperties());
   }

   public static synchronized void configure(Properties config) {
      if (logStreamFactory == null) {
         String julFqn = JuliLogStreamFactory.class.getName();
         String factoryName = config.getProperty("openejb.log.factory", SystemInstance.isInitialized() ? SystemInstance.get().getOptions().get("openejb.log.factory", julFqn) : julFqn);
         if (!"jul".equalsIgnoreCase(factoryName) && !"juli".equalsIgnoreCase(factoryName)) {
            if ("slf4j".equalsIgnoreCase(factoryName)) {
               factoryName = Slf4jLogStreamFactory.class.getName();
            } else if ("log4j".equalsIgnoreCase(factoryName)) {
               if (exists("org.apache.log4j.Logger")) {
                  factoryName = "org.apache.openejb.util.Log4jLogStreamFactory";
               } else {
                  System.out.println("Cannot respect 'openejb.log.factory=log4j' setting as Log4j is not in the classpath.");
               }
            } else if ("pax".equalsIgnoreCase(factoryName)) {
               factoryName = "org.apache.openejb.util.PaxLogStreamFactory";
            } else if ("log4j2".equalsIgnoreCase(factoryName)) {
               factoryName = "org.apache.openejb.util.Log4j2LogStreamFactory";
            }
         } else {
            factoryName = JuliLogStreamFactory.class.getName();
         }

         String[] specialKeys = new String[]{"openejb.jul.forceReload", "openejb.jul.consoleHandlerClazz", "openejb.logger.external"};
         String[] originals = new String[specialKeys.length];

         for(int i = 0; i < specialKeys.length; ++i) {
            originals[i] = JavaSecurityManagers.getSystemProperty(specialKeys[i]);
            String property = config.getProperty(specialKeys[i], SystemInstance.isInitialized() ? SystemInstance.get().getOptions().get(specialKeys[i], (String)null) : null);
            if (property != null) {
               JavaSecurityManagers.setSystemProperty(specialKeys[i], property);
            }
         }

         boolean var10 = false;

         try {
            var10 = true;
            if (factoryName != null) {
               logStreamFactory = createFactory(factoryName);
            }

            if (isLog4jImplied()) {
               logStreamFactory = createFactory("org.apache.openejb.util.Log4jLogStreamFactory");
            }

            if (logStreamFactory == null) {
               logStreamFactory = new JuliLogStreamFactory();
            }

            checkForIgnoredLog4jConfig();
            var10 = false;
         } finally {
            if (var10) {
               int i = 0;

               while(true) {
                  if (i >= specialKeys.length) {
                     ;
                  } else {
                     if (originals[i] == null) {
                        JavaSecurityManagers.removeSystemProperty(specialKeys[i]);
                     } else {
                        JavaSecurityManagers.setSystemProperty(specialKeys[i], originals[i]);
                     }

                     ++i;
                  }
               }
            }
         }

         for(int i = 0; i < specialKeys.length; ++i) {
            if (originals[i] == null) {
               JavaSecurityManagers.removeSystemProperty(specialKeys[i]);
            } else {
               JavaSecurityManagers.setSystemProperty(specialKeys[i], originals[i]);
            }
         }

      }
   }

   private static void checkForIgnoredLog4jConfig() {
      if (!logStreamFactory.getClass().getName().equals("org.apache.openejb.util.Log4jLogStreamFactory")) {
         try {
            Properties configFile = log4j(loadLoggingProperties());
            Properties systemProperties = log4j(SystemInstance.get().getProperties());
            if (configFile.size() == 0 && systemProperties.size() == 0) {
               return;
            }

            if (systemProperties.size() == 1 && "log4j.configurationFile".equals(systemProperties.stringPropertyNames().iterator().next())) {
               return;
            }

            LogStream stream = logStreamFactory.createLogStream(LogCategory.OPENEJB);
            stream.warn("Log4j not installed. The following properties will be ignored.");
            String format = "Ignored %s property '%s'";

            for(Object key : configFile.keySet()) {
               stream.warn(String.format("Ignored %s property '%s'", "conf/logging.properties", key));
            }

            for(Object key : systemProperties.keySet()) {
               stream.warn(String.format("Ignored %s property '%s'", "Property overrides", key));
            }
         } catch (Throwable var6) {
         }

      }
   }

   private static LogStreamFactory createFactory(String factoryName) {
      Class<?> factoryClass = load(factoryName);
      if (factoryClass == null) {
         return null;
      } else {
         try {
            return (LogStreamFactory)factoryClass.newInstance();
         } catch (Throwable var3) {
            return null;
         }
      }
   }

   private static Class<?> load(String factoryName) {
      try {
         ClassLoader classLoader = Logger.class.getClassLoader();
         return classLoader.loadClass(factoryName);
      } catch (Throwable var4) {
         try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            return contextClassLoader.loadClass(factoryName);
         } catch (Throwable var3) {
            try {
               return Class.forName(factoryName);
            } catch (Throwable var2) {
               return null;
            }
         }
      }
   }

   public static Logger getInstance(LogCategory category, String baseName) {
      configure();

      try {
         return loggerCache.compute(new LoggerKey(category, baseName));
      } catch (InterruptedException var4) {
         LogStream logStream = logStreamFactory.createLogStream(category);
         return new Logger(category, logStream, baseName);
      }
   }

   public Logger(LogCategory category, LogStream logStream, String baseName) {
      this.category = category;
      this.baseName = baseName;
      this.logStream = (LogStream)("true".equals(SystemInstance.get().getProperty("openejb.log.async", "true")) && JavaSecurityManagers.getSystemProperty("catalina.home") == null ? new LogStreamAsync(logStream) : logStream);
   }

   public static Logger getInstance(LogCategory category, Class clazz) {
      return getInstance(category, packageName(clazz));
   }

   private static String packageName(Class clazz) {
      String name = clazz.getName();
      return name.substring(0, name.lastIndexOf("."));
   }

   public static boolean isLog4jImplied() {
      if (null == isLog4j) {
         isLog4j = false;
         List<String> locations = new ArrayList();
         Properties configFile = log4j(loadLoggingProperties());
         Properties systemProperties = log4j(SystemInstance.get().getProperties());
         if (configFile.size() > 0) {
            locations.add("conf/logging.properties");
         }

         if (systemProperties.size() > 0) {
            locations.add("Properties overrides");
         }

         if (locations.size() > 0 && exists("org.apache.log4j.Logger")) {
            isLog4j = true;
         }
      }

      return isLog4j;
   }

   private static boolean exists(String s) {
      return load(s) != null;
   }

   private static Properties log4j(Properties system) {
      Properties properties = new Properties();

      for(Map.Entry<Object, Object> entry : system.entrySet()) {
         String key = entry.getKey().toString();
         if (key.startsWith("log4j.") && !key.equals("log4j.configuration")) {
            properties.put(key, entry.getValue());
         }
      }

      return properties;
   }

   private static Properties loadLoggingProperties() {
      try {
         File conf = SystemInstance.get().getConf((String)null);
         File file = new File(conf, "logging.properties");
         return IO.readProperties(file);
      } catch (IOException var2) {
         return new Properties();
      }
   }

   public Logger getChildLogger(String child) {
      return getInstance(this.category.createChild(child), this.baseName);
   }

   private String formatMessage(String message, Object... args) {
      if (args.length == 0) {
         return message;
      } else {
         try {
            MessageFormat mf = messageFormatCache.compute(message);
            return mf.format(args);
         } catch (Exception var4) {
            return "Error in formatting message " + message;
         }
      }
   }

   public boolean isDebugEnabled() {
      return this.logStream.isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.logStream.isErrorEnabled();
   }

   public boolean isFatalEnabled() {
      return this.logStream.isFatalEnabled();
   }

   public boolean isInfoEnabled() {
      return this.logStream.isInfoEnabled();
   }

   public boolean isWarningEnabled() {
      return this.logStream.isWarnEnabled();
   }

   public boolean isLevelEnable(String level) {
      String levelLowerCase = level.toLowerCase(Locale.ENGLISH);
      if ("info".equals(levelLowerCase)) {
         return this.isInfoEnabled();
      } else if ("debug".equals(levelLowerCase)) {
         return this.isDebugEnabled();
      } else if ("warning".equals(levelLowerCase)) {
         return this.isWarningEnabled();
      } else if ("fatal".equals(levelLowerCase)) {
         return this.isFatalEnabled();
      } else {
         return "error".equals(levelLowerCase) ? this.isErrorEnabled() : false;
      }
   }

   public void log(String level, String message) {
      String levelLowerCase = level.toLowerCase(Locale.ENGLISH);
      if ("info".equals(levelLowerCase)) {
         this.info(message);
      } else if ("debug".equals(levelLowerCase)) {
         this.debug(message);
      } else if ("warning".equals(levelLowerCase)) {
         this.warning(message);
      } else if ("fatal".equals(levelLowerCase)) {
         this.fatal(message);
      } else if ("error".equals(levelLowerCase)) {
         this.error(message);
      }

   }

   public String debug(String message) {
      if (this.isDebugEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.debug(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String debug(String message, Object... args) {
      if (this.isDebugEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.debug(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String debug(String message, Throwable t) {
      if (this.isDebugEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.debug(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String debug(String message, Throwable t, Object... args) {
      if (this.isDebugEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.debug(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String error(String message) {
      if (this.isErrorEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.error(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String error(String message, Object... args) {
      if (this.isErrorEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.error(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String error(String message, Throwable t) {
      if (this.isErrorEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.error(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String error(String message, Throwable t, Object... args) {
      if (this.isErrorEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.error(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String fatal(String message) {
      if (this.isFatalEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.fatal(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String fatal(String message, Object... args) {
      if (this.isFatalEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.fatal(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String fatal(String message, Throwable t) {
      if (this.isFatalEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.fatal(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String fatal(String message, Throwable t, Object... args) {
      if (this.isFatalEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.fatal(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String info(String message) {
      if (this.isInfoEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.info(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String info(String message, Object... args) {
      if (this.isInfoEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.info(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String info(String message, Throwable t) {
      if (this.isInfoEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.info(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String info(String message, Throwable t, Object... args) {
      if (this.isInfoEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.info(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String warning(String message) {
      if (this.isWarningEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.warn(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String warning(String message, Object... args) {
      if (this.isWarningEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.warn(msg);
         return msg;
      } else {
         return message;
      }
   }

   public String warning(String message, Throwable t) {
      if (this.isWarningEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         this.logStream.warn(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   public String warning(String message, Throwable t, Object... args) {
      if (this.isWarningEnabled()) {
         String msg = this.getMessage(message, this.baseName);
         msg = this.formatMessage(msg, args);
         this.logStream.warn(msg, t);
         return msg;
      } else {
         return message;
      }
   }

   private String getMessage(String key, String baseName) {
      if (key == null) {
         return "null";
      } else {
         try {
            ResourceBundle bundle = bundleCache.compute(baseName);
            if (bundle != null) {
               try {
                  return bundle.getString(key);
               } catch (MissingResourceException var6) {
                  String parentName = heirarchyCache.compute(baseName);
                  return parentName == null ? key : this.getMessage(key, parentName);
               }
            } else {
               String parentName = heirarchyCache.compute(baseName);
               return parentName == null ? key : this.getMessage(key, parentName);
            }
         } catch (InterruptedException var7) {
            return key;
         }
      }
   }

   static {
      heirarchyCache = new Memoizer<String, String>(heirarchyResolver);
      bundleCache = new Memoizer<String, ResourceBundle>(bundleResolver);
      loggerCache = new Memoizer<LoggerKey, Logger>(loggerResolver);
      messageFormatCache = new Memoizer<String, MessageFormat>(messageFormatResolver);
   }

   protected static class LoggerKey implements Serializable {
      protected final LogCategory category;
      protected final String baseName;
      private final int hash;

      protected LoggerKey(LogCategory category, String baseName) {
         this.category = category;
         this.baseName = baseName;
         int result = category.hashCode();
         this.hash = 31 * result + baseName.hashCode();
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LoggerKey loggerKey = (LoggerKey)LoggerKey.class.cast(o);
            return this.category.equals(loggerKey.category) && this.baseName.equals(loggerKey.baseName);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }
}
