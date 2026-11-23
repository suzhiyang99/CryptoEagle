package org.apache.manifoldcf.core.interfaces;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class IDFactory {
   public static final String _rcsid = "@(#)$Id: IDFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static ArrayList idPool = new ArrayList();
   private static final int poolSize = 100;
   private static final String criticalSectionName = "_IDFACTORY_";
   private static final String globalLockName = "_IDFACTORY_";
   private static final String globalIDDataName = "_IDFACTORY_";

   private IDFactory() {
   }

   public static String make(IThreadContext tc) throws ManifoldCFException {
      ILockManager lockManager = LockManagerFactory.make(tc);
      lockManager.enterWriteCriticalSection("_IDFACTORY_");

      String var16;
      try {
         if (idPool.size() == 0) {
            lockManager.enterWriteLock("_IDFACTORY_");

            try {
               byte[] idData = lockManager.readData("_IDFACTORY_");
               long _id;
               if (idData == null) {
                  _id = 0L;
               } else {
                  _id = new Long(new String(idData, StandardCharsets.UTF_8));
               }

               for(int i = 0; i < 100; ++i) {
                  long newid = System.currentTimeMillis();
                  if (newid <= _id) {
                     newid = _id + 1L;
                  }

                  _id = newid;
                  idPool.add(Long.toString(newid));
               }

               lockManager.writeData("_IDFACTORY_", Long.toString(_id).getBytes(StandardCharsets.UTF_8));
            } finally {
               lockManager.leaveWriteLock("_IDFACTORY_");
            }
         }

         var16 = (String)idPool.remove(idPool.size() - 1);
      } finally {
         lockManager.leaveWriteCriticalSection("_IDFACTORY_");
      }

      return var16;
   }
}
