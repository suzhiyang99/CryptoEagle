package org.apache.openejb.config;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;

public class SystemPropertiesOverride implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Properties properties = new Properties();

      for(Map.Entry<Object, Object> entry : SystemInstance.get().getProperties().entrySet()) {
         String key = entry.getKey().toString();

         for(String prefix : Arrays.asList("openejb.", "tomee.")) {
            if (key.startsWith(prefix)) {
               String property = key.substring(prefix.length());
               properties.put(property, entry.getValue());
            }
         }
      }

      SystemInstance.get().getProperties().putAll(properties);
      return appModule;
   }
}
