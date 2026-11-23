package org.apache.openejb.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PerformanceTimer;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClasspathArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.filter.ExcludeIncludeFilter;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.Filters;
import org.apache.xbean.finder.filter.IncludeExcludeFilter;
import org.apache.xbean.finder.filter.PatternFilter;

public class NewLoaderLogic {
   private static final Logger logger;
   public static final String DEFAULT_EXCLUSIONS_ALIAS = "default-list";
   public static final String ADDITIONAL_EXCLUDES;
   public static final String ADDITIONAL_INCLUDE;
   public static final String EXCLUSION_FILE = "exclusions.list";
   private static String[] exclusions;
   private static volatile Filter filter;

   public static UrlSet filterArchives(Filter filter, ClassLoader classLoader, UrlSet urlSet) {
      for(URL url : urlSet.getUrls()) {
         for(Archive archive : ClasspathArchive.archives(classLoader, new URL[]{url})) {
            FilteredArchive filtered = new FilteredArchive(archive, filter);
            if (!filtered.iterator().hasNext()) {
               urlSet = urlSet.exclude(url);
            }
         }
      }

      return urlSet;
   }

   public static Set<String> callers() {
      return callers(Filters.classes(new String[]{"javax.ejb.embeddable.EJBContainer", "javax.naming.InitialContext"}));
   }

   public static Set<String> callers(Filter start) {
      Set<String> callers = new LinkedHashSet();
      List<StackTraceElement> elements = new ArrayList(Arrays.asList((new Exception()).fillInStackTrace().getStackTrace()));
      Iterator<StackTraceElement> iterator = elements.iterator();

      while(iterator.hasNext()) {
         StackTraceElement element = (StackTraceElement)iterator.next();
         iterator.remove();
         if (start.accept(element.getClassName())) {
            while(iterator.hasNext() && element.getClassName().equals(((StackTraceElement)iterator.next()).getClassName())) {
               iterator.remove();
            }
            break;
         }
      }

      Filter end = Filters.packages(new String[]{"junit.", "org.junit.", "org.testng.", "org.apache.maven.", "org.eclipse.", "com.intellij.", "org.scalatest."});

      for(StackTraceElement element : elements) {
         if (end.accept(element.getClassName())) {
            break;
         }

         callers.add(element.getClassName());
      }

      elements.clear();
      end = Filters.packages(new String[]{"java.", "javax.", "sun.reflect."});
      Iterator<String> classes = callers.iterator();

      while(classes.hasNext()) {
         if (end.accept((String)classes.next())) {
            classes.remove();
         }
      }

      return callers;
   }

   public static UrlSet applyBuiltinExcludes(UrlSet urlSet) throws MalformedURLException {
      return applyBuiltinExcludes(urlSet, (Filter)null);
   }

   public static UrlSet applyBuiltinExcludes(UrlSet urlSet, Filter includeFilter) throws MalformedURLException {
      return applyBuiltinExcludes(urlSet, includeFilter, (Filter)null);
   }

   public static boolean skip(URL url) {
      return skip((URL)url, (Filter)null, (Filter)null);
   }

   public static boolean skip(URL url, Filter includeFilter, Filter excludeFilter) {
      if ("archive".equals(url.getProtocol())) {
         return true;
      } else {
         try {
            File file = URLs.toFile(url);
            String name = NameFiltering.filter(file).getName();
            if (skip(includeFilter, excludeFilter, name)) {
               return true;
            }
         } catch (IllegalArgumentException var5) {
         }

         return false;
      }
   }

   private static boolean skip(Filter includeFilter, Filter excludeFilter, String name) {
      if (includeFilter == null || !includeFilter.accept(name)) {
         if (filter != null && filter.accept(name)) {
            return true;
         }

         if (excludeFilter != null && excludeFilter.accept(name)) {
            return true;
         }
      }

      return false;
   }

   public static boolean skip(String name) {
      getExclusions();
      return filter != null && filter.accept(name);
   }

