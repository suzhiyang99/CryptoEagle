package org.spark_project.jetty.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;

public class PutFilter implements Filter {
   public static final String __PUT = "PUT";
   public static final String __DELETE = "DELETE";
   public static final String __MOVE = "MOVE";
   public static final String __OPTIONS = "OPTIONS";
   Set<String> _operations = new HashSet();
   private ConcurrentMap<String, String> _hidden = new ConcurrentHashMap();
   private ServletContext _context;
   private String _baseURI;
   private boolean _delAllowed;
   private boolean _putAtomic;
   private File _tmpdir;

   public void init(FilterConfig config) throws ServletException {
      this._context = config.getServletContext();
      this._tmpdir = (File)this._context.getAttribute("javax.servlet.context.tempdir");
      if (this._context.getRealPath("/") == null) {
         throw new UnavailableException("Packed war");
      } else {
         String b = config.getInitParameter("baseURI");
         if (b != null) {
            this._baseURI = b;
         } else {
            File base = new File(this._context.getRealPath("/"));
            this._baseURI = base.toURI().toString();
         }

         this._delAllowed = this.getInitBoolean(config, "delAllowed");
         this._putAtomic = this.getInitBoolean(config, "putAtomic");
         this._operations.add("OPTIONS");
         this._operations.add("PUT");
         if (this._delAllowed) {
            this._operations.add("DELETE");
            this._operations.add("MOVE");
         }

      }
   }

