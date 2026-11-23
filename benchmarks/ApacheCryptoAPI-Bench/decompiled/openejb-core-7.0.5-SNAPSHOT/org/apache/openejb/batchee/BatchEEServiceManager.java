package org.apache.openejb.batchee;

import java.util.Properties;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.batchee.container.services.ServicesManager;
import org.apache.batchee.container.services.ServicesManagerLocator;
import org.apache.batchee.container.services.executor.DefaultThreadPoolService;
import org.apache.batchee.container.services.factory.CDIBatchArtifactFactory;
import org.apache.batchee.spi.BatchArtifactFactory;
import org.apache.batchee.spi.BatchThreadPoolService;
import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.observer.event.ObserverAdded;
import org.apache.openejb.util.AppFinder;
import org.apache.openejb.util.classloader.Unwrappable;
import org.apache.webbeans.config.WebBeansContext;

public class BatchEEServiceManager implements ServicesManagerLocator {
   public void initEnvironment(@Observes ObserverAdded event) {
      if (event.getObserver() == this && !Boolean.getBoolean("openejb.batchee.integration.skip")) {
         ServicesManager.setServicesManagerLocator(this);
      }

   }

   public void storeClassLoader(@Observes AssemblerAfterApplicationCreated init) {
      this.doInit(init.getContext());
   }

   private void doInit(AppContext context) {
      if (context.get(ServicesManager.class) == null) {
         Thread thread = Thread.currentThread();
         ClassLoader current = thread.getContextClassLoader();
         thread.setContextClassLoader(context.getClassLoader());
         ServicesManager servicesManager = new ServicesManager();
         Properties properties = new Properties(SystemInstance.get().getProperties());
         properties.putAll(context.getProperties());

         try {
            if (properties.getProperty(BatchArtifactFactory.class.getName()) == null) {
               properties.setProperty(BatchThreadPoolService.class.getName(), TomEEThreadPoolService.class.getName());
            }

            if (properties.getProperty(BatchArtifactFactory.class.getName()) == null) {
               properties.setProperty(BatchArtifactFactory.class.getName(), TomEEArtifactFactory.class.getName());
            }

            servicesManager.init(properties);
         } finally {
            thread.setContextClassLoader(current);
         }

         context.set(ServicesManager.class, servicesManager);
      }
   }

   public ServicesManager find() {
      ClassLoader contextClassLoader = unwrap(Thread.currentThread().getContextClassLoader());
      AppContext context = (AppContext)AppFinder.findAppContextOrWeb(contextClassLoader, AppFinder.AppContextTransformer.INSTANCE);
      if (context != null) {
         this.doInit(context);
         return (ServicesManager)context.get(ServicesManager.class);
      } else {
         throw new IllegalStateException("Can't find ServiceManager for " + contextClassLoader);
      }
   }

   private static ClassLoader unwrap(ClassLoader tccl) {
      if (Unwrappable.class.isInstance(tccl)) {
         ClassLoader unwrapped = ((Unwrappable)Unwrappable.class.cast(tccl)).unwrap();
         if (unwrapped != null) {
            return unwrapped;
         }
      }

      return tccl;
   }

   public static class TomEEArtifactFactory extends CDIBatchArtifactFactory {
      protected BeanManager getBeanManager() {
         return WebBeansContext.currentInstance().getBeanManagerImpl();
      }
   }

   public static class TomEEThreadPoolService extends DefaultThreadPoolService {
      public void executeTask(Runnable work, Object config) {
         Thread thread = Thread.currentThread();
         ClassLoader tccl = thread.getContextClassLoader();
         thread.setContextClassLoader(BatchEEServiceManager.unwrap(tccl));

         try {
            super.executeTask(work, config);
         } finally {
            thread.setContextClassLoader(tccl);
         }

      }
   }
}
