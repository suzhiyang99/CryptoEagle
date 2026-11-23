package org.apache.openejb;

import java.beans.Introspector;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import org.apache.openejb.classloader.ClassLoaderConfigurer;
import org.apache.openejb.classloader.CompositeClassLoaderConfigurer;
import org.apache.openejb.config.QuickJarsTxtParser;
import org.apache.openejb.core.TempClassLoader;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.UrlCache;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.apache.xbean.recipe.ObjectRecipe;

public class ClassLoaderUtil {
   private static final Logger logger;
   private static final Map<String, List<ClassLoader>> classLoadersByApp;
   private static final Map<ClassLoader, Set<String>> appsByClassLoader;
   private static final UrlCache localUrlCache;
   private static final AtomicBoolean skipClearSunJarFile;

   public static void destroyClassLoader(String appId, String appPath) {
      destroyClassLoader(appId);
      destroyClassLoader(appPath);
   }

   public static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   public static File getUrlCachedName(String appId, URL url) {
      return localUrlCache.getUrlCachedName(appId, url);
   }

   public static boolean isUrlCached(String appId, URL url) {
      return localUrlCache.isUrlCached(appId, url);
   }

   public static URL getUrlKeyCached(String appId, File file) {
      return localUrlCache.getUrlKeyCached(appId, file);
   }

   public static URLClassLoader createClassLoaderFirst(String appId, URL[] urls, ClassLoader parent) {
      return cacheClassLoader(appId, new URLClassLoaderFirst(localUrlCache.cacheUrls(appId, urls), parent));
   }

   public static URLClassLoader createClassLoader(String appId, URL[] urls, ClassLoader parent) {
      return cacheClassLoader(appId, new URLClassLoader(localUrlCache.cacheUrls(appId, urls), parent));
   }

   private static URLClassLoader cacheClassLoader(String appId, URLClassLoader classLoader) {
      List<ClassLoader> classLoaders = (List)classLoadersByApp.get(appId);
      if (classLoaders == null) {
         classLoaders = new ArrayList(2);
         classLoadersByApp.put(appId, classLoaders);
      }

      classLoaders.add(classLoader);
      Set<String> apps = (Set)appsByClassLoader.get(classLoader);
      if (apps == null) {
         apps = new LinkedHashSet(1);
         appsByClassLoader.put(classLoader, apps);
      }

      apps.add(appId);
      return classLoader;
   }

   public static void destroyClassLoader(ClassLoader classLoader) {
      Set<String> apps = (Set)appsByClassLoader.remove(classLoader);
      logger.debug("Destroying classLoader '" + toString(classLoader) + "' for apps: " + apps);
      if (apps != null) {
         for(String appId : apps) {
            List<ClassLoader> classLoaders = (List)classLoadersByApp.get(appId);
            if (classLoaders != null) {
               classLoaders.remove(classLoader);
            }

            if (null == classLoaders || classLoaders.isEmpty()) {
               destroyClassLoader(appId);
            }
         }
      }

      cleanOpenJPACache(classLoader);

      for(String jar : getClosedJarFiles(classLoader)) {
         clearSunJarFileFactoryCache(jar);
      }

      if (Closeable.class.isInstance(classLoader)) {
         try {
            ((Closeable)Closeable.class.cast(classLoader)).close();
         } catch (IOException var5) {
         }
      }

   }

   private static List<String> getClosedJarFiles(ClassLoader cl) {
      List<String> files = new ArrayList();
      if (null != cl && cl instanceof URLClassLoader) {
         URLClassLoader ucl = (URLClassLoader)cl;
         Class clazz = URLClassLoader.class;

         try {
            Field ucp = clazz.getDeclaredField("ucp");
            ucp.setAccessible(true);
            Object cp = ucp.get(ucl);
            Field loaders = cp.getClass().getDeclaredField("loaders");
            loaders.setAccessible(true);
            Collection c = (Collection)loaders.get(cp);

            for(Object jl : c.toArray()) {
               try {
                  Field loader = jl.getClass().getDeclaredField("jar");
                  loader.setAccessible(true);
                  JarFile jf = (JarFile)loader.get(jl);
                  files.add(jf.getName());
                  jf.close();
               } catch (Throwable var15) {
               }
            }
         } catch (Throwable var16) {
         }
      }

      return files;
   }

