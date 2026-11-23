package org.apache.meecrowave.openwebbeans;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.ServletContext;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.logging.tomcat.LogFacade;
import org.apache.openwebbeans.se.CDISeScannerService;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.corespi.scanner.xbean.CdiArchive;
import org.apache.webbeans.corespi.scanner.xbean.OwbAnnotationFinder;
import org.apache.webbeans.spi.BeanArchiveService;
import org.apache.webbeans.util.WebBeansUtil;
import org.apache.webbeans.web.scanner.WebScannerService;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.filter.Filter;

public class OWBTomcatWebScannerService extends WebScannerService {
   private final LogFacade logger;
   private final CDISeScannerService delegate;
   protected JarScanFilter filter;
   private String jreBase;
   private final Set<String> urls;
   private String docBase;
   private String shared;
   private Consumer<File> fileVisitor;

   public OWBTomcatWebScannerService() {
      this((CDISeScannerService)null);
   }

   public OWBTomcatWebScannerService(CDISeScannerService delegate) {
      this.logger = new LogFacade(OWBTomcatWebScannerService.class.getName());
      this.urls = new HashSet();
      this.delegate = delegate;
   }

   public void init(Object context) {
      if (this.delegate != null) {
         this.delegate.init(context);
      }

   }

   public OwbAnnotationFinder getFinder() {
      return this.delegate != null ? this.delegate.getFinder() : super.getFinder();
   }

   public Map<BeanArchiveService.BeanArchiveInformation, Set<Class<?>>> getBeanClassesPerBda() {
      return this.delegate != null ? this.delegate.getBeanClassesPerBda() : super.getBeanClassesPerBda();
   }

   public void release() {
      if (this.delegate != null) {
         this.delegate.release();
      } else {
         super.release();
      }

   }

   public Set<Class<?>> getBeanClasses() {
      return this.delegate != null ? this.delegate.getBeanClasses() : super.getBeanClasses();
   }

   public void scan() {
      if (this.delegate != null) {
         if (this.getFinder() == null) {
            this.delegate.scan();
         }

         if (this.finder == null) {
            this.finder = this.getFinder();
         }
      }

      if (this.finder == null) {
         super.scan();
         this.scanGroovy(WebBeansUtil.getCurrentClassLoader());
         if (!this.urls.isEmpty()) {
            this.logger.info("OpenWebBeans scanning:");
            String m2 = (new File(System.getProperty("user.home", "."), ".m2/repository")).getAbsolutePath();
            String base = (String)Optional.ofNullable(this.docBase).orElse("$$$");
            String sharedBase = (String)Optional.ofNullable(this.shared).orElse("$$$");
            String runnerBase = (String)Optional.ofNullable(System.getProperty("meecrowave.base")).orElse("$$$");
            String runnerHome = (String)Optional.ofNullable(System.getProperty("meecrowave.home")).orElse("$$$");
            this.urls.stream().map((u) -> {
               String shownValue = u.replace("file://", "").replace("file:", "").replace("jar:", "").replace("!/META-INF/beans.xml", "").replace("!/", "").replace("META-INF/beans.xml", "");
               if (shownValue.startsWith(m2)) {
                  shownValue = "${maven}/" + shownValue.substring(shownValue.replace(File.separatorChar, '/').lastIndexOf(47) + 1);
               } else if (shownValue.startsWith(base)) {
                  shownValue = "${app}" + shownValue.replace(base, "");
               } else if (shownValue.startsWith(sharedBase)) {
                  shownValue = "${shared}" + shownValue.replace(sharedBase, "");
               } else if (shownValue.startsWith(runnerBase)) {
                  shownValue = "${base}" + shownValue.replace(runnerBase, "");
               } else if (shownValue.startsWith(runnerHome)) {
                  shownValue = "${home}" + shownValue.replace(runnerHome, "");
               }

               return shownValue;
            }).sorted().forEach((v) -> this.logger.info("    " + v));
            if (this.fileVisitor != null) {
               this.urls.stream().filter(this::isFile).map(this::toFile).filter(File::isDirectory).forEach(this.fileVisitor);
            }
         }

         this.urls.clear();
         this.filter = null;
         this.docBase = null;
         this.shared = null;
      }
   }

   private File toFile(String url) {
      try {
         return new File((new URL(url)).getFile());
      } catch (MalformedURLException var3) {
         return new File(url.substring("file://".length(), url.length()));
      }
   }

   private boolean isFile(String url) {
      return url.startsWith("file:") && !url.endsWith("!/") && !url.endsWith("!/META-INF/beans.xml");
   }

   private void scanGroovy(ClassLoader currentClassLoader) {
      if (currentClassLoader != null && currentClassLoader.getClass().getName().equals("groovy.lang.GroovyClassLoader")) {
         try {
            Class<?>[] getLoadedClasses = (Class[])Class[].class.cast(currentClassLoader.getClass().getMethod("getLoadedClasses").invoke(currentClassLoader));
            this.addClassesToDefault(getLoadedClasses);
         } catch (Exception e) {
            (new LogFacade(OWBTomcatWebScannerService.class.getName())).warn(e.getMessage());
         }

      }
   }

