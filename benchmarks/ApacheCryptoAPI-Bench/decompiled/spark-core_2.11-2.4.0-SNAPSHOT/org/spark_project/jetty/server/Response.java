package org.spark_project.jetty.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalSelectorException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.spark_project.jetty.http.DateGenerator;
import org.spark_project.jetty.http.HttpContent;
import org.spark_project.jetty.http.HttpCookie;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpGenerator;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.http.HttpStatus;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.http.PreEncodedHttpField;
import org.spark_project.jetty.io.RuntimeIOException;
import org.spark_project.jetty.server.handler.ErrorHandler;
import org.spark_project.jetty.util.ByteArrayISO8859Writer;
import org.spark_project.jetty.util.QuotedStringTokenizer;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class Response implements HttpServletResponse {
   private static final Logger LOG = Log.getLogger(Response.class);
   private static final String __COOKIE_DELIM = "\",;\\ \t";
   private static final String __01Jan1970_COOKIE = DateGenerator.formatCookieDate(0L).trim();
   private static final int __MIN_BUFFER_SIZE = 1;
   private static final HttpField __EXPIRES_01JAN1970;
   private static final ThreadLocal<StringBuilder> __cookieBuilder;
   public static final String SET_INCLUDE_HEADER_PREFIX = "org.spark_project.jetty.server.include.";
   public static final String HTTP_ONLY_COMMENT = "__HTTP_ONLY__";
   private final HttpChannel _channel;
   private final HttpFields _fields = new HttpFields();
   private final AtomicInteger _include = new AtomicInteger();
   private final HttpOutput _out;
   private int _status = 200;
   private String _reason;
   private Locale _locale;
   private MimeTypes.Type _mimeType;
   private String _characterEncoding;
   private EncodingFrom _encodingFrom;
   private String _contentType;
   private OutputType _outputType;
   private ResponseWriter _writer;
   private long _contentLength;
   private static final EnumSet<EncodingFrom> __localeOverride;

   public Response(HttpChannel channel, HttpOutput out) {
      this._encodingFrom = Response.EncodingFrom.NOT_SET;
      this._outputType = Response.OutputType.NONE;
      this._contentLength = -1L;
      this._channel = channel;
      this._out = out;
   }

   public HttpChannel getHttpChannel() {
      return this._channel;
   }

   protected void recycle() {
      this._status = 200;
      this._reason = null;
      this._locale = null;
      this._mimeType = null;
      this._characterEncoding = null;
      this._contentType = null;
      this._outputType = Response.OutputType.NONE;
      this._contentLength = -1L;
      this._out.recycle();
      this._fields.clear();
      this._encodingFrom = Response.EncodingFrom.NOT_SET;
   }

   public HttpOutput getHttpOutput() {
      return this._out;
   }

   public boolean isIncluding() {
      return this._include.get() > 0;
   }

   public void include() {
      this._include.incrementAndGet();
   }

   public void included() {
      this._include.decrementAndGet();
      if (this._outputType == Response.OutputType.WRITER) {
         this._writer.reopen();
      }

      this._out.reopen();
   }

   public void addCookie(HttpCookie cookie) {
      this.addSetCookie(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), cookie.getMaxAge(), cookie.getComment(), cookie.isSecure(), cookie.isHttpOnly(), cookie.getVersion());
   }

   public void addCookie(Cookie cookie) {
      String comment = cookie.getComment();
      boolean httpOnly = false;
      if (comment != null) {
         int i = comment.indexOf("__HTTP_ONLY__");
         if (i >= 0) {
            httpOnly = true;
            comment = comment.replace("__HTTP_ONLY__", "").trim();
            if (comment.length() == 0) {
               comment = null;
            }
         }
      }

      this.addSetCookie(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), (long)cookie.getMaxAge(), comment, cookie.getSecure(), httpOnly || cookie.isHttpOnly(), cookie.getVersion());
   }

   public void addSetCookie(String name, String value, String domain, String path, long maxAge, String comment, boolean isSecure, boolean isHttpOnly, int version) {
      if (name != null && name.length() != 0) {
         StringBuilder buf = (StringBuilder)__cookieBuilder.get();
         buf.setLength(0);
         boolean quote_name = isQuoteNeededForCookie(name);
         quoteOnlyOrAppend(buf, name, quote_name);
         buf.append('=');
         boolean quote_value = isQuoteNeededForCookie(value);
         quoteOnlyOrAppend(buf, value, quote_value);
         boolean has_domain = domain != null && domain.length() > 0;
         boolean quote_domain = has_domain && isQuoteNeededForCookie(domain);
         boolean has_path = path != null && path.length() > 0;
         boolean quote_path = has_path && isQuoteNeededForCookie(path);
         if (version == 0 && (comment != null || quote_name || quote_value || quote_domain || quote_path || QuotedStringTokenizer.isQuoted(name) || QuotedStringTokenizer.isQuoted(value) || QuotedStringTokenizer.isQuoted(path) || QuotedStringTokenizer.isQuoted(domain))) {
            version = 1;
         }

         if (version == 1) {
            buf.append(";Version=1");
         } else if (version > 1) {
            buf.append(";Version=").append(version);
         }

         if (has_path) {
            buf.append(";Path=");
            quoteOnlyOrAppend(buf, path, quote_path);
         }

         if (has_domain) {
            buf.append(";Domain=");
            quoteOnlyOrAppend(buf, domain, quote_domain);
         }

         if (maxAge >= 0L) {
            buf.append(";Expires=");
            if (maxAge == 0L) {
               buf.append(__01Jan1970_COOKIE);
            } else {
               DateGenerator.formatCookieDate(buf, System.currentTimeMillis() + 1000L * maxAge);
            }

            if (version >= 1) {
               buf.append(";Max-Age=");
               buf.append(maxAge);
            }
         }

         if (isSecure) {
            buf.append(";Secure");
         }

         if (isHttpOnly) {
            buf.append(";HttpOnly");
         }

         if (comment != null) {
            buf.append(";Comment=");
            quoteOnlyOrAppend(buf, comment, isQuoteNeededForCookie(comment));
         }

         this._fields.add(HttpHeader.SET_COOKIE, buf.toString());
         this._fields.put(__EXPIRES_01JAN1970);
      } else {
         throw new IllegalArgumentException("Bad cookie name");
      }
   }

   private static boolean isQuoteNeededForCookie(String s) {
      if (s != null && s.length() != 0) {
         if (QuotedStringTokenizer.isQuoted(s)) {
            return false;
         } else {
            for(int i = 0; i < s.length(); ++i) {
               char c = s.charAt(i);
               if ("\",;\\ \t".indexOf(c) >= 0) {
                  return true;
               }

               if (c < ' ' || c >= 127) {
                  throw new IllegalArgumentException("Illegal character in cookie value");
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private static void quoteOnlyOrAppend(StringBuilder buf, String s, boolean quote) {
      if (quote) {
         QuotedStringTokenizer.quoteOnly(buf, s);
      } else {
         buf.append(s);
      }

   }

   public boolean containsHeader(String name) {
      return this._fields.containsKey(name);
   }

   public String encodeURL(String url) {
      Request request = this._channel.getRequest();
      SessionManager sessionManager = request.getSessionManager();
      if (sessionManager == null) {
         return url;
      } else {
         HttpURI uri = null;
         if (sessionManager.isCheckingRemoteSessionIdEncoding() && URIUtil.hasScheme(url)) {
            uri = new HttpURI(url);
            String path = uri.getPath();
            path = path == null ? "" : path;
            int port = uri.getPort();
            if (port < 0) {
               port = HttpScheme.HTTPS.asString().equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
            }

            if (!request.getServerName().equalsIgnoreCase(uri.getHost())) {
               return url;
            }

            if (request.getServerPort() != port) {
               return url;
            }

            if (!path.startsWith(request.getContextPath())) {
               return url;
            }
         }

         String sessionURLPrefix = sessionManager.getSessionIdPathParameterNamePrefix();
         if (sessionURLPrefix == null) {
            return url;
         } else if (url == null) {
            return null;
         } else if ((!sessionManager.isUsingCookies() || !request.isRequestedSessionIdFromCookie()) && sessionManager.isUsingURLs()) {
            HttpSession session = request.getSession(false);
            if (session == null) {
               return url;
            } else if (!sessionManager.isValid(session)) {
               return url;
            } else {
               String id = sessionManager.getNodeId(session);
               if (uri == null) {
                  uri = new HttpURI(url);
               }

               int prefix = url.indexOf(sessionURLPrefix);
               if (prefix != -1) {
                  int suffix = url.indexOf("?", prefix);
                  if (suffix < 0) {
                     suffix = url.indexOf("#", prefix);
                  }

                  return suffix <= prefix ? url.substring(0, prefix + sessionURLPrefix.length()) + id : url.substring(0, prefix + sessionURLPrefix.length()) + id + url.substring(suffix);
               } else {
                  int suffix = url.indexOf(63);
                  if (suffix < 0) {
                     suffix = url.indexOf(35);
                  }

                  return suffix < 0 ? url + ((HttpScheme.HTTPS.is(uri.getScheme()) || HttpScheme.HTTP.is(uri.getScheme())) && uri.getPath() == null ? "/" : "") + sessionURLPrefix + id : url.substring(0, suffix) + ((HttpScheme.HTTPS.is(uri.getScheme()) || HttpScheme.HTTP.is(uri.getScheme())) && uri.getPath() == null ? "/" : "") + sessionURLPrefix + id + url.substring(suffix);
               }
            }
         } else {
            int prefix = url.indexOf(sessionURLPrefix);
            if (prefix != -1) {
               int suffix = url.indexOf("?", prefix);
               if (suffix < 0) {
                  suffix = url.indexOf("#", prefix);
               }

               return suffix <= prefix ? url.substring(0, prefix) : url.substring(0, prefix) + url.substring(suffix);
            } else {
               return url;
            }
         }
      }
   }

   public String encodeRedirectURL(String url) {
      return this.encodeURL(url);
   }

   /** @deprecated */
   @Deprecated
   public String encodeUrl(String url) {
      return this.encodeURL(url);
   }

   /** @deprecated */
   @Deprecated
   public String encodeRedirectUrl(String url) {
      return this.encodeRedirectURL(url);
   }

   public void sendError(int sc) throws IOException {
      this.sendError(sc, (String)null);
   }

   public void sendError(int code, String message) throws IOException {
      if (!this.isIncluding()) {
         if (this.isCommitted()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborting on sendError on committed response {} {}", code, message);
            }

            code = -1;
         }

         switch (code) {
            case -1:
               this._channel.abort(new IOException());
               return;
            case 102:
               this.sendProcessing();
               return;
            default:
               if (this.isCommitted()) {
                  LOG.warn("cannot sendError(" + code + ", " + message + ") response already committed");
               } else {
                  this.resetBuffer();
               }

               this._characterEncoding = null;
               this.setHeader((HttpHeader)HttpHeader.EXPIRES, (String)null);
               this.setHeader((HttpHeader)HttpHeader.LAST_MODIFIED, (String)null);
               this.setHeader((HttpHeader)HttpHeader.CACHE_CONTROL, (String)null);
               this.setHeader((HttpHeader)HttpHeader.CONTENT_TYPE, (String)null);
               this.setHeader((HttpHeader)HttpHeader.CONTENT_LENGTH, (String)null);
               this._outputType = Response.OutputType.NONE;
               this.setStatus(code);
               this._reason = message;
               Request request = this._channel.getRequest();
               Throwable cause = (Throwable)request.getAttribute("javax.servlet.error.exception");
               if (message == null) {
                  message = cause == null ? HttpStatus.getMessage(code) : cause.toString();
               }

               if (code != 204 && code != 304 && code != 206 && code >= 200) {
                  ErrorHandler error_handler = ErrorHandler.getErrorHandler(this._channel.getServer(), request.getContext() == null ? null : request.getContext().getContextHandler());
                  if (error_handler != null) {
                     request.setAttribute("javax.servlet.error.status_code", new Integer(code));
                     request.setAttribute("javax.servlet.error.message", message);
                     request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
                     request.setAttribute("javax.servlet.error.servlet_name", request.getServletName());
                     error_handler.handle((String)null, this._channel.getRequest(), this._channel.getRequest(), this);
                  } else {
                     this.setHeader(HttpHeader.CACHE_CONTROL, "must-revalidate,no-cache,no-store");
                     this.setContentType(MimeTypes.Type.TEXT_HTML_8859_1.toString());
                     ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(2048);
                     Throwable var7 = null;

                     try {
                        message = StringUtil.sanitizeXmlString(message);
                        String uri = request.getRequestURI();
                        uri = StringUtil.sanitizeXmlString(uri);
                        writer.write("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html;charset=ISO-8859-1\"/>\n");
                        writer.write("<title>Error ");
                        writer.write(Integer.toString(code));
                        writer.write(' ');
                        if (message == null) {
                           writer.write(message);
                        }

                        writer.write("</title>\n</head>\n<body>\n<h2>HTTP ERROR: ");
                        writer.write(Integer.toString(code));
                        writer.write("</h2>\n<p>Problem accessing ");
                        writer.write(uri);
                        writer.write(". Reason:\n<pre>    ");
                        writer.write(message);
                        writer.write("</pre>");
                        writer.write("</p>\n<hr />");
                        this.getHttpChannel().getHttpConfiguration().writePoweredBy(writer, (String)null, "<hr/>");
                        writer.write("\n</body>\n</html>\n");
                        writer.flush();
                        this.setContentLength(writer.size());
                        ServletOutputStream outputStream = this.getOutputStream();
                        Throwable var10 = null;

                        try {
                           writer.writeTo(outputStream);
                           writer.destroy();
                        } catch (Throwable var33) {
                           var10 = var33;
                           throw var33;
                        } finally {
                           if (outputStream != null) {
                              if (var10 != null) {
                                 try {
                                    outputStream.close();
                                 } catch (Throwable var32) {
                                    var10.addSuppressed(var32);
                                 }
                              } else {
                                 outputStream.close();
                              }
                           }

                        }
                     } catch (Throwable var35) {
                        var7 = var35;
                        throw var35;
                     } finally {
                        if (writer != null) {
                           if (var7 != null) {
                              try {
                                 writer.close();
                              } catch (Throwable var31) {
                                 var7.addSuppressed(var31);
                              }
                           } else {
                              writer.close();
                           }
                        }

                     }
                  }
               } else if (code != 206) {
                  this._channel.getRequest().getHttpFields().remove(HttpHeader.CONTENT_TYPE);
                  this._channel.getRequest().getHttpFields().remove(HttpHeader.CONTENT_LENGTH);
                  this._characterEncoding = null;
                  this._mimeType = null;
               }

               this.closeOutput();
         }
      }
   }

   public void sendProcessing() throws IOException {
      if (this._channel.isExpecting102Processing() && !this.isCommitted()) {
         this._channel.sendResponse(HttpGenerator.PROGRESS_102_INFO, (ByteBuffer)null, true);
      }

   }

   public void sendRedirect(int code, String location) throws IOException {
      if (code >= 300 && code < 400) {
         if (!this.isIncluding()) {
            if (location == null) {
               throw new IllegalArgumentException();
            } else {
               if (!URIUtil.hasScheme(location)) {
                  StringBuilder buf = this._channel.getRequest().getRootURL();
                  if (location.startsWith("/")) {
                     location = URIUtil.canonicalPath(location);
                  } else {
                     String path = this._channel.getRequest().getRequestURI();
                     String parent = path.endsWith("/") ? path : URIUtil.parentPath(path);
                     location = URIUtil.canonicalPath(URIUtil.addEncodedPaths(parent, location));
                     if (!location.startsWith("/")) {
                        buf.append('/');
                     }
                  }

                  if (location == null) {
                     throw new IllegalStateException("path cannot be above root");
                  }

                  buf.append(location);
                  location = buf.toString();
               }

               this.resetBuffer();
               this.setHeader(HttpHeader.LOCATION, location);
               this.setStatus(code);
               this.closeOutput();
            }
         }
      } else {
         throw new IllegalArgumentException("Not a 3xx redirect code");
      }
   }

   public void sendRedirect(String location) throws IOException {
      this.sendRedirect(302, location);
   }

   public void setDateHeader(String name, long date) {
      if (!this.isIncluding()) {
         this._fields.putDateField(name, date);
      }

   }

   public void addDateHeader(String name, long date) {
      if (!this.isIncluding()) {
         this._fields.addDateField(name, date);
      }

   }

   public void setHeader(HttpHeader name, String value) {
      if (HttpHeader.CONTENT_TYPE == name) {
         this.setContentType(value);
      } else {
         if (this.isIncluding()) {
            return;
         }

         this._fields.put(name, value);
         if (HttpHeader.CONTENT_LENGTH == name) {
            if (value == null) {
               this._contentLength = -1L;
            } else {
               this._contentLength = Long.parseLong(value);
            }
         }
      }

   }

   public void setHeader(String name, String value) {
      if (HttpHeader.CONTENT_TYPE.is(name)) {
         this.setContentType(value);
      } else {
         if (this.isIncluding()) {
            if (!name.startsWith("org.spark_project.jetty.server.include.")) {
               return;
            }

            name = name.substring("org.spark_project.jetty.server.include.".length());
         }

         this._fields.put(name, value);
         if (HttpHeader.CONTENT_LENGTH.is(name)) {
            if (value == null) {
               this._contentLength = -1L;
            } else {
               this._contentLength = Long.parseLong(value);
            }
         }
      }

   }

   public Collection<String> getHeaderNames() {
      HttpFields fields = this._fields;
      return fields.getFieldNamesCollection();
   }

   public String getHeader(String name) {
      return this._fields.get(name);
   }

   public Collection<String> getHeaders(String name) {
      HttpFields fields = this._fields;
      Collection<String> i = fields.getValuesList(name);
      return (Collection<String>)(i == null ? Collections.emptyList() : i);
   }

   public void addHeader(String name, String value) {
      if (this.isIncluding()) {
         if (!name.startsWith("org.spark_project.jetty.server.include.")) {
            return;
         }

         name = name.substring("org.spark_project.jetty.server.include.".length());
      }

      if (HttpHeader.CONTENT_TYPE.is(name)) {
         this.setContentType(value);
      } else if (HttpHeader.CONTENT_LENGTH.is(name)) {
         this.setHeader(name, value);
      } else {
         this._fields.add(name, value);
      }
   }

   public void setIntHeader(String name, int value) {
      if (!this.isIncluding()) {
         this._fields.putLongField(name, (long)value);
         if (HttpHeader.CONTENT_LENGTH.is(name)) {
            this._contentLength = (long)value;
         }
      }

   }

   public void addIntHeader(String name, int value) {
      if (!this.isIncluding()) {
         this._fields.add(name, Integer.toString(value));
         if (HttpHeader.CONTENT_LENGTH.is(name)) {
            this._contentLength = (long)value;
         }
      }

   }

   public void setStatus(int sc) {
      if (sc <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (!this.isIncluding()) {
            this._status = sc;
            this._reason = null;
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void setStatus(int sc, String sm) {
      this.setStatusWithReason(sc, sm);
   }

   public void setStatusWithReason(int sc, String sm) {
      if (sc <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (!this.isIncluding()) {
            this._status = sc;
            this._reason = sm;
         }

      }
   }

   public String getCharacterEncoding() {
      if (this._characterEncoding == null) {
         String encoding = MimeTypes.getCharsetAssumedFromContentType(this._contentType);
         if (encoding != null) {
            return encoding;
         } else {
            encoding = MimeTypes.getCharsetInferredFromContentType(this._contentType);
            return encoding != null ? encoding : "iso-8859-1";
         }
      } else {
         return this._characterEncoding;
      }
   }

   public String getContentType() {
      return this._contentType;
   }

   public ServletOutputStream getOutputStream() throws IOException {
      if (this._outputType == Response.OutputType.WRITER) {
         throw new IllegalStateException("WRITER");
      } else {
         this._outputType = Response.OutputType.STREAM;
         return this._out;
      }
   }

   public boolean isWriting() {
      return this._outputType == Response.OutputType.WRITER;
   }

   public PrintWriter getWriter() throws IOException {
      if (this._outputType == Response.OutputType.STREAM) {
         throw new IllegalStateException("STREAM");
      } else {
         if (this._outputType == Response.OutputType.NONE) {
            String encoding = this._characterEncoding;
            if (encoding == null) {
               if (this._mimeType != null && this._mimeType.isCharsetAssumed()) {
                  encoding = this._mimeType.getCharsetString();
               } else {
                  encoding = MimeTypes.getCharsetAssumedFromContentType(this._contentType);
                  if (encoding == null) {
                     encoding = MimeTypes.getCharsetInferredFromContentType(this._contentType);
                     if (encoding == null) {
                        encoding = "iso-8859-1";
                     }

                     this.setCharacterEncoding(encoding, Response.EncodingFrom.INFERRED);
                  }
               }
            }

            Locale locale = this.getLocale();
            if (this._writer != null && this._writer.isFor(locale, encoding)) {
               this._writer.reopen();
            } else if ("iso-8859-1".equalsIgnoreCase(encoding)) {
               this._writer = new ResponseWriter(new Iso88591HttpWriter(this._out), locale, encoding);
            } else if ("utf-8".equalsIgnoreCase(encoding)) {
               this._writer = new ResponseWriter(new Utf8HttpWriter(this._out), locale, encoding);
            } else {
               this._writer = new ResponseWriter(new EncodingHttpWriter(this._out, encoding), locale, encoding);
            }

            this._outputType = Response.OutputType.WRITER;
         }

         return this._writer;
      }
   }

   public void setContentLength(int len) {
      if (!this.isCommitted() && !this.isIncluding()) {
         this._contentLength = (long)len;
         if (this._contentLength > 0L) {
            long written = this._out.getWritten();
            if (written > (long)len) {
               throw new IllegalArgumentException("setContentLength(" + len + ") when already written " + written);
            }

            this._fields.putLongField(HttpHeader.CONTENT_LENGTH, (long)len);
            if (this.isAllContentWritten(written)) {
               try {
                  this.closeOutput();
               } catch (IOException e) {
                  throw new RuntimeIOException(e);
               }
            }
         } else if (this._contentLength == 0L) {
            long written = this._out.getWritten();
            if (written > 0L) {
               throw new IllegalArgumentException("setContentLength(0) when already written " + written);
            }

            this._fields.put(HttpHeader.CONTENT_LENGTH, "0");
         } else {
            this._fields.remove(HttpHeader.CONTENT_LENGTH);
         }

      }
   }

   public long getContentLength() {
      return this._contentLength;
   }

   public boolean isAllContentWritten(long written) {
      return this._contentLength >= 0L && written >= this._contentLength;
   }

   public void closeOutput() throws IOException {
      switch (this._outputType) {
         case WRITER:
            this._writer.close();
            if (!this._out.isClosed()) {
               this._out.close();
            }
            break;
         case STREAM:
            this.getOutputStream().close();
            break;
         default:
            this._out.close();
      }

   }

   public long getLongContentLength() {
      return this._contentLength;
   }

   public void setLongContentLength(long len) {
      if (!this.isCommitted() && !this.isIncluding()) {
         this._contentLength = len;
         this._fields.putLongField(HttpHeader.CONTENT_LENGTH.toString(), len);
      }
   }

   public void setContentLengthLong(long length) {
      this.setLongContentLength(length);
   }

   public void setCharacterEncoding(String encoding) {
      this.setCharacterEncoding(encoding, Response.EncodingFrom.SET_CHARACTER_ENCODING);
   }

   private void setCharacterEncoding(String encoding, EncodingFrom from) {
      if (!this.isIncluding() && !this.isWriting()) {
         if (this._outputType != Response.OutputType.WRITER && !this.isCommitted()) {
            if (encoding == null) {
               this._encodingFrom = Response.EncodingFrom.NOT_SET;
               if (this._characterEncoding != null) {
                  this._characterEncoding = null;
                  if (this._mimeType != null) {
                     this._mimeType = this._mimeType.getBaseType();
                     this._contentType = this._mimeType.asString();
                     this._fields.put(this._mimeType.getContentTypeField());
                  } else if (this._contentType != null) {
                     this._contentType = MimeTypes.getContentTypeWithoutCharset(this._contentType);
                     this._fields.put(HttpHeader.CONTENT_TYPE, this._contentType);
                  }
               }
            } else {
               this._encodingFrom = from;
               this._characterEncoding = HttpGenerator.__STRICT ? encoding : StringUtil.normalizeCharset(encoding);
               if (this._mimeType != null) {
                  this._contentType = this._mimeType.getBaseType().asString() + ";charset=" + this._characterEncoding;
                  this._mimeType = (MimeTypes.Type)MimeTypes.CACHE.get(this._contentType);
                  if (this._mimeType != null && !HttpGenerator.__STRICT) {
                     this._fields.put(this._mimeType.getContentTypeField());
                  } else {
                     this._fields.put(HttpHeader.CONTENT_TYPE, this._contentType);
                  }
               } else if (this._contentType != null) {
                  this._contentType = MimeTypes.getContentTypeWithoutCharset(this._contentType) + ";charset=" + this._characterEncoding;
                  this._fields.put(HttpHeader.CONTENT_TYPE, this._contentType);
               }
            }
         }

      }
   }

   public void setContentType(String contentType) {
      if (!this.isCommitted() && !this.isIncluding()) {
         if (contentType == null) {
            if (this.isWriting() && this._characterEncoding != null) {
               throw new IllegalSelectorException();
            }

            if (this._locale == null) {
               this._characterEncoding = null;
            }

            this._mimeType = null;
            this._contentType = null;
            this._fields.remove(HttpHeader.CONTENT_TYPE);
         } else {
            this._contentType = contentType;
            this._mimeType = (MimeTypes.Type)MimeTypes.CACHE.get(contentType);
            String charset;
            if (this._mimeType != null && this._mimeType.getCharset() != null && !this._mimeType.isCharsetAssumed()) {
               charset = this._mimeType.getCharsetString();
            } else {
               charset = MimeTypes.getCharsetFromContentType(contentType);
            }

            if (charset == null) {
               switch (this._encodingFrom) {
                  case NOT_SET:
                  default:
                     break;
                  case INFERRED:
                  case SET_CONTENT_TYPE:
                     if (this.isWriting()) {
                        this._mimeType = null;
                        this._contentType = this._contentType + ";charset=" + this._characterEncoding;
                     } else {
                        this._encodingFrom = Response.EncodingFrom.NOT_SET;
                        this._characterEncoding = null;
                     }
                     break;
                  case SET_LOCALE:
                  case SET_CHARACTER_ENCODING:
                     this._contentType = contentType + ";charset=" + this._characterEncoding;
                     this._mimeType = null;
               }
            } else if (this.isWriting() && !charset.equalsIgnoreCase(this._characterEncoding)) {
               this._mimeType = null;
               this._contentType = MimeTypes.getContentTypeWithoutCharset(this._contentType);
               if (this._characterEncoding != null) {
                  this._contentType = this._contentType + ";charset=" + this._characterEncoding;
               }
            } else {
               this._characterEncoding = charset;
               this._encodingFrom = Response.EncodingFrom.SET_CONTENT_TYPE;
            }

            if (!HttpGenerator.__STRICT && this._mimeType != null) {
               this._contentType = this._mimeType.asString();
               this._fields.put(this._mimeType.getContentTypeField());
            } else {
               this._fields.put(HttpHeader.CONTENT_TYPE, this._contentType);
            }
         }

      }
   }

   public void setBufferSize(int size) {
      if (this.isCommitted()) {
         throw new IllegalStateException("cannot set buffer size after response is in committed state");
      } else if (this.getContentCount() > 0L) {
         throw new IllegalStateException("cannot set buffer size after response has " + this.getContentCount() + " bytes already written");
      } else {
         if (size < 1) {
            size = 1;
         }

         this._out.setBufferSize(size);
      }
   }

   public int getBufferSize() {
      return this._out.getBufferSize();
   }

   public void flushBuffer() throws IOException {
      if (!this._out.isClosed()) {
         this._out.flush();
      }

   }

   public void reset() {
      this.reset(false);
   }

   public void reset(boolean preserveCookies) {
      this.resetForForward();
      this._status = 200;
      this._reason = null;
      this._contentLength = -1L;
      List<HttpField> cookies = preserveCookies ? (List)this._fields.stream().filter((f) -> f.getHeader() == HttpHeader.SET_COOKIE).collect(Collectors.toList()) : null;
      this._fields.clear();
      String connection = this._channel.getRequest().getHeader(HttpHeader.CONNECTION.asString());
      if (connection != null) {
         for(String value : StringUtil.csvSplit((List)null, connection, 0, connection.length())) {
            HttpHeaderValue cb = (HttpHeaderValue)HttpHeaderValue.CACHE.get(value);
            if (cb != null) {
               switch (cb) {
                  case CLOSE:
                     this._fields.put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.toString());
                     break;
                  case KEEP_ALIVE:
                     if (HttpVersion.HTTP_1_0.is(this._channel.getRequest().getProtocol())) {
                        this._fields.put(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.toString());
                     }
                     break;
                  case TE:
                     this._fields.put(HttpHeader.CONNECTION, HttpHeaderValue.TE.toString());
               }
            }
         }
      }

      if (preserveCookies) {
         cookies.forEach((f) -> this._fields.add(f));
      } else {
         Request request = this.getHttpChannel().getRequest();
         HttpSession session = request.getSession(false);
         if (session != null && session.isNew()) {
            SessionManager sm = request.getSessionManager();
            if (sm != null) {
               HttpCookie c = sm.getSessionCookie(session, request.getContextPath(), request.isSecure());
               if (c != null) {
                  this.addCookie(c);
               }
            }
         }
      }

   }

   public void resetForForward() {
      this.resetBuffer();
      this._outputType = Response.OutputType.NONE;
   }

   public void resetBuffer() {
      this._out.resetBuffer();
   }

   protected MetaData.Response newResponseMetaData() {
      return new MetaData.Response(this._channel.getRequest().getHttpVersion(), this.getStatus(), this.getReason(), this._fields, this.getLongContentLength());
   }

   public MetaData.Response getCommittedMetaData() {
      MetaData.Response meta = this._channel.getCommittedMetaData();
      return meta == null ? this.newResponseMetaData() : meta;
   }

   public boolean isCommitted() {
      return this._channel.isCommitted();
   }

   public void setLocale(Locale locale) {
      if (locale != null && !this.isCommitted() && !this.isIncluding()) {
         this._locale = locale;
         this._fields.put(HttpHeader.CONTENT_LANGUAGE, locale.toString().replace('_', '-'));
         if (this._outputType == Response.OutputType.NONE) {
            if (this._channel.getRequest().getContext() != null) {
               String charset = this._channel.getRequest().getContext().getContextHandler().getLocaleEncoding(locale);
               if (charset != null && charset.length() > 0 && __localeOverride.contains(this._encodingFrom)) {
                  this.setCharacterEncoding(charset, Response.EncodingFrom.SET_LOCALE);
               }

            }
         }
      }
   }

   public Locale getLocale() {
      return this._locale == null ? Locale.getDefault() : this._locale;
   }

   public int getStatus() {
      return this._status;
   }

   public String getReason() {
      return this._reason;
   }

   public HttpFields getHttpFields() {
      return this._fields;
   }

   public long getContentCount() {
      return this._out.getWritten();
   }

   public String toString() {
      return String.format("%s %d %s%n%s", this._channel.getRequest().getHttpVersion(), this._status, this._reason == null ? "" : this._reason, this._fields);
   }

   public void putHeaders(HttpContent content, long contentLength, boolean etag) {
      HttpField lm = content.getLastModified();
      if (lm != null) {
         this._fields.put(lm);
      }

      if (contentLength == 0L) {
         this._fields.put(content.getContentLength());
         this._contentLength = content.getContentLengthValue();
      } else if (contentLength > 0L) {
         this._fields.putLongField(HttpHeader.CONTENT_LENGTH, contentLength);
         this._contentLength = contentLength;
      }

      HttpField ct = content.getContentType();
      if (ct != null) {
         this._fields.put(ct);
         this._contentType = ct.getValue();
         this._characterEncoding = content.getCharacterEncoding();
         this._mimeType = content.getMimeType();
      }

      HttpField ce = content.getContentEncoding();
      if (ce != null) {
         this._fields.put(ce);
      }

      if (etag) {
         HttpField et = content.getETag();
         if (et != null) {
            this._fields.put(et);
         }
      }

   }

   public static void putHeaders(HttpServletResponse response, HttpContent content, long contentLength, boolean etag) {
      long lml = content.getResource().lastModified();
      if (lml >= 0L) {
         response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(), lml);
      }

      if (contentLength == 0L) {
         contentLength = content.getContentLengthValue();
      }

      if (contentLength >= 0L) {
         if (contentLength < 2147483647L) {
            response.setContentLength((int)contentLength);
         } else {
            response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Long.toString(contentLength));
         }
      }

      String ct = content.getContentTypeValue();
      if (ct != null && response.getContentType() == null) {
         response.setContentType(ct);
      }

      String ce = content.getContentEncodingValue();
      if (ce != null) {
         response.setHeader(HttpHeader.CONTENT_ENCODING.asString(), ce);
      }

      if (etag) {
         String et = content.getETagValue();
         if (et != null) {
            response.setHeader(HttpHeader.ETAG.asString(), et);
         }
      }

   }

   static {
      __EXPIRES_01JAN1970 = new PreEncodedHttpField(HttpHeader.EXPIRES, DateGenerator.__01Jan1970);
      __cookieBuilder = new ThreadLocal<StringBuilder>() {
         protected StringBuilder initialValue() {
            return new StringBuilder(128);
         }
      };
      __localeOverride = EnumSet.of(Response.EncodingFrom.NOT_SET, Response.EncodingFrom.INFERRED);
   }

   public static enum OutputType {
      NONE,
      STREAM,
      WRITER;
   }

   private static enum EncodingFrom {
      NOT_SET,
      INFERRED,
      SET_LOCALE,
      SET_CONTENT_TYPE,
      SET_CHARACTER_ENCODING;
   }
}
