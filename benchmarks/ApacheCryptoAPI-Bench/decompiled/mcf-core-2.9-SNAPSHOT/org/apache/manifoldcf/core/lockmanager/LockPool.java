package org.apache.manifoldcf.core.lockmanager;

import java.util.HashMap;
import java.util.Map;

public class LockPool {
   public static final String _rcsid = "@(#)$Id: LockPool.java 988245 2010-08-23 18:39:35Z kwright $";
   protected final Map<Object, LockGate> myLocks = new HashMap();
   protected final LockObjectFactory factory;

   public LockPool(LockObjectFactory factory) {
      this.factory = factory;
   }

   public synchronized LockGate getObject(Object lockKey) {
      LockGate lg = (LockGate)this.myLocks.get(lockKey);
      if (lg == null) {
         LockObject lo = this.factory.newLockObject(this, lockKey);
         lg = new LockGate(lockKey, lo, this);
         this.myLocks.put(lockKey, lg);
      }

      return lg;
   }

   public synchronized void releaseObject(Object lockKey, LockGate lockGate) {
      lockGate.makeInvalid();
      this.myLocks.remove(lockKey);
   }
}
