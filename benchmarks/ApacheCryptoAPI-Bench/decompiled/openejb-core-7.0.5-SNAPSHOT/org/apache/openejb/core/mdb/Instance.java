package org.apache.openejb.core.mdb;

import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;

public class Instance {
   public final Object bean;
   public final Map<String, Object> interceptors;
   public final CreationalContext creationalContext;

   public Instance(Object bean, Map<String, Object> interceptors, CreationalContext creationalContext) {
      this.bean = bean;
      this.interceptors = interceptors;
      this.creationalContext = creationalContext;
   }
}
