package org.apache.wicket.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import org.apache.wicket.util.lang.Args;

public final class Streams {
   public static int copyAndClose(InputStream in, OutputStream out) throws IOException {
      int var2;
      try {
         var2 = copy(in, out);
      } finally {
         IOUtils.closeQuietly(in);
         IOUtils.closeQuietly(out);
      }

      return var2;
   }

   public static int copy(InputStream in, OutputStream out) throws IOException {
      return copy(in, out, 4096);
   }

   public static int copy(InputStream in, OutputStream out, int bufSize) throws IOException {
      if (bufSize <= 0) {
         throw new IllegalArgumentException("The parameter 'bufSize' must not be <= 0");
      } else {
         byte[] buffer = new byte[bufSize];
         int bytesCopied = 0;

         while(true) {
            int byteCount = in.read(buffer, 0, buffer.length);
            if (byteCount <= 0) {
               return bytesCopied;
            }

            out.write(buffer, 0, byteCount);
            bytesCopied += byteCount;
         }
      }
   }

   public static void loadFromXml(Properties properties, InputStream inputStream) throws IOException {
      if (properties == null) {
         throw new IllegalArgumentException("properties must not be null");
      } else if (inputStream == null) {
         throw new IllegalArgumentException("inputStream must not be null");
      } else {
         properties.loadFromXML(inputStream);
      }
   }

   public static InputStream readNonCaching(URL url) throws IOException {
      Args.notNull(url, "url");
      URLConnection urlConnection = url.openConnection();
      urlConnection.setUseCaches(false);
      InputStream inputStream = urlConnection.getInputStream();
      return inputStream;
   }

   public static String readString(InputStream in) throws IOException {
      return readString((Reader)(new BufferedReader(new InputStreamReader(in))));
   }

   public static String readString(InputStream in, CharSequence encoding) throws IOException {
      return readString((Reader)(new BufferedReader(new InputStreamReader(in, encoding.toString()))));
   }

   public static String readString(Reader in) throws IOException {
      StringBuilder buffer = new StringBuilder(2048);

      int value;
      while((value = in.read()) != -1) {
         buffer.append((char)value);
      }

      return buffer.toString();
   }

   private Streams() {
   }
}
