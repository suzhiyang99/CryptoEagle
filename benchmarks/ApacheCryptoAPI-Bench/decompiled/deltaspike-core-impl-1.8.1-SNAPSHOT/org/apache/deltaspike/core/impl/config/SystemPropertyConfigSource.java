package org.apache.deltaspike.core.impl.config;

class SystemPropertyConfigSource extends PropertiesConfigSource {
   SystemPropertyConfigSource() {
      super(System.getProperties());
      this.initOrdinal(400);
   }

   public String getConfigName() {
      return "system-properties";
   }
}
