package org.apache.openejb.util;

import java.lang.annotation.Annotation;

public final class AnnotationUtil {
   private AnnotationUtil() {
   }

   public static <T extends Annotation> T getAnnotation(Class<T> a, Class<?> clazz) {
      Class<?> current = clazz;

      do {
         T annotation = current.getAnnotation(a);
         if (annotation != null) {
            return annotation;
         }

         current = current.getSuperclass();
      } while(current != null);

      return null;
   }
}
