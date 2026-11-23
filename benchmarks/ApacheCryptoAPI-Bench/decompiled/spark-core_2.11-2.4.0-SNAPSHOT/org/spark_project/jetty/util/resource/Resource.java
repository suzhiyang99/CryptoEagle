package org.spark_project.jetty.util.resource;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.spark_project.jetty.util.B64Code;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class Resource implements ResourceFactory, Closeable {
   private static final Logger LOG = Log.getLogger(Resource.class);
   public static boolean __defaultUseCaches = true;
   volatile Object _associate;

   public static void setDefaultUseCaches(boolean useCaches) {
      __defaultUseCaches = useCaches;
   }

   public static boolean getDefaultUseCaches() {
      return __defaultUseCaches;
   }

   public static Resource newResource(URI uri) throws MalformedURLException {
      return newResource(uri.toURL());
   }

   public static Resource newResource(URL url) {
      return newResource(url, __defaultUseCaches);
   }

   static Resource newResource(URL url, boolean useCaches) {
      if (url == null) {
         return null;
      } else {
         String url_string = url.toExternalForm();
         if (url_string.startsWith("file:")) {
            try {
               return new PathResource(url);
            } catch (Exception e) {
               LOG.warn(e.toString());
               LOG.debug("EXCEPTION ", (Throwable)e);
               return new BadResource(url, e.toString());
            }
         } else if (url_string.startsWith("jar:file:")) {
            return new JarFileResource(url, useCaches);
         } else {
            return (Resource)(url_string.startsWith("jar:") ? new JarResource(url, useCaches) : new URLResource(url, (URLConnection)null, useCaches));
         }
      }
   }

   public static Resource newResource(String resource) throws MalformedURLException {
      return newResource(resource, __defaultUseCaches);
   }

   public static Resource newResource(String resource, boolean useCaches) throws MalformedURLException {
      URL url = null;

      try {
         url = new URL(resource);
      } catch (MalformedURLException e) {
         if (!resource.startsWith("ftp:") && !resource.startsWith("file:") && !resource.startsWith("jar:")) {
            try {
               if (resource.startsWith("./")) {
                  resource = resource.substring(2);
               }

               File file = (new File(resource)).getCanonicalFile();
               return new PathResource(file);
            } catch (IOException e2) {
               e.addSuppressed(e2);
               throw e;
            }
         }

         LOG.warn("Bad Resource: " + resource);
         throw e;
      }

      return newResource(url, useCaches);
   }

   public static Resource newResource(File file) {
      return new PathResource(file.toPath());
   }

   public static Resource newSystemResource(String resource) throws IOException {
      URL url = null;
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (loader != null) {
         try {
            url = loader.getResource(resource);
            if (url == null && resource.startsWith("/")) {
               url = loader.getResource(resource.substring(1));
            }
         } catch (IllegalArgumentException e) {
            LOG.ignore(e);
            url = null;
         }
      }

      if (url == null) {
         loader = Resource.class.getClassLoader();
         if (loader != null) {
            url = loader.getResource(resource);
            if (url == null && resource.startsWith("/")) {
               url = loader.getResource(resource.substring(1));
            }
         }
      }

      if (url == null) {
         url = ClassLoader.getSystemResource(resource);
         if (url == null && resource.startsWith("/")) {
            url = ClassLoader.getSystemResource(resource.substring(1));
         }
      }

      return url == null ? null : newResource(url);
   }

   public static Resource newClassPathResource(String resource) {
      return newClassPathResource(resource, true, false);
   }

   public static Resource newClassPathResource(String name, boolean useCaches, boolean checkParents) {
      URL url = Resource.class.getResource(name);
      if (url == null) {
         url = Loader.getResource(Resource.class, name);
      }

      return url == null ? null : newResource(url, useCaches);
   }

   public static boolean isContainedIn(Resource r, Resource containingResource) throws MalformedURLException {
      return r.isContainedIn(containingResource);
   }

   protected void finalize() {
      this.close();
   }

   public abstract boolean isContainedIn(Resource var1) throws MalformedURLException;

   /** @deprecated */
   public final void release() {
      this.close();
   }

   public abstract void close();

   public abstract boolean exists();

   public abstract boolean isDirectory();

   public abstract long lastModified();

   public abstract long length();

   /** @deprecated */
   @Deprecated
   public abstract URL getURL();

   public URI getURI() {
      try {
         return this.getURL().toURI();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public abstract File getFile() throws IOException;

   public abstract String getName();

   public abstract InputStream getInputStream() throws IOException;

   public abstract ReadableByteChannel getReadableByteChannel() throws IOException;

   public abstract boolean delete() throws SecurityException;

   public abstract boolean renameTo(Resource var1) throws SecurityException;

   public abstract String[] list();

   public abstract Resource addPath(String var1) throws IOException, MalformedURLException;

   public Resource getResource(String path) {
      try {
         return this.addPath(path);
      } catch (Exception e) {
         LOG.debug(e);
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public String encode(String uri) {
      return null;
   }

   public Object getAssociate() {
      return this._associate;
   }

   public void setAssociate(Object o) {
      this._associate = o;
   }

   public boolean isAlias() {
      return this.getAlias() != null;
   }

   public URI getAlias() {
      return null;
   }

   public String getListHTML(String base, boolean parent) throws IOException {
      base = URIUtil.canonicalPath(base);
      if (base != null && this.isDirectory()) {
         String[] ls = this.list();
         if (ls == null) {
            return null;
         } else {
            Arrays.sort(ls);
            String decodedBase = URIUtil.decodePath(base);
            String title = "Directory: " + deTag(decodedBase);
            StringBuilder buf = new StringBuilder(4096);
            buf.append("<HTML><HEAD>");
            buf.append("<LINK HREF=\"").append("jetty-dir.css").append("\" REL=\"stylesheet\" TYPE=\"text/css\"/><TITLE>");
            buf.append(title);
            buf.append("</TITLE></HEAD><BODY>\n<H1>");
            buf.append(title);
            buf.append("</H1>\n<TABLE BORDER=0>\n");
            if (parent) {
               buf.append("<TR><TD><A HREF=\"");
               buf.append(URIUtil.addEncodedPaths(base, "../"));
               buf.append("\">Parent Directory</A></TD><TD></TD><TD></TD></TR>\n");
            }

            String encodedBase = hrefEncodeURI(base);
            DateFormat dfmt = DateFormat.getDateTimeInstance(2, 2);

            for(int i = 0; i < ls.length; ++i) {
               Resource item = this.addPath(ls[i]);
               buf.append("\n<TR><TD><A HREF=\"");
               String path = URIUtil.addEncodedPaths(encodedBase, URIUtil.encodePath(ls[i]));
               buf.append(path);
               if (item.isDirectory() && !path.endsWith("/")) {
                  buf.append("/");
               }

               buf.append("\">");
               buf.append(deTag(ls[i]));
               buf.append("&nbsp;");
               buf.append("</A></TD><TD ALIGN=right>");
               buf.append(item.length());
               buf.append(" bytes&nbsp;</TD><TD>");
               buf.append(dfmt.format(new Date(item.lastModified())));
               buf.append("</TD></TR>");
            }

            buf.append("</TABLE>\n");
            buf.append("</BODY></HTML>\n");
            return buf.toString();
         }
      } else {
         return null;
      }
   }

   private static String hrefEncodeURI(String param0) {
      // $FF: Couldn't be decompiled
   }

   private static String deTag(String raw) {
      return StringUtil.sanitizeXmlString(raw);
   }

   public void writeTo(OutputStream out, long start, long count) throws IOException {
      InputStream in = this.getInputStream();
      Throwable var7 = null;

      try {
         in.skip(start);
         if (count < 0L) {
            IO.copy(in, out);
         } else {
            IO.copy(in, out, count);
         }
      } catch (Throwable var16) {
         var7 = var16;
         throw var16;
      } finally {
         if (in != null) {
            if (var7 != null) {
               try {
                  in.close();
               } catch (Throwable var15) {
                  var7.addSuppressed(var15);
               }
            } else {
               in.close();
            }
         }

      }

   }

   public void copyTo(File destination) throws IOException {
      if (destination.exists()) {
         throw new IllegalArgumentException(destination + " exists");
      } else {
         OutputStream out = new FileOutputStream(destination);
         Throwable var3 = null;

         try {
            this.writeTo(out, 0L, -1L);
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (out != null) {
               if (var3 != null) {
                  try {
                     out.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  out.close();
               }
            }

         }

      }
   }

   public String getWeakETag() {
      return this.getWeakETag("");
   }

   public String getWeakETag(String suffix) {
      try {
         StringBuilder b = new StringBuilder(32);
         b.append("W/\"");
         String name = this.getName();
         int length = name.length();
         long lhash = 0L;

         for(int i = 0; i < length; ++i) {
            lhash = 31L * lhash + (long)name.charAt(i);
         }

         B64Code.encode(this.lastModified() ^ lhash, b);
         B64Code.encode(this.length() ^ lhash, b);
         b.append(suffix);
         b.append('"');
         return b.toString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public Collection<Resource> getAllResources() {
      try {
         ArrayList<Resource> deep = new ArrayList();
         String[] list = this.list();
         if (list != null) {
            for(String i : list) {
               Resource r = this.addPath(i);
               if (r.isDirectory()) {
                  deep.addAll(r.getAllResources());
               } else {
                  deep.add(r);
               }
            }
         }

         return deep;
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   public static URL toURL(File file) throws MalformedURLException {
      return file.toURI().toURL();
   }
}
