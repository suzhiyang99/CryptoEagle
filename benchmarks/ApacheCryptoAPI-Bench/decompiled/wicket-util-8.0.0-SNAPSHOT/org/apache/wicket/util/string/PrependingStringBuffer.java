package org.apache.wicket.util.string;

public class PrependingStringBuffer {
   private int size;
   private int position;
   private char[] buffer;

   public PrependingStringBuffer() {
      this(16);
   }

   public PrependingStringBuffer(int size) {
      this.buffer = new char[size];
      this.position = size;
      this.size = 0;
   }

   public PrependingStringBuffer(String start) {
      this(start.length() + 16);
      this.prepend(start);
   }

   public PrependingStringBuffer prepend(char ch) {
      int len = 1;
      if (this.position < len) {
         this.expandCapacity(this.size + len);
      }

      this.position -= len;
      this.buffer[this.position] = ch;
      this.size += len;
      return this;
   }

   public PrependingStringBuffer prepend(String str) {
      int len = str.length();
      if (this.position < len) {
         this.expandCapacity(this.size + len);
      }

      str.getChars(0, len, this.buffer, this.position - len);
      this.position -= len;
      this.size += len;
      return this;
   }

   private void expandCapacity(int minimumCapacity) {
      int newCapacity = (this.buffer.length + 1) * 2;
      if (newCapacity < 0) {
         newCapacity = Integer.MAX_VALUE;
      } else if (minimumCapacity > newCapacity) {
         newCapacity = minimumCapacity;
      }

      char[] newValue = new char[newCapacity];
      System.arraycopy(this.buffer, this.position, newValue, newCapacity - this.size, this.size);
      this.buffer = newValue;
      this.position = newCapacity - this.size;
   }

   public int length() {
      return this.size;
   }

   public String toString() {
      return new String(this.buffer, this.position, this.size);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return obj == null ? false : this.toString().equals(obj.toString());
      }
   }

   public int hashCode() {
      return this.toString().hashCode();
   }
}
