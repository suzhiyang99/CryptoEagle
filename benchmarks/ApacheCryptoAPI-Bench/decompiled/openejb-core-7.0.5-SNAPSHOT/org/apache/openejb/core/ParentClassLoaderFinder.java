package org.apache.openejb.core;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;

public interface ParentClassLoaderFinder {
   ClassLoader FALLBACK = OpenEJB.class.getClassLoader();

   ClassLoader getParentClassLoader(ClassLoader var1);

   public static class Helper {
      public static ClassLoader get() {
         ParentClassLoaderFinder parentFinder = SystemInstance.isInitialized() ? (ParentClassLoaderFinder)SystemInstance.get().getComponent(ParentClassLoaderFinder.class) : null;
         return parentFinder != null ? parentFinder.getParentClassLoader(ParentClassLoaderFinder.FALLBACK) : ParentClassLoaderFinder.FALLBACK;
      }
   }
}
