package org.apache.openejb.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import org.apache.openejb.OpenEJBException;

public final class SafeToolkit {
   public static final HashMap codebases = new HashMap();
   private final String systemLocation;

   public static SafeToolkit getToolkit(String systemLocation) {
      return new SafeToolkit(systemLocation);
   }

   private SafeToolkit(String systemLocation) {
      this.systemLocation = systemLocation;
   }

   private Class forName(String className) throws OpenEJBException {
      try {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         return loader.loadClass(className);
      } catch (ClassNotFoundException var3) {
         OpenEJBErrorHandler.classNotFound(this.systemLocation, className);
         return null;
      }
   }

   public Object newInstance(String className) throws OpenEJBException {
      return this.newInstance(this.forName(className));
   }

   public Object newInstance(Class clazz) throws OpenEJBException {
      Object instance = null;

      try {
         instance = clazz.newInstance();
      } catch (InstantiationException var5) {
         OpenEJBErrorHandler.classNotIntantiateable(this.systemLocation, clazz.getName());
      } catch (IllegalAccessException var6) {
         OpenEJBErrorHandler.classNotAccessible(this.systemLocation, clazz.getName());
      } catch (Throwable exception) {
         exception.printStackTrace();
         ClassLoader classLoader = clazz.getClassLoader();
         if (classLoader instanceof URLClassLoader) {
            OpenEJBErrorHandler.classNotIntantiateableFromCodebaseForUnknownReason(this.systemLocation, clazz.getName(), getCodebase((URLClassLoader)classLoader), exception.getClass().getName(), exception.getMessage());
         } else {
            OpenEJBErrorHandler.classNotIntantiateableForUnknownReason(this.systemLocation, clazz.getName(), exception.getClass().getName(), exception.getMessage());
         }
      }

      return instance;
   }

   private static String getCodebase(URLClassLoader urlClassLoader) {
      StringBuilder codebase = new StringBuilder();
      URL[] urlList = urlClassLoader.getURLs();
      codebase.append(urlList[0].toString());

      for(int i = 1; i < urlList.length; ++i) {
         codebase.append(';');
         codebase.append(urlList[i].toString());
      }

      return codebase.toString();
   }
}
