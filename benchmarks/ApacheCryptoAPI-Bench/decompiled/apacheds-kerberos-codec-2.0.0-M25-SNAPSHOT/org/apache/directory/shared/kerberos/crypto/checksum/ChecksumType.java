package org.apache.directory.shared.kerberos.crypto.checksum;

public enum ChecksumType implements Comparable<ChecksumType> {
   UNKNOWN(-1, "UNKNOWN"),
   NULL(0, "NULL"),
   CRC32(1, "CRC32"),
   RSA_MD4(2, "rsa-md4"),
   RSA_MD4_DES(3, "rsa-md4-des"),
   DES_MAC(4, "des-mac"),
   DES_MAC_K(5, "rsa-md5"),
   RSA_MD4_DES_K(6, "rsa-md5"),
   RSA_MD5(7, "rsa-md5"),
   RSA_MD5_DES(8, "rsa-md5-des"),
   RSA_MD5_DES3(9, "rsa-md5-des3"),
   SHA1(10, "sha1"),
   HMAC_SHA1_DES3_KD(12, "hmac-sha1-des3-kd"),
   HMAC_SHA1_DES3(13, "hmac-sha1-des3"),
   SHA1_2(14, "sha1"),
   HMAC_SHA1_96_AES128(15, "hmac-sha1-96-aes128"),
   HMAC_SHA1_96_AES256(16, "hmac-sha1-96-aes256"),
   HMAC_MD5(-138, "hmac-md5");

   private final int value;
   private final String name;

   private ChecksumType(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public static ChecksumType getTypeByValue(int type) {
      switch (type) {
         case -138:
            return HMAC_MD5;
         case -1:
            return UNKNOWN;
         case 0:
            return NULL;
         case 1:
            return CRC32;
         case 2:
            return RSA_MD4;
         case 3:
            return RSA_MD4_DES;
         case 4:
            return DES_MAC;
         case 5:
            return DES_MAC_K;
         case 6:
            return RSA_MD4_DES_K;
         case 7:
            return RSA_MD5;
         case 8:
            return RSA_MD5_DES;
         case 9:
            return RSA_MD5_DES3;
         case 10:
            return SHA1;
         case 12:
            return HMAC_SHA1_DES3_KD;
         case 13:
            return HMAC_SHA1_DES3;
         case 14:
            return SHA1_2;
         case 15:
            return HMAC_SHA1_96_AES128;
         case 16:
            return HMAC_SHA1_96_AES256;
         default:
            return UNKNOWN;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getName() + " (" + this.value + ")";
   }
}
