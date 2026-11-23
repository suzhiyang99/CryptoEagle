package org.spark_project.jetty.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.AsyncContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.DateParser;
import org.spark_project.jetty.http.GzipHttpContent;
import org.spark_project.jetty.http.HttpContent;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.http.PathMap;
import org.spark_project.jetty.http.PreEncodedHttpField;
import org.spark_project.jetty.http.QuotedCSV;
import org.spark_project.jetty.io.WriterOutputStream;
import org.spark_project.jetty.server.HttpOutput;
import org.spark_project.jetty.server.InclusiveByteRange;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.ResourceCache;
import org.spark_project.jetty.server.ResourceContentFactory;
import org.spark_project.jetty.server.Response;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.MultiPartOutputStream;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.resource.ResourceCollection;
import org.spark_project.jetty.util.resource.ResourceFactory;

public class DefaultServlet extends HttpServlet implements ResourceFactory {
   private static final Logger LOG = Log.getLogger(DefaultServlet.class);
   private static final long serialVersionUID = 4930458713846881193L;
   private static final PreEncodedHttpField ACCEPT_RANGES;
   private ServletContext _servletContext;
   private ContextHandler _contextHandler;
   private boolean _acceptRanges = true;
   private boolean _dirAllowed = true;
   private boolean _welcomeServlets = false;
   private boolean _welcomeExactServlets = false;
   private boolean _redirectWelcome = false;
   private boolean _gzip = false;
   private boolean _pathInfoOnly = false;
   private boolean _etags = false;
   private Resource _resourceBase;
   private ResourceCache _cache;
   private HttpContent.Factory _contentFactory;
   private MimeTypes _mimeTypes;
   private String[] _welcomes;
   private Resource _stylesheet;
   private boolean _useFileMappedBuffer = false;
   private HttpField _cacheControl;
   private String _relativeResourceBase;
   private ServletHandler _servletHandler;
   private ServletHolder _defaultHolder;
   private List<String> _gzipEquivalentFileExtensions;

   public void init() throws UnavailableException {
      this._servletContext = this.getServletContext();
      this._contextHandler = this.initContextHandler(this._servletContext);
      this._mimeTypes = this._contextHandler.getMimeTypes();
      this._welcomes = this._contextHandler.getWelcomeFiles();
      if (this._welcomes == null) {
         this._welcomes = new String[]{"index.html", "index.jsp"};
      }

      this._acceptRanges = this.getInitBoolean("acceptRanges", this._acceptRanges);
      this._dirAllowed = this.getInitBoolean("dirAllowed", this._dirAllowed);
      this._redirectWelcome = this.getInitBoolean("redirectWelcome", this._redirectWelcome);
      this._gzip = this.getInitBoolean("gzip", this._gzip);
      this._pathInfoOnly = this.getInitBoolean("pathInfoOnly", this._pathInfoOnly);
      if ("exact".equals(this.getInitParameter("welcomeServlets"))) {
         this._welcomeExactServlets = true;
         this._welcomeServlets = false;
      } else {
         this._welcomeServlets = this.getInitBoolean("welcomeServlets", this._welcomeServlets);
      }

      this._useFileMappedBuffer = this.getInitBoolean("useFileMappedBuffer", this._useFileMappedBuffer);
      this._relativeResourceBase = this.getInitParameter("relativeResourceBase");
      String rb = this.getInitParameter("resourceBase");
      if (rb != null) {
         if (this._relativeResourceBase != null) {
            throw new UnavailableException("resourceBase & relativeResourceBase");
         }

         try {
            this._resourceBase = this._contextHandler.newResource(rb);
         } catch (Exception e) {
            LOG.warn("EXCEPTION ", (Throwable)e);
            throw new UnavailableException(e.toString());
         }
      }

      String css = this.getInitParameter("stylesheet");

      try {
         if (css != null) {
            this._stylesheet = Resource.newResource(css);
            if (!this._stylesheet.exists()) {
               LOG.warn("!" + css);
               this._stylesheet = null;
            }
         }

         if (this._stylesheet == null) {
            this._stylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
         }
      } catch (Exception e) {
         LOG.warn(e.toString());
         LOG.debug(e);
      }

      String cc = this.getInitParameter("cacheControl");
      if (cc != null) {
         this._cacheControl = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cc);
      }

