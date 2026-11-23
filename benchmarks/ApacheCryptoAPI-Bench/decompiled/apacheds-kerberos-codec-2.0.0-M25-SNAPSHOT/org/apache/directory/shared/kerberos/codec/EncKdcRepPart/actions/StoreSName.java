package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreSName extends AbstractReadPrincipalName<EncKdcRepPartContainer> {
   public StoreSName() {
      super("Stores the SName");
   }

   protected void setPrincipalName(PrincipalName principalName, EncKdcRepPartContainer encKdcRepPartContainer) {
      encKdcRepPartContainer.getEncKdcRepPart().setSName(principalName);
      encKdcRepPartContainer.setGrammarEndAllowed(true);
   }
}
