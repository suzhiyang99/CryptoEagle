package org.apache.activemq.artemis.utils;

public final class UUID {
   private static final String kHexChars = "0123456789abcdefABCDEF";
   public static final byte INDEX_CLOCK_HI = 6;
   public static final byte INDEX_CLOCK_MID = 4;
   public static final byte INDEX_CLOCK_LO = 0;
   public static final byte INDEX_TYPE = 6;
   public static final byte INDEX_CLOCK_SEQUENCE = 8;
   public static final byte INDEX_VARIATION = 8;
   public static final byte TYPE_NULL = 0;
   public static final byte TYPE_TIME_BASED = 1;
   public static final byte TYPE_DCE = 2;
   public static final byte TYPE_NAME_BASED = 3;
   public static final byte TYPE_RANDOM_BASED = 4;
   public static final String NAMESPACE_DNS = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
   public static final String NAMESPACE_URL = "6ba7b811-9dad-11d1-80b4-00c04fd430c8";
   public static final String NAMESPACE_OID = "6ba7b812-9dad-11d1-80b4-00c04fd430c8";
   public static final String NAMESPACE_X500 = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";
   private static boolean sDescCaching = true;
   private final byte[] mId;
   private transient String mDesc = null;
   private transient int mHashCode = 0;
   private static final int[] kShifts = new int[]{3, 7, 17, 21, 29, 4, 9};

   public UUID(int type, byte[] data) {
      this.mId = data;
      byte[] var10000 = this.mId;
      var10000[6] = (byte)(var10000[6] & 15);
      var10000 = this.mId;
      var10000[6] |= (byte)(type << 4);
      var10000 = this.mId;
      var10000[8] = (byte)(var10000[8] & 63);
      var10000 = this.mId;
      var10000[8] |= -128;
   }

   public byte[] asBytes() {
      return this.mId;
   }

   public int hashCode() {
      if (this.mHashCode == 0) {
         int result = this.mId[0] & 255;
         result |= result << 16;
         result |= result << 8;

         for(int i = 1; i < 15; i += 2) {
            int curr = (this.mId[i] & 255) << 8 | this.mId[i + 1] & 255;
            int shift = kShifts[i >> 1];
            if (shift > 16) {
               result ^= curr << shift | curr >>> 32 - shift;
            } else {
               result ^= curr << shift;
            }
         }

         int last = this.mId[15] & 255;
         result ^= last << 3;
         result ^= last << 13;
         result ^= last << 27;
         if (result == 0) {
            this.mHashCode = -1;
         } else {
            this.mHashCode = result;
         }
      }

      return this.mHashCode;
   }

   public String toString() {
      if (this.mDesc == null) {
         StringBuffer b = new StringBuffer(36);

         for(int i = 0; i < 16; ++i) {
            switch (i) {
               case 4:
               case 6:
               case 8:
               case 10:
                  b.append('-');
               case 5:
               case 7:
               case 9:
               default:
                  int hex = this.mId[i] & 255;
                  b.append("0123456789abcdefABCDEF".charAt(hex >> 4));
                  b.append("0123456789abcdefABCDEF".charAt(hex & 15));
            }
         }

         if (!sDescCaching) {
            return b.toString();
         }

         this.mDesc = b.toString();
      }

      return this.mDesc;
   }

   public static byte[] stringToBytes(String uuid) {
      byte[] data = new byte[16];
      int dataIdx = 0;

      try {
         int c1Bytes;
         int c2Bytes;
         for(int i = 0; i < uuid.length(); data[dataIdx++] = (byte)((c1Bytes << 4) + c2Bytes)) {
            while(uuid.charAt(i) == '-') {
               ++i;
            }

            char c1 = uuid.charAt(i);
            char c2 = uuid.charAt(i + 1);
            i += 2;
            c1Bytes = Character.digit(c1, 16);
            c2Bytes = Character.digit(c2, 16);
         }

         return data;
      } catch (RuntimeException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public boolean equals(Object o) {
      if (!(o instanceof UUID)) {
         return false;
      } else {
         byte[] otherId = ((UUID)o).mId;
         byte[] thisId = this.mId;

         for(int i = 0; i < 16; ++i) {
            if (otherId[i] != thisId[i]) {
               return false;
            }
         }

         return true;
      }
   }
}
