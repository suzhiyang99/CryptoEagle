package org.spark_project.jetty.util.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;

public class FileDestroyable implements Destroyable {
   private static final Logger LOG = Log.getLogger(FileDestroyable.class);
   final List<File> _files = new ArrayList();

   public FileDestroyable() {
   }

   public FileDestroyable(String file) throws IOException {
      this._files.add(Resource.newResource(file).getFile());
   }

   public FileDestroyable(File file) {
      this._files.add(file);
   }

   public void addFile(String file) throws IOException {
      Resource r = Resource.newResource(file);
      Throwable var3 = null;

      try {
         this._files.add(r.getFile());
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (r != null) {
            if (var3 != null) {
               try {
                  r.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               r.close();
            }
         }

      }

   }

   public void addFile(File file) {
      this._files.add(file);
   }

   public void addFiles(Collection<File> files) {
      this._files.addAll(files);
   }

   public void removeFile(String file) throws IOException {
      Resource r = Resource.newResource(file);
      Throwable var3 = null;

      try {
         this._files.remove(r.getFile());
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (r != null) {
            if (var3 != null) {
               try {
                  r.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               r.close();
            }
         }

      }

   }

   public void removeFile(File file) {
      this._files.remove(file);
   }

   public void destroy() {
      for(File file : this._files) {
         if (file.exists()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Destroy {}", file);
            }

            IO.delete(file);
         }
      }

   }
}
