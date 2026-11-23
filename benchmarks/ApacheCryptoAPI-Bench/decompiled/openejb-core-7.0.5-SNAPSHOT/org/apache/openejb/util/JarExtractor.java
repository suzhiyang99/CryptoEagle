package org.apache.openejb.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Properties;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.loader.Zips;

public class JarExtractor {
   public static final Logger logger;

   public static File extract(File file, String pathname) throws IOException {
      Properties properties = SystemInstance.get().getProperties();
      String key = "tomee.unpack.dir";
      File unpackDir = file.getParentFile();
      if (properties.containsKey("tomee.unpack.dir")) {
         FileUtils base = SystemInstance.get().getBase();
         unpackDir = base.getDirectory(properties.getProperty("tomee.unpack.dir"), true);
      }

      File docBase = new File(unpackDir, pathname);
      docBase = extract(file, docBase);
      return docBase;
   }

   public static File extract(File file, File destinationDir) throws IOException {
      if (destinationDir.exists()) {
         if (destinationDir.lastModified() > file.lastModified()) {
            return destinationDir.getAbsoluteFile();
         }

         if (!deleteDir(destinationDir)) {
            Files.deleteOnExit(destinationDir);
            File pf = destinationDir.getParentFile();
            String name = destinationDir.getName() + System.currentTimeMillis();
            destinationDir = new File(pf, name);
            destinationDir.deleteOnExit();
            Files.deleteOnExit(destinationDir);
         }
      }

      logger.info("Extracting jar: " + file.getAbsolutePath());

      try {
         Files.mkdirs(destinationDir);
      } catch (Files.FileRuntimeException var5) {
         throw new IOException("Failed to create: " + destinationDir);
      }

      try {
         Zips.unzip(file, destinationDir);
      } catch (IOException e) {
         Files.delete(destinationDir);
         throw e;
      }

      logger.info("Extracted path: " + destinationDir.getAbsolutePath());
      return destinationDir.getAbsoluteFile();
   }

   public static boolean copyRecursively(File src, File dest) {
      boolean result = true;
      String[] files;
      if (src.isDirectory()) {
         files = src.list();
         result = dest.mkdir();
      } else {
         files = new String[1];
         files[0] = "";
      }

      if (files == null) {
         files = new String[0];
      }

      for(int i = 0; i < files.length && result; ++i) {
         File fileSrc = new File(src, files[i]);
         File fileDest = new File(dest, files[i]);
         if (fileSrc.isDirectory()) {
            result = copyRecursively(fileSrc, fileDest);
         } else {
            FileChannel ic = null;
            FileChannel oc = null;

            try {
               ic = (new FileInputStream(fileSrc)).getChannel();
               oc = (new FileOutputStream(fileDest)).getChannel();
               ic.transferTo(0L, ic.size(), oc);
            } catch (IOException e) {
               logger.error("Copy failed: src: " + fileSrc + ", dest: " + fileDest, (Throwable)e);
               result = false;
            } finally {
               IO.close(ic);
               IO.close(oc);
            }
         }
      }

      return result;
   }

   public static boolean delete(File dir) {
      return deleteDir(dir);
   }

   public static boolean deleteDir(File dir) {
      if (dir == null) {
         return true;
      } else {
         if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
               for(File file : files) {
                  deleteDir(file);
               }
            }
         }

         return dir.delete();
      }
   }

   protected static File extract(InputStream input, File docBase, String name) throws IOException {
      File file = new File(docBase, name);
      BufferedOutputStream output = null;

      try {
         output = new BufferedOutputStream(new FileOutputStream(file));
         byte[] buffer = new byte[2048];

         while(true) {
            int n = input.read(buffer);
            if (n <= 0) {
               return file;
            }

            output.write(buffer, 0, n);
         }
      } finally {
         if (output != null) {
            try {
               output.close();
            } catch (IOException var12) {
            }
         }

      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, "org.apache.openejb.util.resources");
   }
}
