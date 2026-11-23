package org.apache.directory.shared.kerberos.codec.krbCred.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.messages.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreTickets extends GrammarAction<KrbCredContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreTickets.class);
   private static final boolean IS_DEBUG;

   public void action(KrbCredContainer krbCredContainer) throws DecoderException {
      TLV tlv = krbCredContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder decoder = new Asn1Decoder();
         TicketContainer ticketContainer = new TicketContainer(krbCredContainer.getStream());
         krbCredContainer.rewind();

         try {
            decoder.decode(krbCredContainer.getStream(), ticketContainer);
         } catch (DecoderException e) {
            throw e;
         }

         Ticket ticket = ticketContainer.getTicket();
         krbCredContainer.getKrbCred().addTicket(ticket);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         krbCredContainer.updateParent();
         if (IS_DEBUG) {
            LOG.debug("Ticket : {}", ticket);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
