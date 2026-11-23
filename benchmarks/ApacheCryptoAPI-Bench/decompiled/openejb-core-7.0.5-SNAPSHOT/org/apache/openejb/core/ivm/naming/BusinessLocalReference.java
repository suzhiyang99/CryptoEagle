package org.apache.openejb.core.ivm.naming;

import org.apache.openejb.BeanContext;

public class BusinessLocalReference extends Reference {
   private final BeanContext.BusinessLocalHome businessHome;

   public BusinessLocalReference(BeanContext.BusinessLocalHome businessHome) {
      this.businessHome = businessHome;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.businessHome.create();
   }
}
