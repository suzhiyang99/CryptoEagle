package org.apache.openejb.core.singleton;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import javax.enterprise.context.spi.CreationalContext;

public class Instance {
   public final Object bean;
   public final Map<String, Object> interceptors;
   public final ReadWriteLock lock;
   public final CreationalContext creationalContext;

   public Instance(Object bean, Map<String, Object> interceptors, CreationalContext creationalContext, ReadWriteLock lock) {
      this.bean = bean;
      this.interceptors = interceptors;
      this.lock = lock;
      this.creationalContext = creationalContext;
   }
}
