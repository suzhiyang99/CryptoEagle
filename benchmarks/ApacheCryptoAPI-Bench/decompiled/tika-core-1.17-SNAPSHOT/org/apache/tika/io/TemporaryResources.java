package org.apache.tika.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import org.apache.tika.exception.TikaException;

public class TemporaryResources implements Closeable {
   private final LinkedList<Closeable> resources = new LinkedList();
   private Path tempFileDir = null;

   public void setTemporaryFileDirectory(Path tempFileDir) {
      this.tempFileDir = tempFileDir;
   }

   public void setTemporaryFileDirectory(File tempFileDir) {
      this.tempFileDir = tempFileDir == null ? null : tempFileDir.toPath();
   }

   public Path createTempFile() throws IOException {
      final Path path = this.tempFileDir == null ? Files.createTempFile("apache-tika-", ".tmp") : Files.createTempFile(this.tempFileDir, "apache-tika-", ".tmp");
      this.addResource(new Closeable() {
         public void close() throws IOException {
            Files.delete(path);
         }
      });
      return path;
   }

   public File createTemporaryFile() throws IOException {
      return this.createTempFile().toFile();
   }

   public void addResource(Closeable resource) {
      this.resources.addFirst(resource);
   }

   public <T extends Closeable> T getResource(Class<T> klass) {
      for(Closeable resource : this.resources) {
         if (klass.isAssignableFrom(resource.getClass())) {
            return (T)resource;
         }
      }

      return null;
   }

   public void close() throws IOException {
      IOException exception = null;

      for(Closeable resource : this.resources) {
         try {
            resource.close();
         } catch (IOException e) {
            if (exception == null) {
               exception = e;
            } else {
               exception.addSuppressed(e);
            }
         }
      }

      this.resources.clear();
      if (exception != null) {
         throw exception;
      }
   }

   public void dispose() throws TikaException {
      try {
         this.close();
      } catch (IOException e) {
         throw new TikaException("Failed to close temporary resources", e);
      }
   }
}
