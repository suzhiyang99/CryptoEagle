package org.apache.openejb.core.ivm.naming;

import javax.persistence.EntityManager;

public class PersistenceContextReference extends Reference {
   private final EntityManager em;

   public PersistenceContextReference(EntityManager em) {
      this.em = em;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.em;
   }
}
