package org.apache.openejb.config;

import java.io.ByteArrayInputStream;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.Jndi;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.wls.JaxbWls;
import org.apache.openejb.jee.wls.WeblogicEjbJar;
import org.apache.openejb.jee.wls.WeblogicEnterpriseBean;
import org.apache.openejb.loader.IO;

public class WlsConversion implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.convertModule(ejbModule, appModule.getCmpMappings());
      }

      return appModule;
   }

   private <T> T getDescriptor(EjbModule ejbModule, String descriptor, Class<T> type) {
      Object altDD = ejbModule.getAltDDs().get(descriptor);
      if (altDD instanceof String) {
         try {
            altDD = JaxbWls.unmarshal(type, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var7) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbWls.unmarshal(type, IO.read((URL)altDD));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (altDD == null) {
         return null;
      } else {
         if (altDD instanceof JAXBElement) {
            JAXBElement jaxbElement = (JAXBElement)altDD;
            altDD = jaxbElement.getValue();
         }

         return (T)altDD;
      }
   }

   public void convertModule(EjbModule ejbModule, EntityMappings entityMappings) {
      WeblogicEjbJar weblogicEjbJar = (WeblogicEjbJar)this.getDescriptor(ejbModule, "weblogic-ejb-jar.xml", WeblogicEjbJar.class);
      this.mergeEjbConfig(ejbModule, weblogicEjbJar);
   }

   private void mergeEjbConfig(EjbModule ejbModule, WeblogicEjbJar weblogicEjbJar) {
      OpenejbJar openejbJar = ejbModule.getOpenejbJar();
      if (openejbJar != null) {
         if (weblogicEjbJar != null) {
            if (weblogicEjbJar.getWeblogicEnterpriseBean().size() != 0) {
               for(WeblogicEnterpriseBean ejb : weblogicEjbJar.getWeblogicEnterpriseBean()) {
                  EjbDeployment deployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(ejb.getEjbName());
                  if (deployment != null) {
                     if (ejb.getJndiName() != null) {
                        deployment.getJndi().add(new Jndi(ejb.getJndiName(), "RemoteHome"));
                     }

                     if (ejb.getLocalJndiName() != null) {
                        deployment.getJndi().add(new Jndi(ejb.getLocalJndiName(), "LocalHome"));
                     }
                  }
               }

            }
         }
      }
   }
}
