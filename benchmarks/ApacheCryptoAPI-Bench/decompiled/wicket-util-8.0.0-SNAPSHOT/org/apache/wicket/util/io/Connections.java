package org.apache.wicket.util.io;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Time;

public class Connections {
   private Connections() {
   }

   public static Time getLastModified(URL url) throws IOException {
      File file = Files.getLocalFileFromUrl(url);
      if (file != null) {
         return Files.getLastModified(file);
      } else {
         URLConnection connection = url.openConnection();

         Time var11;
         try {
            long milliseconds;
            if (connection instanceof JarURLConnection) {
               JarURLConnection jarUrlConnection = (JarURLConnection)connection;
               URL jarFileUrl = jarUrlConnection.getJarFileURL();
               URLConnection jarFileConnection = jarFileUrl.openConnection();
               jarFileConnection.setDoInput(false);
               milliseconds = jarFileConnection.getLastModified();
            } else {
               milliseconds = connection.getLastModified();
            }

            if (milliseconds != 0L) {
               var11 = Time.millis(milliseconds);
               return var11;
            }

            var11 = null;
         } finally {
            closeQuietly(connection);
         }

         return var11;
      }
   }

   public static void closeQuietly(URLConnection connection) {
      try {
         close(connection);
      } catch (Exception var2) {
      }

   }

   public static void close(URLConnection connection) throws IOException {
      if (connection != null) {
         String protocol = connection.getURL().getProtocol();
         if ("file".equals(protocol)) {
            connection.getInputStream().close();
         }

         if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection)connection).disconnect();
         }

      }
   }
}
