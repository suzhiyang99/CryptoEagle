package org.apache.openejb.resource;

import java.util.Properties;

public class PropertiesFactory {
   private Properties properties;

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public Properties create() {
      return this.properties;
   }
}
