package org.apache.openejb.core.security.jacc;

import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyContextException;
import org.apache.openejb.assembler.classic.DelegatePermissionCollection;
import org.apache.openejb.loader.SystemInstance;

public class BasicPolicyConfiguration implements PolicyConfiguration {
   static final int OPEN = 1;
   static final int IN_SERVICE = 2;
   static final int DELETED = 3;
   private final String contextID;
   private int state;
   protected final Map<String, PermissionCollection> rolePermissionsMap = new LinkedHashMap();
   protected PermissionCollection unchecked;
   protected PermissionCollection excluded;

   protected BasicPolicyConfiguration(String contextID) {
      this.contextID = contextID;
      this.state = 1;
   }

   public String getContextID() throws PolicyContextException {
      return this.contextID;
   }

   public boolean implies(ProtectionDomain domain, Permission permission) {
      if (this.excluded != null && this.excluded.implies(permission)) {
         return false;
      } else if (this.unchecked != null && this.unchecked.implies(permission)) {
         return true;
      } else {
         Principal[] principals = domain.getPrincipals();
         if (principals.length == 0) {
            return false;
         } else {
            RoleResolver roleResolver = (RoleResolver)SystemInstance.get().getComponent(RoleResolver.class);

            for(String role : roleResolver.getLogicalRoles(principals, this.rolePermissionsMap.keySet())) {
               PermissionCollection permissions = (PermissionCollection)this.rolePermissionsMap.get(role);
               if (permissions != null && permissions.implies(permission)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public void addToRole(String roleName, PermissionCollection permissions) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         Enumeration e = permissions.elements();

         while(e.hasMoreElements()) {
            this.addToRole(roleName, (Permission)e.nextElement());
         }

      }
   }

   public void addToRole(String roleName, Permission permission) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         PermissionCollection permissions = (PermissionCollection)this.rolePermissionsMap.get(roleName);
         if (permissions == null) {
            permissions = new DelegatePermissionCollection();
            this.rolePermissionsMap.put(roleName, permissions);
         }

         permissions.add(permission);
      }
   }

   public void addToUncheckedPolicy(PermissionCollection permissions) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         Enumeration e = permissions.elements();

         while(e.hasMoreElements()) {
            this.addToUncheckedPolicy((Permission)e.nextElement());
         }

      }
   }

   public void addToUncheckedPolicy(Permission permission) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         if (this.unchecked == null) {
            this.unchecked = new DelegatePermissionCollection();
         }

         this.unchecked.add(permission);
      }
   }

   public void addToExcludedPolicy(PermissionCollection permissions) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         Enumeration e = permissions.elements();

         while(e.hasMoreElements()) {
            this.addToExcludedPolicy((Permission)e.nextElement());
         }

      }
   }

   public void addToExcludedPolicy(Permission permission) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         if (this.excluded == null) {
            this.excluded = new DelegatePermissionCollection();
         }

         this.excluded.add(permission);
      }
   }

   public void removeRole(String roleName) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         this.rolePermissionsMap.remove(roleName);
      }
   }

   public void removeUncheckedPolicy() throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         this.unchecked = null;
      }
   }

   public void removeExcludedPolicy() throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         this.excluded = null;
      }
   }

   public void linkConfiguration(PolicyConfiguration link) throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      }
   }

   public void delete() throws PolicyContextException {
      this.state = 3;
   }

   public void commit() throws PolicyContextException {
      if (this.state != 1) {
         throw new UnsupportedOperationException("Not in an open state");
      } else {
         this.state = 2;
      }
   }

   public boolean inService() throws PolicyContextException {
      return this.state == 2;
   }

   public void open(boolean remove) {
      if (remove) {
         this.rolePermissionsMap.clear();
         this.unchecked = null;
         this.excluded = null;
      }

      this.state = 1;
   }

   int getState() {
      return this.state;
   }

   public interface RoleResolver {
      Set<String> getLogicalRoles(Principal[] var1, Set<String> var2);
   }
}
