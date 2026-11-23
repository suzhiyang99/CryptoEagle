package org.apache.openejb;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.loader.Options;
import org.apache.openejb.util.SuperProperties;

public class DeploymentContext {
   private final String id;
   private final Map<Class, Object> data = new HashMap();
   private final Properties properties = (new SuperProperties()).caseInsensitive(true);
   private final Options options;

   public DeploymentContext(String id, Options parent) {
      this.id = id;
      this.options = new Options(this.properties, parent);
   }

   public Properties getProperties() {
      return this.properties;
   }

   public <T> T get(Class<T> type) {
      return (T)this.data.get(type);
   }

   public <T> T set(Class<T> type, T value) {
      return (T)this.data.put(type, value);
   }

   public Options getOptions() {
      return this.options;
   }

   public String getId() {
      return this.id;
   }
}
