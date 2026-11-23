package org.apache.directory.shared.kerberos.codec.krbError.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreSName extends AbstractReadPrincipalName<KrbErrorContainer> {
   public StoreSName() {
      super("KRB-ERROR sname");
   }

   protected void setPrincipalName(PrincipalName principalName, KrbErrorContainer krbErrorContainer) {
      krbErrorContainer.getKrbError().setSName(principalName);
      krbErrorContainer.setGrammarEndAllowed(true);
   }
}
