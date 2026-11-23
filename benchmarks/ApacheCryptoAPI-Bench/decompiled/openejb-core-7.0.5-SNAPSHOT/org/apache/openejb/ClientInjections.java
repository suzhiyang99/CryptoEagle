package org.apache.openejb;

import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public final class ClientInjections {
   private ClientInjections() {
   }

   public static InjectionProcessor<?> clientInjector(Object object) throws OpenEJBException {
      if (object == null) {
         throw new NullPointerException("Object supplied to 'inject' operation is null");
      } else {
         Context clients;
         try {
            clients = (Context)((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb/client/");
         } catch (NamingException e) {
            throw new OpenEJBException(object.getClass().getName(), e);
         }

         Context ctx = null;
         List<Injection> injections = null;

         for(Class<?> current = object.getClass(); current != null && !current.equals(Object.class); current = current.getSuperclass()) {
            try {
               String moduleId = (String)clients.lookup(current.getName());
               ctx = (Context)clients.lookup(moduleId);
               injections = (List)ctx.lookup("info/injections");
               break;
            }
         }

         if (injections == null) {
            throw new OpenEJBException("Unable to find injection meta-data for " + object.getClass().getName() + ".  Ensure that class was annotated with @" + LocalClient.class.getName() + " and was successfully discovered and deployed.  See http://tomee.apache.org/3.0/local-client-injection.html");
         } else {
            return new InjectionProcessor(object, injections, ctx);
         }
      }
   }
}
