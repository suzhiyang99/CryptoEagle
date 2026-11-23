package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.enterprise.inject.spi.BeanManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceProvider;
import javax.transaction.Transaction;
import javax.validation.ValidatorFactory;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.PersistenceUnitInfoImpl;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.InjectableBeanManager;

public class EntityManagerFactoryCallable implements Callable<EntityManagerFactory> {
   public static final String OPENEJB_JPA_INIT_ENTITYMANAGER = "openejb.jpa.init-entitymanager";
   public static final String OPENJPA_ENTITY_MANAGER_FACTORY_POOL = "openjpa.EntityManagerFactoryPool";
   private final String persistenceProviderClassName;
   private final PersistenceUnitInfoImpl unitInfo;
   private final Map<ComparableValidationConfig, ValidatorFactory> potentialValidators;
   private final boolean cdi;
   private ClassLoader appClassLoader;
   private Class<?> provider;

   public EntityManagerFactoryCallable(String persistenceProviderClassName, PersistenceUnitInfoImpl unitInfo, ClassLoader cl, Map<ComparableValidationConfig, ValidatorFactory> validators, boolean hasCdi) {
      this.persistenceProviderClassName = persistenceProviderClassName;
      this.unitInfo = unitInfo;
      this.appClassLoader = cl;
      this.potentialValidators = validators;
      this.cdi = hasCdi;
   }

   public Class<?> getProvider() {
      if (this.provider != null) {
         return this.provider;
      } else {
         ClassLoader old = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.appClassLoader);

         Class var2;
         try {
            var2 = this.provider = this.appClassLoader.loadClass(this.persistenceProviderClassName);
         } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
         } finally {
            Thread.currentThread().setContextClassLoader(old);
         }

         return var2;
      }
   }

   public EntityManagerFactory call() throws Exception {
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(this.appClassLoader);

      EntityManagerFactory var15;
      try {
         Class<?> clazz = this.appClassLoader.loadClass(this.persistenceProviderClassName);
         PersistenceProvider persistenceProvider = (PersistenceProvider)clazz.newInstance();
         Map<String, Object> properties = new HashMap();
         if (!ValidationMode.NONE.equals(this.unitInfo.getValidationMode())) {
            properties.put("javax.persistence.validation.factory", this.potentialValidators != null && this.potentialValidators.size() == 1 ? this.ensureSerializable((ValidatorFactory)this.potentialValidators.values().iterator().next()) : new ValidatorFactoryWrapper(this.potentialValidators));
         }

         if (this.cdi && "true".equalsIgnoreCase(this.unitInfo.getProperties().getProperty("tomee.jpa.cdi", "true")) && "true".equalsIgnoreCase(SystemInstance.get().getProperty("tomee.jpa.cdi", "true"))) {
            properties.put("javax.persistence.bean.manager", Proxy.newProxyInstance(this.appClassLoader, new Class[]{BeanManager.class}, new BmHandler()));
         }

         this.customizeProperties(properties);
         Transaction transaction;
         if (this.unitInfo.isLazilyInitialized()) {
            transaction = OpenEJB.getTransactionManager().suspend();
         } else {
            transaction = null;
         }

         EntityManagerFactory emf;
         try {
            emf = persistenceProvider.createContainerEntityManagerFactory(this.unitInfo, properties);
         } finally {
            if (this.unitInfo.isLazilyInitialized() && transaction != null) {
               OpenEJB.getTransactionManager().resume(transaction);
            }

         }

         if (this.unitInfo.getProperties() != null && "true".equalsIgnoreCase(this.unitInfo.getProperties().getProperty("openejb.jpa.init-entitymanager")) || SystemInstance.get().getOptions().get("openejb.jpa.init-entitymanager", false)) {
            emf.createEntityManager().close();
         }

         if (this.unitInfo.getNonJtaDataSource() != null) {
            ImportSql importer = new ImportSql(this.appClassLoader, this.unitInfo.getPersistenceUnitName(), this.unitInfo.getNonJtaDataSource());
            if (importer.hasSomethingToImport()) {
               emf.createEntityManager().close();
               importer.doImport();
            }
         }

         var15 = emf;
      } finally {
         Thread.currentThread().setContextClassLoader(old);
      }

      return var15;
   }

   private void customizeProperties(Map<String, Object> properties) {
      String pool = SystemInstance.get().getProperty("openjpa.EntityManagerFactoryPool");
      if (pool != null) {
         properties.put("openjpa.EntityManagerFactoryPool", pool);
      }

   }

   public PersistenceUnitInfoImpl getUnitInfo() {
      return this.unitInfo;
   }

   public void overrideClassLoader(ClassLoader loader) {
      this.appClassLoader = loader;
   }

   private ValidatorFactory ensureSerializable(ValidatorFactory factory) {
      return (ValidatorFactory)(Serializable.class.isInstance(factory) ? factory : new SingleValidatorFactoryWrapper(factory));
   }

   private static class BmHandler implements InvocationHandler, Serializable {
      private transient volatile BeanManager bm;

      private BmHandler() {
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            return method.invoke(this.findBm(), args);
         } catch (InvocationTargetException ite) {
            Logger.getInstance(LogCategory.OPENEJB_JPA, EntityManagerFactoryCallable.class).warning("Exception calling CDI, if a lifecycle issue you should maybe set tomee.jpa.factory.lazy=true", ite.getCause());
            throw ite.getCause();
         }
      }

      private Object findBm() {
         if (this.bm == null) {
            synchronized(this) {
               if (this.bm == null) {
                  this.bm = new InjectableBeanManager(WebBeansContext.currentInstance().getBeanManagerImpl());
               }
            }
         }

         return this.bm;
      }
   }
}
