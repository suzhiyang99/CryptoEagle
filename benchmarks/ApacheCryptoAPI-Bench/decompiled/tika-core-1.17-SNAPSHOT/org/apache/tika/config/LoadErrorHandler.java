package org.apache.tika.config;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface LoadErrorHandler {
   LoadErrorHandler IGNORE = new LoadErrorHandler() {
      public void handleLoadError(String classname, Throwable throwable) {
      }

      public String toString() {
         return "IGNORE";
      }
   };
   LoadErrorHandler WARN = new LoadErrorHandler() {
      public void handleLoadError(String classname, Throwable throwable) {
         Logger.getLogger(classname).log(Level.WARNING, "Unable to load " + classname, throwable);
      }

      public String toString() {
         return "WARN";
      }
   };
   LoadErrorHandler THROW = new LoadErrorHandler() {
      public void handleLoadError(String classname, Throwable throwable) {
         throw new RuntimeException("Unable to load " + classname, throwable);
      }

      public String toString() {
         return "THROW";
      }
   };

   void handleLoadError(String var1, Throwable var2);
}
