package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreCName extends AbstractReadPrincipalName<KrbErrorContainer> {
   public StoreCName() {
      super("KRB-ERROR cname");
   }

   protected void setPrincipalName(PrincipalName principalName, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setCName(principalName);
   }
}
