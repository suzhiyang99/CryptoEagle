package org.apache.openejb.core.ivm.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class JndiReference extends Reference {
   private Context context;
   private Hashtable envProperties;
   private final String jndiName;
   private String contextJndiName;

   public JndiReference(Context linkedContext, String jndiName) {
      this.context = linkedContext;
      this.jndiName = jndiName;
   }

   public JndiReference(String contextJndiName, String jndiName) {
      this.contextJndiName = contextJndiName;
      this.jndiName = jndiName;
   }

   public JndiReference(Hashtable envProperties, String jndiName) {
      if (envProperties != null && envProperties.size() != 0) {
         this.envProperties = envProperties;
      } else {
         this.envProperties = null;
      }

      this.jndiName = jndiName;
   }

   public Object getObject() throws javax.naming.NamingException {
      Context externalContext = this.getContext();
      synchronized(externalContext) {
         return externalContext.lookup(this.jndiName);
      }
   }

   protected Context getContext() throws javax.naming.NamingException {
      if (this.context == null) {
         if (this.contextJndiName != null) {
            ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
            this.context = (Context)containerSystem.getJNDIContext().lookup(this.contextJndiName);
         } else {
            this.context = new InitialContext(this.envProperties);
         }
      }

      return this.context;
   }
}
