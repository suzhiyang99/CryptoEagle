package org.apache.openejb.core.ivm.naming;

import javax.persistence.EntityManagerFactory;

public class PersistenceUnitReference extends Reference {
   private final EntityManagerFactory emf;

   public PersistenceUnitReference(EntityManagerFactory emf) {
      this.emf = emf;
   }

   public Object getObject() throws javax.naming.NamingException {
      return this.emf;
   }
}
