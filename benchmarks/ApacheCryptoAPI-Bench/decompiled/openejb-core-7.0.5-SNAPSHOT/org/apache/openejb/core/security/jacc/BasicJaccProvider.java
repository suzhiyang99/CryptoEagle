package org.apache.openejb.core.security.jacc;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import org.apache.openejb.core.security.JaccProvider;

public class BasicJaccProvider extends JaccProvider {
   private final Map<String, BasicPolicyConfiguration> configurations = new HashMap();
   private final java.security.Policy systemPolicy = JaccProvider.Policy.getPolicy();

   public PolicyConfiguration getPolicyConfiguration(String contextID, boolean remove) throws PolicyContextException {
      BasicPolicyConfiguration configuration = (BasicPolicyConfiguration)this.configurations.get(contextID);
      if (configuration == null) {
         configuration = this.createPolicyConfiguration(contextID);
         this.configurations.put(contextID, configuration);
      } else {
         configuration.open(remove);
      }

      return configuration;
   }

   protected BasicPolicyConfiguration createPolicyConfiguration(String contextID) {
      return new BasicPolicyConfiguration(contextID);
   }

   public boolean inService(String contextID) throws PolicyContextException {
      PolicyConfiguration configuration = this.getPolicyConfiguration(contextID, false);
      return configuration.inService();
   }

   public PermissionCollection getPermissions(CodeSource codesource) {
      return this.systemPolicy == null ? null : this.systemPolicy.getPermissions(codesource);
   }

   public void refresh() {
   }

   public boolean implies(ProtectionDomain domain, Permission permission) {
      String contextID = PolicyContext.getContextID();
      if (contextID != null) {
         try {
            BasicPolicyConfiguration configuration = (BasicPolicyConfiguration)this.configurations.get(contextID);
            if (configuration != null && configuration.inService()) {
               return configuration.implies(domain, permission);
            }

            return false;
         } catch (PolicyContextException var5) {
         }
      }

      return this.systemPolicy != null ? this.systemPolicy.implies(domain, permission) : false;
   }

   static {
      try {
         Class.forName(PolicyContext.class.getName());
      } catch (ClassNotFoundException var1) {
      }

   }
}
