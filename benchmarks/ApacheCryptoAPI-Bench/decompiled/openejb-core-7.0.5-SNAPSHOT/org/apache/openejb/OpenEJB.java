package org.apache.openejb;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.transaction.TransactionManager;
import org.apache.openejb.assembler.classic.DeploymentExceptionManager;
import org.apache.openejb.cdi.CdiBuilder;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.spi.Assembler;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEjbVersion;
import org.apache.openejb.util.OptionsLog;
import org.apache.openejb.util.SafeToolkit;

public final class OpenEJB {
   private static Instance instance;
   private static final AtomicReference<Messages> messagesRef = new AtomicReference();

   private OpenEJB() {
   }

   public static ApplicationServer getApplicationServer() {
      return (ApplicationServer)SystemInstance.get().getComponent(ApplicationServer.class);
   }

   public static TransactionManager getTransactionManager() {
      return (TransactionManager)SystemInstance.get().getComponent(TransactionManager.class);
   }

   public static void destroy() {
      Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
      if (assembler != null) {
         assembler.destroy();
      } else {
         SystemInstance.reset();
      }

      instance = null;
   }

   public static void init(Properties props) throws OpenEJBException {
      init(props, (ApplicationServer)null);
   }

   private static Messages messages() {
      Messages m = (Messages)messagesRef.get();
      if (m == null) {
         m = new Messages("org.apache.openejb.util.resources");
         messagesRef.compareAndSet((Object)null, m);
      }

      return m;
   }

   public static void init(Properties initProps, ApplicationServer appServer) throws OpenEJBException {
      if (isInitialized()) {
         if (instance != null) {
            String msg = messages().message("startup.alreadyInitialized");
            logger().error(msg, instance.initialized);
            throw new OpenEJBException(msg, instance.initialized);
         } else {
            String msg = messages().message("startup.alreadyInitialized");
            logger().error(msg);
            throw new OpenEJBException(msg);
         }
      } else {
         instance = appServer == null ? new Instance(initProps) : new Instance(initProps, appServer);
      }
   }

   private static Logger logger() {
      return Logger.getInstance(LogCategory.OPENEJB_STARTUP, "org.apache.openejb.util.resources");
   }

   public static boolean isInitialized() {
      return instance != null || SystemInstance.get().getComponent(ContainerSystem.class) != null;
   }

   public static class Instance {
      private final Throwable initialized;

      public Instance(Properties props) throws OpenEJBException {
         this(props, new ServerFederation());
      }

