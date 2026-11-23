package org.spark_project.jetty.plus.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class PreDestroyCallback extends LifeCycleCallback {
   private static final Logger LOG = Log.getLogger(PreDestroyCallback.class);

   public void validate(Class<?> clazz, Method method) {
      if (method.getExceptionTypes().length > 0) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not throw a checked exception");
      } else if (!method.getReturnType().equals(Void.TYPE)) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not have a return type");
      } else if (Modifier.isStatic(method.getModifiers())) {
         throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot be static");
      }
   }

   public void callback(Object instance) {
      try {
         super.callback(instance);
      } catch (Exception e) {
         LOG.warn("Ignoring exception thrown on preDestroy call to " + this.getTargetClass() + "." + this.getTarget().getName(), (Throwable)e);
      }

   }

   public boolean equals(Object o) {
      return super.equals(o) && o instanceof PreDestroyCallback;
   }
}
