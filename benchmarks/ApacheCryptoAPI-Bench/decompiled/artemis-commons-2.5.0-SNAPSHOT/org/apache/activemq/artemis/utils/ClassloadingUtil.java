package org.apache.activemq.artemis.utils;

import java.net.URL;

public final class ClassloadingUtil {
   private static final String INSTANTIATION_EXCEPTION_MESSAGE = "Your class must have a constructor without arguments. If it is an inner class, it must be static!";

   public static Object newInstanceFromClassLoader(String className) {
      ClassLoader loader = ClassloadingUtil.class.getClassLoader();

      try {
         Class<?> clazz = loader.loadClass(className);
         return clazz.newInstance();
      } catch (Throwable t) {
         if (t instanceof InstantiationException) {
            System.out.println("Your class must have a constructor without arguments. If it is an inner class, it must be static!");
         }

         loader = Thread.currentThread().getContextClassLoader();
         if (loader == null) {
            throw new RuntimeException("No local context classloader", t);
         } else {
            try {
               return loader.loadClass(className).newInstance();
            } catch (InstantiationException e) {
               throw new RuntimeException("Your class must have a constructor without arguments. If it is an inner class, it must be static! " + className, e);
            } catch (ClassNotFoundException e) {
               throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public static Object newInstanceFromClassLoader(String className, Object... objs) {
      ClassLoader loader = ClassloadingUtil.class.getClassLoader();

      try {
         Class<?>[] parametersType = new Class[objs.length];

         for(int i = 0; i < objs.length; ++i) {
            parametersType[i] = objs[i].getClass();
         }

         Class<?> clazz = loader.loadClass(className);
         return clazz.getConstructor(parametersType).newInstance(objs);
      } catch (Throwable t) {
         if (t instanceof InstantiationException) {
            System.out.println("Your class must have a constructor without arguments. If it is an inner class, it must be static!");
         }

         loader = Thread.currentThread().getContextClassLoader();
         if (loader == null) {
            throw new RuntimeException("No local context classloader", t);
         } else {
            try {
               return loader.loadClass(className).newInstance();
            } catch (InstantiationException e) {
               throw new RuntimeException("Your class must have a constructor without arguments. If it is an inner class, it must be static! " + className, e);
            } catch (ClassNotFoundException e) {
               throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public static URL findResource(String resourceName) {
      ClassLoader loader = ClassloadingUtil.class.getClassLoader();

      try {
         URL resource = loader.getResource(resourceName);
         if (resource != null) {
            return resource;
         }
      } catch (Throwable var3) {
      }

      loader = Thread.currentThread().getContextClassLoader();
      return loader == null ? null : loader.getResource(resourceName);
   }
}
