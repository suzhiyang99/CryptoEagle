package org.apache.openejb.util.classloader;

public class DefaultClassLoaderComparator implements ClassLoaderComparator {
   private final ClassLoader reference;

   public DefaultClassLoaderComparator(ClassLoader reference) {
      this.reference = reference;
   }

   public boolean isSame(ClassLoader cl) {
      return this.reference.equals(cl);
   }
}
