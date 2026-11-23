package org.apache.openejb.core;

public class ProvidedClassLoaderFinder implements ParentClassLoaderFinder {
   private final ClassLoader loader;

   public ProvidedClassLoaderFinder(ClassLoader tccl) {
      this.loader = tccl;
   }

   public ClassLoader getParentClassLoader(ClassLoader fallback) {
      return this.loader;
   }
}
