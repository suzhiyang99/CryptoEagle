package org.apache.openejb.core.webservices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.Base64;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.URLs;

public class UriResolver {
   private File file;
   private URI uri;
   private URL url;
   private InputStream is;
   private Class calling;

   public UriResolver() {
   }

   public UriResolver(String path) throws IOException {
      this("", path);
   }

   public UriResolver(String baseUriStr, String uriStr) throws IOException {
      this(baseUriStr, uriStr, (Class)null);
   }

   public UriResolver(String baseUriStr, String uriStr, Class calling) throws IOException {
      this.calling = calling != null ? calling : this.getClass();
      if (uriStr.startsWith("classpath:")) {
         this.tryClasspath(uriStr);
      } else if (baseUriStr != null && baseUriStr.startsWith("jar:")) {
         this.tryJar(baseUriStr, uriStr);
      } else if (uriStr.startsWith("jar:")) {
         this.tryJar(uriStr);
      } else {
         this.tryFileSystem(baseUriStr, uriStr);
      }

   }

   public void resolve(String baseUriStr, String uriStr, Class callingCls) throws IOException {
      this.calling = callingCls != null ? callingCls : this.getClass();
      this.file = null;
      this.uri = null;
      this.is = null;
      if (uriStr.startsWith("classpath:")) {
         this.tryClasspath(uriStr);
      } else if (baseUriStr != null && baseUriStr.startsWith("jar:")) {
         this.tryJar(baseUriStr, uriStr);
      } else if (uriStr.startsWith("jar:")) {
         this.tryJar(uriStr);
      } else {
         this.tryFileSystem(baseUriStr, uriStr);
      }

   }

   private void tryFileSystem(String baseUriStr, String uriStr) throws IOException, MalformedURLException {
      File uriFile = new File(uriStr);
      uriFile = new File(uriFile.getAbsolutePath());
      URI relative;
      if (uriFile.exists()) {
         relative = uriFile.toURI();
      } else {
         relative = URLs.uri(uriStr);
      }

      if (relative.isAbsolute()) {
         this.uri = relative;
         this.url = relative.toURL();

         try {
            HttpURLConnection huc = (HttpURLConnection)this.url.openConnection();
            String host = JavaSecurityManagers.getSystemProperty("http.proxyHost");
            if (host != null) {
               String username = JavaSecurityManagers.getSystemProperty("http.proxy.user");
               String password = JavaSecurityManagers.getSystemProperty("http.proxy.password");
               if (username != null && password != null) {
                  String encoded = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
                  huc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
               }
            }

            this.is = huc.getInputStream();
         } catch (ClassCastException var11) {
            this.is = IO.read(this.url);
         }
      } else if (baseUriStr != null) {
         File baseFile = new File(baseUriStr);
         if (!baseFile.exists() && baseUriStr.startsWith("file:/")) {
            baseFile = new File(baseUriStr.substring(6));
         }

         URI base;
         if (baseFile.exists()) {
            base = baseFile.toURI();
         } else {
            base = URLs.uri(baseUriStr);
         }

         base = base.resolve(relative);
         if (base.isAbsolute()) {
            try {
               baseFile = new File(base);
               if (baseFile.exists()) {
                  this.is = IO.read(base.toURL());
                  this.uri = base;
               } else {
                  this.tryClasspath(base.toString().startsWith("file:") ? base.toString().substring(5) : base.toString());
               }
            } catch (Throwable var13) {
               this.tryClasspath(base.toString().startsWith("file:") ? base.toString().substring(5) : base.toString());
            }
         }
      }

      if (this.uri != null && "file".equals(this.uri.getScheme())) {
         try {
            this.file = new File(this.uri);
         } catch (IllegalArgumentException var12) {
            this.file = URLs.toFile(this.uri.toURL());
            if (!this.file.exists()) {
               this.file = null;
            }
         }
      }

      if (this.is == null && this.file != null && this.file.exists()) {
         this.uri = this.file.toURI();

         try {
            this.is = new FileInputStream(this.file);
         } catch (FileNotFoundException e) {
            throw new OpenEJBRuntimeException("File was deleted! " + uriStr, e);
         }

         this.url = this.file.toURI().toURL();
      } else if (this.is == null) {
         this.tryClasspath(uriStr);
      }

   }

   private void tryJar(String baseStr, String uriStr) throws IOException {
      int i = baseStr.indexOf(33);
      if (i == -1) {
         this.tryFileSystem(baseStr, uriStr);
      }

      String jarBase = baseStr.substring(0, i + 1);
      String jarEntry = baseStr.substring(i + 1);
      URI u = URLs.uri(jarEntry).resolve(uriStr);
      this.tryJar(jarBase + u.toString());
      if (this.is != null) {
         if (u.isAbsolute()) {
            this.url = u.toURL();
         }

      } else {
         this.tryFileSystem("", uriStr);
      }
   }

   private void tryJar(String uriStr) throws IOException {
      int i = uriStr.indexOf(33);
      if (i != -1) {
         this.url = new URL(uriStr);

         try {
            this.is = IO.read(this.url);

            try {
               this.uri = this.url.toURI();
            } catch (URISyntaxException var4) {
            }
         } catch (IOException var5) {
            uriStr = uriStr.substring(i + 1);
            this.tryClasspath(uriStr);
         }

      }
   }

   private void tryClasspath(String uriStr) throws IOException {
      if (uriStr.startsWith("classpath:")) {
         uriStr = uriStr.substring(10);
      }

      this.url = getResource(uriStr, this.calling);
      if (this.url == null) {
         this.tryRemote(uriStr);
      } else {
         try {
            this.uri = this.url.toURI();
         } catch (URISyntaxException var5) {
            String urlStr = this.url.toString();
            if (urlStr.startsWith("jar:")) {
               int pos = urlStr.indexOf(33);
               if (pos != -1) {
                  this.uri = URLs.uri("classpath:" + urlStr.substring(pos + 1));
               }
            }
         }

         this.is = IO.read(this.url);
      }

   }

   private void tryRemote(String uriStr) throws IOException {
      try {
         this.url = new URL(URLEncoder.encode(uriStr, "UTF-8"));
         this.uri = URLs.uri(this.url.toString());
         this.is = IO.read(this.url);
      } catch (MalformedURLException var3) {
      }

   }

   public URI getURI() {
      return this.uri;
   }

   public URL getURL() {
      return this.url;
   }

   public InputStream getInputStream() {
      return this.is;
   }

   public boolean isFile() {
      return this.file != null && this.file.exists();
   }

   public File getFile() {
      return this.file;
   }

   public boolean isResolved() {
      return this.is != null;
   }

   public static URL getResource(String resourceName, Class callingClass) {
      URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
      if (url == null) {
         url = UriResolver.class.getClassLoader().getResource(resourceName);
      }

      if (url == null) {
         ClassLoader cl = callingClass.getClassLoader();
         if (cl != null) {
            url = cl.getResource(resourceName);
         }
      }

      if (url == null) {
         url = callingClass.getResource(resourceName);
      }

      return url == null && resourceName != null && resourceName.charAt(0) != '/' ? getResource('/' + resourceName, callingClass) : url;
   }
}
