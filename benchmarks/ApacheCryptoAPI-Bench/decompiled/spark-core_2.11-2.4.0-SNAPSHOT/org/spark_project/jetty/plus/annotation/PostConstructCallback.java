package org.spark_project.jetty.plus.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PostConstructCallback extends LifeCycleCallback {
   public void validate(Class<?> clazz, Method method) {
      if (method.getExceptionTypes().length > 0) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not throw a checked exception");
      } else if (!method.getReturnType().equals(Void.TYPE)) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not have a return type");
      } else if (Modifier.isStatic(method.getModifiers())) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot be static");
      }
   }

   public void callback(Object instance) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
      super.callback(instance);
   }

   public boolean equals(Object o) {
      return super.equals(o) && o instanceof PostConstructCallback;
   }
}
