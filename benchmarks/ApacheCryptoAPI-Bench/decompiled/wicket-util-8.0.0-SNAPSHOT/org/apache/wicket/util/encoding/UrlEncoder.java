package org.apache.wicket.util.encoding;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
import org.apache.wicket.util.lang.Args;

public class UrlEncoder {
   protected BitSet dontNeedEncoding = new BitSet(256);
   protected static final int caseDiff = 32;
   public static final UrlEncoder QUERY_INSTANCE;
   public static final UrlEncoder PATH_INSTANCE;
   public static final UrlEncoder HEADER_INSTANCE;

   protected UrlEncoder(Type type) {
      for(int i = 97; i <= 122; ++i) {
         this.dontNeedEncoding.set(i);
      }

      for(int var3 = 65; var3 <= 90; ++var3) {
         this.dontNeedEncoding.set(var3);
      }

      for(int var4 = 48; var4 <= 57; ++var4) {
         this.dontNeedEncoding.set(var4);
      }

      this.dontNeedEncoding.set(45);
      this.dontNeedEncoding.set(46);
      this.dontNeedEncoding.set(95);
      this.dontNeedEncoding.set(126);
      this.dontNeedEncoding.set(33);
      this.dontNeedEncoding.set(36);
      switch (type) {
         case QUERY:
            this.dontNeedEncoding.set(32);
            this.dontNeedEncoding.set(42);
            this.dontNeedEncoding.set(47);
            this.dontNeedEncoding.set(44);
            this.dontNeedEncoding.set(58);
            this.dontNeedEncoding.set(64);
            break;
         case PATH:
            this.dontNeedEncoding.set(42);
            this.dontNeedEncoding.set(38);
            this.dontNeedEncoding.set(43);
            this.dontNeedEncoding.set(44);
            this.dontNeedEncoding.set(59);
            this.dontNeedEncoding.set(61);
            this.dontNeedEncoding.set(58);
            this.dontNeedEncoding.set(64);
            break;
         case HEADER:
            this.dontNeedEncoding.set(35);
            this.dontNeedEncoding.set(38);
            this.dontNeedEncoding.set(43);
            this.dontNeedEncoding.set(94);
            this.dontNeedEncoding.set(96);
            this.dontNeedEncoding.set(124);
      }

   }

   public String encode(String s, Charset charset) {
      return this.encode(s, charset.name());
   }

   public String encode(String unsafeInput, String charsetName) {
      String s = unsafeInput.replace("\u0000", "NULL");
      StringBuilder out = new StringBuilder(s.length());
      CharArrayWriter charArrayWriter = new CharArrayWriter();
      Args.notNull(charsetName, "charsetName");

      Charset charset;
      try {
         charset = Charset.forName(charsetName);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var16) {
         throw new RuntimeException(new UnsupportedEncodingException(charsetName));
      }

      int i = 0;

      while(i < s.length()) {
         int c = s.charAt(i);
         if (this.dontNeedEncoding.get(c)) {
            if (c == 32) {
               c = 43;
            }

            out.append((char)c);
            ++i;
         } else {
            do {
               charArrayWriter.write(c);
               if (c >= 55296 && c <= 56319 && i + 1 < s.length()) {
                  int d = s.charAt(i + 1);
                  if (d >= 56320 && d <= 57343) {
                     charArrayWriter.write(d);
                     ++i;
                  }
               }

               ++i;
            } while(i < s.length() && !this.dontNeedEncoding.get(c = s.charAt(i)));

            charArrayWriter.flush();
            String str = new String(charArrayWriter.toCharArray());
            byte[] ba = str.getBytes(charset);

            for(byte b : ba) {
               out.append('%');
               char ch = Character.forDigit(b >> 4 & 15, 16);
               if (Character.isLetter(ch)) {
                  ch = (char)(ch - 32);
               }

               out.append(ch);
               ch = Character.forDigit(b & 15, 16);
               if (Character.isLetter(ch)) {
                  ch = (char)(ch - 32);
               }

               out.append(ch);
            }

            charArrayWriter.reset();
         }
      }

      return out.toString();
   }

   static {
      QUERY_INSTANCE = new UrlEncoder(UrlEncoder.Type.QUERY);
      PATH_INSTANCE = new UrlEncoder(UrlEncoder.Type.PATH);
      HEADER_INSTANCE = new UrlEncoder(UrlEncoder.Type.HEADER);
   }

   public static enum Type {
      QUERY,
      PATH,
      HEADER;
   }
}
