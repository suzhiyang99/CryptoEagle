package org.apache.openejb.cdi;

import org.apache.openejb.observer.Event;
import org.apache.webbeans.config.WebBeansContext;

@Event
public class WebBeansContextBeforeDeploy {
   private final WebBeansContext context;

   public WebBeansContextBeforeDeploy(WebBeansContext webBeansContext) {
      this.context = webBeansContext;
   }

   public WebBeansContext getContext() {
      return this.context;
   }

   public String toString() {
      return "WebBeansContextBeforeDeploy{context=" + this.context + '}';
   }
}
