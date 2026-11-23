package org.apache.openejb.core.ivm.naming;

import org.apache.openejb.BeanContext;

public class BusinessRemoteReference extends Reference {
   private final BeanContext.BusinessRemoteHome businessHome;

   public BusinessRemoteReference(BeanContext.BusinessRemoteHome businessHome) {
      this.businessHome = businessHome;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.businessHome.create();
   }
}
