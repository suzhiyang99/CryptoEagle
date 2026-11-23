package org.apache.openejb.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;

public class UrlCache {
   private static final Logger logger;
   public static final boolean antiJarLocking;
   public static final File cacheDir;
   private final Map<String, Map<URL, File>> cache = new TreeMap();

   public synchronized URL[] cacheUrls(String appId, URL[] urls) {
      if (!antiJarLocking) {
         return urls;
      } else {
         LinkedHashSet<URL> cachedUrls = new LinkedHashSet();
         LinkedList<URL> locationStack = new LinkedList(Arrays.asList(urls));

         while(!locationStack.isEmpty()) {
            URL url = (URL)locationStack.removeFirst();
            if (!cachedUrls.contains(url)) {
               File file = this.cacheUrl(appId, url);
               if (file != null) {
                  try {
                     cachedUrls.add(file.toURI().toURL());
                     List<URL> manifestClassPath = this.getManifestClassPath(url, file);
                     locationStack.addAll(0, manifestClassPath);
                  } catch (MalformedURLException e) {
                     logger.error("Error caching url. Original jar file will be used which may result in a file lock: url=" + url, (Throwable)e);
                     cachedUrls.add(url);
                  }
               } else {
                  cachedUrls.add(url);
               }
            }
         }

         return (URL[])cachedUrls.toArray(new URL[cachedUrls.size()]);
      }
   }

   public synchronized void releaseUrls(String appId) {
      logger.debug("Releasing URLs for application " + appId);
      Map<URL, File> urlFileMap = (Map)this.cache.remove(appId);
      if (urlFileMap != null) {
         for(File file : urlFileMap.values()) {
            if (file.delete()) {
               logger.debug("Deleted cached file " + file);
            } else {
               logger.debug("Unable to delete cached file " + file);
            }
         }
      }

   }

   public File getUrlCachedName(String appId, URL url) {
      Map<URL, File> appCache = this.getAppCache(appId);
      return appCache.containsKey(url) ? (File)appCache.get(url) : null;
   }

   public boolean isUrlCached(String appId, URL url) {
      Map<URL, File> appCache = this.getAppCache(appId);
      return appCache.containsKey(url);
   }

   public URL getUrlKeyCached(String appId, File file) {
      if (file == null) {
         return null;
      } else {
         Map<URL, File> appCache = this.getAppCache(appId);

         for(Map.Entry<URL, File> entry : appCache.entrySet()) {
            if (((File)entry.getValue()).equals(file)) {
               return (URL)entry.getKey();
            }
         }

         URL keyUrl;
         try {
            keyUrl = file.toURI().toURL();
         } catch (MalformedURLException var6) {
            return null;
         }

         if (appCache.containsKey(keyUrl)) {
            return keyUrl;
         } else {
            return null;
         }
      }
   }

   private synchronized File cacheUrl(String appId, URL url) {
      if (!"file".equals(url.getProtocol())) {
         return null;
      } else {
         File sourceFile = URLs.toFile(url);
         if (!sourceFile.exists()) {
            return null;
         } else if (!sourceFile.canRead()) {
            return null;
         } else if (sourceFile.isDirectory()) {
            return sourceFile;
         } else {
            sourceFile = sourceFile.getAbsoluteFile();

            try {
               url = sourceFile.toURI().toURL();
            } catch (MalformedURLException var13) {
            }

            Map<URL, File> appCache = this.getAppCache(appId);
            if (appCache.containsKey(url)) {
               return (File)appCache.get(url);
            } else if (sourceFile.getParentFile().equals(cacheDir)) {
               appCache.put(url, sourceFile);
               return sourceFile;
            } else {
               String name = sourceFile.getName();
               int dot = name.lastIndexOf(".");
               String prefix = name;
               String suffix = "";
               if (dot > 0) {
                  prefix = name.substring(0, dot) + "-";
                  suffix = name.substring(dot, name.length());
               }

               File cacheFile = null;

               boolean success;
               try {
                  try {
                     cacheFile = File.createTempFile(prefix, suffix, cacheDir);
                  } catch (Throwable var14) {
                     File tmp = new File("tmp");
                     if (!tmp.exists() && !tmp.mkdirs()) {
                        throw new IOException("Failed to create local tmp directory: " + tmp.getAbsolutePath());
                     }

                     cacheFile = File.createTempFile(prefix, suffix, tmp);
                  }

                  cacheFile.deleteOnExit();
                  success = JarExtractor.copyRecursively(sourceFile, cacheFile);
               } catch (IOException var15) {
                  success = false;
               }

               if (success) {
                  appCache.put(url, cacheFile);
                  logger.debug("Coppied jar file to " + cacheFile);
                  return cacheFile;
               } else {
                  JarExtractor.delete(cacheFile);
                  logger.error("Unable to copy jar into URL cache directory. Original jar file will be used which may result in a file lock: file=" + sourceFile);
                  return null;
               }
            }
         }
      }
   }

