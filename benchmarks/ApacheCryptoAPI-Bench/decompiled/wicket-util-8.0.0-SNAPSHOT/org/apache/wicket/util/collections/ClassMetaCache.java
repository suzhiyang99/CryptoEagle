package org.apache.wicket.util.collections;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMetaCache<T> {
   private volatile Map<ClassLoader, ConcurrentHashMap<String, T>> cache = Collections.emptyMap();

   public T put(Class<?> key, T value) {
      ConcurrentHashMap<String, T> container = this.getClassLoaderCache(key.getClassLoader(), true);
      return (T)container.put(key(key), value);
   }

   public T get(Class<?> key) {
      ConcurrentHashMap<String, T> container = this.getClassLoaderCache(key.getClassLoader(), false);
      return (T)(container == null ? null : container.get(key(key)));
   }

   private ConcurrentHashMap<String, T> getClassLoaderCache(ClassLoader classLoader, boolean create) {
      ConcurrentHashMap<String, T> container = (ConcurrentHashMap)this.cache.get(classLoader);
      if (container == null) {
         if (!create) {
            return container;
         }

         synchronized(this) {
            container = (ConcurrentHashMap)this.cache.get(classLoader);
            if (container == null) {
               container = new ConcurrentHashMap();
               Map<ClassLoader, ConcurrentHashMap<String, T>> newCache = new WeakHashMap(this.cache);
               newCache.put(classLoader, container);
               this.cache = Collections.unmodifiableMap(newCache);
            }
         }
      }

      return container;
   }

   private static String key(Class<?> clazz) {
      return clazz.getName();
   }
}
