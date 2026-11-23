package org.apache.openejb.config;

import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbLocalRef;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.EjbLink;
import org.apache.openejb.jee.oejb3.ResourceLink;

public class ApplyOpenejbJar implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         Map<String, EjbDeployment> ejbDeployments = ejbModule.getOpenejbJar().getDeploymentsByEjbName();

         for(EnterpriseBean enterpriseBean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            EjbDeployment ejbDeployment = (EjbDeployment)ejbDeployments.get(enterpriseBean.getEjbName());
            enterpriseBean.setId(ejbDeployment.getDeploymentId());

            for(ResourceRef ref : enterpriseBean.getResourceRef()) {
               ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
               if (resourceLink != null && resourceLink.getResId() != null) {
                  ref.setMappedName(resourceLink.getResId());
               }
            }

            for(ResourceEnvRef ref : enterpriseBean.getResourceEnvRef()) {
               ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
               if (resourceLink != null && resourceLink.getResId() != null) {
                  ref.setMappedName(resourceLink.getResId());
               }
            }

            for(MessageDestinationRef ref : enterpriseBean.getMessageDestinationRef()) {
               ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
               if (resourceLink != null && resourceLink.getResId() != null) {
                  ref.setMappedName(resourceLink.getResId());
               }
            }

            for(EjbRef ref : enterpriseBean.getEjbRef()) {
               EjbLink ejbLink = ejbDeployment.getEjbLink(ref.getName());
               if (ejbLink != null && ejbLink.getDeployentId() != null) {
                  ref.setMappedName(ejbLink.getDeployentId());
               }
            }

            for(EjbLocalRef ref : enterpriseBean.getEjbLocalRef()) {
               EjbLink ejbLink = ejbDeployment.getEjbLink(ref.getName());
               if (ejbLink != null && ejbLink.getDeployentId() != null) {
                  ref.setMappedName(ejbLink.getDeployentId());
               }
            }
         }
      }

      return appModule;
   }
}
