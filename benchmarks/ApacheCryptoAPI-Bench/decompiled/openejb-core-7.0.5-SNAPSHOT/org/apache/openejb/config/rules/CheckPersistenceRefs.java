package org.apache.openejb.config.rules;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceContextType;
import org.apache.openejb.jee.SessionBean;

public class CheckPersistenceRefs extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         if (bean instanceof SessionBean) {
            SessionBean sessionBean = (SessionBean)bean;
            if (sessionBean.getSessionType() == null) {
               continue;
            }
         }

         String beanType = this.getType(bean);
         if (!beanType.equals("Stateful")) {
            for(PersistenceContextRef ref : bean.getPersistenceContextRef()) {
               if (this.isExtented(ref)) {
                  String refName = ref.getName();
                  String prefix = bean.getEjbClass() + "/";
                  if (refName.startsWith(prefix)) {
                     refName = refName.substring(prefix.length());
                  }

                  this.fail(bean, "persistenceContextExtented.nonStateful", new Object[]{refName, beanType});
               }
            }
         }
      }

   }

   private boolean isExtented(PersistenceContextRef ref) {
      PersistenceContextType type = ref.getPersistenceContextType();
      return type != null && type.equals(PersistenceContextType.EXTENDED);
   }

   private String getType(EnterpriseBean bean) {
      if (bean instanceof SessionBean) {
         SessionBean sessionBean = (SessionBean)bean;
         switch (sessionBean.getSessionType()) {
            case STATEFUL:
               return "Stateful";
            case STATELESS:
               return "Stateless";
            case SINGLETON:
               return "Singleton";
            case MANAGED:
               return "Managed";
            default:
               throw new IllegalArgumentException("Uknown SessionBean type " + bean.getClass());
         }
      } else if (bean instanceof MessageDrivenBean) {
         return "MessageDriven";
      } else if (bean instanceof EntityBean) {
         return "EJB 2.1 Entity";
      } else {
         throw new IllegalArgumentException("Uknown bean type " + bean.getClass());
      }
   }
}
