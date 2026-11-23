package org.apache.directory.shared.kerberos.codec.types;

public enum TransitedEncodingType {
   NULL(0),
   DOMAIN_X500_COMPRESS(1);

   private final int value;

   private TransitedEncodingType(int value) {
      this.value = value;
   }

   public static TransitedEncodingType getTypeByOrdinal(int type) {
      switch (type) {
         case 1:
            return DOMAIN_X500_COMPRESS;
         default:
            return NULL;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      switch (this) {
         case DOMAIN_X500_COMPRESS:
            return "Domain X500 compress (1)";
         default:
            return "null (0)";
      }
   }
}
