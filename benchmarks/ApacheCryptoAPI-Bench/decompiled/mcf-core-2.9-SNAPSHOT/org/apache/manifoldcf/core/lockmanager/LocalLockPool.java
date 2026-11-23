package org.apache.manifoldcf.core.lockmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LocalLockPool {
   public static final String _rcsid = "@(#)$Id$";
   protected final Map<String, LocalLock> localLocks = new HashMap();

   public LocalLock getLocalLock(String lockKey) {
      LocalLock ll = (LocalLock)this.localLocks.get(lockKey);
      if (ll == null) {
         ll = new LocalLock();
         this.localLocks.put(lockKey, ll);
      }

      return ll;
   }

   public void releaseLocalLock(String lockKey) {
      this.localLocks.remove(lockKey);
   }

   public Set<String> keySet() {
      return this.localLocks.keySet();
   }
}
