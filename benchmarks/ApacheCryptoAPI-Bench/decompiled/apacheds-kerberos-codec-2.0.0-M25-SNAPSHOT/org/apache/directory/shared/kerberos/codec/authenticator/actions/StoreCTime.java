package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.actions.AbstractReadKerberosTime;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;

public class StoreCTime extends AbstractReadKerberosTime<AuthenticatorContainer> {
   public StoreCTime() {
      super("Stores the CTime");
   }

   protected void setKerberosTime(KerberosTime krbtime, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setCTime(krbtime);
      authenticatorContainer.setGrammarEndAllowed(true);
   }
}
