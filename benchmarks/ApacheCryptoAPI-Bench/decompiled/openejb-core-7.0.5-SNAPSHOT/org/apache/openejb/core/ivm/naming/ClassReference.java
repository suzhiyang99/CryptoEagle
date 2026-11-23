package org.apache.openejb.core.ivm.naming;

public class ClassReference extends Reference {
   private final String className;

   public ClassReference(String className) {
      this.className = className;
   }

   public Object getObject() throws javax.naming.NamingException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      try {
         return classLoader.loadClass(this.className);
      } catch (ClassNotFoundException var3) {
         throw new javax.naming.NamingException("Erorr loading class: " + this.className);
      }
   }
}
