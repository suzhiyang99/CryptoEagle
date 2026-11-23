package org.apache.wicket.util.file;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.util.lang.Bytes;

public class Folder extends File {
   private static final long serialVersionUID = 1L;

   public Folder(Folder parent, String child) {
      super((File)parent, child);
   }

   public Folder(java.io.File file) {
      this(file.getPath());
   }

   public Folder(String pathname) {
      super(pathname);
   }

   public Folder(String parent, String child) {
      super(parent, child);
   }

   public Folder(URI uri) {
      super(uri);
   }

   public void ensureExists() throws IOException {
      if (!this.exists() && !this.mkdirs()) {
         throw new IOException("Unable to create folder " + this);
      }
   }

   public Folder folder(String name) {
      return new Folder(this, name);
   }

   public Bytes freeDiskSpace() {
      return Bytes.bytes(super.getFreeSpace());
   }

   public File[] getFiles() {
      return this.getFiles(Folder.FileFilter.ALL_FILES);
   }

   public File[] getNestedFiles() {
      return this.getNestedFiles(Folder.FileFilter.ALL_FILES);
   }

   public File[] getNestedFiles(FileFilter filter) {
      List<File> files = new ArrayList();
      files.addAll(Arrays.asList(this.getFiles(filter)));
      Folder[] folders = this.getFolders();

      for(Folder folder : folders) {
         files.addAll(Arrays.asList(folder.getNestedFiles(filter)));
      }

      return (File[])files.toArray(new File[files.size()]);
   }

   public File[] getFiles(final FileFilter filter) {
      java.io.File[] files = this.listFiles(new java.io.FileFilter() {
         public boolean accept(java.io.File file) {
            return file.isFile() && filter.accept(new File(file));
         }
      });
      if (files == null) {
         return new File[0];
      } else {
         File[] wicketFiles = new File[files.length];

         for(int i = 0; i < files.length; ++i) {
            wicketFiles[i] = new File(files[i]);
         }

         return wicketFiles;
      }
   }

   public Folder[] getFolders() {
      return this.getFolders(new FolderFilter() {
         public boolean accept(Folder folder) {
            String name = folder.getName();
            return !name.equals(".") && !name.equals("..");
         }
      });
   }

   public Folder[] getFolders(final FolderFilter filter) {
      java.io.File[] files = this.listFiles(new java.io.FileFilter() {
         public boolean accept(java.io.File file) {
            return file.isDirectory() && filter.accept(new Folder(file.getPath()));
         }
      });
      if (files == null) {
         return new Folder[0];
      } else {
         Folder[] wicketFolders = new Folder[files.length];

         for(int i = 0; i < files.length; ++i) {
            wicketFolders[i] = new Folder(files[i]);
         }

         return wicketFolders;
      }
   }

   public boolean remove() {
      return this.remove(this);
   }

   public boolean removeFiles() {
      File[] files = this.getFiles();
      boolean success = true;

      for(File file : files) {
         success = file.remove() && success;
      }

      return success;
   }

   private boolean remove(Folder folder) {
      Folder[] folders = this.getFolders();
      boolean success = true;

      for(Folder subfolder : folders) {
         success = subfolder.remove() && success;
      }

      success = this.removeFiles() && success;
      return folder.delete() && success;
   }

   public interface FileFilter {
      FileFilter ALL_FILES = new FileFilter() {
         public boolean accept(File file) {
            return true;
         }
      };

      boolean accept(File var1);
   }

   public interface FolderFilter {
      boolean accept(Folder var1);
   }
}
