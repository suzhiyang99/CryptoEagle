package org.apache.spark.status.api.v1;

import javax.servlet.ServletContext;
import org.spark_project.jetty.server.handler.ContextHandler;

public final class UIRootFromServletContext$ {
   public static final UIRootFromServletContext$ MODULE$;
   private final String attribute;

   static {
      new UIRootFromServletContext$();
   }

   private String attribute() {
      return this.attribute;
   }

   public void setUiRoot(ContextHandler contextHandler, UIRoot uiRoot) {
      contextHandler.setAttribute(this.attribute(), uiRoot);
   }

   public UIRoot getUiRoot(ServletContext context) {
      return (UIRoot)context.getAttribute(this.attribute());
   }

   private UIRootFromServletContext$() {
      MODULE$ = this;
      this.attribute = this.getClass().getCanonicalName();
   }
}
