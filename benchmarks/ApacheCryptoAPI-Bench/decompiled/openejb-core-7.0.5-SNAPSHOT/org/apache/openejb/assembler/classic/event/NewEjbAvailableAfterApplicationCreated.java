package org.apache.openejb.assembler.classic.event;

import java.util.Collection;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.observer.Event;

@Event
public class NewEjbAvailableAfterApplicationCreated {
   private final AppInfo app;
   private final Collection<BeanContext> beanContexts;
   private final String s;

   public NewEjbAvailableAfterApplicationCreated(AppInfo appInfo, Collection<BeanContext> beanContexts) {
      this.app = appInfo;
      this.beanContexts = beanContexts;
      this.s = "NewEjbAvailableAfterApplicationCreated{appId=" + this.app.appId + ", beanContexts=" + beanContexts + "}";
   }

   public AppInfo getApp() {
      return this.app;
   }

   public Collection<BeanContext> getBeanContexts() {
      return this.beanContexts;
   }

   public String toString() {
      return this.s;
   }
}
