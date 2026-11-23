package org.apache.directory.server.kerberos.shared.crypto.encryption;

public class NFold {
   public static byte[] nFold(int n, byte[] data) {
      int k = data.length * 8;
      int lcm = getLcm(n, k);
      int replicate = lcm / k;
      byte[] sumBytes = new byte[lcm / 8];

      for(int i = 0; i < replicate; ++i) {
         int rotation = 13 * i;
         byte[] temp = rotateRight(data, data.length * 8, rotation);

         for(int j = 0; j < temp.length; ++j) {
            sumBytes[j + i * temp.length] = temp[j];
         }
      }

      byte[] sum = new byte[n / 8];
      byte[] nfold = new byte[n / 8];

      for(int m = 0; m < lcm / n; ++m) {
         for(int o = 0; o < n / 8; ++o) {
            sum[o] = sumBytes[o + m * n / 8];
         }

         nfold = sum(nfold, sum, nfold.length * 8);
      }

      return nfold;
   }

   protected static int getLcm(int n1, int n2) {
      int product = n1 * n2;

      do {
         if (n1 < n2) {
            int temp = n1;
            n1 = n2;
            n2 = temp;
         }

         n1 %= n2;
      } while(n1 != 0);

      return product / n2;
   }

   private static byte[] rotateRight(byte[] in, int len, int step) {
      int numOfBytes = (len - 1) / 8 + 1;
      byte[] out = new byte[numOfBytes];

      for(int i = 0; i < len; ++i) {
         int val = getBit(in, i);
         setBit(out, (i + step) % len, val);
      }

      return out;
   }

   protected static byte[] sum(byte[] n1, byte[] n2, int len) {
      int numOfBytes = (len - 1) / 8 + 1;
      byte[] out = new byte[numOfBytes];
      int carry = 0;

      for(int i = len - 1; i > -1; --i) {
         int n1b = getBit(n1, i);
         int n2b = getBit(n2, i);
         int sum = n1b + n2b + carry;
         if (sum != 0 && sum != 1) {
            if (sum == 2) {
               carry = 1;
            } else if (sum == 3) {
               setBit(out, i, 1);
               carry = 1;
            }
         } else {
            setBit(out, i, sum);
            carry = 0;
         }
      }

      if (carry == 1) {
         byte[] carryArray = new byte[n1.length];
         carryArray[carryArray.length - 1] = 1;
         out = sum(out, carryArray, n1.length * 8);
      }

      return out;
   }

   private static int getBit(byte[] data, int pos) {
      int posByte = pos / 8;
      int posBit = pos % 8;
      byte valByte = data[posByte];
      int valInt = valByte >> 8 - (posBit + 1) & 1;
      return valInt;
   }

   private static void setBit(byte[] data, int pos, int val) {
      int posByte = pos / 8;
      int posBit = pos % 8;
      byte oldByte = data[posByte];
      oldByte = (byte)('ｿ' >> posBit & oldByte & 255);
      byte newByte = (byte)(val << 8 - (posBit + 1) | oldByte);
      data[posByte] = newByte;
   }
}
