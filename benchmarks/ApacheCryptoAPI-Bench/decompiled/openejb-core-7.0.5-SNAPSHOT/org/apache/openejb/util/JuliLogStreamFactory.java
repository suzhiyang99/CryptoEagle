package org.apache.openejb.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.log.ConsoleColorHandler;
import org.apache.openejb.log.SingleLineFormatter;
import org.apache.openejb.util.reflection.Reflections;
import org.apache.webbeans.logger.WebBeansLoggerFacade;

public class JuliLogStreamFactory implements LogStreamFactory {
   public static final String OPENEJB_LOG_COLOR_PROP = "openejb.log.color";
   private static String consoleHandlerClazz;
   private static boolean useOpenEJBHandler;

   public LogStream createLogStream(LogCategory logCategory) {
      return new JuliLogStream(logCategory);
   }

   private static void setRootLogger(OpenEJBLogManager value) {
      try {
         Class<?> rootLoggerClass = ClassLoader.getSystemClassLoader().loadClass("java.util.logging.LogManager$RootLogger");
         Constructor<?> cons = rootLoggerClass.getDeclaredConstructor(LogManager.class);
         boolean acc = cons.isAccessible();
         if (!acc) {
            cons.setAccessible(true);
         }

         java.util.logging.Logger rootLogger = (java.util.logging.Logger)java.util.logging.Logger.class.cast(cons.newInstance(value));

         try {
            Reflections.set(value, "rootLogger", rootLogger);
         } finally {
            cons.setAccessible(acc);
         }

         value.addLogger(rootLogger);
         Reflections.invokeByReflection(Reflections.get(value, "systemContext"), "addLocalLogger", new Class[]{java.util.logging.Logger.class}, new Object[]{rootLogger});
         Reflections.invokeByReflection(java.util.logging.Logger.getGlobal(), "setLogManager", new Class[]{LogManager.class}, new Object[]{value});
         value.addLogger(java.util.logging.Logger.getGlobal());
      } catch (Throwable var9) {
      }

   }

   public static boolean isNotIDE() {
      return !JavaSecurityManagers.getSystemProperty("java.class.path").contains("idea_rt");
   }

   private static String reverseProperty(String name) {
      if (name.contains(".") && !name.endsWith(".")) {
         int idx = name.lastIndexOf(46);
         return name.substring(idx) + "." + name.substring(0, idx);
      } else {
         return name;
      }
   }

   private static boolean isOverridableLogger(String name) {
      return useOpenEJBHandler || name.toLowerCase().contains("openejb") || name.toLowerCase().contains("transaction") || name.toLowerCase().contains("cxf") || name.toLowerCase().contains("timer") || name.startsWith("org.apache.") && !name.startsWith("org.apache.geronimo.connector.work.WorkerContext.") || name.startsWith("openjpa.") || name.startsWith("net.sf.ehcache.") || name.startsWith("org.quartz.") || name.startsWith("org.hibernate.");
   }

   private static boolean is(String classname) {
      try {
         JuliLogStreamFactory.class.getClassLoader().loadClass(classname);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   static {
      boolean tomee = is("org.apache.tomee.catalina.TomcatLoader");
      boolean embedded = is("org.apache.tomee.embedded.Container");
      Options options = SystemInstance.isInitialized() ? SystemInstance.get().getOptions() : new Options(JavaSecurityManagers.getSystemProperties());
      boolean forceLogs = options.get("openejb.jul.forceReload", false);
      if ((!tomee || embedded || forceLogs) && JavaSecurityManagers.getSystemProperty("java.util.logging.manager") == null) {
         consoleHandlerClazz = options.get("openejb.jul.consoleHandlerClazz", (String)null);
         if (consoleHandlerClazz == null) {
            if (options.get("openejb.log.color", false) && isNotIDE()) {
               consoleHandlerClazz = ConsoleColorHandler.class.getName();
            } else {
               consoleHandlerClazz = OpenEJBSimpleLayoutHandler.class.getName();
            }
         }

         try {
            ClassLoader.getSystemClassLoader().loadClass(consoleHandlerClazz);
         } catch (ClassNotFoundException var8) {
            consoleHandlerClazz = ConsoleHandler.class.getName();
         }

         if (forceLogs) {
            useOpenEJBHandler = options.get("openejb.jul.forceReload.use-openejb-handler", true);

            try {
               OpenEJBLogManager value = new OpenEJBLogManager();
               Reflections.set(LogManager.class, (Object)null, "manager", value);
               value.forceReset();
               setRootLogger(value);
               value.readConfiguration();
            } catch (Exception var7) {
            }
         }

         JavaSecurityManagers.setSystemProperty("java.util.logging.manager", OpenEJBLogManager.class.getName());
      }

      try {
         if (options.get("openjpa.Log", (String)null) == null) {
            JuliLogStreamFactory.class.getClassLoader().loadClass("org.apache.openjpa.lib.log.LogFactoryAdapter");
            JavaSecurityManagers.setSystemProperty("openjpa.Log", "org.apache.openejb.openjpa.JULOpenJPALogFactory");
         }
      } catch (Exception var6) {
      }

      try {
         JavaSecurityManagers.setSystemProperty(WebBeansLoggerFacade.class.getName(), "org.apache.openejb.cdi.logging.ContainerJULLoggerFactory");
      } catch (Throwable var5) {
      }

   }

   public static class OpenEJBLogManager extends LogManager {
      public void forceReset() {
         super.reset();
      }

      public void reset() throws SecurityException {
      }

      public String getProperty(String name) {
         String parentValue = super.getProperty(name);
         if (SystemInstance.isInitialized()) {
            if (SystemInstance.get().getProperties().containsKey(name)) {
               return SystemInstance.get().getProperty(name);
            }

            String propertyKeyValue = "logging" + JuliLogStreamFactory.reverseProperty(name);
            if (SystemInstance.get().getProperties().containsKey(propertyKeyValue)) {
               return SystemInstance.get().getProperty(propertyKeyValue);
            }
         }

         if (OpenEJBLogManager.class.getName().equals(JavaSecurityManagers.getSystemProperty("java.util.logging.manager")) && JuliLogStreamFactory.isOverridableLogger(name) && parentValue == null) {
            if (name.endsWith(".handlers")) {
               return JuliLogStreamFactory.consoleHandlerClazz;
            }

            if (name.endsWith(".useParentHandlers")) {
               return "false";
            }
         }

         return parentValue;
      }

      static {
         final LogManager mgr = LogManager.getLogManager();
         if (mgr instanceof OpenEJBLogManager) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
               public void run() {
                  ((OpenEJBLogManager)mgr).forceReset();
               }
            });
         }

      }
   }

   public static class OpenEJBSimpleLayoutHandler extends ConsoleHandler {
      public OpenEJBSimpleLayoutHandler() {
         this.setFormatter(new SingleLineFormatter());
      }

      protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
         super.setOutputStream(new FilterOutputStream(System.out) {
            public void write(int b) throws IOException {
               System.out.write(b);
            }

            public void write(byte[] b) throws IOException {
               System.out.write(b);
            }

            public void write(byte[] b, int off, int len) throws IOException {
               System.out.write(b, off, len);
            }

            public void flush() throws IOException {
               System.out.flush();
            }

            public void close() throws IOException {
               this.flush();
            }
         });
      }
   }
}
