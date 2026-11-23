package org.apache.deltaspike.core.impl.config;

class EnvironmentPropertyConfigSource extends MapConfigSource {
   public EnvironmentPropertyConfigSource() {
      super(System.getenv());
      this.initOrdinal(300);
   }

   public String getConfigName() {
      return "environment-properties";
   }

   public String getPropertyValue(String key) {
      String val = super.getPropertyValue(key);
      if (val == null || val.isEmpty()) {
         val = super.getPropertyValue(key.replace('.', '_'));
      }

      return val;
   }

   public boolean isScannable() {
      return false;
   }
}
