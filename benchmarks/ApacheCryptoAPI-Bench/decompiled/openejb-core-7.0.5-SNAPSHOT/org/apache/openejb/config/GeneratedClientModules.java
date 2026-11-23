package org.apache.openejb.config;

import java.util.ArrayList;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.ApplicationClient;

public class GeneratedClientModules {
   public static class Add implements DynamicDeployer {
      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         for(EjbModule ejbModule : appModule.getEjbModules()) {
            if (ejbModule.getClientModule() != null) {
               appModule.getClientModules().add(ejbModule.getClientModule());
               ejbModule.setClientModule((ClientModule)null);
            }
         }

         return appModule;
      }
   }

   public static class Prune implements DynamicDeployer {
      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         for(ClientModule clientModule : new ArrayList(appModule.getClientModules())) {
            boolean haveMainClassAndDescriptor = clientModule.getMainClass() != null && clientModule.getApplicationClient() != null;
            boolean haveAnnotatedClients = clientModule.getLocalClients().size() > 0 || clientModule.getRemoteClients().size() > 0;
            if (clientModule.isEjbModuleGenerated() && !haveMainClassAndDescriptor && !haveAnnotatedClients) {
               appModule.getClientModules().remove(clientModule);
            } else if (clientModule.getApplicationClient() == null) {
               clientModule.setApplicationClient(new ApplicationClient());
            }
         }

         return appModule;
      }
   }
}
