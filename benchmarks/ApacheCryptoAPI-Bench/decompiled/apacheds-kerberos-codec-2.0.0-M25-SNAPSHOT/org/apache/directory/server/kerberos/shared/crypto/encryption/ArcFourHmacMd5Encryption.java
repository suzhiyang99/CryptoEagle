package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

class ArcFourHmacMd5Encryption extends EncryptionEngine {
   public EncryptionType getEncryptionType() {
      return EncryptionType.RC4_HMAC;
   }

   public int getChecksumLength() {
      return 16;
   }

   public int getConfounderLength() {
      return 8;
   }

   public byte[] getDecryptedData(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      return data.getCipher();
   }

   public EncryptedData getEncryptedData(EncryptionKey key, byte[] plainText, KeyUsage usage) {
      return new EncryptedData(this.getEncryptionType(), key.getKeyVersion(), plainText);
   }

   public byte[] encrypt(byte[] plainText, byte[] keyBytes) {
      return this.processCipher(true, plainText, keyBytes);
   }

   public byte[] decrypt(byte[] cipherText, byte[] keyBytes) {
      return this.processCipher(false, cipherText, keyBytes);
   }

   public byte[] calculateIntegrity(byte[] data, byte[] key, KeyUsage usage) {
      try {
         Mac digester = Mac.getInstance("HmacMD5");
         return digester.doFinal(data);
      } catch (NoSuchAlgorithmException var5) {
         return null;
      }
   }

   private byte[] processCipher(boolean isEncrypt, byte[] data, byte[] keyBytes) {
      try {
         Cipher cipher = Cipher.getInstance("ARCFOUR");
         SecretKey key = new SecretKeySpec(keyBytes, "ARCFOUR");
         if (isEncrypt) {
            cipher.init(1, key);
         } else {
            cipher.init(2, key);
         }

         return cipher.doFinal(data);
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }
}
