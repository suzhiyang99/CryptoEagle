package org.apache.directory.shared.kerberos.codec.types;

public enum PrincipalNameType {
   KRB_NT_UNKNOWN(0),
   KRB_NT_PRINCIPAL(1),
   KRB_NT_SRV_INST(2),
   KRB_NT_SRV_HST(3),
   KRB_NT_SRV_XHST(4),
   KRB_NT_UID(5),
   KRB_NT_X500_PRINCIPAL(6),
   KRB_NT_SMTP_NAME(7),
   KRB_NT_ENTERPRISE(10);

   private final int value;

   private PrincipalNameType(int value) {
      this.value = value;
   }

   public static PrincipalNameType getTypeByValue(int type) {
      switch (type) {
         case 0:
            return KRB_NT_UNKNOWN;
         case 1:
            return KRB_NT_PRINCIPAL;
         case 2:
            return KRB_NT_SRV_INST;
         case 3:
            return KRB_NT_SRV_HST;
         case 4:
            return KRB_NT_SRV_XHST;
         case 5:
            return KRB_NT_UID;
         case 6:
            return KRB_NT_X500_PRINCIPAL;
         case 7:
            return KRB_NT_SMTP_NAME;
         case 8:
         case 9:
         default:
            return KRB_NT_UNKNOWN;
         case 10:
            return KRB_NT_ENTERPRISE;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String toString() {
      switch (this) {
         case KRB_NT_UNKNOWN:
            return "Name type not known(" + this.value + ")";
         case KRB_NT_PRINCIPAL:
            return "Just the name of the principal as in DCE, or for users(" + this.value + ")";
         case KRB_NT_SRV_INST:
            return "Service and other unique instance (krbtgt)(" + this.value + ")";
         case KRB_NT_SRV_HST:
            return "Service with host name as instance (telnet, rcommands)(" + this.value + ")";
         case KRB_NT_SRV_XHST:
            return "Service with host as remaining components(" + this.value + ")";
         case KRB_NT_UID:
            return "Unique ID(" + this.value + ")";
         case KRB_NT_X500_PRINCIPAL:
            return "Encoded X.509 Distinguished name [RFC2253](" + this.value + ")";
         case KRB_NT_SMTP_NAME:
            return "Name in form of SMTP email name (e.g., user@example.com)(" + this.value + ")";
         case KRB_NT_ENTERPRISE:
            return "Enterprise name; may be mapped to principal name(" + this.value + ")";
         default:
            return "unknown name type(" + this.value + ")";
      }
   }
}