   public boolean finalizeNativeLibs(ClassLoader cl) {
      Class classClassLoader = ClassLoader.class;
      Field nativeLibraries = null;

      try {
         nativeLibraries = classClassLoader.getDeclaredField("nativeLibraries");
      } catch (NoSuchFieldException var13) {
      }

      if (nativeLibraries == null) {
         return false;
      } else {
         nativeLibraries.setAccessible(true);
         Object obj = null;

         try {
            obj = nativeLibraries.get(cl);
         } catch (IllegalAccessException var12) {
         }

         if (!(obj instanceof Vector)) {
            return false;
         } else {
            for(Object lib : (Vector)obj) {
               try {
                  Method finalize = lib.getClass().getDeclaredMethod("finalize");
                  if (finalize != null) {
                     finalize.setAccessible(true);

                     try {
                        finalize.invoke(lib);
                     } catch (Throwable var10) {
                     }
                  }
               } catch (Throwable var11) {
               }
            }

            return true;
         }
      }
   }

   public static void destroyClassLoader(String appId) {
      logger.debug("Destroying classLoaders for application " + appId);
      List<ClassLoader> classLoaders = (List)classLoadersByApp.remove(appId);
      if (classLoaders != null) {
         Iterator<ClassLoader> it = classLoaders.iterator();

         while(it.hasNext()) {
            ClassLoader cl = (ClassLoader)it.next();
            Set<String> apps = (Set)appsByClassLoader.get(cl);
            if (null != apps) {
               apps.remove(appId);
            }

            if (null != apps && !apps.isEmpty()) {
               logger.debug("ClassLoader " + toString(cl) + " held open by the applications: " + apps);
            } else {
               it.remove();
               appsByClassLoader.remove(cl);
               destroyClassLoader(cl);
               System.gc();
            }
         }
      }

      localUrlCache.releaseUrls(appId);
      clearSunJarFileFactoryCache(appId);
   }

   public static URLClassLoader createTempClassLoader(ClassLoader parent) {
      return new TempClassLoader(parent);
   }

   public static URLClassLoader createTempClassLoader(String appId, URL[] rawUrls, ClassLoader parent) {
      String updatedAppId = appId;
      if (appId != null) {
         File file = new File(appId);
         if (file.exists()) {
            updatedAppId = file.getName();
            if (updatedAppId.endsWith(".war") || updatedAppId.endsWith(".ear")) {
               updatedAppId = updatedAppId.substring(0, updatedAppId.length() - ".war".length());
            }
         }
      }

      ClassLoaderConfigurer configurer1 = QuickJarsTxtParser.parse(new File(appId, "META-INF/jars.txt"));
      ClassLoaderConfigurer configurer2 = QuickJarsTxtParser.parse(new File(appId, "WEB-INF/jars.txt"));
      ClassLoaderConfigurer configurer3 = configurer(updatedAppId);
      if (configurer3 == null) {
         configurer3 = configurer(appId);
      }

      URL[] urls;
      if (configurer1 == null && configurer2 == null && configurer3 == null) {
         urls = rawUrls;
      } else {
         CompositeClassLoaderConfigurer configurer = new CompositeClassLoaderConfigurer(new ClassLoaderConfigurer[]{configurer1, configurer2, configurer3});
         Collection<URL> list = new ArrayList();
         list.addAll(Arrays.asList(rawUrls));
         ClassLoaderConfigurer.Helper.configure(list, configurer);
         urls = (URL[])list.toArray(new URL[list.size()]);
      }

      return new TempClassLoader(createClassLoader(appId, urls, parent));
   }

