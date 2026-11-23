package org.apache.catalina.startup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.WebResource;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.logging.tomcat.LogFacade;
import org.apache.meecrowave.openwebbeans.OWBTomcatWebScannerService;
import org.apache.meecrowave.watching.ReloadOnChangeController;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.corespi.scanner.xbean.CdiArchive;
import org.apache.webbeans.corespi.scanner.xbean.OwbAnnotationFinder;
import org.xml.sax.InputSource;

public class MeecrowaveContextConfig extends ContextConfig {
   private static final byte[] DEFAULT_WEB_XML;
   private final Meecrowave.Builder configuration;
   private final Map<String, Collection<Class<?>>> webClasses = new HashMap();
   private final boolean fixDocBase;
   private final ServletContainerInitializer intializer;
   private OwbAnnotationFinder finder;
   private ReloadOnChangeController watcher;

   public MeecrowaveContextConfig(Meecrowave.Builder configuration, boolean fixDocBase, ServletContainerInitializer intializer) {
      this.configuration = configuration;
      this.fixDocBase = fixDocBase;
      this.intializer = intializer;
   }

   protected void fixDocBase() throws IOException {
      if (this.fixDocBase) {
         super.fixDocBase();
      }
   }

   protected void webConfig() {
      if (this.context.getServletContext().getAttribute("meecrowave.configuration") == null) {
         this.context.getServletContext().setAttribute("meecrowave.configuration", this.configuration);
         this.context.addServletContainerInitializer(this.intializer, Collections.emptySet());
      }

      if (!this.configuration.isTomcatScanning()) {
         super.webConfig();
      } else {
         ClassLoader loader = this.context.getLoader().getClassLoader();
         Thread thread = Thread.currentThread();
         ClassLoader old = thread.getContextClassLoader();
         thread.setContextClassLoader(loader);

         try {
            OWBTomcatWebScannerService scannerService = (OWBTomcatWebScannerService)OWBTomcatWebScannerService.class.cast(WebBeansContext.getInstance().getScannerService());
            scannerService.setFilter((JarScanFilter)Optional.ofNullable(this.context.getJarScanner()).map(JarScanner::getJarScanFilter).orElse((Object)null), this.context.getServletContext());
            scannerService.setDocBase(this.context.getDocBase());
            scannerService.setShared(this.configuration.getSharedLibraries());
            if (this.configuration.getWatcherBouncing() > 0) {
               this.watcher = new ReloadOnChangeController(this.context, this.configuration.getWatcherBouncing());
               scannerService.setFileVisitor((f) -> this.watcher.register(f));
            }

            scannerService.scan();
            this.finder = scannerService.getFinder();
            this.finder.link();
            CdiArchive archive = (CdiArchive)CdiArchive.class.cast(this.finder.getArchive());
            Stream.of(WebServlet.class, WebFilter.class, WebListener.class).forEach((marker) -> this.finder.findAnnotatedClasses(marker).stream().filter((c) -> !Modifier.isAbstract(c.getModifiers()) && Modifier.isPublic(c.getModifiers())).forEach((webComponent) -> ((Collection)this.webClasses.computeIfAbsent(((Map.Entry)archive.classesByUrl().entrySet().stream().filter((e) -> ((CdiArchive.FoundClasses)e.getValue()).getClassNames().contains(webComponent.getName())).findFirst().get()).getKey(), (k) -> new HashSet())).add(webComponent)));
         } finally {
            thread.setContextClassLoader(old);
         }

         try {
            super.webConfig();
         } finally {
            this.webClasses.clear();
            this.finder = null;
         }

      }
   }

   public void lifecycleEvent(LifecycleEvent event) {
      super.lifecycleEvent(event);
      if (this.watcher != null && this.watcher.shouldRun() && Context.class.cast(event.getLifecycle()) == this.context) {
         if ("after_start".equals(event.getType())) {
            this.watcher.start();
         } else if ("before_stop".equals(event.getType())) {
            this.watcher.close();
         }
      }

   }

   protected InputSource getGlobalWebXmlSource() {
      return (InputSource)Optional.ofNullable(super.getGlobalWebXmlSource()).orElse(new InputSource(new ByteArrayInputStream(DEFAULT_WEB_XML)));
   }

   protected void processAnnotationsWebResource(WebResource webResource, WebXml fragment, boolean handlesTypesOnly, Map<String, ContextConfig.JavaClassCacheEntry> javaClassCache) {
      if (this.configuration.isTomcatScanning()) {
         this.webClasses.keySet().stream().filter((k) -> k.endsWith("/WEB-INF/classes")).forEach((k) -> this.processClasses(fragment, handlesTypesOnly, javaClassCache, k));
      }

   }

   protected void processAnnotationsUrl(URL url, WebXml fragment, boolean handlesTypesOnly, Map<String, ContextConfig.JavaClassCacheEntry> javaClassCache) {
      if (this.configuration.isTomcatScanning()) {
         this.processClasses(fragment, handlesTypesOnly, javaClassCache, url.toExternalForm());
      }
   }

   protected void processServletContainerInitializers() {
      if (this.configuration.isTomcatScanning()) {
         try {
            (new WebappServiceLoader(this.context)).load(ServletContainerInitializer.class).forEach((sci) -> {
               Set<Class<?>> classes = new HashSet();
               this.initializerClassMap.put(sci, classes);

               HandlesTypes ht;
               try {
                  ht = (HandlesTypes)sci.getClass().getAnnotation(HandlesTypes.class);
               } catch (NoClassDefFoundError | Exception var5) {
                  return;
               }

               if (ht != null) {
                  Stream.of(ht.value()).forEach((t) -> {
                     if (t.isAnnotation()) {
                        Class<? extends Annotation> annotation = (Class)Class.class.cast(t);
                        this.finder.findAnnotatedClasses(annotation).forEach(classes::add);
                     } else if (t.isInterface()) {
                        this.finder.findImplementations(t).forEach(classes::add);
                     } else {
                        this.finder.findSubclasses(t).forEach(classes::add);
                     }

                  });
               }
            });
         } catch (IOException var2) {
            this.ok = false;
         }

      }
   }

   private void processClasses(WebXml fragment, boolean handlesTypesOnly, Map<String, ContextConfig.JavaClassCacheEntry> javaClassCache, String key) {
      Collection<Class<?>> classes = (Collection)this.webClasses.remove(key);
      if (classes == null && key.endsWith(".jar") && key.startsWith("file:")) {
         classes = (Collection)this.webClasses.remove("jar:" + key + "!/");
      }

      if (classes != null && !classes.isEmpty()) {
         ClassLoader loader = this.context.getLoader().getClassLoader();
         classes.forEach((c) -> {
            try {
               InputStream stream = loader.getResourceAsStream(c.getName().replace('.', '/') + ".class");
               Throwable var7 = null;

               try {
                  super.processAnnotationsStream(stream, fragment, handlesTypesOnly, javaClassCache);
               } catch (Throwable var17) {
                  var7 = var17;
                  throw var17;
               } finally {
                  if (stream != null) {
                     if (var7 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var16) {
                           var7.addSuppressed(var16);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (IOException var19) {
               (new LogFacade(MeecrowaveContextConfig.class.getName())).error("Can't parse " + c);
            }

         });
      }

   }

   static {
      DEFAULT_WEB_XML = "<web-app version=\"3.1\" />".getBytes(StandardCharsets.UTF_8);
   }
}
