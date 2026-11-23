package org.apache.directory.shared.kerberos.codec.options;

public class KdcOptions extends Options {
   public static final int RESERVED_0 = 0;
   public static final int FORWARDABLE = 1;
   public static final int FORWARDED = 2;
   public static final int PROXIABLE = 3;
   public static final int PROXY = 4;
   public static final int ALLOW_POSTDATE = 5;
   public static final int POSTDATED = 6;
   public static final int RESERVED_7 = 7;
   public static final int RENEWABLE = 8;
   public static final int RESERVED_9 = 9;
   public static final int RESERVED_10 = 10;
   public static final int RESERVED_11 = 11;
   public static final int RESERVED_12 = 12;
   public static final int RESERVED_13 = 13;
   public static final int RESERVED_14 = 14;
   public static final int RESERVED_15 = 15;
   public static final int RESERVED_16 = 16;
   public static final int RESERVED_17 = 17;
   public static final int RESERVED_18 = 18;
   public static final int RESERVED_19 = 19;
   public static final int RESERVED_20 = 20;
   public static final int RESERVED_21 = 21;
   public static final int RESERVED_22 = 22;
   public static final int RESERVED_23 = 23;
   public static final int RESERVED_24 = 24;
   public static final int RESERVED_25 = 25;
   public static final int DISABLE_TRANSISTED_CHECKED = 26;
   public static final int RENEWABLE_OK = 27;
   public static final int ENC_TKT_IN_SKEY = 28;
   public static final int RESERVED_29 = 29;
   public static final int RENEW = 30;
   public static final int VALIDATE = 31;
   public static final int MAX_VALUE = 32;

   public KdcOptions() {
      super(32);
   }

   public KdcOptions(byte[] bytes) {
      super(32);
      this.setBytes(bytes);
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      if (this.get(0)) {
         result.append("RESERVED_0 ");
      }

      if (this.get(1)) {
         result.append("FORWARDABLE ");
      }

      if (this.get(2)) {
         result.append("FORWARDED ");
      }

      if (this.get(3)) {
         result.append("PROXIABLE ");
      }

      if (this.get(4)) {
         result.append("PROXY ");
      }

      if (this.get(5)) {
         result.append("ALLOW_POSTDATE ");
      }

      if (this.get(6)) {
         result.append("POSTDATED ");
      }

      if (this.get(7)) {
         result.append("RESERVED_7 ");
      }

      if (this.get(8)) {
         result.append("RENEWABLE ");
      }

      if (this.get(9)) {
         result.append("RESERVED_9 ");
      }

      if (this.get(10)) {
         result.append("RESERVED_10 ");
      }

      if (this.get(11)) {
         result.append("RESERVED_11 ");
      }

      if (this.get(12)) {
         result.append("RESERVED_12 ");
      }

      if (this.get(13)) {
         result.append("RESERVED_13 ");
      }

      if (this.get(14)) {
         result.append("RESERVED_14 ");
      }

      if (this.get(15)) {
         result.append("RESERVED_15 ");
      }

      if (this.get(16)) {
         result.append("RESERVED_16 ");
      }

      if (this.get(17)) {
         result.append("RESERVED_17 ");
      }

      if (this.get(18)) {
         result.append("RESERVED_18 ");
      }

      if (this.get(19)) {
         result.append("RESERVED_19 ");
      }

      if (this.get(20)) {
         result.append("RESERVED_20 ");
      }

      if (this.get(21)) {
         result.append("RESERVED_21 ");
      }

      if (this.get(22)) {
         result.append("RESERVED_22 ");
      }

      if (this.get(23)) {
         result.append("RESERVED_23 ");
      }

      if (this.get(24)) {
         result.append("RESERVED_24 ");
      }

      if (this.get(25)) {
         result.append("RESERVED_25 ");
      }

      if (this.get(26)) {
         result.append("DISABLE_TRANSISTED_CHECKED ");
      }

      if (this.get(27)) {
         result.append("RENEWABLE_OK ");
      }

      if (this.get(28)) {
         result.append("ENC_TKT_IN_SKEY ");
      }

      if (this.get(29)) {
         result.append("RESERVED_29 ");
      }

      if (this.get(30)) {
         result.append("RENEW ");
      }

      if (this.get(31)) {
         result.append("VALIDATE ");
      }

      return result.toString().trim();
   }
}
