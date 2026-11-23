package org.apache.wicket.util.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipResourceStream extends AbstractResourceStream {
   private static final long serialVersionUID = 1L;
   private static final Logger log = LoggerFactory.getLogger(ZipResourceStream.class);
   private final transient ByteArrayOutputStream bytearray;

   public ZipResourceStream(File dir, boolean recursive) {
      Args.notNull(dir, "dir");
      Args.isTrue(dir.isDirectory(), "Not a directory: '{}'", dir);
      this.bytearray = new ByteArrayOutputStream();

      try {
         ZipOutputStream out = new ZipOutputStream(this.bytearray);

         try {
            zipDir(dir, out, "", recursive);
         } finally {
            out.close();
         }

      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public ZipResourceStream(File dir) {
      this(dir, false);
   }

   private static void zipDir(File dir, ZipOutputStream out, String path, boolean recursive) throws IOException {
      Args.notNull(dir, "dir");
      Args.isTrue(dir.isDirectory(), "Not a directory: '{}'", dir);
      String[] files = dir.list();
      int BUFFER = 2048;
      byte[] data = new byte[BUFFER];
      if (files != null) {
         for(String file : files) {
            if (log.isDebugEnabled()) {
               log.debug("Adding: '{}'", file);
            }

            File f = new File(dir, file);
            if (f.isDirectory()) {
               if (recursive) {
                  zipDir(f, out, path + f.getName() + "/", recursive);
               }
            } else {
               out.putNextEntry(new ZipEntry(path + f.getName()));
               FileInputStream fi = new FileInputStream(f);
               BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);

               int count;
               try {
                  while((count = origin.read(data, 0, BUFFER)) != -1) {
                     out.write(data, 0, count);
                  }
               } finally {
                  origin.close();
               }
            }
         }
      }

      if ("".equals(path)) {
         out.close();
      }

   }

   public void close() throws IOException {
   }

   public String getContentType() {
      return null;
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      return new ByteArrayInputStream(this.bytearray.toByteArray());
   }

   public Bytes length() {
      return Bytes.bytes((long)this.bytearray.size());
   }

   public Time lastModifiedTime() {
      return null;
   }
}
