package org.apache.openejb.config;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.jmx.MBean;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.finder.Annotated;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.IAnnotationFinder;

public class MBeanDeployer implements DynamicDeployer {
   private static final Logger logger;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      logger.debug("looking for annotated MBeans in " + appModule.getModuleId());
      List<String> done = new ArrayList();
      ClassLoader cl = appModule.getClassLoader();
      if (cl == null) {
         cl = Thread.currentThread().getContextClassLoader();
         if (cl == null) {
            cl = this.getClass().getClassLoader();
         }
      }

      Collection<Class<? extends Annotation>> mbeanClasses = new ArrayList(2);
      mbeanClasses.add(MBean.class);

      try {
         mbeanClasses.add(cl.loadClass("javax.management.MBean"));
      } catch (ClassNotFoundException | NoClassDefFoundError var13) {
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         if (ejbModule.getFinder() != null) {
            for(Class<? extends Annotation> mclazz : mbeanClasses) {
               for(Annotated<Class<?>> clazz : ejbModule.getFinder().findMetaAnnotatedClasses(mclazz)) {
                  Class<?> realClass = (Class)clazz.get();
                  String name = realClass.getName();
                  if (!done.contains(name)) {
                     ejbModule.getMbeans().add(name);
                     done.add(name);
                  }
               }
            }
         }
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         if (clientModule.getFinder() != null) {
            for(Class<? extends Annotation> mclazz : mbeanClasses) {
               for(Annotated<Class<?>> clazz : clientModule.getFinder().findMetaAnnotatedClasses(mclazz)) {
                  String name = ((Class)clazz.get()).getName();
                  if (!done.contains(name)) {
                     clientModule.getMbeans().add(name);
                  }
               }
            }
         }
      }

      List<URL> libs = appModule.getAdditionalLibraries();
      Iterator<URL> it = libs.iterator();

      while(it.hasNext()) {
         URL url = (URL)it.next();

         for(String location : done) {
            if (url.getFile().equals(location)) {
               it.remove();
            }
         }
      }

      if (libs.size() > 0) {
         IAnnotationFinder finder = new AnnotationFinder(new ConfigurableClasspathArchive(appModule.getClassLoader(), true, libs));

         for(Class<? extends Annotation> mclazz : mbeanClasses) {
            for(Annotated<Class<?>> clazz : finder.findMetaAnnotatedClasses(mclazz)) {
               String name = ((Class)clazz.get()).getName();
               if (!done.contains(name)) {
                  appModule.getAdditionalLibMbeans().add(name);
               }
            }
         }
      }

      return appModule;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, MBeanDeployer.class);
   }
}
