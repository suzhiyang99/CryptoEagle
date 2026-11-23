package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils {
   private static final int LONG_SIZE = 8;

   public static short readShortLE(InputStream stream) throws IOException, BufferUnderrunException {
      return (short)readUShortLE(stream);
   }

   public static short readShortBE(InputStream stream) throws IOException, BufferUnderrunException {
      return (short)readUShortBE(stream);
   }

   public static int readUShortLE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      if ((ch1 | ch2) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (ch2 << 8) + (ch1 << 0);
      }
   }

   public static int readUShortBE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      if ((ch1 | ch2) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (ch1 << 8) + (ch2 << 0);
      }
   }

   public static long readUIntLE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (long)((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0)) & 4294967295L;
      }
   }

   public static long readUIntBE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (long)((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0)) & 4294967295L;
      }
   }

   public static int readIntLE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
      }
   }

   public static int readIntBE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new BufferUnderrunException();
      } else {
         return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
      }
   }

   public static long readLongLE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      int ch5 = stream.read();
      int ch6 = stream.read();
      int ch7 = stream.read();
      int ch8 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
         throw new BufferUnderrunException();
      } else {
         return ((long)ch8 << 56) + ((long)ch7 << 48) + ((long)ch6 << 40) + ((long)ch5 << 32) + ((long)ch4 << 24) + (long)(ch3 << 16) + (long)(ch2 << 8) + (long)(ch1 << 0);
      }
   }

   public static long readLongBE(InputStream stream) throws IOException, BufferUnderrunException {
      int ch1 = stream.read();
      int ch2 = stream.read();
      int ch3 = stream.read();
      int ch4 = stream.read();
      int ch5 = stream.read();
      int ch6 = stream.read();
      int ch7 = stream.read();
      int ch8 = stream.read();
      if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
         throw new BufferUnderrunException();
      } else {
         return ((long)ch1 << 56) + ((long)ch2 << 48) + ((long)ch3 << 40) + ((long)ch4 << 32) + ((long)ch5 << 24) + (long)(ch6 << 16) + (long)(ch7 << 8) + (long)(ch8 << 0);
      }
   }

   public static long readUE7(InputStream stream) throws IOException {
      int i;
      long v;
      for(v = 0L; (i = stream.read()) >= 0; v += (long)(i & 127)) {
         v <<= 7;
         if ((i & 128) != 128) {
            v += (long)i;
            break;
         }
      }

      return v;
   }

   public static short getShortLE(byte[] data) {
      return getShortLE(data, 0);
   }

   public static short getShortLE(byte[] data, int offset) {
      return (short)getUShortLE(data, offset);
   }

   public static int getUShortLE(byte[] data) {
      return getUShortLE(data, 0);
   }

   public static int getUShortLE(byte[] data, int offset) {
      int b0 = data[offset] & 255;
      int b1 = data[offset + 1] & 255;
      return (b1 << 8) + (b0 << 0);
   }

   public static short getShortBE(byte[] data) {
      return getShortBE(data, 0);
   }

   public static short getShortBE(byte[] data, int offset) {
      return (short)getUShortBE(data, offset);
   }

   public static int getUShortBE(byte[] data) {
      return getUShortBE(data, 0);
   }

   public static int getUShortBE(byte[] data, int offset) {
      int b0 = data[offset] & 255;
      int b1 = data[offset + 1] & 255;
      return (b0 << 8) + (b1 << 0);
   }

   public static int getIntLE(byte[] data) {
      return getIntLE(data, 0);
   }

   public static int getIntLE(byte[] data, int offset) {
      int i = offset + 1;
      int b0 = data[offset] & 255;
      int b1 = data[i++] & 255;
      int b2 = data[i++] & 255;
      int b3 = data[i++] & 255;
      return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
   }

   public static int getIntBE(byte[] data) {
      return getIntBE(data, 0);
   }

   public static int getIntBE(byte[] data, int offset) {
      int i = offset + 1;
      int b0 = data[offset] & 255;
      int b1 = data[i++] & 255;
      int b2 = data[i++] & 255;
      int b3 = data[i++] & 255;
      return (b0 << 24) + (b1 << 16) + (b2 << 8) + (b3 << 0);
   }

   public static long getUIntLE(byte[] data) {
      return getUIntLE(data, 0);
   }

   public static long getUIntLE(byte[] data, int offset) {
      long retNum = (long)getIntLE(data, offset);
      return retNum & 4294967295L;
   }

   public static long getUIntBE(byte[] data) {
      return getUIntBE(data, 0);
   }

   public static long getUIntBE(byte[] data, int offset) {
      long retNum = (long)getIntBE(data, offset);
      return retNum & 4294967295L;
   }

   public static long getLongLE(byte[] data, int offset) {
      long result = 0L;

      for(int j = offset + 8 - 1; j >= offset; --j) {
         result <<= 8;
         result |= (long)(255 & data[j]);
      }

      return result;
   }

   public static int ubyteToInt(byte b) {
      return b & 255;
   }

   public static short getUByte(byte[] data, int offset) {
      return (short)(data[offset] & 255);
   }

   public static class BufferUnderrunException extends TikaException {
      private static final long serialVersionUID = 8358288231138076276L;

      public BufferUnderrunException() {
         super("Insufficient data left in stream for required read");
      }
   }
}
