package org.apache.openejb.config;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ModuleProperties implements DynamicDeployer {
   private static final Logger log;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Properties overrides = new Properties();
      overrides.putAll(SystemInstance.get().getProperties());
      overrides.putAll(appModule.getProperties());

      for(DeploymentModule module : appModule.getDeploymentModule()) {
         readProperties(module);
         applyOverrides(overrides, module);
      }

      return appModule;
   }

   private static void readProperties(DeploymentModule module) throws OpenEJBException {
      Object o = module.getAltDDs().get("module.properties");
      if (o instanceof URL) {
         URL url = (URL)o;

         try {
            Properties properties = IO.readProperties(url);
            module.getProperties().putAll(properties);
         } catch (IOException e) {
            throw new OpenEJBException("Cannot read module.properties: " + url, e);
         }
      } else if (o instanceof Properties) {
         module.getProperties().putAll((Properties)o);
      } else if (o != null) {
         throw new OpenEJBException("Unknown module.properties type: " + o.getClass().getName());
      }

   }

   private static void applyOverrides(Properties overrides, DeploymentModule module) {
      String id = module.getModuleId() + ".";

      for(Map.Entry<Object, Object> entry : overrides.entrySet()) {
         String key = entry.getKey().toString();
         if (key.startsWith(id)) {
            String property = key.substring(id.length());
            if (module.getProperties().containsKey(property)) {
               log.debug("Overriding module " + module.getModuleId() + " property " + property + "=" + entry.getValue());
            } else {
               log.debug("Adding module " + module.getModuleId() + " property " + property + "=" + entry.getValue());
            }

            module.getProperties().put(property, entry.getValue());
         }
      }

   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, ModuleProperties.class);
   }
}
