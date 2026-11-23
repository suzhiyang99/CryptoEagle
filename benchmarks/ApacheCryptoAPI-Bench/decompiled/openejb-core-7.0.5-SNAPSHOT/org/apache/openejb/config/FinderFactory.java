package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.Handler;
import org.apache.openejb.jee.HandlerChain;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.PortComponent;
import org.apache.openejb.jee.Servlet;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Classes;
import org.apache.xbean.finder.Annotated;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.IAnnotationFinder;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.archive.ClasspathArchive;

public class FinderFactory {
   private static final FinderFactory factory = new FinderFactory();
   public static final String FORCE_LINK = "openejb.finder.force.link";
   private static volatile boolean MODULE_LIMITED = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.finder.module-scoped", "false"));

   private static FinderFactory get() {
      FinderFactory factory = (FinderFactory)SystemInstance.get().getComponent(FinderFactory.class);
      return factory != null ? factory : FinderFactory.factory;
   }

   public static IAnnotationFinder createFinder(DeploymentModule module) throws Exception {
      return get().create(module);
   }

   public static AnnotationFinder getFinder(ClassLoader classLoader, URL url) {
      return newFinder(ClasspathArchive.archive(classLoader, url));
   }

   public IAnnotationFinder create(DeploymentModule module) throws Exception {
      OpenEJBAnnotationFinder finder;
      if (module instanceof WebModule) {
         WebModule webModule = (WebModule)module;
         finder = newFinder(new WebappAggregatedArchive(webModule, webModule.getScannableUrls()));
         finder = this.useFallbackFinderIfNeededOrLink(module, finder);
      } else if (module instanceof ConnectorModule) {
         ConnectorModule connectorModule = (ConnectorModule)module;
         finder = newFinder(new ConfigurableClasspathArchive(connectorModule, connectorModule.getLibraries()));
         finder = this.useFallbackFinderIfNeededOrLink(module, finder);
      } else if (module instanceof AppModule) {
         AppModule appModule = (AppModule)AppModule.class.cast(module);
         Collection<URL> urls = NewLoaderLogic.applyBuiltinExcludes(new UrlSet(appModule.getAdditionalLibraries())).getUrls();
         urls.addAll(appModule.getScannableContainerUrls());
         finder = newFinder(new WebappAggregatedArchive(module.getClassLoader(), module.getAltDDs(), urls));
         finder = this.useFallbackFinderIfNeededOrLink(module, finder);
      } else if (module.getJarLocation() != null) {
         String location = module.getJarLocation();
         File file = new File(location);
         URL url;
         if (file.exists()) {
            url = file.toURI().toURL();
            File webInfClassesFolder = new File(file, "WEB-INF/classes");
            if (webInfClassesFolder.exists() && webInfClassesFolder.isDirectory()) {
               url = webInfClassesFolder.toURI().toURL();
            }
         } else {
            url = new URL(location);
         }

         if (module instanceof Module) {
            DebugArchive archive = new DebugArchive(new ConfigurableClasspathArchive((Module)module, new URL[]{url}));
            finder = newFinder(archive);
         } else {
            finder = newFinder(new DebugArchive(new ConfigurableClasspathArchive(module.getClassLoader(), url)));
         }

         finder = this.useFallbackFinderIfNeededOrLink(module, finder);
      } else {
         finder = this.fallbackAnnotationFinder(module);
      }

      return (IAnnotationFinder)(MODULE_LIMITED ? new ModuleLimitedFinder(finder) : finder);
   }

   private OpenEJBAnnotationFinder useFallbackFinderIfNeededOrLink(DeploymentModule module, OpenEJBAnnotationFinder finder) {
      if (!finder.foundSomething()) {
         OpenEJBAnnotationFinder fbFinder = this.fallbackAnnotationFinder(module);
         if (fbFinder.foundSomething()) {
            return fbFinder;
         }
      }

      finder.link();
      return finder;
   }

   private OpenEJBAnnotationFinder fallbackAnnotationFinder(DeploymentModule module) {
      OpenEJBAnnotationFinder finder = new OpenEJBAnnotationFinder(new ClassesArchive(ensureMinimalClasses(module)));
      finder.enableMetaAnnotations();
      return finder;
   }

