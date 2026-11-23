package org.apache.directory.shared.kerberos.codec.krbCredInfo.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StorePName extends AbstractReadPrincipalName<KrbCredInfoContainer> {
   public StorePName() {
      super("KrbCredInfo pname");
   }

   protected void setPrincipalName(PrincipalName principalName, KrbCredInfoContainer krbCredInfoContainer) {
      krbCredInfoContainer.getKrbCredInfo().setpName(principalName);
      krbCredInfoContainer.setGrammarEndAllowed(true);
   }
}