   private boolean getInitBoolean(FilterConfig config, String name) {
      String value = config.getInitParameter(name);
      return value != null && value.length() > 0 && (value.startsWith("t") || value.startsWith("T") || value.startsWith("y") || value.startsWith("Y") || value.startsWith("1"));
   }

   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)req;
      HttpServletResponse response = (HttpServletResponse)res;
      String servletPath = request.getServletPath();
      String pathInfo = request.getPathInfo();
      String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
      String resource = URIUtil.addPaths(this._baseURI, pathInContext);
      String method = request.getMethod();
      boolean op = this._operations.contains(method);
      if (op) {
         File file = null;

         try {
            if (method.equals("OPTIONS")) {
               this.handleOptions(chain, request, response);
            } else {
               file = new File(new URI(resource));
               boolean exists = file.exists();
               if (exists && !this.passConditionalHeaders(request, response, file)) {
                  return;
               }

               if (method.equals("PUT")) {
                  this.handlePut(request, response, pathInContext, file);
               } else if (method.equals("DELETE")) {
                  this.handleDelete(request, response, pathInContext, file);
               } else {
                  if (!method.equals("MOVE")) {
                     throw new IllegalStateException();
                  }

                  this.handleMove(request, response, pathInContext, file);
               }
            }
         } catch (Exception e) {
            this._context.log(e.toString(), e);
            response.sendError(500);
         }

      } else {
         if (this.isHidden(pathInContext)) {
            response.sendError(404);
         } else {
            chain.doFilter(request, response);
         }

      }
   }

   private boolean isHidden(String pathInContext) {
      return this._hidden.containsKey(pathInContext);
   }

   public void destroy() {
   }

   public void handlePut(HttpServletRequest request, HttpServletResponse response, String pathInContext, File file) throws ServletException, IOException {
      boolean exists = file.exists();
      if (pathInContext.endsWith("/")) {
         if (!exists) {
            if (!file.mkdirs()) {
               response.sendError(403);
            } else {
               response.setStatus(201);
               response.flushBuffer();
            }
         } else {
            response.setStatus(200);
            response.flushBuffer();
         }
      } else {
         boolean ok = false;

         try {
            this._hidden.put(pathInContext, pathInContext);
            File parent = file.getParentFile();
            parent.mkdirs();
            int toRead = request.getContentLength();
            InputStream in = request.getInputStream();
            if (this._putAtomic) {
               File tmp = File.createTempFile(file.getName(), (String)null, this._tmpdir);
               OutputStream out = new FileOutputStream(tmp, false);
               Throwable var12 = null;

               try {
                  if (toRead >= 0) {
                     IO.copy(in, out, (long)toRead);
                  } else {
                     IO.copy(in, out);
                  }
               } catch (Throwable var58) {
                  var12 = var58;
                  throw var58;
               } finally {
                  if (out != null) {
                     if (var12 != null) {
                        try {
                           out.close();
                        } catch (Throwable var56) {
                           var12.addSuppressed(var56);
                        }
                     } else {
                        out.close();
                     }
                  }

               }

               if (!tmp.renameTo(file)) {
                  throw new IOException("rename from " + tmp + " to " + file + " failed");
               }
            } else {
               OutputStream out = new FileOutputStream(file, false);
               Throwable var64 = null;

               try {
                  if (toRead >= 0) {
                     IO.copy(in, out, (long)toRead);
                  } else {
                     IO.copy(in, out);
                  }
               } catch (Throwable var57) {
                  var64 = var57;
                  throw var57;
               } finally {
                  if (out != null) {
                     if (var64 != null) {
                        try {
                           out.close();
                        } catch (Throwable var55) {
                           var64.addSuppressed(var55);
                        }
                     } else {
                        out.close();
                     }
                  }

               }
            }

            response.setStatus(exists ? 200 : 201);
            response.flushBuffer();
            ok = true;
         } catch (Exception ex) {
            this._context.log(ex.toString(), ex);
            response.sendError(403);
         } finally {
            if (!ok) {
               try {
                  if (file.exists()) {
                     file.delete();
                  }
               } catch (Exception e) {
                  this._context.log(e.toString(), e);
               }
            }

            this._hidden.remove(pathInContext);
         }
      }

   }

   public void handleDelete(HttpServletRequest request, HttpServletResponse response, String pathInContext, File file) throws ServletException, IOException {
      try {
         if (file.delete()) {
            response.setStatus(204);
            response.flushBuffer();
         } else {
            response.sendError(403);
         }
      } catch (SecurityException sex) {
         this._context.log(sex.toString(), sex);
         response.sendError(403);
      }

   }

   public void handleMove(HttpServletRequest request, HttpServletResponse response, String pathInContext, File file) throws ServletException, IOException, URISyntaxException {
      String newPath = URIUtil.canonicalPath(request.getHeader("new-uri"));
      if (newPath == null) {
         response.sendError(400);
      } else {
         String contextPath = request.getContextPath();
         if (contextPath != null && !newPath.startsWith(contextPath)) {
            response.sendError(405);
         } else {
            String newInfo = newPath;
            if (contextPath != null) {
               newInfo = newPath.substring(contextPath.length());
            }

            String new_resource = URIUtil.addPaths(this._baseURI, newInfo);
            File new_file = new File(new URI(new_resource));
            file.renameTo(new_file);
            response.setStatus(204);
            response.flushBuffer();
         }
      }
   }

   public void handleOptions(FilterChain chain, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      chain.doFilter(request, new HttpServletResponseWrapper(response) {
         public void setHeader(String name, String value) {
            if ("Allow".equalsIgnoreCase(name)) {
               Set<String> options = new HashSet();
               options.addAll(Arrays.asList(StringUtil.csvSplit(value)));
               options.addAll(PutFilter.this._operations);
               value = null;

               for(String o : options) {
                  value = value == null ? o : value + ", " + o;
               }
            }

            super.setHeader(name, value);
         }
      });
   }

   protected boolean passConditionalHeaders(HttpServletRequest request, HttpServletResponse response, File file) throws IOException {
      long date = 0L;
      if ((date = request.getDateHeader("if-unmodified-since")) > 0L && file.lastModified() / 1000L > date / 1000L) {
         response.sendError(412);
         return false;
      } else if ((date = request.getDateHeader("if-modified-since")) > 0L && file.lastModified() / 1000L <= date / 1000L) {
         response.reset();
         response.setStatus(304);
         response.flushBuffer();
         return false;
      } else {
         return true;
      }
   }
}
