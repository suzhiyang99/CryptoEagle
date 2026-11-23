package org.apache.directory.shared.kerberos.exceptions;

public class InvalidTicketException extends KerberosException {
   static final long serialVersionUID = 1L;

   public InvalidTicketException(ErrorType errorType) {
      super(errorType);
   }

   public InvalidTicketException(ErrorType errorType, String explanation) {
      super(errorType, explanation);
   }
}
