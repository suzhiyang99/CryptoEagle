package org.apache.directory.shared.kerberos.codec.options;

public class ApOptions extends Options {
   public static final int RESERVED = 0;
   public static final int USE_SESSION_KEY = 1;
   public static final int MUTUAL_REQUIRED = 2;
   public static final int MAX_VALUE = 32;

   public ApOptions() {
      super(32);
   }

   public ApOptions(byte[] options) {
      super(32);
      this.setBytes(options);
   }

   public String toString() {
      StringBuffer result = new StringBuffer();
      if (this.get(2)) {
         result.append("MUTUAL_REQUIRED ");
      }

      if (this.get(0)) {
         result.append("RESERVED ");
      }

      if (this.get(1)) {
         result.append("USE_SESSION_KEY ");
      }

      return result.toString().trim();
   }
}
