package org.apache.directory.shared.kerberos.codec.changePwdData.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.changePwdData.ChangePasswdDataContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreTargName extends AbstractReadPrincipalName<ChangePasswdDataContainer> {
   public StoreTargName() {
      super("Kerberos change password targetName");
   }

   protected void setPrincipalName(PrincipalName principalName, ChangePasswdDataContainer ticketContainer) {
      ticketContainer.getChngPwdData().setTargName(principalName);
      ticketContainer.setGrammarEndAllowed(true);
   }
}
