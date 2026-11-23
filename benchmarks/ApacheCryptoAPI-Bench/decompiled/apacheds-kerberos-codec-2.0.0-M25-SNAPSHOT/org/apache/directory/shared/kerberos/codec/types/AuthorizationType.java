package org.apache.directory.shared.kerberos.codec.types;

public enum AuthorizationType {
   NULL(0),
   AD_IF_RELEVANT(1),
   AD_INTENDED_FOR_SERVER(2),
   AD_INTENDED_FOR_APPLICATION_CLASS(3),
   AD_KDC_ISSUED(4),
   AD_OR(5),
   AD_MANDATORY_TICKET_EXTENSIONS(6),
   AD_IN_TICKET_EXTENSIONS(7),
   AD_MANDATORY_FOR_KDC(8),
   OSF_DCE(64),
   SESAME(65),
   AD_OSF_DCE_PKI_CERTID(66),
   AD_WIN2K_PAC(128),
   AD_ETYPE_NEGOTIATION(129);

   private final int value;

   private AuthorizationType(int value) {
      this.value = value;
   }

   public static AuthorizationType getTypeByValue(int type) {
      switch (type) {
         case 1:
            return AD_IF_RELEVANT;
         case 2:
            return AD_INTENDED_FOR_SERVER;
         case 3:
            return AD_INTENDED_FOR_APPLICATION_CLASS;
         case 4:
            return AD_KDC_ISSUED;
         case 5:
            return AD_OR;
         case 6:
            return AD_MANDATORY_TICKET_EXTENSIONS;
         case 7:
            return AD_IN_TICKET_EXTENSIONS;
         case 8:
            return AD_MANDATORY_FOR_KDC;
         case 64:
            return OSF_DCE;
         case 65:
            return SESAME;
         case 66:
            return AD_OSF_DCE_PKI_CERTID;
         case 128:
            return AD_WIN2K_PAC;
         case 129:
            return AD_ETYPE_NEGOTIATION;
         default:
            return NULL;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      switch (this) {
         case AD_IF_RELEVANT:
            return "if relevant(" + this.value + ")";
         case AD_INTENDED_FOR_SERVER:
            return "intended for server(" + this.value + ")";
         case AD_INTENDED_FOR_APPLICATION_CLASS:
            return "intended for application class(" + this.value + ")";
         case AD_KDC_ISSUED:
            return "kdc issued(" + this.value + ")";
         case AD_OR:
            return "or(" + this.value + ")";
         case AD_MANDATORY_TICKET_EXTENSIONS:
            return "mandatory ticket extensions(" + this.value + ")";
         case AD_IN_TICKET_EXTENSIONS:
            return "in ticket extensions(" + this.value + ")";
         case AD_MANDATORY_FOR_KDC:
            return "mandatory-for-kdc(" + this.value + ")";
         case OSF_DCE:
            return "OSF DCE(" + this.value + ")";
         case SESAME:
            return "sesame(" + this.value + ")";
         case AD_OSF_DCE_PKI_CERTID:
            return "OSF DCE pki certid(" + this.value + ")";
         case AD_WIN2K_PAC:
            return "win 2000 PAC(" + this.value + ")";
         case AD_ETYPE_NEGOTIATION:
            return "etype negociation(" + this.value + ")";
         default:
            return "null(" + this.value + ")";
      }
   }
}
