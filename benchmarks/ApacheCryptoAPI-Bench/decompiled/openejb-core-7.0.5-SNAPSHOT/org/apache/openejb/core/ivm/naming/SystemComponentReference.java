package org.apache.openejb.core.ivm.naming;

import javax.naming.NameNotFoundException;
import org.apache.openejb.loader.SystemInstance;

public class SystemComponentReference extends Reference {
   private final Class<? extends Object> type;

   public SystemComponentReference(Class<? extends Object> type) {
      if (type == null) {
         throw new NullPointerException("type is null");
      } else {
         this.type = type;
      }
   }

   public Object getObject() throws javax.naming.NamingException {
      Object component = SystemInstance.get().getComponent(this.type);
      if (component == null) {
         throw new NameNotFoundException("No " + this.type.getSimpleName() + " registered with the OpenEJB system");
      } else {
         return component;
      }
   }
}
