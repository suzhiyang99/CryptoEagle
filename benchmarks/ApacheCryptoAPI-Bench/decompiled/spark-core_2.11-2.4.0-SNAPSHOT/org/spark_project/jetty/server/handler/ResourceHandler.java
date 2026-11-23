package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.io.WriterOutputStream;
import org.spark_project.jetty.server.HttpOutput;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Response;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.PathResource;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.resource.ResourceFactory;

public class ResourceHandler extends HandlerWrapper implements ResourceFactory {
   private static final Logger LOG = Log.getLogger(ResourceHandler.class);
   ContextHandler _context;
   Resource _baseResource;
   Resource _defaultStylesheet;
   Resource _stylesheet;
   String[] _welcomeFiles = new String[]{"index.html"};
   MimeTypes _mimeTypes;
   String _cacheControl;
   boolean _directory;
   boolean _gzip;
   boolean _etags;
   int _minMemoryMappedContentLength = 0;
   int _minAsyncContentLength = 16384;

   public MimeTypes getMimeTypes() {
      return this._mimeTypes;
   }

   public void setMimeTypes(MimeTypes mimeTypes) {
      this._mimeTypes = mimeTypes;
   }

   public boolean isDirectoriesListed() {
      return this._directory;
   }

   public void setDirectoriesListed(boolean directory) {
      this._directory = directory;
   }

   public int getMinMemoryMappedContentLength() {
      return this._minMemoryMappedContentLength;
   }

   public void setMinMemoryMappedContentLength(int minMemoryMappedFileSize) {
      this._minMemoryMappedContentLength = minMemoryMappedFileSize;
   }

   public int getMinAsyncContentLength() {
      return this._minAsyncContentLength;
   }

   public void setMinAsyncContentLength(int minAsyncContentLength) {
      this._minAsyncContentLength = minAsyncContentLength;
   }

   public boolean isEtags() {
      return this._etags;
   }

   public void setEtags(boolean etags) {
      this._etags = etags;
   }

   public void doStart() throws Exception {
      ContextHandler.Context scontext = ContextHandler.getCurrentContext();
      this._context = scontext == null ? null : scontext.getContextHandler();
      this._mimeTypes = this._context == null ? new MimeTypes() : this._context.getMimeTypes();
      super.doStart();
   }

   public Resource getBaseResource() {
      return this._baseResource == null ? null : this._baseResource;
   }

   public String getResourceBase() {
      return this._baseResource == null ? null : this._baseResource.toString();
   }

   public void setBaseResource(Resource base) {
      this._baseResource = base;
   }

   public void setResourceBase(String resourceBase) {
      try {
         this.setBaseResource(Resource.newResource(resourceBase));
      } catch (Exception e) {
         LOG.warn(e.toString());
         LOG.debug(e);
         throw new IllegalArgumentException(resourceBase);
      }
   }

   public Resource getStylesheet() {
      if (this._stylesheet != null) {
         return this._stylesheet;
      } else {
         if (this._defaultStylesheet == null) {
            this._defaultStylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
         }

         return this._defaultStylesheet;
      }
   }

   public void setStylesheet(String stylesheet) {
      try {
         this._stylesheet = Resource.newResource(stylesheet);
         if (!this._stylesheet.exists()) {
            LOG.warn("unable to find custom stylesheet: " + stylesheet);
            this._stylesheet = null;
         }

      } catch (Exception e) {
         LOG.warn(e.toString());
         LOG.debug(e);
         throw new IllegalArgumentException(stylesheet);
      }
   }

   public String getCacheControl() {
      return this._cacheControl;
   }

   public void setCacheControl(String cacheControl) {
      this._cacheControl = cacheControl;
   }

