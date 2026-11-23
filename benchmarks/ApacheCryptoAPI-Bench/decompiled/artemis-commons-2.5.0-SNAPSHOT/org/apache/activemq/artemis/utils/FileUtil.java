package org.apache.activemq.artemis.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger;

public class FileUtil {
   public static void makeExec(File file) throws IOException {
      try {
         Files.setPosixFilePermissions(file.toPath(), new HashSet(Arrays.asList(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_EXECUTE)));
      } catch (Throwable var2) {
      }

   }

   public static final boolean deleteDirectory(File directory) {
      if (directory.isDirectory()) {
         String[] files = directory.list();
         int num = 5;

         for(int attempts = 0; files == null && attempts < num; ++attempts) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var9) {
            }

            files = directory.list();
         }

         if (files == null) {
            ActiveMQUtilLogger.LOGGER.failedListFilesToCleanup(directory.getAbsolutePath());
         } else {
            for(String file : files) {
               File f = new File(directory, file);
               if (!deleteDirectory(f)) {
                  ActiveMQUtilLogger.LOGGER.failedToCleanupFile(f.getAbsolutePath());
               }
            }
         }
      }

      return directory.delete();
   }
}
