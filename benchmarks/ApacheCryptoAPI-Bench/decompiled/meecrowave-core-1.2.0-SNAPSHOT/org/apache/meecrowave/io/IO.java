package org.apache.meecrowave.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

public final class IO {
   private IO() {
   }

   public static void delete(File dir) {
      if (dir.exists()) {
         if (dir.isFile()) {
            retryDelete(dir);
         } else {
            Stream.of(Optional.ofNullable(dir.listFiles()).orElseGet(() -> new File[0])).forEach((f) -> {
               if (f.isFile()) {
                  retryDelete(f);
               } else {
                  delete(f);
               }

            });
            retryDelete(dir);
         }
      }
   }

   private static void retryDelete(File f) {
      for(int i = 0; i < 3; ++i) {
         if (!f.exists() || f.delete()) {
            return;
         }

         System.gc();

         try {
            Thread.sleep(50L);
         } catch (InterruptedException var3) {
            Thread.interrupted();
         }
      }

      throw new IllegalStateException("Can't delete " + f);
   }

   public static void copy(InputStream is, OutputStream os) throws IOException {
      byte[] buffer = new byte[16384];

      int count;
      while(-1 != (count = is.read(buffer))) {
         os.write(buffer, 0, count);
      }

   }

   public static String toString(InputStream stream) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      copy(stream, baos);
      return new String(baos.toByteArray(), StandardCharsets.UTF_8);
   }

   public static void mkdirs(File d) {
      if (!d.isDirectory() && !d.mkdirs()) {
         throw new IllegalStateException(d + " can't be created");
      }
   }
}
