package org.spark_project.jetty.util.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardOpenOption;
import java.security.Permission;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

/** @deprecated */
@Deprecated
public class FileResource extends Resource {
   private static final Logger LOG = Log.getLogger(FileResource.class);
   private final File _file;
   private final URI _uri;
   private final URI _alias;

   public FileResource(URL url) throws IOException, URISyntaxException {
      File file;
      try {
         file = new File(url.toURI());
         this.assertValidPath(file.toString());
      } catch (URISyntaxException e) {
         throw e;
      } catch (Exception e) {
         if (!url.toString().startsWith("file:")) {
            throw new IllegalArgumentException("!file:");
         }

         LOG.ignore(e);

         try {
            String file_url = "file:" + URIUtil.encodePath(url.toString().substring(5));
            URI uri = new URI(file_url);
            if (uri.getAuthority() == null) {
               file = new File(uri);
            } else {
               file = new File("//" + uri.getAuthority() + URIUtil.decodePath(url.getFile()));
            }
         } catch (Exception e2) {
            LOG.ignore(e2);
            URLConnection connection = url.openConnection();
            Permission perm = connection.getPermission();
            file = new File(perm == null ? url.getFile() : perm.getName());
         }
      }

      this._file = file;
      this._uri = normalizeURI(this._file, url.toURI());
      this._alias = checkFileAlias(this._uri, this._file);
   }

   public FileResource(URI uri) {
      File file = new File(uri);
      this._file = file;

      try {
         URI file_uri = this._file.toURI();
         this._uri = normalizeURI(this._file, uri);
         this.assertValidPath(file.toString());
         if (!URIUtil.equalsIgnoreEncodings(this._uri.toASCIIString(), file_uri.toString())) {
            this._alias = this._file.toURI();
         } else {
            this._alias = checkFileAlias(this._uri, this._file);
         }

      } catch (final URISyntaxException e) {
         throw new InvalidPathException(this._file.toString(), e.getMessage()) {
            {
               this.initCause(e);
            }
         };
      }
   }

   public FileResource(File file) {
      this.assertValidPath(file.toString());
      this._file = file;

      try {
         this._uri = normalizeURI(this._file, this._file.toURI());
      } catch (final URISyntaxException e) {
         throw new InvalidPathException(this._file.toString(), e.getMessage()) {
            {
               this.initCause(e);
            }
         };
      }

      this._alias = checkFileAlias(this._uri, this._file);
   }

   public FileResource(File base, String childPath) {
      String encoded = URIUtil.encodePath(childPath);
      this._file = new File(base, childPath);

      URI uri;
      try {
         if (base.isDirectory()) {
            uri = new URI(URIUtil.addEncodedPaths(base.toURI().toASCIIString(), encoded));
         } else {
            uri = new URI(base.toURI().toASCIIString() + encoded);
         }
      } catch (final URISyntaxException e) {
         throw new InvalidPathException(base.toString() + childPath, e.getMessage()) {
            {
               this.initCause(e);
            }
         };
      }

      this._uri = uri;
      this._alias = checkFileAlias(this._uri, this._file);
   }

   private static URI normalizeURI(File file, URI uri) throws URISyntaxException {
      String u = uri.toASCIIString();
      if (file.isDirectory()) {
         if (!u.endsWith("/")) {
            u = u + "/";
         }
      } else if (file.exists() && u.endsWith("/")) {
         u = u.substring(0, u.length() - 1);
      }

      return new URI(u);
   }

   private static URI checkFileAlias(URI uri, File file) {
      try {
         if (!URIUtil.equalsIgnoreEncodings(uri, file.toURI())) {
            return (new File(uri)).getAbsoluteFile().toURI();
         } else {
            String abs = file.getAbsolutePath();
            String can = file.getCanonicalPath();
            if (!abs.equals(can)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("ALIAS abs={} can={}", abs, can);
               }

               URI alias = (new File(can)).toURI();
               return new URI("file://" + URIUtil.encodePath(alias.getPath()));
            } else {
               return null;
            }
         }
      } catch (Exception e) {
         LOG.warn("bad alias for {}: {}", file, e.toString());
         LOG.debug(e);

         try {
            return new URI("http://eclipse.org/bad/canonical/alias");
         } catch (Exception e2) {
            LOG.ignore(e2);
            throw new RuntimeException(e);
         }
      }
   }

   public Resource addPath(String path) throws IOException, MalformedURLException {
      this.assertValidPath(path);
      path = URIUtil.canonicalPath(path);
      if (path == null) {
         throw new MalformedURLException();
      } else {
         return "/".equals(path) ? this : new FileResource(this._file, path);
      }
   }

   private void assertValidPath(String path) {
      int idx = StringUtil.indexOfControlChars(path);
      if (idx >= 0) {
         throw new InvalidPathException(path, "Invalid Character at index " + idx);
      }
   }

   public URI getAlias() {
      return this._alias;
   }

   public boolean exists() {
      return this._file.exists();
   }

   public long lastModified() {
      return this._file.lastModified();
   }

   public boolean isDirectory() {
      return this._file.exists() && this._file.isDirectory() || this._uri.toASCIIString().endsWith("/");
   }

   public long length() {
      return this._file.length();
   }

   public String getName() {
      return this._file.getAbsolutePath();
   }

   public File getFile() {
      return this._file;
   }

   public InputStream getInputStream() throws IOException {
      return new FileInputStream(this._file);
   }

   public ReadableByteChannel getReadableByteChannel() throws IOException {
      return FileChannel.open(this._file.toPath(), StandardOpenOption.READ);
   }

   public boolean delete() throws SecurityException {
      return this._file.delete();
   }

   public boolean renameTo(Resource dest) throws SecurityException {
      return dest instanceof FileResource ? this._file.renameTo(((FileResource)dest)._file) : false;
   }

   public String[] list() {
      String[] list = this._file.list();
      if (list == null) {
         return null;
      } else {
         int i = list.length;

         while(i-- > 0) {
            if ((new File(this._file, list[i])).isDirectory() && !list[i].endsWith("/")) {
               list[i] = list[i] + "/";
            }
         }

         return list;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (null != o && o instanceof FileResource) {
         FileResource f = (FileResource)o;
         return f._file == this._file || null != this._file && this._file.equals(f._file);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return null == this._file ? super.hashCode() : this._file.hashCode();
   }

   public void copyTo(File destination) throws IOException {
      if (this.isDirectory()) {
         IO.copyDir(this.getFile(), destination);
      } else {
         if (destination.exists()) {
            throw new IllegalArgumentException(destination + " exists");
         }

         IO.copy(this.getFile(), destination);
      }

   }

   public boolean isContainedIn(Resource r) throws MalformedURLException {
      return false;
   }

   public void close() {
   }

   public URL getURL() {
      try {
         return this._uri.toURL();
      } catch (MalformedURLException e) {
         throw new IllegalStateException(e);
      }
   }

   public URI getURI() {
      return this._uri;
   }

   public String toString() {
      return this._uri.toString();
   }
}
