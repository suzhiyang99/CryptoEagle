package org.apache.openejb.util;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DirectoryMonitor {
   public static final Logger logger;
   private final long pollIntervalMillis;
   private final File target;
   private final Listener listener;
   private final Map<String, FileInfo> files = new HashMap();
   private final Timer timer;

   public DirectoryMonitor(File target, Listener listener, long pollIntervalMillis) {
      assert listener == null : "No listener specified";

      assert target.isDirectory() : "File specified is not a directory. " + target.getAbsolutePath();

      assert target.canRead() : "Directory specified cannot be read. " + target.getAbsolutePath();

      assert pollIntervalMillis > 0L : "Poll Interval must be above zero.";

      this.target = target;
      this.listener = listener;
      this.pollIntervalMillis = pollIntervalMillis;
      this.timer = new Timer(this.getClass().getSimpleName());
   }

   private Logger getLogger() {
      return logger;
   }

   public long getPollIntervalMillis() {
      return this.pollIntervalMillis;
   }

   public File getTarget() {
      return this.target;
   }

   public Listener getListener() {
      return this.listener;
   }

   public synchronized void stop() {
      this.timer.cancel();
   }

   public void start() {
      this.initialize();
      this.getLogger().debug("Scanner running.  Polling every " + this.pollIntervalMillis + " milliseconds.");
      this.timer.scheduleAtFixedRate(new TimerTask() {
         public void run() {
            try {
               DirectoryMonitor.this.scan();
            } catch (Exception e) {
               DirectoryMonitor.this.getLogger().error("Scan failed.", (Throwable)e);
            }

         }
      }, this.pollIntervalMillis, this.pollIntervalMillis);
   }

   private void initialize() {
      this.getLogger().debug("Doing initial scan of " + this.target.getAbsolutePath());
      File[] files = this.target.isDirectory() ? this.target.listFiles() : new File[]{this.target};
      if (files != null) {
         for(File file : files) {
            if (file.canRead()) {
               FileInfo now = this.newInfo(file);
               now.setChanging(false);
            }
         }
      }

   }

   public void addFile(File file) {
      this.newInfo(file);
   }

   public void removeFile(File file) {
      FileInfo fileInfo = this.oldInfo(file);
      if (fileInfo != null) {
         this.files.remove(fileInfo.getPath());
      }

   }

   private FileInfo newInfo(File child) {
      FileInfo fileInfo = (FileInfo)(child.isDirectory() ? new DirectoryInfo(child) : new FileInfo(child));
      this.files.put(fileInfo.getPath(), fileInfo);
      return fileInfo;
   }

   public void scan() {
      File[] files = this.target.isDirectory() ? this.target.listFiles() : new File[]{this.target};
      HashSet<String> missingFilesList = new HashSet(this.files.keySet());
      if (files != null) {
         for(File file : files) {
            missingFilesList.remove(file.getAbsolutePath());
            if (!file.canRead()) {
               this.getLogger().debug("not readable " + file.getName());
            } else {
               FileInfo oldStatus = this.oldInfo(file);
               FileInfo newStatus = this.newInfo(file);
               newStatus.diff(oldStatus);
               if (oldStatus == null) {
                  this.getLogger().debug("File Discovered: " + newStatus);
               } else if (newStatus.isChanging()) {
                  this.getLogger().debug("File Changing: " + newStatus);
               } else if (oldStatus.isNewFile()) {
                  this.getLogger().info("New File: " + newStatus);
                  newStatus.setNewFile(!this.listener.fileAdded(file));
               } else if (oldStatus.isChanging()) {
                  this.getLogger().info("Updated File: " + newStatus);
                  this.listener.fileUpdated(file);
                  missingFilesList.remove(oldStatus.getPath());
               }
            }
         }
      }

      for(String path : missingFilesList) {
         this.getLogger().info("File removed: " + path);
         if (this.listener.fileRemoved(new File(path))) {
            this.files.remove(path);
         }
      }

   }

   private FileInfo oldInfo(File file) {
      return (FileInfo)this.files.get(file.getAbsolutePath());
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_DEPLOY, DirectoryMonitor.class.getPackage().getName());
   }

   private static class DirectoryInfo extends FileInfo {
      public DirectoryInfo(File dir) {
         super(dir.getAbsolutePath(), 0L, getLastModifiedInDir(dir));
      }

      private static long getLastModifiedInDir(File dir) {
         assert dir != null;

         long value = dir.lastModified();
         File[] children = dir.listFiles();
         if (children != null) {
            for(File child : children) {
               if (child.canRead()) {
                  long test;
                  if (child.isDirectory()) {
                     test = getLastModifiedInDir(child);
                  } else {
                     test = child.lastModified();
                  }

                  if (test > value) {
                     value = test;
                  }
               }
            }
         }

         return value;
      }
   }

   private static class FileInfo implements Serializable {
      private final String path;
      private long size;
      private long modified;
      private boolean newFile;
      private boolean changing;

      public FileInfo(File file) {
         this(file.getAbsolutePath(), file.length(), file.lastModified());
      }

      public FileInfo(String path, long size, long modified) {
         assert path != null;

         this.path = path;
         this.size = size;
         this.modified = modified;
         this.newFile = true;
         this.changing = true;
      }

      public String getPath() {
         return this.path;
      }

      public long getSize() {
         return this.size;
      }

      public void setSize(long size) {
         this.size = size;
      }

      public long getModified() {
         return this.modified;
      }

      public void setModified(long modified) {
         this.modified = modified;
      }

      public boolean isNewFile() {
         return this.newFile;
      }

      public void setNewFile(boolean newFile) {
         this.newFile = newFile;
      }

      public boolean isChanging() {
         return this.changing;
      }

      public void setChanging(boolean changing) {
         this.changing = changing;
      }

      public boolean isSame(FileInfo info) {
         assert info != null;

         if (!this.path.equals(info.path)) {
            throw new IllegalArgumentException("Should only be used to compare two files representing the same path!");
         } else {
            return this.size == info.size && this.modified == info.modified;
         }
      }

      public String toString() {
         return this.path;
      }

      public void diff(FileInfo old) {
         if (old != null) {
            this.changing = !this.isSame(old);
            this.newFile = old.newFile;
         }

      }
   }

   public interface Listener {
      boolean fileAdded(File var1);

      boolean fileRemoved(File var1);

      void fileUpdated(File var1);
   }
}
