package org.apache.directory.shared.kerberos.codec.principalName.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.principalName.PrincipalNameContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalNameInit extends GrammarAction<PrincipalNameContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(PrincipalNameInit.class);
   private static final boolean IS_DEBUG;

   public PrincipalNameInit() {
      super("Creates a PrincipalName instance");
   }

   public void action(PrincipalNameContainer principalNameContainer) throws DecoderException {
      TLV tlv = principalNameContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         PrincipalName principalName = new PrincipalName();
         principalNameContainer.setPrincipalName(principalName);
         if (IS_DEBUG) {
            LOG.debug("PrincipalName created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
