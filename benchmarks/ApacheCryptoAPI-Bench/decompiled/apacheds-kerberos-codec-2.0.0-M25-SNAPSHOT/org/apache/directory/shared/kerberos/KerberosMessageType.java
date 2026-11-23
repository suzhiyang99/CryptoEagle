package org.apache.directory.shared.kerberos;

public enum KerberosMessageType {
   TICKET(1, "ticket"),
   AUTHENTICATOR(2, "Authenticator"),
   ENC_TICKET_PART(3, "EncTicketPart"),
   AS_REQ(10, "initial authentication request"),
   AS_REP(11, "initial authentication response"),
   TGS_REQ(12, "request for authentication based on TGT"),
   TGS_REP(13, "response to authentication based on TGT"),
   AP_REQ(14, "application request"),
   AP_REP(15, "application response"),
   KRB_SAFE(20, "safe (checksummed) application message"),
   KRB_PRIV(21, "private (encrypted) application message"),
   KRB_CRED(22, "private (encrypted) message to forward credentials"),
   ENC_AS_REP_PART(25, "encrypted authentication reply part"),
   ENC_TGS_REP_PART(26, "encrypted TGT reply part"),
   ENC_AP_REP_PART(27, "encrypted application reply part"),
   ENC_PRIV_PART(28, "encrypted private message part"),
   KRB_ERROR(30, "error response");

   private int value;
   private String message;

   private KerberosMessageType(int value, String message) {
      this.value = value;
      this.message = message;
   }

   public int getValue() {
      return this.value;
   }

   public String getMessage() {
      return this.message;
   }

   public static KerberosMessageType getTypeByValue(int value) {
      switch (value) {
         case 1:
            return TICKET;
         case 2:
            return AUTHENTICATOR;
         case 3:
            return ENC_TICKET_PART;
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 16:
         case 17:
         case 18:
         case 19:
         case 23:
         case 24:
         case 25:
         case 26:
         case 29:
         default:
            return null;
         case 10:
            return AS_REQ;
         case 11:
            return AS_REP;
         case 12:
            return TGS_REQ;
         case 13:
            return TGS_REP;
         case 14:
            return AP_REQ;
         case 15:
            return AP_REP;
         case 20:
            return KRB_SAFE;
         case 21:
            return KRB_PRIV;
         case 22:
            return KRB_CRED;
         case 27:
            return ENC_AP_REP_PART;
         case 28:
            return ENC_PRIV_PART;
         case 30:
            return KRB_ERROR;
      }
   }
}
