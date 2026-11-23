package org.apache.manifoldcf.core.lockmanager;

public class LocalLock {
   private int readCount = 0;
   private int writeCount = 0;
   private int nonExWriteCount = 0;

   public boolean hasWriteLock() {
      return this.writeCount > 0;
   }

   public boolean hasReadLock() {
      return this.readCount > 0;
   }

   public boolean hasNonExWriteLock() {
      return this.nonExWriteCount > 0;
   }

   public void incrementReadLocks() {
      ++this.readCount;
   }

   public void incrementNonExWriteLocks() {
      ++this.nonExWriteCount;
   }

   public void incrementWriteLocks() {
      ++this.writeCount;
   }

   public void decrementReadLocks() {
      --this.readCount;
   }

   public void decrementNonExWriteLocks() {
      --this.nonExWriteCount;
   }

   public void decrementWriteLocks() {
      --this.writeCount;
   }
}
