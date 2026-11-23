package org.apache.openejb.config;

import java.util.ArrayList;
import javax.persistence.Entity;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.configuration.PersistenceUnitDefinition;
import org.apache.openejb.api.configuration.PersistenceUnitDefinitions;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.jpa.unit.TransactionType;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.xbean.finder.IAnnotationFinder;

public class ConfigurationDeployer implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule module : new ArrayList(appModule.getEjbModules())) {
         if (module.getFinder() != null) {
            EjbModule m = null;

            for(Class<?> configClass : module.getFinder().findAnnotatedClasses(PersistenceUnitDefinition.class)) {
               m = m == null ? this.findModule(appModule, module) : m;
               this.configureJpa(appModule, (PersistenceUnitDefinition)configClass.getAnnotation(PersistenceUnitDefinition.class), m.getFinder());
            }

            for(Class<?> configClass : module.getFinder().findAnnotatedClasses(PersistenceUnitDefinitions.class)) {
               for(PersistenceUnitDefinition persistenceUnitDefinition : ((PersistenceUnitDefinitions)configClass.getAnnotation(PersistenceUnitDefinitions.class)).value()) {
                  m = m == null ? this.findModule(appModule, module) : m;
                  this.configureJpa(appModule, persistenceUnitDefinition, m.getFinder());
               }
            }
         }
      }

      return appModule;
   }

   private EjbModule findModule(AppModule appModule, EjbModule module) {
      EjbModule m = module;
      if (module.getFinder().findAnnotatedClasses(Entity.class).isEmpty()) {
         for(EjbModule other : appModule.getEjbModules()) {
            if (other != module && other.getFinder() != null) {
               m = other;
               boolean done = false;

               for(WebModule web : appModule.getWebModules()) {
                  if (web.getModuleId().equals(other.getModuleId())) {
                     done = true;
                     break;
                  }
               }

               if (done) {
                  break;
               }
            }
         }
      }

      return m;
   }

   private void configureJpa(AppModule appModule, PersistenceUnitDefinition annotation, IAnnotationFinder finder) {
      if (annotation != null) {
         String unitName = PropertyPlaceHolderHelper.simpleValue(annotation.unitName());

         for(PersistenceModule module : appModule.getPersistenceModules()) {
            for(PersistenceUnit unit : module.getPersistence().getPersistenceUnit()) {
               if (unitName.equals(unit.getName())) {
                  Logger.getInstance(LogCategory.OPENEJB_STARTUP, ConfigurationDeployer.class).info("Unit[" + unitName + "] overriden by a persistence.xml with root url: " + module.getRootUrl());
                  return;
               }
            }
         }

         PersistenceUnit unit = new PersistenceUnit();
         unit.setName(unitName);
         if (!"auto".equals(annotation.jtaDataSource())) {
            unit.setJtaDataSource(PropertyPlaceHolderHelper.simpleValue(annotation.jtaDataSource()));
         }

         if (!"auto".equals(annotation.nonJtaDataSource())) {
            unit.setNonJtaDataSource(PropertyPlaceHolderHelper.simpleValue(annotation.nonJtaDataSource()));
         }

         for(String prop : annotation.properties()) {
            int equalIndex = prop.indexOf(61);
            unit.setProperty(PropertyPlaceHolderHelper.simpleValue(prop.substring(0, equalIndex)), PropertyPlaceHolderHelper.simpleValue(prop.substring(equalIndex + 1, prop.length())));
         }

         unit.setProperty("openejb.jpa.auto-scan", "true");
         if (!"auto".equals(annotation.entitiesPackage())) {
            unit.setProperty("openejb.jpa.auto-scan.package", PropertyPlaceHolderHelper.simpleValue(annotation.entitiesPackage()));
         }

         if (annotation.ddlAuto()) {
            unit.setProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
            unit.setProperty("javax.persistence.schema-generation.database.action", "create");
         }

         if (annotation.jta()) {
            unit.setTransactionType(TransactionType.JTA);
         } else {
            unit.setTransactionType(TransactionType.RESOURCE_LOCAL);
            unit.setNonJtaDataSource("autoNonJtaDb");
         }

         if (!"auto".equals(annotation.provider())) {
            unit.setProvider(annotation.provider());
         }

         unit.setValidationMode(annotation.validationMode());
         unit.setSharedCacheMode(annotation.cacheMode());
         AnnotationDeployer.doAutoJpa(finder, unit);
         Persistence persistence = new Persistence();
         persistence.addPersistenceUnit(unit);
         appModule.addPersistenceModule(new PersistenceModule(appModule, "@Configuration#" + unitName, persistence));
      }
   }
}
