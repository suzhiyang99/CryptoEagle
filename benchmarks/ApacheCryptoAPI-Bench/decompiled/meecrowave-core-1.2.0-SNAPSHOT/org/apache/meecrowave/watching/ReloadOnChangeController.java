package org.apache.meecrowave.watching;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Context;
import org.apache.meecrowave.logging.tomcat.LogFacade;

public class ReloadOnChangeController implements AutoCloseable, Runnable {
   private final Context context;
   private final long bouncing;
   private final Collection<Path> paths = new ArrayList();
   private WatchService watchService;
   private Thread bouncer;
   private Thread watcher;
   private volatile boolean running = true;
   private volatile long redeployMarker = System.nanoTime();

   public ReloadOnChangeController(Context context, int watcherBouncing) {
      this.context = context;
      this.bouncing = (long)watcherBouncing;
   }

   public void register(File folder) {
      this.paths.add(folder.toPath());
   }

   public void start() {
      if (!this.paths.isEmpty()) {
         try {
            this.watchService = ((Path)this.paths.iterator().next()).getFileSystem().newWatchService();

            for(Path p : this.paths) {
               p.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            }
         } catch (IOException ex) {
            (new LogFacade(ReloadOnChangeController.class.getName())).warn("Hot reloading will not be available", ex);
         }

         this.watcher = new Thread(this);
         this.watcher.setName("meecrowave-watcher-controller");
         this.watcher.start();
      }
   }

   protected synchronized void redeploy() {
      this.context.reload();
   }

   public void close() {
      if (this.running) {
         this.running = false;
         long waitMs = this.bouncing * 2L + 5000L;
         if (this.bouncer != null) {
            try {
               this.bouncer.join(waitMs);
            } catch (InterruptedException var5) {
               Thread.interrupted();
            }
         }

         if (this.watcher != null) {
            try {
               this.watcher.join(waitMs);
            } catch (InterruptedException var4) {
               Thread.interrupted();
            }
         }

      }
   }

   public boolean shouldRun() {
      return !this.paths.isEmpty();
   }

   public void run() {
      if (this.watchService != null) {
         CountDownLatch latch = new CountDownLatch(1);
         this.bouncer = new Thread(() -> {
            long last = this.redeployMarker;
            latch.countDown();
            boolean needsRedeploy = false;
            long antepenultiem = -1L;

            while(this.running) {
               if (this.redeployMarker > last) {
                  antepenultiem = last;
                  last = this.redeployMarker;
                  needsRedeploy = true;
               } else if (needsRedeploy) {
                  antepenultiem = last;
               }

               try {
                  Thread.sleep(this.bouncing);
               } catch (InterruptedException var8) {
                  Thread.interrupted();
                  break;
               }

               if (needsRedeploy && last == antepenultiem) {
                  (new LogFacade(ReloadOnChangeController.class.getName())).info("Redeploying " + this.context.getName());
                  this.redeploy();
               }
            }

         });
         this.bouncer.setName("meecrowave-watcher-redeployer");
         this.bouncer.start();

         try {
            latch.await(1L, TimeUnit.MINUTES);
         } catch (InterruptedException var11) {
            Thread.interrupted();
            return;
         }

         this.paths.forEach((p) -> {
            try {
               Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                     dir.register(ReloadOnChangeController.this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                     return FileVisitResult.CONTINUE;
                  }
               });
            } catch (IOException e) {
               (new LogFacade(ReloadOnChangeController.class.getName())).warn(e.getMessage());
            }

         });

         try {
            while(this.running) {
               WatchKey watchKey = this.watchService.poll(this.bouncing, TimeUnit.MILLISECONDS);
               if (watchKey == null) {
                  Thread.sleep(this.bouncing);
               } else {
                  boolean foundNew = false;

                  for(WatchEvent<?> event : watchKey.pollEvents()) {
                     Path path = (Path)Path.class.cast(event.context());
                     WatchEvent.Kind<?> kind = event.kind();
                     if (!this.isIgnored(kind, path)) {
                        foundNew = true;
                        File file = path.toAbsolutePath().toFile();
                        if (file.isDirectory() && kind == StandardWatchEventKinds.ENTRY_CREATE) {
                           try {
                              path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                           } catch (IOException e) {
                              (new LogFacade(ReloadOnChangeController.class.getName())).warn(e.getMessage());
                           }
                        }
                        break;
                     }
                  }

                  if (foundNew) {
                     (new LogFacade(ReloadOnChangeController.class.getName())).info("Marking to redeploy " + this.context.getName());
                     this.redeployMarker = System.nanoTime();
                  }

                  if (!watchKey.reset()) {
                     watchKey.cancel();
                  }
               }
            }
         } catch (InterruptedException var12) {
            Thread.interrupted();
         }

      }
   }

   private boolean isIgnored(WatchEvent.Kind<?> kind, Path path) {
      String pathStr = path.toString();
      return pathStr.endsWith("___jb_tmp___") || pathStr.endsWith("___jb_old___") || pathStr.endsWith("~") || this.isResource(pathStr);
   }

   private boolean isResource(String pathStr) {
      int idx = pathStr.lastIndexOf(46);
      return idx > 0 && Arrays.asList(".html", ".xhtml", ".js", ".ts", ".css", ".png", ".svg", ".jpg", ".jpeg").contains(pathStr.substring(idx));
   }
}
