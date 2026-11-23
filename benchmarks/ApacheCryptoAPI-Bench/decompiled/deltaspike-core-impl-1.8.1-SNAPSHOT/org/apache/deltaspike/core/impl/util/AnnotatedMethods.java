package org.apache.deltaspike.core.impl.util;

import java.lang.reflect.Method;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public final class AnnotatedMethods {
   private AnnotatedMethods() {
   }

   public static AnnotatedMethod<?> findMethod(AnnotatedType<?> type, Method method) {
      AnnotatedMethod<?> annotatedMethod = null;

      for(AnnotatedMethod<?> am : type.getMethods()) {
         if (am.getJavaMember().equals(method)) {
            annotatedMethod = am;
            break;
         }
      }

      if (annotatedMethod == null) {
         throw new IllegalStateException("No annotated method for " + method);
      } else {
         return annotatedMethod;
      }
   }
}
