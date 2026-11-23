package org.apache.openejb.assembler.classic;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.ArrayEnumeration;

public class DelegatePermissionCollection extends PermissionCollection {
   private static final String PERMISSION_COLLECTION_CLASS = "openejb.permission-collection.class";
   private final PermissionCollection pc = getPermissionCollection();

   public void add(Permission permission) {
      this.pc.add(permission);
   }

   public boolean implies(Permission permission) {
      return this.pc.implies(permission);
   }

   public Enumeration<Permission> elements() {
      return this.pc.elements();
   }

   public static PermissionCollection getPermissionCollection() {
      try {
         return (PermissionCollection)DelegatePermissionCollection.class.getClassLoader().loadClass(SystemInstance.get().getOptions().get("openejb.permission-collection.class", FastPermissionCollection.class.getName())).newInstance();
      } catch (Exception var1) {
         return new FastPermissionCollection();
      }
   }

   public static class FastPermissionCollection extends PermissionCollection {
      private static final int MAX_CACHE_SIZE = SystemInstance.get().getOptions().get("openejb.permission-collection.cache.size", 3000);
      private final List<Permission> permissions = new ArrayList();
      private final Map<Permission, Boolean> alreadyEvaluatedPermissions = new ConcurrentHashMap();

      public synchronized void add(Permission permission) {
         this.permissions.add(permission);
      }

      public synchronized boolean implies(Permission permission) {
         if (this.alreadyEvaluatedPermissions.containsKey(permission)) {
            return (Boolean)this.alreadyEvaluatedPermissions.get(permission);
         } else {
            if (this.alreadyEvaluatedPermissions.size() > MAX_CACHE_SIZE) {
               this.alreadyEvaluatedPermissions.clear();
            }

            for(Permission perm : this.permissions) {
               if (perm.implies(permission)) {
                  this.alreadyEvaluatedPermissions.put(permission, true);
                  return true;
               }
            }

            this.alreadyEvaluatedPermissions.put(permission, false);
            return false;
         }
      }

      public synchronized Enumeration<Permission> elements() {
         return new ArrayEnumeration(this.permissions);
      }
   }
}
