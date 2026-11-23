package org.apache.openejb;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class UndeployException extends OpenEJBException {
   private final List<Throwable> causes = new ArrayList();

   public UndeployException() {
   }

   public UndeployException(String message) {
      super(message);
   }

   public UndeployException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public UndeployException(Throwable rootCause) {
      super(rootCause);
   }

   public List<Throwable> getCauses() {
      return this.causes;
   }

   public void printStackTrace(PrintStream s) {
      super.printStackTrace(s);

      for(Throwable throwable : this.causes) {
         s.print("Nested caused by: ");
         throwable.printStackTrace(s);
      }

   }

   public void printStackTrace(PrintWriter s) {
      super.printStackTrace(s);

      for(Throwable throwable : this.causes) {
         s.print("Nested caused by: ");
         throwable.printStackTrace(s);
      }

   }
}