   public static void clearClassLoaderCaches() {
      clearSunSoftCache(ObjectInputStream.class, "subclassAudits");
      clearSunSoftCache(ObjectOutputStream.class, "subclassAudits");
      clearSunSoftCache(ObjectStreamClass.class, "localDescs");
      clearSunSoftCache(ObjectStreamClass.class, "reflectors");
      Introspector.flushCaches();
   }

   public static void clearSunJarFileFactoryCache(String jarLocation) {
      clearSunJarFileFactoryCacheImpl(jarLocation, 5);
   }

   private static synchronized void clearSunJarFileFactoryCacheImpl(String jarLocation, int attempt) {
      if (!skipClearSunJarFile.get()) {
         logger.debug("Clearing Sun JarFileFactory cache for directory " + jarLocation);

         try {
            Class jarFileFactory = Class.forName("sun.net.www.protocol.jar.JarFileFactory");
            Field fileCacheField = jarFileFactory.getDeclaredField("fileCache");
            fileCacheField.setAccessible(true);
            Map fileCache = (Map)fileCacheField.get((Object)null);
            Map fileCacheCopy = new HashMap(fileCache);
            Field urlCacheField = jarFileFactory.getDeclaredField("urlCache");
            urlCacheField.setAccessible(true);
            Map urlCache = (Map)urlCacheField.get((Object)null);
            Map urlCacheCopy = new HashMap(urlCache);
            Iterator iterator = urlCacheCopy.entrySet().iterator();
            List urlCacheRemoveKeys = new ArrayList();

            while(iterator.hasNext()) {
               Map.Entry entry = (Map.Entry)iterator.next();
               Object key = entry.getKey();
               if (key instanceof ZipFile) {
                  ZipFile zf = (ZipFile)key;
                  File file = new File(zf.getName());
                  if (isParent(jarLocation, file)) {
                     urlCacheRemoveKeys.add(key);
                  }
               } else {
                  logger.warning("Unexpected key type: " + key);
               }
            }

            iterator = fileCacheCopy.entrySet().iterator();
            List fileCacheRemoveKeys = new ArrayList();

            while(iterator.hasNext()) {
               Map.Entry entry = (Map.Entry)iterator.next();
               Object value = entry.getValue();
               if (urlCacheRemoveKeys.contains(value)) {
                  fileCacheRemoveKeys.add(entry.getKey());
               }
            }

            for(Object next : fileCacheRemoveKeys) {
               try {
                  Object remove = fileCache.remove(next);
                  if (null != remove) {
                     logger.debug("Removed item from fileCache: " + remove);
                  }
               } catch (Throwable var17) {
                  logger.warning("Failed to remove item from fileCache: " + next);
               }
            }

            for(Object next : urlCacheRemoveKeys) {
               try {
                  Object remove = urlCache.remove(next);

                  try {
                     ((ZipFile)next).close();
                  } catch (Throwable var15) {
                  }

                  if (null != remove) {
                     logger.debug("Removed item from urlCache: " + remove);
                  }
               } catch (Throwable var16) {
                  logger.warning("Failed to remove item from urlCache: " + next);
               }
            }
         } catch (ConcurrentModificationException e) {
            if (attempt > 0) {
               clearSunJarFileFactoryCacheImpl(jarLocation, attempt - 1);
            } else {
               logger.error("Unable to clear Sun JarFileFactory cache after 5 attempts", (Throwable)e);
            }
         } catch (NoSuchFieldException | ClassNotFoundException var19) {
         } catch (RuntimeException re) {
            if ("java.lang.reflect.InaccessibleObjectException".equals(re.getClass().getName())) {
               skipClearSunJarFile.compareAndSet(false, true);
               return;
            }

            throw re;
         } catch (Throwable e) {
            if (Boolean.getBoolean("openejb.java9.hack")) {
               return;
            }

            logger.error("Unable to clear Sun JarFileFactory cache", e);
         }

      }
   }

