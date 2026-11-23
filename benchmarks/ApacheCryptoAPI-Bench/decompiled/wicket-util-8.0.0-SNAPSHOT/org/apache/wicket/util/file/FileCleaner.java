package org.apache.wicket.util.file;

import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;

public class FileCleaner implements IFileCleaner {
   private final FileCleaningTracker cleaner = new FileCleaningTracker();

   public void track(java.io.File file, Object marker) {
      this.cleaner.track(file, marker);
   }

   public void track(java.io.File file, Object marker, FileDeleteStrategy deleteStrategy) {
      this.cleaner.track(file, marker, deleteStrategy);
   }

   public void destroy() {
      this.cleaner.exitWhenFinished();
   }
}
