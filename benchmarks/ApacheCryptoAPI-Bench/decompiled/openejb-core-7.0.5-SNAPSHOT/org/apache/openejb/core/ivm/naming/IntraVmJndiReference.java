package org.apache.openejb.core.ivm.naming;

import javax.naming.NameNotFoundException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Strings;

public class IntraVmJndiReference extends Reference {
   private final String jndiName;

   public IntraVmJndiReference(String jndiName) {
      this.jndiName = jndiName;
   }

   public String getJndiName() {
      return this.jndiName;
   }

   public Object getObject() throws javax.naming.NamingException {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

      try {
         return containerSystem.getJNDIContext().lookup(this.jndiName);
      } catch (NameNotFoundException var3) {
         return containerSystem.getJNDIContext().lookup("java:module/" + Strings.lastPart(this.getClassName(), '.'));
      } catch (javax.naming.NamingException e) {
         throw (javax.naming.NamingException)(new javax.naming.NamingException("could not look up " + this.jndiName)).initCause(e);
      }
   }

   public String toString() {
      return "IntraVmJndiReference{jndiName='" + this.jndiName + '\'' + '}';
   }
}
