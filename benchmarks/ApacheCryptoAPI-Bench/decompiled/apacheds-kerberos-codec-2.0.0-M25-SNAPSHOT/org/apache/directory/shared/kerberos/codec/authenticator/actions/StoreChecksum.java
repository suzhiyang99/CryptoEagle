package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadCheckSum;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.components.Checksum;

public class StoreChecksum extends AbstractReadCheckSum<AuthenticatorContainer> {
   public StoreChecksum() {
      super("Authenticator cksum");
   }

   protected void setChecksum(Checksum checksum, AuthenticatorContainer authenticatorContainer) {
      authenticatorContainer.getAuthenticator().setCksum(checksum);
   }
}
