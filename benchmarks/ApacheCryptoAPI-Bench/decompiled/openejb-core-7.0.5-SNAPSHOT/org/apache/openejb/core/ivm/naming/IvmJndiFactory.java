package org.apache.openejb.core.ivm.naming;

import java.util.Map;
import javax.naming.Context;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.JndiFactory;

public class IvmJndiFactory implements JndiFactory {
   private final Context jndiRootContext = IvmContext.createRootContext();

   public IvmJndiFactory() {
      try {
         this.jndiRootContext.bind("openejb/local/.", "");
         this.jndiRootContext.bind("openejb/remote/.", "");
         this.jndiRootContext.bind("openejb/client/.", "");
         this.jndiRootContext.bind("openejb/Deployment/.", "");
         this.jndiRootContext.bind("openejb/global/.", "");
      } catch (javax.naming.NamingException e) {
         throw new OpenEJBRuntimeException("this should not happen", e);
      }
   }

   public Context createComponentContext(Map<String, Object> bindings) throws SystemException {
      IvmContext context = new IvmContext();

      try {
         context.bind((String)"java:comp/env/dummy", "dummy");
      } catch (javax.naming.NamingException e) {
         throw new SystemException("Unable to create subcontext 'java:comp/env'.  Exception:" + e.getMessage(), e);
      }

      for(Map.Entry<String, Object> entry : bindings.entrySet()) {
         String name = (String)entry.getKey();
         Object value = entry.getValue();
         if (value != null) {
            try {
               context.bind(name, value);
            } catch (javax.naming.NamingException e) {
               throw new SystemException("Unable to bind '" + name + "' into bean's enc.", e);
            }
         }
      }

      return context;
   }

   public Context createRootContext() {
      return this.jndiRootContext;
   }
}
