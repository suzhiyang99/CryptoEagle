package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadPvno;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;

public class StoreAuthenticatorVno extends AbstractReadPvno<AuthenticatorContainer> {
   public StoreAuthenticatorVno() {
      super("Authenticator vno");
   }

   protected void setPvno(int pvno, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setProtocolVersionNumber(pvno);
   }
}
