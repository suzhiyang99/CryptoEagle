package org.apache.manifoldcf.core.lockmanager;

public class LockObjectFactory {
   public static final String _rcsid = "@(#)$Id$";

   public LockObject newLockObject(LockPool lockPool, Object lockKey) {
      return new LockObject(lockPool, lockKey);
   }
}
