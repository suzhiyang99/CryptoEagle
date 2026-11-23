package org.apache.openejb;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.finder.ResourceFinder;

public final class Extensions {
   private static final Logger LOGGER;

   private Extensions() {
   }

   public static Collection<Class<?>> findExtensions(Finder finder) {
      try {
         return finder.findAvailableClasses("org.apache.openejb.extension");
      } catch (IOException e) {
         LOGGER.error("Extension scanning of 'META-INF/org.apache.openejb.extension' files failed", (Throwable)e);
         return Collections.emptySet();
      }
   }

   public static void installExtensions(Finder finder) {
      try {
         List<Class<?>> classes = finder.findAvailableClasses("org.apache.openejb.extension");
         addExtensions(classes);
      } catch (IOException e) {
         LOGGER.error("Extension scanning of 'META-INF/org.apache.openejb.extension' files failed", (Throwable)e);
      }

   }

   public static void addExtensions(ClassLoader loader, Collection<String> classes) {
      for(String clazz : classes) {
         try {
            Object object = loader.loadClass(clazz).getConstructor().newInstance();
            SystemInstance.get().addObserver(object);
         } catch (Throwable t) {
            LOGGER.error("Extension construction failed" + clazz, t);
         }
      }

   }

   public static void addExtensions(Collection<Class<?>> classes) {
      for(Class<?> clazz : classes) {
         try {
            Object object = clazz.getConstructor().newInstance();
            SystemInstance.get().addObserver(object);
         } catch (Throwable t) {
            LOGGER.error("Extension construction failed" + clazz.getName(), t);
         }
      }

   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, Extensions.class);
   }

   public static class Finder extends ResourceFinder {
      public Finder(String path, boolean useLoader, URL... urls) {
         super(path, !useLoader && urls == null ? new URL[0] : urls);
      }

      public List<String> findAvailableStrings(String uri) throws IOException {
         List<String> strings = super.findAvailableStrings(uri);
         if (!strings.isEmpty()) {
            List<String> copy = new ArrayList(strings);
            strings.clear();

            for(String s : copy) {
               strings.addAll(Arrays.asList(s.replace("\r", "").split("\n")));
            }
         }

         return strings;
      }
   }
}
