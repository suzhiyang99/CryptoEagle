package org.apache.wicket.util.file;

import java.io.IOException;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

public class Path implements IResourceFinder {
   private final Folder folder;

   public Path(String folder) {
      this(new Folder(folder));
   }

   public Path(Folder folder) {
      if (!folder.exists()) {
         throw new IllegalArgumentException("Folder " + folder + " does not exist");
      } else {
         this.folder = folder;
      }
   }

   public IResourceStream find(Class<?> clazz, String pathname) {
      File file = new File(this.folder, pathname);
      return file.exists() ? new FileResourceStream(file) : null;
   }

   public String toString() {
      try {
         return "[Path: folder = " + this.folder.getCanonicalPath() + "]";
      } catch (IOException var2) {
         return "[Path: exception while inspecting folder]";
      }
   }
}
