package org.apache.openejb.core.ivm.naming;

import java.util.Map;

public class MapObjectReference extends Reference {
   private final ThreadLocal<Map<String, Object>> obj;
   private final String key;

   public MapObjectReference(ThreadLocal<Map<String, Object>> obj, String key) {
      this.obj = obj;
      this.key = key;
   }

   public Object getObject() throws javax.naming.NamingException {
      return ((Map)this.obj.get()).get(this.key);
   }
}
