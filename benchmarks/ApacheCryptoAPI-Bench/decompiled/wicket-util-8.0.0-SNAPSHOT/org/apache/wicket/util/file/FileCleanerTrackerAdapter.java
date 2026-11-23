package org.apache.wicket.util.file;

import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.wicket.util.lang.Args;

public class FileCleanerTrackerAdapter extends FileCleaningTracker {
   private final IFileCleaner fileCleaner;

   public FileCleanerTrackerAdapter(IFileCleaner fileCleaner) {
      this.fileCleaner = (IFileCleaner)Args.notNull(fileCleaner, "fileCleaner");
   }

   public void track(java.io.File file, Object marker) {
      this.fileCleaner.track(file, marker);
   }

   public void track(java.io.File file, Object marker, FileDeleteStrategy deleteStrategy) {
      this.fileCleaner.track(file, marker, deleteStrategy);
   }

   public void track(String path, Object marker) {
      this.fileCleaner.track(new File(path), marker);
   }

   public void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
      this.fileCleaner.track(new File(path), marker, deleteStrategy);
   }
}
