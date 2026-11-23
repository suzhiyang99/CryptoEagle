package org.spark_project.jetty.server;

import javax.servlet.ServletException;

public class QuietServletException extends ServletException {
   public QuietServletException() {
   }

   public QuietServletException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public QuietServletException(String message) {
      super(message);
   }

   public QuietServletException(Throwable rootCause) {
      super(rootCause);
   }
}
