package org.apache.openejb.config;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.event.ContainerSystemPostCreate;
import org.apache.openejb.assembler.classic.event.ContainerSystemPreDestroy;
import org.apache.openejb.config.sys.Deployments;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class AutoDeployer {
   private static final Logger logger;
   private static final Semaphore SEMAPHORE;
   private final ConfigurationFactory factory;
   private final long pollIntervalMillis;
   private final Map<String, FileInfo> files = new HashMap();
   private final Timer timer;
   private final List<Deployments> deployments = new ArrayList();

   public AutoDeployer(ConfigurationFactory factory, List<Deployments> deployments) {
      Options options = SystemInstance.get().getOptions();
      Duration interval = (Duration)options.get("openejb.autodeploy.interval", new Duration(2L, TimeUnit.SECONDS));
      if (interval.getUnit() == null) {
         interval.setUnit(TimeUnit.SECONDS);
      }

      this.factory = factory;
      this.deployments.addAll(deployments);
      this.pollIntervalMillis = interval.getUnit().toMillis(interval.getTime());
      this.timer = new Timer(this.getClass().getSimpleName(), true);
   }

   private boolean fileAdded(File file) {
      String appPath = file.getAbsolutePath();
      logger.info("Starting Auto-Deployment of: " + appPath);

      try {
         AppInfo appInfo = this.factory.configureApplication(file);
         appInfo.paths.add(appPath);
         appInfo.paths.add(appInfo.path);
         if (logger.isDebugEnabled()) {
            for(String path : appInfo.paths) {
               logger.debug("Auto-Deployment path: " + path);
            }
         }

         Assembler assembler = getAssembler();
         if (null == assembler) {
            throw new Exception("Assembler is not available for Auto-Deployment of: " + appPath);
         }

         assembler.createApplication(appInfo);
         ((FileInfo)this.files.get(appPath)).setModified(getLastModifiedInDir(new File(appPath)));
      } catch (Exception e) {
         logger.error("Failed Auto-Deployment of: " + appPath, (Throwable)e);
      }

      return true;
   }

   private static Assembler getAssembler() {
      return (Assembler)SystemInstance.get().getComponent(Assembler.class);
   }

   private static boolean fileRemoved(File file) {
      if (null == file) {
         return true;
      } else {
         String path = file.getAbsolutePath();
         Assembler assembler = getAssembler();
         if (null != assembler) {
            for(AppInfo app : assembler.getDeployedApplications()) {
               if (app.paths.contains(path)) {
                  logger.info("Starting Auto-Undeployment of: " + app.appId + " - " + file.getAbsolutePath());

                  try {
                     assembler.destroyApplication(app);

                     for(String location : app.paths) {
                        if (!(new File(location)).equals(file)) {
                           File delete = new File(location.replace("%20", " ").replace("%23", "#"));

                           for(int i = 0; i < 3; ++i) {
                              try {
                                 Files.remove(delete);
                                 break;
                              } catch (Exception var11) {
                                 if (i < 2) {
                                    Thread.sleep(100L);
                                 } else {
                                    logger.warning("Failed to delete: " + delete);
                                 }
                              }
                           }

                           logger.debug("Auto-Undeploy: Delete " + location);
                        }
                     }

                     logger.info("Completed Auto-Undeployment of: " + app.appId);
                  } catch (Throwable e) {
                     logger.error("Auto-Undeploy Failed: " + file.getAbsolutePath(), e);
                  }
                  break;
               }
            }
         }

         return true;
      }
   }

   public void fileUpdated(File file) {
      fileRemoved(file);
      this.fileAdded(file);
   }

   public void observe(@Observes ContainerSystemPostCreate postCreate) {
      this.start();
   }

   public void observe(@Observes ContainerSystemPreDestroy preDestroy) {
      this.stop();
   }

   public void stop() {
      this.timer.cancel();

      try {
         SEMAPHORE.acquire();
      } catch (InterruptedException var5) {
      } finally {
         SEMAPHORE.release();
      }

   }

   public void start() {
      try {
         SEMAPHORE.acquire();
      } catch (InterruptedException var6) {
         logger.warning("AutoDeployer.start failed to obtain lock");
         return;
      }

      try {
         this.initialize();
         logger.info("Starting Auto-Deployer with a polling interval of " + this.pollIntervalMillis + "ms");
         this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
               try {
                  AutoDeployer.this.scan();
               } catch (Exception e) {
                  AutoDeployer.logger.error("Scan failed.", (Throwable)e);
               }

            }
         }, this.pollIntervalMillis, this.pollIntervalMillis);
      } finally {
         SEMAPHORE.release();
      }

   }

   private void initialize() {
      for(File file : this.list()) {
         if (file.canRead()) {
            FileInfo now = this.newInfo(file);
            now.setChanging(false);
            now.setNewFile(false);
            logger.debug("Auto-Deployer initialization found: " + file.getAbsolutePath());
         }
      }

   }

   private FileInfo newInfo(File child) {
      FileInfo fileInfo = (FileInfo)(child.isDirectory() ? new DirectoryInfo(child) : new FileInfo(child));
      this.files.put(fileInfo.getPath(), fileInfo);
      return fileInfo;
   }

   public synchronized void scan() {
      try {
         SEMAPHORE.acquire();
      } catch (InterruptedException var10) {
         logger.warning("AutoDeployer.scan failed to obtain lock");
         return;
      }

      try {
         List<File> files = this.list();
         HashSet<String> missingFilesList = new HashSet(this.files.keySet());

         for(File file : files) {
            missingFilesList.remove(file.getAbsolutePath());
            if (!file.canRead()) {
               logger.debug("not readable " + file.getName());
            } else {
               FileInfo oldStatus = this.oldInfo(file);
               FileInfo newStatus = this.newInfo(file);
               newStatus.diff(oldStatus);
               if (oldStatus == null) {
                  logger.debug("File Discovered: " + newStatus);
               } else if (newStatus.isChanging()) {
                  logger.debug("File Changing: " + newStatus);
               } else if (oldStatus.isNewFile()) {
                  logger.info("New File: " + newStatus);
                  newStatus.setNewFile(!this.fileAdded(file));
                  newStatus.setChanging(false);
               } else if (oldStatus.isChanging()) {
                  logger.info("Updated Auto-Deployer File: " + newStatus);
                  this.fileUpdated(file);
                  missingFilesList.remove(oldStatus.getPath());
               }
            }
         }

         for(String path : missingFilesList) {
            logger.info("File removed: " + path);
            if (fileRemoved(new File(path))) {
               this.files.remove(path);
            }
         }
      } finally {
         SEMAPHORE.release();
      }

   }

   private List<File> list() {
      List<File> files = new ArrayList();
      FileUtils base = SystemInstance.get().getBase();

      for(Deployments deployment : this.deployments) {
         DeploymentsResolver.loadFrom(deployment, base, files);
      }

      return files;
   }

   private FileInfo oldInfo(File file) {
      return (FileInfo)this.files.get(file.getAbsolutePath());
   }

   public static long getLastModifiedInDir(File dir) {
      assert dir != null;

      if (dir.isFile()) {
         return dir.lastModified();
      } else {
         long value = dir.lastModified();
         File[] children = dir.listFiles();
         if (children != null) {
            for(File child : children) {
               if (child.canRead()) {
                  long test;
                  if (child.isDirectory()) {
                     if ((new File(child.getParentFile(), child.getName() + ".war")).exists()) {
                        continue;
                     }

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

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, AutoDeployer.class);
      SEMAPHORE = new Semaphore(1, true);
   }

   private static class DirectoryInfo extends FileInfo {
      public DirectoryInfo(File dir) {
         super(dir.getAbsolutePath(), 0L, AutoDeployer.getLastModifiedInDir(dir));
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
}
