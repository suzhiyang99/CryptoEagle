package org.apache.directory.shared.kerberos.codec.apReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.messages.ApReq;
import org.apache.directory.shared.kerberos.messages.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreTicket extends GrammarAction<ApReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreTicket.class);
   private static final boolean IS_DEBUG;

   public StoreTicket() {
      super("AP-REQ Store Ticket");
   }

   public void action(ApReqContainer apReqContainer) throws DecoderException {
      TLV tlv = apReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder ticketDecoder = new Asn1Decoder();
         TicketContainer ticketContainer = new TicketContainer(apReqContainer.getStream());

         try {
            ticketDecoder.decode(apReqContainer.getStream(), ticketContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         apReqContainer.updateParent();
         Ticket ticket = ticketContainer.getTicket();
         ApReq apReq = apReqContainer.getApReq();
         apReq.setTicket(ticket);
         if (IS_DEBUG) {
            LOG.debug("Stored ticket:  {}", ticket);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
