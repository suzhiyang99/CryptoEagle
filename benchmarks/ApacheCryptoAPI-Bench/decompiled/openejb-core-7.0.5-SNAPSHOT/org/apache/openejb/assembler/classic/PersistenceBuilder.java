package org.apache.openejb.assembler.classic;

import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.PersistenceClassLoaderHandler;
import org.apache.openejb.persistence.PersistenceUnitInfoImpl;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class PersistenceBuilder {
   public static final Logger logger;
   private final PersistenceClassLoaderHandler persistenceClassLoaderHandler;

   public PersistenceBuilder(PersistenceClassLoaderHandler persistenceClassLoaderHandler) {
      this.persistenceClassLoaderHandler = persistenceClassLoaderHandler;
   }

   public ReloadableEntityManagerFactory createEntityManagerFactory(PersistenceUnitInfo info, ClassLoader classLoader, Map<ComparableValidationConfig, ValidatorFactory> validators, boolean hasCdi) throws Exception {
      PersistenceUnitInfoImpl unitInfo = new PersistenceUnitInfoImpl(this.persistenceClassLoaderHandler);
      unitInfo.setId(info.id);
      unitInfo.setPersistenceUnitName(info.name);
      unitInfo.setPersistenceProviderClassName(info.provider);
      unitInfo.setClassLoader(classLoader);
      unitInfo.setExcludeUnlistedClasses(info.excludeUnlistedClasses);
      unitInfo.setLazilyInitialized(info.webappName != null || "true".equalsIgnoreCase(info.properties.getProperty("tomee.jpa.factory.lazy", SystemInstance.get().getProperty("tomee.jpa.factory.lazy", "false"))));
      Context context = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext();
      String jtaDataSourceId = info.jtaDataSource;
      unitInfo.setJtaDataSourceName(jtaDataSourceId);
      if (jtaDataSourceId != null && !SystemInstance.get().hasProperty("openejb.geronimo")) {
         String initialJndiName = jtaDataSourceId;

         try {
            if (!jtaDataSourceId.startsWith("java:openejb/Resource/") && !jtaDataSourceId.startsWith("openejb/Resource/")) {
               jtaDataSourceId = "openejb/Resource/" + jtaDataSourceId;
            }

            CommonDataSource jtaDataSource = (CommonDataSource)context.lookup(jtaDataSourceId);
            unitInfo.setJtaDataSource(jtaDataSource);
         } catch (NamingException e) {
            try {
               unitInfo.setJtaDataSource((DataSource)(new InitialContext()).lookup(initialJndiName));
            } catch (NamingException var17) {
               throw new OpenEJBException("Could not lookup <jta-data-source> '" + jtaDataSourceId + "' for unit '" + unitInfo.getPersistenceUnitName() + "'", e);
            }
         }
      }

      unitInfo.setManagedClassNames(info.classes);
      unitInfo.setMappingFileNames(info.mappingFiles);
      unitInfo.setProperties(info.properties);
      unitInfo.setPersistenceXMLSchemaVersion(info.persistenceXMLSchemaVersion);
      SharedCacheMode sharedCacheMode = (SharedCacheMode)Enum.valueOf(SharedCacheMode.class, info.sharedCacheMode);
      unitInfo.setSharedCacheMode(sharedCacheMode);
      ValidationMode validationMode = (ValidationMode)Enum.valueOf(ValidationMode.class, info.validationMode);
      unitInfo.setValidationMode(validationMode);
      PersistenceUnitTransactionType type = (PersistenceUnitTransactionType)Enum.valueOf(PersistenceUnitTransactionType.class, info.transactionType);
      unitInfo.setTransactionType(type);
      String nonJtaDataSourceId = info.nonJtaDataSource;
      unitInfo.setNonJtaDataSourceName(nonJtaDataSourceId);
      if (nonJtaDataSourceId != null && !SystemInstance.get().hasProperty("openejb.geronimo")) {
         String initialJndiName = nonJtaDataSourceId;

         try {
            if (!nonJtaDataSourceId.startsWith("java:openejb/Resource/")) {
               nonJtaDataSourceId = "java:openejb/Resource/" + nonJtaDataSourceId;
            }

            CommonDataSource nonJtaDataSource = (CommonDataSource)context.lookup(nonJtaDataSourceId);
            unitInfo.setNonJtaDataSource(nonJtaDataSource);
         } catch (NamingException e) {
            try {
               unitInfo.setNonJtaDataSource((DataSource)(new InitialContext()).lookup(initialJndiName));
            } catch (NamingException var15) {
               throw new OpenEJBException("Could not lookup <non-jta-data-source> '" + nonJtaDataSourceId + "' for unit '" + unitInfo.getPersistenceUnitName() + "'", e);
            }
         }
      }

      unitInfo.setRootUrlAndJarUrls(info.persistenceUnitRootUrl, info.jarFiles);
      String persistenceProviderClassName = unitInfo.getPersistenceProviderClassName();
      unitInfo.setPersistenceProviderClassName(persistenceProviderClassName);
      EntityManagerFactoryCallable callable = new EntityManagerFactoryCallable(persistenceProviderClassName, unitInfo, classLoader, validators, hasCdi);
      return new ReloadableEntityManagerFactory(classLoader, callable, unitInfo);
   }

   public static String getOpenEJBJndiName(String unit) {
      return "openejb/PersistenceUnit/" + unit;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, PersistenceBuilder.class);
   }
}
