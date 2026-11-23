package org.apache.deltaspike.core.impl.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesConfigSource extends BaseConfigSource {
   private final Properties properties;

   protected PropertiesConfigSource(Properties properties) {
      this.properties = properties;
   }

   public String getPropertyValue(String key) {
      return this.properties.getProperty(key);
   }

   public Map<String, String> getProperties() {
      Map<String, String> result = new HashMap();

      for(String propertyName : this.properties.stringPropertyNames()) {
         result.put(propertyName, this.properties.getProperty(propertyName));
      }

      return result;
   }

   public boolean isScannable() {
      return true;
   }
}
