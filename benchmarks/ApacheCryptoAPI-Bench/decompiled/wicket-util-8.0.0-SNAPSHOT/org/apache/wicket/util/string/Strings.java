package org.apache.wicket.util.string;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.lang.Args;

public final class Strings {
   public static final String LINE_SEPARATOR;
   private static final char[] HEX_DIGIT = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   private static final Pattern HTML_NUMBER_REGEX = Pattern.compile("&#\\d+;");
   private static final String[] NO_STRINGS = new String[0];
   public static final String SESSION_ID_PARAM_NAME = System.getProperty("wicket.jsessionid.name", "jsessionid");
   private static final String SESSION_ID_PARAM;

   private Strings() {
   }

   public static String afterFirst(String s, char c) {
      if (s == null) {
         return null;
      } else {
         int index = s.indexOf(c);
         return index == -1 ? "" : s.substring(index + 1);
      }
   }

   public static String afterFirstPathComponent(String path, char separator) {
      return afterFirst(path, separator);
   }

   public static String afterLast(String s, char c) {
      if (s == null) {
         return null;
      } else {
         int index = s.lastIndexOf(c);
         return index == -1 ? "" : s.substring(index + 1);
      }
   }

   public static String beforeFirst(String s, char c) {
      if (s == null) {
         return null;
      } else {
         int index = s.indexOf(c);
         return index == -1 ? "" : s.substring(0, index);
      }
   }

   public static String beforeLast(String s, char c) {
      if (s == null) {
         return null;
      } else {
         int index = s.lastIndexOf(c);
         return index == -1 ? "" : s.substring(0, index);
      }
   }

   public static String beforeLastPathComponent(String path, char separator) {
      return beforeLast(path, separator);
   }

   public static String capitalize(String s) {
      if (s == null) {
         return null;
      } else {
         char[] chars = s.toCharArray();
         if (chars.length > 0) {
            chars[0] = Character.toUpperCase(chars[0]);
         }

         return new String(chars);
      }
   }

   public static CharSequence escapeMarkup(CharSequence s) {
      return escapeMarkup(s, false);
   }

   public static CharSequence escapeMarkup(CharSequence s, boolean escapeSpaces) {
      return escapeMarkup(s, escapeSpaces, false);
   }

   public static CharSequence escapeMarkup(CharSequence s, boolean escapeSpaces, boolean convertToHtmlUnicodeEscapes) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         AppendingStringBuffer buffer = new AppendingStringBuffer((int)((double)len * 1.1));

         for(int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            switch (c) {
               case '\t':
                  if (escapeSpaces) {
                     buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                  } else {
                     buffer.append(c);
                  }
                  break;
               case ' ':
                  if (escapeSpaces) {
                     buffer.append("&nbsp;");
                  } else {
                     buffer.append(c);
                  }
                  break;
               case '"':
                  buffer.append("&quot;");
                  break;
               case '&':
                  buffer.append("&amp;");
                  break;
               case '\'':
                  buffer.append("&#039;");
                  break;
               case '<':
                  buffer.append("&lt;");
                  break;
               case '>':
                  buffer.append("&gt;");
                  break;
               default:
                  int ci = '\uffff' & c;
                  if ((ci >= 32 || ci == 9 || ci == 10 || ci == 13) && (!convertToHtmlUnicodeEscapes || ci <= 159)) {
                     buffer.append(c);
                  } else {
                     buffer.append("&#");
                     buffer.append(Integer.toString(ci));
                     buffer.append(';');
                  }
            }
         }

