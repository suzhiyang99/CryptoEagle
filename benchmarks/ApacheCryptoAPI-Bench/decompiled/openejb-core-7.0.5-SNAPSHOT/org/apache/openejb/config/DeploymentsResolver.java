package org.apache.openejb.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.config.sys.Deployments;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.filter.ExcludeIncludeFilter;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.Filters;
import org.apache.xbean.finder.filter.IncludeExcludeFilter;

public class DeploymentsResolver implements DeploymentFilterable {
   private static final String EXCLUDE_INCLUDE_ORDER = SystemInstance.get().getOptions().get("openejb.exclude-include.order", "include-exclude");
   private static final String[] ignoreDirs = SystemInstance.get().getProperty("openejb.ignore.directories", ".svn,_svn,cvs,.git,.hg").split(",");
   private static final Logger logger;
   private static File lib;

   public static boolean isExtractedDir(File f) {
      return (new File(f.getParentFile(), f.getName() + ".war")).exists() ? true : (new File(f.getParentFile(), f.getName() + ".ear")).exists();
   }

   protected static boolean isValidDirectory(File file) {
      if (file.isDirectory() && !file.isHidden() && !file.equals(lib)) {
         String fn = file.getName();

         for(String dir : ignoreDirs) {
            if (fn.equalsIgnoreCase(dir)) {
               return false;
            }
         }

         String[] files = file.list();
         return null != files && files.length > 0;
      } else {
         return false;
      }
   }

   public static void loadFrom(Deployments dep, FileUtils path, List<File> jarList) {
      if (dep.getDir() != null) {
         try {
            loadFromDir(dep, path, jarList);
         } catch (Files.FileDoesNotExistException e) {
            logger.warning("File error: <Deployments dir=\"" + dep.getDir() + "\"> - " + e.getMessage());
         } catch (RuntimeException e) {
            String message = "Runtime error: <Deployments dir=\"" + dep.getDir() + "\"> - " + e.getMessage();
            logger.error(message);
            throw new DeploymentsConfigurationException(message);
         }
      } else if (dep.getFile() != null) {
         try {
            loadFromFile(dep, path, jarList);
         } catch (RuntimeException e) {
            String message = "<Deployments file=\"" + dep.getFile() + "\"> - " + e.getMessage();
            logger.error(message);
            throw new DeploymentsConfigurationException(message);
         }
      }

   }

   private static void loadFromFile(Deployments dep, FileUtils path, List<File> jarList) {
      File file = Files.path(path.getDirectory(), new String[]{dep.getFile()});
      Files.exists(file);
      Files.readable(file);
      Files.file(file);
      if (!jarList.contains(file)) {
         jarList.add(file);
      }

   }

   private static void loadFromDir(Deployments dep, FileUtils path, List<File> jarList) {
      File dir = Files.path(path.getDirectory(), new String[]{dep.getDir()});
      Files.exists(dir);
      Files.readable(dir);
      Files.dir(dir);
      Files.notHidden(dir);
      Map<String, File> files = new LinkedHashMap();
      File[] list = dir.listFiles(new FileFilter() {
         public boolean accept(File f) {
            if (!f.isDirectory()) {
               return true;
            } else {
               return DeploymentsResolver.isValidDirectory(f) && !DeploymentsResolver.isExtractedDir(f);
            }
         }
      });
      if (list != null) {
         for(File file : list) {
            files.put(file.getAbsolutePath(), file);
         }

         for(File file : list) {
            if (isArchive(file)) {
               String archive = file.getAbsolutePath();
               files.remove(archive.substring(0, archive.length() - 4));
            }
         }
      }

      for(File file : files.values()) {
         if (!jarList.contains(file)) {
            jarList.add(file);
         }
      }

   }

   private static boolean isArchive(File file) {
      if (!file.isFile()) {
         return false;
      } else if (!file.getName().toLowerCase().endsWith("ar")) {
         return false;
      } else {
         String name = file.getName();
         char c = name.charAt(name.length() - 4);
         return c == '.';
      }
   }

