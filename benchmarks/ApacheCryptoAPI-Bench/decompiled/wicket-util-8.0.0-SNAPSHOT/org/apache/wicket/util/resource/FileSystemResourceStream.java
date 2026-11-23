package org.apache.wicket.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public class FileSystemResourceStream extends AbstractResourceStream implements IFixedLocationResourceStream {
   private static final long serialVersionUID = 1L;
   private final Path path;
   private transient InputStream inputStream;

   public FileSystemResourceStream(Path path) {
      Args.notNull(path, "path");
      this.path = path;
   }

   public FileSystemResourceStream(File file) {
      Args.notNull(file, "file");
      this.path = file.toPath();
   }

   public FileSystemResourceStream(org.apache.wicket.util.file.File file) {
      Args.notNull(file, "file");
      this.path = file.toPath();
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      if (this.inputStream == null) {
         try {
            this.inputStream = Files.newInputStream(this.path);
         } catch (IOException e) {
            throw new ResourceStreamNotFoundException("Input stream of path " + this.path + " could not be acquired", e);
         }
      }

      return this.inputStream;
   }

   public void close() throws IOException {
      if (this.inputStream != null) {
         this.inputStream.close();
         this.inputStream = null;
      }

   }

   public String getContentType() {
      try {
         String contentType = Files.probeContentType(this.path);
         if (contentType == null) {
            contentType = URLConnection.getFileNameMap().getContentTypeFor(this.path.getFileName().toString());
         }

         return contentType;
      } catch (IOException e) {
         throw new RuntimeException("Content type of path " + this.path + " could not be acquired", e);
      }
   }

   public final Path getPath() {
      return this.path;
   }

   public Time lastModifiedTime() {
      try {
         BasicFileAttributes attributes = Files.readAttributes(this.path, BasicFileAttributes.class);
         FileTime lastModifiedTime = attributes.lastModifiedTime();
         long millis = lastModifiedTime.toMillis();
         return Time.millis(millis);
      } catch (IOException e) {
         throw new RuntimeException("Modification time of path " + this.path + " could not be acquired", e);
      }
   }

   public Bytes length() {
      try {
         BasicFileAttributes attributes = Files.readAttributes(this.path, BasicFileAttributes.class);
         long size = attributes.size();
         return Bytes.bytes(size);
      } catch (IOException e) {
         throw new RuntimeException("Length of path " + this.path + " could not be acquired", e);
      }
   }

   public String locationAsString() {
      return this.path.toString();
   }

   public String toString() {
      return this.locationAsString();
   }
}
