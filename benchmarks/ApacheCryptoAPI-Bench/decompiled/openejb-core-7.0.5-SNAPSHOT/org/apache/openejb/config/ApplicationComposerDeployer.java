package org.apache.openejb.config;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.configuration.ApplicationComposer;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Descriptor;
import org.apache.openejb.testing.Descriptors;
import org.apache.openejb.testing.JaxrsProviders;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.archive.CompositeArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.filter.Filters;

public class ApplicationComposerDeployer implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      if (!appModule.isStandaloneModule()) {
         return appModule;
      } else {
         for(EjbModule ejbModule : appModule.getEjbModules()) {
            if (ejbModule.getFinder() != null) {
               WebModule webModule = null;

               for(WebModule web : appModule.getWebModules()) {
                  if (web.getModuleId().equals(ejbModule.getModuleId())) {
                     webModule = web;
                     break;
                  }
               }

               if (webModule != null) {
                  for(Class<?> clazz : ejbModule.getFinder().findAnnotatedClasses(ApplicationComposer.class)) {
                     ApplicationComposer applicationComposer = (ApplicationComposer)clazz.getAnnotation(ApplicationComposer.class);
                     Descriptor descriptor = (Descriptor)clazz.getAnnotation(Descriptor.class);
                     if (descriptor != null) {
                        this.configureDescriptor(appModule, descriptor);
                     }

                     Descriptors descriptors = (Descriptors)clazz.getAnnotation(Descriptors.class);
                     if (descriptors != null) {
                        for(Descriptor var10000 : descriptors.value()) {
                           this.configureDescriptor(appModule, descriptor);
                        }
                     }

                     Classes classes = (Classes)clazz.getAnnotation(Classes.class);
                     if (classes != null) {
                        this.configureClasses(webModule, ejbModule, applicationComposer, classes);
                     }

                     Object instance = null;
                     AnnotationFinder finder = new AnnotationFinder(new ClassesArchive(org.apache.openejb.util.Classes.ancestors(clazz)));

                     for(Method m : finder.findAnnotatedMethods(org.apache.openejb.testing.Module.class)) {
                        instance = this.configureModule(appModule, ejbModule, clazz, instance, m);
                     }

                     for(Method m : finder.findAnnotatedMethods(Configuration.class)) {
                        instance = this.configureConfiguration(appModule, clazz, instance, m);
                     }

                     JaxrsProviders jaxrsProviders = (JaxrsProviders)clazz.getAnnotation(JaxrsProviders.class);
                     if (jaxrsProviders != null) {
                        for(Class<?> c : jaxrsProviders.value()) {
                           webModule.getJaxrsProviders().add(c.getName());
                        }
                     }
                  }
               }
            }
         }

         return appModule;
      }
   }

   private Object configureConfiguration(AppModule appModule, Class<?> clazz, Object instance, Method m) {
      int modifiers = m.getModifiers();
      if (!Modifier.isPublic(modifiers)) {
         throw new IllegalArgumentException("@Configuration should be public");
      } else {
         boolean isStatic = Modifier.isStatic(modifiers);
         if (!isStatic) {
            try {
               instance = clazz.newInstance();
            } catch (Exception var9) {
            }
         }

         try {
            Object result = m.invoke(isStatic ? null : instance);
            if (Properties.class.isInstance(result)) {
               appModule.getProperties().putAll((Map)Properties.class.cast(result));
               return instance;
            } else {
               throw new IllegalArgumentException(result + " not yet supported (" + m + ")");
            }
         } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   private Object configureModule(AppModule appModule, EjbModule ejbModule, Class<?> clazz, Object instance, Method m) {
      int modifiers = m.getModifiers();
      if (!Modifier.isPublic(modifiers)) {
         throw new IllegalArgumentException("@Module should be public");
      } else {
         boolean isStatic = Modifier.isStatic(modifiers);
         if (!isStatic) {
            try {
               instance = clazz.newInstance();
            } catch (Exception var11) {
            }
         }

         try {
            Object result = m.invoke(isStatic ? null : instance);
            if (EjbJar.class.isInstance(result)) {
               ejbModule.setEjbJar((EjbJar)EjbJar.class.cast(result));
            } else if (Persistence.class.isInstance(result)) {
               Persistence persistence = (Persistence)Persistence.class.cast(result);
               if (!persistence.getPersistenceUnit().isEmpty()) {
                  appModule.getPersistenceModules().add(new PersistenceModule(appModule, this.rootUrl(ejbModule), persistence));
               }
            } else if (PersistenceUnit.class.isInstance(result)) {
               PersistenceUnit unit = (PersistenceUnit)PersistenceUnit.class.cast(result);
               appModule.addPersistenceModule(new PersistenceModule(appModule, this.rootUrl(ejbModule), new Persistence(new PersistenceUnit[]{unit})));
            } else {
               if (!Beans.class.isInstance(result)) {
                  throw new IllegalArgumentException(result + " not yet supported (" + m + ")");
               }

               Beans beans = (Beans)Beans.class.cast(result);
               ejbModule.setBeans(beans);
            }

            return instance;
         } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   private String rootUrl(EjbModule ejbModule) {
      try {
         return ejbModule.getModuleUri().toURL().toExternalForm();
      } catch (Exception var3) {
         return null;
      }
   }

   private void configureClasses(WebModule web, EjbModule ejbModule, ApplicationComposer applicationComposer, Classes classes) {
      ejbModule.getEjbJar().setMetadataComplete(applicationComposer.metadataComplete());
      Collection<Archive> archives = new LinkedList();
      if (classes.value().length > 0) {
         archives.add(new ClassesArchive(classes.value()));
      }

      if (classes.cdi()) {
         Beans beans = new Beans();

         for(Class<?> c : classes.cdiAlternatives()) {
            beans.addAlternativeClass(c);
         }

         for(Class<?> c : classes.cdiDecorators()) {
            beans.addDecorator(c);
         }

         for(Class<?> c : classes.cdiInterceptors()) {
            beans.addInterceptor(c);
         }

         ejbModule.setBeans(beans);
         if (applicationComposer.metadataComplete()) {
            for(Class<?> c : classes.value()) {
               beans.addManagedClass((URL)null, c.getName());
            }

            String name = BeanContext.Comp.openejbCompName(web.getModuleId());
            ManagedBean managedBean = new CompManagedBean(name, BeanContext.Comp.class);
            managedBean.setTransactionType(TransactionType.BEAN);
            ejbModule.getEjbJar().addEnterpriseBean(managedBean);
         }
      }

      CompositeArchive archive = new CompositeArchive(archives);
      Archive finalArchive = (Archive)(classes.excludes().length > 0 ? new FilteredArchive(archive, Filters.invert(Filters.prefixes(classes.excludes()))) : archive);
      ejbModule.setFinder((new FinderFactory.OpenEJBAnnotationFinder(finalArchive)).link());
      web.setFinder(ejbModule.getFinder());
      web.getWebApp().setMetadataComplete(ejbModule.getEjbJar().isMetadataComplete());
   }

   private void configureDescriptor(AppModule appModule, Descriptor descriptor) {
      URL resource = appModule.getClassLoader().getResource(descriptor.path());

      try {
         appModule.getAltDDs().put(descriptor.name(), resource == null ? (new File(descriptor.path())).toURI().toURL() : resource);
      } catch (MalformedURLException e) {
         throw new IllegalArgumentException(e);
      }
   }
}
