package org.apache.openejb.cdi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.enterprise.inject.spi.Extension;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.activemq.jms2.cdi.JMS2CDIExtension;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.service.DefaultLoaderService;
import org.apache.webbeans.spi.LoaderService;
import org.apache.webbeans.spi.plugins.OpenWebBeansPlugin;

public class OptimizedLoaderService implements LoaderService {
   private static final Logger log;
   public static final ThreadLocal<Collection<String>> ADDITIONAL_EXTENSIONS;
   private final LoaderService loaderService;
   private final Properties config;

   public OptimizedLoaderService(Properties appConfig) {
      this((LoaderService)(is("openejb.cdi.ignore-not-loadable-extensions", appConfig, SystemInstance.get().getProperties(), "false") ? new FilterableServiceLoader() : new DefaultLoaderService()), appConfig);
   }

   public OptimizedLoaderService(LoaderService loaderService, Properties appConfig) {
      this.loaderService = loaderService;
      this.config = appConfig;
   }

   public <T> List<T> load(Class<T> serviceType) {
      return this.<T>load(serviceType, Thread.currentThread().getContextClassLoader());
   }

   public <T> List<T> load(Class<T> serviceType, ClassLoader classLoader) {
      if (OpenWebBeansPlugin.class.equals(serviceType)) {
         return this.loadWebBeansPlugins(classLoader);
      } else {
         return Extension.class.equals(serviceType) ? this.loadExtensions(classLoader) : this.loaderService.load(serviceType, classLoader);
      }
   }

   protected List<? extends Extension> loadExtensions(ClassLoader classLoader) {
      List<Extension> list = this.loaderService.load(Extension.class, classLoader);
      Collection<String> additional = (Collection)ADDITIONAL_EXTENSIONS.get();
      if (additional != null) {
         for(String name : additional) {
            try {
               list.add(Extension.class.cast(classLoader.loadClass(name).newInstance()));
            } catch (Exception var7) {
            }
         }
      }

      if (this.hasJms()) {
         list.add(new JMS2CDIExtension());
      }

      Collection<Extension> extensionCopy = new ArrayList(list);
      Iterator<Extension> it = list.iterator();

      while(it.hasNext()) {
         if (it.hasNext() && this.isFiltered(extensionCopy, (Extension)it.next())) {
            it.remove();
         }
      }

      if ("true".equals(this.config.getProperty("openejb.cdi.extensions.sorted", SystemInstance.get().getProperty("openejb.cdi.extensions.sorted")))) {
         Collections.sort(list, new Comparator<Extension>() {
            public int compare(Extension o1, Extension o2) {
               int val1 = this.getVal(o1);
               int val2 = this.getVal(o1);
               return val1 == val2 ? o1.getClass().getName().compareTo(o2.getClass().getName()) : val1 - val2;
            }

            private int getVal(Extension o1) {
               String key = "openejb.cdi.extensions." + o1.getClass().getName() + ".ordinal";
               String config = OptimizedLoaderService.this.config.getProperty(key, SystemInstance.get().getProperty(key));
               return config == null ? 0 : Integer.parseInt(config.trim());
            }
         });
      }

      return list;
   }

   private boolean hasJms() {
      try {
         Thread.currentThread().getContextClassLoader().loadClass("org.apache.activemq.ra.ActiveMQManagedConnectionFactory");
         return true;
      } catch (ClassNotFoundException | NoClassDefFoundError var2) {
         return false;
      }
   }

   private boolean isFiltered(Collection<Extension> extensions, Extension next) {
      ClassLoader containerLoader = ParentClassLoaderFinder.Helper.get();
      Class<? extends Extension> extClass = next.getClass();
      String name = extClass.getName();
      String activeKey = name + ".active";
      SystemInstance systemInstance = SystemInstance.get();
      if (!is(activeKey, this.config, systemInstance.getProperties(), "true")) {
         return true;
      } else if (extClass.getClassLoader() != containerLoader) {
         return false;
      } else {
         switch (name) {
            case "org.apache.bval.cdi.BValExtension":
               for(Extension e : extensions) {
                  String en = e.getClass().getName();
                  if (en.startsWith("org.hibernate.validator.") && en.endsWith("ValidationExtension")) {
                     log.info("Skipping BVal CDI integration cause hibernate was found in the application");
                     return true;
                  }
               }
               break;
            case "org.apache.batchee.container.cdi.BatchCDIInjectionExtension":
               return "true".equals(systemInstance.getProperty("tomee.batchee.cdi.use-extension", "false"));
            case "org.apache.commons.jcs.jcache.cdi.MakeJCacheCDIInterceptorFriendly":
               String spi = "META-INF/services/javax.cache.spi.CachingProvider";

               try {
                  Enumeration<URL> appResources = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/javax.cache.spi.CachingProvider");
                  if (appResources != null && appResources.hasMoreElements()) {
                     Collection<URL> containerResources = Collections.list(containerLoader.getResources("META-INF/services/javax.cache.spi.CachingProvider"));

                     do {
                        if (!containerResources.contains(appResources.nextElement())) {
                           log.info("Skipping JCS CDI integration cause another provide was found in the application");
                           return true;
                        }
                     } while(appResources.hasMoreElements());
                  }
               } catch (Exception var13) {
               }
         }

         return false;
      }
   }

