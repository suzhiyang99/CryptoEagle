package org.apache.openejb.monitoring;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ObjectNameBuilder {
   private final Map<String, String> map;
   private String domain;

   public ObjectNameBuilder() {
      this("default");
   }

   public ObjectNameBuilder(String domain) {
      this.map = new LinkedHashMap();
      this.domain = domain;
   }

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public ObjectName build() {
      StringBuilder sb = new StringBuilder(this.domain + ":");

      try {
         for(Map.Entry<String, String> entry : this.map.entrySet()) {
            String sv = (String)entry.getValue();
            if (null != sv) {
               sv = sv.replace(':', '_');
            }

            sb.append((String)entry.getKey()).append("=").append(sv).append(",");
         }

         sb.deleteCharAt(sb.lastIndexOf(","));
         return new ObjectName(sb.toString());
      } catch (MalformedObjectNameException e) {
         throw new IllegalStateException("Failed to build valid name for: " + sb.toString(), e);
      }
   }

   public ObjectNameBuilder set(String key, String value) {
      if (value != null && !value.isEmpty()) {
         this.map.put(key, value);
      } else {
         this.map.put(key, "<empty>");
      }

      return this;
   }

   public ObjectNameBuilder copy() {
      ObjectNameBuilder builder = new ObjectNameBuilder();
      builder.domain = this.domain;
      builder.map.putAll(this.map);
      return builder;
   }

   public static ObjectName uniqueName(String type, String name, Object object) {
      ObjectNameBuilder jmxName = new ObjectNameBuilder("openejb.management");
      jmxName.set("ObjectType", type);
      jmxName.set("DataSource", name);
      ObjectName objectName = jmxName.build();
      MBeanServer server = LocalMBeanServer.get();
      if (server.isRegistered(objectName)) {
         jmxName.set("DataSource", name + "(" + System.identityHashCode(object) + ")");
         objectName = jmxName.build();
      }

      return objectName;
   }
}