   public static Class<?>[] ensureMinimalClasses(DeploymentModule module) {
      Collection<Class<?>> finderClasses = new HashSet();
      if (EjbModule.class.isInstance(module)) {
         EjbModule ejb = (EjbModule)EjbModule.class.cast(module);
         EnterpriseBean[] enterpriseBeans = ejb.getEjbJar().getEnterpriseBeans();
         ClassLoader classLoader = ejb.getClassLoader();
         if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
         }

         for(EnterpriseBean bean : enterpriseBeans) {
            String name;
            if (SessionBean.class.isInstance(bean)) {
               SessionBean sessionBean = (SessionBean)SessionBean.class.cast(bean);
               if (sessionBean.getProxy() == null) {
                  name = sessionBean.getEjbClass();
               } else {
                  name = sessionBean.getProxy();
               }
            } else {
               name = bean.getEjbClass();
            }

            try {
               Class<?> clazz = classLoader.loadClass(name);
               finderClasses.addAll(Classes.ancestors(clazz));
            } catch (ClassNotFoundException var23) {
            }
         }

         if (ejb.getWebservices() != null) {
            for(WebserviceDescription webservice : ejb.getWebservices().getWebserviceDescription()) {
               for(PortComponent port : webservice.getPortComponent()) {
                  if (port.getHandlerChains() != null) {
                     for(HandlerChain handlerChain : port.getHandlerChains().getHandlerChain()) {
                        for(Handler handler : handlerChain.getHandler()) {
                           if (handler.getHandlerClass() != null) {
                              try {
                                 finderClasses.addAll(Classes.ancestors(classLoader.loadClass(handler.getHandlerClass())));
                              } catch (ClassNotFoundException var22) {
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         for(Interceptor interceptor : ejb.getEjbJar().getInterceptors()) {
            try {
               finderClasses.addAll(Classes.ancestors(classLoader.loadClass(interceptor.getInterceptorClass())));
            } catch (ClassNotFoundException var21) {
            }
         }

         Beans beans = ejb.getBeans();
         if (beans != null && ejb.getEjbJar() != null) {
            for(List<String> managedClasses : beans.getManagedClasses().values()) {
               for(String name : managedClasses) {
                  try {
                     finderClasses.addAll(Classes.ancestors(classLoader.loadClass(name)));
                  } catch (ClassNotFoundException var20) {
                  }
               }
            }

            for(String name : beans.getInterceptors()) {
               try {
                  finderClasses.addAll(Classes.ancestors(classLoader.loadClass(name)));
               } catch (ClassNotFoundException var19) {
               }
            }

            for(String name : beans.getAlternativeClasses()) {
               try {
                  finderClasses.addAll(Classes.ancestors(classLoader.loadClass(name)));
               } catch (ClassNotFoundException var18) {
               }
            }

            for(String name : beans.getDecorators()) {
               try {
                  finderClasses.addAll(Classes.ancestors(classLoader.loadClass(name)));
               } catch (ClassNotFoundException var17) {
               }
            }
         }
      } else if (WebModule.class.isInstance(module)) {
         WebModule web = (WebModule)WebModule.class.cast(module);
         ClassLoader classLoader = web.getClassLoader();
         if (web.getWebApp() != null) {
            for(Servlet s : web.getWebApp().getServlet()) {
               String servletClass = s.getServletClass();
               if (servletClass != null) {
                  try {
                     finderClasses.addAll(Classes.ancestors(classLoader.loadClass(servletClass)));
                  } catch (ClassNotFoundException var16) {
                  }
               }
            }

            for(String s : web.getRestClasses()) {
               try {
                  finderClasses.addAll(Classes.ancestors(classLoader.loadClass(s)));
               } catch (ClassNotFoundException var15) {
               }
            }

            for(String s : web.getEjbWebServices()) {
               try {
                  finderClasses.addAll(Classes.ancestors(classLoader.loadClass(s)));
               } catch (ClassNotFoundException var14) {
               }
            }
         }
      }

      return (Class[])finderClasses.toArray(new Class[finderClasses.size()]);
   }

   private static OpenEJBAnnotationFinder newFinder(Archive archive) {
      return new OpenEJBAnnotationFinder(archive);
   }

   public static Map<String, String> urlByClass(IAnnotationFinder finder) {
      IAnnotationFinder limitedFinder;
      if (finder instanceof ModuleLimitedFinder) {
         limitedFinder = ((ModuleLimitedFinder)finder).getDelegate();
      } else {
         limitedFinder = finder;
      }

      if (limitedFinder instanceof AnnotationFinder) {
         Archive archive = ((AnnotationFinder)limitedFinder).getArchive();
         if (archive instanceof WebappAggregatedArchive) {
            Map<URL, List<String>> index = ((WebappAggregatedArchive)archive).getClassesMap();
            Map<String, String> urlByClasses = new HashMap();

            for(Map.Entry<URL, List<String>> entry : index.entrySet()) {
               String url = ((URL)entry.getKey()).toExternalForm();

               for(String current : (List)entry.getValue()) {
                  urlByClasses.put(current, url);
               }
            }

            return urlByClasses;
         }
      }

      return Collections.emptyMap();
   }

   public static final class DebugArchive implements Archive {
      private final Archive archive;

      private DebugArchive(Archive archive) {
         this.archive = archive;
      }

      public Iterator<Archive.Entry> iterator() {
         return this.archive.iterator();
      }

      public InputStream getBytecode(String s) throws IOException, ClassNotFoundException {
         return this.archive.getBytecode(s);
      }

      public Class<?> loadClass(String s) throws ClassNotFoundException {
         try {
            return this.archive.loadClass(s);
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
         }
      }
   }

   public static class ModuleLimitedFinder implements IAnnotationFinder {
      private final OpenEJBAnnotationFinder delegate;

      public ModuleLimitedFinder(OpenEJBAnnotationFinder delegate) {
         this.delegate = delegate;
      }

      public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
         return this.delegate.isAnnotationPresent(annotation);
      }

      public List<String> getClassesNotLoaded() {
         return this.delegate.getClassesNotLoaded();
      }

      public List<Package> findAnnotatedPackages(Class<? extends Annotation> annotation) {
         return this.delegate.findAnnotatedPackages(annotation);
      }

      public List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation) {
         try {
            return filter(this.delegate.findAnnotatedClasses(annotation), new ClassPredicate(this.getAnnotatedClassNames()));
         } catch (TypeNotPresentException tnpe) {
            throw this.handleException(tnpe, annotation);
         }
      }

      private RuntimeException handleException(TypeNotPresentException tnpe, Class<? extends Annotation> annotation) {
         try {
            Method mtd = AnnotationFinder.class.getDeclaredMethod("getAnnotationInfos", String.class);
            mtd.setAccessible(true);

            for(Object info : (List)mtd.invoke(this.delegate)) {
               if (info instanceof AnnotationFinder.ClassInfo) {
                  AnnotationFinder.ClassInfo classInfo = (AnnotationFinder.ClassInfo)info;

                  try {
                     classInfo.get().isAnnotationPresent(annotation);
                  } catch (TypeNotPresentException tnpe2) {
                     throw new OpenEJBRuntimeException("Missing type for annotation " + annotation.getName() + " on class " + classInfo.getName(), tnpe2);
                  } catch (ThreadDeath var10) {
                  }
               }
            }
         } catch (Throwable var11) {
         }

         return tnpe;
      }

      public List<Class<?>> findInheritedAnnotatedClasses(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findInheritedAnnotatedClasses(annotation), new ClassPredicate(this.getAnnotatedClassNames()));
      }

      public List<Method> findAnnotatedMethods(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findAnnotatedMethods(annotation), new MethodPredicate(this.getAnnotatedClassNames()));
      }

      public List<Constructor> findAnnotatedConstructors(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findAnnotatedConstructors(annotation), new ConstructorPredicate(this.getAnnotatedClassNames()));
      }

      public List<Field> findAnnotatedFields(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findAnnotatedFields(annotation), new FieldPredicate(this.getAnnotatedClassNames()));
      }

      public List<Class<?>> findClassesInPackage(String packageName, boolean recursive) {
         return filter(this.delegate.findClassesInPackage(packageName, recursive), new ClassPredicate(this.getAnnotatedClassNames()));
      }

      public <T> List<Class<? extends T>> findSubclasses(Class<T> clazz) {
         return filter(this.delegate.findSubclasses(clazz), new ClassPredicate(this.getAnnotatedClassNames()));
      }

      public <T> List<Class<? extends T>> findImplementations(Class<T> clazz) {
         return filter(this.delegate.findImplementations(clazz), new ClassPredicate(this.getAnnotatedClassNames()));
      }

      public List<Annotated<Method>> findMetaAnnotatedMethods(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findMetaAnnotatedMethods(annotation), new AnnotatedMethodPredicate(this.getAnnotatedClassNames()));
      }

      public List<Annotated<Field>> findMetaAnnotatedFields(Class<? extends Annotation> annotation) {
         return filter(this.delegate.findMetaAnnotatedFields(annotation), new AnnotatedFieldPredicate(this.getAnnotatedClassNames()));
      }

      public List<Annotated<Class<?>>> findMetaAnnotatedClasses(Class<? extends Annotation> annotation) {
         try {
            return filter(this.delegate.findMetaAnnotatedClasses(annotation), new AnnotatedClassPredicate(this.getAnnotatedClassNames()));
         } catch (TypeNotPresentException tnpe) {
            throw this.handleException(tnpe, annotation);
         }
      }

      public List<String> getAnnotatedClassNames() {
         return this.delegate.getAnnotatedClassNames();
      }

      private static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
         List<T> ts = new ArrayList();

         for(T t : list) {
            if (predicate.accept(t)) {
               ts.add(t);
            }
         }

         return ts;
      }

