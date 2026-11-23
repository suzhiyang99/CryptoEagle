package org.apache.activemq.artemis.utils;

import io.netty.buffer.ByteBuf;
import java.lang.ref.SoftReference;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.logs.ActiveMQUtilBundle;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger;

public final class UTF8Util {
   private static final boolean isTrace;
   private static final ThreadLocal<SoftReference<StringUtilBuffer>> currenBuffer;

   private UTF8Util() {
   }

   public static void writeNullableString(ByteBuf buffer, String val) {
      if (val == null) {
         buffer.writeByte(0);
      } else {
         buffer.writeByte(1);
         writeString(buffer, val);
      }

   }

   public static void writeString(ByteBuf buffer, String val) {
      int length = val.length();
      buffer.writeInt(length);
      if (length < 9) {
         for(int i = 0; i < val.length(); ++i) {
            buffer.writeShort((short)val.charAt(i));
         }
      } else if (length < 4095) {
         saveUTF(buffer, val);
      } else {
         SimpleString.writeSimpleString(buffer, new SimpleString(val));
      }

   }

   public static void saveUTF(ByteBuf out, String str) {
      StringUtilBuffer buffer = getThreadLocalBuffer();
      if (str.length() > 65535) {
         throw ActiveMQUtilBundle.BUNDLE.stringTooLong(str.length());
      } else {
         int len = calculateUTFSize(str, buffer);
         if (len > 65535) {
            throw ActiveMQUtilBundle.BUNDLE.stringTooLong(len);
         } else {
            out.writeShort((short)len);
            if (len > buffer.byteBuffer.length) {
               buffer.resizeByteBuffer(len);
            }

            if ((long)len == (long)str.length()) {
               for(int byteLocation = 0; byteLocation < len; ++byteLocation) {
                  buffer.byteBuffer[byteLocation] = (byte)buffer.charBuffer[byteLocation];
               }

               out.writeBytes(buffer.byteBuffer, 0, len);
            } else {
               if (isTrace) {
                  ActiveMQUtilLogger.LOGGER.trace("Saving string with utfSize=" + len + " stringSize=" + str.length());
               }

               int stringLength = str.length();
               int charCount = 0;

               for(int i = 0; i < stringLength; ++i) {
                  char charAtPos = buffer.charBuffer[i];
                  if (charAtPos >= 1 && charAtPos < 127) {
                     buffer.byteBuffer[charCount++] = (byte)charAtPos;
                  } else if (charAtPos >= 2048) {
                     buffer.byteBuffer[charCount++] = (byte)(224 | charAtPos >> 12 & 15);
                     buffer.byteBuffer[charCount++] = (byte)(128 | charAtPos >> 6 & 63);
                     buffer.byteBuffer[charCount++] = (byte)(128 | charAtPos >> 0 & 63);
                  } else {
                     buffer.byteBuffer[charCount++] = (byte)(192 | charAtPos >> 6 & 31);
                     buffer.byteBuffer[charCount++] = (byte)(128 | charAtPos >> 0 & 63);
                  }
               }

               out.writeBytes(buffer.byteBuffer, 0, len);
            }

         }
      }
   }

   public static String readUTF(ActiveMQBuffer input) {
      StringUtilBuffer buffer = getThreadLocalBuffer();
      int size = input.readUnsignedShort();
      if (size > buffer.byteBuffer.length) {
         buffer.resizeByteBuffer(size);
      }

      if (size > buffer.charBuffer.length) {
         buffer.resizeCharBuffer(size);
      }

      if (isTrace) {
         ActiveMQUtilLogger.LOGGER.trace("Reading string with utfSize=" + size);
      }

      int count = 0;
      int charCount = 0;
      input.readBytes((byte[])buffer.byteBuffer, 0, size);

      while(count < size) {
         int byte1 = buffer.byteBuffer[count++];
         if (byte1 > 0 && byte1 <= 127) {
            buffer.charBuffer[charCount++] = (char)byte1;
         } else {
            int c = byte1 & 255;
            switch (c >> 4) {
               case 12:
               case 13:
                  int byte2 = buffer.byteBuffer[count++];
                  buffer.charBuffer[charCount++] = (char)((c & 31) << 6 | byte2 & 63);
                  break;
               case 14:
                  int byte2 = buffer.byteBuffer[count++];
                  int byte3 = buffer.byteBuffer[count++];
                  buffer.charBuffer[charCount++] = (char)((c & 15) << 12 | (byte2 & 63) << 6 | (byte3 & 63) << 0);
                  break;
               default:
                  throw new InternalError("unhandled utf8 byte " + c);
            }
         }
      }

      return new String(buffer.charBuffer, 0, charCount);
   }

   public static StringUtilBuffer getThreadLocalBuffer() {
      SoftReference<StringUtilBuffer> softReference = (SoftReference)currenBuffer.get();
      StringUtilBuffer value;
      if (softReference == null) {
         value = new StringUtilBuffer();
         softReference = new SoftReference(value);
         currenBuffer.set(softReference);
      } else {
         value = (StringUtilBuffer)softReference.get();
      }

      if (value == null) {
         value = new StringUtilBuffer();
         softReference = new SoftReference(value);
         currenBuffer.set(softReference);
      }

      return value;
   }

   public static void clearBuffer() {
      SoftReference<StringUtilBuffer> ref = (SoftReference)currenBuffer.get();
      if (ref.get() != null) {
         ref.clear();
      }

   }

   public static int calculateUTFSize(String str, StringUtilBuffer stringBuffer) {
      int calculatedLen = 0;
      int stringLength = str.length();
      if (stringLength > stringBuffer.charBuffer.length) {
         stringBuffer.resizeCharBuffer(stringLength);
      }

      str.getChars(0, stringLength, stringBuffer.charBuffer, 0);

      for(int i = 0; i < stringLength; ++i) {
         char c = stringBuffer.charBuffer[i];
         if (c >= 1 && c < 127) {
            ++calculatedLen;
         } else if (c >= 2048) {
            calculatedLen += 3;
         } else {
            calculatedLen += 2;
         }
      }

      return calculatedLen;
   }

   static {
      isTrace = ActiveMQUtilLogger.LOGGER.isTraceEnabled();
      currenBuffer = new ThreadLocal();
   }

   public static class StringUtilBuffer {
      public char[] charBuffer;
      public byte[] byteBuffer;

      public void resizeCharBuffer(int newSize) {
         if (newSize > this.charBuffer.length) {
            this.charBuffer = new char[newSize];
         }

      }

      public void resizeByteBuffer(int newSize) {
         if (newSize > this.byteBuffer.length) {
            this.byteBuffer = new byte[newSize];
         }

      }

      public StringUtilBuffer() {
         this(1024, 1024);
      }

      public StringUtilBuffer(int sizeChar, int sizeByte) {
         this.charBuffer = new char[sizeChar];
         this.byteBuffer = new byte[sizeByte];
      }
   }
}