   public Resource getResource(String path) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} getResource({})", this._context == null ? this._baseResource : this._context, this._baseResource, path);
      }

      if (path != null && path.startsWith("/")) {
         try {
            Resource base = this._baseResource;
            if (base == null) {
               return this._context == null ? null : this._context.getResource(path);
            } else {
               path = URIUtil.canonicalPath(path);
               Resource r = base.addPath(path);
               if (r != null && r.isAlias() && (this._context == null || !this._context.checkAlias(path, r))) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("resource={} alias={}", r, r.getAlias());
                  }

                  return null;
               } else {
                  return r;
               }
            }
         } catch (Exception e) {
            LOG.debug(e);
            return null;
         }
      } else {
         return null;
      }
   }

   protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
      Boolean included = request.getAttribute("javax.servlet.include.request_uri") != null;
      String servletPath;
      String pathInfo;
      if (included != null && included) {
         servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
         pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
         if (servletPath == null && pathInfo == null) {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
         }
      } else {
         servletPath = request.getServletPath();
         pathInfo = request.getPathInfo();
      }

      String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
      return this.getResource(pathInContext);
   }

   public String[] getWelcomeFiles() {
      return this._welcomeFiles;
   }

   public void setWelcomeFiles(String[] welcomeFiles) {
      this._welcomeFiles = welcomeFiles;
   }

   protected Resource getWelcome(Resource directory) throws MalformedURLException, IOException {
      for(int i = 0; i < this._welcomeFiles.length; ++i) {
         Resource welcome = directory.addPath(this._welcomeFiles[i]);
         if (welcome.exists() && !welcome.isDirectory()) {
            return welcome;
         }
      }

      return null;
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (!baseRequest.isHandled()) {
         boolean skipContentBody = false;
         if (!HttpMethod.GET.is(request.getMethod())) {
            if (!HttpMethod.HEAD.is(request.getMethod())) {
               super.handle(target, baseRequest, request, response);
               return;
            }

            skipContentBody = true;
         }

         Resource resource = this.getResource(request);
         if (LOG.isDebugEnabled()) {
            if (resource == null) {
               LOG.debug("resource=null");
            } else {
               LOG.debug("resource={} alias={} exists={}", resource, resource.getAlias(), resource.exists());
            }
         }

         if (resource == null || !resource.exists()) {
            if (!target.endsWith("/jetty-dir.css")) {
               super.handle(target, baseRequest, request, response);
               return;
            }

            resource = this.getStylesheet();
            if (resource == null) {
               return;
            }

            response.setContentType("text/css");
         }

         baseRequest.setHandled(true);
         if (resource.isDirectory()) {
            String pathInfo = request.getPathInfo();
            boolean endsWithSlash = (pathInfo == null ? request.getServletPath() : pathInfo).endsWith("/");
            if (!endsWithSlash) {
               response.sendRedirect(response.encodeRedirectURL(request.getRequestURI() + "/"));
               return;
            }

            Resource welcome = this.getWelcome(resource);
            if (welcome == null || !welcome.exists()) {
               this.doDirectory(request, response, resource);
               baseRequest.setHandled(true);
               return;
            }

            resource = welcome;
         }

         long last_modified = resource.lastModified();
         String etag = null;
         if (this._etags) {
            String ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
            etag = resource.getWeakETag();
            if (ifnm != null && resource != null && ifnm.equals(etag)) {
               response.setStatus(304);
               baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);
               return;
            }
         }

         if (last_modified > 0L) {
            long if_modified = request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
            if (if_modified > 0L && last_modified / 1000L <= if_modified / 1000L) {
               response.setStatus(304);
               return;
            }
         }

         String mime = this._mimeTypes.getMimeByExtension(resource.toString());
         if (mime == null) {
            mime = this._mimeTypes.getMimeByExtension(request.getPathInfo());
         }

         this.doResponseHeaders(response, resource, mime);
         if (this._etags) {
            baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);
         }

         if (last_modified > 0L) {
            response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(), last_modified);
         }

         if (!skipContentBody) {
            OutputStream out = null;

            try {
               var21 = response.getOutputStream();
            } catch (IllegalStateException var16) {
               var21 = new WriterOutputStream(response.getWriter());
            }

            if (!(var21 instanceof HttpOutput)) {
               resource.writeTo((OutputStream)var21, 0L, resource.length());
            } else {
               int min_async_size = this._minAsyncContentLength == 0 ? response.getBufferSize() : this._minAsyncContentLength;
               if (request.isAsyncSupported() && min_async_size > 0 && resource.length() >= (long)min_async_size) {
                  final AsyncContext async = request.startAsync();
                  async.setTimeout(0L);
                  Callback callback = new Callback() {
                     public void succeeded() {
                        async.complete();
                     }

                     public void failed(Throwable x) {
                        ResourceHandler.LOG.warn(x.toString());
                        ResourceHandler.LOG.debug(x);
                        async.complete();
                     }
                  };
                  if (this._minMemoryMappedContentLength >= 0 && resource.length() > (long)this._minMemoryMappedContentLength && resource.length() < 2147483647L && resource instanceof PathResource) {
                     ByteBuffer buffer = BufferUtil.toMappedBuffer(resource.getFile());
                     ((HttpOutput)var21).sendContent(buffer, callback);
                  } else {
                     ReadableByteChannel channel = resource.getReadableByteChannel();
                     if (channel != null) {
                        ((HttpOutput)var21).sendContent(channel, callback);
                     } else {
                        ((HttpOutput)var21).sendContent(resource.getInputStream(), callback);
                     }
                  }
               } else if (this._minMemoryMappedContentLength > 0 && resource.length() > (long)this._minMemoryMappedContentLength && resource instanceof PathResource) {
                  ByteBuffer buffer = BufferUtil.toMappedBuffer(resource.getFile());
                  ((HttpOutput)var21).sendContent(buffer);
               } else {
                  ReadableByteChannel channel = resource.getReadableByteChannel();
                  if (channel != null) {
                     ((HttpOutput)var21).sendContent(channel);
                  } else {
                     ((HttpOutput)var21).sendContent(resource.getInputStream());
                  }
               }
            }

         }
      }
   }

   protected void doDirectory(HttpServletRequest request, HttpServletResponse response, Resource resource) throws IOException {
      if (this._directory) {
         String listing = resource.getListHTML(request.getRequestURI(), request.getPathInfo().lastIndexOf("/") > 0);
         response.setContentType("text/html;charset=utf-8");
         response.getWriter().println(listing);
      } else {
         response.sendError(403);
      }

   }

   protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
      if (mimeType != null) {
         response.setContentType(mimeType);
      }

      long length = resource.length();
      if (response instanceof Response) {
         HttpFields fields = ((Response)response).getHttpFields();
         if (length > 0L) {
            ((Response)response).setLongContentLength(length);
         }

         if (this._cacheControl != null) {
            fields.put(HttpHeader.CACHE_CONTROL, this._cacheControl);
         }
      } else {
         if (length > 2147483647L) {
            response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Long.toString(length));
         } else if (length > 0L) {
            response.setContentLength((int)length);
         }

         if (this._cacheControl != null) {
            response.setHeader(HttpHeader.CACHE_CONTROL.asString(), this._cacheControl);
         }
      }

   }
}
