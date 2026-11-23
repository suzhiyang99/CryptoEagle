package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceUnitRef;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.ServiceRef;

public class GeronimoMappedName implements DynamicDeployer {
   private static final String MAPPED_NAME_PREFIX = "jndi:java:comp/geronimo/env/";

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      this.mapReferences(appModule);
      return appModule;
   }

   private void mapReferences(AppModule appModule) {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.mapReferences(ejbModule.getEjbJar());
      }

   }

   private void mapReferences(EjbJar ejbJar) {
      if (ejbJar != null) {
         for(EnterpriseBean enterpriseBean : ejbJar.getEnterpriseBeans()) {
            for(EjbRef ref : enterpriseBean.getEjbRef()) {
               String mappedName = ref.getMappedName();
               if (mappedName != null && (mappedName.startsWith("jndi:corbaloc") || mappedName.startsWith("jndi:corbaname"))) {
                  String refName = ref.getEjbRefName();
                  ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
               }

               if (null == mappedName && ref.getEjbRefName().equals("ejb/MEJB")) {
                  ref.setMappedName("mejb/ejb/mgmt/MEJB");
               }
            }

            for(MessageDestinationRef ref : enterpriseBean.getMessageDestinationRef()) {
               String refName = ref.getMessageDestinationRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }

            for(PersistenceContextRef ref : enterpriseBean.getPersistenceContextRef()) {
               String refName = ref.getPersistenceContextRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }

            for(PersistenceUnitRef ref : enterpriseBean.getPersistenceUnitRef()) {
               String refName = ref.getPersistenceUnitRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }

            for(ResourceRef ref : enterpriseBean.getResourceRef()) {
               String refName = ref.getResRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }

            for(ResourceEnvRef ref : enterpriseBean.getResourceEnvRef()) {
               String refName = ref.getResourceEnvRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }

            for(ServiceRef ref : enterpriseBean.getServiceRef()) {
               String refName = ref.getServiceRefName();
               ref.setMappedName("jndi:java:comp/geronimo/env/" + refName);
            }
         }

      }
   }
}
