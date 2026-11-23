package org.apache.openejb.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class NameFiltering {
   private static final List<String> INVALID = new ArrayList();

   private NameFiltering() {
   }

   public static File filter(File location) {
      String lastName = location.getName();
      if ("test-classes".equals(lastName) || "classes".equals(lastName)) {
         File parentFile = location.getParentFile();
         if (parentFile != null && "target".equals(parentFile.getName())) {
            File grandParentFile = parentFile.getParentFile();
            if (grandParentFile != null) {
               return grandParentFile;
            }
         }
      }

      if ("test".equals(lastName) || "main".equals(lastName)) {
         File parentFile = location.getParentFile();
         if (parentFile != null && "classes".equals(parentFile.getName())) {
            File grandParentFile = parentFile.getParentFile();
            if (grandParentFile != null && "build".equals(grandParentFile.getName())) {
               File grandGrandParentFile = grandParentFile.getParentFile();
               if (grandGrandParentFile != null) {
                  return grandGrandParentFile;
               }
            }
         }
      }

      File current;
      for(current = location; current != null && INVALID.contains(current.getName()); current = current.getParentFile()) {
      }

      return current == null ? location : current;
   }

   static {
      INVALID.add("classes");
      INVALID.add("test-classes");
      INVALID.add("target");
      INVALID.add("build");
      INVALID.add("dist");
      INVALID.add("bin");
   }
}
