package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;

public class StoreSeqNumber extends AbstractReadInteger<AuthenticatorContainer> {
   public StoreSeqNumber() {
      super("Authenticator seq-number", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setSeqNumber(value);
      authenticatorContainer.setGrammarEndAllowed(true);
   }
}
