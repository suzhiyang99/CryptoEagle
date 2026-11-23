package org.apache.directory.shared.kerberos.exceptions;

import org.apache.directory.shared.kerberos.messages.KrbError;

public class KerberosException extends Exception {
   private static final long serialVersionUID = 2968072183596955597L;
   private KrbError error;
   private final int errorCode;
   private byte[] explanatoryData;

   public KerberosException(ErrorType errorType) {
      super(errorType.getMessage());
      this.errorCode = errorType.getValue();
   }

   public KerberosException(KrbError error) {
      super(error.getMessageType().getMessage());
      this.errorCode = error.getErrorCode().getValue();
      this.error = error;
   }

   public KerberosException(ErrorType errorType, Throwable cause) {
      super(errorType.getMessage(), cause);
      this.errorCode = errorType.getValue();
   }

   public KerberosException(ErrorType errorType, String msg) {
      super(msg);
      this.errorCode = errorType.getValue();
   }

   public KerberosException(ErrorType errorType, String msg, Throwable cause) {
      super(msg, cause);
      this.errorCode = errorType.getValue();
   }

   public KerberosException(ErrorType errorType, byte[] explanatoryData) {
      super(errorType.getMessage());
      this.errorCode = errorType.getValue();
      this.explanatoryData = explanatoryData;
   }

   public KerberosException(ErrorType errorType, byte[] explanatoryData, Throwable cause) {
      super(errorType.getMessage(), cause);
      this.errorCode = errorType.getValue();
      this.explanatoryData = explanatoryData;
   }

   public int getErrorCode() {
      return this.errorCode;
   }

   public byte[] getExplanatoryData() {
      return this.explanatoryData;
   }

   protected KerberosException(int errorCode, String msg) {
      super(msg);
      this.errorCode = errorCode;
   }

   protected KerberosException(int errorCode, String msg, Throwable cause) {
      super(msg, cause);
      this.errorCode = errorCode;
   }

   protected KerberosException(int errorCode, String msg, byte[] explanatoryData) {
      super(msg);
      this.errorCode = errorCode;
      this.explanatoryData = explanatoryData;
   }

   protected KerberosException(int errorCode, String msg, byte[] explanatoryData, Throwable cause) {
      super(msg, cause);
      this.errorCode = errorCode;
      this.explanatoryData = explanatoryData;
   }

   public KrbError getError() {
      return this.error;
   }
}
