package org.apache.openejb.resource.jdbc.router;

import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public abstract class AbstractRouter implements Router {
   private static final String OPENEJB_RESOURCE = "openejb:Resource/";
   private final Context ctx = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext();

   protected Object getJndiResource(String name) throws NamingException {
      return this.ctx.lookup(name);
   }

   protected Object getOpenEJBResource(String name) throws NamingException {
      return this.getJndiResource("openejb:Resource/" + name);
   }
}
