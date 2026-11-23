package org.apache.manifoldcf.core.lockmanager;

import java.util.ArrayList;
import java.util.List;
import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class LockGate {
   protected final List<Long> threadRequests = new ArrayList();
   protected final LockObject lockObject;
   protected final Object lockKey;
   protected LockPool lockPool;

   public LockGate(Object lockKey, LockObject lockObject, LockPool lockPool) {
      this.lockKey = lockKey;
      this.lockObject = lockObject;
      this.lockPool = lockPool;
   }

   public void makeInvalid() {
      synchronized(this) {
         this.lockPool = null;
         this.lockObject.makeInvalid();
      }
   }

   protected void waitForPermission(Long threadID) throws InterruptedException, ExpiredObjectException {
      synchronized(this) {
         this.threadRequests.add(threadID);
      }

      try {
         while(true) {
            synchronized(this) {
               if (this.lockPool == null) {
                  throw new ExpiredObjectException("Invalid");
               }

               if (((Long)this.threadRequests.get(0)).equals(threadID)) {
                  return;
               }

               this.wait();
            }
         }
      } catch (InterruptedException e) {
         this.freePermission(threadID);
         throw e;
      } catch (ExpiredObjectException e) {
         this.freePermission(threadID);
         throw e;
      } catch (Error e) {
         this.freePermission(threadID);
         throw e;
      } catch (RuntimeException e) {
         this.freePermission(threadID);
         throw e;
      }
   }

   protected void freePermission(Long threadID) {
      synchronized(this) {
         this.threadRequests.remove(threadID);
         this.notifyAll();
      }
   }

   public void enterWriteLock(Long threadID) throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterWriteLock();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void enterWriteLockNoWait(Long threadID) throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterWriteLockNoWait();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void leaveWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      synchronized(this) {
         if (this.lockObject.leaveWriteLock() && this.threadRequests.size() == 0 && this.lockPool != null) {
            this.lockPool.releaseObject(this.lockKey, this);
         }

      }
   }

   public void enterNonExWriteLock(Long threadID) throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterNonExWriteLock();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void enterNonExWriteLockNoWait(Long threadID) throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterNonExWriteLockNoWait();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void leaveNonExWriteLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      synchronized(this) {
         if (this.lockObject.leaveNonExWriteLock() && this.threadRequests.size() == 0 && this.lockPool != null) {
            this.lockPool.releaseObject(this.lockKey, this);
         }

      }
   }

   public void enterReadLock(Long threadID) throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterReadLock();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void enterReadLockNoWait(Long threadID) throws ManifoldCFException, LockException, LocalLockException, InterruptedException, ExpiredObjectException {
      this.waitForPermission(threadID);

      try {
         this.lockObject.enterReadLockNoWait();
      } finally {
         this.freePermission(threadID);
      }

   }

   public void leaveReadLock() throws ManifoldCFException, InterruptedException, ExpiredObjectException {
      synchronized(this) {
         if (this.lockObject.leaveReadLock() && this.threadRequests.size() == 0 && this.lockPool != null) {
            this.lockPool.releaseObject(this.lockKey, this);
         }

      }
   }
}
