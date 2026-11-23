package org.apache.activemq.artemis.api.core;

import io.netty.buffer.ByteBuf;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class SimpleString implements CharSequence, Serializable, Comparable<SimpleString> {
   private static final long serialVersionUID = 4204223851422244307L;
   private final byte[] data;
   private transient int hash;
   private transient String str;

   public static SimpleString toSimpleString(String string) {
      return string == null ? null : new SimpleString(string);
   }

   public SimpleString(String string) {
      int len = string.length();
      this.data = new byte[len << 1];
      int j = 0;

      for(int i = 0; i < len; ++i) {
         char c = string.charAt(i);
         byte low = (byte)(c & 255);
         this.data[j++] = low;
         byte high = (byte)(c >> 8 & 255);
         this.data[j++] = high;
      }

      this.str = string;
   }

   public SimpleString(byte[] data) {
      this.data = data;
   }

   public SimpleString(char c) {
      this.data = new byte[2];
      byte low = (byte)(c & 255);
      this.data[0] = low;
      byte high = (byte)(c >> 8 & 255);
      this.data[1] = high;
   }

   public int length() {
      return this.data.length >> 1;
   }

   public char charAt(int pos) {
      if (pos >= 0 && pos < this.data.length >> 1) {
         pos <<= 1;
         return (char)(this.data[pos] & 255 | this.data[pos + 1] << 8 & '\uff00');
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public CharSequence subSequence(int start, int end) {
      return this.subSeq(start, end);
   }

   public static SimpleString readNullableSimpleString(ByteBuf buffer) {
      int b = buffer.readByte();
      return b == 0 ? null : readSimpleString(buffer);
   }

   public static SimpleString readSimpleString(ByteBuf buffer) {
      int len = buffer.readInt();
      if (len > buffer.readableBytes()) {
         throw new IndexOutOfBoundsException();
      } else {
         byte[] data = new byte[len];
         buffer.readBytes(data);
         return new SimpleString(data);
      }
   }

   public static void writeNullableSimpleString(ByteBuf buffer, SimpleString val) {
      if (val == null) {
         buffer.writeByte(0);
      } else {
         buffer.writeByte(1);
         writeSimpleString(buffer, val);
      }

   }

   public static void writeSimpleString(ByteBuf buffer, SimpleString val) {
      byte[] data = val.getData();
      buffer.writeInt(data.length);
      buffer.writeBytes(data);
   }

   public SimpleString subSeq(int start, int end) {
      int len = this.data.length >> 1;
      if (end >= start && start >= 0 && end <= len) {
         int newlen = end - start << 1;
         byte[] bytes = new byte[newlen];
         System.arraycopy(this.data, start << 1, bytes, 0, newlen);
         return new SimpleString(bytes);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int compareTo(SimpleString o) {
      return this.toString().compareTo(o.toString());
   }

   public byte[] getData() {
      return this.data;
   }

   public boolean startsWith(SimpleString other) {
      byte[] otherdata = other.data;
      if (otherdata.length > this.data.length) {
         return false;
      } else {
         for(int i = 0; i < otherdata.length; ++i) {
            if (this.data[i] != otherdata[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public String toString() {
      if (this.str == null) {
         int len = this.data.length >> 1;
         char[] chars = new char[len];
         int j = 0;

         for(int i = 0; i < len; ++i) {
            int low = this.data[j++] & 255;
            int high = this.data[j++] << 8 & '\uff00';
            chars[i] = (char)(low | high);
         }

         this.str = new String(chars);
      }

      return this.str;
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof SimpleString) {
         SimpleString s = (SimpleString)other;
         if (this.data.length != s.data.length) {
            return false;
         } else {
            for(int i = 0; i < this.data.length; ++i) {
               if (this.data[i] != s.data[i]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      if (this.hash == 0) {
         int tmphash = 0;

         for(byte element : this.data) {
            tmphash = (tmphash << 5) - tmphash + element;
         }

         this.hash = tmphash;
      }

      return this.hash;
   }

   public SimpleString[] split(char delim) {
      List<SimpleString> all = null;
      byte low = (byte)(delim & 255);
      byte high = (byte)(delim >> 8 & 255);
      int lasPos = 0;

      for(int i = 0; i < this.data.length; i += 2) {
         if (this.data[i] == low && this.data[i + 1] == high) {
            byte[] bytes = new byte[i - lasPos];
            System.arraycopy(this.data, lasPos, bytes, 0, bytes.length);
            lasPos = i + 2;
            if (all == null) {
               all = new ArrayList(2);
            }

            all.add(new SimpleString(bytes));
         }
      }

      if (all == null) {
         return new SimpleString[]{this};
      } else {
         byte[] bytes = new byte[this.data.length - lasPos];
         System.arraycopy(this.data, lasPos, bytes, 0, bytes.length);
         all.add(new SimpleString(bytes));
         SimpleString[] parts = new SimpleString[all.size()];
         return (SimpleString[])all.toArray(parts);
      }
   }

   public boolean contains(char c) {
      byte low = (byte)(c & 255);
      byte high = (byte)(c >> 8 & 255);

      for(int i = 0; i < this.data.length; i += 2) {
         if (this.data[i] == low && this.data[i + 1] == high) {
            return true;
         }
      }

      return false;
   }

   public SimpleString concat(String toAdd) {
      return this.concat(new SimpleString(toAdd));
   }

   public SimpleString concat(SimpleString toAdd) {
      byte[] bytes = new byte[this.data.length + toAdd.getData().length];
      System.arraycopy(this.data, 0, bytes, 0, this.data.length);
      System.arraycopy(toAdd.getData(), 0, bytes, this.data.length, toAdd.getData().length);
      return new SimpleString(bytes);
   }

   public SimpleString concat(char c) {
      byte[] bytes = new byte[this.data.length + 2];
      System.arraycopy(this.data, 0, bytes, 0, this.data.length);
      bytes[this.data.length] = (byte)(c & 255);
      bytes[this.data.length + 1] = (byte)(c >> 8 & 255);
      return new SimpleString(bytes);
   }

   public int sizeof() {
      return 4 + this.data.length;
   }

   public static int sizeofString(SimpleString str) {
      return str.sizeof();
   }

   public static int sizeofNullableString(SimpleString str) {
      return str == null ? 1 : 1 + str.sizeof();
   }

   public void getChars(int srcBegin, int srcEnd, char[] dst, int dstPos) {
      if (srcBegin < 0) {
         throw new StringIndexOutOfBoundsException(srcBegin);
      } else if (srcEnd > this.length()) {
         throw new StringIndexOutOfBoundsException(srcEnd);
      } else if (srcBegin > srcEnd) {
         throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
      } else {
         int j = srcBegin * 2;
         int d = dstPos;

         for(int i = srcBegin; i < srcEnd; ++i) {
            int low = this.data[j++] & 255;
            int high = this.data[j++] << 8 & '\uff00';
            dst[d++] = (char)(low | high);
         }

      }
   }
}
