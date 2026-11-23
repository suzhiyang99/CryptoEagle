package org.apache.manifoldcf.core.interfaces;

public class ManifoldCFException extends Exception {
   public static final String _rcsid = "@(#)$Id: ManifoldCFException.java 988245 2010-08-23 18:39:35Z kwright $";
   public static final int GENERAL_ERROR = 0;
   public static final int DATABASE_ERROR = 1;
   public static final int INTERRUPTED = 2;
   public static final int SETUP_ERROR = 3;
   public static final int DATABASE_CONNECTION_ERROR = 4;
   public static final int REPOSITORY_CONNECTION_ERROR = 5;
   public static final int DATABASE_TRANSACTION_ABORT = 6;
   protected int errcode;

   public ManifoldCFException(String errString) {
      super(errString);
      this.errcode = 0;
   }

   public ManifoldCFException(String errString, int errcode) {
      super(errString);
      this.errcode = errcode;
   }

   public ManifoldCFException(String errString, Throwable cause, int errcode) {
      super(errString, cause);
      this.errcode = errcode;
   }

   public ManifoldCFException(String errString, Throwable cause) {
      super(errString, cause);
      this.errcode = 0;
   }

   public ManifoldCFException(Throwable cause, int errcode) {
      super(cause);
      this.errcode = errcode;
   }

   public ManifoldCFException(Throwable cause) {
      super(cause);
      this.errcode = 0;
   }

   public int getErrorCode() {
      return this.errcode;
   }
}
