package org.apache.directory.shared.kerberos.codec.kdcReqBody.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.KdcReqBodyContainer;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.components.KdcReqBody;
import org.apache.directory.shared.kerberos.messages.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddTicket extends GrammarAction<KdcReqBodyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddTicket.class);
   private static final boolean IS_DEBUG;

   public AddTicket() {
      super("KDC-REQ-BODY Add Ticket");
   }

   public void action(KdcReqBodyContainer kdcReqBodyContainer) throws DecoderException {
      TLV tlv = kdcReqBodyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder ticketDecoder = new Asn1Decoder();
         TicketContainer ticketContainer = new TicketContainer(kdcReqBodyContainer.getStream());
         kdcReqBodyContainer.rewind();

         try {
            ticketDecoder.decode(kdcReqBodyContainer.getStream(), ticketContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         kdcReqBodyContainer.updateParent();
         Ticket ticket = ticketContainer.getTicket();
         KdcReqBody kdcReqBody = kdcReqBodyContainer.getKdcReqBody();
         kdcReqBody.addAdditionalTicket(ticket);
         kdcReqBodyContainer.setGrammarEndAllowed(true);
         if (IS_DEBUG) {
            LOG.debug("Added ticket:  {}", ticket);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
