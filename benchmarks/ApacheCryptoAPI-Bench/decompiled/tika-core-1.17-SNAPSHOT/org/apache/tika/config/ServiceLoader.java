package org.apache.tika.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ServiceLoader {
   private static volatile ClassLoader contextClassLoader = null;
   private static final Map<Object, RankedService> services = new HashMap();
   private final ClassLoader loader;
   private final LoadErrorHandler handler;
   private final InitializableProblemHandler initializableProblemHandler;
   private final boolean dynamic;
   private static final Pattern COMMENT = Pattern.compile("#.*");
   private static final Pattern WHITESPACE = Pattern.compile("\\s+");

   static ClassLoader getContextClassLoader() {
      ClassLoader loader = contextClassLoader;
      if (loader == null) {
         loader = ServiceLoader.class.getClassLoader();
      }

      if (loader == null) {
         loader = ClassLoader.getSystemClassLoader();
      }

      return loader;
   }

   public static void setContextClassLoader(ClassLoader loader) {
      contextClassLoader = loader;
   }

   static void addService(Object reference, Object service, int rank) {
      synchronized(services) {
         services.put(reference, new RankedService(service, rank));
      }
   }

   static Object removeService(Object reference) {
      synchronized(services) {
         return services.remove(reference);
      }
   }

   public ServiceLoader(ClassLoader loader, LoadErrorHandler handler, InitializableProblemHandler initializableProblemHandler, boolean dynamic) {
      this.loader = loader;
      this.handler = handler;
      this.initializableProblemHandler = initializableProblemHandler;
      this.dynamic = dynamic;
   }

   public ServiceLoader(ClassLoader loader, LoadErrorHandler handler, boolean dynamic) {
      this(loader, handler, InitializableProblemHandler.WARN, dynamic);
   }

   public ServiceLoader(ClassLoader loader, LoadErrorHandler handler) {
      this(loader, handler, false);
   }

   public ServiceLoader(ClassLoader loader) {
      this(loader, Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE);
   }

   public ServiceLoader() {
      this(getContextClassLoader(), Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE, true);
   }

   public boolean isDynamic() {
      return this.dynamic;
   }

   public LoadErrorHandler getLoadErrorHandler() {
      return this.handler;
   }

   public InitializableProblemHandler getInitializableProblemHandler() {
      return this.initializableProblemHandler;
   }

   public InputStream getResourceAsStream(String name) {
      return this.loader != null ? this.loader.getResourceAsStream(name) : null;
   }

   public ClassLoader getLoader() {
      return this.loader;
   }

   public <T> Class<? extends T> getServiceClass(Class<T> iface, String name) throws ClassNotFoundException {
      if (this.loader == null) {
         throw new ClassNotFoundException("Service class " + name + " is not available");
      } else {
         Class<?> klass = Class.forName(name, true, this.loader);
         if (klass.isInterface()) {
            throw new ClassNotFoundException("Service class " + name + " is an interface");
         } else if (!iface.isAssignableFrom(klass)) {
            throw new ClassNotFoundException("Service class " + name + " does not implement " + iface.getName());
         } else {
            return klass;
         }
      }
   }

   public Enumeration<URL> findServiceResources(String filePattern) {
      try {
         Enumeration<URL> resources = this.loader.getResources(filePattern);
         return resources;
      } catch (IOException var4) {
         List<URL> empty = Collections.emptyList();
         return Collections.enumeration(empty);
      }
   }

   public <T> List<T> loadServiceProviders(Class<T> iface) {
      List<T> providers = new ArrayList();
      providers.addAll(this.loadDynamicServiceProviders(iface));
      providers.addAll(this.loadStaticServiceProviders(iface));
      return providers;
   }

   public <T> List<T> loadDynamicServiceProviders(Class<T> iface) {
      if (this.dynamic) {
         synchronized(services) {
            List<RankedService> list = new ArrayList(services.values());
            Collections.sort(list);
            List<T> providers = new ArrayList(list.size());

            for(RankedService service : list) {
               if (service.isInstanceOf(iface)) {
                  providers.add(service.service);
               }
            }

            return providers;
         }
      } else {
         return new ArrayList(0);
      }
   }

   protected <T> List<String> identifyStaticServiceProviders(Class<T> iface) {
      List<String> names = new ArrayList();
      if (this.loader != null) {
         String serviceName = iface.getName();
         Enumeration<URL> resources = this.findServiceResources("META-INF/services/" + serviceName);

         for(URL resource : Collections.list(resources)) {
            try {
               this.collectServiceClassNames(resource, names);
            } catch (IOException e) {
               this.handler.handleLoadError(serviceName, e);
            }
         }
      }

      return names;
   }

   public <T> List<T> loadStaticServiceProviders(Class<T> iface) {
      List<T> providers = new ArrayList();
      if (this.loader != null) {
         for(String name : this.identifyStaticServiceProviders(iface)) {
            try {
               Class<?> klass = this.loader.loadClass(name);
               if (iface.isAssignableFrom(klass)) {
                  T instance = (T)klass.newInstance();
                  if (instance instanceof Initializable) {
                     ((Initializable)instance).checkInitialization(this.initializableProblemHandler);
                  }

                  providers.add(instance);
               }
            } catch (Throwable t) {
               this.handler.handleLoadError(name, t);
            }
         }
      }

      return providers;
   }

   private void collectServiceClassNames(URL resource, Collection<String> names) throws IOException {
      InputStream stream = resource.openStream();
      Throwable var4 = null;

      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

         for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            line = COMMENT.matcher(line).replaceFirst("");
            line = WHITESPACE.matcher(line).replaceAll("");
            if (line.length() > 0) {
               names.add(line);
            }
         }
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (stream != null) {
            if (var4 != null) {
               try {
                  stream.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               stream.close();
            }
         }

      }

   }

   private static class RankedService implements Comparable<RankedService> {
      private Object service;
      private int rank;

      public RankedService(Object service, int rank) {
         this.service = service;
         this.rank = rank;
      }

      public boolean isInstanceOf(Class<?> iface) {
         return iface.isAssignableFrom(this.service.getClass());
      }

      public int compareTo(RankedService that) {
         return that.rank - this.rank;
      }
   }
}
