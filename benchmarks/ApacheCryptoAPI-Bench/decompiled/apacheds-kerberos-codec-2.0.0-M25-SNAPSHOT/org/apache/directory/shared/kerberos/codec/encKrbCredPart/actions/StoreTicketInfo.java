package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.KrbCredInfoContainer;
import org.apache.directory.shared.kerberos.components.KrbCredInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreTicketInfo extends GrammarAction<EncKrbCredPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreTicketInfo.class);
   private static final boolean IS_DEBUG;

   public void action(EncKrbCredPartContainer encKrbCredPartContainer) throws DecoderException {
      TLV tlv = encKrbCredPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder decoder = new Asn1Decoder();
         KrbCredInfoContainer ticketInfoContainer = new KrbCredInfoContainer();
         ticketInfoContainer.setStream(encKrbCredPartContainer.getStream());
         encKrbCredPartContainer.rewind();

         try {
            decoder.decode(encKrbCredPartContainer.getStream(), ticketInfoContainer);
         } catch (DecoderException e) {
            throw e;
         }

         KrbCredInfo ticketInfo = ticketInfoContainer.getKrbCredInfo();
         encKrbCredPartContainer.getEncKrbCredPart().addTicketInfo(ticketInfo);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         encKrbCredPartContainer.updateParent();
         encKrbCredPartContainer.setGrammarEndAllowed(true);
         if (IS_DEBUG) {
            LOG.debug("KrbCredInfo : {}", ticketInfo);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
