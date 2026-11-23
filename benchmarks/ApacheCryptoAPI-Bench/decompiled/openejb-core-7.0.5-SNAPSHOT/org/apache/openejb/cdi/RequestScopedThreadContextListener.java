package org.apache.openejb.cdi;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ThreadContextListener;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContextsService;

public class RequestScopedThreadContextListener implements ThreadContextListener {
   public void contextEntered(ThreadContext oldContext, ThreadContext newContext) {
      BeanContext beanContext = newContext.getBeanContext();
      WebBeansContext webBeansContext = beanContext.getModuleContext().getAppContext().getWebBeansContext();
      if (webBeansContext != null) {
         ContextsService contextsService = webBeansContext.getContextsService();
         Context requestContext = ((CdiAppContextsService)CdiAppContextsService.class.cast(contextsService)).getRequestContext(false);
         if (requestContext == null) {
            contextsService.startContext(RequestScoped.class, CdiAppContextsService.EJB_REQUEST_EVENT);
            newContext.set(DestroyContext.class, new DestroyContext(contextsService, newContext));
         }

      }
   }

   public void contextExited(ThreadContext exitedContext, ThreadContext reenteredContext) {
      if (exitedContext != null) {
         DestroyContext destroyContext = (DestroyContext)exitedContext.get(DestroyContext.class);
         if (destroyContext != null && destroyContext.threadContext == exitedContext) {
            destroyContext.contextsService.endContext(RequestScoped.class, CdiAppContextsService.EJB_REQUEST_EVENT);
            ((CdiAppContextsService)CdiAppContextsService.class.cast(destroyContext.contextsService)).removeThreadLocals();
         }
      }
   }

   private static final class DestroyContext {
      private final ContextsService contextsService;
      private final ThreadContext threadContext;

      private DestroyContext(ContextsService contextsService, ThreadContext threadContext) {
         this.contextsService = contextsService;
         this.threadContext = threadContext;
      }
   }
}
