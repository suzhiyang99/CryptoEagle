package org.apache.openejb.config;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ApplicationProperties implements DynamicDeployer {
   private static final Logger log;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      this.readPropertiesFiles(appModule);
      this.applyOverrides(appModule);
      return appModule;
   }

   private void readPropertiesFiles(AppModule appModule) throws OpenEJBException {
      Collection<DeploymentModule> deploymentModule = appModule.getDeploymentModule();
      deploymentModule.add(appModule);

      for(DeploymentModule module : deploymentModule) {
         Object o = module.getAltDDs().get("application.properties");
         if (o instanceof URL) {
            URL url = (URL)o;

            try {
               Properties properties = IO.readProperties(url);
               appModule.getProperties().putAll(properties);
            } catch (IOException e) {
               throw new OpenEJBException("Cannot read application.properties: " + url, e);
            }
         } else if (o instanceof Properties) {
            appModule.getProperties().putAll((Properties)o);
         } else if (o != null) {
            throw new OpenEJBException("Unknown application.properties type: " + o.getClass().getName());
         }
      }

   }

   private void applyOverrides(AppModule appModule) {
      String id = appModule.getModuleId() + ".";
      Properties properties = SystemInstance.get().getProperties();

      for(Map.Entry<Object, Object> entry : properties.entrySet()) {
         String key = entry.getKey().toString();
         if (key.startsWith(id)) {
            String property = key.substring(id.length());
            if (appModule.getProperties().containsKey(property)) {
               log.debug("Overriding application " + appModule.getModuleId() + " property " + property + "=" + entry.getValue());
            } else {
               log.debug("Adding application " + appModule.getModuleId() + " property " + property + "=" + entry.getValue());
            }

            appModule.getProperties().put(property, entry.getValue());
         }
      }

   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, ApplicationProperties.class);
   }
}
