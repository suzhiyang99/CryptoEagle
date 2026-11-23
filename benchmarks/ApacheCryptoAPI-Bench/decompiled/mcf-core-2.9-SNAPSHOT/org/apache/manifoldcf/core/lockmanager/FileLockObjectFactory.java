package org.apache.manifoldcf.core.lockmanager;

import java.io.File;

public class FileLockObjectFactory extends LockObjectFactory {
   public static final String _rcsid = "@(#)$Id$";
   protected final File synchDir;

   public FileLockObjectFactory(File synchDir) {
      this.synchDir = synchDir;
   }

   public LockObject newLockObject(LockPool lockPool, Object lockKey) {
      return new FileLockObject(lockPool, lockKey, this.synchDir);
   }
}
