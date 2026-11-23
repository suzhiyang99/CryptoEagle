package org.apache.deltaspike.core.impl.config;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.impl.util.JndiUtils;

@Typed
class LocalJndiConfigSource extends BaseConfigSource {
   private static final String BASE_NAME = "java:comp/env/deltaspike/";

   public LocalJndiConfigSource() {
      this.initOrdinal(200);
   }

   public String getPropertyValue(String key) {
      try {
         return (String)JndiUtils.lookup(this.getJndiKey(key), String.class);
      } catch (Exception var3) {
         return null;
      }
   }

   private String getJndiKey(String key) {
      return key.startsWith("java:comp/env") ? key : "java:comp/env/deltaspike/" + key;
   }

   public Map<String, String> getProperties() {
      Map<String, String> result = new HashMap();
      result.putAll(JndiUtils.list("java:comp/env/deltaspike/", String.class));
      result.putAll(JndiUtils.list("java:comp/env", String.class));
      return result;
   }

   public String getConfigName() {
      return "java:comp/env/deltaspike/";
   }

   public boolean isScannable() {
      return false;
   }
}
