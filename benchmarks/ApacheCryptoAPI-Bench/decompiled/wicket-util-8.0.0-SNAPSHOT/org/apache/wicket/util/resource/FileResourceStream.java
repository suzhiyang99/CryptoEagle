package org.apache.wicket.util.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public class FileResourceStream extends AbstractResourceStream implements IFixedLocationResourceStream {
   private static final long serialVersionUID = 1L;
   private final File file;
   private transient InputStream inputStream;

   public FileResourceStream(File file) {
      Args.notNull(file, "file");
      this.file = file;
   }

   public FileResourceStream(java.io.File file) {
      this.file = new File(file);
   }

   public void close() throws IOException {
      if (this.inputStream != null) {
         this.inputStream.close();
         this.inputStream = null;
      }

   }

   public String getContentType() {
      String contentType = null;
      if (this.file != null) {
         contentType = URLConnection.getFileNameMap().getContentTypeFor(this.file.getName());
      }

      return contentType;
   }

   public File getFile() {
      return this.file;
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      if (this.inputStream == null) {
         try {
            this.inputStream = new FileInputStream(this.file);
         } catch (FileNotFoundException e) {
            throw new ResourceStreamNotFoundException("Resource " + this.file + " could not be found", e);
         }
      }

      return this.inputStream;
   }

   public Time lastModifiedTime() {
      return this.file != null ? this.file.lastModifiedTime() : null;
   }

   public String toString() {
      return this.file != null ? this.file.toString() : "";
   }

   public Bytes length() {
      return this.file != null ? Bytes.bytes(this.file.length()) : null;
   }

   public String locationAsString() {
      return this.file != null ? this.file.getAbsolutePath() : null;
   }
}
