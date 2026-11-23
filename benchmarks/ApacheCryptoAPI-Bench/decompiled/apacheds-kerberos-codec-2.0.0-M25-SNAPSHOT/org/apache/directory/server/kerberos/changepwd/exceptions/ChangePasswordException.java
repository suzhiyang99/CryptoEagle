package org.apache.directory.server.kerberos.changepwd.exceptions;

import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class ChangePasswordException extends KerberosException {
   private static final long serialVersionUID = 4880242751298831543L;

   public ChangePasswordException(ChangePasswdErrorType errorType) {
      super(errorType.getValue(), errorType.getMessage());
   }

   public ChangePasswordException(ChangePasswdErrorType errorType, String message) {
      super(errorType.getValue(), message);
   }

   public ChangePasswordException(ChangePasswdErrorType errorType, Throwable cause) {
      super(errorType.getValue(), errorType.getMessage(), cause);
   }

   public ChangePasswordException(ChangePasswdErrorType errorType, byte[] explanatoryData) {
      super(errorType.getValue(), errorType.getMessage(), explanatoryData);
   }

   public ChangePasswordException(ChangePasswdErrorType errorType, byte[] explanatoryData, Throwable cause) {
      super(errorType.getValue(), errorType.getMessage(), explanatoryData, cause);
   }
}