   private List<? extends OpenWebBeansPlugin> loadWebBeansPlugins(ClassLoader loader) {
      List<OpenWebBeansPlugin> list = new ArrayList(2);
      list.add(new CdiPlugin());

      try {
         Class<?> clazz = loader.loadClass("org.apache.geronimo.openejb.cdi.GeronimoWebBeansPlugin");

         try {
            list.add(OpenWebBeansPlugin.class.cast(clazz.newInstance()));
         } catch (Exception var5) {
            log.error("Unable to load OpenWebBeansPlugin: GeronimoWebBeansPlugin");
         }
      } catch (ClassNotFoundException var6) {
      }

      return list;
   }

   private static boolean is(String activeKey, Properties config, Properties fallback, String or) {
      return Boolean.parseBoolean(config.getProperty(activeKey, fallback.getProperty(activeKey, or)));
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB.createChild("cdi"), OptimizedLoaderService.class);
      ADDITIONAL_EXTENSIONS = new ThreadLocal();
   }

   private static class FilterableServiceLoader implements LoaderService {
      private static final String SERVICE_CONFIG = "META-INF/services/" + Extension.class.getName();
      private static final String FILE_ENCODING = "UTF-8";
      private List<Class<?>> foundServiceClasses;
      private ClassLoader loader;

      private FilterableServiceLoader() {
         this.foundServiceClasses = new ArrayList();
      }

      private List<Extension> loadServiceImplementations() {
         List<Class<?>> result = this.resolveServiceImplementations();
         if (result == null) {
            return Collections.emptyList();
         } else {
            List<Extension> foundServices = new ArrayList();

            for(Class<?> serviceClass : result) {
               try {
                  foundServices.add(Extension.class.cast(serviceClass.newInstance()));
               } catch (InstantiationException | IllegalAccessException | NoClassDefFoundError e) {
                  OptimizedLoaderService.log.error("Ignoring a CDI Extension, cause it can't be instantiated (" + serviceClass + "): " + ((Throwable)e).getMessage());
               }
            }

            return foundServices;
         }
      }

      private List<Class<?>> resolveServiceImplementations() {
         for(URL configFile : this.getConfigFileList()) {
            this.loadConfiguredServices(configFile);
         }

         return this.foundServiceClasses;
      }

      private List<URL> getConfigFileList() {
         List<URL> serviceFiles = new ArrayList();

         try {
            Enumeration<URL> serviceFileEnumerator = this.loader.getResources(SERVICE_CONFIG);

            while(serviceFileEnumerator.hasMoreElements()) {
               serviceFiles.add(serviceFileEnumerator.nextElement());
            }

            return serviceFiles;
         } catch (Exception e) {
            throw new IllegalStateException("Failed to load Extension configured in " + SERVICE_CONFIG, e);
         }
      }

      private void loadConfiguredServices(URL serviceFile) {
         try {
            InputStream inputStream = serviceFile.openStream();
            Throwable var3 = null;

            try {
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

               String serviceClassName;
               while((serviceClassName = bufferedReader.readLine()) != null) {
                  serviceClassName = this.extractConfiguredServiceClassName(serviceClassName);
                  if (!"".equals(serviceClassName)) {
                     this.loadService(serviceClassName);
                  }
               }
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (inputStream != null) {
                  if (var3 != null) {
                     try {
                        inputStream.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     inputStream.close();
                  }
               }

            }

         } catch (Exception e) {
            throw new IllegalStateException("Failed to process service-config: " + serviceFile, e);
         }
      }

      private String extractConfiguredServiceClassName(String currentConfigLine) {
         int startOfComment = currentConfigLine.indexOf(35);
         if (startOfComment > -1) {
            currentConfigLine = currentConfigLine.substring(0, startOfComment);
         }

         return currentConfigLine.trim();
      }

      private void loadService(String serviceClassName) {
         try {
            Class<Extension> serviceClass = this.loader.loadClass(serviceClassName);
            if (!this.foundServiceClasses.contains(serviceClass)) {
               this.foundServiceClasses.add(serviceClass);
            }
         } catch (ClassNotFoundException | NoClassDefFoundError var4) {
            OptimizedLoaderService.log.error("Ignoring " + serviceClassName + " cause it can't be loaded.");
         }

      }

      public <T> List<T> load(Class<T> serviceType) {
         throw new UnsupportedOperationException();
      }

      public <T> List<T> load(Class<T> serviceType, ClassLoader classLoader) {
         this.loader = classLoader;

         List var3;
         try {
            var3 = this.loadServiceImplementations();
         } finally {
            this.loader = null;
            this.foundServiceClasses.clear();
         }

         return var3;
      }
   }
}
