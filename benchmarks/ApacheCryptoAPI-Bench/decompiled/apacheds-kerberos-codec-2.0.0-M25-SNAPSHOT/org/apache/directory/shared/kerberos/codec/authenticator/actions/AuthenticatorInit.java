package org.apache.directory.shared.kerberos.codec.authenticator.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.messages.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticatorInit extends GrammarAction<AuthenticatorContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorInit.class);
   private static final boolean IS_DEBUG;

   public AuthenticatorInit() {
      super("Creates a Authenticator instance");
   }

   public void action(AuthenticatorContainer authenticatorContainer) throws DecoderException {
      TLV tlv = authenticatorContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Authenticator authenticator = new Authenticator();
         authenticatorContainer.setAuthenticator(authenticator);
         if (IS_DEBUG) {
            LOG.debug("Authenticator created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