   private void addClassesToDefault(Class<?>[] all) throws Exception {
      if (all != null && all.length != 0) {
         Field linking = AnnotationFinder.class.getDeclaredField("linking");
         Method readClassDef = AnnotationFinder.class.getDeclaredMethod("readClassDef", Class.class);
         if (!readClassDef.isAccessible()) {
            readClassDef.setAccessible(true);
         }

         if (!linking.isAccessible()) {
            linking.setAccessible(true);
         }

         URI uri = URI.create("jar:file://!/");
         URL url = uri.toURL();
         String key = uri.toASCIIString();
         CdiArchive.FoundClasses foundClasses = (CdiArchive.FoundClasses)this.archive.classesByUrl().get(key);
         if (foundClasses == null) {
            BeanArchiveService beanArchiveService = this.webBeansContext().getBeanArchiveService();
            foundClasses = (CdiArchive.FoundClasses)CdiArchive.FoundClasses.class.cast(CdiArchive.FoundClasses.class.getConstructor(CdiArchive.class, URL.class, Collection.class, BeanArchiveService.BeanArchiveInformation.class).newInstance(null, url, new HashSet(), beanArchiveService.getBeanArchiveInformation(url)));
            this.archive.classesByUrl().put(key, foundClasses);
         }

         foundClasses.getClassNames().addAll((Collection)Stream.of(all).map(Class::getName).collect(Collectors.toSet()));

         try {
            linking.set(this.finder, true);
            Stream.of(all).forEach((c) -> {
               try {
                  readClassDef.invoke(this.finder, c);
               } catch (IllegalAccessException e) {
                  throw new IllegalStateException(e);
               } catch (InvocationTargetException e) {
                  throw new IllegalStateException(e.getCause());
               }
            });
         } finally {
            try {
               linking.set(this.finder, false);
            } catch (IllegalAccessException var14) {
            }

         }

      }
   }

   protected void filterExcludedJars(Set<URL> classPathUrls) {
      String jreBaseTmp;
      try {
         jreBaseTmp = (new File(System.getProperty("java.home"))).toURI().toURL().toExternalForm();
      } catch (MalformedURLException var4) {
         jreBaseTmp = System.getProperty("java.home");
      }

      this.jreBase = jreBaseTmp;
      super.filterExcludedJars(classPathUrls);
   }

   protected int isExcludedJar(String path) {
      if (!path.startsWith(this.jreBase) && (!path.startsWith("jar:") || path.indexOf(this.jreBase) != 4)) {
         if (path.startsWith("jar:file:") && path.endsWith(".jar!/")) {
            int lastSep = path.substring(0, path.length() - 2).lastIndexOf(47);
            if (lastSep > 0) {
               return this.filter != null && this.filter.check(JarScanType.PLUGGABILITY, path.substring(lastSep + 1, path.length() - 2)) ? -1 : path.indexOf(".jar") - 1;
            }
         }

         int filenameIdx = path.replace(File.separatorChar, '/').replace("!/", "").lastIndexOf(47) + 1;
         if (filenameIdx >= 0 && filenameIdx < path.length()) {
            return this.filter != null && this.filter.check(JarScanType.PLUGGABILITY, path.substring(filenameIdx)) ? -1 : path.indexOf(".jar") - 1;
         } else {
            return -1;
         }
      } else {
         return this.jreBase.length();
      }
   }

   public void setFilter(JarScanFilter filter, ServletContext ctx) {
      this.filter = filter;
      super.init(ctx);
      Meecrowave.Builder config = (Meecrowave.Builder)Meecrowave.Builder.class.cast(((ServletContext)ServletContext.class.cast(ctx)).getAttribute("meecrowave.configuration"));
      if (this.filter == null) {
         this.filter = new KnownJarsFilter(config);
      }

      Filter userFilter = (Filter)this.webBeansContext().getService(Filter.class);
      if (KnowClassesFilter.class.isInstance(userFilter)) {
         ((KnowClassesFilter)KnowClassesFilter.class.cast(userFilter)).init(config);
      }

   }

   protected void addWebBeansXmlLocation(URL beanArchiveUrl) {
      String url = beanArchiveUrl.toExternalForm();
      if (this.urls.add(Optional.of(url).map((s) -> s.startsWith("jar:") && s.endsWith("!/META-INF/beans.xml") ? s.substring("jar:".length(), s.length() - "!/META-INF/beans.xml".length()) : s).get())) {
         super.doAddWebBeansXmlLocation(beanArchiveUrl);
      }

   }

   public void setShared(String shared) {
      this.shared = (String)Optional.ofNullable(shared).map(File::new).filter(File::isDirectory).map(File::getAbsolutePath).orElse((Object)null);
   }

   public void setDocBase(String docBase) {
      this.docBase = docBase;
   }

   public void setFileVisitor(Consumer<File> fileVisitor) {
      this.fileVisitor = fileVisitor;
   }

   protected WebBeansContext webBeansContext() {
      return WebBeansContext.getInstance();
   }
}
