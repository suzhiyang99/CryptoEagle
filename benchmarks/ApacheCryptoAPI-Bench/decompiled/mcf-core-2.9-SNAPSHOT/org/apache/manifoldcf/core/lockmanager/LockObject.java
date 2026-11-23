package org.apache.manifoldcf.core.lockmanager;

import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class LockObject {
   public static final String _rcsid = "@(#)$Id: LockObject.java 988245 2010-08-23 18:39:35Z kwright $";
   protected final Object lockKey;
   private LockPool lockPool;
   private volatile boolean obtainedWrite = false;
   private volatile int obtainedRead = 0;
   private volatile int obtainedNonExWrite = 0;
   protected static final String LOCKEDANOTHERTHREAD = "Locked by another thread in this JVM";
   protected static final String LOCKEDANOTHERJVM = "Locked by another JVM";

   public LockObject(LockPool lockPool, Object lockKey) {
      this.lockPool = lockPool;
      this.lockKey = lockKey;
   }

   public synchronized void makeInvalid() {
      this.lockPool = null;
   }

   public synchronized void enterWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      while(this.lockPool != null) {
         try {
            if (this.obtainedWrite) {
               throw new LocalLockException("Locked by another thread in this JVM");
            }

            if (this.obtainedRead <= 0 && this.obtainedNonExWrite <= 0) {
               this.obtainGlobalWriteLock();
               this.obtainedWrite = true;
               return;
            }

            throw new LocalLockException("Locked by another thread in this JVM");
         } catch (LocalLockException var2) {
            this.wait();
         }
      }

      throw new ExpiredObjectException("Invalid");
   }

   public synchronized void enterWriteLockNoWait() throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (this.obtainedWrite) {
         throw new LocalLockException("Locked by another thread in this JVM");
      } else if (this.obtainedRead <= 0 && this.obtainedNonExWrite <= 0) {
         this.obtainGlobalWriteLockNoWait();
         this.obtainedWrite = true;
      } else {
         throw new LocalLockException("Locked by another thread in this JVM");
      }
   }

   protected void obtainGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void obtainGlobalWriteLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.obtainGlobalWriteLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }

   public synchronized boolean leaveWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (!this.obtainedWrite) {
         throw new RuntimeException("JVM failure: Don't hold lock for object " + this.toString());
      } else {
         this.clearGlobalWriteLock();
         this.obtainedWrite = false;
         this.notifyAll();
         return true;
      }
   }

   protected void clearGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void clearGlobalWriteLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.clearGlobalWriteLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }

   public synchronized void enterNonExWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      while(this.lockPool != null) {
         try {
            if (!this.obtainedWrite && this.obtainedRead <= 0) {
               if (this.obtainedNonExWrite > 0) {
                  ++this.obtainedNonExWrite;
                  return;
               }

               this.obtainGlobalNonExWriteLock();
               ++this.obtainedNonExWrite;
               return;
            }

            throw new LocalLockException("Locked by another thread in this JVM");
         } catch (LocalLockException var2) {
            this.wait();
         }
      }

      throw new ExpiredObjectException("Invalid");
   }

   public synchronized void enterNonExWriteLockNoWait() throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (!this.obtainedWrite && this.obtainedRead <= 0) {
         if (this.obtainedNonExWrite > 0) {
            ++this.obtainedNonExWrite;
         } else {
            this.obtainGlobalNonExWriteLockNoWait();
            ++this.obtainedNonExWrite;
         }
      } else {
         throw new LocalLockException("Locked by another thread in this JVM");
      }
   }

   protected void obtainGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void obtainGlobalNonExWriteLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.obtainGlobalNonExWriteLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }

   public synchronized boolean leaveNonExWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (this.obtainedNonExWrite == 0) {
         throw new RuntimeException("JVM error: Don't hold lock for object " + this.toString());
      } else if (this.obtainedNonExWrite > 1) {
         --this.obtainedNonExWrite;
         return false;
      } else {
         this.clearGlobalNonExWriteLock();
         --this.obtainedNonExWrite;
         this.notifyAll();
         return true;
      }
   }

   protected void clearGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void clearGlobalNonExWriteLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.clearGlobalNonExWriteLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }

   public synchronized void enterReadLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      while(this.lockPool != null) {
         try {
            if (!this.obtainedWrite && this.obtainedNonExWrite <= 0) {
               if (this.obtainedRead > 0) {
                  ++this.obtainedRead;
                  return;
               }

               this.obtainGlobalReadLock();
               this.obtainedRead = 1;
               return;
            }

            throw new LocalLockException("Locked by another thread in this JVM");
         } catch (LocalLockException var2) {
            this.wait();
         }
      }

      throw new ExpiredObjectException("Invalid");
   }

   public synchronized void enterReadLockNoWait() throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (!this.obtainedWrite && this.obtainedNonExWrite <= 0) {
         if (this.obtainedRead > 0) {
            ++this.obtainedRead;
         } else {
            this.obtainGlobalReadLockNoWait();
            this.obtainedRead = 1;
         }
      } else {
         throw new LocalLockException("Locked by another thread in this JVM");
      }
   }

   protected void obtainGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void obtainGlobalReadLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.obtainGlobalReadLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }

   public synchronized boolean leaveReadLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      if (this.lockPool == null) {
         throw new ExpiredObjectException("Invalid");
      } else if (this.obtainedRead == 0) {
         throw new RuntimeException("JVM error: Don't hold lock for object " + this.toString());
      } else if (this.obtainedRead > 1) {
         --this.obtainedRead;
         return false;
      } else {
         this.clearGlobalReadLock();
         --this.obtainedRead;
         this.notifyAll();
         return true;
      }
   }

   protected void clearGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
   }

   protected void clearGlobalReadLock() throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.clearGlobalReadLockNoWait();
            return;
         } catch (LockException var2) {
            ManifoldCF.sleep(10L);
         }
      }
   }
}
