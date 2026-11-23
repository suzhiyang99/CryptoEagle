package org.apache.directory.shared.kerberos.codec.types;

public enum SamType {
   PA_SAM_TYPE_ENIGMA(1, "Enigma Logic"),
   PA_SAM_TYPE_DIGI_PATH(2, "Digital Pathways"),
   PA_SAM_TYPE_SKEY_K0(3, "S/key where KDC has key 0"),
   PA_SAM_TYPE_SKEY(4, "Traditional S/Key"),
   PA_SAM_TYPE_SECURID(5, "Security Dynamics"),
   PA_SAM_TYPE_CRYPTOCARD(6, "CRYPTOCard"),
   PA_SAM_TYPE_APACHE(7, "Apache Software Foundation");

   private String name;
   private int ordinal;

   private SamType(int ordinal, String name) {
      this.ordinal = ordinal;
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public static SamType getTypeByOrdinal(int ordinal) {
      for(SamType st : values()) {
         if (ordinal == st.getOrdinal()) {
            return st;
         }
      }

      return PA_SAM_TYPE_APACHE;
   }

   public int getOrdinal() {
      return this.ordinal;
   }
}
