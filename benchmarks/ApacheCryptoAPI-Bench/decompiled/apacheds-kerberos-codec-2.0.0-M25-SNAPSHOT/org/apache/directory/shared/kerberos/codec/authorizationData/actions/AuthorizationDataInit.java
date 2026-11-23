package org.apache.directory.shared.kerberos.codec.authorizationData.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationDataInit extends GrammarAction<AuthorizationDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AuthorizationDataInit.class);
   private static final boolean IS_DEBUG;

   public AuthorizationDataInit() {
      super("Creates a AuthorizationData instance");
   }

   public void action(AuthorizationDataContainer authorizationDataContainer) throws DecoderException {
      TLV tlv = authorizationDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         if (authorizationDataContainer.getAuthorizationData() == null) {
            AuthorizationData authData = new AuthorizationData();
            authorizationDataContainer.setAuthorizationData(authData);
            if (IS_DEBUG) {
               LOG.debug("AuthorizationData created");
            }
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