   public static UrlSet applyBuiltinExcludes(UrlSet urlSet, Filter includeFilter, Filter excludeFilter) throws MalformedURLException {
      getExclusions();
      List<URL> urls = urlSet.getUrls();
      Iterator<URL> iterator = urls.iterator();

      while(iterator.hasNext()) {
         URL url = (URL)iterator.next();
         if (skip(url, includeFilter, excludeFilter)) {
            iterator.remove();
         }
      }

      return new UrlSet(urls);
   }

   /** @deprecated */
   @Deprecated
   public static void setExclusions(String[] exclusionArray) {
      exclusions = exclusionArray;
      filter = null;
      getFilter();
      logExclusions(exclusionArray);
   }

   public static void addAdditionalCustomFilter(final Filter excluded, final Filter included) {
      getExclusions();
      if (excluded != null && included != null) {
         synchronized(NewLoaderLogic.class) {
            final Filter builtIn = new OptimizedExclusionFilter(getExclusions());
            filter = new Filter() {
               public boolean accept(String name) {
                  return !included.accept(name) && (builtIn.accept(name) || excluded.accept(name));
               }
            };
         }
      } else if (excluded != null) {
         synchronized(NewLoaderLogic.class) {
            final Filter builtIn = new OptimizedExclusionFilter(getExclusions());
            filter = new Filter() {
               public boolean accept(String name) {
                  return builtIn.accept(name) || excluded.accept(name);
               }
            };
         }
      } else if (included != null) {
         synchronized(NewLoaderLogic.class) {
            final Filter builtIn = new OptimizedExclusionFilter(getExclusions());
            filter = new Filter() {
               public boolean accept(String name) {
                  return !included.accept(name) && builtIn.accept(name);
               }
            };
         }
      }

      logExclusions(exclusions);
   }

   private static void logExclusions(String[] exclusionArray) {
      if (logger.isDebugEnabled()) {
         logger.debug("Exclusion prefixes: [");

         for(String ex : exclusionArray) {
            logger.debug("-" + ex);
         }

         logger.debug("]");
      }

   }

   public static String[] getExclusions() {
      if (exclusions != null) {
         return exclusions;
      } else {
         FileInputStream fis = null;

         try {
            File exclusionsFile = SystemInstance.get().getConf("exclusions.list");
            if (exclusionsFile != null && exclusionsFile.exists()) {
               fis = new FileInputStream(exclusionsFile);
               exclusions = readInputStreamList(fis);
               logger.info("Loaded classpath exclusions from: " + exclusionsFile.getAbsolutePath());
            }
         } catch (Throwable var10) {
         } finally {
            IO.close(fis);
         }

         if (exclusions == null) {
            exclusions = readDefaultExclusions();
         }

         List<String> excludes = new ArrayList(exclusions.length + 5);
         excludes.addAll(Arrays.asList(exclusions));
         if (ADDITIONAL_EXCLUDES != null) {
            Collections.addAll(excludes, ADDITIONAL_EXCLUDES.split("[ \t\r\n]*,[ \t\n\n]*"));
         }

         if (ADDITIONAL_INCLUDE != null) {
            for(String rawInclude : ADDITIONAL_INCLUDE.split("[ \t\n\n]*,[ \t\n\n]*")) {
               String include = rawInclude.trim();
               Iterator<String> excluded = excludes.iterator();

               while(excluded.hasNext()) {
                  if (((String)excluded.next()).startsWith(include)) {
                     excluded.remove();
                  }
               }
            }
         }

         exclusions = (String[])excludes.toArray(new String[excludes.size()]);
         getFilter();
         logExclusions(exclusions);
         return exclusions;
      }
   }

   public static Filter getFilter() {
      if (filter == null) {
         synchronized(NewLoaderLogic.class) {
            if (filter == null) {
               filter = new OptimizedExclusionFilter(getExclusions());
            }
         }
      }

      return filter;
   }

