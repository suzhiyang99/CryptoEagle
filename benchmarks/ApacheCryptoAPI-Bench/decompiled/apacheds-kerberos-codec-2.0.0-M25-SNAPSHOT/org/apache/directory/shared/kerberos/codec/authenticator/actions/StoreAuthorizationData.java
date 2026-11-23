package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadAuthorizationData;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;

public class StoreAuthorizationData extends AbstractReadAuthorizationData<AuthenticatorContainer> {
   public StoreAuthorizationData() {
      super("Authenticator authorization-data");
   }

   protected void setAuthorizationData(AuthorizationData authorizationData, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setAuthorizationData(authorizationData);
      authenticatorContainer.setGrammarEndAllowed(true);
   }
}
