package org.apache.openejb.config.sys;

import java.util.Properties;
import org.apache.openejb.util.SuperProperties;

public class PojoConfig {
   private final Properties properties = (new SuperProperties()).caseInsensitive(true);

   public Properties getProperties() {
      return this.properties;
   }

   public boolean hasProperties() {
      return this.properties.size() > 0;
   }
}