      public Instance(Properties initProps, ApplicationServer appServer) throws OpenEJBException {
         if (appServer == null) {
            throw new IllegalArgumentException("appServer must not be null");
         } else {
            this.initialized = new InitializationException("Initialized at " + new Date());

            try {
               SystemInstance.init(initProps);
               Logger.configure();
               OptionsLog.install();
            } catch (Exception e) {
               throw new OpenEJBException(e);
            }

            SystemInstance system = SystemInstance.get();
            Logger logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, "org.apache.openejb.util.resources");
            system.setComponent(DeploymentExceptionManager.class, new DeploymentExceptionManager());
            system.setComponent(ApplicationServer.class, appServer);
            OpenEjbVersion versionInfo = OpenEjbVersion.get();
            if (!system.getOptions().get("openejb.nobanner", true)) {
               versionInfo.print(System.out);
            }

            Logger logger2 = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
            String[] bannerValues = new String[]{null, versionInfo.getUrl(), (new Date()).toString(), versionInfo.getCopyright(), versionInfo.getVersion(), versionInfo.getDate(), versionInfo.getTime(), null};

            for(int i = 0; i < bannerValues.length; ++i) {
               if (bannerValues[i] == null) {
                  logger2.info("startup.banner." + i);
               } else {
                  logger2.info("startup.banner." + i, bannerValues[i]);
               }
            }

            logger.info("openejb.home = " + system.getHome().getDirectory().getAbsolutePath());
            logger.info("openejb.base = " + system.getBase().getDirectory().getAbsolutePath());
            CdiBuilder.initializeOWB();
            String className = system.getOptions().get("openejb.assembler", (String)null);
            logger.debug("startup.instantiatingAssemblerClass", className);

            Assembler assembler;
            try {
               assembler = (Assembler)(className == null ? new org.apache.openejb.assembler.classic.Assembler() : (Assembler)SafeToolkit.getToolkit("OpenEJB").newInstance(className));
            } catch (OpenEJBException oe) {
               logger.fatal("startup.assemblerCannotBeInstantiated", (Throwable)oe);
               throw oe;
            } catch (Throwable t) {
               String msg = OpenEJB.messages().message("startup.openejbEncounteredUnexpectedError");
               logger.fatal(msg, t);
               throw new OpenEJBException(msg, t);
            }

            try {
               assembler.init(system.getProperties());
            } catch (OpenEJBException oe) {
               logger.fatal("startup.assemblerFailedToInitialize", (Throwable)oe);
               throw oe;
            } catch (Throwable t) {
               String msg = OpenEJB.messages().message("startup.assemblerEncounteredUnexpectedError");
               logger.fatal(msg, t);
               throw new OpenEJBException(msg, t);
            }

            try {
               assembler.build();
            } catch (OpenEJBException oe) {
               logger.fatal("startup.assemblerFailedToBuild", (Throwable)oe);
               throw oe;
            } catch (Throwable t) {
               String msg = OpenEJB.messages().message("startup.assemblerEncounterUnexpectedBuildError");
               logger.fatal(msg, t);
               throw new OpenEJBException(msg, t);
            }

            ContainerSystem containerSystem = assembler.getContainerSystem();
            if (containerSystem == null) {
               String msg = OpenEJB.messages().message("startup.assemblerReturnedNullContainer");
               logger.fatal(msg);
               throw new OpenEJBException(msg);
            } else {
               system.setComponent(ContainerSystem.class, containerSystem);
               if (logger.isDebugEnabled()) {
                  logger.debug("startup.debugContainers", containerSystem.containers().length);
                  if (containerSystem.containers().length > 0) {
                     Container[] c = containerSystem.containers();
                     logger.debug("startup.debugContainersType");

                     for(int i = 0; i < c.length; ++i) {
                        String entry = "   ";
                        switch (c[i].getContainerType()) {
                           case BMP_ENTITY:
                              entry = entry + "BMP ENTITY  ";
                              break;
                           case CMP_ENTITY:
                              entry = entry + "CMP ENTITY  ";
                              break;
                           case STATEFUL:
                              entry = entry + "STATEFUL    ";
                              break;
                           case STATELESS:
                              entry = entry + "STATELESS   ";
                              break;
                           case MESSAGE_DRIVEN:
                              entry = entry + "MESSAGE     ";
                        }

                        entry = entry + c[i].getContainerID();
                        logger.debug("startup.debugEntry", entry);
                     }
                  }

                  logger.debug("startup.debugDeployments", containerSystem.deployments().length);
                  if (containerSystem.deployments().length > 0) {
                     logger.debug("startup.debugDeploymentsType");
                     BeanContext[] d = containerSystem.deployments();

                     for(int i = 0; i < d.length; ++i) {
                        String entry = "   ";
                        switch (d[i].getComponentType()) {
                           case BMP_ENTITY:
                              entry = entry + "BMP_ENTITY  ";
                              break;
                           case CMP_ENTITY:
                              entry = entry + "CMP_ENTITY  ";
                              break;
                           case STATEFUL:
                              entry = entry + "STATEFUL    ";
                              break;
                           case MANAGED:
                              entry = entry + "MANAGED     ";
                              break;
                           case STATELESS:
                              entry = entry + "STATELESS   ";
                              break;
                           case SINGLETON:
                              entry = entry + "SINGLETON   ";
                              break;
                           case MESSAGE_DRIVEN:
                              entry = entry + "MESSAGE     ";
                        }

                        entry = entry + d[i].getDeploymentID();
                        logger.debug("startup.debugEntry", entry);
                     }
                  }
               }

               SecurityService securityService = assembler.getSecurityService();
               if (securityService == null) {
                  String msg = OpenEJB.messages().message("startup.assemblerReturnedNullSecurityService");
                  logger.fatal(msg);
                  throw new OpenEJBException(msg);
               } else {
                  logger.debug("startup.securityService", securityService.getClass().getName());
                  system.setComponent(SecurityService.class, securityService);
                  TransactionManager transactionManager = assembler.getTransactionManager();
                  if (transactionManager == null) {
                     String msg = OpenEJB.messages().message("startup.assemblerReturnedNullTransactionManager");
                     logger.fatal(msg);
                     throw new OpenEJBException(msg);
                  } else {
                     logger.debug("startup.transactionManager", transactionManager.getClass().getName());
                     system.setComponent(TransactionManager.class, transactionManager);
                     logger.debug("startup.ready");
                  }
               }
            }
         }
      }

      public Throwable getInitialized() {
         return this.initialized.fillInStackTrace();
      }
   }

   public static class InitializationException extends Exception {
      public InitializationException(String message) {
         super(message);
      }
   }
}