      String resourceCache = this.getInitParameter("resourceCache");
      int max_cache_size = this.getInitInt("maxCacheSize", -2);
      int max_cached_file_size = this.getInitInt("maxCachedFileSize", -2);
      int max_cached_files = this.getInitInt("maxCachedFiles", -2);
      if (resourceCache != null) {
         if (max_cache_size != -1 || max_cached_file_size != -2 || max_cached_files != -2) {
            LOG.debug("ignoring resource cache configuration, using resourceCache attribute");
         }

         if (this._relativeResourceBase != null || this._resourceBase != null) {
            throw new UnavailableException("resourceCache specified with resource bases");
         }

         this._cache = (ResourceCache)this._servletContext.getAttribute(resourceCache);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Cache {}={}", resourceCache, this._contentFactory);
         }
      }

      this._etags = this.getInitBoolean("etags", this._etags);

      try {
         if (this._cache == null && (max_cached_files != -2 || max_cache_size != -2 || max_cached_file_size != -2)) {
            this._cache = new ResourceCache((ResourceCache)null, this, this._mimeTypes, this._useFileMappedBuffer, this._etags, this._gzip);
            if (max_cache_size >= 0) {
               this._cache.setMaxCacheSize(max_cache_size);
            }

            if (max_cached_file_size >= -1) {
               this._cache.setMaxCachedFileSize(max_cached_file_size);
            }

            if (max_cached_files >= -1) {
               this._cache.setMaxCachedFiles(max_cached_files);
            }

            this._servletContext.setAttribute(resourceCache == null ? "resourceCache" : resourceCache, this._cache);
         }
      } catch (Exception e) {
         LOG.warn("EXCEPTION ", (Throwable)e);
         throw new UnavailableException(e.toString());
      }

      if (this._cache != null) {
         this._contentFactory = this._cache;
      } else {
         this._contentFactory = new ResourceContentFactory(this, this._mimeTypes, this._gzip);
         if (resourceCache != null) {
            this._servletContext.setAttribute(resourceCache, this._contentFactory);
         }
      }

      this._gzipEquivalentFileExtensions = new ArrayList();
      String otherGzipExtensions = this.getInitParameter("otherGzipFileExtensions");
      if (otherGzipExtensions != null) {
         StringTokenizer tok = new StringTokenizer(otherGzipExtensions, ",", false);

         while(tok.hasMoreTokens()) {
            String s = tok.nextToken().trim();
            this._gzipEquivalentFileExtensions.add(s.charAt(0) == '.' ? s : "." + s);
         }
      } else {
         this._gzipEquivalentFileExtensions.add(".svgz");
      }

      this._servletHandler = (ServletHandler)this._contextHandler.getChildHandlerByClass(ServletHandler.class);

      for(ServletHolder h : this._servletHandler.getServlets()) {
         if (h.getServletInstance() == this) {
            this._defaultHolder = h;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("resource base = " + this._resourceBase);
      }

   }

   protected ContextHandler initContextHandler(ServletContext servletContext) {
      ContextHandler.Context scontext = ContextHandler.getCurrentContext();
      if (scontext == null) {
         if (servletContext instanceof ContextHandler.Context) {
            return ((ContextHandler.Context)servletContext).getContextHandler();
         } else {
            throw new IllegalArgumentException("The servletContext " + servletContext + " " + servletContext.getClass().getName() + " is not " + ContextHandler.Context.class.getName());
         }
      } else {
         return ContextHandler.getCurrentContext().getContextHandler();
      }
   }

   public String getInitParameter(String name) {
      String value = this.getServletContext().getInitParameter("org.spark_project.jetty.servlet.Default." + name);
      if (value == null) {
         value = super.getInitParameter(name);
      }

      return value;
   }

   private boolean getInitBoolean(String name, boolean dft) {
      String value = this.getInitParameter(name);
      if (value != null && value.length() != 0) {
         return value.startsWith("t") || value.startsWith("T") || value.startsWith("y") || value.startsWith("Y") || value.startsWith("1");
      } else {
         return dft;
      }
   }

   private int getInitInt(String name, int dft) {
      String value = this.getInitParameter(name);
      if (value == null) {
         value = this.getInitParameter(name);
      }

      return value != null && value.length() > 0 ? Integer.parseInt(value) : dft;
   }

   public Resource getResource(String pathInContext) {
      Resource r = null;
      if (this._relativeResourceBase != null) {
         pathInContext = URIUtil.addPaths(this._relativeResourceBase, pathInContext);
      }

      try {
         if (this._resourceBase != null) {
            r = this._resourceBase.addPath(pathInContext);
            if (!this._contextHandler.checkAlias(pathInContext, r)) {
               r = null;
            }
         } else if (this._servletContext instanceof ContextHandler.Context) {
            r = this._contextHandler.getResource(pathInContext);
         } else {
            URL u = this._servletContext.getResource(pathInContext);
            r = this._contextHandler.newResource(u);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Resource " + pathInContext + "=" + r);
         }
      } catch (IOException e) {
         LOG.ignore(e);
      }

      if ((r == null || !r.exists()) && pathInContext.endsWith("/jetty-dir.css")) {
         r = this._stylesheet;
      }

      return r;
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String servletPath = null;
      String pathInfo = null;
      Enumeration<String> reqRanges = null;
      boolean included = request.getAttribute("javax.servlet.include.request_uri") != null;
      if (included) {
         servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
         pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
         if (servletPath == null) {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
         }
      } else {
         servletPath = this._pathInfoOnly ? "/" : request.getServletPath();
         pathInfo = request.getPathInfo();
         reqRanges = request.getHeaders(HttpHeader.RANGE.asString());
         if (!this.hasDefinedRange(reqRanges)) {
            reqRanges = null;
         }
      }

      String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
      boolean endsWithSlash = (pathInfo == null ? request.getServletPath() : pathInfo).endsWith("/");
      boolean gzippable = this._gzip && !endsWithSlash && !included && reqRanges == null;
      HttpContent content = null;
      boolean release_content = true;

      try {
         content = this._contentFactory.getContent(pathInContext, response.getBufferSize());
         if (LOG.isDebugEnabled()) {
            LOG.info("content={}", content);
         }

         if (content != null && content.getResource().exists()) {
            if (content.getResource().isDirectory()) {
               this.sendWelcome(content, pathInContext, endsWithSlash, included, request, response);
               return;
            }

            if (endsWithSlash && pathInContext.length() > 1) {
               String q = request.getQueryString();
               pathInContext = pathInContext.substring(0, pathInContext.length() - 1);
               String uri = URIUtil.addPaths(this._servletContext.getContextPath(), pathInContext);
               if (q != null && q.length() != 0) {
                  uri = uri + "?" + q;
               }

               response.sendRedirect(response.encodeRedirectURL(uri));
               return;
            }

            if (!included && !this.passConditionalHeaders(request, response, content)) {
               return;
            }

            HttpContent gzip_content = gzippable ? content.getGzipContent() : null;
            if (gzip_content != null) {
               response.addHeader(HttpHeader.VARY.asString(), HttpHeader.ACCEPT_ENCODING.asString());
               String accept = request.getHeader(HttpHeader.ACCEPT_ENCODING.asString());
               if (accept != null && accept.indexOf("gzip") >= 0) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("gzip={}", gzip_content);
                  }

                  content = gzip_content;
               }
            }

            if (this.isGzippedContent(pathInContext)) {
               response.setHeader(HttpHeader.CONTENT_ENCODING.asString(), "gzip");
            }

            release_content = this.sendData(request, response, included, content, reqRanges);
            return;
         }

         if (included) {
            throw new FileNotFoundException("!" + pathInContext);
         }

         response.sendError(404);
      } catch (IllegalArgumentException e) {
         LOG.warn("EXCEPTION ", (Throwable)e);
         if (!response.isCommitted()) {
            response.sendError(500, e.getMessage());
         }

         return;
      } finally {
         if (release_content && content != null) {
            content.release();
         }

      }

   }

   protected void sendWelcome(HttpContent content, String pathInContext, boolean endsWithSlash, boolean included, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (endsWithSlash && (pathInContext.length() != 1 || request.getAttribute("org.spark_project.jetty.server.nullPathInfo") == null)) {
         String welcome = this.getWelcomeFile(pathInContext);
         if (welcome != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("welcome={}", welcome);
            }

            if (this._redirectWelcome) {
               response.setContentLength(0);
               String uri = URIUtil.encodePath(URIUtil.addPaths(this._servletContext.getContextPath(), welcome));
               String q = request.getQueryString();
               if (q != null && !q.isEmpty()) {
                  uri = uri + "?" + q;
               }

               response.sendRedirect(response.encodeRedirectURL(uri));
            } else {
               RequestDispatcher dispatcher = this._servletContext.getRequestDispatcher(welcome);
               if (dispatcher != null) {
                  if (included) {
                     dispatcher.include(request, response);
                  } else {
                     request.setAttribute("org.spark_project.jetty.server.welcome", welcome);
                     dispatcher.forward(request, response);
                  }
               }
            }

         } else {
            if (included || this.passConditionalHeaders(request, response, content)) {
               this.sendDirectory(request, response, content.getResource(), pathInContext);
            }

         }
      } else {
         StringBuffer buf = request.getRequestURL();
         synchronized(buf) {
            int param = buf.lastIndexOf(";");
            if (param < 0) {
               buf.append('/');
            } else {
               buf.insert(param, '/');
            }

            String q = request.getQueryString();
            if (q != null && q.length() != 0) {
               buf.append('?');
               buf.append(q);
            }

            response.setContentLength(0);
            response.sendRedirect(response.encodeRedirectURL(buf.toString()));
         }
      }
   }

   protected boolean isGzippedContent(String path) {
      if (path == null) {
         return false;
      } else {
         for(String suffix : this._gzipEquivalentFileExtensions) {
            if (path.endsWith(suffix)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean hasDefinedRange(Enumeration<String> reqRanges) {
      return reqRanges != null && reqRanges.hasMoreElements();
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.doGet(request, response);
   }

   protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.sendError(405);
   }

   protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setHeader("Allow", "GET,HEAD,POST,OPTIONS");
   }

   private String getWelcomeFile(String pathInContext) throws MalformedURLException, IOException {
      if (this._welcomes == null) {
         return null;
      } else {
         String welcome_servlet = null;

         for(int i = 0; i < this._welcomes.length; ++i) {
            String welcome_in_context = URIUtil.addPaths(pathInContext, this._welcomes[i]);
            Resource welcome = this.getResource(welcome_in_context);
            if (welcome != null && welcome.exists()) {
               return welcome_in_context;
            }

            if ((this._welcomeServlets || this._welcomeExactServlets) && welcome_servlet == null) {
               PathMap.MappedEntry<?> entry = this._servletHandler.getHolderEntry(welcome_in_context);
               if (entry != null && entry.getValue() != this._defaultHolder && (this._welcomeServlets || this._welcomeExactServlets && entry.getKey().equals(welcome_in_context))) {
                  welcome_servlet = welcome_in_context;
               }
            }
         }

         return welcome_servlet;
      }
   }

   protected boolean passConditionalHeaders(HttpServletRequest request, HttpServletResponse response, HttpContent content) throws IOException {
      try {
         String ifm = null;
         String ifnm = null;
         String ifms = null;
         long ifums = -1L;
         if (request instanceof Request) {
            HttpFields fields = ((Request)request).getHttpFields();
            int i = fields.size();

            while(i-- > 0) {
               HttpField field = fields.getField(i);
               if (field.getHeader() != null) {
                  switch (field.getHeader()) {
                     case IF_MATCH:
                        ifm = field.getValue();
                        break;
                     case IF_NONE_MATCH:
                        ifnm = field.getValue();
                        break;
                     case IF_MODIFIED_SINCE:
                        ifms = field.getValue();
                        break;
                     case IF_UNMODIFIED_SINCE:
                        ifums = DateParser.parseDate(field.getValue());
                  }
               }
            }
         } else {
            ifm = request.getHeader(HttpHeader.IF_MATCH.asString());
            ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
            ifms = request.getHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
            ifums = request.getDateHeader(HttpHeader.IF_UNMODIFIED_SINCE.asString());
         }

         if (!HttpMethod.HEAD.is(request.getMethod())) {
            if (this._etags) {
               String etag = content.getETagValue();
               if (ifm != null) {
                  boolean match = false;
                  if (etag != null) {
                     for(String tag : new QuotedCSV(true, new String[]{ifm})) {
                        if (etag.equals(tag) || tag.endsWith("--gzip\"") && etag.equals(GzipHttpContent.removeGzipFromETag(tag))) {
                           match = true;
                           break;
                        }
                     }
                  }

                  if (!match) {
                     response.setStatus(412);
                     return false;
                  }
               }

               if (ifnm != null && etag != null) {
                  if (!etag.equals(ifnm) && (!ifnm.endsWith("--gzip\"") || ifnm.indexOf(44) >= 0 || !etag.equals(GzipHttpContent.removeGzipFromETag(ifnm)))) {
                     for(String tag : new QuotedCSV(true, new String[]{ifnm})) {
                        if (etag.equals(tag) || tag.endsWith("--gzip\"") && etag.equals(GzipHttpContent.removeGzipFromETag(tag))) {
                           response.setStatus(304);
                           response.setHeader(HttpHeader.ETAG.asString(), tag);
                           return false;
                        }
                     }

                     return true;
                  }

                  response.setStatus(304);
                  response.setHeader(HttpHeader.ETAG.asString(), ifnm);
                  return false;
               }
            }

            if (ifms != null) {
               String mdlm = content.getLastModifiedValue();
               if (mdlm != null && ifms.equals(mdlm)) {
                  response.setStatus(304);
                  if (this._etags) {
                     response.setHeader(HttpHeader.ETAG.asString(), content.getETagValue());
                  }

                  response.flushBuffer();
                  return false;
               }

               long ifmsl = request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
               if (ifmsl != -1L && content.getResource().lastModified() / 1000L <= ifmsl / 1000L) {
                  response.setStatus(304);
                  if (this._etags) {
                     response.setHeader(HttpHeader.ETAG.asString(), content.getETagValue());
                  }

                  response.flushBuffer();
                  return false;
               }
            }

            if (ifums != -1L && content.getResource().lastModified() / 1000L > ifums / 1000L) {
               response.sendError(412);
               return false;
            }
         }

         return true;
      } catch (IllegalArgumentException var14) {
         if (!response.isCommitted()) {
            response.sendError(400, var14.getMessage());
         }

         throw var14;
      }
   }

   protected void sendDirectory(HttpServletRequest request, HttpServletResponse response, Resource resource, String pathInContext) throws IOException {
      if (!this._dirAllowed) {
         response.sendError(403);
      } else {
         byte[] data = null;
         String base = URIUtil.addPaths(request.getRequestURI(), "/");
         if (this._resourceBase != null) {
            if (this._resourceBase instanceof ResourceCollection) {
               resource = this._resourceBase.addPath(pathInContext);
            }
         } else if (this._contextHandler.getBaseResource() instanceof ResourceCollection) {
            resource = this._contextHandler.getBaseResource().addPath(pathInContext);
         }

         String dir = resource.getListHTML(base, pathInContext.length() > 1);
         if (dir == null) {
            response.sendError(403, "No directory");
         } else {
            data = dir.getBytes("utf-8");
            response.setContentType("text/html;charset=utf-8");
            response.setContentLength(data.length);
            response.getOutputStream().write(data);
         }
      }
   }

   protected boolean sendData(HttpServletRequest request, HttpServletResponse response, boolean include, final HttpContent content, Enumeration<String> reqRanges) throws IOException {
      long content_length = content.getContentLengthValue();
      OutputStream out = null;

      boolean written;
      try {
         var26 = response.getOutputStream();
         written = var26 instanceof HttpOutput ? ((HttpOutput)var26).isWritten() : true;
      } catch (IllegalStateException var25) {
         var26 = new WriterOutputStream(response.getWriter());
         written = true;
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug(String.format("sendData content=%s out=%s async=%b", content, var26, request.isAsyncSupported()));
      }

      if (reqRanges != null && reqRanges.hasMoreElements() && content_length >= 0L) {
         List<InclusiveByteRange> ranges = InclusiveByteRange.satisfiableRanges(reqRanges, content_length);
         if (ranges == null || ranges.size() == 0) {
            this.putHeaders(response, content, 0L);
            response.setStatus(416);
            response.setHeader(HttpHeader.CONTENT_RANGE.asString(), InclusiveByteRange.to416HeaderRangeString(content_length));
            content.getResource().writeTo((OutputStream)var26, 0L, content_length);
            return true;
         }

         if (ranges.size() == 1) {
            InclusiveByteRange singleSatisfiableRange = (InclusiveByteRange)ranges.get(0);
            long singleLength = singleSatisfiableRange.getSize(content_length);
            this.putHeaders(response, content, singleLength);
            response.setStatus(206);
            if (!response.containsHeader(HttpHeader.DATE.asString())) {
               response.addDateHeader(HttpHeader.DATE.asString(), System.currentTimeMillis());
            }

            response.setHeader(HttpHeader.CONTENT_RANGE.asString(), singleSatisfiableRange.toHeaderRangeString(content_length));
            content.getResource().writeTo((OutputStream)var26, singleSatisfiableRange.getFirst(content_length), singleLength);
            return true;
         }

         this.putHeaders(response, content, -1L);
         String mimetype = content == null ? null : content.getContentTypeValue();
         if (mimetype == null) {
            LOG.warn("Unknown mimetype for " + request.getRequestURI());
         }

         MultiPartOutputStream multi = new MultiPartOutputStream((OutputStream)var26);
         response.setStatus(206);
         if (!response.containsHeader(HttpHeader.DATE.asString())) {
            response.addDateHeader(HttpHeader.DATE.asString(), System.currentTimeMillis());
         }

         String ctp;
         if (request.getHeader(HttpHeader.REQUEST_RANGE.asString()) != null) {
            ctp = "multipart/x-byteranges; boundary=";
         } else {
            ctp = "multipart/byteranges; boundary=";
         }

         response.setContentType(ctp + multi.getBoundary());
         InputStream in = content.getResource().getInputStream();
         long pos = 0L;
         int length = 0;
         String[] header = new String[ranges.size()];

         for(int i = 0; i < ranges.size(); ++i) {
            InclusiveByteRange ibr = (InclusiveByteRange)ranges.get(i);
            header[i] = ibr.toHeaderRangeString(content_length);
            length = (int)((long)length + (long)((i > 0 ? 2 : 0) + 2 + multi.getBoundary().length() + 2 + (mimetype == null ? 0 : HttpHeader.CONTENT_TYPE.asString().length() + 2 + mimetype.length()) + 2 + HttpHeader.CONTENT_RANGE.asString().length() + 2 + header[i].length() + 2 + 2) + (ibr.getLast(content_length) - ibr.getFirst(content_length)) + 1L);
         }

         length += 4 + multi.getBoundary().length() + 2 + 2;
         response.setContentLength(length);

         for(int i = 0; i < ranges.size(); ++i) {
            InclusiveByteRange ibr = (InclusiveByteRange)ranges.get(i);
            multi.startPart(mimetype, new String[]{HttpHeader.CONTENT_RANGE + ": " + header[i]});
            long start = ibr.getFirst(content_length);
            long size = ibr.getSize(content_length);
            if (in != null) {
               if (start < pos) {
                  in.close();
                  in = content.getResource().getInputStream();
                  pos = 0L;
               }

               if (pos < start) {
                  in.skip(start - pos);
                  pos = start;
               }

               IO.copy((InputStream)in, (OutputStream)multi, size);
               pos += size;
            } else {
               content.getResource().writeTo(multi, start, size);
            }
         }

         if (in != null) {
            in.close();
         }

         multi.close();
      } else if (include) {
         content.getResource().writeTo((OutputStream)var26, 0L, content_length);
      } else if (!written && var26 instanceof HttpOutput) {
         this.putHeaders(response, content, 0L);
         if (request.isAsyncSupported()) {
            final AsyncContext context = request.startAsync();
            context.setTimeout(0L);
            ((HttpOutput)var26).sendContent(content, new Callback() {
               public void succeeded() {
                  context.complete();
                  content.release();
               }

               public void failed(Throwable x) {
                  if (x instanceof IOException) {
                     DefaultServlet.LOG.debug(x);
                  } else {
                     DefaultServlet.LOG.warn(x);
                  }

                  context.complete();
                  content.release();
               }

               public String toString() {
                  return String.format("DefaultServlet@%x$CB", DefaultServlet.this.hashCode());
               }
            });
            return false;
         }

         ((HttpOutput)var26).sendContent(content);
      } else {
         this.putHeaders(response, content, written ? -1L : 0L);
         ByteBuffer buffer = content.getIndirectBuffer();
         if (buffer != null) {
            BufferUtil.writeTo(buffer, (OutputStream)var26);
         } else {
            content.getResource().writeTo((OutputStream)var26, 0L, content_length);
         }
      }

      return true;
   }

   protected void putHeaders(HttpServletResponse response, HttpContent content, long contentLength) {
      if (response instanceof Response) {
         Response r = (Response)response;
         r.putHeaders(content, contentLength, this._etags);
         HttpFields f = r.getHttpFields();
         if (this._acceptRanges) {
            f.put(ACCEPT_RANGES);
         }

         if (this._cacheControl != null) {
            f.put(this._cacheControl);
         }
      } else {
         Response.putHeaders(response, content, contentLength, this._etags);
         if (this._acceptRanges) {
            response.setHeader(ACCEPT_RANGES.getName(), ACCEPT_RANGES.getValue());
         }

         if (this._cacheControl != null) {
            response.setHeader(this._cacheControl.getName(), this._cacheControl.getValue());
         }
      }

   }

   public void destroy() {
      if (this._cache != null) {
         this._cache.flushCache();
      }

      super.destroy();
   }

   static {
      ACCEPT_RANGES = new PreEncodedHttpField(HttpHeader.ACCEPT_RANGES, "bytes");
   }
}
