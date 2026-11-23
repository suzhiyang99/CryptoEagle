package org.apache.openejb.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.JndiConsumer;

public class CleanEnvEntries implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      appModule = this.removeUnsetEnvEntries(appModule);
      appModule = this.fillInMissingType(appModule);
      return appModule;
   }

   public AppModule removeUnsetEnvEntries(AppModule appModule) throws OpenEJBException {
      Map<String, EnvEntry> appEnvEntryMap = getAppEnvEntryMap(appModule);

      for(ClientModule module : appModule.getClientModules()) {
         JndiConsumer consumer = module.getApplicationClient();
         if (consumer != null) {
            this.removeUnsetEnvEntries(appEnvEntryMap, consumer);
         }
      }

      for(WebModule module : appModule.getWebModules()) {
         JndiConsumer consumer = module.getWebApp();
         if (consumer != null) {
            this.removeUnsetEnvEntries(appEnvEntryMap, consumer);
         }
      }

      for(EjbModule module : appModule.getEjbModules()) {
         EjbJar ejbJar = module.getEjbJar();
         if (ejbJar != null) {
            for(EnterpriseBean consumer : ejbJar.getEnterpriseBeans()) {
               if (!CompManagedBean.class.isInstance(consumer)) {
                  this.removeUnsetEnvEntries(appEnvEntryMap, consumer);
               }
            }
         }
      }

      return appModule;
   }

   private static Map<String, EnvEntry> getAppEnvEntryMap(AppModule appModule) {
      return appModule.getApplication() != null && appModule.getApplication().getEnvEntry() != null ? appModule.getApplication().getEnvEntryMap() : Collections.emptyMap();
   }

   private void removeUnsetEnvEntries(Map<String, EnvEntry> appEntries, JndiConsumer consumer) {
      Iterator<EnvEntry> entries = consumer.getEnvEntry().iterator();

      while(entries.hasNext()) {
         EnvEntry entry = (EnvEntry)entries.next();
         if (entry.getEnvEntryValue() == null && entry.getLookupName() == null) {
            EnvEntry appEntry = (EnvEntry)appEntries.get(entry.getName());
            if (appEntry != null && appEntry.getEnvEntryValue() != null) {
               entry.setEnvEntryValue(appEntry.getEnvEntryValue());
            } else {
               entries.remove();
            }
         }
      }

   }

   public AppModule fillInMissingType(AppModule appModule) throws OpenEJBException {
      for(ClientModule module : appModule.getClientModules()) {
         JndiConsumer consumer = module.getApplicationClient();
         if (consumer != null) {
            this.fillInMissingType((JndiConsumer)consumer, (DeploymentModule)module);
         }
      }

      for(WebModule module : appModule.getWebModules()) {
         JndiConsumer consumer = module.getWebApp();
         if (consumer != null) {
            this.fillInMissingType((JndiConsumer)consumer, (DeploymentModule)module);
         }
      }

      for(EjbModule module : appModule.getEjbModules()) {
         EjbJar ejbJar = module.getEjbJar();
         if (ejbJar != null) {
            for(EnterpriseBean consumer : ejbJar.getEnterpriseBeans()) {
               this.fillInMissingType((JndiConsumer)consumer, (DeploymentModule)module);
            }
         }
      }

      return appModule;
   }

   private void fillInMissingType(JndiConsumer consumer, DeploymentModule module) {
      ClassLoader loader = module.getClassLoader();

      for(EnvEntry entry : consumer.getEnvEntry()) {
         this.fillInMissingType(loader, entry);
      }

   }

   private void fillInMissingType(ClassLoader loader, EnvEntry entry) {
      if (entry.getType() == null) {
         if (entry.getLookupName() == null) {
            if (entry.getInjectionTarget().size() != 0) {
               Set<Class> types = new HashSet();

               for(InjectionTarget target : entry.getInjectionTarget()) {
                  if (target.getInjectionTargetClass() != null && target.getInjectionTargetName() != null) {
                     types.add(this.getType(loader, target));
                  }
               }

               this.normalize(types);
               Class<?> type = types.size() == 1 ? (Class)types.iterator().next() : String.class;
               entry.setType(type.getName());
            }
         }
      }
   }

   private void normalize(Set<Class> types) {
      types.remove(Object.class);
      if (types.contains(Integer.TYPE)) {
         types.remove(Integer.TYPE);
         types.add(Integer.class);
      }

      if (types.contains(Character.TYPE)) {
         types.remove(Character.TYPE);
         types.add(Character.class);
      }

      if (types.contains(Short.TYPE)) {
         types.remove(Short.TYPE);
         types.add(Short.class);
      }

      if (types.contains(Long.TYPE)) {
         types.remove(Long.TYPE);
         types.add(Long.class);
      }

      if (types.contains(Float.TYPE)) {
         types.remove(Float.TYPE);
         types.add(Float.class);
      }

      if (types.contains(Double.TYPE)) {
         types.remove(Double.TYPE);
         types.add(Double.class);
      }

      if (types.contains(Boolean.TYPE)) {
         types.remove(Boolean.TYPE);
         types.add(Boolean.class);
      }

      if (types.contains(Byte.TYPE)) {
         types.remove(Byte.TYPE);
         types.add(Byte.class);
      }

   }

   private Class<?> getType(ClassLoader loader, InjectionTarget target) {
      try {
         Class<?> clazz = loader.loadClass(target.getInjectionTargetClass());

         try {
            Field field = clazz.getDeclaredField(target.getInjectionTargetName());
            return field.getType();
         } catch (NoSuchFieldException var11) {
            String bestName = "set" + StringUtils.capitalize(target.getInjectionTargetName());
            String name = "set" + target.getInjectionTargetName().toLowerCase(Locale.ENGLISH);
            Class<?> found = null;

            for(Method method : clazz.getDeclaredMethods()) {
               if (method.getParameterTypes().length == 1) {
                  if (method.getName().equals(bestName)) {
                     return method.getParameterTypes()[0];
                  }

                  if (method.getName().toLowerCase(Locale.ENGLISH).equals(name)) {
                     found = method.getParameterTypes()[0];
                  }
               }
            }

            if (found != null) {
               return found;
            }
         }
      } catch (Throwable var12) {
      }

      return Object.class;
   }
}
