package org.apache.openejb.core.ivm.naming;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.openejb.OpenEJBException;

public class NamingException extends javax.naming.NamingException {
   private final OpenEJBException delegate;

   public NamingException(String message, OpenEJBException delegateArg) {
      super(message);
      this.delegate = delegateArg;
   }

   public NamingException(String message, Throwable rootCause) {
      this.delegate = new OpenEJBException(message, rootCause);
   }

   public String getMessage() {
      return this.delegate.getMessage();
   }

   public void printStackTrace() {
      this.delegate.printStackTrace();
   }

   public void printStackTrace(PrintStream stream) {
      this.delegate.printStackTrace(stream);
   }

   public void printStackTrace(PrintWriter writer) {
      this.delegate.printStackTrace(writer);
   }
}
