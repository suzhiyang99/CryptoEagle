package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadRealm;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;

public class StoreCRealm extends AbstractReadRealm<AuthenticatorContainer> {
   public StoreCRealm() {
      super("Authenticator crealm value");
   }

   protected void setRealm(String realm, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setCRealm(realm);
   }
}
