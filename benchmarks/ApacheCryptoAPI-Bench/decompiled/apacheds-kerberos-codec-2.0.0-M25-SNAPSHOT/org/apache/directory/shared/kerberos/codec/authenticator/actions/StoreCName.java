package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPrincipalName;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class StoreCName extends AbstractReadPrincipalName<AuthenticatorContainer> {
   public StoreCName() {
      super("Authenticator cname");
   }

   protected void setPrincipalName(PrincipalName principalName, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setCName(principalName);
   }
}
