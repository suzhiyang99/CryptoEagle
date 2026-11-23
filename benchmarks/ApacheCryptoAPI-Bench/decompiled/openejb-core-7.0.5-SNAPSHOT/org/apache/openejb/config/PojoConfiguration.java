package org.apache.openejb.config;

import java.util.Properties;

public class PojoConfiguration {
   private final Properties properties;

   public PojoConfiguration(Properties properties) {
      this.properties = properties;
   }

   public Properties getProperties() {
      return this.properties;
   }
}
