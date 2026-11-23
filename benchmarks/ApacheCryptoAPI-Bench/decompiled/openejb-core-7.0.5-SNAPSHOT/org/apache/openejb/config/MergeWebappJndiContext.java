package org.apache.openejb.config;

import java.util.Map;
import javax.ejb.EJBContext;
import javax.ejb.EntityContext;
import javax.ejb.MessageDrivenContext;
import javax.ejb.SessionContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceContextType;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.TransactionType;

public class MergeWebappJndiContext implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(WebModule webModule : appModule.getWebModules()) {
         if (webModule.getFinder() != null) {
            for(EjbModule ejbModule : appModule.getEjbModules()) {
               if (ejbModule.getFinder() != webModule.getFinder()) {
                  String forceMerge = ejbModule.getProperties().getProperty("openejb.ejbmodule.MergeWebappJndiContext");
                  if (forceMerge != null) {
                     for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
                        this.copy(webModule.getWebApp().getResourceEnvRefMap(), bean.getResourceEnvRefMap());
                     }
                  }
               } else {
                  this.merge(ejbModule, webModule);
               }
            }
         }
      }

      return appModule;
   }

   private void merge(EjbModule ejbModule, WebModule webModule) {
      JndiConsumer webApp = webModule.getWebApp();
      EjbJar ejbJar = ejbModule.getEjbJar();

      for(EnterpriseBean bean : ejbJar.getEnterpriseBeans()) {
         this.merge(bean.getEnvEntryMap(), webApp.getEnvEntryMap());
         this.merge(bean.getEjbRefMap(), webApp.getEjbRefMap());
         this.merge(bean.getEjbLocalRefMap(), webApp.getEjbLocalRefMap());
         this.merge(bean.getServiceRefMap(), webApp.getServiceRefMap());
         this.merge(bean.getResourceRefMap(), webApp.getResourceRefMap());
         this.merge(bean.getResourceEnvRefMap(), webApp.getResourceEnvRefMap());
         this.merge(bean.getMessageDestinationRefMap(), webApp.getMessageDestinationRefMap());
         this.merge(bean.getPersistenceContextRefMap(), webApp.getPersistenceContextRefMap());
         this.merge(bean.getPersistenceUnitRefMap(), webApp.getPersistenceUnitRefMap());
         this.mergeUserTransaction(bean.getResourceRefMap(), webApp.getResourceRefMap(), webApp);
         this.mergeUserTransaction(bean.getResourceEnvRefMap(), webApp.getResourceEnvRefMap(), webApp);
         this.mergeUserTransaction(webApp.getResourceRefMap(), bean.getResourceRefMap(), bean);
         this.mergeUserTransaction(webApp.getResourceEnvRefMap(), bean.getResourceEnvRefMap(), bean);
      }

      SessionBean aggregator = new SessionBean();

      for(EnterpriseBean a : ejbJar.getEnterpriseBeans()) {
         aggregator.getEnvEntryMap().putAll(a.getEnvEntryMap());
         aggregator.getEjbRefMap().putAll(a.getEjbRefMap());
         aggregator.getEjbLocalRefMap().putAll(a.getEjbLocalRefMap());
         aggregator.getServiceRefMap().putAll(a.getServiceRefMap());
         aggregator.getResourceRefMap().putAll(a.getResourceRefMap());
         aggregator.getResourceEnvRefMap().putAll(a.getResourceEnvRefMap());
         aggregator.getMessageDestinationRefMap().putAll(a.getMessageDestinationRefMap());
         aggregator.getPersistenceContextRefMap().putAll(a.getPersistenceContextRefMap());
         aggregator.getPersistenceUnitRefMap().putAll(a.getPersistenceUnitRefMap());
      }

      for(EnterpriseBean a : ejbJar.getEnterpriseBeans()) {
         this.copy(aggregator.getEnvEntryMap(), a.getEnvEntryMap());
         this.copy(aggregator.getEjbRefMap(), a.getEjbRefMap());
         this.copy(aggregator.getEjbLocalRefMap(), a.getEjbLocalRefMap());
         this.copy(aggregator.getServiceRefMap(), a.getServiceRefMap());
         this.copy(aggregator.getResourceRefMap(), a.getResourceRefMap());
         this.copy(aggregator.getResourceEnvRefMap(), a.getResourceEnvRefMap());
         this.copy(aggregator.getMessageDestinationRefMap(), a.getMessageDestinationRefMap());
         this.copy(aggregator.getPersistenceContextRefMap(), a.getPersistenceContextRefMap());
         this.copy(aggregator.getPersistenceUnitRefMap(), a.getPersistenceUnitRefMap());
         this.mergeUserTransaction(aggregator.getResourceRefMap(), a.getResourceRefMap(), a);
         this.mergeUserTransaction(aggregator.getResourceEnvRefMap(), a.getResourceEnvRefMap(), a);
      }

   }

   private <R extends JndiReference> void merge(Map<String, R> a, Map<String, R> b) {
      this.copy(a, b);
      this.copy(b, a);
   }

   private <R extends JndiReference> void copy(Map<String, R> from, Map<String, R> to) {
      for(R a : from.values()) {
         if (!this.isPrivateReference(a)) {
            R b = (R)(to.get(a.getKey()));
            if (b == null) {
               if (!isExtendedPersistenceContext(a)) {
                  to.put(a.getKey(), a);
               }
            } else {
               b.getInjectionTarget().addAll(a.getInjectionTarget());
               if (b instanceof EnvEntry && a instanceof EnvEntry) {
                  EnvEntry eb = (EnvEntry)b;
                  EnvEntry ea = (EnvEntry)a;
                  if (eb.getEnvEntryValue() == null) {
                     eb.setEnvEntryValue(ea.getEnvEntryValue());
                  }

                  if (eb.getEnvEntryType() == null) {
                     eb.setEnvEntryType(ea.getEnvEntryType());
                  }
               }
            }
         }
      }

   }

   private static <R extends JndiReference> boolean isExtendedPersistenceContext(R b) {
      return b instanceof PersistenceContextRef && PersistenceContextType.EXTENDED.equals(((PersistenceContextRef)b).getPersistenceContextType());
   }

   private <R extends JndiReference> boolean isPrivateReference(R a) {
      if (!this.isResourceRef(a)) {
         return false;
      } else {
         Class[] types = new Class[]{EJBContext.class, EntityContext.class, SessionContext.class, MessageDrivenContext.class, UserTransaction.class};

         for(Class type : types) {
            if (type.getName().equals(a.getType())) {
               return true;
            }
         }

         return false;
      }
   }

   private <R extends JndiReference> boolean isResourceRef(R a) {
      return a instanceof ResourceRef || a instanceof ResourceEnvRef;
   }

   private <R extends JndiReference> void mergeUserTransaction(Map<String, R> from, Map<String, R> to, JndiConsumer consumer) {
      if (consumer instanceof EnterpriseBean) {
         EnterpriseBean enterpriseBean = (EnterpriseBean)consumer;
         if (enterpriseBean.getTransactionType() != TransactionType.BEAN) {
            return;
         }
      }

      for(R a : from.values()) {
         if (UserTransaction.class.getName().equals(a.getType())) {
            R b = (R)(to.get(a.getKey()));
            if (b == null) {
               to.put(a.getKey(), a);
            } else {
               b.getInjectionTarget().addAll(a.getInjectionTarget());
            }
         }
      }

   }
}
