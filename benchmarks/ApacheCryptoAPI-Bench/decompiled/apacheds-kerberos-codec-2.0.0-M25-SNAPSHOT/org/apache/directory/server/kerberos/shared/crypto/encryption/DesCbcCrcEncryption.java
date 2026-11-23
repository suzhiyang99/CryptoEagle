package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.zip.CRC32;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class DesCbcCrcEncryption extends EncryptionEngine {
   private static final byte[] iv = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

   public EncryptionType getEncryptionType() {
      return EncryptionType.DES_CBC_CRC;
   }

   public int getConfounderLength() {
      return 8;
   }

   public int getChecksumLength() {
      return 4;
   }

   public byte[] calculateIntegrity(byte[] data, byte[] key, KeyUsage usage) {
      CRC32 crc32 = new CRC32();
      crc32.update(data);
      return this.int2octet((int)crc32.getValue());
   }

   private byte[] int2octet(int value) {
      byte[] bytes = new byte[4];
      int i = 0;

      for(int shift = 24; i < 4; shift -= 8) {
         bytes[i] = (byte)(255 & value >> shift);
         ++i;
      }

      return bytes;
   }

   public byte[] getDecryptedData(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      byte[] decryptedData = this.decrypt(data.getCipher(), key.getKeyValue());
      byte[] oldChecksum = new byte[this.getChecksumLength()];
      System.arraycopy(decryptedData, this.getConfounderLength(), oldChecksum, 0, oldChecksum.length);

      for(int i = this.getConfounderLength(); i < this.getConfounderLength() + this.getChecksumLength(); ++i) {
         decryptedData[i] = 0;
      }

      byte[] newChecksum = this.calculateIntegrity(decryptedData, key.getKeyValue(), usage);
      if (!Arrays.equals(oldChecksum, newChecksum)) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY);
      } else {
         return this.removeLeadingBytes(decryptedData, this.getConfounderLength(), this.getChecksumLength());
      }
   }

   public EncryptedData getEncryptedData(EncryptionKey key, byte[] plainText, KeyUsage usage) {
      byte[] conFounder = this.getRandomBytes(this.getConfounderLength());
      byte[] zeroedChecksum = new byte[this.getChecksumLength()];
      byte[] dataBytes = this.concatenateBytes(conFounder, this.concatenateBytes(zeroedChecksum, plainText));
      byte[] paddedDataBytes = this.padString(dataBytes);
      byte[] checksumBytes = this.calculateIntegrity(paddedDataBytes, (byte[])null, usage);

      for(int i = this.getConfounderLength(); i < this.getConfounderLength() + this.getChecksumLength(); ++i) {
         paddedDataBytes[i] = checksumBytes[i - this.getConfounderLength()];
      }

      byte[] encryptedData = this.encrypt(paddedDataBytes, key.getKeyValue());
      return new EncryptedData(this.getEncryptionType(), key.getKeyVersion(), encryptedData);
   }

   public byte[] encrypt(byte[] plainText, byte[] keyBytes) {
      return this.processCipher(true, plainText, keyBytes);
   }

   public byte[] decrypt(byte[] cipherText, byte[] keyBytes) {
      return this.processCipher(false, cipherText, keyBytes);
   }

   private byte[] processCipher(boolean isEncrypt, byte[] data, byte[] keyBytes) {
      try {
         Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
         SecretKey key = new SecretKeySpec(keyBytes, "DES");
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
