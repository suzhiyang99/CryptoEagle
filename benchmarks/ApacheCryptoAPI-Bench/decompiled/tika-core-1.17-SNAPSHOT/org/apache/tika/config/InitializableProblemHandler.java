package org.apache.tika.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaConfigException;

public interface InitializableProblemHandler {
   InitializableProblemHandler IGNORE = new InitializableProblemHandler() {
      public void handleInitializableProblem(String className, String message) {
      }

      public String toString() {
         return "IGNORE";
      }
   };
   InitializableProblemHandler INFO = new InitializableProblemHandler() {
      public void handleInitializableProblem(String classname, String message) {
         Logger.getLogger(classname).log(Level.INFO, message);
      }

      public String toString() {
         return "INFO";
      }
   };
   InitializableProblemHandler WARN = new InitializableProblemHandler() {
      public void handleInitializableProblem(String classname, String message) {
         Logger.getLogger(classname).log(Level.WARNING, message);
      }

      public String toString() {
         return "WARN";
      }
   };
   InitializableProblemHandler THROW = new InitializableProblemHandler() {
      public void handleInitializableProblem(String classname, String message) throws TikaConfigException {
         throw new TikaConfigException(message);
      }

      public String toString() {
         return "THROW";
      }
   };
   InitializableProblemHandler DEFAULT = WARN;

   void handleInitializableProblem(String var1, String var2) throws TikaConfigException;
}
