package org.apache.openejb.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.openejb.cdi.logging.Log4jLoggerFactory;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;

public class Log4jLogStreamFactory implements LogStreamFactory {
   private static final String LOGGING_PROPERTIES_FILE = "logging.properties";
   private static final String STANDALONE_PROPERTIES_FILE = "log4j.standalone.";
   private static final String EMBEDDED_PROPERTIES_FILE = "log4j.embedded.logging.properties";

   public LogStream createLogStream(LogCategory logCategory) {
      return new Log4jLogStream(logCategory);
   }

   public Log4jLogStreamFactory() {
      try {
         boolean externalLogging = SystemInstance.get().getOptions().get("openejb.logger.external", false);
         if (!externalLogging) {
            this.configureInternal();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      JavaSecurityManagers.setSystemProperty("openwebbeans.logging.factory", "org.apache.openejb.cdi.logging.Log4jLoggerFactory");
   }

   private void configureInternal() throws IOException {
      JavaSecurityManagers.setSystemProperty("openjpa.Log", "log4j");
      JavaSecurityManagers.setSystemProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");
      JavaSecurityManagers.setSystemProperty("openwebbeans.logging.factory", Log4jLoggerFactory.class.getName());
      boolean embedded = SystemInstance.get().getOptions().get("openejb.logging.embedded", false);
      File confDir = SystemInstance.get().getConf((String)null);
      if (confDir == null) {
         confDir = SystemInstance.get().getBase().getDirectory("conf");
      }

      File loggingPropertiesFile = new File(confDir, "logging.properties");
      if (!embedded && confDir.exists() || embedded && loggingPropertiesFile.exists()) {
         if (!loggingPropertiesFile.exists()) {
            loggingPropertiesFile = new File(confDir, "log4j.standalone.logging.properties");
         }

         if (loggingPropertiesFile.exists()) {
            Properties properties = IO.readProperties(loggingPropertiesFile);
            applyOverrides(properties);
            this.preprocessProperties(properties);
            PropertyConfigurator.configure(properties);
         } else {
            File log4jProperties = new File(confDir, "log4j.properties");
            if (log4jProperties.exists()) {
               PropertyConfigurator.configure(log4jProperties.toURI().toURL());
            } else {
               File log4jXml = new File(confDir, "log4j.xml");
               if (log4jXml.exists()) {
                  DOMConfigurator.configure(log4jXml.toURI().toURL());
               } else {
                  File log4j2Xml = new File(confDir, "log4j2.xml");
                  if (!log4j2Xml.exists()) {
                     this.installLoggingPropertiesFile(loggingPropertiesFile);
                  }
               }
            }
         }
      } else {
         this.configureEmbedded();
      }

   }

   private static void applyOverrides(Properties properties) {
      Properties system = SystemInstance.get().getProperties();

      for(Map.Entry<Object, Object> entry : system.entrySet()) {
         String key = entry.getKey().toString();
         if (key.startsWith("log4j.") && !key.equals("log4j.configuration")) {
            properties.put(key, entry.getValue());
         }
      }

   }

   private void preprocessProperties(Properties properties) {
      FileUtils base = SystemInstance.get().getBase();
      File confDir = SystemInstance.get().getConf((String)null);
      File baseDir = base.getDirectory();
      File userDir = (new File("foo")).getParentFile();
      File[] paths = new File[]{confDir, baseDir, userDir};
      List<File> missing = new ArrayList();

      for(Map.Entry<Object, Object> entry : properties.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         if (key.endsWith(".File")) {
            boolean found = false;

            for(int i = 0; i < paths.length && !found; ++i) {
               File path = paths[i];
               File logfile = new File(path, value);
               if (logfile.getParentFile().exists()) {
                  properties.setProperty(key, logfile.getAbsolutePath());
                  found = true;
               }
            }

            if (!found) {
               File logfile = new File(paths[0], value);
               missing.add(logfile);
            }
         }
      }

      if (missing.size() > 0) {
         org.apache.log4j.Logger logger = this.getFallabckLogger();
         logger.error("Logging may not operate as expected.  The directories for the following files do not exist so no file can be created.  See the list below.");

         for(int i = 0; i < missing.size(); ++i) {
            File file = (File)missing.get(i);
            logger.error("[" + i + "] " + file.getAbsolutePath());
         }
      }

   }

   private org.apache.log4j.Logger getFallabckLogger() {
      org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("OpenEJB.logging");
      return logger;
   }

   private void configureEmbedded() {
      URL resource = ConfUtils.getResource("log4j.embedded.logging.properties");
      if (resource == null) {
         System.err.println("FATAL ERROR WHILE CONFIGURING LOGGING!!!. MISSING embedded.logging.properties FILE ");
      } else {
         Properties properties = asProperies(resource);
         applyOverrides(properties);
         PropertyConfigurator.configure(properties);
         java.util.logging.Logger logger = java.util.logging.Logger.getLogger("org.apache");
         java.util.logging.Logger parent = logger.getParent();
         parent.setLevel(Level.WARNING);
      }

   }

   private static Properties asProperies(URL resource) {
      Properties properties = new Properties();

      try {
         IO.readProperties(resource, properties);
      } catch (Throwable var3) {
      }

      return properties;
   }

   private void installLoggingPropertiesFile(File loggingPropertiesFile) throws IOException {
      String name = "log4j.standalone.logging.properties";
      URL resource = Thread.currentThread().getContextClassLoader().getResource("log4j.standalone.logging.properties");
      if (resource == null) {
         System.err.println("FATAL ERROR WHILE CONFIGURING LOGGING!!!. MISSING RESOURCE log4j.standalone.logging.properties");
      } else {
         Properties props = IO.readProperties(resource);
         this.preprocessProperties(props);
         OutputStream out = IO.write(loggingPropertiesFile);

         try {
            props.store(out, "OpenEJB Default Log4j Configuration");
         } finally {
            IO.close(out);
         }

         PropertyConfigurator.configure(props);
      }
   }
}
