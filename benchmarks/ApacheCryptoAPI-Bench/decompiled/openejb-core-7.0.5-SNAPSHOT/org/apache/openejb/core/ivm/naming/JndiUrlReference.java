package org.apache.openejb.core.ivm.naming;

import javax.naming.InitialContext;

public class JndiUrlReference extends Reference {
   private final String jndiName;

   public JndiUrlReference(String jndiName) {
      this.jndiName = jndiName;
   }

   public Object getObject() throws javax.naming.NamingException {
      return (new InitialContext()).lookup(this.jndiName);
   }

   public String getJndiName() {
      return this.jndiName;
   }
}
