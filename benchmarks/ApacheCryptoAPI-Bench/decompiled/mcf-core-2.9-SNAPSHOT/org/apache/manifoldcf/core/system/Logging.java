package org.apache.manifoldcf.core.system;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class Logging {
   public static final String _rcsid = "@(#)$Id: Logging.java 988245 2010-08-23 18:39:35Z kwright $";
   public static Logger root = null;
   public static Logger misc = null;
   public static Logger db = null;
   public static Logger lock = null;
   public static Logger cache = null;
   public static Logger keystore = null;
   public static Logger perf = null;
   public static Logger diagnostics = null;
   private static HashMap loggerTable = null;
   private static HashMap logLevelMap = null;

   public static synchronized void initializeLoggingSystem(File logConfigFile) {
      if (logLevelMap == null) {
         logLevelMap = new HashMap();
         logLevelMap.put("OFF", Level.OFF);
         logLevelMap.put("FATAL", Level.FATAL);
         logLevelMap.put("WARN", Level.WARN);
         logLevelMap.put("ERROR", Level.ERROR);
         logLevelMap.put("INFO", Level.INFO);
         logLevelMap.put("DEBUG", Level.DEBUG);
         logLevelMap.put("ALL", Level.ALL);
         loggerTable = new HashMap();
         System.setProperty("log4j.configurationFile", logConfigFile.toString());
      }
   }

   public static synchronized void initializeLoggers() {
      if (misc == null) {
         root = newLogger("org.apache.manifoldcf.root");
         misc = newLogger("org.apache.manifoldcf.misc");
         db = newLogger("org.apache.manifoldcf.db");
         lock = newLogger("org.apache.manifoldcf.lock");
         cache = newLogger("org.apache.manifoldcf.cache");
         keystore = newLogger("org.apache.manifoldcf.keystore");
         perf = newLogger("org.apache.manifoldcf.perf");
         diagnostics = newLogger("org.apache.manifoldcf.diagnostics");
      }
   }

   public static void setLogLevels(IThreadContext threadContext) throws ManifoldCFException {
      for(Map.Entry e : loggerTable.entrySet()) {
         Logger logger = (Logger)e.getValue();
         String loggername = (String)e.getKey();
         String level = LockManagerFactory.getProperty(threadContext, loggername);
         Level loglevel = null;
         if (level != null && level.length() > 0) {
            loglevel = (Level)logLevelMap.get(level);
         }

         if (loglevel == null) {
            loglevel = Level.WARN;
         }

         try {
            logger.setLevel(loglevel);
         } catch (Exception ex) {
            System.err.println("Unable to set log level " + level + " on logger " + loggername);
            ex.printStackTrace();
         }
      }

   }

   public static final Logger getLogger(String loggerName) {
      return (Logger)loggerTable.get(loggerName);
   }

   public static final Logger newLogger(String s) {
      Logger l = Logger.getLogger(s);
      loggerTable.put(s, l);
      return l;
   }
}