   public static List<URL> loadFromClasspath(ClassLoader classLoader) {
      ClasspathSearcher searchResult = (new ClasspathSearcher()).loadUrls(classLoader);
      if (searchResult.prefiltered != null && searchResult.urlSet != null) {
         try {
            List<URL> jarList = new ArrayList(searchResult.urls.size());
            int size = searchResult.urls.size();
            if (size == 0 && searchResult.include.length() > 0) {
               logger.warning("No classpath URLs matched.  Current settings: openejb.deployments.classpath.exclude='" + searchResult.exclude + "', " + "openejb.deployments.classpath.include" + "='" + searchResult.include + "'");
               return jarList;
            } else if (size == 0 && !searchResult.filterDescriptors && searchResult.prefiltered.getUrls().size() == 0) {
               return jarList;
            } else {
               if (size < 20) {
                  logger.debug("Inspecting classpath for applications: " + size + " urls.");
               } else {
                  boolean willScrape = searchResult.requireDescriptors.size() < RequireDescriptors.values().length;
                  if (size < 50 && willScrape) {
                     logger.info("Inspecting classpath for applications: " + size + " urls. Consider adjusting your exclude/include.  Current settings: " + "openejb.deployments.classpath.exclude" + "='" + searchResult.exclude + "', " + "openejb.deployments.classpath.include" + "='" + searchResult.include + "'");
                  } else if (willScrape) {
                     logger.warning("Inspecting classpath for applications: " + size + " urls.");
                     logger.warning("ADJUST THE EXCLUDE/INCLUDE!!!.  Current settings: openejb.deployments.classpath.exclude='" + searchResult.exclude + "', " + "openejb.deployments.classpath.include" + "='" + searchResult.include + "'");
                  }
               }

               long begin = System.currentTimeMillis();
               processUrls("DeploymentsResolver1", searchResult.urls, classLoader, searchResult.requireDescriptors, jarList);
               long end = System.currentTimeMillis();
               long time = end - begin;
               UrlSet unchecked = new UrlSet(new URL[0]);
               if (!searchResult.filterDescriptors) {
                  unchecked = NewLoaderLogic.applyBuiltinExcludes(searchResult.prefiltered.exclude(searchResult.prefiltered));
                  if (searchResult.filterSystemApps) {
                     unchecked = unchecked.exclude(".*/openejb-[^/]+(.(jar|ear|war)(./)?|/target/classes/?)");
                  }

                  processUrls("DeploymentsResolver2", unchecked.getUrls(), classLoader, EnumSet.allOf(RequireDescriptors.class), jarList);
               }

               if (logger.isDebugEnabled()) {
                  int urlCount = searchResult.urlSet.getUrls().size() + unchecked.getUrls().size();
                  logger.debug("DeploymentsResolver: URLs after filtering: " + urlCount);

                  for(URL url : searchResult.urlSet.getUrls()) {
                     logger.debug("Annotations path: " + url);
                  }

                  for(URL url : unchecked.getUrls()) {
                     logger.debug("Descriptors path: " + url);
                  }
               }

               int urlSize = searchResult.urls.size();
               if (urlSize == 0) {
                  return jarList;
               } else {
                  if (time < 1000L) {
                     logger.debug("Searched " + urlSize + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urlSize + " milliseconds per url.");
                  } else if (time >= 4000L && urlSize >= 3) {
                     if (time < 10000L) {
                        logger.warning("Searched " + urlSize + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urlSize + " milliseconds per url.");
                        logger.warning("Consider adjusting your openejb.deployments.classpath.exclude and openejb.deployments.classpath.include settings.  Current settings: exclude='" + searchResult.exclude + "', include='" + searchResult.include + "'");
                     } else {
                        logger.fatal("Searched " + urlSize + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urlSize + " milliseconds per url.  TOO LONG!");
                        logger.fatal("ADJUST THE EXCLUDE/INCLUDE!!!.  Current settings: openejb.deployments.classpath.exclude='" + searchResult.exclude + "', " + "openejb.deployments.classpath.include" + "='" + searchResult.include + "'");
                        List<String> list = new ArrayList();

                        for(URL url : searchResult.urls) {
                           list.add(url.toExternalForm());
                        }

                        Collections.sort(list);

                        for(String url : list) {
                           logger.info("Matched: " + url);
                        }
                     }
                  } else {
                     logger.info("Searched " + urlSize + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urlSize + " milliseconds per url.");
                  }

                  return jarList;
               }
            }
         } catch (IOException e1) {
            logger.warning("Unable to search classpath for modules: Received Exception: " + e1.getClass().getName() + " " + e1.getMessage(), (Throwable)e1);
            return new ArrayList();
         }
      } else {
         return new ArrayList();
      }
   }

   /** @deprecated */
   @Deprecated
   public static void loadFromClasspath(FileUtils ignored, List<URL> jarList, ClassLoader classLoader) {
      jarList.addAll(loadFromClasspath(classLoader));
   }

   /** @deprecated */
   @Deprecated
   public static void processUrls(String caller, List<URL> urls, ClassLoader classLoader, Set<RequireDescriptors> requireDescriptors, FileUtils ignored, List<URL> jarList) {
      processUrls(caller, urls, classLoader, requireDescriptors, jarList);
   }

