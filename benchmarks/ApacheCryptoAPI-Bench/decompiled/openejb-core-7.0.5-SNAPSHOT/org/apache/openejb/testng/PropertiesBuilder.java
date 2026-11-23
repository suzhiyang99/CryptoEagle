package org.apache.openejb.testng;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesBuilder {
   private final Properties properties = new Properties();

   public PropertiesBuilder p(String key, String value) {
      return this.property(key, value);
   }

   public PropertiesBuilder property(String key, String value) {
      this.properties.setProperty(key, value);
      return this;
   }

   public Properties build() {
      return this.properties;
   }

   public Map<String, String> asMap() {
      Map<String, String> map = new HashMap();

      for(Map.Entry<Object, Object> entry : this.properties.entrySet()) {
         map.put(String.class.cast(entry.getKey()), String.class.cast(entry.getValue()));
      }

      return map;
   }
}
