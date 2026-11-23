package org.apache.openejb.core.ivm.naming;

import org.apache.openejb.BeanContext;

public class BusinessLocalBeanReference extends Reference {
   private final BeanContext.BusinessLocalBeanHome businessHome;

   public BusinessLocalBeanReference(BeanContext.BusinessLocalBeanHome localBeanHome) {
      this.businessHome = localBeanHome;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.businessHome.create();
   }
}
