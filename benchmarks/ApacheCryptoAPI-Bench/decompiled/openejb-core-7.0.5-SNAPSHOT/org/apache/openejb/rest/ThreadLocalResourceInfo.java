package org.apache.openejb.rest;

import java.lang.reflect.Method;
import javax.ws.rs.container.ResourceInfo;

public class ThreadLocalResourceInfo extends AbstractRestThreadLocalProxy<ResourceInfo> implements ResourceInfo {
   protected ThreadLocalResourceInfo() {
      super(ResourceInfo.class);
   }

   public Method getResourceMethod() {
      return ((ResourceInfo)this.get()).getResourceMethod();
   }

   public Class<?> getResourceClass() {
      return ((ResourceInfo)this.get()).getResourceClass();
   }
}
