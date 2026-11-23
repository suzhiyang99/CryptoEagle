package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import org.apache.openejb.Injection;
import org.apache.openejb.OpenEJBException;

public class InjectionBuilder {
   private final ClassLoader classLoader;

   public InjectionBuilder(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public List<Injection> buildInjections(JndiEncInfo jndiEnc) throws OpenEJBException {
      List<Injection> injections = new ArrayList();

      for(EnvEntryInfo info : jndiEnc.envEntries) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(EjbReferenceInfo info : jndiEnc.ejbReferences) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(EjbReferenceInfo info : jndiEnc.ejbLocalReferences) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(PersistenceUnitReferenceInfo info : jndiEnc.persistenceUnitRefs) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(PersistenceContextReferenceInfo info : jndiEnc.persistenceContextRefs) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(ResourceReferenceInfo info : jndiEnc.resourceRefs) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(ResourceEnvReferenceInfo info : jndiEnc.resourceEnvRefs) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      for(ServiceReferenceInfo info : jndiEnc.serviceRefs) {
         for(InjectionInfo target : info.targets) {
            Injection injection = this.injection(info.referenceName, target.propertyName, target.className);
            injections.add(injection);
         }
      }

      return injections;
   }

   private Injection injection(String referenceName, String propertyName, String className) {
      Class<?> targetClass;
      try {
         targetClass = this.loadClass(className);
      } catch (OpenEJBException var6) {
         targetClass = null;
      }

      return targetClass == null ? new Injection(referenceName, propertyName, className) : new Injection(referenceName, propertyName, targetClass);
   }

   private Class loadClass(String className) throws OpenEJBException {
      try {
         Class clazz = Class.forName(className, true, this.classLoader);
         return clazz;
      } catch (Throwable var3) {
         throw new OpenEJBException("Unable to load class " + className);
      }
   }
}
