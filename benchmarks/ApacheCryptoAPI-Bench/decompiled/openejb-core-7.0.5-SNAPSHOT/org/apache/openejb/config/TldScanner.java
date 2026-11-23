package org.apache.openejb.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.DaemonThreadFactory;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.filter.Filters;

public class TldScanner {
   private static final Map<ClassLoader, Set<URL>> cache = new WeakHashMap();
   private static final Map<Integer, Set<URL>> cacheByhashCode = new WeakHashMap();

   public static Set<URL> scan(ClassLoader classLoader) throws OpenEJBException {
      if (skip()) {
         return Collections.emptySet();
      } else if (classLoader == null) {
         return Collections.emptySet();
      } else {
         Set<URL> urls = (Set)cache.get(classLoader);
         if (urls != null) {
            return urls;
         } else {
            Set<URL> result = scanClassLoaderForTagLibs(classLoader);
            cache.put(classLoader, result);
            return result;
         }
      }
   }

   private static boolean skip() {
      return !"true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.taglib.scan", "true"));
   }

   public static Set<URL> scanClassLoaderForTagLibs(ClassLoader classLoader) throws OpenEJBException {
      if (skip()) {
         return Collections.emptySet();
      } else {
         Set<URL> tldUrls = new HashSet();
         if (classLoader == null) {
            return tldUrls;
         } else if (classLoader == Object.class.getClassLoader()) {
            return tldUrls;
         } else {
            List<URL> urls = urls(classLoader);
            int hashCodeForUrls = hash(urls);
            Set<URL> cachedSet = (Set)cacheByhashCode.get(hashCodeForUrls);
            if (cachedSet != null) {
               return cachedSet;
            } else {
               tldUrls.addAll(scan(classLoader.getParent()));
               if (urls.size() > 0) {
                  ExecutorService es = Executors.newFixedThreadPool(Math.min(urls.size(), 2 * Runtime.getRuntime().availableProcessors() + 1), new DaemonThreadFactory(new Object[]{"OpenEJB-tld-server-scanning"}));
                  Collection<Future<Set<URL>>> futures = new ArrayList(urls.size());
                  Iterator var7 = urls.iterator();

                  label66:
                  while(true) {
                     URL url;
                     while(true) {
                        if (!var7.hasNext()) {
                           es.shutdown();

                           for(Future<Set<URL>> set : futures) {
                              try {
                                 tldUrls.addAll((Collection)set.get());
                              } catch (Exception var11) {
                              }
                           }
                           break label66;
                        }

                        url = (URL)var7.next();
                        if (!url.getProtocol().equals("jar")) {
                           break;
                        }

                        try {
                           String path = url.getPath();
                           if (path.endsWith("!/")) {
                              path = path.substring(0, path.length() - 2);
                           }

                           url = new URL(path);
                           break;
                        } catch (MalformedURLException e) {
                           DeploymentLoader.logger.warning("JSP tag library location bad: " + url.toExternalForm(), (Throwable)e);
                        }
                     }

                     if (url.getProtocol().equals("file")) {
                        final File file;
                        try {
                           file = URLs.toFile(url).getCanonicalFile().getAbsoluteFile();
                        } catch (IOException e) {
                           DeploymentLoader.logger.warning("JSP tag library location bad: " + url.toExternalForm(), (Throwable)e);
                           continue;
                        }

                        futures.add(es.submit(new Callable<Set<URL>>() {
                           public Set<URL> call() throws Exception {
                              return TldScanner.scanForTagLibs(file);
                           }
                        }));
                     }
                  }
               }

               cacheByhashCode.put(hashCodeForUrls, tldUrls);
               return tldUrls;
            }
         }
      }
   }

   static Set<URL> scanWarForTagLibs(File war) {
      Set<URL> urls = new HashSet();
      File webInfDir = new File(war, "WEB-INF");
      if (!webInfDir.isDirectory()) {
         return urls;
      } else {
         LinkedList<File> files = new LinkedList();
         File[] list = webInfDir.listFiles();
         if (list != null) {
            for(File file : list) {
               if (!"lib".equals(file.getName()) && !"classes".equals(file.getName())) {
                  files.add(file);
               }
            }
         }

         File webInfMetaInf = new File(webInfDir, "classes/META-INF");
         if (webInfMetaInf.exists()) {
            files.addAll(Arrays.asList(webInfMetaInf.listFiles(new FilenameFilter() {
               public boolean accept(File dir, String name) {
                  return name.endsWith(".tld");
               }
            })));
         }

         if (files.isEmpty()) {
            return urls;
         } else {
            while(!files.isEmpty()) {
               File file = (File)files.removeFirst();
               if (file.isDirectory()) {
                  File[] a = file.listFiles();
                  if (a != null) {
                     files.addAll(Arrays.asList(a));
                  }
               } else if (file.getName().endsWith(".tld")) {
                  try {
                     file = file.getCanonicalFile().getAbsoluteFile();
                     urls.add(file.toURI().toURL());
                  } catch (IOException e) {
                     DeploymentLoader.logger.warning("JSP tag library location bad: " + file.getAbsolutePath(), (Throwable)e);
                  }
               }
            }

            return urls;
         }
      }
   }

   static Set<URL> scanForTagLibs(File file) {
      Set<URL> tldLocations = new HashSet();

      try {
         String location = file.toURI().toURL().toExternalForm();
         if (location.endsWith(".jar")) {
            Set<URL> urls = scanJarForTagLibs(file);
            tldLocations.addAll(urls);
         } else if (file.getName().endsWith(".tld")) {
            URL url = file.toURI().toURL();
            tldLocations.add(url);
         }
      } catch (IOException e) {
         DeploymentLoader.logger.warning("Error scanning for JSP tag libraries: " + file.getAbsolutePath(), (Throwable)e);
      }

      return tldLocations;
   }

   static Set<URL> scanJarForTagLibs(File file) {
      Set<URL> urls = new HashSet();
      if (!file.isFile()) {
         return urls;
      } else {
         JarFile jarFile = null;

         try {
            jarFile = new JarFile(file);
            URL jarFileUrl = new URL("jar", "", -1, file.toURI().toURL().toExternalForm() + "!/");

            for(JarEntry entry : Collections.list(jarFile.entries())) {
               String name = entry.getName();
               if (name.startsWith("META-INF/") && name.endsWith(".tld")) {
                  URL url = new URL(jarFileUrl, name);
                  urls.add(url);
               }
            }
         } catch (IOException e) {
            DeploymentLoader.logger.warning("Error scanning jar for JSP tag libraries: " + file.getAbsolutePath(), (Throwable)e);
         } finally {
            if (jarFile != null) {
               try {
                  jarFile.close();
               } catch (IOException var15) {
               }
            }

         }

         return urls;
      }
   }

   public static void quickClean(ClassLoader loader) {
      if (loader != null) {
         cache.remove(loader);
         if (loader.getParent() != TldScanner.class.getClassLoader()) {
            quickClean(loader.getParent());
         }

      }
   }

   public static void forceCompleteClean(ClassLoader loader) {
      if (loader != null) {
         quickClean(loader);
         cacheByhashCode.remove(hash(urls(loader)));
         if (loader.getParent() != TldScanner.class.getClassLoader()) {
            forceCompleteClean(loader.getParent());
         }

      }
   }

   private static List<URL> urls(ClassLoader classLoader) {
      UrlSet urlSet = new UrlSet(new URL[0]);
      if (classLoader instanceof URLClassLoader) {
         URLClassLoader urlClassLoader = (URLClassLoader)classLoader;

         try {
            urlSet = new UrlSet(urlClassLoader.getURLs());
         } catch (NullPointerException var6) {
            return Collections.emptyList();
         }
      } else {
         try {
            urlSet = new UrlSet(classLoader);
         } catch (IOException e) {
            DeploymentLoader.logger.warning("Error scanning class loader for JSP tag libraries", (Throwable)e);
         }
      }

      try {
         urlSet = URLs.cullSystemJars(urlSet);
         urlSet = NewLoaderLogic.applyBuiltinExcludes(urlSet, Filters.tokens(new String[]{"taglibs-standard-impl", "taglibs-standard-jstlel", "javax.faces-2.", "spring-security-taglibs", "spring-webmvc"}), Filters.prefixes(new String[]{"commons-jcs-", "myfaces-", "tomcat-websocket.jar"}));
      } catch (IOException e) {
         DeploymentLoader.logger.warning("Error scanning class loader for JSP tag libraries", (Throwable)e);
      }

      return urlSet.getUrls();
   }

   private static int hash(List<URL> urls) {
      int hash = 0;

      for(URL u : urls) {
         hash *= 31;
         if (u != null) {
            hash += u.toExternalForm().hashCode();
         }
      }

      return hash;
   }
}
