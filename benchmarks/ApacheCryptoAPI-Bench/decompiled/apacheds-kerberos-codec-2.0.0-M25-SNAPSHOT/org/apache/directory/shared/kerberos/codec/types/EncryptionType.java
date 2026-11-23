package org.apache.directory.shared.kerberos.codec.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.directory.api.util.Strings;

public enum EncryptionType {
   UNKNOWN(-1, "UNKNOWN"),
   NULL(0, "null"),
   DES_CBC_CRC(1, "des-cbc-crc"),
   DES_CBC_MD4(2, "des-cbc-md4"),
   DES_CBC_MD5(3, "des-cbc-md5"),
   RESERVED4(4, "[reserved]"),
   DES3_CBC_MD5(5, "des3-cbc-md5"),
   RESERVED6(6, "[reserved]"),
   DES3_CBC_SHA1(7, "des3-cbc-sha1"),
   DSAWITHSHA1_CMSOID(9, "dsaWithSHA1-CmsOID"),
   MD5WITHRSAENCRYPTION_CMSOID(10, "md5WithRSAEncryption-CmsOID"),
   SHA1WITHRSAENCRYPTION_CMSOID(11, "sha1WithRSAEncryption-CmsOID"),
   RC2CBC_ENVOID(12, "rc2CBC-EnvOID"),
   RSAENCRYPTION_ENVOID(13, "rsaEncryption-EnvOID"),
   RSAES_OAEP_ENV_OID(14, "rsaES-OAEP-ENV-OID"),
   DES_EDE3_CBC_ENV_OID(15, "des-ede3-cbc-Env-OID"),
   DES3_CBC_SHA1_KD(16, "des3-cbc-sha1-kd"),
   AES128_CTS_HMAC_SHA1_96(17, "aes128-cts-hmac-sha1-96"),
   AES256_CTS_HMAC_SHA1_96(18, "aes256-cts-hmac-sha1-96"),
   RC4_HMAC(23, "rc4-hmac"),
   RC4_HMAC_EXP(24, "rc4-hmac-exp"),
   SUBKEY_KEYMATERIAL(65, "subkey-keymaterial"),
   RC4_MD4(-128, "rc4-md4"),
   RC4_HMAC_OLD(-133, "rc4-hmac-old"),
   RC4_HMAC_OLD_EXP(-135, "rc4-hmac-old-exp");

   private final int value;
   private final String name;
   private static Map<String, EncryptionType> encryptionTypesByName = new HashMap();
   private static Map<Integer, EncryptionType> encryptionTypesByValue = new HashMap();

   private EncryptionType(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public static Collection<EncryptionType> getEncryptionTypes() {
      return encryptionTypesByName.values();
   }

   public static EncryptionType getTypeByValue(int type) {
      return encryptionTypesByValue.containsKey(type) ? (EncryptionType)encryptionTypesByValue.get(type) : UNKNOWN;
   }

   public int getValue() {
      return this.value;
   }

   public String getName() {
      return this.name;
   }

   public static EncryptionType getByName(String type) {
      if (type == null) {
         return UNKNOWN;
      } else {
         String lcType = Strings.toLowerCaseAscii(type);
         return encryptionTypesByName.containsKey(lcType) ? (EncryptionType)encryptionTypesByName.get(lcType) : UNKNOWN;
      }
   }

   public String toString() {
      return this.getName() + " (" + this.value + ")";
   }

   static {
      for(EncryptionType type : values()) {
         encryptionTypesByName.put(Strings.toLowerCaseAscii(type.getName()), type);
         encryptionTypesByValue.put(type.getValue(), type);
      }

   }
}
