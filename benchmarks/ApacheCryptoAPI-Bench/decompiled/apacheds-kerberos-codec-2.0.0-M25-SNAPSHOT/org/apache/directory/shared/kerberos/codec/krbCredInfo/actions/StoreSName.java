package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreSName extends AbstractReadPrincipalName<KrbCredInfoContainer> {
   public StoreSName() {
      super("KrbCredInfo sname");
   }

   protected void setPrincipalName(PrincipalName principalName, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setsName(principalName);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
