package org.apache.openejb.config.typed.util;

import java.util.Map;
import java.util.Properties;
import org.apache.openejb.config.sys.AbstractService;

public class Builders {
   public static Properties getProperties(AbstractService service) {
      ObjectMap map = new ObjectMap(service);
      Properties properties = new Properties();

      for(Map.Entry<String, Object> entry : map.entrySet()) {
         Object value = entry.getValue();
         if (value != null) {
            properties.put(entry.getKey(), value.toString());
         } else {
            properties.put(entry.getKey(), "");
         }
      }

      return properties;
   }
}
