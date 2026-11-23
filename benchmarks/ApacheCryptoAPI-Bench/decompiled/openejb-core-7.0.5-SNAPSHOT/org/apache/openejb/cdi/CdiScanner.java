package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.decorator.Decorator;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.BeansInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.cdi.transactional.MandatoryInterceptor;
import org.apache.openejb.cdi.transactional.NeverInterceptor;
import org.apache.openejb.cdi.transactional.NotSupportedInterceptor;
import org.apache.openejb.cdi.transactional.RequiredInterceptor;
import org.apache.openejb.cdi.transactional.RequiredNewInterceptor;
import org.apache.openejb.cdi.transactional.SupportsInterceptor;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.classloader.ClassLoaderComparator;
import org.apache.openejb.util.classloader.DefaultClassLoaderComparator;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.intercept.InterceptorsManager;
import org.apache.webbeans.spi.BDABeansXmlScanner;
import org.apache.webbeans.spi.BeanArchiveService;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.spi.BeanArchiveService.BeanDiscoveryMode;

public class CdiScanner implements ScannerService {
   public static final String OPENEJB_CDI_FILTER_CLASSLOADER = "openejb.cdi.filter.classloader";
   private static final Class<?>[] TRANSACTIONAL_INTERCEPTORS = new Class[]{MandatoryInterceptor.class, NeverInterceptor.class, NotSupportedInterceptor.class, RequiredInterceptor.class, RequiredNewInterceptor.class, SupportsInterceptor.class};
   private final Set<Class<?>> classes = new HashSet();
   private final Set<Class<?>> startupClasses = new HashSet();
   private final Set<URL> beansXml = new HashSet();
   private final boolean logDebug = "true".equals(SystemInstance.get().getProperty("openejb.cdi.noclassdeffound.log", "false"));
   private WebBeansContext webBeansContext;
   private ClassLoader containerLoader;

   public void setContext(WebBeansContext webBeansContext) {
      this.webBeansContext = webBeansContext;
   }

