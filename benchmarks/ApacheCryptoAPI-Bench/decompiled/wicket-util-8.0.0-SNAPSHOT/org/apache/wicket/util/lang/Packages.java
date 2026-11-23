package org.apache.wicket.util.lang;

import org.apache.wicket.util.string.StringList;

public final class Packages {
   public static String absolutePath(Class<?> p, String path) {
      String packName = p != null ? extractPackageName(p) : "";
      return absolutePath(packName, path);
   }

   public static String absolutePath(Package p, String relativePath) {
      return absolutePath(p.getName(), relativePath);
   }

   public static String absolutePath(String packageName, String path) {
      if (path.startsWith("/")) {
         return path.substring(1);
      } else {
         StringList absolutePath = StringList.tokenize(packageName, ".");
         StringList folders = StringList.tokenize(path, "/\\");
         int i = 0;

         for(int size = folders.size(); i < size; ++i) {
            String folder = folders.get(i);
            if ("..".equals(folder)) {
               if (absolutePath.size() <= 0) {
                  throw new IllegalArgumentException("Invalid path " + path);
               }

               absolutePath.removeLast();
            } else {
               absolutePath.add(folder);
            }
         }

         return absolutePath.join("/");
      }
   }

   public static String extractPackageName(Class<?> forClass) {
      return parent(forClass.getName());
   }

   public static String parent(String packageName) {
      int pos = packageName.lastIndexOf(".");
      if (pos < 0) {
         pos = packageName.lastIndexOf("/");
         if (pos < 0) {
            pos = 0;
         }
      }

      String parent = packageName.substring(0, pos);
      return parent;
   }

   public static String resolveScope(Class<?> forClass) {
      String packName = extractPackageName(forClass);
      return packName.replace('.', '/');
   }

   private Packages() {
   }
}
