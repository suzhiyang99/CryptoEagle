package org.apache.openejb.ri.sp;

import java.security.Permission;
import java.security.PermissionCollection;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContextException;
import org.apache.openejb.util.JavaSecurityManagers;

public class PseudoPolicyConfigurationFactory extends PolicyConfigurationFactory {
   public static void install() {
      JavaSecurityManagers.setSystemProperty("javax.security.jacc.PolicyConfigurationFactory.provider", PseudoPolicyConfigurationFactory.class.getName());
   }

   public PolicyConfiguration getPolicyConfiguration(final String contextID, boolean remove) throws PolicyContextException {
      return new PolicyConfiguration() {
         public String getContextID() throws PolicyContextException {
            return contextID;
         }

         public void addToRole(String roleName, PermissionCollection permissions) throws PolicyContextException {
         }

         public void addToRole(String roleName, Permission permission) throws PolicyContextException {
         }

         public void addToUncheckedPolicy(PermissionCollection permissions) throws PolicyContextException {
         }

         public void addToUncheckedPolicy(Permission permission) throws PolicyContextException {
         }

         public void addToExcludedPolicy(PermissionCollection permissions) throws PolicyContextException {
         }

         public void addToExcludedPolicy(Permission permission) throws PolicyContextException {
         }

         public void removeRole(String roleName) throws PolicyContextException {
         }

         public void removeUncheckedPolicy() throws PolicyContextException {
         }

         public void removeExcludedPolicy() throws PolicyContextException {
         }

         public void linkConfiguration(PolicyConfiguration link) throws PolicyContextException {
         }

         public void delete() throws PolicyContextException {
         }

         public void commit() throws PolicyContextException {
         }

         public boolean inService() throws PolicyContextException {
            return false;
         }
      };
   }

   public boolean inService(String contextID) throws PolicyContextException {
      return true;
   }
}
