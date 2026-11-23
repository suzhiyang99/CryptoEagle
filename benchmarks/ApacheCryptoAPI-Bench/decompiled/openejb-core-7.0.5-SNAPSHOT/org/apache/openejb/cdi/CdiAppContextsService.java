package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.annotation.InitializedLiteral;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.event.EventMetadataImpl;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.web.context.WebContextsService;

public class CdiAppContextsService extends WebContextsService implements ContextsService {
   public static final Object EJB_REQUEST_EVENT = new Object();
   private static final Logger logger;
   private static final ThreadLocal<Collection<Runnable>> endRequestRunnables;

   public CdiAppContextsService(WebBeansContext wbc) {
      super(wbc);
   }

   private void runEndRequestTasks() {
      for(Runnable r : (Collection)endRequestRunnables.get()) {
         try {
            r.run();
         } catch (Exception e) {
            logger.error(e.getMessage(), (Throwable)e);
         }
      }

      endRequestRunnables.remove();
   }

   public static void pushRequestReleasable(Runnable runnable) {
      ((Collection)endRequestRunnables.get()).add(runnable);
   }

   public void init(Object initializeObject) {
      super.init(initializeObject);
      if (initializeObject != null) {
         Object event = initializeObject;
         if (StartupObject.class.isInstance(initializeObject)) {
            StartupObject so = (StartupObject)StartupObject.class.cast(initializeObject);
            if (so.isFromWebApp()) {
               event = so.getWebContext().getServletContext();
            } else if (so.getAppInfo().webAppAlone) {
               event = SystemInstance.get().getComponent(ServletContext.class);
            }
         } else if (ServletContextEvent.class.isInstance(initializeObject)) {
            event = ((ServletContextEvent)ServletContextEvent.class.cast(initializeObject)).getServletContext();
         }

         if (!FiredManually.class.isInstance(event)) {
            this.applicationStarted(event);
         }
      }

   }

   public void applicationStarted(Object event) {
      Object appEvent = event != null ? event : this.applicationContext;
      this.webBeansContext.getBeanManagerImpl().fireEvent(appEvent, new EventMetadataImpl((Type)null, ServletContext.class.isInstance(appEvent) ? ServletContext.class : Object.class, (InjectionPoint)null, new Annotation[]{InitializedLiteral.INSTANCE_APPLICATION_SCOPED}, this.webBeansContext), false);
   }

   public void destroy(Object destroyObject) {
      super.destroy(destroyObject);
      this.removeThreadLocals();
   }

   protected void destroyRequestContext(Object requestEvent) {
      this.runEndRequestTasks();
      super.destroyRequestContext(requestEvent);
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB.createChild("cdi"), CdiAppContextsService.class);
      endRequestRunnables = new ThreadLocal<Collection<Runnable>>() {
         protected Collection<Runnable> initialValue() {
            return new ArrayList();
         }
      };
   }

   public interface FiredManually {
   }
}
