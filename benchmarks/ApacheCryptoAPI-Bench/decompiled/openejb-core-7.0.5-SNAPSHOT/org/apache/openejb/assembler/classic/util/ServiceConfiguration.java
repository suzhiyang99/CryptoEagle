package org.apache.openejb.assembler.classic.util;

import java.util.Collection;
import java.util.Properties;
import org.apache.openejb.assembler.classic.ServiceInfo;

public class ServiceConfiguration {
   private final Properties properties;
   private final Collection<ServiceInfo> availableServices;

   public ServiceConfiguration(Properties properties, Collection<ServiceInfo> availableServices) {
      if (properties == null) {
         this.properties = new Properties();
      } else {
         this.properties = properties;
      }

      this.availableServices = availableServices;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public Collection<ServiceInfo> getAvailableServices() {
      return this.availableServices;
   }
}
