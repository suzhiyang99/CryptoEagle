package org.apache.openejb.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.openejb.loader.Files;
import org.apache.xbean.finder.UrlSet;

public final class URLs {
   public static File toFile(URL url) {
      return Files.toFile(url);
   }

   public static URL toFileUrl(URL url) {
      if ("jar".equals(url.getProtocol())) {
         try {
            String spec = url.getFile();
            int separator = spec.indexOf(33);
            if (separator == -1) {
               throw new MalformedURLException("no ! found in jar url spec:" + spec);
            } else {
               return new URL(spec.substring(0, separator++));
            }
         } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
         }
      } else if ("file".equals(url.getProtocol())) {
         return url;
      } else {
         throw new IllegalArgumentException("Unsupported URL scheme: " + url.toExternalForm());
      }
   }

   public static String toFilePath(URL url) {
      return toFile(url).getAbsolutePath();
   }

   public static UrlSet cullSystemAndOpenEJBJars(UrlSet original) throws IOException {
      return cullSystemJars(cullOpenEJBJars(original));
   }

   public static UrlSet cullSystemJars(UrlSet original) throws IOException {
      String sunboot = JavaSecurityManagers.getSystemProperty("sun.boot.class.path", "");
      UrlSet urls = new UrlSet(original.getUrls());
      urls = urls.exclude(ClassLoader.getSystemClassLoader().getParent());
      urls = urls.excludeJvm();
      if (!sunboot.isEmpty()) {
         urls = urls.excludePaths(sunboot);
      }

      urls = urls.exclude(".*/JavaVM.framework/.*");
      return urls;
   }

   public static UrlSet cullOpenEJBJars(UrlSet original) throws IOException {
      UrlSet urls = new UrlSet(original.getUrls());
      urls = urls.exclude(".*openejb.*");
      return urls;
   }

   public static URI uri(String uri) {
      if (!uri.startsWith("file") && !uri.startsWith("jar") && !uri.isEmpty()) {
         File f = new File(uri);
         if (f.exists()) {
            return f.toURI();
         }
      }

      return URI.create(uri.replace(" ", "%20").replace("#", "%23"));
   }

   private URLs() {
   }
}
