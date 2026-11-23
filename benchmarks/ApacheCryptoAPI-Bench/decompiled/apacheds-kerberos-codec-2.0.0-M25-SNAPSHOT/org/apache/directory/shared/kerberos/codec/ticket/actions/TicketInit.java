package org.apache.directory.shared.kerberos.codec.ticket.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.messages.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketInit extends GrammarAction<TicketContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(TicketInit.class);

   public TicketInit() {
      super("Ticket initialization");
   }

   public void action(TicketContainer ticketContainer) throws DecoderException {
      TLV tlv = ticketContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         Ticket ticket = new Ticket();
         ticketContainer.setTicket(ticket);
      }
   }
}
