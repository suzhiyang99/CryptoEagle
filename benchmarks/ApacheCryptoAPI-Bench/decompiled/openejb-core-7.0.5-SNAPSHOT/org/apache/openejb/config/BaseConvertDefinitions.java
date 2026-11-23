package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.openejb.jee.JndiConsumer;

public abstract class BaseConvertDefinitions implements DynamicDeployer {
   protected String cleanUpName(String factory) {
      String name = factory.replaceFirst("java:comp/env/", "");
      name = name.replaceFirst("java:/", "");
      name = name.replaceFirst("java:", "");
      return name;
   }

   protected List<JndiConsumer> collectConsumers(AppModule appModule) {
      List<JndiConsumer> jndiConsumers = new ArrayList();

      for(ClientModule module : appModule.getClientModules()) {
         JndiConsumer consumer = module.getApplicationClient();
         if (consumer != null) {
            jndiConsumers.add(consumer);
         }
      }

      for(WebModule webModule : appModule.getWebModules()) {
         JndiConsumer consumer = webModule.getWebApp();
         if (consumer != null) {
            jndiConsumers.add(consumer);
         }
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         Collections.addAll(jndiConsumers, ejbModule.getEjbJar().getEnterpriseBeans());
      }

      if (appModule.getApplication() != null) {
         jndiConsumers.add(appModule.getApplication());
      }

      return jndiConsumers;
   }
}