         return buffer;
      }
   }

   public static CharSequence unescapeMarkup(String markup) {
      String unescapedMarkup = StringEscapeUtils.unescapeHtml(markup);
      return unescapedMarkup;
   }

   public static String firstPathComponent(String path, char separator) {
      if (path == null) {
         return null;
      } else {
         int index = path.indexOf(separator);
         return index == -1 ? path : path.substring(0, index);
      }
   }

   public static String fromEscapedUnicode(String escapedUnicodeString) {
      int off = 0;
      char[] in = escapedUnicodeString.toCharArray();
      int len = in.length;
      char[] out = new char[len];
      int outLen = 0;
      int end = off + len;

      while(off < end) {
         char aChar = in[off++];
         if (aChar == '\\') {
            aChar = in[off++];
            if (aChar == 'u') {
               int value = 0;

               for(int i = 0; i < 4; ++i) {
                  aChar = in[off++];
                  switch (aChar) {
                     case '0':
                     case '1':
                     case '2':
                     case '3':
                     case '4':
                     case '5':
                     case '6':
                     case '7':
                     case '8':
                     case '9':
                        value = (value << 4) + aChar - 48;
                        break;
                     case ':':
                     case ';':
                     case '<':
                     case '=':
                     case '>':
                     case '?':
                     case '@':
                     case 'G':
                     case 'H':
                     case 'I':
                     case 'J':
                     case 'K':
                     case 'L':
                     case 'M':
                     case 'N':
                     case 'O':
                     case 'P':
                     case 'Q':
                     case 'R':
                     case 'S':
                     case 'T':
                     case 'U':
                     case 'V':
                     case 'W':
                     case 'X':
                     case 'Y':
                     case 'Z':
                     case '[':
                     case '\\':
                     case ']':
                     case '^':
                     case '_':
                     case '`':
                     default:
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                     case 'A':
                     case 'B':
                     case 'C':
                     case 'D':
                     case 'E':
                     case 'F':
                        value = (value << 4) + 10 + aChar - 65;
                        break;
                     case 'a':
                     case 'b':
                     case 'c':
                     case 'd':
                     case 'e':
                     case 'f':
                        value = (value << 4) + 10 + aChar - 97;
                  }
               }

               out[outLen++] = (char)value;
            } else {
               if (aChar == 't') {
                  aChar = '\t';
               } else if (aChar == 'r') {
                  aChar = '\r';
               } else if (aChar == 'n') {
                  aChar = '\n';
               } else if (aChar == 'f') {
                  aChar = '\f';
               }

               out[outLen++] = aChar;
            }
         } else {
            out[outLen++] = aChar;
         }
      }

      return new String(out, 0, outLen);
   }

   public static boolean isEmpty(CharSequence string) {
      return string == null || string.length() == 0 || string.toString().trim().length() == 0;
   }

   public static boolean isEqual(String string1, String string2) {
      if (string1 == null && string2 == null) {
         return true;
      } else if (isEmpty(string1) && isEmpty(string2)) {
         return true;
      } else {
         return string1 != null && string2 != null ? string1.equals(string2) : false;
      }
   }

   public static boolean isTrue(String s) throws StringValueConversionException {
      if (s != null) {
         if (s.equalsIgnoreCase("true")) {
            return true;
         } else if (s.equalsIgnoreCase("false")) {
            return false;
         } else if (!s.equalsIgnoreCase("on") && !s.equalsIgnoreCase("yes") && !s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("1")) {
            if (!s.equalsIgnoreCase("off") && !s.equalsIgnoreCase("no") && !s.equalsIgnoreCase("n") && !s.equalsIgnoreCase("0")) {
               if (isEmpty(s)) {
                  return false;
               } else {
                  throw new StringValueConversionException("Boolean value \"" + s + "\" not recognized");
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static String join(String separator, List<String> fragments) {
      return fragments == null ? "" : join(separator, (String[])fragments.toArray(new String[fragments.size()]));
   }

   public static String join(String separator, String... fragments) {
      if (fragments != null && fragments.length >= 1) {
         if (fragments.length < 2) {
            return fragments[0];
         } else {
            StringBuilder buff = new StringBuilder(128);
            if (fragments[0] != null) {
               buff.append(fragments[0]);
            }

            for(int i = 1; i < fragments.length; ++i) {
               String fragment = fragments[i];
               if (fragments[i - 1] != null || fragment != null) {
                  boolean lhsClosed = fragments[i - 1].endsWith(separator);
                  boolean rhsClosed = fragment.startsWith(separator);
                  if (!isEmpty(separator) && lhsClosed && rhsClosed) {
                     buff.append(fragment.substring(1));
                  } else if (!lhsClosed && !rhsClosed) {
                     if (!isEmpty(fragment)) {
                        buff.append(separator);
                     }

                     buff.append(fragment);
                  } else {
                     buff.append(fragment);
                  }
               }
            }

            return buff.toString();
         }
      } else {
         return "";
      }
   }

   public static String lastPathComponent(String path, char separator) {
      if (path == null) {
         return null;
      } else {
         int index = path.lastIndexOf(separator);
         return index == -1 ? path : path.substring(index + 1);
      }
   }

   public static CharSequence replaceAll(CharSequence s, CharSequence searchFor, CharSequence replaceWith) {
      if (s == null) {
         return null;
      } else if (searchFor != null && !"".equals(searchFor)) {
         if (replaceWith == null) {
            replaceWith = "";
         }

         String searchString = searchFor.toString();
         int matchIndex = search(s, searchString, 0);
         if (matchIndex == -1) {
            return s;
         } else {
            int size = s.length();
            int replaceWithLength = replaceWith.length();
            int searchForLength = searchFor.length();
            if (replaceWithLength > searchForLength) {
               size += replaceWithLength - searchForLength;
            }

            AppendingStringBuffer buffer = new AppendingStringBuffer(size + 16);
            int pos = 0;

            do {
               append(buffer, s, pos, matchIndex);
               buffer.append((Object)replaceWith);
               pos = matchIndex + searchForLength;
               matchIndex = search(s, searchString, pos);
            } while(matchIndex != -1);

            buffer.append((Object)s.subSequence(pos, s.length()));
            return buffer;
         }
      } else {
         return s;
      }
   }

   public static String replaceHtmlEscapeNumber(String str) {
      if (str == null) {
         return null;
      } else {
         for(Matcher matcher = HTML_NUMBER_REGEX.matcher(str); matcher.find(); matcher = HTML_NUMBER_REGEX.matcher(str)) {
            int pos = matcher.start();
            int end = matcher.end();
            int number = Integer.parseInt(str.substring(pos + 2, end - 1));
            char ch = (char)number;
            str = str.substring(0, pos) + ch + str.substring(end);
         }

         return str;
      }
   }

   public static String[] split(String s, char c) {
      if (s != null && s.length() != 0) {
         List<String> strings = new ArrayList();
         int pos = 0;

         while(true) {
            int next = s.indexOf(c, pos);
            if (next == -1) {
               strings.add(s.substring(pos));
               String[] result = new String[strings.size()];
               strings.toArray(result);
               return result;
            }

            strings.add(s.substring(pos, next));
            pos = next + 1;
         }
      } else {
         return NO_STRINGS;
      }
   }

   public static String stripEnding(String s, String ending) {
      if (s == null) {
         return null;
      } else if (ending != null && !"".equals(ending)) {
         int endingLength = ending.length();
         int sLength = s.length();
         if (endingLength > sLength) {
            return s;
         } else {
            int index = s.lastIndexOf(ending);
            int endpos = sLength - endingLength;
            return index == endpos ? s.substring(0, endpos) : s;
         }
      } else {
         return s;
      }
   }

   public static String stripJSessionId(String url) {
      if (isEmpty(url)) {
         return url;
      } else {
         int ixSemiColon = url.toLowerCase(Locale.ENGLISH).indexOf(SESSION_ID_PARAM);
         if (ixSemiColon == -1) {
            return url;
         } else {
            int ixQuestionMark = url.indexOf(63);
            if (ixQuestionMark == -1) {
               return url.substring(0, ixSemiColon);
            } else {
               return ixQuestionMark <= ixSemiColon ? url : url.substring(0, ixSemiColon) + url.substring(ixQuestionMark);
            }
         }
      }
   }

   public static Boolean toBoolean(String s) throws StringValueConversionException {
      return isTrue(s);
   }

   public static char toChar(String s) throws StringValueConversionException {
      if (s != null) {
         if (s.length() == 1) {
            return s.charAt(0);
         } else {
            throw new StringValueConversionException("Expected single character, not \"" + s + "\"");
         }
      } else {
         throw new StringValueConversionException("Character value was null");
      }
   }

   public static String toEscapedUnicode(String unicodeString) {
      if (unicodeString != null && unicodeString.length() != 0) {
         int len = unicodeString.length();
         int bufLen = len * 2;
         StringBuilder outBuffer = new StringBuilder(bufLen);

         for(int x = 0; x < len; ++x) {
            char aChar = unicodeString.charAt(x);
            if (aChar > '=' && aChar < 127) {
               if (aChar == '\\') {
                  outBuffer.append('\\');
                  outBuffer.append('\\');
               } else {
                  outBuffer.append(aChar);
               }
            } else {
               switch (aChar) {
                  case '\t':
                     outBuffer.append('\\');
                     outBuffer.append('t');
                     break;
                  case '\n':
                     outBuffer.append('\\');
                     outBuffer.append('n');
                     break;
                  case '\f':
                     outBuffer.append('\\');
                     outBuffer.append('f');
                     break;
                  case '\r':
                     outBuffer.append('\\');
                     outBuffer.append('r');
                     break;
                  case ' ':
                     if (x == 0) {
                        outBuffer.append('\\');
                     }

                     outBuffer.append(' ');
                     break;
                  case '!':
                  case '#':
                  case ':':
                  case '=':
                     outBuffer.append('\\');
                     outBuffer.append(aChar);
                     break;
                  default:
                     if (aChar >= ' ' && aChar <= '~') {
                        outBuffer.append(aChar);
                     } else {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex(aChar >> 12 & 15));
                        outBuffer.append(toHex(aChar >> 8 & 15));
                        outBuffer.append(toHex(aChar >> 4 & 15));
                        outBuffer.append(toHex(aChar & 15));
                     }
               }
            }
         }

         return outBuffer.toString();
      } else {
         return unicodeString;
      }
   }

   public static CharSequence toMultilineMarkup(CharSequence s) {
      if (s == null) {
         return null;
      } else {
         AppendingStringBuffer buffer = new AppendingStringBuffer();
         int newlineCount = 0;
         buffer.append("<p>");

         for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
               case '\n':
                  ++newlineCount;
               case '\r':
                  break;
               default:
                  if (newlineCount == 1) {
                     buffer.append("<br/>");
                  } else if (newlineCount > 1) {
                     buffer.append("</p><p>");
                  }

                  buffer.append(c);
                  newlineCount = 0;
            }
         }

         if (newlineCount == 1) {
            buffer.append("<br/>");
         } else if (newlineCount > 1) {
            buffer.append("</p><p>");
         }

         buffer.append("</p>");
         return buffer;
      }
   }

   public static String toString(Object object) {
      if (object == null) {
         return null;
      } else if (object instanceof Throwable) {
         return toString((Throwable)object);
      } else if (object instanceof String) {
         return (String)object;
      } else {
         return object instanceof String[] && ((String[])((String[])object)).length == 1 ? ((String[])((String[])object))[0] : object.toString();
      }
   }

   public static String toString(Throwable throwable) {
      if (throwable == null) {
         return "<Null Throwable>";
      } else {
         List<Throwable> al = new ArrayList();
         Throwable cause = throwable;
         al.add(throwable);

         while(cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
            al.add(cause);
         }

         AppendingStringBuffer sb = new AppendingStringBuffer(256);
         int length = al.size() - 1;
         cause = (Throwable)al.get(length);
         if (throwable instanceof RuntimeException) {
            sb.append("Message: ");
            sb.append(throwable.getMessage());
            sb.append("\n\n");
         }

         sb.append("Root cause:\n\n");
         outputThrowable(cause, sb, false);
         if (length > 0) {
            sb.append("\n\nComplete stack:\n\n");

            for(int i = 0; i < length; ++i) {
               outputThrowable((Throwable)al.get(i), sb, true);
               sb.append('\n');
            }
         }

         return sb.toString();
      }
   }

   private static void append(AppendingStringBuffer buffer, CharSequence s, int from, int to) {
      if (s instanceof AppendingStringBuffer) {
         AppendingStringBuffer asb = (AppendingStringBuffer)s;
         buffer.append(asb.getValue(), from, to - from);
      } else {
         buffer.append((Object)s.subSequence(from, to));
      }

   }

   private static void outputThrowable(Throwable cause, AppendingStringBuffer sb, boolean stopAtWicketServlet) {
      sb.append((Object)cause);
      sb.append("\n");
      StackTraceElement[] trace = cause.getStackTrace();

      for(int i = 0; i < trace.length; ++i) {
         String traceString = trace[i].toString();
         if (!traceString.startsWith("sun.reflect.") || i <= 1) {
            sb.append("     at ");
            sb.append(traceString);
            sb.append("\n");
            if (stopAtWicketServlet && (traceString.startsWith("org.apache.wicket.protocol.http.WicketServlet") || traceString.startsWith("org.apache.wicket.protocol.http.WicketFilter"))) {
               return;
            }
         }
      }

   }

   private static int search(CharSequence s, String searchString, int pos) {
      if (s instanceof String) {
         return ((String)s).indexOf(searchString, pos);
      } else if (s instanceof StringBuffer) {
         return ((StringBuffer)s).indexOf(searchString, pos);
      } else if (s instanceof StringBuilder) {
         return ((StringBuilder)s).indexOf(searchString, pos);
      } else {
         return s instanceof AppendingStringBuffer ? ((AppendingStringBuffer)s).indexOf(searchString, pos) : s.toString().indexOf(searchString, pos);
      }
   }

   private static char toHex(int nibble) {
      return HEX_DIGIT[nibble & 15];
   }

   public static int lengthInBytes(String string, Charset charset) {
      Args.notNull(string, "string");
      if (charset != null) {
         try {
            return string.getBytes(charset.name()).length;
         } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException("StringResourceStream created with unsupported charset: " + charset.name());
         }
      } else {
         return string.getBytes().length;
      }
   }

   public static boolean startsWith(String str, String prefix, boolean caseSensitive) {
      return caseSensitive ? str.startsWith(prefix) : str.toLowerCase().startsWith(prefix.toLowerCase());
   }

   public static int indexOf(CharSequence sequence, char ch) {
      if (sequence != null) {
         for(int i = 0; i < sequence.length(); ++i) {
            if (sequence.charAt(i) == ch) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
      if (s != null && t != null) {
         int n = s.length();
         int m = t.length();
         if (n == 0) {
            return m;
         } else if (m == 0) {
            return n;
         } else {
            if (n > m) {
               CharSequence tmp = s;
               s = t;
               t = tmp;
               n = m;
               m = tmp.length();
            }

            int[] p = new int[n + 1];
            int[] d = new int[n + 1];

            for(int i = 0; i <= n; p[i] = i++) {
            }

            for(int j = 1; j <= m; ++j) {
               char t_j = t.charAt(j - 1);
               d[0] = j;

               for(int var12 = 1; var12 <= n; ++var12) {
                  int cost = s.charAt(var12 - 1) == t_j ? 0 : 1;
                  d[var12] = Math.min(Math.min(d[var12 - 1] + 1, p[var12] + 1), p[var12 - 1] + cost);
               }

               int[] _d = p;
               p = d;
               d = _d;
            }

            return p[n];
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static String toHexString(byte[] bytes) {
      Args.notNull(bytes, "bytes");
      StringBuilder hex = new StringBuilder(bytes.length << 1);

      for(byte b : bytes) {
         hex.append(toHex(b >> 4));
         hex.append(toHex(b));
      }

      return hex.toString();
   }

   public static <T extends Enum<T>> T toEnum(CharSequence value, Class<T> enumClass) {
      Args.notNull(enumClass, "enumClass");
      Args.notNull(value, "value");

      try {
         return (T)Enum.valueOf(enumClass, value.toString());
      } catch (Exception e) {
         throw new StringValueConversionException(String.format("Cannot convert '%s' to enum constant of type '%s'.", value, enumClass), e);
      }
   }

   public static String defaultIfEmpty(String originalString, String defaultValue) {
      return isEmpty(originalString) ? defaultValue : originalString;
   }

   static {
      SESSION_ID_PARAM = ';' + SESSION_ID_PARAM_NAME + '=';
      LINE_SEPARATOR = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty("line.separator");
         }
      });
   }
}
