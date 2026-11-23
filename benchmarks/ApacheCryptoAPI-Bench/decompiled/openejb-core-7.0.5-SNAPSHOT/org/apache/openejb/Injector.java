package org.apache.openejb;

import javax.annotation.ManagedBean;
import javax.enterprise.context.spi.CreationalContext;
import org.apache.openejb.api.LocalClient;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.inject.OWBInjector;

public class Injector {
   private static Logger logger;

   public static <T> T inject(T object) {
      assert object != null;

      Class<?> clazz = object.getClass();
      BeanContext context = resolve(clazz);
      if (context != null) {
         InjectionProcessor processor = new InjectionProcessor(object, context.getInjections(), context.getJndiContext());
         cdiInjections(context, object);

         try {
            return (T)processor.createInstance();
         } catch (OpenEJBException e) {
            throw new InjectionException(clazz.getName(), e);
         }
      } else if (!isAnnotatedLocalClient(clazz)) {
         throw new NoInjectionMetaDataException(clazz.getName());
      } else {
         try {
            InjectionProcessor<?> processor = ClientInjections.clientInjector(object);
            cdiInjections((BeanContext)null, object);
            return (T)processor.createInstance();
         } catch (OpenEJBException e) {
            throw new NoInjectionMetaDataException("Injection failed", e);
         }
      }
   }

   private static <T> void cdiInjections(BeanContext context, T object) {
      if (context != null && context.getWebBeansContext() != null) {
         ThreadContext oldContext = null;
         ThreadContext callContext = new ThreadContext(context, (Object)null, Operation.INJECTION);
         oldContext = ThreadContext.enter(callContext);

         try {
            OWBInjector.inject(context.getWebBeansContext().getBeanManagerImpl(), object, (CreationalContext)null);
         } catch (Throwable t) {
            logger().warning("an error occured while injecting the class '" + object.getClass().getName() + "': " + t.getMessage());
         } finally {
            ThreadContext.exit(oldContext);
         }

      }
   }

   private static boolean isAnnotatedLocalClient(Class<?> clazz) {
      for(Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
         if (current.getAnnotation(LocalClient.class) != null) {
            return true;
         }
      }

      return false;
   }

   public static BeanContext resolve(Class<?> clazz) {
      for(ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         BeanContext context = containerSystem.getBeanContext(clazz.getName());
         if (context != null) {
            return context;
         }

         for(BeanContext context : containerSystem.deployments()) {
            if (clazz == context.getBeanClass()) {
               return context;
            }
         }
      }

      return null;
   }

   private static Logger logger() {
      if (logger == null) {
         logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, OpenEjbContainer.class);
      }

      return logger;
   }

   public static class NoInjectionMetaDataException extends IllegalStateException {
      public NoInjectionMetaDataException(String s) {
         this(s, (Exception)null);
      }

      public NoInjectionMetaDataException(String s, Exception e) {
         super(String.format("%s : Annotate the class with @%s so it can be discovered in the application scanning process", s, ManagedBean.class.getName()), e);
      }
   }

   public static class InjectionException extends IllegalStateException {
      public InjectionException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