   public static void processUrls(String caller, List<URL> urls, ClassLoader classLoader, Set<RequireDescriptors> requireDescriptors, List<URL> jarList) {
      for(URL url : urls) {
         String urlProtocol = url.getProtocol();
         boolean isValidURL = urlProtocol.equals("jar") || urlProtocol.equals("file");
         if (!isValidURL) {
            logger.warning("Unknown protocol " + urlProtocol);
         } else {
            if (logger.isDebugEnabled()) {
               logger.debug(caller + ".processing: " + url);
            }

            try {
               DeploymentLoader deploymentLoader = new DeploymentLoader();
               Class<? extends DeploymentModule> moduleType = deploymentLoader.discoverModuleType(url, classLoader, requireDescriptors);
               if (AppModule.class.isAssignableFrom(moduleType) || EjbModule.class.isAssignableFrom(moduleType) || PersistenceModule.class.isAssignableFrom(moduleType) || ConnectorModule.class.isAssignableFrom(moduleType) || ClientModule.class.isAssignableFrom(moduleType)) {
                  URL archive = URLs.toFileUrl(url);
                  if (!jarList.contains(archive)) {
                     jarList.add(archive);
                     File file = URLs.toFile(archive);
                     logger.info("Found " + moduleType.getSimpleName() + " in classpath: " + file.getAbsolutePath());
                  }
               }
            } catch (IOException e) {
               logger.warning("Unable to determine the module type of " + url.toExternalForm() + ": Exception: " + e.getMessage(), (Throwable)e);
            } catch (UnknownModuleTypeException var14) {
            }
         }
      }

   }

   static {
      logger = DeploymentLoader.logger;

      try {
         lib = SystemInstance.get().getHome().getDirectory("lib", false);
      } catch (IOException var1) {
      }

   }

   public static class DeploymentsConfigurationException extends RuntimeException {
      public DeploymentsConfigurationException(String message) {
         super(message);
      }
   }

   public static class ClasspathSearcher {
      private final String include;
      private final String exclude;
      private final boolean filterSystemApps;
      private final Set<RequireDescriptors> requireDescriptors;
      private final boolean filterDescriptors;
      private UrlSet urlSet;
      private UrlSet prefiltered;
      private List<URL> urls;

      public ClasspathSearcher() {
         Options options = SystemInstance.get().getOptions();
         this.include = options.get("openejb.deployments.classpath.include", ".*");
         this.exclude = options.get("openejb.deployments.classpath.exclude", "");
         this.filterSystemApps = options.get("openejb.deployments.classpath.filter.systemapps", true);
         this.requireDescriptors = options.getAll("openejb.deployments.classpath.require.descriptor", new RequireDescriptors[]{RequireDescriptors.CLIENT});
         this.filterDescriptors = options.get("openejb.deployments.classpath.filter.descriptors", false);
      }

      public List<URL> getUrls() {
         return this.urls;
      }

      private UrlSet cleanUpUrlSet(UrlSet set) {
         if (set.size() >= 5) {
            return set;
         } else {
            List<URL> copy = set.getUrls();

            for(URL url : set.getUrls()) {
               try {
                  if ("file".equals(url.getProtocol()) && copy.contains(new URL("jar:" + url.toExternalForm() + "!/"))) {
                     copy.remove(url);
                  }
               } catch (MalformedURLException var6) {
               }
            }

            return new UrlSet(copy);
         }
      }

      public ClasspathSearcher loadUrls(ClassLoader classLoader) {
         try {
            UrlSet original = this.cleanUpUrlSet(new UrlSet(classLoader));
            this.urlSet = URLs.cullSystemJars(original);
            this.prefiltered = this.urlSet;
            Filter includeFilter = Filters.patterns(new String[]{this.include});
            if (".*".equals(this.include) && "".equals(this.exclude)) {
               includeFilter = null;
            } else {
               Filter filter;
               if (DeploymentsResolver.EXCLUDE_INCLUDE_ORDER.startsWith("include")) {
                  filter = new IncludeExcludeFilter(includeFilter, Filters.patterns(new String[]{this.exclude}));
               } else {
                  filter = new ExcludeIncludeFilter(includeFilter, Filters.patterns(new String[]{this.exclude}));
               }

               this.urlSet = this.urlSet.filter(filter);
            }

            if (this.prefiltered.size() == this.urlSet.size()) {
               this.urlSet = NewLoaderLogic.applyBuiltinExcludes(this.urlSet, includeFilter);
               if (this.filterSystemApps) {
                  this.urlSet = this.urlSet.exclude(".*/openejb-[^/]+(.(jar|ear|war)(!/)?|/target/(test-)?classes/?)");
               }
            }

            boolean isWindows = JavaSecurityManagers.getSystemProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH).startsWith("windows");
            if (isWindows && Boolean.parseBoolean(SystemInstance.get().getProperty("openejb.resolver.windows.lowercase-urls", "true"))) {
               this.urls = new ArrayList();

               for(URL url : this.urlSet.getUrls()) {
                  String ef = url.toExternalForm().toLowerCase();
                  URL u = new URL(ef);
                  if (!this.urls.contains(u)) {
                     this.urls.add(u);
                  }
               }
            } else {
               this.urls = this.urlSet.getUrls();
            }

            return this;
         } catch (IOException e1) {
            DeploymentsResolver.logger.warning("Unable to search classpath", (Throwable)e1);
            return this;
         }
      }
   }
}
