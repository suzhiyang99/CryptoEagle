package org.apache.openejb.assembler.classic.event;

import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.observer.Event;

@Event
public class AssemblerBeforeApplicationDestroyed {
   private final AppInfo app;
   private final AppContext context;

   public AssemblerBeforeApplicationDestroyed(AppInfo appInfo, AppContext appContext) {
      this.app = appInfo;
      this.context = appContext;
   }

   public AppInfo getApp() {
      return this.app;
   }

   public AppContext getContext() {
      return this.context;
   }

   public String toString() {
      return "AssemblerBeforeApplicationDestroyed{app=" + this.app.appId + "}";
   }
}
