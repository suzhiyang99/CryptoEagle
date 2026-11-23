package org.apache.directory.server.kerberos.changepwd.exceptions;

public enum ChangePasswdErrorType {
   KRB5_KPASSWD_MALFORMED(1, "Request failed due to being malformed."),
   KRB5_KPASSWD_HARDERROR(2, "Request failed due to a hard error in processing the request."),
   KRB5_KPASSWD_AUTHERROR(3, "Request failed due to an error in authentication processing."),
   KRB5_KPASSWD_SOFTERROR(4, "Request failed due to a soft error in processing the request."),
   KRB5_KPASSWD_ACCESSDENIED(5, "Requestor not authorized."),
   KRB5_KPASSWD_BAD_VERSION(6, "Protocol version unsupported."),
   KRB5_KPASSWD_INITIAL_FLAG_NEEDED(7, "Initial flag required."),
   KRB5_KPASSWD_UNKNOWN_ERROR(8, "Request failed for an unknown reason.");

   private final String name;
   private final int value;

   private ChangePasswdErrorType(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public String getMessage() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   public static ChangePasswdErrorType getTypeByValue(int value) {
      for(ChangePasswdErrorType et : values()) {
         if (value == et.getValue()) {
            return et;
         }
      }

      return KRB5_KPASSWD_UNKNOWN_ERROR;
   }

   public int getValue() {
      return this.value;
   }
}
