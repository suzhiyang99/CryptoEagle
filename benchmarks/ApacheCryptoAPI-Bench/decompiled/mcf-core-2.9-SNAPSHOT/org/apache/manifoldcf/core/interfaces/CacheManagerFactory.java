package org.apache.manifoldcf.core.interfaces;

import org.apache.manifoldcf.core.cachemanager.CacheManager;

public class CacheManagerFactory {
   public static final String _rcsid = "@(#)$Id: CacheManagerFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final String cacheManager = "_CacheManager_";

   private CacheManagerFactory() {
   }

   public static ICacheManager make(IThreadContext context) throws ManifoldCFException {
      Object o = context.get("_CacheManager_");
      if (o == null || !(o instanceof ICacheManager)) {
         o = new CacheManager(context);
         context.save("_CacheManager_", o);
      }

      return (ICacheManager)o;
   }
}
