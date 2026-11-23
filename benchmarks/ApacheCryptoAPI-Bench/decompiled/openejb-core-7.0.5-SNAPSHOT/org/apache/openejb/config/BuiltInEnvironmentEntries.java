package org.apache.openejb.config;

import java.util.Map;
import javax.enterprise.concurrent.ContextService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.ResourceEnvRef;

public class BuiltInEnvironmentEntries implements DynamicDeployer {
   private final boolean addDefaults;

   public BuiltInEnvironmentEntries(boolean addDefaults) {
      this.addDefaults = addDefaults;
   }

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(ClientModule module : appModule.getClientModules()) {
         JndiConsumer consumer = module.getApplicationClient();
         if (consumer != null) {
            this.add(consumer, module, appModule, false);
         }
      }

      for(WebModule module : appModule.getWebModules()) {
         JndiConsumer consumer = module.getWebApp();
         if (consumer != null) {
            this.add(consumer, module, appModule, this.addDefaults);
         }
      }

      for(EjbModule module : appModule.getEjbModules()) {
         EjbJar ejbJar = module.getEjbJar();
         if (ejbJar != null) {
            for(EnterpriseBean consumer : ejbJar.getEnterpriseBeans()) {
               this.add(consumer, module, appModule, this.addDefaults && BeanContext.Comp.class.getName().equals(consumer.getEjbClass()));
            }
         }
      }

      return appModule;
   }

   private void add(JndiConsumer jndi, DeploymentModule module, DeploymentModule app, boolean defaults) {
      this.add(jndi.getEnvEntryMap(), (new EnvEntry()).name("java:module/ModuleName").value(module.getModuleId()).type(String.class));
      this.add(jndi.getEnvEntryMap(), (new EnvEntry()).name("java:app/AppName").value(app.getModuleId()).type(String.class));
      this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/BeanManager").type(BeanManager.class));
      this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/Validator").type(Validator.class));
      this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/ValidatorFactory").type(ValidatorFactory.class));
      this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/TransactionManager").type(TransactionManager.class));
      this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/TransactionSynchronizationRegistry").type(TransactionSynchronizationRegistry.class));
      if (defaults) {
         this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/DefaultManagedExecutorService").type(ManagedExecutorService.class));
         this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/DefaultManagedScheduledExecutorService").type(ManagedScheduledExecutorService.class));
         this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/DefaultManagedThreadFactory").type(ManagedThreadFactory.class));
         this.add(jndi.getResourceEnvRefMap(), (new ResourceEnvRef()).name("java:comp/DefaultContextService").type(ContextService.class));

         try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            contextClassLoader.loadClass("org.apache.activemq.ActiveMQSslConnectionFactory");
            ResourceEnvRef ref = (new ResourceEnvRef()).name("java:comp/DefaultJMSConnectionFactory").type(contextClassLoader.loadClass("javax.jms.ConnectionFactory"));
            this.add(jndi.getResourceEnvRefMap(), ref);
         } catch (NoClassDefFoundError | ClassNotFoundException var7) {
         }
      }

      this.add(jndi.getEnvEntryMap(), (new EnvEntry()).name("java:comp/ComponentName").value(jndi.getJndiConsumerName()).type(String.class));
   }

   private <E extends JndiReference> void add(Map<String, E> map, E entry) {
      E existing = (E)(map.get(entry.getKey()));
      map.put(entry.getKey(), entry);
      if (existing != null) {
         entry.getInjectionTarget().addAll(existing.getInjectionTarget());
      }

   }
}
