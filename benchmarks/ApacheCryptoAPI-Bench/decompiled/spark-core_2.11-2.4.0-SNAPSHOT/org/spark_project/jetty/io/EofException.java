package org.spark_project.jetty.io;

import java.io.EOFException;

public class EofException extends EOFException {
   public EofException() {
   }

   public EofException(String reason) {
      super(reason);
   }

   public EofException(Throwable th) {
      if (th != null) {
         this.initCause(th);
      }

   }
}
