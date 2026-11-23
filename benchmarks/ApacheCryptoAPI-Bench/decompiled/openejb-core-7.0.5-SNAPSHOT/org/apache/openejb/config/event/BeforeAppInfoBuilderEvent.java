package org.apache.openejb.config.event;

import org.apache.openejb.config.AppModule;
import org.apache.openejb.observer.Event;

@Event
public class BeforeAppInfoBuilderEvent {
   private final AppModule appModule;

   public BeforeAppInfoBuilderEvent(AppModule appModule) {
      this.appModule = appModule;
   }

   public AppModule getAppModule() {
      return this.appModule;
   }

   public String toString() {
      return "BeforeAppInfoBuilderEvent{appModule=" + this.appModule + '}';
   }
}
