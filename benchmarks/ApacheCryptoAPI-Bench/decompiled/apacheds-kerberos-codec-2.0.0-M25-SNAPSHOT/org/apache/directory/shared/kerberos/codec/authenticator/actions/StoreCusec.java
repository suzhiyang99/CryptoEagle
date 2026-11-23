package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;

public class StoreCusec extends AbstractReadInteger<AuthenticatorContainer> {
   public StoreCusec() {
      super("Authenticator cusec", 0, 999999);
   }

   protected void setIntegerValue(int value, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setCusec(value);
   }
}
