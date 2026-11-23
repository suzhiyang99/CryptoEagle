package org.apache.openejb.assembler.classic;

import java.security.PermissionCollection;
import java.util.HashMap;
import java.util.Map;

public class PolicyContext {
   private final PermissionCollection excludedPermissions = new DelegatePermissionCollection();
   private final PermissionCollection uncheckedPermissions = new DelegatePermissionCollection();
   private final Map<String, PermissionCollection> rolePermissions = new HashMap();
   private final String contextId;

   public PolicyContext(String contextId) {
      this.contextId = contextId;
   }

   public PermissionCollection getExcludedPermissions() {
      return this.excludedPermissions;
   }

   public PermissionCollection getUncheckedPermissions() {
      return this.uncheckedPermissions;
   }

   public Map<String, PermissionCollection> getRolePermissions() {
      return this.rolePermissions;
   }

   public String getContextID() {
      return this.contextId;
   }
}
