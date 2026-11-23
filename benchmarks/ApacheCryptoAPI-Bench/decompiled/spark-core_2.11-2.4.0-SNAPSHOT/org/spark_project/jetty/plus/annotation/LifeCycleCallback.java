package org.spark_project.jetty.plus.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.spark_project.jetty.util.IntrospectionUtil;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.TypeUtil;

public abstract class LifeCycleCallback {
   public static final Object[] __EMPTY_ARGS = new Object[0];
   private Method _target;
   private Class<?> _targetClass;
   private String _className;
   private String _methodName;

   public Class<?> getTargetClass() {
      return this._targetClass;
   }

   public String getTargetClassName() {
      return this._className;
   }

   public String getMethodName() {
      return this._methodName;
   }

   public Method getTarget() {
      return this._target;
   }

   public void setTarget(String className, String methodName) {
      this._className = className;
      this._methodName = methodName;
   }

   public void setTarget(Class<?> clazz, String methodName) {
      try {
         Method method = IntrospectionUtil.findMethod(clazz, methodName, (Class[])null, true, true);
         this.validate(clazz, method);
         this._target = method;
         this._targetClass = clazz;
         this._className = clazz.getCanonicalName();
         this._methodName = methodName;
      } catch (NoSuchMethodException var4) {
         throw new IllegalArgumentException("Method " + methodName + " not found on class " + clazz.getName());
      }
   }

   public void callback(Object instance) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if (this._target == null) {
         if (this._targetClass == null) {
            this._targetClass = Loader.loadClass((Class)null, this._className);
         }

         this._target = this._targetClass.getDeclaredMethod(this._methodName, TypeUtil.NO_ARGS);
      }

      if (this._target != null) {
         boolean accessibility = this.getTarget().isAccessible();
         this.getTarget().setAccessible(true);
         this.getTarget().invoke(instance, __EMPTY_ARGS);
         this.getTarget().setAccessible(accessibility);
      }

   }

   public Method findMethod(Package pack, Class<?> clazz, String methodName, boolean checkInheritance) {
      if (clazz == null) {
         return null;
      } else {
         try {
            Method method = clazz.getDeclaredMethod(methodName);
            if (!checkInheritance) {
               return method;
            } else {
               int modifiers = method.getModifiers();
               return !Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers) && (Modifier.isPrivate(modifiers) || !pack.equals(clazz.getPackage())) ? this.findMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, true) : method;
            }
         } catch (NoSuchMethodException var7) {
            return this.findMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, true);
         }
      }
   }

   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof LifeCycleCallback)) {
         return false;
      } else {
         LifeCycleCallback callback = (LifeCycleCallback)o;
         if (callback.getTargetClass() == null) {
            if (this.getTargetClass() != null) {
               return false;
            }
         } else if (!callback.getTargetClass().equals(this.getTargetClass())) {
            return false;
         }

         if (callback.getTarget() == null) {
            if (this.getTarget() != null) {
               return false;
            }
         } else if (!callback.getTarget().equals(this.getTarget())) {
            return false;
         }

         return true;
      }
   }

   public abstract void validate(Class<?> var1, Method var2);
}
