package org.apache.tika.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.tika.config.ServiceLoader;

public class ServiceLoaderUtils {
   public static <T> void sortLoadedClasses(List<T> loaded) {
      Collections.sort(loaded, new Comparator<T>() {
         public int compare(T c1, T c2) {
            String n1 = c1.getClass().getName();
            String n2 = c2.getClass().getName();
            boolean t1 = n1.startsWith("org.apache.tika.");
            boolean t2 = n2.startsWith("org.apache.tika.");
            if (t1 == t2) {
               return n1.compareTo(n2);
            } else {
               return t1 ? -1 : 1;
            }
         }
      });
   }

   public static <T> T newInstance(String className) {
      return (T)newInstance(className, ServiceLoader.class.getClassLoader());
   }

   public static <T> T newInstance(String className, ClassLoader loader) {
      try {
         Class loadedClass = Class.forName(className, true, loader);
         T instance = (T)loadedClass.newInstance();
         return instance;
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}
