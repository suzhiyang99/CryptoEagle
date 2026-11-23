package org.apache.openejb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.openejb.loader.IO;

public final class JarCreator {
   private static final int BUFFER_SIZE = 1024;

   private JarCreator() {
   }

   public static void jarDir(File dir, File zipName) throws IOException, IllegalArgumentException {
      String[] entries = dir.list();
      JarOutputStream out = new JarOutputStream(new FileOutputStream(zipName));

      try {
         String prefix = dir.getAbsolutePath();
         if (!prefix.endsWith(File.separator)) {
            prefix = prefix + File.separator;
         }

         for(String entry : entries) {
            File f = new File(dir, entry);
            jarFile(out, f, prefix);
         }
      } finally {
         IO.close(out);
      }

   }

   private static void jarFile(JarOutputStream out, File f, String prefix) throws IOException {
      if (f.isDirectory()) {
         File[] files = f.listFiles();
         if (null != files) {
            for(File child : files) {
               jarFile(out, child, prefix);
            }
         }
      } else {
         String path = f.getPath().replace(prefix, "");
         FileInputStream in = new FileInputStream(f);
         Throwable var20 = null;

         try {
            JarEntry entry = new JarEntry(path);
            out.putNextEntry(entry);
            byte[] buffer = new byte[1024];

            int bytesRead;
            while((bytesRead = in.read(buffer)) != -1) {
               out.write(buffer, 0, bytesRead);
            }
         } catch (Throwable var16) {
            var20 = var16;
            throw var16;
         } finally {
            if (in != null) {
               if (var20 != null) {
                  try {
                     in.close();
                  } catch (Throwable var15) {
                     var20.addSuppressed(var15);
                  }
               } else {
                  in.close();
               }
            }

         }
      }

   }
}
