package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.server.kerberos.shared.crypto.checksum.ChecksumEngine;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

abstract class AesCtsSha1Encryption extends EncryptionEngine implements ChecksumEngine {
   private static final byte[] iv = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   public int getConfounderLength() {
      return 16;
   }

   public int getChecksumLength() {
      return 12;
   }

   protected abstract int getKeyLength();

   public byte[] calculateChecksum(byte[] data, byte[] key, KeyUsage usage) {
      byte[] Kc = this.deriveKey(key, this.getUsageKc(usage), 128, this.getKeyLength());
      byte[] checksum = this.processChecksum(data, Kc);
      return this.removeTrailingBytes(checksum, 0, checksum.length - this.getChecksumLength());
   }

   public byte[] calculateIntegrity(byte[] data, byte[] key, KeyUsage usage) {
      byte[] Ki = this.deriveKey(key, this.getUsageKi(usage), 128, this.getKeyLength());
      byte[] checksum = this.processChecksum(data, Ki);
      return this.removeTrailingBytes(checksum, 0, checksum.length - this.getChecksumLength());
   }

   public byte[] getDecryptedData(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      byte[] Ke = this.deriveKey(key.getKeyValue(), this.getUsageKe(usage), 128, this.getKeyLength());
      byte[] encryptedData = data.getCipher();
      byte[] oldChecksum = new byte[this.getChecksumLength()];
      System.arraycopy(encryptedData, encryptedData.length - this.getChecksumLength(), oldChecksum, 0, oldChecksum.length);
      encryptedData = this.removeTrailingBytes(encryptedData, 0, this.getChecksumLength());
      byte[] decryptedData = this.decrypt(encryptedData, Ke);
      byte[] withoutConfounder = this.removeLeadingBytes(decryptedData, this.getConfounderLength(), 0);
      byte[] newChecksum = this.calculateIntegrity(decryptedData, key.getKeyValue(), usage);
      if (!Arrays.equals(oldChecksum, newChecksum)) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY);
      } else {
         return withoutConfounder;
      }
   }

   public EncryptedData getEncryptedData(EncryptionKey key, byte[] plainText, KeyUsage usage) {
      byte[] Ke = this.deriveKey(key.getKeyValue(), this.getUsageKe(usage), 128, this.getKeyLength());
      byte[] conFounder = this.getRandomBytes(this.getConfounderLength());
      byte[] dataBytes = this.concatenateBytes(conFounder, plainText);
      byte[] checksumBytes = this.calculateIntegrity(dataBytes, key.getKeyValue(), usage);
      byte[] encryptedData = this.encrypt(dataBytes, Ke);
      byte[] cipherText = this.concatenateBytes(encryptedData, checksumBytes);
      return new EncryptedData(this.getEncryptionType(), key.getKeyVersion(), cipherText);
   }

   public byte[] encrypt(byte[] plainText, byte[] keyBytes) {
      return this.processCipher(true, plainText, keyBytes);
   }

   public byte[] decrypt(byte[] cipherText, byte[] keyBytes) {
      return this.processCipher(false, cipherText, keyBytes);
   }

   protected byte[] deriveKey(byte[] baseKey, byte[] usage, int n, int k) {
      return this.deriveRandom(baseKey, usage, n, k);
   }

   private byte[] processChecksum(byte[] data, byte[] key) {
      try {
         SecretKey sk = new SecretKeySpec(key, "AES");
         Mac mac = Mac.getInstance("HmacSHA1");
         mac.init(sk);
         return mac.doFinal(data);
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }

   private byte[] processCipher(boolean isEncrypt, byte[] data, byte[] keyBytes) {
      try {
         Cipher cipher = Cipher.getInstance("AES/CTS/NoPadding");
         SecretKey key = new SecretKeySpec(keyBytes, "AES");
         AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
         if (isEncrypt) {
            cipher.init(1, key, paramSpec);
         } else {
            cipher.init(2, key, paramSpec);
         }

         return cipher.doFinal(data);
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }
}
