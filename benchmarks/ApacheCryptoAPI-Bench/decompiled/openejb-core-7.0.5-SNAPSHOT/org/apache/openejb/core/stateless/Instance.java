package org.apache.openejb.core.stateless;

import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import org.apache.openejb.util.Pool;

public class Instance {
   public final Object bean;
   public final Map<String, Object> interceptors;
   public final CreationalContext creationalContext;
   private Pool<Instance>.Entry poolEntry;

   public Instance(Object bean, Map<String, Object> interceptors, CreationalContext creationalContext) {
      this.bean = bean;
      this.interceptors = interceptors;
      this.creationalContext = creationalContext;
   }

   public Pool<Instance>.Entry getPoolEntry() {
      return this.poolEntry;
   }

   public void setPoolEntry(Pool<Instance>.Entry poolEntry) {
      this.poolEntry = poolEntry;
   }
}
