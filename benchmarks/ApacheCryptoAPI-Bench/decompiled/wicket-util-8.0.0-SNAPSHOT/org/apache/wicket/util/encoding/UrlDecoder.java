package org.apache.wicket.util.encoding;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlDecoder {
   private static final Logger LOG = LoggerFactory.getLogger(UrlDecoder.class);
   private final boolean decodePlus;
   public static final UrlDecoder QUERY_INSTANCE = new UrlDecoder(true);
   public static final UrlDecoder PATH_INSTANCE = new UrlDecoder(false);

   private UrlDecoder(boolean decodePlus) {
      this.decodePlus = decodePlus;
   }

   public String decode(String s, Charset enc) {
      return this.decode(s, enc.name());
   }

   public String decode(String s, String enc) {
      if (Strings.isEmpty(s)) {
         return s;
      } else {
         int numChars = s.length();
         StringBuilder sb = new StringBuilder(numChars > 500 ? numChars / 2 : numChars);
         int i = 0;
         if (enc.length() == 0) {
            throw new RuntimeException(new UnsupportedEncodingException("URLDecoder: empty string enc parameter"));
         } else {
            byte[] bytes = null;

            while(i < numChars) {
               char c = s.charAt(i);
               switch (c) {
                  case '%':
                     try {
                        if (bytes == null) {
                           bytes = new byte[(numChars - i) / 3];
                        }

                        int pos = 0;

                        while(i + 2 < numChars && c == '%') {
                           bytes[pos++] = (byte)Integer.parseInt(s.substring(i + 1, i + 3), 16);
                           i += 3;
                           if (i < numChars) {
                              c = s.charAt(i);
                           }
                        }

                        if (i < numChars && c == '%') {
                           LOG.info("Incomplete trailing escape (%) pattern in '%s'. The escape character (%) will be ignored.", s);
                           ++i;
                        } else {
                           try {
                              sb.append(new String(bytes, 0, pos, enc));
                           } catch (UnsupportedEncodingException e) {
                              throw new RuntimeException(e);
                           }
                        }
                     } catch (NumberFormatException e) {
                        LOG.info("Illegal hex characters in escape (%) pattern in '{}'. The escape character (%) will be ignored. NumberFormatException: {} ", s, e.getMessage());
                        ++i;
                     }
                     break;
                  case '+':
                     sb.append((char)(this.decodePlus ? ' ' : '+'));
                     ++i;
                     break;
                  default:
                     sb.append(c);
                     ++i;
               }
            }

            return sb.toString().replace("\u0000", "NULL");
         }
      }
   }
}
