package org.apache.openejb.cdi;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.webbeans.spi.ContextsService;

public final class ScopeHelper {
   private ScopeHelper() {
   }

   public static void startContexts(ContextsService contextsService, ServletContext servletContext, HttpSession session) throws Exception {
      contextsService.startContext(SessionScoped.class, session);
      contextsService.startContext(RequestScoped.class, (Object)null);
      contextsService.startContext(ConversationScoped.class, (Object)null);
   }

   public static void stopContexts(ContextsService contextsService, ServletContext servletContext, HttpSession session) throws Exception {
      contextsService.endContext(SessionScoped.class, session);
      contextsService.endContext(RequestScoped.class, (Object)null);
      contextsService.endContext(ConversationScoped.class, (Object)null);
      if (CdiAppContextsService.class.isInstance(contextsService)) {
         ((CdiAppContextsService)CdiAppContextsService.class.cast(contextsService)).removeThreadLocals();
      }

   }
}
