package org.apache.directory.shared.kerberos.flags;

import org.apache.directory.api.asn1.util.BitString;

public abstract class AbstractKerberosFlags extends BitString {
   public static final int MAX_SIZE = 32;
   protected int value;

   public AbstractKerberosFlags() {
      super(32);
      this.value = 0;
   }

   public AbstractKerberosFlags(int value) {
      super(32);
      this.setData(value);
   }

   public void setData(int value) {
      byte[] bytes = new byte[5];
      bytes[0] = 0;
      bytes[1] = (byte)(value >> 24);
      bytes[3] = (byte)(value >> 16 & 255);
      bytes[3] = (byte)(value >> 8 & 255);
      bytes[4] = (byte)(value & 255);
      super.setData(bytes);
      this.value = value;
   }

   public AbstractKerberosFlags(byte[] flags) {
      super(flags);
      if (flags != null && flags.length == 5) {
         this.value = (flags[1] & 255) << 24 | (flags[2] & 255) << 16 | (flags[3] & 255) << 8 | 255 & flags[4];
      } else {
         throw new IllegalArgumentException("The given flags is not correct");
      }
   }

   public int getIntValue() {
      return this.value;
   }

   public static boolean isFlagSet(int flags, int flag) {
      return (flags & 1 << 31 - flag) != 0;
   }

   public boolean isFlagSet(KerberosFlag flag) {
      int mask = 1 << 31 - flag.getValue();
      return (this.value & mask) != 0;
   }

   public boolean isFlagSet(int flag) {
      return (this.value & 1 << 31 - flag) != 0;
   }

   public void setFlag(KerberosFlag flag) {
      int pos = 31 - flag.getValue();
      this.setBit(flag.getValue());
      this.value |= 1 << pos;
   }

   public void setFlag(int flag) {
      int pos = 31 - flag;
      this.setBit(flag);
      this.value |= 1 << pos;
   }

   public void clearFlag(KerberosFlag flag) {
      int pos = 31 - flag.getValue();
      this.clearBit(flag.getValue());
      this.value &= ~(1 << pos);
   }

   public void clearFlag(int flag) {
      int pos = 31 - flag;
      this.clearBit(flag);
      this.value &= ~(1 << pos);
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.value;
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         AbstractKerberosFlags other = (AbstractKerberosFlags)obj;
         return this.value == other.value;
      }
   }
}
