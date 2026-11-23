package org.spark_project.jetty.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.spark_project.jetty.util.resource.Resource;

public class Loader {
   public static URL getResource(Class<?> loadClass, String name) {
      URL url = null;
      ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
      if (context_loader != null) {
         url = context_loader.getResource(name);
      }

      if (url == null && loadClass != null) {
         ClassLoader load_loader = loadClass.getClassLoader();
         if (load_loader != null && load_loader != context_loader) {
            url = load_loader.getResource(name);
         }
      }

      if (url == null) {
         url = ClassLoader.getSystemResource(name);
      }

      return url;
   }

   public static Class loadClass(Class loadClass, String name) throws ClassNotFoundException {
      ClassNotFoundException ex = null;
      Class<?> c = null;
      ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
      if (context_loader != null) {
         try {
            c = context_loader.loadClass(name);
         } catch (ClassNotFoundException e) {
            ex = e;
         }
      }

      if (c == null && loadClass != null) {
         ClassLoader load_loader = loadClass.getClassLoader();
         if (load_loader != null && load_loader != context_loader) {
            try {
               c = load_loader.loadClass(name);
            } catch (ClassNotFoundException e) {
               if (ex == null) {
                  ex = e;
               }
            }
         }
      }

      if (c == null) {
         try {
            c = Class.forName(name);
         } catch (ClassNotFoundException e) {
            if (ex != null) {
               throw ex;
            }

            throw e;
         }
      }

      return c;
   }

   public static ResourceBundle getResourceBundle(Class<?> loadClass, String name, boolean checkParents, Locale locale) throws MissingResourceException {
      MissingResourceException ex = null;
      ResourceBundle bundle = null;

      for(ClassLoader loader = Thread.currentThread().getContextClassLoader(); bundle == null && loader != null; loader = bundle == null && checkParents ? loader.getParent() : null) {
         try {
            bundle = ResourceBundle.getBundle(name, locale, loader);
         } catch (MissingResourceException e) {
            if (ex == null) {
               ex = e;
            }
         }
      }

      for(ClassLoader var11 = loadClass == null ? null : loadClass.getClassLoader(); bundle == null && var11 != null; var11 = bundle == null && checkParents ? var11.getParent() : null) {
         try {
            bundle = ResourceBundle.getBundle(name, locale, var11);
         } catch (MissingResourceException e) {
            if (ex == null) {
               ex = e;
            }
         }
      }

      if (bundle == null) {
         try {
            bundle = ResourceBundle.getBundle(name, locale);
         } catch (MissingResourceException e) {
            if (ex == null) {
               ex = e;
            }
         }
      }

      if (bundle != null) {
         return bundle;
      } else {
         throw ex;
      }
   }

   public static String getClassPath(ClassLoader loader) throws Exception {
      StringBuilder classpath;
      for(classpath = new StringBuilder(); loader != null && loader instanceof URLClassLoader; loader = loader.getParent()) {
         URL[] urls = ((URLClassLoader)loader).getURLs();
         if (urls != null) {
            for(int i = 0; i < urls.length; ++i) {
               Resource resource = Resource.newResource(urls[i]);
               File file = resource.getFile();
               if (file != null && file.exists()) {
                  if (classpath.length() > 0) {
                     classpath.append(File.pathSeparatorChar);
                  }

                  classpath.append(file.getAbsolutePath());
               }
            }
         }
      }

      return classpath.toString();
   }
}
