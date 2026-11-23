package org.apache.openejb.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.openejb.loader.JarLocation;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.archive.CompositeArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.filter.Filter;

public class WebappAggregatedArchive implements Archive, ScanConstants {
   private final Map<URL, List<String>> map;
   private ScanUtil.ScanHandler handler;
   private boolean scanXmlExists;
   private Archive archive;

   public WebappAggregatedArchive(Module module, Iterable<URL> urls, Filter filter) {
      this.map = new HashMap();
      URL scanXml = (URL)module.getAltDDs().get("scan.xml");
      if (scanXml != null) {
         try {
            this.handler = ScanUtil.read(scanXml);
            this.scanXmlExists = true;
            if ((this.handler.getPackages() == null || this.handler.getPackages().isEmpty()) && this.handler.getClasses() != null && !this.handler.getClasses().isEmpty() && this.handler.isOptimized()) {
               Collection<Class<?>> loaded = new ArrayList(this.handler.getClasses().size());

               for(String clazz : this.handler.getClasses()) {
                  try {
                     Class<?> aClass = module.getClassLoader().loadClass(clazz);
                     loaded.add(aClass);
                     URL jar = JarLocation.jarLocation(aClass).toURI().toURL();
                     List<String> list = (List)this.map.get(jar);
                     if (list == null) {
                        list = new ArrayList();
                        this.map.put(jar, list);
                     }

                     list.add(clazz);
                  } catch (ClassNotFoundException e) {
                     throw new IllegalArgumentException(e);
                  }
               }

               this.archive = new ClassesArchive((Class[])loaded.toArray(new Class[loaded.size()]));
               return;
            }
         } catch (IOException var12) {
         }
      }

      this.archive = new CompositeArchive(this.doScan(module.getClassLoader(), urls, filter));
   }

   private List<Archive> doScan(ClassLoader loader, Iterable<URL> urls, Filter filter) {
      List<Archive> archives = new ArrayList();

      for(URL url : urls) {
         List<String> classes = new ArrayList();
         Archive archive = new FilteredArchive(new ConfigurableClasspathArchive(loader, Collections.singletonList(url)), new ScanXmlSaverFilter(this.scanXmlExists, this.handler, classes, filter));
         this.map.put(url, classes);
         archives.add(archive);
      }

      return archives;
   }

   public WebappAggregatedArchive(Archive delegate, Iterable<URL> urls) {
      this.map = new HashMap();
      List<Archive> archives = this.doScan(Thread.currentThread().getContextClassLoader(), urls, (Filter)null);
      List<String> classes = new ArrayList();
      Archive archive = new FilteredArchive(delegate, new ScanXmlSaverFilter(this.scanXmlExists, this.handler, classes, (Filter)null));

      try {
         this.map.put(new URL("jar:file://!/META-INF/beans.xml"), classes);
      } catch (MalformedURLException e) {
         throw new IllegalArgumentException(e);
      }

      archives.add(archive);
      this.archive = new CompositeArchive(archives);
   }

   public WebappAggregatedArchive(Module module, Iterable<URL> urls) {
      this((Module)module, urls, (Filter)null);
   }

   public WebappAggregatedArchive(ClassLoader classLoader, Map<String, Object> altDDs, Collection<URL> xmls) {
      this((Module)(new ConfigurableClasspathArchive.FakeModule(classLoader, altDDs)), xmls);
   }

   public Map<URL, List<String>> getClassesMap() {
      return this.map;
   }

   public InputStream getBytecode(String className) throws IOException, ClassNotFoundException {
      return this.archive.getBytecode(className);
   }

   public Class<?> loadClass(String className) throws ClassNotFoundException {
      return this.archive.loadClass(className);
   }

   public Iterator<Archive.Entry> iterator() {
      return this.archive.iterator();
   }

   public static class ScanXmlSaverFilter implements Filter {
      private final boolean scanXmlExists;
      private final ScanUtil.ScanHandler handler;
      private final List<String> classes;
      private final Filter otherFilter;

      public ScanXmlSaverFilter(boolean scanXmlExists, ScanUtil.ScanHandler handler, List<String> classes, Filter otherFilter) {
         this.scanXmlExists = scanXmlExists;
         this.handler = handler;
         this.classes = classes;
         this.otherFilter = otherFilter;
      }

      public boolean accept(String name) {
         boolean accept = this.otherFilter == null || this.otherFilter.accept(name);
         if (this.scanXmlExists) {
            for(String packageName : this.handler.getPackages()) {
               if (name.startsWith(packageName) && accept) {
                  this.classes.add(name);
                  return true;
               }
            }

            for(String className : this.handler.getClasses()) {
               if (className.equals(name) && accept) {
                  this.classes.add(name);
                  return true;
               }
            }

            return false;
         } else {
            if (accept) {
               this.classes.add(name);
            }

            return accept;
         }
      }
   }
}
