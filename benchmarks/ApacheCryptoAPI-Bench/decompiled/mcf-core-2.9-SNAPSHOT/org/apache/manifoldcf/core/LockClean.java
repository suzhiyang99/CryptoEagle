package org.apache.manifoldcf.core;

import java.io.File;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.ThreadContextFactory;
import org.apache.manifoldcf.core.lockmanager.FileLockManager;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class LockClean implements InitializationCommand {
   public static final String _rcsid = "@(#)$Id: LockClean.java 988245 2010-08-23 18:39:35Z kwright $";

   public void execute() throws ManifoldCFException {
      ManifoldCF.initializeEnvironment(ThreadContextFactory.make());
      File synchDir = FileLockManager.getSynchDirectoryProperty();
      if (synchDir != null && synchDir.isDirectory()) {
         this.removeContentsOfDirectory(synchDir);
      }

      Logging.root.info("Synchronization storage cleaned up");
   }

   private void removeContentsOfDirectory(File directory) {
      File[] files = directory.listFiles();

      for(int i = 0; i < files.length; ++i) {
         if (files[i].isDirectory()) {
            this.removeDirectory(files[i]);
         } else {
            files[i].delete();
         }
      }

   }

   private void removeDirectory(File directory) {
      this.removeContentsOfDirectory(directory);
      directory.delete();
   }

   public static void main(String[] args) {
      if (args.length != 0) {
         System.err.println("Usage: LockClean");
         System.exit(1);
      }

      LockClean lockClean = new LockClean();

      try {
         lockClean.execute();
         System.err.println("Synchronization storage cleaned up");
      } catch (ManifoldCFException e) {
         e.printStackTrace(System.err);
         System.exit(2);
      }

   }
}
