package org.apache.openejb.core;

import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import org.apache.openejb.BeanContext;

public class InstanceContext {
   private final BeanContext beanContext;
   private final Object bean;
   private final Map<String, Object> interceptors;
   private final CreationalContext creationalContext;
   private Object instanceData;

   public InstanceContext(BeanContext beanContext, Object bean, Map<String, Object> interceptors, CreationalContext creationalContext) {
      this.beanContext = beanContext;
      this.bean = bean;
      this.interceptors = interceptors;
      this.creationalContext = creationalContext;
   }

   public CreationalContext getCreationalContext() {
      return this.creationalContext;
   }

   public BeanContext getBeanContext() {
      return this.beanContext;
   }

   public Object getBean() {
      return this.bean;
   }

   public Map<String, Object> getInterceptors() {
      return this.interceptors;
   }

   public Object getInstanceData() {
      return this.instanceData;
   }

   public void setInstanceData(Object instanceData) {
      this.instanceData = instanceData;
   }
}
