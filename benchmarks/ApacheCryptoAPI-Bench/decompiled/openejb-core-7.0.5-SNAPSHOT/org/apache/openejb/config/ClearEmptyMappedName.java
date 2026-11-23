package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;

public class ClearEmptyMappedName implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         for(JndiConsumer consumer : ejbModule.getEjbJar().getEnterpriseBeans()) {
            this.clearEmptyMappedName(consumer);
         }
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         this.clearEmptyMappedName(clientModule.getApplicationClient());
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.clearEmptyMappedName(webModule.getWebApp());
      }

      return appModule;
   }

   private void clearEmptyMappedName(JndiConsumer consumer) {
      if (consumer != null) {
         List<JndiReference> refs = new ArrayList();
         refs.addAll(consumer.getEjbLocalRef());
         refs.addAll(consumer.getEjbRef());
         refs.addAll(consumer.getEnvEntry());
         refs.addAll(consumer.getMessageDestinationRef());
         refs.addAll(consumer.getPersistenceContextRef());
         refs.addAll(consumer.getPersistenceUnitRef());
         refs.addAll(consumer.getResourceEnvRef());
         refs.addAll(consumer.getResourceRef());
         refs.addAll(consumer.getServiceRef());

         for(JndiReference ref : refs) {
            if (ref.getMappedName() != null && ref.getMappedName().length() == 0) {
               ref.setMappedName((String)null);
            }
         }

      }
   }
}
