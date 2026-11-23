package org.apache.openejb.core.ivm.naming.openejb;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class openejbURLContextFactory implements ObjectFactory {
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable env) throws NamingException {
      return getContext();
   }

   public static Context getContext() throws NamingException {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      Context context = (Context)containerSystem.getJNDIContext().lookup("openejb");
      return context;
   }
}
