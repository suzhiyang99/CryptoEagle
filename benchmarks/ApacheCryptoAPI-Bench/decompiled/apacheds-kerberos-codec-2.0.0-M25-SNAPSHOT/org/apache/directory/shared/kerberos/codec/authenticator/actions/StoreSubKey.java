package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadEncryptionKey;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class StoreSubKey extends AbstractReadEncryptionKey<AuthenticatorContainer> {
   public StoreSubKey() {
      super("Authenticator subkey");
   }

   protected void setEncryptionKey(EncryptionKey encryptionKey, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setSubKey(encryptionKey);
      authenticatorContainer.setGrammarEndAllowed(true);
   }
}
