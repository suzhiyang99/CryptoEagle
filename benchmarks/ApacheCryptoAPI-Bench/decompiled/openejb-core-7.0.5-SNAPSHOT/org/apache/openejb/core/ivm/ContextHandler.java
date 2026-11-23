package org.apache.openejb.core.ivm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ivm.naming.ContextWrapper;
import org.apache.openejb.core.ivm.naming.IvmContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class ContextHandler extends ContextWrapper {
   public ContextHandler(Context jndiContext) {
      super(jndiContext);
   }

   public Object lookup(Name name) throws NamingException {
      try {
         return this.context.lookup(name);
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
         return "java:".equals(name) ? this.context : this.context.lookup(name);
      } catch (UndeclaredThrowableException ute) {
         Throwable e = ute.getUndeclaredThrowable();

         while(e != null) {
            if (InvocationTargetException.class.isInstance(e)) {
               Throwable unwrap = ((InvocationTargetException)InvocationTargetException.class.cast(e)).getCause();
               if (e == unwrap) {
                  throw new NameNotFoundException(name);
               }

               e = unwrap;
            } else {
               if (!UndeclaredThrowableException.class.isInstance(e)) {
                  break;
               }

               Throwable unwrap = ((UndeclaredThrowableException)UndeclaredThrowableException.class.cast(e)).getUndeclaredThrowable();
               if (e == unwrap) {
                  throw new NameNotFoundException(name);
               }

               e = unwrap;
            }

            if (NameNotFoundException.class.isInstance(e)) {
               throw (NameNotFoundException)NameNotFoundException.class.cast(e);
            }
         }

         throw new NameNotFoundException(name);
      } catch (NameNotFoundException nnfe) {
         try {
            return ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup(name);
         } catch (NameNotFoundException var6) {
            try {
               ThreadContext threadContext = ThreadContext.getThreadContext();
               if (threadContext != null) {
                  return threadContext.getBeanContext().getModuleContext().getModuleJndiContext().lookup(name);
               }
            } catch (Exception var5) {
            }

            throw nnfe;
         }
      }
   }

   public void setReadOnly() {
      if (IvmContext.class.isInstance(this.context)) {
         ((IvmContext)IvmContext.class.cast(this.context)).setReadOnly(true);
      }

   }
}
