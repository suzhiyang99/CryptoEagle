package org.apache.openejb;

import java.util.concurrent.Semaphore;
import org.apache.openejb.config.ServiceUtils;
import org.apache.openejb.util.Messages;

public class Core {
   public static void warmup() {
   }

   static {
      Thread preloadMessages = new Thread() {
         public void run() {
            new Messages("org.apache.openejb.util.resources");
            new Messages("org.apache.openejb.config");
            new Messages("org.apache.openejb.config.resources");
         }
      };
      preloadMessages.start();
      Thread preloadServiceProviders = new Thread() {
         public void run() {
            try {
               ServiceUtils.getServiceProviders();
            } catch (OpenEJBException var2) {
            }

         }
      };
      preloadServiceProviders.start();
      final int permits = 2 * Runtime.getRuntime().availableProcessors() + 1;
      final Semaphore semaphore = new Semaphore(0);
      final ClassLoader loader = OpenEjbContainer.class.getClassLoader();

      try {
         Class.forName("org.apache.tomee.catalina.Warmup", true, loader);
      } catch (Throwable var15) {
         try {
            Class.forName("org.apache.openejb.util.Logger", true, loader);
         } catch (Throwable var14) {
         }

         final String[] classes = new String[]{"org.slf4j.LoggerFactory", "org.slf4j.impl.StaticLoggerBinder", "org.apache.openejb.config.sys.JaxbJavaee", "org.apache.bval.jsr.ApacheValidationProvider", "org.apache.bval.jsr.ApacheValidatorFactory", "org.apache.bval.jsr.ConstraintAnnotationAttributes", "org.apache.bval.jsr.ConstraintDefaults", "org.apache.bval.jsr.groups.GroupsComputer", "org.apache.bval.jsr.xml.ValidationMappingParser", "org.apache.bval.util.PrivilegedActions", "org.apache.geronimo.transaction.manager.TransactionManagerImpl", "org.apache.openejb.InterfaceType", "org.apache.openejb.assembler.classic.Assembler", "org.apache.openejb.assembler.classic.AssemblerTool", "org.apache.openejb.cdi.CdiBuilder", "org.apache.openejb.cdi.ThreadSingletonServiceImpl", "org.apache.openejb.config.AppValidator", "org.apache.openejb.config.AnnotationDeployer", "org.apache.openejb.config.AutoConfig", "org.apache.openejb.config.ConfigurationFactory", "org.apache.openejb.config.MBeanDeployer", "org.apache.openejb.config.PersistenceContextAnnFactory", "org.apache.openejb.core.ServerFederation", "org.apache.openejb.core.ivm.EjbHomeProxyHandler$1", "org.apache.openejb.core.ivm.EjbHomeProxyHandler$MethodType", "org.apache.openejb.core.managed.ManagedContainer$MethodType", "org.apache.openejb.loader.FileUtils", "org.apache.openejb.loader.IO", "org.apache.openejb.loader.SystemInstance", "org.apache.openejb.monitoring.StatsInterceptor", "org.apache.openejb.persistence.JtaEntityManagerRegistry", "org.apache.openejb.util.Join", "org.apache.openejb.util.JuliLogStreamFactory", "org.apache.openejb.util.LogCategory", "org.apache.openejb.util.Messages", "org.apache.openejb.util.SafeToolkit", "org.apache.openejb.util.StringTemplate", "org.apache.openejb.util.proxy.ProxyManager", "org.apache.openjpa.enhance.PCRegistry", "org.apache.openjpa.lib.util.Localizer", "org.apache.webbeans.logger.WebBeansLoggerFacade", "org.apache.xbean.naming.reference.SimpleReference", "org.apache.xbean.propertyeditor.PropertyEditors", "org.apache.xbean.propertyeditor.ReferenceIdentityMap", "org.apache.xbean.recipe.ReflectionUtil"};
         final int part = Math.max(1, (int)Math.round((double)classes.length * (double)1.0F / (double)permits));

         for(final int i = 0; i < permits; ++i) {
            final int offset = i * part;
            Thread thread = new Thread() {
               {
                  this.setName("core-" + i);
               }

               public void run() {
                  int max = offset + part;
                  if (i == permits - 1) {
                     max = classes.length;
                  }

                  for(int c = offset; c < max; ++c) {
                     try {
                        Class.forName(classes[c], true, loader);
                     } catch (Throwable var4) {
                     }
                  }

                  semaphore.release();
               }
            };
            thread.setDaemon(true);
            thread.start();
         }

         try {
            semaphore.acquire(permits);
         } catch (InterruptedException var13) {
            Thread.interrupted();
         }
      }

      try {
         preloadServiceProviders.join();
         preloadMessages.join();
      } catch (InterruptedException var12) {
         Thread.interrupted();
      }

   }
}
