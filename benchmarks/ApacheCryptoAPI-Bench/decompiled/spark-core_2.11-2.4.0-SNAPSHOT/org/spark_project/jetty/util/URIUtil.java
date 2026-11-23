package org.spark_project.jetty.util;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class URIUtil implements Cloneable {
   private static final Logger LOG = Log.getLogger(URIUtil.class);
   public static final String SLASH = "/";
   public static final String HTTP = "http";
   public static final String HTTPS = "https";
   public static final Charset __CHARSET;

   private URIUtil() {
   }

   public static String encodePath(String path) {
      if (path != null && path.length() != 0) {
         StringBuilder buf = encodePath((StringBuilder)null, path, 0);
         return buf == null ? path : buf.toString();
      } else {
         return path;
      }
   }

   public static StringBuilder encodePath(StringBuilder buf, String path) {
      return encodePath(buf, path, 0);
   }

   private static StringBuilder encodePath(StringBuilder buf, String path, int offset) {
      byte[] bytes = null;
      if (buf == null) {
         label90:
         for(int i = offset; i < path.length(); ++i) {
            char c = path.charAt(i);
            switch (c) {
               case ' ':
               case '"':
               case '#':
               case '%':
               case '\'':
               case ';':
               case '<':
               case '>':
               case '?':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '`':
               case '{':
               case '|':
               case '}':
                  buf = new StringBuilder(path.length() * 2);
                  break label90;
               default:
                  if (c > 127) {
                     bytes = path.getBytes(__CHARSET);
                     buf = new StringBuilder(path.length() * 2);
                     break label90;
                  }
            }
         }

         if (buf == null) {
            return null;
         }
      }

      int i;
      label81:
      for(i = offset; i < path.length(); ++i) {
         char c = path.charAt(i);
         switch (c) {
            case ' ':
               buf.append("%20");
               break;
            case '"':
               buf.append("%22");
               break;
            case '#':
               buf.append("%23");
               break;
            case '%':
               buf.append("%25");
               break;
            case '\'':
               buf.append("%27");
               break;
            case ';':
               buf.append("%3B");
               break;
            case '<':
               buf.append("%3C");
               break;
            case '>':
               buf.append("%3E");
               break;
            case '?':
               buf.append("%3F");
               break;
            case '[':
               buf.append("%5B");
               break;
            case '\\':
               buf.append("%5C");
               break;
            case ']':
               buf.append("%5D");
               break;
            case '^':
               buf.append("%5E");
               break;
            case '`':
               buf.append("%60");
               break;
            case '{':
               buf.append("%7B");
               break;
            case '|':
               buf.append("%7C");
               break;
            case '}':
               buf.append("%7D");
               break;
            default:
               if (c > 127) {
                  bytes = path.getBytes(__CHARSET);
                  break label81;
               }

               buf.append(c);
         }
      }

      if (bytes != null) {
         for(; i < bytes.length; ++i) {
            byte c = bytes[i];
            switch (c) {
               case 32:
                  buf.append("%20");
                  break;
               case 34:
                  buf.append("%22");
                  break;
               case 35:
                  buf.append("%23");
                  break;
               case 37:
                  buf.append("%25");
                  break;
               case 39:
                  buf.append("%27");
                  break;
               case 59:
                  buf.append("%3B");
                  break;
               case 60:
                  buf.append("%3C");
                  break;
               case 62:
                  buf.append("%3E");
                  break;
               case 63:
                  buf.append("%3F");
                  break;
               case 91:
                  buf.append("%5B");
                  break;
               case 92:
                  buf.append("%5C");
                  break;
               case 93:
                  buf.append("%5D");
                  break;
               case 94:
                  buf.append("%5E");
                  break;
               case 96:
                  buf.append("%60");
                  break;
               case 123:
                  buf.append("%7B");
                  break;
               case 124:
                  buf.append("%7C");
                  break;
               case 125:
                  buf.append("%7D");
                  break;
               default:
                  if (c < 0) {
                     buf.append('%');
                     TypeUtil.toHex((byte)c, buf);
                  } else {
                     buf.append((char)c);
                  }
            }
         }
      }

      return buf;
   }

   public static StringBuilder encodeString(StringBuilder buf, String path, String encode) {
      if (buf == null) {
         for(int i = 0; i < path.length(); ++i) {
            char c = path.charAt(i);
            if (c == '%' || encode.indexOf(c) >= 0) {
               buf = new StringBuilder(path.length() << 1);
               break;
            }
         }

         if (buf == null) {
            return null;
         }
      }

      for(int i = 0; i < path.length(); ++i) {
         char c = path.charAt(i);
         if (c != '%' && encode.indexOf(c) < 0) {
            buf.append(c);
         } else {
            buf.append('%');
            StringUtil.append(buf, (byte)(255 & c), 16);
         }
      }

      return buf;
   }

   public static String decodePath(String path) {
      return decodePath(path, 0, path.length());
   }

   public static String decodePath(String path, int offset, int length) {
      try {
         Utf8StringBuilder builder = null;
         int end = offset + length;

         label59:
         for(int i = offset; i < end; ++i) {
            char c = path.charAt(i);
            switch (c) {
               case '%':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(path.length());
                     builder.append(path, offset, i - offset);
                  }

                  if (i + 2 >= end) {
                     throw new IllegalArgumentException("Bad URI % encoding");
                  }

                  char u = path.charAt(i + 1);
                  if (u == 'u') {
                     builder.append((char)('\uffff' & TypeUtil.parseInt((String)path, i + 2, 4, 16)));
                     i += 5;
                  } else {
                     builder.append((byte)(255 & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2))));
                     i += 2;
                  }
                  break;
               case ';':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(path.length());
                     builder.append(path, offset, i - offset);
                  }

                  do {
                     ++i;
                     if (i >= end) {
                        continue label59;
                     }
                  } while(path.charAt(i) != '/');

                  builder.append('/');
                  break;
               default:
                  if (builder != null) {
                     builder.append(c);
                  }
            }
         }

         if (builder != null) {
            return builder.toString();
         } else if (offset == 0 && length == path.length()) {
            return path;
         } else {
            return path.substring(offset, end);
         }
      } catch (Utf8Appendable.NotUtf8Exception e) {
         LOG.warn(path.substring(offset, offset + length) + " " + e);
         LOG.debug(e);
         return decodeISO88591Path(path, offset, length);
      }
   }

   private static String decodeISO88591Path(String path, int offset, int length) {
      StringBuilder builder = null;
      int end = offset + length;

      label51:
      for(int i = offset; i < end; ++i) {
         char c = path.charAt(i);
         switch (c) {
            case '%':
               if (builder == null) {
                  builder = new StringBuilder(path.length());
                  builder.append(path, offset, i - offset);
               }

               if (i + 2 >= end) {
                  throw new IllegalArgumentException();
               }

               char u = path.charAt(i + 1);
               if (u == 'u') {
                  builder.append((char)('\uffff' & TypeUtil.parseInt((String)path, i + 2, 4, 16)));
                  i += 5;
               } else {
                  builder.append((byte)(255 & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2))));
                  i += 2;
               }
               break;
            case ';':
               if (builder == null) {
                  builder = new StringBuilder(path.length());
                  builder.append(path, offset, i - offset);
               }

               do {
                  ++i;
                  if (i >= end) {
                     continue label51;
                  }
               } while(path.charAt(i) != '/');

               builder.append('/');
               break;
            default:
               if (builder != null) {
                  builder.append(c);
               }
         }
      }

      if (builder != null) {
         return builder.toString();
      } else if (offset == 0 && length == path.length()) {
         return path;
      } else {
         return path.substring(offset, end);
      }
   }

   public static String addEncodedPaths(String p1, String p2) {
      if (p1 != null && p1.length() != 0) {
         if (p2 != null && p2.length() != 0) {
            int split = p1.indexOf(59);
            if (split < 0) {
               split = p1.indexOf(63);
            }

            if (split == 0) {
               return p2 + p1;
            } else {
               if (split < 0) {
                  split = p1.length();
               }

               StringBuilder buf = new StringBuilder(p1.length() + p2.length() + 2);
               buf.append(p1);
               if (buf.charAt(split - 1) == '/') {
                  if (p2.startsWith("/")) {
                     buf.deleteCharAt(split - 1);
                     buf.insert(split - 1, p2);
                  } else {
                     buf.insert(split, p2);
                  }
               } else if (p2.startsWith("/")) {
                  buf.insert(split, p2);
               } else {
                  buf.insert(split, '/');
                  buf.insert(split + 1, p2);
               }

               return buf.toString();
            }
         } else {
            return p1;
         }
      } else {
         return p1 != null && p2 == null ? p1 : p2;
      }
   }

   public static String addPaths(String p1, String p2) {
      if (p1 != null && p1.length() != 0) {
         if (p2 != null && p2.length() != 0) {
            boolean p1EndsWithSlash = p1.endsWith("/");
            boolean p2StartsWithSlash = p2.startsWith("/");
            if (p1EndsWithSlash && p2StartsWithSlash) {
               if (p2.length() == 1) {
                  return p1;
               }

               if (p1.length() == 1) {
                  return p2;
               }
            }

            StringBuilder buf = new StringBuilder(p1.length() + p2.length() + 2);
            buf.append(p1);
            if (p1.endsWith("/")) {
               if (p2.startsWith("/")) {
                  buf.setLength(buf.length() - 1);
               }
            } else if (!p2.startsWith("/")) {
               buf.append("/");
            }

            buf.append(p2);
            return buf.toString();
         } else {
            return p1;
         }
      } else {
         return p1 != null && p2 == null ? p1 : p2;
      }
   }

   public static String parentPath(String p) {
      if (p != null && !"/".equals(p)) {
         int slash = p.lastIndexOf(47, p.length() - 2);
         return slash >= 0 ? p.substring(0, slash + 1) : null;
      } else {
         return null;
      }
   }

   public static String canonicalPath(String path) {
      if (path != null && path.length() != 0) {
         int end = path.length();

         int start;
         label184:
         for(start = path.lastIndexOf(47, end); end > 0; start = path.lastIndexOf(47, start - 1)) {
            switch (end - start) {
               case 2:
                  if (path.charAt(start + 1) == '.') {
                     break label184;
                  }
                  break;
               case 3:
                  if (path.charAt(start + 1) == '.' && path.charAt(start + 2) == '.') {
                     break label184;
                  }
            }

            end = start;
         }

         if (start >= end) {
            return path;
         } else {
            StringBuilder buf = new StringBuilder(path);
            int delStart = -1;
            int delEnd = -1;
            int skip = 0;

            while(end > 0) {
               switch (end - start) {
                  case 2:
                     if (buf.charAt(start + 1) != '.') {
                        if (skip > 0) {
                           --skip;
                           if (skip == 0) {
                              delStart = start >= 0 ? start : 0;
                              if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                                 ++delStart;
                              }
                           }
                        }
                     } else {
                        if (start < 0 && buf.length() > 2 && buf.charAt(1) == '/' && buf.charAt(2) == '/') {
                           break;
                        }

                        if (delEnd < 0) {
                           delEnd = end;
                        }

                        delStart = start;
                        if (start >= 0 && (start != 0 || buf.charAt(start) != '/')) {
                           if (end == buf.length()) {
                              delStart = start + 1;
                           }

                           for(end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {
                           }
                           continue;
                        }

                        delStart = start + 1;
                        if (delEnd < buf.length() && buf.charAt(delEnd) == '/') {
                           ++delEnd;
                        }
                     }
                     break;
                  case 3:
                     if (buf.charAt(start + 1) == '.' && buf.charAt(start + 2) == '.') {
                        delStart = start;
                        if (delEnd < 0) {
                           delEnd = end;
                        }

                        ++skip;

                        for(end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {
                        }
                        continue;
                     }

                     if (skip > 0) {
                        --skip;
                        if (skip == 0) {
                           delStart = start >= 0 ? start : 0;
                           if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                              ++delStart;
                           }
                        }
                     }
                     break;
                  default:
                     if (skip > 0) {
                        --skip;
                        if (skip == 0) {
                           delStart = start >= 0 ? start : 0;
                           if (delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                              ++delStart;
                           }
                        }
                     }
               }

               if (skip <= 0 && delStart >= 0 && delEnd >= delStart) {
                  buf.delete(delStart, delEnd);
                  delEnd = -1;
                  delStart = -1;
                  if (skip > 0) {
                     delEnd = end;
                  }
               }

               for(end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {
               }
            }

            if (skip > 0) {
               return null;
            } else {
               if (delEnd >= 0) {
                  buf.delete(delStart, delEnd);
               }

               return buf.toString();
            }
         }
      } else {
         return path;
      }
   }

   public static String compactPath(String path) {
      if (path != null && path.length() != 0) {
         int state = 0;
         int end = path.length();
         int i = 0;

         while(true) {
            label54: {
               if (i < end) {
                  char c = path.charAt(i);
                  switch (c) {
                     case '/':
                        ++state;
                        if (state != 2) {
                           break label54;
                        }
                        break;
                     case '?':
                        return path;
                     default:
                        state = 0;
                        break label54;
                  }
               }

               if (state < 2) {
                  return path;
               }

               StringBuilder buf = new StringBuilder(path.length());
               buf.append(path, 0, i);

               for(; i < end; ++i) {
                  char c = path.charAt(i);
                  switch (c) {
                     case '/':
                        if (state++ == 0) {
                           buf.append(c);
                        }
                        break;
                     case '?':
                        buf.append(path, i, end);
                        return buf.toString();
                     default:
                        state = 0;
                        buf.append(c);
                  }
               }

               return buf.toString();
            }

            ++i;
         }
      } else {
         return path;
      }
   }

   public static boolean hasScheme(String uri) {
      for(int i = 0; i < uri.length(); ++i) {
         char c = uri.charAt(i);
         if (c == ':') {
            return true;
         }

         if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (i <= 0 || (c < '0' || c > '9') && c != '.' && c != '+' && c != '-')) {
            break;
         }
      }

      return false;
   }

   public static String newURI(String scheme, String server, int port, String path, String query) {
      StringBuilder builder = newURIBuilder(scheme, server, port);
      builder.append(path);
      if (query != null && query.length() > 0) {
         builder.append('?').append(query);
      }

      return builder.toString();
   }

   public static StringBuilder newURIBuilder(String scheme, String server, int port) {
      StringBuilder builder = new StringBuilder();
      appendSchemeHostPort(builder, scheme, server, port);
      return builder;
   }

   public static void appendSchemeHostPort(StringBuilder url, String scheme, String server, int port) {
      url.append(scheme).append("://").append(HostPort.normalizeHost(server));
      if (port > 0) {
         switch (scheme) {
            case "http":
               if (port != 80) {
                  url.append(':').append(port);
               }
               break;
            case "https":
               if (port != 443) {
                  url.append(':').append(port);
               }
               break;
            default:
               url.append(':').append(port);
         }
      }

   }

   public static void appendSchemeHostPort(StringBuffer url, String scheme, String server, int port) {
      synchronized(url) {
         url.append(scheme).append("://").append(HostPort.normalizeHost(server));
         if (port > 0) {
            switch (scheme) {
               case "http":
                  if (port != 80) {
                     url.append(':').append(port);
                  }
                  break;
               case "https":
                  if (port != 443) {
                     url.append(':').append(port);
                  }
                  break;
               default:
                  url.append(':').append(port);
            }
         }

      }
   }

   public static boolean equalsIgnoreEncodings(String uriA, String uriB) {
      int lenA = uriA.length();
      int lenB = uriB.length();
      int a = 0;
      int b = 0;

      while(a < lenA && b < lenB) {
         int oa = uriA.charAt(a++);
         int ca = oa;
         if (oa == 37) {
            ca = TypeUtil.convertHexDigit(uriA.charAt(a++)) * 16 + TypeUtil.convertHexDigit(uriA.charAt(a++));
         }

         int ob = uriB.charAt(b++);
         int cb = ob;
         if (ob == 37) {
            cb = TypeUtil.convertHexDigit(uriB.charAt(b++)) * 16 + TypeUtil.convertHexDigit(uriB.charAt(b++));
         }

         if (ca == 47 && oa != ob) {
            return false;
         }

         if (ca != cb) {
            return decodePath(uriA).equals(decodePath(uriB));
         }
      }

      return a == lenA && b == lenB;
   }

   public static boolean equalsIgnoreEncodings(URI uriA, URI uriB) {
      if (uriA.equals(uriB)) {
         return true;
      } else {
         if (uriA.getScheme() == null) {
            if (uriB.getScheme() != null) {
               return false;
            }
         } else if (!uriA.getScheme().equals(uriB.getScheme())) {
            return false;
         }

         if (uriA.getAuthority() == null) {
            if (uriB.getAuthority() != null) {
               return false;
            }
         } else if (!uriA.getAuthority().equals(uriB.getAuthority())) {
            return false;
         }

         return equalsIgnoreEncodings(uriA.getPath(), uriB.getPath());
      }
   }

   public static URI addPath(URI uri, String path) {
      String base = uri.toASCIIString();
      StringBuilder buf = new StringBuilder(base.length() + path.length() * 3);
      buf.append(base);
      if (buf.charAt(base.length() - 1) != '/') {
         buf.append('/');
      }

      int offset = path.charAt(0) == '/' ? 1 : 0;
      encodePath(buf, path, offset);
      return URI.create(buf.toString());
   }

   static {
      __CHARSET = StandardCharsets.UTF_8;
   }
}
