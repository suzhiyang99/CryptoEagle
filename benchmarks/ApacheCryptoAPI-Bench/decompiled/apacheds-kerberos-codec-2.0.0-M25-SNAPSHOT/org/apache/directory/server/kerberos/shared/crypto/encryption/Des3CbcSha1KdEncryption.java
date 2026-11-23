package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.kerberos.shared.crypto.checksum.ChecksumEngine;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class Des3CbcSha1KdEncryption extends EncryptionEngine implements ChecksumEngine {
   private static final byte[] iv = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

   public EncryptionType getEncryptionType() {
      return EncryptionType.DES3_CBC_SHA1_KD;
   }

   public int getConfounderLength() {
      return 8;
   }

   public int getChecksumLength() {
      return 20;
   }

   public ChecksumType checksumType() {
      return ChecksumType.HMAC_SHA1_DES3_KD;
   }

   public byte[] calculateChecksum(byte[] data, byte[] key, KeyUsage usage) {
      byte[] Kc = this.deriveKey(key, this.getUsageKc(usage), 64, 168);
      return this.processChecksum(data, Kc);
   }

   public byte[] calculateIntegrity(byte[] data, byte[] key, KeyUsage usage) {
      byte[] Ki = this.deriveKey(key, this.getUsageKi(usage), 64, 168);
      return this.processChecksum(data, Ki);
   }

   public byte[] getDecryptedData(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      byte[] Ke = this.deriveKey(key.getKeyValue(), this.getUsageKe(usage), 64, 168);
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
      byte[] Ke = this.deriveKey(key.getKeyValue(), this.getUsageKe(usage), 64, 168);
      byte[] conFounder = this.getRandomBytes(this.getConfounderLength());
      byte[] paddedPlainText = this.padString(plainText);
      byte[] dataBytes = this.concatenateBytes(conFounder, paddedPlainText);
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
      byte[] result = this.deriveRandom(baseKey, usage, n, k);
      result = this.randomToKey(result);
      return result;
   }

   protected byte[] randomToKey(byte[] seed) {
      int kBytes = 24;
      byte[] result = new byte[kBytes];
      byte[] fillingKey = Strings.EMPTY_BYTES;
      int pos = 0;

      for(int i = 0; i < kBytes; ++i) {
         if (pos < fillingKey.length) {
            result[i] = fillingKey[pos];
            ++pos;
         } else {
            byte[] var7 = this.getBitGroup(seed, i / 8);
            fillingKey = this.setParity(var7);
            pos = 0;
            result[i] = fillingKey[pos];
            ++pos;
         }
      }

      return result;
   }

   protected byte[] getBitGroup(byte[] seed, int group) {
      int srcPos = group * 7;
      byte[] result = new byte[7];
      System.arraycopy(seed, srcPos, result, 0, 7);
      return result;
   }

   protected byte[] setParity(byte[] in) {
      byte[] expandedIn = new byte[8];
      System.arraycopy(in, 0, expandedIn, 0, in.length);
      this.setBit(expandedIn, 62, this.getBit(in, 7));
      this.setBit(expandedIn, 61, this.getBit(in, 15));
      this.setBit(expandedIn, 60, this.getBit(in, 23));
      this.setBit(expandedIn, 59, this.getBit(in, 31));
      this.setBit(expandedIn, 58, this.getBit(in, 39));
      this.setBit(expandedIn, 57, this.getBit(in, 47));
      this.setBit(expandedIn, 56, this.getBit(in, 55));
      byte[] out = new byte[8];
      int bitCount = 0;
      int index = 0;

      for(int i = 0; i < 64; ++i) {
         if ((i + 1) % 8 == 0) {
            if (bitCount % 2 == 0) {
               this.setBit(out, i, 1);
            }

            ++index;
            bitCount = 0;
         } else {
            int val = this.getBit(expandedIn, index);
            boolean bit = val > 0;
            if (bit) {
               this.setBit(out, i, val);
               ++bitCount;
            }

            ++index;
         }
      }

      return out;
   }

   private byte[] processCipher(boolean isEncrypt, byte[] data, byte[] keyBytes) {
      try {
         Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
         SecretKey key = new SecretKeySpec(keyBytes, "DESede");
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

   private byte[] processChecksum(byte[] data, byte[] key) {
      try {
         SecretKey sk = new SecretKeySpec(key, "DESede");
         Mac mac = Mac.getInstance("HmacSHA1");
         mac.init(sk);
         return mac.doFinal(data);
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }
}
