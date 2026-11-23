package org.apache.tika.detect;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class MagicDetector implements Detector {
   private final MediaType type;
   private final int length;
   private final byte[] pattern;
   private final int patternLength;
   private final boolean isRegex;
   private final boolean isStringIgnoreCase;
   private final byte[] mask;
   private final int offsetRangeBegin;
   private final int offsetRangeEnd;

   public static MagicDetector parse(MediaType mediaType, String type, String offset, String value, String mask) {
      int start = 0;
      int end = 0;
      if (offset != null) {
         int colon = offset.indexOf(58);
         if (colon == -1) {
            start = Integer.parseInt(offset);
            end = start;
         } else {
            start = Integer.parseInt(offset.substring(0, colon));
            end = Integer.parseInt(offset.substring(colon + 1));
         }
      }

      byte[] patternBytes = decodeValue(value, type);
      byte[] maskBytes = null;
      if (mask != null) {
         maskBytes = decodeValue(mask, type);
      }

      return new MagicDetector(mediaType, patternBytes, maskBytes, type.equals("regex"), type.equals("stringignorecase"), start, end);
   }

   private static byte[] decodeValue(String value, String type) {
      if (value != null && type != null) {
         byte[] decoded = null;
         String tmpVal = null;
         int radix = 8;
         if (value.startsWith("0x")) {
            tmpVal = value.substring(2);
            radix = 16;
         } else {
            tmpVal = value;
            radix = 8;
         }

         if (!type.equals("string") && !type.equals("regex") && !type.equals("unicodeLE") && !type.equals("unicodeBE")) {
            if (type.equals("stringignorecase")) {
               decoded = decodeString(value.toLowerCase(Locale.ROOT), type);
            } else if (type.equals("byte")) {
               decoded = tmpVal.getBytes(StandardCharsets.UTF_8);
            } else if (!type.equals("host16") && !type.equals("little16")) {
               if (type.equals("big16")) {
                  int i = Integer.parseInt(tmpVal, radix);
                  decoded = new byte[]{(byte)(i >> 8), (byte)(i & 255)};
               } else if (!type.equals("host32") && !type.equals("little32")) {
                  if (type.equals("big32")) {
                     long i = Long.parseLong(tmpVal, radix);
                     decoded = new byte[]{(byte)((int)((i & -16777216L) >> 24)), (byte)((int)((i & 16711680L) >> 16)), (byte)((int)((i & 65280L) >> 8)), (byte)((int)(i & 255L))};
                  }
               } else {
                  long i = Long.parseLong(tmpVal, radix);
                  decoded = new byte[]{(byte)((int)(i & 255L)), (byte)((int)((i & 65280L) >> 8)), (byte)((int)((i & 16711680L) >> 16)), (byte)((int)((i & -16777216L) >> 24))};
               }
            } else {
               int i = Integer.parseInt(tmpVal, radix);
               decoded = new byte[]{(byte)(i & 255), (byte)(i >> 8)};
            }
         } else {
            decoded = decodeString(value, type);
         }

         return decoded;
      } else {
         return null;
      }
   }

   private static byte[] decodeString(String value, String type) {
      if (value.startsWith("0x")) {
         byte[] vals = new byte[(value.length() - 2) / 2];

         for(int i = 0; i < vals.length; ++i) {
            vals[i] = (byte)Integer.parseInt(value.substring(2 + i * 2, 4 + i * 2), 16);
         }

         return vals;
      } else {
         CharArrayWriter decoded = new CharArrayWriter();

         for(int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) != '\\') {
               decoded.write(value.charAt(i));
            } else if (value.charAt(i + 1) == '\\') {
               decoded.write(92);
               ++i;
            } else if (value.charAt(i + 1) == 'x') {
               decoded.write(Integer.parseInt(value.substring(i + 2, i + 4), 16));
               i += 3;
            } else if (value.charAt(i + 1) == 'r') {
               decoded.write(13);
               ++i;
            } else if (value.charAt(i + 1) == 'n') {
               decoded.write(10);
               ++i;
            } else {
               int j;
               for(j = i + 1; j < i + 4 && j < value.length() && Character.isDigit(value.charAt(j)); ++j) {
               }

               decoded.write(Short.decode("0" + value.substring(i + 1, j)).byteValue());
               i = j - 1;
            }
         }

         char[] chars = decoded.toCharArray();
         byte[] bytes;
         if ("unicodeLE".equals(type)) {
            bytes = new byte[chars.length * 2];

            for(int i = 0; i < chars.length; ++i) {
               bytes[i * 2] = (byte)(chars[i] & 255);
               bytes[i * 2 + 1] = (byte)(chars[i] >> 8);
            }
         } else if ("unicodeBE".equals(type)) {
            bytes = new byte[chars.length * 2];

            for(int i = 0; i < chars.length; ++i) {
               bytes[i * 2] = (byte)(chars[i] >> 8);
               bytes[i * 2 + 1] = (byte)(chars[i] & 255);
            }
         } else {
            bytes = new byte[chars.length];

            for(int i = 0; i < bytes.length; ++i) {
               bytes[i] = (byte)chars[i];
            }
         }

         return bytes;
      }
   }

   public MagicDetector(MediaType type, byte[] pattern) {
      this(type, pattern, 0);
   }

   public MagicDetector(MediaType type, byte[] pattern, int offset) {
      this(type, pattern, (byte[])null, offset, offset);
   }

   public MagicDetector(MediaType type, byte[] pattern, byte[] mask, int offsetRangeBegin, int offsetRangeEnd) {
      this(type, pattern, mask, false, offsetRangeBegin, offsetRangeEnd);
   }

   public MagicDetector(MediaType type, byte[] pattern, byte[] mask, boolean isRegex, int offsetRangeBegin, int offsetRangeEnd) {
      this(type, pattern, mask, isRegex, false, offsetRangeBegin, offsetRangeEnd);
   }

   public MagicDetector(MediaType type, byte[] pattern, byte[] mask, boolean isRegex, boolean isStringIgnoreCase, int offsetRangeBegin, int offsetRangeEnd) {
      if (type == null) {
         throw new IllegalArgumentException("Matching media type is null");
      } else if (pattern == null) {
         throw new IllegalArgumentException("Magic match pattern is null");
      } else if (offsetRangeBegin >= 0 && offsetRangeEnd >= offsetRangeBegin) {
         this.type = type;
         this.isRegex = isRegex;
         this.isStringIgnoreCase = isStringIgnoreCase;
         this.patternLength = Math.max(pattern.length, mask != null ? mask.length : 0);
         if (this.isRegex) {
            this.length = 8192;
         } else {
            this.length = this.patternLength;
         }

         this.mask = new byte[this.patternLength];
         this.pattern = new byte[this.patternLength];

         for(int i = 0; i < this.patternLength; ++i) {
            if (mask != null && i < mask.length) {
               this.mask[i] = mask[i];
            } else {
               this.mask[i] = -1;
            }

            if (i < pattern.length) {
               this.pattern[i] = (byte)(pattern[i] & this.mask[i]);
            } else {
               this.pattern[i] = 0;
            }
         }

         this.offsetRangeBegin = offsetRangeBegin;
         this.offsetRangeEnd = offsetRangeEnd;
      } else {
         throw new IllegalArgumentException("Invalid offset range: [" + offsetRangeBegin + "," + offsetRangeEnd + "]");
      }
   }

   public MediaType detect(InputStream input, Metadata metadata) throws IOException {
      if (input == null) {
         return MediaType.OCTET_STREAM;
      } else {
         input.mark(this.offsetRangeEnd + this.length);

         try {
            int offset = 0;

            while(offset < this.offsetRangeBegin) {
               long n = input.skip((long)(this.offsetRangeBegin - offset));
               if (n > 0L) {
                  offset = (int)((long)offset + n);
               } else {
                  if (input.read() == -1) {
                     MediaType var6 = MediaType.OCTET_STREAM;
                     return var6;
                  }

                  ++offset;
               }
            }

            byte[] buffer = new byte[this.length + (this.offsetRangeEnd - this.offsetRangeBegin)];
            int n = input.read(buffer);
            if (n > 0) {
               offset += n;
            }

            while(n != -1 && offset < this.offsetRangeEnd + this.length) {
               int bufferOffset = offset - this.offsetRangeBegin;
               n = input.read(buffer, bufferOffset, buffer.length - bufferOffset);
               if (n > 0) {
                  offset += n;
               }
            }

            if (this.isRegex) {
               int flags = 0;
               if (this.isStringIgnoreCase) {
                  flags = 2;
               }

               Pattern p = Pattern.compile(new String(this.pattern, StandardCharsets.UTF_8), flags);
               ByteBuffer bb = ByteBuffer.wrap(buffer);
               CharBuffer result = StandardCharsets.ISO_8859_1.decode(bb);
               Matcher m = p.matcher(result);
               boolean match = false;

               for(int i = 0; i <= this.offsetRangeEnd - this.offsetRangeBegin; ++i) {
                  m.region(i, this.length + i);
                  match = m.lookingAt();
                  if (match) {
                     MediaType var13 = this.type;
                     return var13;
                  }
               }
            } else {
               if (offset < this.offsetRangeBegin + this.length) {
                  MediaType var22 = MediaType.OCTET_STREAM;
                  return var22;
               }

               for(int i = 0; i <= this.offsetRangeEnd - this.offsetRangeBegin; ++i) {
                  boolean match = true;

                  for(int j = 0; match && j < this.length; ++j) {
                     int masked = buffer[i + j] & this.mask[j];
                     if (this.isStringIgnoreCase) {
                        masked = Character.toLowerCase(masked);
                     }

                     match = masked == this.pattern[j];
                  }

                  if (match) {
                     MediaType var26 = this.type;
                     return var26;
                  }
               }
            }

            MediaType var21 = MediaType.OCTET_STREAM;
            return var21;
         } finally {
            input.reset();
         }
      }
   }

   public int getLength() {
      return this.patternLength;
   }

   public String toString() {
      return "Magic Detection for " + this.type + " looking for " + this.pattern.length + " bytes = " + this.pattern + " mask = " + this.mask;
   }
}
