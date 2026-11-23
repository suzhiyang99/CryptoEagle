package org.apache.manifoldcf.core.lockmanager;

import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ZooKeeperLockObject extends LockObject {
   public static final String _rcsid = "@(#)$Id$";
   private static final String LOCK_PATH_PREFIX = "/org.apache.manifoldcf.locks-";
   private final ZooKeeperConnectionPool pool;
   private final String lockPath;
   private ZooKeeperConnection currentConnection = null;

   public ZooKeeperLockObject(LockPool lockPool, Object lockKey, ZooKeeperConnectionPool pool) {
      super(lockPool, lockKey);
      this.pool = pool;
      this.lockPath = "/org.apache.manifoldcf.locks-" + ZooKeeperConnection.zooKeeperSafeName(lockKey.toString());
   }

   protected void obtainGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before write locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            succeeded = this.currentConnection.obtainWriteLockNoWait(this.lockPath);
            if (!succeeded) {
               throw new LockException("Locked by another JVM");
            }
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void obtainGlobalWriteLock() throws ManifoldCFException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before write locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            this.currentConnection.obtainWriteLock(this.lockPath);
            succeeded = true;
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void clearGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection == null) {
         throw new IllegalStateException("Cannot clear write lock we don't have: " + this.lockPath);
      } else {
         this.clearLock();
      }
   }

   protected void obtainGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before non-ex-write locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            succeeded = this.currentConnection.obtainNonExWriteLockNoWait(this.lockPath);
            if (!succeeded) {
               throw new LockException("Locked by another JVM");
            }
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void obtainGlobalNonExWriteLock() throws ManifoldCFException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before non-ex-write locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            this.currentConnection.obtainNonExWriteLock(this.lockPath);
            succeeded = true;
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void clearGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection == null) {
         throw new IllegalStateException("Cannot clear non-ex-write lock we don't have: " + this.lockPath);
      } else {
         this.clearLock();
      }
   }

   protected void obtainGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before read locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            succeeded = this.currentConnection.obtainReadLockNoWait(this.lockPath);
            if (!succeeded) {
               throw new LockException("Locked by another JVM");
            }
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void obtainGlobalReadLock() throws ManifoldCFException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a connection before read locking: " + this.lockPath);
      } else {
         boolean succeeded = false;
         this.currentConnection = this.pool.grab();

         try {
            this.currentConnection.obtainReadLock(this.lockPath);
            succeeded = true;
         } finally {
            if (!succeeded) {
               this.pool.release(this.currentConnection);
               this.currentConnection = null;
            }

         }

      }
   }

   protected void clearGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.currentConnection == null) {
         throw new IllegalStateException("Cannot clear read lock we don't have: " + this.lockPath);
      } else {
         this.clearLock();
      }
   }

   protected void clearLock() throws ManifoldCFException, InterruptedException {
      this.currentConnection.releaseLock();
      this.pool.release(this.currentConnection);
      this.currentConnection = null;
   }
}