   private synchronized Map<URL, File> getAppCache(String appId) {
      Map<URL, File> urlFileMap = (Map)this.cache.get(appId);
      if (urlFileMap == null) {
         urlFileMap = new LinkedHashMap();
         this.cache.put(appId, urlFileMap);
      }

      return urlFileMap;
   }

   private List<URL> getManifestClassPath(URL codeSource, File location) {
      try {
         Manifest manifest = this.loadManifest(location);
         if (manifest == null) {
            return Collections.emptyList();
         } else {
            String manifestClassPath = manifest.getMainAttributes().getValue(Name.CLASS_PATH);
            if (manifestClassPath == null) {
               return Collections.emptyList();
            } else {
               LinkedList<URL> classPathUrls = new LinkedList();
               StringTokenizer tokenizer = new StringTokenizer(manifestClassPath, " ");

               while(tokenizer.hasMoreTokens()) {
                  String entry = tokenizer.nextToken();

                  try {
                     URL entryUrl = new URL(codeSource, entry);
                     classPathUrls.addLast(entryUrl);
                  } catch (MalformedURLException var9) {
                  }
               }

               return classPathUrls;
            }
         }
      } catch (IOException var10) {
         return Collections.emptyList();
      }
   }

   private Manifest loadManifest(File location) throws IOException {
      if (location.isDirectory()) {
         File manifestFile = new File(location, "META-INF/MANIFEST.MF");
         if (manifestFile.isFile() && manifestFile.canRead()) {
            InputStream in = null;

            Manifest var4;
            try {
               in = IO.read(manifestFile);
               var4 = new Manifest(in);
            } finally {
               close((Closeable)in);
            }

            return var4;
         } else {
            return null;
         }
      } else {
         JarFile jarFile = new JarFile(location);

         Manifest in;
         try {
            in = jarFile.getManifest();
         } finally {
            close(jarFile);
         }

         return in;
      }
   }

   private static File createCacheDir() {
      try {
         FileUtils openejbBase = SystemInstance.get().getBase();
         File dir = null;
         if (SystemInstance.get().getConf((String)null).exists()) {
            try {
               dir = openejbBase.getDirectory("temp");
            } catch (IOException var7) {
            }
         }

         if (dir == null) {
            dir = Files.tmpdir();
         }

         if (dir.exists()) {
            File[] files = dir.listFiles();
            if (null != files) {
               for(File f : files) {
                  deleteDir(f);
               }
            }
         } else {
            dir = createCacheDir(new File(dir.getAbsolutePath()));
         }

         return dir;
      } catch (IOException e) {
         throw new OpenEJBRuntimeException(e);
      }
   }

   private static File createCacheDir(File dir) throws IOException {
      if (dir.exists() && dir.isDirectory()) {
         return dir;
      } else if (dir.exists() && !dir.isDirectory()) {
         throw new IOException("Cache temp directory held by file: " + dir);
      } else if (!dir.mkdirs()) {
         throw new IOException("Unable to create cache temp directory: " + dir);
      } else {
         Thread.yield();
         return dir;
      }
   }

   public static void deleteDir(File dir) {
      if (dir != null) {
         File[] fileNames = dir.listFiles();
         if (fileNames != null) {
            for(File file : fileNames) {
               if (file.isDirectory()) {
                  deleteDir(file);
               } else if (file.delete()) {
                  logger.debug("Deleted file " + file);
               } else {
                  logger.debug("Unable to delete file " + file);
               }
            }
         }

         if (dir.delete()) {
            logger.debug("Deleted file " + dir);
         } else {
            logger.debug("Unable to delete file " + dir);
         }

      }
   }

   private static void close(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }

   private static void close(JarFile closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, UrlCache.class);
      antiJarLocking = SystemInstance.get().getOptions().get("antiJarLocking", false);
      if (antiJarLocking) {
         cacheDir = createCacheDir();
         logger.info("AntiJarLocking enabled. Using URL cache dir " + cacheDir);
      } else {
         cacheDir = null;
      }

   }
}
