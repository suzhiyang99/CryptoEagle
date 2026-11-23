package org.apache.wicket.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.wicket.util.encoding.UrlDecoder;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {
   private static final Logger logger = LoggerFactory.getLogger(Files.class);
   private static final String URL_FILE_PREFIX = "file:";
   private static final String URL_LOCAL_JAR_FILE_PREFIX = "jar:file:";
   private static final String FILENAME_FORBIDDEN_CHARACTERS = "\"*/:<>?\\|,";

   private Files() {
   }

   public static String basePath(String path, String extension) {
      return extension != null ? path.substring(0, path.length() - extension.length() - 1) : path;
   }

   public static String extension(String path) {
      return path.indexOf(46) != -1 ? Strings.lastPathComponent(path, '.') : null;
   }

   public static String filename(String path) {
      return Strings.lastPathComponent(path.replace('/', java.io.File.separatorChar), java.io.File.separatorChar);
   }

   public static boolean remove(java.io.File file) {
      if (file != null && file.isFile()) {
         for(int j = 0; j < 5; ++j) {
            for(int i = 0; i < 10; ++i) {
               if (file.delete()) {
                  return true;
               }
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               Thread.currentThread().interrupt();
            }
         }
      }

      return false;
   }

   public static boolean removeFolder(java.io.File folder) {
      if (folder == null) {
         return false;
      } else {
         if (folder.isDirectory()) {
            java.io.File[] files = folder.listFiles();
            if (files != null) {
               for(java.io.File file : files) {
                  if (file.isDirectory()) {
                     removeFolder(file);
                  } else {
                     remove(file);
                  }
               }
            }
         }

         return folder.delete();
      }
   }

   public static boolean removeAsync(java.io.File file, IFileCleaner fileCleaner) {
      if (file != null && !file.isDirectory()) {
         Args.notNull(fileCleaner, "fileCleaner");
         fileCleaner.track(file, new Object());
         return true;
      } else {
         return false;
      }
   }

   public static boolean removeFolderAsync(java.io.File folder, IFileCleaner fileCleaner) {
      if (folder != null && !folder.isFile()) {
         Args.notNull(fileCleaner, "fileCleaner");
         fileCleaner.track(folder, new Object(), new FolderDeleteStrategy());
         return true;
      } else {
         return false;
      }
   }

   public static int writeTo(java.io.File file, InputStream input) throws IOException {
      return writeTo(file, input, 4096);
   }

   public static byte[] readBytes(java.io.File file) throws IOException {
      FileInputStream stream = new FileInputStream(file);

      byte[] var2;
      try {
         var2 = IOUtils.toByteArray((InputStream)stream);
      } finally {
         stream.close();
      }

      return var2;
   }

   public static int writeTo(java.io.File file, InputStream input, int bufSize) throws IOException {
      FileOutputStream out = new FileOutputStream(file);

      int var4;
      try {
         var4 = Streams.copy(input, out, bufSize);
      } finally {
         out.close();
      }

      return var4;
   }

   public static String cleanupFilename(String filename) {
      String name = filename;

      for(int i = 0; i < "\"*/:<>?\\|,".length(); ++i) {
         name = name.replace("\"*/:<>?\\|,".charAt(i), '_');
      }

      return name;
   }

   public static void copy(java.io.File sourceFile, java.io.File targetFile) throws IOException {
      BufferedInputStream in = null;
      BufferedOutputStream out = null;

      try {
         in = new BufferedInputStream(new FileInputStream(sourceFile));
         out = new BufferedOutputStream(new FileOutputStream(targetFile));
         IOUtils.copy((InputStream)in, (OutputStream)out);
      } finally {
         try {
            IOUtils.close(in);
         } finally {
            IOUtils.close(out);
         }
      }

   }

   public static java.io.File getLocalFileFromUrl(URL url) {
      URL location = (URL)Args.notNull(url, "url");
      return getLocalFileFromUrl(UrlDecoder.PATH_INSTANCE.decode(location.toExternalForm(), "UTF-8"));
   }

   public static java.io.File getLocalFileFromUrl(String url) {
      String location = (String)Args.notNull(url, "url");
      if (location.startsWith("file:")) {
         return new java.io.File(location.substring("file:".length()));
      } else if (location.startsWith("jar:file:")) {
         String path = location.substring("jar:file:".length());
         int resourceAt = path.indexOf(33);
         return resourceAt == -1 ? null : new java.io.File(path.substring(0, resourceAt));
      } else {
         return null;
      }
   }

   public static Time getLastModified(java.io.File file) {
      long millis = file.lastModified();
      return millis == 0L ? null : Time.millis(millis);
   }

   public static boolean mkdirs(java.io.File folder) {
      for(int j = 0; j < 5; ++j) {
         for(int i = 0; i < 10; ++i) {
            if (folder.mkdirs()) {
               return true;
            }
         }

         try {
            Thread.sleep(100L);
            if (folder.exists()) {
               return true;
            }
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         }
      }

      logger.error("Failed to create directory: " + folder);
      return false;
   }
}
