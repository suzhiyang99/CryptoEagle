package org.apache.openejb.config;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.core.EmptyResourcesClassLoader;
import org.apache.openejb.loader.SystemInstance;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.archive.ClasspathArchive;
import org.apache.xbean.finder.archive.CompositeArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.FilterList;
import org.apache.xbean.finder.filter.PackageFilter;

public class ConfigurableClasspathArchive extends CompositeArchive implements ScanConstants {
   public ConfigurableClasspathArchive(Module module, URL... urls) {
      this((Module)module, (Iterable)Arrays.asList(urls));
   }

   public ConfigurableClasspathArchive(Module module, Iterable<URL> urls) {
      this(module, false, urls);
   }

   public ConfigurableClasspathArchive(ClassLoader loader, Iterable<URL> urls) {
      this(loader, false, urls);
   }

   public ConfigurableClasspathArchive(ClassLoader loader, boolean forceDescriptor, Iterable<URL> urls) {
      this((Module)(new FakeModule(loader)), forceDescriptor, urls);
   }

   public ConfigurableClasspathArchive(ClassLoader loader, URL url) {
      this((Module)(new FakeModule(loader)), (Iterable)Arrays.asList(url));
   }

   public ConfigurableClasspathArchive(Module module, boolean forceDescriptor, Iterable<URL> urls) {
      super(archive(module, urls, forceDescriptor));
   }

   public static List<Archive> archive(Module module, Iterable<URL> urls, boolean forceDescriptor) {
      List<Archive> archives = new ArrayList();

      for(URL location : urls) {
         try {
            archives.add(archive(module, location, forceDescriptor));
         } catch (Exception var7) {
         }
      }

      return archives;
   }

   public static Archive archive(Module module, URL location, boolean forceDescriptor) {
      ClassLoader loader = module.getClassLoader();
      String name = "META-INF/" + name();

      try {
         URL scanXml = (new URLClassLoader(new URL[]{location}, new EmptyResourcesClassLoader())).getResource(name);
         if (scanXml == null && !forceDescriptor) {
            return ClasspathArchive.archive(loader, location);
         } else if (scanXml == null) {
            return new ClassesArchive(new Class[0]);
         } else {
            ScanUtil.ScanHandler scan = ScanUtil.read(scanXml);
            Archive packageArchive = packageArchive(scan.getPackages(), loader, location);
            Archive classesArchive = classesArchive(scan.getPackages(), scan.getClasses(), loader);
            if (packageArchive != null && classesArchive != null) {
               return new CompositeArchive(new Archive[]{classesArchive, packageArchive});
            } else {
               return packageArchive != null ? packageArchive : classesArchive;
            }
         }
      } catch (IOException var9) {
         return (Archive)(forceDescriptor ? new ClassesArchive(new Class[0]) : ClasspathArchive.archive(loader, location));
      }
   }

   private static String name() {
      return SystemInstance.get().getProperty("openejb.scan.xml.name", "scan.xml");
   }

   public static Archive packageArchive(Set<String> packageNames, ClassLoader loader, URL url) {
      return !packageNames.isEmpty() ? new FilteredArchive(ClasspathArchive.archive(loader, url), filters(packageNames)) : null;
   }

   private static Filter filters(Set<String> packageNames) {
      List<Filter> filters = new ArrayList();

      for(String packageName : packageNames) {
         filters.add(new PackageFilter(packageName));
      }

      return new FilterList(filters);
   }

   public static Archive classesArchive(Set<String> packages, Set<String> classnames, ClassLoader loader) {
      Class<?>[] classes = new Class[classnames.size()];
      int i = 0;

      for(String clazz : classnames) {
         if (packages == null || !clazzInPackage(packages, clazz)) {
            try {
               classes[i++] = loader.loadClass(clazz);
            } catch (ClassNotFoundException var8) {
            }
         }
      }

      if (i != classes.length) {
         Class<?>[] updatedClasses = new Class[i];
         System.arraycopy(classes, 0, updatedClasses, 0, i);
         classes = updatedClasses;
      }

      return new ClassesArchive(classes);
   }

   private static boolean clazzInPackage(Collection<String> packagename, String clazz) {
      for(String str : packagename) {
         if (clazz.startsWith(str)) {
            return true;
         }
      }

      return false;
   }

   public static class FakeModule extends Module {
      public FakeModule(ClassLoader loader) {
         this(loader, (Map)null);
      }

      public FakeModule(ClassLoader loader, Map<String, Object> altDD) {
         this(loader, altDD, ConfigurableClasspathArchive.name());
      }

      public FakeModule(ClassLoader loader, Map<String, Object> altDD, String name) {
         super(false);
         this.setClassLoader(loader);
         URL scanXml;
         if (altDD == null) {
            scanXml = loader.getResource("META-INF/" + name);
         } else {
            scanXml = (URL)altDD.get(name);
         }

         if (scanXml != null) {
            this.getAltDDs().put("scan.xml", scanXml);
         }

      }
   }
}