   public void init(Object object) {
      if (StartupObject.class.isInstance(object)) {
         this.containerLoader = ParentClassLoaderFinder.Helper.get();
         StartupObject startupObject = (StartupObject)StartupObject.class.cast(object);
         AppInfo appInfo = startupObject.getAppInfo();
         ClassLoader classLoader = startupObject.getClassLoader();
         ClassLoaderComparator comparator;
         if (classLoader instanceof ClassLoaderComparator) {
            comparator = (ClassLoaderComparator)classLoader;
         } else {
            comparator = new DefaultClassLoaderComparator(classLoader);
         }

         WebBeansContext webBeansContext = startupObject.getWebBeansContext();
         InterceptorsManager interceptorsManager = webBeansContext.getInterceptorsManager();
         Iterator var8 = appInfo.ejbJars.iterator();

         while(true) {
            EjbJarInfo ejbJar;
            BeansInfo beans;
            while(true) {
               if (!var8.hasNext()) {
                  return;
               }

               ejbJar = (EjbJarInfo)var8.next();
               beans = ejbJar.beans;
               if (beans != null && !"false".equalsIgnoreCase(ejbJar.properties.getProperty("openejb.cdi.activated"))) {
                  if (startupObject.isFromWebApp()) {
                     if (!ejbJar.moduleId.equals(startupObject.getWebContext().getId())) {
                        continue;
                     }
                     break;
                  } else if (!ejbJar.webapp || appInfo.webAppAlone) {
                     break;
                  }
               }
            }

            if (appInfo.webAppAlone || !ejbJar.webapp) {
               this.classes.addAll(Arrays.asList(TRANSACTIONAL_INTERCEPTORS));

               for(Class<?> interceptor : TRANSACTIONAL_INTERCEPTORS) {
                  interceptorsManager.addEnabledInterceptorClass(interceptor);
               }
            }

            ClassLoader scl = ClassLoader.getSystemClassLoader();
            boolean filterByClassLoader = "true".equals(ejbJar.properties.getProperty("openejb.cdi.filter.classloader", SystemInstance.get().getProperty("openejb.cdi.filter.classloader", "true")));
            BeanArchiveService beanArchiveService = webBeansContext.getBeanArchiveService();
            boolean openejb = OpenEJBBeanInfoService.class.isInstance(beanArchiveService);
            Map<BeansInfo.BDAInfo, BeanArchiveService.BeanArchiveInformation> infoByBda = new HashMap();

            for(BeansInfo.BDAInfo bda : beans.bdas) {
               if (bda.uri != null) {
                  try {
                     this.beansXml.add(bda.uri.toURL());
                  } catch (MalformedURLException var23) {
                  }
               }

               infoByBda.put(bda, this.handleBda(startupObject, classLoader, comparator, beans, scl, filterByClassLoader, beanArchiveService, openejb, bda));
            }

            for(BeansInfo.BDAInfo bda : beans.noDescriptorBdas) {
               this.handleBda(startupObject, classLoader, comparator, beans, scl, filterByClassLoader, beanArchiveService, openejb, bda);
            }

            if (startupObject.getBeanContexts() != null) {
               for(BeanContext bc : startupObject.getBeanContexts()) {
                  String name = bc.getBeanClass().getName();
                  if (!BeanContext.Comp.class.getName().equals(name)) {
                     boolean cdi = false;

                     for(BeansInfo.BDAInfo bda : beans.bdas) {
                        BeanArchiveService.BeanArchiveInformation info = (BeanArchiveService.BeanArchiveInformation)infoByBda.get(bda);
                        if (info.getBeanDiscoveryMode() != BeanDiscoveryMode.NONE && bda.managedClasses.contains(name)) {
                           this.classes.add(this.load(name, classLoader));
                           cdi = true;
                           break;
                        }
                     }

                     if (!cdi) {
                        for(BeansInfo.BDAInfo bda : beans.noDescriptorBdas) {
                           if (bda.managedClasses.contains(name)) {
                              this.classes.add(this.load(name, classLoader));
                              break;
                           }
                        }
                     }
                  }
               }
            }

            if ("true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.cdi.debug", "false"))) {
               Logger logger = Logger.getInstance(LogCategory.OPENEJB, CdiScanner.class.getName());
               logger.info("CDI beans for " + startupObject.getAppInfo().appId + (startupObject.getWebContext() != null ? " webcontext = " + startupObject.getWebContext().getContextRoot() : ""));
               List<String> names = new ArrayList(this.classes.size());

               for(Class<?> c : this.classes) {
                  names.add(c.getName());
               }

               Collections.sort(names);

               for(String c : names) {
                  logger.info("    " + c);
               }
            }
         }
      }
   }

   private void addClasses(Collection<String> list, ClassLoader loader) {
      for(String s : list) {
         Class<?> load = this.load(s, loader);
         if (load != null) {
            this.classes.add(load);
         }
      }

   }

   private BeanArchiveService.BeanArchiveInformation handleBda(StartupObject startupObject, ClassLoader classLoader, ClassLoaderComparator comparator, BeansInfo beans, ClassLoader scl, boolean filterByClassLoader, BeanArchiveService beanArchiveService, boolean openejb, BeansInfo.BDAInfo bda) {
      BeanArchiveService.BeanArchiveInformation information;
      if (openejb) {
         OpenEJBBeanInfoService beanInfoService = (OpenEJBBeanInfoService)OpenEJBBeanInfoService.class.cast(beanArchiveService);
         information = beanInfoService.createBeanArchiveInformation(bda, beans, classLoader);

         try {
            beanInfoService.getBeanArchiveInfo().put(bda.uri == null ? null : bda.uri.toURL(), information);
         } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
         }
      } else {
         try {
            information = beanArchiveService.getBeanArchiveInformation(bda.uri.toURL());
         } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
         }
      }

      this.addClasses(information.getAlternativeClasses(), classLoader);
      this.addClasses(information.getDecorators(), classLoader);
      this.addClasses(information.getInterceptors(), classLoader);
      this.addClasses(information.getAlternativeStereotypes(), classLoader);
      boolean scanModeAnnotated = BeanDiscoveryMode.ANNOTATED.equals(information.getBeanDiscoveryMode());
      boolean noScan = BeanDiscoveryMode.NONE.equals(information.getBeanDiscoveryMode());
      boolean isNotEarWebApp = startupObject.getWebContext() == null;
      if (!noScan) {
         if (scanModeAnnotated) {
            try {
               Logger.getInstance(LogCategory.OPENEJB, CdiScanner.class.getName()).info("Using annotated mode for " + bda.uri.toASCIIString() + " looking all classes to find CDI beans, maybe think to add a beans.xml if not there or add the jar to exclusions.list");
            } catch (Exception var18) {
            }
         }

         for(String name : bda.managedClasses) {
            if (!information.isClassExcluded(name)) {
               Class clazz = this.load(name, classLoader);
               if (clazz != null) {
                  if (scanModeAnnotated) {
                     if (this.isBean(clazz)) {
                        this.classes.add(clazz);
                        if (beans.startupClasses.contains(name)) {
                           this.startupClasses.add(clazz);
                        }
                     }
                  } else {
                     ClassLoader loader = clazz.getClassLoader();
                     if (!filterByClassLoader || comparator.isSame(loader) || (loader.equals(scl) || loader == this.containerLoader) && isNotEarWebApp) {
                        this.classes.add(clazz);
                        if (beans.startupClasses.contains(name)) {
                           this.startupClasses.add(clazz);
                        }
                     }
                  }
               }
            }
         }
      }

      return information;
   }

   private boolean isBean(Class clazz) {
      try {
         for(Annotation a : clazz.getAnnotations()) {
            Class<? extends Annotation> annotationType = a.annotationType();
            BeanManagerImpl beanManager = this.webBeansContext.getBeanManagerImpl();
            if (beanManager.isScope(annotationType) || beanManager.isStereotype(annotationType) || beanManager.isInterceptorBinding(annotationType) || Decorator.class == a.annotationType()) {
               return true;
            }
         }
      } catch (Throwable var8) {
      }

      return false;
   }

   public boolean isBDABeansXmlScanningEnabled() {
      return false;
   }

   public BDABeansXmlScanner getBDABeansXmlScanner() {
      return null;
   }

   private Class load(String className, ClassLoader classLoader) {
      try {
         Class<?> loadClass = classLoader.loadClass(className);
         this.tryToMakeItFail(loadClass);
         return loadClass;
      } catch (ClassNotFoundException var4) {
         return null;
      } catch (NoClassDefFoundError e) {
         if (this.logDebug) {
            Logger.getInstance(LogCategory.OPENEJB_CDI, CdiScanner.class).warning(className + " -> " + e);
         }

         return null;
      }
   }

   private void tryToMakeItFail(Class<?> loadClass) {
      loadClass.getDeclaredFields();
      loadClass.getDeclaredMethods();
   }

   public void scan() {
   }

   public Set<URL> getBeanXmls() {
      return this.beansXml;
   }

   public Set<Class<?>> getBeanClasses() {
      return this.classes;
   }

   public void release() {
      this.classes.clear();
   }

   public Set<Class<?>> getStartupClasses() {
      return this.startupClasses;
   }
}
