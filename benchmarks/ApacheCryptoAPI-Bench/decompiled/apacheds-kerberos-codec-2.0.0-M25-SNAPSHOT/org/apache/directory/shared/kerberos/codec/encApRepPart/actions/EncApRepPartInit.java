package org.apache.directory.shared.kerberos.codec.encApRepPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;
import org.apache.directory.shared.kerberos.messages.EncApRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncApRepPartInit extends GrammarAction<EncApRepPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncApRepPartInit.class);
   private static final boolean IS_DEBUG;

   public EncApRepPartInit() {
      super("Creates an EncApRepPart instance");
   }

   public void action(EncApRepPartContainer encApRepPartContainer) throws DecoderException {
      TLV tlv = encApRepPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         EncApRepPart encApRepPart = new EncApRepPart();
         encApRepPartContainer.setEncApRepPart(encApRepPart);
         if (IS_DEBUG) {
            LOG.debug("EncApRepPart created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
