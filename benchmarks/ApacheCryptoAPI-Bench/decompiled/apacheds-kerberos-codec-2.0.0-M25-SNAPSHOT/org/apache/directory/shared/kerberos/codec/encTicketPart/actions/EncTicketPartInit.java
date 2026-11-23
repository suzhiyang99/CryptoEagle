package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.components.EncTicketPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncTicketPartInit extends GrammarAction<EncTicketPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncTicketPartInit.class);
   private static final boolean IS_DEBUG;

   public EncTicketPartInit() {
      super("Creates a EncTicketPart instance");
   }

   public void action(EncTicketPartContainer encTicketPartContainer) throws DecoderException {
      TLV tlv = encTicketPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         EncTicketPart encTicketPart = new EncTicketPart();
         encTicketPartContainer.setEncTicketPart(encTicketPart);
         if (IS_DEBUG) {
            LOG.debug("EncTicketPart created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
