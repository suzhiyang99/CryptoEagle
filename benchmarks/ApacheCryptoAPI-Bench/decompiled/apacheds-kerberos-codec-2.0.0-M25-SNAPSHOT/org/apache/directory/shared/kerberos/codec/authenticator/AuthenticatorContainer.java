package org.apache.directory.shared.kerberos.codec.authenticator;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.Authenticator;

public class AuthenticatorContainer extends AbstractContainer {
   private Authenticator authenticator;

   public AuthenticatorContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(AuthenticatorGrammar.getInstance());
      this.setTransition(AuthenticatorStatesEnum.START_STATE);
   }

   public Authenticator getAuthenticator() {
      return this.authenticator;
   }

   public void setAuthenticator(Authenticator authenticator) {
      this.authenticator = authenticator;
   }
}
