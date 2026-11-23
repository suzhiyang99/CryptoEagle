package org.apache.directory.shared.kerberos.codec.options;

import org.apache.directory.shared.kerberos.flags.AbstractKerberosFlags;

public abstract class Options extends AbstractKerberosFlags {
   protected Options(int maxSize) {
      super(maxSize);
   }

   public boolean match(Options options, int option) {
      return options.get(option) == this.get(option);
   }

   public boolean get(int index) {
      if (index >= 0 && index < this.size()) {
         return super.getBit(index);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void set(int index) {
      if (index >= 0 && index < this.size()) {
         this.setBit(index);
      }
   }

   public void clear(int index) {
      if (index >= 0 && index < this.size()) {
         this.clearBit(index);
      }
   }

   public byte[] getBytes() {
      return super.getData();
   }

   protected void setBytes(byte[] bytes) {
      super.setData(bytes);
   }

   public String toString() {
      return super.toString();
   }
}
