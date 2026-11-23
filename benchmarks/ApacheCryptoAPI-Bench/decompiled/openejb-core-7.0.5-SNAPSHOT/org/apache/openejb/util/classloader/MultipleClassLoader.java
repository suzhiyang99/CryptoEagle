package org.apache.openejb.util.classloader;

public class MultipleClassLoader extends ClassLoader implements ClassLoaderComparator {
   private final ClassLoader second;

   public MultipleClassLoader(ClassLoader first, ClassLoader second) {
      super(first);
      this.second = second;
   }

   public Class<?> loadClass(String name) throws ClassNotFoundException {
      try {
         return super.loadClass(name);
      } catch (ClassNotFoundException cnfe) {
         if (this.second != this.getParent()) {
            return this.loadClassSecond(name);
         } else {
            throw cnfe;
         }
      } catch (NoClassDefFoundError ncdfe) {
         if (this.second != this.getParent()) {
            return this.loadClassSecond(name);
         } else {
            throw ncdfe;
         }
      } catch (LinkageError le) {
         if (this.second != this.getParent()) {
            return this.loadClassSecond(name);
         } else {
            throw le;
         }
      }
   }

   public Class<?> loadClassSecond(String name) throws ClassNotFoundException {
      return this.second.loadClass(name);
   }

   public boolean equals(Object other) {
      return this == other || this.getParent().equals(other);
   }

   public int hashCode() {
      return this.getParent().hashCode();
   }

   public boolean isSame(ClassLoader cl) {
      return this.equals(cl);
   }
}
