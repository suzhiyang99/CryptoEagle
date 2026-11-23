package org.apache.manifoldcf.core.lockmanager;

public class ZooKeeperLockObjectFactory extends LockObjectFactory {
   public static final String _rcsid = "@(#)$Id$";
   protected final ZooKeeperConnectionPool pool;

   public ZooKeeperLockObjectFactory(ZooKeeperConnectionPool pool) {
      this.pool = pool;
   }

   public LockObject newLockObject(LockPool lockPool, Object lockKey) {
      return new ZooKeeperLockObject(lockPool, lockKey, this.pool);
   }
}
