package org.apache.wicket.util.file;

import java.io.IOException;
import org.apache.commons.io.FileDeleteStrategy;

public class FolderDeleteStrategy extends FileDeleteStrategy {
   protected FolderDeleteStrategy() {
      super("folder");
   }

   public boolean deleteQuietly(java.io.File folder) {
      if (folder != null && !folder.isFile()) {
         java.io.File[] files = folder.listFiles();
         if (files != null) {
            for(java.io.File file : files) {
               if (file.isDirectory()) {
                  this.deleteQuietly(file);
               } else {
                  super.deleteQuietly(file);
               }
            }
         }

         return super.deleteQuietly(folder);
      } else {
         return false;
      }
   }

   public void delete(java.io.File folder) throws IOException {
      if (folder != null && !folder.isFile()) {
         java.io.File[] files = folder.listFiles();
         if (files != null) {
            for(java.io.File file : files) {
               super.delete(file);
            }
         }

      }
   }
}