   private static String[] readDefaultExclusions() {
      InputStream is = null;
      String[] read = null;

      try {
         is = NewLoaderLogic.class.getResourceAsStream("/default.exclusions");
         read = readInputStreamList(is);
         logger.debug("Loaded default.exclusions");
      } catch (Throwable var6) {
      } finally {
         IO.close(is);
      }

      return read;
   }

   public static String sanitize(String value) {
      if (value.endsWith("*.jar")) {
         return value.substring(0, value.length() - 5);
      } else {
         return value.endsWith("*") ? value.substring(0, value.length() - 1) : value;
      }
   }

   public static String[] readInputStreamList(InputStream is) {
      List<String> list = new ArrayList();
      BufferedReader reader = null;

      try {
         reader = new BufferedReader(new InputStreamReader(is));

         String line;
         while((line = reader.readLine()) != null) {
            String value = line.trim();
            if (!line.startsWith("#") && !value.isEmpty()) {
               if ("default-list".equals(value)) {
                  Collections.addAll(list, readDefaultExclusions());
               } else {
                  list.add(sanitize(value));
               }
            }
         }
      } catch (Throwable var13) {
         logger.warning("readInputStreamList: Failed to read provided stream");
      } finally {
         if (null != reader) {
            try {
               reader.close();
            } catch (Throwable var12) {
            }
         }

      }

      return (String[])list.toArray(new String[list.size()]);
   }

