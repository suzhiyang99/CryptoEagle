package org.apache.openejb.cdi;

import org.apache.openejb.observer.Event;
import org.apache.webbeans.config.WebBeansContext;

@Event
public class WebBeansContextCreated {
   private final WebBeansContext context;

   public WebBeansContextCreated(WebBeansContext webBeansContext) {
      this.context = webBeansContext;
   }

   public WebBeansContext getContext() {
      return this.context;
   }

   public String toString() {
      return "WebBeansContextCreated{context=" + this.context + '}';
   }
}
