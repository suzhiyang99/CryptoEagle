package org.apache.openejb.persistence;

import javax.persistence.EntityManagerFactory;

public class EntityManagerTxKey {
   private final EntityManagerFactory entityManagerFactory;

   public EntityManagerTxKey(EntityManagerFactory entityManagerFactory) {
      this.entityManagerFactory = entityManagerFactory;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EntityManagerTxKey that = (EntityManagerTxKey)o;
         return this.entityManagerFactory.equals(that.entityManagerFactory);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.entityManagerFactory.hashCode();
   }

   public String toString() {
      return this.entityManagerFactory.toString();
   }
}
