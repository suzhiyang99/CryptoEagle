package org.apache.openejb.core.security.jaas;

import java.security.Principal;

public class GroupPrincipal implements Principal {
   private final String name;
   private transient int hash;

   public GroupPrincipal(String name) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null");
      } else {
         this.name = name;
      }
   }

   public String getName() {
      return this.name;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GroupPrincipal that = (GroupPrincipal)o;
         return this.name.equals(that.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      if (this.hash == 0) {
         this.hash = this.name.hashCode();
      }

      return this.hash;
   }

   public String toString() {
      return this.name;
   }
}
