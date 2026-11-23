package org.apache.activemq.artemis.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.logs.ActiveMQUtilBundle;
import org.jboss.logging.Logger;

public class ByteUtil {
   public static final String NON_ASCII_STRING = "@@@@@";
   private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
   private static final String prefix = "^\\s*(\\d+)\\s*";
   private static final String suffix = "(b)?\\s*$";
   private static final Pattern ONE = Pattern.compile("^\\s*(\\d+)\\s*(b)?\\s*$", 2);
   private static final Pattern KILO = Pattern.compile("^\\s*(\\d+)\\s*k(b)?\\s*$", 2);
   private static final Pattern MEGA = Pattern.compile("^\\s*(\\d+)\\s*m(b)?\\s*$", 2);
   private static final Pattern GIGA = Pattern.compile("^\\s*(\\d+)\\s*g(b)?\\s*$", 2);

   public static void debugFrame(Logger logger, String message, ByteBuf byteIn) {
      if (logger.isTraceEnabled()) {
         int location = byteIn.readerIndex();
         byte[] frame = new byte[byteIn.writerIndex()];
         byteIn.readBytes(frame);

         try {
            logger.trace(message + "\n" + formatGroup(bytesToHex(frame), 8, 16));
         } catch (Exception e) {
            logger.warn(e.getMessage(), e);
         }

         byteIn.readerIndex(location);
      }

   }

   public static String formatGroup(String str, int groupSize, int lineBreak) {
      StringBuffer buffer = new StringBuffer();
      int line = 1;
      buffer.append("/*  1 */ \"");

      for(int i = 0; i < str.length(); i += groupSize) {
         buffer.append(str.substring(i, i + Math.min(str.length() - i, groupSize)));
         if ((i + groupSize) % lineBreak == 0) {
            buffer.append("\" +\n/* ");
            ++line;
            if (line < 10) {
               buffer.append(" ");
            }

            buffer.append(Integer.toString(line) + " */ \"");
         } else if ((i + groupSize) % groupSize == 0 && str.length() - i > groupSize) {
            buffer.append("\" + \"");
         }
      }

      buffer.append("\";");
      return buffer.toString();
   }

   public static String maxString(String value, int size) {
      return value.length() < size ? value : value.substring(0, size / 2) + " ... " + value.substring(value.length() - size / 2);
   }

   public static String bytesToHex(byte[] bytes) {
      char[] hexChars = new char[bytes.length * 2];

      for(int j = 0; j < bytes.length; ++j) {
         int v = bytes[j] & 255;
         hexChars[j * 2] = hexArray[v >>> 4];
         hexChars[j * 2 + 1] = hexArray[v & 15];
      }

      return new String(hexChars);
   }

   public static String bytesToHex(byte[] bytes, int groupSize) {
      if (bytes == null) {
         return "NULL";
      } else if (bytes.length == 0) {
         return "[]";
      } else {
         char[] hexChars = new char[bytes.length * 2 + numberOfGroups(bytes, groupSize)];
         int outPos = 0;

         for(int j = 0; j < bytes.length; ++j) {
            if (j > 0 && j % groupSize == 0) {
               hexChars[outPos++] = ' ';
            }

            int v = bytes[j] & 255;
            hexChars[outPos++] = hexArray[v >>> 4];
            hexChars[outPos++] = hexArray[v & 15];
         }

         return new String(hexChars);
      }
   }

   public static String toSimpleString(byte[] bytes) {
      SimpleString simpleString = new SimpleString(bytes);
      String value = simpleString.toString();

      for(char c : value.toCharArray()) {
         if (c < ' ' || c > 127) {
            return "@@@@@";
         }
      }

      return value;
   }

   private static int numberOfGroups(byte[] bytes, int groupSize) {
      int groups = bytes.length / groupSize;
      if (bytes.length % groupSize == 0) {
         --groups;
      }

      return groups;
   }

   public static byte[] longToBytes(long x) {
      ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer(8, 8);
      buffer.writeLong(x);
      return buffer.array();
   }

   public static byte[] hexToBytes(String hexStr) {
      byte[] bytes = new byte[hexStr.length() / 2];

      for(int i = 0; i < bytes.length; ++i) {
         bytes[i] = (byte)Integer.parseInt(hexStr.substring(2 * i, 2 * i + 2), 16);
      }

      return bytes;
   }

   public static String readLine(ActiveMQBuffer buffer) {
      StringBuilder sb = new StringBuilder("");

      for(char c = buffer.readChar(); c != '\n'; c = buffer.readChar()) {
         sb.append(c);
      }

      return sb.toString();
   }

   public static byte[] getActiveArray(ByteBuffer buffer) {
      byte[] ret = new byte[buffer.remaining()];
      if (buffer.hasArray()) {
         byte[] array = buffer.array();
         System.arraycopy(array, buffer.arrayOffset() + buffer.position(), ret, 0, ret.length);
      } else {
         buffer.slice().get(ret);
      }

      return ret;
   }

   public static long convertTextBytes(String text) {
      try {
         Matcher m = ONE.matcher(text);
         if (m.matches()) {
            return Long.valueOf(Long.parseLong(m.group(1)));
         } else {
            m = KILO.matcher(text);
            if (m.matches()) {
               return Long.valueOf(Long.parseLong(m.group(1)) * 1024L);
            } else {
               m = MEGA.matcher(text);
               if (m.matches()) {
                  return Long.valueOf(Long.parseLong(m.group(1)) * 1024L * 1024L);
               } else {
                  m = GIGA.matcher(text);
                  return m.matches() ? Long.valueOf(Long.parseLong(m.group(1)) * 1024L * 1024L * 1024L) : Long.parseLong(text);
               }
            }
         }
      } catch (NumberFormatException var2) {
         throw ActiveMQUtilBundle.BUNDLE.failedToParseLong(text);
      }
   }
}
