package org.apache.wicket.util.lang;

public final class Classes {
   public static String name(Class<?> c) {
      String name = null;
      if (c != null) {
         while(c.isAnonymousClass()) {
            c = c.getSuperclass();
         }

         name = c.getName();
      }

      return name;
   }

   public static String simpleName(Class<?> c) {
      String simpleName = null;
      if (c != null) {
         while(c.isAnonymousClass()) {
            c = c.getSuperclass();
         }

         simpleName = c.getSimpleName();
      }

      return simpleName;
   }

   public static Class<?> relativeClass(Class<?> scope, String path) throws ClassNotFoundException {
      return Class.forName(Packages.absolutePath(scope, path).replace('/', '.'));
   }

   private Classes() {
   }
}
