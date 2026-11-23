package org.apache.directory.shared.kerberos.codec.types;

public enum PaDataType {
   NULL(0),
   PA_TGS_REQ(1),
   PA_ENC_TIMESTAMP(2),
   PA_PW_SALT(3),
   PA_ENC_UNIX_TIME(5),
   PA_SANDIA_SECUREID(6),
   PA_SESAME(7),
   PA_OSF_DCE(8),
   PA_CYBERSAFE_SECUREID(9),
   PA_ASF3_SALT(10),
   PA_ENCTYPE_INFO(11),
   SAM_CHALLENGE(12),
   SAM_RESPONSE(13),
   PA_PK_AS_REQ(14),
   PA_PK_AS_REP(15),
   PA_ENCTYPE_INFO2(19),
   PA_USE_SPECIFIED_KVNO(20),
   SAM_REDIRECT(21),
   PA_GET_FROM_TYPED_DATA(22);

   private final int value;

   private PaDataType(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static PaDataType getTypeByValue(int type) {
      switch (type) {
         case 1:
            return PA_TGS_REQ;
         case 2:
            return PA_ENC_TIMESTAMP;
         case 3:
            return PA_PW_SALT;
         case 4:
         case 16:
         case 17:
         case 18:
         default:
            return NULL;
         case 5:
            return PA_ENC_UNIX_TIME;
         case 6:
            return PA_SANDIA_SECUREID;
         case 7:
            return PA_SESAME;
         case 8:
            return PA_OSF_DCE;
         case 9:
            return PA_CYBERSAFE_SECUREID;
         case 10:
            return PA_ASF3_SALT;
         case 11:
            return PA_ENCTYPE_INFO;
         case 12:
            return SAM_CHALLENGE;
         case 13:
            return SAM_RESPONSE;
         case 14:
            return PA_PK_AS_REQ;
         case 15:
            return PA_PK_AS_REQ;
         case 19:
            return PA_ENCTYPE_INFO2;
         case 20:
            return PA_USE_SPECIFIED_KVNO;
         case 21:
            return SAM_REDIRECT;
         case 22:
            return PA_GET_FROM_TYPED_DATA;
      }
   }

   public String toString() {
      switch (this) {
         case PA_TGS_REQ:
            return "TGS request.(" + this.value + ")";
         case PA_ENC_TIMESTAMP:
            return "Encrypted timestamp.(" + this.value + ")";
         case PA_PW_SALT:
            return "password salt(" + this.value + ")";
         case PA_ENC_UNIX_TIME:
            return "enc unix time(" + this.value + ")";
         case PA_SANDIA_SECUREID:
            return "sandia secureid(" + this.value + ")";
         case PA_SESAME:
            return "sesame(" + this.value + ")";
         case PA_OSF_DCE:
            return "OSF DCE(" + this.value + ")";
         case PA_CYBERSAFE_SECUREID:
            return "cybersafe secureid(" + this.value + ")";
         case PA_ASF3_SALT:
            return "ASF3 salt(" + this.value + ")";
         case PA_ENCTYPE_INFO:
            return "Encryption info.(" + this.value + ")";
         case SAM_CHALLENGE:
            return "SAM challenge.(" + this.value + ")";
         case SAM_RESPONSE:
            return "SAM response.(" + this.value + ")";
         case PA_PK_AS_REQ:
            return "PK as request(" + this.value + ")";
         case PA_ENCTYPE_INFO2:
            return "Encryption info.(" + this.value + ")";
         case PA_PK_AS_REP:
            return "PK as response(" + this.value + ")";
         case PA_USE_SPECIFIED_KVNO:
            return "use specified key version(" + this.value + ")";
         case SAM_REDIRECT:
            return "SAM redirect.(" + this.value + ")";
         case PA_GET_FROM_TYPED_DATA:
            return "Get from typed data(" + this.value + ")";
         default:
            return "null(" + this.value + ")";
      }
   }
}
