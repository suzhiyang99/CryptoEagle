package org.apache.deltaspike.core.impl.config;

import java.util.Map;

public abstract class MapConfigSource extends BaseConfigSource {
   private final Map<String, String> map;

   public MapConfigSource(Map<String, String> map) {
      this.map = map;
   }

   public Map<String, String> getProperties() {
      return this.map;
   }

   public String getPropertyValue(String key) {
      return (String)this.map.get(key);
   }

   public boolean isScannable() {
      return true;
   }
}
