package org.spark_project.jetty.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.spark_project.jetty.http.BadMessageException;
import org.spark_project.jetty.http.HostPortHttpField;
import org.spark_project.jetty.http.HttpCookie;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpScheme;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.session.AbstractSession;
import org.spark_project.jetty.util.Attributes;
import org.spark_project.jetty.util.AttributesMap;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.MultiMap;
import org.spark_project.jetty.util.MultiPartInputStreamParser;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.UrlEncoded;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class Request implements HttpServletRequest {
   public static final String __MULTIPART_CONFIG_ELEMENT = "org.spark_project.jetty.multipartConfig";
   public static final String __MULTIPART_INPUT_STREAM = "org.spark_project.jetty.multiPartInputStream";
   public static final String __MULTIPART_CONTEXT = "org.spark_project.jetty.multiPartContext";
   private static final Logger LOG = Log.getLogger(Request.class);
   private static final Collection<Locale> __defaultLocale = Collections.singleton(Locale.getDefault());
   private static final int __NONE = 0;
   private static final int _STREAM = 1;
   private static final int __READER = 2;
   private static final MultiMap<String> NO_PARAMS = new MultiMap<String>();
   private final HttpChannel _channel;
   private final List<ServletRequestAttributeListener> _requestAttributeListeners = new ArrayList();
   private final HttpInput _input;
   private MetaData.Request _metaData;
   private String _contextPath;
   private String _servletPath;
   private String _pathInfo;
   private boolean _secure;
   private String _asyncNotSupportedSource = null;
   private boolean _newContext;
   private boolean _cookiesExtracted = false;
   private boolean _handled = false;
   private boolean _contentParamsExtracted;
   private boolean _requestedSessionIdFromCookie = false;
   private Attributes _attributes;
   private Authentication _authentication;
   private String _characterEncoding;
   private ContextHandler.Context _context;
   private CookieCutter _cookies;
   private DispatcherType _dispatcherType;
   private int _inputState = 0;
   private MultiMap<String> _queryParameters;
   private MultiMap<String> _contentParameters;
   private MultiMap<String> _parameters;
   private String _queryEncoding;
   private BufferedReader _reader;
   private String _readerEncoding;
   private InetSocketAddress _remote;
   private String _requestedSessionId;
   private Map<Object, HttpSession> _savedNewSessions;
   private UserIdentity.Scope _scope;
   private HttpSession _session;
   private SessionManager _sessionManager;
   private long _timeStamp;
   private MultiPartInputStreamParser _multiPartInputStream;
   private AsyncContextState _async;

   public static Request getBaseRequest(ServletRequest request) {
      if (request instanceof Request) {
         return (Request)request;
      } else {
         Object channel = request.getAttribute(HttpChannel.class.getName());
         if (channel instanceof HttpChannel) {
            return ((HttpChannel)channel).getRequest();
         } else {
            while(request instanceof ServletRequestWrapper) {
               request = ((ServletRequestWrapper)request).getRequest();
            }

            return request instanceof Request ? (Request)request : null;
         }
      }
   }

   public Request(HttpChannel channel, HttpInput input) {
      this._channel = channel;
      this._input = input;
   }

   public HttpFields getHttpFields() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         throw new IllegalStateException();
      } else {
         return metadata.getFields();
      }
   }

   public HttpInput getHttpInput() {
      return this._input;
   }

   public boolean isPush() {
      return Boolean.TRUE.equals(this.getAttribute("org.spark_project.jetty.pushed"));
   }

   public boolean isPushSupported() {
      return this.getHttpChannel().getHttpTransport().isPushSupported();
   }

   public PushBuilder getPushBuilder() {
      if (!this.isPushSupported()) {
         throw new IllegalStateException();
      } else {
         HttpFields fields = new HttpFields(this.getHttpFields().size() + 5);
         boolean conditional = false;

         for(HttpField field : this.getHttpFields()) {
            HttpHeader header = field.getHeader();
            if (header == null) {
               fields.add(field);
            } else {
               switch (header) {
                  case IF_MATCH:
                  case IF_RANGE:
                  case IF_UNMODIFIED_SINCE:
                  case RANGE:
                  case EXPECT:
                  case REFERER:
                  case COOKIE:
                  case AUTHORIZATION:
                     break;
                  case IF_NONE_MATCH:
                  case IF_MODIFIED_SINCE:
                     conditional = true;
                     break;
                  default:
                     fields.add(field);
               }
            }
         }

         String id = null;

         try {
            HttpSession session = this.getSession();
            if (session != null) {
               session.getLastAccessedTime();
               id = session.getId();
            } else {
               id = this.getRequestedSessionId();
            }
         } catch (IllegalStateException var6) {
            id = this.getRequestedSessionId();
         }

         PushBuilder builder = new PushBuilderImpl(this, fields, this.getMethod(), this.getQueryString(), id, conditional);
         builder.addHeader("referer", this.getRequestURL().toString());
         return builder;
      }
   }

   public void addEventListener(EventListener listener) {
      if (listener instanceof ServletRequestAttributeListener) {
         this._requestAttributeListeners.add((ServletRequestAttributeListener)listener);
      }

      if (listener instanceof AsyncListener) {
         throw new IllegalArgumentException(listener.getClass().toString());
      }
   }

   private MultiMap<String> getParameters() {
      if (!this._contentParamsExtracted) {
         this._contentParamsExtracted = true;
         if (this._contentParameters == null) {
            try {
               this.extractContentParameters();
            } catch (IllegalArgumentException | IllegalStateException e) {
               throw new BadMessageException("Unable to parse form content", e);
            }
         }
      }

      if (this._queryParameters == null) {
         try {
            this.extractQueryParameters();
         } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BadMessageException("Unable to parse URI query", e);
         }
      }

      if (this._queryParameters != NO_PARAMS && this._queryParameters.size() != 0) {
         if (this._contentParameters != NO_PARAMS && this._contentParameters.size() != 0) {
            this._parameters = new MultiMap<String>();
            this._parameters.addAllValues(this._queryParameters);
            this._parameters.addAllValues(this._contentParameters);
         } else {
            this._parameters = this._queryParameters;
         }
      } else {
         this._parameters = this._contentParameters;
      }

      MultiMap<String> parameters = this._parameters;
      return parameters == null ? NO_PARAMS : parameters;
   }

   private void extractQueryParameters() {
      MetaData.Request metadata = this._metaData;
      if (metadata != null && metadata.getURI() != null && metadata.getURI().hasQuery()) {
         this._queryParameters = new MultiMap<String>();
         if (this._queryEncoding == null) {
            metadata.getURI().decodeQueryTo(this._queryParameters);
         } else {
            try {
               metadata.getURI().decodeQueryTo(this._queryParameters, this._queryEncoding);
            } catch (UnsupportedEncodingException e) {
               if (LOG.isDebugEnabled()) {
                  LOG.warn(e);
               } else {
                  LOG.warn(e.toString());
               }
            }
         }
      } else {
         this._queryParameters = NO_PARAMS;
      }

   }

   private void extractContentParameters() {
      String contentType = this.getContentType();
      if (contentType != null && !contentType.isEmpty()) {
         this._contentParameters = new MultiMap<String>();
         contentType = HttpFields.valueParameters(contentType, (Map)null);
         int contentLength = this.getContentLength();
         if (contentLength != 0) {
            if (MimeTypes.Type.FORM_ENCODED.is(contentType) && this._inputState == 0 && this._channel.getHttpConfiguration().isFormEncodedMethod(this.getMethod())) {
               this.extractFormParameters(this._contentParameters);
            } else if (contentType.startsWith("multipart/form-data") && this.getAttribute("org.spark_project.jetty.multipartConfig") != null && this._multiPartInputStream == null) {
               this.extractMultipartParameters(this._contentParameters);
            }
         }
      } else {
         this._contentParameters = NO_PARAMS;
      }

   }

   public void extractFormParameters(MultiMap<String> params) {
      try {
         int maxFormContentSize = -1;
         int maxFormKeys = -1;
         if (this._context != null) {
            maxFormContentSize = this._context.getContextHandler().getMaxFormContentSize();
            maxFormKeys = this._context.getContextHandler().getMaxFormKeys();
         }

         if (maxFormContentSize < 0) {
            Object obj = this._channel.getServer().getAttribute("org.spark_project.jetty.server.Request.maxFormContentSize");
            if (obj == null) {
               maxFormContentSize = 200000;
            } else if (obj instanceof Number) {
               Number size = (Number)obj;
               maxFormContentSize = size.intValue();
            } else if (obj instanceof String) {
               maxFormContentSize = Integer.valueOf((String)obj);
            }
         }

         if (maxFormKeys < 0) {
            Object obj = this._channel.getServer().getAttribute("org.spark_project.jetty.server.Request.maxFormKeys");
            if (obj == null) {
               maxFormKeys = 1000;
            } else if (obj instanceof Number) {
               Number keys = (Number)obj;
               maxFormKeys = keys.intValue();
            } else if (obj instanceof String) {
               maxFormKeys = Integer.valueOf((String)obj);
            }
         }

         int contentLength = this.getContentLength();
         if (contentLength > maxFormContentSize && maxFormContentSize > 0) {
            throw new IllegalStateException("Form too large: " + contentLength + " > " + maxFormContentSize);
         }

         InputStream in = this.getInputStream();
         if (this._input.isAsync()) {
            throw new IllegalStateException("Cannot extract parameters with async IO");
         }

         UrlEncoded.decodeTo(in, params, this.getCharacterEncoding(), contentLength < 0 ? maxFormContentSize : -1, maxFormKeys);
      } catch (IOException e) {
         if (LOG.isDebugEnabled()) {
            LOG.warn(e);
         } else {
            LOG.warn(e.toString());
         }
      }

   }

   private void extractMultipartParameters(MultiMap<String> result) {
      try {
         this.getParts(result);
      } catch (ServletException | IOException e) {
         LOG.warn(e);
         throw new RuntimeException(e);
      }
   }

   public AsyncContext getAsyncContext() {
      HttpChannelState state = this.getHttpChannelState();
      if (this._async != null && state.isAsyncStarted()) {
         return this._async;
      } else {
         throw new IllegalStateException(state.getStatusString());
      }
   }

   public HttpChannelState getHttpChannelState() {
      return this._channel.getState();
   }

   public Object getAttribute(String name) {
      if (name.startsWith("org.spark_project.jetty")) {
         if (Server.class.getName().equals(name)) {
            return this._channel.getServer();
         }

         if (HttpChannel.class.getName().equals(name)) {
            return this._channel;
         }

         if (HttpConnection.class.getName().equals(name) && this._channel.getHttpTransport() instanceof HttpConnection) {
            return this._channel.getHttpTransport();
         }
      }

      return this._attributes == null ? null : this._attributes.getAttribute(name);
   }

   public Enumeration<String> getAttributeNames() {
      return this._attributes == null ? Collections.enumeration(Collections.emptyList()) : AttributesMap.getAttributeNamesCopy(this._attributes);
   }

   public Attributes getAttributes() {
      if (this._attributes == null) {
         this._attributes = new AttributesMap();
      }

      return this._attributes;
   }

   public Authentication getAuthentication() {
      return this._authentication;
   }

   public String getAuthType() {
      if (this._authentication instanceof Authentication.Deferred) {
         this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
      }

      return this._authentication instanceof Authentication.User ? ((Authentication.User)this._authentication).getAuthMethod() : null;
   }

   public String getCharacterEncoding() {
      if (this._characterEncoding == null) {
         this.getContentType();
      }

      return this._characterEncoding;
   }

   public HttpChannel getHttpChannel() {
      return this._channel;
   }

   public int getContentLength() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return -1;
      } else {
         return metadata.getContentLength() != Long.MIN_VALUE ? (int)metadata.getContentLength() : (int)metadata.getFields().getLongField(HttpHeader.CONTENT_LENGTH.toString());
      }
   }

   public long getContentLengthLong() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return -1L;
      } else {
         return metadata.getContentLength() != Long.MIN_VALUE ? metadata.getContentLength() : metadata.getFields().getLongField(HttpHeader.CONTENT_LENGTH.toString());
      }
   }

   public long getContentRead() {
      return this._input.getContentConsumed();
   }

   public String getContentType() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return null;
      } else {
         String content_type = metadata.getFields().get(HttpHeader.CONTENT_TYPE);
         if (this._characterEncoding == null && content_type != null) {
            MimeTypes.Type mime = (MimeTypes.Type)MimeTypes.CACHE.get(content_type);
            String charset = mime != null && mime.getCharset() != null ? mime.getCharset().toString() : MimeTypes.getCharsetFromContentType(content_type);
            if (charset != null) {
               this._characterEncoding = charset;
            }
         }

         return content_type;
      }
   }

   public ContextHandler.Context getContext() {
      return this._context;
   }

   public String getContextPath() {
      return this._contextPath;
   }

   public Cookie[] getCookies() {
      MetaData.Request metadata = this._metaData;
      if (metadata != null && !this._cookiesExtracted) {
         this._cookiesExtracted = true;

         for(String c : metadata.getFields().getValuesList(HttpHeader.COOKIE)) {
            if (this._cookies == null) {
               this._cookies = new CookieCutter();
            }

            this._cookies.addCookieField(c);
         }

         if (this._cookies != null && this._cookies.getCookies().length != 0) {
            return this._cookies.getCookies();
         } else {
            return null;
         }
      } else {
         return this._cookies != null && this._cookies.getCookies().length != 0 ? this._cookies.getCookies() : null;
      }
   }

   public long getDateHeader(String name) {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? -1L : metadata.getFields().getDateField(name);
   }

   public DispatcherType getDispatcherType() {
      return this._dispatcherType;
   }

   public String getHeader(String name) {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? null : metadata.getFields().get(name);
   }

   public Enumeration<String> getHeaderNames() {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? Collections.emptyEnumeration() : metadata.getFields().getFieldNames();
   }

   public Enumeration<String> getHeaders(String name) {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return Collections.emptyEnumeration();
      } else {
         Enumeration<String> e = metadata.getFields().getValues(name);
         return e == null ? Collections.enumeration(Collections.emptyList()) : e;
      }
   }

   public int getInputState() {
      return this._inputState;
   }

   public ServletInputStream getInputStream() throws IOException {
      if (this._inputState != 0 && this._inputState != 1) {
         throw new IllegalStateException("READER");
      } else {
         this._inputState = 1;
         if (this._channel.isExpecting100Continue()) {
            this._channel.continue100(this._input.available());
         }

         return this._input;
      }
   }

   public int getIntHeader(String name) {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? -1 : (int)metadata.getFields().getLongField(name);
   }

   public Locale getLocale() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return Locale.getDefault();
      } else {
         List<String> acceptable = metadata.getFields().getQualityCSV(HttpHeader.ACCEPT_LANGUAGE);
         if (acceptable.isEmpty()) {
            return Locale.getDefault();
         } else {
            String language = (String)acceptable.get(0);
            language = HttpFields.stripParameters(language);
            String country = "";
            int dash = language.indexOf(45);
            if (dash > -1) {
               country = language.substring(dash + 1).trim();
               language = language.substring(0, dash).trim();
            }

            return new Locale(language, country);
         }
      }
   }

   public Enumeration<Locale> getLocales() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return Collections.enumeration(__defaultLocale);
      } else {
         List<String> acceptable = metadata.getFields().getQualityCSV(HttpHeader.ACCEPT_LANGUAGE);
         if (acceptable.isEmpty()) {
            return Collections.enumeration(__defaultLocale);
         } else {
            List<Locale> locales = (List)acceptable.stream().map((language) -> {
               language = HttpFields.stripParameters(language);
               String country = "";
               int dash = language.indexOf(45);
               if (dash > -1) {
                  country = language.substring(dash + 1).trim();
                  language = language.substring(0, dash).trim();
               }

               return new Locale(language, country);
            }).collect(Collectors.toList());
            return Collections.enumeration(locales);
         }
      }
   }

   public String getLocalAddr() {
      if (this._channel == null) {
         try {
            String name = InetAddress.getLocalHost().getHostAddress();
            if ("0.0.0.0".equals(name)) {
               return null;
            }

            return name;
         } catch (UnknownHostException e) {
            LOG.ignore(e);
         }
      }

      InetSocketAddress local = this._channel.getLocalAddress();
      if (local == null) {
         return "";
      } else {
         InetAddress address = local.getAddress();
         return address == null ? local.getHostString() : address.getHostAddress();
      }
   }

   public String getLocalName() {
      if (this._channel == null) {
         try {
            String name = InetAddress.getLocalHost().getHostName();
            if ("0.0.0.0".equals(name)) {
               return null;
            }

            return name;
         } catch (UnknownHostException e) {
            LOG.ignore(e);
         }
      }

      InetSocketAddress local = this._channel.getLocalAddress();
      return local.getHostString();
   }

   public int getLocalPort() {
      if (this._channel == null) {
         return 0;
      } else {
         InetSocketAddress local = this._channel.getLocalAddress();
         return local.getPort();
      }
   }

   public String getMethod() {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? null : metadata.getMethod();
   }

   public String getParameter(String name) {
      return (String)this.getParameters().getValue(name, 0);
   }

   public Map<String, String[]> getParameterMap() {
      return Collections.unmodifiableMap(this.getParameters().toStringArrayMap());
   }

   public Enumeration<String> getParameterNames() {
      return Collections.enumeration(this.getParameters().keySet());
   }

   public String[] getParameterValues(String name) {
      List<String> vals = this.getParameters().getValues(name);
      return vals == null ? null : (String[])vals.toArray(new String[vals.size()]);
   }

   public MultiMap<String> getQueryParameters() {
      return this._queryParameters;
   }

   public void setQueryParameters(MultiMap<String> queryParameters) {
      this._queryParameters = queryParameters;
   }

   public void setContentParameters(MultiMap<String> contentParameters) {
      this._contentParameters = contentParameters;
   }

   public void resetParameters() {
      this._parameters = null;
   }

   public String getPathInfo() {
      return this._pathInfo;
   }

   public String getPathTranslated() {
      return this._pathInfo != null && this._context != null ? this._context.getRealPath(this._pathInfo) : null;
   }

   public String getProtocol() {
      MetaData.Request metadata = this._metaData;
      if (metadata == null) {
         return null;
      } else {
         HttpVersion version = metadata.getHttpVersion();
         return version == null ? null : version.toString();
      }
   }

   public HttpVersion getHttpVersion() {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? null : metadata.getHttpVersion();
   }

   public String getQueryEncoding() {
      return this._queryEncoding;
   }

   public String getQueryString() {
      MetaData.Request metadata = this._metaData;
      return metadata.getURI().getQuery();
   }

   public BufferedReader getReader() throws IOException {
      if (this._inputState != 0 && this._inputState != 2) {
         throw new IllegalStateException("STREAMED");
      } else if (this._inputState == 2) {
         return this._reader;
      } else {
         String encoding = this.getCharacterEncoding();
         if (encoding == null) {
            encoding = "iso-8859-1";
         }

         if (this._reader == null || !encoding.equalsIgnoreCase(this._readerEncoding)) {
            final ServletInputStream in = this.getInputStream();
            this._readerEncoding = encoding;
            this._reader = new BufferedReader(new InputStreamReader(in, encoding)) {
               public void close() throws IOException {
                  in.close();
               }
            };
         }

         this._inputState = 2;
         return this._reader;
      }
   }

   public String getRealPath(String path) {
      return this._context == null ? null : this._context.getRealPath(path);
   }

   public InetSocketAddress getRemoteInetSocketAddress() {
      InetSocketAddress remote = this._remote;
      if (remote == null) {
         remote = this._channel.getRemoteAddress();
      }

      return remote;
   }

   public String getRemoteAddr() {
      InetSocketAddress remote = this._remote;
      if (remote == null) {
         remote = this._channel.getRemoteAddress();
      }

      if (remote == null) {
         return "";
      } else {
         InetAddress address = remote.getAddress();
         return address == null ? remote.getHostString() : address.getHostAddress();
      }
   }

   public String getRemoteHost() {
      InetSocketAddress remote = this._remote;
      if (remote == null) {
         remote = this._channel.getRemoteAddress();
      }

      return remote == null ? "" : remote.getHostString();
   }

   public int getRemotePort() {
      InetSocketAddress remote = this._remote;
      if (remote == null) {
         remote = this._channel.getRemoteAddress();
      }

      return remote == null ? 0 : remote.getPort();
   }

   public String getRemoteUser() {
      Principal p = this.getUserPrincipal();
      return p == null ? null : p.getName();
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      if (path != null && this._context != null) {
         if (!path.startsWith("/")) {
            String relTo = URIUtil.addPaths(this._servletPath, this._pathInfo);
            int slash = relTo.lastIndexOf("/");
            if (slash > 1) {
               relTo = relTo.substring(0, slash + 1);
            } else {
               relTo = "/";
            }

            path = URIUtil.addPaths(relTo, path);
         }

         return this._context.getRequestDispatcher(path);
      } else {
         return null;
      }
   }

   public String getRequestedSessionId() {
      return this._requestedSessionId;
   }

   public String getRequestURI() {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? null : metadata.getURI().getPath();
   }

   public StringBuffer getRequestURL() {
      StringBuffer url = new StringBuffer(128);
      URIUtil.appendSchemeHostPort(url, this.getScheme(), this.getServerName(), this.getServerPort());
      url.append(this.getRequestURI());
      return url;
   }

   public Response getResponse() {
      return this._channel.getResponse();
   }

   public StringBuilder getRootURL() {
      StringBuilder url = new StringBuilder(128);
      URIUtil.appendSchemeHostPort(url, this.getScheme(), this.getServerName(), this.getServerPort());
      return url;
   }

   public String getScheme() {
      MetaData.Request metadata = this._metaData;
      String scheme = metadata == null ? null : metadata.getURI().getScheme();
      return scheme == null ? HttpScheme.HTTP.asString() : scheme;
   }

   public String getServerName() {
      MetaData.Request metadata = this._metaData;
      String name = metadata == null ? null : metadata.getURI().getHost();
      return name != null ? name : this.findServerName();
   }

   private String findServerName() {
      MetaData.Request metadata = this._metaData;
      HttpField host = metadata == null ? null : metadata.getFields().getField(HttpHeader.HOST);
      if (host != null) {
         if (!(host instanceof HostPortHttpField) && host.getValue() != null && !host.getValue().isEmpty()) {
            host = new HostPortHttpField(host.getValue());
         }

         if (host instanceof HostPortHttpField) {
            HostPortHttpField authority = (HostPortHttpField)host;
            metadata.getURI().setAuthority(authority.getHost(), authority.getPort());
            return authority.getHost();
         }
      }

      String name = this.getLocalName();
      if (name != null) {
         return name;
      } else {
         try {
            return InetAddress.getLocalHost().getHostAddress();
         } catch (UnknownHostException e) {
            LOG.ignore(e);
            return null;
         }
      }
   }

   public int getServerPort() {
      MetaData.Request metadata = this._metaData;
      HttpURI uri = metadata == null ? null : metadata.getURI();
      int port = uri != null && uri.getHost() != null ? uri.getPort() : this.findServerPort();
      if (port <= 0) {
         return this.getScheme().equalsIgnoreCase("https") ? 443 : 80;
      } else {
         return port;
      }
   }

   private int findServerPort() {
      MetaData.Request metadata = this._metaData;
      HttpField host = metadata == null ? null : metadata.getFields().getField(HttpHeader.HOST);
      if (host != null) {
         HostPortHttpField authority = host instanceof HostPortHttpField ? (HostPortHttpField)host : new HostPortHttpField(host.getValue());
         metadata.getURI().setAuthority(authority.getHost(), authority.getPort());
         return authority.getPort();
      } else {
         return this._channel != null ? this.getLocalPort() : -1;
      }
   }

   public ServletContext getServletContext() {
      return this._context;
   }

   public String getServletName() {
      return this._scope != null ? this._scope.getName() : null;
   }

   public String getServletPath() {
      if (this._servletPath == null) {
         this._servletPath = "";
      }

      return this._servletPath;
   }

   public ServletResponse getServletResponse() {
      return this._channel.getResponse();
   }

   public String changeSessionId() {
      HttpSession session = this.getSession(false);
      if (session == null) {
         throw new IllegalStateException("No session");
      } else {
         if (session instanceof AbstractSession) {
            AbstractSession abstractSession = (AbstractSession)session;
            abstractSession.renewId(this);
            if (this.getRemoteUser() != null) {
               abstractSession.setAttribute("org.spark_project.jetty.security.sessionCreatedSecure", Boolean.TRUE);
            }

            if (abstractSession.isIdChanged()) {
               this._channel.getResponse().addCookie(this._sessionManager.getSessionCookie(abstractSession, this.getContextPath(), this.isSecure()));
            }
         }

         return session.getId();
      }
   }

   public HttpSession getSession() {
      return this.getSession(true);
   }

   public HttpSession getSession(boolean create) {
      if (this._session != null) {
         if (this._sessionManager == null || this._sessionManager.isValid(this._session)) {
            return this._session;
         }

         this._session = null;
      }

      if (!create) {
         return null;
      } else if (this.getResponse().isCommitted()) {
         throw new IllegalStateException("Response is committed");
      } else if (this._sessionManager == null) {
         throw new IllegalStateException("No SessionManager");
      } else {
         this._session = this._sessionManager.newHttpSession(this);
         HttpCookie cookie = this._sessionManager.getSessionCookie(this._session, this.getContextPath(), this.isSecure());
         if (cookie != null) {
            this._channel.getResponse().addCookie(cookie);
         }

         return this._session;
      }
   }

   public SessionManager getSessionManager() {
      return this._sessionManager;
   }

   public long getTimeStamp() {
      return this._timeStamp;
   }

   public HttpURI getHttpURI() {
      MetaData.Request metadata = this._metaData;
      return metadata == null ? null : metadata.getURI();
   }

   public void setHttpURI(HttpURI uri) {
      MetaData.Request metadata = this._metaData;
      metadata.setURI(uri);
   }

   public UserIdentity getUserIdentity() {
      if (this._authentication instanceof Authentication.Deferred) {
         this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
      }

      return this._authentication instanceof Authentication.User ? ((Authentication.User)this._authentication).getUserIdentity() : null;
   }

   public UserIdentity getResolvedUserIdentity() {
      return this._authentication instanceof Authentication.User ? ((Authentication.User)this._authentication).getUserIdentity() : null;
   }

   public UserIdentity.Scope getUserIdentityScope() {
      return this._scope;
   }

   public Principal getUserPrincipal() {
      if (this._authentication instanceof Authentication.Deferred) {
         this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
      }

      if (this._authentication instanceof Authentication.User) {
         UserIdentity user = ((Authentication.User)this._authentication).getUserIdentity();
         return user.getUserPrincipal();
      } else {
         return null;
      }
   }

   public boolean isHandled() {
      return this._handled;
   }

   public boolean isAsyncStarted() {
      return this.getHttpChannelState().isAsyncStarted();
   }

   public boolean isAsyncSupported() {
      return this._asyncNotSupportedSource == null;
   }

   public boolean isRequestedSessionIdFromCookie() {
      return this._requestedSessionId != null && this._requestedSessionIdFromCookie;
   }

   public boolean isRequestedSessionIdFromUrl() {
      return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
   }

   public boolean isRequestedSessionIdFromURL() {
      return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
   }

   public boolean isRequestedSessionIdValid() {
      if (this._requestedSessionId == null) {
         return false;
      } else {
         HttpSession session = this.getSession(false);
         return session != null && this._sessionManager.getSessionIdManager().getClusterId(this._requestedSessionId).equals(this._sessionManager.getClusterId(session));
      }
   }

   public boolean isSecure() {
      return this._secure;
   }

   public void setSecure(boolean secure) {
      this._secure = secure;
   }

   public boolean isUserInRole(String role) {
      if (this._authentication instanceof Authentication.Deferred) {
         this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
      }

      return this._authentication instanceof Authentication.User ? ((Authentication.User)this._authentication).isUserInRole(this._scope, role) : false;
   }

   public HttpSession recoverNewSession(Object key) {
      return this._savedNewSessions == null ? null : (HttpSession)this._savedNewSessions.get(key);
   }

   public void setMetaData(MetaData.Request request) {
      this._metaData = request;
      this.setMethod(request.getMethod());
      HttpURI uri = request.getURI();
      String path = uri.getDecodedPath();
      String info;
      if (path != null && path.length() != 0) {
         if (!path.startsWith("/")) {
            if (!"*".equals(path) && !HttpMethod.CONNECT.is(this.getMethod())) {
               this.setPathInfo(path);
               throw new BadMessageException(400, "Bad URI");
            }

            info = path;
         } else {
            info = URIUtil.canonicalPath(path);
         }
      } else {
         if (!uri.isAbsolute()) {
            this.setPathInfo("");
            throw new BadMessageException(400, "Bad URI");
         }

         path = "/";
         uri.setPath(path);
         info = path;
      }

      if (info == null) {
         this.setPathInfo(path);
         throw new BadMessageException(400, "Bad URI");
      } else {
         this.setPathInfo(info);
      }
   }

   public MetaData.Request getMetaData() {
      return this._metaData;
   }

   public boolean hasMetaData() {
      return this._metaData != null;
   }

   protected void recycle() {
      this._metaData = null;
      if (this._context != null) {
         throw new IllegalStateException("Request in context!");
      } else {
         if (this._inputState == 2) {
            try {
               for(int r = this._reader.read(); r != -1; r = this._reader.read()) {
               }
            } catch (Exception e) {
               LOG.ignore(e);
               this._reader = null;
            }
         }

         this._dispatcherType = null;
         this.setAuthentication(Authentication.NOT_CHECKED);
         this.getHttpChannelState().recycle();
         if (this._async != null) {
            this._async.reset();
         }

         this._async = null;
         this._asyncNotSupportedSource = null;
         this._handled = false;
         if (this._attributes != null) {
            this._attributes.clearAttributes();
         }

         this._characterEncoding = null;
         this._contextPath = null;
         if (this._cookies != null) {
            this._cookies.reset();
         }

         this._cookiesExtracted = false;
         this._context = null;
         this._newContext = false;
         this._pathInfo = null;
         this._queryEncoding = null;
         this._requestedSessionId = null;
         this._requestedSessionIdFromCookie = false;
         this._secure = false;
         this._session = null;
         this._sessionManager = null;
         this._scope = null;
         this._servletPath = null;
         this._timeStamp = 0L;
         this._queryParameters = null;
         this._contentParameters = null;
         this._parameters = null;
         this._contentParamsExtracted = false;
         this._inputState = 0;
         if (this._savedNewSessions != null) {
            this._savedNewSessions.clear();
         }

         this._savedNewSessions = null;
         this._multiPartInputStream = null;
         this._remote = null;
         this._input.recycle();
      }
   }

   public void removeAttribute(String name) {
      Object old_value = this._attributes == null ? null : this._attributes.getAttribute(name);
      if (this._attributes != null) {
         this._attributes.removeAttribute(name);
      }

      if (old_value != null && !this._requestAttributeListeners.isEmpty()) {
         ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, old_value);

         for(ServletRequestAttributeListener listener : this._requestAttributeListeners) {
            listener.attributeRemoved(event);
         }
      }

   }

   public void removeEventListener(EventListener listener) {
      this._requestAttributeListeners.remove(listener);
   }

   public void saveNewSession(Object key, HttpSession session) {
      if (this._savedNewSessions == null) {
         this._savedNewSessions = new HashMap();
      }

      this._savedNewSessions.put(key, session);
   }

   public void setAsyncSupported(boolean supported, String source) {
      this._asyncNotSupportedSource = supported ? null : (source == null ? "unknown" : source);
   }

   public void setAttribute(String name, Object value) {
      Object old_value = this._attributes == null ? null : this._attributes.getAttribute(name);
      if ("org.spark_project.jetty.server.Request.queryEncoding".equals(name)) {
         this.setQueryEncoding(value == null ? null : value.toString());
      } else if ("org.spark_project.jetty.server.sendContent".equals(name)) {
         LOG.warn("Deprecated: org.eclipse.jetty.server.sendContent");
      }

      if (this._attributes == null) {
         this._attributes = new AttributesMap();
      }

      this._attributes.setAttribute(name, value);
      if (!this._requestAttributeListeners.isEmpty()) {
         ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, old_value == null ? value : old_value);

         for(ServletRequestAttributeListener l : this._requestAttributeListeners) {
            if (old_value == null) {
               l.attributeAdded(event);
            } else if (value == null) {
               l.attributeRemoved(event);
            } else {
               l.attributeReplaced(event);
            }
         }
      }

   }

   public void setAttributes(Attributes attributes) {
      this._attributes = attributes;
   }

   public void setAuthentication(Authentication authentication) {
      this._authentication = authentication;
   }

   public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
      if (this._inputState == 0) {
         this._characterEncoding = encoding;
         if (!StringUtil.isUTF8(encoding)) {
            try {
               Charset.forName(encoding);
            } catch (UnsupportedCharsetException e) {
               throw new UnsupportedEncodingException(e.getMessage());
            }
         }

      }
   }

   public void setCharacterEncodingUnchecked(String encoding) {
      this._characterEncoding = encoding;
   }

   public void setContentType(String contentType) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.getFields().put(HttpHeader.CONTENT_TYPE, contentType);
      }

   }

   public void setContext(ContextHandler.Context context) {
      this._newContext = this._context != context;
      this._context = context;
   }

   public boolean takeNewContext() {
      boolean nc = this._newContext;
      this._newContext = false;
      return nc;
   }

   public void setContextPath(String contextPath) {
      this._contextPath = contextPath;
   }

   public void setCookies(Cookie[] cookies) {
      if (this._cookies == null) {
         this._cookies = new CookieCutter();
      }

      this._cookies.setCookies(cookies);
   }

   public void setDispatcherType(DispatcherType type) {
      this._dispatcherType = type;
   }

   public void setHandled(boolean h) {
      this._handled = h;
   }

   public void setMethod(String method) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.setMethod(method);
      }

   }

   public void setHttpVersion(HttpVersion version) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.setHttpVersion(version);
      }

   }

   public boolean isHead() {
      MetaData.Request metadata = this._metaData;
      return metadata != null && HttpMethod.HEAD.is(metadata.getMethod());
   }

   public void setPathInfo(String pathInfo) {
      this._pathInfo = pathInfo;
   }

   public void setQueryEncoding(String queryEncoding) {
      this._queryEncoding = queryEncoding;
   }

   public void setQueryString(String queryString) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.getURI().setQuery(queryString);
      }

      this._queryEncoding = null;
   }

   public void setRemoteAddr(InetSocketAddress addr) {
      this._remote = addr;
   }

   public void setRequestedSessionId(String requestedSessionId) {
      this._requestedSessionId = requestedSessionId;
   }

   public void setRequestedSessionIdFromCookie(boolean requestedSessionIdCookie) {
      this._requestedSessionIdFromCookie = requestedSessionIdCookie;
   }

   public void setURIPathQuery(String requestURI) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.getURI().setPathQuery(requestURI);
      }

   }

   public void setScheme(String scheme) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.getURI().setScheme(scheme);
      }

   }

   public void setAuthority(String host, int port) {
      MetaData.Request metadata = this._metaData;
      if (metadata != null) {
         metadata.getURI().setAuthority(host, port);
      }

   }

   public void setServletPath(String servletPath) {
      this._servletPath = servletPath;
   }

   public void setSession(HttpSession session) {
      this._session = session;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this._sessionManager = sessionManager;
   }

   public void setTimeStamp(long ts) {
      this._timeStamp = ts;
   }

   public void setUserIdentityScope(UserIdentity.Scope scope) {
      this._scope = scope;
   }

   public AsyncContext startAsync() throws IllegalStateException {
      if (this._asyncNotSupportedSource != null) {
         throw new IllegalStateException("!asyncSupported: " + this._asyncNotSupportedSource);
      } else {
         HttpChannelState state = this.getHttpChannelState();
         if (this._async == null) {
            this._async = new AsyncContextState(state);
         }

         AsyncContextEvent event = new AsyncContextEvent(this._context, this._async, state, this, this, this.getResponse());
         state.startAsync(event);
         return this._async;
      }
   }

   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
      if (this._asyncNotSupportedSource != null) {
         throw new IllegalStateException("!asyncSupported: " + this._asyncNotSupportedSource);
      } else {
         HttpChannelState state = this.getHttpChannelState();
         if (this._async == null) {
            this._async = new AsyncContextState(state);
         }

         AsyncContextEvent event = new AsyncContextEvent(this._context, this._async, state, this, servletRequest, servletResponse);
         event.setDispatchContext(this.getServletContext());
         event.setDispatchPath(URIUtil.encodePath(URIUtil.addPaths(this.getServletPath(), this.getPathInfo())));
         state.startAsync(event);
         return this._async;
      }
   }

   public String toString() {
      return String.format("%s%s%s %s%s@%x", this.getClass().getSimpleName(), this._handled ? "[" : "(", this.getMethod(), this.getHttpURI(), this._handled ? "]" : ")", this.hashCode());
   }

   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
      if (this._authentication instanceof Authentication.Deferred) {
         this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this, response));
         return !(this._authentication instanceof Authentication.ResponseSent);
      } else {
         response.sendError(401);
         return false;
      }
   }

   public Part getPart(String name) throws IOException, ServletException {
      this.getParts();
      return this._multiPartInputStream.getPart(name);
   }

   public Collection<Part> getParts() throws IOException, ServletException {
      if (this.getContentType() != null && this.getContentType().startsWith("multipart/form-data")) {
         return this.getParts((MultiMap)null);
      } else {
         throw new ServletException("Content-Type != multipart/form-data");
      }
   }

   private Collection<Part> getParts(MultiMap<String> params) throws IOException, ServletException {
      if (this._multiPartInputStream == null) {
         this._multiPartInputStream = (MultiPartInputStreamParser)this.getAttribute("org.spark_project.jetty.multiPartInputStream");
      }

      if (this._multiPartInputStream == null) {
         MultipartConfigElement config = (MultipartConfigElement)this.getAttribute("org.spark_project.jetty.multipartConfig");
         if (config == null) {
            throw new IllegalStateException("No multipart config for servlet");
         }

         this._multiPartInputStream = new MultiPartInputStreamParser(this.getInputStream(), this.getContentType(), config, this._context != null ? (File)this._context.getAttribute("javax.servlet.context.tempdir") : null);
         this.setAttribute("org.spark_project.jetty.multiPartInputStream", this._multiPartInputStream);
         this.setAttribute("org.spark_project.jetty.multiPartContext", this._context);
         Collection<Part> parts = this._multiPartInputStream.getParts();
         ByteArrayOutputStream os = null;

         for(Part p : parts) {
            MultiPartInputStreamParser.MultiPart mp = (MultiPartInputStreamParser.MultiPart)p;
            if (mp.getContentDispositionFilename() == null) {
               String charset = null;
               if (mp.getContentType() != null) {
                  charset = MimeTypes.getCharsetFromContentType(mp.getContentType());
               }

               InputStream is = mp.getInputStream();
               Throwable var10 = null;

               try {
                  if (os == null) {
                     os = new ByteArrayOutputStream();
                  }

                  IO.copy((InputStream)is, (OutputStream)os);
                  String content = new String(os.toByteArray(), charset == null ? StandardCharsets.UTF_8 : Charset.forName(charset));
                  if (this._contentParameters == null) {
                     this._contentParameters = params == null ? new MultiMap() : params;
                  }

                  this._contentParameters.add(mp.getName(), content);
               } catch (Throwable var19) {
                  var10 = var19;
                  throw var19;
               } finally {
                  if (is != null) {
                     if (var10 != null) {
                        try {
                           is.close();
                        } catch (Throwable var18) {
                           var10.addSuppressed(var18);
                        }
                     } else {
                        is.close();
                     }
                  }

               }

               os.reset();
            }
         }
      }

      return this._multiPartInputStream.getParts();
   }

   public void login(String username, String password) throws ServletException {
      if (this._authentication instanceof Authentication.Deferred) {
         this._authentication = ((Authentication.Deferred)this._authentication).login(username, password, this);
         if (this._authentication == null) {
            throw new Authentication.Failed("Authentication failed for username '" + username + "'");
         }
      } else {
         throw new Authentication.Failed("Authenticated failed for username '" + username + "'. Already authenticated as " + this._authentication);
      }
   }

   public void logout() throws ServletException {
      if (this._authentication instanceof Authentication.User) {
         ((Authentication.User)this._authentication).logout();
      }

      this._authentication = Authentication.UNAUTHENTICATED;
   }

   public void mergeQueryParameters(String oldQuery, String newQuery, boolean updateQueryString) {
      MultiMap<String> newQueryParams = null;
      if (newQuery != null) {
         newQueryParams = new MultiMap<String>();
         UrlEncoded.decodeTo(newQuery, newQueryParams, UrlEncoded.ENCODING);
      }

      MultiMap<String> oldQueryParams = this._queryParameters;
      if (oldQueryParams == null && oldQuery != null) {
         oldQueryParams = new MultiMap<String>();
         UrlEncoded.decodeTo(oldQuery, oldQueryParams, this.getQueryEncoding());
      }

      MultiMap<String> mergedQueryParams;
      if (newQueryParams != null && newQueryParams.size() != 0) {
         if (oldQueryParams != null && oldQueryParams.size() != 0) {
            mergedQueryParams = new MultiMap<String>(newQueryParams);
            mergedQueryParams.addAllValues(oldQueryParams);
         } else {
            mergedQueryParams = newQueryParams == null ? NO_PARAMS : newQueryParams;
         }
      } else {
         mergedQueryParams = oldQueryParams == null ? NO_PARAMS : oldQueryParams;
      }

      this.setQueryParameters(mergedQueryParams);
      this.resetParameters();
      if (updateQueryString) {
         if (newQuery == null) {
            this.setQueryString(oldQuery);
         } else if (oldQuery == null) {
            this.setQueryString(newQuery);
         } else {
            StringBuilder mergedQuery = new StringBuilder();
            if (newQuery != null) {
               mergedQuery.append(newQuery);
            }

            for(Map.Entry<String, List<String>> entry : mergedQueryParams.entrySet()) {
               if (newQueryParams == null || !newQueryParams.containsKey(entry.getKey())) {
                  for(String value : (List)entry.getValue()) {
                     if (mergedQuery.length() > 0) {
                        mergedQuery.append("&");
                     }

                     URIUtil.encodePath(mergedQuery, (String)entry.getKey());
                     mergedQuery.append('=');
                     URIUtil.encodePath(mergedQuery, value);
                  }
               }
            }

            this.setQueryString(mergedQuery.toString());
         }
      }

   }

   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
      throw new ServletException("HttpServletRequest.upgrade() not supported in Jetty");
   }
}
