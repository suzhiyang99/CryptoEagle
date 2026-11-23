package org.apache.openejb.core.ivm.naming.java;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.core.ivm.ContextHandler;
import org.apache.openejb.core.ivm.naming.ContextWrapper;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class javaURLContextFactory implements ObjectFactory {
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable env) throws NamingException {
      return getContext();
   }

   public static Context getContext() {
      ThreadContext callContext = ThreadContext.getThreadContext();
      if (callContext != null) {
         BeanContext di = callContext.getBeanContext();
         if (di != null) {
            return di.getJndiEnc();
         } else {
            ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
            return containerSystem.getJNDIContext();
         }
      } else {
         ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
         ClassLoader current = Thread.currentThread().getContextClassLoader();
         Context globalContext = containerSystem.getJNDIContext();
         if (current == null) {
            return globalContext;
         } else {
            for(AppContext appContext : containerSystem.getAppContexts()) {
               for(WebContext web : appContext.getWebContexts()) {
                  if (current.equals(web.getClassLoader())) {
                     return new ContextHandler(web.getJndiEnc());
                  }
               }

               if (current.equals(appContext.getClassLoader())) {
                  return new ContextHandler(appContext.getAppJndiContext());
               }
            }

            return globalContext;
         }
      }
   }

   private static class ContextWithglobalFallbackWrapper extends ContextWrapper {
      public ContextWithglobalFallbackWrapper(Context first) {
         super(first);
      }

      public Object lookup(Name name) throws NamingException {
         try {
            return super.lookup(name);
         } catch (NameNotFoundException nnfe) {
            try {
               return ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup(name);
            } catch (NameNotFoundException var4) {
               throw nnfe;
            }
         }
      }

      public Object lookup(String name) throws NamingException {
         try {
            return super.lookup(name);
         } catch (NameNotFoundException nnfe) {
            try {
               return ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup(name);
            } catch (NameNotFoundException var4) {
               throw nnfe;
            }
         }
      }
   }
}
