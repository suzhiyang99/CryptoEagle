package org.apache.openejb.core;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import org.apache.openejb.core.ivm.naming.ContextWrapper;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class OpenEJBInitialContextFactory implements InitialContextFactory {
   public Context getInitialContext(Hashtable env) throws NamingException {
      return new LocalFallbackContextWrapper(((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext());
   }

   private static final class LocalFallbackContextWrapper extends ContextWrapper {
      private final ConcurrentMap<String, String> mapping;

      private LocalFallbackContextWrapper(Context jndiContext) {
         super(jndiContext);
         this.mapping = new ConcurrentHashMap();
      }

      public Object lookup(String userName) throws NamingException {
         String jndi = (String)this.mapping.get(userName);
         if (jndi == null) {
            jndi = userName;
         }

         try {
            return super.lookup(jndi);
         } catch (NameNotFoundException nnfe) {
            if (!jndi.startsWith("java:") && !jndi.startsWith("openejb:")) {
               try {
                  String ejb = "java:openejb/local/" + jndi;
                  Object lookup = super.lookup(ejb);
                  this.mapping.put(userName, ejb);
                  return lookup;
               } catch (NameNotFoundException var8) {
                  try {
                     String resource = "java:openejb/Resource/" + jndi;
                     Object resourceInstance = super.lookup(resource);
                     this.mapping.put(userName, resource);
                     return resourceInstance;
                  } catch (NameNotFoundException var7) {
                     throw nnfe;
                  }
               }
            } else {
               throw nnfe;
            }
         }
      }
   }
}