   private static boolean isParent(String jarLocation, File file) {
      for(File dir = new File(jarLocation); file != null; file = file.getParentFile()) {
         if (file.equals(dir)) {
            return true;
         }
      }

      return false;
   }

   public static void clearSunSoftCache(Class clazz, String fieldName) {
      try {
         Field field = clazz.getDeclaredField(fieldName);
         field.setAccessible(true);
         Map cache = (Map)field.get((Object)null);
         cache.clear();
      } catch (Throwable var4) {
      }

   }

   public static void cleanOpenJPACache(ClassLoader classLoader) {
      if (classLoader != ClassLoader.getSystemClassLoader()) {
         try {
            Class<?> pcRegistryClass = ClassLoaderUtil.class.getClassLoader().loadClass("org.apache.openjpa.enhance.PCRegistry");
            Method deRegisterMethod = pcRegistryClass.getMethod("deRegister", ClassLoader.class);
            deRegisterMethod.invoke((Object)null, classLoader);
         } catch (Throwable var3) {
         }
      }

   }

   private static String toString(ClassLoader classLoader) {
      return classLoader == null ? "null" : classLoader.getClass().getSimpleName() + "@" + System.identityHashCode(classLoader);
   }

   public static String resourceName(String s) {
      return s.replace(".", "/") + ".class";
   }

   public static ClassLoaderConfigurer configurer(String rawId) {
      String id = rawId;
      if (rawId != null && (rawId.startsWith("/") || rawId.startsWith("\\")) && !(new File(rawId)).exists() && rawId.length() > 1) {
         id = rawId.substring(1);
      }

      if (id == null) {
         id = "";
      }

      String key = "tomee.classloader.configurer." + id + ".clazz";
      String impl = SystemInstance.get().getProperty(key);
      if (impl == null) {
         key = "tomee.classloader.configurer.clazz";
         impl = SystemInstance.get().getProperty(key);
         if (impl == null) {
            key = "openejb.classloader.configurer." + id + ".clazz";
            impl = SystemInstance.get().getProperty(key);
            if (impl == null) {
               key = "openejb.classloader.configurer.clazz";
               impl = SystemInstance.get().getProperty(key);
            }
         }
      }

      if (impl == null) {
         return null;
      } else {
         key = key.substring(0, key.length() - "clazz".length());
         boolean list = false;

         try {
            ClassLoaderUtil.class.getClassLoader().loadClass(impl);
         } catch (ClassNotFoundException var8) {
            list = true;
         }

         if (!list) {
            return createConfigurer(key, impl);
         } else {
            String[] names = impl.split(",");
            ClassLoaderConfigurer[] configurers = new ClassLoaderConfigurer[names.length];

            for(int i = 0; i < names.length; ++i) {
               configurers[i] = createConfigurer(names[i], SystemInstance.get().getProperty(names[i] + ".clazz"));
            }

            return new CompositeClassLoaderConfigurer(configurers);
         }
      }
   }

   private static ClassLoaderConfigurer createConfigurer(String key, String impl) {
      try {
         ObjectRecipe recipe = new ObjectRecipe(impl);

         for(Map.Entry<Object, Object> entry : SystemInstance.get().getProperties().entrySet()) {
            String entryKey = entry.getKey().toString();
            if (entryKey.startsWith(key)) {
               String newKey = entryKey.substring(key.length());
               if (!"clazz".equals(newKey)) {
                  recipe.setProperty(newKey, entry.getValue());
               }
            }
         }

         Object instance = recipe.create();
         if (instance instanceof ClassLoaderConfigurer) {
            return (ClassLoaderConfigurer)instance;
         }

         logger.error(impl + " is not a classlaoder configurer, using default behavior");
      } catch (Exception var7) {
         logger.error("Can't create classloader configurer " + impl + ", using default behavior");
      }

      return null;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, ClassLoaderUtil.class);
      classLoadersByApp = new HashMap();
      appsByClassLoader = new HashMap();
      localUrlCache = new UrlCache();
      skipClearSunJarFile = new AtomicBoolean();
   }
}
