package org.apache.openejb.config;

import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.Jndi;
import org.apache.openejb.jee.oejb3.OpenejbJar;

public class MappedNameBuilder implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         if (openejbJar == null) {
            return appModule;
         }

         Map<String, EjbDeployment> ejbDeployments = openejbJar.getDeploymentsByEjbName();

         for(EnterpriseBean enterpriseBean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            EjbDeployment ejbDeployment = (EjbDeployment)ejbDeployments.get(enterpriseBean.getEjbName());
            if (ejbDeployment != null) {
               String mappedName = enterpriseBean.getMappedName();
               if (mappedName != null && mappedName.length() > 0) {
                  ejbDeployment.getJndi().add(new Jndi(mappedName, "Remote"));
               }
            }
         }
      }

      return appModule;
   }
}
