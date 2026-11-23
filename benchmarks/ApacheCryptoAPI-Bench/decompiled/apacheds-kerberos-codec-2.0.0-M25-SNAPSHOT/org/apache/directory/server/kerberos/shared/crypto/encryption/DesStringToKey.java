package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.api.util.Strings;

public class DesStringToKey {
   public byte[] getKey(String passPhrase) {
      return this.generateKey(passPhrase);
   }

   public byte[] getKey(String password, String realmName, String userName) {
      return this.generateKey(password + realmName + userName);
   }

   protected byte[] generateKey(String passPhrase) {
      byte[] encodedByteArray = this.characterEncodeString(passPhrase);
      byte[] paddedByteArray = this.padString(encodedByteArray);
      byte[] secretKey = this.fanFold(paddedByteArray);
      secretKey = this.setParity(secretKey);
      secretKey = this.getStrongKey(secretKey);
      secretKey = this.calculateChecksum(paddedByteArray, secretKey);
      secretKey = this.setParity(secretKey);
      secretKey = this.getStrongKey(secretKey);
      return secretKey;
   }

   protected byte[] setParity(byte[] in) {
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
            int val = this.getBit(in, index);
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

   protected int getBit(byte[] data, int pos) {
      int posByte = pos / 8;
      int posBit = pos % 8;
      byte valByte = data[posByte];
      int valInt = valByte >> 8 - (posBit + 1) & 1;
      return valInt;
   }

   protected void setBit(byte[] data, int pos, int val) {
      int posByte = pos / 8;
      int posBit = pos % 8;
      byte oldByte = data[posByte];
      oldByte = (byte)('ｿ' >> posBit & oldByte & 255);
      byte newByte = (byte)(val << 8 - (posBit + 1) | oldByte);
      data[posByte] = newByte;
   }

   protected byte[] fanFold(byte[] paddedByteArray) {
      byte[] secretKey = new byte[8];
      int div = paddedByteArray.length / 8;

      for(int ii = 0; ii < div; ++ii) {
         byte[] blockValue1 = new byte[8];
         System.arraycopy(paddedByteArray, ii * 8, blockValue1, 0, 8);
         if (ii % 2 == 1) {
            byte tempbyte1 = 0;
            byte tempbyte2 = 0;
            byte[] blockValue2 = new byte[8];

            for(int jj = 0; jj < 8; ++jj) {
               tempbyte2 = 0;

               for(int kk = 0; kk < 4; ++kk) {
                  tempbyte2 = (byte)(1 << 7 - kk & 255);
                  tempbyte1 = (byte)(tempbyte1 | (blockValue1[jj] & tempbyte2) >>> 7 - 2 * kk);
                  tempbyte2 = 0;
               }

               for(int kk = 4; kk < 8; ++kk) {
                  tempbyte2 = (byte)(1 << 7 - kk & 255);
                  tempbyte1 = (byte)(tempbyte1 | (blockValue1[jj] & tempbyte2) << 2 * kk - 7);
                  tempbyte2 = 0;
               }

               blockValue2[7 - jj] = tempbyte1;
               tempbyte1 = 0;
            }

            for(int jj = 0; jj < 8; ++jj) {
               blockValue2[jj] = (byte)((blockValue2[jj] & 255) >>> 1 & 255);
            }

            System.arraycopy(blockValue2, 0, blockValue1, 0, blockValue2.length);
         }

         for(int jj = 0; jj < 8; ++jj) {
            blockValue1[jj] = (byte)((blockValue1[jj] & 255) << 1 & 255);
         }

         for(int jj = 0; jj < 8; ++jj) {
            secretKey[jj] ^= blockValue1[jj];
         }
      }

      return secretKey;
   }

   protected byte[] calculateChecksum(byte[] data, byte[] keyBytes) {
      try {
         Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
         SecretKey key = new SecretKeySpec(keyBytes, "DES");
         AlgorithmParameterSpec paramSpec = new IvParameterSpec(keyBytes);
         cipher.init(1, key, paramSpec);
         byte[] result = cipher.doFinal(data);
         byte[] checksum = new byte[8];
         System.arraycopy(result, result.length - 8, checksum, 0, 8);
         return checksum;
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }

   protected byte[] getStrongKey(byte[] secretKey) {
      try {
         if (DESKeySpec.isWeak(secretKey, 0)) {
            secretKey[7] = (byte)(secretKey[7] ^ 240);
         }

         return secretKey;
      } catch (InvalidKeyException var3) {
         return new byte[8];
      }
   }

   protected byte[] characterEncodeString(String string) {
      byte[] encodedByteArray = new byte[string.length()];
      encodedByteArray = Strings.getBytesUtf8(string);
      return encodedByteArray;
   }

   protected byte[] padString(byte[] encodedString) {
      int length;
      if (encodedString.length < 8) {
         length = encodedString.length;
      } else {
         length = encodedString.length % 8;
      }

      if (length == 0) {
         return encodedString;
      } else {
         byte[] paddedByteArray = new byte[8 - length + encodedString.length];

         for(int ii = paddedByteArray.length - 1; ii > encodedString.length - 1; --ii) {
            paddedByteArray[ii] = 0;
         }

         System.arraycopy(encodedString, 0, paddedByteArray, 0, encodedString.length);
         return paddedByteArray;
      }
   }
}