      public IAnnotationFinder getDelegate() {
         return this.delegate;
      }

      private abstract static class Predicate<T> {
         protected final List<String> accepted;

         public Predicate(List<String> list) {
            this.accepted = list;
         }

         protected boolean accept(T t) {
            return this.accepted.contains(this.name(t));
         }

         protected abstract String name(T var1);
      }

      private static class ClassPredicate<T> extends Predicate<Class<? extends T>> {
         public ClassPredicate(List<String> list) {
            super(list);
         }

         protected String name(Class<? extends T> aClass) {
            return aClass.getName();
         }
      }

      private static class MethodPredicate extends Predicate<Method> {
         public MethodPredicate(List<String> list) {
            super(list);
         }

         protected String name(Method method) {
            return method.getDeclaringClass().getName();
         }
      }

      private static class FieldPredicate extends Predicate<Field> {
         public FieldPredicate(List<String> list) {
            super(list);
         }

         protected String name(Field field) {
            return field.getDeclaringClass().getName();
         }
      }

      private static class ConstructorPredicate extends Predicate<Constructor> {
         public ConstructorPredicate(List<String> list) {
            super(list);
         }

         protected String name(Constructor constructor) {
            return constructor.getDeclaringClass().getName();
         }
      }

      private static class AnnotatedClassPredicate extends Predicate<Annotated<Class<?>>> {
         public AnnotatedClassPredicate(List<String> list) {
            super(list);
         }

