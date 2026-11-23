package org.apache.openejb.assembler.classic.event;

import java.util.Collection;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.observer.Event;

@Event
public class AssemblerAfterApplicationCreated {
   private final AppInfo app;
   private final AppContext context;
   private final Collection<BeanContext> deployedEjbs;

   public AssemblerAfterApplicationCreated(AppInfo appInfo, AppContext appContext, Collection<BeanContext> ejbs) {
      this.app = appInfo;
      this.context = appContext;
      this.deployedEjbs = ejbs;
   }

   public AppInfo getApp() {
      return this.app;
   }

   public AppContext getContext() {
      return this.context;
   }

   public Collection<BeanContext> getDeployedEjbs() {
      return this.deployedEjbs;
   }

   public String toString() {
      return "AssemblerAfterApplicationCreated{app=" + this.app.appId + "}";
   }
}
