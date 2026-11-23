package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.SecureRandom;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public abstract class EncryptionEngine {
   private static final SecureRandom random = new SecureRandom();

   protected abstract byte[] getDecryptedData(EncryptionKey var1, EncryptedData var2, KeyUsage var3) throws KerberosException;

   protected abstract EncryptedData getEncryptedData(EncryptionKey var1, byte[] var2, KeyUsage var3);

   protected abstract EncryptionType getEncryptionType();

   protected abstract int getConfounderLength();

   protected abstract int getChecksumLength();

   protected abstract byte[] encrypt(byte[] var1, byte[] var2);

   protected abstract byte[] decrypt(byte[] var1, byte[] var2);

   protected abstract byte[] calculateIntegrity(byte[] var1, byte[] var2, KeyUsage var3);

   protected byte[] deriveRandom(byte[] key, byte[] usage, int n, int k) {
      byte[] nFoldedUsage = NFold.nFold(n, usage);
      int kBytes = k / 8;
      byte[] result = new byte[kBytes];
      byte[] fillingKey = this.encrypt(nFoldedUsage, key);
      int pos = 0;

      for(int i = 0; i < kBytes; ++i) {
         if (pos < fillingKey.length) {
            result[i] = fillingKey[pos];
            ++pos;
         } else {
            fillingKey = this.encrypt(fillingKey, key);
            pos = 0;
            result[i] = fillingKey[pos];
            ++pos;
         }
      }

      return result;
   }

   protected byte[] getRandomBytes(int size) {
      byte[] bytes = new byte[size];
      random.nextBytes(bytes);
      return bytes;
   }

   protected byte[] padString(byte[] encodedString) {
      int x;
      if (encodedString.length < 8) {
         x = encodedString.length;
      } else {
         x = encodedString.length % 8;
      }

      if (x == 0) {
         return encodedString;
      } else {
         byte[] paddedByteArray = new byte[8 - x + encodedString.length];

         for(int y = paddedByteArray.length - 1; y > encodedString.length - 1; --y) {
            paddedByteArray[y] = 0;
         }

         System.arraycopy(encodedString, 0, paddedByteArray, 0, encodedString.length);
         return paddedByteArray;
      }
   }

   protected byte[] concatenateBytes(byte[] array1, byte[] array2) {
      int l1 = array1.length;
      int l2 = array2.length;
      byte[] concatenatedBytes = new byte[l1 + l2];
      System.arraycopy(array1, 0, concatenatedBytes, 0, l1);
      System.arraycopy(array2, 0, concatenatedBytes, l1, l2);
      return concatenatedBytes;
   }

   protected byte[] removeLeadingBytes(byte[] array, int confounder, int checksum) {
      byte[] lessBytes = new byte[array.length - confounder - checksum];
      int j = 0;

      for(int i = confounder + checksum; i < array.length; ++i) {
         lessBytes[j] = array[i];
         ++j;
      }

      return lessBytes;
   }

   protected byte[] removeTrailingBytes(byte[] array, int confounder, int checksum) {
      byte[] lessBytes = new byte[array.length - confounder - checksum];
      int j = 0;

      for(int i = 0; i < array.length - confounder - checksum; ++i) {
         lessBytes[j] = array[i];
         ++j;
      }

      return lessBytes;
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

   protected byte[] getUsageKc(KeyUsage usage) {
      return this.getUsage(usage.getOrdinal(), (byte)-103);
   }

   protected byte[] getUsageKe(KeyUsage usage) {
      return this.getUsage(usage.getOrdinal(), (byte)-86);
   }

   protected byte[] getUsageKi(KeyUsage usage) {
      return this.getUsage(usage.getOrdinal(), (byte)85);
   }

   private byte[] getUsage(int usage, byte constant) {
      byte[] bytes = new byte[5];
      bytes[0] = (byte)(usage >>> 24 & 255);
      bytes[1] = (byte)(usage >> 16 & 255);
      bytes[2] = (byte)(usage >> 8 & 255);
      bytes[3] = (byte)(usage & 255);
      bytes[4] = constant;
      return bytes;
   }
}