   public static void _loadFromClasspath(FileUtils base, List<URL> jarList, ClassLoader classLoader) {
      PerformanceTimer timer = new PerformanceTimer();
      timer.event("create filters");
      Options options = SystemInstance.get().getOptions();
      String include = "";
      String exclude = "";
      PatternFilter classpathInclude = new PatternFilter(options.get("openejb.deployments.classpath.include", ".*"));
      PatternFilter classpathExclude = new PatternFilter(options.get("openejb.deployments.classpath.exclude", ""));
      Filter classpathFilter = new ExcludeIncludeFilter(classpathInclude, classpathExclude);
      PatternFilter packageInclude = new PatternFilter(options.get("openejb.deployments.package.include", ".*"));
      PatternFilter packageExclude = new PatternFilter(options.get("openejb.deployments.package.exclude", ""));
      IncludeExcludeFilter packageFilter;
      if (classpathInclude.getPattern().pattern().equals(".*") && packageInclude.getPattern().pattern().equals(".*")) {
         timer.event("callers");
         Set<String> callers = callers();
         timer.event("parse packages");
         callers.size();
         Set<String> packages = new HashSet();

         for(String caller : callers) {
            String[] parts = caller.split("\\.");
            if (parts.length > 2) {
               parts = new String[]{parts[0], parts[1]};
            }

            packages.add(Join.join(".", (Object[])parts));
         }

         Filter includes = Filters.packages((String[])packages.toArray(new String[packages.size()]));
         packageFilter = new IncludeExcludeFilter(includes, packageExclude);
      } else {
         packageFilter = new IncludeExcludeFilter(packageInclude, packageExclude);
      }

      timer.event("urlset");
      Set<RequireDescriptors> requireDescriptors = options.getAll("openejb.deployments.classpath.require.descriptor", new RequireDescriptors[]{RequireDescriptors.CLIENT});

      try {
         UrlSet urlSet = new UrlSet(classLoader);
         timer.event("exclude system urls");
         urlSet = URLs.cullSystemJars(urlSet);
         timer.event("classpath filter");
         UrlSet beforeFiltering = urlSet;
         urlSet = urlSet.filter(classpathFilter);
         if (urlSet.size() == 0) {
            String message = String.format("Classpath Include/Exclude resulted in zero URLs.  There were %s possible URLs before filtering and 0 after: include=\"%s\", exclude=\"%s\"", beforeFiltering.size(), "", "");
            logger.error(message);
            logger.info("Eligible Classpath before filtering:");

            for(URL url : beforeFiltering) {
               logger.info(String.format("   %s", url.toExternalForm()));
            }
         }

         boolean userSuppliedClasspathFilter = beforeFiltering.size() != urlSet.size();
         if (!userSuppliedClasspathFilter) {
            logger.info("Applying buildin classpath excludes");
            timer.event("buildin excludes");
            urlSet = applyBuiltinExcludes(urlSet);
         }

         DeploymentsResolver.processUrls("NewLoaderLogic1", urlSet.getUrls(), classLoader, EnumSet.allOf(RequireDescriptors.class), base, jarList);
         timer.event("package filter");
         urlSet = filterArchives(packageFilter, classLoader, urlSet);
         timer.event("process urls");
         List<URL> urls = urlSet.getUrls();
         long begin = System.currentTimeMillis();
         DeploymentsResolver.processUrls("NewLoaderLogic2", urls, classLoader, requireDescriptors, base, jarList);
         long end = System.currentTimeMillis();
         long time = end - begin;
         timer.stop(System.out);
         UrlSet unchecked = new UrlSet(new URL[0]);
         DeploymentsResolver.processUrls("NewLoaderLogic3", unchecked.getUrls(), classLoader, EnumSet.allOf(RequireDescriptors.class), base, jarList);
         if (logger.isDebugEnabled()) {
            int urlCount = urlSet.getUrls().size() + unchecked.getUrls().size();
            logger.debug("URLs after filtering: " + urlCount);

            for(URL url : urlSet.getUrls()) {
               logger.debug("Annotations path: " + url);
            }

            for(URL url : unchecked.getUrls()) {
               logger.debug("Descriptors path: " + url);
            }
         }

         if (urls.size() == 0) {
            return;
         }

         if (time < 1000L) {
            logger.debug("Searched " + urls.size() + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urls.size() + " milliseconds per url.");
         } else if (time >= 4000L && urls.size() >= 3) {
            if (time < 10000L) {
               logger.warning("Searched " + urls.size() + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urls.size() + " milliseconds per url.");
               logger.warning("Consider adjusting your openejb.deployments.classpath.exclude and openejb.deployments.classpath.include settings.  Current settings: exclude='', include=''");
            } else {
               logger.fatal("Searched " + urls.size() + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urls.size() + " milliseconds per url.  TOO LONG!");
               logger.fatal("ADJUST THE EXCLUDE/INCLUDE!!!.  Current settings: openejb.deployments.classpath.exclude='', openejb.deployments.classpath.include=''");
               List<String> list = new ArrayList();

               for(URL url : urls) {
                  list.add(url.toExternalForm());
               }

               Collections.sort(list);

               for(String url : list) {
                  logger.info("Matched: " + url);
               }
            }
         } else {
            logger.info("Searched " + urls.size() + " classpath urls in " + time + " milliseconds.  Average " + time / (long)urls.size() + " milliseconds per url.");
         }
      } catch (IOException e1) {
         e1.printStackTrace();
         logger.warning("Unable to search classpath for modules: Received Exception: " + e1.getClass().getName() + " " + e1.getMessage(), (Throwable)e1);
      }

   }

   static {
      logger = DeploymentLoader.logger;
      ADDITIONAL_EXCLUDES = SystemInstance.get().getOptions().get("openejb.additional.exclude", (String)null);
      ADDITIONAL_INCLUDE = SystemInstance.get().getOptions().get("openejb.additional.include", (String)null);
   }

   public static class OptimizedExclusionFilter implements Filter {
      private final Set<String> included = new HashSet();

      public OptimizedExclusionFilter(String[] exclusions) {
         this.included.addAll(Arrays.asList(exclusions));

         for(String e : exclusions) {
            if (e.endsWith("-")) {
               this.included.add(e.substring(0, e.length() - 1) + ".jar");
            }
         }

      }

      public boolean accept(String name) {
         for(int i = 1; i <= name.length(); ++i) {
            if (this.included.contains(name.substring(0, i))) {
               return true;
            }
         }

         return false;
      }
   }
}
