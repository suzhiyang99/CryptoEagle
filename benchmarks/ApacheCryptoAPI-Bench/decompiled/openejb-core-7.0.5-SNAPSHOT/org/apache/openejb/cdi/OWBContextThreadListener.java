package org.apache.openejb.cdi;

import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.ModuleContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ThreadContextListener;
import org.apache.openejb.loader.SystemInstance;
import org.apache.webbeans.config.WebBeansContext;

public class OWBContextThreadListener implements ThreadContextListener {
   private final ThreadSingletonService singletonService = (ThreadSingletonService)SystemInstance.get().getComponent(ThreadSingletonService.class);

   public void contextEntered(ThreadContext oldContext, ThreadContext newContext) {
      BeanContext beanContext = newContext.getBeanContext();
      if (beanContext != null) {
         ModuleContext moduleContext = beanContext.getModuleContext();
         AppContext appContext = moduleContext.getAppContext();
         WebBeansContext owbContext = appContext.getWebBeansContext();
         Object oldOWBContext;
         if (owbContext != null) {
            oldOWBContext = this.singletonService.contextEntered(owbContext);
         } else {
            oldOWBContext = null;
         }

         OWBContextHolder holder = new OWBContextHolder(oldOWBContext);
         newContext.set(OWBContextHolder.class, holder);
      }
   }

   public void contextExited(ThreadContext exitedContext, ThreadContext reenteredContext) {
      OWBContextHolder oldOWBContext = (OWBContextHolder)exitedContext.get(OWBContextHolder.class);
      if (oldOWBContext == null) {
         throw new NullPointerException("OWBContext not set in this thread");
      } else {
         Object oldOWBContextContext = oldOWBContext.getContext();
         if (oldOWBContextContext != null) {
            this.singletonService.contextExited(oldOWBContextContext);
         }

      }
   }

   private static final class OWBContextHolder {
      private final Object context;

      private OWBContextHolder(Object context) {
         this.context = context;
      }

      public Object getContext() {
         return this.context;
      }
   }
}
