package org.apache.openejb.config.rules;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.TransactionType;

public class CheckUserTransactionRefs extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         if (bean.getTransactionType() == TransactionType.CONTAINER) {
            for(ResourceEnvRef resRef : bean.getResourceEnvRef()) {
               if ("javax.transaction.UserTransaction".equals(resRef.getResourceEnvRefType())) {
                  this.fail(bean, "userTransactionRef.forbiddenForCmtdBeans", new Object[]{resRef.getName()});
               }
            }
         }
      }

   }
}
