package org.apache.activemq.artemis.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FactoryFinder {
   private static ObjectFactory objectFactory = new StandaloneObjectFactory();
   private final String path;

   public static ObjectFactory getObjectFactory() {
      return objectFactory;
   }

   public static void setObjectFactory(ObjectFactory objectFactory) {
      FactoryFinder.objectFactory = objectFactory;
   }

   public FactoryFinder(String path) {
      this.path = path;
   }

   public Object newInstance(String key) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
      return objectFactory.create(this.path + key);
   }

   protected static class StandaloneObjectFactory implements ObjectFactory {
      final ConcurrentMap<String, Class> classMap = new ConcurrentHashMap();

      public Object create(String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
         Class clazz = (Class)this.classMap.get(path);
         if (clazz == null) {
            clazz = loadClass(this.loadProperties(path));
            this.classMap.put(path, clazz);
         }

         return clazz.newInstance();
      }

      static Class loadClass(Properties properties) throws ClassNotFoundException, IOException {
         String className = properties.getProperty("class");
         if (className == null) {
            throw new IOException("Expected property is missing: class");
         } else {
            Class clazz = null;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
               try {
                  clazz = loader.loadClass(className);
               } catch (ClassNotFoundException var5) {
               }
            }

            if (clazz == null) {
               clazz = FactoryFinder.class.getClassLoader().loadClass(className);
            }

            return clazz;
         }
      }

      public Properties loadProperties(String uri) throws IOException {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
            classLoader = StandaloneObjectFactory.class.getClassLoader();
         }

         InputStream in = classLoader.getResourceAsStream(uri);
         if (in == null) {
            in = FactoryFinder.class.getClassLoader().getResourceAsStream(uri);
            if (in == null) {
               throw new IOException("Could not find factory class for resource: " + uri);
            }
         }

         BufferedInputStream reader = null;

         Properties var6;
         try {
            reader = new BufferedInputStream(in);
            Properties properties = new Properties();
            properties.load(reader);
            var6 = properties;
         } finally {
            try {
               reader.close();
            } catch (Exception var13) {
            }

         }

         return var6;
      }
   }

   public interface ObjectFactory {
      Object create(String var1) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException;
   }
}