         protected String name(Annotated<Class<?>> aClass) {
            return ((Class)aClass.get()).getName();
         }
      }

      private static class AnnotatedMethodPredicate extends Predicate<Annotated<Method>> {
         public AnnotatedMethodPredicate(List<String> list) {
            super(list);
         }

         protected String name(Annotated<Method> method) {
            return ((Method)method.get()).getDeclaringClass().getName();
         }
      }

      private static class AnnotatedFieldPredicate extends Predicate<Annotated<Field>> {
         public AnnotatedFieldPredicate(List<String> list) {
            super(list);
         }

         protected String name(Annotated<Field> field) {
            return ((Field)field.get()).getDeclaringClass().getName();
         }
      }
   }

   public static class OpenEJBAnnotationFinder extends AnnotationFinder {
      private static final String[] JVM_SCANNING_CONFIG = SystemInstance.get().getProperty("openejb.scanning.xbean.jvm", "java.").split(",");

      public OpenEJBAnnotationFinder(Archive archive) {
         super(archive);
      }

      protected boolean isJvm(String name) {
         return sharedIsJvm(name);
      }

      public static boolean sharedIsJvm(String name) {
         for(String s : JVM_SCANNING_CONFIG) {
            if (name.startsWith(s)) {
               return true;
            }
         }

         return false;
      }

      public boolean foundSomething() {
         return !this.classInfos.isEmpty();
      }
   }

   public static class DoLoadClassesArchive extends ClassesArchive {
      public DoLoadClassesArchive(ClassLoader loader, Collection<String> classes) {
         super(load(loader, classes));
      }

      private static Iterable<Class<?>> load(ClassLoader loader, Collection<String> classes) {
         Collection<Class<?>> loaded = new ArrayList(classes.size());

         for(String n : classes) {
            try {
               loaded.add(loader.loadClass(n));
            } catch (ClassNotFoundException var6) {
            }
         }

         return loaded;
      }
   }
}
